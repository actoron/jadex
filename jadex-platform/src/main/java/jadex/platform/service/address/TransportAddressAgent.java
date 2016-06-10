package jadex.platform.service.address;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent 
@ProvidedServices(@ProvidedService(type=ITransportAddressService.class, implementation=@Implementation(TransportAddressService.class)))
@Properties(value=@NameValue(name="system", value="true"))
public class TransportAddressAgent
{
}
