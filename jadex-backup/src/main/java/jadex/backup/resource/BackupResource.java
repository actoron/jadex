package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.Base64;
import jadex.commons.SUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
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
			if(getResourceId()==null || !getResourceId().equals(id))
			{
				throw new RuntimeException("Local resource already exists with different global id.");
			}
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
	 *  Also updates the meta information, when the file or directory has changed on disk.
	 *  @param file The local file.
	 *  @return The file info.
	 */
	public FileInfo	getFileInfo(File file)
	{
		try
		{
			FileInfo	ret	= null;
			String	rpath	= root.getCanonicalPath();
			String	fpath	= file.getCanonicalPath();
			if(!fpath.startsWith(rpath))
			{
				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
			}
			String	location	= rpath.equals(fpath) ? "/" : fpath.substring(rpath.length()).replace(File.separatorChar, '/');
			
			// Existing file -> load props and check if update needed.
			boolean	update	= false;
			if(props.containsKey(location))
			{
				ret	= new FileInfo(location, file.isDirectory(), file.isDirectory() ? 0 : file.length(), props.getProperty(location));
				update	= file.lastModified()>ret.getVTime(getLocalId());
			}
			
			// New file -> store at current time.
			else if(file.exists())
			{
				ret	= new FileInfo(location, file.isDirectory(), file.isDirectory() ? 0 : file.length(), "");
				update	= true;
			}
			
			if(update)
			{
				
				String	hash	= null;
				
				// Build hash for contents of directory (Todo: hash for files)
				if(file.isDirectory())
				{
					final MessageDigest	md	= MessageDigest.getInstance("SHA-1");
					String[]	files	= file.list(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							return !".jadexbackup".equals(name);
						}
					});
					for(String name: files)
					{
						md.update((byte)0);	// marker between directory names to avoid {a, bc} being the same as {ab, c}. 
						md.update(name.getBytes("UTF-8"));
					}
					
					hash	= new String(Base64.encode(md.digest()));
				}
				
				ret.bumpVTime(getLocalId(), file.lastModified(), hash);
				props.setProperty(location, ret.getVTime());
				save();				
			}
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Check if the resource needs to be updated when compared to the given time stamps.
	 *  When no update is needed, additional time stamps (if any) are merged into the stored meta information.
	 *  @param fi	The file info data to be compared with the local state.
	 *  @return	True, when the local file needs updating.
	 *  @throws Exception when a conflict was found.
	 */
	public boolean	needsUpdate(FileInfo fi)
	{
		FileInfo	local	= getFileInfo(getFile(fi.getLocation()));
		boolean	update	= local==null || fi.isNewerThan(local);
		
		// When changed: check for conflict (hack? only for files)
		if(update && local!=null && !local.isDirectory() && local.isNewerThan(fi))
		{
			throw new RuntimeException("Found conflict: "+fi.getLocation());
		}
		
		// When not changed: add new time stamps to meta information
		if(!update && local!=null)
		{
			local.updateVTimes(fi);
		}
		
		return update;
	}

	/**
	 *  Get a temporary location for downloading a file.
	 *  @param path	The resource location.
	 *  @param remote	The download source.
	 */
	public File	getTempLocation(String path, IResourceService remote)
	{
		return new File(root, remote.getLocalId()+"_"+path.replace('/', '_'));
	}

	/**
	 *  Update a file with a new version.
	 *  @param fi	The remote file info.
	 *  @param tmp	The new file already downloaded to a temporary location.
	 *  @throws Exception when a conflict was detected.
	 */
	public void	updateFile(FileInfo fi, File tmp)
	{
		if(needsUpdate(fi))
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(fi.getLocation());
			FileInfo	ofi	= getFileInfo(orig);
			orig.delete();
			tmp.renameTo(orig);

			// Update meta information to reflect new current state.
			// todo: file hash code.
			if(ofi!=null)
			{
				ofi.updateVTimes(fi);
				ofi.setVTime(getLocalId(), orig.lastModified());
				props.setProperty(ofi.getLocation(), ofi.getVTime());
			}
			else
			{
				fi.setVTime(getLocalId(), orig.lastModified());
				props.setProperty(fi.getLocation(), fi.getVTime());
			}
			save();
		}
		else
		{
			tmp.delete();
		}
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
	
	/**
	 *  Read meta info.
	 */
	public static Properties readMetaInfo(File root)
	{
		Properties ret = null;
		
		File meta = new File(root, ".jadexbackup");
		if(meta.exists())
		{
			ret = new Properties();
			File fprops	= new File(meta, "resource.properties");
			if(fprops.exists())
			{
				try
				{
					FileInputStream	fips = new FileInputStream(fprops);
					ret.load(fips);
					fips.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the global id if already under jadex control.
	 */
	public static String getGlobalId(File root)
	{
		Properties props = readMetaInfo(root);
		if(props!=null)
		{
			return props.getProperty("id");
		}
		return null;
	}
}
