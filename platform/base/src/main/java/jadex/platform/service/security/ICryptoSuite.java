package jadex.platform.service.security;

import jadex.bridge.JadexVersion;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.platform.service.security.handshake.BasicSecurityMessage;

public interface ICryptoSuite
{
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public byte[] encryptAndSign(byte[] content);
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public byte[] decryptAndAuth(byte[] content);
	
	/**
	 *  Decrypt and authenticates a locally encrypted message.
	 *  
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public byte[] decryptAndAuthLocal(byte[] content);
	
	/**
	 *  Gets the security infos related to the authentication state.
	 *  
	 *  @return The security infos for decrypted messages.
	 */
	public ISecurityInfo getSecurityInfos();
	
	/**
	 *  Returns if the suite is expiring and should be replaced.
	 *  
	 *  @return True, if the suite is expiring and should be replaced.
	 */
	public boolean isExpiring();
	
	/**
	 *  Handles handshake messages.
	 *  
	 *  @param agent The security agent object.
	 *  @param incomingmessage A message received from the other side of the handshake,
	 *  					   set to null for initial message.
	 *  @return True, if handshake continues, false when finished.
	 *  @throws SecurityException if handshake failed.
	 */
	public boolean handleHandshake(SecurityAgent agent, BasicSecurityMessage incomingmessage);
	
	/**
	 *  Gets the ID used to identify the handshake of the suite.
	 *  
	 *  @return Handshake ID.
	 */
	public String getHandshakeId();
	
	/**
	 *  Sets the ID used to identify the handshake of the suite.
	 *  
	 *  @param id Handshake ID.
	 */
	public void setHandshakeId(String id);
	
	/**
	 *  Gets the version of the remote Jadex platform.
	 *  @return The Jadex version.
	 */
	public JadexVersion getRemoteVersion();
	
	/**
	 *  Sets the version of the remote Jadex platform.
	 *  @param jadexversion The Jadex version.
	 */
	public void setRemoteVersion(JadexVersion jadexversion);
	
	/**
	 *  Sets if the suite represents the protocol initializer.
	 * @param initializer True, if initializer.
	 */
	public void setInitializer(boolean initializer);
}
