package jadex.micro.testcases.semiautomatic.nfpropreq;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.nonfunctional.search.ComposedEvaluator;
import jadex.bridge.sensor.service.ExecutionTimeEvaluator;
import jadex.bridge.sensor.service.ExecutionTimeProperty;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.MethodInfo;
import jadex.commons.Tuple2;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@Service
@RequiredServices(@RequiredService(name="aser", type=IAService.class, multiple=true, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true),
	nfprops=@NFRProperty(value=ExecutionTimeProperty.class, methodname="test")))
public class UserAgent
{
	@Agent
	protected IInternalAccess agent;
		
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		// todo: make ITerminable in DefaultServiceFetcher
		
		try
		{
			while(true)
			{
				ComposedEvaluator<IAService> ranker = new ComposedEvaluator<IAService>();
				ranker.addEvaluator(new ExecutionTimeEvaluator(agent.getExternalAccess(), new MethodInfo(IAService.class.getMethod("test", new Class[0])), true));
				ITerminableIntermediateFuture<IAService> sfut = agent.getComponentFeature(IRequiredServicesFeature.class).getServices("aser");
				Collection<Tuple2<IAService, Double>> res = SServiceProvider.rankServicesWithScores(sfut, ranker, null).get();
				System.out.println("Found: "+res);
				IAService aser = res.iterator().next().getFirstEntity();
				aser.test().get();
			}
		}
		catch(Exception e)
		{
			System.out.println("User agent problem: "+e);
		}
	}
	
}
