package jadex.bridge.service.types.remote;

import java.io.OutputStream;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * 
 */
public class ServiceInputConnectionProxy implements IInputConnection
{
	/** The original connection (not transferred). */
	protected ServiceOutputConnection con;
	
	/** The connection id. */
	protected int conid;
	
	/** The initiator id. */
	protected IComponentIdentifier	initiator;
	
	/** The participant id. */
	protected IComponentIdentifier	participant;
	
	/** The non-functional properties. */
	protected Map<String, Object> nonfunc;
	
	/**
	 * 
	 */
	public ServiceInputConnectionProxy()
	{
		// Bean constructor.
	}
	
	/**
	 * 
	 */
	public ServiceInputConnectionProxy(ServiceOutputConnection con)
	{
		this.con = con;
//		this.initiator = IComponentIdentifier.LOCAL.get();
	}
	
	/**
	 * 
	 */
	public void setOutputConnection(IOutputConnection ocon)
	{
		con.setOutputConnection(ocon);
		initiator	= ocon.getInitiator();
		participant	= ocon.getParticipant();
		nonfunc = ocon.getNonFunctionalProperties();
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
//	public void setParticipant(ITransportComponentIdentifier participant)
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
	 * 
	 */
	public int read(byte[] buffer)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public int read()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the number of available bytes.
	 *  @return The number of available bytes. 
	 */
	public int available()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public void close()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<byte[]> aread()
	{
		throw new UnsupportedOperationException();
	}
	
	
//	public IFuture<Byte> areadNext()
//	{
//		throw new UnsupportedOperationException();
//	}
		
	/**
	 *  Read all data from output stream to the connection.
	 *  The result is an intermediate future that reports back the size that was read.
	 *  It can also be used to terminate reading.
	 *  @param is The input stream.
	 *  @param component The component.
	 */
	public ISubscriptionIntermediateFuture<Long> writeToOutputStream(final OutputStream os, final IExternalAccess component)
	{
		throw new UnsupportedOperationException();
	}
}