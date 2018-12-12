package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  A map processor allows for traversing maps.
 */
public class MultiCollectionProcessor implements ITraverseProcessor
{
	/**
	 *  Create a new multi-collection processor.
	 */
	public MultiCollectionProcessor()
	{
	}
	
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
		return SReflect.isSupertype(MultiCollection.class, clazz);
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
		MultiCollection<Object, Object> ret = (MultiCollection<Object, Object>)getReturnObject(object, clazz, context);
		MultiCollection<Object, Object> map = (MultiCollection<Object, Object>)object;
		TraversedObjectsContext.put(context, object, ret);
		
		Set<Object> keyset = map.keySet();
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		for(int i=0; i<keys.length; i++)
		{
			Object key = keys[i];
			Class<?> keyclazz = key != null? key.getClass() : null;
			Object newkey = traverser.doTraverse(key, keyclazz, conversionprocessors, processors, mode, targetcl, context);
			if (newkey != Traverser.IGNORE_RESULT)
			{
				Collection<Object> vals = map.get(key);
				for(Object val : vals)
				{
					Class<?> valclazz = val!=null? val.getClass(): null;
					Object newval = traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
					
					if(newval != Traverser.IGNORE_RESULT && (SCloner.isCloneContext(context) || newval!=val))
						ret.add(newkey, newval);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
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
				// Using linked hash map as default to avoid loosing order if has order.
				ret = new LinkedHashMap<Object, Object>();
			}
		}
		
		return ret;
	}
}