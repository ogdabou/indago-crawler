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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class ScrapingJobServices
{
	public static Logger	LOGGER				= Logger.getLogger(ScrapingJobServices.class.getName());

	@Value("${scrapingJobsApiUrl}")
	private String			scrapingJobsApiUrl;

	@Value("${scrapingApiKey}")
	private String			scrapingApiKey;

	@Value("${scrapingJobsNumberToGet}")
	private String			scrapingJobsNumberToGet;

	@Value("${scrapingProjectId}")
	private final String	scrapingProjectId	= "4002";

	public ScrapingJobServices()
	{
		// getLatestFinishedJobs();
	}

	public List<ScrapingJob> getLatestFinishedJobs(String spiderName)
	{
		List<ScrapingJob> jobs = new ArrayList<ScrapingJob>();

		String query = "https://dash.scrapinghub.com/api/jobs/list.json?project=" + scrapingProjectId + "&apikey="
				+ scrapingApiKey + "&spider=" + spiderName + "&state=" + "finished" + "&count="
				+ scrapingJobsNumberToGet;
		URL url;
		try
		{
			url = new URL(query);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String jsonValue;
			while ((jsonValue = in.readLine()) != null)
			{
				LOGGER.info("Full datas received :" + jsonValue);
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
			e.printStackTrace();
		}
		return jobs;

	}
}
