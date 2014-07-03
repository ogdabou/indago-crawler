package giveme.common.services;

import giveme.common.beans.Article;
import giveme.common.models.PDFConverterRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.exec.CommandLine;

public class PDFService
{
	/** Each element is a column. eg : ls -l /dir, 1 is ls, 2 is -l, 3rd is /dir */

	// @Value(value = "${pdfBinary}")
	private String	binary;

	// @Value(value = "{pdfThreadConverterNumber}")
	private int		threadNumber;

	ExecutorService	executorPool	= Executors.newFixedThreadPool(threadNumber);

	public PDFService()
	{
	}

	public void generatePDFFromArticle(Article article)
	{
		String pdfName = generatePDFFromUrl(article.getUrl(), article.getTitle());

	}

	public String generatePDFFromUrl(String url, String title)
	{
		title = fromatPDFTitle(title);
		CommandLine commandLine = new CommandLine("wkhtmltopdf");
		List<String> command = new ArrayList<String>();
		commandLine.addArgument(binary);
		commandLine.addArgument(url);
		commandLine.addArgument(title);
		PDFConverterRunnable converter = new PDFConverterRunnable(commandLine);
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
}
