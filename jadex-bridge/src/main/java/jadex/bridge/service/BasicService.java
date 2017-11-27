package jadex.bridge.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.component.impl.NFPropertyComponentFeature;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.ServiceKeyExtractor;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.commons.SReflect;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Basic service provide a simple default isValid() implementation
 *  that returns true after start service and false afterwards.
 */
public class BasicService implements IInternalService //extends NFMethodPropertyProvider implements IInternalService
{
	//-------- constants --------

//	/** Constant for timeout name in non-functional properties. */
//	public static final String TIMEOUT = "timeout";

	//-------- attributes --------

	/** The id counter. */
	protected static long idcnt;
	
	/** Internal access to its component. */
	protected IInternalAccess internalaccess;
	
	/** The started state. */
	protected volatile boolean started;
	
	/** The shutdowned state. */
	protected volatile boolean shutdowned;
	
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
//		super(null);
		
//		if(properties!=null && properties.size()>0)
//			System.out.println("sdyf");
		
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
		
//		if(type.isAnnotationPresent(TargetResolver.class))
//		{
//			TargetResolver tr = type.getAnnotation(TargetResolver.class);
//			if(this.properties==null) 
//				this.properties = new HashMap<String, Object>();
//			this.properties.put(TargetResolver.TARGETRESOLVER, tr.value());
//		}
//		
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
	
//	/**
//	 * 
//	 */
//	public void initNFProperties()
//	{
//		IService ser = (IService)internalaccess.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(type);
//		
//		List<Class<?>> classes = new ArrayList<Class<?>>();
//		Class<?> superclazz = type;
//		while(superclazz != null && !Object.class.equals(superclazz))
//		{
//			classes.add(superclazz);
//			superclazz = superclazz.getSuperclass();
//		}
//		superclazz = impltype!=null? impltype: this.getClass();
//		while(superclazz != null && !BasicService.class.equals(superclazz) && !Object.class.equals(superclazz))
//		{
//			classes.add(superclazz);
//			superclazz = superclazz.getSuperclass();
//		}
//		Collections.reverse(classes);
//		
//		for(Class<?> sclazz: classes)
//		{
//			if(sclazz.isAnnotationPresent(NFProperties.class))
//			{
//				if(nfproperties==null)
//					nfproperties = new HashMap<String, INFProperty<?,?>>();
//				addNFProperties(sclazz.getAnnotation(NFProperties.class), nfproperties, ser, null);
//			}
//			
//			Method[] methods = sclazz.getMethods();
//			for(Method m : methods)
//			{
//				if(m.isAnnotationPresent(NFProperties.class))
//				{
//					if(methodnfproperties==null)
//						methodnfproperties = new HashMap<MethodInfo, Map<String,INFProperty<?,?>>>();
//					
//					Map<String,INFProperty<?,?>> nfmap = methodnfproperties.get(new MethodInfo(m));
//					if(nfmap == null)
//					{
//						nfmap = new HashMap<String, INFProperty<?,?>>();
//						methodnfproperties.put(new MethodInfo(m), nfmap);
//					}
//				}
//			}
//		}
//		
//		if(methodnfproperties!=null)
//		{
//			for(MethodInfo key: methodnfproperties.keySet())
//			{
//				Map<String,INFProperty<?,?>> nfmap = methodnfproperties.get(key);
//				addNFProperties(key.getMethod(internalaccess.getClassLoader()).getAnnotation(NFProperties.class), nfmap, ser, key);
//			}
//		}
//	}
	
//	/**
//	 *  Add nf properties from a type.
//	 */
//	public void addNFProperties(NFProperties nfprops, Map<String, INFProperty<?, ?>> nfps, IService ser, MethodInfo mi)
//	{
//		for(NFProperty nfprop : nfprops.value())
//		{
//			Class<?> clazz = nfprop.value();
//			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, internalaccess, ser, mi);
//			nfps.put(prop.getName(), prop);
//		}
//	}
	
	//-------- methods --------
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 *  
	 */
	public IFuture<Boolean> isValid()
	{
//		if(getServiceIdentifier().getServiceName().indexOf("Decoupled")!=-1)
//			System.out.println("isValid: "+getServiceIdentifier()+": "+(started && !shutdowned));
		return started && !shutdowned ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Set the service identifier.
	 */
	public void createServiceIdentifier(String name, Class<?> implclazz, IResourceIdentifier rid, Class<?> type, String scope, boolean unrestricted)
	{
		this.sid = createServiceIdentifier(providerid, name, type, implclazz, rid, scope, unrestricted);
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
//		setParent(internalaccess.getExternalAccess());
//		
//		// init properties when access is available
		
		return initNFProperties();
		
//		return IFuture.DONE;
	}
	
	/**
	 *  Init the non-functional properties (todo: move to other location?)
	 */
	protected IFuture<Void> initNFProperties()
	{
		final Future<Void> ret = new Future<Void>();
		if(getInternalAccess().getComponentFeature0(INFPropertyComponentFeature.class)!=null)
		{
			final INFPropertyComponentFeature nfcf = getInternalAccess().getComponentFeature(INFPropertyComponentFeature.class);
			IProvidedServicesFeature psf = getInternalAccess().getComponentFeature(IProvidedServicesFeature.class);
			IInternalService ser = (IInternalService)getInternalAccess().getComponentFeature(IProvidedServicesFeature.class).getProvidedService(type);
			Class<?> impltype = psf.getProvidedServiceRawImpl(ser.getServiceIdentifier())!=null? psf.getProvidedServiceRawImpl(ser.getServiceIdentifier()).getClass(): null;
			// todo: make internal interface for initProperties
//			if(type!=null && type.getName().indexOf("ITest")!=-1)
//				System.out.println("sdfsdf");
			((NFPropertyComponentFeature)nfcf).initNFProperties(ser, impltype)
				.addResultListener(getInternalAccess().getComponentFeature(IExecutionFeature.class)	// TODO: why wrong thread (start 2x autoterminate on 6-core) 
					.createResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
			{
				public void customResultAvailable(Void result) throws Exception
				{					
					nfcf.getProvidedServicePropertyProvider(sid).getNFPropertyValue(TagProperty.NAME).addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
//							System.out.println("Starting serviceINIT: "+getServiceIdentifier()+" "+getInternalAccess().getComponentFeature(IExecutionFeature.class).isComponentThread());
							Collection<String> coll = (Collection<String>)result;
							if(coll!=null && coll.size()>0)
							{
								if(properties==null)
									properties = new HashMap<String, Object>();
								
								Set<String> tags = new LinkedHashSet<String>(coll);
								// Hack!!! save props in service identifier 
//								properties.put(TagProperty.SERVICE_PROPERTY_NAME, tags);
								((ServiceIdentifier)sid).setTags(tags);
								// Hack!!! re-index
								ServiceRegistry reg = (ServiceRegistry)ServiceRegistry.getRegistry(sid.getProviderId());
								IService orig = reg.getIndexer().getValues(ServiceKeyExtractor.KEY_TYPE_SID, getServiceIdentifier().toString()).iterator().next();
								reg.getIndexer().addValue(orig);
							}
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							exception.printStackTrace();
//							System.out.println("Starting serviceINITEX: "+getServiceIdentifier()+" "+getInternalAccess().getComponentFeature(IExecutionFeature.class).isComponentThread());
							ret.setResult(null);
						}
					});
				}
			}));
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
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
		if(started)
		{
			ex = true;
		}
		else
		{
			started = true;
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
		Class<?> servicetype, Class<?> serviceimpl, IResourceIdentifier rid, String scope, boolean unrestricted)
	{
		Set<String> networknames = (Set<String>)Starter.getPlatformValue(providerid, Starter.DATA_NETWORKNAMESCACHE);
		return createServiceIdentifier(providerid, servicename, servicetype, serviceimpl, rid, scope, networknames, unrestricted);
	}
	
	/**
	 *  Create a new service identifier.
	 *  @param providerid The provider id.
	 *  @param servicename The service name.
	 *  @return A service identifier.
	 */
	public static IServiceIdentifier createServiceIdentifier(IComponentIdentifier providerid, String servicename, 
		Class<?> servicetype, Class<?> serviceimpl, IResourceIdentifier rid, String scope, Set<String> networknames, boolean unrestricted)
	{
		return new ServiceIdentifier(providerid, servicetype, servicename!=null? servicename: generateServiceName(serviceimpl), rid, scope, networknames, unrestricted);
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
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return SReflect.getUnqualifiedClassName(getClass())+"("+sid+")";
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the default timeout for a method.
	 */
	public static long getMethodTimeout(Class<?>[] interfaces, Method method, boolean remote)
	{
		long ret = Timeout.UNSET;
		
		Class<?>[] allinterfaces = SReflect.getSuperInterfaces(interfaces);
		
		long deftimeout	= Timeout.UNSET;
		for(int i=0; deftimeout==Timeout.UNSET && i<allinterfaces.length; i++)
		{
			// Default timeout for interface (only if method is declared in this interface)
			if(allinterfaces[i].isAnnotationPresent(Timeout.class) && 
				SReflect.getMethod(allinterfaces[i], method.getName(), method.getParameterTypes())!=null)
			{
				Timeout	ta	= (Timeout)allinterfaces[i].getAnnotation(Timeout.class);
				deftimeout = remote? ta.remote(): ta.local();
				if(Timeout.UNSET==deftimeout)
					deftimeout = ta.value();
			}
		}
		
		// Timeout on method overrides global timeout settings
		if(method.isAnnotationPresent(Timeout.class))
		{
			Timeout	ta	= method.getAnnotation(Timeout.class);
			ret = remote? ta.remote(): ta.local();
			if(Timeout.UNSET==ret)
				ret = ta.value();
		}
		
		if(Timeout.UNSET!=deftimeout && Timeout.UNSET==ret)
		{
			ret = deftimeout;
		}
				
//		return ret==Timeout.UNSET? remote? BasicService.getRemoteDefaultTimeout(): BasicService.getLocalDefaultTimeout(): ret;
		return ret;
	}
	
//	/**
//	 *  Get the implementation type.
//	 *  @return The implementation type.
//	 */
//	public IFuture<Class<?>> getImplementationType()
//	{
//		return new Future<Class<?>>(impltype!=null? impltype: getClass());
//	}
	
	/**
	 *  Get the interface type.
	 *  @return The interface type.
	 */
	public Class<?> getInterfaceType()
	{
		return type;
	}
	
//	/**
//	 * 
//	 */
//	public Class<?> getFeatureClass(Class<?> type)
//	{
//		IComponentFeature feat = (IComponentFeature)getInternalAccess().getComponentFeature(type);
//		return feat.getExternalFacadeType(this);
//	}
}
