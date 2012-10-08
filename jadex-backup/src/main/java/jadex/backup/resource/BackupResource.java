package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Iterator;
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
	 *  @param location	The resource-relative file location.
	 *  @return The local file.
	 */
	public File	getFile(String location)
	{
		return new File(root, location.replace('/', File.separatorChar));
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

			// Existing file -> check for change.
			if(props.containsKey(location))
			{
				ret	= new FileInfo(location, file.isDirectory(), file.isDirectory() ? 0 : file.length(), props.getProperty(location));
				
				// File changed -> increment local vector time point to indicate changes.
				if(file.lastModified()>ret.getVTime(getLocalId()))
				{
					ret.updateVTime(getLocalId(), file.lastModified());
					props.setProperty(location, ret.getVTime());
					save();				
				}
			}
			
			// New file -> store at current time.
			else
			{
				ret	= new FileInfo(location, file.isDirectory(), file.isDirectory() ? 0 : file.length(), "");
				ret.updateVTime(getLocalId(), file.lastModified());
				props.setProperty(location, ret.getVTime());
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
	 *  Check if the resource needs to be updated when compared to the given time stamps.
	 *  @param fi	The file info data to be compared with the local state.
	 *  @return	True, when the local file needs updating.
	 *  @throws Exception when a conflict was found.
	 */
	public boolean	needsUpdate(FileInfo fi)
	{
		boolean	update	= true;
		
		if(props.containsKey(fi.getLocation()))
		{
			update	= false;
			Map<String, Long>	vtimes	= FileInfo.parseVTime(props.getProperty(fi.getLocation()));
			for(Iterator<String> it=vtimes.keySet().iterator(); !update && it.hasNext(); )
			{
				String	key	= it.next();
				update	= fi.getVTime(key)>vtimes.get(key).longValue();
			}
		}
		
		if(update && getFile(fi.getLocation()).lastModified()>fi.getVTime(getLocalId()))
		{
			throw new RuntimeException("Found conflict: "+fi.getLocation());
		}
		
		return update;
	}

	/**
	 *  Check if some newer local time stamp exists when compared to the given time stamps.
	 *  @param fi	The file info data to be compared with the local state.
	 *  @throws Exception when a conflict was found.
	 */
	public void	checkForConflicts(FileInfo fi)
	{
		if(getFile(fi.getLocation()).lastModified()>fi.getVTime(getLocalId()))
		{
			throw new RuntimeException("Found conflict with: "+getLocalId());
		}
			
		if(props.containsKey(fi.getLocation()))
		{
			Map<String, Long>	vtimes	= FileInfo.parseVTime(props.getProperty(fi.getLocation()));
			for(String key: vtimes.keySet())
			{
				if(vtimes.get(key).intValue()>fi.getVTime(key))
				{
					throw new RuntimeException("Found conflict with: "+key);
				}
			}
		}
	}
	
	/**
	 *  Update the file info with e.g. remote information.
	 *  @param fi	The file info data to be merged with the local state.
	 *  @throws Exception when a conflict was found.
	 */
	public void	updateFileInfo(FileInfo fi)
	{
		checkForConflicts(fi);
		props.setProperty(fi.getLocation(), fi.getVTime());
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
}
