package jadex.micro.testcases.subscriptionlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.testcases.TestAgent;

/**
 *  Test getting values of a subscription future.
 */
@Agent
public class SubscriptionListenerTestAgent extends TestAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/**
	 *  Perform  the test.
	 *  @param cms	The cms of the platform to test (local or remote).
	 * 	@param local	True when tests runs on local platform. 
	 *  @return	The test result.
	 */
	protected IFuture<TestReport>	test(IExternalAccess platform, final boolean local)
	{
		final Future<TestReport>	ret	= new Future<TestReport>();
		IComponentIdentifier root = platform.getId().getRoot();
		createComponent(ProviderAgent.class.getName()+".class", root, null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier provider)
			{
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ITestService.class).setProvider(provider))
					.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
				{
					public void customResultAvailable(ITestService ts)
					{
						ISubscriptionIntermediateFuture<String>	fut	= ts.test();
						Collection<String>	vals1	= fut.get();
						Collection<String>	vals2	= fut.getIntermediateResults();
						
						final TestReport	tr	= new TestReport(local ? "#1" : "#2", "Test getting values of a "+(local ? "local" : "remote")+" subscription future.");
						if(vals1.toString().equals("[a, b, c]") && vals1.equals(vals2))
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setFailed("Wrong vals: "+vals1+", "+vals2);
						}
						
						platform.getExternalAccess(provider).killComponent()
							.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
						{
							public void customResultAvailable(Map<String, Object> map)
							{
								ret.setResult(tr);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Main for reproducing termination heisenbug.
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimal();
		CreationInfo ci = new CreationInfo().setFilenameClass(SubscriptionListenerTestAgent.class);
		IExternalAccess	platform	= Starter.createPlatform(config).get();
		
		while(true)
		{
			List<IFuture<IExternalAccess>>	agents	= new ArrayList<>();
			
			// Start many agents
			for(int i=0; i<1; i++)
			{
				agents.add(platform.createComponent(ci));				
			}
			
			// Wait for all agents started
			agents.stream().forEach(agent -> agent.get());
			
			// Wait for all agents finished
			agents.stream().forEach(agent -> agent.get().waitForTermination().get(30000));
		}
	}
}
