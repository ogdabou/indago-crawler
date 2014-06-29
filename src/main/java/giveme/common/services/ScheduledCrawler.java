package giveme.common.services;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class ScheduledCrawler
{
	public static Logger	LOGGER	= Logger.getLogger(ScheduledCrawler.class.getName());

	@Autowired
	JobsServices			scrapingJobServices;

	@Scheduled(fixedDelay = 5000)
	public void startCrawl()
	{
		scrapingJobServices.getLatestFinishedJobs();
	}

	@PostConstruct
	public void start()
	{

	}
}
