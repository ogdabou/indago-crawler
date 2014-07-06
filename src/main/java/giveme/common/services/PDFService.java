package giveme.common.services;

import giveme.common.beans.Article;
import giveme.common.models.PDFConverterRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.exec.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class PDFService
{

	public static Logger			LOGGER	= Logger.getLogger(PDFService.class.getName());
	/** Each element is a column. eg : ls -l /dir, 1 is ls, 2 is -l, 3rd is /dir */

	@Value(value = "${pdfBinary}")
	private String					binary;

	@Value(value = "{pdfThreadConverterNumber}")
	private int						threadNumber;

	private final ExecutorService	executorPool;

	public PDFService()
	{
		executorPool = Executors.newFixedThreadPool(threadNumber);
	}

	public PDFService(int threads)
	{
		executorPool = Executors.newFixedThreadPool(threads);
	}

	public String generatePDFFromArticle(Article article)
	{
		return generatePDFFromUrl(article.getUrl(), article.getTitle());
	}

	public String generatePDFFromUrl(String url, String title)
	{
		title = fromatPDFTitle(title);
		CommandLine commandLine = new CommandLine("wkhtmltopdf");
		commandLine.addArgument(binary);
		commandLine.addArgument(url);
		commandLine.addArgument(title);
		PDFConverterRunnable converter = new PDFConverterRunnable(commandLine, title);
		executorPool.execute(converter);
		return title;

	}

	private String fromatPDFTitle(String title)
	{
		title = title.replaceAll(" ", "_");
		StringBuilder builder = new StringBuilder(title);
		builder.append(".pdf");
		return builder.toString();
	}

	public ExecutorService getExecutorPool()
	{
		return executorPool;
	}

}
