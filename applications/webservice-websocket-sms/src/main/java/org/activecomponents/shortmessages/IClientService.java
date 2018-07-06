package org.activecomponents.shortmessages;

import java.util.Collection;

import jadex.commons.future.IFuture;

/**
 *  The service interface for the client components,
 *  i.e. the browsers.
 */
public interface IClientService
{
	/**
	 *  Called when a message was received.
	 *  @param text The text.
	 */
	public IFuture<Void> receiveMessage(ShortMessage message);
	
	/**
	 *  Called when followers changed.
	 *  @param followers The current followers.
	 */
	public IFuture<Void> followersChanged(Collection<User> followers);
	
//	/**
//	 *  Called when followed by set changed.
//	 *  @param followed The currently followed people.
//	 */
//	public IFuture<Void> followedsChanged(Collection<User> followeds);
	
	/**
	 *  The online state changed.
	 */
	public IFuture<Void> onlineStateChanged(User user);
	
	/**
	 *  Called when a user was newly registered.
	 *  @param user The new user.
	 */
	public IFuture<Void> userAdded(User user);
}
