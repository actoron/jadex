package jadex.bdiv3.annotation;

/**
 * 
 */
public @interface Body
{
	/**
	 *  The body as seperate class.
	 */
	public Class<?> value() default Object.class;

	/**
	 *  The body as required service.
	 */
	public ServicePlan service() default @ServicePlan(name="");
	
}