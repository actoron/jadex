package jadex.transformation.jsonserializer.processors.read;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class JsonReadContext
{
	/** Already known objects */
	protected Map<Integer, Object> idobjects = new HashMap<Integer, Object>();
	protected List<Object> knownobjects = new ArrayList<Object>();
	
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
//		knownobjects.add(obj);
		if(idx>-1)
			idobjects.put(Integer.valueOf(idx), obj);
//		System.out.println("objs: "+knownobjects);
//		knownobjects.put(Integer.valueOf(knownobjects.size()), obj);
	}
	
	/**
	 *  Returns the known objects.
	 *  @return Known objects.
	 */
	public Object getKnownObject(int num)
	{
		try
		{
//			return knownobjects.get(num);
			return idobjects.get(num);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
//		knownobjects.put(Integer.valueOf(knownobjects.size()), obj);
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
