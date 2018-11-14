package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.Boolean3;
import jadex.micro.MicroAgentFactory;


/**
 *  Marker for agent class and variable.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Agent
{
	/**
	 *  If the agent body has a void return value
	 *  or no body at all this flag can be used to 
	 *  determine if the agent should be kept alive.
	 */
	public Boolean3 keepalive() default Boolean3.NULL;
	
	/**
	 *  Specify if the component should be suspened on creation.
	 */
	public Boolean3 suspend() default Boolean3.NULL;
	
//	/**
//	 *  Specify if the component is a master and leads to killing of parent when stopped.
//	 */
//	public Boolean3 master() default Boolean3.NULL;
	
//	/**
//	 *  Specify if the component is a daemon that doesn't prevent autoshutdown of its parent.
//	 */
//	public Boolean3 daemon() default Boolean3.NULL;
	
//	/**
//	 *  Specify if the component be killed automatically when no more subcomponents exist.
//	 */
//	public Boolean3 autoshutdown() default Boolean3.NULL;
	
	/**
	 *  Specify if the subcomponent should run synchronously on its parent's thread.
	 */
	public Boolean3 synchronous() default Boolean3.NULL;
	
//	/**
//	 *  Specify if the component is persistable.
//	 */
//	public Boolean3 persistable() default Boolean3.NULL;
	
	/**
	 *  Specify if monitoring should be enabled on the component.
	 */
	public PublishEventLevel monitoring() default PublishEventLevel.NULL;
	
	/**
	 *  Auto-provide provided services that are directly implemented by the agent.
	 */
	public Boolean3 autoprovide() default Boolean3.NULL;
	
	/**
	 *  Auto-start this agent on platform startup.
	 */
	public Autostart autostart() default @Autostart();
	
	/**
	 *  The agent type. 
	 */
	public String type() default MicroAgentFactory.TYPE;
}
