package jadex.bridge.fipa;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
		
		Object	hrec	= header.getProperty(IMsgHeader.RECEIVER);
		
		// At least one receiver should be set.
		if((frec==null || frec.isEmpty())
			&& (hrec==null || hrec instanceof IComponentIdentifier[] && ((IComponentIdentifier[])hrec).length==0))
		{
			throw new IllegalArgumentException("No receiver specified: "+msg);
		}

		// Copy header receiver(s) to FIPA message object
		if(frec==null && hrec instanceof IComponentIdentifier)
		{
			msg.addReceiver((IComponentIdentifier)hrec);
		}
		else if(frec==null && hrec instanceof IComponentIdentifier[])
		{
			msg.setReceivers(new LinkedHashSet<IComponentIdentifier>(Arrays.asList((IComponentIdentifier[])hrec)));
		}
		
		// Copy FIPA receiver(s) to header
		else if(frec!=null && hrec==null)
		{
			header.addProperty(IMsgHeader.RECEIVER, frec);
		}
			
		// Check consistency of FIPA vs. header receivers.
		else
		{
			Set<IComponentIdentifier>	tmp	= hrec instanceof IComponentIdentifier
				? Collections.singleton((IComponentIdentifier)hrec)
				: new HashSet<IComponentIdentifier>(Arrays.asList((IComponentIdentifier[])hrec));
				
			if(!frec.equals(tmp))
			{
				throw new IllegalArgumentException("Inconsistent msg/header receivers: "+frec+" vs. "+tmp);				
			}
			// else equal -> NOP
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
		return //SUtil.safeCollection(message.getReceivers()).contains(reply.getSender())	// Not required, e.g. protocol receiver plans create template w/o sender/receiver 
			/*&&*/ SUtil.equals(message.getConversationId(), reply.getConversationId())
			&& SUtil.equals(message.getReplyWith(), reply.getInReplyTo());
	}
}
