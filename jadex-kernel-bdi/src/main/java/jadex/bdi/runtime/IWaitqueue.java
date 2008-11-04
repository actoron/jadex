package jadex.bdi.runtime;

/**
 *  Interface for the waitqueue of plans. The waitqueue makes
 *  a plan plan responsive to events permanently. 
 */
public interface IWaitqueue extends IWaitAbstraction
{
	/**
	 *  Get all collected elements.
	 *  @return The elements.
	 */
	public Object[] getElements();

	/**
	 *  Get and remove the next element.
	 *  @return The next element (or null if none).
	 */
	public Object removeNextElement();
	

	/**
	 *  Remove an element.
	 *  @param element The element to remove.
	 */
	public void removeElement(Object element);
	
	/**
	 *  Get the number of events in the waitqueue.
	 *  @return The size of the waitqueue.
	 */
	public int	size();

	/**
	 *  Test if the waitqueue is empty.
	 *  @return True, if empty.
	 */
	public boolean	isEmpty();
}
