package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Security;
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
	//public ServiceScope scope() default ServiceScope.GLOBAL;
	public ServiceScope scope() default ServiceScope.DEFAULT;
	
	/** 
	 *  The scope expression to be evaluated on service initialization (only used when scope is set to {@see ServiceScope.EXPRESSION}).
	 */
	public String scopeexpression() default "";
	
	/** 
	 *  Override security settings from service interface or implementation.
	 */
	public Security security() default @Security(roles={});
	
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
