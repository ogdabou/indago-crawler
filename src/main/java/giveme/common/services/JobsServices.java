package giveme.common.services;

import giveme.common.models.ScrapingJob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class JobsServices
{
	private final String	projectId	= "4002";
	public static Logger	LOGGER		= Logger.getLogger(JobsServices.class.getName());

	public JobsServices()
	{
		// getLatestFinishedJobs();
	}

	public List<ScrapingJob> getLatestFinishedJobs()
	{
		List<ScrapingJob> jobs = new ArrayList<>();

		String query = "https://dash.scrapinghub.com/api/jobs/list.json?project=" + projectId
				+ "&apikey=78c8f0b2681f4203a31f5277c2696c88" + "&spider=" + "VOLTAIRE_FOCUS" + "&state=" + "finished"
				+ "&cout=" + "10";
		// LOGGER.info("query : " + query);
		URL url;
		try
		{
			url = new URL(query);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String jsonValue;
			while ((jsonValue = in.readLine()) != null)
			{
				LOGGER.debug("Full datas received :" + jsonValue);
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

				JsonNode rootNode = mapper.readTree(jsonValue);
				String jobsJson = rootNode.path("jobs").toString();

				jobs = mapper.readValue(jobsJson,
						mapper.getTypeFactory().constructCollectionType(List.class, ScrapingJob.class));
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
