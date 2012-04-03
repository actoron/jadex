package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.BeanIntrospectorFactory;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Processor that traverses Java beans.
 */
public class BeanProcessor implements ITraverseProcessor
{
//	protected BeanReflectionIntrospector intro = new BeanReflectionIntrospector();
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(5000);
	
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
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
//		System.out.println("fp: "+object);
		Object ret = getReturnObject(object, clazz, clone);
		traversed.put(object, ret);
		
		try
		{
//			System.out.println("cloned: "+object.getClass());
//			ret = object.getClass().newInstance();
			
			traverseProperties(object, traversed, processors, traverser, clone, ret, context);
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
			List<ITraverseProcessor> processors, Traverser traverser, boolean clone, Object ret, Object context)
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
					Object val = prop.getPropertyValue(object, name);//getGetter().invoke(object, new Object[0]);
					if(val!=null) 
					{
						Object newval = traverser.traverse(val, prop.getType(), cloned, processors, clone, context);
						if(clone || val!=newval)
							prop.setPropertyValue(ret, name, newval);
//							prop.getSetter().invoke(ret, new Object[]{newval});
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
				Constructor	c	= clazz.getDeclaredConstructors()[0];
				c.setAccessible(true);
				Class[] paramtypes = c.getParameterTypes();
				Object[] paramvalues = new Object[paramtypes.length];
				for(int i=0; i<paramtypes.length; i++)
				{
					if(paramtypes[i].equals(boolean.class))
					{
						paramvalues[i] = Boolean.FALSE;
					}
					else if(SReflect.isBasicType(paramtypes[i]))
					{
						paramvalues[i] = 0;
					}
				}
				ret = c.newInstance(paramvalues);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}