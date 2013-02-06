package jadex.rules.eca;

/**
 *  Interface for an event.
 */
public interface IEvent
{
	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getType(); //EventType

	/**
	 *  Get the content.
	 *  @return the content.
	 */
	public Object getContent();
}
