package org.activecomponents.shortmessages;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IResultCommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that implements the core services for 
 *  - sending messages to followers
 *  - user management
 */
@ProvidedServices({
	@ProvidedService(type=IUserService.class, implementation=@Implementation(UserService.class)),
	@ProvidedService(type=IShortMessageService.class, implementation=@Implementation(ShortMessageService.class))
})
@Agent
public class ShortMessageAgent
{
	/**
	 *  Notify online clients of some events. 
	 *  @param component
	 *  @return
	 */
	public static IFuture<Void> notifyClient(final IInternalAccess component, User rec, final IResultCommand<IFuture<Void>, IClientService> cmd)
	{
		if(rec==null)
			return IFuture.DONE;
		
		Set<User> recs = new HashSet<User>();
		recs.add(rec);
		return notifyClients(component, recs, cmd);
	}
	
	/**
	 *  Notify online clients of some events. 
	 *  @param component
	 *  @return
	 */
	public static IFuture<Void> notifyClients(final IInternalAccess component, Collection<User> recs, final IResultCommand<IFuture<Void>, IClientService> cmd)
	{
		if(recs.size()==0)
			return IFuture.DONE;
		
		final Future<Void> ret = new Future<Void>();
		
		final Collection<User> receivers = new HashSet<User>(recs);
		
		final IUserService us = SServiceProvider.getDeclaredService(component, IUserService.class).get();
		
		SServiceProvider.getServices(component, IClientService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IIntermediateResultListener<IClientService>()
		{
			public void intermediateResultAvailable(IClientService service)
			{
				try
				{
					Collection<String> tags = (Collection<String>)SNFPropertyProvider.getNFPropertyValue(component.getExternalAccess(), ((IService)service).getServiceIdentifier(), TagProperty.NAME).get();
					if(tags!=null)
					{
//						final User user = us.getUser(tags.iterator().next()).get();
						final User user = us.getUserByEmail(tags.iterator().next()).get();
						if(receivers.remove(user))
						{
							// Should method wait for the result?!
							cmd.execute(service).addResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
								}
								
								public void exceptionOccurred(Exception exception)
								{
									System.out.println("Exception in notification of: "+user);
									exception.printStackTrace();
								}
							});
						}
						
						if(receivers.size()==0)
						{
//							System.out.println("Notified all receivers");
							ret.setResultIfUndone(null);
						}
					}
				}
				catch(Exception e)
				{
					IServiceIdentifier sid = ((IService)service).getServiceIdentifier();
					System.out.println("service has no tags: "+sid+" "+sid.getServiceType().getTypeName());
					e.printStackTrace();
					// service has no tags
				}
			}
			
			public void finished()
			{
	//			System.out.println("fin search");
				ret.setResultIfUndone(null);
			}
			
			public void resultAvailable(Collection<IClientService> result)
			{
				for(IClientService cs: result)
				{
					intermediateResultAvailable(cs);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex in search: "+exception);
				ret.setException(exception);
			}
		});
		
		return ret;
	}
}
