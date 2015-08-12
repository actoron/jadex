package jadex.platform.service.marshal;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.remote.ServiceInputConnectionProxy;
import jadex.bridge.service.types.remote.ServiceOutputConnectionProxy;
import jadex.commons.IChangeListener;
import jadex.commons.IRemotable;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SReflect;
import jadex.commons.collection.LRU;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.platform.service.message.streams.InputConnection;
import jadex.platform.service.message.streams.LocalInputConnectionHandler;
import jadex.platform.service.message.streams.LocalOutputConnectionHandler;
import jadex.platform.service.message.streams.OutputConnection;

/**
 *  Marshal service implementation.
 */
public class MarshalService extends BasicService implements IMarshalService
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
		Class<?>	ti	= SReflect.classForName0("jadex.xml.TypeInfo", MarshalService.class.getClassLoader());
		if(ti!=null)
		{
			refs.put(ti, tf);
		}
		
		REFERENCES = Collections.unmodifiableMap(refs);
	}
	
	//-------- attributes --------
	
	/** The clone processors. */
	protected List<ITraverseProcessor> processors;
	
	/** The reference class cache (clazz->boolean (is reference)). */
	protected Map<Class<?>, boolean[]> references;
	
	//-------- constructors --------
	
	/**
	 *  Create marshal service.
	 */
	public MarshalService(IInternalAccess access)
	{
		super(access.getComponentIdentifier(), IMarshalService.class, null);
	}
	
	//-------- methods --------
		
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		references = Collections.synchronizedMap(new LRU<Class<?>, boolean[]>(500));
//		processors = Collections.synchronizedList(new ArrayList<ITraverseProcessor>());
		processors = Collections.synchronizedList(Traverser.getDefaultProcessors());
				
		// Problem: if micro agent implements a service it cannot
		// be determined if the service or the agent should be transferred.
		// Per default a service is assumed.
		
		// Insert before FieldProcessor that is always applicable
		processors.add(processors.size()-1, new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				return object!=null && !(object instanceof BasicService) 
					&& object.getClass().isAnnotationPresent(Service.class);
			}
			
			public Object process(Object object, Type type,
				List<ITraverseProcessor> processors, Traverser traverser,
				Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
			{
				return BasicServiceInvocationHandler.getPojoServiceProxy(object);
			}
		});
		
		// Add processor for streams
		processors.add(processors.size()-1, new ITraverseProcessor()
		{
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				boolean ret = false;
				if(object instanceof ServiceInputConnectionProxy)
				{
					ret = true;
					// does not work because initiator/participant are always null :-(
//					ServiceInputConnectionProxy sp = (ServiceInputConnectionProxy)object;
//					if(sp.getInitiator()!=null && sp.getParticipant()!=null)
//					{
//						ret = sp.getInitiator().getPlatformName().equals(sp.getParticipant().getPlatformName());
//					}
				}
				return ret;
			}
			
			public Object process(Object object, Type type,
				List<ITraverseProcessor> processors, Traverser traverser,
				Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
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
			public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
			{
				boolean ret = false;
				if(object instanceof ServiceOutputConnectionProxy)
				{
					ret = true; 
					// does not work because initiator/participant are always null :-(
//					ServiceOutputConnectionProxy sp = (ServiceOutputConnectionProxy)object;
//					if(sp.getInitiator()!=null && sp.getParticipant()!=null)
//					{
//						ret = sp.getInitiator().getPlatformName().equals(sp.getParticipant().getPlatformName());
//					}
				}
				return ret;
			}
			
			public Object process(Object object, Type type,
				List<ITraverseProcessor> processors, Traverser traverser,
				Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
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
		
		return IFuture.DONE;
	}
		
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
		return IFuture.DONE;
	}
	
	//-------- class reference management --------

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
	 *  Register a class with reference values for local and remote.
	 */
	public void setReferenceProperties(Class clazz, boolean localref, boolean remoteref)
	{
		references.put(clazz, new boolean[]{localref, remoteref});
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
//		Object target = getObject();
//		if(Proxy.isProxyClass(target.getClass()))
//			System.out.println("blubb "+Proxy.getInvocationHandler(target).getClass().getName());
//		return Proxy.isProxyClass(target.getClass()) && Proxy.getInvocationHandler(target).getClass().getName().indexOf("Remote")!=-1;

	}
	
	//-------- local clone processors --------
	
	/**
	 *  Get the clone processors.
	 */
	public List<ITraverseProcessor> getCloneProcessors()
	{
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

	//-------- remote clone processors --------

//	/**
//	 *  Add a rmi preprocessor.
//	 */
//	public IFuture<Void> addRMIPreProcessor(@Reference IRMIPreprocessor proc);
//		
//	/**
//	 *  Remove a rmi postprocessor.
//	 */
//	public IFuture<Void> removeRMIPreProcessor(@Reference IRMIPreprocessor proc);
//	
//	/**
//	 *  Add a rmi postprocessor.
//	 */
//	public IFuture<Void> addRMIPostProcessor(@Reference IRMIPostprocessor proc);
//		
//	/**
//	 *  Remove a rmi postprocessor.
//	 */
//	public IFuture<Void> removeRMIPostProcessor(@Reference IRMIPostprocessor proc);
//	
//	/**
//	 *  Get the rmi preprocessors.
//	 */
//	public IIntermediateFuture<IRMIPreProcessor> getRMIPreProcessors();
//	
//	/**
//	 *  Get the rmi postprocessors.
//	 */
//	public IIntermediateFuture<IRMIPostProcessor> getRMIPostProcessors();

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
//		boolean ret = object instanceof IRemotable 
//			|| object instanceof IResultListener || object instanceof IIntermediateResultListener
//			|| object instanceof IFuture || object instanceof IIntermediateFuture
//			|| object instanceof IChangeListener || object instanceof IRemoteChangeListener;
////			|| object instanceof IService;// || object instanceof IExternalAccess;
		
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
//				System.out.println("cont: "+cl+" "+references.get(cl));
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
//			System.out.println("refsize: "+references.size());
		}
		
//		System.out.println("object ref? "+ret+" "+object.getClass()+" "+object);
		
//		boolean test = object instanceof IRemotable 
//			|| object instanceof IResultListener || object instanceof IIntermediateResultListener
//			|| object instanceof IFuture || object instanceof IIntermediateFuture
//			|| object instanceof IChangeListener || object instanceof IRemoteChangeListener;
//		|| object instanceof IService;// || object instanceof IExternalAccess;
		
//		if(ret==false && test!=ret)
//			System.out.println("wrong reference semantics");
		
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
				Class<?> serviceinterface = ((IService)object).getServiceIdentifier().getServiceType().getType(cl);
				if(!ret.contains(serviceinterface))
					ret.add(serviceinterface);
			}
		}
		
		return (Class[])ret.toArray(new Class[ret.size()]);
	}
}
