package giveme.common.services;

import giveme.common.dao.SpiderDao;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class ScheduledCrawler
{
	public static Logger	LOGGER	= Logger.getLogger(ScheduledCrawler.class.getName());

	@Autowired
	private SpiderDao		spiderDao;

	@Autowired
	private ArticleServices	articleService;

	private int				runned	= 0;

	@Scheduled(fixedDelay = 300000)
	public void startCrawl()
	{
		LOGGER.info("RUNNING " + runned);
		articleService.refreshAll();
		runned++;
	}

	@PostConstruct
	public void start()
	{
	}
}
