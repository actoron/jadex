package jadex.commons.transformation.binaryserializer;

import jadex.commons.transformation.traverser.*;
import jadex.commons.transformation.traverser.IBeanIntrospector;

public class BeanIntrospectorFactory {
	private BeanIntrospectorFactory() {
		
	}
	
	public static BeanIntrospectorFactory getInstance() {
		return new BeanIntrospectorFactory();
	}
	
	public IBeanIntrospector getBeanIntrospector() {
		return getBeanIntrospector(200);
	}
	
	public IBeanIntrospector getBeanIntrospector(int lrusize) {
		/* $if !android $ */
		return new BeanDelegateReflectionIntrospector(lrusize);
		/* $else $
		return new BeanReflectionIntrospector(lrusize);
		$endif $ */
	}
}
