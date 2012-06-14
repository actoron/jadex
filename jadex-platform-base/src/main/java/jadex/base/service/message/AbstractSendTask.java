package jadex.base.service.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.MessageFailureException;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Abstract base class for sending a message with the message service.
 */
public abstract class AbstractSendTask implements ISendTask
{
	//-------- attributes --------
	
	/** The encoded message envelope. */
	protected byte[] data;

	/** The message prolog. */
	protected byte[] prolog;

	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The codecs. */
	protected ICodec[] codecs;
	
	/** The managed receivers. */
	protected IComponentIdentifier[] receivers;

	/** The transports to be tried. */
	protected List<ITransport> transports;
	
	/** The non-functional properties. */
	protected Map<String, Object> nonfunc;
	

	/** The future for the sending result. */
	protected Future<Void>	future;
	
	/** Is some transport interested in the task? */
	protected int	interest;
	
	/** True, if the token is acquired. */
	protected boolean	acquired;
	
	/** The list of waiting transports. */
	protected List<IResultCommand<IFuture<Void>, Void>>	waiting;

	//-------- constructors --------
	
	/**
	 *  Create a new task.
	 */
	public AbstractSendTask(IComponentIdentifier[] receivers, 
		ITransport[] transports, ICodec[] codecs, Map<String, Object> nonfunc)
	{
		codecs = codecs==null? new ICodec[0]: codecs;

		for(int i=0; i<receivers.length; i++)
		{
			if(receivers[i].getAddresses()==null)
				throw new IllegalArgumentException("Addresses must not null");
		}
		
		codecids = new byte[codecs.length];
		for(int i=0; i<codecids.length; i++)
			codecids[i] = codecs[i].getCodecId();
		
		this.receivers = receivers.clone();
		this.transports = new ArrayList<ITransport>(Arrays.asList(transports));
		this.codecs	= codecs.clone();
		this.nonfunc = nonfunc;
		this.future	= new Future<Void>();
	}
	
	/**
	 *  Shallow copy a task.
	 */
	public AbstractSendTask(AbstractSendTask task)
	{
		this.data	= task.data;
		this.prolog	= task.prolog;
		this.codecids	= task.codecids;
		this.codecs	= task.codecs;
		this.receivers	= task.receivers;
		this.transports	= task.transports;
		
		this.future	= new Future<Void>();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the messagetype.
	 *  @return the messagetype.
	 */
	public abstract MessageType getMessageType();
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public abstract Object getMessage();
	
	/**
	 *  Get the receivers.
	 *  @return the receivers.
	 */
	public IComponentIdentifier[] getReceivers()
	{
		return receivers;
	}
	
	/**
	 *  Get the transports.
	 *  @return the transports.
	 */
	public List<ITransport> getTransports()
	{
		return transports;
	}
	
	/**
	 *  Get the future.
	 */
	public Future<Void>	getFuture()
	{
		return future;
	}
	
	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	/**
	 *  Get the non-functional requirements.
	 *  @return The non-functional properties.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
//		System.out.println("nonfunc: "+nonfunc);
		return nonfunc;
	}

	/**
	 *  Use transports to send the message.
	 */
	public void doSendMessage()
	{
		// Fetch all addresses
		Set<String>	addresses	= new LinkedHashSet<String>();
		for(int i=0; i<receivers.length; i++)
		{
			String[]	raddrs	= receivers[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				addresses.add(raddrs[j]);
			}			
		}
		// Determine applicable transport/address pairs.
		List<Tuple2<ITransport, String>>	sendpairs	= new ArrayList<Tuple2<ITransport, String>>();
		for(int i=0; i<getTransports().size(); i++)
		{
			ITransport transport = (ITransport)getTransports().get(i);
			for(String address: addresses)
			{
				if(transport.isApplicable(address) && transport.isNonFunctionalSatisfied(getNonFunctionalProperties()))
				{
					interest++;
					sendpairs.add(new Tuple2<ITransport, String>(transport, address));
				}
			}
		}
		
		if(sendpairs.isEmpty())
		{
			getFuture().setException(new MessageFailureException(getMessage(), getMessageType(), receivers, 
				"No transports available for sending message: "+ SUtil.arrayToString(receivers)+", "+SUtil.arrayToString(receivers[0].getAddresses())+", "+SUtil.arrayToString(getTransports())));								
		}
		else
		{
			for(Tuple2<ITransport, String> sendpair: sendpairs)
			{
				sendpair.getFirstEntity().sendMessage(sendpair.getSecondEntity(), this);
			}
		}
	}
	
	//--------- methods used by transports ---------
	
	/**
	 *  Called by the transport when is is ready to send the message,
	 *  i.e. when a connection is established.
	 *  @param send	The code to be executed to send the message.
	 */
	public void ready(IResultCommand<IFuture<Void>, Void> send)
	{
		boolean	dosend;
		synchronized(this)
		{
			dosend	= !acquired && !future.isDone();
			acquired	= true;
			if(!dosend && !future.isDone())
			{
				if(waiting==null)
				{
					waiting	= new LinkedList<IResultCommand<IFuture<Void>, Void>>();
				}
				waiting.add(send);
			}
		}
		if(dosend)
		{
			try
			{
				send.execute(null).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						done(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
						done(exception);
					}
				});
			}
			catch(Exception e)
			{
				done(e);
			}
		}
	}
	
	/**
	 *  The message sending is done. 
	 *  @param e	The exception (if any). Null denotes successful sending.
	 */
	protected void done(Exception e)
	{
		if(e!=null)
		{
			IResultCommand<IFuture<Void>, Void>	next	= null;
			boolean	nointerest;
			synchronized(this)
			{
				interest--;
				nointerest	= interest==0;
				acquired	= false;
				if(waiting!=null && !waiting.isEmpty())
				{
					next	= waiting.remove(0);
				}
			}
			if(next!=null)
			{
				ready(next);
			}
			else if(nointerest)
			{
				future.setException(e);
			}
		}
		else
		{
			future.setResult(null);
		}
	}
	
	/**
	 *  Encode the object with the codecs.
	 */
	protected byte[] encode(Object obj)
	{
		Object enc_msg = obj;
		for(int i=0; i<codecs.length; i++)
		{
			enc_msg	= codecs[i].encode(enc_msg, getClass().getClassLoader());
		}
		return (byte[])enc_msg;
	}
}
