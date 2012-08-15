package jadex.base.relay;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.message.ICodec;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;


/**
 *  Data object to collect information about
 *  connected platforms.
 */
public class PlatformInfo
{
	//-------- constants --------
	
	/** The short time format (only time). */
	public static final DateFormat	TIME_FORMAT_SHORT	= new SimpleDateFormat("k:mm:ss z");
	
	/** The long time format (time and date). */
	public static final DateFormat	TIME_FORMAT_LONG	= new SimpleDateFormat("k:mm:ss z, EEE, MMM d, yyyy");
	
	//-------- attributes --------
	
	/** The db id. */
	protected Integer	dbid;
	
	/** The platform id. */
	protected String	id;
	
	/** The host ip. */
	protected String	hostip;
	
	/** The host name. */
	protected String	hostname;
	
	/** The host name future for eager resolving. */
	protected IFuture<String>	hostnamefut;
	
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
	
	/** The preferred codecs (if any). */
	protected ICodec[] pcodecs;
	
	/** The properties (if any). */
	protected Map<String, String>	properties;
	
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
		
		// Escape HTML code in strings to avoid cross-site scripting.
		this.id	= id!=null ? SUtil.makeConform(id) : null;
		this.hostip	= hostip!=null ? SUtil.makeConform(hostip) : null;
		this.hostname	= hostname!=null ? SUtil.makeConform(hostname) : null;
		this.scheme	= scheme!=null ? SUtil.makeConform(protocol) : null;
		
		this.connect_time	= connect_time;
		this.disconnect_time	= disconnect_time;
		this.msg_cnt	= msg_cnt;
		this.bytes_received	= bytes_received;
		this.total_transmission_time	= total_transmission_time;

		// Resolve hostname, when only IP is supplied
		// Prefetch the hostname in background as blocking getCanonicalHostName() can be slow due to network timeouts.
		if(hostname.equals(hostip))
		{
			final Future<String>	fut	= new Future<String>();
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						String	tmp	= InetAddress.getByName(PlatformInfo.this.hostip).getCanonicalHostName();
						if(tmp.equals(PlatformInfo.this.hostip))
						{
							tmp	= "IP "+tmp;
						}
						fut.setResult(tmp);
					}
					catch(UnknownHostException e)
					{
						fut.setResult("unknown");
					}
					System.out.println("hostip: "+PlatformInfo.this.hostip+", "+fut.get(null));
				}
			}).start();
			hostnamefut	= fut;
		}

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
		if(hostnamefut!=null)
		{
			this.hostname	= hostnamefut.get(new ThreadSuspendable());
			this.hostnamefut	= null;
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
		return SUtil.bytesToString((long)bytes_received);
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
		return SUtil.bytesToString((long)val) + " / sec.";
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
	 *  Get the location (i.e. city, region, country).
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getLocation()
	{
		return GeoIPService.getGeoIPService().getLocation(hostip);
	}
	
	/**
	 *  Get the country code (e.g. de, us, ...).
	 *  Dynamically resolved by GeoIP, if available.
	 */
	public String	getCountryCode()
	{
		return GeoIPService.getGeoIPService().getCountryCode(hostip);
	}
	
	/**
	 *  Get the location as latitude,longitude.
	 */
	public String	getPosition()
	{
		return GeoIPService.getGeoIPService().getPosition(hostip);
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
		if(awainfo!=null)
		{
			setProperties(awainfo.getProperties());
			StatsDB.getDB().save(this);
		}
	}
	
	/**
	 *  Set db id
	 */
	public void	setDBId(Integer dbid)
	{
		this.dbid	= dbid;
	}
	
	/**
	 *  The the platform id.
	 */
	public void setId(String id)
	{
		this.id	= id;
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return "PlatformInfo(id="+getDBId()+", platform="+getId()+", host="+getHostName()+"("+getHostIP()+"), scheme="+getScheme()
			+ ", connected="+getConnectTime()+", disconnected="+getDisconnectTime()
			+ ", messages="+getMessageCount()+"("+getByteCount()+"), rate="+getTransferRate()
			+ ", properties="+properties+")";
	}

	/**
	 *  Set the disconnect time.
	 */
	public void setDisconnectDate(Date distime)
	{
		this.disconnect_time	= distime;
	}
	
	/**
	 *  Set the connect time.
	 */
	public void setConnectDate(Date contime)
	{
		this.connect_time	= contime;
	}

	/**
	 *  Get the preferred codecs.
	 *  The relay server will send awareness infos to the platform using this codecs.
	 */
	public ICodec[] getPreferredCodecs()
	{
		return pcodecs;
	}
	
	/**
	 *  Set the preferred codecs.
	 *  The relay server will send awareness infos to the platform using this codecs.
	 */
	public void setPreferredCodecs(ICodec[] pcodecs)
	{
		this.pcodecs	= pcodecs;
	}

	/**
	 *  Get the properties.
	 *  @return The properties, if any.
	 */
	public Map<String, String>	getProperties()
	{
		return properties;
	}
	
	/**
	 *  Set the properties.
	 *  @param props The properties.
	 */
	public void	setProperties(Map<String, String> props)
	{
		this.properties	= props;
	}
}
