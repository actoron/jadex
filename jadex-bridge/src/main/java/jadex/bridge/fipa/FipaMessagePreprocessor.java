package jadex.bridge.fipa;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.IMessagePreprocessor;
import jadex.commons.SUtil;

/**
 *  Preprocessor fpr FIPA messages.
 */
public class FipaMessagePreprocessor	implements IMessagePreprocessor<FipaMessage>
{
	/**
	 *  Preprocess a message before sending.
	 *  @param header	The message header, may be changed by preprocessor.
	 *  @param msg	The user object, may be changed by preprocessor.
	 */
	public void	preprocessMessage(IMsgHeader header, FipaMessage msg)
	{
		// Set/check consistent sender.
		IComponentIdentifier	fsen	= msg.getSender();
		IComponentIdentifier	hsen	= (IComponentIdentifier)header.getProperty(IMsgHeader.SENDER);
		assert	hsen!=null : "Message feature should always provider sender!";
		if(fsen==null)
		{
			msg.setSender(hsen);
		}
		else if(!fsen.equals(hsen))
		{
			throw new IllegalArgumentException("Inconsistent msg/header sender: "+fsen+" vs. "+hsen);
		}
		
		// Set/check consistent receiver.
		Set<IComponentIdentifier>	frec	= msg.getReceivers();
		IComponentIdentifier	hrec	= (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
		if(frec==null)
		{
			msg.addReceiver(hrec);
		}
		else if(hrec==null && frec.size()==1)
		{
			// TODO: multiple receivers
			header.addProperty(IMsgHeader.RECEIVER, msg.getReceivers().iterator().next());
		}
		else// if(!frec.equals(hrec))
		{
			throw new IllegalArgumentException("Inconsistent/unsupported msg/header receivers: "+frec+" vs. "+hrec);
		}
		
		// Set/check consistent conv id.
		String	fconv	= msg.getConversationId();
		String	hconv	= (String)header.getProperty(IMsgHeader.CONVERSATION_ID);
		if(fconv==null)
		{
			msg.setConversationId(hconv);
		}
		else if(hconv==null)
		{
			header.addProperty(IMsgHeader.CONVERSATION_ID, msg.getConversationId());
		}
		else if(!fconv.equals(hconv))
		{
			throw new IllegalArgumentException("Inconsistent msg/header conversation IDs: "+fconv+" vs. "+hconv);
		}
	}
	
	/**
	 *  Optionally check for reply matches.
	 *  Currently only used in BDIX.
	 *  @param	message	The initial message object.
	 *  @param	reply	The replied message object.
	 *  @return	true when the reply matches the initial message.
	 */
	public boolean	isReply(FipaMessage message, FipaMessage reply)
	{
		return SUtil.safeCollection(message.getReceivers()).contains(reply.getSender())
			&& SUtil.equals(message.getConversationId(), reply.getConversationId())
			&& SUtil.equals(message.getReplyWith(), reply.getInReplyTo());
	}
}
