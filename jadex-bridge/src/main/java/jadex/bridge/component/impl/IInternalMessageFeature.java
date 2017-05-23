package jadex.bridge.component.impl;

import java.util.Map;

import jadex.bridge.service.types.security.IMsgSecurityInfos;

/**
 *  A component feature for message-based communication.
 */
public interface IInternalMessageFeature
{
	/**
	 *  Inform the component that a message has arrived.
	 *  
	 *  @param header The message header.
	 *  @param bodydata The encrypted message that arrived.
	 */
	public void messageArrived(Map<String, Object> header, byte[] bodydata);
	
	/**
	 *  Inform the component that a message has arrived.
	 *  
	 *  @param secinfos The security meta infos.
	 *  @param header The message header.
	 *  @param body The message that arrived.
	 */
	public void messageArrived(IMsgSecurityInfos secinfos, Map<String, Object> header, Object body);
}
