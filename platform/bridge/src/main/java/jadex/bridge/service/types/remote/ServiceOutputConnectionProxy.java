package jadex.bridge.service.types.remote;

import java.io.InputStream;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * 
 */
public class ServiceOutputConnectionProxy implements IOutputConnection
{
	/** The original connection. */
	protected ServiceInputConnection con;
	
	/** The connection id. */
	protected int conid;
	
	/** The initiator id. */
	protected IComponentIdentifier initiator;
	
	/** The participant id. */
	protected IComponentIdentifier participant;

	/** The non-functional properties. */
	protected Map<String, Object> nonfunc;

	
	/**
	 *  Create a new connection proxy
	 */
	public ServiceOutputConnectionProxy()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new connection proxy
	 */
	public ServiceOutputConnectionProxy(ServiceInputConnection con)
	{
		this.con = con;
	}
	
	/**
	 * 
	 */
	public void setInputConnection(IInputConnection icon)
	{
		con.setInputConnection(icon);
		initiator	= icon.getInitiator();
		participant	= icon.getParticipant();
		nonfunc = icon.getNonFunctionalProperties();
	}
	
	/**
	 *  Get the connectionid.
	 *  @return The connectionid.
	 */
	public int getConnectionId()
	{
		return conid;
	}

	/**
	 *  Set the connectionid.
	 *  @param connectionid The connectionid to set.
	 */
	public void setConnectionId(int conid)
	{
		this.conid = conid;
	}
	
	/**
	 *  Get the initiator id.
	 *  @return The initiator id.
	 */
	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	/**
	 *  Set the initiator.
	 *  @param initiator The initiator to set.
	 */
	public void setInitiator(IComponentIdentifier initiator)
	{
		this.initiator = initiator;
	}

	/**
	 *  Get the participant id.
	 *  @return The participant id.
	 */
	public IComponentIdentifier getParticipant()
	{
		return participant;
	}

	/**
	 *  Set the participant.
	 *  @param participant The participant to set.
	 */
	public void setParticipant(IComponentIdentifier participant)
	{
		this.participant = participant;
	}
	
	/**
	 *  Get the non-functional properties of the connection.
	 *  @return The properties.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
		return nonfunc;
	}
	
	/**
	 *  Set the non-functional properties of the connection.
	 *  @param nonfunc The properties.
	 */
	public void setNonFunctionalProperties(Map<String, Object> nonfunc)
	{
		this.nonfunc = nonfunc;
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data)
	{
		return new Future<Void>(new UnsupportedOperationException());
	}
	
	/**
	 *  Flush the data.
	 */
	public void flush()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Integer> waitForReady()
	{
		return new Future<Integer>(new UnsupportedOperationException());
	}
	
	/**
	 *  Close the connection.
	 */
	// todo: make IFuture<Void> ?
	public void close()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Do write all data from the input stream.  
	 */
	public ISubscriptionIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component)
	{
		throw new UnsupportedOperationException();
	}
}
