package jadex.tools.web.chat;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.tools.web.jcc.IJCCPluginService;

/**
 *  Interface for the starter plugin service.
 *  
 *  Note: cid needs to be always last parameter. It is used to remote 
 *  control another platform using a webjcc plugin on the gateway.
 */
@Service(system=true)
@Security(roles=Security.UNRESTRICTED)
public interface IJCCChatService extends IJCCPluginService //,IChatGuiService
{
	/**
	 *  Get the nickname of a user.
	 *  @param cid The platform.
	 *  @return The nickname.
	 */
	public IFuture<String> getNickName(IComponentIdentifier cid);
	
	/**
	 *  Get the image of a user.
	 *  @param cid The owner.
	 *  @return The image.
	 */
	public IFuture<byte[]> getImage(IComponentIdentifier cid);
	
	/**
	 *  Get the status of a user.
	 *  @param cid The owner.
	 *  @return The status.
	 */
	public IFuture<String> getStatus(IComponentIdentifier cid);
	
	
	/**
	 *  Set the user name.
	 */
	public IFuture<Void> setNickName(String nick, IComponentIdentifier cid);
	
	/**
	 *  Set the avatar image.
	 */
	public IFuture<Void> setImage(byte[] image, IComponentIdentifier cid);
	
	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	// Not necessary due to SFuture.getNoTimeoutFuture
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<ChatEvent> subscribeToEvents(IComponentIdentifier cid);
	
	//-------- chatting --------
	
	/**
	 *  Search for available chat services.
	 *  @return The currently available remote services.
	 * /
	public IIntermediateFuture<IChatService> findUsers();*/
	
	/**
	 *  Get available chat users.
	 *  @return The currently available remote services.
	 */
	public IFuture<Collection<IChatService>> getUsers(IComponentIdentifier cid);
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @param receivers The receivers the message should be sent to.
	 *  @param self Flag if message should also be sent to service itself.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(String text, IComponentIdentifier[] receivers, boolean self, IComponentIdentifier cid);
	
	/**
	 *  Post a status change.
	 *  @param status The new status or null for no change.
	 *  @param image The new avatar image or null for no change.
	 *  @param receivers The receivers.
	 */
	public IIntermediateFuture<IChatService> status(String status, byte[] image, IComponentIdentifier[] receivers, IComponentIdentifier cid);

	//-------- file handling --------
	
	/**
	 *  Get a snapshot of the currently managed file transfers.
	 * /
	public IIntermediateFuture<TransferInfo> getFileTransfers();
	
	/**
	 *  Send a local file to the target component.
	 *  @param filename	The file name.
	 *  @param cid	The id of a remote chat component.
	 * /
	public IFuture<Void> sendFile(String filename, IComponentIdentifier cid);

	/**
	 *  Send a file to the target component via bytes.
	 *  @param filepath	The file path, local to the chat component.
	 *  @param cid	The id of a remote chat component.
	 * /
	public IFuture<Void> sendFile(final String fname, final byte[] data, final IComponentIdentifier cid);
	
	/**
	 *  Accept a waiting file transfer.
	 *  @param id	The transfer id. 
	 *  @param filename	The location of the file (possibly changed by user). 
	 * /
	public IFuture<Void> acceptFile(String id, String filename);
	
	/**
	 *  Reject a waiting file transfer.
	 *  @param id The transfer id. 
	 * /
	public IFuture<Void> rejectFile(String id);
	
	/**
	 *  Cancel an ongoing file transfer.
	 *  @param id The transfer id. 
	 * /
	public IFuture<Void> cancelTransfer(String id);
	*/
}
