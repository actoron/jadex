package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.service.component.multiinvoke.IMultiplexDistributor;
import jadex.bridge.service.component.multiinvoke.SimpleMultiplexDistributor;

/**
 *  
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiplexDistributor
{
//	/** The default one (argument) to all (services) distribution strategy. */
//	public static final Class<? extends IMultiplexDistributor> ONE_TO_ALL = SimpleMultiplexDistributor.class;
//	
//	/** The default one (argument) to each (service) distribution strategy. */
//	public static final Class<? extends IMultiplexDistributor> ONE_TO_EACH = SequentialMultiplexDistributor.class;
	
	/**
	 *  The multiplex distributor class.
	 */
	public Class<? extends IMultiplexDistributor> value() default SimpleMultiplexDistributor.class; // ONE_TO_ALL
	
	/**
	 *  The service filter.
	 */
	public Value filter() default @Value();
	
	/**
	 *  The parameter converter.
	 */
	public Value paramconverter() default @Value();
}
