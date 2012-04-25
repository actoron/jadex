package jadex.base.service.deployment;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.deployment.FileContent;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.commons.Tuple2;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

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
@Service
public class DeploymentService implements IDeploymentService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent; 
	
//	/** File id counter. */
//	protected int fileidcnt;
//	
//	/** The incoming fragmented files. */
//	protected LRU writestreams;
//	
//	/** The multi collection of outgoing fragmented files. */
//	protected LRU readstreams;

//	/**
//	 *  Create a new deployment service.
//	 */
//	public DeploymentService(IServiceProvider provider)
//	{
//		super(provider.getId(), IDeploymentService.class, null);
//		this.writestreams = new LRU(10);
//		this.readstreams = new LRU(10);
//		writestreams.setCleaner(new ILRUEntryCleaner()
//		{
//			public void cleanupEldestEntry(Entry eldest)
//			{
//				Object[] ws = (Object[])eldest.getValue();
//				if(ws!=null)
//				{
//					try
//					{
//						FileOutputStream fos = (FileOutputStream)ws[0];
//						fos.close();
//					}
//					catch(Exception e)
//					{
//					}
//				}
//			}
//		});
//		readstreams.setCleaner(new ILRUEntryCleaner()
//		{
//			public void cleanupEldestEntry(Entry eldest)
//			{
//				Object[] ws = (Object[])eldest.getValue();
//				if(ws!=null)
//				{
//					try
//					{
//						FileInputStream fis = (FileInputStream)ws[0];
//						fis.close();
//					}
//					catch(Exception e)
//					{
//					}
//				}
//			}
//		});
//	}
	
	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return True, when the file has been copied.
	 */
	public ITerminableIntermediateFuture<Long> uploadFile(IInputConnection con, String path)
	{
		try
		{
			return con.writeToOutputStream(new FileOutputStream(path), agent.getExternalAccess());
		}
		catch(Exception e)
		{
			return new TerminableIntermediateFuture<Long>(e);
		}
	}
	
//	/**
//	 *  Get a file.
//	 *  @return The file data as FileData.
//	 */
//	public IFuture<Tuple2<FileContent,String>> getFile(String path, String fileid)
//	{
//		Future<Tuple2<FileContent,String>> ret = new Future<Tuple2<FileContent,String>>();
//		
//		File file = new File(path);
//		
//		try
//		{
//			FileInputStream fis;
//			int fragment;
//			
//			if(fileid==null)
//				fileid = getNextFileId();
//			
//			Object[] ws = (Object[])readstreams.get(fileid);
//			if(ws==null)
//			{
//				fis = new FileInputStream(file);
//				fragment = 0;
//			}
//			else
//			{
//				fis = (FileInputStream)ws[0];
//				fragment = ((Integer)ws[1]).intValue();
//			}
//			
//			final int fragmentsize = FRAGMENT_SIZE;
//			final int len = (int)file.length();
//			int num = (int)(len/fragmentsize);
//			final int last = (int)(len%fragmentsize);
//			final int fragments = num + (last>0? 1: 0);
//			
//			FileContent fc = FileContent.createFragment(fis, file.getName(), fragment==fragments-1? last: fragmentsize, len);
//			
//			if(fragment==fragments-1)
//			{
//				try
//				{
//					fis.close();
//				}
//				catch(Exception e)
//				{
//				}
//				readstreams.remove(fileid);
//			}
//			
//			ret.setResult(new Tuple2<FileContent,String>(fc, fileid));
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
//		
//		
////		try
////		{
////			FileContent fc = new FileContent(new File(path));
////			ret.setResult(fc);
////		}
////		catch(Exception e)
////		{
////			ret.setException(e);
////		}
//		return ret;
//	}

//	/**
//	 *  Put a file.
//	 *  @param file The file data.
//	 *  @param path The target path.
//	 *  @return null when all was ok.
//	 */
//	public IFuture<String> putFile(FileContent filecontent, String path, String fileid)
//	{
//		Future<String> ret = new Future<String>();
//		
//		try
//		{
//			FileOutputStream fos;
//			int len;
//			
//			if(fileid==null)
//				fileid = getNextFileId();
//			
//			Object[] ws = (Object[])writestreams.get(fileid);
//			if(ws==null)
//			{
//				fos = new FileOutputStream(new File(path+"/"+filecontent.getFilename()));
//				len = 0;
//			}
//			else
//			{
//				fos = (FileOutputStream)ws[0];
//				len = ((Integer)ws[1]).intValue();
//			}
//			
//			len += filecontent.getData().length;
//			fos.write(filecontent.getData());
//			
//			writestreams.put(fileid, new Object[]{fos, new Integer(len)});
//			
//			if(len == filecontent.getSize())
//			{
//				try
//				{
//					fos.close();
//				}
//				catch(Exception e)
//				{
//				}
//				writestreams.remove(fileid);
//			}
//			ret.setResult(fileid);
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Rename a file.
	 *  @param path The target path.
	 *  @return True, if rename was successful.
	 */
	public IFuture<String> renameFile(String path, String name)
	{
		Future<String> ret = new Future<String>();
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
	public IFuture<Void> deleteFile(String path)
	{
		Future<Void> ret = new Future<Void>();
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
	public IFuture<FileData[]> getRoots()
	{
		File[] roots = File.listRoots();
		return new Future<FileData[]>(FileData.convertToRemoteFiles(roots));
	}
	
	/**
	 *  Execute a file.
	 *  @param path The filename to execute.
	 */
	public IFuture<Void> openFile(String path)
	{
		Future<Void> ret = new Future<Void>();
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
	
//	/**
//	 *  Get the next file id.
//	 */
//	protected String getNextFileId()
//	{
//		return ""+fileidcnt++;
//	}
	
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
