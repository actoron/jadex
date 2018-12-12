package jadex.bdiv3x.runtime;

import jadex.bdiv3.runtime.IBeliefListener;


/**
 *  The interface for all beliefs (concrete and referenced).
 */
public interface IBelief extends IElement
{
	/**
	 *  Set a fact of a belief.
	 *  Only changes the belief, if the new value is not equal to the old value.
	 *  @param fact The new fact.
	 *  @return True, if the value was changed.
	 */
	public boolean setFact(Object fact);

	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public Object getFact();

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
	public Class<?>	getClazz();

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
	public <T> void addBeliefListener(IBeliefListener<T> listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public <T> void removeBeliefListener(IBeliefListener<T> listener);
	
}
