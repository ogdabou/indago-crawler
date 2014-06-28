package giveme.common.models;

import java.util.Date;

public class ScrapingJob
{
	public ScrapingJob()
	{
		// TODO Auto-generated constructor stub
	}

	private String	spider;
	private String	id;
	private Date	started_time;
	private Date	updated_time;

	public Date getUpdated_time()
	{
		return updated_time;
	}

	public void setUpdated_time(Date updated_time)
	{
		this.updated_time = updated_time;
	}

	public String getSpider()
	{
		return spider;
	}

	public void setSpider(String spider)
	{
		this.spider = spider;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Date getStarted_time()
	{
		return started_time;
	}

	public void setStarted_time(Date started_time)
	{
		this.started_time = started_time;
	}

}
