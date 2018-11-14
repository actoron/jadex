package jadex.bdiv3.runtime.wrappers;

/**
 * 
 */
public interface IEventPublisher
{
	/**
	 *  An entry was added to the collection.
	 */
	public void entryAdded(Object value, int index);
	
	/**
	 *  An entry was removed from the collection.
	 */
	public void entryRemoved(Object value, int index);
	
	/**
	 *  An entry was changed in the collection.
	 */
	public void entryChanged(Object oldvalue, Object newvalue, int index);
	
	/**
	 *  An entry was added to the map.
	 */
	public void	entryAdded(Object key, Object value);
	
	/**
	 *  An entry was removed from the map.
	 */
	public void	entryRemoved(Object key, Object value);
	
	/**
	 *  An entry was changed in the map.
	 */
	public void	entryChanged(Object key, Object oldvalue, Object newvalue);
}
