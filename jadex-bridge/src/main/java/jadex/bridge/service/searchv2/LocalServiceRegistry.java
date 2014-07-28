package jadex.bridge.service.searchv2;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class LocalServiceRegistry
{
	/** The map of published services sorted by type. */
	protected Map<ClassInfo, Set<IService>> services;
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public void addService(IService service)
	{
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
	public void removeService(IService service)
	{
		if(services!=null)
		{
			Set<IService> sers = services.get(service.getServiceIdentifier().getServiceType());
			if(sers!=null)
			{
				sers.remove(service);
			}
		}
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Set<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		Set<T> ret = null;
		if(services!=null)
		{
			Set<IService> sers = services.get(new ClassInfo(type));
			if(sers!=null && sers.size()>0)
			{
				ret = new HashSet<T>();
				for(IService ser: sers)
				{
					if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope) || RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
					{
						ret.add((T)ser);
					}
					else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
					{
						IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
						if(target.getPlatformName().equals(cid.getPlatformName())) 
						{
							String tln = target.getLocalName();
							String cln = cid.getLocalName();
							
//							int idx = cln.indexOf(".");
//							if(idx!=-1)
//							{
//								String paname = name.substring(at+1, idx);
//								String pfname = name.substring(idx+1);
//							}
						}
					}
				}
			}
		}
		return ret;
	}
}
