package giveme.controllers;

import java.util.ArrayList;

public class ArticleDescriptionMapper
{
	private ArrayList<String> category;
	private ArrayList<ArticleDescriptionJson> variants;
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

	public ArrayList<ArticleDescriptionJson> getVariants()
	{
		return variants;
	}

	public void setVariants(ArrayList<ArticleDescriptionJson> variants)
	{
		this.variants = variants;
	}
}
