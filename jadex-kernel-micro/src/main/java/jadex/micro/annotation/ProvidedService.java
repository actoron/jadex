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
	 *  The name (for referencing/overriding).
	 */
	public String name() default "";
	
	/** 
	 *  The service interface type. 
	 */
	public Class type();
	
	/**
	 *  The service implementation.
	 */
	public Implementation implementation();
	
	/**
	 *  Publish details.
	 */
	public Publish publish() default @Publish(url="", type=Object.class);
}
