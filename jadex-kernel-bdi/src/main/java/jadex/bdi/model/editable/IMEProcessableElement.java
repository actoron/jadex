package jadex.bdi.model.editable;

import jadex.bdi.model.IMParameterElement;
import jadex.bdi.model.IMProcessableElement;

/**
 * 
 */
public interface IMEProcessableElement extends IMProcessableElement, IMParameterElement
{
	/**
	 *  Test if is posttoall.
	 *  @return True, if posttoaall.
	 */
	public void setPostToAll(boolean posttoall);
	
	/**
	 *  Test if is random selection.
	 *  @return True, if is random selection.
	 */
	public void setRandomSelection(boolean random);
}
