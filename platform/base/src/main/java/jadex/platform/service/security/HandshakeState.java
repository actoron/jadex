package jadex.platform.service.security;

import java.util.HashSet;
import java.util.Set;

import jadex.commons.future.Future;
import jadex.platform.service.security.handshake.BasicSecurityMessage;

/**
 *  Class maintaining the state of a handshake in progress.
 */
public class HandshakeState
{
	/** Future used to wait for the handshake to finish. */
	protected Future<ICryptoSuite> resultfut;
	
	/** Conversation ID. */
	protected String conversationid;
	
	/** The crypto suite once initialized */
	protected ICryptoSuite cryptosuite;
	
	/** Time when the handshake expires. */
	protected long expirationtime;
	
	/** Arrived handshake messages to filter duplicates. */
	protected Set<String> arrivedmessages = new HashSet<>();
	
	/**
	 *  Sets the expiration time of the handshake.
	 *  
	 *  @param expirationtime The expiration time of the handshake.
	 */
	public void setExpirationTime(long expirationtime)
	{
		this.expirationtime = expirationtime;
	}
	
	/**
	 *  Gets the expiration time of the handshake.
	 *  
	 *  @return The expiration time of the handshake.
	 */
	public long getExpirationTime()
	{
		return expirationtime;
	}
	
	/**
	 *  Returns the result future.
	 *  
	 *  @return The result future.
	 */
	public Future<ICryptoSuite> getResultFuture()
	{
		return resultfut;
	}
	
	/**
	 *  Sets the result future.
	 *  
	 *  @param resultfut The result future.
	 */
	public void setResultFuture(Future<ICryptoSuite> resultfut)
	{
		this.resultfut = resultfut;
	}

	/**
	 *  Gets the conversation ID.
	 * 
	 *  @return The conversation ID.
	 */
	public String getConversationId()
	{
		return conversationid;
	}

	/**
	 *  Sets the conversation ID.
	 * 
	 *  @param conversationid The conversation ID.
	 */
	public void setConversationId(String conversationid)
	{
		this.conversationid = conversationid;
	}

	/**
	 *  Gets the crypto suite.
	 * 
	 *  @return The crypto suite.
	 */
	public ICryptoSuite getCryptoSuite()
	{
		return cryptosuite;
	}

	/**
	 *  Sets the crypto suite.
	 * 
	 *  @param cryptosuite The crypto suite to set.
	 */
	public void setCryptoSuite(ICryptoSuite cryptosuite)
	{
		this.cryptosuite = cryptosuite;
	}
	
	/**
	 *  Filters for duplicate handshake messages.
	 *  @param msg The message
	 *  @return True, if message is duplicate. 
	 */
	public boolean isDuplicate(BasicSecurityMessage msg)
	{
		if (arrivedmessages != null && arrivedmessages.contains(msg.getMessageId()))
			return true;
		arrivedmessages.add(msg.getMessageId());
		return false;
	}
}
