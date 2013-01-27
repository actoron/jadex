package jadex.bdiv3.annotation;

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
}
