package giveme.common.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JDBCConnector
{

	public static Logger	LOGGER	= Logger.getLogger(JDBCConnector.class.getName());

	@Value("${dbUrl}")
	private String			jdbcUrl;

	@Value("${dbUser}")
	private String			jdbcUser;

	@Value("${dbPwd}")
	private String			jdbcPw;

	public JDBCConnector()
	{
	}

	public Connection getConnection()
	{
		Connection connection = null;
		try
		{

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e)
		{

			LOGGER.error(e.getMessage());
		}

		try
		{

			connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPw);

		} catch (SQLException e)
		{
			LOGGER.info("Connection Failed! for this database " + jdbcPw + jdbcUrl + jdbcUser);
			e.printStackTrace();
		}
		if (connection != null)
		{
		}
		else
		{
			LOGGER.info("Failed to make connection!");
		}
		return connection;
	}

}