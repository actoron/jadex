package jadex.platform.service.security;

import jadex.commons.future.Future;

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
	
	/** Expiration time for the handshake */
	
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
	public void setResultfut(Future<ICryptoSuite> resultfut)
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
}
