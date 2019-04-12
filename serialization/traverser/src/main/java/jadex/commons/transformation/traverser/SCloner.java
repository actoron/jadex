package jadex.commons.transformation.traverser;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 *  Deep cloner.
 */
public class SCloner
{
	/**
	 *  Check if a context is a clone contexxt.
	 *  @param context The context.
	 *  @return True, if context is a clone context.
	 */
	public static final boolean isCloneContext(Object context)
	{
		return context instanceof CloneContext;
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object)
	{
		return clone(object, (ClassLoader) null);
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object, List<ITraverseProcessor> processors)
	{
		return clone(object, null, processors, null);
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object
	 *  @param targetcl ClassLoader if different from original.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object, ClassLoader targetcl)
	{
		return clone(object, null, null, targetcl);
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object
	 *  @param targetcl ClassLoader if different from original.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object, Traverser traverser, List<ITraverseProcessor> processors, ClassLoader targetcl)
	{
//		if(object!=null)
//		{
//			if(object.getClass().toString().indexOf("Response")!=-1)
//				System.out.println("cloning: "+object.getClass());
//		}
		
		traverser = traverser != null? traverser:Traverser.getInstance();
		return traverser.traverse(object, null, null, processors == null? Traverser.getDefaultProcessors():processors, Traverser.MODE.PLAIN, targetcl, new CloneContext());
	}
	
	/**
	 *  Creates a bean object from class.
	 *  
	 *  @param intro Bean introspector to use.
	 *  @param clazz The class.
	 *  @return Instantiated bean object.
	 */
	public static final Object createBeanObject(IBeanIntrospector intro, Class<?> clazz)
	{
		Object bean = null;
		
		MethodHandle mcon = intro.getBeanConstructor(clazz, true, false);
		if (mcon == null)
		{
			// Allow non-public bean constructors
			Constructor<?> c = null;
			try
			{
				c = clazz.getDeclaredConstructor();
			}
			catch (Exception e)
			{
			}
			
			if (c != null)
			{
				try
				{
					if(!Modifier.isPublic(c.getModifiers()) || !Modifier.isPublic(clazz.getModifiers()))
					{
						c.setAccessible(true);
					}
					bean = c.newInstance();
				}
				catch (Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
			}
			else
			{
				c = clazz.getDeclaredConstructors()[0];
				c.setAccessible(true);
				Class<?>[] paramtypes = c.getParameterTypes();
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
				
				try
				{
					bean = c.newInstance(paramvalues);
				}
				catch (Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
			}
		}
		else
		{
			try
			{
				bean = mcon.invokeExact();
			}
			catch (Throwable e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
		
		return bean;
	}
	
	/** Extendable clone context. */
	protected static class CloneContext extends TraversedObjectsContext
	{
		 /** Creates context. */
		public CloneContext()
		{
			super();
		}
	}
}
