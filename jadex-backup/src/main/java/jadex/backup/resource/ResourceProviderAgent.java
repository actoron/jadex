package jadex.backup.resource;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  A component that publishes a local folder.
 */
@Arguments(
	@Argument(name="dir", clazz=String.class, description="The directory to publish.")
)
@ProvidedServices(
	@ProvidedService(type=IResourceService.class, implementation=@Implementation(expression="$pojoagent"))
)
@Agent
@Service
public class ResourceProviderAgent	implements IResourceService
{
	//-------- attributes --------
	
	/** The directory to publish as resource. */
	@AgentArgument
	protected String	dir;
	
	/** The resource meta information. */
	protected BackupResource	resource;
	
	/** Future for a scan in progress. */
	protected ITerminableFuture<Void>	scan;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@AgentCreated
	public IFuture<Void>	start()
	{
		Future<Void>	ret	= new Future<Void>();
		try
		{
			this.resource	= new BackupResource(new File(dir));
			this.scan	= scan();
			ret.setResult(null);			
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public void	stop()
	{
		if(scan!=null)
		{
			scan.terminate();
		}
		
		resource.dispose();
	}
	
	//-------- IResourceService interface --------

	/**
	 *  Get the resource id.
	 *  The resource id is a globally unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the service.
	 */
	public String	getResourceId()
	{
		return resource.getResourceId();
	}
	
	/**
	 *  Get information about local files.
	 */
	public IFuture<FileInfo[]>	getFiles(FileInfo dir)
	{
		List<FileInfo>	ret	= null;
		File	fdir	= resource.toFile(dir);
		if(dir==null || fdir.lastModified()>dir.getTimeStamp())
		{
			ret	= new ArrayList<FileInfo>();
			for(String file: fdir.list())
			{
				FileInfo	fi	= resource.toFileInfo(new File(fdir, file));
				if(!".jadexbackup".equals(fi.getLocation()))
				{
					ret.add(fi);
				}
			}
		}
		return new Future<FileInfo[]>(ret==null ? null : ret.toArray(new FileInfo[ret.size()]));
	}
	
	//-------- helper methods --------
	
	/**
	 *  Scan the directory and update the meta information.
	 */
	protected ITerminableFuture<Void>	scan()
	{
		
		return null;
	}
}
