package jadex.gpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MContext
{
	//-------- attributes --------
	
	/** The elements. */
	protected List elements;
	
	//-------- methods --------
	
	/**
	 *  Get the elements.
	 *  @return The elements.
	 */
	public List getElements()
	{
		return elements;
	}

	/**
	 *  Add a context element.
	 *  @param param The context element.
	 */
	public void addContextElement(MContextElement element)
	{
		if(elements==null)
			elements = new ArrayList();
		elements.add(element);
	}
	
	/**
	 *  Remove a context element.
	 *  @param param The context element.
	 */
	public void removeContextElement(MContextElement element)
	{
		if(elements!=null)
			elements.remove(element);
	}
}
