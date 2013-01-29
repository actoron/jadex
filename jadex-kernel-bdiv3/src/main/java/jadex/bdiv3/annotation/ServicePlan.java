package jadex.bdiv3.annotation;

import jadex.bdiv3.runtime.IServiceParameterMapper;

/**
 * 
 */
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
