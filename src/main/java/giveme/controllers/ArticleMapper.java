package giveme.controllers;

import giveme.common.beans.Articles;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArticleMapper
{
	public Long id;
	public ArrayList<String> title;
	public String articleContent;
	public String aritcleCover;
	public ArrayList<String> description;
	public Long authorId;
	public Long category;
	public String url;
	@JsonProperty(value = "publication_date")
	public ArrayList<String> publicationDate;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public ArrayList<String> getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate(ArrayList<String> publicationDate)
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

	public String getArticleContent()
	{
		return articleContent;
	}

	public ArrayList<String> getTitle()
	{
		return title;
	}

	public void setTitle(ArrayList<String> title)
	{
		this.title = title;
	}

	public void setArticleContent(String articleContent)
	{
		this.articleContent = articleContent;
	}

	public String getAritcleCover()
	{
		return aritcleCover;
	}

	public void setAritcleCover(String aritcleCover)
	{
		this.aritcleCover = aritcleCover;
	}

	public ArrayList<String> getDescription()
	{
		return description;
	}

	public void setDescription(ArrayList<String> description)
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

	public Articles convert()
	{
		Articles cv = new Articles();
		cv.setAritcleCover(getAritcleCover());
		cv.setContent(getArticleContent());
		cv.setId(getId());
		cv.setAuthorId(getAuthorId());
		if (getDescription() != null)
		{
			cv.setDescription(getDescription().get(0));
		}
		cv.setTitle(getTitle().get(0));
		cv.setCategory(getCategory());
		cv.setUrl(getUrl());
		if (getPublicationDate() != null)
		{
			cv.setPublicationDate(getPublicationDate().get(0));
		}
		return cv;
	}
}
