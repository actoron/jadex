package jadex.platform.service.registry;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.IRegistryEvent;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.bridge.service.types.registry.RegistryUpdateEvent;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Service for normal peers to send local changes to a selected superpeer.
 */
@Service
public class PeerRegistrySynchronizationService implements IPeerRegistrySynchronizationService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** Local registry observer. */
	protected LocalRegistryObserver lrobs;
	
	/** The current superpeer service. */
	protected ISuperpeerRegistrySynchronizationService spregser;
	
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
		// Subscribe to changes of the local registry to inform my superpeer
		lrobs = new LocalRegistryObserver(component.getComponentIdentifier(), new AgentDelayRunner(component))
		{
			public void notifyObservers(final RegistryEvent event)
			{
				getSuperpeerService(false).addResultListener(new ComponentResultListener<ISuperpeerRegistrySynchronizationService>(new IResultListener<ISuperpeerRegistrySynchronizationService>()
				{
					public void resultAvailable(final ISuperpeerRegistrySynchronizationService spser)
					{
//						System.out.println("localobs");
						IResultListener<RegistryUpdateEvent> lis = new IResultListener<RegistryUpdateEvent>()
						{
							public void resultAvailable(RegistryUpdateEvent spevent) 
							{
								if(spevent.isRemoved())
								{
									spser.updateClientData(lrobs.getCurrentStateEvent()).addResultListener(this);
									System.out.println("Send full client update to superpeer: "+((IService)spregser).getServiceIdentifier().getProviderId());
								}
								// Calls notify observers at latest 
								lrobs.setTimelimit((long)(spevent.getLeasetime()*0.9));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// Exception during update client call on superpeer
								// Superpeer could have vanished or network partition
								
								System.out.println("Exception with superpeer, resetting");
								
								spregser = null;
							}
						};
						
						spser.updateClientData(event).addResultListener(lis);
						System.out.println("Send client delta update to superpeer: "+((IService)spregser).getServiceIdentifier().getProviderId());
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("No superpeer found to send client data to");
						// Not a problem because on first occurrence sends full data (removeds are lost)
//						System.out.println("no superpeer found");
					}
				}, component));
			}
		};
	}
	
	/**
	 *  Get the superpeer service.
	 */
	protected IFuture<ISuperpeerRegistrySynchronizationService> getSuperpeerService(boolean force)
	{
		final Future<ISuperpeerRegistrySynchronizationService> ret = new Future<ISuperpeerRegistrySynchronizationService>();
		
		if(force)
			spregser = null;
		
		if(spregser!=null)
		{
			ret.setResult(spregser);
		}
		else
		{
			// If superpeerservice==null force a new search
			getRegistry().getSuperpeer(spregser==null).addResultListener(
				new ComponentResultListener<IComponentIdentifier>(new ExceptionDelegationResultListener<IComponentIdentifier, ISuperpeerRegistrySynchronizationService>(ret)
			{
				public void customResultAvailable(IComponentIdentifier spcid)
				{
					SServiceProvider.getService(component, spcid, ISuperpeerRegistrySynchronizationService.class).addResultListener(
						new DelegationResultListener<ISuperpeerRegistrySynchronizationService>(ret)
					{
						public void customResultAvailable(final ISuperpeerRegistrySynchronizationService spser)
						{
							spregser = spser;
							ret.setResult(spregser);
						}
					});
				}
			}, component));
		}
		
		return ret;
	}
		
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected IServiceRegistry getRegistry()
	{
		return ServiceRegistry.getRegistry(component.getComponentIdentifier());
	}
}
