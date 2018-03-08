package jadex.commons.transformation;

/**
 *  Self-referencing bean.
 *
 */
public class E
{
	protected E selfreference;
	
	/**
	 *  Gets the self-reference.
	 *  @param selfreference The reference.
	 */
	public E getSelfReference()
	{
		return selfreference;
	}
	
	/**
	 *  Sets the self-reference.
	 *  @param selfreference The reference.
	 */
	public void setSelfReference(E selfreference)
	{
		this.selfreference = selfreference;
	}
}
