package jadex.bridge;


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
}
