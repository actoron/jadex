package jadex.platform.service.security.handshake;

import jadex.bridge.IComponentIdentifier;

/**
 *  Message signaling the rejection of the handshake.
 *
 */
public class HandshakeRejectionMessage extends BasicSecurityMessage
{
	/**
	 *  Creates the message.
	 */
	public HandshakeRejectionMessage()
	{
	}
	
	/**
	 *  Creates the message.
	 */
	public HandshakeRejectionMessage(IComponentIdentifier sender, String conversationid)
	{
		super(sender, conversationid);
	}
}
