package giveme.common.dao;

import giveme.common.beans.Articles;
import giveme.controllers.JDBCConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class ArticleDao
{
	private final String TABLE_NAME = "articles";
	public static Logger LOGGER = Logger.getLogger(ArticleDao.class.getName());

	@Autowired
	JDBCConnector connector;

	private Connection jdbcConnection;

	public List<Articles> list()
	{
		jdbcConnection = connector.getConnection();

		final List<Articles> articleList = new ArrayList<>();

		try
		{
			final String query = "select * from " + TABLE_NAME;
			final ResultSet rs = jdbcConnection.createStatement().executeQuery(
					query);
			while (rs.next())
			{
				final Articles ar = createArticleFromResultSet(rs);
				articleList.add(ar);
			}
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}

		return articleList;
	}

	public void save(Articles ar)
	{
		jdbcConnection = connector.getConnection();

		final List<Articles> articleList = new ArrayList<>();

		try
		{
			final String query = "insert into " + TABLE_NAME
					+ "(titre_article, contenu_article, "
					+ "couv_article, description, id_auteur, id_categorie)"
					+ " VALUES (?, ?, ?, ?, ?, ?)";
			final ResultSet rs = jdbcConnection.createStatement().executeQuery(
					query);
			while (rs.next())
			{
				final PreparedStatement statement = jdbcConnection
						.prepareStatement(query,
								Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, ar.getTitle());
				statement.setString(2, ar.getContent());
				statement.setString(3, "");
				statement.setString(4, ar.getDescription());
				statement.setLong(5, ar.getAuthorId());
				statement.setLong(6, ar.getCategory());
				ResultSet idresult = statement.getGeneratedKeys();
				if (idresult.next() && idresult != null)
				{
					ar.setId(idresult.getLong("id_article"));
				}
			}
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}
	}

	private Articles createArticleFromResultSet(ResultSet rs)
	{
		Articles ar = null;
		try
		{
			ar = new Articles();
			ar.setContent(rs.getString("contenu_article"));
			ar.setAuthorId(rs.getLong("id_auteur"));
			ar.setCategory(rs.getLong("id_categorie"));
			ar.setTitle(rs.getString("titre_article"));
			ar.setId(rs.getLong("id_article"));
			ar.setDescription(rs.getString("description"));
			LOGGER.info("added " + ar);
		} catch (SQLException e)
		{
			LOGGER.error(e.getMessage());
		}
		return ar;
	}
}
