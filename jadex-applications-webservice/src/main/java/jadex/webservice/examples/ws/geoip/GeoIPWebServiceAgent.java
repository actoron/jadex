package jadex.webservice.examples.ws.geoip;

import jadex.extension.ws.invoke.WebServiceAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that wraps a normal web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@Imports({"jadex.extension.ws.invoke.*", "jadex.webservice.examples.ws.geoip.gen.*"})
@ProvidedServices(@ProvidedService(type=IGeoIPService.class, implementation=@Implementation(
	expression="$pojoagent.createServiceImplementation(IGeoIPService.class, new WebServiceMappingInfo(GeoIPService.class, \"getGeoIPServiceSoap\"))")))
public class GeoIPWebServiceAgent extends WebServiceAgent
{
}
