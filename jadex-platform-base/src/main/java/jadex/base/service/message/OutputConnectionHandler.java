package jadex.base.service.message;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.Collections;
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

//	/** The current sequence number. */
//	protected int seqnumber;
	
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

	
	/** Flag if multipackets should be used. */
	protected boolean multipackets;
	
	/** The packet size to collect (in bytes). */
	protected int mpmaxsize;
	
	/** The collected data for a packet. */
	protected List<byte[]> multipacket;
	
	/** The current multipacket size. */
	protected int mpsize;
	
	
	/** Close request flag (when a closereq message was received). */
	protected boolean closereqflag;

	/** The close (for resend). */
	protected StreamSendTask closetask;

	
	/**
	 * 
	 */
	public OutputConnectionHandler(MessageService ms)
	{
		super(ms);
		this.tosend = Collections.synchronizedList(new ArrayList<Tuple2<StreamSendTask, Future<Void>>>());
		this.sent = Collections.synchronizedMap(new HashMap<Integer, StreamSendTask>());

		this.lastack = -1;
		this.maxsend = 30;
		
		this.ackcnt = 10;
		this.acktimeout = 15000;
		
		this.multipackets = true;
		this.mpmaxsize = 20;
		this.multipacket = new ArrayList<byte[]>();
		this.mpsize = 0;
	}
	
	/**
	 *  Received a request to close the connection.
	 */
	public void closeRequestReceived()
	{
		if(isDataFinished())
			con.close();
		else
			closereqflag = true;
		
		// todo: additional timer?
	}
	
	/**
	 *  Response to close message. 
	 *  
	 *  The participant has acked the close -> close this site also.
	 */
	public void ackCloseReceived()
	{
		System.out.println("received ack close");
		// Set connection as closed.
		con.setClosed();
		closetask = null;
	}
	
	/**
	 *  Send close message on close init.
	 */
	public IFuture<Void> doClose()
	{
		if(closetask!=null)
			throw new RuntimeException("Must be only called once");

		closetask = (StreamSendTask)createTask(StreamSendTask.CLOSE, null, null);
		return sendTask(closetask);
	}
	
	
	/**
	 *  Called from connection.
	 *  
	 *  Uses: sent, tosend
	 */
	public IFuture<Void> send(byte[] dat)
	{
		IFuture<Void> ret = new Future<Void>();

		System.out.println("called send: "+sent.size());
		
		sendStored();

		if(multipackets)
		{
			ret = addMultipacket(dat);
		}
		else
		{
			StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, dat, getNextSequenceNumber());
			if(maxsend-sent.size()>0)
			{
				System.out.println("send: "+task.getSequenceNumber());
				ret = sendTask(task, null);
			}
			else
			{
				ret = new Future<Void>();
				tosend.add(new Tuple2<StreamSendTask, Future<Void>>(task, (Future<Void>)ret));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Called from connection, 
	 * 
	 *  Uses: sent, tosend
	 */
	// synchronized to avoid being called twice at the same time
	protected synchronized void sendStored()
	{
		int allowed = maxsend-sent.size();
		for(int i=0; i<allowed && tosend.size()>0; i++)
		{
			Tuple2<StreamSendTask, Future<Void>> tup = tosend.remove(0);
			System.out.println("send: "+tup.getFirstEntity().getSequenceNumber());
			sendTask(tup.getFirstEntity(), tup.getSecondEntity());
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> addMultipacket(byte[] data)
	{
		IFuture<Void> ret = new Future<Void>();
		
		int start = 0;
		int len = Math.min(mpmaxsize-mpsize, data.length);
		
		List<IFuture<Void>> futs = new ArrayList<IFuture<Void>>();
		while(len>0)
		{
			byte[] part = new byte[len];
			System.arraycopy(data, start, part, 0, len);
			futs.add(internalAddMultiPacket(part));
			start += len;
			len = Math.min(mpmaxsize-mpsize, data.length-start);
		}
		
		if(futs.size()>0)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(futs.size(), 
				new DelegationResultListener<Void>((Future<Void>)ret));
			for(int i=0; i<futs.size(); i++)
			{
				futs.get(i).addResultListener(lis);
			}
		}
		else
		{
			ret = IFuture.DONE;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> internalAddMultiPacket(byte[] data)
	{
		IFuture<Void> ret = IFuture.DONE;
		
		multipacket.add(data);
		mpsize += data.length;
		
		if(mpsize==mpmaxsize)
			ret = sendMultiPacket();
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> sendMultiPacket()
	{
		IFuture<Void> ret = IFuture.DONE;
		
		if(multipacket.size()>0)
		{
			byte[] target = new byte[mpsize];
			int start = 0;
			for(int i=0; i<multipacket.size(); i++)
			{
				byte[] tmp = multipacket.get(i);
				System.arraycopy(tmp, 0, target, start, tmp.length);
				start += tmp.length;
			}
			
			StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, target, getNextSequenceNumber());
			if(maxsend-sent.size()>0)
			{
				ret = sendTask(task, null);
			}
			else
			{
				ret = new Future<Void>();
				tosend.add(new Tuple2<StreamSendTask, Future<Void>>(task, (Future<Void>)ret));
			}
			
			multipacket.clear();
			mpsize = 0;
		}
		
		return ret;
	}
	
	/**
	 *  Flush the data.
	 */
	public void flush()
	{
		if(multipackets)
			sendMultiPacket();
	}
	
	/**
	 *  Called from message service.
	 */
	public IFuture<Void> sendTask(StreamSendTask task, Future<Void> fut)
	{
		IFuture<Void> ret = fut;
		
		if(ret==null)
		{
			ret = sendTask(task);
		}
		else
		{
			sendTask(task).addResultListener(new DelegationResultListener<Void>(fut));
		}
		
		// add task to unacknowledged sent list 
		sent.put(task.getSequenceNumber(), task);
		
		// create timer if none is active
		createTimer(task.getSequenceNumber());
		
		return ret;
	}
	
	/**
	 *  Called from timer.
	 *  
	 *  Uses: sent
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
	 *  
	 *  Uses: sent, lastack
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

		// Try to send stored messages after some others have been acknowledged
		sendStored();
		
		// Try to close if close is requested.
		if(isCloseRequested() && isDataFinished())
			close();
	}
	
	/**
	 *  Get the closereq.
	 *  @return The closereq.
	 */
	public boolean isCloseRequested()
	{
		return closereqflag;
	}

	/**
	 * 
	 */
	public boolean isDataFinished()
	{
		// All acks received.
		return sent.isEmpty();
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
	
//	/**
//	 *  Get the seqnumber.
//	 *  @return the seqnumber.
//	 */
//	public int getSequenceNumber()
//	{
//		return seqnumber;
//	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendClose()
	{
		return sendTask(createTask(StreamSendTask.CLOSE, null, null));
		
//		Future<Void> ret = new Future<Void>();
//		
//		IComponentStep<Void> closecom = new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				return sendTask(createTask(StreamSendTask.CLOSE, null, null));
//			}
//		};
//		
//		return ret;
	}
	
	/**
	 * 
	 */
	// Synchronized with timer on this
	public synchronized void createTimer(int seqno)
	{
		// Test if packets have been sent till last timer was inited
		if(timer==null)
		{
			if(getReceivedSequenceNumber()>seqno)
			{
				timer = ms.getClockService().createTimer(System.currentTimeMillis()+acktimeout, new TimedObject(seqno+ackcnt));
			}
			else
			{
				timer = null;
			}
		}
	}

	/**
	 * 
	 */
	public class TimedObject implements ITimedObject
	{
		/** The sequence number. */
		protected int seqno;
		
		/**
		 * 
		 */
		public TimedObject(int seqno)
		{
			this.seqno = seqno;
		}
		
		/**
		 * 
		 */
		public void timeEventOccurred(long currenttime)
		{
			// Send all packets of the segment again.
			for(int i=0; i<ackcnt; i++)
			{
				resend(i+seqno);
			}
			timer = null;
			createTimer(seqno+ackcnt);
		}
	}
}
