package jadex.bridge.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.NFMethodPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Basic service provide a simple default isValid() implementation
 *  that returns true after start service and false afterwards.
 */
public class BasicService extends NFMethodPropertyProvider implements IInternalService
{
	//-------- constants --------
	
	/** Constant for remote default timeout. */
	private static long DEFAULT_REMOTE = 30000;

	/** Constant for local default timeout. */
	private static long DEFAULT_LOCAL = 30000;
	
	static
	{
		// Set deftimeout from environment, if set.
		String	dtoprop	= System.getProperty("jadex.deftimeout", System.getenv("jadex.deftimeout"));
		if(dtoprop!=null)
		{
			System.out.println("Property jadex.deftimeout is deprecated. Use jadex_deftimeout instead.");

		}
		else
		{
			dtoprop	= System.getProperty("jadex_deftimeout", System.getenv("jadex_deftimeout"));
		}
		if(dtoprop!=null)
		{
			DEFAULT_LOCAL = Long.parseLong(dtoprop);
			DEFAULT_REMOTE = Long.parseLong(dtoprop);
			System.out.println("Setting jadex_deftimeout: "+dtoprop);
		}
	}
	
	/**
	 *  Get the remote default timeout.
	 */
	public static long	getRemoteDefaultTimeout()
	{
		return DEFAULT_REMOTE;
	}

	/**
	 *  Get the local default timeout.
	 */
	public static long	getLocalDefaultTimeout()
	{
		return DEFAULT_LOCAL;
	}

	/**
	 *  Set the remote default timeout.
	 */
	public static void	setRemoteDefaultTimeout(long timeout)
	{
		DEFAULT_REMOTE	= timeout;
//		System.err.println("Setting Jadex default timeout: "+DEFAULT_REMOTE);
//		Thread.dumpStack();
	}

	/**
	 *  Set the local default timeout.
	 */
	public static void	setLocalDefaultTimeout(long timeout)
	{
		DEFAULT_LOCAL	= timeout;
//		System.err.println("Setting Jadex default timeout: "+DEFAULT_LOCAL);
//		Thread.dumpStack();
	}

	//-------- attributes --------

	/** The id counter. */
	protected static long idcnt;
	
	/** Internal access to its component. */
	protected IInternalAccess internalaccess;
	
	/** The started state. */
	protected boolean started;
	
	/** The shutdowned state. */
	protected boolean shutdowned;
	
	/** The service id. */
	protected IServiceIdentifier sid;
	
	/** The service properties. */
	private Map<String, Object> properties;
	
	/** The provider id. */
	protected IComponentIdentifier providerid;
	
	
	protected Class<?> type;
	
	protected Class<?> impltype;
	
	//-------- constructors --------

	/**
	 *  Create a new service.
	 */
	// todo: remove type!!!
	public BasicService(IComponentIdentifier providerid, Class<?> type, Map<String, Object> properties)
	{
		this(providerid, type, null, properties);
	}
	
	/**
	 *  Create a new service.
	 */
	// todo: remove type!!!
	public BasicService(IComponentIdentifier providerid, Class<?> type, Class<?> impltype, Map<String, Object> properties)
	{
		super(null);
		
//		if(!SReflect.isSupertype(type, getClass()))
//			throw new RuntimeException("Service must implement provided interface: "+getClass().getName()+", "+type.getName());
		this.providerid = providerid;
//		this.type = type;
//		this.implclazz = implclazz;
		this.properties	= properties;
		
		this.type = type;
		this.impltype = impltype;
		
		// todo: move to be able to use the constant
		// jadex.base.gui.componentviewer.IAbstractViewerPanel.PROPERTY_VIEWERCLASS
		Object guiclazz = properties!=null? properties.get("componentviewer.viewerclass"): null;
		
		if(guiclazz==null && type.isAnnotationPresent(GuiClass.class))
		{
			GuiClass gui = (GuiClass)type.getAnnotation(GuiClass.class);
			guiclazz = gui.value();
			if(this.properties==null)
				this.properties = new HashMap<String, Object>();
			this.properties.put("componentviewer.viewerclass", guiclazz);
//			System.out.println("found: "+guiclazz);
		}
		else if(guiclazz==null && type.isAnnotationPresent(GuiClassName.class))
		{
			GuiClassName gui = (GuiClassName)type.getAnnotation(GuiClassName.class);
			guiclazz = gui.value();
			if(this.properties==null)
				this.properties = new HashMap<String, Object>();
			this.properties.put("componentviewer.viewerclass", guiclazz);
//			System.out.println("found: "+guiclazz);
		}
		else if(guiclazz==null && type.isAnnotationPresent(GuiClassNames.class))
		{
			GuiClassNames anno = type.getAnnotation(GuiClassNames.class);
			GuiClassName[] guis = anno.value();
			String[] guiClasses = new String[guis.length];
			for (int i = 0; i < guis.length; i++) {
				guiClasses[i] = guis[i].value();
			}
			if(this.properties==null) 
				this.properties = new HashMap<String, Object>();
			this.properties.put("componentviewer.viewerclass", guiClasses);
		}
		
//		if(type.isAnnotationPresent(NFProperties.class))
//		{
//			if(nfproperties==null)
//				nfproperties = new HashMap<String, INFProperty<?,?>>();
//			addNFProperties(type.getAnnotation(NFProperties.class), nfproperties, null);
//		}
//		
//		Method[] methods = type.getMethods();
//		for(Method m : methods)
//		{
//			if(m.isAnnotationPresent(NFProperties.class))
//			{
//				if(methodnfproperties==null)
//					methodnfproperties = new HashMap<Method, Map<String,INFProperty<?,?>>>();
//				Map<String,INFProperty<?,?>> nfmap = methodnfproperties.get(m);
//				if (nfmap == null)
//				{
//					nfmap = new HashMap<String, INFProperty<?,?>>();
//					methodnfproperties.put(m, nfmap);
//				}
//				addNFProperties(m.getAnnotation(NFProperties.class), nfmap, new MethodInfo(m));
//			}
//		}
	}
	
	/**
	 * 
	 */
	public void initNFProperties()
	{
		IService ser = internalaccess.getServiceContainer().getProvidedService(type);
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superclazz = type;
		while(superclazz != null && !Object.class.equals(superclazz))
		{
			classes.add(superclazz);
			superclazz = superclazz.getSuperclass();
		}
		superclazz = impltype!=null? impltype: this.getClass();
		while(superclazz != null && !BasicService.class.equals(superclazz) && !Object.class.equals(superclazz))
		{
			classes.add(superclazz);
			superclazz = superclazz.getSuperclass();
		}
		Collections.reverse(classes);
		
		for(Class<?> sclazz: classes)
		{
			if(sclazz.isAnnotationPresent(NFProperties.class))
			{
				if(nfproperties==null)
					nfproperties = new HashMap<String, INFProperty<?,?>>();
				addNFProperties(sclazz.getAnnotation(NFProperties.class), nfproperties, ser, null);
			}
			
			Method[] methods = sclazz.getMethods();
			for(Method m : methods)
			{
				if(m.isAnnotationPresent(NFProperties.class))
				{
					if(methodnfproperties==null)
						methodnfproperties = new HashMap<MethodInfo, Map<String,INFProperty<?,?>>>();
					
					Map<String,INFProperty<?,?>> nfmap = methodnfproperties.get(new MethodInfo(m));
					if(nfmap == null)
					{
						nfmap = new HashMap<String, INFProperty<?,?>>();
						methodnfproperties.put(new MethodInfo(m), nfmap);
					}
				}
			}
		}
		
		if(methodnfproperties!=null)
		{
			for(MethodInfo key: methodnfproperties.keySet())
			{
				Map<String,INFProperty<?,?>> nfmap = methodnfproperties.get(key);
				addNFProperties(key.getMethod(internalaccess.getClassLoader()).getAnnotation(NFProperties.class), nfmap, ser, key);
			}
		}
	}
	
	/**
	 *  Add nf properties from a type.
	 */
	public void addNFProperties(NFProperties nfprops, Map<String, INFProperty<?, ?>> nfps, IService ser, MethodInfo mi)
	{
		for(NFProperty nfprop : nfprops.value())
		{
			Class<?> clazz = nfprop.value();
			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, internalaccess, ser, mi);
			nfps.put(prop.getName(), prop);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 *  
	 *  todo: why is method synchronized?
	 */
	public synchronized IFuture<Boolean> isValid()
	{
//		if(getServiceIdentifier().getServiceName().indexOf("Decoupled")!=-1)
//			System.out.println("isValid: "+getServiceIdentifier()+": "+(started && !shutdowned));
		return new Future<Boolean>(started && !shutdowned? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Set the service identifier.
	 */
	public void createServiceIdentifier(String name, Class<?> implclazz, IResourceIdentifier rid, Class<?> type)
	{
		this.sid = createServiceIdentifier(providerid, name, type, implclazz, rid);
	}
	
	/**
	 *  Get the service id.
	 *  @return The service id.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		if(sid==null)
			throw new RuntimeException("No service identifier: "+this);
//			sid = createServiceIdentifier(providerid, name, type, implclazz==null ? getClass() : implclazz);
		return sid;
	}
	
	/**
	 *  Get a service property.
	 *  @return The service property (if any).
	 * /
	public Object getProperty(String name)
	{
		return properties!=null ? properties.get(name) : null; 
	}*/
	
	/**
	 *  Get the providerid.
	 *  @return the providerid.
	 */
	public IComponentIdentifier getProviderId()
	{
		return providerid;
	}
	
	/**
	 *  Sets the access for the component.
	 *  @param access Component access.
	 */
	public IFuture<Void> setComponentAccess(IInternalAccess access)
	{
		internalaccess = access;
		setParent(internalaccess.getExternalAccess());
		
		// init properties when access is available
		initNFProperties();
		
		return IFuture.DONE;
	}

	/**
	 *  Get a service property.
	 *  @return The service property (if any).
	 */
	public Map<String, Object> getPropertyMap()
	{
		Map<String, Object>	ret;
		if(properties!=null)
		{
			ret	= properties;
		}
		else
		{
			ret	= Collections.emptyMap();
		}
		return ret;
	}
	
	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setPropertyMap(Map<String, Object> properties)
	{
		this.properties = properties;
	}
	
//	/**
//	 *  Get the hosting component of the service.
//	 *  @return The component.
//	 */
//	public IFuture<IExternalAccess> getComponent()
//	{
//	}

	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	startService()
	{
//		System.out.println("start: "+this);
		Future<Void> ret = new Future<Void>();
		
		boolean ex = false;
		synchronized(this)
		{
			if(started)
			{
				ex = true;
			}
			else
			{
				started = true;
			}
		}
		
		if(ex)
		{
			ret.setException(new RuntimeException("Already running."));
		}
		else 
		{
			ret.setResult(null);
//			ret.setResult(getServiceIdentifier());
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void>	shutdownService()
	{
//		if(getClass().getName().indexOf("ContextSer")!=-1)
//			System.out.println("shutdown service: "+getServiceIdentifier());

		// Deregister pojo->sid mapping in shutdown.
		BasicServiceInvocationHandler.removePojoServiceProxy(sid);
		
		final Future<Void> ret = new Future<Void>();
		isValid().addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
//				if(getClass().getName().indexOf("ContextSer")!=-1)
//					System.out.println("shutdowned service: "+getServiceIdentifier());
				
				if(!result.booleanValue())
				{
					ret.setException(new RuntimeException("Not running."));
				}
				else
				{
					shutdowned = true;
					ret.setResult(null);
//					System.out.println("shutdowned service: "+getServiceIdentifier());
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Generate a unique name.
	 *  @param The calling service class.
	 */
	public static String generateServiceName(Class<?> service)
	{
		synchronized(BasicService.class)
		{
			return SReflect.getInnerClassName(service)+"_#"+idcnt++;
		}
	}
	
	/**
	 *  Create a new service identifier.
	 *  @param providerid The provider id.
	 *  @param servicename The service name.
	 *  @return A service identifier.
	 */
	public static IServiceIdentifier createServiceIdentifier(IComponentIdentifier providerid, String servicename, 
		Class<?> servicetype, Class<?> serviceimpl, IResourceIdentifier rid)
	{
		return new ServiceIdentifier(providerid, servicetype, servicename!=null? servicename: generateServiceName(serviceimpl), rid);
	}
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getInternalAccess() 
	{
		return internalaccess;
	}
	
	/**
	 *  Check if the service is valid.
	 * /
	public IFuture checkValid()
	{
		Future ret = new Future();
		if(!isValid())
			ret.setException(new RuntimeException("Service invalid: "+getServiceIdentifier()));
		else
			ret.setResult(null);
		return ret;
	}*/
	
	/**
	 *  Check if the service is equal. The service is considered equal if the service identifiers match.
	 *  
	 *  @param obj Object of comparison.
	 *  @return True, if the object is a service with a matching service identifier.
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof IService)
		{
			return getServiceIdentifier().equals(((IService) obj).getServiceIdentifier());
		}
		return false;
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + getServiceIdentifier().hashCode();
	}
}
