package jadex.bridge.nonfunctional;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Static helper class for accessing nf properties also remotely.
 */
public class SNFPropertyProvider
{
	/**
	 *  Returns the declared names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public static IFuture<String[]> getNFPropertyNames(IExternalAccess component)
	{
		return component.scheduleStep(new IComponentStep<String[]>()
		{
			public IFuture<String[]> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().getNFPropertyNames();
			}
		});
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public static IFuture<String[]> getNFAllPropertyNames(IExternalAccess component)
	{
		return component.scheduleStep(new IComponentStep<String[]>()
		{
			public IFuture<String[]> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().getNFAllPropertyNames();
			}
		});
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public static IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos(IExternalAccess component)
	{
		return component.scheduleStep(new IComponentStep<Map<String, INFPropertyMetaInfo>>()
		{
			public IFuture<Map<String, INFPropertyMetaInfo>> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().getNFPropertyMetaInfos();
			}
		});
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public static IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(IExternalAccess component, final String name)
	{
		return component.scheduleStep(new IComponentStep<INFPropertyMetaInfo>()
		{
			public IFuture<INFPropertyMetaInfo> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().getNFPropertyMetaInfo(name);
			}
		});
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public static <T> IFuture<T> getNFPropertyValue(IExternalAccess component, final String name)
	{
		return component.scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().getNFPropertyValue(name);
			}
		});
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(String name, Class<U> unit);
	public static <T, U> IFuture<T> getNFPropertyValue(IExternalAccess component, final String name, final U unit)
	{
		return component.scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().getNFPropertyValue(name, unit);
			}
		});
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param nfprop The property.
	 */
	public static IFuture<Void> addNFProperty(IExternalAccess component, final INFProperty<?, ?> nfprop)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().addNFProperty(nfprop);
			}
		});
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public static IFuture<Void> removeNFProperty(IExternalAccess component, final String name)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().removeNFProperty(name);
			}
		});
	}
	
	/**
	 *  Shutdown the provider.
	 */
	public static IFuture<Void> shutdownNFPropertyProvider(IExternalAccess component)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getComponentPropertyProvider().shutdownNFPropertyProvider();
			}
		});
	}
	
	//-------- service methods --------
	
	/**
	 *  Returns the declared names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public static IFuture<String[]> getNFPropertyNames(IExternalAccess component, final IServiceIdentifier sid)
	{
		return component.scheduleStep(new IComponentStep<String[]>()
		{
			public IFuture<String[]> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyNames();
			}
		});
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public static IFuture<String[]> getNFAllPropertyNames(IExternalAccess component, final IServiceIdentifier sid)
	{
		return component.scheduleStep(new IComponentStep<String[]>()
		{
			public IFuture<String[]> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getNFAllPropertyNames();
			}
		});
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public static IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos(IExternalAccess component, final IServiceIdentifier sid)
	{
		return component.scheduleStep(new IComponentStep<Map<String, INFPropertyMetaInfo>>()
		{
			public IFuture<Map<String, INFPropertyMetaInfo>> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyMetaInfos();
			}
		});
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public static IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(IExternalAccess component, final IServiceIdentifier sid, final String name)
	{
		return component.scheduleStep(new IComponentStep<INFPropertyMetaInfo>()
		{
			public IFuture<INFPropertyMetaInfo> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyMetaInfo(name);
			}
		});
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public static <T> IFuture<T> getNFPropertyValue(IExternalAccess component, final IServiceIdentifier sid, final String name)
	{
		return component.scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyValue(name);
			}
		});
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(String name, Class<U> unit);
	public static <T, U> IFuture<T> getNFPropertyValue(IExternalAccess component, final IServiceIdentifier sid, final String name, final U unit)
	{
		return component.scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getNFPropertyValue(name, unit);
			}
		});
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param nfprop The property.
	 */
	public static IFuture<Void> addNFProperty(IExternalAccess component, final IServiceIdentifier sid, final INFProperty<?, ?> nfprop)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).addNFProperty(nfprop);
			}
		});
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public static IFuture<Void> removeNFProperty(IExternalAccess component, final IServiceIdentifier sid, final String name)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).removeNFProperty(name);
			}
		});
	}
	
	/**
	 *  Shutdown the provider.
	 */
	public static IFuture<Void> shutdownNFPropertyProvider(IExternalAccess component, final IServiceIdentifier sid)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).shutdownNFPropertyProvider();
			}
		});
	}
	
	//-------- methods methods --------
	
	/**
	 *  Returns meta information about a non-functional properties of all methods.
	 *  @return The meta information about a non-functional properties.
	 */
	public static IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> getMethodNFPropertyMetaInfos(IExternalAccess component, final IServiceIdentifier sid)
	{
		return component.scheduleStep(new IComponentStep<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>>()
		{
			public IFuture<Map<MethodInfo, Map<String, INFPropertyMetaInfo>>> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfos();
			}
		});
	}
	
	/**
	 *  Returns the names of all non-functional properties of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @return The names of the non-functional properties of the specified method.
	 */
	public static IFuture<String[]> getMethodNFPropertyNames(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method)
	{
		return component.scheduleStep(new IComponentStep<String[]>()
		{
			public IFuture<String[]> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyNames(method);
			}
		});
	}
	
	/**
	 *  Returns the names of all non-functional properties of this method.
	 *  This includes the properties of all parent components.
	 *  @return The names of the non-functional properties of this method.
	 */
	public static IFuture<String[]> getMethodNFAllPropertyNames(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method)
	{
		return component.scheduleStep(new IComponentStep<String[]>()
		{
			public IFuture<String[]> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFAllPropertyNames(method);
			}
		});
	}
	
	/**
	 *  Returns meta information about a non-functional properties of a method.
	 *  @return The meta information about a non-functional properties.
	 */
	public static IFuture<Map<String, INFPropertyMetaInfo>> getMethodNFPropertyMetaInfos(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method)
	{
		return component.scheduleStep(new IComponentStep<Map<String, INFPropertyMetaInfo>>()
		{
			public IFuture<Map<String, INFPropertyMetaInfo>> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfos(method);
			}
		});
	}
	
	/**
	 *  Returns the meta information about a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of the specified method.
	 */
	public static IFuture<INFPropertyMetaInfo> getMethodNFPropertyMetaInfo(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		return component.scheduleStep(new IComponentStep<INFPropertyMetaInfo>()
		{
			public IFuture<INFPropertyMetaInfo> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyMetaInfo(method, name);
			}
		});
	}
	
	/**
	 *  Returns the current value of a non-functional property of the specified method.
	 *  @param method The method targeted by this operation.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of the specified method.
	 */
	public static <T> IFuture<T> getMethodNFPropertyValue(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		return component.scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyValue(method, name);
			}
		});
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
	public static <T, U> IFuture<T> getMethodNFPropertyValue(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method, final String name, final U unit)
	{
		return component.scheduleStep(new IComponentStep<T>()
		{
			public IFuture<T> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).getMethodNFPropertyValue(method, name, unit);
			}
		});
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param nfprop The property.
	 */
	public static IFuture<Void> addMethodNFProperty(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method, final INFProperty<?, ?> nfprop)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).addMethodNFProperty(method, nfprop);
			}
		});
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param The name.
	 */
	public static IFuture<Void> removeMethodNFProperty(IExternalAccess component, final IServiceIdentifier sid, final MethodInfo method, final String name)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				INFPropertyComponentFeature nfp = ia.getComponentFeature(INFPropertyComponentFeature.class);
				return nfp.getProvidedServicePropertyProvider(sid).removeMethodNFProperty(method, name);
			}
		});
	}
	
}
