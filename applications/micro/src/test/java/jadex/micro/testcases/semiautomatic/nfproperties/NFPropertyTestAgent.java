package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@ProvidedServices(@ProvidedService(type=ICoreDependentService.class, implementation=@Implementation(NFPropertyTestService.class)))
@NFProperties(@NFProperty(name="componentcores", value=CoreNumberProperty2.class))
public class NFPropertyTestAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentBody
	public IFuture<Void> body()
	{
		ICoreDependentService cds = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ICoreDependentService.class)).get();
		IService iscds = (IService)cds;
//		INFPropertyProvider prov = (INFPropertyProvider)iscds.getExternalComponentFeature(INFPropertyComponentFeature.class);
//		String[] names = SNFPropertyProvider.getNFPropertyNames(agent.getExternalAccess(), iscds.getId()).get();
		String[] names = agent.getNFPropertyNames(iscds.getServiceId()).get();
		
		System.out.println("Begin list of non-functional properties:");
		for(String name : names)
		{
			System.out.println(name);
		}
		System.out.println("Finished list of non-functional properties.");
		
		System.out.println("Service Value: " + agent.getNFPropertyValue(iscds.getServiceId(), "cores").get());
		
		System.out.println("Component Value, requested from Service: " + agent.getNFPropertyValue(iscds.getServiceId(), "componentcores").get());
//		try
//		{
//			System.out.println("Speed Value for method: " +iscds.getNFPropertyValue(ICoreDependentService.class.getMethod("testMethod", new Class<?>[0]), "methodspeed").get());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
		
		return IFuture.DONE;
	}
}
