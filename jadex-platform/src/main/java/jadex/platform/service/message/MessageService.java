package jadex.platform.service.message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentNotFoundException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IOutputConnection;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.MessageFailureException;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.ServiceTerminatedException;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IMessageListener;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.ISerializer;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.SCloner;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.platform.service.address.TransportAddressService;
import jadex.platform.service.awareness.discovery.message.IMessageAwarenessService;
import jadex.platform.service.message.streams.AbstractConnectionHandler;
import jadex.platform.service.message.streams.AckInfo;
import jadex.platform.service.message.streams.InitInfo;
import jadex.platform.service.message.streams.InputConnection;
import jadex.platform.service.message.streams.InputConnectionHandler;
import jadex.platform.service.message.streams.OutputConnection;
import jadex.platform.service.message.streams.OutputConnectionHandler;
import jadex.platform.service.message.streams.StreamSendTask;
import jadex.platform.service.message.transport.ITransport;
import jadex.platform.service.message.transport.MessageEnvelope;
import jadex.platform.service.remote.RemoteMethodInvocationHandler;

/**
 * The Message service serves several message-oriented purposes: a) sending and
 * delivering messages by using transports b) management of transports
 * (add/remove)
 * 
 * The message service performs sending and delivering messages by separate
 * actions that are individually executed on the execution service, i.e. they
 * are delivered synchronous or asynchronous depending on the execution service
 * mode.
 */
public class MessageService extends BasicService implements IMessageService
{
	// -------- constants --------

	// -------- attributes --------

	/** The component. */
	protected IInternalAccess component;

	/** The transports. */
	protected List<ITransport> transports;

	/** All addresses of this platform. */
	private String[] addresses;

	/** The message types. */
	protected Map messagetypes;

	/** The deliver message action executed by platform executor. */
	protected DeliverMessage delivermsg;

	/** The logger. */
	protected Logger logger;

	/** The listeners (listener->filter). */
	protected Map<IMessageListener, IFilter> listeners;

	/** The cashed clock service. */
	protected IClockService clockservice;

	/** The library service. */
	protected ILibraryService libservice;

	/** The execution service. */
	protected IExecutionService exeservice;

	// /** The address service. */
	protected ITransportAddressService addrservice;
	protected TransportAddressBook taddresses;

	/** The awareness service. */
	// protected IAwarenessManagementService ams;

	/** The cms. */
	protected IComponentManagementService cms;

	/**
	 * The class loader of the message service (only for envelope en/decoding,
	 * content is handled by receiver class loader).
	 */
	protected ClassLoader msgsrvcl;

	/** The target managers (platform id->manager). */
	protected LRU<IComponentIdentifier, SendManager> managers;

	/** The remote marshaling config for messages. */
	protected RemoteMarshalingConfig remotemarshalingconfig;

	/** The delivery handler map. */
	protected Map<Byte, ICommand<byte[]>> deliveryhandlers;

	/** The delivery handler map for local deliveries. */
	protected Map<Byte, ICommand<byte[]>> localdeliveryhandlers;

	/** The initiator connections. */
	protected Map<Integer, AbstractConnectionHandler> icons;

	/** The participant connections. */
	protected Map<Integer, AbstractConnectionHandler> pcons;

	/** The default content language (if not specified). */
	protected String deflanguage;

	/**
	 * Enable strict communication (i.e. fail on recoverable decoding errors).
	 */
	protected boolean strictcom;

	// -------- constructors --------

	/**
	 * Constructor for Outbox.
	 * 
	 * @param platform
	 */
	public MessageService(IInternalAccess component, Logger logger,
			ITransport[] transports, MessageType[] messagetypes)
	{
		this(component, logger, transports, messagetypes, null, null, false);
	}

	/**
	 * Constructor for Outbox.
	 * 
	 * @param platform
	 */
	public MessageService(IInternalAccess component, Logger logger,
			ITransport[] transports, MessageType[] messagetypes,
			String deflanguage, RemoteMarshalingConfig rmc, boolean strictcom)
	{
		super(component.getComponentIdentifier(), IMessageService.class, null);

		// Register communication classes with aliases
		// STransformation.registerClass(MessageEnvelope.class);
		STransformation.registerClass(AckInfo.class);
		STransformation.registerClass(InitInfo.class);

		this.strictcom = strictcom;
		this.component = component;
		this.transports = SCollection.createArrayList();
		for (int i = 0; i < transports.length; i++)
		{
			// Allow nulls to make it easier to exclude transports via platform
			// configuration.
			if (transports[i] != null)
				this.transports.add(transports[i]);
		}
		this.messagetypes = SCollection.createHashMap();
		for (int i = 0; i < messagetypes.length; i++)
			this.messagetypes.put(messagetypes[i].getName(), messagetypes[i]);
		this.delivermsg = new DeliverMessage();
		this.logger = logger;

		this.managers = new LRU<IComponentIdentifier, SendManager>(800);
		this.remotemarshalingconfig = rmc != null ? rmc
				: new RemoteMarshalingConfig();

		// The default language for content.
		this.deflanguage = deflanguage == null ? SFipa.JADEX_XML : deflanguage;

		this.deliveryhandlers = new HashMap<Byte, ICommand<byte[]>>();
		deliveryhandlers.put(MapSendTask.MESSAGE_TYPE_MAP,
				new MapDeliveryHandler());
		deliveryhandlers.put(StreamSendTask.MESSAGE_TYPE_STREAM,
				new StreamDeliveryHandler());

		this.icons = Collections.synchronizedMap(
				new HashMap<Integer, AbstractConnectionHandler>());
		this.pcons = Collections.synchronizedMap(
				new HashMap<Integer, AbstractConnectionHandler>());
	}

	// -------- interface methods --------

	/**
	 * 
	 */
	public IInputConnection getParticipantInputConnection(int conid,
			ITransportComponentIdentifier initiator,
			ITransportComponentIdentifier participant,
			Map<String, Object> nonfunc)
	{
		return initInputConnection(conid, initiator, participant, nonfunc);
	}

	/**
	 * 
	 */
	public IOutputConnection getParticipantOutputConnection(int conid,
			ITransportComponentIdentifier initiator,
			ITransportComponentIdentifier participant,
			Map<String, Object> nonfunc)
	{
		return initOutputConnection(conid, initiator, participant, nonfunc);
	}

	/**
	 * Create a virtual output connection.
	 */
	public OutputConnection internalCreateOutputConnection(
			IComponentIdentifier sender, IComponentIdentifier receiver,
			Map<String, Object> nonfunc)
	{
		UUID uuconid = UUID.randomUUID();
		int conid = uuconid.hashCode();
		OutputConnectionHandler och = new OutputConnectionHandler(this,
				nonfunc);
		icons.put(conid, och);
		OutputConnection con = new OutputConnection(
				TransportAddressService.getTransportComponentIdentifier(sender,
						taddresses),
				TransportAddressService.getTransportComponentIdentifier(
						receiver, taddresses),
				conid, true, och);
		return con;
	}

	/**
	 * Create a virtual output connection.
	 */
	public IFuture<IOutputConnection> createOutputConnection(
			IComponentIdentifier sender, IComponentIdentifier receiver,
			Map<String, Object> nonfunc)
	{
		return new Future<IOutputConnection>(
				internalCreateOutputConnection(sender, receiver, nonfunc));
	}

	/**
	 * Create a virtual input connection.
	 */
	public InputConnection internalCreateInputConnection(
			IComponentIdentifier sender, IComponentIdentifier receiver,
			Map<String, Object> nonfunc)
	{
		UUID uuconid = UUID.randomUUID();
		int conid = uuconid.hashCode();
		InputConnectionHandler ich = new InputConnectionHandler(this, nonfunc);
		icons.put(conid, ich);
		InputConnection con = new InputConnection(
				TransportAddressService.getTransportComponentIdentifier(sender,
						taddresses),
				TransportAddressService.getTransportComponentIdentifier(
						receiver, taddresses),
				conid, true, ich);
		return con;
	}

	/**
	 * Create a virtual input connection.
	 */
	public IFuture<IInputConnection> createInputConnection(
			IComponentIdentifier sender, IComponentIdentifier receiver,
			Map<String, Object> nonfunc)
	{
		return new Future<IInputConnection>(
				internalCreateInputConnection(sender, receiver, nonfunc));
	}

	/**
	 * Send a message.
	 * 
	 * @param message
	 *            The message as key value pairs.
	 * @param msgtype
	 *            The message type.
	 * @param sender
	 *            The sender component identifier.
	 * @param rid
	 *            The resource identifier used by the sending component (i.e.
	 *            corresponding to classes of objects in the message map).
	 * @param realrec
	 *            The real receiver if different from the message receiver (e.g.
	 *            message to rms encapsulating service call to other component).
	 * @param serializerid
	 *            ID of the serializer for encoding the message.
	 * @param codecids
	 *            The codecs to use for encoding (if different from default).
	 * @return Future that indicates an exception when messages could not be
	 *         delivered to components.
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> origmsg,
			final MessageType type, final IComponentIdentifier osender,
			final IResourceIdentifier rid,
			final IComponentIdentifier realrec, final Byte serializerid,
			final byte[] codecids)// , final Map<String, Object> nonfunc)
	{
		final Future<Void> ret = new Future<Void>();

		final IComponentIdentifier loc = IComponentIdentifier.LOCAL.get();

//		 System.err.println("send msg2: "+osender+" "+origmsg.get(SFipa.CONTENT));
		final Map<String, Object> msg = new HashMap<String, Object>(origmsg);

		final ITransportComponentIdentifier sender = TransportAddressService
				.getTransportComponentIdentifier(osender, taddresses);

		libservice.getClassLoader(rid).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
				{
					public void customResultAvailable(final ClassLoader cl)
					{

						if (loc != null
								&& IComponentIdentifier.LOCAL.get() != null
								&& !loc.equals(
										IComponentIdentifier.LOCAL.get()))
						{
							logger.severe(
									"Component thread backswitch failed. Should be: "
											+ loc + " but is "
											+ IComponentIdentifier.LOCAL.get());
						}

						// IComponentIdentifier sender =
						// adapter.getComponentIdentifier();
						if (sender == null)
						{
							ret.setException(new RuntimeException(
									"Sender must not be null: " + msg));
							return;
						}

						// Automatically add optional meta information.
						String senid = type.getSenderIdentifier();
						// if(msg.get(senid)==null)
						msg.put(senid, sender);

						final String idid = type.getIdIdentifier();
						if (msg.get(idid) == null)
							msg.put(idid, SUtil
									.createUniqueId(sender.getLocalName()));

						final String sd = type.getTimestampIdentifier();
						if (msg.get(sd) == null)
						{
							msg.put(sd, "" + clockservice.getTime());
						}

						final String ridid = type.getResourceIdIdentifier();
						if (msg.get(ridid) == null && rid != null
								&& rid.getGlobalIdentifier() != null
								&& !ResourceIdentifier.isJadexRid(rid))
						{
							msg.put(ridid, rid);
						}

						// final String realrecid =
						// type.getRealReceiverIdentifier();
						// if(msg.get(realrecid)==null && realrec!=null)
						// {
						// msg.put(realrecid, realrec);
						// }

						// Check receivers.
						Object tmp = msg.get(type.getReceiverIdentifier());
						if (tmp == null || SReflect.isIterable(tmp)
								&& !SReflect.getIterator(tmp).hasNext())
						{
							ret.setException(new RuntimeException(
									"Receivers must not be empty: " + msg));
							return;
						}
						
						cms.getExternalAccess(sender).addResultListener(
								new ExceptionDelegationResultListener<IExternalAccess, Void>(
										ret)
								{
									public void customResultAvailable(
											IExternalAccess exta)
									{
										// System.out.println("msgservice
										// calling doSendMessage()");
										// System.out.println("on2:
										// "+IComponentIdentifier.CALLER.get()+"
										// "+IComponentIdentifier.LOCAL.get());

										// System.err.println("send msg4:
										// "+sender+" "+msg.get(SFipa.CONTENT));
										doSendMessage(msg, type, realrec,
												rid, exta, cl, ret,
												serializerid, codecids);
									}

									public void exceptionOccurred(
											Exception exception)
									{
										super.exceptionOccurred(exception);
									}
								});
					}
				});

		return ret;
	}

	/**
	 * Extracted method to be callable from listener.
	 */
	protected void doSendMessage(Map<String, Object> msg,
			final MessageType type, IComponentIdentifier realrec,
			IResourceIdentifier rid, IExternalAccess comp, final ClassLoader cl,
			Future<Void> ret, Byte serializerid, byte[] codecids)
	{
		final Map<String, Object> msgcopy = new HashMap<String, Object>(msg);

		List<ITraverseProcessor> procs = Traverser.getDefaultProcessors();
		procs.add(1, new ITraverseProcessor()
		{
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				return TransportAddressService.getTransportComponentIdentifier(
						(ITransportComponentIdentifier) object, taddresses);
				// return
				// internalUpdateComponentIdentifier((ITransportComponentIdentifier)object);
			}

			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				return object instanceof ITransportComponentIdentifier;
			}
		});

		// Ignore service proxies.
		procs.add(1, new ITraverseProcessor()
		{
			public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
			{
				return object;
			}

			public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
			{
				Class<?> clazz = SReflect.getClass(type);
				return Proxy.isProxyClass(clazz) && (Proxy.getInvocationHandler(
						object) instanceof BasicServiceInvocationHandler
						|| Proxy.getInvocationHandler(
								object) instanceof RemoteMethodInvocationHandler);
			}
		});

		String[] names = (String[]) msgcopy.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++)
		{
			String name = names[i];
			Object value = msgcopy.get(name);
			value = Traverser.traverseObject(value, null, procs, Traverser.MODE.PLAIN, null);
//			value = Traverser.traverseObject(value, null, procs, null, false, null);
			msgcopy.put(name, value);
		}

		IComponentIdentifier sender = (IComponentIdentifier) msgcopy
				.get(type.getSenderIdentifier());
		// if(sender.getAddresses()==null || sender.getAddresses().length==0)
//			System.out.println("schrott2");

		IFilter[] fils;
		IMessageListener[] lis;
		synchronized (this)
		{
			fils = listeners == null ? null
					: listeners.values().toArray(new IFilter[listeners.size()]);
			lis = listeners == null ? null
					: listeners.keySet()
							.toArray(new IMessageListener[listeners.size()]);
		}

		if (lis != null)
		{
			// Hack?!
			IMessageAdapter msgadapter = new DefaultMessageAdapter(msgcopy,
					type);
			for (int i = 0; i < lis.length; i++)
			{
				IMessageListener li = (IMessageListener) lis[i];
				boolean match = false;
				try
				{
					match = fils[i] == null || fils[i].filter(msgadapter);
				}
				catch (Exception e)
				{
					logger.warning(
							"Filter threw exception: " + fils[i] + ", " + e);
				}
				if (match)
				{
					try
					{
						li.messageSent(msgadapter);
					}
					catch (Exception e)
					{
						logger.warning(
								"Listener threw exception: " + li + ", " + e);
					}
				}
			}
		}

		// Sending a message is delegated to SendManagers
		// Each SendManager is responsible for a specific destination
		// in order to decouple sending to different destinations.

		// Determine manager tasks
		MultiCollection<SendManager, IComponentIdentifier> managers = new MultiCollection<SendManager, IComponentIdentifier>();
		String recid = type.getReceiverIdentifier();
		Object tmp = msgcopy.get(recid);
		if (SReflect.isIterable(tmp))
		{
			for (Iterator<?> it = SReflect.getIterator(tmp); it.hasNext();)
			{
				IComponentIdentifier cid = (IComponentIdentifier) it.next();
				ITransportComponentIdentifier tcid = TransportAddressService
						.getTransportComponentIdentifier(cid, taddresses);
				SendManager sm = getSendManager(tcid);
				managers.add(sm, tcid);
			}
		}
		else
		{
			IComponentIdentifier cid = (IComponentIdentifier) tmp;
			ITransportComponentIdentifier tcid = TransportAddressService
					.getTransportComponentIdentifier(cid, taddresses);
			SendManager sm = getSendManager(tcid);
			managers.add(sm, tcid);
		}

		byte[] cids = codecids;
		if (cids == null || cids.length == 0)
			cids = remotemarshalingconfig.getDefaultCodecIds();
		final ITraverseProcessor[] preprocessors = remotemarshalingconfig
				.getPreprocessors();
		final ISerializer serializer = serializerid != null
				? remotemarshalingconfig.getSerializer(serializerid)
				: remotemarshalingconfig.getDefaultSerializer();
		final ICodec[] codecs = getBinaryCodecs(cids);

		final CounterResultListener<Void> crl = new CounterResultListener<Void>(
				managers.size(), false,
				new DelegationResultListener<Void>(ret));
		for (Iterator<?> it = managers.keySet().iterator(); it.hasNext();)
		{
			final SendManager tm = (SendManager) it.next();
			ITransportComponentIdentifier[] recs = managers.getCollection(tm)
					.toArray(new ITransportComponentIdentifier[0]);
			
			// Remove stuff that's already in the envelope
			Object msgrecs = msgcopy.get(SFipa.RECEIVERS);
			if (msgrecs != null && msgrecs instanceof IComponentIdentifier[] && ((IComponentIdentifier[]) msgrecs).length == recs.length)
				msgcopy.remove(SFipa.RECEIVERS);
			msgcopy.remove(SFipa.X_RECEIVER);
			msgcopy.remove(SFipa.X_RID);
			
			MapSendTask task = new MapSendTask(msgcopy, type, recs, realrec,
					rid, getTransports(), preprocessors, serializer, codecs,
					cl);
			tm.addMessage(task).addResultListener(crl);
		}

		// sendmsg.addMessage(msgcopy, type, receivers, ret);
	}

	/**
	 * Get array of binary codecs for codec ids.
	 */
	public ICodec[] getBinaryCodecs(byte[] codecids)
	{
		ICodec[] codecs = new ICodec[codecids.length];
		for (int i = 0; i < codecs.length; i++)
		{
			codecs[i] = remotemarshalingconfig.getCodec(codecids[i]);
		}
		return codecs;
	}

	/**
	 * Get the serializers.
	 * 
	 * @return The serializer.
	 */
	public IFuture<Map<Byte, ISerializer>> getAllSerializers()
	{
		return new Future<Map<Byte, ISerializer>>(
				remotemarshalingconfig.getAllSerializers());
	}

	/**
	 * Get the codecs with message codecs.
	 * 
	 * @return The codec factory.
	 */
	public IFuture<Map<Byte, ICodec>> getAllCodecs()
	{
		return new Future<Map<Byte, ICodec>>(
				remotemarshalingconfig.getAllCodecs());
	}

	/**
	 * Get the serializers and codecs.
	 * 
	 * @return The serializer and codecs.
	 */
	public IFuture<Tuple2<Map<Byte, ISerializer>, Map<Byte, ICodec>>> getAllSerializersAndCodecs()
	{
		Tuple2<Map<Byte, ISerializer>, Map<Byte, ICodec>> ret = new Tuple2<Map<Byte, ISerializer>, Map<Byte, ICodec>>(
				remotemarshalingconfig.getAllSerializers(),
				remotemarshalingconfig.getAllCodecs());
		return new Future<Tuple2<Map<Byte, ISerializer>, Map<Byte, ICodec>>>(
				ret);
	}

	/**
	 * Get the default codecs.
	 * 
	 * @return The default codecs.
	 */
	public IFuture<ICodec[]> getDefaultCodecs()
	{
		return new Future<ICodec[]>(
				remotemarshalingconfig.getDefaultCodecs());
	}

	/**
	 * Deliver a message to the intended components. Called from transports.
	 * 
	 * @param message
	 *            The native message. (Synchronized because can be called from
	 *            concurrently executing transports)
	 */
	public void deliverMessage(byte[] msg)
	{
		delivermsg.addMessage(msg);
	}
	
	/**
	 *  Delivers a message locally using cloning, used by local transport.
	 *  @param task The send task used to send the message.
	 */
	public void deliverMessageLocally(final ISendTask task)
	{
		
		exeservice.execute(new IExecutable()
		{
			boolean doexecute = true;
			
			public boolean execute()
			{
				if (doexecute)
				{
					System.out.println("Executing local delivery.");
					if (task instanceof MapSendTask)
					{
						final MapSendTask mst = (MapSendTask) task;
						Map<String, Object> msg = (Map<String, Object>) SCloner.clone(mst.getRawMessage());
						deliverToAllReceivers(mst.getReceivers(), cms, null, msg, logger, mst.getMessageType());
					}
					else if (task instanceof StreamSendTask)
					{
						StreamSendTask sst = (StreamSendTask) task;
						sst.setRawMessage(SCloner.clone(sst.getRawMessage()));
						deliverDecodedStreamMessage(sst.getStreamMessageType(), sst.getStreamId(), sst.getSequenceNumber()!=null?sst.getSequenceNumber():-1, sst.getRawMessage());
					}
					doexecute = false;
				}
				else
				{
					System.out.println("Skipping spurious execution.");
				}
				return false;
			}
		});
	}

	/**
	 * Adds a transport for this outbox.
	 * 
	 * @param transport
	 *            The transport.
	 */
	public void addTransport(ITransport transport)
	{
		transports.add(transport);
		addresses = null;
	}

	/**
	 * Remove a transport for the outbox.
	 * 
	 * @param transport
	 *            The transport.
	 */
	public void removeTransport(ITransport transport)
	{
		transports.remove(transport);
		transport.shutdown();
		addresses = null;
	}

	/**
	 * Moves a transport up or down.
	 * 
	 * @param up
	 *            Move up?
	 * @param transport
	 *            The transport to move.
	 */
	public synchronized void changeTransportPosition(boolean up,
			ITransport transport)
	{
		int index = transports.indexOf(transport);
		if (up && index > 0)
		{
			ITransport temptrans = (ITransport) transports.get(index - 1);
			transports.set(index - 1, transport);
			transports.set(index, temptrans);
		}
		else if (index != -1 && index < transports.size() - 1)
		{
			ITransport temptrans = (ITransport) transports.get(index + 1);
			transports.set(index + 1, transport);
			transports.set(index, temptrans);
		}
		else
		{
			throw new RuntimeException("Cannot change transport position from "
					+ index + (up ? " up" : " down"));
		}
	}

	/**
	 * Get the adresses of a component.
	 * 
	 * @return The addresses of this component.
	 */
	public String[] internalGetAddresses()
	{
		if (addresses == null)
		{
			ITransport[] trans = (ITransport[]) transports
					.toArray(new ITransport[transports.size()]);
			ArrayList addrs = new ArrayList();
			for (int i = 0; i < trans.length; i++)
			{
				String[] traddrs = trans[i].getAddresses();
				for (int j = 0; traddrs != null && j < traddrs.length; j++)
					addrs.add(traddrs[j]);
			}
			addresses = (String[]) addrs.toArray(new String[addrs.size()]);

			// System.out.println("addresses: "+SUtil.arrayToString(addresses));
		}

		return addresses;
	}

	/**
	 * Get the adresses of a component.
	 * 
	 * @return The addresses of this component.
	 */
	public IFuture<String[]> getAddresses()
	{
		return new Future<String[]>(internalGetAddresses());
	}

	/**
	 * Get addresses of all transports.
	 * 
	 * @return The address schemes of all transports.
	 */
	public String[] getAddressSchemes()
	{
		ITransport[] trans = (ITransport[]) transports
				.toArray(new ITransport[transports.size()]);
		ArrayList schemes = new ArrayList();
		for (int i = 0; i < trans.length; i++)
		{
			String[] aschemes = trans[i].getServiceSchemas();
			schemes.addAll(Arrays.asList(aschemes));
		}

		return (String[]) schemes.toArray(new String[schemes.size()]);
	}

	/**
	 * Get the transports.
	 * 
	 * @return The transports.
	 */
	public ITransport[] getTransports()
	{
		ITransport[] transportsArray = new ITransport[transports.size()];
		return (ITransport[]) transports.toArray(transportsArray);
	}

	/**
	 * Get a send target manager for addresses.
	 */
	public SendManager getSendManager(IComponentIdentifier cid)
	{
		SendManager ret = managers.get(cid.getRoot());

		if (ret == null)
		{
			ret = new SendManager();
			managers.put(cid.getRoot(), ret);
		}

		return ret;
	}

	public RemoteMarshalingConfig getRemoteMarshalingConfig()
	{
		return remotemarshalingconfig;
	}

	// -------- IPlatformService interface --------

	/**
	 * Start the service.
	 */
	public IFuture<Void> startService()
	{
		final Future<Void> ret = new Future<Void>();

		super.startService()
				.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if (transports.size() == 0)
						{
							ret.setException(new RuntimeException(
									"MessageService has no working transport for sending messages."));
						}
						else
						{
							exeservice = SServiceProvider.getLocalService(
									component, IExecutionService.class,
									RequiredServiceInfo.SCOPE_PLATFORM, false);
							cms = SServiceProvider.getLocalService(component,
									IComponentManagementService.class,
									RequiredServiceInfo.SCOPE_PLATFORM, false);
							ITransport[] tps = (ITransport[]) transports
									.toArray(new ITransport[transports.size()]);
							CollectionResultListener<Void> lis = new CollectionResultListener<Void>(
									tps.length, true,
									new ExceptionDelegationResultListener<Collection<Void>, Void>(
											ret)
									{
										public void customResultAvailable(
												Collection<Void> result)
										{
											if (result.isEmpty())
											{
												ret.setException(
														new RuntimeException(
																"MessageService has no working transport for sending messages."));
											}
											else
											{
												addrservice = SServiceProvider
														.getLocalService(
																component,
																ITransportAddressService.class,
																RequiredServiceInfo.SCOPE_PLATFORM,
																false);
												addrservice
														.getTransportAddresses()
														.addResultListener(
																new ExceptionDelegationResultListener<TransportAddressBook, Void>(
																		ret)
																{
																	public void customResultAvailable(
																			TransportAddressBook result)
																	{
																		taddresses = result;

																		addrservice
																				.addPlatformAddresses(
																						new ComponentIdentifier(
																								component
																										.getComponentIdentifier()
																										.getRoot()
																										.getName(),
																								internalGetAddresses()))
																				.addResultListener(
																						new DelegationResultListener<Void>(
																								ret)
																						{
																							public void customResultAvailable(
																									Void result)
																							{
																								clockservice = SServiceProvider
																										.getLocalService(
																												component,
																												IClockService.class,
																												RequiredServiceInfo.SCOPE_PLATFORM,
																												false);
																								libservice = SServiceProvider
																										.getLocalService(
																												component,
																												ILibraryService.class,
																												RequiredServiceInfo.SCOPE_PLATFORM,
																												false);
																								libservice
																										.getClassLoader(
																												component
																														.getModel()
																														.getResourceIdentifier())
																										.addResultListener(
																												new ExceptionDelegationResultListener<ClassLoader, Void>(
																														ret)
																												{
																													public void customResultAvailable(
																															ClassLoader result)
																													{
																														msgsrvcl = result;
																														startStreamSendAliveBehavior();
																														startStreamCheckAliveBehavior();
																														// ams
																														// =
																														// SServiceProvider.getLocalService(component,
																														// IAwarenessManagementService.class,
																														// RequiredServiceInfo.SCOPE_PLATFORM);
																														ret.setResult(
																																null);
																													}
																												});
																							}
																						});
																	}
																});
											}
										}
									});

							for (int i = 0; i < tps.length; i++)
							{
								final ITransport transport = tps[i];
								IFuture<Void> fut = transport.start();
								fut.addResultListener(lis);
								fut.addResultListener(
										new IResultListener<Void>()
										{
											public void resultAvailable(
													Void result)
											{
											}

											public void exceptionOccurred(
													final Exception exception)
											{
												transports.remove(transport);
												getComponent().scheduleStep(
														new IComponentStep<Void>()
														{
															public IFuture<Void> execute(
																	IInternalAccess ia)
															{
																ia.getLogger()
																		.warning(
																				"Could not initialize transport: "
																						+ transport
																						+ " reason: "
																						+ exception);
																return IFuture.DONE;
															}
														});
											}
										});
							}
						}
					}
				});
		return ret;
	}

	/**
	 * Called when the platform shuts down. Do necessary cleanup here (if any).
	 */
	public IFuture<Void> shutdownService()
	{
		Future<Void> ret = new Future<Void>();
		SendManager[] tmp = (SendManager[]) managers.values()
				.toArray(new SendManager[managers.size()]);
		final SendManager[] sms = (SendManager[]) SUtil.arrayToSet(tmp)
				.toArray(new SendManager[0]);
		// System.err.println("MessageService shutdown start:
		// "+(transports.size()+sms.length+1));
		final CounterResultListener<Void> crl = new CounterResultListener<Void>(
				transports.size() + sms.length + 1, true,
				new DelegationResultListener<Void>(ret));
		
		super.shutdownService().addResultListener(crl);

		for (int i = 0; i < sms.length; i++)
		{
			// System.err.println("MessageService executor cancel: "+sms[i]);
			exeservice.cancel(sms[i]).addResultListener(crl);
		}

		for (int i = 0; i < transports.size(); i++)
		{
			((ITransport) transports.get(i)).shutdown().addResultListener(crl);
		}

		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}

		return ret;
	}

	/**
	 * Get the message type.
	 * 
	 * @param type
	 *            The type name.
	 * @return The message type.
	 */
	public MessageType getMessageType(String type)
	{
		return (MessageType) messagetypes.get(type);
	}

	/**
	 * Add a message listener.
	 * 
	 * @param listener
	 *            The change listener.
	 * @param filter
	 *            An optional filter to only receive notifications for matching
	 *            messages.
	 */
	public synchronized IFuture<Void> addMessageListener(
			IMessageListener listener, IFilter filter)
	{
		if (listeners == null)
			listeners = new LinkedHashMap();
		listeners.put(listener, filter);
		return IFuture.DONE;
	}

	/**
	 * Remove a message listener.
	 * 
	 * @param listener
	 *            The change listener.
	 */
	public synchronized IFuture<Void> removeMessageListener(
			IMessageListener listener)
	{
		listeners.remove(listener);
		return IFuture.DONE;
	}

	/**
	 * Adds preprocessors to the encoding stage.
	 * 
	 * @param Preprocessors.
	 */
	public IFuture<Void> addPreprocessors(ITraverseProcessor[] processors)
	{
		remotemarshalingconfig.addPreprocessors(processors);
		return IFuture.DONE;
	}

	/**
	 * Adds postprocessors to the encoding stage.
	 * 
	 * @param Postprocessors.
	 */
	public IFuture<Void> addPostprocessors(ITraverseProcessor[] processors)
	{
		remotemarshalingconfig.addPostprocessors(processors);
		return IFuture.DONE;
	}

	/**
	 * Add message codec.
	 * 
	 * @param codec
	 *            The codec.
	 */
	public IFuture<Void> addBinaryCodec(ICodec codec)
	{
		remotemarshalingconfig.addCodec(codec);
		return IFuture.DONE;
	}

	/**
	 * Remove message codec.
	 * 
	 * @param codec
	 *            The codec.
	 */
	public IFuture<Void> removeBinaryCodec(ICodec codec)
	{
		remotemarshalingconfig.removeCodec(codec);
		return IFuture.DONE;
	}

	/**
	 * Announce that addresses of transports might have changed.
	 */
	public IFuture<Void> refreshAddresses()
	{
		addresses = null;
		for (IDiscoveryService ds : SServiceProvider.getLocalServices(component,
				IDiscoveryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
		{
			ds.republish();
		}

		// this is suboptimal currently because internalGetAddresses has to
		// rebuild addresses immediately so
		// it could be done here
		return addrservice.addPlatformAddresses(new ComponentIdentifier(
				component.getComponentIdentifier().getRoot().getName(),
				internalGetAddresses()));
	}

	// -------- internal methods --------

	/**
	 * Get the component.
	 * 
	 * @return The component.
	 */
	public IExternalAccess getComponent()
	{
		return component.getExternalAccess();
	}

	/**
	 * 
	 */
	public void startStreamSendAliveBehavior()
	{
		final long lt = StreamSendTask
				.getMinLeaseTime(getComponent().getComponentIdentifier());
		if (lt != Timeout.NONE)
		{
			getComponent().scheduleStep(new IComponentStep<Void>()
			{
				@Classname("sendAlive")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// System.out.println("sendAlive: "+pcons+" "+icons);
					AbstractConnectionHandler[] mypcons = (AbstractConnectionHandler[]) pcons
							.values().toArray(new AbstractConnectionHandler[0]);
					for (int i = 0; i < mypcons.length; i++)
					{
						if (!mypcons[i].isClosed())
						{
							mypcons[i].sendAlive();
						}
					}
					AbstractConnectionHandler[] myicons = (AbstractConnectionHandler[]) icons
							.values().toArray(new AbstractConnectionHandler[0]);
					for (int i = 0; i < myicons.length; i++)
					{
						if (!myicons[i].isClosed())
						{
							myicons[i].sendAlive();
						}
					}

					waitForRealDelay(lt, this);

					return IFuture.DONE;
				}
			});
		}
	}

	/**
	 * 
	 */
	public void startStreamCheckAliveBehavior()
	{
		final long lt = StreamSendTask
				.getMinLeaseTime(getComponent().getComponentIdentifier());
		// System.out.println("to is: "+lt);
		if (lt != Timeout.NONE)
		{
			getComponent().scheduleStep(new IComponentStep<Void>()
			{
				@Classname("checkAlive")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// final IComponentStep<Void> step = this;
					// final Future<Void> ret = new Future<Void>();

					AbstractConnectionHandler[] mypcons = (AbstractConnectionHandler[]) pcons
							.values().toArray(new AbstractConnectionHandler[0]);
					for (int i = 0; i < mypcons.length; i++)
					{
						if (!mypcons[i].isConnectionAlive())
						{
							mypcons[i].close();
							pcons.remove(Integer
									.valueOf(mypcons[i].getConnectionId()));
						}
					}
					AbstractConnectionHandler[] myicons = (AbstractConnectionHandler[]) icons
							.values().toArray(new AbstractConnectionHandler[0]);
					for (int i = 0; i < myicons.length; i++)
					{
						if (!myicons[i].isConnectionAlive())
						{
							myicons[i].close();
							icons.remove(Integer
									.valueOf(myicons[i].getConnectionId()));
						}
					}

					waitForRealDelay(lt, this);

					return IFuture.DONE;
				}
			});
		}
	}

	/**
	 * Deliver a message to the receivers.
	 */
	protected void internalDeliverMessage(byte[] rawmsg)
	{
		try
		{
			ICommand<byte[]> handler;

			byte rmt = rawmsg[0];

			handler = deliveryhandlers.get(rmt);
			if (handler == null)
				throw new RuntimeException(
						"Corrupt message, unknown delivery handler code.");
			handler.execute(rawmsg);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			MessageEnvelope me = null;
			try
			{
				me = MapSendTask.decodeMessageEnvelope(rawmsg,
						remotemarshalingconfig.getAllSerializers(),
						remotemarshalingconfig.getAllCodecs(), msgsrvcl, null);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			logger.warning("Message could not be delivered to receivers: "
					+ (me != null ? me.getReceivers() : "unknown") + ", " + e);
		}
	}

	/**
	 * Gets the classloader for the receivers.
	 * 
	 * @param rid
	 *            Explicitly defined global RID.
	 * @param servicerec
	 *            Targeted received in case of services.
	 * @param receivers
	 *            The receivers.
	 * @return The classloader.
	 */
	protected IFuture<ClassLoader> getRIDClassLoader(IResourceIdentifier rid, IComponentIdentifier realrec, final IComponentIdentifier[] receivers)
	{
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		// TODO: Enable when global RIDs work properly
		// if (rid != null && [valid global ID])
		// {
		// getRIDClassLoader(rid).addResultListener(new
		// DelegationResultListener<ClassLoader>(ret));
		// }
		// else if (servicerec != null)
//		System.out.println("RID IS " + receivers[0].getLocalName());
//		if (realrec != null && component.getComponentIdentifier().getRoot().equals(realrec))
//		{
//			libservice.getClassLoader(null).addResultListener(new DelegationResultListener<ClassLoader>(ret));
//		}
//		else 
		if (realrec != null)
		{
			cms.getComponentDescription(realrec).addResultListener(
					new IResultListener<IComponentDescription>()
					{
						public void resultAvailable(IComponentDescription desc)
						{
							getRIDClassLoader(desc.getResourceIdentifier()).addResultListener(new DelegationResultListener<ClassLoader>(ret));
						}
						public void exceptionOccurred(Exception exception)
						{
							if (exception instanceof ComponentNotFoundException)
							{
								// Real receiver is gone/has terminated, attempt alternative decoding to let proxy receiver (e.g. rms) deal with it.
								libservice.getClassLoader(null).addResultListener(new DelegationResultListener<ClassLoader>(ret));
							}
						};
					});
		}
		else
		{
			// TODO: Include identity checks
			cms.getComponentDescription(receivers[0]).addResultListener(new ExceptionDelegationResultListener<IComponentDescription, ClassLoader>(ret)
					{
						public void customResultAvailable(
								IComponentDescription desc) throws Exception
						{
							getRIDClassLoader(desc.getResourceIdentifier())
									.addResultListener(
											new DelegationResultListener<ClassLoader>(ret));
						}
					});
		}

		return ret;
	}

	/**
	 * Gets the classloader for the rid.
	 * 
	 * @param rid
	 *            The rid.
	 * @return The classloader.
	 */
	protected IFuture<ClassLoader> getRIDClassLoader(IResourceIdentifier rid)
	{
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		libservice.getClassLoader(rid).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, ClassLoader>(
						ret)
				{
					public void customResultAvailable(ClassLoader result)
							throws Exception
					{
						ret.setResult(result);
					}
				});
		return ret;
	}

	/**
	 * Send message(s) executable.
	 */
	public class SendManager implements IExecutable
	{
		// -------- attributes --------

		/** The list of messages to send. */
		protected List<AbstractSendTask> tasks;

		// -------- constructors --------

		/**
		 * Send manager.
		 */
		public SendManager()
		{
			this.tasks = new ArrayList<AbstractSendTask>();
		}

		// -------- methods --------

		/**
		 * Send a message.
		 */
		public boolean execute()
		{
			AbstractSendTask tmp = null;
			boolean isempty;

			synchronized (this)
			{
				if (!tasks.isEmpty())
					tmp = tasks.remove(0);
				isempty = tasks.isEmpty();
			}
			final AbstractSendTask task = tmp;

			if (task != null)
			{
				// Todo: move back to send manager thread after isValid()
				// (hack!!! currently only works because message service is raw)
				// hack!!! doesn't make much sense to check isValid as send
				// manager executes on different thread.
				isValid().addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						if (result.booleanValue())
						{
							task.doSendMessage();
						}

						// Quit when service was terminated.
						else
						{
							// System.out.println("send message not executed");
							task.getFuture()
									.setException(new MessageFailureException(
											task.getRawMessage(),
											task.getMessageType(), null,
											"Message service terminated."));
							// isempty = true;
							// messages.clear();
						}
					}

					public void exceptionOccurred(Exception exception)
					{
						// System.out.println("send message not executed");
						task.getFuture()
								.setException(new MessageFailureException(
										task.getRawMessage(),
										task.getMessageType(), null,
										"Message service terminated."));
						// isempty = true;
						// messages.clear();
					}
				});
			}

			return !isempty;
		}

		/**
		 * Add a message to be sent.
		 * 
		 * @param message
		 *            The message.
		 */
		public IFuture<Void> addMessage(final AbstractSendTask task)
		{
			// if(new Random().nextInt(1000)==0)
			// {
			// task.getFuture().setException(new RuntimeException("Random
			// message error for testing: "+task.getFuture()));
			// }
			// else
			{
				isValid().addResultListener(
						new ExceptionDelegationResultListener<Boolean, Void>(
								task.getFuture())
						{
							public void customResultAvailable(Boolean result)
							{
								if (result.booleanValue())
								{
									synchronized (SendManager.this)
									{
										tasks.add(task);
									}

									exeservice.execute(SendManager.this);
								}
								// Fail when service was shut down.
								else
								{
									// System.out.println("message not added");
									task.getFuture().setException(
											new ServiceTerminatedException(
													getServiceIdentifier()));
								}
							}
						});
			}

			return task.getFuture();
		}
	}

	/**
	 * Deliver message(s) executable.
	 */
	protected class DeliverMessage implements IExecutable
	{
		// -------- attributes --------

		/** The list of messages to send. */
		protected List<byte[]> messages;

		// -------- constructors --------

		/**
		 * Create a new deliver message executable.
		 */
		public DeliverMessage()
		{
			this.messages = new ArrayList<byte[]>();
		}

		// -------- methods --------

		/**
		 * Deliver the message.
		 */
		public boolean execute()
		{
			byte[] tmp = null;
			boolean isempty;

			synchronized (this)
			{
				if (!messages.isEmpty())
					tmp = messages.remove(0);
				isempty = messages.isEmpty();
			}

			if (tmp != null)
			{
				internalDeliverMessage(tmp);
			}

			return !isempty;
		}

		/**
		 * Add a message to be delivered.
		 */
		public void addMessage(byte[] msg)
		{
			synchronized (this)
			{
				messages.add(msg);
			}

			exeservice.execute(DeliverMessage.this);
		}
	}

	/**
	 * Handle stream messages.
	 */
	class StreamDeliveryHandler implements ICommand<byte[]>
	{
		/**
		 * Execute the command.
		 */
		public void execute(byte[] obj)
		{
			try
			{
				byte[] rawmsg = (byte[]) obj;
				// System.out.println("aaaa: "+mycnt+"
				// "+getComponent().getComponentIdentifier());
				// System.out.println("Received binary:
				// "+SUtil.arrayToString(rawmsg));
				int idx = 1;
				byte type = rawmsg[idx++];

				byte serializerid = rawmsg[idx++];
				byte[] codec_ids = new byte[rawmsg[idx++]];
				// byte[] bconid = new byte[4];
				// for(int i=0; i<codec_ids.length; i++)
				// {
				// codec_ids[i] = rawmsg[idx++];
				// }
				System.arraycopy(rawmsg, idx, codec_ids, 0, codec_ids.length);
				idx += codec_ids.length;
				// for(int i=0; i<4; i++)
				// {
				// bconid[i] = rawmsg[idx++];
				// }
				// final int conid = SUtil.bytesToInt(bconid);
				final int conid = SUtil.bytesToInt(rawmsg, idx);
				idx += 4;

				int seqnumber = -1;
				if (type == StreamSendTask.DATA_OUTPUT_INITIATOR || type == StreamSendTask.DATA_INPUT_PARTICIPANT)
				{
					// for(int i=0; i<4; i++)
					// {
					// bconid[i] = rawmsg[idx++];
					// }
					// seqnumber = SUtil.bytesToInt(bconid);
					seqnumber = SUtil.bytesToInt(rawmsg, idx);
					idx += 4;
					// System.out.println("seqnr: "+seqnumber);
				}

				final Object data;
				if (serializerid == -1)
				{
					byte[] tdata = new byte[rawmsg.length - idx];
					System.arraycopy(rawmsg, idx, tdata, 0,
							rawmsg.length - idx);
					for (int i = codec_ids.length - 1; i > -1; i--)
					{
						ICodec dec = remotemarshalingconfig
								.getCodec(codec_ids[i]);
						tdata = dec.decode(tdata);
					}
					data = tdata;
				}
				else
				{
					// Object tmp = new ByteArrayInputStream(rawmsg, idx,
					// rawmsg.length-idx);
					byte[] tmp = new byte[rawmsg.length - idx];
					System.arraycopy(rawmsg, idx, tmp, 0, tmp.length);

					for (int i = codec_ids.length - 1; i > -1; i--)
					{
						ICodec dec = remotemarshalingconfig
								.getCodec(codec_ids[i]);
						tmp = dec.decode(tmp);
					}
					data = remotemarshalingconfig.getAllSerializers()
							.get(serializerid).decode(tmp,
									getClass().getClassLoader(), null, null);
					// data = tmp;
				}

				// Handle output connection participant side
				deliverDecodedStreamMessage(type, conid, seqnumber, data);

				// System.out.println("bbbb: "+mycnt+"
				// "+getComponent().getComponentIdentifier());
			}
			// catch(Throwable e)
			catch (final Exception e)
			{
				e.printStackTrace();
				getComponent().scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getLogger().warning(
								"Exception in stream: " + e.getMessage());
						return IFuture.DONE;
					}
				});
			}
		}
	}

	protected void deliverDecodedStreamMessage(byte type, int conid,
			int seqnumber, Object data)
	{
		if (type == StreamSendTask.INIT_OUTPUT_INITIATOR)
		{
			InitInfo ii = (InitInfo) data;
			initInputConnection(conid, ii.getInitiator(), ii.getParticipant(),
					ii.getNonFunctionalProperties());
			addrservice.addPlatformAddresses(ii.getInitiator());
			addrservice.addPlatformAddresses(ii.getParticipant());
		}
		else if (type == StreamSendTask.ACKINIT_OUTPUT_PARTICIPANT)
		{
			// System.out.println("CCC: ack init");
			OutputConnectionHandler och = (OutputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (och != null)
			{
				och.ackReceived(StreamSendTask.INIT, data);
			}
			else
			{
				System.out.println(
						"OutputStream not found (ackinit): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.DATA_OUTPUT_INITIATOR)
		{
			// System.out.println("received data");
			InputConnectionHandler ich = (InputConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.addData(seqnumber, (byte[]) data);
			}
			else
			{
				System.out.println("InputStream not found (dai): " + conid + " "
						+ pcons + " "
						+ getComponent().getComponentIdentifier());
			}
		}
		else if (type == StreamSendTask.CLOSE_OUTPUT_INITIATOR)
		{
			// System.out.println("CCC: close");
			InputConnectionHandler ich = (InputConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.closeReceived(SUtil.bytesToInt((byte[]) data));
			}
			else
			{
				System.out.println("InputStream not found (coi): " + component
						+ ", " + System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.ACKCLOSE_OUTPUT_PARTICIPANT)
		{
			// System.out.println("CCC: ackclose");
			OutputConnectionHandler och = (OutputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (och != null)
			{
				och.ackReceived(StreamSendTask.CLOSE, data);
			}
			else
			{
				System.out.println(
						"OutputStream not found (ackclose): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.CLOSEREQ_OUTPUT_PARTICIPANT)
		{
			// System.out.println("CCC: closereq");
			OutputConnectionHandler och = (OutputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (och != null)
			{
				och.closeRequestReceived();
			}
			else
			{
				System.out.println(
						"OutputStream not found (closereq): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.ACKCLOSEREQ_OUTPUT_INITIATOR)
		{
			// System.out.println("CCC: ackclosereq");
			InputConnectionHandler ich = (InputConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.ackReceived(StreamSendTask.CLOSEREQ, data);
				// ich.ackCloseRequestReceived();
			}
			else
			{
				System.out.println("OutputStream not found (ackclosereq): "
						+ component + ", " + System.currentTimeMillis() + ", "
						+ conid);
			}
		}
		else if (type == StreamSendTask.ACKDATA_OUTPUT_PARTICIPANT)
		{
			// Handle input connection initiator side
			OutputConnectionHandler och = (OutputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (och != null)
			{
				AckInfo ackinfo = (AckInfo) data;
				och.ackDataReceived(ackinfo);
			}
			else
			{
				System.out.println(
						"OutputStream not found (ackdata): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}

		else if (type == StreamSendTask.INIT_INPUT_INITIATOR)
		{
			InitInfo ii = (InitInfo) data;
			initOutputConnection(conid, ii.getInitiator(), ii.getParticipant(),
					ii.getNonFunctionalProperties());
			addrservice.addPlatformAddresses(ii.getInitiator());
			addrservice.addPlatformAddresses(ii.getParticipant());
		}
		else if (type == StreamSendTask.ACKINIT_INPUT_PARTICIPANT)
		{
			InputConnectionHandler ich = (InputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.ackReceived(StreamSendTask.INIT, data);
			}
			else
			{
				System.out.println(
						"InputStream not found (ackinit): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.DATA_INPUT_PARTICIPANT)
		{
			InputConnectionHandler ich = (InputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.addData(seqnumber, (byte[]) data);
			}
			else
			{
				System.out.println(
						"InputStream not found (data input): " + conid);
			}
		}
		else if (type == StreamSendTask.ACKDATA_INPUT_INITIATOR)
		{
			OutputConnectionHandler och = (OutputConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (och != null)
			{
				AckInfo ackinfo = (AckInfo) data;
				och.ackDataReceived(ackinfo);
			}
			else
			{
				System.out.println(
						"OutputStream not found (ackdata): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.CLOSEREQ_INPUT_INITIATOR)
		{
			OutputConnectionHandler och = (OutputConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (och != null)
			{
				och.closeRequestReceived();
			}
			else
			{
				System.out
						.println("InputStream not found (closereq): " + conid);
			}
		}
		else if (type == StreamSendTask.ACKCLOSEREQ_INPUT_PARTICIPANT)
		{
			InputConnectionHandler ich = (InputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.ackReceived(StreamSendTask.CLOSEREQ, data);
			}
			else
			{
				System.out.println("InputStream not found (ackclosereq): "
						+ component + ", " + System.currentTimeMillis() + ", "
						+ conid);
			}
		}
		else if (type == StreamSendTask.CLOSE_INPUT_PARTICIPANT)
		{
			InputConnectionHandler ich = (InputConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.closeReceived(SUtil.bytesToInt((byte[]) data));
			}
			else
			{
				System.out.println("OutputStream not found (closeinput): "
						+ component + ", " + System.currentTimeMillis() + ", "
						+ conid);
			}
		}
		else if (type == StreamSendTask.ACKCLOSE_INPUT_INITIATOR)
		{
			OutputConnectionHandler ich = (OutputConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (ich != null)
			{
				ich.ackReceived(StreamSendTask.CLOSE, data);
			}
			else
			{
				System.out.println(
						"InputStream not found (ackclose): " + component + ", "
								+ System.currentTimeMillis() + ", " + conid);
			}
		}

		// Handle lease time update
		else if (type == StreamSendTask.ALIVE_INITIATOR)
		{
			// System.out.println("alive initiator");
			AbstractConnectionHandler con = (AbstractConnectionHandler) pcons
					.get(Integer.valueOf(conid));
			if (con != null)
			{
				con.setAliveTime(System.currentTimeMillis());
			}
			else
			{
				System.out.println("Stream not found (alive ini): " + component
						+ ", " + System.currentTimeMillis() + ", " + conid);
			}
		}
		else if (type == StreamSendTask.ALIVE_PARTICIPANT)
		{
			// System.out.println("alive particpant");
			AbstractConnectionHandler con = (AbstractConnectionHandler) icons
					.get(Integer.valueOf(conid));
			if (con != null)
			{
				con.setAliveTime(System.currentTimeMillis());
			}
			else
			{
				System.out.println("Stream not found (alive par): " + component
						+ ", " + System.currentTimeMillis() + ", " + conid);
			}
		}
	}

	/**
	 * Handle map messages, i.e. normal text messages.
	 */
	class MapDeliveryHandler implements ICommand<byte[]>
	{
		/**
		 * Execute the command.
		 */
		public void execute(final byte[] obj)
		{

			final List<Exception> errors = new ArrayList<Exception>();
			final IErrorReporter rep = strictcom ? null : new IErrorReporter()
			{
				public void exceptionOccurred(Exception e)
				{
					e.printStackTrace();
					errors.add(e);
				}
			};
			MessageEnvelope tmpenv = null;
			try
			{
				tmpenv = MapSendTask.decodeMessageEnvelope(obj,
						remotemarshalingconfig.getAllSerializers(),
						remotemarshalingconfig.getAllCodecs(), msgsrvcl, null);
			}
			catch (Exception e)
			{
				component.getLogger().warning("MessageService failed to decode envelope: " + obj);
			}
			final MessageEnvelope me = tmpenv;
			
			if (!errors.isEmpty())
			{
				logger.warning(
						"Ignored errors during message decoding: " + errors);
			}
			
			final String type = me.getTypeName();
			final IComponentIdentifier[] receivers = me.getReceivers();
			// System.out.println("Received message:
			// "+SUtil.arrayToString(receivers));
			final MessageType messagetype = getMessageType(type);

			// Content decoding works as follows:
			// Find correct classloader for each receiver by
			// a) if message contains rid ask library service for classloader
			// (global rids are resolved with maven, locals possibly with peer
			// to peer jar transfer)
			// b) if library service could not resolve rid or message does not
			// contain rid the receiver classloader can be used

			final Future<Void> ret = new Future<Void>();
			// todo: what to do with exception here?
			// ret.addResultListener(new IResultListener<Void>()
			// {
			// public void resultAvailable(Void result)
			// {
			// }
			// public void exceptionOccurred(Exception exception)
			// {
			// exception.printStackTrace();
			// }
			// });
			getRIDClassLoader(me.getRid(), me.getRealRec(),	me.getReceivers()).addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
			{
				public void customResultAvailable(final ClassLoader classloader)
				{
					@SuppressWarnings("unchecked")
//					final Map<String, Object> msg = (Map<String, Object>) MapSendTask.decodeMessage(obj, remotemarshalingconfig.getPostprocessors(),remotemarshalingconfig.getAllSerializers(),remotemarshalingconfig.getAllCodecs(),classloader, rep);
					Map<String, Object> tempmsg = null;
					try
					{
						tempmsg = (Map<String, Object>) MapSendTask.decodeMessage(me, remotemarshalingconfig.getPostprocessors(),remotemarshalingconfig.getAllSerializers(),remotemarshalingconfig.getAllCodecs(),classloader, rep);
					}
					catch (Exception e)
					{
						component.getLogger().warning("MessageService failed to decode message: " + me.getReceivers() + " " + me.getRealRec() + " " + me.getRid());
					}
					final Map<String, Object> msg = tempmsg; 
					if (!msg.containsKey(SFipa.RECEIVERS))
						msg.put(SFipa.RECEIVERS, me.getReceivers());
					if (me.getRealRec() != null)
						msg.put(SFipa.X_RECEIVER, me.getRealRec());
					if (me.getRid() != null)
						msg.put(SFipa.X_RID, me.getRid());
					
					// Announce receiver to message awareness
					ITransportComponentIdentifier sender = (ITransportComponentIdentifier) msg
							.get(messagetype
									.getSenderIdentifier());
					announceComponentIdentifier(sender);
					addrservice.addPlatformAddresses(sender);

					SServiceProvider.getService(component,IComponentManagementService.class,RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
					{
						public void customResultAvailable(
								IComponentManagementService cms)
						{
							deliverToAllReceivers(receivers,cms,classloader,msg, logger,messagetype).addResultListener(new DelegationResultListener<Void>(ret)
							{
								public void customResultAvailable(Void result)
								{
									IFilter[] fils;
									IMessageListener[] lis;
									synchronized (this)
									{
										fils = listeners == null
												? null
												: (IFilter[]) listeners
														.values()
														.toArray(
																new IFilter[listeners
																		.size()]);
										lis = listeners == null
												? null
												: (IMessageListener[]) listeners
														.keySet()
														.toArray(
																new IMessageListener[listeners
																		.size()]);
									}

									if (lis != null)
									{
										IMessageAdapter message = new DefaultMessageAdapter(msg, messagetype);
										for (int i = 0; i < lis.length; i++)
										{
											IMessageListener li = (IMessageListener) lis[i];
											boolean match = false;
											try
											{
												match = fils[i] == null || fils[i].filter(message);
											}
											catch (Exception e)
											{
												logger.warning("Filter threw exception: " + fils[i] + ", " + e);
											}
											if (match)
											{
												try
												{
													li.messageReceived(message);
												}
												catch (Exception e)
												{
													logger.warning("Listener threw exception: " + li + ", " + e);
												}
											}
										}
									}
								}
							});
						}
					});
				}
			});
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> deliverToAllReceivers(
			final IComponentIdentifier[] receivers,
			final IComponentManagementService cms,
			final ClassLoader classloader, final Map msg, final Logger logger,
			final MessageType messagetype)
	{
		final Future<Void> ret = new Future<Void>();

		final int[] i = new int[1];
		deliverToReceiver(receivers, i[0], cms, classloader, msg, logger,
				messagetype).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						if (++i[0] < receivers.length)
						{
							deliverToReceiver(receivers, i[0], cms, classloader,
									msg, logger, messagetype)
											.addResultListener(this);
						}
						else
						{
							ret.setResult(null);
						}
					}

					public void exceptionOccurred(Exception exception)
					{
						resultAvailable(null);							
					}
				});

		return ret;
	}

	/**
	 * 
	 */
	protected IFuture<Void> deliverToReceiver(
			final IComponentIdentifier[] receivers, final int i,
			final IComponentManagementService cms,
			final ClassLoader classloader, final Map<String, Object> msg,
			final Logger logger, final MessageType messagetype)
	{
		// System.out.println("dtr: "+SUtil.arrayToString(receivers)+" "+i+"
		// "+classloader);

		final Future<Void> ret = new Future<Void>();

		final IComponentIdentifier receiver = receivers[i];

		// Copy message for state isolation.
		final Map<String, Object> fmessage = new HashMap<String, Object>(msg);

		// Perform decoding on component thread (necessary for rms)
		cms.getExternalAccess(receiver).addResultListener(
				new ExceptionDelegationResultListener<IExternalAccess, Void>(
						ret)
				{
					public void customResultAvailable(IExternalAccess exta)
					{
						exta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								IInternalMessageFeature com = (IInternalMessageFeature) ia
										.getComponentFeature(
												IMessageFeature.class);

								if (com != null)
								{
									ClassLoader cl = classloader != null
											? classloader : ia.getClassLoader();

									try
									{
										com.messageArrived(
												new DefaultMessageAdapter(
														fmessage, messagetype));
									}
									catch (Exception e)
									{
										logger.warning(
												"Message could not be delivered to local receiver: "
														+ receiver + ", "
														+ fmessage
																.get(messagetype
																		.getIdIdentifier())
														+ ", " + e);

										// todo: notify sender that message
										// could not be delivered!
										// Problem: there is no connection back
										// to the sender, so that
										// the only chance is sending a separate
										// failure message.
									}
								}
								else
								{
									logger.warning(
											"Message could not be delivered to local receiver (no communication feature): "
													+ receiver + ", "
													+ fmessage.get(messagetype
															.getIdIdentifier()));
								}

								return IFuture.DONE;
							}
						}).addResultListener(component
								.getComponentFeature(IExecutionFeature.class)
								.createResultListener(
										new DelegationResultListener<Void>(
												ret)));
					}

					public void exceptionOccurred(Exception exception)
					{
						logger.warning(
								"Message could not be delivered to local receiver: "
										+ receiver + ", "
										+ msg.get(messagetype.getIdIdentifier())
										+ ", " + exception);
						ret.setResult(null);
					}
				});

		return ret;
	}

	/** The (real) system clock timer. */
	protected volatile Timer timer;

	/**
	 * Wait for a time delay on the (real) system clock.
	 */
	public TimerTask waitForRealDelay(long delay, final IComponentStep<?> step)
	{
		if (timer == null)
		{
			synchronized (this)
			{
				if (timer == null)
				{
					timer = new Timer(
							component.getComponentIdentifier().getName()
									+ ".message.timer",
							true);
				}
			}
		}

		TimerTask ret = new TimerTask()
		{
			public void run()
			{
				try
				{
					getComponent().scheduleStep(step);
				}
				catch (ComponentTerminatedException cte)
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
	 * Create local input connection side after receiving a remote init output
	 * message. May be called multiple times and does nothing, if connection
	 * already exists.
	 */
	protected IInputConnection initInputConnection(final int conid,
			final ITransportComponentIdentifier initiator,
			final ITransportComponentIdentifier participant,
			final Map<String, Object> nonfunc)
	{
		boolean created;
		InputConnectionHandler ich = null;
		InputConnection con = null;
		synchronized (this)
		{
			ich = (InputConnectionHandler) pcons.get(Integer.valueOf(conid));
			if (ich == null)
			{
				ich = new InputConnectionHandler(MessageService.this, nonfunc);
				con = new InputConnection(initiator, participant, conid, false,
						ich);
				pcons.put(Integer.valueOf(conid), ich);
				// System.out.println("created for: "+conid+" "+pcons+"
				// "+getComponent().getComponentIdentifier());
				created = true;
			}
			else
			{
				con = ich.getInputConnection();
				created = false;
			}
		}

		if (created)
		{
			ich.initReceived();

			final InputConnection fcon = con;
			final Future<Void> ret = new Future<Void>();
			SServiceProvider
					.getService(component, IComponentManagementService.class,
							RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(
							new ExceptionDelegationResultListener<IComponentManagementService, Void>(
									ret)
							{
								public void customResultAvailable(
										IComponentManagementService cms)
								{
									cms.getExternalAccess(participant)
											.addResultListener(
													new ExceptionDelegationResultListener<IExternalAccess, Void>(
															ret)
													{
														public void customResultAvailable(
																IExternalAccess ea)
														{
															ea.scheduleStep(
																	new IComponentStep<Void>()
																	{
																		public IFuture<Void> execute(
																				IInternalAccess ia)
																		{
																			IInternalMessageFeature com = (IInternalMessageFeature) ia
																					.getComponentFeature(
																							IMessageFeature.class);
																			if (com != null)
																			{
																				com.streamArrived(
																						fcon);
																			}
																			else
																			{
																				ia.getLogger()
																						.warning(
																								"Component received stream, but ha no communication feature: "
																										+ fcon);
																			}

																			return IFuture.DONE;
																		}
																	});
														}
													});
								}
							});
		}
		else
		{
			// If connection arrives late
			if (nonfunc != null)
				ich.setNonFunctionalProperties(nonfunc);
		}

		return con;
	}

	/**
	 * Create local output connection side after receiving a remote init input
	 * message. May be called multiple times and does nothing, if connection
	 * already exists.
	 */
	protected IOutputConnection initOutputConnection(final int conid,
			final ITransportComponentIdentifier initiator,
			final ITransportComponentIdentifier participant,
			final Map<String, Object> nonfunc)
	{
		boolean created;
		OutputConnectionHandler och;
		OutputConnection con = null;
		synchronized (this)
		{
			och = (OutputConnectionHandler) pcons.get(Integer.valueOf(conid));
			if (och == null)
			{
				och = new OutputConnectionHandler(MessageService.this, nonfunc);
				con = new OutputConnection(initiator, participant, conid, false,
						och);
				pcons.put(Integer.valueOf(conid), och);
				// System.out.println("created: "+con.hashCode());
				created = true;
			}
			else
			{
				con = och.getOutputConnection();
				created = false;
			}
		}

		if (created)
		{
			och.initReceived();

			final OutputConnection fcon = con;
			final Future<Void> ret = new Future<Void>();
			SServiceProvider
					.getService(component, IComponentManagementService.class,
							RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(
							new ExceptionDelegationResultListener<IComponentManagementService, Void>(
									ret)
							{
								public void customResultAvailable(
										IComponentManagementService cms)
								{
									cms.getExternalAccess(participant)
											.addResultListener(
													new ExceptionDelegationResultListener<IExternalAccess, Void>(
															ret)
													{
														public void customResultAvailable(
																IExternalAccess ea)
														{
															ea.scheduleStep(
																	new IComponentStep<Void>()
																	{
																		public IFuture<Void> execute(
																				IInternalAccess ia)
																		{
																			IInternalMessageFeature com = (IInternalMessageFeature) ia
																					.getComponentFeature(
																							IMessageFeature.class);
																			if (com != null)
																			{
																				com.streamArrived(
																						fcon);
																			}
																			else
																			{
																				ia.getLogger()
																						.warning(
																								"Component received stream, but ha no communication feature: "
																										+ fcon);
																			}

																			return IFuture.DONE;
																		}
																	});
														}
													});
								}
							});
		}
		else
		{
			// If connection arrives late
			if (nonfunc != null)
				och.setNonFunctionalProperties(nonfunc);
		}

		return con;
	}

	/**
	 * 
	 */
	public static void main(String[] args)
	{
		boolean doread = false;
		int max = 100000;

		if (args.length > 0)
			doread = args[0].equals("read");

		int len = 10000;

		long start = System.currentTimeMillis();

		if (doread)
		{
			ServerSocket ss = null;
			try
			{
				ss = new ServerSocket(44444);
				Socket s = ss.accept();
				InputStream is = new BufferedInputStream(s.getInputStream());

				byte[] read = new byte[len];
				int packcnt = 0;
				for (; packcnt < max; packcnt++)
				{
					int cnt = 0;
					while (cnt < len)
					{
						int bytes_read = is.read(read, cnt, len - cnt);
						if (bytes_read == -1)
							throw new IOException("Stream closed");
						cnt += bytes_read;
					}
					System.out.println("read packet: " + packcnt);
				}

				is.close();
				s.close();

				long end = System.currentTimeMillis();
				System.out.println("Needed: " + (end - start));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					ss.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				byte[] write = new byte[len];
				for (int i = 0; i < write.length; i++)
				{
					write[i] = (byte) (i % 10);
				}

				Socket s = new Socket(InetAddress.getByName("134.100.11.230"),
						44444);
				OutputStream os = new BufferedOutputStream(s.getOutputStream());

				for (int i = 0; i < max; i++)
				{
					os.write(write);
					os.flush();
					System.out.println("wrote packet: " + i);
				}

				os.close();
				s.close();

				long end = System.currentTimeMillis();
				System.out.println("Needed: " + (end - start));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	long mwstime;
	IMessageAwarenessService mws;

	/**
	 * Announce a component identifier to message awareness and address service.
	 */
	protected void announceComponentIdentifier(
			final ITransportComponentIdentifier cid)
	{
		// Search for mws only every 5 seconds.
		if (System.currentTimeMillis() - mwstime > 5000)
		{
			mwstime = System.currentTimeMillis();
			getComponent().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					SServiceProvider
							.getService(ia, IMessageAwarenessService.class,
									RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(ia
									.getComponentFeature(
											IExecutionFeature.class)
									.createResultListener(
											new IResultListener<IMessageAwarenessService>()
											{
												public void resultAvailable(
														IMessageAwarenessService result)
												{
													mws = result;
													announceComponentIdentifier(
															cid);
												}

												public void exceptionOccurred(
														Exception exception)
												{
													// ignore if message
													// awareness service not
													// found
												}
											}));

					return IFuture.DONE;
				}
			});
		}
		else if (mws != null)
		{
			mws.announceComponentIdentifier(cid);
		}
	}
}
