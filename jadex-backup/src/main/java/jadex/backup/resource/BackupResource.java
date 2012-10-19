package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;

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
	//-------- constants --------
	
	/** State when local and remote files are the same. */
	public static final String FILE_UNCHANGED = "file_unchanged";

	/** State when remote file is newer than local file. */
	public static final String FILE_REMOTE_MODIFIED = "file_remote_modified";

	/** State when local file is newer than remote file. */
	public static final String FILE_LOCAL_MODIFIED = "file_local_modified";

	/** State when both local file and remote file are modified. */
	public static final String FILE_CONFLICT = "file_conflict";

	/** State when file was added remotely and doesn't exist locally. */
	public static final String FILE_REMOTE_ADDED = "file_remote_added";

//	public static final String FILE_REMOVED = "file_removed";
	
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
	 *  Check if a local file info is current, i.e. the local file has not changed.
	 *  @param fi The local file info.
	 *  @return False when the local file has changed with respect to the previous information.
	 */
	public boolean isCurrent(FileInfo fi)
	{
		FileInfo	current	= getFileInfo(getFile(fi.getLocation()));
		return current.getVTime(getLocalId())==fi.getVTime(getLocalId());
	}

	/**
	 *  Get the state of the local file with respect to a remote file info.
	 *  @param fi The remote file info.
	 *  @return The current local file info and the file state compared to the remote info.
	 */
	public Tuple2<FileInfo, String> getState(FileInfo fi)
	{
		String ret;
		
		FileInfo	local	= getFileInfo(getFile(fi.getLocation()));
		if(local==null)
		{
			ret = FILE_REMOTE_ADDED;
		}
		else
		{
			if(fi.isNewerThan(local))
			{
				// When changed: check for conflict (hack? only for files)
				if(!local.isDirectory() && !local.isNewerThan(fi))
				{
					ret = FILE_REMOTE_MODIFIED;
				}
				else
				{
					ret = FILE_CONFLICT;
				}
			}
			else
			{
				// When not changed: add new time stamps to meta information
				local.updateVTimes(fi);
				props.setProperty(local.getLocation(), local.getVTime());
				save();
				
				if(local.isNewerThan(fi))
				{
					ret	= FILE_LOCAL_MODIFIED;
				}
				else
				{
					ret	= FILE_UNCHANGED;
				}
			}
		}
		
//		System.out.println("state: "+ret+", "+fi.getLocation());
		
		return new Tuple2<FileInfo, String>(local, ret);
	}

	/**
	 *  Get a temporary location for downloading a file.
	 *  @param path	The resource location.
	 *  @param remote	The download source.
	 */
	public File	getTempLocation(String path, IResourceService remote)
	{
		File	meta	= new File(root, ".jadexbackup");
		return new File(meta, remote.getLocalId()+"_"+path.replace('/', '_'));
	}

	/**
	 *  Update a file with a new version.
	 *  @param localfi	The local file info.
	 *  @param remotefi	The remote file info.
	 *  @param tmp	The new remote file already downloaded to a temporary location.
	 */
	public void	updateFromRemote(FileInfo localfi, FileInfo remotefi, File tmp)
	{
		if(isCurrent(localfi))
		{
			throw new RuntimeException("Local file has changed: "+localfi.getLocation());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(localfi.getLocation());
			FileInfo	ofi	= getFileInfo(orig);
			if(orig.exists())
			{
				if(!orig.delete())
				{
					throw new RuntimeException("Cannot delete: "+localfi.getLocation());					
				}
			}
			orig.getParentFile().mkdirs();
			if(!tmp.renameTo(orig))
			{
				throw new RuntimeException("Cannot rename: "+localfi.getLocation());
			}

			// Update meta information to reflect new current state.
			// todo: file hash code.
			if(ofi!=null)
			{
				ofi.bumpVTime(getLocalId(), orig.lastModified(), null);
				ofi.updateVTimes(remotefi);
				props.setProperty(ofi.getLocation(), ofi.getVTime());
			}
			else
			{
				remotefi.setVTime(getLocalId(), orig.lastModified());
				props.setProperty(remotefi.getLocation(), remotefi.getVTime());
			}
			save();
		}
	}

	/**
	 *  Copy the original file and update the file with a new version.
	 *  @param localfi	The local file info.
	 *  @param remotefi	The remote file info.
	 *  @param tmp	The new remote file already downloaded to a temporary location.
	 */
	public void updateAsCopy(FileInfo localfi, FileInfo remotefi, File tmp)
	{
		if(isCurrent(localfi))
		{
			throw new RuntimeException("Local file has changed: "+localfi.getLocation());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(localfi.getLocation());
			FileInfo	ofi	= getFileInfo(orig);
			if(!orig.exists())
			{
				throw new RuntimeException("File does not exist: "+localfi.getLocation());					
			}
			if(!orig.renameTo(getCopyLocation(orig)))
			{
				throw new RuntimeException("Cannot rename: "+localfi.getLocation());
			}
			if(!tmp.renameTo(orig))
			{
				throw new RuntimeException("Cannot rename: "+localfi.getLocation());
			}

			// Update meta information to reflect new current state.
			// todo: file hash code.
			if(ofi!=null)
			{
				ofi.bumpVTime(getLocalId(), orig.lastModified(), null);
				ofi.updateVTimes(remotefi);
				props.setProperty(ofi.getLocation(), ofi.getVTime());
			}
			else
			{
				remotefi.setVTime(getLocalId(), orig.lastModified());
				props.setProperty(remotefi.getLocation(), remotefi.getVTime());
			}
			save();
		}
	}
	
	/**
	 *  Override a remote change and update local file info as if the current local file was newer.
	 *  @param localfi	The local file info.
	 *  @param remotefi	The remote file info.
	 */
	public void overrideRemoteChange(FileInfo localfi, FileInfo remotefi)
	{
		if(isCurrent(localfi))
		{
			throw new RuntimeException("Local file has changed: "+localfi.getLocation());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(localfi.getLocation());
			FileInfo	ofi	= getFileInfo(orig);

			// Update local file such that it becomes newer than the remote version.
			if(!orig.setLastModified(System.currentTimeMillis()))
			{
				throw new RuntimeException("Cannot set time stamp: "+localfi.getLocation());					
			}
			
			// Update meta information to reflect new current state.
			// todo: file hash code.
			
			// Mark local file info as current with respect to remote version.
			ofi.setVTime(getLocalId(), orig.lastModified());	// Do not bump as file remained the same (todo: should be checked by hash code anyways)
			ofi.updateVTimes(remotefi);
			props.setProperty(ofi.getLocation(), ofi.getVTime());
			save();
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
	 *  Get a location for saving a copy of a file.
	 *  @param file	The original file name.
	 *  @return The name for the file copy.
	 */
	protected static File	getCopyLocation(File orig)
	{
		File	dir	= orig.getParentFile();
		String	name	= orig.getName();
		String	prefix	= name;
		String	suffix	= null;
		int	idx	= name.lastIndexOf('.');
		if(idx!=-1)
		{
			prefix	= name.substring(0, idx);
			suffix	= name.substring(idx+1);
			
			idx	= prefix.lastIndexOf('.');
			if(idx!=-1)
			{
				String	end	= prefix.substring(idx+1);
				if(end.startsWith("copy"))
				{
					String	num	= end.substring(4);
					boolean	strip	= "".equals(num);
					try
					{
						if(!strip)
						{
							Integer.parseInt(end);
							strip	= true;
						}
					}
					catch(NumberFormatException e)
					{
					}
					if(strip)
					{
						prefix	= prefix.substring(0, idx);
					}
				}
			}
		}
		
		int	cnt	= 0;
		File	ret	= new File(dir, prefix+".copy"+(suffix!=null ? "."+suffix : "."));
		while(ret.exists())
		{
			ret	= new File(dir, prefix+".copy"+(++cnt)+(suffix!=null ? "."+suffix : "."));
		}
		
		return ret;
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
