package jadex.bridge.component.streams;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.Tuple;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	//-------- constants ----------
	
	/** The message type for streams. */
	public static final byte MESSAGE_TYPE_STREAM = 99;

//		/** The minimal lease time. */
	// Todo: individual value transmitted to remote platforms and used for each connection separately.
//		public static long MIN_LEASETIME;
	
	/** Constants for message types. */
	
	/** Init a connection. */
	public static final String INIT = "INIT";
	/** Acknowledge init. */
	public static final String ACKINIT = "ACKINIT";
	/** Send data message. */
	public static final String DATA = "DATA";
	/** Acknowledge data message. */
	public static final String ACKDATA = "ACKDATA"; 
	/** Close the connection. */
	public static final String CLOSE = "CLOSE";
	/** Acknowledge the close message. */
	public static final String ACKCLOSE = "ACKCLOSE"; 
	/** Close request (from participant which cannot close itself). */
	public static final String CLOSEREQ = "CLOSEREQ";
	/** Acknowledge the close request. */ 
	public static final String ACKCLOSEREQ = "ACKCLOSEREQ"; 
	/** The alive message. */
	public static final String ALIVE = "ALIVE";
	
	/** Create virtual output connection - from initiator. */
	public static final byte INIT_OUTPUT_INITIATOR = 1; 
	/** Ack the init - from initiator. */
	public static final byte ACKINIT_OUTPUT_PARTICIPANT = 2; 
	/** Send data - from initiator. */
	public static final byte DATA_OUTPUT_INITIATOR = 3;
	/** Ack data/close - from participant .*/
	public static final byte ACKDATA_OUTPUT_PARTICIPANT = 4;
	/** Request close connection - from participant. */
	public static final byte CLOSEREQ_OUTPUT_PARTICIPANT = 5;
	/** Ack for close request - from initiator .*/
	public static final byte ACKCLOSEREQ_OUTPUT_INITIATOR = 6;
	/** Close connection - from initiator. */
	public static final byte CLOSE_OUTPUT_INITIATOR = 7;
	/** Ack data/close - from participant .*/
	public static final byte ACKCLOSE_OUTPUT_PARTICIPANT = 8;

	
	/** Create virtual input connection - from initiator. */ 
	public static final byte INIT_INPUT_INITIATOR = 11;
	/** Ack the init - from participant. */
	public static final byte ACKINIT_INPUT_PARTICIPANT = 12; 
	/** Send data - from participant. */
	public static final byte DATA_INPUT_PARTICIPANT = 13;
	/** Ack data - from participant .*/
	public static final byte ACKDATA_INPUT_INITIATOR = 14;
	/** Close request connection - from initiator. */
	public static final byte CLOSEREQ_INPUT_INITIATOR = 15;
	/** Ack for close request - from initiator .*/
	public static final byte ACKCLOSEREQ_INPUT_PARTICIPANT = 16;
	/** Close connection - from participant. */
	public static final byte CLOSE_INPUT_PARTICIPANT = 17;
	/** Ack for close - from initiator .*/
	public static final byte ACKCLOSE_INPUT_INITIATOR = 18;

	
	/** Alive message - from initiator. */ 
	public static final byte ALIVE_INITIATOR = 20;
	/** Alive message - from participant. */ 
	public static final byte ALIVE_PARTICIPANT = 21;

	/** String type, boolean input, boolean initiator. */
	public static final Map<Tuple, Byte> MESSAGETYPES;
	
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	static
	{
		MESSAGETYPES = new HashMap<Tuple, Byte>();
		
		MESSAGETYPES.put(new Tuple(INIT, false, true), Byte.valueOf(INIT_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKINIT, false, false), Byte.valueOf(ACKINIT_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(DATA, false, true), Byte.valueOf(DATA_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKDATA, false, false), Byte.valueOf(ACKDATA_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(CLOSE, false, true), Byte.valueOf(CLOSE_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKCLOSE, false, false), Byte.valueOf(ACKCLOSE_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(CLOSEREQ, false, false), Byte.valueOf(CLOSEREQ_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ACKCLOSEREQ, false, true), Byte.valueOf(ACKCLOSEREQ_OUTPUT_INITIATOR));

		MESSAGETYPES.put(new Tuple(INIT, true, true), Byte.valueOf(INIT_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKINIT, true, false), Byte.valueOf(ACKINIT_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(DATA, true, false), Byte.valueOf(DATA_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ACKDATA, true, true), Byte.valueOf(ACKDATA_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSE, true, false), Byte.valueOf(CLOSE_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ACKCLOSE, true, true), Byte.valueOf(ACKCLOSE_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSEREQ, true, true), Byte.valueOf(CLOSEREQ_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKCLOSEREQ, true, false), Byte.valueOf(ACKCLOSEREQ_INPUT_PARTICIPANT));

		MESSAGETYPES.put(new Tuple(ALIVE, true, true), Byte.valueOf(ALIVE_INITIATOR));
		MESSAGETYPES.put(new Tuple(ALIVE, false, true), Byte.valueOf(ALIVE_INITIATOR));
		MESSAGETYPES.put(new Tuple(ALIVE, true, false), Byte.valueOf(ALIVE_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ALIVE, false, false), Byte.valueOf(ALIVE_PARTICIPANT));
	}
	
	/**
	 *  Get the message type.
	 *  @param type The type.
	 *  @param input Flag if in input connection.
	 *  @param initiator Flag if is initiator side.
	 */
	public static byte getMessageType(String type, boolean input, boolean initiator)
	{
		try
		{
			return MESSAGETYPES.get(new Tuple(type, input, initiator));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	//-------- attributes --------
	
	/** The message service. */
//	protected MessageService ms;
//	protected IMessageFeature mf;
	
	protected IInternalAccess component;
	
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

	/** The timer. */
	protected java.util.Timer timer;

	//-------- constructors --------
	
	/**
	 *  Create a new connection handler.
	 */
	public AbstractConnectionHandler(IInternalAccess component, Map<String, Object> nonfunc)
	{
		// Use timeouts relative to deftimeout
		this(component, nonfunc, 3, Starter.getScaledDefaultTimeout(component.getId(), 1.0/3), 
			Starter.getScaledDefaultTimeout(component.getId(), 1.0/2));
	}
	
	/**
	 *  Create a new connection handler.
	 */
	public AbstractConnectionHandler(IInternalAccess component, Map<String, Object> nonfunc, int maxresends, long acktimeout, long leasetime)
	{
		this.component = component;
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
				sendTask(createTask(ACKINIT, null, null, nonfunc));
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
					StreamPacket task = createTask(INIT, ii, true, null, nonfunc);
					sendAcknowledgedMessage(task, INIT).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
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
					StreamPacket task = createTask(ALIVE, null, null, null);
					sendTask(task).addResultListener(new DelegationResultListener<Void>(ret));
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
	
//	/**
//	 *  Get the codecs. 
//	 *  @return The codecs.
//	 */
//	protected ICodec[] getCodecs()
//	{
//		return ms.getBinaryCodecs(ms.getRemoteMarshalingConfig().getDefaultCodecIds());
//	}
//	
//	/**
//	 *  Get the codec ids.
//	 *  @return The codec ids.
//	 */
//	protected byte[] getCodecIds()
//	{
//		return ms.getRemoteMarshalingConfig().getDefaultCodecIds();
//	}
//	
//	/**
//	 *  Get the transports.
//	 *  @return The transports.
//	 */
//	protected ITransport[] getTransports()
//	{
//		return ms.getTransports();
//	}
	
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
		return getMessageType(type, contype, getConnection().isInitiatorSide());
	}
	
	/**
	 *  Create a new task.
	 *  @param type The message type.
	 *  @param content The content.
	 *  @param seqnumber The sequence number.
	 *  @return The task for sending the message.
	 */
	protected StreamPacket createTask(String type, byte[] content, Integer seqnumber, Map<String, Object> nonfunc)
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
	protected StreamPacket createTask(String type, Object content, boolean useserializer, Integer seqnumber, Map<String, Object> nonfunc)
	{
		return new StreamPacket(getMessageType(type), getConnectionId(), content, seqnumber, getConnection().isInitiatorSide()? getConnection().getParticipant(): getConnection().getInitiator());
//		return new StreamSendTask(getMessageType(type), content==null? StreamSendTask.EMPTY_BYTE_ARRAY: content,
//			getConnectionId(), getConnection().isInitiatorSide()? new ITransportComponentIdentifier[]{getConnection().getParticipant()}: new ITransportComponentIdentifier[]{getConnection().getInitiator()}, 
//			getTransports(), ms.getRemoteMarshalingConfig().getPreprocessors(), useserializer?ms.getRemoteMarshalingConfig().getDefaultSerializer():null, getCodecs(), seqnumber, nonfunc); 
	}
	
	/**
	 *  Send a task.
	 *  @param task The task.
	 */
	protected IFuture<Void> sendTask(final StreamPacket task)
	{
//		System.out.println("sendTask: "+SUtil.arrayToString(task.getProlog())+" "+task.getData().length);
		
		IComponentIdentifier rec = task.receiver;
		task.receiver = null;
		IFuture<Void> ret = getMessageFeature().sendMessage(task, rec);
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
////				if(task instanceof StreamSendTask)
////				{
////					System.out.println("ack send fini: "+((StreamSendTask)task).getSequenceNumber()+" "+System.currentTimeMillis());
////				}
////				System.out.println("sent: "+SUtil.arrayToString(task.getProlog()));
//				// nop if could be sent
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				// close connection in case of send error.
////				getConnection().setClosed();
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
	protected IFuture<Object> sendAcknowledgedMessage(StreamPacket task, Object id)
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
		return getExecutionFeature().scheduleStep(step);
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
			ret	= waitForRealDelay(acktimeout, new IComponentStep<Void>()
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
	 *  Wait for a time delay on the (real) system clock.
	 */
	public TimerTask waitForRealDelay(long delay, final IComponentStep<?> step)
	{
		if(timer==null)
		{
			synchronized(this)
			{
				if(timer==null)
				{
					timer = new java.util.Timer(component.getId().getName()+".message.timer", true);
				}
			}
		}
		
		TimerTask	ret	= new TimerTask()
		{
			public void run()
			{
				try
				{
					getComponent().scheduleStep(step);
				}
				catch(ComponentTerminatedException cte)
				{
					// ignore and stop timer.
					timer.cancel();
				}
			}
		};
		timer.schedule(ret, delay);
		
		return ret;
	}
	
	/**
	 *  Get the component.
	 *  @return the component
	 */
	public IInternalAccess getComponent()
	{
		return component;
	}

	/**
	 *  Struct class that holds send information.
	 */
	public static class SendInfo
	{
		//-------- attributes --------
		
		/** The send task. */
		protected StreamPacket task;
		
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
		public SendInfo(StreamPacket task, Object id, int trycnt,
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
		public StreamPacket getTask()
		{
			return task;
		}

		/**
		 *  Set the task.
		 *  @param task The task to set.
		 */
		public void setTask(StreamPacket task)
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
	
//	/**
//	 * 
//	 */
//	public void createGui()
//	{
//		JFrame f = new JFrame();
//		f.setLayout(new BorderLayout());
//		
//		f.add(createPanel(), BorderLayout.CENTER);
//		f.setLocation(SGUI.calculateMiddlePosition(f));
//		f.pack();
//		f.setVisible(true);
//	}
//	
//	/**
//	 * 
//	 */
//	protected JPanel createPanel()
//	{
//		return new ConnectionPanel();
//	}
//	
//	/**
//	 * 
//	 */
//	public class ConnectionPanel extends JPanel
//	{
//		/**
//		 * 
//		 */
//		public ConnectionPanel()
//		{
//			PropertiesPanel pp = new PropertiesPanel("Connection properties");
//			final JTextField tfalivetime = pp.createTextField("alivetime");
//			final JTextField tfleasetime = pp.createTextField("leasetime");
//			final JTextField tfunacked = pp.createTextField("unacked");
//			final JTextField tfmaxresends = pp.createTextField("maxresends");
//			final JTextField tfacktimeout = pp.createTextField("acktimeout");
//			
//			Timer t = new Timer(1000, new ActionListener()
//			{
//				public void actionPerformed(ActionEvent e)
//				{
//					tfalivetime.setText(""+alivetime);
//					tfleasetime.setText(""+leasetime);
//					tfunacked.setText(""+unacked);
//					tfmaxresends.setText(""+maxresends);
//					tfacktimeout.setText(""+acktimeout);
//				}
//			});
//			t.start();
//			
//			setLayout(new BorderLayout());
//			add(pp, BorderLayout.CENTER);
//		}
//	}
	
	/**
	 *  Get the message feature.
	 */
	public IMessageFeature getMessageFeature()
	{
		return component.getFeature(IMessageFeature.class);
	}
	
	/**
	 *  Get the execution feature.
	 */
	public IExecutionFeature getExecutionFeature()
	{
		return component.getFeature(IExecutionFeature.class);
	}
	
//	/**
//	 *  Handle stream messages.
//	 */
//	class StreamDeliveryHandler implements ICommand
//	{
//		/**
//		 *  Execute the command.
//		 */
//		public void execute(Object obj)
//		{
//			try
//			{
//				byte[] rawmsg = (byte[])obj;
//				int mycnt = cnt++;
////				System.out.println("aaaa: "+mycnt+" "+getComponent().getComponentIdentifier());
////				System.out.println("Received binary: "+SUtil.arrayToString(rawmsg));
//				int idx = 1;
//				byte type = rawmsg[idx++];
//				
//				byte[] codec_ids = new byte[rawmsg[idx++]];
//				byte[] bconid = new byte[4];
//				for(int i=0; i<codec_ids.length; i++)
//				{
//					codec_ids[i] = rawmsg[idx++];
//				}
//				for(int i=0; i<4; i++)
//				{
//					bconid[i] = rawmsg[idx++];
//				}
//				final int conid = SUtil.bytesToInt(bconid);
//				
//				int seqnumber = -1;
//				if(type==StreamSendTask.DATA_OUTPUT_INITIATOR || type==StreamSendTask.DATA_INPUT_PARTICIPANT)
//				{
//					for(int i=0; i<4; i++)
//					{
//						bconid[i] = rawmsg[idx++];
//					}
//					seqnumber = SUtil.bytesToInt(bconid);
//	//				System.out.println("seqnr: "+seqnumber);
//				}
//				
//				final Object data;
//				if(codec_ids.length==0)
//				{
//					data = new byte[rawmsg.length-idx];
//					System.arraycopy(rawmsg, idx, data, 0, rawmsg.length-idx);
//				}
//				else
//				{
//					Object tmp = new ByteArrayInputStream(rawmsg, idx, rawmsg.length-idx);
//					for(int i=codec_ids.length-1; i>-1; i--)
//					{
//						ICodec dec = codecfactory.getCodec(codec_ids[i]);
//						tmp = dec.decode(tmp, classloader, null);
//					}
//					data = tmp;
//				}
//	
//				// Handle output connection participant side
//				if(type==StreamSendTask.INIT_OUTPUT_INITIATOR)
//				{
//					InitInfo ii = (InitInfo)data;
//					initInputConnection(conid, ii.getInitiator(), ii.getParticipant(), ii.getNonFunctionalProperties());
//					addrservice.addPlatformAddresses(ii.getInitiator());
//					addrservice.addPlatformAddresses(ii.getParticipant());
//				}
//				else if(type==StreamSendTask.ACKINIT_OUTPUT_PARTICIPANT)
//				{
////					System.out.println("CCC: ack init");
//					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(och!=null)
//					{
//						och.ackReceived(StreamSendTask.INIT, data);
//					}
//					else
//					{
//						System.out.println("OutputStream not found (ackinit): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.DATA_OUTPUT_INITIATOR)
//				{
////					System.out.println("received data");
//					InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.addData(seqnumber, (byte[])data);
//					}
//					else
//					{
//						System.out.println("InputStream not found (dai): "+conid+" "+pcons+" "+getComponent().getComponentIdentifier());
//					}
//				}
//				else if(type==StreamSendTask.CLOSE_OUTPUT_INITIATOR)
//				{
////					System.out.println("CCC: close");
//					InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.closeReceived(SUtil.bytesToInt((byte[])data));
//					}
//					else
//					{
//						System.out.println("InputStream not found (coi): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ACKCLOSE_OUTPUT_PARTICIPANT)
//				{
////					System.out.println("CCC: ackclose");
//					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(och!=null)
//					{
//						och.ackReceived(StreamSendTask.CLOSE, data);
//					}
//					else
//					{
//						System.out.println("OutputStream not found (ackclose): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.CLOSEREQ_OUTPUT_PARTICIPANT)
//				{
////					System.out.println("CCC: closereq");
//					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(och!=null)
//					{
//						och.closeRequestReceived();
//					}
//					else
//					{
//						System.out.println("OutputStream not found (closereq): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ACKCLOSEREQ_OUTPUT_INITIATOR)
//				{
////					System.out.println("CCC: ackclosereq");
//					InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.ackReceived(StreamSendTask.CLOSEREQ, data);
//	//					ich.ackCloseRequestReceived();
//					}
//					else
//					{
//						System.out.println("OutputStream not found (ackclosereq): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ACKDATA_OUTPUT_PARTICIPANT)
//				{
//					// Handle input connection initiator side
//					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(och!=null)
//					{
//						AckInfo ackinfo = (AckInfo)data;
//						och.ackDataReceived(ackinfo);
//					}
//					else
//					{
//						System.out.println("OutputStream not found (ackdata): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				
//				else if(type==StreamSendTask.INIT_INPUT_INITIATOR)
//				{
//					InitInfo ii = (InitInfo)data;
//					initOutputConnection(conid, ii.getInitiator(), ii.getParticipant(), ii.getNonFunctionalProperties());
//					addrservice.addPlatformAddresses(ii.getInitiator());
//					addrservice.addPlatformAddresses(ii.getParticipant());
//				}
//				else if(type==StreamSendTask.ACKINIT_INPUT_PARTICIPANT)
//				{
//					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.ackReceived(StreamSendTask.INIT, data);
//					}
//					else
//					{
//						System.out.println("InputStream not found (ackinit): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.DATA_INPUT_PARTICIPANT)
//				{
//					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.addData(seqnumber, (byte[])data);
//					}
//					else
//					{
//						System.out.println("InputStream not found (data input): "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ACKDATA_INPUT_INITIATOR)
//				{
//					OutputConnectionHandler och = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(och!=null)
//					{
//						AckInfo ackinfo = (AckInfo)data;
//						och.ackDataReceived(ackinfo);	
//					}
//					else
//					{
//						System.out.println("OutputStream not found (ackdata): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.CLOSEREQ_INPUT_INITIATOR)
//				{
//					OutputConnectionHandler och = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(och!=null)
//					{
//						och.closeRequestReceived();
//					}
//					else
//					{
//						System.out.println("InputStream not found (closereq): "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ACKCLOSEREQ_INPUT_PARTICIPANT)
//				{
//					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.ackReceived(StreamSendTask.CLOSEREQ, data);
//					}
//					else
//					{
//						System.out.println("InputStream not found (ackclosereq): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.CLOSE_INPUT_PARTICIPANT)
//				{
//					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.closeReceived(SUtil.bytesToInt((byte[])data));
//					}
//					else
//					{
//						System.out.println("OutputStream not found (closeinput): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ACKCLOSE_INPUT_INITIATOR)
//				{
//					OutputConnectionHandler ich = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(ich!=null)
//					{
//						ich.ackReceived(StreamSendTask.CLOSE, data);
//					}
//					else
//					{
//						System.out.println("InputStream not found (ackclose): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				
//				// Handle lease time update
//				else if(type==StreamSendTask.ALIVE_INITIATOR)
//				{
//	//				System.out.println("alive initiator");
//					AbstractConnectionHandler con = (AbstractConnectionHandler)pcons.get(Integer.valueOf(conid));
//					if(con!=null)
//					{
//						con.setAliveTime(System.currentTimeMillis());
//					}
//					else
//					{
//						System.out.println("Stream not found (alive ini): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//				else if(type==StreamSendTask.ALIVE_PARTICIPANT)
//				{
//	//				System.out.println("alive particpant");
//					AbstractConnectionHandler con = (AbstractConnectionHandler)icons.get(Integer.valueOf(conid));
//					if(con!=null)
//					{
//						con.setAliveTime(System.currentTimeMillis());
//					}
//					else
//					{
//						System.out.println("Stream not found (alive par): "+component+", "+System.currentTimeMillis()+", "+conid);
//					}
//				}
//	
////				System.out.println("bbbb: "+mycnt+" "+getComponent().getComponentIdentifier());
//			}
////			catch(Throwable e)
//			catch(final Exception e)
//			{
////				e.printStackTrace();
//				getComponent().scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ia.getLogger().warning("Exception in stream: "+e.getMessage());
//						return IFuture.DONE;
//					}
//				});
//			}
//		}
//	}
}
