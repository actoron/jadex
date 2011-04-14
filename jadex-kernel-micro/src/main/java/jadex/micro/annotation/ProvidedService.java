package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Provided service annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProvidedService
{
	/** 
	 *  The service interface type. 
	 */
	public Class type();
	
	/**
	 *  The creation expression.
	 */
	public Class implementation() default Object.class;

	/**
	 *  The creation expression.
	 */
	public String expression() default "";

	/**
	 *  The direct flag.
	 */
	public boolean direct() default false;
}
