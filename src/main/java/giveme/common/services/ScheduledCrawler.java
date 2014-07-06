package giveme.common.services;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is the scheduled crawler service. See spring-schedulers.xml
 *
 * @author ogdabou
 *
 */
@Component("scheduledCrawler")
public class ScheduledCrawler
{
	public static Logger	LOGGER	= Logger.getLogger(ScheduledCrawler.class.getName());

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
