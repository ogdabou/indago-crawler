package giveme.controllers;

import giveme.common.beans.Article;
import giveme.common.dao.ArticleDao;
import giveme.common.dao.AuthorDao;
import giveme.common.dao.CategorieDao;
import giveme.common.services.ArticleServices;
import giveme.common.services.HTMLGenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
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

	@Autowired
	HTMLGenerator			htmlGenerator;

	/**
	 *
	 */
	public ItemController()
	{
		LOGGER.info("Item API initialization");
	}

	/**
	 * Force the download of the HTML page
	 *
	 * @param response
	 */
	@RequestMapping(value = "/update/index", method = RequestMethod.GET)
	public void updateIndex(HttpServletResponse response)
	{
		try
		{
			List<Article> articleList = articleDao.list();
			String newIndex = htmlGenerator.updateIndex(articleList);
			InputStream responseStream = new ByteArrayInputStream(newIndex.getBytes());

			String fileName = URLEncoder.encode("index.html", "UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/force-download; charset=utf-8; data:text/html");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			IOUtils.copy(responseStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/update/content", method = RequestMethod.GET)
	public void updateContent(HttpServletResponse response)
	{
		try
		{
			ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(response.getOutputStream());
			zipOutput.setLevel(ZipArchiveOutputStream.STORED);

			List<Article> articleList = articleDao.list();
			String indexFile = htmlGenerator.updateIndex(articleList);

			htmlGenerator.generateImageFiles(articleList, zipOutput);
			Map<Long, String> articleFiles = htmlGenerator.generateArticleFiles(articleList);

			writeBytesToZipOutputStream(zipOutput, "index.html", indexFile.getBytes());
			for (Long articleId : articleFiles.keySet())
			{
				writeBytesToZipOutputStream(zipOutput, "articles/" + articleId + ".html", articleFiles.get(articleId)
						.getBytes());
			}
			response.flushBuffer();
			zipOutput.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void writeFileToZipOutputStream(ZipArchiveOutputStream zipOutput, File imgFile)
	{
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(imgFile, imgFile.getName());
		try
		{
			zipOutput.putArchiveEntry(zipEntry);
			zipOutput.closeArchiveEntry();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Write bytes to archive
	 *
	 * @param zipOutput
	 * @param fileName
	 * @param bs
	 */
	private void writeBytesToZipOutputStream(ZipArchiveOutputStream zipOutput, String fileName, byte[] bs)
	{
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(fileName);
		try
		{
			zipOutput.putArchiveEntry(zipEntry);
			zipOutput.write(bs);
			zipOutput.closeArchiveEntry();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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