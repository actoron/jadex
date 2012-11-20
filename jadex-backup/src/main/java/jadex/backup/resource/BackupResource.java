package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

	/** State when file was added locally and doesn't exist remotely. */
	public static final String FILE_LOCAL_ADDED = "file_local_added";

	/** State when file was deleted remotely and not changed locally. */
	public static final String FILE_REMOTE_DELETED = "file_remote_deleted";

	/** State when file was deleted locally and not changed remotely. */
	public static final String FILE_LOCAL_DELETED = "file_local_deleted";

	/** State when file was deleted remotely and changed locally. */
	public static final String FILE_REMOTE_DELETED_CONFLICT = "file_remote_deleted_conflict";

	/** State when file was deleted locally and changed remotely. */
	public static final String FILE_LOCAL_DELETED_CONFLICT = "file_local_deleted_conflict";
	
	/** State change table &lt;remote modified, local modified, remote existing, local existing> -> state. */
	public static final Map<Tuple, String>	STATE_CHANGES;
	
	static
	{
		// State change table <remote modified, local modified, remote existing, local existing>
		Map<Tuple, String>	statechanges	= new LinkedHashMap<Tuple, String>();
		statechanges.put(new Tuple(new Object[]{false,	false,	true,	true}),	FILE_UNCHANGED);
		statechanges.put(new Tuple(new Object[]{false,	true,	true,	true}),	FILE_LOCAL_MODIFIED);
		statechanges.put(new Tuple(new Object[]{true,	false,	true,	true}),	FILE_REMOTE_MODIFIED);
		statechanges.put(new Tuple(new Object[]{true,	true,	true,	true}),	FILE_CONFLICT);
		
		statechanges.put(new Tuple(new Object[]{false,	true,	false,	true}),	FILE_LOCAL_ADDED);
		statechanges.put(new Tuple(new Object[]{true,	false,	false,	true}),	FILE_REMOTE_DELETED);
		statechanges.put(new Tuple(new Object[]{true,	true,	false,	true}),	FILE_REMOTE_DELETED_CONFLICT);
		
		statechanges.put(new Tuple(new Object[]{false,	true,	true,	false}),	FILE_LOCAL_DELETED);
		statechanges.put(new Tuple(new Object[]{true,	false,	true,	false}),	FILE_REMOTE_ADDED);
		statechanges.put(new Tuple(new Object[]{true,	true,	true,	false}),	FILE_LOCAL_DELETED_CONFLICT);
		
		statechanges.put(new Tuple(new Object[]{false,	false,	false,	false}),	FILE_UNCHANGED);
		statechanges.put(new Tuple(new Object[]{false,	true,	false,	false}),	FILE_UNCHANGED);
		statechanges.put(new Tuple(new Object[]{true,	false,	false,	false}),	FILE_UNCHANGED);
		statechanges.put(new Tuple(new Object[]{true,	true,	false,	false}),	FILE_UNCHANGED);

		STATE_CHANGES	= Collections.unmodifiableMap(statechanges);
	}

	
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
	 *  Get the location for a file.
	 *  @param file	The file.
	 *  @return	The file location as absolute path from the resource root.
	 */
	public String	getLocation(File file)
	{
		try
		{
			String	rpath	= root.getCanonicalPath();
			String	fpath	= file.getCanonicalPath();
			if(!fpath.startsWith(rpath))
			{
				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
			}
			return rpath.equals(fpath) ? "/" : fpath.substring(rpath.length()).replace(File.separatorChar, '/');
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the file info for a local file.
	 *  Also updates the meta information, when the file or directory has changed on disk.
	 *  @param location The local file.
	 *  @return The file info.
	 */
	public FileInfo	getFileInfo(String location)
	{
		try
		{
			File	file	= getFile(location);
			FileInfo	ret	= new FileInfo(location, file.isDirectory(), file.isDirectory() ? 0 : file.length(),
				props.containsKey(location) ? props.getProperty(location) : "");
			
			// Known file? -> check if update needed based on last modified or...
			// ...change in file existence? -> store at current time
			if(file.lastModified()>ret.getVTime(getLocalId()) || ret.isExisting()!=file.exists())
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
				
				ret.bumpVTime(getLocalId(), file.exists() ? file.lastModified() : System.currentTimeMillis(), hash, file.exists());
				props.setProperty(location, ret.getVTime());
				save();				
			}
			// else return info for unknown file but don't save new info.
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Check if a local file info is current, i.e. the local file has not changed.
	 *  @param location	The file location.
	 *  @param fi	The local file info or null if the file does not exist locally.
	 *  @return False when the local file has changed with respect to the previous information.
	 */
	public boolean isCurrent(String location, FileInfo fi)
	{
		FileInfo current = getFileInfo(location);
		return current==null && fi==null || current!=null && fi!=null && current.getVTime(getLocalId())==fi.getVTime(getLocalId());
	}

	/**
	 *  Get the state of the local file with respect to a remote file info.
	 *  @param fi The remote file info.
	 *  @return The current local file info and the file state compared to the remote info.
	 */
	public Tuple2<FileInfo, String> getState(FileInfo fi)
	{
		// (hack? only for files)
		FileInfo	local	= getFileInfo(fi.getLocation());
		String ret	= STATE_CHANGES.get(new Tuple(new Object[]{fi.isNewerThan(local), local.isNewerThan(fi), fi.isExisting(), local.isExisting()}));
		
		if(FILE_UNCHANGED.equals(ret))
		{
			// When not changed: add new time stamps to meta information
			local.updateVTimes(fi, true);
			props.setProperty(local.getLocation(), local.getVTime());
			save();
		}
		
//		System.out.println("state: "+ret+", "+fi.getLocation());
		
		return new Tuple2<FileInfo, String>(local, ret);
	}
	
	/**
	 *  Get the file infos of a directory.
	 */
	public List<FileInfo> getDirectoryContents(FileInfo dir)
	{
		File	fdir	= getFile(dir.getLocation());
		if(!fdir.isDirectory())
		{
			throw new IllegalArgumentException("Not a directory: "+dir.getLocation());
		}
		String[]	list = fdir.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return !".jadexbackup".equals(name);
			}
		});
		if(list==null)
		{
			throw new RuntimeException("Could not read directory: "+dir.getLocation());
		}
		
		if(getFileInfo(dir.getLocation()).isNewerThan(dir))
		{
			throw new RuntimeException("Local resource has changed: "+dir.getLocation());
		}
		
		Set<String>	deleted	= new LinkedHashSet<String>();
		for(Object loc: props.keySet())
		{
			String	sloc	= (String) loc;
			if(sloc.length()!=dir.getLocation().length() && sloc.startsWith(dir.getLocation()) && sloc.substring(dir.getLocation().length()+1).indexOf("/")==-1)
			{
				deleted.add((String)loc);
			}
		}
		
		List<FileInfo>	ret	= new ArrayList<FileInfo>();
		for(String file: list)
		{
			FileInfo	fi = getFileInfo(getLocation(new File(fdir, file)));
			ret.add(fi);
			deleted.remove(fi.getLocation());
		}
		
		for(String file: deleted)
		{
			FileInfo	fi = getFileInfo(getLocation(new File(fdir, file)));
			ret.add(fi);
		}

		return ret;
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
	 *  @param tmp	The new remote file already downloaded to a temporary location or null if the file was deleted remotely.
	 */
	public void	updateFromRemote(FileInfo localfi, FileInfo remotefi, File tmp)
	{
		if(!isCurrent(remotefi.getLocation(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getLocation());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(remotefi.getLocation());
			FileInfo	ofi	= getFileInfo(remotefi.getLocation());
			if(orig.exists())
			{
				if(!orig.delete())
				{
					throw new RuntimeException("Cannot delete: "+remotefi.getLocation());					
				}
			}
			if(tmp!=null)
			{
				orig.getParentFile().mkdirs();
				if(!tmp.renameTo(orig))
				{
					throw new RuntimeException("Cannot rename: "+remotefi.getLocation());
				}
			}

			// Update meta information to reflect new current state.
			// todo: file hash code.
			ofi.bumpVTime(getLocalId(), orig.exists() ? orig.lastModified() : System.currentTimeMillis(), null, orig.exists());
			ofi.updateVTimes(remotefi, true);
			props.setProperty(ofi.getLocation(), ofi.getVTime());
			save();
		}
	}

	/**
	 *  Copy the original file and update the file with a new version.
	 *  @param localfi	The local file info.
	 *  @param remotefi	The remote file info.
	 *  @param tmp	The new remote file already downloaded to a temporary location or null if the file was deleted remotely.
	 */
	public void updateAsCopy(FileInfo localfi, FileInfo remotefi, File tmp)
	{
		if(!isCurrent(remotefi.getLocation(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getLocation());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(remotefi.getLocation());
			FileInfo	ofi	= getFileInfo(remotefi.getLocation());
			if(!orig.exists())
			{
				throw new RuntimeException("File does not exist: "+remotefi.getLocation());					
			}
			if(!orig.renameTo(getCopyLocation(orig)))
			{
				throw new RuntimeException("Cannot rename: "+remotefi.getLocation());
			}
			if(tmp!=null)
			{
				orig.getParentFile().mkdirs();
				if(!tmp.renameTo(orig))
				{
					throw new RuntimeException("Cannot rename: "+remotefi.getLocation());
				}
			}

			// Update meta information to reflect new current state.
			// todo: file hash code.
			ofi.bumpVTime(getLocalId(), orig.exists() ? orig.lastModified() : System.currentTimeMillis(), null, orig.exists());
			ofi.updateVTimes(remotefi, true);
			props.setProperty(ofi.getLocation(), ofi.getVTime());
			save();
		}
	}
	
	/**
	 *  Override a remote change and update local file info as if the current local file was newer.
	 *  @param localfi	The local file info or null if the file does not exist locally.
	 *  @param remotefi	The remote file info.
	 */
	public void overrideRemoteChange(FileInfo localfi, FileInfo remotefi)
	{
		if(!isCurrent(remotefi.getLocation(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getLocation());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(remotefi.getLocation());
			FileInfo	ofi	= getFileInfo(remotefi.getLocation());

			// Update local file such that it becomes newer than the remote version.
			if(!orig.setLastModified(System.currentTimeMillis()))
			{
				throw new RuntimeException("Cannot set time stamp: "+remotefi.getLocation());					
			}
			
			// Update meta information to reflect new current state.
			// todo: file hash code.
			
			// Mark local file info as current with respect to remote version.
			ofi.setVTime(getLocalId(), orig.lastModified());	// Do not bump as file remained the same (todo: should be checked by hash code anyways)
			ofi.updateVTimes(remotefi, false);
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
