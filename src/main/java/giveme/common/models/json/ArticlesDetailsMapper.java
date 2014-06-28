package giveme.common.models.json;

import java.util.ArrayList;

public class ArticlesDetailsMapper
{
	private ArrayList<String> category;
	private ArrayList<ArticlesDetailsJson> variants;
	private String url;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public ArrayList<String> getCategory()
	{
		return category;
	}

	public void setCategory(ArrayList<String> category)
	{
		this.category = category;
	}

	public ArrayList<ArticlesDetailsJson> getVariants()
	{
		return variants;
	}

	public void setVariants(ArrayList<ArticlesDetailsJson> variants)
	{
		this.variants = variants;
	}
}
