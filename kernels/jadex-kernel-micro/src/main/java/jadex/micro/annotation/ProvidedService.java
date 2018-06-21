package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.publish.IPublishService;

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
	public Class<?> type();
	
	/** 
	 *  The visibility scope.
	 */
	public String scope() default RequiredServiceInfo.SCOPE_GLOBAL;
	
	/**
	 *  The service implementation.
	 */
	public Implementation implementation() default @Implementation(expression="$pojoagent!=null? $pojoagent: $component");
	
	/**
	 *  Publish details.
	 */
	public Publish publish() default @Publish(publishid="", publishtype=IPublishService.PUBLISH_WS, mapping=Object.class);
	
	/**
	 *  Properties for the provided service.
	 */
	public NameValue[] properties() default {};
}
