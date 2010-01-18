package jadex.wfms.parametertypes;

public class ListChoice
{
	private String[] choices;
	
	private String selection;
	
	public ListChoice()
	{
		choices = new String[0];
	}
	
	public ListChoice(String[] choices)
	{
		this.choices = choices;
	}
	
	public String[] getChoices()
	{
		return choices;
	}
	
	public void setChoices(String[] choices)
	{
		this.choices = choices;
	}
	
	public String getSelection()
	{
		return selection;
	}
	
	public void setSelection(String selection)
	{
		this.selection = selection;
	}
}
