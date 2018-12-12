package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Set.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		Set ret = (Set)getReturnObject(object, clazz, context);
		Set set = (Set)object;
		TraversedObjectsContext.put(context, object, ret);
		
		Object[] vals = set.toArray(new Object[set.size()]);
		for(int i=0; i<vals.length; i++)
		{
			Class valclazz = vals[i]!=null? vals[i].getClass(): null;
			Object newval = traverser.doTraverse(vals[i], valclazz, conversionprocessors, processors, mode, targetcl, context);
			
			if (newval != Traverser.IGNORE_RESULT)
			{
				if(SCloner.isCloneContext(context))
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
	public Object getReturnObject(Object object, Class<?> clazz, Object context)
	{
		Object ret = object;
		
		if(SCloner.isCloneContext(context))
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