package jadex.micro.annotation;

/**
 * 
 */
public @interface Implementation
{
	/**
	 *  The direct flag.
	 */
	public boolean direct() default false;
	
	/**
	 *  The creation class.
	 */
	public Class value() default Object.class;

	/**
	 *  The creation expression.
	 */
	public String expression() default "";

	/**
	 *  The binding for forwarding service calls to another component.
	 */
	public Binding binding() default @Binding();
}
