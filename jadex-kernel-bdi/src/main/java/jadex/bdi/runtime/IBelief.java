package jadex.bdi.runtime;

import jadex.bdi.model.IMElement;



/**
 *  The interface for all beliefs (concrete and referenced).
 */
public interface IBelief extends IElement
{
	/**
	 *  Set a fact of a belief.
	 *  @param fact The new fact.
	 */
	public void setFact(Object fact);

	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public Object	getFact();

	/**
	 *  Indicate that the fact of this belief was modified.
	 *  Calling this method causes an internal fact changed
	 *  event that might cause dependent actions.
	 */
	public void modified();
	
	/**
	 *  Get the value class.
	 *  @return The valuec class.
	 */
	public Class	getClazz();

	/**
	 *  Is this belief accessable.
	 *  @return False, if the belief cannot be accessed.
	 */
//	public boolean isAccessible();
	
	//-------- listeners --------
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(IBeliefListener listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(IBeliefListener listener);
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement();
}
