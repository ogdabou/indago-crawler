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

	Connection connection = null;

	public JDBCConnector()
	{

		LOGGER.info("-------- PostgreSQL "
				+ "JDBC Connection Connection ------------");
		try
		{

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e)
		{

			LOGGER.info("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			LOGGER.error(e.getMessage());
			return;
		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		try
		{

			this.connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/postgres", "postgres",
					"root");

		} catch (SQLException e)
		{

			LOGGER.info("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if (this.connection != null)
		{
			LOGGER.info("You made it, take control your database now!");
		} else
		{
			LOGGER.info("Failed to make connection!");
		}
	}

	public Connection getConnection()
	{
		return this.connection;
	}

	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}

}