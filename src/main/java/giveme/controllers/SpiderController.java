package giveme.controllers;

import giveme.common.beans.Spider;
import giveme.common.dao.SpiderDao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SpiderController
{
	public static Logger	LOGGER	= Logger.getLogger(SpiderController.class.getName());
	@Autowired
	private SpiderDao		spiderDao;

	@RequestMapping(value = "spider/create/{spiderName}", method = RequestMethod.GET)
	public void createNewSpider(@ModelAttribute(value = "spiderName") String spiderName)
	{
		if (spiderDao.findByName(spiderName) == null)
		{
			spiderDao.save(new Spider(spiderName));
		}
		else
		{
			LOGGER.error("Spider " + spiderName + " already registed.");
		}
	}
}
