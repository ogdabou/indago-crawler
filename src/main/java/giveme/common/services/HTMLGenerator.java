package giveme.common.services;

import giveme.common.beans.Article;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
