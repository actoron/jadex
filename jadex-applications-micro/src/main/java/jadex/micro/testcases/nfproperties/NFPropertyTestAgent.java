package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@ProvidedServices(@ProvidedService(type=ICoreDependentService.class, implementation=@Implementation(NFPropertyTestService.class)))
@NFProperties(@NFProperty(name="componentcores", type=CoreNumberProperty2.class))
public class NFPropertyTestAgent
{
	@Agent
	protected MicroAgent agent;
	
	@AgentBody
	public IFuture<Void> body()
	{
		ICoreDependentService cds = SServiceProvider.getService(agent.getServiceProvider(), ICoreDependentService.class).get();
		IService iscds = (IService) cds;
		String[] names = iscds.getNFPropertyNames().get();
		
		System.out.println("Begin list of non-functional properties:");
		for (String name : names)
		{
			System.out.println(name);
		}
		System.out.println("Finished list of non-functional properties.");
		
		System.out.println("Service Value: " + iscds.getNFPropertyValue("cores").get());
		
		System.out.println("Component Value, requested from Service: " + iscds.getNFPropertyValue("componentcores").get());
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
