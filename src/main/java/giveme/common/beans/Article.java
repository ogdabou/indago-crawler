package giveme.common.beans;

import org.codehaus.jackson.annotate.JsonProperty;

public class Article
{

	private Long		id;
	private String		title;
	private String		content;
	private String		aritcleCover;
	private String		description;
	private Author		author;
	private String		url;
	private String		sources;
	private Categorie	categorie;

	public Article()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * Used by tests
	 *
	 * @param url
	 * @param title
	 */
	public Article(String title, String url)
	{
		this.title = title;
		this.url = url;
	}

	public String getSources()
	{
		return sources;
	}

	public void setSources(String sources)
	{
		this.sources = sources;
	}

	@JsonProperty(value = "publication_date")
	public String	publicationDate;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate)
	{
		this.publicationDate = publicationDate;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String articleContent)
	{
		content = articleContent;
	}

	public String getAritcleCover()
	{
		return aritcleCover;
	}

	public void setAritcleCover(String aritcleCover)
	{
		this.aritcleCover = aritcleCover;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor(Author author)
	{
		this.author = author;
	}

	public Categorie getCategorie()
	{
		return categorie;
	}

	public void setCategorie(Categorie categorie)
	{
		this.categorie = categorie;
	}

	@Override
	public String toString()
	{
		return "[ url=" + url + ", category=" + categorie.getCategory() + ", author=" + author.getAuthor() + ", "
				+ " description=" + description + ", articleCover=" + aritcleCover + ", content=" + content;
	}

	public void fillMissingParams(Article detailedArticle)
	{
		if (detailedArticle.getAuthor() != null)
		{
			author = detailedArticle.getAuthor();
		}
		if (detailedArticle.getContent() != null)
		{
			content = detailedArticle.getContent();
		}
		if (detailedArticle.getSources() != null)
		{
			sources = detailedArticle.getSources();
		}
		if (detailedArticle.getTitle() != null)
		{
			title = detailedArticle.getTitle();
		}
		if (detailedArticle.getUrl() != null)
		{
			url = detailedArticle.getUrl();
		}
		if (detailedArticle.getPublicationDate() != null)
		{
			publicationDate = detailedArticle.getPublicationDate();
		}

	}
}
