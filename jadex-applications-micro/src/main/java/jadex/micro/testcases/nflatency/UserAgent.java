package jadex.micro.testcases.nflatency;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.MethodInfo;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@Service
@RequiredServices(@RequiredService(name="aser", type=ITestService.class, multiple=true, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true),
	nfprops=@NFRProperty(value=LatencyProperty.class, methodname="methodA", methodparametertypes=long.class)))
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
			MethodInfo mi = new MethodInfo(ITestService.class.getMethod("methodA", new Class[]{long.class}));
			while(true)
			{
//				ComposedEvaluator<ITestService> ranker = new ComposedEvaluator<ITestService>();
//				ranker.addEvaluator(new ExecutionTimeEvaluator(new MethodInfo(ITestService.class.getMethod("methodA", new Class[]{long.class})), true));
//				ITerminableIntermediateFuture<ITestService> sfut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("aser");
//				Collection<Tuple2<ITestService, Double>> res = SServiceProvider.rankServicesWithScores(sfut, ranker, null).get();
//				System.out.println("Found: "+res);
//				ITestService aser = res.iterator().next().getFirstEntity();
				
				ITerminableIntermediateFuture<ITestService> sfut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("aser");
				Collection<ITestService> tss = sfut.get();
				INFPropertyComponentFeature nfp = agent.getComponentFeature(INFPropertyComponentFeature.class);
				if(tss.size()>0)
				{
					ITestService ts = tss.iterator().next();
					ts.methodA(100).get();
					Long lat = (Long)nfp.getRequiredServicePropertyProvider(((IService)ts).getServiceIdentifier()).getMethodNFPropertyValue(mi, LatencyProperty.NAME).get();
//					Long lat = (Long)SNFPropertyProvider.getRequiredMethodNFPropertyValue(agent.getExternalAccess(), ((IService)ts).getServiceIdentifier(), mi, LatencyProperty.NAME).get();
//					INFMixedPropertyProvider pp = ((INFRPropertyProvider)ts).getRequiredServicePropertyProvider().get();
//					Long lat = (Long)pp.getMethodNFPropertyValue(mi, LatencyProperty.NAME).get();
					System.out.println("latency: "+lat);
				}
				else
				{
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000).get();
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("User agent problem: "+e);
		}
	}
	
}