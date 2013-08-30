package jadex.bridge.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
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
public class BasicService implements IInternalService
{	
	//-------- constants --------
	
	/** Default service timeout. */
	// Hack!!! field to be set by starter and read by Timeout annotation
//	public static long	DEFTIMEOUT	= 30000;
	
	/** Constant for remote default timeout. */
	public static long DEFAULT_REMOTE = 30000;//BasicService.DEFTIMEOUT;

	/** Constant for local default timeout. */
	public static long DEFAULT_LOCAL = 30000;//BasicService.DEFTIMEOUT;
	
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
	
	/** Non-functional properties. */
	protected Map<String, INFProperty<?, ?>> nfproperties;
	
	/** Non-functional properties of methods. */
	protected Map<MethodInfo, Map<String, INFProperty<?, ?>>> methodnfproperties;
	
	/** The service properties. */
	protected Map<String, Object> properties;
	
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
			Class<?> clazz = nfprop.type();
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
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFPropertyNames()
	{
		return new Future<String[]>(nfproperties != null? nfproperties.keySet().toArray(new String[nfproperties.size()]) : new String[0]);
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos()
	{
		Future<Map<String, INFPropertyMetaInfo>> ret = new Future<Map<String,INFPropertyMetaInfo>>();
		
		Map<String, INFPropertyMetaInfo> res = new HashMap<String, INFPropertyMetaInfo>();
		if(nfproperties!=null)
		{
			for(String key: nfproperties.keySet())
			{
				res.put(key, nfproperties.get(key).getMetaInfo());
			}
			ret.setResult(res);
		}
		
		return ret;
	}

	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(String name)
	{
		Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
		
		INFPropertyMetaInfo mi = nfproperties != null? nfproperties.get(name) != null? nfproperties.get(name).getMetaInfo() : null : null;
		
		if(mi == null)
		{
			internalaccess.getExternalAccess().getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
		}
		else
		{
			ret.setResult(mi);
		}
		
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNFPropertyValue(String name)
	{
		Future<T> ret = new Future<T>();
		
		INFProperty<T, ?> prop = (INFProperty<T, ?>) (nfproperties != null? nfproperties.get(name) : null);
		
		if (prop != null)
		{
			try
			{
				prop.getValue().addResultListener(new DelegationResultListener<T>(ret));
			}
			catch (Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			IFuture<T> fut = internalaccess.getExternalAccess().getNFPropertyValue(name);
			fut.addResultListener(new DelegationResultListener<T>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
//	public<T, U> IFuture<T> getNFPropertyValue(String name, Class<U> unit)
	public<T, U> IFuture<T> getNFPropertyValue(String name, U unit)
	{
		Future<T> ret = new Future<T>();
		
		INFProperty<T, U> prop = (INFProperty<T, U>) (nfproperties != null? nfproperties.get(name) : null);
		
		if(prop != null)
		{
			try
			{
				prop.getValue(unit).addResultListener(new DelegationResultListener<T>(ret));
			}
			catch (Exception e)
			{
				ret.setException(e);
		//			ret.setException(new ClassCastException("Requested value type (" + String.valueOf(type) + ") does not match value type (" + String.valueOf(reto.getClass()) + ") for this non-functional property: " + name));
			}
		}
		else
		{
//			internalaccess.getExternalAccess().getNFPropertyValue(name, unit).addResultListener(new DelegationResultListener<T>(ret));
			IFuture<T> fut = internalaccess.getExternalAccess().getNFPropertyValue(name, unit);
			fut.addResultListener(new DelegationResultListener<T>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param metainfo The metainfo.
	 */
	public IFuture<Void> addNFProperty(INFProperty<?, ?> nfprop)
	{
		if(nfproperties==null)
			nfproperties = new HashMap<String, INFProperty<?,?>>();
		nfproperties.put(nfprop.getName(), nfprop);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public IFuture<Void> removeNFProperty(String name)
	{
		Future<Void> ret = new Future<Void>();
		if(nfproperties!=null)
		{
			INFProperty<?, ?> prop = nfproperties.remove(name);
			if(prop!=null)
			{
				prop.dispose().addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Returns meta information about a non-functional properties of all methods.
	 *  @return The meta information about a non-functional properties.
	 */
	public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>>  getMethodNFPropertyMetaInfos()
	{
		Map<MethodInfo, Map<String, INFPropertyMetaInfo>> ret = new HashMap<MethodInfo, Map<String,INFPropertyMetaInfo>>();
		if(methodnfproperties!=null)
		{
			for(MethodInfo mi: methodnfproperties.keySet())
			{
				Map<String, INFPropertyMetaInfo> res = new HashMap<String, INFPropertyMetaInfo>();
				ret.put(mi, res);
				Map<String, INFProperty<?, ?>> tmp = methodnfproperties.get(mi);
				for(String name: tmp.keySet())
				{
					INFProperty<?, ?> prop = tmp.get(name);
					res.put(name, prop.getMetaInfo());
				}
			}
		}
		return new Future<Map<MethodInfo,Map<String,INFPropertyMetaInfo>>>(ret);
	}
	
	/**
	 *  Returns meta information about a non-functional properties of a method.
	 *  @return The meta information about a non-functional properties.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getMethodNFPropertyMetaInfos(MethodInfo method)
	{
		Map<String, INFPropertyMetaInfo> ret = new HashMap<String, INFPropertyMetaInfo>();
		
		if(methodnfproperties!=null)
		{
			Map<String, INFProperty<?, ?>> tmp = methodnfproperties.get(method);
			for(String name: tmp.keySet())
			{
				INFProperty<?, ?> prop = tmp.get(name);
				ret.put(name, prop.getMetaInfo());
			}
		}
		
		return new Future<Map<String,INFPropertyMetaInfo>>(ret);
	}

	
	/**
	 *  Returns the names of all non-functional properties of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @return The names of the non-functional properties of the specified method.
	 */
	public IFuture<String[]> getMethodNFPropertyNames(MethodInfo method)
	{
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		return new Future<String[]>(nfmap != null? nfmap.keySet().toArray(new String[nfproperties.size()]) : new String[0]);
	}
	
	/**
	 *  Returns the meta information about a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of the specified method.
	 */
	public IFuture<INFPropertyMetaInfo> getMethodNFPropertyMetaInfo(MethodInfo method, String name)
	{
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		INFProperty<?, ?> prop = nfmap != null? nfmap.get(name) : null;
		INFPropertyMetaInfo mi = prop != null? prop.getMetaInfo() : null;
		return mi != null? new Future<INFPropertyMetaInfo>(mi) : getNFPropertyMetaInfo(name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
	public <T> IFuture<T> getMethodNFPropertyValue(MethodInfo method, String name)
	{
		Future<T> ret = new Future<T>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		INFProperty<T, ?> prop = (INFProperty<T, ?>) (nfmap != null? nfmap.get(name) : null);
		if (prop != null)
		{
			try
			{
				prop.getValue().addResultListener(new DelegationResultListener<T>(ret));
			}
			catch (Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret = (Future<T>) getNFPropertyValue(name);
		}
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method, performs unit conversion.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(Method method, String name, Class<U> unit)
	public <T, U> IFuture<T> getMethodNFPropertyValue(MethodInfo method, String name, U unit)
	{
		Future<T> ret = new Future<T>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		INFProperty<T, U> prop = (INFProperty<T, U>) (nfmap != null? nfmap.get(name) : null);
		if (prop != null)
		{
			try
			{
				prop.getValue(unit).addResultListener(new DelegationResultListener<T>(ret));
			}
			catch (Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret = (Future<T>) getNFPropertyValue(name, unit);
		}
		return ret;
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addMethodNFProperty(MethodInfo method, INFProperty<?, ?> nfprop)
	{
		if(methodnfproperties==null)
			methodnfproperties = new HashMap<MethodInfo, Map<String,INFProperty<?,?>>>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		if (nfmap == null)
		{
			nfmap = new HashMap<String, INFProperty<?,?>>();
			methodnfproperties.put(method, nfmap);
		}
		nfmap.put(nfprop.getName(), nfprop);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param The name.
	 */
	public IFuture<Void> removeMethodNFProperty(MethodInfo method, String name)
	{
		Future<Void> ret = new Future<Void>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		if(nfmap != null)
		{
			INFProperty<?, ?> prop = nfmap.remove(name);
			if(prop!=null)
			{
				prop.dispose().addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Sets the access for the component.
	 *  @param access Component access.
	 */
	public IFuture<Void> setComponentAccess(IInternalAccess access)
	{
		internalaccess = access;
		
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
//		if(sid.getProviderId().getParent()==null && getClass().getName().indexOf("ContextSer")!=-1)
//			System.out.println("shutdown service: "+getServiceIdentifier());

		// Deregister pojo->sid mapping in shutdown.
		BasicServiceInvocationHandler.removePojoServiceProxy(sid);
		
		final Future<Void> ret = new Future<Void>();
		isValid().addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
//				if(sid.getProviderId().getParent()==null && getClass().getName().indexOf("ContextSer")!=-1)
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
		if (obj instanceof IService)
		{
			return getServiceIdentifier().equals(((IService) obj).getServiceIdentifier());
		}
		return false;
	}
}
