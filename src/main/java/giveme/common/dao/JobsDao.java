package giveme.common.dao;

import giveme.common.models.ScrapingJob;
import giveme.common.services.JDBCConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class JobsDao extends IDao<ScrapingJob>
{
	@Autowired
	JDBCConnector	connector;

	public JobsDao()
	{
		TABLE_NAME = "scraping_job";
		LOGGER = Logger.getLogger(JobsDao.class.getName());
	}

	@Override
	public void save(ScrapingJob toSave)
	{
		connection = connector.getConnection();

		try
		{
			final String query = "insert into " + TABLE_NAME + " (id, started_time, spider) " + " VALUES (?, ?, ?);";

			final PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, toSave.getId());
			statement.setTimestamp(2, new Timestamp(toSave.getStarted_time().getTime()));
			statement.setString(3, toSave.getSpider());
			statement.executeUpdate();
			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next() && rs != null)
			{
				toSave.setSpider(rs.getString("spider"));
			}
			LOGGER.debug("Saved spider " + toSave.getSpider());
			connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ScrapingJob findById(String jobId)
	{
		connection = connector.getConnection();
		ScrapingJob job = null;
		try
		{
			final String query = "select * from " + TABLE_NAME + " where id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, jobId);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				job = createObjectFromResultSet(rs);
			}
			connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return job;
	}

	@Override
	public ScrapingJob createObjectFromResultSet(ResultSet rs) throws SQLException
	{
		ScrapingJob sj = new ScrapingJob();
		sj.setId(rs.getString("id"));
		sj.setSpider(rs.getString("spider"));
		sj.setUpdated_time(rs.getTimestamp("updated_time"));
		sj.setStarted_time(rs.getTimestamp("started_time"));
		return sj;
	}

	@Override
	public JDBCConnector getJDBCConnector()
	{
		return connector;
	}
}
