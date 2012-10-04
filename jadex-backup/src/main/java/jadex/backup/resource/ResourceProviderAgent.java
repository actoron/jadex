package jadex.backup.resource;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
@Arguments({
	@Argument(name="dir", clazz=String.class, description="The directory to publish."),
	@Argument(name="id", clazz=String.class, description="The unique id of the global resource.")
})
@ProvidedServices(
	@ProvidedService(type=IResourceService.class, implementation=@Implementation(expression="$pojoagent"))
)
@Agent
@Service
public class ResourceProviderAgent	implements IResourceService
{
	//-------- attributes --------
	
	/** The component. */
	@Agent
	protected IInternalAccess	component;
	
	/** The directory to publish as resource. */
	@AgentArgument
	protected String	dir;
	
	/** The global resource id. */
	@AgentArgument
	protected String	id;
	
	/** The resource meta information. */
	protected BackupResource	resource;
	
//	/** Future for a scan in progress. */
//	protected ITerminableFuture<Void>	scan;
	
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
			this.resource	= new BackupResource(id, new File(dir), component.getComponentIdentifier());
//			this.scan	= scan();
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
//		if(scan!=null)
//		{
//			scan.terminate();
//		}
		
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
		File	fdir	= resource.getFile(dir);
//		if(fdir.lastModified()>dir.getTimeStamp())
		{
			ret	= new ArrayList<FileInfo>();
			for(String file: fdir.list())
			{
				FileInfo	fi	= resource.getFileInfo(new File(fdir, file));
				if(!".jadexbackup".equals(file))
				{
					ret.add(fi);
				}
			}
		}
		return new Future<FileInfo[]>(ret==null ? null : ret.toArray(new FileInfo[ret.size()]));
	}
	
	//-------- helper methods --------
	
//	/**
//	 *  Scan the directory and update the meta information.
//	 */
//	protected ITerminableFuture<Void>	scan()
//	{
//		TerminableFuture<Void>	ret	= new TerminableFuture<Void>();
//		
//		List<File>	todo	= new LinkedList<File>();
//		todo.add(resource.getResourceRoot());
//		scan(todo, ret);
//		
//		return ret;
//	}
//	
//	/**
//	 *  Incrementally scan local files and directories.
//	 */
//	protected void	scan(final List<File> todo, final TerminableFuture<Void> ret)
//	{
//		// Keep processing for 50 milliseconds.
//		long	start	= System.currentTimeMillis(); 
//		while(!ret.isDone() && !todo.isEmpty() && System.currentTimeMillis()-start<50)
//		{
//			File	next	= todo.remove(0);
//			if(next.isDirectory())
//			{
//				// Todo: meta information about directories.
//				todo.addAll(Arrays.asList(next.listFiles()));
//			}
//			else
//			{
//				FileInfo	fi	= resource.getFileInfo(next);
//				if(fi.getTimeStamp()!=next.lastModified())
//				{
//					fi.setTimeStamp(next.lastModified());
//					resource.setFileInfo(next, fi);
//				}
//			}
//		}
//		
//		if(todo.isEmpty())
//		{
//			ret.setResultIfUndone(null);
//		}
//		else if(!ret.isDone())
//		{
//			// Continue after step to stay reactive.
//			component.getExternalAccess().scheduleStep(new IComponentStep<Void>()
//			{
//				public IFuture<Void> execute(IInternalAccess ia)
//				{
//					scan(todo, ret);
//					return IFuture.DONE;
//				}
//			});
//		}
//	}
}
