package jadex.bridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 *	Some version infos for Jadex, loaded from version.properties in classpath.
 */
public class VersionInfo
{
	//-------- constants --------

	/** The version info singleton. */
	
	protected static final VersionInfo instance = new VersionInfo();
	
	/** The text date format (e.g. January 13, 2012). */
	public final DateFormat	DATE_FORMAT_TEXT = new SimpleDateFormat("MMMM d, yyyy");
	
	/** The short date format (e.g. 2012/01/13). */
	public final DateFormat	DATE_FORMAT_NUMBER = new SimpleDateFormat("yyyy/MM/dd");

	/** The short date format (e.g. 2012/01/13). */
	public final DateFormat	DATE_FORMAT_TIMESTAMP = new SimpleDateFormat("yyyyMMdd.HHmmss");

	//-------- attributes --------
	
	/** The version string (e.g. 2.2-RC1). */
	protected String version;
	
	/** The Jadex version as bean object. */
	protected JadexVersion jadexversion;
	
	/** The release date. */
	protected Date date;
	
	//-------- constructors --------
	
	/**
	 *  Create a new version info object.
	 */
	protected VersionInfo()
	{
		try
		{
			jadexversion = new JadexVersion();
			Properties	props	= new Properties();
			//InputStream is = SUtil.getResource0("jadexversion.properties", VersionInfo.class.getClassLoader());
			InputStream	is = VersionInfo.class.getResourceAsStream("jadexversion.properties");
			
			// Hack!!! when running Jadex from source in eclipse there is no properties
			if(is==null)
			{
				File	fprops	= new File("../../src/main/buildutils/jadexversion.properties");
				is	= new FileInputStream(fprops);
			}
			
			//InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("jadexversion.properties");
			props.load(is);
			is.close();
			
//			System.out.println(props);
			String timestamp = props.getProperty("jadextimestamp");
//			System.out.println("timestamp: "+timestamp);
			
			try
			{
				jadexversion.setMinorVersion(Integer.parseInt(props.getProperty("jadexversion_minor")));
				jadexversion.setMajorVersion(Integer.parseInt(props.getProperty("jadexversion_major")));
			}
			catch (Exception e)
			{
			}
			version	= props.getProperty("jadexversion");
			
			//version	= props.getProperty("jadex_build_version");
			// Format is yyyyMMdd.HHmmss
			if(timestamp!=null)
			{
				date = new Date(Long.parseLong(timestamp) * 1000);
			}
//			System.out.println("build date: "+date);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		date = date!=null ? date : new Date();
		version	= version!=null ? version :
			jadexversion.isUnknown() ? "n/a"
			: jadexversion.getMajorVersion()+"."+jadexversion.getMinorVersion()
				+".9999-SNAPSHOT";

	}
	
	//-------- methods --------
	
	/**
	 *  Get the version info object.
	 */
	public static VersionInfo getInstance()
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
	 *  Returns the version of Jadex.
	 * 
	 *  @return Version of Jadex.
	 */
	public JadexVersion getJadexVersion()
	{
		return jadexversion;
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

	/**
	 *  Get a human readable version string.
	 */
	public String	toString()
	{
		return "Jadex Version " + version + " ("+getTimestamp()+")";
	}
	
	/**
	 *  Check if the current version is uptodate.
	 */
	public static void	main(String[] args)
	{
		try
		{
			String	updateurl	= "https://repo1.maven.org/maven2/org/activecomponents/jadex/jadex-distribution-minimal/maven-metadata.xml";
			URL	url	= new URL(updateurl);
			URLConnection con	= url.openConnection();
			con.connect();
			long	lastmod	= con.getLastModified();
			System.out.println("lastmod: "+new Date(lastmod));
			InputStream	is	= con.getInputStream();
			Scanner	s	= new Scanner(is);
			s.findAll(Pattern.compile("<version>([^<]*)</version>"))
				.map(match -> match.group(1)).forEach(System.out::println);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
