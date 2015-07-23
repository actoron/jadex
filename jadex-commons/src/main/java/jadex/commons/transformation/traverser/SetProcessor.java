package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.lang.reflect.Type;
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
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Set.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		Set ret = (Set)getReturnObject(object, clazz, clone);
		Set set = (Set)object;
		
		traversed.put(object, ret);
		
		Object[] vals = set.toArray(new Object[set.size()]);
		for(int i=0; i<vals.length; i++)
		{
			Class valclazz = vals[i]!=null? vals[i].getClass(): null;
			Object newval = traverser.doTraverse(vals[i], valclazz, traversed, processors, clone, targetcl, context);
			
			if (newval != Traverser.IGNORE_RESULT)
			{
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