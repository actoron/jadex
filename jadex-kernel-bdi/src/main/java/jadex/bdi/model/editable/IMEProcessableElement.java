package jadex.bdi.model.editable;

import jadex.bdi.model.IMProcessableElement;

/**
 * 
 */
public interface IMEProcessableElement extends IMProcessableElement, IMEParameterElement
{
	/**
	 *  Test if is posttoall.
	 */
	public void setPostToAll(boolean posttoall);
	
	/**
	 *  Test if is random selection.
	 */
	public void setRandomSelection(boolean random);
}
