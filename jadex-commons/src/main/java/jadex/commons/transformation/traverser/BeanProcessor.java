package jadex.commons.traverser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Processor that traverses Java beans.
 */
class BeanProcessor implements ITraverseProcessor
{
	BeanReflectionIntrospector intro = new BeanReflectionIntrospector();
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return true;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone)
	{
//		System.out.println("fp: "+object);
		Object ret = getReturnObject(object, clazz, clone);
		traversed.put(object, ret);
		
		try
		{
//			System.out.println("cloned: "+object.getClass());
//			ret = object.getClass().newInstance();
			
			traverseProperties(object, traversed, processors, traverser, clone, ret);
		}
		catch(Exception e)
		{
			throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Clone all properties of an object.
	 */
	protected void traverseProperties(Object object, Map<Object, Object> cloned, 
		List<ITraverseProcessor> processors, Traverser traverser, boolean clone, Object ret)
	{
		Class clazz = object.getClass();
			
//		while(clazz!=null && clazz!=Object.class) 
		{
			// Get all declared fields (public, protected and private)
			
			Map props = intro.getBeanProperties(clazz, false);
			
			for(Iterator it=props.keySet().iterator(); it.hasNext(); )
			{
				try
				{
					String name = (String)it.next();
					BeanProperty prop = (BeanProperty)props.get(name);
					Object val = prop.getGetter().invoke(object, new Object[0]);
					if(val!=null) 
					{
						Object newval = traverser.traverse(val, prop.getType(), cloned, processors, clone);
						if(clone || val!=newval)
							prop.getSetter().invoke(ret, new Object[]{newval});
					}
				}
				catch(Exception e)
				{
					throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
				}
			}
			
//			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 *  Get the object that is returned.
	 */
	public Object getReturnObject(Object object, Class clazz, boolean clone)
	{
		Object ret = object;
		if(clone)
		{
			try
			{
				ret = object.getClass().newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}