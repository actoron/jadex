package jadex.commons.transformation.binaryserializer;

/* $if !android $ */
import jadex.commons.transformation.traverser.BeanDelegateReflectionIntrospector;
/* $else $
import jadex.commons.transformation.traverser.BeanReflectionIntrospector;
$endif $ */
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
		
		/* $if !android $ */
		return new BeanDelegateReflectionIntrospector(lrusize);
		/* $else $
		return new BeanReflectionIntrospector(lrusize);
		$endif $ */
	}
}
