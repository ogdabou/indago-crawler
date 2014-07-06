package giveme.common.services;

import static giveme.common.services.UrlServices.computeSrcUrlsInHtmlElement;
import static giveme.common.services.UrlServices.computeUrlWithContextPath;
import static giveme.shared.ItemType.ARTICLE;
import static giveme.shared.ItemType.ARTICLE_DETAILS;
import giveme.common.beans.Article;
import giveme.common.beans.Author;
import giveme.common.beans.Categorie;
import giveme.common.beans.Spider;
import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.dao.CategorieDao;
import giveme.common.dao.JobsDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class ArticleServices
{
	public static Logger		LOGGER	= Logger.getLogger(ArticleServices.class.getName());

	@Autowired
	private AuthorDao			authordao;

	@Autowired
	private CategorieDao		categoryDao;

	@Autowired
	private ArticleDao			articleDao;

	@Autowired
	private SpiderDao			spiderDao;

	@Autowired
	private JobsDao				scrapingJobDao;

	@Autowired
	private UrlServices			urlService;

	@Autowired
	private ScrapingJobServices	scrapingJobServices;

	@Autowired
	private HTMLGenerator		htmlGenerator;

	// @Autowired
	// private PDFService pdfService;

	@Value("${scrapingItemsApiUrl}")
	private String				scrapingItemsApiUrl;

	@Value("${scrapingApiKey}")
	private String				scrapingApiKey;

	@Value("${scrapingProjectId}")
	private String				scrapingProjectId;

	public ArticleServices()
	{
	}

	public void buildIndex()
	{
		articleDao.list();
	}

	/**
	 *
	 */
	public void refreshAll()
	{
		List<Spider> spiderList = spiderDao.list();
		LOGGER.info("Got " + spiderList.size() + " spiders to update.");
		for (Spider spider : spiderList)
		{
			List<ScrapingJob> scrapingJobs = scrapingJobServices.getLatestFinishedJobs(spider.getSpider());
			LOGGER.info("Got " + scrapingJobs.size() + " jobs to update from spider " + spider.getSpider());
			crawlJobs(spider, scrapingJobs);
		}
	}

	/**
	 *
	 * @param spider
	 * @param scrapingJobs
	 */
	private void crawlJobs(Spider spider, List<ScrapingJob> scrapingJobs)
	{
		for (ScrapingJob job : scrapingJobs)
		{
			// if (scrapingJobDao.findById(job.getId()) == null)
			// {
			LOGGER.info("Crawling job " + job.getId() + " from spider " + spider.getSpider());
			try
			{
				getArticleFromJob(job.getId());
				// scrapingJobDao.save(job);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			// }
		}
	}

	/**
	 *
	 * @param jobId
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private List<Article> getArticleFromJob(String jobId) throws JsonParseException, JsonMappingException,
	JsonProcessingException, IOException
	{
		List<Article> articleList = new ArrayList<Article>();
		List<Article> articleWithDetails = new ArrayList<Article>();
		String start = "0";
		String end = "30";
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
				if (article != null)
				{
					articleList.add(article);
				}
			}
		}

		articleList = mergeDetailsAndContent(articleList, articleWithDetails);

		LOGGER.info(articleList.size() + " articles builded");
		for (Article finalArticle : articleList)
		{
			saveOrUpdateIfExists(finalArticle);
		}
		in.close();
		return articleList;
	}

	/**
	 * Presenters are articles retrieved on the LIST page of article, like the
	 *
	 * @param articleList
	 * @param articlePresenters
	 * @return
	 */
	private List<Article> mergeDetailsAndContent(List<Article> articleList, List<Article> articlePresenters)
	{
		List<Article> finalList = new ArrayList<Article>();
		for (Article articleContent : articleList)
		{
			for (Article articleFromPresenter : articlePresenters)
			{
				if (articleFromPresenter.getTitle() != null
						&& articleFromPresenter.getTitle().equals(articleContent.getTitle()))
				{
					setAuthorOrCreateWithCategory(articleContent, articleFromPresenter);
					articleFromPresenter.fillMissingParams(articleContent);
					resolveUrls(articleFromPresenter);
					finalList.add(articleFromPresenter);
				}
			}
		}
		return finalList;
	}

	/**
	 *
	 * @param articleContent
	 * @param articleFromPresenter
	 */
	private void setAuthorOrCreateWithCategory(Article articleContent, Article articleFromPresenter)
	{
		if (articleContent.getAuthor() == null)
		{
			if (articleFromPresenter.getCategorie() != null)
			{
				Author aut = new Author();
				aut.setAuthor(articleFromPresenter.getCategorie().getCategory());
				articleFromPresenter.setAuthor(aut);
			}
		}
	}

	private void resolveUrls(Article article)
	{
		String url = article.getUrl();
		if (article.getContent() != null)
		{
			article.setContent(computeSrcUrlsInHtmlElement(article.getContent(), "img", "src", url));
		}
		if (article.getAritcleCover() != null)
		{
			article.setAritcleCover(computeUrlWithContextPath(article.getAritcleCover(), url));
		}
		if (article.getSources() != null)
		{
			article.setSources(computeSrcUrlsInHtmlElement(article.getSources(), "a", "url", url));
		}
	}

	/**
	 *
	 * @param articleWithoutDetails
	 * @param detailedArticleWithoutcontent
	 */
	private void saveOrUpdateIfExists(Article article)
	{
		if (!articleExists(article.getTitle()))
		{
			LOGGER.info("saved " + article.getTitle());
			if (!authorExists(article.getAuthor()))
			{
				authordao.save(article.getAuthor());
			}
			articleDao.save(article);
		}
	}

	/**
	 *
	 * @param author
	 * @return
	 */
	private boolean authorExists(Author author)
	{
		if (authordao.findByName(author.getAuthor()) != null)
		{
			return true;
		}
		return false;
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
		LOGGER.debug("Article " + title + " exists");
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
		Article art = null;

		Author author = buildAuthor(articleMap);
		if (author != null)
		{
			art = new Article();
			art.setAuthor(author);
			if (articleMap.getContent() != null && articleMap.getPublication_date() != null
					&& articleMap.getTitle() != null && articleMap.getUrl() != null)
			{

				art.setContent(articleMap.getContent().get(0));
				art.setPublicationDate(articleMap.getPublication_date().get(0));
				art.setTitle(articleMap.getTitle().get(0));
				art.setUrl(articleMap.getUrl());
			}

			if (articleMap.getSources() != null)
			{
				art.setSources(articleMap.getSources().get(0));
			}
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
		if (articleMap.getAuthor() != null && articleMap.getAuthor().get(0) != null)
		{
			String authorName = articleMap.getAuthor().get(0);
			author = authordao.findByName(authorName);
			if (author == null)
			{
				author = new Author();
				author.setAuthor(authorName);
				LOGGER.debug("author " + author.getAuthor() + " builded");
			}
			return author;
		}
		return author;
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
	private ArrayList<Article> fillArticlesWithDetails(ArticlesDetailsMapper articleDescription, Categorie category)
	{
		ArrayList<Article> articleList = new ArrayList<Article>();
		for (ArticlesDetailsJson detailsJson : articleDescription.getVariants())
		{
			Article article = new Article();
			article.setCategorie(category);

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
				LOGGER.debug("Categorie " + categorie.getCategory() + " already exists.");
				return categorie;
			}
			else
			{
				categorie = new Categorie();
				categorie.setCategory(articleDescription.getCategory().get(0));
			}
			categoryDao.save(categorie);
		}
		return categorie;
	}
}
