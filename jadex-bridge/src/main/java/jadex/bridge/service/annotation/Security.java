package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Set the required security level for a service or object or its methods.
 */
//@Target({ElementType.TYPE, ElementType.METHOD})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
// @Inherited does only work for classes, not for interfaces, grrr.
public @interface Security
{
	//-------- constants --------
	
	/** The unrestricted security level (access is granted to all). */
	public static final String	UNRESTRICTED	= "__security_unrestricted__";
	
	/** The most restricted security level (access is only granted with correct platform master password). */
	public static final String	PASSWORD	= "security_password";
	
	//-------- properties --------
	
	/**
	 *  Supply the security level.
	 */
	public String value();
}
