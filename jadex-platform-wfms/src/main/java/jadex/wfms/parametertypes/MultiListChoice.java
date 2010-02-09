package jadex.wfms.parametertypes;

/**
 * Class for multiple-choice lists.
 *
 */
public class MultiListChoice
{
	private Object[] choices;
	
	private Object[] selections;
	
	public MultiListChoice()
	{
		this(new Object[0]);
	}
	
	public MultiListChoice(Object[] choices)
	{
		this(choices, new Object[0]);
	}
	
	public MultiListChoice(Object[] choices, Object[] selections)
	{
		this.choices = choices;
		this.selections = selections;
	}

	/**
	 *  Get the choices.
	 *  @return The choices.
	 */
	public Object[] getChoices()
	{
		return choices;
	}

	/**
	 *  Set the choices.
	 *  @param choices The choices to set.
	 */
	public void setChoices(Object[] choices)
	{
		this.choices = choices;
	}

	/**
	 *  Get the selections.
	 *  @return The selections.
	 */
	public Object[] getSelections()
	{
		return selections;
	}

	/**
	 *  Set the selections.
	 *  @param selections The selections to set.
	 */
	public void setSelections(Object[] selections)
	{
		this.selections = selections;
	}
}
