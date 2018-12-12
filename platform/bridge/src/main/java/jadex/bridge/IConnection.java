package jadex.bridge;

import java.util.Map;


/**
 *  Base interface for all Jadex connections (streams).
 */
public interface IConnection
{
	/**
	 *  Get the connection id.
	 *  @return The connection id.
	 */
	public int getConnectionId();
	
	/**
	 *  Get the initiator.
	 *  @return The initiator.
	 */
	public IComponentIdentifier getInitiator();
	
	/**
	 *  Get the participant.
	 *  @return The participant.
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

