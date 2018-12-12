package jadex.platform.service.context;


import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.context.IContextService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the context service.
 */
@Agent(predecessors="jadex.platform.service.library.LibraryAgent", 
	autostart=Boolean3.TRUE)
@Arguments(@Argument(name="contextserviceclass", clazz=Class.class))
@ProvidedServices(@ProvidedService(type=IContextService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="$args.contextserviceclass!=null ? jadex.commons.SReflect.classForName0($args.contextserviceclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : jadex.commons.SReflect.isAndroid() ? jadex.platform.service.context.AndroidContextService.class.getConstructor(new Class[]{jadex.bridge.IComponentIdentifier.class}).newInstance(new Object[]{$component.getId()}) : jadex.platform.service.context.ContextService.class.getConstructor(new Class[]{jadex.bridge.IComponentIdentifier.class}).newInstance(new Object[]{$component.getId()})")))
//@Properties(value=@NameValue(name="system", value="true"))
public class ContextAgent
{
}