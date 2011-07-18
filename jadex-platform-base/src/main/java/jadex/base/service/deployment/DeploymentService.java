package jadex.base.service.deployment;

import jadex.base.gui.filetree.FileData;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/* $if !android $ */
import java.awt.Desktop;
/* $endif $ */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map.Entry;

/**
 *  Service for deployment files on file system.
 */
public class DeploymentService extends BasicService implements IDeploymentService
{
	/** Default fragment size 10kB. */
	public static final int FRAGMENT_SIZE = 1024*10;
	
	/** File id counter. */
	protected int fileidcnt;
	
	/** The incoming fragmented files. */
	protected LRU writestreams;
	
	/** The multi collection of outgoing fragmented files. */
	protected LRU readstreams;

	/**
	 *  Create a new deployment service.
	 */
	public DeploymentService(IServiceProvider provider)
	{
		super(provider.getId(), IDeploymentService.class, null);
		this.writestreams = new LRU(10);
		this.readstreams = new LRU(10);
		writestreams.setCleaner(new ILRUEntryCleaner()
		{
			public void cleanupEldestEntry(Entry eldest)
			{
				Object[] ws = (Object[])eldest.getValue();
				if(ws!=null)
				{
					try
					{
						FileOutputStream fos = (FileOutputStream)ws[0];
						fos.close();
					}
					catch(Exception e)
					{
					}
				}
			}
		});
		readstreams.setCleaner(new ILRUEntryCleaner()
		{
			public void cleanupEldestEntry(Entry eldest)
			{
				Object[] ws = (Object[])eldest.getValue();
				if(ws!=null)
				{
					try
					{
						FileInputStream fis = (FileInputStream)ws[0];
						fis.close();
					}
					catch(Exception e)
					{
					}
				}
			}
		});
	}
	
	/**
	 *  Get a file.
	 *  @return The file data as FileData.
	 */
	public IFuture getFile(String path, String fileid)
	{
		Future ret = new Future();
		
		File file = new File(path);
		
		try
		{
			FileInputStream fis;
			int fragment;
			
			if(fileid==null)
				fileid = getNextFileId();
			
			Object[] ws = (Object[])readstreams.get(fileid);
			if(ws==null)
			{
				fis = new FileInputStream(file);
				fragment = 0;
			}
			else
			{
				fis = (FileInputStream)ws[0];
				fragment = ((Integer)ws[1]).intValue();
			}
			
			final int fragmentsize = FRAGMENT_SIZE;
			final int len = (int)file.length();
			int num = (int)(len/fragmentsize);
			final int last = (int)(len%fragmentsize);
			final int fragments = num + (last>0? 1: 0);
			
			FileContent fc = FileContent.createFragment(fis, file.getName(), fragment==fragments-1? last: fragmentsize, len);
			
			if(fragment==fragments-1)
			{
				try
				{
					fis.close();
				}
				catch(Exception e)
				{
				}
				readstreams.remove(fileid);
			}
			
			ret.setResult(new Object[]{fc, fileid});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		
//		try
//		{
//			FileContent fc = new FileContent(new File(path));
//			ret.setResult(fc);
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
		return ret;
	}

	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return null when all was ok.
	 */
	public IFuture putFile(FileContent filecontent, String path, String fileid)
	{
		Future ret = new Future();
		
		try
		{
			FileOutputStream fos;
			int len;
			
			if(fileid==null)
				fileid = getNextFileId();
			
			Object[] ws = (Object[])writestreams.get(fileid);
			if(ws==null)
			{
				fos = new FileOutputStream(new File(path+"/"+filecontent.getFilename()));
				len = 0;
			}
			else
			{
				fos = (FileOutputStream)ws[0];
				len = ((Integer)ws[1]).intValue();
			}
			
			len += filecontent.data.length;
			fos.write(filecontent.data);
			
			writestreams.put(fileid, new Object[]{fos, new Integer(len)});
			
			if(len == filecontent.getSize())
			{
				try
				{
					fos.close();
				}
				catch(Exception e)
				{
				}
				writestreams.remove(fileid);
			}
			ret.setResult(fileid);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Rename a file.
	 *  @param path The target path.
	 *  @return True, if rename was successful.
	 */
	public IFuture renameFile(String path, String name)
	{
		Future ret = new Future();
		try
		{
			File file = new File(path);
			String newname = file.getParent()+"/"+name;
			if(file.renameTo(new File(newname)))
			{
				ret.setResult(name);
			}
			else
			{
				ret.setException(new RuntimeException());
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Delete a file.
	 *  @param path The target path.
	 *  @return True, if delete was successful.
	 */
	public IFuture deleteFile(String path)
	{
		Future ret = new Future();
		try
		{
			// file.toPath().delete(); since 1.7 throws Exception
			File file = new File(path);
			if(file.delete())
			{
				ret.setResult(null);
			}
			else
			{
				ret.setException(new RuntimeException());
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the root devices.
	 *  @return The root device files.
	 */
	public IFuture getRoots()
	{
		File[] roots = File.listRoots();
		return new Future(FileData.convertToRemoteFiles(roots));
	}
	
	/**
	 *  Execute a file.
	 *  @param path The filename to execute.
	 */
	public IFuture openFile(String path)
	{
		Future ret = new Future();
		try
		{
			File file = new File(path);
			/* $if !android $ */
			Desktop.getDesktop().open(file);
			/* $endif $ */
			// exec produces strange exceptions?!
//			Runtime.getRuntime().exec(path);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the next file id.
	 */
	protected String getNextFileId()
	{
		return ""+fileidcnt++;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			Runtime.getRuntime().exec("notepad.exe");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
