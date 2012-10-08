package jadex.backup.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 *  Public meta information about a file in a resource
 *  used for transfer between resource providers and clients.
 */
public class FileInfo
{
	//-------- attributes --------
	
	/** The file location relative to the resource root (using '/' as separator char). */
	protected String	location;
	
	/** True, if the file is a directory. */
	protected boolean	directory;
	
	/** The file size. */
	protected long	size;
	
	/** The cached vector times as map (transferred as string "platform1@time1.platform2@time2..."). */
	protected Map<String, Long>	vtimes;
	
	//-------- constructors --------
	
	/**
	 *  Create a file info.
	 */
	public FileInfo()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a file info.
	 */
	public FileInfo(String location, boolean directory, long size, String vtime)
	{
		this.location	= location;
		this.directory	= directory;
		this.size	= size;
		this.vtimes	= parseVTime(vtime);
	}
	
	//-------- bean accessors --------
	
	/**
	 *  Get the location.
	 *  @return the location.
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 *  Set the location.
	 *  @param location The location to set.
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	/**
	 *  Get the directory.
	 *  @return the directory.
	 */
	public boolean isDirectory()
	{
		return directory;
	}

	/**
	 *  Set the directory.
	 *  @param directory The directory to set.
	 */
	public void setDirectory(boolean directory)
	{
		this.directory = directory;
	}
	
	/**
	 *  Get the file size.
	 */
	public long	getSize()
	{
		return size;
	}
	
	/**
	 *  Set the file size.
	 */
	public void	setSize(long size)
	{
		this.size	= size;
	}

	/**
	 *  Get the vector time.
	 */
	public String	getVTime()
	{
		return vtimesToString(vtimes);
	}
	
	/**
	 *  Set the vector time.
	 */
	public void	setVTime(String vtime)
	{
		this.vtimes	= parseVTime(vtime);
	}

	//-------- methods --------
	
	/**
	 *  Get a part of the vector time.
	 *  @param node	The platform.
	 *  @return The time.
	 */
	public long	getVTime(String node)
	{
		return vtimes.containsKey(node) ? vtimes.get(node).longValue() : 0;
	}
	
	/**
	 *  Update a part of the vector time.
	 *  @param vtime	The original vtime string.
	 *  @param node	The platform.
	 *  @param time	The time.
	 */
	public void	updateVTime(String node, long time)
	{
		vtimes.put(node, new Long(time));
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the vector time as map.
	 */
	protected static Map<String, Long>	parseVTime(String vtime)
	{
		Map<String, Long>	vtimes	= new LinkedHashMap<String, Long>();
		StringTokenizer	stok	= new StringTokenizer(vtime, "@.", true);
		String	last	= null;
		while(stok.hasMoreTokens())
		{
			String	next	= stok.nextToken();
			if("@".equals(next) && stok.hasMoreTokens())
			{
				vtimes.put(last, new Long(Long.parseLong(stok.nextToken())));
			}
			last	= next;
		}
		return vtimes;
	}
	
	/**
	 *  Get the vector time as string.
	 */
	protected static String	vtimesToString(Map<String, Long> vtimes)
	{
		StringBuffer	buf	= new StringBuffer();
		for(String key: vtimes.keySet())
		{
			if(buf.length()>0)
			{
				buf.append('.');
			}
			buf.append(key);
			buf.append("@");
			buf.append(vtimes.get(key));
		}
		return buf.toString();
	}
}
