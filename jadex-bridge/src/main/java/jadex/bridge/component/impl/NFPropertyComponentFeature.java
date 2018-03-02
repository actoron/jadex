package jadex.bridge.component.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.modelinfo.NFPropertyInfo;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.nonfunctional.NFMethodPropertyProvider;
import jadex.bridge.nonfunctional.NFPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.annotation.SNameValue;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.MethodInfo;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
					INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getComponent(), null, null, nfprop.getParameters());
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
//						Class<?> impltype = psf.getProvidedServiceRawImpl(ser.getServiceIdentifier())!=null? psf.getProvidedServiceRawImpl(ser.getServiceIdentifier()).getClass(): null;
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
			this.compprovider = new NFPropertyProvider(getComponent().getComponentIdentifier().getParent(), getComponent()); 
		
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
			ret = new NFMethodPropertyProvider(null, getComponent()); 
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
			ret = new NFMethodPropertyProvider(getComponent().getComponentIdentifier(), getComponent()); 
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
	public IFuture<Void> initNFProperties(final IInternalService ser, Class<?> impltype)
	{
		final Future<Void> ret = new Future<Void>();
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superclazz = ser.getServiceIdentifier().getServiceType().getType(getComponent().getClassLoader());
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
		INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(ser.getServiceIdentifier());
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(nfprops.value().length, new DelegationResultListener<Void>(ret));
		for(NFProperty nfprop : nfprops.value())
		{
			Class<?> clazz = nfprop.value();
			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, getComponent(), ser, null, SNameValue.createUnparsedExpressionsList(nfprop.parameters()));
			prov.addNFProperty(prop).addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Add nf properties from a type.
	 */
	public IFuture<Void> addNFMethodProperties(NFProperties nfprops, IService ser, MethodInfo mi)
	{
		Future<Void> ret = new Future<Void>();
		
		INFMixedPropertyProvider prov = getProvidedServicePropertyProvider(ser.getServiceIdentifier());
		CounterResultListener<Void> lis = new CounterResultListener<Void>(nfprops.value().length, new DelegationResultListener<Void>(ret));
		for(NFProperty nfprop : nfprops.value())
		{
			Class<?> clazz = ((NFProperty)nfprop).value();
			INFProperty<?, ?> prop = AbstractNFProperty.createProperty(clazz, getComponent(), ser, mi, SNameValue.createUnparsedExpressionsList(nfprop.parameters()));
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
			ret = (T)getProvidedServicePropertyProvider(((IService)context).getServiceIdentifier());
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
	};
}
