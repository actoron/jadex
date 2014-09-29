package jadex.bridge.component.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.nonfunctional.NFMethodPropertyProvider;
import jadex.bridge.nonfunctional.NFPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.MethodInfo;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class NFPropertyComponentFeature extends AbstractComponentFeature implements INFPropertyComponentFeature
{
	//-------- attributes --------
	
	/** The component property provider. */
	protected INFPropertyProvider compprovider;
	
	/** The nf property providers for required services. */
	protected Map<IServiceIdentifier, INFMixedPropertyProvider> proserprops;
	
	/** The nf property providers for required services. */
	protected Map<IServiceIdentifier, INFMixedPropertyProvider> reqserprops;
	
	/** The max number of preserved req service providers. */
	protected int maxreq;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public NFPropertyComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void>	init()
	{
		final Future<Void> ret = new Future<Void>();
//		getComponent().getComponentDescription().
		
		IProvidedServicesFeature psf = getComponent().getComponentFeature(IProvidedServicesFeature.class);
		if(psf!=null)
		{
			Map<Class<?>, Collection<IInternalService>> sers = ((ProvidedServicesComponentFeature)psf).getServices();
			for(Class<?> type: sers.keySet())
			{
				for(IInternalService ser: sers.get(type))
				{
					IInternalService 
				}
			}
		}
		
		IComponentManagementService cms = SServiceProvider.getLocalService(getComponent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		IComponentIdentifier pacid = getComponent().getComponentIdentifier().getParent();
		if(pacid!=null)
		{
			cms.getExternalAccess(pacid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret) 
			{
				public void customResultAvailable(IExternalAccess exta) 
				{
					exta.scheduleStep(new IComponentStep<INFPropertyProvider>() 
					{
						public IFuture<INFPropertyProvider> execute(IInternalAccess ia) 
						{
							INFPropertyComponentFeature nff = ia.getComponentFeature(INFPropertyComponentFeature.class);
							return new Future<INFPropertyProvider>(nff.getComponentPropertyProvider());
						}
					}).addResultListener(new ExceptionDelegationResultListener<INFPropertyProvider, Void>(ret) 
					{
						public void customResultAvailable(final INFPropertyProvider nfp) 
						{
							compprovider = new NFPropertyProvider() 
							{
								public IInternalAccess getInternalAccess() 
								{
									return getComponent();
								}
								
								public INFPropertyProvider getParent()
								{
									return nfp;
								}
							};
							ret.setResult(null);
						}
					});
				}
			});
		}
		else
		{
			this.compprovider = new NFPropertyProvider() 
			{
				public IInternalAccess getInternalAccess() 
				{
					return getComponent();
				}
				
				public INFPropertyProvider getParent()
				{
					return null;
				}
			};
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get the component property provider.
	 */
	public INFPropertyProvider getComponentPropertyProvider()
	{
		return compprovider;
	}
	
	/**
	 *  Get the required service property provider for a service.
	 */
	public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid)
	{
		INFMixedPropertyProvider ret = null;
		if(reqserprops==null)
		{
			reqserprops = new LRU<IServiceIdentifier, INFMixedPropertyProvider>(maxreq, new ILRUEntryCleaner<IServiceIdentifier, INFMixedPropertyProvider>()
			{
				public void cleanupEldestEntry(Entry<IServiceIdentifier, INFMixedPropertyProvider> eldest)
				{
					eldest.getValue().shutdownNFPropertyProvider().addResultListener(new DefaultResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
						}
					});
				}
			}); 
		}
		ret = reqserprops.get(sid);
		if(ret==null)
		{
			ret = new NFMethodPropertyProvider() 
			{
				public IInternalAccess getInternalAccess() 
				{
					return getComponent();
				}
				
				// parent of required service property?
				public INFPropertyProvider getParent()
				{
					return null;
				}
			}; 
			reqserprops.put(sid, ret);
		}
		return ret;
	}
	
	/**
	 *  Has the service a property provider.
	 */
	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
	{
		return reqserprops!=null? reqserprops.get(sid)!=null: false;
	}
	
//	/**
//	 *  Get the required service property provider for a service.
//	 */
//	public INFMixedPropertyProvider getRequiredServicePropertyProvider(String name);
	
	/**
	 *  Get the provided service property provider for a service.
	 */
	public INFMixedPropertyProvider getProvidedServicePropertyProvider(IServiceIdentifier sid)
	{
		INFMixedPropertyProvider ret = null;
		if(proserprops==null)
		{
			proserprops = new HashMap<IServiceIdentifier, INFMixedPropertyProvider>();
		}
		ret = proserprops.get(sid);
		if(ret==null)
		{
			ret = new NFMethodPropertyProvider() 
			{
				public IInternalAccess getInternalAccess() 
				{
					return getComponent();
				}
				
				public INFPropertyProvider getParent()
				{
					return compprovider;
				}
			}; 
			proserprops.put(sid, ret);
		}
		return ret;
	}
	
//	/**
//	 *  Get the provided service property provider for a service.
//	 */
//	public INFMixedPropertyProvider getProvidedServicePropertyProvider(Class<?> iface)
//	{
//	}
	
	/**
	 * 
	 */
	public void initNFProperties(IInternalService ser)
	{
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superclazz = ser.getServiceIdentifier().getServiceType().getType(cl);
		while(superclazz != null && !Object.class.equals(superclazz))
		{
			classes.add(superclazz);
			superclazz = superclazz.getSuperclass();
		}
		superclazz = ser.impltype!=null? impltype: this.getClass();
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
	public void addNFProperties(NFProperties nfprops, Map<String, INFProperty<?,?>> nfps, IService ser, MethodInfo mi)
	{
		INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(ser.getServiceIdentifier());
		for(NFProperty nfprop : nfprops.value())
		{
			Class<?> clazz = nfprop.value();
			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, getComponent(), ser, mi);
			prov.addNFProperty(prop);
		}
	}
}
