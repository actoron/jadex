package jadex.bdi.runtime;

/**
 *  Interface for all processable elements (goals, internal and message events).
 */
// Todo: other interface that includes change events (for plan.getReason())
public interface IProcessableElement extends IParameterElement
{
	//-------- BDI event properties --------

	/**
	 *  Is it a post-to-all event.
	 *  @return True, if post-to-all is set.
	 */
	public boolean isPostToAll();

	/**
	 *  Get the random selection flag.
	 *  @return True, when applicable
	 *  selection is random style.
	 */
	public boolean	isRandomSelection();
}
