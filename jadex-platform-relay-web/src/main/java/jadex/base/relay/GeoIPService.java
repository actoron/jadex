package jadex.base.relay;

import java.io.File;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;

/**
 *  Helper object to resolve IP addresses to Geo locations.
 *  Uses free API and database file available at: http://www.maxmind.com/app/geolitecity
 *  Requires GeoLiteCity.dat to be present in <user.home>/.relaystats directory.
 */
public class GeoIPService
{
	//-------- static part --------

	/** The singleton db object. */
	protected static GeoIPService	singleton	= new GeoIPService();
	
	/**
	 *  Get the db instance.
	 */
	public static GeoIPService	getGeoIPService()
	{
		return singleton;
	}
	
	//-------- attributes --------
	
	/** The lookup service (if any). */
	protected LookupService	ls;
	
	//-------- constructors --------
	
	/**
	 *  Create the db object.
	 */
	public GeoIPService()
	{
		try
		{
			// Set up geo ip lookup service.
			String	systemdir	= new File(System.getProperty("user.home"), ".relaystats").getAbsolutePath();
			System.out.println("Expecting GeoLiteCity.dat in: "+systemdir);
		    ls	= new LookupService(new File(systemdir, "GeoLiteCity.dat").getAbsolutePath(), LookupService.GEOIP_MEMORY_CACHE);
		}
		catch(Exception e)
		{
			// Ignore errors and let relay work without geo location.
			System.err.println("Warning: Relay could not initialize GeoIP service: "+ e);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Fetch location name for an IP address.
	 */
	public String	getLocation(String ip)
	{
		String	ret	= null;
		
		if(ls!=null)
		{
			try
			{
				Location	loc	= ls.getLocation(ip);
				ret	= loc.city;
				String	reg	= regionName.regionNameByCode(loc.countryCode, loc.region);
				if(!loc.city.equals(reg))
				{
					ret	+= ", "+reg;
				}
				if(!loc.countryName.equals(loc.city) && !loc.countryName.equals(reg))
				{
					ret	+= ", "+loc.countryName;
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				System.err.println("Warning: Could not get Geo location: "+ e);
			}
		}
		
		if(ret==null)
		{
			ret	= "unknown";
		}
		
		return ret;
	}

	/**
	 *  Fetch country code for an IP address or null, if not found.
	 */
	public String	getCountryCode(String ip)
	{
		String	ret	= null;
		
		if(ls!=null)
		{
			try
			{
				Location	loc	= ls.getLocation(ip);
				ret	= loc.countryCode.toLowerCase();
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				System.err.println("Warning: Could not get Geo location: "+ e);
			}
		}
		
		return ret;
	}
}
