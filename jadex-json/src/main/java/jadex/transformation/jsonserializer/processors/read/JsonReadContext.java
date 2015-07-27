package jadex.transformation.jsonserializer.processors.read;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class JsonReadContext
{
	/** Already known objects */
//	protected Map<Integer, Object> knownobjects;
	public List<Object> knownobjects = new ArrayList<Object>();
	
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
	public void addKnownObject(Object obj)
	{
		knownobjects.add(obj);
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
			return knownobjects.get(num);
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
