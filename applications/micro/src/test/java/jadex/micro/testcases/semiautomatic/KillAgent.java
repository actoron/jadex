package jadex.micro.testcases.semiautomatic;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
// Todo: what is this agent supposed to test!?
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class KillAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IComponentManagementService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.createComponent(null, "jadex.micro.MicroAgent.class", new CreationInfo(agent.getIdentifier()), null)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(IComponentIdentifier result) 
					{
						System.out.println("Micro agent started: "+result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
					}
				}));
				
				ret.setResult(null);
				agent.killComponent();
			}
		});
		return ret;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
//		ThreadSuspendable sus = new ThreadSuspendable();
		IExternalAccess pl = Starter.createPlatform(new String[]{"-gui", "false", "-autoshutdown", "false"}).get();
		IComponentManagementService cms = pl.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		
		for(int i=0; i<1000; i++)
		{
			IComponentIdentifier cid = cms.createComponent(KillAgent.class.getName()+".class", null).getFirstResult();
			try
			{
				cms.destroyComponent(cid).get();
			}
			catch(Exception e)
			{
				System.out.println("Ex: "+e.getMessage());
			}
		}
		
		try
		{
			Thread.currentThread().sleep(30000);
		}
		catch(Exception e)
		{
		}
		
		System.out.println("fini");
	}
}
