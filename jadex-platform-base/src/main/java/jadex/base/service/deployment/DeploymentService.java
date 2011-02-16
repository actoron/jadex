package jadex.base.service.deployment;

import jadex.base.gui.filetree.RemoteFile;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

import java.awt.Desktop;
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
		return new Future(RemoteFile.convertToRemoteFiles(roots));
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
			Desktop.getDesktop().open(file);
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
