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
import java.io.InputStream;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 *  Local meta information for a resource.
 *  Keeps hash codes, time stamps, vector times etc. for
 *  all files and directories of the resource.
 */
public class BackupResource implements IBackupResource
{
	//-------- attributes --------
	
	/** The resource root directory. */
	protected File	root;
	
	/** A stream needed for file locking. */
	protected FileOutputStream	lockfos;
	
	/** The lock file handle to assure only one backup instance is running for the resource. */
	protected FileLock	lock;
	
	/** The resource properties. */
	protected Properties	props;
	
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
			props.setProperty("localid", SUtil.createUniqueId(cid.getPlatformPrefix(), 3)+"_"+root.getCanonicalPath());
			save();
		}
	}
	
	/** 
	 * 
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
	 * 
	 */
	public String	getResourceId()
	{
		return props.getProperty("id");
	}
	
	/** 
	 * 
	 */
	public String	getLocalId()
	{
		return props.getProperty("localid");
	}
	
	/** 
	 * 
	 */
	public File	getResourceRoot()
	{
		return root;
	}

	/** 
	 * 
	 */
	protected File	getFile(String location)
	{
		return new File(root, location.replace('/', File.separatorChar));
	}
	
	/** 
	 * 
	 */
	public InputStream	getFileData(String location)
	{
		try
		{
			File file = new File(root, location.replace('/', File.separatorChar));
			return new FileInputStream(file);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/** 
	 * 
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
	 * 
	 */
	public FileMetaInfo	getFileInfo(String location)
	{
		try
		{
			File file = getFile(location);
			FileMetaInfo ret = new FileMetaInfo(new FileData(location, file.isDirectory(), file.exists(), file.isDirectory() ? 0 : file.length(), file.lastModified()),
				props.containsKey(location) ? props.getProperty(location) : null);
			
			// Known file? -> check if update needed based on last modified or...
			// ...change in file existence? -> store at current time
//			if((file.lastModified()>ret.getVTime(getLocalId()) && ret.getSize()!=ret.getData().getSize()) 
//				|| ret.isExisting()!=file.exists())
			if(file.lastModified()>ret.getVTime(getLocalId()) || ret.isExisting()!=file.exists())
			{
				// file.lastModified()>ret.getVTime(getLocalId()) is true because ret.getVTime(getLocalId()) is 0 at beginning
				// update meta info
				String	hash = file.exists()? new String(Base64.encode(SUtil.computeFileHash(file.getCanonicalPath()))): null;
				ret.bumpVTime(getLocalId(), file.exists()? file.lastModified() : System.currentTimeMillis(), hash, file.exists(), file.length());
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
	 * 
	 */
	public boolean isCurrent(String location, FileMetaInfo fi)
	{
		FileMetaInfo current = getFileInfo(location);
		return current==null && fi==null || current!=null && fi!=null && current.getVTime(getLocalId())==fi.getVTime(getLocalId());
	}

	/** 
	 * 
	 */
	public Tuple2<FileMetaInfo, String> getState(FileMetaInfo fi)
	{
		// (hack? only for files)
		FileMetaInfo	local	= getFileInfo(fi.getPath());
		Tuple tup = new Tuple(new Object[]{fi.isNewerThan(local), local.isNewerThan(fi), fi.isExisting(), local.isExisting()});
//		System.out.println("getState: "+fi.getPath());
		String ret	= STATE_CHANGES.get(tup);
		
		if(FILE_UNCHANGED.equals(ret))
		{
			// When not changed: add new time stamps to meta information
			local.updateVTimes(fi, true);
			props.setProperty(local.getPath(), local.getVTime());
			save();
		}
		
		System.out.println("state: "+ret+", "+fi.getPath());
		
		return new Tuple2<FileMetaInfo, String>(local, ret);
	}
	
	/** 
	 * 
	 */
	public List<FileMetaInfo> getDirectoryContents(FileMetaInfo dir)
	{
		File	fdir	= getFile(dir.getPath());
		if(!fdir.isDirectory())
		{
			throw new IllegalArgumentException("Not a directory: "+dir.getPath());
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
			throw new RuntimeException("Could not read directory: "+dir.getPath());
		}
		
		if(getFileInfo(dir.getPath()).isNewerThan(dir))
		{
			throw new RuntimeException("Local resource has changed: "+dir.getPath());
		}
		
		Set<String>	deleted	= new LinkedHashSet<String>();
		for(Object loc: props.keySet())
		{
			String	sloc	= (String) loc;
			if(sloc.length()!=dir.getPath().length() && sloc.startsWith(dir.getPath()) && sloc.substring(dir.getPath().length()+1).indexOf("/")==-1)
			{
				deleted.add((String)loc);
			}
		}
		
		List<FileMetaInfo>	ret	= new ArrayList<FileMetaInfo>();
		for(String file: list)
		{
			FileMetaInfo	fi = getFileInfo(getLocation(new File(fdir, file)));
			ret.add(fi);
			deleted.remove(fi.getPath());
		}
		
		for(String file: deleted)
		{
			FileMetaInfo	fi = getFileInfo(getLocation(new File(fdir, file)));
			ret.add(fi);
		}

		return ret;
	}

	/** 
	 * 
	 */
	public File	getTempLocation(String path, IResourceService remote)
	{
		File	meta	= new File(root, ".jadexbackup");
		return new File(meta, remote.getLocalId()+"_"+path.replace('/', '_'));
	}

	/** 
	 * 
	 */
	public void	updateFromRemote(FileMetaInfo localfi, FileMetaInfo remotefi, File tmp)
	{
		if(!isCurrent(remotefi.getPath(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getPath());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(remotefi.getPath());
			FileMetaInfo	ofi	= getFileInfo(remotefi.getPath());
			if(orig.exists())
			{
				if(!orig.delete())
				{
					throw new RuntimeException("Cannot delete: "+remotefi.getPath());					
				}
			}
			if(tmp!=null)
			{
				orig.getParentFile().mkdirs();
				if(!tmp.renameTo(orig))
				{
					throw new RuntimeException("Cannot rename: "+remotefi.getPath());
				}
			}

			// Update meta information to reflect new current state.
			// todo: file hash code.
			ofi.bumpVTime(getLocalId(), orig.exists() ? orig.lastModified() : System.currentTimeMillis(), null, orig.exists(), orig.length());
			ofi.updateVTimes(remotefi, true);
			props.setProperty(ofi.getPath(), ofi.getVTime());
			save();
		}
	}

	/** 
	 * 
	 */
	public void updateAsCopy(FileMetaInfo localfi, FileMetaInfo remotefi, File tmp)
	{
		if(!isCurrent(remotefi.getPath(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getPath());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(remotefi.getPath());
			FileMetaInfo	ofi	= getFileInfo(remotefi.getPath());
			if(!orig.exists())
			{
				throw new RuntimeException("File does not exist: "+remotefi.getPath());					
			}
			if(!orig.renameTo(getCopyLocation(orig)))
			{
				throw new RuntimeException("Cannot rename: "+remotefi.getPath());
			}
			if(tmp!=null)
			{
				orig.getParentFile().mkdirs();
				if(!tmp.renameTo(orig))
				{
					throw new RuntimeException("Cannot rename: "+remotefi.getPath());
				}
			}

			// Update meta information to reflect new current state.
			// todo: file hash code.
			ofi.bumpVTime(getLocalId(), orig.exists() ? orig.lastModified() : System.currentTimeMillis(), null, orig.exists(), orig.length());
			ofi.updateVTimes(remotefi, true);
			props.setProperty(ofi.getPath(), ofi.getVTime());
			save();
		}
	}
	
	/** 
	 * 
	 */
	public void overrideRemoteChange(FileMetaInfo localfi, FileMetaInfo remotefi)
	{
		if(!isCurrent(remotefi.getPath(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getPath());
		}
		else
		{
			// Todo: all this should be atomic (how?)
			File	orig	= getFile(remotefi.getPath());
			FileMetaInfo	ofi	= getFileInfo(remotefi.getPath());

			// Update local file such that it becomes newer than the remote version.
			if(!orig.setLastModified(System.currentTimeMillis()))
			{
				throw new RuntimeException("Cannot set time stamp: "+remotefi.getPath());					
			}
			
			// Update meta information to reflect new current state.
			// todo: file hash code.
			
			// Mark local file info as current with respect to remote version.
			ofi.setVTime(getLocalId(), orig.lastModified());	// Do not bump as file remained the same (todo: should be checked by hash code anyways)
			ofi.updateVTimes(remotefi, false);
			props.setProperty(ofi.getPath(), ofi.getVTime());
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
