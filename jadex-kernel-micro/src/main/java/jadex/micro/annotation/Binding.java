package jadex.micro.annotation;

import jadex.bridge.service.RequiredServiceInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The argument annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Binding
{
	/**
	 *  The argument name.
	 */
	public String name();
	
	/**
	 *  The component name.
	 */
	public String componentname() default "";
	
	/**
	 *  The component type.
	 */
	public String componenttype() default "";
	
	/**
	 *  The component filename.
	 */
	public String componentfilename() default "";
	
	/**
	 *  The component filename.
	 */
	public String scope() default RequiredServiceInfo.SCOPE_APPLICATION;

	/**
	 *  The dynamic binding flag.
	 */
	public boolean dynamic() default false;

	/**
	 *  The create component flag.
	 */
	public boolean create() default false;
}
