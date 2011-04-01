package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.service.IExternalWfmsService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationException;

public class ConnectPlan extends Plan
{
	public void body()
	{
		/*IExternalWfmsService wfms = null;
		ILibraryService ls = (ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		final ClassLoader cl = ls.getClassLoader();
		Class x = null;
		try
		{
			x = Class.forName("jadex.wfms.service.IExternalWfmsService", true, ls.getClassLoader());
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(x.getCanonicalName());
		System.out.println(x.equals(IExternalWfmsService.class));
		Object o =  SServiceProvider.getService(getScope().getServiceProvider(), new IResultSelector()
		{
			
			public IFuture selectServices(Map services)
			{
				Future ret = new Future();
				for (Iterator it = services.keySet().iterator(); it.hasNext();)
				{
					Class clazz = (Class) it.next();
					if (clazz.getName().equals("jadex.wfms.service.IExternalWfmsService"))
					{
						Collection coll = (Collection) services.get(clazz);
						if (coll.size() > 0)
						{
							System.out.println("Comp");
							System.out.println(coll.iterator().next() instanceof IExternalWfmsService);
							System.out.println(clazz.equals(IExternalWfmsService.class));
							System.out.println(cl);
							System.out.println(clazz.getClassLoader());
							System.out.println(IExternalWfmsService.class.getClassLoader());
							
							ret.setResult(Arrays.asList(new Object[] {coll.iterator().next()}));
							break;
						}
						System.out.println(clazz.hashCode());
						System.out.println(IExternalWfmsService.class.hashCode());
						System.out.println(services.get(clazz));
					}
				}
//					System.out.println(((Class) it.next()).getName());
				if (!ret.isDone())
					ret.setResult(null);
				
				return ret;
			}
			
			public boolean isFinished(Collection results)
			{
				// TODO Auto-generated method stub
				return results.size() > 0;
			}
			
			public Object getCacheKey()
			{
				// TODO Auto-generated method stub
				return hashCode();
			}
		}).get(this);
		System.out.println(o instanceof IExternalWfmsService);*/
		IExternalWfmsService wfms = (IExternalWfmsService) SServiceProvider.getService(getScope().getServiceProvider(), IExternalWfmsService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(this);
		ClientInfo info = new ClientInfo((String) getParameter("user_name").getValue());
		
		if (Boolean.FALSE.equals(wfms.authenticate(getComponentIdentifier(), info).get(this)))
			fail(new AuthenticationException());
		
		Set caps = (Set) wfms.getCapabilities(getComponentIdentifier()).get(this);
		System.out.println("Caps " + caps);
		getParameter("capabilities").setValue(caps);
		System.out.println(wfms instanceof IExternalWfmsService);
		getBeliefbase().getBelief("wfms").setFact(wfms);
	}
}
