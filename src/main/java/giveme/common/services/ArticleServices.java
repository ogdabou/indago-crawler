package giveme.common.services;

import static giveme.shared.ItemType.ARTICLE;
import static giveme.shared.ItemType.ARTICLE_DETAILS;
import giveme.common.beans.Article;
import giveme.common.beans.Author;
import giveme.common.beans.Categorie;
import giveme.common.beans.Spider;
import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.dao.CategorieDao;
import giveme.common.dao.SpiderDao;
import giveme.common.models.ScrapingJob;
import giveme.common.models.json.ArticleMapper;
import giveme.common.models.json.ArticlesDetailsJson;
import giveme.common.models.json.ArticlesDetailsMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class ArticleServices
{
	public static Logger		LOGGER				= Logger.getLogger(ArticleServices.class.getName());

	@Autowired
	private AuthorDao			authordao;

	@Autowired
	private CategorieDao		categoryDao;

	@Autowired
	private ArticleDao			articleDao;

	@Autowired
	private SpiderDao			spiderDao;

	@Autowired
	private ScrapingJobServices	scrapingJobServices;

	@Value("${scrapingItemsApiUrl}")
	private String				scrapingItemsApiUrl;

	@Value("${scrapingApiKey}")
	private String				scrapingApiKey;

	@Value("${scrapingProjectId}")
	private final String		scrapingProjectId	= "4002";

	public ArticleServices()
	{
	}

	public void refreshAll()
	{
		List<Spider> spiderList = spiderDao.list();
		LOGGER.info("Got " + spiderList.size() + " spiders to update.");
		for (Spider spider : spiderList)
		{
			List<ScrapingJob> scrapingJobs = scrapingJobServices.getLatestFinishedJobs(spider.getSpider());
			LOGGER.info("Got " + scrapingJobs.size() + " jobs to update.");
			for (ScrapingJob job : scrapingJobs)
			{
				try
				{
					getArticleFromJob(job.getId());
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public List<Article> getArticleFromJob(String jobId) throws JsonParseException, JsonMappingException,
			JsonProcessingException, IOException
	{
		List<Article> articleList = new ArrayList<>();
		List<Article> articleWithDetails = new ArrayList<>();
		String start = "0";
		String end = "10";
		String query = scrapingItemsApiUrl + "/" + jobId + "?apikey=" + scrapingApiKey + "&start=" + jobId + "/"
				+ start + "&count=" + end;
		URL url = new URL(query);

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String jsonValue;

		while ((jsonValue = in.readLine()) != null)
		{
			LOGGER.debug("received " + jsonValue);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode rootNode = mapper.readTree(jsonValue);

			String itemType = extractItemType(rootNode);

			if (ARTICLE_DETAILS.getType().equals(itemType))
			{
				articleWithDetails = extractDetails(jsonValue, mapper);

			}
			else if (ARTICLE.getType().equals(itemType))
			{
				Article article = extractArticle(jsonValue, mapper);
				articleList.add(article);
			}
		}

		articleList = mergeDetailsAndContent(articleList, articleWithDetails);
		in.close();
		return articleList;
	}

	/**
	 * Article content must be in HTML
	 *
	 * @param articleContent
	 */
	public String computeSrcUrls(String articleContent, String url)
	{
		int lastSlash = url.lastIndexOf("/");
		String contextPath = url.substring(0, lastSlash);

		Document content = Jsoup.parse(articleContent);
		Elements imgs = content.select("img");
		for (Element element : imgs)
		{
			String imgUrl = element.attr("url");
			if (imgUrl.indexOf("/") != 0)
			{
				LOGGER.info("img url " + imgUrl + " is relative");
				String computedImgUrl = contextPath + imgUrl;
				element.attr("url", computedImgUrl);
				LOGGER.info("Final element is : " + element.toString());
			}
			else
			{
				//
			}

		}
		return content.toString();
	}

	/**
	 *
	 * @param articleList
	 * @param articles
	 * @return
	 */
	private List<Article> mergeDetailsAndContent(List<Article> articleList, List<Article> articles)
	{
		for (Article articleWithoutDetails : articleList)
		{

			for (Article detailedArticleWithoutcontent : articles)
			{
				if (articleWithoutDetails.getTitle().equals(detailedArticleWithoutcontent.getTitle()))
				{
					detailedArticleWithoutcontent.fillMissingParams(articleWithoutDetails);
					saveOrUpdateIfExists(articleWithoutDetails, detailedArticleWithoutcontent);
				}
			}
		}
		return articles;
	}

	/**
	 *
	 * @param articleWithoutDetails
	 * @param detailedArticleWithoutcontent
	 */
	private void saveOrUpdateIfExists(Article articleWithoutDetails, Article detailedArticleWithoutcontent)
	{
		if (!articleExists(articleWithoutDetails.getTitle()))
		{
			articleDao.save(detailedArticleWithoutcontent);
		}
		else
		{
			articleDao.update(detailedArticleWithoutcontent);
		}
	}

	/**
	 *
	 * @param title
	 * @return
	 */
	private boolean articleExists(String title)
	{
		Article art = articleDao.findByTitle(title);
		if (art == null)
		{
			return false;
		}
		LOGGER.info("Article " + title + " exists");
		return true;
	}

	/**
	 *
	 * @param rootNode
	 * @return
	 */
	private String extractItemType(JsonNode rootNode)
	{
		String itemType = rootNode.path("_type").getTextValue();
		return itemType;
	}

	/**
	 *
	 * @param jsonValue
	 * @param mapper
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private Article extractArticle(String jsonValue, ObjectMapper mapper) throws JsonParseException,
			JsonMappingException, IOException
	{
		ArticleMapper articleMap = mapper.readValue(jsonValue, ArticleMapper.class);

		Author author = buildAuthor(articleMap);

		Article art = new Article();
		art.setAuthorId(author.getAuthorId());

		if (articleMap.getContent() != null)
		{
			art.setContent(articleMap.getContent().get(0));
		}
		if (articleMap.getPublication_date() != null)
		{
			art.setPublicationDate(articleMap.getPublication_date().get(0));
		}
		if (articleMap.getSources() != null)
		{
			art.setSources(articleMap.getSources().get(0));
		}
		if (articleMap.getTitle() != null)
		{
			art.setTitle(articleMap.getTitle().get(0));
		}
		if (articleMap.getUrl() != null)
		{
			art.setUrl(articleMap.getUrl());
		}

		return art;
	}

	/**
	 *
	 * @param articleMap
	 * @return
	 */
	private Author buildAuthor(ArticleMapper articleMap)
	{
		Author author = null;
		if (articleMap.getAuthor() != null)
		{
			String authorName = articleMap.getAuthor().get(0);
			author = authordao.findByName(authorName);
			if (author == null)
			{
				LOGGER.info("author " + author + " saved");
				author = new Author();
				author.setAuthor(authorName);
				authordao.save(author);
			}
			else
			{
				LOGGER.info("Author " + author.getAuthor() + " already exist");
			}
			return author;
		}
		return author;
	}

	/**
	 *
	 * @return
	 */
	public Article convert()
	{
		Article cv = new Article();

		return cv;
	}

	/**
	 *
	 * @param jsonValue
	 * @param mapper
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	private ArrayList<Article> extractDetails(String jsonValue, ObjectMapper mapper) throws IOException,
			JsonParseException, JsonMappingException
	{
		ArticlesDetailsMapper articleDescription = mapper.readValue(jsonValue, ArticlesDetailsMapper.class);
		Categorie category = buildCategory(articleDescription);
		if (category != null)
		{
			return fillArticlesWithDetails(articleDescription, category);
		}
		else
		{
			return new ArrayList<Article>();
		}
	}

	/**
	 *
	 * @param articleDescription
	 * @param category
	 * @return
	 */
	public ArrayList<Article> fillArticlesWithDetails(ArticlesDetailsMapper articleDescription, Categorie category)
	{
		ArrayList<Article> articleList = new ArrayList<>();
		for (ArticlesDetailsJson detailsJson : articleDescription.getVariants())
		{
			Article article = new Article();
			article.setCategoryId(category.getCategoryId());

			if (detailsJson.getArticle_url() != null)
			{
				article.setUrl(detailsJson.getArticle_url().get(0));
			}

			if (detailsJson.getPublicationDate() != null)
			{
				article.setUrl(detailsJson.getPublicationDate().get(0));
			}

			if (detailsJson.getDescription() != null)
			{
				article.setDescription(detailsJson.getDescription().get(0));
			}

			if (detailsJson.getImagUrl() != null)
			{
				article.setAritcleCover(detailsJson.getImagUrl().get(0));
			}

			if (detailsJson.getTitle() != null)
			{
				article.setTitle(detailsJson.getTitle().get(0));
			}

			if (detailsJson.getUrl() != null)
			{
				article.setUrl(detailsJson.getUrl());
			}
			articleList.add(article);
		}

		return articleList;
	}

	/**
	 *
	 * @param articleDescription
	 * @return
	 */
	private Categorie buildCategory(ArticlesDetailsMapper articleDescription)
	{
		Categorie categorie = null;
		if (articleDescription.getCategory() != null && !articleDescription.getCategory().isEmpty())
		{
			categorie = categoryDao.findByName(articleDescription.getCategory().get(0));
			if (categorie != null)
			{
				LOGGER.info("Categorie " + categorie.getCategory() + " already exists.");
				return categorie;
			}
			else
			{

			}

			categoryDao.save(categorie);
		}
		return categorie;
	}
}
