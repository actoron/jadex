package jadex.tools.web.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.base.SRemoteGui;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.ICommand;
import jadex.commons.MethodInfo;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Starter web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="chatweb", type=IJCCChatService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCChatPluginAgent extends JCCPluginAgent implements IJCCChatService
{
	/*@OnService(name="chatguiservice")
	public void setService(IChatGuiService service)
	{
	}*/
	
	/**
	 *  Get the chat gui service.
	 */
	protected IChatGuiService getChatService()
	{
		return agent.getLocalService(new ServiceQuery<IChatGuiService>(IChatGuiService.class).setExcludeOwner(true).setScope(ServiceScope.PLATFORM));
	}
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("Chat");
	}
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(100);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/chat/chat.js";
	}
	
	/**
	 *  Get the plugin icon.
	 *  @return The plugin icon.
	 */
	public IFuture<byte[]> getPluginIcon()
	{
		return loadResource("jadex/tools/web/chat/chat.png");
	}
	
	/**
	 *  Set the user name.
	 */
	public IFuture<Void> setNickName(String nick)
	{
		return  getChatService().setNickName(nick);
	}
	
	/**
	 *  Get the user name.
	 */
	public IFuture<String> getNickName()
	{
		return getChatService().getNickName();
	}
	
	/**
	 *  Set the avatar image.
	 */
	public IFuture<Void> setImage(byte[] image)
	{
		return getChatService().setImage(image);
	}
	
	/**
	 *  Get the avatar image.
	 */
	public IFuture<byte[]> getImage()
	{
		return getChatService().getImage();
	}
	
	// download directory
	
	// notification sounds (only gui settings!?)

	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	// Not necessary due to SFuture.getNoTimeoutFuture
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<ChatEvent> subscribeToEvents()
	{
		return getChatService().subscribeToEvents();
	}
	
	//-------- chatting --------
		
	/**
	 *  Get available chat users.
	 *  @return The currently available remote services.
	 */
	public IFuture<Collection<IChatService>> getUsers()
	{
		return getChatService().getUsers();
	}
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @param receivers The receivers the message should be sent to.
	 *  @param self Flag if message should also be sent to service itself.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(String text, IComponentIdentifier[] receivers, boolean self)
	{
		return getChatService().message(text, receivers, self);
	}
	
	/**
	 *  Post a status change.
	 *  @param status The new status or null for no change.
	 *  @param image The new avatar image or null for no change.
	 *  @param receivers The receivers.
	 */
	public IIntermediateFuture<IChatService> status(String status, byte[] image, IComponentIdentifier[] receivers)
	{
		return getChatService().status(status, image, receivers);
	}

	//-------- file handling --------
	
	/**
	 *  Get a snapshot of the currently managed file transfers.
	 */
	public IIntermediateFuture<TransferInfo> getFileTransfers()
	{
		return getChatService().getFileTransfers();
	}
	
	/**
	 *  Send a local file to the target component.
	 *  @param filename	The file name.
	 *  @param cid	The id of a remote chat component.
	 */
	public IFuture<Void> sendFile(String filename, IComponentIdentifier cid)
	{
		return getChatService().sendFile(filename, cid);
	}

	/**
	 *  Send a file to the target component via bytes.
	 *  @param filepath	The file path, local to the chat component.
	 *  @param cid	The id of a remote chat component.
	 */
	public IFuture<Void> sendFile(final String fname, final byte[] data, final IComponentIdentifier cid)
	{
		return getChatService().sendFile(fname, data, cid);
	}
	
	/**
	 *  Accept a waiting file transfer.
	 *  @param id	The transfer id. 
	 *  @param filename	The location of the file (possibly changed by user). 
	 */
	public IFuture<Void> acceptFile(String id, String filename)
	{
		return getChatService().acceptFile(id, filename);
	}
	
	/**
	 *  Reject a waiting file transfer.
	 *  @param id The transfer id. 
	 */
	public IFuture<Void> rejectFile(String id)
	{
		return getChatService().rejectFile(id);
	}
	
	/**
	 *  Cancel an ongoing file transfer.
	 *  @param id The transfer id. 
	 */
	public IFuture<Void> cancelTransfer(String id)
	{
		return getChatService().cancelTransfer(id);
	}
	
	// specific chat service methods for other chat services
	
	/**
	 *  Get the nickname of a user.
	 *  @param cid The owner.
	 *  @return The nickname.
	 */
	public IFuture<String> getNickName(IComponentIdentifier cid)
	{
		Future<String> ret = new Future<String>();
		agent.searchService(new ServiceQuery<IChatService>(IChatService.class).setOwner(cid)).then(ser ->
		{
			ser.getNickName().delegate(ret);
		}).catchEx(ret);
		return ret;
	}
	
	/**
	 *  Get the image of a user.
	 *  @param cid The owner.
	 *  @return The image.
	 */
	public IFuture<byte[]> getImage(IComponentIdentifier cid)
	{
		Future<byte[]> ret = new Future<byte[]>();
		agent.searchService(new ServiceQuery<IChatService>(IChatService.class).setOwner(cid)).then(ser ->
		{
			ser.getImage().delegate(ret);
		}).catchEx(ret);
		
		return ret;
	}
	
	/**
	 *  Get the status of a user.
	 *  @param cid The owner.
	 *  @return The status.
	 */
	public IFuture<String> getStatus(IComponentIdentifier cid)
	{
		Future<String> ret = new Future<String>();
		agent.searchService(new ServiceQuery<IChatService>(IChatService.class).setOwner(cid)).then(ser ->
		{
			ser.getStatus().delegate(ret);
		}).catchEx(ret);
		return ret;
	}
	
}
