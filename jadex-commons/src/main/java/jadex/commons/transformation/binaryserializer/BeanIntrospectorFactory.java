package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.BeanReflectionIntrospector;
import jadex.commons.transformation.traverser.IBeanIntrospector;

import java.lang.reflect.Constructor;

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
}
