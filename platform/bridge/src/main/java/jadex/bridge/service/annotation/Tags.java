package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.sensor.service.TagProperty;

/**
 *  Service search tags.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tags
{
	/**
	 *  The tags as strings or expressions (using %{})
	 */
	public String[] value() default {};
	
	/**
	 *  Argument name for fetching tag values.
	 */
	public String argumentname() default ""; 
}

// from jadex.micro.testcases.nfservicetags.ITestService

//per default use component argument 'tag' (shortcut for the second)
//@NFProperties(@NFProperty(value=TagProperty.class)) 
//@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name=TagProperty.ARGUMENT, value="\"tag\""))) // == TagProperty.NAME

//directly add 'mytag'
//@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name=TagProperty.NAME, value="\"mytag\"")))

//@NFProperties(@NFProperty(value=TagProperty.class, parameters={
//	@NameValue(name=TagProperty.NAME, values={TagProperty.PLATFORM_NAME, TagProperty.JADEX_VERSION, "\"mytag\""}), 
//	@NameValue(name=TagProperty.ARGUMENT, value="\"tag\"") // additionally get tags from arguments 'tag'
//}))
