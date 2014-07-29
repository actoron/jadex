package jadex.bridge.service.searchv2;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Local service registry.
 */
public class LocalServiceRegistry
{
	/** The map of published services sorted by type. */
	protected Map<ClassInfo, Set<IService>> services;
	
	public LocalServiceRegistry(String tst)
	{
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public synchronized void addService(IService service)
	{
//		System.out.println("added: "+service.getServiceIdentifier().getServiceType());
		
		if(services==null)
		{
			services = new HashMap<ClassInfo, Set<IService>>();
		}
		
		Set<IService> sers = services.get(service.getServiceIdentifier().getServiceType());
		if(sers==null)
		{
			sers = new HashSet<IService>();
			services.put(service.getServiceIdentifier().getServiceType(), sers);
		}
		
		sers.add(service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public synchronized void removeService(IService service)
	{
		if(services!=null)
		{
			Set<IService> sers = services.get(service.getServiceIdentifier().getServiceType());
			if(sers!=null)
			{
				sers.remove(service);
			}
			else
			{
				System.out.println("Could not remove service from registry: "+service.getServiceIdentifier());
			}
		}
		else
		{
			System.out.println("Could not remove service from registry: "+service.getServiceIdentifier());
		}
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		T ret = null;
		if(services!=null)
		{
			Set<IService> sers = services.get(new ClassInfo(type));
			if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
			{
				for(IService ser: sers)
				{
					if(checkService(cid, ser, scope))
					{
						ret = (T)ser;
						break;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		Set<T> ret = null;
		if(services!=null)
		{
			Set<IService> sers = services.get(new ClassInfo(type));
			if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
			{
				ret = new HashSet<T>();
				for(IService ser: sers)
				{
					if(checkService(cid, ser, scope))
					{
						ret.add((T)ser);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to scope.
	 */
	protected boolean checkService(IComponentIdentifier cid, IService ser, String scope)
	{
		boolean ret = false;
		if(scope==null)
		{
			scope = RequiredServiceInfo.SCOPE_APPLICATION;
		}
		
		if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope) || RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			ret = target.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(target).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			ret = target.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(target).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getServiceIdentifier().getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			
			String subname = getSubcomponentName(target);
			ret = target.getName().endsWith(subname);
			
//			while(target!=null)
//			{
//				if(target.equals(parent))
//				{
//					ret.add((T)ser);
//					break;
//				}
//				else
//				{
//					target = target.getParent();
//				}
//			}
		}
		else if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			
			while(cid!=null)
			{
				if(target.equals(cid))
				{
					ret = true;
					break;
				}
				else
				{
					cid = cid.getParent();
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the application name. Equals the local component name in case it is a child of the platform.
	 *  broadcast@awa.plat1 -> awa
	 *  @return The application name.
	 */
	public static String getApplicationName(IComponentIdentifier cid)
	{
		String ret = cid.getName();
		int idx;
		// If it is a direct subcomponent
		if((idx = ret.lastIndexOf('.')) != -1)
		{
			// cut off platform name
			ret = ret.substring(0, idx);
			// cut off local name 
			if((idx = ret.indexOf('@'))!=-1)
				ret = ret.substring(idx + 1);
			if((idx = ret.indexOf('.'))!=-1)
				ret = ret.substring(idx + 1);
		}
		else
		{
			ret = cid.getLocalName();
		}
		return ret;
	}
	
	/**
	 *  Get the subcomponent name.
	 *  @param cid The component id.
	 *  @return The subcomponent name.
	 */
	public static String getSubcomponentName(IComponentIdentifier cid)
	{
		String ret = cid.getName();
		int idx;
		if((idx = ret.indexOf('@'))!=-1)
			ret = ret.substring(idx + 1);
		return ret;
	}
}
