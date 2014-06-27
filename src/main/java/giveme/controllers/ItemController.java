package giveme.controllers;

import giveme.common.beans.ArticleDetails;
import giveme.common.beans.Articles;
import giveme.common.dao.ArticleDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

	public ItemController()
	{
		LOGGER.info("Item API initialization");
	}

	@RequestMapping(value = "/update/items", method = RequestMethod.GET)
	public void updateItemsApi() throws IOException
	{
		List<Articles> articleList = getArticles();
		List<ArticleDetails> details = getArticlesDetails();
		articleList = complete(articleList, details);
		articleDao.list();
	}

	private List<Articles> complete(List<Articles> articleList,
			List<ArticleDetails> details)
	{
		for (ArticleDetails articleDetails : details)
		{
			for (Articles article : articleList)
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

	private List<ArticleDetails> getArticlesDetails() throws IOException
	{
		List<ArticleDetails> list = new ArrayList<>();
		String job = "4002/4/4";
		String start = "0";
		String end = "10";
		String query = "https://storage.scrapinghub.com/items/" + job
				+ "?apikey=78c8f0b2681f4203a31f5277c2696c88" /*
															 * + "&start=" + job
															 * + "/" + start +
															 * "&count=" + end
															 */;
		// LOGGER.info("query : " + query);
		URL url = new URL(query);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));

		String jsonValue;
		while ((jsonValue = in.readLine()) != null)
		{
			// LOGGER.info("received " + jsonValue);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArticleDescriptionMapper articleDescription = mapper.readValue(
					jsonValue, ArticleDescriptionMapper.class);
			ArticleDetails art = articleDescription.convert();
			list.add(art);
			LOGGER.info(art.getArticle_url());
		}
		in.close();
		return list;
	}

	private List<Articles> getArticles() throws MalformedURLException,
			IOException, JsonParseException, JsonMappingException
	{
		List<Articles> articleList = new ArrayList<>();
		String job = "4002/3/95";
		String start = "0";
		String end = "10";
		String query = "https://storage.scrapinghub.com/items/" + job
				+ "?apikey=78c8f0b2681f4203a31f5277c2696c88" /*
															 * + "&start=" + job
															 * + "/" + start +
															 * "&count=" + end
															 */;
		LOGGER.info("query : " + query);
		URL url = new URL(query);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));

		String jsonValue;
		while ((jsonValue = in.readLine()) != null)
		{
			// LOGGER.info("received " + jsonValue);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArticleMapper article = mapper.readValue(jsonValue,
					ArticleMapper.class);
			Articles art = article.convert();
			articleList.add(art);
			// LOGGER.info("title: " + article.getTitle());
			// LOGGER.info("content : " + article.getUrl());
			// LOGGER.info("publicationDate: " + article.getPublicationDate());
		}
		in.close();
		return articleList;
	}
}