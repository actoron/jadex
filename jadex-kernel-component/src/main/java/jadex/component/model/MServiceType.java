package jadex.component.model;


/**
 * 
 */
public class MServiceType extends MExpressionType
{
	//-------- attributes --------

	/** The from. */
	protected String from;

	//-------- constructors --------

	/**
	 *  Create a new expression.
	 */
	public MServiceType()
	{
	}

	//-------- methods --------
	
	/**
	 *  Get the from.
	 *  @return the from.
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 *  Set the from.
	 *  @param from The from to set.
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}
}
