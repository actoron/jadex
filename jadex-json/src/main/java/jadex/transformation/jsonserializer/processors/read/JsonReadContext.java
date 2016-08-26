package jadex.transformation.jsonserializer.processors.read;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 
 */
public class JsonReadContext
{
	/** Already known objects */
//	protected Map<Integer, Object> idobjects = new HashMap<Integer, Object>();
//	protected List<Object> knownobjects = new ArrayList<Object>();
	protected Map<Integer, Object> idobjects = new HashMap<Integer, Object>();
	
	protected LinkedList<Integer> idstack = new LinkedList<Integer>();
	
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
