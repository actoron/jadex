package jadex.bdi.testcases.semiautomatic;

import java.net.URL;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.publish.IPublishService;

/**
 *  Plan that creates and searches services. 
 */
public class ServicesPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
//		IInternalService service = getInterpreter().getComponentFeature(IProvidedServicesFeature.class).createService(new PrintHelloService(), IPrintHelloService.class, null);
		PublishInfo pi = new PublishInfo("http://localhost:8080/hello/", IPublishService.PUBLISH_RS, IPrintHelloService.class);
//		ProvidedServiceInfo	psi	= new ProvidedServiceInfo();
//		psi.setPublish(new PublishInfo("http://localhost:8080/hello/", IPublishService.PUBLISH_RS, IPrintHelloService.class));
		getAgent().getComponentFeature(IProvidedServicesFeature.class).addService("ser", IPrintHelloService.class, new PrintHelloService(), pi, null);
		
		waitFor(500);
		
		// Call service internally
		IPrintHelloService phs = getAgent().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(
			IPrintHelloService.class, RequiredServiceInfo.SCOPE_LOCAL));
		phs.printHello();
		
		// Call service via REST
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					new URL("http://localhost:8080/hello/printHello").getContent();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}

