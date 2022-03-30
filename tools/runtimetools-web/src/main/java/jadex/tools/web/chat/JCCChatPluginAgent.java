package jadex.tools.web.chat;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.micro.annotation.Agent;
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
	/**
	 *  Test if the plugin is functional, i.e. if the jcc plugin
	 *  e.g. finds a domain service to work with.
	 *  @return True, if plugin can be used.
	 */
	public IFuture<Boolean> isAvailable(IComponentIdentifier cid)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		//ret.setResult(Boolean.FALSE);
		
		getChatService(cid).then(cs ->
		{
			ret.setResult(Boolean.TRUE);
		}).catchEx(ex ->
		{
			ret.setResult(Boolean.FALSE);
		});
		
		return ret;
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
	public IFuture<Void> setNickName(String nick, IComponentIdentifier cid)
	{
		return getChatGuiService(cid).thenCompose(s -> s.setNickName(nick));
	}
	
	/**
	 *  Set the avatar image.
	 */
	public IFuture<Void> setImage(byte[] image, IComponentIdentifier cid)
	{
		return getChatGuiService(cid).thenCompose(s -> s.setImage(image));
	}
	
	// download directory
	
	// notification sounds (only gui settings!?)

	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	// Not necessary due to SFuture.getNoTimeoutFuture
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<ChatEvent> subscribeToEvents(IComponentIdentifier cid)
	{
		return (ISubscriptionIntermediateFuture<ChatEvent>)getChatGuiService(cid).thenCompose(s -> s.subscribeToEvents(), SubscriptionIntermediateDelegationFuture.class);
	}
	
	//-------- chatting --------
		
	/**
	 *  Get available chat users.
	 *  @return The currently available remote services.
	 */
	public IFuture<Collection<IChatService>> getUsers(IComponentIdentifier cid)
	{
		return getChatGuiService(cid).thenCompose(s -> s.getUsers());
	}
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @param receivers The receivers the message should be sent to.
	 *  @param self Flag if message should also be sent to service itself.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> postMessage(String text, IComponentIdentifier[] receivers, boolean self, IComponentIdentifier cid)
	{
		//System.out.println("message: "+text);
		return (IIntermediateFuture<IChatService>)getChatGuiService(cid).thenCompose(s -> s.postMessage(text, receivers, self), IntermediateFuture.class);
	}
	
	/**
	 *  Post an image.
	 *  @param text The text message.
	 *  @param receivers The receivers the message should be sent to.
	 *  @param self Flag if message should also be sent to service itself.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> postImage(byte[] image, IComponentIdentifier[] receivers, boolean self, IComponentIdentifier cid)
	{
		//System.out.println("image: "+text);
		return (IIntermediateFuture<IChatService>)getChatGuiService(cid).thenCompose(s -> s.postImage(image, receivers, self), IntermediateFuture.class);
	}
	
	/**
	 *  Post a status change.
	 *  @param status The new status or null for no change.
	 *  @param image The new avatar image or null for no change.
	 *  @param receivers The receivers.
	 */
	public IIntermediateFuture<IChatService> postStatus(String status, byte[] image, IComponentIdentifier[] receivers, IComponentIdentifier cid)
	{
		return (IIntermediateFuture<IChatService>)getChatGuiService(cid).thenCompose(s -> s.postStatus(status, image, receivers), IntermediateFuture.class);
	}

	//-------- file handling --------
	
	// todo
	
	// specific chat service methods for other chat services
	
	/**
	 *  Get the nickname of a user.
	 *  @param cid The owner.
	 *  @return The nickname.
	 */
	public IFuture<String> getNickName(IComponentIdentifier cid)
	{
		return getChatService(cid).thenCompose(s -> s.getNickName());
		/*Future<String> ret = new Future<>();
		getChatService(cid).then(s -> 
		{
			s.getNickName().then(n -> {System.out.println("getNick in webjcc: "+n+" "+cid+" "+((IService)s).getServiceId().getProviderId()); ret.setResult(n);});
		});
		return ret;*/
	}
	
	/**
	 *  Get the image of a user.
	 *  @param cid The owner.
	 *  @return The image.
	 */
	public IFuture<byte[]> getImage(IComponentIdentifier cid)
	{
		return getChatService(cid).thenCompose(s -> s.getImage());
	}
	
	/**
	 *  Get the status of a user.
	 *  @param cid The owner.
	 *  @return The status.
	 */
	public IFuture<String> getStatus(IComponentIdentifier cid)
	{
		return getChatService(cid).thenCompose(s -> s.getStatus());
	}
	
	/**
	 *  Get the chat gui service of the own platform or of cid platform.
	 *  @param cid The platform id.
	 *  @return The service
	 */
	protected IFuture<IChatGuiService> getChatGuiService(IComponentIdentifier cid)
	{
		//if(cid==null)
		//	Thread.dumpStack();
		if(cid==null || cid.hasSameRoot(getAgent().getId()))
		{
			return getAgent().searchService(new ServiceQuery<IChatGuiService>(IChatGuiService.class).setScope(ServiceScope.PLATFORM));
		}
		else
		{
			// IChatGuiService is only visible locally
			return getAgent().searchService(new ServiceQuery<IChatGuiService>(IChatGuiService.class).setProvider(cid));
		}
	}
	
	/**
	 *  Get the chat service of the own platform or of cid platform.
	 *  @param cid The platform id.
	 *  @return The service
	 */
	protected IFuture<IChatService> getChatService(IComponentIdentifier cid)
	{
		//if(cid==null)
		//	Thread.dumpStack();
		if(cid==null || cid.hasSameRoot(getAgent().getId()))
		{
			return getAgent().searchService(new ServiceQuery<IChatService>(IChatService.class).setScope(ServiceScope.PLATFORM));
		}
		else
		{
			return getAgent().searchService(new ServiceQuery<IChatService>(IChatService.class).setProvider(cid));
		}
	}
}
