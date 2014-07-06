package giveme.common.dao;

import giveme.common.beans.Article;
import giveme.common.services.JDBCConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class ArticleDao extends IDao<Article>
{
	@Autowired
	JDBCConnector					connector;

	@Autowired
	CategorieDao					categorieDao;

	@Autowired
	AuthorDao						authorDao;

	private final SimpleDateFormat	creationDateFormat	= new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");

	public ArticleDao()
	{
		TABLE_NAME = "articles";
		LOGGER = Logger.getLogger(ArticleDao.class.getName());
	}

	@Override
	public void save(Article ar)
	{
		connection = connector.getConnection();

		try
		{
			final String query = "insert into "
					+ TABLE_NAME
					+ " (titre_article, contenu_article, "
					+ "couv_article, description, id_auteur, id_categorie, url, publication_date, sources, creation_date)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			final PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, ar.getTitle());
			statement.setString(2, ar.getContent());
			statement.setString(3, ar.getAritcleCover());
			statement.setString(4, ar.getDescription());
			statement.setLong(5, ar.getAuthor().getAuthorId());
			statement.setLong(6, ar.getCategorie().getCategoryId());
			statement.setString(7, ar.getUrl());
			statement.setString(8, ar.getPublicationDate());
			statement.setString(9, ar.getSources());
			Timestamp creationTimeStamp = new Timestamp(creationDateFormat.parse(creationDateFormat.format(new Date()))
					.getTime());
			statement.setTimestamp(10, creationTimeStamp);
			statement.executeUpdate();
			ResultSet idresult = statement.getGeneratedKeys();
			if (idresult.next() && idresult != null)
			{
				ar.setId(idresult.getLong("id_article"));
			}
			LOGGER.debug("Saved Article " + ar.getTitle());
			connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Article findByTitle(String title)
	{
		connection = connector.getConnection();
		Article article = null;
		try
		{
			final String query = "select * from " + TABLE_NAME + " WHERE titre_article = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, title);
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				article = createObjectFromResultSet(rs);
			}
			connection.close();
			return article;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Article createObjectFromResultSet(ResultSet rs)
	{
		Article ar = null;
		try
		{
			ar = new Article();
			ar.setContent(rs.getString("contenu_article"));
			ar.setAuthor(authorDao.findById(rs.getLong("id_auteur")));
			ar.setCategorie(categorieDao.findById(rs.getLong("id_categorie")));
			ar.setTitle(rs.getString("titre_article"));
			ar.setId(rs.getLong("id_article"));
			ar.setDescription(rs.getString("description"));
			ar.setPublicationDate(rs.getString("publication_date"));
			ar.setUrl(rs.getString("url"));
			ar.setSources(rs.getString("sources"));
		} catch (SQLException e)
		{
			LOGGER.error(e.getMessage());
		}
		return ar;
	}

	public void update(Article ar)
	{
		connection = connector.getConnection();

		try
		{
			final String query = "update "
					+ TABLE_NAME
					+ " set titre_article = ?, contenu_article = ?, "
					+ "couv_article = ?, description = ?, id_auteur = ?, id_categorie = ?, url = ?, publication_date = ?, sources = ?";

			final PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, ar.getTitle());
			statement.setString(2, ar.getContent());
			statement.setString(3, ar.getAritcleCover());
			statement.setString(4, ar.getDescription());
			statement.setLong(5, ar.getAuthor().getAuthorId());
			statement.setLong(6, ar.getCategorie().getCategoryId());
			statement.setString(7, ar.getUrl());
			statement.setString(8, ar.getPublicationDate());
			statement.setString(9, ar.getSources());
			statement.executeUpdate();
			LOGGER.debug("Updated Article " + ar.getTitle());
			connection.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public JDBCConnector getJDBCConnector()
	{
		return connector;
	}
}
