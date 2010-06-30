package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  Interface for the waitqueue of plans. The waitqueue makes
 *  a plan plan responsive to events permanently. 
 */
public interface IEAWaitqueue extends IEAWaitAbstraction
{
	/**
	 *  Get all collected elements.
	 *  @return The elements.
	 */
//	public Object[] getElements();
	public IFuture getElements();

	/**
	 *  Get and remove the next element.
	 *  @return The next element (or null if none).
	 */
//	public Object removeNextElement();
	public IFuture removeNextElement();	

	/**
	 *  Remove an element.
	 *  @param element The element to remove.
	 */
	public IFuture removeElement(Object element);
	
	/**
	 *  Get the number of events in the waitqueue.
	 *  @return The size of the waitqueue.
	 */
//	public int size();
	public IFuture size();

	/**
	 *  Test if the waitqueue is empty.
	 *  @return True, if empty.
	 */
//	public boolean isEmpty();
	public IFuture isEmpty();
}