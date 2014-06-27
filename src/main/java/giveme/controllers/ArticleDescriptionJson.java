package giveme.controllers;

import giveme.common.beans.ArticleDetails;

import java.util.ArrayList;

public class ArticleDescriptionJson
{
	private ArrayList<String> article_url;
	private ArrayList<String> description;
	private ArrayList<String> imagUrl;
	private String url;
	private ArrayList<String> title;
	private ArrayList<String> publicationDate;

	public ArrayList<String> getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate(ArrayList<String> publicationDate)
	{
		this.publicationDate = publicationDate;
	}

	public ArrayList<String> getArticle_url()
	{
		return article_url;
	}

	public void setArticle_url(ArrayList<String> article_url)
	{
		this.article_url = article_url;
	}

	public ArrayList<String> getDescription()
	{
		return description;
	}

	public void setDescription(ArrayList<String> description)
	{
		this.description = description;
	}

	public ArrayList<String> getImagUrl()
	{
		return imagUrl;
	}

	public void setImagUrl(ArrayList<String> imagUrl)
	{
		this.imagUrl = imagUrl;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public ArrayList<String> getTitle()
	{
		return title;
	}

	public void setTitle(ArrayList<String> title)
	{
		this.title = title;
	}

	public ArticleDetails convert()
	{
		ArticleDetails articleDetails = new ArticleDetails();
		if (getArticle_url() != null)
		{
			articleDetails.setArticle_url(getArticle_url().get(0));
		}
		if (getDescription() != null)
		{
			articleDetails.setDescription(getDescription().get(0));
		}
		if (getImagUrl() != null)
		{
			articleDetails.setImageUrl(getImagUrl().get(0));
		}
		if (getTitle() != null)
		{
			articleDetails.setTitle(getTitle().get(0));
		}
		articleDetails.setUrl(getUrl());
		return articleDetails;
	}
}