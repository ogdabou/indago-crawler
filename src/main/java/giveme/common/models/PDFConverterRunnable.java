package giveme.common.models;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.log4j.Logger;

public class PDFConverterRunnable implements Runnable
{
	public static Logger		LOGGER		= Logger.getLogger(PDFConverterRunnable.class.getName());
	private final Executor		executor	= new DefaultExecutor();
	private final CommandLine	command;
	private final String		title;

	public PDFConverterRunnable(CommandLine commandToExecute, String title)
	{
		command = commandToExecute;
		this.title = title;
	}

	@Override
	public void run()
	{
		LOGGER.info("Starting thread " + Thread.currentThread().getId());
		ExecuteWatchdog watchdog = new ExecuteWatchdog(200000);
		executor.setWatchdog(watchdog);
		try
		{
			executor.execute(command);
			LOGGER.info("Converted to " + title);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
