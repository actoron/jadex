package jadex.bridge.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.SReflect;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
	/** The started state. */
	protected boolean started;
	
	/** The shutdowned state. */
	protected boolean shutdowned;
	
	/** The service id. */
	protected IServiceIdentifier sid;
	
	/** Non-functional properties. */
	protected Map<String, INFProperty<?, ?>> nfproperties;
	
	/** The service properties. */
	protected Map<String, Object> properties;
	
	/** The provider id. */
	protected IComponentIdentifier providerid;
	
	//-------- constructors --------

	/**
	 *  Create a new service.
	 */
	// todo: remove type!!!
	public BasicService(IComponentIdentifier providerid, Class<?> type, Map<String, Object> properties)
	{
//		if(!SReflect.isSupertype(type, getClass()))
//			throw new RuntimeException("Service must implement provided interface: "+getClass().getName()+", "+type.getName());
		this.providerid = providerid;
//		this.type = type;
//		this.implclazz = implclazz;
		this.properties	= properties;
		
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
		
		if (type.isAnnotationPresent(NFProperties.class) || this.getClass().isAnnotationPresent(NFProperties.class))
		{
			List<NFProperty> nfprops = new ArrayList<NFProperty>();
			NFProperties typenfprops = type.getAnnotation(NFProperties.class);
			if (typenfprops != null)
			{
				nfprops.addAll((Collection<? extends NFProperty>) Arrays.asList(typenfprops.value()));
			}
			Class<?> clazz = this.getClass();
			typenfprops = this.getClass().getAnnotation(NFProperties.class);
			if (typenfprops != null)
			{
				nfprops.addAll((Collection<? extends NFProperty>) Arrays.asList(typenfprops.value()));
			}
			
			for (NFProperty nfprop : nfprops)
			{
				Class<?> clazz = nfprop.type();
				try
				{
					Constructor<?> con = clazz.getConstructor(String.class);
					INFProperty<?, ?> prop = (INFProperty<?, ?>) con.newInstance(nfprop.name());
					
					if (nfproperties == null)
					{
						nfproperties = new HashMap<String, INFProperty<?,?>>();
					}
					nfproperties.put(nfprop.name(), prop);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
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
	 *  
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNonFunctionalPropertyNames()
	{
		return new Future<String[]>(nfproperties != null? nfproperties.keySet().toArray(new String[nfproperties.size()]) : new String[0]);
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNfPropertyMetaInfo(String name)
	{
		return new Future<INFPropertyMetaInfo>(nfproperties != null? nfproperties.get(name) != null? nfproperties.get(name).getMetaInfo() : null : null);
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public<T extends Object> IFuture<T> getNonFunctionalPropertyValue(String name, Class<T> type)
	{
		Future<T> ret = new Future<T>();
		
		INFProperty<T, ?> prop = (INFProperty<T, ?>) (nfproperties != null? nfproperties.get(name) : null);
		
		try
		{
			ret.setResult(prop != null? prop.getValue(type) : null);
		}
		catch (Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public<T extends Object, U extends Object> IFuture<T> getNonFunctionalPropertyValue(String name, Class<T> type, Class<U> unit)
	{
		Future<T> ret = new Future<T>();
		
		INFProperty<T, U> prop = (INFProperty<T, U>) (nfproperties != null? nfproperties.get(name) : null);
		
		try
		{
			ret.setResult(prop != null? prop.getValue(type, unit) : null);
		}
		catch (Exception e)
		{
			ret.setException(e);
//			ret.setException(new ClassCastException("Requested value type (" + String.valueOf(type) + ") does not match value type (" + String.valueOf(reto.getClass()) + ") for this non-functional property: " + name));
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
				if(sid.getProviderId().getParent()==null && getClass().getName().indexOf("ContextSer")!=-1)
					System.out.println("shutdowned service: "+getServiceIdentifier());
				
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
