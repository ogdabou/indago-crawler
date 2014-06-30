package giveme.common.dao;

import giveme.common.beans.Categorie;
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
public class CategorieDao extends IDao<Categorie>
{

	public CategorieDao()
	{
		TABLE_NAME = "\"Categorie\"";
		LOGGER = Logger.getLogger(CategorieDao.class.getName());
	}

	@Autowired
	JDBCConnector		connector;

	private Connection	jdbcConnection;

	@Override
	public void save(Categorie toSave)
	{

		jdbcConnection = connector.getConnection();

		try
		{
			final String query = "insert into " + TABLE_NAME + " (libelle_categorie) " + " VALUES (?);";

			final PreparedStatement statement = jdbcConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, toSave.getCategory());
			statement.executeUpdate();
			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next() && rs != null)
			{
				toSave.setCategoryId(rs.getLong("id_categorie"));
			}
			jdbcConnection.close();
			LOGGER.info("Saved category " + toSave.getCategory());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Categorie findByName(String categoryName)
	{
		connection = connector.getConnection();
		Categorie cat = null;
		try
		{
			final String query = "select * from " + TABLE_NAME + " WHERE libelle_categorie = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, categoryName);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				cat = createObjectFromResultSet(rs);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return cat;
	}

	@Override
	public Categorie createObjectFromResultSet(ResultSet rs) throws SQLException
	{
		Categorie cat = null;
		try
		{
			cat = new Categorie();
			cat.setCategory(rs.getString("libelle_categorie"));
			cat.setCategoryId(rs.getLong("id_categorie"));
		} catch (SQLException e)
		{
			LOGGER.error(e.getMessage());
		}
		return cat;
	}
}
