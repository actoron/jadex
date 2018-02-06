package jadex.bridge.service.types.chat;

import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;


/**
 *  Global chat service. Called to send messages,
 *  send files and user status.
 */
@Security(roles=Security.UNRESTRICTED)
@Service(system=true)
public interface IChatService
{
	//-------- constants --------
	
	/** The default user state. */
	public static final String	STATE_IDLE	= "idle";
	
	/** The user typing a message. */
	public static final String	STATE_TYPING	= "typing";
	
	/** The user is currently away from the chat. */
	public static final String	STATE_AWAY	= "away";
	
	/** The state for a disconnected user. */
	public static final String	STATE_DEAD	= "dead";
	
	//-------- methods --------
	
	/**
	 *  Get the user name.
	 */
	public IFuture<String>	getNickName();
	
	/**
	 *  Get the user image.
	 */
	public IFuture<byte[]>	getImage();
	
	/**
	 *  Get the current status.
	 */
	public IFuture<String>	getStatus();
	
	/**
	 *  Post a message
	 *  @param nick The sender's nick name.
	 *  @param text The text message.
	 */
//	@Timeout(local=12345, remote=54321) // for testing.
	public IFuture<Void> message(String nick, String text, boolean privatemessage);
	
	/**
	 *  Post a status or nick name, or image change.
	 *  @param nick The (possibly changed) nick name.
	 *  @param status The new status or null for no change.
	 *  @param image The new image or null for no change.
	 */
	public IFuture<Void> status(String nick, String status, byte[] image);
	
	/**
	 *  Send a file.
	 *  
	 *  @param nick The sender's nick name.
	 *  @param filename The filename.
	 *  @param size The size of the file.
	 *  @param id An optional id to identify the transfer (e.g. for resume after error).
	 *  @param con The connection.
	 *  
	 *  @return The returned future publishes updates about the total number of bytes received.
	 *    Exception messages of the returned future correspond to file transfer states (aborted vs. error vs. rejected).
	 */
	public ITerminableIntermediateFuture<Long> sendFile(String nick, String filename, long size, String id, IInputConnection con);
		
	/**
	 *  Send a file. Alternative method signature.
	 *  
	 *  @param nick The sender's nick name.
	 *  @param filename The filename.
	 *  @param size The size of the file.
	 *  @param id An optional id to identify the transfer (e.g. for resume after error).
	 *  
	 *  @return When the upload is accepted, the output connection for sending the file is returned.
	 */
	public ITerminableFuture<IOutputConnection> startUpload(String nick, String filename, long size, String id);
}
