package jadex.rules.eca;

/**
 * 
 */
public interface IEvent
{
	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getType();

	/**
	 *  Get the content.
	 *  @return the content.
	 */
	public Object getContent();
}
