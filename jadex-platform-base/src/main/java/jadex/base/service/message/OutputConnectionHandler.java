package jadex.base.service.message;

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
	/** The data sent. */
	protected Map<Integer, StreamSendTask> sent;

	/** The data to send. */
	protected List<Tuple2<StreamSendTask, Future<Void>>> tosend;

	/** The last acknowledged packet number. */
	protected int lastack;
	
	/** The max number of packets that can be sent without an ack is received. */
	protected int maxsend;
	
	/**
	 * 
	 */
	public OutputConnectionHandler()
	{
		super(null);
		this.lastack = -1;
		this.maxsend = 30;
		this.tosend = new ArrayList<Tuple2<StreamSendTask, Future<Void>>>();
		this.sent = new HashMap<Integer, StreamSendTask>();
	}
	
	/**
	 *  Called from connection.
	 */
	public IFuture<Void> send(StreamSendTask task)
	{
		IFuture<Void> ret = new Future<Void>();
		
		Integer sq = task.getSequenceNumber();
		if(sq==null)
			throw new RuntimeException("Data send task has no sequence number.");
		
		boolean wassent = false;
		int allowed = maxsend-sent.size();
		
		System.out.println("called send: "+sent.size());
		
		for(int i=0; i<allowed; i++)
		{
			if(tosend.size()>0)
			{
				Tuple2<StreamSendTask, Future<Void>> tup = tosend.remove(0);
				getConnection().sendTask(tup.getFirstEntity()).addResultListener(new DelegationResultListener<Void>(tup.getSecondEntity()));
				int seqno = tup.getFirstEntity().getSequenceNumber();
				System.out.println("send: "+seqno);
				sent.put(seqno, tup.getFirstEntity());
			}
			else
			{
				System.out.println("send: "+task.getSequenceNumber());
				ret = getConnection().sendTask(task);
				sent.put(sq, task);
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
	public IFuture<Void> resend(int seqnumber)
	{
		StreamSendTask task = sent.get(new Integer(seqnumber));
		if(task==null)
			throw new RuntimeException("Resend not possible, data not found.");
		return getConnection().sendTask(task);
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
	
//	/**
//	 *  Get the connection.
//	 *  @return the connection.
//	 */
//	public AbstractConnection getConnection()
//	{
//		return con;
//	}
	
}
