package jadex.base.service.message;

import jadex.base.AbstractComponentAdapter;
import jadex.base.fipa.SFipa;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ContentException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.MessageFailureException;
import jadex.bridge.ServiceTerminatedException;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IMessageListener;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.traverser.ITraverseProcessor;
import jadex.commons.traverser.Traverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


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
    public static IContentCodec[] DEFCODECS = new IContentCodec[]
    {
        new jadex.base.contentcodecs.JavaXMLContentCodec(),
        new jadex.base.contentcodecs.JadexXMLContentCodec(),
        new jadex.base.contentcodecs.NuggetsXMLContentCodec()
    };

    /** No addresses constant. */
    protected String LOCAL = "local";
    
	//-------- attributes --------

	/** The component. */
    protected IExternalAccess component;

	/** The transports. */
	protected List transports;

	/** All addresses of this platform. */
	private String[] addresses;

	/** The message types. */
	protected Map messagetypes;
	
	/** The deliver message action executed by platform executor. */
	protected DeliverMessage delivermsg;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The listeners (listener->filter). */
	protected Map listeners;
	
	/** The cashed clock service. */
	protected IClockService	clockservice;
	
	/** The library service. */
	protected ILibraryService libservice;
	
//	/** The cashed clock service. */
//	protected IComponentManagementService cms;
	
	/** The target managers. */
	protected LRU managers;
	
	/** The content codecs. */
	protected List contentcodecs;
	
	/** The codec factory for messages. */
	protected CodecFactory codecfactory;
	
	//-------- constructors --------

	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(IExternalAccess component, Logger logger, ITransport[] transports, 
		MessageType[] messagetypes)
	{
		this(component, logger, transports, messagetypes, null, null);
	}
	
	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(IExternalAccess component, Logger logger, ITransport[] transports, 
		MessageType[] messagetypes, IContentCodec[] contentcodecs, CodecFactory codecfactory)
	{
		super(component.getServiceProvider().getId(), IMessageService.class, null);

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
		
		this.managers = new LRU(800);
		if(contentcodecs!=null)
		{
			for(int i=0; i<contentcodecs.length; i++)
			{
				addContentCodec(contentcodecs[i]);
			}
		}
		this.codecfactory = codecfactory!=null? codecfactory: new CodecFactory();
	}
	
	//-------- interface methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	public IFuture<Void> sendMessage(final Map origmsg, final MessageType type, 
		IComponentIdentifier osender, final IResourceIdentifier rid, final byte[] codecids)
	{
		final Map msg = new HashMap(origmsg);
		
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier sender = internalUpdateComponentIdentifier(osender);
		
		libservice.getClassLoader(rid)
			.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
		{
			public void customResultAvailable(final ClassLoader cl)
			{
//				IComponentIdentifier sender = adapter.getComponentIdentifier();
				if(sender==null)
				{
					ret.setException(new RuntimeException("Sender must not be null: "+msg));
					return;
				}
			
				// Replace own component identifiers.
				// Now done just before send
//				String[] params = type.getParameterNames();
//				for(int i=0; i<params.length; i++)
//				{
//					Object o = msg.get(params[i]);
//					if(o instanceof IComponentIdentifier)
//					{
//						msg.put(params[i], updateComponentIdentifier((IComponentIdentifier)o));
//					}
//				}
//				String[] paramsets = type.getParameterSetNames();
//				for(int i=0; i<paramsets.length; i++)
//				{
//					Object o = msg.get(paramsets[i]);
//					
//					if(SReflect.isIterable(o))
//					{
//						List rep = new ArrayList();
//						for(Iterator it=SReflect.getIterator(o); it.hasNext(); )
//						{
//							Object item = it.next();
//							if(item instanceof IComponentIdentifier)
//							{
//								rep.add(updateComponentIdentifier((IComponentIdentifier)item));
//							}
//							else
//							{
//								rep.add(item);
//							}
//						}
//						msg.put(paramsets[i], rep);
//					}
//					else if(o instanceof IComponentIdentifier)
//					{
//						msg.put(paramsets[i], updateComponentIdentifier((IComponentIdentifier)o));
//					}
//				}
				
				// Automatically add optional meta information.
				String senid = type.getSenderIdentifier();
				if(msg.get(senid)==null)
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
				if(msg.get(ridid)==null)
				{
					msg.put(ridid, rid);
				}
				
				// Check receivers.
				Object tmp = msg.get(type.getReceiverIdentifier());
				if(tmp==null || SReflect.isIterable(tmp) &&	!SReflect.getIterator(tmp).hasNext())
				{
					ret.setException(new RuntimeException("Receivers must not be empty: "+msg));
					return;
				}
				
				if(SReflect.isIterable(tmp))
				{
					for(Iterator it=SReflect.getIterator(tmp); it.hasNext(); )
					{
						IComponentIdentifier rec = (IComponentIdentifier)it.next();
						if(rec==null)
						{
							ret.setException(new MessageFailureException(msg, type, null, "A receiver nulls: "+msg));
							return;
						}
						// Addresses may only null for local messages, i.e. intra platform communication
						else if(rec.getAddresses()==null && 
							!(rec.getPlatformName().equals(component.getComponentIdentifier().getPlatformName())))
						{
							ret.setException(new MessageFailureException(msg, type, null, "A receiver addresses nulls: "+msg));
							return;
						}
					}
				}
				
				// External access of sender required for content encoding etc.
				SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						cms.getExternalAccess(sender).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
//								System.out.println("msgservice calling doSendMessage()");
								doSendMessage(msg, type, exta, cl, ret, codecids);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Extracted method to be callable from listener.
	 */
	protected void doSendMessage(Map msg, MessageType type, IExternalAccess comp, ClassLoader cl, Future ret, byte[] codecids)
	{
		Map msgcopy	= new HashMap(msg);
		
		// Conversion via platform specific codecs
		// Hack?! Preprocess content to enhance component identifiers.
		IContentCodec[] compcodecs = getContentCodecs(comp.getModel(), cl);
		List<ITraverseProcessor> procs = Traverser.getDefaultTraversalProcessors();
		procs.add(1, new ITraverseProcessor()
		{
			public Object process(Object object, Class clazz,
				List<ITraverseProcessor> processors, Traverser traverser,
				Map<Object, Object> traversed)
			{
				return internalUpdateComponentIdentifier((IComponentIdentifier)object);
			}
			
			public boolean isApplicable(Object object, Class clazz)
			{
				return object instanceof IComponentIdentifier;
			}
		});
		
		for(Iterator it=msgcopy.keySet().iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			Object	value	= msgcopy.get(name);
			value = Traverser.traverseObject(value, procs);
			msgcopy.put(name, value);
			
			IContentCodec codec = type.findContentCodec(compcodecs, msg, name);
			if(codec==null)
				codec = type.findContentCodec(getContentCodecs(), msg, name);
			
			if(codec!=null)
			{
				msgcopy.put(name, codec.encode(value, cl));
			}
			else if(value!=null && !(value instanceof String) 
				&& !(name.equals(type.getSenderIdentifier()) || name.equals(type.getReceiverIdentifier())
					|| name.equals(type.getResourceIdIdentifier())))
			{	
				ret.setException(new ContentException("No content codec found for: "+name+", "+msgcopy));
				return;
			}
		}
		
		IComponentIdentifier sender = (IComponentIdentifier)msgcopy.get(type.getSenderIdentifier());
		if(sender.getAddresses()==null || sender.getAddresses().length==0)
			System.out.println("schrott2");
		
		IFilter[] fils;
		IMessageListener[] lis;
		synchronized(this)
		{
			fils = listeners==null? null: (IFilter[])listeners.values().toArray(new IFilter[listeners.size()]);
			lis = listeners==null? null: (IMessageListener[])listeners.keySet().toArray(new IMessageListener[listeners.size()]);
		}
		
		if(lis!=null)
		{
			// Hack?!
			IMessageAdapter msgadapter = new DefaultMessageAdapter(msg, type);
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
		MultiCollection managers = new MultiCollection();
		String recid = type.getReceiverIdentifier();
		Object tmp	= msgcopy.get(recid);
		if(SReflect.isIterable(tmp))
		{
			for(Iterator it = SReflect.getIterator(tmp); it.hasNext(); )
			{
				IComponentIdentifier cid = (IComponentIdentifier)it.next();
				SendManager sm = getSendManager(cid); 
				managers.put(sm, cid);
			}
		}
		else
		{
			IComponentIdentifier cid = (IComponentIdentifier)tmp;
			SendManager sm = getSendManager(cid); 
			managers.put(sm, cid);
		}
		
		CodecFactory	codecfac	= (CodecFactory)getCodecFactory();
		byte[] cids	= codecids;
		if(cids==null || cids.length==0)
			cids = codecfac.getDefaultCodecIds();
		ICodec[] codecs = new ICodec[cids.length];
		for(int i=0; i<codecs.length; i++)
		{
			codecs[i] = codecfac.getCodec(cids[i]);
		}
		
		CollectionResultListener crl = new CollectionResultListener(managers.size(), false, new DelegationResultListener(ret));
		for(Iterator it=managers.keySet().iterator(); it.hasNext();)
		{
			SendManager tm = (SendManager)it.next();
			IComponentIdentifier[] recs = (IComponentIdentifier[])managers.getCollection(tm)
				.toArray(new IComponentIdentifier[0]);
			ManagerSendTask task = new ManagerSendTask(msgcopy, type, recs, getTransports(), cids, codecs);
//			System.out.println("msgservice adding send task");
			tm.addMessage(task).addResultListener(crl);
//			task.getSendManager().addMessage(task).addResultListener(crl);
		}
		
//		sendmsg.addMessage(msgcopy, type, receivers, ret);
	}
	
	/**
	 *  Get content codecs.
	 *  @return The content codecs.
	 */
	public IContentCodec[] getContentCodecs()
	{
		return contentcodecs==null? DEFCODECS: (IContentCodec[])contentcodecs.toArray(new IContentCodec[contentcodecs.size()]);
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
			for(Iterator it=props.keySet().iterator(); ret==null && it.hasNext();)
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

	/**
	 *  Deliver a message to the intended components. Called from transports.
	 *  @param message The native message. 
	 *  (Synchronized because can be called from concurrently executing transports)
	 */
	public void deliverMessage(Map message, String msgtype, IComponentIdentifier[] receivers)
	{	
		delivermsg.addMessage(message, msgtype, receivers);
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
			String scheme = trans[i].getServiceSchema();
			schemes.add(scheme);
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
		SendManager ret = null;
		
		String[] adrs = cid.getAddresses();
		
		if(adrs==null || adrs.length==0)
		{
			ret = (SendManager)managers.get(LOCAL);
		}
		else
		{
			for(int i=0; i<adrs.length && ret==null; i++)
			{
				ret = (SendManager)managers.get(adrs[i]);
			}
		}
		
		if(ret==null)
		{
			ret = new SendManager();
			
			if(adrs==null || adrs.length==0)
			{
				managers.put(LOCAL, ret);
			}
			else
			{
				for(int i=0; i<adrs.length; i++)
				{
					managers.put(adrs[i], ret);
				}
			}
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

		super.startService().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ITransport[] tps = (ITransport[])transports.toArray(new ITransport[transports.size()]);
				if(transports.size()==0)
				{
					ret.setException(new RuntimeException("MessageService has no working transport for sending messages."));
				}
				else
				{
					CollectionResultListener lis = new CollectionResultListener(tps.length, true, new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							if(((Collection)result).isEmpty())
							{
								ret.setException(new RuntimeException("MessageService has no working transport for sending messages."));
							}
							else
							{
								SServiceProvider.getService(component.getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										clockservice = (IClockService)result;
										SServiceProvider.getService(component.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DelegationResultListener(ret)
										{
											public void customResultAvailable(Object result)
											{
												libservice = (ILibraryService)result;
												ret.setResult(null);
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
						IFuture	fut	= transport.start();
						fut.addResultListener(lis);
						fut.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
							}
							
							public void exceptionOccurred(final Exception exception)
							{
								transports.remove(transport);
								component.scheduleStep(new IComponentStep<Void>()
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
//		ret.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
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
		final CounterResultListener	crl	= new CounterResultListener(transports.size()+sms.length+1, true,
			new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					super.customResultAvailable(getServiceIdentifier());
				}
			})
		{
//			public void intermediateResultAvailable(Object result)
//			{
//				System.err.println("MessageService shutdown intermediate result: "+result+", "+cnt);
//				super.intermediateResultAvailable(result);
//			}
//			public boolean intermediateExceptionOccurred(Exception exception)
//			{
//				System.err.println("MessageService shutdown intermediate error: "+exception+", "+cnt);
//				return super.intermediateExceptionOccurred(exception);
//			}
		};
		super.shutdownService().addResultListener(crl);

		SServiceProvider.getService(component.getServiceProvider(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IExecutionService	exe	= (IExecutionService)result;
				
				for(int i=0; i<sms.length; i++)
				{
//					System.err.println("MessageService executor cancel: "+sms[i]);
					exe.cancel(sms[i]).addResultListener(crl);
				}
				
				for(int i=0; i<transports.size(); i++)
				{
//					System.err.println("MessageService transport shutdown: "+transports.get(i));
					((ITransport)transports.get(i)).shutdown().addResultListener(crl);
				}
			}
		});
		
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
//	 *  Get a codec.
//	 *  @param codecid The codec id.
//	 *  @return The codec.
//	 */
//	public IFuture getCodecFactory(byte id)
//	{
//		return new Future(codecfactory.getCodec(id));
//	}
	
	/**
	 *  Get the codec factory
	 *  @return The codec factory.
	 */
	@Excluded
	public Object	getCodecFactory()
	{
		return codecfactory;
	}
	
	/**
	 *  Update component identifier.
	 *  @param cid The component identifier.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier internalUpdateComponentIdentifier(IComponentIdentifier cid)
	{
		ComponentIdentifier ret = null;
		if(cid.getPlatformName().equals(component.getComponentIdentifier().getRoot().getLocalName()))
		{
			ret = new ComponentIdentifier(cid.getName(), internalGetAddresses());
//			System.out.println("Rewritten cid: "+ret+" :"+SUtil.arrayToString(ret.getAddresses()));
		}
		return ret==null? cid: ret;
	}
	
	/**
	 *  Update component identifier.
	 *  @param cid The component identifier.
	 *  @return The component identifier.
	 */
	public IFuture<IComponentIdentifier> updateComponentIdentifier(IComponentIdentifier cid)
	{
		return new Future<IComponentIdentifier>(internalUpdateComponentIdentifier(cid));
	}
	
	//-------- internal methods --------
	
	/**
	 *  Deliver a message to the receivers.
	 */
	protected void internalDeliverMessage(final Map msg, final String type, final IComponentIdentifier[] receivers)
	{
		final MessageType	messagetype	= getMessageType(type);
		final Map	decoded	= new HashMap();	// Decoded messages cached by class loader to avoid decoding the same message more than once, when the same class loader is used.
		
		// Content decoding works as follows:
		// Find correct classloader for each receiver by
		// a) if message contains rid ask library service for classloader (global rids are resolved with maven, locals possibly with peer to peer jar transfer)
		// b) if library service could not resolve rid or message does not contain rid the receiver classloader can be used
		
		getRIDClassLoader(msg, getMessageType(type)).addResultListener(new DefaultResultListener<ClassLoader>()
		{
			public void resultAvailable(final ClassLoader classloader)
			{
				SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
					.addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						for(int i = 0; i < receivers.length; i++)
						{
	//						final int cnt = i; 
							AbstractComponentAdapter component = (AbstractComponentAdapter)cms.getComponentAdapter(receivers[i]);
							if(component != null)
							{
								ClassLoader cl = classloader!=null? classloader: component.getComponentInstance().getClassLoader();
								Map	message	= (Map)decoded.get(cl);
								
								if(message==null)
								{
									if(receivers.length>1)
									{
										message	= new HashMap(msg);
										decoded.put(cl, message);
									}
									else
									{
										// Skip creation of copy when only one receiver.
										message	= msg;
									}
	
									// Conversion via platform specific codecs
									IContentCodec[] compcodecs = getContentCodecs(component.getModel(), cl);
									for(Iterator it=message.keySet().iterator(); it.hasNext(); )
									{
										String name = (String)it.next();
										Object value = message.get(name);
																			
										IContentCodec codec = messagetype.findContentCodec(compcodecs, message, name);
										if(codec==null)
											codec = messagetype.findContentCodec(getContentCodecs(), message, name);
										
										if(codec!=null)
										{
											try
											{
												Object val = codec.decode((byte[])value, cl);
												message.put(name, val);
											}
											catch(Exception e)
											{
												ContentException ce = new ContentException(new String((byte[])value), e);
												message.put(name, ce);
											}
										}
									}
								}
	
								try
								{
									component.receiveMessage(message, messagetype);
								}
								catch(Exception e)
								{
									logger.warning("Message could not be delivered to receiver: " + receivers[i] + ", "+ message.get(messagetype.getIdIdentifier())+", "+e);
	
									// todo: notify sender that message could not be delivered!
									// Problem: there is no connection back to the sender, so that
									// the only chance is sending a separate failure message.
								}
							}
							else
							{
								logger.warning("Message could not be delivered to receiver: " + receivers[i] + ", "+ msg.get(messagetype.getIdIdentifier()));
	
								// todo: notify sender that message could not be delivered!
								// Problem: there is no connection back to the sender, so that
								// the only chance is sending a separate failure message.
							}
						}
	
						IFilter[] fils;
						IMessageListener[] lis;
						synchronized(this)
						{
							fils = listeners==null? null: (IFilter[])listeners.values().toArray(new IFilter[listeners.size()]);
							lis = listeners==null? null: (IMessageListener[])listeners.keySet().toArray(new IMessageListener[listeners.size()]);
						}
						
						if(lis!=null)
						{
							// Hack?! Use message decoded for some component. What if listener has different class loader? 
							Map	message	= decoded.isEmpty() ? msg : (Map)decoded.get(decoded.keySet().iterator().next());
							IMessageAdapter msg = new DefaultMessageAdapter(message, messagetype);
							for(int i=0; i<lis.length; i++)
							{
								IMessageListener li = (IMessageListener)lis[i];
								boolean	match	= false;
								try
								{
									match	= fils[i]==null || fils[i].filter(msg);
								}
								catch(Exception e)
								{
									logger.warning("Filter threw exception: "+fils[i]+", "+e);
								}
								if(match)
								{
									try
									{
										li.messageReceived(msg);
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
	
	/**
	 *  Get the classloader for a resource identifier.
	 */
	protected IFuture<ClassLoader> getRIDClassLoader(Map msg, MessageType mt)
	{
		final Future<ClassLoader> ret = new Future<ClassLoader>();
		
//		MessageType mt = getMessageType(type);
		String ridid = mt.getResourceIdIdentifier();
		final IResourceIdentifier rid = (IResourceIdentifier)msg.get(ridid);
		if(rid!=null)
		{
			IFuture<ILibraryService> fut = SServiceProvider.getServiceUpwards(component.getServiceProvider(), ILibraryService.class);
			fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, ClassLoader>(ret)
			{
				public void customResultAvailable(ILibraryService ls)
				{
					ls.getClassLoader(rid).addResultListener(new DelegationResultListener<ClassLoader>(ret));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.resultAvailable(null);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Send message(s) executable.
	 */
	protected class SendManager implements IExecutable
	{
		//-------- attributes --------
		
		/** The list of messages to send. */
		protected List messages;
		
		//-------- constructors --------
		
		/**
		 *  Send manager.
		 */
		public SendManager()
		{
			this.messages = new ArrayList();
		}
		
		//-------- methods --------
	
		/**
		 *  Send a message.
		 */
		public boolean execute()
		{
			Object[] tmp = null;
			boolean isempty;
			
			synchronized(this)
			{
				if(!messages.isEmpty())
					tmp = (Object[])messages.remove(0);
				isempty = messages.isEmpty();
			}
			final Object[] ftmp = tmp;
			
			isValid().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue())
					{
//						System.err.println("MessageService SendManager.execute");
						if(ftmp!=null)
						{
							final ManagerSendTask task = (ManagerSendTask)ftmp[0];
							final Future ret = (Future)ftmp[1];
							
							IComponentIdentifier[] receivers = task.getReceivers();
//							System.out.println("recs: "+SUtil.arrayToString(receivers)+" "+task.hashCode());
							
							if(task.getTransports().isEmpty())
							{
								ret.setException(new MessageFailureException(task.getMessage(), task.getMessageType(), receivers, 
									"Message could not be delivered to (all) receivers: "+ SUtil.arrayToString(receivers)+", "+SUtil.arrayToString(receivers[0].getAddresses())));								
							}
							else
							{
								// Try next transport.
								ITransport transport = (ITransport)task.getTransports().remove(0);
//								transport.sendMessage(task.getMessage(), task.getMessageType().getName(), receivers, task.getCodecIds())
								transport.sendMessage(task)
									.addResultListener(new DelegationResultListener(ret)
								{
									public void exceptionOccurred(Exception exception)
									{
										// On success re-add message with original receivers. 
										addMessage(task).addResultListener(new DelegationResultListener(ret));
									}
								});
							}
						}
					}
					
					// Quit when service was terminated.
					else
					{
//						System.out.println("send message not executed");
						if(ftmp!=null)
						{
							ManagerSendTask task = (ManagerSendTask)ftmp[0];
							Future ret = (Future)ftmp[1];
							ret.setException(new MessageFailureException(task.getMessage(), task.getMessageType(), null, "Message service terminated."));
						}
//						isempty	= true;
//						messages.clear();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("send message not executed");
					if(ftmp!=null)
					{
						ManagerSendTask task = (ManagerSendTask)ftmp[0];
						Future ret = (Future)ftmp[1];
						ret.setException(new MessageFailureException(task.getMessage(), task.getMessageType(), null, "Message service terminated."));
					}
//					isempty	= true;
//					messages.clear();
				}
			});
			
			return !isempty;
		}
		
		/**
		 *  Add a message to be sent.
		 *  @param message The message.
		 */
		public IFuture addMessage(final ManagerSendTask task)
		{
			final Future ret = new Future();
			
			isValid().addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue())
					{
						synchronized(SendManager.this)
						{
							messages.add(new Object[]{task, ret});
						}
						
						SServiceProvider.getService(component.getServiceProvider(), IExecutionService.class, 
							RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								try
								{
									((IExecutionService)result).execute(SendManager.this);
								}
								catch(RuntimeException e)
								{
									// ignore if execution service is shutting down.
								}						
							}
						});
					}
					// Fail when service was shut down. 
					else
					{
						System.out.println("message not added");
						ret.setException(new ServiceTerminatedException(getServiceIdentifier()));
					}
				}
			});
			
			return ret;
		}
	}
	
	/**
	 *  Deliver message(s) executable.
	 */
	protected class DeliverMessage implements IExecutable
	{
		//-------- attributes --------
		
		/** The list of messages to send. */
		protected List messages;
		
		//-------- constructors --------
		
		/**
		 *  Create a new deliver message executable.
		 */
		public DeliverMessage()
		{
			this.messages = new ArrayList();
		}
		
		//-------- methods --------
		
		/**
		 *  Deliver the message.
		 */
		public boolean execute()
		{
			Object[] tmp = null;
			boolean isempty;
			
			synchronized(this)
			{
				if(!messages.isEmpty())
					tmp = (Object[])messages.remove(0);
				isempty = messages.isEmpty();
			}
			
			if(tmp!=null)
			{
				try
				{
					internalDeliverMessage((Map)tmp[0], (String)tmp[1], (IComponentIdentifier[])tmp[2]);
				}
				catch(Exception e)
				{
					logger.warning("Message could not be delivered to receiver: "+tmp[2]+", "+e);
				}
			}
			
			return !isempty;
		}
		
		/**
		 *  Add a message to be delivered.
		 */
		public void addMessage(Map message, String type, IComponentIdentifier[] receivers)
		{
			IComponentIdentifier sender = (IComponentIdentifier)message.get(getMessageType(type).getSenderIdentifier());
			if(sender.getAddresses()==null || sender.getAddresses().length==0)
				System.out.println("schrott: "+sender);
			
			synchronized(this)
			{
				messages.add(new Object[]{message, type, receivers});
			}
			
			SServiceProvider.getService(component.getServiceProvider(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					try
					{
						((IExecutionService)result).execute(DeliverMessage.this);
					}
					catch(RuntimeException e)
					{
						// ignore if execution service is shutting down.
					}
				}
			});
		}
	}
}


