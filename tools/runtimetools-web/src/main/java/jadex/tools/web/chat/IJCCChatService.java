package jadex.tools.web.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;

/**
 *  Interface for the starter plugin service.
 *  
 *  Note: cid needs to be always last parameter. It is used to remote 
 *  control another platform using a webjcc plugin on the gateway.
 */
@Service(system=true)
@Security(roles=Security.UNRESTRICTED)
public interface IJCCChatService extends IJCCPluginService, IChatGuiService
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
}
