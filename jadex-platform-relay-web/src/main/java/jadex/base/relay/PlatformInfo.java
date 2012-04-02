package jadex.base.relay;

import jadex.bridge.service.types.awareness.AwarenessInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 *  Data object to collect information about
 *  connected platforms.
 */
public class PlatformInfo
{
	//-------- constants --------
	
	/** The short time format (only time). */
	protected static final DateFormat	TIME_FORMAT_SHORT	= new SimpleDateFormat("k:mm:ss z");
	
	/** The long time format (time and date). */
	protected static final DateFormat	TIME_FORMAT_LONG	= new SimpleDateFormat("k:mm:ss z, EEE, MMM d, yyyy");
	
	//-------- attributes --------
	
	/** The db id. */
	protected Integer	dbid;
	
	/** The platform id. */
	protected String	id;
	
	/** The host ip. */
	protected String	hostip;
	
	/** The host name. */
	protected String	hostname;
	
	/** The protocol (e.g. http or https). */
	protected String	scheme;
	
	/** The time when the connection was established. */
	protected Date	connect_time;
	
	/** The time when the connection was lost. */
	protected Date	disconnect_time;
	
	/** The number of received bytes. */
	protected double	bytes_received;
	
	/** The accumulated time spent for transmitting data (millis). */
	protected double	total_transmission_time;
	
	/** The number of messages for the recipient. */
	protected int	msg_cnt;
	
	/** The awareness info (if any). */
	protected AwarenessInfo	awainfo;
	
	//-------- constructors --------
	
	/**
	 *  Create a platform info.
	 */
	public PlatformInfo(String id, String hostip, String hostname, String protocol)
	{
		this(null, id, hostip, hostname, protocol, new Date(), null, 0, 0, 0);
		
		StatsDB.getDB().save(this);
	}
	
	/**
	 *  Constructor used by db.
	 */
	public PlatformInfo(Integer dbid, String id, String hostip, String hostname, String protocol,
		Date connect_time, Date disconnect_time, int msg_cnt, double bytes_received, double total_transmission_time)
	{
		this.dbid	= dbid;
		this.id	= id;
		this.hostip	= hostip;
		this.hostname	= hostname;
		this.scheme	= protocol;
		this.connect_time	= connect_time;
		this.disconnect_time	= disconnect_time;
		this.msg_cnt	= msg_cnt;
		this.bytes_received	= bytes_received;
		this.total_transmission_time	= total_transmission_time;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id.
	 */
	public String	getId()
	{
		return id;
	}
	
	/**
	 *  Get the host.
	 */
	public String	getHostIP()
	{
		return hostip;
	}
	
	/**
	 *  Get the resolved host name.
	 */
	public String	getHostName()
	{
		if(hostname.equals(hostip))
		{
			try
			{
				hostname	= InetAddress.getByName(hostip).getCanonicalHostName();
			}
			catch(UnknownHostException e)
			{
				hostname	= "unknown";
			}
		}
		return hostname;
	}
	
	/**
	 *  Get the host.
	 */
	public String	getScheme()
	{
		return scheme;
	}
	
	/**
	 *  Get the connect time.
	 */
	public String	getConnectTime()
	{
		GregorianCalendar	cal	= new GregorianCalendar();
		GregorianCalendar	con	= new GregorianCalendar();
		con.setTime(connect_time);
		
		DateFormat	df	= (con.get(Calendar.YEAR)!=cal.get(Calendar.YEAR)
			|| con.get(Calendar.MONTH)!=cal.get(Calendar.MONTH)
			|| con.get(Calendar.DAY_OF_MONTH)!=cal.get(Calendar.DAY_OF_MONTH))
			? TIME_FORMAT_LONG : TIME_FORMAT_SHORT;
		
		return df.format(connect_time);
	}
	
	/**
	 *  Get the disconnect time.
	 */
	public String	getDisconnectTime()
	{
		if(disconnect_time!=null)
		{
			GregorianCalendar	cal	= new GregorianCalendar();
			GregorianCalendar	con	= new GregorianCalendar();
			con.setTime(disconnect_time);
			
			DateFormat	df	= (con.get(Calendar.YEAR)!=cal.get(Calendar.YEAR)
				|| con.get(Calendar.MONTH)!=cal.get(Calendar.MONTH)
				|| con.get(Calendar.DAY_OF_MONTH)!=cal.get(Calendar.DAY_OF_MONTH))
				? TIME_FORMAT_LONG : TIME_FORMAT_SHORT;
			
			return df.format(disconnect_time);
		}
		else
		{
			return "";
		}
	}
	
	/**
	 *  Get the connect date.
	 */
	public Date	getConnectDate()
	{
		return connect_time;
	}
	
	/**
	 *  Get the disconnect date.
	 */
	public Date	getDisconnectDate()
	{
		return disconnect_time;
	}
	
	/**
	 *  Get the message count.
	 */
	public int	getMessageCount()
	{
		return msg_cnt;
	}
	
	/**
	 *  Get the byte count as beautified string.
	 */
	public String	getByteCount()
	{
		return bytesToString(bytes_received);
	}
	
	/**
	 *  Get the byte count as raw value
	 */
	public double	getBytes()
	{
		return bytes_received;
	}
	
	/**
	 *  Get the average transfer rate as beautified string.
	 */
	public String	getTransferRate()
	{
		double	val	= bytes_received / total_transmission_time;
		return bytesToString(val) + " / sec.";
	}
	
	/**
	 *  Get the transfer time as raw value (millis).
	 */
	public double	getTransferTime()
	{
		return total_transmission_time;
	}
	
	/**
	 *  Get the awareness info.
	 */
	public AwarenessInfo	getAwarenessInfo()
	{
		return awainfo;
	}
	
	/**
	 *  Get the db id
	 */
	public Integer	getDBId()
	{
		return dbid;
	}
	
	/**
	 *  Get the city.
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getCity()
	{
		return GeoIPService.getGeoIPService().getCity(hostip);
	}
	
	/**
	 *  Get the region (e.g. federal state).
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getRegion()
	{
		return GeoIPService.getGeoIPService().getRegion(hostip);
	}
	
	/**
	 *  Get the country.
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getCountry()
	{
		return GeoIPService.getGeoIPService().getCountry(hostip);
	}
	
	//-------- modifier methods --------
	
	/**
	 *  Add a sent message.
	 *  @param bytes	The number of bytes sent.
	 *  @param time	The time required for sending (nanos).
	 */
	public void	addMessage(int bytes, long time)
	{
		msg_cnt++;
		bytes_received	+= bytes;
		total_transmission_time	+= time / 1000000.0;
		
//		StatsDB.getDB().save(this);
	}
	
	/**
	 *  Platform with same id has reconnected.
	 */
	public void	reconnect(String hostip, String hostname)
	{
		if(!hostip.equals(this.hostip))
		{
			throw new RuntimeException("Platform already connected from different ip: "+this.hostip);
		}
//		this.hostip	= hostip;
		this.hostname	= hostname;
//		this.connect_time	= new Date();
		
//		StatsDB.getDB().save(this);
	}
	
	/**
	 *  Platform has disconnected.
	 */
	public void	disconnect()
	{
		this.disconnect_time	= new Date();
		
		StatsDB.getDB().save(this);
	}
	
	/**
	 *  Set the awareness info.
	 */
	public void	setAwarenessInfo(AwarenessInfo awainfo)
	{
		this.awainfo	= awainfo;
	}
	
	/**
	 *  Set db id
	 */
	public void	setDBId(Integer dbid)
	{
		this.dbid	= dbid;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get bytes as string.
	 */
	public static String	bytesToString(double bytes)
	{
		String	ret;
		if(bytes>1024*1024*1024)
		{
			ret	= ((int)(bytes/(1024*1024*1024)*10))/10.0 + " GB"; 
		}
		else if(bytes>1024*1024)
		{
			ret	= ((int)(bytes/(1024*1024)*10))/10.0 + " MB"; 
		}
		else if(bytes>1024)
		{
			ret	= ((int)(bytes/(1024)*10))/10.0 + " KB"; 
		}
		else
		{
			ret	= Integer.toString((int)bytes)+ " B"; 
		}
		
		return ret;
	}

	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return "PlatformInfo(id="+getDBId()+", platform="+getId()+", host="+getHostName()+"("+getHostIP()+"), scheme="+getScheme()
			+ ", connected="+getConnectTime()+", disconnected="+getDisconnectTime()
			+ ", messages="+getMessageCount()+"("+getByteCount()+"), rate="+getTransferRate()
			+ ")";
	}
}
