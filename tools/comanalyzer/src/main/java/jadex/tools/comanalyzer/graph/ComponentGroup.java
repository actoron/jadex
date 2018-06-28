package jadex.tools.comanalyzer.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Base class for Groups (of Agents or Messages) in the
 * ComponentGroupMultiGraph.
 */
public class ComponentGroup implements IComponentGroup
{

	//-------- attributes --------

	/** The elements of the group */
	protected List elements;

	//-------- constructors --------

	/**
	 * Create a new group.
	 */
	public ComponentGroup()
	{
		elements = new ArrayList();
	}

	/**
	 * Create a new group with given elements.
	 * 
	 * @param elements The elements of the new group.
	 */
	public ComponentGroup(List elements)
	{
		this.elements = elements;
	}

	//-------- IComponentGroup interface --------

	/**
	 * Add an element to the group.
	 * @param k The element to add.
	 */
	public void addElement(Object k)
	{
		elements.add(k);
	}

	/**
	 * Remove an element from the group.
	 * @param k The element to remove.
	 */
	public void removeElement(Object k)
	{
		elements.remove(k);
	}

	/**
	 * Returns a list of the elements.
	 * @return The list of element.
	 */
	public List getElements()
	{
		return elements;
	}

	/**
	 * Returns the size (number of elements) for the group.
	 * @return The size of the group.
	 */
	public int size()
	{
		return elements.size();
	}

	/**
	 * Returns the iterator over the elements.
	 * @return The iterator
	 */
	public Iterator iterator()
	{
		return elements.iterator();
	}

	/**
	 * Check if the group contains the given element.
	 * @param k The element to check.
	 * @return <code>true</code> if the group contains the element.
	 */
	public boolean contains(Object k)
	{
		return elements.contains(k);
	}

	//-------- ComponentGroup methods --------

	/**
	 * If there is only one element in the group, return this element, else
	 * <code>null</code>
	 * @return The element if there is only one element in the group.
	 */
	public Object getSingelton()
	{
		return elements.size() == 1 ? elements.get(0) : null;
	}

	/**
	 * Returns <code>true</code>, if there is only one one element in the group.
	 * @return <code>true</code> if the size of the group is one.
	 */
	public boolean isSingelton()
	{
		return (elements.size() == 1);
	}

}