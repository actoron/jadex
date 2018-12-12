package jadex.bdiv3.model;

/**
 *  Reference to another element.
 */
public class MElementRef extends MElement
{
	/** The referenced element name. */
	protected String ref;

	/** The exported flag. */
	protected boolean exported;
	
	/** The exported flag. */
	protected boolean result;
	
	/**
	 *  Get the ref.
	 *  @return The ref
	 */
	public String getRef()
	{
		return ref;
	}

	/**
	 *  The ref to set.
	 *  @param ref The ref to set
	 */
	public void setRef(String ref)
	{
		this.ref = internalName(ref);
	}

	/**
	 *  Get the exported.
	 *  @return The exported
	 */
	public boolean isExported()
	{
		return exported;
	}

	/**
	 *  The exported to set.
	 *  @param exported The exported to set
	 */
	public void setExported(boolean exported)
	{
		this.exported = exported;
	}

	/**
	 *  Get the result.
	 *  @return The result
	 */
	public boolean isResult()
	{
		return result;
	}

	/**
	 *  The result to set.
	 *  @param result The result to set
	 */
	public void setResult(boolean result)
	{
		this.result = result;
	}
	
}
