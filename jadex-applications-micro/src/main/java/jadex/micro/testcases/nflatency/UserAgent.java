package jadex.micro.testcases.nflatency;

import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFRPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.MethodInfo;
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
@RequiredServices(@RequiredService(name="aser", type=ITestService.class, multiple=true, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true),
	nfprops=@NFRProperty(value=LatencyProperty.class, methodname="methodA", methodparametertypes=long.class)))
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
			MethodInfo mi = new MethodInfo(ITestService.class.getMethod("methodA", new Class[]{long.class}));
			while(true)
			{
//				ComposedEvaluator<ITestService> ranker = new ComposedEvaluator<ITestService>();
//				ranker.addEvaluator(new ExecutionTimeEvaluator(new MethodInfo(ITestService.class.getMethod("methodA", new Class[]{long.class})), true));
//				ITerminableIntermediateFuture<ITestService> sfut = agent.getRequiredServices("aser");
//				Collection<Tuple2<ITestService, Double>> res = SServiceProvider.rankServicesWithScores(sfut, ranker, null).get();
//				System.out.println("Found: "+res);
//				ITestService aser = res.iterator().next().getFirstEntity();
				
				ITerminableIntermediateFuture<ITestService> sfut = agent.getRequiredServices("aser");
				Collection<ITestService> tss = sfut.get();
				if(tss.size()>0)
				{
					ITestService ts = tss.iterator().next();
					ts.methodA(100).get();
					INFMixedPropertyProvider pp = ((INFRPropertyProvider)ts).getRequiredServicePropertyProvider().get();
					Long lat = (Long)pp.getMethodNFPropertyValue(mi, LatencyProperty.NAME).get();
					System.out.println("latency: "+lat);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("User agent problem: "+e);
		}
	}
	
}