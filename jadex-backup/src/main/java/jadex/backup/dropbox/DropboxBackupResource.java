package jadex.backup.dropbox;

import jadex.backup.resource.FileData;
import jadex.backup.resource.FileMetaInfo;
import jadex.backup.resource.IBackupResource;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.bouncycastle.util.encoders.Base64;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.WebAuthSession;

/**
 * 
 */
public class DropboxBackupResource implements IBackupResource
{
	//-------- attributes --------
	
//	/** The resource root directory. */
//	protected File	root;
//	
//	/** A stream needed for file locking. */
//	protected FileOutputStream	lockfos;
//	
//	/** The lock file handle to assure only one backup instance is running for the resource. */
//	protected FileLock	lock;
	
	/** The resource properties. */
	protected Properties props;
	
	/** The dropbox api. */
	protected DropboxAPI<WebAuthSession> api;
	
	/** The cid. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------
	
	/**
	 *  Open a resource.
	 *  @param id The global id.
	 *  @param root	The resource root directory.
	 *  @throws Exception, if the resource is already opened by another component or process.
	 */
	public DropboxBackupResource(String id, IComponentIdentifier cid, 
		String akey, String asecret, String skey, String ssecret) throws Exception
	{
		this.cid = cid;
		this.api = DropboxTest.createSession(akey, asecret, skey, ssecret);
		
		this.props	= new Properties();
		
		// Try to find the ".jadexbackup" folder 
		File f = new File("./.jadexbackup/resource.properties");
		if(f.exists())
		{
			FileInputStream	fips = new FileInputStream(f);
			props.load(fips);
			fips.close();
		}
		else
		{
			if(id==null)
			{
				id	= "dropbox_"+UUID.randomUUID().toString(); // todo?
			}
			props.setProperty("id", id);
			props.setProperty("localid", SUtil.createUniqueId(cid.getPlatformPrefix(), 3));
			save();
		}
		
//		// Try to find the ".jadexbackup" folder 
//		FileData fd = DropboxTest.getFileData(api, "./"+cid.getPlatformPrefix()+"/.jadexbackup/resource.properties");
//		if(fd!=null && fd.isExisting())
//		{
//			byte[] data = DropboxTest.getFileContent(api, fd.getPath());
//			ByteArrayInputStream is = new ByteArrayInputStream(data);
//			this.props	= new Properties();
//			props.load(is);
//		}
//		else
//		{
//			if(id==null)
//			{
//				id	= "dropbox_"+UUID.randomUUID().toString(); // todo?
//			}
//			props.setProperty("id", id);
//			props.setProperty("localid", SUtil.createUniqueId(cid.getPlatformPrefix(), 3));
//			save();
//		}
	}
	
	/** 
	 * 
	 */
	public void	dispose()
	{
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
	
//	/** 
//	 * 
//	 */
//	public File	getResourceRoot()
//	{
//		return root;
//	}

//	/** 
//	 * 
//	 */
//	public File	getFile(String location)
//	{
//		return new File(root, location.replace('/', File.separatorChar));
//	}
	
	/** 
	 * 
	 */
	public InputStream	getFileData(String path)
	{
		try
		{
			byte[] data = DropboxTest.getFileContent(api, path);
			return new ByteArrayInputStream(data);
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
	
//	/** 
//	 * 
//	 */
//	public String	getPath(File file)
//	{
//		try
//		{
//			String	rpath	= root.getCanonicalPath();
//			String	fpath	= file.getCanonicalPath();
//			if(!fpath.startsWith(rpath))
//			{
//				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
//			}
//			return rpath.equals(fpath) ? "/" : fpath.substring(rpath.length()).replace(File.separatorChar, '/');
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}
	
	/** 
	 * 
	 */
	public FileMetaInfo	getFileInfo(String path)
	{
		try
		{
			FileData file = DropboxTest.getFileData(api, path);
			FileMetaInfo ret = new FileMetaInfo(file, props.containsKey(path)? props.getProperty(path): null);
			
			// Known file? -> check if update needed based on last modified or...
			// ...change in file existence? -> store at current time
//			if((file.getLastModified()>ret.getVTime(getLocalId()) && ret.getSize()!=ret.getData().getSize())
//				|| ret.isExisting()!=file.isExisting())
			if(file.getLastModified()>ret.getVTime(getLocalId()) || ret.isExisting()!=file.isExisting())
			{
				String	hash	= null;
				
				// cannot get updated information about the file without downloading it
				// -> save info when file is uploaded
				
				ret.bumpVTime(getLocalId(), file.isExisting() ? file.getLastModified() : System.currentTimeMillis(), hash, file.isExisting(), file.getSize());
				props.setProperty(path, ret.getVTime());
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
		String ret	= STATE_CHANGES.get(new Tuple(new Object[]{fi.isNewerThan(local), local.isNewerThan(fi), fi.isExisting(), local.isExisting()}));
		
		if(FILE_UNCHANGED.equals(ret))
		{
			// When not changed: add new time stamps to meta information
			local.updateVTimes(fi, true);
			props.setProperty(local.getPath(), local.getVTime());
			save();
		}
		
		if(FILE_CONFLICT.equals(ret))
			System.out.println("state: "+ret+", "+fi.getPath());
		
		return new Tuple2<FileMetaInfo, String>(local, ret);
	}
	
	/** 
	 * 
	 */
	public List<FileMetaInfo> getDirectoryContents(FileMetaInfo dir)
	{
		List<FileData> cs = DropboxTest.getChildren(api, dir.getPath(), new IFilter<FileData>()
		{
			public boolean filter(FileData fd)
			{
				return fd.getPath().indexOf(".jadexbackup")==-1;
			}
		});
		
		if(cs==null)
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
			String	sloc	= (String)loc;
			if(sloc.length()!=dir.getPath().length() && sloc.startsWith(dir.getPath()) && sloc.substring(dir.getPath().length()+1).indexOf("/")==-1)
			{
				deleted.add((String)loc);
			}
		}
		
		List<FileMetaInfo>	ret	= new ArrayList<FileMetaInfo>();
		for(FileData file: cs)
		{
			FileMetaInfo	fi = getFileInfo(file.getPath());
			ret.add(fi);
			deleted.remove(fi.getPath());
		}
		
		for(String file: deleted)
		{
			FileMetaInfo fi = getFileInfo(file);
			ret.add(fi);
		}

		return ret;
	}

	/** 
	 * 
	 */
	public File	getTempLocation(String path, IResourceService remote)
	{
		File	meta	= new File("./.jadexbackup");
		return new File(meta, remote.getLocalId()+"_"+path.replace('/', '_'));
	}

	/** 
	 *  Called after remote file already has been downloaded and saved as tmp file.
	 */
	public void	updateFromRemote(FileMetaInfo localfi, FileMetaInfo remotefi, File tmp)
	{
		if(!isCurrent(remotefi.getPath(), localfi))
		{
			throw new RuntimeException("Local file has changed: "+remotefi.getPath());
		}
		else
		{
			try
			{
				FileData orig = DropboxTest.getFileData(api, remotefi.getPath());
				
				// upload file to dropbox
				FileInputStream fis = new FileInputStream(tmp);
				String hash = new String(Base64.encode(SUtil.computeFileHash(tmp.getAbsolutePath())));
				DropboxTest.overwriteFileData(api, localfi.getPath(), fis, tmp.length());
				// Ask again for file to save server update time
				FileData newf = DropboxTest.getFileData(api, remotefi.getPath());
				
				FileMetaInfo ofi = getFileInfo(remotefi.getPath());
	
				// Update meta information to reflect new current state.
				// todo: file hash code.
				ofi.bumpVTime(getLocalId(), orig.isExisting() ? orig.getLastModified() : newf.getLastModified(), hash, orig.isExisting(), orig.getSize());
				ofi.updateVTimes(remotefi, true);
				props.setProperty(ofi.getPath(), ofi.getVTime());
				save();
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
			try
			{
				FileData orig = DropboxTest.getFileData(api, remotefi.getPath());
				FileMetaInfo	ofi	= getFileInfo(remotefi.getPath());
				
				if(!orig.isExisting())
				{
					throw new RuntimeException("File does not exist: "+remotefi.getPath());					
				}
				
				// upload file to dropbox
				FileInputStream fis = new FileInputStream(tmp);
				DropboxTest.overwriteFileData(api, localfi.getPath(), fis, tmp.length());
				
				// Update meta information to reflect new current state.
				// todo: file hash code.
				ofi.bumpVTime(getLocalId(), orig.isExisting() ? orig.getLastModified() : System.currentTimeMillis(), null, orig.isExisting(), orig.getSize());
				ofi.updateVTimes(remotefi, true);
				props.setProperty(ofi.getPath(), ofi.getVTime());
				save();
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
			FileData orig = DropboxTest.getFileData(api, remotefi.getPath());
			DropboxTest.setLastModified(api, remotefi.getPath(), System.currentTimeMillis());
			
//			// Todo: all this should be atomic (how?)
//			File	orig	= getFile(remotefi.getPath());
			FileMetaInfo	ofi	= getFileInfo(remotefi.getPath());
//
//			// Update local file such that it becomes newer than the remote version.
//			if(!orig.setLastModified(System.currentTimeMillis()))
//			{
//				throw new RuntimeException("Cannot set time stamp: "+remotefi.getPath());					
//			}
			
			// Update meta information to reflect new current state.
			// todo: file hash code.
			
			// Mark local file info as current with respect to remote version.
			ofi.setVTime(getLocalId(), orig.getLastModified());	// Do not bump as file remained the same (todo: should be checked by hash code anyways)
			ofi.updateVTimes(remotefi, false);
			props.setProperty(ofi.getPath(), ofi.getVTime());
			save();
		}
	}
	
	//-------- helper methods --------

//	/**
//	 *  Save the meta information.
//	 */
//	protected void	save()
//	{
//		try
//		{
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			props.store(os, "Jadex Backup meta information.");
//			byte[] data = os.toByteArray(); // todo: avoid using byte[] holding complete file in mem
//			ByteArrayInputStream is = new ByteArrayInputStream(data);
//			DropboxTest.putFileData(api, "/.jadexbackup/resource.properties", is, data.length);
//		}
//		catch(RuntimeException e)
//		{
//			throw e;
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}
	
	/**
	 *  Save the meta information.
	 */
	protected void	save()
	{
		try
		{
			File meta = new File("./.jadexbackup");
			meta.mkdirs();
			File fprops	= new File(meta, "resource.properties");
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
	
//	/**
//	 *  Get a location for saving a copy of a file.
//	 *  @param file	The original file name.
//	 *  @return The name for the file copy.
//	 */
//	protected static File	getCopyLocation(File orig)
//	{
//		File	dir	= orig.getParentFile();
//		String	name	= orig.getName();
//		String	prefix	= name;
//		String	suffix	= null;
//		int	idx	= name.lastIndexOf('.');
//		if(idx!=-1)
//		{
//			prefix	= name.substring(0, idx);
//			suffix	= name.substring(idx+1);
//			
//			idx	= prefix.lastIndexOf('.');
//			if(idx!=-1)
//			{
//				String	end	= prefix.substring(idx+1);
//				if(end.startsWith("copy"))
//				{
//					String	num	= end.substring(4);
//					boolean	strip	= "".equals(num);
//					try
//					{
//						if(!strip)
//						{
//							Integer.parseInt(end);
//							strip	= true;
//						}
//					}
//					catch(NumberFormatException e)
//					{
//					}
//					if(strip)
//					{
//						prefix	= prefix.substring(0, idx);
//					}
//				}
//			}
//		}
//		
//		int	cnt	= 0;
//		File	ret	= new File(dir, prefix+".copy"+(suffix!=null ? "."+suffix : "."));
//		while(ret.exists())
//		{
//			ret	= new File(dir, prefix+".copy"+(++cnt)+(suffix!=null ? "."+suffix : "."));
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Read meta info.
//	 */
//	public static Properties readMetaInfo(File root)
//	{
//		Properties ret = null;
//		
//		File meta = new File(root, ".jadexbackup");
//		if(meta.exists())
//		{
//			ret = new Properties();
//			File fprops	= new File(meta, "resource.properties");
//			if(fprops.exists())
//			{
//				try
//				{
//					FileInputStream	fips = new FileInputStream(fprops);
//					ret.load(fips);
//					fips.close();
//				}
//				catch(Exception e)
//				{
//				}
//			}
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the global id if already under jadex control.
//	 */
//	public static String getGlobalId(File root)
//	{
//		Properties props = readMetaInfo(root);
//		if(props!=null)
//		{
//			return props.getProperty("id");
//		}
//		return null;
//	}
}