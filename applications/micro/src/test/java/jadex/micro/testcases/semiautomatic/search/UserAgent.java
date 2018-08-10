package jadex.micro.testcases.semiautomatic.search;

import java.util.Collection;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@RequiredServices({@RequiredService(name = "testService", type = ITestService.class, multiple = true, scope=RequiredServiceInfo.SCOPE_PLATFORM)})
public class UserAgent
{
    @Agent
    protected IInternalAccess agent;
    
    @AgentCreated
    public void init() 
    {
        System.out.println("Agent created");
        IFuture<Collection<ITestService>> fut = agent.getFeature(IRequiredServicesFeature.class).getServices("testService");
        Collection<ITestService> sers = fut.get();
        System.out.println("fetched all available services: "+sers.size());
    }
    
    public static void main(String[] args)
	{
//    	ThreadSuspendable sus = new ThreadSuspendable();
		IExternalAccess plat = Starter.createPlatform(new String[]{"-gui", "false"}).get();
		
		final Future<Void> fut = new Future<Void>();
		
		final int max = 2500;
		final int[] cnt = new int[1];
		for(int i=0; i<max; i++)
		{
			plat.createComponent(null, new CreationInfo().setFilename(ProviderAgent.class.getName()+".class")).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
			{
				public void firstResultAvailable(IComponentIdentifier result)
				{
					cnt[0]++;
					System.out.println("created: "+result+" "+cnt[0]);
					if(cnt[0]==max)
					{
						fut.setResult(null);
					}
				}
				
				public void secondResultAvailable(Map<String, Object> result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					fut.setExceptionIfUndone(exception);
				}
			});
		}
		
		fut.get();
		
		plat.createComponent(null, new CreationInfo().setFilename(UserAgent.class.getName()+".class")).get();
	}
}