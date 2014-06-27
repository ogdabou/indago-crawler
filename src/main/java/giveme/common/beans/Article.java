package giveme.common.beans;

import org.codehaus.jackson.annotate.JsonProperty;

public class Article
{

	private Long id;
	private String title;
	private String content;
	private String aritcleCover;
	private String description;
	private Long authorId;
	private Long categoryId;
	private String url;
	private String sources;

	public String getSources()
	{
		return sources;
	}

	public void setSources(String sources)
	{
		this.sources = sources;
	}

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

	public Long getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(Long category)
	{
		categoryId = category;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return "[ url=" + url + ", categoryId=" + categoryId + ", authorId="
				+ authorId + ", " + " description=" + description
				+ ", articleCover=" + aritcleCover + ", content=" + content;
	}

	public void MergeWithDetailed(Article detailedArticle)
	{
		authorId = detailedArticle.getAuthorId();
		content = detailedArticle.getContent();
		sources = detailedArticle.getSources();
		title = detailedArticle.getTitle();
		url = detailedArticle.getUrl();
		publicationDate = detailedArticle.getPublicationDate();
	}
}
