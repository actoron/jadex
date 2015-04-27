package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.publish.IPublishService;

import java.net.URL;

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
		IInternalService	service	= getInterpreter().createInternalService(new PrintHelloService(), IPrintHelloService.class, null);
		ProvidedServiceInfo	psi	= new ProvidedServiceInfo();
		psi.setPublish(new PublishInfo("http://localhost:8080/hello/", IPublishService.PUBLISH_RS, IPrintHelloService.class));
		getComponentFeature(IRequiredServicesFeature.class).addService(service, psi);
		
		waitFor(500);
		
		// Call service internally
		IPrintHelloService phs = (IPrintHelloService)SServiceProvider.getService(getInterpreter(), 
			IPrintHelloService.class, RequiredServiceInfo.SCOPE_LOCAL).get();
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

