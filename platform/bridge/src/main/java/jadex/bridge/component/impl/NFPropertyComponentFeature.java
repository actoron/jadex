package jadex.bridge.component.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.modelinfo.NFPropertyInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.nonfunctional.NFMethodPropertyProvider;
import jadex.bridge.nonfunctional.NFPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.annotation.SNameValue;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Tags;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.MethodInfo;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Feature for non functional properties of the component, provided/required services and methods.
 */
public class NFPropertyComponentFeature extends AbstractComponentFeature implements INFPropertyComponentFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(INFPropertyComponentFeature.class, NFPropertyComponentFeature.class,
		new Class<?>[]{IProvidedServicesFeature.class, IRequiredServicesFeature.class}, null, false);
	
	//-------- attributes --------
	
	/** The component property provider. */
	protected INFPropertyProvider compprovider;
	
	/** The nf property providers for required services. */
	protected Map<IServiceIdentifier, INFMixedPropertyProvider> proserprops;
	
	/** The nf property providers for required services. */
	protected Map<IServiceIdentifier, INFMixedPropertyProvider> reqserprops;
	
	/** The max number of preserved req service providers. */
	protected int maxreq;
	
	/** The parent provider. */
	protected INFPropertyProvider parent;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public NFPropertyComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		this.maxreq = 100; // todo: make configurable
	}
	
	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void>	init()
	{
//		System.out.println("init start: "+getComponent().getComponentIdentifier());
		
		final Future<Void> ret = new Future<Void>();
		
		int cnt = 0;
		LateCounterListener<Void> lis = new LateCounterListener<Void>(new DelegationResultListener<Void>(ret));
		
		// Init nf component props
		List<NFPropertyInfo> nfprops = getComponent().getModel().getNFProperties();
		if(nfprops!=null)
		{
			for(NFPropertyInfo nfprop: nfprops)
			{
				try
				{
					Class<?> clazz = nfprop.getClazz().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports());
					INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getInternalAccess(), null, null, nfprop.getParameters());
					cnt++;
					getComponentPropertyProvider().addNFProperty(nfp).addResultListener(lis);
				}
				catch(Exception e)
				{
					getComponent().getLogger().warning("Property creation problem: "+e);
				}
			}
		}
		
		// now done in basic service
//		IProvidedServicesFeature psf = getComponent().getComponentFeature(IProvidedServicesFeature.class);
//		if(psf!=null)
//		{
//			Map<Class<?>, Collection<IInternalService>> sers = ((ProvidedServicesComponentFeature)psf).getServices();
//			if(sers!=null)
//			{
//				for(Class<?> type: sers.keySet())
//				{
//					for(IInternalService ser: sers.get(type))
//					{
//						cnt++;
//						Class<?> impltype = psf.getProvidedServiceRawImpl(ser.getId())!=null? psf.getProvidedServiceRawImpl(ser.getServiceIdentifier()).getClass(): null;
//						initNFProperties(ser, impltype).addResultListener(lis);
//					}
//				}
//			}
//		}
		
		lis.setMax(cnt);
		
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.out.println("init end: "+getComponent().getComponentIdentifier());
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("init end ex: "+getComponent().getComponentIdentifier());
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Get the component property provider.
	 */
	public INFPropertyProvider getComponentPropertyProvider()
	{
		if(compprovider==null)
			this.compprovider = new NFPropertyProvider(getComponent().getId().getParent(), getInternalAccess()); 
		
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
			ret = new NFMethodPropertyProvider(null, getInternalAccess()); 
			reqserprops.put(sid, ret);
//			System.out.println("created req ser provider: "+sid+" "+hashCode());
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
			// TODO: parent???
//			ret = new NFMethodPropertyProvider(getComponent().getComponentIdentifier(), getComponent()); 
			ret = new NFMethodPropertyProvider(null, getInternalAccess()); 
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
	 *  Init the service and method nf properties. 
	 */
	public IFuture<Void> initNFProperties(final IInternalService ser, Class<?> impltype)
	{
		final Future<Void> ret = new Future<Void>();
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superclazz = ser.getServiceId().getServiceType().getType(getComponent().getClassLoader());
		while(superclazz != null && !Object.class.equals(superclazz))
		{
			classes.add(superclazz);
			superclazz = superclazz.getSuperclass();
		}
		
		if(impltype!=null)
		{
			superclazz = impltype;
			while(superclazz != null && !BasicService.class.equals(superclazz) && !Object.class.equals(superclazz))
			{
				classes.add(superclazz);
				superclazz = superclazz.getSuperclass();
			}
		}
//		Collections.reverse(classes);
		
		int cnt = 0;
		
		LateCounterListener<Void> lis = new LateCounterListener<Void>(new DelegationResultListener<Void>(ret));
		
		Map<MethodInfo, Method> meths = new HashMap<MethodInfo, Method>();
		for(Class<?> sclazz: classes)
		{
			if(sclazz.isAnnotationPresent(NFProperties.class))
			{
				addNFProperties(sclazz.getAnnotation(NFProperties.class), ser).addResultListener(lis);
				cnt++;
			}
			
			if(sclazz.isAnnotationPresent(Tags.class))
			{
				addTags(sclazz.getAnnotation(Tags.class), ser).addResultListener(lis);
				cnt++;
			}
			
			Method[] methods = sclazz.getMethods();
			for(Method m : methods)
			{
				if(m.isAnnotationPresent(NFProperties.class))
				{
					MethodInfo mis = new MethodInfo(m.getName(), m.getParameterTypes());
					if(!meths.containsKey(mis))
					{
						meths.put(mis, m);
					}
				}
			}
		}
		
		for(MethodInfo key: meths.keySet())
		{
			addNFMethodProperties(meths.get(key).getAnnotation(NFProperties.class), ser, key).addResultListener(lis);
			cnt++;
		}
		
		// Set the number of issued calls
		lis.setMax(cnt);
		
		return ret;
	}
	
	/**
	 *  Add nf properties from a type.
	 */
	public IFuture<Void> addNFProperties(NFProperties nfprops, IService ser)
	{
		Future<Void> ret = new Future<Void>();
		INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(ser.getServiceId());
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(nfprops.value().length, new DelegationResultListener<Void>(ret));
		for(NFProperty nfprop : nfprops.value())
		{
			Class<?> clazz = nfprop.value();
			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, getInternalAccess(), ser, null, SNameValue.createUnparsedExpressionsList(nfprop.parameters()));
			prov.addNFProperty(prop).addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Add nf properties from a type.
	 */
	public IFuture<Void> addTags(Tags tags, IService ser)
	{
		INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(ser.getServiceId());
		
		List<UnparsedExpression> params = new ArrayList<>();
		
		if(tags.argumentname().length()>0)
			params.add(new UnparsedExpression(TagProperty.ARGUMENT, "\""+tags.argumentname()+"\""));
		
		for(int i=0; i<tags.value().length; i++)
		{
			params.add(new UnparsedExpression(TagProperty.NAME+"_"+i, tags.value()[i]));
		}
		
		INFProperty<?, ?> prop = AbstractNFProperty.createProperty(TagProperty.class, getInternalAccess(), ser, null, params);
		
		return prov.addNFProperty(prop);
	}
	
	/**
	 *  Add nf properties from a type.
	 */
	public IFuture<Void> addNFMethodProperties(NFProperties nfprops, IService ser, MethodInfo mi)
	{
		Future<Void> ret = new Future<Void>();
		
		INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(ser.getServiceId());
		CounterResultListener<Void> lis = new CounterResultListener<Void>(nfprops.value().length, new DelegationResultListener<Void>(ret));
		for(NFProperty nfprop : nfprops.value())
		{
			Class<?> clazz = ((NFProperty)nfprop).value();
			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, getInternalAccess(), ser, mi, SNameValue.createUnparsedExpressionsList(nfprop.parameters()));
			prov.addMethodNFProperty(mi, prop).addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Get external feature facade.
	 */
	public <T> T getExternalFacade(Object context)
	{
		T ret = null;
		if(context instanceof IService)
		{
//			IServiceIdentifier sid = (IServiceIdentifier)context;
			ret = (T)getProvidedServicePropertyProvider(((IService)context).getServiceId());
		}
		else 
		{
			ret = (T)getComponentPropertyProvider();
		}
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public <T> Class<T> getExternalFacadeType(Object context)
//	{
//		Class<T> ret = (Class<T>)INFPropertyComponentFeature.class;
//		if(context instanceof IService)
//		{
//			ret = (Class<T>)INFMixedPropertyProvider.class;
//		}
//		return ret;
//	}
	
	
	/**
	 *  Returns the declared names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFPropertyNames()
	{
		return getComponentPropertyProvider().getNFPropertyNames();
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFAllPropertyNames()
	{
		return getComponentPropertyProvider().getNFAllPropertyNames();
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos()
	{
		return getComponentPropertyProvider().getNFPropertyMetaInfos();
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(String name)
	{
		return getNFPropertyMetaInfo(name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNFPropertyValue(String name)
	{
		return getComponentPropertyProvider().getNFPropertyValue(name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T, U> IFuture<T> getNFPropertyValue(String name, U unit)
	{
		return getComponentPropertyProvider().getNFPropertyValue(name, unit);
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addNFProperty(INFProperty<?, ?> nfprop)
	{
		return getComponentPropertyProvider().addNFProperty(nfprop);
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public IFuture<Void> removeNFProperty(String name)
	{
		return getComponentPropertyProvider().removeNFProperty(name);
	}
	
	/**
	 *  Shutdown the provider.
	 */
	public IFuture<Void> shutdownNFPropertyProvider()
	{
		return getComponentPropertyProvider().shutdownNFPropertyProvider();
	}
	
	//-------- service methods --------
	
	/**
	 *  Returns the declared names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFPropertyNames(IServiceIdentifier sid)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			 return getProvidedServicePropertyProvider(sid).getNFPropertyNames();
		}
		else
		{
			final Future<String[]> ret = new Future<String[]>();

			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String[]>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<String[]>()
					{
						@Classname("getNFPropertyNames9")
						public IFuture<String[]> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyNames();
						}
					}).addResultListener(new DelegationResultListener<String[]>(ret));
				}
			});

			return ret;
		}
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFAllPropertyNames(final IServiceIdentifier sid)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			 return getProvidedServicePropertyProvider(sid).getNFAllPropertyNames();
		}
		else
		{
			final Future<String[]> ret = new Future<String[]>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String[]>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<String[]>()
					{
						@Classname("getNFAllPropertyNames10")
						public IFuture<String[]> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getNFAllPropertyNames();
						}
					}).addResultListener(new DelegationResultListener<String[]>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos(IServiceIdentifier sid)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(sid);
			if(prov!=null)
			{
				IFuture<Map<String, INFPropertyMetaInfo>> metainf = prov.getNFPropertyMetaInfos();
				if(metainf!=null)
				{
					return metainf;
				}
			}
			return new Future<Map<String,INFPropertyMetaInfo>>(new HashMap<String,INFPropertyMetaInfo>());
		}
		else
		{
			final Future<Map<String, INFPropertyMetaInfo>> ret = new Future<Map<String, INFPropertyMetaInfo>>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Map<String, INFPropertyMetaInfo>>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Map<String, INFPropertyMetaInfo>>()
					{
						@Classname("getNFPropertyMetaInfos11")
						public IFuture<Map<String, INFPropertyMetaInfo>> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							INFMixedPropertyProvider prov = nfp.getProvidedServicePropertyProvider(sid);
							if(prov!=null)
							{
								IFuture<Map<String, INFPropertyMetaInfo>> metainf = prov.getNFPropertyMetaInfos();
								if(metainf!=null)
								{
									return metainf;
								}
							}
							return new Future<Map<String,INFPropertyMetaInfo>>(new HashMap<String,INFPropertyMetaInfo>());
						}
					}).addResultListener(new DelegationResultListener<Map<String, INFPropertyMetaInfo>>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(final IServiceIdentifier sid, final String name)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getNFPropertyMetaInfo(name);
		}
		else
		{
			final Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, INFPropertyMetaInfo>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<INFPropertyMetaInfo>()
					{
						@Classname("getNFPropertyMetaInfo12")
						public IFuture<INFPropertyMetaInfo> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyMetaInfo(name);
						}
					}).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNFPropertyValue(final IServiceIdentifier sid, final String name)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getNFPropertyValue(name);
		}
		else
		{
			final Future<T> ret = new Future<T>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<T>()
					{
						@Classname("getNFPropertyValue13")
						public IFuture<T> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyValue(name);
						}
					}).addResultListener(new DelegationResultListener<T>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T, U> IFuture<T> getNFPropertyValue(final IServiceIdentifier sid, final String name, final U unit)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getNFPropertyValue(name, unit);
		}
		else
		{
			final Future<T> ret = new Future<T>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<T>()
					{
						@Classname("getNFPropertyValue14")
						public IFuture<T> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyValue(name, unit);
						}
					}).addResultListener(new DelegationResultListener<T>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addNFProperty(final IServiceIdentifier sid, final INFProperty<?, ?> nfprop)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).addNFProperty(nfprop);
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Void>()
					{
						@Classname("addNFProperty15")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).addNFProperty(nfprop);
						}
					}).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public IFuture<Void> removeNFProperty(final IServiceIdentifier sid, final String name)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).removeNFProperty(name);
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Void>()
					{
						@Classname("removeNFProperty16")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).removeNFProperty(name);
						}
					}).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Shutdown the provider.
	 */
	public IFuture<Void> shutdownNFPropertyProvider(final IServiceIdentifier sid)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).shutdownNFPropertyProvider();
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Void>()
					{
						@Classname("shutdownNFPropertyProvider17")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).shutdownNFPropertyProvider();
						}
					}).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			return ret;
		}
	}
	
	//-------- provided service methods --------
	
	/**
	 *  Returns meta information about a non-functional properties of all methods.
	 *  @return The meta information about a non-functional properties.
	 */
	public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> getMethodNFPropertyMetaInfos(final IServiceIdentifier sid)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfos();
		}
		else
		{
			final Future<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> ret = new Future<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>>();

			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Map<MethodInfo, Map<String, INFPropertyMetaInfo>>>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>>()
					{
						@Classname("getMethodNFPropertyMetaInfos18")
						public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfos();
						}
					}).addResultListener(new DelegationResultListener<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the names of all non-functional properties of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @return The names of the non-functional properties of the specified method.
	 */
	public IFuture<String[]> getMethodNFPropertyNames(final IServiceIdentifier sid, final MethodInfo method)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyNames(method);
		}
		else
		{
			final Future<String[]> ret = new Future<String[]>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String[]>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<String[]>()
					{
						@Classname("getMethodNFPropertyNames19")
						public IFuture<String[]> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyNames(method);
						}
					}).addResultListener(new DelegationResultListener<String[]>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the names of all non-functional properties of this method.
	 *  This includes the properties of all parent components.
	 *  @return The names of the non-functional properties of this method.
	 */
	public IFuture<String[]> getMethodNFAllPropertyNames(final IServiceIdentifier sid, final MethodInfo method)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyNames(method);
		}
		else
		{
			final Future<String[]> ret = new Future<String[]>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String[]>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<String[]>()
					{
						@Classname("getMethodNFAllPropertyNames20")
						public IFuture<String[]> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFAllPropertyNames(method);
						}
					}).addResultListener(new DelegationResultListener<String[]>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns meta information about a non-functional properties of a method.
	 *  @return The meta information about a non-functional properties.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getMethodNFPropertyMetaInfos(final IServiceIdentifier sid, final MethodInfo method)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfos(method);
		}
		else
		{
			final Future<Map<String, INFPropertyMetaInfo>> ret = new Future<Map<String, INFPropertyMetaInfo>>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Map<String, INFPropertyMetaInfo>>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Map<String, INFPropertyMetaInfo>>()
					{
						@Classname("getMethodNFPropertyMetaInfos21")
						public IFuture<Map<String, INFPropertyMetaInfo>> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfos(method);
						}
					}).addResultListener(new DelegationResultListener<Map<String, INFPropertyMetaInfo>>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the meta information about a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of the specified method.
	 */
	public IFuture<INFPropertyMetaInfo> getMethodNFPropertyMetaInfo(final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfo(method, name);
		}
		else
		{
			final Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, INFPropertyMetaInfo>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<INFPropertyMetaInfo>()
					{
						@Classname("getMethodNFPropertyMetaInfo22")
						public IFuture<INFPropertyMetaInfo> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfo(method, name);
						}
					}).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
	public <T> IFuture<T> getMethodNFPropertyValue(final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyValue(method, name);
		}
		else
		{
			final Future<T> ret = new Future<T>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<T>()
					{
						@Classname("getMethodNFPropertyValue23")
						public IFuture<T> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyValue(method, name);
						}
					}).addResultListener(new DelegationResultListener<T>(ret));
				}
			});
			return ret;
		}
		
//		ret.addResultListener(new IResultListener<T>()
//		{
//			public void resultAvailable(T result)
//			{
//				System.out.println("t: "+result);
//			}
//
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method, performs unit conversion.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(Method method, String name, Class<U> unit);
	public <T, U> IFuture<T> getMethodNFPropertyValue(final IServiceIdentifier sid, final MethodInfo method, final String name, final U unit)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).getMethodNFPropertyValue(method, name, unit);
		}
		else
		{
			final Future<T> ret = new Future<T>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<T>()
					{
						@Classname("getMethodNFPropertyValue24")
						public IFuture<T> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyValue(method, name, unit);
						}
					}).addResultListener(new DelegationResultListener<T>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addMethodNFProperty(final IServiceIdentifier sid, final MethodInfo method, final INFProperty<?, ?> nfprop)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).addMethodNFProperty(method, nfprop);
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Void>()
					{
						@Classname("addMethodNFProperty25")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).addMethodNFProperty(method, nfprop);
						}
					}).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param The name.
	 */
	public IFuture<Void> removeMethodNFProperty(final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		if(sid.getProviderId().equals(getInternalAccess().getId()))
		{
			return getProvidedServicePropertyProvider(sid).removeMethodNFProperty(method, name);
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					result.scheduleStep(new ImmediateComponentStep<Void>()
					{
						@Classname("removeMethodNFProperty26")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							INFPropertyComponentFeature nfp = ia.getFeature(INFPropertyComponentFeature.class);
							return nfp.getProvidedServicePropertyProvider(sid).removeMethodNFProperty(method, name);
						}
					}).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			return ret;
		}
	}

	//-------- required properties --------
	
	/**
	 *  Returns the declared names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getRequiredNFPropertyNames(final IServiceIdentifier sid)
	{
		return getRequiredServicePropertyProvider(sid).getNFPropertyNames();
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getRequiredNFAllPropertyNames(final IServiceIdentifier sid)
	{
		return getRequiredServicePropertyProvider(sid).getNFAllPropertyNames();
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getRequiredNFPropertyMetaInfos(final IServiceIdentifier sid)
	{
		return getRequiredServicePropertyProvider(sid).getNFPropertyMetaInfos();
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getRequiredNFPropertyMetaInfo(final IServiceIdentifier sid, final String name)
	{
		return getRequiredServicePropertyProvider(sid).getNFPropertyMetaInfo(name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getRequiredNFPropertyValue(final IServiceIdentifier sid, final String name)
	{
		return getRequiredServicePropertyProvider(sid).getNFPropertyValue(name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(String name, Class<U> unit);
	public <T, U> IFuture<T> getRequiredNFPropertyValue(final IServiceIdentifier sid, final String name, final U unit)
	{
		return getRequiredServicePropertyProvider(sid).getNFPropertyValue(name, unit);
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addRequiredNFProperty(final IServiceIdentifier sid, final INFProperty<?, ?> nfprop)
	{
		return getRequiredServicePropertyProvider(sid).addNFProperty(nfprop);
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public IFuture<Void> removeRequiredNFProperty(final IServiceIdentifier sid, final String name)
	{
		return getRequiredServicePropertyProvider(sid).removeNFProperty(name);
	}
	
	/**
	 *  Shutdown the provider.
	 */
	public IFuture<Void> shutdownRequiredNFPropertyProvider(final IServiceIdentifier sid)
	{
		return getRequiredServicePropertyProvider(sid).shutdownNFPropertyProvider();
	}
	
	/**
	 *  Returns meta information about a non-functional properties of all methods.
	 *  @return The meta information about a non-functional properties.
	 */
	public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> getRequiredMethodNFPropertyMetaInfos(final IServiceIdentifier sid)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFPropertyMetaInfos();
	}
	
	/**
	 *  Returns the names of all non-functional properties of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @return The names of the non-functional properties of the specified method.
	 */
	public IFuture<String[]> getRequiredMethodNFPropertyNames(final IServiceIdentifier sid, final MethodInfo method)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFPropertyNames(method);
	}
	
	/**
	 *  Returns the names of all non-functional properties of this method.
	 *  This includes the properties of all parent components.
	 *  @return The names of the non-functional properties of this method.
	 */
	public IFuture<String[]> getRequiredMethodNFAllPropertyNames(final IServiceIdentifier sid, final MethodInfo method)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFAllPropertyNames(method);
	}
	
	/**
	 *  Returns meta information about a non-functional properties of a method.
	 *  @return The meta information about a non-functional properties.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getRequiredMethodNFPropertyMetaInfos(final IServiceIdentifier sid, final MethodInfo method)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFPropertyMetaInfos(method);
	}
	
	/**
	 *  Returns the meta information about a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of the specified method.
	 */
	public IFuture<INFPropertyMetaInfo> getRequiredMethodNFPropertyMetaInfo(final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFPropertyMetaInfo(method, name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
	public <T> IFuture<T> getRequiredMethodNFPropertyValue(final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFPropertyValue(method, name);
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method, performs unit conversion.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(Method method, String name, Class<U> unit);
	public <T, U> IFuture<T> getRequiredMethodNFPropertyValue(final IServiceIdentifier sid, final MethodInfo method, final String name, final U unit)
	{
		return getRequiredServicePropertyProvider(sid).getMethodNFPropertyValue(method, name, unit);
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addRequiredMethodNFProperty(final IServiceIdentifier sid, final MethodInfo method, final INFProperty<?, ?> nfprop)
	{
		return getRequiredServicePropertyProvider(sid).addMethodNFProperty(method, nfprop);
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param The name.
	 */
	public IFuture<Void> removeRequiredMethodNFProperty(final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		return getRequiredServicePropertyProvider(sid).removeMethodNFProperty(method, name);
	}
	
	
	
	/**
	 *  Counter listener that allows to set the max after usage.
	 */
	public static class LateCounterListener<T> implements IResultListener<T>
	{
		IResultListener<T> delegate;
		int max = -1;
		int cnt = 0;
		
		public LateCounterListener(IResultListener<T> delegate)
		{
			this.delegate = delegate;
		}
		
		public void resultAvailable(T result)
		{
			cnt++;
			check();
		}
		
		public void exceptionOccurred(Exception exception)
		{
			cnt++;
			check();
		}
		
		protected void check()
		{
			if(max>-1 && max==cnt)
			{
				delegate.resultAvailable(null);
			}
		}
		
		public void setMax(int max)
		{
			this.max = max;
			check();
		}
	}
}
