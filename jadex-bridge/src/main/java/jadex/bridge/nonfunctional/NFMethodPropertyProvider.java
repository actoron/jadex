package jadex.bridge.nonfunctional;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.MethodInfo;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Default implementation for a method property provider.
 */
public class NFMethodPropertyProvider extends NFPropertyProvider implements INFMixedPropertyProvider
{
	/** Non-functional properties of methods. */
	protected Map<MethodInfo, Map<String, INFProperty<?, ?>>> methodnfproperties;
	
//	/**
//	 *  Create a new provider.
//	 */
//	public NFMethodPropertyProvider(INFPropertyProvider parent)
//	{
//		super(parent);
//	}
	
	/**
	 *  Create a new provider.
	 */
	public NFMethodPropertyProvider(IComponentIdentifier parent, IInternalAccess component)
	{
		super(parent, component);
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
	 *  Returns the names of all non-functional properties of this method.
	 *  @return The names of the non-functional properties of this method.
	 */
	// todo: does this method makes sense because on parent it invokes getNFAllPropertyNames?
	public IFuture<String[]> getMethodNFAllPropertyNames(final MethodInfo method)
	{
		final Future<String[]> ret = new Future<String[]>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		final String[] myprops = nfmap != null? nfmap.keySet().toArray(new String[nfproperties.size()]) : new String[0];
		
		if(getParentId()!=null)
		{
			IComponentManagementService cms = SServiceProvider.getLocalService(getInternalAccess(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			cms.getExternalAccess(getParentId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String[]>(ret)
			{
				public void customResultAvailable(IExternalAccess component) 
				{
					SNFPropertyProvider.getNFAllPropertyNames(component).addResultListener(new DelegationResultListener<String[]>(ret)
					{
						public void customResultAvailable(String[] result)
						{
							Set<String> tmp = new LinkedHashSet<String>();
							for(String p: result)
							{
								tmp.add(p);
							}
							for(String p: myprops)
							{
								tmp.add(p);
							}
							ret.setResult((String[])tmp.toArray(new String[tmp.size()]));
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(myprops);		
		}
		
//		getParent().addResultListener(new ExceptionDelegationResultListener<INFPropertyProvider, String[]>(ret)
//		{
//			public void customResultAvailable(INFPropertyProvider parent)
//			{
//				if(parent!=null)
//				{
//					parent.getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret)
//					{
//						public void customResultAvailable(String[] result)
//						{
//							Set<String> tmp = new LinkedHashSet<String>();
//							for(String p: result)
//							{
//								tmp.add(p);
//							}
//							for(String p: myprops)
//							{
//								tmp.add(p);
//							}
//							ret.setResult((String[])tmp.toArray(new String[tmp.size()]));
//						}
//					});
//				}
//				else
//				{
//					ret.setResult(myprops);
//				}
//			}
//		});
			
		return ret;
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
		if(prop != null)
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
			ret = (Future<T>)getNFPropertyValue(name);
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
			ret = (Future<T>)getNFPropertyValue(name, unit);
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
		final Future<Void> ret = new Future<Void>();
		
		if(methodnfproperties==null)
			methodnfproperties = new HashMap<MethodInfo, Map<String,INFProperty<?,?>>>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		if (nfmap == null)
		{
			nfmap = new HashMap<String, INFProperty<?,?>>();
			methodnfproperties.put(method, nfmap);
		}
		nfmap.put(nfprop.getName(), nfprop);
		
		if(getInternalAccess().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
		{
			MonitoringEvent me = new MonitoringEvent(getInternalAccess().getComponentIdentifier(), getInternalAccess().getComponentDescription().getCreationTime(), 
				MonitoringEvent.TYPE_PROPERTY_REMOVED, System.currentTimeMillis(), PublishEventLevel.COARSE);
			me.setProperty("propname", nfprop.getName());
			getInternalAccess().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(me, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param method The method targeted by this operation.
	 *  @param The name.
	 */
	public IFuture<Void> removeMethodNFProperty(MethodInfo method, final String name)
	{
		final Future<Void> ret = new Future<Void>();
		Map<String, INFProperty<?, ?>> nfmap = methodnfproperties != null? methodnfproperties.get(method) : null;
		if(nfmap != null)
		{
			INFProperty<?, ?> prop = nfmap.remove(name);
			if(prop!=null)
			{
				prop.dispose().addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if(getInternalAccess().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
						{
							MonitoringEvent me = new MonitoringEvent(getInternalAccess().getComponentIdentifier(), getInternalAccess().getComponentDescription().getCreationTime(), 
								MonitoringEvent.TYPE_PROPERTY_REMOVED, System.currentTimeMillis(), PublishEventLevel.COARSE);
							me.setProperty("propname", name);
							getInternalAccess().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(me, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret));
						}
						else
						{
							ret.setResult(null);
						}
					}
				});
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
	 *  Shutdown the provider.
	 */
	public IFuture<Void> shutdownNFPropertyProvider()
	{
		final Future<Void> ret = new Future<Void>();
		
		super.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(methodnfproperties!=null)
				{
					int cnt = 0;
					for(Map<String, INFProperty<?, ?>> maps: methodnfproperties.values())
					{
						for(INFProperty<?, ?> prop: maps.values())
						{
							cnt++;
						}
					}
					
					CounterResultListener<Void> lis = new CounterResultListener<Void>(cnt, true, new DelegationResultListener<Void>(ret));
					for(Map<String, INFProperty<?, ?>> maps: methodnfproperties.values())
					{
						for(INFProperty<?, ?> prop: maps.values())
						{
							prop.dispose().addResultListener(lis);
						}
					}
				}
				else
				{
					ret.setResult(null);
				}
			}
		});
		
		return ret;
	}
}
