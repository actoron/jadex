package jadex.platform.service.message.streams;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.types.message.ICodec;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.platform.service.transport.ITransport;

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

	/** The non-functional properties. */
	protected Map<String, Object> nonfunc;

	//-------- constructors --------
	
	/**
	 *  Create a new connection handler.
	 */
	public AbstractConnectionHandler(MessageService ms, Map<String, Object> nonfunc)
	{
		// Use timeouts relative to deftimeout
		this(ms, nonfunc, 3, Starter.getScaledRemoteDefaultTimeout(ms.getComponent().getComponentIdentifier(), 1.0/3), 
			Starter.getScaledRemoteDefaultTimeout(ms.getComponent().getComponentIdentifier(), 1.0/2));
	}
	
	/**
	 *  Create a new connection handler.
	 */
	public AbstractConnectionHandler(MessageService ms, Map<String, Object> nonfunc, int maxresends, long acktimeout, long leasetime)
	{
		this.ms = ms;
		this.nonfunc = nonfunc;
		this.maxresends = maxresends;
		this.acktimeout = acktimeout;
		this.leasetime = leasetime;

		this.alivetime = System.currentTimeMillis();
		this.unacked = new HashMap<Object, SendInfo>();
		
//		createGui();
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
				sendTask(createTask(StreamSendTask.ACKINIT, null, null, nonfunc));
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
//					System.out.println("received ack: "+si.getId()+" "+si.getTryCnt());
					if(si.getTimer()!=null)
					{
						si.getTimer().cancel();
					}
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
		boolean isalive = getLeasetime()<0 || System.currentTimeMillis()<alivetime+getLeasetime()*2.5;
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
		// Hack!!! Closed before init
		return getConnection()==null || getConnection().isClosed();
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
		return scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void> ret = new Future<Void>();
				try
				{
					InitInfo ii = new InitInfo(getConnection().getInitiator(), getConnection().getParticipant(), nonfunc);
					AbstractSendTask task = createTask(StreamSendTask.INIT, ii, true, null, nonfunc);
					sendAcknowledgedMessage(task, StreamSendTask.INIT).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
					{
						public void customResultAvailable(Object result)
						{
							ret.setResult(null);
						}
					});
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
				return ret;
			}
		});
	}
	
	/**
	 *  Send alive message.
	 */
	public IFuture<Void> sendAlive()
	{
		return scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void> ret = new Future<Void>();
				try
				{
					AbstractSendTask task = createTask(StreamSendTask.ALIVE, null, null, null);
					sendTask(task);
					task.getFuture().addResultListener(new DelegationResultListener<Void>(ret));
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
				return ret;
			}
		});
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
		return ms.getBinaryCodecs(ms.getRemoteMarshalingConfig().getDefaultCodecIds());
	}
	
	/**
	 *  Get the codec ids.
	 *  @return The codec ids.
	 */
	protected byte[] getCodecIds()
	{
		return ms.getRemoteMarshalingConfig().getDefaultCodecIds();
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
	 *  Get the non-functional properties.
	 *  @return The non-functional properties.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
		return nonfunc;
	}
	
	/**
	 *  Set the non-functional properties.
	 *  @param nonfunc The non-functional properties.
	 */
	public void setNonFunctionalProperties(Map<String, Object> nonfunc)
	{
		this.nonfunc = nonfunc;
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
	protected AbstractSendTask createTask(String type, byte[] content, Integer seqnumber, Map<String, Object> nonfunc)
	{
		return createTask(type, content, false, seqnumber, nonfunc);
	}
	
	/**
	 *  Create a new task.
	 *  @param type The message type.
	 *  @param content The content.
	 *  @param usecodecs Flag if codecs should be used to encode the content.
	 *  @param seqnumber The sequence number.
	 *  @return The task for sending the message.
	 */
	protected AbstractSendTask createTask(String type, Object content, boolean useserializer, Integer seqnumber, Map<String, Object> nonfunc)
	{
		return new StreamSendTask(getMessageType(type), content==null? StreamSendTask.EMPTY_BYTE_ARRAY: content,
			getConnectionId(), getConnection().isInitiatorSide()? new ITransportComponentIdentifier[]{getConnection().getParticipant()}: new ITransportComponentIdentifier[]{getConnection().getInitiator()}, 
			getTransports(), ms.getRemoteMarshalingConfig().getPreprocessors(), useserializer?ms.getRemoteMarshalingConfig().getDefaultSerializer():null, getCodecs(), seqnumber, nonfunc); 
	}
	
	/**
	 *  Send a task.
	 *  @param task The task.
	 */
	protected void sendTask(final AbstractSendTask task)
	{
//		System.out.println("sendTask: "+SUtil.arrayToString(task.getProlog())+" "+task.getData().length);
		
		IComponentIdentifier[] recs = task.getReceivers();
		if(recs.length!=1)
			throw new RuntimeException("Must have exactly one receiver.");
		SendManager sm = ms.getSendManager(recs[0]);
		
		sm.addMessage(task);
		task.getFuture().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				if(task instanceof StreamSendTask)
//				{
//					System.out.println("ack send fini: "+((StreamSendTask)task).getSequenceNumber()+" "+System.currentTimeMillis());
//				}
//				System.out.println("sent: "+SUtil.arrayToString(task.getProlog()));
				// nop if could be sent
			}
			public void exceptionOccurred(Exception exception)
			{
				// close connection in case of send error.
//				getConnection().setClosed();
			}
		});
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
		TimerTask	ret;
		
		if(acktimeout!=Timeout.NONE)
		{
			// Test if packets have been sent till last timer was inited
			ret	= ms.waitForRealDelay(acktimeout, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					SendInfo si = unacked.get(id);
					if(si!=null)
					{
						if(si.getTryCnt()>=maxresends)
						{
	//						System.out.println("Message could not be sent.");
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
		}
		else
		{
			ret	= null;
		}
		
		return ret;
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
	
	/**
	 * 
	 */
	public void createGui()
	{
		JFrame f = new JFrame();
		f.setLayout(new BorderLayout());
		
		f.add(createPanel(), BorderLayout.CENTER);
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.pack();
		f.setVisible(true);
	}
	
	/**
	 * 
	 */
	protected JPanel createPanel()
	{
		return new ConnectionPanel();
	}
	
	/**
	 * 
	 */
	public class ConnectionPanel extends JPanel
	{
		/**
		 * 
		 */
		public ConnectionPanel()
		{
			PropertiesPanel pp = new PropertiesPanel("Connection properties");
			final JTextField tfalivetime = pp.createTextField("alivetime");
			final JTextField tfleasetime = pp.createTextField("leasetime");
			final JTextField tfunacked = pp.createTextField("unacked");
			final JTextField tfmaxresends = pp.createTextField("maxresends");
			final JTextField tfacktimeout = pp.createTextField("acktimeout");
			
			Timer t = new Timer(1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					tfalivetime.setText(""+alivetime);
					tfleasetime.setText(""+leasetime);
					tfunacked.setText(""+unacked);
					tfmaxresends.setText(""+maxresends);
					tfacktimeout.setText(""+acktimeout);
				}
			});
			t.start();
			
			setLayout(new BorderLayout());
			add(pp, BorderLayout.CENTER);
		}
	}
	
}
