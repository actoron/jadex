package jadex.commons.transformation.traverser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.binaryserializer.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Processor that traverses Java beans.
 */
public class BeanProcessor implements ITraverseProcessor
{
//	protected BeanReflectionIntrospector intro = new BeanReflectionIntrospector();
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(5000);
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		return true;
	}
	
	/**
	 *  Allow adding the object to the traversed state.
	 * 	@param orig Original object.
	 * 	@param preprocessed Preprocessed object.
	 * 	@param traversed Traversed state.
	 */
	public void addToTraversedState(Object orig, Object preprocessed, Map<Object, Object> traversed)
	{
		
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
//		System.out.println("fp: "+object);
		Class<?> clazz = SReflect.getClass(type);
		Object ret = getReturnObject(object, clazz, targetcl, context);
		TraversedObjectsContext.put(context, object, ret);
		
		try
		{
//			System.out.println("cloned: "+object.getClass());
//			ret = object.getClass().newInstance();
			
			traverseProperties(object, conversionprocessors, processors, mode, traverser, targetcl, ret, context);
		}
		catch(Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		
		return ret;
	}
	
	/**
	 *  Clone all properties of an object.
	 */
	protected void traverseProperties(Object object, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, Object ret, Object context)
	{
		Class clazz = object.getClass();
			
//		while(clazz!=null && clazz!=Object.class) 
		{
			// Get all declared fields (public, protected and private)
			
			Map props = intro.getBeanProperties(clazz, true, false);
			
			for(Iterator it=props.keySet().iterator(); it.hasNext(); )
			{
				try
				{
					String name = (String)it.next();
					BeanProperty prop = (BeanProperty)props.get(name);
					if(prop.isReadable() && prop.isWritable())
					{
						Object val = prop.getPropertyValue(object);//getGetter().invoke(object, new Object[0]);
						if(val!=null) 
						{
							Object newval = traverser.doTraverse(val, prop.getType(), conversionprocessors, processors, mode, targetcl, context);
							if(newval != Traverser.IGNORE_RESULT && (object!=ret || val!=newval))
								prop.setPropertyValue(ret, newval);
	//							prop.getSetter().invoke(ret, new Object[]{newval});
						}
					}
				}
				catch(Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
			}
//			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 *  Get the object that is returned.
	 */
	public Object getReturnObject(Object object, Class<?> clazz, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		if(SCloner.isCloneContext(context) || targetcl!=null && !clazz.equals(SReflect.classForName0(clazz.getName(), targetcl)))
		{
			if(targetcl!=null)
				clazz	= SReflect.classForName0(clazz.getName(), targetcl);
			
			Constructor	c;
			
			try
			{
				c	= clazz.getConstructor(new Class[0]);
			}
			catch(NoSuchMethodException nsme)
			{
				c	= clazz.getDeclaredConstructors()[0];
			}

			try
			{
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
				System.out.println("beanproc ex: "+object+" "+c);
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}