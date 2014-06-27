package giveme.common.dao;

import giveme.controllers.JDBCConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class IDao<T>
{
	protected String TABLE_NAME;
	protected Connection connection;
	public static Logger LOGGER;

	@Autowired
	JDBCConnector jdbcConnector;

	public List<T> list()
	{
		LOGGER.info("Listing shows.");
		this.connection = this.jdbcConnector.getConnection();

		final List<T> resultList = new ArrayList<T>();
		try
		{
			final String query = "select * from " + this.TABLE_NAME;
			final ResultSet rs = this.connection.createStatement()
					.executeQuery(query);
			while (rs.next())
			{
				final T resultObject = createObjectFromResultSet(rs);
				resultList.add(resultObject);
			}
			this.connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		LOGGER.info(resultList.size() + " " + this.TABLE_NAME + " found.");
		return resultList;
	}

	public abstract void save(T toSave);

	public abstract T createObjectFromResultSet(ResultSet rs)
			throws SQLException;
}
