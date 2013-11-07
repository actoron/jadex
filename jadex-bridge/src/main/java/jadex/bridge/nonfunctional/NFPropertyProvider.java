package jadex.bridge.nonfunctional;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Base impl for nf property property provider.
 */
public abstract class NFPropertyProvider implements INFPropertyProvider
{
	/** The parent. */
	protected INFPropertyProvider parent;
	
	/** Non-functional properties. */
	protected Map<String, INFProperty<?, ?>> nfproperties;
	
	/**
	 *  Create a new provider.
	 */
	public NFPropertyProvider()
	{
	}
	
	/**
	 *  Create a new provider.
	 */
	public NFPropertyProvider(INFPropertyProvider parent)
	{
		this.parent = parent;
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
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFAllPropertyNames()
	{
		final Future<String[]> ret = new Future<String[]>();
		final String[] myprops = nfproperties != null? nfproperties.keySet().toArray(new String[nfproperties.size()]) : new String[0];
		if(getParent()!=null)
		{
			getParent().getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret)
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
		else
		{
			ret.setResult(myprops);
		}
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
	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(String name)
	{
		Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
		
		INFPropertyMetaInfo mi = nfproperties != null? nfproperties.get(name) != null? nfproperties.get(name).getMetaInfo() : null : null;
		
		if(mi != null)
		{
			ret.setResult(mi);
		}
		else if(getParent()!=null)
		{
			getParent().getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Property not found: "+name));
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
		else if(getParent()!=null)
		{
			IFuture<T> fut = getParent().getNFPropertyValue(name);
			fut.addResultListener(new DelegationResultListener<T>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Property not found: "+name));
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
		else if(getParent()!=null)
		{
//			internalaccess.getExternalAccess().getNFPropertyValue(name, unit).addResultListener(new DelegationResultListener<T>(ret));
			IFuture<T> fut = getParent().getNFPropertyValue(name, unit);
			fut.addResultListener(new DelegationResultListener<T>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Property not found: "+name));
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
		
		if(getInternalAccess().hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
		{
			MonitoringEvent me = new MonitoringEvent(getInternalAccess().getComponentIdentifier(), getInternalAccess().getComponentDescription().getCreationTime(), 
				MonitoringEvent.TYPE_PROPERTY_REMOVED, System.currentTimeMillis(), PublishEventLevel.COARSE);
			me.setProperty("propname", nfprop.getName());
			getInternalAccess().publishEvent(me, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret));
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
						if(getInternalAccess().hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
						{
							MonitoringEvent me = new MonitoringEvent(getInternalAccess().getComponentIdentifier(), getInternalAccess().getComponentDescription().getCreationTime(), 
								MonitoringEvent.TYPE_PROPERTY_REMOVED, System.currentTimeMillis(), PublishEventLevel.COARSE);
							me.setProperty("propname", name);
							getInternalAccess().publishEvent(me, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret));
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
	public INFPropertyProvider getParent()
	{
		return parent;
	}

	/**
	 *  Set the parent. 
	 *  @param parent The parent to set.
	 */
	public void setParent(INFPropertyProvider parent)
	{
		this.parent = parent;
	}
	
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
	public abstract IInternalAccess getInternalAccess();
}
