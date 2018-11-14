package jadex.bridge.nonfunctional;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Base impl for nf property property provider.
 */
public class NFPropertyProvider implements INFPropertyProvider
{
	/** The parent. */
//	protected INFPropertyProvider nfparent;
	protected IComponentIdentifier parent;
	
	/** The component. */
	protected IInternalAccess component;
	
	/** Non-functional properties. */
	protected Map<String, INFProperty<?, ?>> nfproperties;
	
	/**
	 *  Create a new provider.
	 */
	public NFPropertyProvider(IComponentIdentifier parent, IInternalAccess component)
	{
		this.parent = parent;
		this.component = component;
	}
	
//	/**
//	 *  Create a new provider.
//	 */
//	public NFPropertyProvider(INFPropertyProvider parent)
//	{
//		this.nfparent = parent;
//	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFPropertyNames()
	{
		return new Future<String[]>(nfproperties != null? nfproperties.keySet().toArray(new String[nfproperties.size()]) : new String[0]);
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFAllPropertyNames()
	{
		final Future<String[]> ret = new Future<String[]>();
		final String[] myprops = nfproperties != null? nfproperties.keySet().toArray(new String[nfproperties.size()]) : new String[0];
		
		if(getParentId()!=null)
		{
//			IComponentManagementService cms = getInternalAccess().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			getInternalAccess().getExternalAccess(getParentId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String[]>(ret)
			{
				public void customResultAvailable(IExternalAccess component) 
				{
					component.getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret)
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
		}
		ret.setResult(res);
		
		return ret;
	}

	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(final String name)
	{
		final Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
		
		INFPropertyMetaInfo mi = nfproperties != null? nfproperties.get(name) != null? nfproperties.get(name).getMetaInfo() : null : null;
		
		if(mi != null)
		{
			ret.setResult(mi);
		}
		else 
		{
			if(getParentId()!=null)
			{
//				IComponentManagementService cms = getInternalAccess().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
				getInternalAccess().getExternalAccess(getParentId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, INFPropertyMetaInfo>(ret)
				{
					public void customResultAvailable(IExternalAccess component) 
					{
						component.getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
					}
				});
			}
			else
			{
				ret.setException(new RuntimeException("Property not found: "+name));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNFPropertyValue(final String name)
	{
		final Future<T> ret = new Future<T>();
		
		INFProperty<T, ?> prop = (INFProperty<T, ?>) (nfproperties != null? nfproperties.get(name) : null);
		
		if(prop != null)
		{
			try
			{
				prop.getValue().addResultListener(new DelegationResultListener<T>(ret));
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else 
		{
			if(getParentId()!=null)
			{
//				IComponentManagementService cms = getInternalAccess().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
				getInternalAccess().getExternalAccess(getParentId()).addResultListener(new ComponentResultListener<IExternalAccess>(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
				{
					public void customResultAvailable(IExternalAccess pacomponent) 
					{
						IFuture<T> res = pacomponent.getNFPropertyValue(name);
						res.addResultListener(new ComponentResultListener<T>(new DelegationResultListener<T>(ret), component));
					}
				}, component));
			}
			else
			{
				ret.setException(new RuntimeException("Property not found: "+name));
			}
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
	public<T, U> IFuture<T> getNFPropertyValue(final String name, final U unit)
	{
		final Future<T> ret = new Future<T>();
		
		INFProperty<T, U> prop = (INFProperty<T, U>) (nfproperties != null? nfproperties.get(name) : null);
		
		if(prop!=null)
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
			if(getParentId()!=null)
			{
//				IComponentManagementService cms = getInternalAccess().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
				getInternalAccess().getExternalAccess(getParentId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
				{
					public void customResultAvailable(IExternalAccess component) 
					{
						IFuture<T> res = component.getNFPropertyValue(name, unit);
						res.addResultListener(new DelegationResultListener<T>(ret));
					}
				});
			}
			else
			{
				ret.setException(new RuntimeException("Property not found: "+name));
			}
		}	
		
		return ret;
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param metainfo The metainfo.
	 */
	public IFuture<Void> addNFProperty(INFProperty<?, ?> nfprop)
	{
		final Future<Void> ret = new Future<Void>();
		if(nfproperties==null)
			nfproperties = new HashMap<String, INFProperty<?,?>>();
		nfproperties.put(nfprop.getName(), nfprop);
		
		if(getInternalAccess().getFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
		{
			MonitoringEvent me = new MonitoringEvent(getInternalAccess().getId(), getInternalAccess().getDescription().getCreationTime(), 
				MonitoringEvent.TYPE_PROPERTY_ADDED, System.currentTimeMillis(), PublishEventLevel.COARSE);
			me.setProperty("propname", nfprop.getName());
			getInternalAccess().getFeature(IMonitoringComponentFeature.class).publishEvent(me, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public IFuture<Void> removeNFProperty(final String name)
	{
		final Future<Void> ret = new Future<Void>();
		if(nfproperties!=null)
		{
			INFProperty<?, ?> prop = nfproperties.remove(name);
			if(prop!=null)
			{
				prop.dispose().addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if(getInternalAccess().getFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
						{
							MonitoringEvent me = new MonitoringEvent(getInternalAccess().getId(), getInternalAccess().getDescription().getCreationTime(), 
								MonitoringEvent.TYPE_PROPERTY_REMOVED, System.currentTimeMillis(), PublishEventLevel.COARSE);
							me.setProperty("propname", name);
							getInternalAccess().getFeature(IMonitoringComponentFeature.class).publishEvent(me, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret));
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
	 *  Get the parent.
	 *  return The parent.
	 */
//	public abstract IFuture<INFPropertyProvider> getParent();
//	public abstract Object getParent();
	
//	public final IFuture<INFPropertyProvider> getParent()
//	{
//		return null;
//	}
	
	public IComponentIdentifier getParentId()
	{
		return parent;
	}
	
//	/**
//	 *  Get the parent.
//	 *  return The parent.
//	 */
//	public INFPropertyProvider getParent()
//	{
//		return nfparent;
//	}
//
//	/**
//	 *  Set the parent. 
//	 *  @param parent The parent to set.
//	 */
//	public void setParent(INFPropertyProvider parent)
//	{
//		this.nfparent = parent;
//	}
	
	/**
	 *  Shutdown the provider.
	 */
	public IFuture<Void> shutdownNFPropertyProvider()
	{
		Future<Void> ret = new Future<Void>();
		if(nfproperties!=null)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(nfproperties.size(), true, new DelegationResultListener<Void>(ret));
			for(INFProperty<?, ?> prop: nfproperties.values())
			{
				prop.dispose().addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return component;
	}
}
