package jadex.bridge.service.types.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 *  Service for connecting a user interface to a running chat service.
 */
public interface IChatGuiService
{
	//-------- settings and events --------
	
	/**
	 *  Set the user name.
	 */
	public IFuture<Void>	setNickName(String nick);
	
	/**
	 *  Get the user name.
	 */
	public IFuture<String>	getNickName();
	
	// download directory
	
	// notification sounds (only gui settings!?)

	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	public ISubscriptionIntermediateFuture<ChatEvent>	subscribeToEvents();
	
	//-------- chatting --------
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(String text);
	
	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IIntermediateFuture<IChatService> status(final String status);

	//-------- file handling --------
	
	/**
	 *  Send a local file to the target component.
	 *  @param filename	The file name.
	 *  @param cid	The id of a remote chat component.
	 */
	public IFuture<Void>	sendFile(String filename, IComponentIdentifier cid);

	/**
	 *  Accept a waiting file transfer.
	 *  @param id	The transfer id. 
	 *  @param filename	The location of the file (possibly changed by user). 
	 */
	public IFuture<Void>	acceptFile(String id, String filename);
	
	/**
	 *  Reject a waiting file transfer.
	 *  @param id	The transfer id. 
	 */
	public IFuture<Void>	rejectFile(String id);
	
	/**
	 *  Cancel an ongoing file transfer.
	 *  @param id	The transfer id. 
	 */
	public IFuture<Void>	cancelTransfer(String id);
}
