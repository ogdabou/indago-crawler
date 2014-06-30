package giveme.controllers;

import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.dao.CategorieDao;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ItemController
{
	public static Logger	LOGGER	= Logger.getLogger(ItemController.class.getName());

	@Autowired
	private ArticleDao		articleDao;

	@Autowired
	private CategorieDao	categoryDao;

	@Autowired
	private AuthorDao		authordao;

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
		// List<Article> articles = findArticles();
		//
		// List<Article> ar = articleDao.list();
		// for (Article article : ar)
		// {
		// LOGGER.info(article.getTitle());
		// }
	}

	// /**
	// *
	// * @return
	// * @throws IOException
	// */
	// private List<Article> findArticles() throws IOException
	// {
	// List<Article> articleList = new ArrayList<>();
	// String job = "4002/5/6";
	// String start = "0";
	// String end = "10";
	// String query = "https://storage.scrapinghub.com/items/" + job +
	// "?apikey=78c8f0b2681f4203a31f5277c2696c88"
	// + "&start=" + job + "/" + start + "&count=" + end;
	// // LOGGER.info("query : " + query);
	// URL url = new URL(query);
	//
	// BufferedReader in = new BufferedReader(new
	// InputStreamReader(url.openStream()));
	//
	// String jsonValue;
	//
	// List<Article> articles = new ArrayList<>();
	// while ((jsonValue = in.readLine()) != null)
	// {
	// // LOGGER.info("received " + jsonValue);
	// ObjectMapper mapper = new ObjectMapper();
	// mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	//
	// JsonNode rootNode = mapper.readTree(jsonValue);
	//
	// String itemType = extractItemType(rootNode);
	//
	// if (ARTICLE_DETAILS.getType().equals(itemType))
	// {
	// articles = extractDetails(jsonValue, mapper);
	//
	// }
	// else if (ARTICLE.getType().equals(itemType))
	// {
	// Article article = extractArticle(jsonValue, mapper);
	// articleList.add(article);
	// }
	// }
	//
	// articleList = mergeDetailsAndContent(articleList, articles);
	// in.close();
	// return articleList;
	// }

}