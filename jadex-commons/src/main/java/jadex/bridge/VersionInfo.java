package jadex.bridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;


/**
 *	Some version infos for Jadex.
 */
public class VersionInfo
{
	//-------- constants --------

	/** The version info singleton. */
	protected static final VersionInfo	instance	= new VersionInfo();
	
	/** The text date format (e.g. January 13, 2012). */
	public final DateFormat	DATE_FORMAT_TEXT	= new SimpleDateFormat("MMMM d, yyyy");
	
	/** The short date format (e.g. 2012/01/13). */
	public final DateFormat	DATE_FORMAT_NUMBER	= new SimpleDateFormat("yyyy/MM/dd");

	/** The short date format (e.g. 2012/01/13). */
	public final DateFormat	DATE_FORMAT_TIMESTAMP	= new SimpleDateFormat("yyyyMMdd.HHmmss");

	//-------- attributes --------
	
	/** The version string (e.g. 2.2-RC1). */
	protected String version;

	/** The release date. */
	protected Date	date;

	//-------- constructors --------
	
	/**
	 *  Create a new version info object.
	 */
	protected VersionInfo()
	{
		try
		{
			Properties	props	= new Properties();
			InputStream	is	= VersionInfo.class.getResourceAsStream("version.properties");
			props.load(is);
			is.close();
			
//			System.out.println(props);
			String	timestamp	= props.getProperty("jadex_build_timestamp");
//			System.out.println("timestamp: "+timestamp);
			if(timestamp.startsWith("$"))
			{
				// non build -> use file date
				URL	url	= VersionInfo.class.getResource("version.properties");
				URLConnection	con	= url.openConnection();
				date	= new Date(con.getLastModified());
				con.getInputStream().close();	// Required to release file handle!
//				System.out.println("file date: "+date);
				
				// non build -> find settings file.
				File	start	= new File(url.getPath());
				while(start.exists() && start.getParentFile()!=null)
				{
					start	= start.getParentFile();
					File	settings	= new File(start, "gradle.properties");
					if(settings.exists())
					{
						is	= new FileInputStream(settings);
						props.load(is);
						is.close();
						version	= props.getProperty("jadex_build_version");
						break;
					}
				}
			}
			else
			{
				version	= props.getProperty("jadex_build_version");
				// Format is yyyyMMdd.HHmmss
				int	year	= Integer.parseInt(timestamp.substring(0, 4));
				int	month	= Integer.parseInt(timestamp.substring(4, 6));
				int	day	= Integer.parseInt(timestamp.substring(6, 8));
				int	hour	= Integer.parseInt(timestamp.substring(9, 11));
				int	min	= Integer.parseInt(timestamp.substring(11, 13));
				int	sec	= 0; // Integer.parseInt(timestamp.substring(13, 15));	// Seconds not present on toaster! why???
				date	= new GregorianCalendar(year, month-1, day, hour, min, sec).getTime();	// month starts with 0 for january, grrr.
//				System.out.println("build date: "+date);
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			date	= date!=null ? date : new Date();
			version	= version!=null ? version : "n/a";
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the version info object.
	 */
	public static VersionInfo	getInstance()
	{
		return instance;
	}
	
	/**
	 *  Get the version string.
	 */
	public String getVersion()
	{
		return version!=null ? version : "unknown";
	}
	
	/**
	 *  Get the release date.
	 *  @return null, if unknown.
	 */
	public Date getDate()
	{
		return date;
	}
	
	/**
	 *  Get the release date as long text string
	 *  (format=MMMM d, yyyy, e.g. January 13, 2012).
	 */
	public String getTextDateString()
	{
		return getDate()==null ? "unknown" : DATE_FORMAT_TEXT.format(getDate());
	}
	
	/**
	 *  Get the release date as short number string
	 *  (format=yyyy/MM/dd, e.g. 2012/01/13).
	 */
	public String getNumberDateString()
	{
		return getDate()==null ? "unknown" : DATE_FORMAT_NUMBER.format(getDate());
	}
	
	/**
	 *  Get the release date as timestamp string
	 *  (format=yyyyMMdd.HHmmss, e.g. 20120113.174803).
	 */
	public String getTimestamp()
	{
		return getDate()==null ? "unknown" : DATE_FORMAT_TIMESTAMP.format(getDate());
	}
}
