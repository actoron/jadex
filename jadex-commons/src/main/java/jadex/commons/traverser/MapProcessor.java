package jadex.commons.traverser;

import jadex.commons.SReflect;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  A map processor allows for traversing maps.
 */
public class MapProcessor implements ITraverseProcessor
{
	/** The clone falg. */
	protected boolean clone;
	
	/**
	 *  Create a new map processor.
	 */
	public MapProcessor()
	{
		this(false);
	}
	
	/**
	 *  Create a new map processor.
	 */
	public MapProcessor(boolean clone)
	{
		this.clone = clone;
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz)
	{
		return SReflect.isSupertype(Map.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
		Map ret = (Map)getReturnObject(object, clazz);
		Map map = (Map)object;
		
		traversed.put(object, ret);
		for(Iterator<Object> it=map.keySet().iterator(); it.hasNext(); )
		{
			Object key = it.next();
			Object val = map.get(key);
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors);
			
			if(clone || newval!=val)
				ret.put(key, newval);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class clazz)
	{
		Object ret = object;
		
		if(clone)
		{
			try
			{
				ret = clazz.newInstance();
			}
			catch(Exception e)
			{
				// Using linked hash map as default to avoid loosing order if has order.
				ret = new LinkedHashMap();
			}
		}
		
		return ret;
	}
}