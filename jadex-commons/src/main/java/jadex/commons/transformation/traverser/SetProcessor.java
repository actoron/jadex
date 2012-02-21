package jadex.commons.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  A set processor allows for traversing set.
 */
public class SetProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return SReflect.isSupertype(Set.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone)
	{
		Set ret = (Set)getReturnObject(object, clazz, clone);
		Set set = (Set)object;
		
		traversed.put(object, ret);
		
		Object[] vals = set.toArray(new Object[set.size()]);
		for(int i=0; i<vals.length; i++)
		{
			Class valclazz = vals[i]!=null? vals[i].getClass(): null;
			Object newval = traverser.traverse(vals[i], valclazz, traversed, processors, clone);
			
			if(clone)
			{
				ret.add(newval);
			}
			else if(vals[i]!=newval)
			{
				// todo: could cause problems when not cloning but ordered set
				ret.remove(vals[i]);
				ret.add(newval);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the return object.
	 */
	public Object getReturnObject(Object object, Class clazz, boolean clone)
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
				ret = new LinkedHashSet();
			}
		}
		
		return ret;
	}
}