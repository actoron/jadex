package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class OutputConnectionHandler extends AbstractConnectionHandler
{
	/** The data sent (not acknowledged). */
	protected Map<Integer, StreamSendTask> sent;

	/** The data to send. */
	protected List<Tuple2<StreamSendTask, Future<Void>>> tosend;

	/** The current sequence number. */
	protected int seqnumber;
	
	/** The last acknowledged packet number. */
	protected int lastack;
	
	/** The max number of packets that can be sent without an ack is received. */
	protected int maxsend;

	/**
	 * 
	 */
	public OutputConnectionHandler(MessageService ms)
	{
		super(ms);
		this.seqnumber = 0;
		this.lastack = -1;
		this.maxsend = 30;
		this.tosend = new ArrayList<Tuple2<StreamSendTask, Future<Void>>>();
		this.sent = new HashMap<Integer, StreamSendTask>();
	}
	
	/**
	 *  Called from connection.
	 */
	public IFuture<Void> send(byte[] dat)
	{
		IFuture<Void> ret = new Future<Void>();

		StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, dat, seqnumber);
		
		boolean wassent = false;
		int allowed = maxsend-sent.size();
		
		System.out.println("called send: "+sent.size());
		
		for(int i=0; i<allowed; i++)
		{
			if(tosend.size()>0)
			{
				Tuple2<StreamSendTask, Future<Void>> tup = tosend.remove(0);
				sendTask(tup.getFirstEntity()).addResultListener(new DelegationResultListener<Void>(tup.getSecondEntity()));
				int seqno = tup.getFirstEntity().getSequenceNumber();
				System.out.println("send: "+seqno);
				sent.put(seqno, tup.getFirstEntity());
			}
			else
			{
				System.out.println("send: "+seqnumber);
				ret = sendTask(task);
				sent.put(new Integer(seqnumber), task);
				wassent = true;
				break;
			}
		}
	
		if(!wassent)
			tosend.add(new Tuple2<StreamSendTask, Future<Void>>(task, (Future<Void>)ret));
		
		return ret;
	}
	
	/**
	 *  Called from message service.
	 */
	public IFuture<Void> send(int seqnumber, byte[] data)
	{
		StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, data, new Integer(seqnumber++));
		return sendTask(task);
	}
	
	/**
	 *  Called from message service.
	 */
	public IFuture<Void> resend(int seqnumber)
	{
		StreamSendTask task = sent.get(new Integer(seqnumber));
		if(task==null)
			throw new RuntimeException("Resend not possible, data not found.");
		return sendTask(task);
	}
	
	/**
	 *  Called from message service.
	 */
	public void ack(int seqnumber)
	{
		System.out.println("ack: "+seqnumber);
		for(int i=seqnumber; i>lastack; i--)
		{
			StreamSendTask task = sent.remove(new Integer(i));
			if(task==null)
				throw new RuntimeException("Ack not possible, data not found.");
		}
		this.lastack = seqnumber;
	}

	/**
	 *  Set the connection closed.
	 */
	public void setClosed()
	{
		con.setClosed();
	}
	
	/**
	 * 
	 */
	public OutputConnection getOutputConnection()
	{
		return (OutputConnection)getConnection();
	}
	
}
