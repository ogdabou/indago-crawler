package giveme.common.models.json;

import java.util.ArrayList;

public class ArticleMapper
{
	public ArrayList<String> title;
	public ArrayList<String> content;
	private ArrayList<String> author;
	public String url;
	public ArrayList<String> publication_date;
	private ArrayList<String> sources;

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

	public ArrayList<String> getContent()
	{
		return content;
	}

	public void setContent(ArrayList<String> content)
	{
		this.content = content;
	}

	public ArrayList<String> getAuthor()
	{
		return author;
	}

	public void setAuthor(ArrayList<String> author)
	{
		this.author = author;
	}

	public ArrayList<String> getPublication_date()
	{
		return publication_date;
	}

	public void setPublication_date(ArrayList<String> publication_date)
	{
		this.publication_date = publication_date;
	}

	public ArrayList<String> getSources()
	{
		return sources;
	}

	public void setSources(ArrayList<String> sources)
	{
		this.sources = sources;
	}

}
