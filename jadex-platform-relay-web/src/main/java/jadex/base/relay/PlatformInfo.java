package jadex.base.relay;

import jadex.bridge.service.types.awareness.AwarenessInfo;

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
	
	/** The platform id. */
	protected Object	id;
	
	/** The platform host. */
	protected String	host;
	
	/** The time when the connection was established. */
	protected Date	connect_time;
	
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
	public PlatformInfo(Object id, String host)
	{
		this.id	= id;
		this.host	= host;
		this.connect_time	= new Date();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id.
	 */
	public Object	getId()
	{
		return id;
	}
	
	/**
	 *  Get the host.
	 */
	public String	getHost()
	{
		return host;
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
	 *  Get the average transfer rate as beautified string.
	 */
	public String	getTransferRate()
	{
		double	val	= bytes_received / total_transmission_time;
		return bytesToString(val) + " / sec.";
	}
	
	/**
	 *  Get the awareness info.
	 */
	public AwarenessInfo	getAwarenessInfo()
	{
		return awainfo;
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
	}
	
	/**
	 *  Platform with same id has reconnected.
	 */
	public void	reconnect(String host)
	{
		// Todo: store history of previous values (with max length?)
		this.host	= host;
		this.connect_time	= new Date();
	}
	
	/**
	 *  Set the awareness info.
	 */
	public void	setAwarenessInfo(AwarenessInfo awainfo)
	{
		this.awainfo	= awainfo;
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
}
