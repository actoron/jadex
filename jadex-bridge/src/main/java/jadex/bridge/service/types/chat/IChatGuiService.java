package jadex.bridge.service.types.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Timeout;
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
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<ChatEvent>	subscribeToEvents();
	
	//-------- chatting --------
	
	/**
	 *  Search for available chat services.
	 *  @return The currently available remote services.
	 */
	public IIntermediateFuture<IChatService> findUsers();
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(String text, IComponentIdentifier[] receivers);
	
	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IIntermediateFuture<IChatService> status(final String status);

	//-------- file handling --------
	
	/**
	 *  Get a snapshot of the currently managed file transfers.
	 */
	public IIntermediateFuture<TransferInfo>	getFileTransfers();
	
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
