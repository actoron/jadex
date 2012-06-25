package jadex.bridge.service.annotation;

import jadex.bridge.service.component.multiinvoke.IMultiplexDistributor;
import jadex.bridge.service.component.multiinvoke.SequentialMultiplexDistributor;
import jadex.bridge.service.component.multiinvoke.SimpleMultiplexDistributor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Target method annotation. Can be used to express
 *  to what exact service method a call should be routed to.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiplexDistributor
{
	/** The default one (argument) to all (services) distribution strategy. */
	public static final Class<? extends IMultiplexDistributor> ONE_TO_ALL = SimpleMultiplexDistributor.class;
	
	/** The default one (argument) to each (service) distribution strategy. */
	public static final Class<? extends IMultiplexDistributor> ONE_TO_EACH = SequentialMultiplexDistributor.class;

	/**
	 *  The multiplex distributor class.
	 */
	public Class<? extends IMultiplexDistributor> type() default SimpleMultiplexDistributor.class; // ONE_TO_ALL
	
	/**
	 *  The filter expression.
	 */
	public String filter();
}
