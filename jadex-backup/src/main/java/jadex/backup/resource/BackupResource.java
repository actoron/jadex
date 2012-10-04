package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.StringTokenizer;
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
	
	/** The local component identifier. */
	protected IComponentIdentifier	cid;
	
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
		this.cid	= cid;
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
			props.setProperty("vtime", "0");
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
	 *  Get the file info for a local file.
	 *  @param file The local file.
	 *  @return The file info.
	 */
	public FileInfo	getFileInfo(File file)
	{
		try
		{
			String	rpath	= root.getCanonicalPath();
			String	fpath	= file.getCanonicalPath();
			if(!fpath.startsWith(rpath))
			{
				throw new IllegalArgumentException("File '"+fpath+"' must be contained in resource root '"+rpath+"'.");
			}
			
			String	location	= fpath.substring(rpath.length()+1).replace(File.separatorChar, '/');
			
			String	vtime	= null;
			if(props.containsKey(location))
			{
				Tuple2<Long, String>	entry	= parseEntry(props.getProperty(location));
				vtime	= entry.getSecondEntity();
				if(file.lastModified()!=entry.getFirstEntity().longValue())
				{
					// Increment local vector time point to indicate changes.
					int	flvtime	= getVTime(vtime, cid.getPlatformPrefix());
					int	lvtime	= 0;
					if(props.containsKey("vtime"))
					{
						lvtime	= Integer.parseInt(props.getProperty("vtime"));
					}

					if(lvtime>flvtime)
					{
						flvtime	= lvtime;
					}
					else
					{
						flvtime	= ++lvtime;
						props.setProperty("vtime", Integer.toString(lvtime));
					}
					
					vtime	= updateVTime(vtime, cid.getPlatformPrefix(), flvtime);
					props.setProperty(location, createEntry(file.lastModified(), vtime));
					save();				
				}
			}
			else
			{
				int	lvtime	= 0;
				if(props.containsKey("vtime"))
				{
					lvtime	= Integer.parseInt(props.getProperty("vtime"));
				}
				vtime	= updateVTime(vtime, cid.getPlatformPrefix(), lvtime);
				props.setProperty(location, createEntry(file.lastModified(), vtime));
				save();				
			}
			
			FileInfo	ret	= new FileInfo(location, file.isDirectory(), vtime);
			
			return ret;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
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
	
	/**
	 *  Get a part of the vector time.
	 *  @param vtime	The vtime string.
	 *  @param node	The platform.
	 *  @return The time.
	 */
	protected static int	getVTime(String vtime, String node)
	{
		int	ret	= 0;
		StringBuffer	buf	= new StringBuffer();
		StringTokenizer	stok	= new StringTokenizer(vtime, "@.", true);
		String	last	= null;
		while(stok.hasMoreTokens())
		{
			String	next	= stok.nextToken();
			buf.append(next);
			if("@".equals(next) && node.equals(last) && stok.hasMoreTokens())
			{
				ret	= Integer.parseInt(stok.nextToken());
				break;
			}
			last	= next;
		}
			
		return ret;
	}
	
	/**
	 *  Update a part of the vector time.
	 *  @param vtime	The original vtime string.
	 *  @param node	The platform.
	 *  @param time	The time.
	 *  @return The updated vtime string.
	 */
	protected static String	updateVTime(String vtime, String node, int time)
	{
		boolean	found	= false;
		if(vtime!=null)
		{
			StringBuffer	buf	= new StringBuffer();
			StringTokenizer	stok	= new StringTokenizer(vtime, "@.", true);
			String	last	= null;
			while(stok.hasMoreTokens())
			{
				String	next	= stok.nextToken();
				buf.append(next);
				if("@".equals(next) && node.equals(last) && stok.hasMoreTokens())
				{
					found	= true;
					stok.nextToken();	// skip old time.
					buf.append(time);	// add new time.
				}
				last	= next;
			}
			
			if(!found)
			{
				if(buf.length()>0)
				{
					buf.append('.');
				}
				buf.append(node);
				buf.append("@");
				buf.append(time);
			}
			
			vtime	= buf.toString();
		}
		else
		{
			vtime	= node + "@" + time;
		}
		
		return vtime;
	}
}
