package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.BeanDelegateReflectionIntrospector;
import jadex.commons.transformation.traverser.BeanReflectionIntrospector;
import jadex.commons.transformation.traverser.IBeanIntrospector;

public class BeanIntrospectorFactory 
{
	private BeanIntrospectorFactory() 
	{
	}
	
	public static BeanIntrospectorFactory getInstance() 
	{
		return new BeanIntrospectorFactory();
	}
	
	public IBeanIntrospector getBeanIntrospector() 
	{
		return getBeanIntrospector(200);
	}
	
	public IBeanIntrospector getBeanIntrospector(int lrusize) 
	{
//		return new BeanReflectionIntrospector(lrusize);
		
		if(!SReflect.isAndroid())
		{
			return new BeanDelegateReflectionIntrospector(lrusize);
		}
		else
		{
			return new BeanReflectionIntrospector(lrusize);
		}
	}
}
