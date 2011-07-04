package jadex.wfms.parametertypes;


public class Text
{
	private String text;
	
	public Text()
	{
		this("");
	}
	
	public Text(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String toString()
	{
		return text;
	}
}
