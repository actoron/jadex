package jadex.backup.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.UUID;

/**
 *  Local meta information for a resource.
 *  Keeps hashcodes, timestamps, vector times etc. for
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
	public BackupResource(File root)	throws Exception
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
			String id	= root.getName()+"_"+UUID.randomUUID().toString();
			props.setProperty("id", id);
			FileOutputStream	fops	= new FileOutputStream(fprops);
			props.store(fops, "Jadex Backup meta information.");
			fops.close();
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
	 *  Get the resource root directory.
	 */
	public File	getResourceRoot()
	{
		return root;
	}

	//-------- helper methods --------
	
	/**
	 *  Get the file for a file info.
	 *  @param fi	The file info.
	 *  @return The local file.
	 */
	public File	toFile(FileInfo fi)
	{
		return new File(root, fi.getLocation().replace('/', File.separatorChar));
	}
	
	/**
	 *  Get the file info for a local file.
	 *  @param file The local file.
	 *  @return The file info.
	 */
	public FileInfo	toFileInfo(File file)
	{
		try
		{
			String	rpath	= root.getCanonicalPath();
			String	fpath	= file.getCanonicalPath();
			if(!fpath.startsWith(rpath))
			{
				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
			}
			
			FileInfo	ret	= new FileInfo();
			ret.setLocation(fpath.substring(rpath.length()+1).replace(File.separatorChar, '/'));
			ret.setDirectory(file.isDirectory());
			ret.setTimeStamp(file.lastModified());
			
			return ret;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
