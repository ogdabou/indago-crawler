package giveme.common.dao;

import giveme.common.beans.Spider;
import giveme.controllers.JDBCConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class SpiderDAO extends IDao<Spider>
{
	@Autowired
	JDBCConnector	connector;

	public SpiderDAO()
	{
		TABLE_NAME = "spider";
		LOGGER = Logger.getLogger(SpiderDAO.class.getName());
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
			LOGGER.info("Saved author " + toSave.getSpider());
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

}
