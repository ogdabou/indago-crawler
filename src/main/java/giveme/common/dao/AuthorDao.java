package giveme.common.dao;

import giveme.common.beans.Author;
import giveme.common.services.JDBCConnector;

import java.sql.Connection;
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
public class AuthorDao extends IDao<Author>
{

	public AuthorDao()
	{
		TABLE_NAME = "\"auteur\"";
		LOGGER = Logger.getLogger(AuthorDao.class.getName());
	}

	@Autowired
	JDBCConnector		connector;

	private Connection	jdbcConnection;

	@Override
	public void save(Author toSave)
	{

		connection = connector.getConnection();

		try
		{
			final String query = "insert into " + TABLE_NAME + " (nom_auteur) " + " VALUES (?);";

			final PreparedStatement statement = jdbcConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, toSave.getAuthor());
			statement.executeUpdate();
			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next() && rs != null)
			{
				toSave.setAuthorId(rs.getLong("id_auteur"));
			}
			LOGGER.info("Saved author " + toSave.getAuthor());
			connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Author findByName(String authorName)
	{
		connection = connector.getConnection();
		Author author = null;
		try
		{
			final String query = "select * from " + TABLE_NAME + " WHERE nom_auteur = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, authorName);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				author = createObjectFromResultSet(rs);
			}
			connection.close();
			return author;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Author createObjectFromResultSet(ResultSet rs) throws SQLException
	{
		Author author = null;
		try
		{
			author = new Author();
			author.setAuthorId(rs.getLong("id_auteur"));
			author.setAuthor(rs.getString("nom_auteur"));
		} catch (SQLException e)
		{
			LOGGER.error(e.getMessage());
		}
		return author;
	}

	public Author findById(long id)
	{
		connection = connector.getConnection();
		Author author = null;
		try
		{
			final String query = "select * from " + TABLE_NAME + " WHERE id_auteur = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				author = createObjectFromResultSet(rs);
			}
			connection.close();
			return author;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JDBCConnector getJDBCConnector()
	{
		return connector;
	}
}
