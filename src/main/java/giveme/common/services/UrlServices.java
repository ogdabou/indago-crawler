package giveme.common.services;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class UrlServices
{
	public static Logger	LOGGER	= Logger.getLogger(UrlServices.class.getName());

	public static String computeUrlWithContextPath(String toModify, String url)
	{
		int lastSlash = url.lastIndexOf("/");
		String contextPath = url.substring(0, lastSlash);
		if (!toModify.contains(contextPath))
		{
			String separator = "";
			if (toModify.indexOf("/") != 0)
			{
				separator = "/";
			}
			LOGGER.debug("Start element is : " + toModify + " is relative");
			String computedUrl = contextPath + separator + toModify;
			LOGGER.debug("Final string is : " + computedUrl);
			return computedUrl;
		}
		return toModify;
	}

	/**
	 * Article content must be in HTML
	 *
	 * @param articleContent
	 */
	public static String computeSrcUrlsInHtmlElement(String toModify, String elementAsString, String attribute,
			String url)
	{
		Document content = Jsoup.parse(toModify);
		Elements imgs = content.select(elementAsString);
		for (Element element : imgs)
		{
			String elementUrl = element.attr(attribute);
			String computedUrl = computeUrlWithContextPath(elementUrl, url);
			element.attr(attribute, computedUrl);
		}
		return content.toString();
	}
}
