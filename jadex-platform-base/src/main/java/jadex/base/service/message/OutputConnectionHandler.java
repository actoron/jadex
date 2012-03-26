package jadex.base.service.message;

import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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

	
	/** The max delay before an acknowledgement is received. */
	protected long acktimeout;
	
	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The current timer. */
	protected ITimer timer;

	
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
		
		this.ackcnt = 10;
		this.acktimeout = 15000;
	}
	
	/**
	 *  Called from connection.
	 */
	public IFuture<Void> send(byte[] dat)
	{
		IFuture<Void> ret = new Future<Void>();

		StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, dat, seqnumber++);
		
		boolean wassent = false;
		int allowed = maxsend-sent.size();
		
		System.out.println("called send: "+sent.size());
		
		for(int i=0; i<allowed; i++)
		{
			if(tosend.size()>0)
			{
				Tuple2<StreamSendTask, Future<Void>> tup = tosend.remove(0);
				sendTask(tup.getFirstEntity()).addResultListener(new DelegationResultListener<Void>(tup.getSecondEntity()));
				final Integer seqno = new Integer(tup.getFirstEntity().getSequenceNumber());
				System.out.println("send: "+seqno);
				sent.put(seqno, tup.getFirstEntity());
				initTimer(seqno);
			}
			else
			{
				System.out.println("send: "+seqnumber);
				ret = sendTask(task);
				Integer seqno = new Integer(seqnumber);
				sent.put(seqno, task);
				wassent = true;
				initTimer(seqno);
				break;
			}
		}
	
		if(!wassent)
			tosend.add(new Tuple2<StreamSendTask, Future<Void>>(task, (Future<Void>)ret));
		
		return ret;
	}
	
//	/**
//	 *  Called from message service.
//	 */
//	public IFuture<Void> send(int seqnumber, byte[] data)
//	{
//		StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, data, new Integer(seqnumber));
//		return sendTask(task);
//	}
	
	/**
	 * 
	 */
	// Synchronized with timer on this
	public synchronized void initTimer(final Integer seqno)
	{
		// todo: selective acknowledgement
		if(timer==null)
		{
			timer = ms.getClockService().createTimer(acktimeout, new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
					// Send all packets of the segment again.
					for(int i=0; i<ackcnt; i++)
					{
						resend(i+seqno);
						synchronized(OutputConnectionHandler.this)
						{
							timer = null;
						}
					}
				}
			});
		}
	}
	
	/**
	 *  Called from message service.
	 */
	public IFuture<Void> resend(int seqnumber)
	{
		IFuture<Void> ret = IFuture.DONE;
		
		StreamSendTask task = sent.get(new Integer(seqnumber));
		if(task!=null)
		{
			System.out.println("resend: "+seqnumber);
			ret = sendTask(task);
		}
		
		return ret;
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
