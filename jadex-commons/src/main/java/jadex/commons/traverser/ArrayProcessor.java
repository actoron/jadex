package jadex.commons.traverser;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 *  An array processor allows for traversing arrays.
 */
class ArrayProcessor implements ITraverseProcessor
{
	/** The array processor. */
	protected boolean clone;
	
	/**
	 *  Create a new array processor.
	 */
	public ArrayProcessor()
	{
		this(false);
	}
	
	/**
	 *  Create a new array processor.
	 */
	public ArrayProcessor(boolean clone)
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
		return object.getClass().isArray();
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
		Object ret = getReturnObject(object, clazz);
		int length = Array.getLength(object);
		Class type = clazz.getComponentType();
		
		traversed.put(object, ret);
		for(int i=0; i<length; i++) 
		{
			Object val = Array.get(object, i);
			Object newval = traverser.traverse(val, type, traversed, processors);
			if(clone || newval!=val)
				Array.set(ret, i, newval);
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
			int length = Array.getLength(object);
			Class type = clazz.getComponentType();
			return Array.newInstance(type, length);
		}
		
		return ret;
	}
}