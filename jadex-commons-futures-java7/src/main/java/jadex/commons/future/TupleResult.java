package jadex.commons.future;

/**
 *  Used by tuple futures as internal result wrappers.
 *  
 *  Helper struct for results that saves the result number.
 */
public class TupleResult
{
	/** The number. */
	protected int num;
	
	/** The result. */
	protected Object result;

	/**
	 *  Create a new TupleResult. 
	 */
	public TupleResult()
	{
	}

	/**
	 *  Create a new SequenceResult. 
	 */
	public TupleResult(int num, Object result)
	{
		this.num = num;
		this.result = result;
	}

	/**
	 *  Get the num.
	 *  @return The num.
	 */
	public int getNum()
	{
		return num;
	}

	/**
	 *  Set the num.
	 *  @param num The num to set.
	 */
	public void setNum(int num)
	{
		this.num = num;
	}

	/**
	 *  Get the result.
	 *  @return The result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "("+num+": "+result+")";
	}
}

