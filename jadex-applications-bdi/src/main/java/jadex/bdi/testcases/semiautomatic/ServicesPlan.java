package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
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
		Object	service	= new PrintHelloService();

		ProvidedServiceInfo	psi	= new ProvidedServiceInfo();
		psi.setPublish(new PublishInfo("http://localhost:8080/hello/", IPublishService.PUBLISH_RS, IPrintHelloService.class, null));
		
		boolean moni = getComponentDescription().getMonitoring()!=null? getComponentDescription().getMonitoring().booleanValue(): false;

		IInternalService	is	= BasicServiceInvocationHandler.createProvidedServiceProxy(
			(IInternalAccess)getScope(), getInterpreter().getAgentAdapter(), service, null,
			IPrintHelloService.class, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED,
			null, getInterpreter().isCopy(), getInterpreter().isRealtime(),
			getInterpreter().getModel().getResourceIdentifier(), moni, null);
		
		getServiceContainer().addService(is, psi, null);
		
		IPrintHelloService phs = (IPrintHelloService)SServiceProvider.getService(getServiceContainer(), 
			IPrintHelloService.class, RequiredServiceInfo.SCOPE_LOCAL).get(this);
		phs.printHello();
	}
}

