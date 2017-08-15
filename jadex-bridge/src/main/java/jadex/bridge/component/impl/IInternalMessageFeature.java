package jadex.bridge.component.impl;

import java.util.Map;
import java.util.UUID;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IConnection;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.streams.InputConnection;
import jadex.bridge.component.streams.OutputConnection;
import jadex.bridge.component.streams.OutputConnectionHandler;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  A component feature for message-based communication.
 */
public interface IInternalMessageFeature
{
	/**
	 *  Forwards the prepared message to the transport layer.
	 *  
	 *  @param header The message header.
	 *  @param encryptedbody The encrypted message body.
	 *  @return Null, when done, exception if failed.
	 */
	public IFuture<Void> sendToTransports(IMsgHeader header, byte[] encryptedbody);
	
	/**
	 *  Inform the component that a message has arrived.
	 *  Called from transports (i.e. remote messages).
	 *  
	 *  @param header The message header.
	 *  @param bodydata The encrypted message that arrived.
	 */
	public void messageArrived(IMsgHeader header, byte[] encryptedbody);
	
	/**
	 *  Inform the component that a message has arrived.
	 *  Called directly for intra-platform message delivery (i.e. local messages)
	 *  and indirectly for remote messages.
	 *  
	 *  @param secinfos The security meta infos.
	 *  @param header The message header.
	 *  @param body The message that arrived.
	 */
	public void messageArrived(IMsgSecurityInfos secinfos, IMsgHeader header, Object body);
	
	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con);
	
	/**
	 *  Get the participant input connection.
	 */
	public IInputConnection getParticipantInputConnection(int conid, IComponentIdentifier initiator, IComponentIdentifier participant, Map<String, Object> nonfunc);
	
	/**
	 *  Get the participant output connection.
	 */
	public IOutputConnection getParticipantOutputConnection(int conid, IComponentIdentifier initiator, IComponentIdentifier participant, Map<String, Object> nonfunc);

	/**
	 *  Create a virtual output connection.
	 */
	public OutputConnection internalCreateOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc);
	
	/**
	 *  Create a virtual input connection.
	 */
	public InputConnection internalCreateInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc);
}
