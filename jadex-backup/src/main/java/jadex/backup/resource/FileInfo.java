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
	
	/** The vector time ("platform1@time1.platform2@time2..."). */
	protected String	vtime;
	
	/** The cached vector times as map (not transferred). */
	protected Map<String, Integer>	vtimes;
	
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
	public FileInfo(String location, boolean directory, String vtime)
	{
		this.location	= location;
		this.directory	= directory;
		this.vtime	= vtime;
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
	 *  Get the vector time.
	 */
	public String	getVTime()
	{
		return vtime;
	}
	
	/**
	 *  Set the vector time.
	 */
	public void	setVTime(String vtime)
	{
		this.vtime	= vtime;
	}

	//-------- methods --------
	
	/**
	 *  Get a part of the vector time.
	 *  @param node	The platform.
	 *  @return The time.
	 */
	public int	getVTime(String node)
	{
		return getVTimeMap().containsKey(node) ? getVTimeMap().get(node).intValue() : 0;
	}
	
	/**
	 *  Update a part of the vector time.
	 *  @param vtime	The original vtime string.
	 *  @param node	The platform.
	 *  @param time	The time.
	 *  @return The updated vtime string.
	 */
	public String	updateVTime(String node, int time)
	{
		Map<String, Integer>	vtimes	= getVTimeMap();
		vtimes.put(node, new Integer(time));
		
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
		setVTime(buf.toString());
		
		return getVTime();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the vector time as map.
	 */
	protected Map<String, Integer>	getVTimeMap()
	{
		if(vtimes==null)
		{
			vtimes	= new LinkedHashMap<String, Integer>();
			StringTokenizer	stok	= new StringTokenizer(vtime, "@.", true);
			String	last	= null;
			while(stok.hasMoreTokens())
			{
				String	next	= stok.nextToken();
				if("@".equals(next) && stok.hasMoreTokens())
				{
					vtimes.put(last, new Integer(Integer.parseInt(stok.nextToken())));
				}
				last	= next;
			}
		}
		return vtimes;
		
	}
}
