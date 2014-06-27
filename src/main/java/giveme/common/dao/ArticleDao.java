package giveme.common.dao;

import giveme.common.beans.Article;
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

	public List<Article> list()
	{
		jdbcConnection = connector.getConnection();

		final List<Article> articleList = new ArrayList<>();

		try
		{
			final String query = "select * from " + TABLE_NAME;
			final ResultSet rs = jdbcConnection.createStatement().executeQuery(
					query);
			while (rs.next())
			{
				final Article ar = createArticleFromResultSet(rs);
				articleList.add(ar);
			}
			jdbcConnection.close();
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage());
		}

		return articleList;
	}

	public void save(Article ar)
	{
		jdbcConnection = connector.getConnection();

		try
		{
			final String query = "insert into " + TABLE_NAME
					+ " (titre_article, contenu_article, "
					+ "couv_article, description, id_auteur, id_categorie)"
					+ " VALUES (?, ?, ?, ?, ?, ?)";

			final PreparedStatement statement = jdbcConnection
					.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, "coucou");
			statement.setString(2, ar.getContent());
			statement.setString(3, ar.getAritcleCover());
			statement.setString(4, ar.getDescription());
			statement.setLong(5, ar.getAuthorId());
			statement.setLong(6, ar.getCategoryId());
			statement.executeUpdate();
			ResultSet idresult = statement.getGeneratedKeys();
			if (idresult.next() && idresult != null)
			{
				ar.setId(idresult.getLong("id_article"));
			}

			jdbcConnection.close();
		} catch (Exception e)
		{
			e.printStackTrace();

		}
	}

	private Article createArticleFromResultSet(ResultSet rs)
	{
		Article ar = null;
		try
		{
			ar = new Article();
			ar.setContent(rs.getString("contenu_article"));
			ar.setAuthorId(rs.getLong("id_auteur"));
			ar.setCategoryId(rs.getLong("id_categorie"));
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
