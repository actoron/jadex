package jadex.platform.service.message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.ContentException;
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
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.EncodingContext;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.bridge.service.types.message.IMessageListener;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
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
import jadex.platform.service.message.transport.codecs.CodecFactory;
import jadex.platform.service.remote.RemoteMethodInvocationHandler;


/**
 *  The Message service serves several message-oriented purposes: a) sending and
 *  delivering messages by using transports b) management of transports
 *  (add/remove)
 *  
 *  The message service performs sending and delivering messages by separate actions
 *  that are individually executed on the execution service, i.e. they are delivered
 *  synchronous or asynchronous depending on the execution service mode.
 */
public class MessageService extends BasicService implements IMessageService
{
	//-------- constants --------
	
	/** The default codecs. */
    public static IContentCodec[] CODECS = !SReflect.isAndroid() ? new IContentCodec[]
    {
        new jadex.platform.service.message.contentcodecs.JavaXMLContentCodec(),
        new jadex.platform.service.message.contentcodecs.JadexXMLContentCodec(),
        new jadex.platform.service.message.contentcodecs.NuggetsXMLContentCodec(),
		new jadex.platform.service.message.contentcodecs.JadexBinaryContentCodec()
    }
    : SUtil.androidUtils().hasXmlSupport()? 
    	new IContentCodec[]
	    {
	    		new jadex.platform.service.message.contentcodecs.JadexBinaryContentCodec()
	    }
        :new IContentCodec[]
        {
        	new jadex.platform.service.message.contentcodecs.JadexBinaryContentCodec(),
        	new jadex.platform.service.message.contentcodecs.JadexXMLContentCodec()
        };

	//-------- attributes --------

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
	protected Logger	logger;
	
	/** The listeners (listener->filter). */
	protected Map<IMessageListener, IFilter> listeners;
	
	/** The cashed clock service. */
	protected IClockService	clockservice;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The execution service. */
	protected IExecutionService exeservice;

//	/** The address service. */
	protected ITransportAddressService addrservice;
	protected TransportAddressBook taddresses;
	
	/** The awareness service. */
//	protected IAwarenessManagementService ams;
	
	/** Release date cache */
	protected LRU<IComponentIdentifier, Date> releasedatecache = new LRU<IComponentIdentifier, Date>(100);
	
	/** The cms. */
	protected IComponentManagementService cms;
	
	/** The class loader of the message service (only for envelope en/decoding, content is handled by receiver class loader). */
	protected ClassLoader classloader;
	
	/** The target managers (platform id->manager). */
	protected LRU<IComponentIdentifier, SendManager> managers;
		
	/** The codec factory for messages. */
	protected CodecFactory codecfactory;
	
	/** The delivery handler map. */
	protected Map<Byte, ICommand> deliveryhandlers;
	
	
	/** The initiator connections. */
	protected Map<Integer, AbstractConnectionHandler> icons;

	/** The participant connections. */
	protected Map<Integer, AbstractConnectionHandler> pcons;

	
	/** The content codecs. */
	protected List contentcodecs;
	
	/** The default content language (if not specified). */
	protected String deflanguage;
	
	/** The map of content codec infos. */
	protected Map<IComponentIdentifier, Map<Class<?>, Object[]>> contentcodecinfos;
	
	/** Enable strict communication (i.e. fail on recoverable decoding errors). */
	protected boolean	strictcom;
	
	//-------- constructors --------

	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(IInternalAccess component, Logger logger, ITransport[] transports, 
		MessageType[] messagetypes)
	{
		this(component, logger, transports, messagetypes, null, null, null, false);
	}
	
	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(IInternalAccess component, Logger logger, ITransport[] transports, 
		MessageType[] messagetypes, IContentCodec[] contentcodecs, String deflanguage, CodecFactory codecfactory, boolean strictcom)
	{
		super(component.getComponentIdentifier(), IMessageService.class, null);
		
		// Register communication classes with aliases
		STransformation.registerClass(MessageEnvelope.class);
		STransformation.registerClass(AckInfo.class);
		STransformation.registerClass(InitInfo.class);

		this.strictcom	= strictcom;
		this.component = component;
		this.transports = SCollection.createArrayList();
		for(int i=0; i<transports.length; i++)
		{
			// Allow nulls to make it easier to exclude transports via platform configuration.
			if(transports[i]!=null)
				this.transports.add(transports[i]);
		}
		this.messagetypes	= SCollection.createHashMap();
		for(int i=0; i<messagetypes.length; i++)
			this.messagetypes.put(messagetypes[i].getName(), messagetypes[i]);		
		this.delivermsg = new DeliverMessage();
		this.logger = logger;
		
		this.managers = new LRU<IComponentIdentifier, SendManager>(800);
		if(contentcodecs!=null)
		{
			for(int i=0; i<contentcodecs.length; i++)
			{
				addContentCodec(contentcodecs[i]);
			}
		}
		this.codecfactory = codecfactory!=null? codecfactory: new CodecFactory();
		
		// The default language for content.
		this.deflanguage = deflanguage==null? SFipa.JADEX_XML: deflanguage;
		
		this.deliveryhandlers = new HashMap<Byte, ICommand>();
		deliveryhandlers.put(MapSendTask.MESSAGE_TYPE_MAP, new MapDeliveryHandler());
		deliveryhandlers.put(StreamSendTask.MESSAGE_TYPE_STREAM, new StreamDeliveryHandler());
		
		this.icons = Collections.synchronizedMap(new HashMap<Integer, AbstractConnectionHandler>());
		this.pcons = Collections.synchronizedMap(new HashMap<Integer, AbstractConnectionHandler>());		
	}
	
	//-------- interface methods --------

	/**
	 * 
	 */
	public IInputConnection getParticipantInputConnection(int conid, ITransportComponentIdentifier initiator, ITransportComponentIdentifier participant, Map<String, Object> nonfunc)
	{
		return initInputConnection(conid, initiator, participant, nonfunc);
	}
	
	/**
	 * 
	 */
	public IOutputConnection getParticipantOutputConnection(int conid, ITransportComponentIdentifier initiator, ITransportComponentIdentifier participant, Map<String, Object> nonfunc)
	{
		return initOutputConnection(conid, initiator, participant, nonfunc);
	}
	
	/**
	 *  Create a virtual output connection.
	 */
	public OutputConnection internalCreateOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		UUID uuconid = UUID.randomUUID();
		int conid = uuconid.hashCode();
		OutputConnectionHandler och = new OutputConnectionHandler(this, nonfunc);
		icons.put(conid, och);
		OutputConnection con = new OutputConnection(TransportAddressService.getTransportComponentIdentifier(sender, taddresses), 
			TransportAddressService.getTransportComponentIdentifier(receiver, taddresses), conid, true, och);
//		System.out.println("created ocon: "+component+", "+System.currentTimeMillis()+", "+och.getConnectionId());
		return con;
	}
	
	/**
	 *  Create a virtual output connection.
	 */
	public IFuture<IOutputConnection> createOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		return new Future<IOutputConnection>(internalCreateOutputConnection(sender, receiver, nonfunc));
	}

	/**
	 *  Create a virtual input connection.
	 */
	public InputConnection internalCreateInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		UUID uuconid = UUID.randomUUID();
		int conid = uuconid.hashCode();
		InputConnectionHandler ich = new InputConnectionHandler(this, nonfunc);
		icons.put(conid, ich);
		InputConnection con = new InputConnection(TransportAddressService.getTransportComponentIdentifier(sender, taddresses), 
			TransportAddressService.getTransportComponentIdentifier(receiver, taddresses), conid, true, ich);
//		System.out.println("created icon: "+component+", "+System.currentTimeMillis()+", "+ich.getConnectionId());
		return con;
	}
	
	/**
	 *  Create a virtual input connection.
	 */
	public IFuture<IInputConnection> createInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		return new Future<IInputConnection>(internalCreateInputConnection(sender, receiver, nonfunc));
	}

	/**
	 *  Send a message.
	 *  @param message The message as key value pairs.
	 *  @param msgtype The message type.
	 *  @param sender The sender component identifier.
	 *  @param rid The resource identifier used by the sending component (i.e. corresponding to classes of objects in the message map).
	 *  @param realrec The real receiver if different from the message receiver (e.g. message to rms encapsulating service call to other component).
	 *  @param codecids The codecs to use for encoding (if different from default).
	 *  @return Future that indicates an exception when messages could not be delivered to components. 
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> origmsg, final MessageType type, 
		final IComponentIdentifier osender, final IResourceIdentifier rid, 
		final IComponentIdentifier realrec, final byte[] codecids)//, final Map<String, Object> nonfunc)
	{
		final Future<Void> ret = new Future<Void>();
		
//		IResultListener<DiscoveryInfo> disclistener = new IResultListener<DiscoveryInfo>()
//		{
//			public void resultAvailable(final DiscoveryInfo discoveryinfo)
//			{
//				
//				
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				resultAvailable(null);
//			}
//		};
//		
//		if (ams != null)
//		{
//			ams.getPlatformInfo(realrec.getRoot()).addResultListener(disclistener);
//		}
//		else
//		{
//			disclistener.resultAvailable(null);
//		}
		
		final IComponentIdentifier loc = IComponentIdentifier.LOCAL.get();
		
//		System.err.println("send msg2: "+osender+" "+origmsg.get(SFipa.CONTENT));
		final Map<String, Object> msg = new HashMap<String, Object>(origmsg);
		
		final ITransportComponentIdentifier sender = TransportAddressService.getTransportComponentIdentifier(osender, taddresses);
		
//		final IComponentIdentifier sender = internalUpdateComponentIdentifier(osender);
//		addrservice.getTransportComponentIdentifier(osender).addResultListener(new ExceptionDelegationResultListener<ITransportComponentIdentifier, Void>(ret)
//		{
//			public void customResultAvailable(final ITransportComponentIdentifier sender)
//			{
//				System.out.println("on0: "+IComponentIdentifier.CALLER.get()+" "+IComponentIdentifier.LOCAL.get());

		libservice.getClassLoader(rid)
			.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
		{
			public void customResultAvailable(final ClassLoader cl)
			{
//				System.err.println("send msg3: "+sender+" "+msg.get(SFipa.CONTENT));
//				System.out.println("on1: "+IComponentIdentifier.CALLER.get()+" "+IComponentIdentifier.LOCAL.get());
				
				if(loc!=null && IComponentIdentifier.LOCAL.get()!=null && !loc.equals(IComponentIdentifier.LOCAL.get()))
				{
					logger.severe("Component thread backswitch failed. Should be: "+loc+" but is "+IComponentIdentifier.LOCAL.get());
				}
					
//						IComponentIdentifier sender = adapter.getComponentIdentifier();
				if(sender==null)
				{
					ret.setException(new RuntimeException("Sender must not be null: "+msg));
					return;
				}
			
				// Replace own component identifiers.
				// Now done just before send
//						String[] params = type.getParameterNames();
//						for(int i=0; i<params.length; i++)
//						{
//							Object o = msg.get(params[i]);
//							if(o instanceof IComponentIdentifier)
//							{
//								msg.put(params[i], updateComponentIdentifier((IComponentIdentifier)o));
//							}
//						}
//						String[] paramsets = type.getParameterSetNames();
//						for(int i=0; i<paramsets.length; i++)
//						{
//							Object o = msg.get(paramsets[i]);
//							
//							if(SReflect.isIterable(o))
//							{
//								List rep = new ArrayList();
//								for(Iterator it=SReflect.getIterator(o); it.hasNext(); )
//								{
//									Object item = it.next();
//									if(item instanceof IComponentIdentifier)
//									{
//										rep.add(updateComponentIdentifier((IComponentIdentifier)item));
//									}
//									else
//									{
//										rep.add(item);
//									}
//								}
//								msg.put(paramsets[i], rep);
//							}
//							else if(o instanceof IComponentIdentifier)
//							{
//								msg.put(paramsets[i], updateComponentIdentifier((IComponentIdentifier)o));
//							}
//						}
				
				// Automatically add optional meta information.
				String senid = type.getSenderIdentifier();
//				if(msg.get(senid)==null)
				msg.put(senid, sender);
				
				final String idid = type.getIdIdentifier();
				if(msg.get(idid)==null)
					msg.put(idid, SUtil.createUniqueId(sender.getLocalName()));

				final String sd = type.getTimestampIdentifier();
				if(msg.get(sd)==null)
				{
					msg.put(sd, ""+clockservice.getTime());
				}
				
				final String ridid = type.getResourceIdIdentifier();
				if(msg.get(ridid)==null && rid!=null && rid.getGlobalIdentifier()!=null && !ResourceIdentifier.isJadexRid(rid))
				{
					msg.put(ridid, rid);
				}
				
//						final String realrecid = type.getRealReceiverIdentifier();
//						if(msg.get(realrecid)==null && realrec!=null)
//						{
//							msg.put(realrecid, realrec);
//						}
				
				// Check receivers.
				Object tmp = msg.get(type.getReceiverIdentifier());
				if(tmp==null || SReflect.isIterable(tmp) &&	!SReflect.getIterator(tmp).hasNext())
				{
					ret.setException(new RuntimeException("Receivers must not be empty: "+msg));
					return;
				}
//						cms.getExternalAccess(sender).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//								{
//									public void customResultAvailable(IExternalAccess exta)
//									{
////										System.out.println("msgservice calling doSendMessage()");
////										System.out.println("on2: "+IComponentIdentifier.CALLER.get()+" "+IComponentIdentifier.LOCAL.get());
//										
////										System.err.println("send msg4: "+sender+" "+msg.get(SFipa.CONTENT));
//										IEncodingContext enccont = new EncodingContext(new Date());
//										doSendMessage(msg, type, exta, cl, ret, codecids, enccont);
//									}
//									public void exceptionOccurred(Exception exception)
//									{
//										super.exceptionOccurred(exception);
//									}
//								});
				
//						System.out.println("Getting final release date: " + msg);
				getReleaseDate(type, msg).addResultListener(new ExceptionDelegationResultListener<Date, Void>(ret)
				{
					public void customResultAvailable(Date result)
					{
//								System.out.println("Got final release date: " + String.valueOf(result));
						final Date freleasedate = result;
//								final Date freleasedate = null;
						
						// External access of sender required for content encoding etc.
//								SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
//									.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//								{
//									public void customResultAvailable(IComponentManagementService cms)
//									{
//										String	smsg	= "MessageService.sendMessage("+msg+")";
//										ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//										next.setProperty("debugsource", smsg);
								
								cms.getExternalAccess(sender).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
								{
									public void customResultAvailable(IExternalAccess exta)
									{
//												System.out.println("msgservice calling doSendMessage()");
//												System.out.println("on2: "+IComponentIdentifier.CALLER.get()+" "+IComponentIdentifier.LOCAL.get());
										
//												System.err.println("send msg4: "+sender+" "+msg.get(SFipa.CONTENT));
										IEncodingContext enccont = new EncodingContext(freleasedate);
										doSendMessage(msg, type, exta, cl, ret, codecids, enccont);
									}
									public void exceptionOccurred(Exception exception)
									{
										super.exceptionOccurred(exception);
									}
								});
//									}
//								});
					}
				});
//					}
//				});
			}
		});
		
		
//	

//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex msg send: "+exception);
//			}
//		});
		
		return ret;
	}

	/**
	 *  Extracted method to be callable from listener.
	 */
	protected void doSendMessage(Map<String, Object> msg, final MessageType type, IExternalAccess comp, 
		final ClassLoader cl, Future<Void> ret, byte[] codecids, final IEncodingContext enccontext)
	{
		final Map<String, Object> msgcopy	= new HashMap<String, Object>(msg);

		// Conversion via platform specific codecs
		// Hack?! Preprocess content to enhance component identifiers.
		IContentCodec[] compcodecs = getContentCodecs(comp.getModel(), cl);
		List<ITraverseProcessor> procs = Traverser.getDefaultProcessors();
		procs.add(1, new ITraverseProcessor()
		{
			public Object process(Object object, Type type,
				List<ITraverseProcessor> processors, Traverser traverser,
				Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
			{
				return TransportAddressService.getTransportComponentIdentifier((ITransportComponentIdentifier)object, taddresses);
//				return internalUpdateComponentIdentifier((ITransportComponentIdentifier)object);
			}
			
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				return object instanceof ITransportComponentIdentifier;
			}
		});
		
		// Ignore service proxies.
		procs.add(1, new ITraverseProcessor()
		{
			public Object process(Object object, Type type,
				List<ITraverseProcessor> processors, Traverser traverser,
				Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
			{
				return object;
			}
			
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				Class<?> clazz = SReflect.getClass(type);
				return Proxy.isProxyClass(clazz) &&
					(Proxy.getInvocationHandler(object) instanceof BasicServiceInvocationHandler
						|| Proxy.getInvocationHandler(object) instanceof RemoteMethodInvocationHandler);
			}
		});
		
		String[] names = (String[])msgcopy.keySet().toArray(new String[0]);
		for(int i=0; i<names.length; i++)
		{
			String	name	= names[i];
			Object	value	= msgcopy.get(name);
			value = Traverser.traverseObject(value, procs, false, null);
			msgcopy.put(name, value);
			
			IContentCodec codec = type.findContentCodec(compcodecs, msgcopy, name);
			if(codec==null)
				codec = type.findContentCodec(getContentCodecs(), msgcopy, name);
			
			if(codec!=null)
			{
				msgcopy.put(name, codec.encode(value, cl, getContentCodecInfo(comp.getComponentIdentifier()), enccontext));
			}
			else if(value!=null && !((value instanceof String) || (value instanceof byte[])) 
				&& !(name.equals(type.getSenderIdentifier()) || name.equals(type.getReceiverIdentifier())
				|| name.equals(type.getResourceIdIdentifier()) || name.equals(type.getNonFunctionalPropertiesIdentifier())
				|| name.equals(type.getRealReceiverIdentifier())))
			{	
				// HACK!!!
				if(SFipa.FIPA_MESSAGE_TYPE.equals(type) && !msgcopy.containsKey(SFipa.LANGUAGE))
				{
					Properties props = new Properties();
					props.put(SFipa.LANGUAGE, deflanguage);
					IContentCodec[] codecs = getContentCodecs();
					for(int j=0; j<codecs.length; j++)
					{
						if(codecs[j].match(props))
						{
							codec = codecs[j];
							msgcopy.put(SFipa.LANGUAGE, deflanguage);
							break;
						}
					}
				}	
				
				if(codec!=null)
				{
					msgcopy.put(name, codec.encode(value, cl, getContentCodecInfo(comp.getComponentIdentifier()), enccontext));
				}
				else if(!SFipa.JADEX_RAW.equals(msgcopy.get(SFipa.LANGUAGE)))
				{
					ret.setException(new ContentException("No content codec found for: "+name+", "+msgcopy));
					return;
				}
			}
		}
		
		IComponentIdentifier sender = (IComponentIdentifier)msgcopy.get(type.getSenderIdentifier());
//		if(sender.getAddresses()==null || sender.getAddresses().length==0)
//			System.out.println("schrott2");
		
		IFilter[] fils;
		IMessageListener[] lis;
		synchronized(this)
		{
			fils = listeners==null? null: listeners.values().toArray(new IFilter[listeners.size()]);
			lis = listeners==null? null: listeners.keySet().toArray(new IMessageListener[listeners.size()]);
		}
		
		if(lis!=null)
		{
			// Hack?!
			IMessageAdapter msgadapter = new DefaultMessageAdapter(msgcopy, type);
			for(int i=0; i<lis.length; i++)
			{
				IMessageListener li = (IMessageListener)lis[i];
				boolean	match	= false;
				try
				{
					match	= fils[i]==null || fils[i].filter(msgadapter);
				}
				catch(Exception e)
				{
					logger.warning("Filter threw exception: "+fils[i]+", "+e);
				}
				if(match)
				{
					try
					{
						li.messageSent(msgadapter);
					}
					catch(Exception e)
					{
						logger.warning("Listener threw exception: "+li+", "+e);
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
		Object tmp	= msgcopy.get(recid);
		if(SReflect.isIterable(tmp))
		{
			for(Iterator<?> it = SReflect.getIterator(tmp); it.hasNext(); )
			{
				IComponentIdentifier cid = (IComponentIdentifier)it.next();
				ITransportComponentIdentifier tcid = TransportAddressService.getTransportComponentIdentifier(cid, taddresses);
				SendManager sm = getSendManager(tcid); 
				managers.add(sm, tcid);
			}
		}
		else
		{
			IComponentIdentifier cid = (IComponentIdentifier)tmp;
			ITransportComponentIdentifier tcid = TransportAddressService.getTransportComponentIdentifier(cid, taddresses);
			SendManager sm = getSendManager(tcid); 
			managers.add(sm, tcid);
		}
		
		byte[] cids	= codecids;
		if(cids==null || cids.length==0)
			cids = codecfactory.getDefaultCodecIds();
		final ICodec[] codecs = getMessageCodecs(cids);
//		ICodec[] codecs = new ICodec[cids.length];
//		for(int i=0; i<codecs.length; i++)
//		{
//			codecs[i] = codecfactory.getCodec(cids[i]);
//		}
		
		final CounterResultListener<Void> crl = new CounterResultListener<Void>(managers.size(), false, new DelegationResultListener<Void>(ret));
		for(Iterator<?> it=managers.keySet().iterator(); it.hasNext();)
		{
			final SendManager tm = (SendManager)it.next();
			ITransportComponentIdentifier[] recs = managers.getCollection(tm).toArray(new ITransportComponentIdentifier[0]);
			MapSendTask task = new MapSendTask(msgcopy, type, recs, getTransports(), codecs, cl, enccontext);
			tm.addMessage(task).addResultListener(crl);
			
//			addrservice.getTransportComponentIdentifiers(recs).addResultListener(new IResultListener<ITransportComponentIdentifier[]>()
//			{
//				public void resultAvailable(ITransportComponentIdentifier[] trecs)
//				{
//					MapSendTask task = new MapSendTask(msgcopy, type, trecs, getTransports(), codecs, cl, enccontext);
//					tm.addMessage(task).addResultListener(crl);
////					task.getSendManager().addMessage(task).addResultListener(crl);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//				}
//			});
		}
		
//		sendmsg.addMessage(msgcopy, type, receivers, ret);
	}
	
	/**
	 *  Get array of message codecs for codec ids.
	 */
	public ICodec[] getMessageCodecs(byte[] codecids)
	{
		ICodec[] codecs = new ICodec[codecids.length];
		for(int i=0; i<codecs.length; i++)
		{
			codecs[i] = codecfactory.getCodec(codecids[i]);
		}
		return codecs;
	}
	
	/**
	 *  Get content codecs.
	 *  @return The content codecs.
	 */
	public IContentCodec[] getContentCodecs()
	{
		return contentcodecs==null? CODECS: (IContentCodec[])contentcodecs.toArray(new IContentCodec[contentcodecs.size()]);
	}
	
	/**
	 *  Get a matching content codec.
	 *  @param props The properties.
	 *  @return The content codec.
	 */
	public IContentCodec[] getContentCodecs(IModelInfo model, ClassLoader cl)
	{
		List ret = null;
		Map	props	= model.getProperties();
		if(props!=null)
		{
			for(Iterator it=props.keySet().iterator(); it.hasNext();)
			{
				String name = (String)it.next();
				if(name.startsWith("contentcodec."))
				{
					if(ret==null)
						ret	= new ArrayList();
					ret.add(model.getProperty(name, cl));
				}
			}
		}

		return ret!=null? (IContentCodec[])ret.toArray(new IContentCodec[ret.size()]): null;
	}
	
//	/**
//	 *  Get a matching content codec.
//	 *  @param props The properties.
//	 *  @return The content codec.
//	 */
//	public Map<Class<?>, Object[]> getContentCodecInfo(IModelInfo model, ClassLoader cl)
//	{
//		Map<Class<?>, Object[]> ret = null;
//		Map	props	= model.getProperties();
//		if(props!=null)
//		{
//			for(Iterator it=props.keySet().iterator(); ret==null && it.hasNext();)
//			{
//				String name = (String)it.next();
//				if(name.startsWith("contentcodecinfo"))
//				{
//					ret = (Map<Class<?>, Object[]>)model.getProperty(name, cl);
//				}
//			}
//		}
//
//		return ret;
//	}
	
	/**
	 *  Get a matching content codec.
	 *  @param props The properties.
	 */
	// todo: called from rms, hack :-(
	public void setContentCodecInfo(IComponentIdentifier cid, Map<Class<?>, Object[]> info)
	{
		if(contentcodecinfos==null)
			contentcodecinfos = Collections.synchronizedMap(new HashMap<IComponentIdentifier, Map<Class<?>, Object[]>>());
		contentcodecinfos.put(cid, info);
	}
	
	/**
	 *  Get a matching content codec.
	 *  @param props The properties.
	 *  @return The content codec.
	 */
	public Map<Class<?>, Object[]> getContentCodecInfo(IComponentIdentifier cid)
	{
		Map<Class<?>, Object[]> ret = (Map<Class<?>, Object[]>)contentcodecinfos.get(cid);
//		if(ret==null)
//			System.out.println("sdffdsdf");
		return ret;
//		return (Map<Class<?>, Object[]>)contentcodecinfos.get(cid);
	}

	/**
	 *  Get the codec factory.
	 *  @return The codec factory.
	 */
	public CodecFactory getCodecFactory()
	{
		return codecfactory;
	}
	
//	/**
//	 *  Get the clock service.
//	 *  @return The clock service.
//	 */
//	public IClockService getClockService()
//	{
//		return clockservice;
//	}
	
//	/**
//	 *  Deliver a message to some components.
//	 */
//	public void deliverMessage(Map<String, Object> msg, String type, IComponentIdentifier[] receivers)
//	{
//		delivermsg.addMessage(new MessageEnvelope(msg, Arrays.asList(receivers), type));
//	}
	
	/**
	 *  Get the codecs with message codecs.
	 *  @return The codec factory.
	 */
	public IFuture<Map<Byte, ICodec>> getAllCodecs()
	{
		return new Future<Map<Byte, ICodec>>(getCodecFactory().getAllCodecs());
	}
	
	/**
	 *  Get the default codecs.
	 *  @return The default codecs.
	 */
	public IFuture<ICodec[]> getDefaultCodecs()
	{
		return new Future<ICodec[]>(getCodecFactory().getDefaultCodecs());
	}
	
	/**
	 *  Deliver a message to the intended components. Called from transports.
	 *  @param message The native message. 
	 *  (Synchronized because can be called from concurrently executing transports)
	 */
	public void deliverMessage(Object msg)
	{
		delivermsg.addMessage(msg);
	}
	
	/**
	 *  Adds a transport for this outbox.
	 *  @param transport The transport.
	 */
	public void addTransport(ITransport transport)
	{
		transports.add(transport);
		addresses = null;
	}

	/**
	 *  Remove a transport for the outbox.
	 *  @param transport The transport.
	 */
	public void removeTransport(ITransport transport)
	{
		transports.remove(transport);
		transport.shutdown();
		addresses = null;
	}

	/**
	 *  Moves a transport up or down.
	 *  @param up Move up?
	 *  @param transport The transport to move.
	 */
	public synchronized void changeTransportPosition(boolean up, ITransport transport)
	{
		int index = transports.indexOf(transport);
		if(up && index>0)
		{
			ITransport temptrans = (ITransport)transports.get(index - 1);
			transports.set(index - 1, transport);
			transports.set(index, temptrans);
		}
		else if(index!=-1 && index<transports.size()-1)
		{
			ITransport temptrans = (ITransport)transports.get(index + 1);
			transports.set(index + 1, transport);
			transports.set(index, temptrans);
		}
		else
		{
			throw new RuntimeException("Cannot change transport position from "
				+index+(up? " up": " down"));
		}
	}

	/**
	 *  Get the adresses of a component.
	 *  @return The addresses of this component.
	 */
	public String[] internalGetAddresses()
	{
		if(addresses == null)
		{
			ITransport[] trans = (ITransport[])transports.toArray(new ITransport[transports.size()]);
			ArrayList addrs = new ArrayList();
			for(int i = 0; i < trans.length; i++)
			{
				String[] traddrs = trans[i].getAddresses();
				for(int j = 0; traddrs!=null && j<traddrs.length; j++)
					addrs.add(traddrs[j]);
			}
			addresses = (String[])addrs.toArray(new String[addrs.size()]);
			
//			System.out.println("addresses: "+SUtil.arrayToString(addresses));
		}

		return addresses;
	}
	
	/**
	 *  Get the adresses of a component.
	 *  @return The addresses of this component.
	 */
	public IFuture<String[]> getAddresses()
	{
		return new Future<String[]>(internalGetAddresses());
	}
	
	/**
	 *  Get addresses of all transports.
	 *  @return The address schemes of all transports.
	 */
	public String[] getAddressSchemes()
	{
		ITransport[] trans = (ITransport[])transports.toArray(new ITransport[transports.size()]);
		ArrayList schemes = new ArrayList();
		for(int i = 0; i < trans.length; i++)
		{
			String[] aschemes = trans[i].getServiceSchemas();
			schemes.addAll(Arrays.asList(aschemes));
		}

		return (String[])schemes.toArray(new String[schemes.size()]);
	}

	/**
	 *  Get the transports.
	 *  @return The transports.
	 */
	public ITransport[] getTransports()
	{
		ITransport[] transportsArray = new ITransport[transports.size()];
		return (ITransport[])transports.toArray(transportsArray);
	}
	
	/**
	 *  Get a send target manager for addresses.
	 */
	public SendManager getSendManager(IComponentIdentifier cid)
	{
		SendManager ret = managers.get(cid.getRoot());
		
		if(ret==null)
		{
			ret = new SendManager();
			managers.put(cid.getRoot(), ret);
		}
		
		return ret;
	}

	//-------- IPlatformService interface --------
	
//	/**
//	 *  Start the service.
//	 */
//	public IFuture startService()
//	{
//		final Future ret = new Future();
//		
//		ITransport[] tps = (ITransport[])transports.toArray(new ITransport[transports.size()]);
//		if(transports.size()==0)
//		{
//			ret.setException(new RuntimeException("MessageService has no working transport for sending messages."));
//		}
//		else
//		{
//			CounterResultListener lis = new CounterResultListener(tps.length, new IResultListener()
//			{
//				public void resultAvailable(Object result)
//				{
//					SServiceProvider.getService(provider, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IResultListener()
//					{
//						public void resultAvailable(Object result)
//						{
//							clockservice = (IClockService)result;
//							SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new IResultListener()
//							{
//								public void resultAvailable(Object result)
//								{
//									cms = (IComponentManagementService)result;
//									MessageService.super.startService().addResultListener(new DelegationResultListener(ret));
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									ret.setException(exception);
//								}
//							});
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							ret.setException(exception);
//						}
//					});
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//				}
//			});
//			
//			for(int i=0; i<tps.length; i++)
//			{
//				try
//				{
//					tps[i].start().addResultListener(lis);
//				}
//				catch(Exception e)
//				{
//					System.out.println("Could not initialize transport: "+tps[i]+" reason: "+e);
//					transports.remove(tps[i]);
//				}
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
		final Future<Void> ret = new Future<Void>();

		super.startService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(transports.size()==0)
				{
					ret.setException(new RuntimeException("MessageService has no working transport for sending messages."));
				}
				else
				{
					exeservice	= SServiceProvider.getLocalService(component, IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
					cms	=  SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
					ITransport[] tps = (ITransport[])transports.toArray(new ITransport[transports.size()]);
					CollectionResultListener<Void> lis = new CollectionResultListener<Void>(tps.length, true,
						new ExceptionDelegationResultListener<Collection<Void>, Void>(ret)
					{
						public void customResultAvailable(Collection<Void> result)
						{
							if(result.isEmpty())
							{
								ret.setException(new RuntimeException("MessageService has no working transport for sending messages."));
							}
							else
							{
								addrservice	= SServiceProvider.getLocalService(component, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
								addrservice.getTransportAddresses().addResultListener(new ExceptionDelegationResultListener<TransportAddressBook, Void>(ret)
								{
									public void customResultAvailable(TransportAddressBook result)
									{
										taddresses = result;
										
										addrservice.addPlatformAddresses(new ComponentIdentifier(component.getComponentIdentifier().getRoot().getName(), internalGetAddresses()))
											.addResultListener(new DelegationResultListener<Void>(ret)
										{
											public void customResultAvailable(Void result) 
											{
												clockservice	= SServiceProvider.getLocalService(component, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
												libservice	= SServiceProvider.getLocalService(component, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
												libservice.getClassLoader(component.getModel().getResourceIdentifier())
													.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
												{
													public void customResultAvailable(ClassLoader result)
													{
														classloader = result;
														startStreamSendAliveBehavior();
														startStreamCheckAliveBehavior();
//														ams	= SServiceProvider.getLocalService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
														ret.setResult(null);
													}
												});
											}
										});
									}
								});
							}
						}
					});
									
					for(int i=0; i<tps.length; i++)
					{
						final ITransport	transport	= tps[i];
						IFuture<Void>	fut	= transport.start();
						fut.addResultListener(lis);
						fut.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
							}
							
							public void exceptionOccurred(final Exception exception)
							{
								transports.remove(transport);
								getComponent().scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										ia.getLogger().warning("Could not initialize transport: "+transport+" reason: "+exception);
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
	 *  Called when the platform shuts down. Do necessary cleanup here (if any).
	 */
	public IFuture<Void> shutdownService()
	{
		Future<Void>	ret	= new Future<Void>();
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.err.println("MessageService shutdown end");
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.err.println("MessageService shutdown error");
//				exception.printStackTrace();
//			}
//		});
		SendManager[] tmp = (SendManager[])managers.values().toArray(new SendManager[managers.size()]);
		final SendManager[] sms = (SendManager[])SUtil.arrayToSet(tmp).toArray(new SendManager[0]);
//		System.err.println("MessageService shutdown start: "+(transports.size()+sms.length+1));
		final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(transports.size()+sms.length+1, true, new DelegationResultListener<Void>(ret));
//		{
//			public void intermediateResultAvailable(Void result)
//			{
//				System.err.println("MessageService shutdown intermediate result: "+result+", "+cnt);
//				super.intermediateResultAvailable(result);
//			}
//			public boolean intermediateExceptionOccurred(Exception exception)
//			{
//				System.err.println("MessageService shutdown intermediate error: "+exception+", "+cnt);
//				return super.intermediateExceptionOccurred(exception);
//			}
//		};
		super.shutdownService().addResultListener(crl);

		for(int i=0; i<sms.length; i++)
		{
//			System.err.println("MessageService executor cancel: "+sms[i]);
			exeservice.cancel(sms[i]).addResultListener(crl);
		}
		
		for(int i=0; i<transports.size(); i++)
		{
//			System.err.println("MessageService transport shutdown: "+transports.get(i));
			((ITransport)transports.get(i)).shutdown().addResultListener(crl);
		}
		
		if(timer!=null)
		{
			timer.cancel();
			timer	= null;
		}
		
		return ret;
	}

	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	public MessageType getMessageType(String type)
	{
		return (MessageType)messagetypes.get(type);
	}
	
	/**
	 *  Add a message listener.
	 *  @param listener The change listener.
	 *  @param filter An optional filter to only receive notifications for matching messages. 
	 */
	public synchronized IFuture<Void> addMessageListener(IMessageListener listener, IFilter filter)
	{
		if(listeners==null)
			listeners = new LinkedHashMap();
		listeners.put(listener, filter);
		return new Future(null);
	}
	
	/**
	 *  Remove a message listener.
	 *  @param listener The change listener.
	 */
	public synchronized IFuture<Void> removeMessageListener(IMessageListener listener)
	{
		listeners.remove(listener);
		return new Future(null);
	}
	
	/**
	 *  Add content codec type.
	 *  @param codec The codec type.
	 */
	public IFuture<Void> addContentCodec(IContentCodec codec)
	{
		if(contentcodecs==null)
			contentcodecs = new ArrayList();
		contentcodecs.add(codec);
		return new Future(null);
	}
	
	/**
	 *  Remove content codec type.
	 *  @param codec The codec type.
	 */
	public IFuture<Void> removeContentCodec(IContentCodec codec)
	{
		if(contentcodecs!=null)
			contentcodecs.remove(codec);
		return new Future(null);
	}
	
	/**
	 *  Add message codec type.
	 *  @param codec The codec type.
	 */
	public IFuture<Void> addMessageCodec(Class codec)
	{
		codecfactory.addCodec(codec);
		return new Future(null);
	}
	
	/**
	 *  Remove message codec type.
	 *  @param codec The codec type.
	 */
	public IFuture<Void> removeMessageCodec(Class codec)
	{
		codecfactory.removeCodec(codec);
		return new Future(null);
	}
	
//	/**
//	 *  Update component identifier.
//	 *  @param cid The component identifier.
//	 *  @return The component identifier.
//	 */
//	public ITransportComponentIdentifier internalUpdateComponentIdentifier(ITransportComponentIdentifier cid)
//	{
//		TransportComponentIdentifier ret = null;
//		if(cid.getPlatformName().equals(component.getComponentIdentifier().getRoot().getLocalName()))
//		{
//			ret = new TransportComponentIdentifier(cid.getName(), internalGetAddresses());
////			System.out.println("Rewritten cid: "+ret+" :"+SUtil.arrayToString(ret.getAddresses()));
//		}
//		return ret==null? cid: ret;
//	}
	
	/**
	 *  Announce that addresses of transports might have changed.
	 */
	public IFuture<Void> refreshAddresses()
	{
		addresses	= null;
		for(IDiscoveryService ds: SServiceProvider.getLocalServices(component, IDiscoveryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
		{
			ds.republish();
		}

		// this is suboptimal currently because internalGetAddresses has to rebuild addresses immediately so
		// it could be done here
		return addrservice.addPlatformAddresses(new ComponentIdentifier(component.getComponentIdentifier().getRoot().getName(), internalGetAddresses()));
	}
	
//	/**
//	 *  Update component identifier.
//	 *  @param cid The component identifier.
//	 *  @return The component identifier.
//	 */
//	public IFuture<ITransportComponentIdentifier> updateComponentIdentifier(ITransportComponentIdentifier cid)
//	{
//		return new Future<ITransportComponentIdentifier>(internalUpdateComponentIdentifier(cid));
//	}
	
	//-------- internal methods --------
	
	/**
	 *  Get the component.
	 *  @return The component.
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
		final long lt = StreamSendTask.getMinLeaseTime(getComponent().getComponentIdentifier());
		if(lt!=Timeout.NONE)
		{
			getComponent().scheduleStep(new IComponentStep<Void>()
			{
				@Classname("sendAlive")
				public IFuture<Void> execute(IInternalAccess ia)
				{
	//				System.out.println("sendAlive: "+pcons+" "+icons);
					AbstractConnectionHandler[] mypcons = (AbstractConnectionHandler[])pcons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<mypcons.length; i++)
					{
						if(!mypcons[i].isClosed())
						{
							mypcons[i].sendAlive();
						}
					}
					AbstractConnectionHandler[] myicons = (AbstractConnectionHandler[])icons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<myicons.length; i++)
					{
						if(!myicons[i].isClosed())
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
		final long lt = StreamSendTask.getMinLeaseTime(getComponent().getComponentIdentifier());
//		System.out.println("to is: "+lt);
		if(lt!=Timeout.NONE)
		{
			getComponent().scheduleStep(new IComponentStep<Void>()
			{
				@Classname("checkAlive")
				public IFuture<Void> execute(IInternalAccess ia)
				{
	//				final IComponentStep<Void> step = this;
	//				final Future<Void> ret = new Future<Void>();
					
					AbstractConnectionHandler[] mypcons = (AbstractConnectionHandler[])pcons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<mypcons.length; i++)
					{
						if(!mypcons[i].isConnectionAlive())
						{
	//						System.out.println("removed con: "+component+", "+System.currentTimeMillis()+", "+mypcons[i].getConnectionId());
							mypcons[i].close();
							pcons.remove(Integer.valueOf(mypcons[i].getConnectionId()));
						}
					}
					AbstractConnectionHandler[] myicons = (AbstractConnectionHandler[])icons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<myicons.length; i++)
					{
						if(!myicons[i].isConnectionAlive())
						{
	//						System.out.println("removed con: "+component+", "+System.currentTimeMillis()+", "+myicons[i].getConnectionId());
							myicons[i].close();
							icons.remove(Integer.valueOf(myicons[i].getConnectionId()));
						}
					}
					
					waitForRealDelay(lt, this);
					
					return IFuture.DONE;
				}
			});
		}
	}
	

	/**
	 *  Deliver a message to the receivers.
	 */
	protected void internalDeliverMessage(Object obj)
	{
		MessageEnvelope	me	= null;
		try
		{
			ICommand handler;
			if(obj instanceof MessageEnvelope)
			{
				me	= (MessageEnvelope)obj;
				handler = deliveryhandlers.get(MapSendTask.MESSAGE_TYPE_MAP);
			}
			else
			{
				byte[]	rawmsg	= (byte[])obj;
				int	idx	= 0;
				byte rmt = rawmsg[idx++];
				handler = deliveryhandlers.get(rmt);
			}
			if(handler==null)
				throw new RuntimeException("Corrupt message, unknown delivery handler code.");
			handler.execute(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.warning("Message could not be delivered to receivers: "+(me!=null ? me.getReceivers() : "unknown") +", "+e);
		}
	}
	
	/**
	 *  Get the classloader for a resource identifier.
	 */
	protected IFuture<ClassLoader> getRIDClassLoader(final Map msg, final MessageType mt)
	{
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		
//		MessageType mt = getMessageType(type);
		final IResourceIdentifier rid = (IResourceIdentifier)msg.get(mt.getResourceIdIdentifier());
		final IComponentIdentifier	realrec	= (IComponentIdentifier)msg.get(mt.getRealReceiverIdentifier());
		
//		System.out.println("getRIDCl: "+SUtil.arrayToString(msg.get(SFipa.RECEIVERS))+" "+rid+" "+realrec);
		
		// Explanation for only using global rids:
		// Local rids are mapped to a path, but different platforms may use different
		// means for inlcuding them a) as new resource with custom rid b) in the startup path with platform rid
		if(rid!=null && rid.getGlobalIdentifier()!=null)
		{
			SServiceProvider.getService(component, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<ILibraryService, ClassLoader>(ret)
			{
				public void customResultAvailable(final ILibraryService ls)
				{
					ls.getClassLoader(rid).addResultListener(new DelegationResultListener<ClassLoader>(ret)
					{
						public void customResultAvailable(ClassLoader result)
						{
//							System.out.println("got: "+result+" "+rid);
							// Hack!!! Use current platform class loader for rms message, if no rid class loader available.
							if(result==null)
							{
								Object	recs	= msg.get(mt.getReceiverIdentifier());
								if((recs instanceof IComponentIdentifier[] && ((IComponentIdentifier[])recs).length==1
									&& ((IComponentIdentifier[])recs)[0].getLocalName().equals("rms"))
									|| (recs instanceof List && ((List<?>)recs).size()==1
										&& ((IComponentIdentifier)((List<?>)recs).get(0)).getLocalName().equals("rms")))
								{
//									System.out.println("cl is global");
									ls.getClassLoader(null).addResultListener(new DelegationResultListener<ClassLoader>(ret));
								}
								else
								{
//									System.out.println("cl is ridloader: "+result);
									super.customResultAvailable(null);
								}
							}
							else
							{
								super.customResultAvailable(result);
							}
						}
					});
				}
				public void exceptionOccurred(Exception exception)
				{
					super.resultAvailable(null);
				}
			});
		}
		else if(realrec!=null)
		{
			SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, ClassLoader>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getComponentDescription(realrec).addResultListener(new ExceptionDelegationResultListener<IComponentDescription, ClassLoader>(ret)
					{
						public void customResultAvailable(final IComponentDescription desc)
						{
							SServiceProvider.getService(component, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
								.addResultListener(new ExceptionDelegationResultListener<ILibraryService, ClassLoader>(ret)
							{
								public void customResultAvailable(ILibraryService ls)
								{
									ls.getClassLoader(desc.getResourceIdentifier()).addResultListener(new DelegationResultListener<ClassLoader>(ret));
								}
								public void exceptionOccurred(Exception exception)
								{
									super.resultAvailable(null);
								}
							});
						}
					});
				}
				public void exceptionOccurred(Exception exception)
				{
					super.resultAvailable(null);
				}
			});
		}
		else
		{
			// Hack? Use global loader if no rid declared? Use x_receiver?
			
			SServiceProvider.getService(component, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<ILibraryService, ClassLoader>(ret)
			{
				public void customResultAvailable(final ILibraryService ls)
				{
					ls.getClassLoader(null).addResultListener(new DelegationResultListener<ClassLoader>(ret)
					{
						public void customResultAvailable(ClassLoader result)
						{
							super.customResultAvailable(result);
						}
					});
				}
			});
//			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Send message(s) executable.
	 */
	public class SendManager implements IExecutable
	{
		//-------- attributes --------
		
		/** The list of messages to send. */
		protected List<AbstractSendTask> tasks;
		
		//-------- constructors --------
		
		/**
		 *  Send manager.
		 */
		public SendManager()
		{
			this.tasks = new ArrayList<AbstractSendTask>();
		}
		
		//-------- methods --------
	
		/**
		 *  Send a message.
		 */
		public boolean execute()
		{
			AbstractSendTask	tmp = null;
			boolean isempty;
			
			synchronized(this)
			{
				if(!tasks.isEmpty())
					tmp = tasks.remove(0);
				isempty = tasks.isEmpty();
			}
			final AbstractSendTask	task = tmp;
			
			if(task!=null)
			{
				// Todo: move back to send manager thread after isValid()
				// (hack!!! currently only works because message service is raw)
				// hack!!! doesn't make much sense to check isValid as send manager executes on different thread.
				isValid().addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						if(result.booleanValue())
						{
							task.doSendMessage();
						}
						
						// Quit when service was terminated.
						else
						{
	//						System.out.println("send message not executed");
							task.getFuture().setException(new MessageFailureException(task.getMessage(), task.getMessageType(), null, "Message service terminated."));
	//						isempty	= true;
	//						messages.clear();
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
	//					System.out.println("send message not executed");
						task.getFuture().setException(new MessageFailureException(task.getMessage(), task.getMessageType(), null, "Message service terminated."));
	//					isempty	= true;
	//					messages.clear();
					}
				});
			}
			
			return !isempty;
		}
		
		/**
		 *  Add a message to be sent.
		 *  @param message The message.
		 */
		public IFuture<Void> addMessage(final AbstractSendTask task)
		{
//			if(new Random().nextInt(1000)==0)
//			{
//				task.getFuture().setException(new RuntimeException("Random message error for testing: "+task.getFuture()));
//			}
//			else
			{
				isValid().addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(task.getFuture())
				{
					public void customResultAvailable(Boolean result)
					{
						if(result.booleanValue())
						{
							synchronized(SendManager.this)
							{
								tasks.add(task);
							}
							
							exeservice.execute(SendManager.this);
						}
						// Fail when service was shut down. 
						else
						{
	//						System.out.println("message not added");
							task.getFuture().setException(new ServiceTerminatedException(getServiceIdentifier()));
						}
					}
				});
			}
			
			return task.getFuture();
		}
	}
	
	/**
	 *  Deliver message(s) executable.
	 */
	protected class DeliverMessage implements IExecutable
	{
		//-------- attributes --------
		
		/** The list of messages to send. */
		protected List<Object> messages;
		
		//-------- constructors --------
		
		/**
		 *  Create a new deliver message executable.
		 */
		public DeliverMessage()
		{
			this.messages = new ArrayList<Object>();
		}
		
		//-------- methods --------
		
		/**
		 *  Deliver the message.
		 */
		public boolean execute()
		{
			Object tmp = null;
			boolean isempty;
			
			synchronized(this)
			{
				if(!messages.isEmpty())
					tmp = messages.remove(0);
				isempty = messages.isEmpty();
			}
			
			if(tmp!=null)
			{
				internalDeliverMessage(tmp);
			}
			
			return !isempty;
		}
		
		/**
		 *  Add a message to be delivered.
		 */
		public void addMessage(Object msg)
		{
			synchronized(this)
			{
				messages.add(msg);
			}
			
			exeservice.execute(DeliverMessage.this);
		}
	}
	
	int cnt;
	
	/**
	 *  Handle stream messages.
	 */
	class StreamDeliveryHandler implements ICommand
	{
		/**
		 *  Execute the command.
		 */
		public void execute(Object obj)
		{
			try
			{
				byte[] rawmsg = (byte[])obj;
				int mycnt = cnt++;
//				System.out.println("aaaa: "+mycnt+" "+getComponent().getComponentIdentifier());
//				System.out.println("Received binary: "+SUtil.arrayToString(rawmsg));
				int idx = 1;
				byte type = rawmsg[idx++];
				
				byte[] codec_ids = new byte[rawmsg[idx++]];
				byte[] bconid = new byte[4];
				for(int i=0; i<codec_ids.length; i++)
				{
					codec_ids[i] = rawmsg[idx++];
				}
				for(int i=0; i<4; i++)
				{
					bconid[i] = rawmsg[idx++];
				}
				final int conid = SUtil.bytesToInt(bconid);
				
				int seqnumber = -1;
				if(type==StreamSendTask.DATA_OUTPUT_INITIATOR || type==StreamSendTask.DATA_INPUT_PARTICIPANT)
				{
					for(int i=0; i<4; i++)
					{
						bconid[i] = rawmsg[idx++];
					}
					seqnumber = SUtil.bytesToInt(bconid);
	//				System.out.println("seqnr: "+seqnumber);
				}
				
				final Object data;
				if(codec_ids.length==0)
				{
					data = new byte[rawmsg.length-idx];
					System.arraycopy(rawmsg, idx, data, 0, rawmsg.length-idx);
				}
				else
				{
					Object tmp = new ByteArrayInputStream(rawmsg, idx, rawmsg.length-idx);
					for(int i=codec_ids.length-1; i>-1; i--)
					{
						ICodec dec = codecfactory.getCodec(codec_ids[i]);
						tmp = dec.decode(tmp, classloader, null);
					}
					data = tmp;
				}
	
				// Handle output connection participant side
				if(type==StreamSendTask.INIT_OUTPUT_INITIATOR)
				{
					InitInfo ii = (InitInfo)data;
					initInputConnection(conid, ii.getInitiator(), ii.getParticipant(), ii.getNonFunctionalProperties());
					addrservice.addPlatformAddresses(ii.getInitiator());
					addrservice.addPlatformAddresses(ii.getParticipant());
				}
				else if(type==StreamSendTask.ACKINIT_OUTPUT_PARTICIPANT)
				{
//					System.out.println("CCC: ack init");
					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(och!=null)
					{
						och.ackReceived(StreamSendTask.INIT, data);
					}
					else
					{
						System.out.println("OutputStream not found (ackinit): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.DATA_OUTPUT_INITIATOR)
				{
//					System.out.println("received data");
					InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.addData(seqnumber, (byte[])data);
					}
					else
					{
						System.out.println("InputStream not found (dai): "+conid+" "+pcons+" "+getComponent().getComponentIdentifier());
					}
				}
				else if(type==StreamSendTask.CLOSE_OUTPUT_INITIATOR)
				{
//					System.out.println("CCC: close");
					InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.closeReceived(SUtil.bytesToInt((byte[])data));
					}
					else
					{
						System.out.println("InputStream not found (coi): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.ACKCLOSE_OUTPUT_PARTICIPANT)
				{
//					System.out.println("CCC: ackclose");
					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(och!=null)
					{
						och.ackReceived(StreamSendTask.CLOSE, data);
					}
					else
					{
						System.out.println("OutputStream not found (ackclose): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.CLOSEREQ_OUTPUT_PARTICIPANT)
				{
//					System.out.println("CCC: closereq");
					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(och!=null)
					{
						och.closeRequestReceived();
					}
					else
					{
						System.out.println("OutputStream not found (closereq): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.ACKCLOSEREQ_OUTPUT_INITIATOR)
				{
//					System.out.println("CCC: ackclosereq");
					InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.ackReceived(StreamSendTask.CLOSEREQ, data);
	//					ich.ackCloseRequestReceived();
					}
					else
					{
						System.out.println("OutputStream not found (ackclosereq): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.ACKDATA_OUTPUT_PARTICIPANT)
				{
					// Handle input connection initiator side
					OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(och!=null)
					{
						AckInfo ackinfo = (AckInfo)data;
						och.ackDataReceived(ackinfo);
					}
					else
					{
						System.out.println("OutputStream not found (ackdata): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				
				else if(type==StreamSendTask.INIT_INPUT_INITIATOR)
				{
					InitInfo ii = (InitInfo)data;
					initOutputConnection(conid, ii.getInitiator(), ii.getParticipant(), ii.getNonFunctionalProperties());
					addrservice.addPlatformAddresses(ii.getInitiator());
					addrservice.addPlatformAddresses(ii.getParticipant());
				}
				else if(type==StreamSendTask.ACKINIT_INPUT_PARTICIPANT)
				{
					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.ackReceived(StreamSendTask.INIT, data);
					}
					else
					{
						System.out.println("InputStream not found (ackinit): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.DATA_INPUT_PARTICIPANT)
				{
					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.addData(seqnumber, (byte[])data);
					}
					else
					{
						System.out.println("InputStream not found (data input): "+conid);
					}
				}
				else if(type==StreamSendTask.ACKDATA_INPUT_INITIATOR)
				{
					OutputConnectionHandler och = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(och!=null)
					{
						AckInfo ackinfo = (AckInfo)data;
						och.ackDataReceived(ackinfo);	
					}
					else
					{
						System.out.println("OutputStream not found (ackdata): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.CLOSEREQ_INPUT_INITIATOR)
				{
					OutputConnectionHandler och = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(och!=null)
					{
						och.closeRequestReceived();
					}
					else
					{
						System.out.println("InputStream not found (closereq): "+conid);
					}
				}
				else if(type==StreamSendTask.ACKCLOSEREQ_INPUT_PARTICIPANT)
				{
					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.ackReceived(StreamSendTask.CLOSEREQ, data);
					}
					else
					{
						System.out.println("InputStream not found (ackclosereq): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.CLOSE_INPUT_PARTICIPANT)
				{
					InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.closeReceived(SUtil.bytesToInt((byte[])data));
					}
					else
					{
						System.out.println("OutputStream not found (closeinput): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.ACKCLOSE_INPUT_INITIATOR)
				{
					OutputConnectionHandler ich = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(ich!=null)
					{
						ich.ackReceived(StreamSendTask.CLOSE, data);
					}
					else
					{
						System.out.println("InputStream not found (ackclose): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				
				// Handle lease time update
				else if(type==StreamSendTask.ALIVE_INITIATOR)
				{
	//				System.out.println("alive initiator");
					AbstractConnectionHandler con = (AbstractConnectionHandler)pcons.get(Integer.valueOf(conid));
					if(con!=null)
					{
						con.setAliveTime(System.currentTimeMillis());
					}
					else
					{
						System.out.println("Stream not found (alive ini): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
				else if(type==StreamSendTask.ALIVE_PARTICIPANT)
				{
	//				System.out.println("alive particpant");
					AbstractConnectionHandler con = (AbstractConnectionHandler)icons.get(Integer.valueOf(conid));
					if(con!=null)
					{
						con.setAliveTime(System.currentTimeMillis());
					}
					else
					{
						System.out.println("Stream not found (alive par): "+component+", "+System.currentTimeMillis()+", "+conid);
					}
				}
	
//				System.out.println("bbbb: "+mycnt+" "+getComponent().getComponentIdentifier());
			}
//			catch(Throwable e)
			catch(final Exception e)
			{
//				e.printStackTrace();
				getComponent().scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getLogger().warning("Exception in stream: "+e.getMessage());
						return IFuture.DONE;
					}
				});
			}
		}
	}
	
	/**
	 *  Handle map messages, i.e. normal text messages.
	 */
	class MapDeliveryHandler implements ICommand
	{
		/**
		 *  Execute the command.
		 */
		public void execute(Object obj)
		{
			MessageEnvelope me;
			if(obj instanceof MessageEnvelope)
			{
				me	= (MessageEnvelope)obj;
			}
			else
			{
				final List<Exception>	errors	= new ArrayList<Exception>();
				IErrorReporter	rep	= strictcom ? null :new IErrorReporter()
				{
					public void exceptionOccurred(Exception e)
					{
						errors.add(e);
					}
				};
				me = (MessageEnvelope)MapSendTask.decodeMessage((byte[])obj, codecfactory.getAllCodecs(), classloader, rep);
				
				if(!errors.isEmpty())
				{
					logger.warning("Ignored errors during message decoding: "+errors);
//					for(Exception e: errors)
//					{
//						e.printStackTrace();
//					}
				}
//				byte[]	rawmsg	= (byte[])obj;
//				int	idx	= 0;
//				byte rmt = rawmsg[idx++];
//				byte[] codec_ids = new byte[rawmsg[idx++]];
//				for(int i=0; i<codec_ids.length; i++)
//				{
//					codec_ids[i] = rawmsg[idx++];
//				}
//		
//				Object tmp = new ByteArrayInputStream(rawmsg, idx, rawmsg.length-idx);
//				for(int i=codec_ids.length-1; i>-1; i--)
//				{
//					ICodec dec = codecfactory.getCodec(codec_ids[i]);
//					tmp = dec.decode(tmp, classloader);
//				}
//				me	= (MessageEnvelope)tmp;
			}
		
			final Map<String, Object> msg	= me.getMessage();
			final String type	= me.getTypeName();
			final IComponentIdentifier[] receivers	= me.getReceivers();
//			System.out.println("Received message: "+SUtil.arrayToString(receivers));
			final MessageType	messagetype	= getMessageType(type);
			
//			if(msg.get(SFipa.X_NONFUNCTIONAL)!=null
//				&& ((Map)msg.get(SFipa.X_NONFUNCTIONAL)).get("cause") instanceof String)
//			{
//				System.out.println("sdklvugi: "+msg.get(SFipa.SENDER));
//			}
			
			// Announce receiver to message awareness
			ITransportComponentIdentifier sender = (ITransportComponentIdentifier)msg.get(messagetype.getSenderIdentifier());
			announceComponentIdentifier(sender);
			addrservice.addPlatformAddresses(sender);
			
			// Content decoding works as follows:
			// Find correct classloader for each receiver by
			// a) if message contains rid ask library service for classloader (global rids are resolved with maven, locals possibly with peer to peer jar transfer)
			// b) if library service could not resolve rid or message does not contain rid the receiver classloader can be used
			
			final Future<Void> ret = new Future<Void>();
			// todo: what to do with exception here?
//			ret.addResultListener(new IResultListener<Void>()
//			{
//				public void resultAvailable(Void result)
//				{
//				}
//				public void exceptionOccurred(Exception exception)
//				{
//					exception.printStackTrace();
//				}
//			});
			
			getRIDClassLoader(msg, getMessageType(type))
				.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
			{
				public void customResultAvailable(final ClassLoader classloader)
				{
					SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
					{
						public void customResultAvailable(IComponentManagementService cms)
						{
							deliverToAllReceivers(receivers, cms, classloader, msg, logger, messagetype)
								.addResultListener(new DelegationResultListener<Void>(ret)
							{
								public void customResultAvailable(Void result)
								{
									IFilter[] fils;
									IMessageListener[] lis;
									synchronized(this)
									{
										fils = listeners==null? null: (IFilter[])listeners.values().toArray(new IFilter[listeners.size()]);
										lis = listeners==null? null: (IMessageListener[])listeners.keySet().toArray(new IMessageListener[listeners.size()]);
									}
									
									if(lis!=null)
									{
										// Decode message for listener. What if listener has different class loader?
										decodeMessage(logger, messagetype, msg, classloader, null, component);
										IMessageAdapter message = new DefaultMessageAdapter(msg, messagetype);
										for(int i=0; i<lis.length; i++)
										{
											IMessageListener li = (IMessageListener)lis[i];
											boolean	match	= false;
											try
											{
												match	= fils[i]==null || fils[i].filter(message);
											}
											catch(Exception e)
											{
												logger.warning("Filter threw exception: "+fils[i]+", "+e);
											}
											if(match)
											{
												try
												{
													li.messageReceived(message);
												}
												catch(Exception e)
												{
													logger.warning("Listener threw exception: "+li+", "+e);
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
	protected IFuture<Void> deliverToAllReceivers(final IComponentIdentifier[] receivers, final IComponentManagementService cms, 
		final ClassLoader classloader, final Map msg, final Logger logger, final MessageType messagetype)
	{
		final Future<Void> ret = new Future<Void>();
		
		final int[] i = new int[1];
		deliverToReceiver(receivers, i[0], cms, classloader, msg, logger, messagetype)
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(++i[0]<receivers.length)
				{
					deliverToReceiver(receivers, i[0], cms, classloader, msg, logger, messagetype).addResultListener(this);
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
	protected IFuture<Void> deliverToReceiver(final IComponentIdentifier[] receivers, final int i, final IComponentManagementService cms, final ClassLoader classloader, 
		final Map<String, Object> msg, final Logger logger, final MessageType messagetype)
	{
//		System.out.println("dtr: "+SUtil.arrayToString(receivers)+" "+i+" "+classloader);
		
		final Future<Void> ret = new Future<Void>();
		
		final IComponentIdentifier receiver = receivers[i];

		// Copy message for state isolation.
		final Map<String, Object>	fmessage	= new HashMap<String, Object>(msg);
		
		// Perform decoding on component thread (necessary for rms)
		cms.getExternalAccess(receiver).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess exta)
			{
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IInternalMessageFeature	com	= (IInternalMessageFeature)ia.getComponentFeature(IMessageFeature.class);
						
						if(com!=null)
						{
							ClassLoader cl = classloader!=null? classloader: ia.getClassLoader();
							decodeMessage(logger, messagetype, fmessage, cl, receiver, ia);
							
							try
							{
								com.messageArrived(new DefaultMessageAdapter(fmessage, messagetype));
							}
							catch(Exception e)
							{
								logger.warning("Message could not be delivered to local receiver: " + receiver + ", "+ fmessage.get(messagetype.getIdIdentifier())+", "+e);
								
								// todo: notify sender that message could not be delivered!
								// Problem: there is no connection back to the sender, so that
								// the only chance is sending a separate failure message.
							}
						}
						else
						{
							logger.warning("Message could not be delivered to local receiver (no communication feature): " + receiver + ", "+ fmessage.get(messagetype.getIdIdentifier()));							
						}
						
						return IFuture.DONE;
					}
				}).addResultListener(component.getComponentFeature(IExecutionFeature.class)
					.createResultListener( new DelegationResultListener<Void>(ret)));
			}
			public void exceptionOccurred(Exception exception)
			{
				logger.warning("Message could not be delivered to local receiver: " + receiver + ", "+ msg.get(messagetype.getIdIdentifier())+", "+exception);
				ret.setResult(null);
			}
		});
			
		return ret;
	}
	
	/** The (real) system clock timer. */
	protected volatile Timer	timer;
	
	/**
	 *  Wait for a time delay on the (real) system clock.
	 */
	public TimerTask	waitForRealDelay(long delay, final IComponentStep<?> step)
	{
		if(timer==null)
		{
			synchronized(this)
			{
				if(timer==null)
				{
					timer	= new Timer(component.getComponentIdentifier().getName()+".message.timer", true);
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
	 *  Create local input connection side after receiving a remote init output message.
	 *  May be called multiple times and does nothing, if connection already exists.
	 */
	protected IInputConnection	initInputConnection(final int conid, final ITransportComponentIdentifier initiator, 
		final ITransportComponentIdentifier participant, final Map<String, Object> nonfunc)
	{
		boolean	created;
		InputConnectionHandler ich	= null;
		InputConnection con	= null;
		synchronized(this)
		{
			ich	= (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
			if(ich==null)
			{
				ich = new InputConnectionHandler(MessageService.this, nonfunc);
				con = new InputConnection(initiator, participant, conid, false, ich);
				pcons.put(Integer.valueOf(conid), ich);
//				System.out.println("created for: "+conid+" "+pcons+" "+getComponent().getComponentIdentifier());
				created	= true;
			}
			else
			{
				con	= ich.getInputConnection();
				created	= false;
			}
		}
		
		if(created)
		{
			ich.initReceived();
			
			final InputConnection	fcon	= con;
			final Future<Void> ret = new Future<Void>();
			SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(participant).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
					{
						public void customResultAvailable(IExternalAccess ea)
						{
							ea.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									IInternalMessageFeature	com	= (IInternalMessageFeature)ia.getComponentFeature(IMessageFeature.class);
									if(com!=null)
									{
										com.streamArrived(fcon);
									}
									else
									{
										ia.getLogger().warning("Component received stream, but ha no communication feature: "+fcon);
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
			if(nonfunc!=null)
				ich.setNonFunctionalProperties(nonfunc);
		}
		
		return con;
	}

	/**
	 *  Create local output connection side after receiving a remote init input message.
	 *  May be called multiple times and does nothing, if connection already exists.
	 */
	protected IOutputConnection	initOutputConnection(final int conid, final ITransportComponentIdentifier initiator, 
		final ITransportComponentIdentifier participant, final Map<String, Object> nonfunc)
	{
		boolean	created;
		OutputConnectionHandler och;
		OutputConnection con	= null;
		synchronized(this)
		{
			och	= (OutputConnectionHandler) pcons.get(Integer.valueOf(conid));
			if(och==null)
			{
				och = new OutputConnectionHandler(MessageService.this, nonfunc);
				con = new OutputConnection(initiator, participant, conid, false, och);
				pcons.put(Integer.valueOf(conid), och);
//				System.out.println("created: "+con.hashCode());
				created	= true;
			}
			else
			{
				con	= och.getOutputConnection();
				created	= false;
			}
		}
		
		if(created)
		{
			och.initReceived();
			
			final OutputConnection	fcon	= con;
			final Future<Void> ret = new Future<Void>();
			SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(participant).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
					{
						public void customResultAvailable(IExternalAccess ea)
						{
							ea.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									IInternalMessageFeature	com	= (IInternalMessageFeature)ia.getComponentFeature(IMessageFeature.class);
									if(com!=null)
									{
										com.streamArrived(fcon);
									}
									else
									{
										ia.getLogger().warning("Component received stream, but ha no communication feature: "+fcon);
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
			if(nonfunc!=null)
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
		
		if(args.length>0)
			doread = args[0].equals("read");
		
		int len = 10000;
		
		long start = System.currentTimeMillis();
		
		if(doread)
		{
			ServerSocket ss = null;
			try
			{
				ss = new ServerSocket(44444);
				Socket s = ss.accept();
				InputStream is = new BufferedInputStream(s.getInputStream());
				
				byte[] read = new byte[len];
				int packcnt = 0;
				for(; packcnt<max; packcnt++)
				{
					int cnt = 0;
					while(cnt<len) 
					{
						int bytes_read = is.read(read, cnt, len-cnt);
						if(bytes_read==-1) 
							throw new IOException("Stream closed");
						cnt += bytes_read;
					}
					System.out.println("read packet: "+packcnt);
				}
				
				is.close();
				s.close();
				
				long end = System.currentTimeMillis();
				System.out.println("Needed: "+(end-start));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					ss.close();
				}
				catch(Exception e)
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
				for(int i=0; i<write.length; i++)
				{
					write[i] = (byte)(i%10);
				}
				
				Socket s = new Socket(InetAddress.getByName("134.100.11.230"), 44444);
				OutputStream os = new BufferedOutputStream(s.getOutputStream());
				
				for(int i=0; i<max; i++)
				{
					os.write(write);
					os.flush();
					System.out.println("wrote packet: "+i);
				}
				
				os.close();
				s.close();
				
				long end = System.currentTimeMillis();
				System.out.println("Needed: "+(end-start));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	long mwstime;
	IMessageAwarenessService	mws;
	
	/**
	 *  Announce a component identifier to message awareness 
	 *	and address service.
	 */
	protected void announceComponentIdentifier(final ITransportComponentIdentifier cid)
	{
		// Search for mws only every 5 seconds.
		if(System.currentTimeMillis()-mwstime>5000)
		{
			mwstime	= System.currentTimeMillis();
			getComponent().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					SServiceProvider.getService(ia, IMessageAwarenessService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IMessageAwarenessService>()
					{
						public void resultAvailable(IMessageAwarenessService result)
						{
							mws	= result;
							announceComponentIdentifier(cid);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// ignore if message awareness service not found
						}
					}));
					
					return IFuture.DONE; 
				}
			});
		}
		else if(mws!=null)
		{
			mws.announceComponentIdentifier(cid);
		}
	}
	
	/**
	 *  Get the release date from a message.
	 */
	protected IFuture<Date> getReleaseDate(MessageType type, final Map<String, Object> msg)
	{
		final Future<Date> ret = new Future<Date>();
		Object tmp = msg.get(type.getReceiverIdentifier());
		
		if(tmp instanceof IComponentIdentifier)
		{
			tmp = new IComponentIdentifier[] { (IComponentIdentifier) tmp };
		}
		
		if(SReflect.isIterable(tmp))
		{
			int size = 0;
			for(Iterator<?> it=SReflect.getIterator(tmp); it.hasNext(); )
			{
				++size;
				it.next();
			}
			
			final CollectionResultListener<Date> crl = new CollectionResultListener<Date>(size, false, new ExceptionDelegationResultListener<Collection<Date>, Date>(ret)
			{
				public void customResultAvailable(Collection<Date> result)
				{
					Date releasedate = null;
					for(Date date : result)
					{
						if (date != null && (releasedate == null || releasedate.after(date)))
						{
							releasedate = date;
						}
					}
					
					// Unknown platform date, assume oldest chain.
					if(releasedate == null)
					{
						releasedate = new Date(1);
					}
					
					ret.setResult(releasedate);
				}
			});
			
			for(Iterator<?> it=SReflect.getIterator(tmp); it.hasNext(); )
			{
				final IComponentIdentifier rec = (IComponentIdentifier)it.next();
				if(rec==null)
				{
					crl.exceptionOccurred(new MessageFailureException(msg, type, null, "A receiver nulls: "+msg));
				}
				// Addresses may only null for local messages, i.e. intra platform communication
				else if((
					(rec instanceof ITransportComponentIdentifier && ((ITransportComponentIdentifier)rec).getAddresses()==null) 
					|| !(rec instanceof ITransportComponentIdentifier)) &&
					!(rec.getPlatformName().equals(component.getComponentIdentifier().getPlatformName())))
				{
					crl.exceptionOccurred(new MessageFailureException(msg, type, null, "A receiver addresses nulls: "+msg));
				}
				else if(!releasedatecache.containsKey(rec.getRoot()))
				{
					SServiceProvider.getService(component, IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM, false).addResultListener(new IResultListener<IAwarenessManagementService>()
					{
						public void resultAvailable(IAwarenessManagementService ams)
						{
							ams.getPlatformInfo(rec.getRoot()).addResultListener(new IResultListener<DiscoveryInfo>()
							{
								public void resultAvailable(DiscoveryInfo info)
								{
									if (info != null)
									{
										Map<String, String> props = info.getProperties();
										String stringdate = props != null? props.get(AwarenessInfo.PROPERTY_JADEXDATE): null;
										Date date = stringdate != null? new Date(Long.parseLong(stringdate)) : null;
										releasedatecache.put(rec.getRoot(), date);
										crl.resultAvailable(date);
									}
									else
									{
										releasedatecache.put(rec.getRoot(), null);
										crl.resultAvailable(null);
									}
								}
								
								public void exceptionOccurred(
										Exception exception)
								{
									releasedatecache.put(rec.getRoot(), null);
									crl.resultAvailable(null);
								}
								
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							releasedatecache.put(rec.getRoot(), null);
							crl.resultAvailable(null);
						}
					});
				}
				else
				{
					Date date = releasedatecache.get(rec.getRoot());
					crl.resultAvailable(date);
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Decode a message.
	 */
	protected void decodeMessage(final Logger logger, final MessageType messagetype, final Map<String, Object> fmessage, ClassLoader cl, IComponentIdentifier rec, IInternalAccess component)
	{
//		System.out.println("dec: "+cl+" "+component.getComponentIdentifier()+" "+MessageService.this.component.getComponentIdentifier());
		// Conversion via platform specific codecs
		if(rec==null)
		{
			Object recs = fmessage.get(messagetype.getReceiverIdentifier());
			if(SReflect.isIterable(recs))
			{
				rec = (IComponentIdentifier)SReflect.getIterator(recs).next();
			}
			else
			{
				rec = (IComponentIdentifier)recs;
			}
		}
		IContentCodec[] compcodecs = getContentCodecs(component.getModel(), cl);
		for(Iterator it=fmessage.keySet().iterator(); it.hasNext(); )
		{
			String name = (String)it.next();
			Object value = fmessage.get(name);
												
			IContentCodec codec = messagetype.findContentCodec(compcodecs, fmessage, name);
			if(codec==null)
				codec = messagetype.findContentCodec(getContentCodecs(), fmessage, name);
			
			if(codec!=null)
			{
//				System.out.println("dec2: "+codec+fmessage);
				try
				{
					final List<Exception>	errors	= new ArrayList<Exception>();
					IErrorReporter	rep	= strictcom ? null :new IErrorReporter()
					{
						public void exceptionOccurred(Exception e)
						{
							errors.add(e);
						}
					};
					Object val = codec.decode((byte[])value, cl, getContentCodecInfo(rec), rep);
					
					if(!errors.isEmpty())
					{
						logger.warning("Ignored errors during message decoding: "+errors);
//						for(Exception e: errors)
//						{
//							e.printStackTrace();
//						}
					}
					fmessage.put(name, val);
				}
				catch(Exception e)
				{
//					System.out.println("classloader: "+cl);
//					e.printStackTrace();
					if(!(e instanceof ContentException))
					{
						// Todo: find out why 50MB sized messages are sent... 
						if(((byte[])value).length>3000)
						{
							byte[]	tmp = new byte[3000];
							System.arraycopy(value, 0, tmp, 0, tmp.length);
							logger.info("ContentException: "+((byte[])value).length+", "+fmessage+", "+new String(tmp, Charset.forName("UTF-8")));
							value	= tmp;
						}
						e = new ContentException(new String((byte[])value, Charset.forName("UTF-8")), e);
					}
					fmessage.put(name, e);
				}
			}
			
			if(fmessage.get(name) instanceof byte[])
			{
				System.out.println("message problem\n"+new String((byte[])fmessage.get(name), Charset.forName("UTF-8")));
			}
		}
	}
}


