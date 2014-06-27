package giveme.controllers;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class ArticleServices
{
	public static Logger LOGGER = Logger.getLogger(ArticleServices.class
			.getName());

	public ArticleServices()
	{
		// TODO Auto-generated constructor stub
	}

	// http://www.voltairenet.org/article184192.html

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
			} else
			{
				//
			}

		}
		return content.toString();
	}
}
