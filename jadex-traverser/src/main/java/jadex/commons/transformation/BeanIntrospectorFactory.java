package jadex.commons.transformation;

import java.lang.reflect.Constructor;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.BeanReflectionIntrospector;
import jadex.commons.transformation.traverser.IBeanIntrospector;

/**
 *  Factory for generating bean introspectors.
 */
public class BeanIntrospectorFactory 
{
	/** If true, attempt to use delegate optimization */;
	protected static final boolean OPTIMIZE = false;
	
	/** Class of the optimizing delegate introspector. */
	protected static final String DELEGATE_INTRO_CLASS = "jadex.commons.transformation.traverser.BeanDelegateReflectionIntrospector";
	
	/** The factor instance */
	protected static volatile BeanIntrospectorFactory instance;
	
	/**
	 *  Private constructor.
	 */
	private BeanIntrospectorFactory() 
	{
	}
	
	/**
	 *  Gets a factory instance.
	 *  
	 *  @return Instance.
	 */
	public static BeanIntrospectorFactory getInstance() 
	{
		if(instance == null)
		{
			synchronized (BeanIntrospectorFactory.class)
			{
				if(instance == null)
				{
					instance = new BeanIntrospectorFactory();
				}
			}
		}
		return instance;
	}
	
	/**
	 *  Gets an introspector with default lru size.
	 *  
	 *  @return The introspector.
	 */
	public IBeanIntrospector getBeanIntrospector() 
	{
		return getBeanIntrospector(200);
	}
	
	/**
	 *  Gets an introspector.
	 *  
	 *  @return The introspector.
	 */
	public IBeanIntrospector getBeanIntrospector(int lrusize) 
	{
		IBeanIntrospector ret = null;
		if(OPTIMIZE && !SReflect.isAndroid())
		{
			ClassLoader cl = BeanIntrospectorFactory.class.getClassLoader();
			try
			{
				Class<?> introclass = cl.loadClass(DELEGATE_INTRO_CLASS);
				if (introclass != null)
				{
					Constructor<?> con = introclass.getConstructor(int.class);
					ret = (IBeanIntrospector) con.newInstance(lrusize);
				}
			}
			catch (Exception e)
			{
			}
		}
		
		if(ret == null)
		{
			return new BeanReflectionIntrospector(lrusize);
		}
		
		return ret;
	}
	
	/**
	 *  Converts a bean to a string.
	 *  
	 *  @param bean The bean.
	 *  @param cl The classloader.
	 */
	public static String beanToString(Object bean, ClassLoader cl)
	{
		if(cl == null)
			cl = SUtil.class.getClassLoader();
		StringBuilder beanstr = new StringBuilder("[");
		IBeanIntrospector is = BeanIntrospectorFactory.getInstance().getBeanIntrospector();
		Map<String, BeanProperty> props = is.getBeanProperties(bean.getClass(), true, false);
		boolean notfirst = false;
		for(Map.Entry<String, BeanProperty> entry : props.entrySet())
		{
			if(notfirst)
			{
				beanstr.append(",");
			}
			else
			{
				notfirst = true;
			}
			String name = entry.getKey();
			Object val = entry.getValue().getPropertyValue(bean);
			beanstr.append(name);
			beanstr.append("=");
			beanstr.append(String.valueOf(val));
		}
		beanstr.append("]");
		return beanstr.toString();
	}
}
