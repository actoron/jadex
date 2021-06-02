package jadex.platform.service.security.handshake;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.JadexVersion;

/**
 *  Final message in the initial handshake.
 *
 */
public class InitialHandshakeFinalMessage extends BasicSecurityMessage
{
	/** The chosen crypto suite. */
	protected String chosencryptosuite;
	
	/** The Jadex version of the sender. */
	protected JadexVersion jadexversion;
	
	/**
	 *  Creates the message.
	 */
	public InitialHandshakeFinalMessage()
	{
		
	}
	
	/**
	 *  Creates the message.
	 */
	public InitialHandshakeFinalMessage(IComponentIdentifier sender, String conversationid, String chosencryptosuite, JadexVersion jadexversion)
	{
		super(sender, conversationid);
		this.chosencryptosuite = chosencryptosuite;
	}
	
	/**
	 *  Gets the chosen crypto suite.
	 * 
	 *  @return The chosen crypto suite.
	 */
	public String getChosenCryptoSuite()
	{
		return chosencryptosuite;
	}
	
	/**
	 *  Sets the chosen crypto suite.
	 * 
	 *  @param chosencryptosuite The chosen crypto suite.
	 */
	public void setChosenCryptoSuite(String chosencryptosuite)
	{
		this.chosencryptosuite = chosencryptosuite;
	}
	
	/**
	 *  Gets the Jadex version.
	 *
	 *  @return The Jadex version.
	 */
	public JadexVersion getJadexVersion()
	{
		return jadexversion;
	}

	/**
	 *  Sets the Jadex version.
	 *  
	 *  @param jadexversion The Jadex version to set.
	 */
	public void setJadexVersion(JadexVersion jadexversion)
	{
		this.jadexversion = jadexversion;
	}
}
