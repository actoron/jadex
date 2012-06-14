package jadex.bridge;

import java.util.Map;


/**
 * 
 */
public interface IConnection
{
	/**
	 *  Get the connection id.
	 */
	public int getConnectionId();
	
	/**
	 *  Get the initiator.
	 */
	public IComponentIdentifier getInitiator();
	
	/**
	 *  Get the participant.
	 */
	public IComponentIdentifier getParticipant();
	
	/**
	 *  Close the connection.
	 */
	// todo: make IFuture<Void> ?
	public void close();
	
	/**
	 *  Get the non-functional properties of the connection.
	 */
	public Map<String, Object> getNonFunctionalProperties();
}

