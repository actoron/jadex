package jadex.platform.service.marshal;


import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the marshal service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IMarshalService.class, 
	implementation=@Implementation(value=MarshalService.class, proxytype=Implementation.PROXYTYPE_RAW)))
//@Properties(value=@NameValue(name="system", value="true"))
public class MarshalAgent
{
}
