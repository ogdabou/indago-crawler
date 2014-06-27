package giveme.common.beans;

public class ArticleDetails
{
	private String article_url;
	private String description;
	private String imageUrl;
	private String url;
	private String title;

	public String getArticle_url()
	{
		return article_url;
	}

	public void setArticle_url(String article_url)
	{
		this.article_url = article_url;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String toString()
	{
		return "[ " + "title=" + title + ", url=" + url + ", description="
				+ description + ", img=" + imageUrl + ", art_url="
				+ article_url;
	}

}
