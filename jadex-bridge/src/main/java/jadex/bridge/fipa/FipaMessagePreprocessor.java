package jadex.bridge.fipa;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.IMessagePreprocessor;

/**
 *  Preprocessor fpr FIPA messages.
 */
public class FipaMessagePreprocessor	implements IMessagePreprocessor
{
	/**
	 *  Preprocess a message before sending.
	 *  @param header	The message header, may be changed by preprocessor.
	 *  @param msg	The user object, may be changed by preprocessor.
	 */
	public void	preprocessMessage(IMsgHeader header, Object msg)
	{
		FipaMessage	fmsg	= (FipaMessage)msg;
		
		// Set/check consistent sender.
		IComponentIdentifier	fsen	= fmsg.getSender();
		IComponentIdentifier	hsen	= (IComponentIdentifier)header.getProperty(IMsgHeader.SENDER);
		assert	hsen!=null : "Message feature should always provider sender!";
		if(fsen==null)
		{
			fmsg.setReceiver(hsen);
		}
		else if(!fsen.equals(hsen))
		{
			throw new IllegalArgumentException("Inconsistent msg/header sender: "+fsen+" vs. "+hsen);
		}
		
		// Set/check consistent receiver.
		IComponentIdentifier	frec	= fmsg.getReceiver();
		IComponentIdentifier	hrec	= (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
		if(frec==null)
		{
			fmsg.setReceiver(hrec);
		}
		else if(hrec==null)
		{
			header.addProperty(IMsgHeader.RECEIVER, fmsg.getReceiver());
		}
		else if(!frec.equals(hrec))
		{
			throw new IllegalArgumentException("Inconsistent msg/header receivers: "+frec+" vs. "+hrec);
		}
		
		// Set/check consistent conv id.
		String	fconv	= fmsg.getConversationId();
		String	hconv	= (String)header.getProperty(IMsgHeader.CONVERSATION_ID);
		if(fconv==null)
		{
			fmsg.setConversationId(hconv);
		}
		else if(hconv==null)
		{
			header.addProperty(IMsgHeader.CONVERSATION_ID, fmsg.getConversationId());
		}
		else if(!fconv.equals(hconv))
		{
			throw new IllegalArgumentException("Inconsistent msg/header conversation IDs: "+fconv+" vs. "+hconv);
		}		
	}
}
