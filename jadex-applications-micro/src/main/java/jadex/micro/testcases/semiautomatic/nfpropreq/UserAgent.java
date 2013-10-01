package jadex.micro.testcases.semiautomatic.nfpropreq;

import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.nonfunctional.search.ComposedEvaluator;
import jadex.bridge.sensor.service.ExecutionTimeProperty;
import jadex.bridge.sensor.service.WaitqueueEvaluator;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.MethodInfo;
import jadex.commons.Tuple2;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Collection;

@Agent
@Service
@RequiredServices(@RequiredService(name="aser", type=IAService.class, multiple=true, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true),
	nfprops=@NFRProperty(ExecutionTimeProperty.class)))
public class UserAgent
{
	@Agent
	protected MicroAgent agent;
		
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
				ranker.addEvaluator(new WaitqueueEvaluator(new MethodInfo(IAService.class.getMethod("test", new Class[0]))));
				ITerminableIntermediateFuture<IAService> sfut = agent.getRequiredServices("aser");
				Collection<Tuple2<IAService, Double>> res = SServiceProvider.rankServicesWithScores(sfut, ranker, null).get();
				System.out.println("Found: "+res);
				IAService aser = res.iterator().next().getFirstEntity();
				aser.test().get();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
