package jadex.webservice.examples.ws.geoip;

import jadex.commons.future.IFuture;
import jadex.webservice.examples.ws.geoip.gen.GeoIP;

/**
 *  Example web service interface in Jadex.
 *  The original synchronous web service interface
 *  is made asynchronous to fit the programming model
 *  and avoid deadlocks.
 *  
 *  Note: As parameters the JAX-WS generated data types
 *  have to be used.
 */
public interface IGeoIPService
{
	/**
	 *  Get geo information for ip.
	 */
	public IFuture<GeoIP> getGeoIP(String ip);
	
	/**
	 *  Get geo context.
	 */
	public IFuture<GeoIP> GetGeoIPContext();
}
