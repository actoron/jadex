package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 *  Local meta information for a resource.
 *  Keeps hash codes, time stamps, vector times etc. for
 *  all files and directories of the resource.
 */
public class BackupResource
{
	//-------- attributes --------
	
	/** The resource root directory. */
	protected File	root;
	
	/** A stream needed for file locking. */
	protected FileOutputStream	lockfos;
	
	/** The lock file handle to assure only one backup instance is running for the resource. */
	protected FileLock	lock;
	
	/** The resource properties. */
	protected Properties props;
	
	//-------- constructors --------
	
	/**
	 *  Open a resource.
	 *  @param root	The resource root directory.
	 *  @throws Exception, if the resource is already opened by another component or process.
	 */
	public BackupResource(String id, File root, IComponentIdentifier cid)	throws Exception
	{
		this.root	= root;
		File	meta	= new File(root, ".jadexbackup");
		meta.mkdirs();
		
		this.lockfos	= new FileOutputStream(new File(meta, "lock"));
		this.lock	= lockfos.getChannel().tryLock();
		if(lock==null)
		{
			throw new RuntimeException("Resource is locked. Used by other backup process?");
		}
		
		this.props	= new Properties();
		File	fprops	= new File(meta, "resource.properties");
		if(fprops.exists())
		{
			FileInputStream	fips	= new FileInputStream(fprops);
			props.load(fips);
			fips.close();
		}
		else
		{
			if(id==null)
			{
				id	= root.getName()+"_"+UUID.randomUUID().toString();
			}
			props.setProperty("id", id);
			props.setProperty("localid", SUtil.createUniqueId(cid.getPlatformPrefix(), 3));
			setVTime(getLocalId(), 0);
			save();
		}
	}
	
	/**
	 *  Close the resource.
	 */
	public void	dispose()
	{
		try
		{
			lock.release();
			lockfos.close();
		}
		catch(IOException e)
		{
			// ignore.
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the resource id.
	 *  The resource id is a globally unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 */
	public String	getResourceId()
	{
		return props.getProperty("id");
	}
	
	/**
	 *  Get the local resource id.
	 *  The local resource id is a unique id that is
	 *  used to identify an individual instance of a
	 *  distributed resource on a specific host.
	 */
	public String	getLocalId()
	{
		return props.getProperty("localid");
	}
	
	/**
	 *  Get the resource root directory.
	 */
	public File	getResourceRoot()
	{
		return root;
	}

	/**
	 *  Get the file for a file info.
	 *  @param fi	The file info.
	 *  @return The local file.
	 */
	public File	getFile(FileInfo fi)
	{
		return new File(root, fi.getLocation().replace('/', File.separatorChar));
	}
	
	/**
	 *  Get the last known-to-be-synchronized vector time of a node.
	 *  @param node	The local resource id of the node.
	 *  @return	The time point. 
	 */
	public int	getVTime(String node)
	{
		int	ret	= 0;
		String	vtimekey	= "vtime_"+node;
		if(props.containsKey(vtimekey))
		{
			ret	= Integer.parseInt(props.getProperty(vtimekey));
		}
		return ret;
	}
	
	/**
	 *  Set the last known-to-be-synchronized vector time of a node.
	 *  @param node	The local resource id of the node.
	 *  @param time	The time point.
	 */
	public void	setVTime(String node, int time)
	{
		String	vtimekey	= "vtime_"+node;
		props.setProperty(vtimekey, Integer.toString(time));
		save();				
	}
	
	/**
	 *  Get the file info for a local file.
	 *  @param file The local file.
	 *  @return The file info.
	 */
	public FileInfo	getFileInfo(File file)
	{
		try
		{
			FileInfo	ret;
			String	rpath	= root.getCanonicalPath();
			String	fpath	= file.getCanonicalPath();
			if(!fpath.startsWith(rpath))
			{
				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
			}
			String	location	= rpath.equals(fpath) ? "/" : fpath.substring(rpath.length()).replace(File.separatorChar, '/');

			int	lvtime	= getVTime(getLocalId());
			
			// Existing file -> check for change.
			if(props.containsKey(location))
			{
				Tuple2<Long, String>	entry	= parseEntry(props.getProperty(location));
				ret	= new FileInfo(location, file.isDirectory(),  entry.getSecondEntity());
				
				// File changed -> increment local vector time point to indicate changes.
				if(file.lastModified()!=entry.getFirstEntity().longValue())
				{
					// Same as biggest local time -> increment both.
					if(lvtime == ret.getVTime(getLocalId()))
					{
						lvtime++;
						setVTime(getLocalId(), lvtime);
					}
					ret.updateVTime(getLocalId(), lvtime);
					props.setProperty(location, createEntry(file.lastModified(), ret.getVTime()));
					save();				
				}
			}
			
			// New file -> store at current lvtime.
			else
			{
				ret	= new FileInfo(location, file.isDirectory(), "");
				ret.updateVTime(getLocalId(), lvtime);
				props.setProperty(location, createEntry(file.lastModified(), ret.getVTime()));
				save();				
			}
			
			return ret;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Update the file info with e.g. remote information.
	 *  @param fi	The file info data to be merged with the local state.
	 */
	public void	updateFileInfo(FileInfo fi)
	{
		long	lastmod	= 0;
		if(props.containsKey(fi.getLocation()))
		{
			Tuple2<Long, String>	entry	= parseEntry(props.getProperty(fi.getLocation()));
			lastmod	= entry.getFirstEntity().longValue();
			FileInfo	orig = new FileInfo(fi.getLocation(), false,  entry.getSecondEntity());
			Map<String, Integer>	vtimes	= orig.getVTimeMap();
			for(String key: vtimes.keySet())
			{
				if(vtimes.get(key).intValue()>fi.getVTime(key))
				{
					fi.updateVTime(key, vtimes.get(key).intValue());
				}
			}
		}
		
		props.setProperty(fi.getLocation(), createEntry(lastmod, fi.getVTime()));
		save();
	}
	
	//-------- helper methods --------

	/**
	 *  Save the meta information.
	 */
	protected void	save()
	{
		try
		{
			File	meta	= new File(root, ".jadexbackup");
			File	fprops	= new File(meta, "resource.properties");
			FileOutputStream	fops	= new FileOutputStream(fprops);
			props.store(fops, "Jadex Backup meta information.");
			fops.close();
		}
		catch(Exception e)
		{
			// todo: deal with errors.
			e.printStackTrace();
		}
	}
	
	
	//-------- helper methods --------
	
	/**
	 *  Create a file entry.
	 *  @param timestamp	The current file time stamp.
	 *  @param vtime	The vector time string.
	 */
	protected static String	createEntry(long timestamp, String vtime)
	{
		return timestamp + "." + vtime;
	}
	
	/**
	 *  Parse a file entry.
	 *  @return A tuple containing the local time stamp and the vector time string.
	 */
	protected static Tuple2<Long, String>	parseEntry(String entry)
	{
		int	idx	= entry.indexOf('.');
		long	timestamp	= Long.parseLong(entry.substring(0, idx));
		String	vtime	= entry.substring(idx+1);
		return new Tuple2<Long, String>(new Long(timestamp), vtime);
	}
}
