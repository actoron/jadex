package jadex.base.service.remote;

import jadex.base.service.message.MessageService;
import jadex.base.service.message.OutputConnection;
import jadex.base.service.remote.commands.AbstractRemoteCommand;
import jadex.base.service.remote.commands.RemoteGetExternalAccessCommand;
import jadex.base.service.remote.commands.RemoteSearchCommand;
import jadex.base.service.remote.xml.RMIPostProcessor;
import jadex.base.service.remote.xml.RMIPreProcessor;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.AnyResultSelector;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.TypeResultSelector;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.LRU;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.commons.transformation.binaryserializer.DecodingContext;
import jadex.commons.transformation.binaryserializer.EncodingContext;
import jadex.commons.transformation.binaryserializer.IDecoderHandler;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.IPreProcessor;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/* $if !android $ */
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
/* $else $
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLStreamException;
$endif $ */


/**
 *  The remote service management service is responsible for 
 *  handling remote service invocations (similar to RMI).
 */
public class RemoteServiceManagementService extends BasicService implements IRemoteServiceManagementService
{
	//-------- constants --------
	
	/** Language constant for binary-encoded messages.
	 *  RMS needs its own language to prevent the message service
	 *  from decoding RMS messages.
	 */
	public static String RMS_JADEX_BINARY = "rms-" + SFipa.JADEX_BINARY;
	
	/** Language constant for XML-encoded messages.
	 *  RMS needs its own language to prevent the message service
	 *  from decoding RMS messages.
	 */
	public static String RMS_JADEX_XML = "rms-" + SFipa.JADEX_XML;
	
	/** Excluded remote methods (for all methods)
	 *	Excluded methods throw UnsupportedOperationException. */
	public static String REMOTE_EXCLUDED = "remote_excluded";
	
	/** Uncached remote methods (for methods with no parameters)
	 *	Uncached methods will be invoked on every call. */
	public static String REMOTE_UNCACHED = "remote_uncached";
	
	/** Synchronous remote methods (for methods with void return value). 
     *	If void methods are declared synchronous they will block the caller until
     *	the method has been executed on the remote side (exception thus can arrive). */
	public static String REMOTE_SYNCHRONOUS = "remote_synchronous";

	/** Replacement methods to be executed instead of remote method invocation. */
	public static String REMOTE_METHODREPLACEMENT = "remote_methodreplacement";

	/** Timeout for remote method invocation. */
	public static String REMOTE_TIMEOUT = "remote_timeout";

	/** The default timeout. */
	public static long DEFAULT_TIMEOUT = 15000;
		
	//-------- attributes --------
	
	/** The component. */
	protected IMicroExternalAccess component;
	
	/** The map of waiting calls (callid -> future). */
	protected Map<String, Future<Object>> waitingcalls;
	
	/** The map of processing calls (callid -> terniable future). */
	protected Map<String, Object> processingcalls;
	
	/** The map of termination commands without futures (callid -> command). 
	    This can happen whenever a remote invocation command is executed after the terminate arrives. */
	protected LRU<String, Runnable> terminationcommands;

	/** The remote reference module. */
	protected RemoteReferenceModule rrm;
	
	/** The rmi object to xml writer. */
	protected Writer writer;
	
	/** The rmi xml to object reader. */
	protected Reader reader;
	
	/** Preprocessors for binary encoding. */
	protected List<ITraverseProcessor> binpreprocs;
	
	/** Postprocessors for binary decoding. */
	protected List<IDecoderHandler> binpostprocs;
	
	/** Flag whether to use binary encoding. */
	protected boolean binarymode;
	
	/** The timer. */
	protected Timer	timer;
	
	/** The marshal service. */
	protected IMarshalService marshal;
	
	/** The message service. */
	protected IMessageService msgservice;
	
//	protected Map<Integer, IInputConnection> icons;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote service management service.
	 */
	public RemoteServiceManagementService(IMicroExternalAccess component, 
		ILibraryService libservice, final IMarshalService marshal, final IMessageService msgservice, boolean binarymode)
	{
		super(component.getServiceProvider().getId(), IRemoteServiceManagementService.class, null);

		this.component = component;
		this.rrm = new RemoteReferenceModule(this, libservice, marshal);
		this.waitingcalls = new HashMap<String, Future<Object>>();
		this.processingcalls = new HashMap<String, Object>();
		this.terminationcommands = new LRU<String, Runnable>(100);
		this.timer	= new Timer(true);
		this.marshal = marshal;
		this.msgservice = msgservice;
		this.binarymode = binarymode;
//		this.icons = new HashMap<Integer, IInputConnection>();
		
		// Reader that supports conversion of proxyinfo to proxy.
		Set<TypeInfo> typeinfosread = JavaReader.getTypeInfos();
		
		QName[] pr = new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.base.service.remote", "ProxyReference")};
		TypeInfo ti_rr = new TypeInfo(new XMLInfo(pr), 
			new ObjectInfo(ProxyReference.class, new RMIPostProcessor(rrm)));
		typeinfosread.add(ti_rr);

		QName[] icp = new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.base.service.remote", "ServiceInputConnectionProxy")};
		TypeInfo ti_icp = new TypeInfo(new XMLInfo(icp), 
			new ObjectInfo(ServiceInputConnectionProxy.class, new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				ServiceInputConnectionProxy icp = (ServiceInputConnectionProxy)object;
				int conid = icp.getConnectionId();
				IInputConnection icon = ((MessageService)msgservice).getParticipantInputConnection(conid);
				return icon;
			}
			
			public int getPass()
			{
				return 0;
			}
		}));
		typeinfosread.add(ti_icp);
		
		this.reader = new Reader(new TypeInfoPathManager(typeinfosread), false, false, false, new XMLReporter()
		{
			public void report(String message, String error, Object info, Location location)
				throws XMLStreamException
			{
				List<Tuple>	errors	= (List<Tuple>)((ReadContext)Reader.READ_CONTEXT.get()).getUserContext();
				errors.add(new Tuple(new Object[]{message, error, info, location}));
			}
		}, new BeanObjectReaderHandler(typeinfosread));
		
		
		// Writer that supports conversion :
		// isRemoteReference to remote reference
		// pojo services to underlying service proxies 
		// enhance component identifier with addresses
		
		Set<TypeInfo> typeinfoswrite = JavaWriter.getTypeInfos();
		final RMIPreProcessor preproc = new RMIPreProcessor(rrm);
		
//		TypeInfo ti_proxyable = new TypeInfo(new XMLInfo(pr, null, false, preproc), 
//			new ObjectInfo(IRemotable.class));
//		typeinfoswrite.add(ti_proxyable);
		
		// Component identifier enhancement now done in MessageService sendMessage
		QName[] ppr = new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.bridge", "ComponentIdentifier")};
		final IComponentIdentifier root = component.getComponentIdentifier().getRoot();
		IPreProcessor cidpp = new IPreProcessor()
		{
			public Object preProcess(IContext context, Object object)
			{
				IComponentIdentifier src = (IComponentIdentifier)object;
				ComponentIdentifier ret = null;
				if(src.getPlatformName().equals(root.getLocalName()))
				{
					String[] addresses = (String[])((Object[])context.getUserContext())[1];
					ret = new ComponentIdentifier(src.getName(), addresses);
//					System.out.println("Rewritten cid: "+ret);
				}
				
				return ret==null? src: ret;
			}
		};
		TypeInfo ti_cids = new TypeInfo(new XMLInfo(ppr, cidpp), new ObjectInfo(IComponentIdentifier.class));
		typeinfoswrite.add(ti_cids);
		
		BeanObjectWriterHandler wh = new BeanObjectWriterHandler(typeinfoswrite, true);
		
		// Add pre processor thst maps pojo services to underlying service proxies 
		// (have to be processed further with RMIPreprocessor)
		wh.addPreProcessor(new IFilter()
		{
			public boolean filter(Object obj)
			{
				return obj!=null && !(obj instanceof BasicService) && obj.getClass().isAnnotationPresent(Service.class);
			}
		}, new IPreProcessor()
		{
			public Object preProcess(IContext context, Object object)
			{
				return BasicServiceInvocationHandler.getPojoServiceProxy(object);
			}
		});
		
		// Add preprocessor that tests if is remote reference and replaces with 
		wh.addPreProcessor(new IFilter()
		{
			public boolean filter(Object obj)
			{
				return marshal.isRemoteReference(obj);
			}
		}, preproc);
		
		// Streams
		wh.addPreProcessor(new IFilter()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof ServiceInputConnectionProxy;
			}
		}, new IPreProcessor()
		{
			public Object preProcess(IContext context, Object object)
			{
				IComponentIdentifier receiver = (IComponentIdentifier)((Object[])context.getUserContext())[0];
				ServiceInputConnectionProxy con = (ServiceInputConnectionProxy)object;
				// hack todo:
				OutputConnection ocon = ((MessageService)msgservice).internalCreateOutputConnection(RemoteServiceManagementService.this.component.getComponentIdentifier(), receiver);
				con.setOutputConnection(ocon);
				return null;
			}
		});
		
		this.writer = new Writer(wh);
//		{
//			public void writeObject(WriteContext wc, Object object, QName tag) throws Exception 
//			{
//				// todo: cleanup rmi pre/postprocessing 
//				// extra preprocessing of commands should be removed
//				// should instead use ifReference() with context information (how/where to express?)
//				
////				System.out.println("object: "+object);
////				if(object instanceof RemoteResultCommand)
////					System.out.println("huhuhu");
//				
//				if(marshal.isRemoteReference(object))
//				{
////					System.out.println("changed: "+object.getClass()+" "+object);
//					object = preproc.preProcess(wc, object);
//				}
////				else
////				{
////					System.out.println("kept: "+object.getClass()+" "+object);
////				}
//				
//				// Perform pojo service replacement (for local and remote calls).
//				// Test if it is pojo service impl.
//				// Has to be mapped to new proxy then
//				
//				if(object!=null && !(object instanceof BasicService) && object.getClass().isAnnotationPresent(Service.class))
//				{
////					System.out.println("test");
//					// Check if the argument type refers to the pojo service
////					Service ser = object.getClass().getAnnotation(Service.class);
////					if(SReflect.isSupertype(ser.value(), sic.getMethod().getParameterTypes()[i]))
//					{
//						object = BasicServiceInvocationHandler.getPojoServiceProxy(object);
////						System.out.println("proxy: "+object);
//					}
//				}
//				
//				super.writeObject(wc, object, tag);
//			};
//		};
		
		
		// Equivalent pre- and postprocessors for binary mode.
		
		binpostprocs = new ArrayList<IDecoderHandler>();
		IDecoderHandler rmipostproc = new IDecoderHandler()
		{
			public boolean isApplicable(Class clazz)
			{
				return ProxyReference.class.equals(clazz);
			}
			
			public Object decode(Class clazz, DecodingContext context)
			{
				try
				{
					return rrm.getProxy((ProxyReference)context.getLastObject(), context.getClassloader());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
		binpostprocs.add(rmipostproc);
		
		binpreprocs = new ArrayList<ITraverseProcessor>();
		ITraverseProcessor bpreproc = new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
			{
				return ComponentIdentifier.class.equals(clazz);
			}
			
			public Object process(Object object, Class<?> clazz,
					List<ITraverseProcessor> processors, Traverser traverser,
					Map<Object, Object> traversed, boolean clone, Object context)
			{
				IComponentIdentifier src = (IComponentIdentifier)object;
				ComponentIdentifier ret = null;
				if(src.getPlatformName().equals(root.getLocalName()))
				{
					//Object[] oa = (Object[])((EncodingContext) context).getUserContext();
					String[] addresses = (String[])((Object[])((EncodingContext) context).getUserContext())[1];
					ret = new ComponentIdentifier(src.getName(), addresses);
				}
				
				return ret==null? src: ret;
			}
		};
		binpreprocs.add(bpreproc);
		
		bpreproc = new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
			{
				return object != null && !(object instanceof BasicService) && object.getClass().isAnnotationPresent(Service.class);
			}
			
			public Object process(Object object, Class<?> clazz,
					List<ITraverseProcessor> processors, Traverser traverser,
					Map<Object, Object> traversed, boolean clone, Object context)
			{
				return BasicServiceInvocationHandler.getPojoServiceProxy(object);
			}
		};
		binpreprocs.add(bpreproc);
		
		bpreproc = new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
			{
				return marshal.isRemoteReference(object);
			}
			
			public Object process(Object object, Class<?> clazz,
					List<ITraverseProcessor> processors, Traverser traverser,
					Map<Object, Object> traversed, boolean clone, Object context)
			{
				try
				{
					Object[] uc = (Object[])((EncodingContext) context).getUserContext();
					return rrm.getProxyReference(object, (IComponentIdentifier)uc[0], ((EncodingContext) context).getClassLoader());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
		binpreprocs.add(bpreproc);
	}
	
	//-------- methods --------
	
//	protected void addInputConnection(IInputConnection icon)
//	{
//		icons.put(icon.get, value);
//	}
	
	/**
	 *  Get service proxies from a remote platform.
	 *  (called from arbitrary components)
	 *  @param cid Component id that is used to start the search.
	 *  @param manager The search manager.
	 *  @param decider The visit decider.
	 *  @param selector The result selector.
	 *  @return Collection or single result (i.e. service proxies). 
	 */
	public IFuture<Object> getServiceProxies(final IComponentIdentifier cid, 
		final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		Future<Object> ret = new Future<Object>();
		
		component.scheduleStep(new IComponentStep<Object>()
		{
			@XMLClassname("getServiceProxies")
			public IFuture<Object> execute(IInternalAccess ia)
			{
				final Future<Object> fut = new Future<Object>();
				ia.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(fut)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
						// Hack! create remote rms cid with "rms" assumption.
						IComponentIdentifier rrms = new ComponentIdentifier("rms@"+cid.getPlatformName(), cid.getAddresses());
						final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
						RemoteSearchCommand content = new RemoteSearchCommand(cid, manager, 
							decider, selector, callid);
						
						sendMessage(rrms, content, callid, -1, fut);
					}
				});
				
				return fut;
			}
		}).addResultListener(new DelegationResultListener<Object>(ret));
		
		return ret;
	}
	
	/**
	 *  Get a service proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid	The remote provider id.
	 *  @param service	The service type.
	 *  @param scope	The search scope. 
	 *  @return The service proxy.
	 */
	public IFuture<Object> getServiceProxy(final IComponentIdentifier cid, final Class<?> service, String scope)
	{
		Future<Object>	ret	= new Future<Object>();
		getServiceProxies(cid, SServiceProvider.getSearchManager(false, scope), SServiceProvider.getVisitDecider(true, scope), 
			new TypeResultSelector(service, true)).addResultListener(new DelegationResultListener<Object>(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(result!=null && !((Collection<Object>)result).isEmpty())
					super.customResultAvailable(((Collection<Object>)result).iterator().next());
				else
					super.exceptionOccurred(new ServiceNotFoundException("No proxy for service found: "+cid+", "+service.getName()));
			}
		});
		return ret;
	}
	
	/**
	 *  Get all service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid	The remote provider id.
	 *  @param service	The service type.
	 *  @param scope	The search scope. 
	 *  @return The service proxy.
	 */
	public IFuture<Object> getServiceProxies(IComponentIdentifier cid, Class<?> service, String scope)
	{
		return getServiceProxies(cid, SServiceProvider.getSearchManager(true, scope), SServiceProvider.getVisitDecider(false, scope), 
			new TypeResultSelector(service, true));
	}
	
	/**
	 *  Get all declared service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid The remote provider id.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture<Object> getDeclaredServiceProxies(IComponentIdentifier cid)
	{
		return getServiceProxies(cid, SServiceProvider.localmanager, SServiceProvider.contdecider, 
			new AnyResultSelector(false, false));
	}
	
	/**
	 *  Get an external access proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid Component target id.
	 *  @return External access of remote component. 
	 */
	public IFuture<Object> getExternalAccessProxy(final IComponentIdentifier cid)
	{
		final Future<Object> ret = new Future<Object>();
		
		component.scheduleStep(new IComponentStep<Object>()
		{
			@XMLClassname("getExternalAccessProxy")
			public IFuture<Object> execute(IInternalAccess ia)
			{
				final Future<Object> fut = new Future<Object>();
				ia.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(fut)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
						// Hack! create remote rms cid with "rms" assumption.
//						IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
						IComponentIdentifier rrms = new ComponentIdentifier("rms@"+cid.getPlatformName(), cid.getAddresses());
						final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
						RemoteGetExternalAccessCommand content = new RemoteGetExternalAccessCommand(cid, callid);
						
						sendMessage(rrms, content, callid, -1, fut);
					}
				});
				
				return fut;
			}
		}).addResultListener(new DelegationResultListener<Object>(ret));
		
		return ret;
	}
	
	/**
	 *  Get the component.
	 *  @return the component.
	 */
	public IMicroExternalAccess getComponent()
	{
		return component;
	}
	
	/**
	 *  Get the rms component identifier.
	 *  @return The rms component identifier.
	 */
	public IComponentIdentifier getRMSComponentIdentifier()
	{
		return component.getComponentIdentifier();
	}
	
	/**
	 *  Get the waiting calls.
	 *  @return the waiting calls.
	 * /
	public Map getWaitingCalls()
	{
		return waitingcalls;
	}*/
	
	/**
	 *  Get the reader.
	 *  @return The reader.
	 */
	public Reader getReader()
	{
		return reader;
	}

	/**
	 *  Get the writer.
	 *  @return the writer.
	 */
	public Writer getWriter()
	{
		return writer;
	}
	
	/**
	 *  Get the preprocessors for binary encoding.
	 *  @return Binary preprocessors.
	 */
	public List<ITraverseProcessor> getBinaryPreProcessors()
	{
		return binpreprocs;
	}
	
	/**
	 *  Get the postprocessors for binary decoding.
	 *  @return Binary postprocessors.
	 */
	public List<IDecoderHandler> getBinaryPostProcessors()
	{
		return binpostprocs;
	}
	
	/**
	 *  Get the msg service.
	 *  @return the msg service.
	 */
	public IMessageService getMessageService()
	{
		return msgservice;
	}

	/**
	 *  Add a new waiting call.
	 *  @param callid The callid.
	 *  @param future The future.
	 */
	public void putWaitingCall(String callid, Future<Object> future)
	{
		getRemoteReferenceModule().checkThread();
		waitingcalls.put(callid, future);
	}
	
	/**
	 *  Get a waiting call future.
	 *  @param callid The callid.
	 *  @return The future.
	 */
	public Future<Object> getWaitingCall(String callid)
	{
		getRemoteReferenceModule().checkThread();
		return waitingcalls.get(callid);
	}
	
	/**
	 *  Remove a waiting call.
	 *  @param callid The callid.
	 *  @return The future.
	 */
	public Future<Object> removeWaitingCall(String callid)
	{
		getRemoteReferenceModule().checkThread();
		return waitingcalls.remove(callid);
	}
	
	/**
	 *  Add a new processing call.
	 *  @param callid The callid.
	 *  @param future The future.
	 */
	public void putProcessingCall(String callid, Object future)
	{
		getRemoteReferenceModule().checkThread();
		processingcalls.put(callid, future);
	}
	
	/**
	 *  Get a processing call future.
	 *  @param callid The callid.
	 *  @return The future.
	 */
	public Object getProcessingCall(String callid)
	{
		getRemoteReferenceModule().checkThread();
		return processingcalls.get(callid);
	}
	
	/**
	 *  Remove a processing call.
	 *  @param callid The callid.
	 *  @return The future.
	 */
	public Object removeProcessingCall(String callid)
	{
		getRemoteReferenceModule().checkThread();
		return processingcalls.remove(callid);
	}
	
	/**
	 *  Add a termination command.
	 */
	public void addTerminationCommand(String callid, Runnable command)
	{
		terminationcommands.put(callid, command);
	}
	
	/**
	 *  Remove a termination command.
	 */
	public Runnable removeTerminationCommand(String callid)
	{
		return terminationcommands.remove(callid);
	}
	
	/**
	 *  Get the remote reference module.
	 *  @return the rrm.
	 */
	public RemoteReferenceModule getRemoteReferenceModule()
	{
		return rrm;
	}
	
//	protected static Map errors = Collections.synchronizedMap(new LRU(200));
//	public Map interestingcalls = new HashMap();
	/**
	 *  Send the request message of a remote method invocation.
	 *  (Can safely be called from any thread).
	 */
	public void sendMessage(final IComponentIdentifier receiver, final Object content,
		final String callid, final long to, final Future<Object> future)
	{
//		System.out.println("RMS sending: "+content);
		
		try
		{
			component.scheduleStep(new IComponentStep<Void>()
			{
				@XMLClassname("sendMessage")
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					Future<Void>	pre	= new Future<Void>(); 
					if(content instanceof AbstractRemoteCommand)
					{
						((AbstractRemoteCommand)content).preprocessCommand(ia, rrm, receiver)
							.addResultListener(new DelegationResultListener<Void>(pre));
					}
					else
					{
						pre.setResult(null);
					}
					
					pre.addResultListener(new ExceptionDelegationResultListener<Void, Object>(future)
					{
						public void customResultAvailable(Void v)
						{
							final long timeout = to<=0? DEFAULT_TIMEOUT: to;
							
							putWaitingCall(callid, future);
							// Remove waiting call when future is done
							future.addResultListener(new IResultListener<Object>()
							{
								public void resultAvailable(Object result)
								{
									removeWaitingCall(callid);
								}
								public void exceptionOccurred(Exception exception)
								{
									removeWaitingCall(callid);								
									ia.getLogger().info("Remote exception occurred: "+receiver+", "+exception.toString());
								}
							});
	//						System.out.println("Waitingcalls: "+waitingcalls.size());
							
							final Map<String, Object> msg = new HashMap<String, Object>();
							msg.put(SFipa.SENDER, component.getComponentIdentifier());
							msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
							msg.put(SFipa.CONVERSATION_ID, callid);
			//				msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
							if (binarymode)
								msg.put(SFipa.LANGUAGE, RMS_JADEX_BINARY);
							else
								msg.put(SFipa.LANGUAGE, RMS_JADEX_XML);
							
							ia.getServiceContainer().searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
								.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Object>(future)
							{
								public void customResultAvailable(final ILibraryService ls)
								{
									ia.getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
										.addResultListener(new ExceptionDelegationResultListener<IMessageService, Object>(future)
									{
										public void customResultAvailable(final IMessageService ms)
										{
											// todo: use rid of sender?! (not important as writer does not use classloader, only nuggets)
											ls.getClassLoader(ia.getModel().getResourceIdentifier())
												.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Object>(future)
											{
												public void customResultAvailable(final ClassLoader cl)
												{
													msgservice.getAddresses().addResultListener(new ExceptionDelegationResultListener<String[], Object>(future)
													{
														public void customResultAvailable(String[] addresses)
														{
															Object cont = null;
															// Hack!!! Manual encoding for using custom class loader at receiver side.
															if (binarymode)
															{
																cont = BinarySerializer.objectToByteArray(content, binpreprocs, new Object[]{receiver, addresses}, cl);
															}
															else
															{
																cont = Writer.objectToXML(getWriter(), content, cl, new Object[]{receiver, addresses});
															}
															
															msg.put(SFipa.CONTENT, cont);
	
															
	//														System.out.println("RMS sending to2: "+receiver+", "+(content!=null?SReflect.getClassName(content.getClass()):null));
					//										if(cont.indexOf("getServices")!=-1)
					//										{
					//											interestingcalls.put(callid, cont);
					//										}
															
															ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, ia.getComponentIdentifier(), ia.getModel().getResourceIdentifier(), null)
																.addResultListener(new ExceptionDelegationResultListener<Void, Object>(future)
															{
																public void customResultAvailable(Void result)
																{
	//																System.out.println("sent: "+callid);
																	// ok message could be sent.
																	timer.schedule(new TimerTask()
																	{
																		public void run()
																		{
																			ia.getExternalAccess().scheduleStep(new IComponentStep<Void>()
																			{
																				public IFuture<Void> execute(IInternalAccess ia)
																				{
																					if(!future.isDone())
																					{
																						removeWaitingCall(callid);
																						future.setExceptionIfUndone(new RuntimeException("No reply received and timeout occurred: "+SUtil.arrayToString(msg.get(SFipa.RECEIVERS))+", "+callid)
																						{
																							public void printStackTrace()
																							{
																								Thread.dumpStack();
																								super.printStackTrace();
																							}
																						}
																						);
																					}
																					return IFuture.DONE;
																				}
																			});
					//														System.out.println("timeout triggered: "+msg);
																		}
																	}, timeout);
																}
															});
														}
													});
												}
											});
										}
									});
								}
							});
						}
					});
					
					return IFuture.DONE;
				}
			});
		}
		catch(ComponentTerminatedException e)
		{
			future.setException(e);
		}
	}

	/**
	 *  Called when the service is shut down.
	 */
	public IFuture<Void> shutdownService()
	{
		timer.cancel();
		return super.shutdownService();
	}
}

