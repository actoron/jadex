package jadex.platform.service.serialization;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.component.impl.remotecommands.IMethodReplacement;
import jadex.bridge.component.impl.remotecommands.ProxyInfo;
import jadex.bridge.component.impl.remotecommands.ProxyReference;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.component.streams.InputConnection;
import jadex.bridge.component.streams.LocalInputConnectionHandler;
import jadex.bridge.component.streams.LocalOutputConnectionHandler;
import jadex.bridge.component.streams.OutputConnection;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IBrokenProxy;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Replacement;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Synchronous;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.annotation.Uncached;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.remote.ServiceInputConnectionProxy;
import jadex.bridge.service.types.remote.ServiceOutputConnectionProxy;
import jadex.commons.IChangeListener;
import jadex.commons.IRemotable;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.ImmutableProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.javaparser.SJavaParser;

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
	//-------- constants --------
	
	/** The predefined reference settings (clazz->boolean (is reference)). */
	public static final Map<Class<?>, boolean[]> REFERENCES;
	
	static
	{
		Map<Class<?>, boolean[]>	refs	= new HashMap<Class<?>, boolean[]>();
		boolean[] tt = new boolean[]{true, true};
		refs.put(IRemotable.class, tt);
		refs.put(IResultListener.class, tt);
		refs.put(IIntermediateResultListener.class, tt);
		refs.put(IFuture.class, tt);
		refs.put(IIntermediateFuture.class, tt);
		refs.put(IChangeListener.class, tt);
		refs.put(IRemoteChangeListener.class, tt);
		refs.put(ClassLoader.class, tt);
		
		boolean[] tf = new boolean[]{true, false};
		refs.put(URL.class, tf);
		refs.put(InetAddress.class, tf);
		refs.put(Inet4Address.class, tf);
		refs.put(Inet6Address.class, tf);
		refs.put(IComponentIdentifier.class, tf);
		refs.put(BasicComponentIdentifier.class, tf);
		Class<?> ti = SReflect.classForName0("jadex.xml.TypeInfo", RemoteReferenceModule.class.getClassLoader());
		if(ti!=null)
			refs.put(ti, tf);
		
		REFERENCES = Collections.unmodifiableMap(refs);
	}
	
//	/** Debug flag. */
//	public static final boolean DEBUG = false;
//
//	/** The default lease time. */
//	public static final long DEFAULT_LEASETIME = 300000;
//	
//	/** leasetime*factor is used to determine when an entry should be removed. */
//	public static final double WAITFACTOR = 1.5;
	
	//-------- attributes --------
	
	/** The platform cid. */
	protected IComponentIdentifier	platform;
	
	/** The reference class cache (clazz->boolean (is reference)). */
	protected Map<Class<?>, boolean[]> references;

//	/** The remote management service. */
//	protected RemoteServiceManagementService rsms;
//	
	/** The cache of proxy infos (class -> proxy info). */
	protected Map<Object, ProxyInfo> proxyinfos;
//	
//	/** The map of target objects (rr  -> target object). */
//	protected Map<RemoteReference, Object> targetobjects;
//	
//	/** The map of target components and services (rr  -> target comp). */
//	protected Map<RemoteReference, Object> targetcomps;
//	
//	/** The inverse map of target object to remote references (target objects -> rr). */
//	protected Map<Object, RemoteReference> remoterefs;
//	
//	/** The id counter. */
//	protected long idcnt;
//
//	/** The proxycount count map. (rr -> number of proxies created for rr). */
//	protected Map<RemoteReference, Integer> proxycount;
//	
//	/** The proxy dates (date -> rr). */
//	protected Map<Long, RemoteReference> proxydates;
//	
//	/** The remote reference holders of a object (rr -> holder (rms cid)). */
//	protected Map<RemoteReference, Map<RemoteReferenceHolder, RemoteReferenceHolder>> holders;
//	
//	
//	/** The library service. */
//	protected ILibraryService libservice;
//	
//	/** The marshal service. */
//	protected IMarshalService marshalservice;
//	
//	/** The renew behaviour id. */
//	protected long renewid;
//	
//	/** The remove behaviour id. */
//	protected long removeid;
//	
//	/** The timer. */
//	protected Timer	timer; 
	
	/** The clone processors. */
	protected List<ITraverseProcessor> processors;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote reference module.
	 */
	public RemoteReferenceModule(IComponentIdentifier platform)
	{
		this.platform	= platform;
		references = Collections.synchronizedMap(new LRU<Class<?>, boolean[]>(500));

//		this.rsms = rsms;
//		this.libservice = libservice;
//		this.marshalservice = marshalservice;
//		this.timer	= new Timer(true);
//		
		this.proxyinfos = new LRU<Object, ProxyInfo>(200);
//		this.targetobjects = new HashMap<RemoteReference, Object>();
//		this.targetcomps = new WeakValueMap(); // <RemoteReference, Object>
//		this.remoterefs = new WeakHashMap<Object, RemoteReference> ();
//		
//		this.proxycount = new HashMap<RemoteReference, Integer>();
//		this.proxydates = new TreeMap<Long, RemoteReference>();
//		this.holders = new HashMap<RemoteReference, Map<RemoteReferenceHolder, RemoteReferenceHolder>>();
	}
	
	//-------- methods --------
	
	/**
	 *  Get a remote reference for a component for transport. 
	 *  (Called during marshalling from writer).
	 */
	public ProxyReference getProxyReference(Object target, IComponentIdentifier tmpholder, final ClassLoader cl)
	{
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
		
//		// Remember that this rr is sent to some other process (until the addRef message arrives).
//		if(rr.isObjectReference())
//		{
//			addTemporaryRemoteReference(rr, tmpholder);
//		}
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		// -> not necessary due to only single threaded access via agent thread
		

		Object tcid = target instanceof IExternalAccess? (Object)((IExternalAccess)target).getModel().getFullName(): target.getClass();
		ProxyInfo pi;
		
		// Use saved proxyinfo if the proxy itself is broken 
		if(target instanceof IBrokenProxy)
		{
			pi = ((IBrokenProxy)target).getProxyInfo();
		}
		else
		{
			Class<?>[] remoteinterfaces = getRemoteInterfaces(target, cl);
			
			if(remoteinterfaces.length==0)
				throw new RuntimeException("Proxyable object has no remote interfaces: "+target);
			
			synchronized(this)
			{
				pi = (ProxyInfo)proxyinfos.get(tcid);
				if(pi==null)
				{
					pi = createProxyInfo(target, remoteinterfaces, cl, platform);
					proxyinfos.put(tcid, pi);
		//			System.out.println("add: "+tcid+" "+pi);
				}
			}
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
	protected static ProxyInfo createProxyInfo(Object target, Class<?>[] remoteinterfaces, ClassLoader cl, IComponentIdentifier platform)
	{
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
			Object ex = SJavaParser.getProperty(properties, Excluded.class.getName(), imports, null, cl);
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
			Object syn = SJavaParser.getProperty(properties, Synchronous.class.getName(), imports, null, cl);
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
			Object un = SJavaParser.getProperty(properties, Uncached.class.getName(), imports, null, cl);
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
			Object mr = SJavaParser.getProperty(properties, Replacement.class.getName(), imports, null, cl);
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
			Object to = SJavaParser.getProperty(properties, Timeout.class.getName(), imports, null, cl);
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
				@SuppressWarnings("unchecked")
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
				if(to!=Timeout.UNSET && to!=Starter.getRemoteDefaultTimeout(platform))
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
		RemoteReference ret;// = (RemoteReference)remoterefs.get(target);
		
		// Create a remote reference if not yet available.
//		if(ret==null)
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
						ret = new RemoteReference(bsh.getServiceIdentifier().getProviderId(), bsh.getServiceIdentifier());
					}
				}
				else if(handler instanceof RemoteMethodInvocationHandler)
				{
					RemoteMethodInvocationHandler	rmih	= (RemoteMethodInvocationHandler)Proxy.getInvocationHandler(target);
					ret	= rmih.pr.getRemoteReference();
				}
				else
				{
					// TODO: can not happen?
					throw new UnsupportedOperationException("Proxy type not supproetd: "+target);
//					ret = generateRemoteReference();
				}
			}
			else if(target instanceof IExternalAccess)
			{
				ret = new RemoteReference(((IExternalAccess)target).getComponentIdentifier(), ((IExternalAccess)target).getComponentIdentifier());
//				System.out.println("component ref: "+ret);
			}
			else if(target instanceof IService)
			{
				ret = new RemoteReference(((IService)target).getServiceIdentifier().getProviderId(), ((IService)target).getServiceIdentifier());
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
					ret = new RemoteReference(((ServiceInfo)target).getManagementService().getServiceIdentifier().getProviderId(), ((ServiceInfo)target).getManagementService().getServiceIdentifier());
	//				System.out.println("service ref: "+ret);
				}
			}
			else
			{
				throw new UnsupportedOperationException("Plain remote objects not yet supported: "+target);
//				ret = generateRemoteReference();
			}
			
//			if(ret!=null && add)
//			{
//				System.out.println("Adding rr: "+ret+" "+target);
//				remoterefs.put(orig, ret);
//				targetobjects.put(ret, orig);
//			}
		}

//		System.out.println("rr: "+target+" "+ret);
		
		return ret;
	}
	
//	/**
//	 *  Delete a remote reference.
//	 *  @param rr The remote reference.
//	 */
//	protected void deleteRemoteReference(RemoteReference rr)
//	{
//		checkThread();
//		Object target = targetobjects.remove(rr);
//		remoterefs.remove(target);
////		System.out.println("Removing rr: "+rr+" "+target);
//	}
	
//	/**
//	 *  Shutdown the module.
//	 *  Sends notifications to all 
//	 */
//	protected IFuture<Void>	shutdown()
//	{
//		Future<Void>	ret	= new Future<Void>();
//		checkThread();
//		timer.cancel();
//		timer	= null;
//		
//		// todo: wait until all remote ref messages have been sent?!
//		
//		// wait no longer than 5 seconds for unregistering remote references.
//		RemoteReference[] rrs = (RemoteReference[])proxycount.keySet().toArray(new RemoteReference[0]);
////		CounterResultListener<Void> crl = new CounterResultListener<Void>(rrs.length, true,
////			new TimeoutResultListener<Void>(10000, rsms.getComponent(), true, new DelegationResultListener<Void>(ret)));
//
////		{
////			public void customResultAvailable(Void result)
////			{
////				System.out.println("shutti (res)");
////				super.resultAvailable(null);
////			}
////			public void exceptionOccurred(Exception exception)
////			{
////				System.out.println("shutti (ex)");
////				super.exceptionOccurred(exception);
////			}
////		}));
//		
////		System.out.println("shut: "+SUtil.arrayToString(rrs));
//		
//		for(int i=0; i<rrs.length; i++)
//		{
//			// Cannot wait until all send remove refs return (platforms may have vanished)
//			sendRemoveRemoteReference(rrs[i]);//.addResultListener(crl);
//		}
//		ret.setResult(null);
//		
//		return ret;
//	}
	
	/**
	 *  Get a target object per remote reference.
	 *  @param rr The remote reference.
	 *  @return The target object.
	 */
	public Object	getTargetObject(RemoteReference rr)
	{
		Object	ret;
		
		if(rr.getTargetIdentifier() instanceof IServiceIdentifier)
		{
			IServiceIdentifier sid = (IServiceIdentifier)rr.getTargetIdentifier();
			
			// fetch service via its id
			IInternalAccess	access	= IInternalExecutionFeature.LOCAL.get();	// TODO: Hack!!! How to inject local component access?
			if(access==null)
			{
				throw new IllegalStateException("Must be run on component that received remote execution message.");
			}
			if(!access.getComponentIdentifier().equals(sid.getProviderId()))
			{
				throw new IllegalStateException("Must be request for service of component that received remote execution message.");				
			}
			
			ret	= access.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(sid);
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

			IInternalAccess	access	= IInternalExecutionFeature.LOCAL.get();	// TODO: Hack!!! How to inject local component access?
			if(access==null)
			{
				throw new IllegalStateException("Must be run on component that received remote execution message.");
			}
			if(!access.getComponentIdentifier().equals(cid))
			{
				throw new IllegalStateException("Must be request for access of component that received remote execution message.");				
			}
			ret	= access.getExternalAccess();
			
//			SServiceProvider.getService(access, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService cms) 
//				{
//					// fetch target component via component identifier.
//					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
//					{
//						public void customResultAvailable(IExternalAccess result)
//						{
//							ret.setResult(result);
//						}
//					});
//				}
//			});
		}
		else //(rr.getTargetIdentifier() instanceof String)
		{
			throw new UnsupportedOperationException("Plain remote objects not yet supported: "+rr.getTargetIdentifier());
//			Object o = targetobjects.get(rr);
//			if(o!=null)
//			{
//				ret.setResult(o);
//			}
//			else
//			{
//				ret.setException(new RuntimeException("Remote object not found: "+rr));
//			}
		}
		
		return ret;
	}
	
//	/**
//	 *  Remove a target object.
//	 *  @param rr The remote reference.
//	 *  @return The target object.
//	 */
//	protected Object removeTargetObject(RemoteReference rr)
//	{
//		checkThread();
//		return targetobjects.remove(rr);
//	}
	
//	/**
//	 *  Generate a remote reference.
//	 *  @return The remote reference.
//	 */
//	protected RemoteReference generateRemoteReference()
//	{
//		checkThread();
//		return new RemoteReference(rsms.getRMSComponentIdentifier(), ""+idcnt++);
//	}
	
	//-------- management of proxies --------

	/**
	 *  Get a proxy for a proxy reference.
	 *  @param pr The proxy reference.
	 */
	public Object getProxy(ProxyReference pr, ClassLoader classloader, boolean tolerant)
	{
		Object ret;
		
		// If is local return local target object.
		if(pr.getRemoteReference().getRemoteComponent().getRoot().equals(platform))
		{
			if(pr.getRemoteReference().getTargetIdentifier() instanceof IServiceIdentifier)
			{
				IServiceIdentifier	sid	= (IServiceIdentifier)pr.getRemoteReference().getTargetIdentifier();
				ret	= SServiceProvider.getLocalService(null, sid, sid.getServiceType().getType(classloader));
			}
			else if(pr.getRemoteReference().getTargetIdentifier() instanceof IComponentIdentifier)
			{
				// TODO: get external access sync???
				throw new UnsupportedOperationException("Local reuse of remote components not yet supported.");
			}
			else
			{
				throw new UnsupportedOperationException("Plain remote objects not yet supported.");
			}
		}
		// Else return new proxy.
		else
		{
//			System.out.println("interfaces of proxy: "+SUtil.arrayToString(pi.getTargetInterfaces()));
			
			// TODO: support mapping of pojo services?
//			Class<?>[] tmp = pr.getProxyInfo().getTargetInterfaces();
//			Class<?>[] interfaces = new Class[tmp.length+1];
//			System.arraycopy(tmp, 0, interfaces, 0, tmp.length);
//			interfaces[tmp.length] = IFinalize.class;
//			Class<?>[]	interfaces	=  pr.getProxyInfo().getTargetInterfaces();
			ClassInfo[]	ifaces	=  pr.getProxyInfo().getTargetInterfaces();
			List<Class<?>> tmp = new ArrayList<Class<?>>();
			for(ClassInfo ci: ifaces)
			{
				Class<?> cl = ci.getType(classloader);
				if(cl!=null)
				{
					tmp.add(cl);
				}
				else if(tolerant)
				{
					if(!tmp.contains(IBrokenProxy.class))
						tmp.add(IBrokenProxy.class);
				}
				else 
				{
					throw new RuntimeException("Class could not be loaded: "+ci);
				}
			}
			Class<?>[] interfaces = new Class<?>[tmp.size()];
			for(int i=0; i<tmp.size(); i++)
			{
				interfaces[i] = tmp.get(i);
			}
			
			// Which classloader to use for proxy creation?
			// a) from sender: allows receiver to have all (also implementations) what sender has
			// b) from receiver: so only interfaces are available but allows compatibility of sender
			//    and receiver even if they use different versions
			// c) enhance xml to annotate the resource the classes belong to (best solution)
			// currently just uses the 'global' platform classloader 
			
			IInternalAccess	access	= IInternalExecutionFeature.LOCAL.get();	// TODO: Hack!!! How to inject local component access?
			if(access==null)
			{
				throw new IllegalStateException("Must be run on component that received remote execution message.");
			}
			ret = Proxy.newProxyInstance(classloader, 
				interfaces, new RemoteMethodInvocationHandler(access, pr));
			
//			incProxyCount(pr.getRemoteReference());
			
		}
		
//		System.out.println("resolved proxy ref to: "+ret+" "+pr.getRemoteReference());
		
		return ret;
	}
	
	//-------- dgc --------
	
//	/**
//	 *  Increment the proxy count for a remote reference.
//	 *  @param rr The remote reference for the proxy.
//	 */
//	protected void incProxyCount(RemoteReference rr)
//	{
//		if(DEBUG)
//		{
//			if(proxycount.size()!=proxydates.size())
//				System.out.println("ipc start");
//		}
//		
//		checkThread();
//		// Only keep track of proxies for java objects.
//		// Components and services are not subject of gc.
//		
//		if(rr.isObjectReference())
//		{
//			boolean notify = false;
////			RemoteReference origrr = (RemoteReference)proxycountkeys.get(rr);
//			Integer cnt = (Integer)proxycount.remove(rr);
//			if(cnt==null)
//			{
////				proxycountkeys.put(rr, rr);
//				proxycount.put(rr, Integer.valueOf(1));
//				notify = true;
//				
//				// todo: transfer lease time interval?!
////				rr.setExpiryDate(clock.getTime()+DEFAULT_LEASETIME);
//				proxydates.put(Long.valueOf(System.currentTimeMillis()+DEFAULT_LEASETIME), rr);
//				
//				// Initiate check procedure.
//				startRenewalBehaviour();
//			}
//			else
//			{
//				proxycount.put(rr, Integer.valueOf(cnt.intValue()+1));
//			}
//				
//	//		System.out.println("Add proxy: "+rr+" "+cnt);
//			
//			if(notify)
//				sendAddRemoteReference(rr);
//			
//			if(DEBUG)
//			{
//				if(proxycount.size()!=proxydates.size())
//					System.out.println("ipc end");
//			}
//		}
//	}
	
//	/**
//	 *  Decrease the proxy count for a remote reference.
//	 *  @param rr The remote reference for the proxy.
//	 */
//	protected void decProxyCount(RemoteReference rr)
//	{
//		if(DEBUG)
//		{
//			if(proxycount.size()!=proxydates.size())
//				System.out.println("dpc start");
//		}
//		
//		checkThread();
//		// Only keep track of proxies for java objects.
//		// Components and services are not subject of gc.
//		
//		if(rr.isObjectReference())
//		{
//			boolean notify = false;
////			RemoteReference origrr = (RemoteReference)proxycountkeys.remove(rr);
//			Integer cnt = (Integer)proxycount.remove(rr);
//			int nv = cnt.intValue()-1;
//			if(nv==0)
//			{
//				notify = true;
//				proxydates.values().remove(rr);
////				System.out.println("Remove proxy: "+rr+" "+nv);
//			}
//			else
//			{
////				proxycountkeys.put(rr, rr);
//				proxycount.put(rr, Integer.valueOf(nv));
//			}
//				
////			System.out.println("Remove proxy: "+rr+" "+nv+" "+proxycount);
//			if(notify)
//				sendRemoveRemoteReference(rr);
//		}
//		
//		if(DEBUG)
//		{
//			if(proxycount.size()!=proxydates.size())
//				System.out.println("dpc end");
//		}
//	}
	
//	/**
//	 *  Start the removal behavior.
//	 */
//	protected void startRenewalBehaviour()
//	{
//		final long renewid = ++this.renewid;
//		
//		rsms.getComponent().scheduleStep(new IComponentStep<Void>()
//		{
//			@Classname("startRenewal")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				if(renewid == RemoteReferenceModule.this.renewid)
//				{
////					final RemoteServiceManagementAgent agent = (RemoteServiceManagementAgent)ia;
//					
//					if(DEBUG)
//					{
//						if(proxycount.size()!=proxydates.size())
//							System.out.println("srb start");
//					}
//					
////					System.out.println("Starting renewal behavior: "+removeid);
////					if(proxydates.size()>0)
////					{
////						System.out.println("Checking proxies: "+proxydates.size()+" "+proxycount.size());
////						for(Iterator it=proxydates.keySet().iterator(); it.hasNext(); )
////						{
////							Long key = (Long)it.next();
////							System.out.println("\t "+key+" "+" "+System.currentTimeMillis()+" "+proxydates.get(key));
////						}
////					}
//					
//					long diff = 0;
//					Long[] dates = (Long[])proxydates.keySet().toArray(new Long[proxydates.size()]);
//					for(int i=0; i<dates.length; i++)
//					{
//						diff = dates[i].longValue()-System.currentTimeMillis();
//						if(diff<=0)
//						{
//							final RemoteReference rr = (RemoteReference)proxydates.remove(dates[i]);
////							System.out.println("renewal sent for: "+rr);
//							IResultListener<Void> lis = ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
//							{
//								public void resultAvailable(Void result)
//								{
//									if(DEBUG)
//										System.out.println("Renewed successfully lease for: "+rr);
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									if(DEBUG)
//										System.out.println("Failed to renew lease for: "+rr);
//								}
//							});
//							sendAddRemoteReference(rr).addResultListener(lis);
//							
//							long expirydate = System.currentTimeMillis()+DEFAULT_LEASETIME;
//							proxydates.put(Long.valueOf(expirydate), rr);
//							diff = DEFAULT_LEASETIME;
//						}
//						else
//						{
//							break;
//						}
//					}
//					
////					System.out.println("prxy: "+proxycount);
//					
//					if(DEBUG)
//					{
//						if(proxycount.size()!=proxydates.size())
//							System.out.println("srb end");
//					}
//					
//					if(proxycount.size()>0 && diff>0 && timer!=null) // can be null if shutdown was already called
//					{
////						System.out.println("renewal behaviour waiting: "+diff);
//						final IComponentStep<Void>	step = this;
//						timer.schedule(new TimerTask()
//						{
//							public void run()
//							{
//								rsms.getComponent().scheduleStep(step);
//							}
//						}, diff);
//					}
//				}
//				
////				System.out.println("renewal behaviour exit");
//				return IFuture.DONE;
//			}
//		});
//	}
	
//	/**
//	 *  Start removal behavior for expired holders.
//	 */
//	protected void startRemovalBehaviour()
//	{
//		final long removeid = ++this.removeid;
//		
//		rsms.getComponent().scheduleStep(new IComponentStep<Void>()
//		{
//			@Classname("startRemoval")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				if(removeid == RemoteReferenceModule.this.removeid)
//				{
////					System.out.println("Starting removal behavior: "+removeid);
////					if(holders.size()>0)
////					{
////						System.out.println("Checking holders: ");
////						for(Iterator it=holders.keySet().iterator(); it.hasNext(); )
////						{
////							Object key = it.next();
////							System.out.println("\t "+key+" "+((Map)holders.get(key)).keySet());
////						}
////					}
//					
//					for(Iterator<RemoteReference> it=holders.keySet().iterator(); it.hasNext(); )
//					{
//						RemoteReference rr = (RemoteReference)it.next();
//						Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
//						for(Iterator<RemoteReferenceHolder> it2=hds.keySet().iterator(); it2.hasNext(); )
//						{
//							RemoteReferenceHolder rrh = it2.next();
//							if(System.currentTimeMillis() > rrh.getExpiryDate()+DEFAULT_LEASETIME*WAITFACTOR)
//							{
//								if(DEBUG)
//									System.out.println("Removing expired holder: "+rr+" "+rrh+" "+rrh.getExpiryDate()+" "+System.currentTimeMillis());
//								it2.remove();
////								hds.remove(rrh);
//								if(hds.size()==0)
//								{
//									it.remove();
////									holders.remove(rr);
//									deleteRemoteReference(rr);
//								}
//							}
//						}
//					}
//					
//					if(holders.size()>0 && timer!=null) // can be null if shutdown was already called
//					{
//						final IComponentStep<Void>	step	= this;
//						timer.schedule(new TimerTask()
//						{
//							public void run()
//							{
//								rsms.getComponent().scheduleStep(step);
//							}
//						}, 5000);
//					}
//				}
//				return IFuture.DONE;
//			}
//		});
//	}
	
//	/**
//	 *  Send addRef to the origin process of the remote reference.
//	 *  @param rr The remote reference.
//	 */
//	public Future<Void> sendAddRemoteReference(final RemoteReference rr)
//	{
//		checkThread();
//		// DGC: notify rr origin that a new proxy of target object exists
//		// todo: handle failures!
//		final Future<Void> ret = new Future<Void>();
//		
////		System.out.println("send add: "+rr);
//		final String callid = SUtil.createUniqueId(rsms.getRMSComponentIdentifier().getLocalName());
//		RemoteDGCAddReferenceCommand com = new RemoteDGCAddReferenceCommand(rr, rsms.getRMSComponentIdentifier(), callid);
//		Future<Object> fut = new Future<Object>();
//		fut.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				ret.setResult(null);
//			}
//		});
//		rsms.sendMessage(rr.getRemoteManagementServiceIdentifier(), null, com, callid, Starter.getRemoteDefaultTimeout(rsms.getComponent().getComponentIdentifier()), fut, null, null);
//		
//		return ret;
//	}
	
//	/**
//	 *  Send removeRef to the origin process of the remote reference.
//	 *  @param rr The remote reference.
//	 */
//	public Future<Void> sendRemoveRemoteReference(final RemoteReference rr)
//	{
//		checkThread();
//		// DGC: notify rr origin that a new proxy of target object exists
//		// todo: handle failures!
//		final Future<Void> ret = new Future<Void>();
////		System.out.println("send rem: "+rr);
//		final String callid = SUtil.createUniqueId(rsms.getRMSComponentIdentifier().getLocalName());
//		RemoteDGCRemoveReferenceCommand com = new RemoteDGCRemoveReferenceCommand(rr, rsms.getRMSComponentIdentifier(), callid);
////		System.out.println("send start: "+rr);
////		future.addResultListener(new IResultListener()
////		{
////			public void resultAvailable(Object result)
////			{
////				System.out.println("send end: "+rr);
////			}
////			
////			public void exceptionOccurred(Exception exception)
////			{
////				System.out.println("send ex: "+rr);
////			}
////		});
//		
//		Future<Object> fut = new Future<Object>();
//		fut.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				ret.setResult(null);
//			}
//		});
//		rsms.sendMessage(rr.getRemoteManagementServiceIdentifier(), null, com, callid, Starter.getRemoteDefaultTimeout(rsms.getComponent().getComponentIdentifier()), fut, null, null);
//		return ret;
//	}
	
//	/**
//	 *  Add a new temporary holder to a remote object.
//	 *  @param rr The remote reference.
//	 *  @param holder The cid of the holding rms.
//	 */
//	protected void addTemporaryRemoteReference(final RemoteReference rr, final IComponentIdentifier holder)
//	{
//		checkThread();
//		Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
//		if(hds==null)
//		{
//			hds = new HashMap<RemoteReferenceHolder, RemoteReferenceHolder>();
//			holders.put(rr, hds);
//			startRemovalBehaviour();
//		}
//		
//		long expirydate = System.currentTimeMillis()+DEFAULT_LEASETIME;
//		TemporaryRemoteReferenceHolder newth = new TemporaryRemoteReferenceHolder(holder, expirydate);
//		TemporaryRemoteReferenceHolder oldth = (TemporaryRemoteReferenceHolder)hds.get(newth);
//		if(oldth==null)
//		{
//			hds.put(newth, newth);
//		}
//		else
//		{
//			// Update existing holder.
//			oldth.setNumber(oldth.getNumber()+1);
//			oldth.setExpiryDate(expirydate);
//		}
////		System.out.println("Holders for (temp add): "+rr+" add: "+holder+" "+hds.keySet());
//	}
	
//	/**
//	 *  Add a new holder to a remote object.
//	 *  @param rr The remote reference.
//	 *  @param holder The cid of the holding rms.
//	 */
//	public void addRemoteReference(final RemoteReference rr, final IComponentIdentifier holder)
//	{
//		checkThread();
//		Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
//		if(hds==null)
//		{
//			hds = new HashMap<RemoteReferenceHolder, RemoteReferenceHolder>();
//			holders.put(rr, hds);
//			startRemovalBehaviour();
//		}
//		
//		long expirydate = System.currentTimeMillis()+DEFAULT_LEASETIME;
//		RemoteReferenceHolder newh = new RemoteReferenceHolder(holder, expirydate);
//		RemoteReferenceHolder oldh = (RemoteReferenceHolder)hds.get(newh);
//		
//		if(oldh==null)
//		{
////			throw new RuntimeException("Holder already contained: "+holder);
//			hds.put(newh, newh);
//		}
//		else
//		{
//			// Renew expiry date of existing holder.
//			oldh.setExpiryDate(expirydate);
//			if(DEBUG)
//				System.out.println("renewed lease for: "+rr+" "+oldh);
//		}
//		
//		// Decrement number (and possibly remove) temporary holder.
//		TemporaryRemoteReferenceHolder th = (TemporaryRemoteReferenceHolder)hds.get(
//			new TemporaryRemoteReferenceHolder(holder, 0));
//		if(th!=null)
//		{
//			th.setNumber(th.getNumber()-1);
//			if(th.getNumber()==0)
//			{
//				hds.remove(th); // hds.size() != 0 
//			}
//		}
//
////		System.out.println("Holders for (add): "+rr+" add: "+holder+" "+hds.keySet());
//	}
	
//	/**
//	 *  Remove a new holder from a remote object.
//	 *  @param rr The remote reference.
//	 *  @param holder The cid of the holding rms.
//	 */
//	public void removeRemoteReference(final RemoteReference rr, final IComponentIdentifier holder)
//	{
//		checkThread();
//		Map<RemoteReferenceHolder, RemoteReferenceHolder> hds = (Map<RemoteReferenceHolder, RemoteReferenceHolder>)holders.get(rr);
////		if(hds==null || !hds.contains(holder))
////			throw new RuntimeException("Holder not contained: "+holder);
//
////		System.out.println("Holders for (rem): "+result+" rem: "+holder+" "+hds);
//		
//		if(hds!=null)
//		{
//			hds.remove(new RemoteReferenceHolder(holder, 0));
//			if(hds.size()==0)
//			{
//				holders.remove(rr);
//				deleteRemoteReference(rr);
//			}
//		}
//	}
	
//	/**
//	 *  Check if correct thread access.
//	 */
//	protected void checkThread()
//	{
//		if(DEBUG)
//		{
//			// Hack!
//			if(rsms.getComponent().isExternalThread())
//			{
//				System.out.println("wrong thread: "+Thread.currentThread());
//				Thread.dumpStack();
//			}
//		}
//	}
//
//	/**
//	 *  Get the marshalservice.
//	 *  @return the marshalservice.
//	 */
//	public IMarshalService getMarshalService()
//	{
//		return marshalservice;
//	}
//
//	/**
//	 *  Get the libservice.
//	 *  @return the libservice.
//	 */
//	public ILibraryService getLibraryService()
//	{
//		return libservice;
//	}
	
	
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
	
	//-------- from marshal service --------
	
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture
	 *  - if the object has used an @Reference annotation at type level
	 *  - has been explicitly set to be reference
	 */
	public boolean isRemoteReference(Object object)
	{
		return isReference(object, false);
	}
	
	/**
	 *  Test if an object is a remote object.
	 */
	@Excluded
	public boolean isRemoteObject(Object target)
	{
		boolean ret = false;
		
		if(Proxy.isProxyClass(target.getClass()))
		{
			Object handler = Proxy.getInvocationHandler(target);
			if(handler instanceof BasicServiceInvocationHandler)
			{
				BasicServiceInvocationHandler bsh = (BasicServiceInvocationHandler)handler;
				// Hack! Needed for dynamically bound delegation services of composites (virtual)
				ret = bsh.getDomainService()==null;
				if(!ret)
					return isRemoteObject(bsh.getDomainService());
			}
			else 
			{
				// todo: remove string based remote check! RemoteMethodInvocationHandler is in package jadex.platform.service.remote
				ret = Proxy.getInvocationHandler(target).getClass().getName().indexOf("Remote")!=-1;
			}
		}
		return ret;
//			Object target = getObject();
//			if(Proxy.isProxyClass(target.getClass()))
//				System.out.println("blubb "+Proxy.getInvocationHandler(target).getClass().getName());
//			return Proxy.isProxyClass(target.getClass()) && Proxy.getInvocationHandler(target).getClass().getName().indexOf("Remote")!=-1;

	}
	
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture, IIntermediateFuture, 
	 *  	IResultListener, IIntermediateResultListener, IChangeListener, IRemoteChangeListener
	 *  - if the object has used an @Reference annotation at type level
	 */
	public boolean isReference(Object object, boolean local)
	{
		boolean ret = false;
//			boolean ret = object instanceof IRemotable 
//				|| object instanceof IResultListener || object instanceof IIntermediateResultListener
//				|| object instanceof IFuture || object instanceof IIntermediateFuture
//				|| object instanceof IChangeListener || object instanceof IRemoteChangeListener;
////				|| object instanceof IService;// || object instanceof IExternalAccess;
		
		if(!ret && object!=null)
		{
			boolean localret = ret;
			boolean remoteret = ret;
		
			Class<?> cl = object.getClass();
			// Avoid creating list for frequent case that class is already contained
			boolean[] isref = (boolean[])references.get(cl);
			if(isref!=null)
			{
				localret = isref[0];
				remoteret = isref[1];
//					System.out.println("cont: "+cl+" "+references.get(cl));
			}
			else
			{
				List<Class<?>> todo = new ArrayList<Class<?>>();
				todo.add(cl);
				isref = null;
				while(todo.size()>0 && isref==null)
				{
					Class<?> clazz = (Class<?>)todo.remove(0);
					isref = (boolean[])references.get(clazz);
					if(isref!=null)
					{
						localret = isref[0];
						remoteret = isref[1];
						break;
					}
					else
					{
						isref = (boolean[])REFERENCES.get(clazz);
						if(isref!=null)
						{
							localret = isref[0];
							remoteret = isref[1];
							break;
						}
						else
						{
							remoteret	= remoteret || SReflect.isSupertype(IRemotable.class, clazz);
							Reference ref = (Reference)clazz.getAnnotation(Reference.class);
							if(ref!=null)
							{
								localret = ref.local();
								remoteret = remoteret || ref.remote();
								break;
							}
							else
							{
								Class<?> superclazz = clazz.getSuperclass();
								if(superclazz!=null && !superclazz.equals(Object.class))
									todo.add(superclazz);
								Class<?>[] interfaces = clazz.getInterfaces();
								for(int i=0; i<interfaces.length; i++)
								{
									todo.add(interfaces[i]);
								}
							}
						}
					}
				}
			}
			references.put(cl, new boolean[]{localret, remoteret});
			ret = local? localret: remoteret;
//				System.out.println("refsize: "+references.size());
		}
		
//			System.out.println("object ref? "+ret+" "+object.getClass()+" "+object);
		
//			boolean test = object instanceof IRemotable 
//				|| object instanceof IResultListener || object instanceof IIntermediateResultListener
//				|| object instanceof IFuture || object instanceof IIntermediateFuture
//				|| object instanceof IChangeListener || object instanceof IRemoteChangeListener;
//			|| object instanceof IService;// || object instanceof IExternalAccess;
		
//			if(ret==false && test!=ret)
//				System.out.println("wrong reference semantics");
		
		return ret;
	}
	
	/**
	 *  Get the proxy interfaces (empty list if none).
	 */
	public Class<?>[] getRemoteInterfaces(Object object, ClassLoader cl)
	{
		List<Class<?>> ret = new ArrayList<Class<?>>();
		
		if(object!=null)
		{
			List<Class<?>> todo = new ArrayList<Class<?>>();
			Set<Class<?>> done = new HashSet<Class<?>>();
			todo.add(object.getClass());
			
			while(todo.size()>0)
			{
				Class<?> clazz = (Class<?>)todo.remove(0);
				done.add(clazz);
				
				if(clazz.isInterface())
				{
					boolean isref = SReflect.isSupertype(IRemotable.class, clazz)
						|| REFERENCES.containsKey(clazz) && REFERENCES.get(clazz)[1];
					if(!isref)
					{
						Reference ref = (Reference)clazz.getAnnotation(Reference.class);
						isref = ref!=null && ref.remote();
					}
					if(!isref)
					{
						Service ser = clazz.getAnnotation(Service.class);
						isref = ser!=null;
					}
					if(isref)
					{
						if(!ret.contains(clazz))
							ret.add(clazz);
					}
				}
				Class<?> superclazz = clazz.getSuperclass();
				if(superclazz!=null && !superclazz.equals(Object.class) && !done.contains(superclazz))
				{
					todo.add(superclazz);
				}
				Class<?>[] interfaces = clazz.getInterfaces();
				for(int i=0; i<interfaces.length; i++)
				{
					if(!done.contains(superclazz))
					{
						todo.add(interfaces[i]);
					}
				}
			}
			
			if(object instanceof IService)
			{
				// Hack!!! Should not need class loader at all?
				// getType0 required, cf. ServiceCallTest (why wrong class loader?)
				Class<?> serviceinterface = ((IService)object).getServiceIdentifier().getServiceType().getType0();
				if(serviceinterface==null)
				{
					// getType(cl) required, cf. RemoteReferenceTest (remote proxy with only typename) -> use ClassInfo in ProxyInfo instead of Class<?>
					serviceinterface = ((IService)object).getServiceIdentifier().getServiceType().getType(cl);
				}
				assert serviceinterface!=null;
				if(!ret.contains(serviceinterface))
					ret.add(serviceinterface);
			}
		}
		
		for(Class<?> cls: ret)
		{
			if(cls==null)
				throw new RuntimeException("An interface could not be resolved: "+ret);
		}
		
		return (Class[])ret.toArray(new Class[ret.size()]);
	}
	
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture
	 *  - if the object has used an @Reference annotation at type level
	 *  - has been explicitly set to be reference
	 */
	public boolean isLocalReference(Object object)
	{
		return isReference(object, true);
	}
	
	/**
	 *  Get the clone processors.
	 *  @return The clone processors.
	 */
	public List<ITraverseProcessor> getCloneProcessors()
	{
		if(processors==null)
		{
			processors = Collections.synchronizedList(Traverser.getDefaultProcessors());
			// Problem: if micro agent implements a service it cannot
			// be determined if the service or the agent should be transferred.
			// Per default a service is assumed.

			// All proxies?!
			processors.add(new ImmutableProcessor()
			{
				@Override
				public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
				{
					return Proxy.isProxyClass(object.getClass());
				}
			});
			
			processors.add(new ITraverseProcessor()
			{
				public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
				{
					return object instanceof IBrokenProxy;
				}
				
				public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
				{
					return getProxyReference(object, null, targetcl);
				}
			});
			
			// Insert before FieldProcessor that is always applicable
			processors.add(processors.size()-1, new ITraverseProcessor()
			{
				public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
				{
					return object!=null && !(object instanceof BasicService) 
						&& object.getClass().isAnnotationPresent(Service.class);
				}
				
				public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
				{
					return BasicServiceInvocationHandler.getPojoServiceProxy(object);
				}
			});
			
			// Add processor for streams
			processors.add(processors.size()-1, new ITraverseProcessor()
			{
				public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
				{
					boolean ret = false;
					if(object instanceof ServiceInputConnectionProxy)
					{
						ret = true;
						// does not work because initiator/participant are always null :-(
//						ServiceInputConnectionProxy sp = (ServiceInputConnectionProxy)object;
//						if(sp.getInitiator()!=null && sp.getParticipant()!=null)
//						{
//							ret = sp.getInitiator().getPlatformName().equals(sp.getParticipant().getPlatformName());
//						}
					}
					return ret;
				}
				
				public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
				{
					ServiceInputConnectionProxy sicp = (ServiceInputConnectionProxy)object;
					
					LocalInputConnectionHandler ich = new LocalInputConnectionHandler(sicp.getNonFunctionalProperties());
					LocalOutputConnectionHandler och = new LocalOutputConnectionHandler(sicp.getNonFunctionalProperties(), ich);
					ich.setConnectionHandler(och);

					InputConnection icon = new InputConnection(null, null, sicp.getConnectionId(), false, ich);
					OutputConnection ocon = new OutputConnection(null, null, sicp.getConnectionId(), true, och);
					
					sicp.setOutputConnection(ocon);
					
					return icon;
				}
			});
			
			// Add processor for streams
			processors.add(processors.size()-1, new ITraverseProcessor()
			{
				public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
				{
					boolean ret = false;
					if(object instanceof ServiceOutputConnectionProxy)
					{
						ret = true; 
						// does not work because initiator/participant are always null :-(
//						ServiceOutputConnectionProxy sp = (ServiceOutputConnectionProxy)object;
//						if(sp.getInitiator()!=null && sp.getParticipant()!=null)
//						{
//							ret = sp.getInitiator().getPlatformName().equals(sp.getParticipant().getPlatformName());
//						}
					}
					return ret;
				}
				
				public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
				{
					ServiceOutputConnectionProxy socp = (ServiceOutputConnectionProxy)object;
					
					LocalOutputConnectionHandler och = new LocalOutputConnectionHandler(socp.getNonFunctionalProperties());
					LocalInputConnectionHandler ich = new LocalInputConnectionHandler(socp.getNonFunctionalProperties(), och);
					och.setConnectionHandler(ich);

					InputConnection icon = new InputConnection(null, null, socp.getConnectionId(), false, ich);
					socp.setInputConnection(icon);
					OutputConnection ocon = new OutputConnection(null, null, socp.getConnectionId(), true, och);
					
					return ocon;
				}
			});
		}
		return new ArrayList(processors);
	}
	
	/**
	 *  Add a clone processor.
	 */
	public void addCloneProcessor(@Reference ITraverseProcessor proc)
	{
		this.processors.add(proc);
	}
		
	/**
	 *  Remove a clone processor.
	 */
	public void removeCloneProcessor(@Reference ITraverseProcessor proc)
	{
		this.processors.remove(proc);
	}
}
