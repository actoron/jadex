package jadex.commons.transformation;

import java.lang.reflect.Constructor;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.DefaultBeanIntrospector;
import jadex.commons.transformation.traverser.IBeanIntrospector;

/**
 *  Factory for generating bean introspectors.
 */
public class BeanIntrospectorFactory 
{
	protected int DEFAULT_INTROSPECTOR_CACHE_SIZE = 20000;
	
	/** The factor instance */
	protected static volatile BeanIntrospectorFactory instance;
	
	/** The introspector. */
	protected IBeanIntrospector introspector;
	
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
		if (introspector == null)
			introspector = getBeanIntrospector(DEFAULT_INTROSPECTOR_CACHE_SIZE);
		return introspector;
	}
	
	/**
	 *  Gets an introspector.
	 *  
	 *  @return The introspector.
	 */
	public IBeanIntrospector getBeanIntrospector(int lrusize) 
	{
		introspector = new DefaultBeanIntrospector(lrusize);
		
		return introspector;
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
