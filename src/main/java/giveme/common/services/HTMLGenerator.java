package giveme.common.services;

import giveme.common.beans.Article;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public class HTMLGenerator
{
	public static Logger	LOGGER		= Logger.getLogger(HTMLGenerator.class.getName());

	private final String	indexPath;

	private final String	targetPath	= "/wtpwebapps/resources/embedded/";

	private File			indexFile;

	public HTMLGenerator()
	{
		indexPath = "/wtpwebapps/resources/index.html";

		LOGGER.info("Path to html page is " + indexPath);
	}

	public String updateIndex(List<Article> articleList)
	{
		try
		{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("embedded/index.html");
			// xmlParser say to JSOUP not to resolve urls..
			Document content = Jsoup.parse(in, "utf-8", "", Parser.xmlParser());
			Elements articleListElements = content.select("div.aritcle-list");
			for (Element articleListElement : articleListElements)
			{
				for (Article article : articleList)
				{
					Element articleItem = articleListElement.appendElement("div");
					articleItem.addClass("article-list-item");
					articleItem.addClass("row");

					Element articleLinkElement = articleItem.appendElement("a");
					articleLinkElement.attr("href", "/content/articles/" + article.getId() + ".html");

					Element articleTitleElement = articleLinkElement.appendElement("h3");
					articleTitleElement.append(article.getTitle());

					Element articleDescriptionElement = articleItem.appendElement("div");
					articleDescriptionElement.addClass("article-list-item-description");
					articleDescriptionElement.append(article.getDescription());
				}
			}
			return content.toString();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Map<Long, String> generateArticleFiles(List<Article> articleList)
	{
		Map<Long, String> articleFiles = new HashMap<Long, String>();
		try
		{
			for (Article article : articleList)
			{
				InputStream in = this.getClass().getClassLoader().getResourceAsStream("embedded/article_template.html");
				// xmlParser say to JSOUP not to resolve urls..
				Document content = Jsoup.parse(in, "utf-8", "", Parser.xmlParser());
				Element titleEl = content.getElementById("article_title");
				titleEl.appendText(article.getTitle());

				Element publicationEl = content.getElementById("publication_date");
				publicationEl.appendText(article.getPublicationDate());

				Element authorEl = content.getElementById("author");
				authorEl.appendText(article.getAuthor().getAuthor());

				Element contentEl = content.getElementById("article_content");
				// TODO here we would need some imags downlading
				contentEl.append(article.getContent());
				if (article.getSources() != null)
				{
					Element sourcesEl = content.getElementById("article_sources");
					sourcesEl.append(article.getSources());
				}
				articleFiles.put(article.getId(), content.toString());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return articleFiles;
	}

	/**
	 * At this point, each image should have a complete and absolute external
	 * URL. We should always be able to download it.
	 *
	 * @param articleList
	 * @param zipOutput
	 * @return
	 */
	public Map<Long, List<File>> generateImageFiles(List<Article> articleList, ZipArchiveOutputStream zipOutput)
	{
		Map<Long, List<File>> articlesImages = new HashMap<Long, List<File>>();

		try
		{
			for (Article article : articleList)
			{
				List<File> imgList = new ArrayList<File>();
				InputStream in = new ByteArrayInputStream(article.getContent().getBytes());
				Document content = Jsoup.parse(in, "utf-8", "", Parser.xmlParser());
				Elements imgs = content.getElementsByTag("img");
				int imageId = 0;
				for (Element imgEl : imgs)
				{
					String imgUrl = imgEl.attr("src");
					LOGGER.info("Downloading image from " + imgUrl);
					URL url = new URL(imgUrl);
					InputStream is = url.openStream();
					String fileName = "img" + imageId;
					if (imgUrl.contains(".jpg"))
					{
						fileName += ".jpg";
					}
					else if (imgUrl.contains(".png"))
					{
						fileName += ".png";
					}
					else if (imgUrl.contains(".gif"))
					{
						fileName += ".gif";
					}
					File imgFile = new File(fileName);
					byte[] b = new byte[2048];
					int length;
					fileName = article.getId() + "/" + fileName;
					File tmp = File.createTempFile("lol-", "");
					OutputStream os = new FileOutputStream(tmp);
					while ((length = is.read(b)) != -1)
					{
						os.write(b, 0, length);
					}
					ZipArchiveEntry imgEntry = new ZipArchiveEntry(tmp, "imgArt/" + fileName);
					byte[] imgBytes = IOUtils.toByteArray(new FileInputStream(tmp));
					imgEntry.setSize(imgBytes.length);
					zipOutput.putArchiveEntry(imgEntry);
					zipOutput.write(imgBytes);
					zipOutput.closeArchiveEntry();
					is.close();
					os.close();
					LOGGER.info(imgEl.toString());
					imgEl.attr("src", "/content/imgArt/" + fileName);
					LOGGER.info(imgEl.toString());
					imgList.add(imgFile);
					tmp.delete();
					imageId++;
				}
				article.setContent(content.toString());
				articlesImages.put(article.getId(), imgList);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return articlesImages;
	}
}
