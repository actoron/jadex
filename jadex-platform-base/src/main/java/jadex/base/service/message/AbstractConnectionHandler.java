package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 *  Abstract base class for connection handlers.
 *  
 *  Is called from the message service and the connection.
 *  
 *  Ensures that all calls from threads other than the message service component
 *  are scheduled on this thread to avoid multithreading issues in this class.
 */
public class AbstractConnectionHandler implements IAbstractConnectionHandler
{
	//-------- attributes --------
	
	/** The message service. */
	protected MessageService ms;
	
	/** The connection. */
	protected AbstractConnection con;
	
	/** The latest alive time. */
	protected long alivetime;
			
	/** The lease time. */
	protected long leasetime;
	
	/** The unacknowledged messages. */
	protected Map<Object, SendInfo> unacked; 
	
	/** The maximum number of resends. */
	protected int maxresends;

	/** The max delay before an acknowledgement is received. */
	protected long acktimeout;

	//-------- constructors --------
	
	/**
	 *  Create a new connection handler.
	 */
	public AbstractConnectionHandler(MessageService ms)
	{
		this(ms, 3, 10000, 15000);
	}
	
	/**
	 *  Create a new connection handler.
	 */
	public AbstractConnectionHandler(MessageService ms, int maxresends, long acktimeout, long leasetime)
	{
		this.ms = ms;
		this.maxresends = maxresends;
		this.acktimeout = acktimeout;
		this.leasetime = leasetime;

		this.alivetime = System.currentTimeMillis();
		this.unacked = new HashMap<Object, SendInfo>();
	}
	
	/**
	 *  Set the connection (needed as connection and handler need each other).
	 *  The connections uses this method to set itself as connection in their constructor.
	 */
	// Cannot be decoupled, otherwise connection may still be null
	public void setConnection(final AbstractConnection con)
	{
//		scheduleStep(new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
				AbstractConnectionHandler.this.con = con;
//				return IFuture.DONE;
//			}
//		});
	}
	
	//-------- methods called from message service --------
	
	// All calls from the message service may occur on different threads
	// Hence they are all routed to the same component thread
		
	/**
	 *  Received the init message.
	 */
	public void initReceived()
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				con.setInited();
				sendTask(createTask(StreamSendTask.ACKINIT, null, null));
				return IFuture.DONE;
			}
		});
	}
	

	/**
	 *  Called when an ack was received.
	 *  The id is used to identify the original message task.
	 *  This will cancel the timer (resending) task
	 *  and call the future of sendAcknowledgedMessage().
	 */
	public void ackReceived(final Object id, final Object content)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				SendInfo si = unacked.remove(id);
				if(si!=null)
				{
					si.getTimer().cancel();
					si.getResult().setResult(content);
				}
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Close the connection.
	 *  Notifies the other side that the connection has been closed.
	 */
	public void close()
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				con.close();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Set the alive time of the other connection side.
	 *  @param alivetime The alive time.
	 */
	public void setAliveTime(final long alivetime)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("new lease: "+alivetime);
				AbstractConnectionHandler.this.alivetime = alivetime;
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Test if the connection is alive.
	 *  @return True, if is alive.
	 */
	// Is already called from correct message service thread
	public boolean isConnectionAlive()
	{
		boolean isalive = System.currentTimeMillis()<alivetime+getLeasetime()*2.5;
//		System.out.println("alive: "+isalive+" "+alivetime+" "+System.currentTimeMillis());
		return isalive;
	}
	
	/**
	 *  Get the closed.
	 *  @return The closed.
	 */
	// Is already called from correct message service thread
	public boolean isClosed()
	{
		return getConnection().isClosed();
	}
	
//	/**
//	 *  Get the closed.
//	 *  @return The closed.
//	 */
//	public boolean isClosing()
//	{
//		return getConnection().isClosing();
//	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	// Can be called savely from any thread, id is immutable
	public int getConnectionId()
	{
		return getConnection().getConnectionId();
	}
	
	//-------- methods called from connection --------
	
	/**
	 *  Called from connection.
	 *  Initiates closing procedure (is different for initiator and participant).
	 */
	// Is overridded from input and output connection subclasses
	public IFuture<Void> doClose()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Send init message.
	 */
	public IFuture<Void> sendInit()
	{
		final Future<Void> ret = new Future<Void>();
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				AbstractSendTask task = createTask(StreamSendTask.INIT, new IComponentIdentifier[]{
					getConnection().getInitiator(), getConnection().getParticipant()}, true, null);
				sendAcknowledgedMessage(task, StreamSendTask.INIT).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(null);
					}
				});
				return IFuture.DONE;
			}
		});
		return ret;
	}
	
	/**
	 *  Send alive message.
	 */
	public IFuture<Void> sendAlive()
	{
		final Future<Void> ret = new Future<Void>();
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				sendTask(createTask(StreamSendTask.ALIVE, null, null))
					.addResultListener(new DelegationResultListener<Void>(ret));
				return IFuture.DONE;
			}
		});
		return ret;
	}
	
	/**
	 * 
	 */
	public void notifyInited()
	{
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get the connection.
	 *  @return The connection.
	 */
	protected AbstractConnection getConnection()
	{
		return con;
	}
	
	/**
	 *  Get the codecs. 
	 *  @return The codecs.
	 */
	protected ICodec[] getCodecs()
	{
		return ms.getMessageCodecs(ms.getCodecFactory().getDefaultCodecIds());
	}
	
	/**
	 *  Get the codec ids.
	 *  @return The codec ids.
	 */
	protected byte[] getCodecIds()
	{
		return ms.getCodecFactory().getDefaultCodecIds();
	}
	
	/**
	 *  Get the transports.
	 *  @return The transports.
	 */
	protected ITransport[] getTransports()
	{
		return ms.getTransports();
	}
	
	/**
	 *  Get the message type for a given type, e.g. init, close (defined in StreamSendTask).
	 *  @param type The message type.
	 *  @return The message type for message sending.
	 */
	protected byte getMessageType(String type)
	{
		// Connection type is determined by initiator and is constant per connection
		boolean contype = getConnection().isInitiatorSide()? getConnection().isInputConnection(): !getConnection().isInputConnection();
		return StreamSendTask.getMessageType(type, contype, getConnection().isInitiatorSide());
	}
	
	/**
	 *  Create a new task.
	 *  @param type The message type.
	 *  @param content The content.
	 *  @param seqnumber The sequence number.
	 *  @return The task for sending the message.
	 */
	protected AbstractSendTask createTask(String type, Object content, Integer seqnumber)
	{
		return createTask(type, content, false, seqnumber);
	}
	
	/**
	 *  Create a new task.
	 *  @param type The message type.
	 *  @param content The content.
	 *  @param usecodecs Flag if codecs should be used to encode the content.
	 *  @param seqnumber The sequence number.
	 *  @return The task for sending the message.
	 */
	protected AbstractSendTask createTask(String type, Object content, boolean usecodecs, Integer seqnumber)
	{
		return new StreamSendTask(getMessageType(type), content==null? StreamSendTask.EMPTY_BYTE_ARRAY: content,
			getConnectionId(), getConnection().isInitiatorSide()? new IComponentIdentifier[]{getConnection().getParticipant()}: new IComponentIdentifier[]{getConnection().getInitiator()}, 
			getTransports(), usecodecs? getCodecIds(): null, usecodecs? getCodecs(): null, seqnumber);
	}
	
	/**
	 *  Send a task. Automatically closes the stream if
	 *  the other side could not be reached.
	 *  @param task The task.
	 */
	protected IFuture<Void> sendTask(final AbstractSendTask task)
	{
//		System.out.println("sendTask: "+SUtil.arrayToString(task.getProlog())+" "+task.getData().length);
		
		IComponentIdentifier[] recs = task.getReceivers();
		if(recs.length!=1)
			throw new RuntimeException("Must have exactly one receiver.");
		SendManager sm = ms.getSendManager(recs[0]);
		
		IFuture<Void> ret = sm.addMessage(task);
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
////				System.out.println("sent: "+SUtil.arrayToString(task.getProlog()));
//				// nop if could be sent
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				// close connection in case of send error.
//				getConnection().setClosed();
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Send a message and wait for an ack.
	 *  Automatically resends messages () if not is ack is received within acktimeout.
	 *  @param task The send task.
	 *  @param id The id of the call (e.g. sequence number).
	 *  @return Exception if sending and resending failed.
	 */
	protected IFuture<Object> sendAcknowledgedMessage(AbstractSendTask task, Object id)
	{
		SendInfo si = unacked.get(id);
		
		Future<Object> ret;

		sendTask(task); // .addResultListener(new DelegationResultListener<Void>(fut));
		
		if(si==null)
		{
			ret = new Future<Object>();
			si = new SendInfo(task, id, 1, createAckTimer(id), ret);
			unacked.put(id, si);
		}
		else
		{
			ret = si.getResult();
			si.setTryCnt(si.getTryCnt()+1);
			si.setTimer(createAckTimer(id));
		}
		
//		System.out.println("sent msg: "+si.getId()+" "+si.getTryCnt()+" "+SUtil.arrayToString(task.getReceivers()));
		
		return ret;
	}
	
	/**
	 *  Get the leasetime.
	 *  @return the leasetime.
	 */
	protected long getLeasetime()
	{
		return leasetime;
	}
	
	/**
	 *  Schedule a step on the message service component.
	 */
	protected <E> IFuture<E> scheduleStep(IComponentStep<E> step)
	{
		return ms.getComponent().scheduleStep(step);
	}
	
	/**
	 *  Triggers resends of packets if no ack has been received in acktimeout.
	 *  @param id The message id.
	 *  @return The timer.
	 */
	protected TimerTask	createAckTimer(final Object id)
	{
		// Test if packets have been sent till last timer was inited
		return ms.waitForRealDelay(acktimeout, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				SendInfo si = unacked.get(id);
				if(si!=null)
				{
					if(si.getTryCnt()>=maxresends)
					{
						System.out.println("Message could not be sent.");
						si.getResult().setException(new RuntimeException("Message could not be sent."));
						unacked.remove(id);
					}
					else
					{
						sendAcknowledgedMessage(si.getTask(), id);
						createAckTimer(id);
					}
				}
				return IFuture.DONE;
			}
		});
//		ITimer ret = ms.getClockService().createTimer(, new ITimedObject()
//		{
//			public void timeEventOccurred(long currenttime)
//			{
//				try
//				{
//					scheduleStep(new IComponentStep<Void>()
//					{
//						public IFuture<Void> execute(IInternalAccess ia)
//						{
//							SendInfo si = unacked.get(id);
//							if(si!=null)
//							{
//								if(si.getTryCnt()>=maxresends)
//								{
//									System.out.println("Message could not be sent.");
//									si.getResult().setException(new RuntimeException("Message could not be sent."));
//									unacked.remove(id);
//								}
//								else
//								{
//									sendAcknowledgedMessage(si.getTask(), id);
//									createAckTimer(id);
//								}
//							}
//							return IFuture.DONE;
//						}
//					});
//				}
//				catch(ComponentTerminatedException e)
//				{
//					// nop
//				}
//			}
//		});
		
//		return ret;
	}
	
	/**
	 *  Struct class that holds send information.
	 */
	public static class SendInfo
	{
		//-------- attributes --------
		
		/** The send task. */
		protected AbstractSendTask task;
		
		/** The id. */
		protected Object id;
		
		/** The number of timer this task has been executed. */
		protected int trycnt;
		
		/** The timer for triggering resends. */
		protected TimerTask timer;
		
		/** The result future of the call. */
		protected Future<Object> result;

		//-------- constructors --------

		/**
		 *  Create a new send info.
		 */
		public SendInfo(AbstractSendTask task, Object id, int trycnt,
				TimerTask timer, Future<Object> result)
		{
			this.task = task;
			this.id = id;
			this.trycnt = trycnt;
			this.timer = timer;
			this.result = result;
		}

		//-------- methods --------

		/**
		 *  Get the task.
		 *  @return the task.
		 */
		public AbstractSendTask getTask()
		{
			return task;
		}

		/**
		 *  Set the task.
		 *  @param task The task to set.
		 */
		public void setTask(AbstractSendTask task)
		{
			this.task = task;
		}

		/**
		 *  Get the id.
		 *  @return the id.
		 */
		public Object getId()
		{
			return id;
		}

		/**
		 *  Set the id.
		 *  @param id The id to set.
		 */
		public void setId(Object id)
		{
			this.id = id;
		}
		
		/**
		 *  Get the trycnt.
		 *  @return the trycnt.
		 */
		public int getTryCnt()
		{
			return trycnt;
		}

		/**
		 *  Set the trycnt.
		 *  @param trycnt The trycnt to set.
		 */
		public void setTryCnt(int trycnt)
		{
			this.trycnt = trycnt;
		}

		/**
		 *  Get the timer.
		 *  @return the timer.
		 */
		public TimerTask getTimer()
		{
			return timer;
		}

		/**
		 *  Set the timer.
		 *  @param timer The timer to set.
		 */
		public void setTimer(TimerTask timer)
		{
			this.timer = timer;
		}

		/**
		 *  Get the result.
		 *  @return the result.
		 */
		public Future<Object> getResult()
		{
			return result;
		}

		/**
		 *  Set the result.
		 *  @param result The result to set.
		 */
		public void setResult(Future<Object> result)
		{
			this.result = result;
		}
		
	}
	
}
