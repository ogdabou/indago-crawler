package services;

import giveme.common.beans.Article;
import giveme.common.services.PDFService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations =
{ "classpath:indago.properties" })
public class PDFServicesTest
{
	public static Logger	LOGGER	= Logger.getLogger(PDFServicesTest.class.getName());

	/**
	 * Only the article title and URL is needed for this section
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void generatePDFFromArticleTest() throws InterruptedException
	{
		List<Article> articleList = Arrays.asList(new Article("google", "google.com"), new Article("stackoverflow",
				"stackoverflow.com"), new Article("bonjour", "bonjourmadame.fr"), new Article("bind", "bing.com"));
		LOGGER.info("RUNNING TEST");
		PDFService pdfService = new PDFService(2);
		for (Article article : articleList)
		{
			pdfService.generatePDFFromArticle(article);
		}
		pdfService.getExecutorPool().shutdown();
		pdfService.getExecutorPool().awaitTermination(5, TimeUnit.MINUTES);
	}
}
