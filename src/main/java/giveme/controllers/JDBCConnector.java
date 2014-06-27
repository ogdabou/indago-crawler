package giveme.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JDBCConnector
{

	public static Logger LOGGER = Logger.getLogger(JDBCConnector.class
			.getName());

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

			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/postgres", "postgres",
					"root");

		} catch (SQLException e)
		{

			LOGGER.info("Connection Failed! Check output console");
			e.printStackTrace();
		}
		if (connection != null)
		{
		} else
		{
			LOGGER.info("Failed to make connection!");
		}
		return connection;
	}

}