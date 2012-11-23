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

/**
 *  A component that publishes a local folder.
 */
@Arguments({
	@Argument(name="resource", clazz=IBackupResource.class, description="The backup resource."),
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
	
	/** The resource meta information. */
	@AgentArgument
	protected IBackupResource	resource;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@AgentCreated
	public IFuture<Void>	start()
	{
		if(resource==null)
		{
			return new Future<Void>(new IllegalArgumentException("Ressource nulls."));
		}
		
		return IFuture.DONE;
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
	public IBackupResource	getResource()
	{
		return resource;
	}
}
