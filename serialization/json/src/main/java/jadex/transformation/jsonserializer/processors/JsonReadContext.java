package jadex.transformation.jsonserializer.processors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import jadex.commons.transformation.traverser.IUserContextContainer;

/**
 * 
 */
public class JsonReadContext implements IUserContextContainer
{
	/** Already known objects */
//	protected Map<Integer, Object> idobjects = new HashMap<Integer, Object>();
//	protected List<Object> knownobjects = new ArrayList<Object>();
	protected Map<Integer, Object> idobjects = new HashMap<Integer, Object>();
	
	protected LinkedList<Integer> idstack = new LinkedList<Integer>();
	
	protected Object usercontext;
	
//	/** Flag if next object should be ignored in known objects. */
//	public boolean ignorenext;
	
//	/**
//	 *  Returns the known objects.
//	 *  @return Known objects.
//	 */
//	public Map<Integer, Object> getKnownObjects()
//	{
//		return knownobjects;
//	}
	
	/**
	 *  Returns the known objects.
	 *  @return Known objects.
	 */
	public void addKnownObject(Object obj, int idx)
	{
		if(idx>-1)
		{
			idobjects.put(idx, obj);
			idstack.set(0, idx);
		}
	}
	
	public void pushIdStack()
	{
		idstack.push(null);
	}
	
	
	public Integer popIdStack()
	{
		return idstack.pop();
	}
	
	/**
	 *  Returns the known objects.
	 *  @return Known objects.
	 */
	public Object getKnownObject(int num)
	{
		return idobjects.get(num);
	}
	
	/**
	 *  Returns the known objects.
	 *  @return Known objects.
	 */
	public void setKnownObject(int num, Object obj)
	{
		idobjects.put(num, obj);
	}
	
	/**
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}
	
	/**
	 *  Sets the user context.
	 *  @param usercontext The user context.
	 */
	public void setUserContext(Object usercontext)
	{
		this.usercontext = usercontext;
	}

//	/**
//	 *  Get the ignorenext. 
//	 *  @return The ignorenext
//	 */
//	public boolean isIgnoreNext()
//	{
//		return ignorenext;
//	}
//
//	/**
//	 *  Set the ignorenext.
//	 *  @param ignorenext The ignorenext to set
//	 */
//	public void setIgnoreNext(boolean ignorenext)
//	{
//		this.ignorenext = ignorenext;
//	}
	
}
