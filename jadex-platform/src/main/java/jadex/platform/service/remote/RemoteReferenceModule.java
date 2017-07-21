package jadex.platform.service.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.WeakHashMap;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Replacement;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Synchronous;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.annotation.Uncached;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.collection.WeakValueMap;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.javaparser.SJavaParser;
import jadex.platform.service.address.TransportAddressService;
import jadex.platform.service.remote.commands.RemoteDGCAddReferenceCommand;
import jadex.platform.service.remote.commands.RemoteDGCRemoveReferenceCommand;
import jadex.platform.service.remote.replacements.DefaultEqualsMethodReplacement;
import jadex.platform.service.remote.replacements.DefaultHashcodeMethodReplacement;

/**
 *  This class implements the rmi handling. It mainly supports:
 *  - remote reference management
 *  - creation of proxy references for transferring IProxyable objects
 *  - creation of proxies on the remote side of a target object
 *  - distributed garbage collection for target (remote) objects using reference counting
 *  - management of interfaceproperties for metadata such as exclusion or replacement of methods
 */
public class RemoteReferenceModule
{
	/** Debug flag. */
	public static final boolean DEBUG = false;

	/** The default lease time. */
//	public static long DEFAULT_LEASETIME = 15000;
	public static final long DEFAULT_LEASETIME = 300000;
	
	/** leasetime*factor is used to determine when an entry should be removed. */
//	public static double WAITFACTOR = 2.2;
	public static final double WAITFACTOR = 1.5;
	
	//-------- attributes --------

	/** The remote management service. */
	protected RemoteServiceManagementService rsms;
	
	/** The cache of proxy infos (class -> proxy info). */
	protected Map<Object, ProxyInfo> proxyinfos;
	
	/** The map of target objects (rr  -> target object). */
	protected Map<RemoteReference, Object> targetobjects;
	
	/** The map of target components and services (rr  -> target comp). */
	protected Map<RemoteReference, Object> targetcomps;
	
	/** The inverse map of target object to remote references (target objects -> rr). */
	protected Map<Object, RemoteReference> remoterefs;
	
	/** The id counter. */
	protected long idcnt;

	/** The proxycount count map. (rr -> number of proxies created for rr). */
	protected Map<RemoteReference, Integer> proxycount;
	
	/** The proxy dates (date -> rr). */
	protected Map<Long, RemoteReference> proxydates;
	
	/** The remote reference holders of a object (rr -> holder (rms cid)). */
	protected Map<RemoteReference, Map<RemoteReferenceHolder, RemoteReferenceHolder>> holders;
	
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The marshal service. */
	protected IMarshalService marshalservice;
	
	/** The renew behaviour id. */
	protected long renewid;
	
	/** The remove behaviour id. */
	protected long removeid;
	
	/** The timer. */
	protected Timer	timer; 
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote reference module.
	 */
	public RemoteReferenceModule(RemoteServiceManagementService rsms, ILibraryService libservice, IMarshalService marshalservice)
	{
		this.rsms = rsms;
		this.libservice = libservice;
		this.marshalservice = marshalservice;
		this.timer	= new Timer(true);
		
		this.proxyinfos = new LRU<Object, ProxyInfo>(200);
		this.targetobjects = new HashMap<RemoteReference, Object>();
		this.targetcomps = new WeakValueMap(); // <RemoteReference, Object>
		this.remoterefs = new WeakHashMap<Object, RemoteReference> ();
		
		this.proxycount = new HashMap<RemoteReference, Integer>();
		this.proxydates = new TreeMap<Long, RemoteReference>();
		this.holders = new HashMap<RemoteReference, Map<RemoteReferenceHolder, RemoteReferenceHolder>>();
	}
	
	//-------- methods --------
	
	/**
	 *  Get a remote reference for a component for transport. 
	 *  (Called during marshalling from writer).
	 */
	public ProxyReference getProxyReference(Object target, IComponentIdentifier tmpholder, final ClassLoader cl)
	{
		checkThread();
		
		// Strip required service proxy if any, to avoid exception due to being called from wrong thread.
		if(Proxy.isProxyClass(target.getClass()))
		{
			InvocationHandler	handler	= Proxy.getInvocationHandler(target);
			if(handler instanceof BasicServiceInvocationHandler)
			{
				if(((BasicServiceInvocationHandler)handler).isRequired())
				{
					target	= ((BasicServiceInvocationHandler)handler).getDomainService();
				}
				
			}
		}
		
		// todo: should all ids of remote objects be saved in table?
		
		// Note: currently agents use model information e.g. componentviewer.viewerclass
		// to add specific properties, so that proxies are cached per agent model type due
		// to cached method call getPropertyMap().
		
		RemoteReference rr = getRemoteReference(target);
		
		// Remember that this rr is send to some other process (until the addRef message arrives).
		if(rr.isObjectReference())
			addTemporaryRemoteReference(rr, tmpholder);
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		// -> not necessary due to only single threaded access via agent thread
		
		Class<?>[] remoteinterfaces = marshalservice.getRemoteInterfaces(target, cl);
		
		if(remoteinterfaces.length==0)
			throw new RuntimeException("Proxyable object has no remote interfaces: "+target);

		Object tcid = target instanceof IExternalAccess? (Object)((IExternalAccess)target).getModel().getFullName(): target.getClass();
		ProxyInfo pi = (ProxyInfo)proxyinfos.get(tcid);
		if(pi==null)
		{
			pi = createProxyInfo(target, remoteinterfaces, cl);
			proxyinfos.put(tcid, pi);
//			System.out.println("add: "+tcid+" "+pi);
		}
		
		ProxyReference	ret	= new ProxyReference(pi, rr);

		// Check interface methods and possibly cache constant calls.
		Class<?>[] allinterfaces = SReflect.getSuperInterfaces(remoteinterfaces);
		for(int i=0; i<allinterfaces.length; i++)
		{
			Method[] methods = allinterfaces[i].getMethods();
			for(int j=0; j<methods.length; j++)
			{
				addCachedMethodValue(ret, pi, methods[j], target);
			}
		}
		// Check object methods and possibly cache constant calls.
		Method[] methods = Object.class.getMethods();
		for(int i=0; i<methods.length; i++)
		{
			addCachedMethodValue(ret, pi, methods[i], target);
		}
		
		return ret;
	}
	
	/**
	 *  Create a proxy info for a service. 
	 */
	protected ProxyInfo createProxyInfo(Object target, Class<?>[] remoteinterfaces, ClassLoader cl)
	{
		checkThread();
		// todo: dgc, i.e. remember that target is a remote object (for which a proxyinfo is sent away).
			
		ProxyInfo ret = new ProxyInfo(remoteinterfaces);
		Map<String, Object> properties = null;
		
		// Hack! as long as registry is not there
		String[]	imports	= null;
//		ClassLoader	cl	= null;
		if(target instanceof IExternalAccess)
		{
			imports	= ((IExternalAccess)target).getModel().getAllImports();
//			cl	= libservice.getClassLoader(((IExternalAccess)target).getModel().getResourceIdentifier());
			properties = ((IExternalAccess)target).getModel().getProperties();		
		}
		else if(target instanceof IService)
		{
			properties = ((IService)target).getPropertyMap();
		}
		
		Class<?> targetclass = target.getClass();
		
		// Check for excluded and synchronous methods.
		if(properties!=null)
		{
			Object ex = SJavaParser.getProperty(properties, RemoteServiceManagementService.REMOTE_EXCLUDED, imports, null, cl);
			if(ex!=null)
			{
				for(Iterator<Object> it = SReflect.getIterator(ex); it.hasNext(); )
				{
					MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
					for(int j=0; j<mis.length; j++)
					{
						ret.addExcludedMethod(mis[j]);
					}
				}
			}
			Object syn = SJavaParser.getProperty(properties, RemoteServiceManagementService.REMOTE_SYNCHRONOUS, imports, null, cl);
			if(syn!=null)
			{
				for(Iterator<Object> it = SReflect.getIterator(syn); it.hasNext(); )
				{
					MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
					for(int j=0; j<mis.length; j++)
					{
						ret.addSynchronousMethod(mis[j]);
					}
				}
			}
			Object un = SJavaParser.getProperty(properties, RemoteServiceManagementService.REMOTE_UNCACHED, imports, null, cl);
			if(un!=null)
			{
				for(Iterator<Object> it = SReflect.getIterator(un); it.hasNext(); )
				{
					MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
					for(int j=0; j<mis.length; j++)
					{
						ret.addUncachedMethod(mis[j]);
					}
				}
			}
			Object mr = SJavaParser.getProperty(properties, RemoteServiceManagementService.REMOTE_METHODREPLACEMENT, imports, null, cl);
			if(mr!=null)
			{
				for(Iterator<Object> it = SReflect.getIterator(mr); it.hasNext(); )
				{
					Object[] tmp = (Object[])it.next();
					MethodInfo[] mis = getMethodInfo(tmp[0], targetclass, false);
					for(int j=0; j<mis.length; j++)
					{
						ret.addMethodReplacement(mis[j], (IMethodReplacement)tmp[1]);
					}
				}
			}
			Object to = SJavaParser.getProperty(properties, RemoteServiceManagementService.REMOTE_TIMEOUT, imports, null, cl);
			if(to!=null)
			{
				for(Iterator<Object> it = SReflect.getIterator(to); it.hasNext(); )
				{
					Object[] tmp = (Object[])it.next();
					MethodInfo[] mis = getMethodInfo(tmp[0], targetclass, false);
					for(int j=0; j<mis.length; j++)
					{
						ret.addMethodTimeout(mis[j], ((Number)tmp[1]).longValue());
					}
				}
			}
			Object td = SJavaParser.getProperty(properties, ITargetResolver.TARGETRESOLVER, imports, null, cl);
			if(td!=null)
			{
				Class<ITargetResolver> tmp = (Class<ITargetResolver>)td;
				ret.setTargetResolverClazz(tmp);
			}
		}
		
		// Add properties from annotations.
		// Todo: merge with external properties (which precedence?)
		
		Class<?>[] allinterfaces = SReflect.getSuperInterfaces(remoteinterfaces);
		
		for(int i=0; i<allinterfaces.length; i++)
		{
			// Default timeout for interface
//			Long	deftimeout	= null;
//			if(allinterfaces[i].isAnnotationPresent(Timeout.class))
//			{
//				Timeout	ta	= (Timeout)allinterfaces[i].getAnnotation(Timeout.class);
//				deftimeout	= new Long(ta.value());
//			}
			
			boolean	allex	= allinterfaces[i].isAnnotationPresent(Excluded.class);
			boolean	allsec	= allinterfaces[i].isAnnotationPresent(SecureTransmission.class);
			
//			if(allinterfaces[i].isAnnotationPresent(TargetResolver.class))
//			{
//				TargetResolver tr = allinterfaces[i].getAnnotation(TargetResolver.class);
//				ret.setTargetResolverClazz((Class)tr.value()); 
//			}
			
			Method[]	methods	= allinterfaces[i].getDeclaredMethods();
			for(int j=0; j<methods.length; j++)
			{
				// Excluded
				if(allex || methods[j].isAnnotationPresent(Excluded.class))
				{
					ret.addExcludedMethod(new MethodInfo(methods[j]));
				}
				
				// Secured
				if(allsec || methods[j].isAnnotationPresent(SecureTransmission.class))
				{
					ret.addSecureMethod(new MethodInfo(methods[j]));
				}
				
				// Uncached
				if(methods[j].isAnnotationPresent(Uncached.class))
				{
					if(methods[j].getParameterTypes().length>0)
					{
						System.err.println("Warning: Uncached property is only applicable to methods without parameters: "+methods[j]);						
					}
					else if(void.class.equals(methods[j].getReturnType()))
					{
						System.err.println("Warning: Uncached property is not applicable to void methods: "+methods[j]);						
					}
					else if(methods[j].getReturnType().isAssignableFrom(IFuture.class))
					{
						System.err.println("Warning: Uncached property is not applicable to IFuture methods: "+methods[j]);						
					}
					else
					{
						ret.addUncachedMethod(new MethodInfo(methods[j]));
					}
				}
				
				// Synchronous
				if(methods[j].isAnnotationPresent(Synchronous.class))
				{
					if(!void.class.equals(methods[j].getReturnType()))
					{
						System.err.println("Warning: Synchronous property is only applicable to void methods: "+methods[j]);						
					}
					else
					{
						ret.addSynchronousMethod(new MethodInfo(methods[j]));
					}
				}
				
				// Replacement
				if(methods[j].isAnnotationPresent(Replacement.class))
				{
					Replacement	ra	= methods[j].getAnnotation(Replacement.class);
//					Class	rep	= SReflect.findClass0(ra.value(), null, libservice.getClassLoader());
					Class<?>	rep	= SReflect.classForName0(ra.value(), cl);
					if(rep!=null)
					{
						try
						{
							IMethodReplacement	mr	= (IMethodReplacement)rep.newInstance();
							ret.addMethodReplacement(new MethodInfo(methods[j]), mr);
						}
						catch(Exception e)
						{
							System.err.println("Warning: Replacement class "+rep.getName()+" could not be instantiated: "+e);
						}
					}
					else
					{
						System.err.println("Warning: Replacement class not found: "+ra.value());
					}
				}
				
				// Timeout
//				if(methods[j].isAnnotationPresent(Timeout.class))
//				{
//					Timeout	ta	= methods[j].getAnnotation(Timeout.class);
//					ret.addMethodTimeout(new MethodInfo(methods[j]), ta.value());
//				}
//				else if(deftimeout!=null)
//				{
//					ret.addMethodTimeout(new MethodInfo(methods[j]), deftimeout.longValue());					
//				}
//				if(methods[j].getName().indexOf("subscribe")!=-1)
//					System.out.println("hjgff");
				long to = BasicService.getMethodTimeout(remoteinterfaces, methods[j], true);
				// Do not save default value (overhead)
				if(to!=Timeout.UNSET && to!=Starter.getRemoteDefaultTimeout(rsms.getComponent().getComponentIdentifier()))
					ret.addMethodTimeout(new MethodInfo(methods[j]), to);
			}
		}
		
		// Add default replacement for equals() and hashCode().
		Method	equals	= SReflect.getMethod(Object.class, "equals", new Class[]{Object.class});
		if(ret.getMethodReplacement(equals)==null)
		{
			MethodInfo[] mis = getMethodInfo(equals, targetclass, false);
			for(int i=0; i<mis.length; i++)
			{
				ret.addMethodReplacement(mis[i], new DefaultEqualsMethodReplacement());
			}
		}
		Method	hashcode = SReflect.getMethod(Object.class, "hashCode", new Class[0]);
		if(ret.getMethodReplacement(hashcode)==null)
		{
			MethodInfo[] mis = getMethodInfo(hashcode, targetclass, true);
			for(int i=0; i<mis.length; i++)
			{
				ret.addMethodReplacement(mis[i], new DefaultHashcodeMethodReplacement());
			}
		}
		
		// Add replacement for external component features (just provides a new fascade)
//		if(target instanceof IExternalAccess)
//		{
//			Method getfeat = SReflect.getMethod(IExternalAccess.class, "getExternalComponentFeature", new Class[]{Class.class});
//			if(ret.getMethodReplacement(getfeat)==null)
//			{
//				MethodInfo[] mis = getMethodInfo(getfeat, targetclass, true);
//				for(int i=0; i<mis.length; i++)
//				{
//					ret.addMethodReplacement(mis[i], new GetComponentFeatureMethodReplacement());
//				}
//			}
//		}
//		else if(target instanceof IService)
//		{
//			Method getfeat = SReflect.getMethod(IService.class, "getExternalComponentFeature", new Class[]{Class.class});
//			if(ret.getMethodReplacement(getfeat)==null)
//			{
//				MethodInfo[] mis = getMethodInfo(getfeat, targetclass, true);
//				for(int i=0; i<mis.length; i++)
//				{
//					ret.addMethodReplacement(mis[i], new GetComponentFeatureMethodReplacement());
//				}
//			}
//		}
		
		// Add getClass as excluded. Otherwise the target class must be present on
		// the computer which only uses the proxy.
		Method getclass = SReflect.getMethod(Object.class, "getClass", new Class[0]);
		if(ret.getMethodReplacement(getclass)==null)
		{
			ret.addExcludedMethod(new MethodInfo(getclass));
		}
		
		return ret;
	}
	
	/**
	 *  Add a cached method value to the proxy info.
	 */
	public static void addCachedMethodValue(ProxyReference pr, ProxyInfo pi, Method m, Object target)
	{
		// only cache when not excluded, not cached and not replaced
		if(!pi.isUncached(m) && !pi.isExcluded(m) && !pi.isReplaced(m)) 
		{
			Class<?> rt = m.getReturnType();
			Class<?>[] ar = m.getParameterTypes();
			
			if(void.class.equals(rt))
			{
//				System.out.println("Warning, void method call will be executed asynchronously: "+type+" "+methods[i].getName());
			}
			else if(!(SReflect.isSupertype(IFuture.class, rt)))
			{
//				if(ar.length>0)
//				{
////					System.out.println("Warning, service method is blocking: "+type+" "+methods[i].getName());
//				}
//				else
				if(ar.length==0)
				{
					// Invoke method to get constant return value.
					try
					{
//						System.out.println("Calling for caching: "+m);
						Object val = m.invoke(target, new Object[0]);
						pr.putCache(m.getName(), val);
					}
					catch(Exception e)
					{
//						System.err.println("Warning, constant service method threw exception: "+m);
//						e.printStackTrace();
//						throw(new RuntimeException(e));
						pr.putCache(m.getName(), e);
					}
				}
			}
		}
	}
	
	/**
	 *  Get method info.
	 */
	public static MethodInfo[] getMethodInfo(Object iden, Class<?> targetclass, boolean noargs)
	{
		MethodInfo[] ret;
		
		if(iden instanceof String)
		{
			if(noargs)
			{
				Method	method	= SReflect.getMethod(targetclass, (String)iden, new Class[0]);
				if(method==null)
					method	= SReflect.getMethod(Object.class, (String)iden, new Class[0]);
				
				if(method!=null)
				{
					ret = new MethodInfo[]{new MethodInfo(method)};
				}
				else
				{
					throw new RuntimeException("Method not found: "+iden);
				}
			}
			else
			{
				Method[] ms = SReflect.getMethods(targetclass, (String)iden);
				if(ms.length==0)
				{
					ms = SReflect.getMethods(Object.class, (String)iden);
				}
				
				if(ms.length==1)
				{
					ret = new MethodInfo[]{new MethodInfo(ms[0])};
				}
				else if(ms.length>1)
				{
					// Exclude all if more than one fits?!
					ret = new MethodInfo[ms.length];
					for(int i=0; i<ret.length; i++)
						ret[i] = new MethodInfo(ms[i]);
					
					// Check if the methods are equal = same signature (e.g. defined in different interfaces)
//					boolean eq = true;
//					Method m0 = ms[0];
//					for(int i=1; i<ms.length && eq; i++)
//					{
//						if(!hasEqualSignature(m0, ms[i]))
//							eq = false;
//					}
//					if(!eq)
//						throw new RuntimeException("More than one method with the name availble: "+tmp);
//					else
//						ret = new MethodInfo(m0);
				}
				else
				{
					throw new RuntimeException("Method not found: "+iden);
				}
			}
		}
		else
		{
			ret = new MethodInfo[]{new MethodInfo((Method)iden)};
		}
		
		return ret;
	}
	
	/**
	 *  Get a remote reference.
	 *  @param target The (local) remote object.
	 */
	protected RemoteReference getRemoteReference(Object target)
	{
		return getRemoteReference(target, target, true);
	}
	
	/**
	 *  Get a remote reference.
	 *  @param target The (local) remote object.
	 */
	protected RemoteReference getRemoteReference(Object target, Object orig, boolean add)
	{
		checkThread();
		RemoteReference ret = (RemoteReference)remoterefs.get(target);
		
		// Create a remote reference if not yet available.
		if(ret==null)
		{
			if(Proxy.isProxyClass(target.getClass()))
			{
				Object handler = Proxy.getInvocationHandler(target);
				if(handler instanceof BasicServiceInvocationHandler)
				{
					BasicServiceInvocationHandler bsh = (BasicServiceInvocationHandler)handler;
					Object ser = bsh.getService();
					// Has to look into service as could be nested remote handler inside.
					if(ser instanceof IService)
					{
						ret = getRemoteReference(ser, orig, false);
					}
					else 
					{
						ret = new RemoteReference(TransportAddressService.getTransportComponentIdentifier(rsms.getRMSComponentIdentifier(), rsms.getAddresses()), bsh.getServiceIdentifier());
					}
				}
				else if(handler instanceof RemoteMethodInvocationHandler)
				{
					RemoteMethodInvocationHandler	rmih	= (RemoteMethodInvocationHandler)Proxy.getInvocationHandler(target);
					ret	= rmih.pr.getRemoteReference();
				}
			}
			else if(target instanceof IExternalAccess)
			{
				ret = new RemoteReference(TransportAddressService.getTransportComponentIdentifier(rsms.getRMSComponentIdentifier(), rsms.getAddresses()), ((IExternalAccess)target).getComponentIdentifier());
//				System.out.println("component ref: "+ret);
			}
			else if(target instanceof IService)
			{
				ret = new RemoteReference(TransportAddressService.getTransportComponentIdentifier(rsms.getRMSComponentIdentifier(), rsms.getAddresses()), ((IService)target).getServiceIdentifier());
//				System.out.println("service ref: "+ret);
			}
			else if(target instanceof ServiceInfo)
			{
				ServiceInfo si = (ServiceInfo)target;
				if(Proxy.isProxyClass(si.getDomainService().getClass()))
				{
					ret = getRemoteReference(si.getDomainService(), orig, false);
				}
				else
				{
					ret = new RemoteReference(TransportAddressService.getTransportComponentIdentifier(rsms.getRMSComponentIdentifier(), rsms.getAddresses()), ((ServiceInfo)target).getManagementService().getServiceIdentifier());
	//				System.out.println("service ref: "+ret);
				}
			}
			else
			{
				ret = generateRemoteReference();
			}
			
			if(ret!=null && add)
			{
//				System.out.println("Adding rr: "+ret+" "+target);
				remoterefs.put(orig, ret);
				targetobjects.put(ret, orig);
			}
		}

//		System.out.println("rr: "+target+" "+ret);
		
		return ret;
	}
	
	/**
	 *  Delete a remote reference.
	 *  @param rr The remote reference.
	 */
	protected void deleteRemoteReference(RemoteReference rr)
	{
		checkThread();
		Object target = targetobjects.remove(rr);
		remoterefs.remove(target);
//		System.out.println("Removing rr: "+rr+" "+target);
	}
	
	/**
	 *  Shutdown the module.
	 *  Sends notifications to all 
	 */
	protected IFuture<Void>	shutdown()
	{
		Future<Void>	ret	= new Future<Void>();
		checkThread();
		timer.cancel();
		timer	= null;
		
		// todo: wait until all remote ref messages have been sent?!
		
		// wait no longer than 5 seconds for unregistering remote references.
		RemoteReference[] rrs = (RemoteReference[])proxycount.keySet().toArray(new RemoteReference[0]);
//		CounterResultListener<Void> crl = new CounterResultListener<Void>(rrs.length, true,
//			new TimeoutResultListener<Void>(10000, rsms.getComponent(), true, new DelegationResultListener<Void>(ret)));

//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("shutti (res)");
//				super.resultAvailable(null);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("shutti (ex)");
//				super.exceptionOccurred(exception);
//			}
//		}));
		
//		System.out.println("shut: "+SUtil.arrayToString(rrs));
		
		for(int i=0; i<rrs.length; i++)
		{
			// Cannot wait until all send remove refs return (platforms may have vanished)
			sendRemoveRemoteReference(rrs[i]);//.addResultListener(crl);
		}
		ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Get a target object per remote reference.
	 *  @param rr The remote reference.
	 *  @return The target object.
	 */
	public IFuture<Object> getTargetObject(RemoteReference rr)
	{
		checkThread();
		final Future<Object> ret = new Future<Object>();
				
		if(rr.getTargetIdentifier() instanceof IServiceIdentifier)
		{
			IServiceIdentifier sid = (IServiceIdentifier)rr.getTargetIdentifier();
			
			// fetch service via its id
			SServiceProvider.getService(rsms.getComponent(), sid)
				.addResultListener(new DelegationResultListener<Object>(ret));
		}
		else if(rr.getTargetIdentifier() instanceof IComponentIdentifier)
		{
			final IComponentIdentifier cid = (IComponentIdentifier)rr.getTargetIdentifier();
			
			// fetch component via target component id
//			rsms.getComponent().scheduleStep(new IComponentStep<IExternalAccess>()
//			{
//				public IFuture<IExternalAccess> execute(IInternalAccess ia)
//				{
//					final Future<IExternalAccess> ret = new Future<IExternalAccess>();
//					SServiceProvider.getService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
//	//						.addResultListener(component.createResultListener(new IResultListener()
//					{
//						public void customResultAvailable(IComponentManagementService cms)
//						{
//	//						ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//	//						next.setProperty("debugsource", "RemoteReferenceModule.getTargetObject()");
//							
//							// fetch target component via component identifier.
//							cms.getExternalAccess(cid).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
//						}
//					});
//				}
//			}).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret));
			
			SServiceProvider.getService(rsms.getComponent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms) 
				{
					// fetch target component via component identifier.
					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
					{
						public void customResultAvailable(IExternalAccess result)
						{
							ret.setResult(result);
						}
					});
				}
			});
		}
		else //(rr.getTargetIdentifier() instanceof String)
		{
			Object o = targetobjects.get(rr);
			if(o!=null)
			{
				ret.setResult(o);
			}
			else
			{
				ret.setException(new RuntimeException("Remote object not found: "+rr));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove a target object.
	 *  @param rr The remote reference.
	 *  @return The target object.
	 */
	protected Object removeTargetObject(RemoteReference rr)
	{
		checkThread();
		return targetobjects.remove(rr);
	}
	
	/**
	 *  Generate a remote reference.
	 *  @return The remote reference.
	 */
	protected RemoteReference generateRemoteReference()
	{
		checkThread();
		return new RemoteReference(TransportAddressService.getTransportComponentIdentifier(rsms.getRMSComponentIdentifier(), rsms.getAddresses()), ""+idcnt++);
	}
	
	//-------- management of proxies --------

	/**
	 *  Get a proxy for a proxy reference.
	 *  @param pr The proxy reference.
	 */
	public Object getProxy(ProxyReference pr, ClassLoader classloader)
	{
		checkThread();
		Object ret;
		
//		RemoteReference rr = pi.getRemoteReference();
		
		// If is local return local target object.
		if(pr.getRemoteReference().getRemoteManagementServiceIdentifier().equals(rsms.getRMSComponentIdentifier()))
		{
			ret = targetobjects.containsKey(pr.getRemoteReference())
				? targetobjects.get(pr.getRemoteReference())
				: targetcomps.get(pr.getRemoteReference());
			if(ret==null)
				System.out.println("No object for reference: "+pr.getRemoteReference());
		}
		// Else return new or old proxy.
		else
		{
//			System.out.println("interfaces of proxy: "+SUtil.arrayToString(pi.getTargetInterfaces()));
			
			Class<?>[] tmp = pr.getProxyInfo().getTargetInterfaces();
			Class<?>[] interfaces = new Class[tmp.length+1];
			System.arraycopy(tmp, 0, interfaces, 0, tmp.length);
			interfaces[tmp.length] = IFinalize.class;
			
//			ret = Proxy.newProxyInstance(libservice.getClassLoader(), 
//				interfaces, new RemoteMethodInvocationHandler(rsms, pr));
			
			// Which classloader to use for proxy creation?
			// a) from sender: allows receiver to have all (also implementations) what sender has
			// b) from receiver: so only interfaces are available but allows compatibility of sender
			//    and receiver even if they use different versions
			// c) enhance xml to annotate the resource the classes belong to (best solution)
			// currently just uses the 'global' platform classloader 
			
			ret = ProxyFactory.newProxyInstance(classloader, 
				interfaces, new RemoteMethodInvocationHandler(rsms, pr));
			
			incProxyCount(pr.getRemoteReference());
			
//			ret = proxies.get(rr);
//			if(ret==null)
//			{
//				synchronized(this)
//				{
//					ret = proxies.get(rr);
//					if(ret==null)
//					{
//						ret = Proxy.newProxyInstance(rsms.getComponent().getModel().getClassLoader(), 
//							pi.getTargetInterfaces(), new RemoteMethodInvocationHandler(rsms, pi));
//						proxies.put(rr, ret);
//						
//						sendAddRemoteReference(rr);
//					}
//				}
//			}
		}
		
//		System.out.println("resolved proxy ref to: "+ret+" "+pr.getRemoteReference());
		
		return ret;
	}
	
	//-------- dgc --------
	
	/**
	 *  Increment the proxy count for a remote reference.
	 *  @param rr The remote reference for the proxy.
	 */
	protected void incProxyCount(RemoteReference rr)
	{
		if(DEBUG)
		{
			if(proxycount.size()!=proxydates.size())
				System.out.println("ipc start");
		}
		
		checkThread();
		// Only keep track of proxies for java objects.
		// Components and services are not subject of gc.
		
		if(rr.isObjectReference())
		{
			boolean notify = false;
//			RemoteReference origrr = (RemoteReference)proxycountkeys.get(rr);
			Integer cnt = (Integer)proxycount.remove(rr);
			if(cnt==null)
			{
//				proxycountkeys.put(rr, rr);
				proxycount.put(rr, Integer.valueOf(1));
				notify = true;
				
				// todo: transfer lease time interval?!
//				rr.setExpiryDate(clock.getTime()+DEFAULT_LEASETIME);
				proxydates.put(Long.valueOf(System.currentTimeMillis()+DEFAULT_LEASETIME), rr);
				
				// Initiate check procedure.
				startRenewalBehaviour();
			}
			else
			{
				proxycount.put(rr, Integer.valueOf(cnt.intValue()+1));
			}
				
	//		System.out.println("Add proxy: "+rr+" "+cnt);
			
			if(notify)
				sendAddRemoteReference(rr);
			
			if(DEBUG)
			{
				if(proxycount.size()!=proxydates.size())
					System.out.println("ipc end");
			}
		}
	}
	
	/**
	 *  Decrease the proxy count for a remote reference.
	 *  @param rr The remote reference for the proxy.
	 */
	protected void decProxyCount(RemoteReference rr)
	{
		if(DEBUG)
		{
			if(proxycount.size()!=proxydates.size())
				System.out.println("dpc start");
		}
		
		checkThread();
		// Only keep track of proxies for java objects.
		// Components and services are not subject of gc.
		
		if(rr.isObjectReference())
		{
			boolean notify = false;
//			RemoteReference origrr = (RemoteReference)proxycountkeys.remove(rr);
			Integer cnt = (Integer)proxycount.remove(rr);
			int nv = cnt.intValue()-1;
			if(nv==0)
			{
				notify = true;
				proxydates.values().remove(rr);
//				System.out.println("Remove proxy: "+rr+" "+nv);
			}
			else
			{
//				proxycountkeys.put(rr, rr);
				proxycount.put(rr, Integer.valueOf(nv));
			}
				
//			System.out.println("Remove proxy: "+rr+" "+nv+" "+proxycount);
			if(notify)
				sendRemoveRemoteReference(rr);
		}
		
		if(DEBUG)
		{
			if(proxycount.size()!=proxydates.size())
				System.out.println("dpc end");
		}
	}
	
	/**
	 *  Start the removal behavior.
	 */
	protected void startRenewalBehaviour()
	{
		final long renewid = ++this.renewid;
		
		rsms.getComponent().scheduleStep(new IComponentStep<Void>()
		{
			@Classname("startRenewal")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(renewid == RemoteReferenceModule.this.renewid)
				{
//					final RemoteServiceManagementAgent agent = (RemoteServiceManagementAgent)ia;
					
					if(DEBUG)
					{
						if(proxycount.size()!=proxydates.size())
							System.out.println("srb start");
					}
					
//					System.out.println("Starting renewal behavior: "+removeid);
//					if(proxydates.size()>0)
//					{
//						System.out.println("Checking proxies: "+proxydates.size()+" "+proxycount.size());
//						for(Iterator it=proxydates.keySet().iterator(); it.hasNext(); )
//						{
//							Long key = (Long)it.next();
//							System.out.println("\t "+key+" "+" "+System.currentTimeMillis()+" "+proxydates.get(key));
//						}
//					}
					
					long diff = 0;
					Long[] dates = (Long[])proxydates.keySet().toArray(new Long[proxydates.size()]);
					for(int i=0; i<dates.length; i++)
					{
						diff = dates[i].longValue()-System.currentTimeMillis();
						if(diff<=0)
						{
							final RemoteReference rr = (RemoteReference)proxydates.remove(dates[i]);
//							System.out.println("renewal sent for: "+rr);
							IResultListener<Void> lis = ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									if(DEBUG)
										System.out.println("Renewed successfully lease for: "+rr);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									if(DEBUG)
										System.out.println("Failed to renew lease for: "+rr);
								}
							});
							sendAddRemoteReference(rr).addResultListener(lis);
							
							long expirydate = System.currentTimeMillis()+DEFAULT_LEASETIME;
							proxydates.put(Long.valueOf(expirydate), rr);
							diff = DEFAULT_LEASETIME;
						}
						else
						{
							break;
						}
					}
					
//					System.out.println("prxy: "+proxycount);
					
					if(DEBUG)
					{
						if(proxycount.size()!=proxydates.size())
							System.out.println("srb end");
					}
					
					if(proxycount.size()>0 && diff>0 && timer!=null) // can be null if shutdown was already called
					{
//						System.out.println("renewal behaviour waiting: "+diff);
						final IComponentStep<Void>	step = this;
						timer.schedule(new TimerTask()
						{
							public void run()
							{
								rsms.getComponent().scheduleStep(step);
							}
						}, diff);
					}
				}
				
//				System.out.println("renewal behaviour exit");
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Start removal behavior for expired holders.
	 */
	protected void startRemovalBehaviour()
	{
		final long removeid = ++this.removeid;
		
		rsms.getComponent().scheduleStep(new IComponentStep<Void>()
		{
			@Classname("startRemoval")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(removeid == RemoteReferenceModule.this.removeid)
				{
//					System.out.println("Starting removal behavior: "+removeid);
//					if(holders.size()>0)
//					{
//						System.out.println("Checking holders: ");
//						for(Iterator it=holders.keySet().iterator(); it.hasNext(); )
//						{
//							Object key = it.next();
//							System.out.println("\t "+key+" "+((Map)holders.get(key)).keySet());
//						}
//					}
					
					for(Iterator<RemoteReference> it=holders.keySet().iterator(); it.hasNext(); )
					{
						RemoteReference rr = (RemoteReference)it.next();
						Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
						for(Iterator<RemoteReferenceHolder> it2=hds.keySet().iterator(); it2.hasNext(); )
						{
							RemoteReferenceHolder rrh = it2.next();
							if(System.currentTimeMillis() > rrh.getExpiryDate()+DEFAULT_LEASETIME*WAITFACTOR)
							{
								if(DEBUG)
									System.out.println("Removing expired holder: "+rr+" "+rrh+" "+rrh.getExpiryDate()+" "+System.currentTimeMillis());
								it2.remove();
//								hds.remove(rrh);
								if(hds.size()==0)
								{
									it.remove();
//									holders.remove(rr);
									deleteRemoteReference(rr);
								}
							}
						}
					}
					
					if(holders.size()>0 && timer!=null) // can be null if shutdown was already called
					{
						final IComponentStep<Void>	step	= this;
						timer.schedule(new TimerTask()
						{
							public void run()
							{
								rsms.getComponent().scheduleStep(step);
							}
						}, 5000);
					}
				}
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Send addRef to the origin process of the remote reference.
	 *  @param rr The remote reference.
	 */
	public Future<Void> sendAddRemoteReference(final RemoteReference rr)
	{
		checkThread();
		// DGC: notify rr origin that a new proxy of target object exists
		// todo: handle failures!
		final Future<Void> ret = new Future<Void>();
		
//		System.out.println("send add: "+rr);
		final String callid = SUtil.createUniqueId(rsms.getRMSComponentIdentifier().getLocalName());
		RemoteDGCAddReferenceCommand com = new RemoteDGCAddReferenceCommand(rr, rsms.getRMSComponentIdentifier(), callid);
		Future<Object> fut = new Future<Object>();
		fut.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(null);
			}
		});
		rsms.sendMessage(rr.getRemoteManagementServiceIdentifier(), null, com, callid, Starter.getRemoteDefaultTimeout(rsms.getComponent().getComponentIdentifier()), fut, null, null);
		
		return ret;
	}
	
	/**
	 *  Send removeRef to the origin process of the remote reference.
	 *  @param rr The remote reference.
	 */
	public Future<Void> sendRemoveRemoteReference(final RemoteReference rr)
	{
		checkThread();
		// DGC: notify rr origin that a new proxy of target object exists
		// todo: handle failures!
		final Future<Void> ret = new Future<Void>();
//		System.out.println("send rem: "+rr);
		final String callid = SUtil.createUniqueId(rsms.getRMSComponentIdentifier().getLocalName());
		RemoteDGCRemoveReferenceCommand com = new RemoteDGCRemoveReferenceCommand(rr, rsms.getRMSComponentIdentifier(), callid);
//		System.out.println("send start: "+rr);
//		future.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				System.out.println("send end: "+rr);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("send ex: "+rr);
//			}
//		});
		
		Future<Object> fut = new Future<Object>();
		fut.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(null);
			}
		});
		rsms.sendMessage(rr.getRemoteManagementServiceIdentifier(), null, com, callid, Starter.getRemoteDefaultTimeout(rsms.getComponent().getComponentIdentifier()), fut, null, null);
		return ret;
	}
	
	/**
	 *  Add a new temporary holder to a remote object.
	 *  @param rr The remote reference.
	 *  @param holder The cid of the holding rms.
	 */
	protected void addTemporaryRemoteReference(final RemoteReference rr, final IComponentIdentifier holder)
	{
		checkThread();
		Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
		if(hds==null)
		{
			hds = new HashMap<RemoteReferenceHolder, RemoteReferenceHolder>();
			holders.put(rr, hds);
			startRemovalBehaviour();
		}
		
		long expirydate = System.currentTimeMillis()+DEFAULT_LEASETIME;
		TemporaryRemoteReferenceHolder newth = new TemporaryRemoteReferenceHolder(holder, expirydate);
		TemporaryRemoteReferenceHolder oldth = (TemporaryRemoteReferenceHolder)hds.get(newth);
		if(oldth==null)
		{
			hds.put(newth, newth);
		}
		else
		{
			// Update existing holder.
			oldth.setNumber(oldth.getNumber()+1);
			oldth.setExpiryDate(expirydate);
		}
//		System.out.println("Holders for (temp add): "+rr+" add: "+holder+" "+hds.keySet());
	}
	
	/**
	 *  Add a new holder to a remote object.
	 *  @param rr The remote reference.
	 *  @param holder The cid of the holding rms.
	 */
	public void addRemoteReference(final RemoteReference rr, final IComponentIdentifier holder)
	{
		checkThread();
		Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
		if(hds==null)
		{
			hds = new HashMap<RemoteReferenceHolder, RemoteReferenceHolder>();
			holders.put(rr, hds);
			startRemovalBehaviour();
		}
		
		long expirydate = System.currentTimeMillis()+DEFAULT_LEASETIME;
		RemoteReferenceHolder newh = new RemoteReferenceHolder(holder, expirydate);
		RemoteReferenceHolder oldh = (RemoteReferenceHolder)hds.get(newh);
		
		if(oldh==null)
		{
//			throw new RuntimeException("Holder already contained: "+holder);
			hds.put(newh, newh);
		}
		else
		{
			// Renew expiry date of existing holder.
			oldh.setExpiryDate(expirydate);
			if(DEBUG)
				System.out.println("renewed lease for: "+rr+" "+oldh);
		}
		
		// Decrement number (and possibly remove) temporary holder.
		TemporaryRemoteReferenceHolder th = (TemporaryRemoteReferenceHolder)hds.get(
			new TemporaryRemoteReferenceHolder(holder, 0));
		if(th!=null)
		{
			th.setNumber(th.getNumber()-1);
			if(th.getNumber()==0)
			{
				hds.remove(th); // hds.size() != 0 
			}
		}

//		System.out.println("Holders for (add): "+rr+" add: "+holder+" "+hds.keySet());
	}
	
	/**
	 *  Remove a new holder from a remote object.
	 *  @param rr The remote reference.
	 *  @param holder The cid of the holding rms.
	 */
	public void removeRemoteReference(final RemoteReference rr, final IComponentIdentifier holder)
	{
		checkThread();
		Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
//		if(hds==null || !hds.contains(holder))
//			throw new RuntimeException("Holder not contained: "+holder);

//		System.out.println("Holders for (rem): "+result+" rem: "+holder+" "+hds);
		
		if(hds!=null)
		{
			hds.remove(new RemoteReferenceHolder(holder, 0));
			if(hds.size()==0)
			{
				holders.remove(rr);
				deleteRemoteReference(rr);
			}
		}
	}
	
	/**
	 *  Check if correct thread access.
	 */
	protected void checkThread()
	{
		if(DEBUG)
		{
			// Hack!
			if(rsms.getComponent().isExternalThread())
			{
				System.out.println("wrong thread: "+Thread.currentThread());
				Thread.dumpStack();
			}
		}
	}

	/**
	 *  Get the marshalservice.
	 *  @return the marshalservice.
	 */
	public IMarshalService getMarshalService()
	{
		return marshalservice;
	}

	/**
	 *  Get the libservice.
	 *  @return the libservice.
	 */
	public ILibraryService getLibraryService()
	{
		return libservice;
	}
	
	
//	Code for retrying a command
//	rsms.getComponent().scheduleStep(new ICommand()
//	{
//		public void execute(Object args)
//		{
//			final RemoteServiceManagementAgent agent = (RemoteServiceManagementAgent)args;
//			final int[] retrycnt = new int[1];
//			IResultListener lis = agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
//			{
//				public void resultAvailable(Object source, Object result)
//				{
//					long expirydate = clock.getTime()+DEFAULT_LEASETIME;
//					proxydates.put(new Long(expirydate), rr);
//				}
//				
//				public void exceptionOccurred(Object source, Exception exception)
//				{
//					// retry 2 times
//					if(retrycnt[0]<2)
//						sendAddRemoteReference(rr).addResultListener(this);
//					else
//						System.out.println("Failed to renew lease for: "+rr);
//					retrycnt[0]++;
//				}
//			});
//		}
//	});
}
