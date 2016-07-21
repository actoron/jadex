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
	
	/**
	 *  Set the avatar image.
	 */
	public IFuture<Void>	setImage(byte[] image);
	
	/**
	 *  Get the avatar image.
	 */
	public IFuture<byte[]>	getImage();
	
	// download directory
	
	// notification sounds (only gui settings!?)

	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	// Not necessary due to SFuture.getNoTimeoutFuture
//	@Timeout(Timeout.NONE)
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
	 *  @param receivers The receivers the message should be sent to.
	 *  @param self Flag if message should also be sent to service itself.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(String text, IComponentIdentifier[] receivers, boolean self);
	
	/**
	 *  Post a status change.
	 *  @param status The new status or null for no change.
	 *  @param image The new avatar image or null for no change.
	 */
	public IIntermediateFuture<IChatService> status(String status, byte[] image, IComponentIdentifier[] receivers);

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
	 *  Send a file to the target component via bytes.
	 *  @param filepath	The file path, local to the chat component.
	 *  @param cid	The id of a remote chat component.
	 */
	public IFuture<Void> sendFile(final String fname, final byte[] data, final IComponentIdentifier cid);
	
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
