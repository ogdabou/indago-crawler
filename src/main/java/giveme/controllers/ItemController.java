package giveme.controllers;

import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.dao.CategorieDao;
import giveme.common.services.ArticleServices;

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

	@Autowired
	private ArticleServices	articleServices;

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
		articleServices.refreshAll();
	}
}