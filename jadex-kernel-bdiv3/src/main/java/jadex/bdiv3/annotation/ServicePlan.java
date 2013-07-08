package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bdiv3.runtime.impl.IServiceParameterMapper;

/**
 * 
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServicePlan
{
	/**
	 * 
	 */
	public String name();
	
	/**
	 * 
	 */
	public String method() default "";
	
	/**
	 * 
	 */
	public Class<? extends IServiceParameterMapper> mapper() default IServiceParameterMapper.class;
//	public Class<? extends IServiceParameterMapper<?>> mapper() default IServiceParameterMapper.class;
}
