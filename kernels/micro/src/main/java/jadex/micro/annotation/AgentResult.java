package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marker for agent result field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentResult
{
	/**
	 *  The result name.
	 *  Is optional. If not set the field name
	 *  must correspond to the result name.
	 */
	public String value() default "";
	
	/**
	 *  The result conversion expression.
	 *  
	 *  The coversion expression is used to
	 *  convert a default value.
	 *  $value refers to the field value.
	 *  
	 *  Is optional. 
	 */
	public String convert() default "";
	
	/**
	 *  The result conversion expression.
	 *  
	 *  The coversion expression is used to
	 *  convert a result value before saving
	 *  $value refers to the field value.
	 *  
	 *  Is optional. 
	 */
	public String convertback() default "";
}
