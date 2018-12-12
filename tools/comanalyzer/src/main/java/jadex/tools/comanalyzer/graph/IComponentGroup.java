package jadex.tools.comanalyzer.graph;

import java.util.Iterator;
import java.util.List;


/**
 * The interface for element groups in the ComponentGroupMultiGraph.
 */
public interface IComponentGroup
{

	/**
	 * Adds an element to the group
	 * 
	 * @param k The element.
	 */
	void addElement(Object k);

	/**
	 * Removes an element from the group
	 * 
	 * @param k The element.
	 */
	void removeElement(Object k);

	/**
	 * @return The size of the group.
	 */
	int size();

	/**
	 * @return The iterator of the group.
	 */
	Iterator iterator();

	/**
	 * @return The element list.
	 */
	List getElements();

	/**
	 * Returns <code>true</code> if the element is in the group.
	 * 
	 * @param k The element.
	 * @return <code>true</code> if the element is in the group.
	 */
	boolean contains(Object k);

}