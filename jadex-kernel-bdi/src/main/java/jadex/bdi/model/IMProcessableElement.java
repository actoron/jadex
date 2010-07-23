package jadex.bdi.model;


/**
 *  Interface for processable element model.
 */
public interface IMProcessableElement extends IMParameterElement
{
	/**
	 *  Test if is posttoall.
	 *  @return True, if posttoaall.
	 */
	public boolean isPostToAll();
	
	/**
	 *  Test if is random selection.
	 *  @return True, if is random selection.
	 */
	public boolean isRandomSelection();
}
