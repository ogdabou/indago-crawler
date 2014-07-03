package giveme.common.models;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

public class PDFConverterRunnable implements Runnable
{
	private final Executor		executor	= new DefaultExecutor();
	private final CommandLine	command;

	public PDFConverterRunnable(CommandLine commandToExecute)
	{
		command = commandToExecute;
	}

	@Override
	public void run()
	{
		ExecuteWatchdog watchdog = new ExecuteWatchdog(200000);
		executor.setWatchdog(watchdog);
		try
		{
			executor.execute(command);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
