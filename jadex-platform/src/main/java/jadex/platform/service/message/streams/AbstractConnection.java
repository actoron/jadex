package jadex.platform.service.message.streams;

import java.util.Map;

import jadex.bridge.IConnection;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.future.IResultListener;

/**
 *  Abstract base class for connections.
 *  
 *  Connection code is called from connection users, i.e. active components,
 *  on their on thread. Calls from the connection handler occur on another
 *  thread so that these calls must be synchronized. 
 */
public abstract class AbstractConnection implements IConnection
{
	//-------- attributes --------
	
	/** Boolean flag if connection is inited, closing, closed. */
	protected boolean inited;
	protected boolean closed;
	protected boolean closing;
	
	/** The connection id. */
	protected int id;
	
	/** The connection initiator. */
	protected ITransportComponentIdentifier initiator;

	/** The participant. */
	protected ITransportComponentIdentifier participant;

	/** The input flag. */
	protected boolean input;
	
	/** The initiator flag. */
	protected boolean ini;
	
	/** The abstract connection handler. */
	protected IAbstractConnectionHandler ch;
		
	//-------- constructors --------
	
	/**
	 *  Create a new input connection.
	 */
	public AbstractConnection(ITransportComponentIdentifier sender, 
		ITransportComponentIdentifier receiver, int id, boolean input, boolean initiator, IAbstractConnectionHandler ch)
	{
		this.initiator = sender;
		this.participant = receiver;
		this.id = id;
		this.input = input;
		this.ini = initiator;
		this.ch = ch;
		
		if(ch==null)
			throw new IllegalArgumentException("Connection handler must not null.");
		
		// Send init message if initiator side.
		ch.setConnection(this);
		
		if(isInitiatorSide())
			ch.sendInit().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				setInited();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				close();
			}
		});
	}
	
	//-------- IConnection methods --------
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getConnectionId()
	{
		return id;
	}
	
	/**
	 *  Get the initiator.
	 *  @return The initiator.
	 */
	public ITransportComponentIdentifier getInitiator()
	{
		return initiator;
	}

	/**
	 *  Get the participant.
	 *  @return The participant.
	 */
	public ITransportComponentIdentifier getParticipant()
	{
		return participant;
	}
	
	//-------- methods --------	
	
	/**
	 *  Get the inited.
	 *  @return the inited.
	 */
	public synchronized boolean isInited()
	{
		return inited;
	}

	/**
	 *  Set the inited.
	 *  @param inited The inited to set.
	 */
	public void setInited()
	{
		synchronized(this)
		{
			this.inited = true;
		}
		ch.notifyInited();
	}
	
	/**
	 *  Set the connection to closed.
	 */
	public synchronized void setClosing()
	{
		this.closing = true;
	}
	
	/**
	 *  Set the connection to closed.
	 */
	public synchronized void setClosed()
	{
		this.closed = true;
	}
	
	/**
	 *  Get the closed.
	 *  @return The closed.
	 */
	public synchronized boolean isClosing()
	{
		return closing;
	}
	
	/**
	 *  Get the closed.
	 *  @return The closed.
	 */
	public synchronized boolean isClosed()
	{
		return closed;
	}

	/**
	 *  Close the connection.
	 *  Notifies the other side that the connection has been closed.
	 */
	public void close()
	{
		synchronized(this)
		{
			if(closing || closed)
				return;			
		}

		setClosing();	

		ch.doClose();
	}
	
	/**
	 *  Test if this connection is the initiator side.
	 *  @return True if is initiator.
	 */
	public boolean isInitiatorSide()
	{
		return ini;
	}

	/**
	 *  Test if this connection is an input connection.
	 *  @return True if is initiator.
	 */
	public boolean isInputConnection()
	{
		return input;
	}
	
	/**
	 *  Get the non-functional properties of the connection.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
		return ch.getNonFunctionalProperties();
	}
	
	/**
	 *  Get the connection handler.
	 */
	public IAbstractConnectionHandler getConnectionHandler()
	{
		return ch;
	}
}
