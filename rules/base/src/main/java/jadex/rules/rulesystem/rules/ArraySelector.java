package jadex.rules.rulesystem.rules;

/**
 *	Represents an access to an array.
 */
public class ArraySelector
{
	//-------- attributes --------
	
	/** The value source for the index. */
	protected Object	indexsource;
	
	//-------- constructors --------
	
	/**
	 *  Create a new array selector.
	 *  @param	indexsource	The value source for the index.
	 */
	public ArraySelector(Object indexsource)
	{
		this.indexsource	= indexsource;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the index source.
	 *  @return	The value source for the index.
	 */
	public Object	getIndexSource()
	{
		return indexsource;
	}
}
