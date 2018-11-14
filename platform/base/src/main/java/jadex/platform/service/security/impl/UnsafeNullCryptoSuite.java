package jadex.platform.service.security.impl;

import java.util.logging.Logger;

import org.bouncycastle.util.Pack;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.platform.service.security.ICryptoSuite;
import jadex.platform.service.security.SecurityInfo;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.security.handshake.BasicSecurityMessage;

/**
 *  Always-trusting, no encryption, no authentication suite.
 * 
 *  UNSAFE: Only use for testing.
 *
 */
public class UnsafeNullCryptoSuite implements ICryptoSuite
{
	/** Authentication Suite ID. */
	protected static final int AUTH_SUITE_ID = 523382039;
	
	/** The security infos. */
	protected SecurityInfo secinf;
	
	/** The handshake ID. */
	protected String handshakeid;
	
	public UnsafeNullCryptoSuite()
	{
		Logger.getLogger("security").warning("Unsafe crypto suite enabled: " + getClass().getName());
	}
	
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public byte[] encryptAndSign(byte[] content)
	{
		byte[] ret = new byte[content.length + 4];
		Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
		System.arraycopy(content, 0, ret, 4, content.length);
		return ret;
	}
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public byte[] decryptAndAuth(byte[] content)
	{
		byte[] ret = null;
		if (AUTH_SUITE_ID == Pack.littleEndianToInt(content, 0))
		{
			ret = new byte[content.length - 4];
			System.arraycopy(content, 4, ret, 0, ret.length);
		}
		return ret;
	}
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public byte[] decryptAndAuthLocal(byte[] content)
	{
		byte[] ret = null;
		if (AUTH_SUITE_ID == Pack.littleEndianToInt(content, 0))
		{
			ret = new byte[content.length - 4];
			System.arraycopy(content, 4, ret, 0, ret.length);
		}
		return ret;
	}
	
	/**
	 *  Gets the security infos related to the authentication state.
	 *  
	 *  @return The security infos for decrypted messages.
	 */
	public ISecurityInfo getSecurityInfos()
	{
		return secinf;
	}
	
	/**
	 *  Returns if the suite is expiring and should be replaced.
	 *  
	 *  @return True, if the suite is expiring and should be replaced.
	 */
	public boolean isExpiring()
	{
		return false;
	}
	
	/**
	 *  Handles handshake messages.
	 *  
	 *  @param agent The security agent object.
	 *  @param incomingmessage A message received from the other side of the handshake,
	 *  					   set to null for initial message.
	 *  @return True, if handshake continues, false when finished.
	 *  @throws SecurityException if handshake failed.
	 */
	public boolean handleHandshake(SecurityAgent agent, BasicSecurityMessage incomingmessage)
	{
		secinf = new SecurityInfo();
//		secinf.setPlatformAuthenticated(true);
		secinf.setTrustedPlatform(true);
		secinf.setAdminPlatform(true);
		secinf.setNetworks(agent.getInternalNetworks().keySet());
		secinf.setSharedNetworks(secinf.getNetworks());
		secinf.setAllowDefaultAuthorization(true);
		
		if (!(incomingmessage instanceof NullMessage))
			agent.sendSecurityHandshakeMessage(incomingmessage.getSender(), new NullMessage(agent.getComponentIdentifier(), incomingmessage.getConversationId()));
		
		return false;
	}
	
	/**
	 *  Gets the ID used to identify the handshake of the suite.
	 *  
	 *  @return Handshake ID.
	 */
	public String getHandshakeId()
	{
		return handshakeid;
	}
	
	/**
	 *  Sets the ID used to identify the handshake of the suite.
	 *  
	 *  @param id Handshake ID.
	 */
	public void setHandshakeId(String id)
	{
		handshakeid = id;
	}
	
	/**
	 *  Null message handshake class.
	 *  @author jander
	 *
	 */
	protected static class NullMessage extends BasicSecurityMessage
	{
		/**
		 *  Create message.
		 */
		public NullMessage()
		{
		}
		
		/**
		 *  Create message.
		 */
		public NullMessage(IComponentIdentifier sender, String conversationid)
		{
			super(sender, conversationid);
		}
	}
}
