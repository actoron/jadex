package jadex.base.service.deployment;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

import java.io.File;

/**
 *  Service for deployment files on file system.
 */
public class DeploymentService extends BasicService implements IDeploymentService
{
	/**
	 *  Create a new deployment service.
	 */
	public DeploymentService(IServiceProvider provider)
	{
		super(provider.getId(), IDeploymentService.class, null);
	}
	
	/**
	 *  Get a file.
	 *  @return The file data as FileData.
	 */
	public IFuture getFile(String path)
	{
		Future ret = new Future();
		try
		{
			FileData fd = new FileData(new File(path));
			ret.setResult(fd);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}

	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return null when all was ok.
	 */
	public IFuture putFile(FileData filedata, String path)
	{
		Future ret = new Future();
		try
		{
			filedata.writeFile(new File(path+"/"+filedata.getFilename()));
			ret.setResult(null);
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
			file.renameTo(new File(name));
			ret.setResult(name);
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
		File file = new File(path);
//		System.out.println(file.exists()+" "+file.canRead()+" "+file.canWrite()+" "+file.canExecute());
//		try
//		{
//			file.toPath().delete();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		if(file.delete())
		{
			ret.setResult(null);
		}
		else
		{
			ret.setException(new RuntimeException());
		}
		return ret;
	}
}
