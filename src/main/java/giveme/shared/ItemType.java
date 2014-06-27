package giveme.shared;

public enum ItemType
{
	ARTICLE("article"), ARTICLE_DETAILS("article_description");

	private String type;

	private ItemType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

}
