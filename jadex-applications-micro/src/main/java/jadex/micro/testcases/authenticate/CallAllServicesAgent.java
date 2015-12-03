package jadex.micro.testcases.authenticate;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
public class CallAllServicesAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Call the service methods.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IIntermediateFuture<ITestService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("ts");
		fut.addResultListener(new IIntermediateResultListener<ITestService>()
		{
			public void intermediateResultAvailable(final ITestService ts)
			{
				System.out.println("found: "+ts);
				ts.method("test1").addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						System.out.println("called: "+((IService)ts).getServiceIdentifier());
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("exception calling: "+((IService)ts).getServiceIdentifier()+" "+exception);
					}
				});
			}
			
			public void finished()
			{
				System.out.println("Finished");
			}
			
			public void resultAvailable(Collection<ITestService> result)
			{
				for(ITestService ts: result)
				{
					intermediateResultAvailable(ts);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				exception.printStackTrace();
			}
		});
		
		return ret;
	}
}
