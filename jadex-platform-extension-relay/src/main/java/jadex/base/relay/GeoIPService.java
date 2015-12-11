package jadex.base.relay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;

import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;

/**
 *  Helper object to resolve IP addresses to Geo locations.
 *  Uses free API and database file available at: http://www.maxmind.com/app/geolitecity
 *  Downloads GeoLiteCity.dat into <user.home>/.relaystats directory.
 */
public class GeoIPService
{
	//-------- static part --------

	/** The singleton db object. */
	protected static final GeoIPService	singleton	= new GeoIPService();
	
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
	
	/** The date of the last update check. */
	protected long	lastupdate;
	
	//-------- methods --------
	
	/**
	 *  Fetch location name for an IP address.
	 */
	public String	getLocation(String ip)
	{
		updateDB();
		String	ret	= null;
		
		if(ls!=null)
		{
			try
			{
				Location	loc	= ls.getLocation(ip);
				if(loc!=null)
				{
					ret	= loc.city;
					
					String	reg	= regionName.regionNameByCode(loc.countryCode, loc.region);
					if(ret==null)
					{
						ret	= reg;
					}
					else if(!ret.equals(reg))
					{
						ret	+= ", "+reg;
					}
					
					if(ret==null)
					{
						ret	= loc.countryName;
					}
					else if(loc.countryName!=null && !loc.countryName.equals(loc.city) && !loc.countryName.equals(reg))
					{
						ret	+= ", "+loc.countryName;
					}
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not get Geo location: "+ e);
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
		updateDB();
		String	ret	= null;
		
		if(ls!=null)
		{
			try
			{
				Location	loc	= ls.getLocation(ip);
				if(loc!=null)
				{
					ret	= loc.countryCode.toLowerCase();
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not get Geo location: "+ e);
			}
		}
		
		return ret;
	}

	/**
	 *  Get the position as latitude,longitude.
	 */
	public String	getPosition(String ip)
	{
		updateDB();
		String	ret	= null;
		
		if(ls!=null)
		{
			try
			{
				Location	loc	= ls.getLocation(ip);
				if(loc!=null)
				{
					ret	= loc.latitude+","+loc.longitude;
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not get Geo location: "+ e);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Download / check for updates of the GeoIP database.
	 */
	public void	updateDB()
	{
		// Check every 12 hours
		long	update	= System.currentTimeMillis(); 
		if(update-lastupdate>12*60*60*1000)
		{
			synchronized(this)
			{
				if(update-lastupdate>12*60*60*1000)
				{
					lastupdate	= update;
					
					File	dbfile	= new File(RelayHandler.SYSTEMDIR, "GeoLiteCity.dat");
					
					try
					{
						URL	url	= new URL("http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz");
						HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
						if(con.getLastModified()>dbfile.lastModified())	// 0 for non-existant file
						{
							// Close lookup service before updating the database.
							if(ls!=null)
							{
								ls.close();
								ls	= null;
							}
							
							File	tmpfile	= new File(dbfile.getParentFile(), "GeoLiteCity.dat.tmp");
							InputStream	is	= new GZIPInputStream(con.getInputStream());
							OutputStream	os	= new FileOutputStream(tmpfile);
							byte[]	buf	= new byte[8192];
							int read;
							while((read=is.read(buf))!=-1)
							{
								os.write(buf, 0, read);
							}
							os.close();
							is.close();
							if(dbfile.exists())
							{
								// Keep last version, if there are problems with new version.
								File	oldfile	= new File(dbfile.getParentFile(), "GeoLiteCity.dat.old");
								if(oldfile.exists())
								{
									if(!oldfile.delete())
									{
										RelayHandler.getLogger().info("Cannot delete GeoIP database: "+oldfile);
									}
								}
								if(!dbfile.renameTo(oldfile))
								{
									RelayHandler.getLogger().info("Cannot rename old GeoIP database to: "+oldfile);
								}
							}
							if(!tmpfile.renameTo(dbfile))
							{
								RelayHandler.getLogger().info("Cannot rename GeoIP database to: "+dbfile);
							}
							else
							{
								RelayHandler.getLogger().info("Downloaded GeoIP database to: "+dbfile);
							}
						}
					}
					catch(Exception e)
					{
						RelayHandler.getLogger().warning("Warning: Relay could not access GeoIP database: "+ e);
					}

					if(ls==null)
					{
						try
						{
							// Set up geo ip lookup service.
							RelayHandler.getLogger().info("Using GeoIP database from: "+dbfile);
						    ls	= new LookupService(dbfile.getAbsolutePath(), LookupService.GEOIP_MEMORY_CACHE);
						}
						catch(Exception e)
						{
							// Ignore errors and let relay work without geo location.
							RelayHandler.getLogger().warning("Warning: Relay could not initialize GeoIP service: "+ e);
						}
					}
				}
			}
		}
	}
	
	public static void	main(String[] args) throws MalformedURLException
	{
		String	address	= "relay-http://www2.activecomponents.org/relay";
		String	host	= new URL(RelayConnectionManager.httpAddress(address)).getHost();
		System.out.println(host+": "+getGeoIPService().getLocation(host));
	}
}
