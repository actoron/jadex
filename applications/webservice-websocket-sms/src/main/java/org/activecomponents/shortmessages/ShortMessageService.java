package org.activecomponents.shortmessages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Service that sends messages to followers.
 *  - Follower is online: directly call a client message
 *  - Follower is offline: add to his postbox
 */
@Service
public class ShortMessageService implements IShortMessageService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;

	/** The post boxes of the users. */
	protected Map<User, Collection<ShortMessage>> postboxes = new HashMap<User, Collection<ShortMessage>>();
	
	/**
	 *  Init of service
	 */
	@ServiceStart
	public void init()
	{
		IUserService us = component.getFeature(IProvidedServicesFeature.class).getProvidedService(IUserService.class);
		User lars = us.getUserByEmail("lars@example.com").get();
		User hans = us.getUserByEmail("hans@example.com").get();
		User dirk = us.getUserByEmail("dirk@example.com").get();
		addMessageToPostbox(lars, new ShortMessage("I had such a nice soup.", hans));
		addMessageToPostbox(lars, new ShortMessage("I bought a new book called '5 hours late but still in time'.", dirk));
	}
	
	/**
	 *  Send a message to the followers.
	 *  @param text The text.
	 *  @param sender The sender.
	 */
	public IFuture<Void> sendMessage(final String text, final String usertoken)
	{
		final Future<Void> ret = new Future<Void>();
		
		final IUserService us = component.getFeature(IProvidedServicesFeature.class).getProvidedService(IUserService.class);
		
		User sender = us.getUser(usertoken).get();
		
		final Set<User> followers = new HashSet<User>();
		Collection<User> fs = us.getFollowers(usertoken).get();
		
		if(fs!=null && fs.size()>0)
		{
			final ShortMessage message = new ShortMessage(text, sender);
			for(User follower: fs)
			{
				addMessageToPostbox(follower, message);
			}
			
			followers.addAll(fs);
		
			ShortMessageAgent.notifyClients(component, followers, new IResultCommand<IFuture<Void>, IClientService>()
			{
				public IFuture<Void> execute(IClientService service)
				{
					return service.receiveMessage(message);
				}
			}).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			System.out.println("No followers online: "+usertoken);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get all messages.
	 *  @param usertoken The user token.
	 *  @return the messages.
	 */
	public IFuture<Collection<ShortMessage>> getMessages(String usertoken)
	{
		IUserService us = component.getFeature(IProvidedServicesFeature.class).getProvidedService(IUserService.class);
		User user = us.getUser(usertoken).get();
		Collection<ShortMessage> box = postboxes.get(user);
		return new Future<Collection<ShortMessage>>(box!=null? box: Collections.EMPTY_LIST);
	}
	
	/**
	 *  Add a message to a postbox of a user.
	 *  @param user The user.
	 *  @param message The message.
	 */
	protected void addMessageToPostbox(User user, ShortMessage message)
	{
		IUserService us = component.getFeature(IProvidedServicesFeature.class).getProvidedService(IUserService.class);
		
		// Exchange user objects with correct ones from the database
		User fuser = us.getUserByEmail(user.getEmail()).get();
		User fsender = us.getUserByEmail(message.getSender().getEmail()).get();
		message.setSender(fsender);
		
		Collection<ShortMessage> box = postboxes.get(user);
		if(box==null)
		{
			box = new ArrayList<ShortMessage>();
			postboxes.put(fuser, box);
		}
		box.add(message);
	}
}
