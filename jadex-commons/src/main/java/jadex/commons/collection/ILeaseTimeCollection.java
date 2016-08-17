package jadex.commons.collection;

import java.util.Collection;

/**
 *  Special methods for a lease time collection.
 */
public interface ILeaseTimeCollection<E> extends Collection<E>
{
	/**
	 *  Add a new entry.
	 *  @param e The entry.
	 *  @param leasetime The leasetime.
	 *  @return True, if new entry.
	 */
	public boolean add(E e, long leasetime);
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public boolean update(E e);
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public boolean update(E e, long leasetime);
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public void touch(E e);
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public void touch(E e, long leasetime);
}
