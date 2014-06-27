package giveme.common.beans;

import org.codehaus.jackson.annotate.JsonProperty;

public class Articles
{

	private Long id;
	private String title;
	private String content;
	private String aritcleCover;
	private String description;
	private Long authorId;
	private Long category;
	private String url;

	@JsonProperty(value = "publication_date")
	public String publicationDate;

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

	public Long getAuthorId()
	{
		return authorId;
	}

	public void setAuthorId(Long authorId)
	{
		this.authorId = authorId;
	}

	public Long getCategory()
	{
		return category;
	}

	public void setCategory(Long category)
	{
		this.category = category;
	}

}
