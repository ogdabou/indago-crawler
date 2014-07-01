package giveme.common.services;

import giveme.common.dao.SpiderDao;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is the scheduled crawler service. See spring-schedulers.xml
 * 
 * @author ogdabou
 *
 */
@Service("scheduledCrawler")
public class ScheduledCrawler
{
	public static Logger	LOGGER	= Logger.getLogger(ScheduledCrawler.class.getName());

	@Autowired
	private SpiderDao		spiderDao;

	@Autowired
	private ArticleServices	articleService;

	/**
	 * This function is scheduled, see spring-schedulers.xml
	 */
	public void startCrawl()
	{
		articleService.refreshAll();
	}

	@PostConstruct
	public void start()
	{
	}
}
