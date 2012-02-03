package jadex.webservice.examples.ws.geoip;

import jadex.commons.future.DefaultResultListener;
import jadex.extension.ws.invoke.WebServiceAgent;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.webservice.examples.ws.geoip.gen.GeoIP;

import java.net.InetAddress;

/**
 *  Agent that wraps a normal web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@Imports({"jadex.base.service.ws.*", "jadex.micro.examples.ws.geoip.gen.*"})
@ProvidedServices(@ProvidedService(type=IGeoIPService.class, implementation=@Implementation(
	expression="$pojoagent.createServiceImplementation(IGeoIPService.class, new WebServiceMappingInfo(GeoIPService.class, \"getGeoIPServiceSoap\"))")))
public class GeoIPWebServiceAgent extends WebServiceAgent
{
	//-------- attributes --------
	
	@Agent
	protected MicroAgent agent;
	
	//-------- emthods --------

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		IGeoIPService geoser = (IGeoIPService)agent.getServiceContainer().getProvidedServices(IGeoIPService.class)[0];
		try
		{	
			String ip = InetAddress.getLocalHost().getHostAddress();
			geoser.getGeoIP(ip).addResultListener(new DefaultResultListener<GeoIP>()
			{
				public void resultAvailable(GeoIP geoip) 
				{
					System.out.println("Welcome user from: "+geoip.getCountryName());
					agent.killAgent();
				};
			});
		}
		catch(Exception e)
		{
			System.out.println("Unknown ip: "+e);
		}
	}
}
