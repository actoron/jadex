package jadex.webservice.examples.ws.geoip;

import java.net.InetAddress;

import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.rs.invoke.RestServiceAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.webservice.examples.ws.geoip.gen.GeoIP;


/**
 *  Agent that searches and uses the geoip service
 *  of another Jadex agent.
 */
@Agent
@RequiredServices(@RequiredService(name="geoipservice", type=IGeoIPService.class))
public class GeoIPUserAgent extends RestServiceAgent
{
	//-------- emthods --------

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		IFuture<IGeoIPService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("geoipservice");
		fut.addResultListener(new DefaultResultListener<IGeoIPService>()
		{
			public void resultAvailable(IGeoIPService gs)
			{
				try
				{	
					String ip = InetAddress.getLocalHost().getHostAddress();
					gs.getGeoIP(ip).addResultListener(new DefaultResultListener<GeoIP>()
					{
						public void resultAvailable(GeoIP geoip) 
						{
							System.out.println("Welcome user from: "+geoip.getCountryName());
//							agent.killAgent();
						};
					});
				}
				catch(Exception e)
				{
					System.out.println("Unknown ip: "+e);
				}
			}
		});
	}
}



