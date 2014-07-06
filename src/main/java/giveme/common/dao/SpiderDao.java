package giveme.common.dao;

import giveme.common.beans.Spider;
import giveme.common.services.JDBCConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class SpiderDao extends IDao<Spider>
{
	@Autowired
	JDBCConnector	connector;

	public SpiderDao()
	{
		TABLE_NAME = "spider";
		LOGGER = Logger.getLogger(SpiderDao.class.getName());
	}

	@Override
	public void save(Spider toSave)
	{

		connection = connector.getConnection();

		try
		{
			final String query = "insert into " + TABLE_NAME + " (spider) " + " VALUES (?);";

			final PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, toSave.getSpider());
			statement.executeUpdate();
			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next() && rs != null)
			{
				toSave.setSpider(rs.getString("spider"));
			}
			LOGGER.info("Saved spider " + toSave.getSpider());
			connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Spider createObjectFromResultSet(ResultSet rs) throws SQLException
	{
		Spider sp = new Spider();
		sp.setSpider(rs.getString("spider"));
		return sp;
	}

	public Spider findByName(String spiderName)
	{
		connection = connector.getConnection();
		Spider spider = null;
		try
		{
			final String query = "select * from " + TABLE_NAME + " where spider = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, spiderName);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				spider = createObjectFromResultSet(rs);
			}
			connection.close();
			return spider;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return spider;
	}

	@Override
	public JDBCConnector getJDBCConnector()
	{
		return connector;
	}

}
