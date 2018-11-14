package jadex.wfms.parametertypes;

public class ListChoice
{
	private Object[] choices;
	
	private Object selection;
	
	public ListChoice()
	{
		this(new Object[0]);
	}
	
	public ListChoice(Object[] choices)
	{
		this(choices, null);
	}
	
	public ListChoice(Object[] choices, Object selection)
	{
		this.choices = choices;
		this.selection = selection;
	}
	
	public Object[] getChoices()
	{
		return choices;
	}
	
	public void setChoices(Object[] choices)
	{
		this.choices = choices;
	}
	
	public Object getSelection()
	{
		return selection;
	}
	
	public void setSelection(Object selection)
	{
		this.selection = selection;
	}
}
