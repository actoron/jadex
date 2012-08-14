package jadex.base.service.deployment;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Service for deployment files on file system.
 */
@Service
public class DeploymentService implements IDeploymentService
{
	/** The agent. */
	@ServiceComponent
	protected IExternalAccess agent; 
	
	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return True, when the file has been copied.
	 */
	public ITerminableIntermediateFuture<Long> uploadFile(IInputConnection con, String path, String name)
	{
//		TerminableIntermediateFuture<Long> ret = new TerminableIntermediateFuture<Long>();
//		ret.setFinished();
//		return ret;
		System.out.println("uploadFile: "+Thread.currentThread());
		try
		{
			return con.writeToOutputStream(new FileOutputStream(path+File.separator+name), agent);
		}
		catch(Exception e)
		{
			return new TerminableIntermediateFuture<Long>(e);
		}
	}
	
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
	public IFuture<Void> openFile(final String path)
	{
		final Future<Void> ret = new Future<Void>();
		SServiceProvider.getService(agent.getServiceProvider(), IContextService.class).addResultListener(
				new DefaultResultListener<IContextService>()
				{
					public void resultAvailable(IContextService cs)
					{
						try
						{
							cs.openFile(path);
						}
						catch (IOException e)
						{
							e.printStackTrace();
							ret.setException(e);
						}
					}
				});

		// exec produces strange exceptions?!
		// Runtime.getRuntime().exec(path);
		ret.setResult(null);
		return ret;
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
