package jadex.commons.transformation.binaryserializer;

/*if_not[android] */
import jadex.commons.transformation.traverser.BeanDelegateReflectionIntrospector;
/* else[android]
import jadex.commons.transformation.traverser.BeanReflectionIntrospector;
end[android] */
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
		
		/*if_not[android] */
		return new BeanDelegateReflectionIntrospector(lrusize);
		/* else[android]
		return new BeanReflectionIntrospector(lrusize);
		end[android] */
	}
}
