package giveme.controllers;

import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.services.ArticleServices;
import giveme.common.services.HTMLGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

@Controller
@Component
@Repository
public class EmbeddedController
{

	@Autowired
	HTMLGenerator	htmlGenerator;

	@Autowired
	ArticleServices	articleServices;

	@Autowired
	ArticleDao		articleDao;

	@Autowired
	AuthorDao		authorDAO;

	// @RequestMapping(value = "/update/index", method = RequestMethod.GET)
	// @ResponseBody
	// public String updateItemsApi() throws IOException
	// {
	// articleServices.buildIndex();
	// return "";
	// // List<Author> aut = authorDAO.list();
	// // List<Article> articleList = articleDao.list();
	// // return htmlGenerator.updateIndex(articleList);
	// }

}
