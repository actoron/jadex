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

/**
 *  A component that publishes a local folder.
 */
@Arguments({
	@Argument(name="dir", clazz=String.class, description="The directory to publish."),
	@Argument(name="id", clazz=String.class, description="The unique id of the global resource.")
})
@ProvidedServices({
	@ProvidedService(type=IResourceService.class, implementation=@Implementation(ResourceService.class)),
	@ProvidedService(type=ILocalResourceService.class, implementation=@Implementation(LocalResourceService.class))
})
@Agent
@Service(IResourceService.class)
public class ResourceProviderAgent
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
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@AgentCreated
	public IFuture<Void>	start()
	{
		if(dir==null)
		{
			return new Future<Void>(new IllegalArgumentException("Dir nulls."));
		}
		if(id==null)
		{
			return new Future<Void>(new IllegalArgumentException("Id nulls."));
		}
		
		Future<Void>	ret	= new Future<Void>();
		try
		{
			this.resource	= new BackupResource(id, new File(dir), component.getComponentIdentifier());
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
		resource.dispose();
	}
	
	//-------- methods --------

	/**
	 *  Get the backup resource.
	 */
	public BackupResource	getResource()
	{
		return resource;
	}
}
