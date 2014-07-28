package jadex.micro.testcases.search;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Collection;

@Agent
@RequiredServices({@RequiredService(name = "testService", type = ITestService.class, multiple = true, 
	binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)) })
public class UserAgent 
{
    @Agent
    protected MicroAgent agent;
    
    @AgentCreated
    public void init() 
    {
        System.out.println("Agent created");
        IFuture<Collection<ITestService>> fut = agent.getServiceContainer().getRequiredServices("testService");
        Collection<ITestService> sers = fut.get();
        System.out.println("fetched all available services: "+sers.size());
    }
    
    public static void main(String[] args)
	{
    	ThreadSuspendable sus = new ThreadSuspendable();
		IExternalAccess plat = Starter.createPlatform(new String[]{"-gui", "false"}).get(sus);
		IComponentManagementService cms = SServiceProvider.getService(plat.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
		
		for(int i=0; i<2500; i++)
		{
			cms.createComponent(ProviderAgent.class.getName()+".class", null);
//			IComponentIdentifier cid = cms.createComponent(ProviderAgent.class.getName()+".class", null).getFirstResult(sus);
			System.out.println("created: "+i);//+" "+cid);
		}
		
		cms.createComponent(UserAgent.class.getName()+".class", null).get(sus);
	}
}