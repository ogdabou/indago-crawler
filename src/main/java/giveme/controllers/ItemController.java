package giveme.controllers;

import static giveme.shared.ItemType.ARTICLE;
import static giveme.shared.ItemType.ARTICLE_DETAILS;
import giveme.common.beans.Article;
import giveme.common.beans.ArticleDetails;
import giveme.common.beans.Author;
import giveme.common.beans.Categorie;
import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.dao.CategorieDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ItemController
{
	public static Logger LOGGER = Logger.getLogger(ItemController.class
			.getName());

	@Autowired
	ArticleDao articleDao;

	@Autowired
	CategorieDao categoryDao;

	@Autowired
	AuthorDao authordao;

	/**
	 * 
	 */
	public ItemController()
	{
		LOGGER.info("Item API initialization");
	}

	/**
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/update/items", method = RequestMethod.GET)
	public void updateItemsApi() throws IOException
	{
		// List<Articles> articleList = getArticles();
		List<ArticleDetails> details = getArticlesDetails();
		// articleList = complete(articleList, details);
		articleDao.list();
	}

	/**
	 * 
	 * @param articleList
	 * @param details
	 * @return
	 */
	private List<Article> complete(List<Article> articleList,
			List<ArticleDetails> details)
	{
		for (ArticleDetails articleDetails : details)
		{
			for (Article article : articleList)
			{
				String computedUrl = article.getUrl().substring(
						article.getUrl().lastIndexOf("/"));
				if (articleDetails.getArticle_url().equals(computedUrl))
				{
					LOGGER.info("coucou :)");
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<ArticleDetails> getArticlesDetails() throws IOException
	{
		List<ArticleDetails> list = new ArrayList<>();
		String job = "4002/5/6";
		String start = "0";
		String end = "10";
		String query = "https://storage.scrapinghub.com/items/" + job
				+ "?apikey=78c8f0b2681f4203a31f5277c2696c88" + "&start=" + job
				+ "/" + start + "&count=" + end;
		// LOGGER.info("query : " + query);
		URL url = new URL(query);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));

		String jsonValue;

		List<Article> articles = new ArrayList<>();
		while ((jsonValue = in.readLine()) != null)
		{
			// LOGGER.info("received " + jsonValue);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			JsonNode rootNode = mapper.readTree(jsonValue);
			String itemType = rootNode.path("_type").toString()
					.replaceAll("\"", "");

			if (ARTICLE_DETAILS.getType().equals(itemType))
			{
				articles = extractDetails(jsonValue, mapper);

			} else if (ARTICLE.getType().equals(itemType))
			{
				Article article = extractArticle(jsonValue, mapper);
				for (Article detailedArticleWithoutcontent : articles)
				{
					if (article.getTitle().equals(
							detailedArticleWithoutcontent.getTitle()))
					{
						detailedArticleWithoutcontent
								.MergeWithDetailed(article);
						LOGGER.info("Article " + article.getTitle()
								+ " merged.");
						articleDao.save(detailedArticleWithoutcontent);
					}
				}
			}
		}
		in.close();
		return list;
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
	private Article extractArticle(String jsonValue, ObjectMapper mapper)
			throws JsonParseException, JsonMappingException, IOException
	{
		ArticleMapper articleMap = mapper.readValue(jsonValue,
				ArticleMapper.class);
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
			if ((author = authordao.findByName(authorName)) == null)
			{
				LOGGER.info("author " + author + " saved");
				author = new Author();
				author.setAuthor(authorName);
				authordao.save(author);
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
	private ArrayList<Article> extractDetails(String jsonValue,
			ObjectMapper mapper) throws IOException, JsonParseException,
			JsonMappingException
	{
		ArticleDescriptionMapper articleDescription = mapper.readValue(
				jsonValue, ArticleDescriptionMapper.class);

		Categorie category = buildCategory(articleDescription);
		return fillArticlesWithDetails(articleDescription, category);
	}

	/**
	 * 
	 * @param articleDescription
	 * @param category
	 * @return
	 */
	public ArrayList<Article> fillArticlesWithDetails(
			ArticleDescriptionMapper articleDescription, Categorie category)
	{
		ArrayList<Article> articleList = new ArrayList<>();
		for (ArticleDescriptionJson detailsJson : articleDescription
				.getVariants())
		{
			Article article = new Article();
			article.setCategoryId(category.getCategoryId());

			ArticleDetails detail = new ArticleDetails();

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
	private Categorie buildCategory(ArticleDescriptionMapper articleDescription)
	{
		Categorie categorie = null;
		if (articleDescription.getCategory() != null)
		{
			categorie = new Categorie();
			categorie.setCategory(articleDescription.getCategory().get(0));
			List<Categorie> catList = categoryDao.list();
			LOGGER.info("Found " + catList.size() + " categories.");

			for (Categorie cat : catList)
			{
				if (cat.getCategory().equals(categorie.getCategory()))
				{
					LOGGER.info("Categorie " + categorie.getCategory()
							+ " already exists.");
					return cat;
				}
			}
			categoryDao.save(categorie);
		}
		return categorie;
	}
}