package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bdiv3.runtime.impl.IServiceParameterMapper;

/**
 *  Annotation for a service plan, i.e. a service invocation is used as plan.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServicePlan
{
	/**
	 *  The required service name.
	 */
	public String name();
	
	/**
	 *  The method name.
	 */
	public String method() default "";
	
	/**
	 *  The parameter mapper between goal parameters and service arguments/results.
	 */
	public Class<? extends IServiceParameterMapper> mapper() default IServiceParameterMapper.class;
//	public Class<? extends IServiceParameterMapper<?>> mapper() default IServiceParameterMapper.class;
}
