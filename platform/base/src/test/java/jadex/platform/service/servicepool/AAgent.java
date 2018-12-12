package jadex.platform.service.servicepool;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Functionality agent that provides service A.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$pojoagent")))
public class AAgent implements IAService
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Example method 1.
	 */
	public IFuture<String> ma1(String str)
	{
//		System.out.println("ma1 called with: "+str+" "+agent.getComponentIdentifier().getLocalName());
		return new Future<String>(str+" result of ma1");
	}
	
	/**
	 *  Example method 2.
	 */
	public IIntermediateFuture<Integer> ma2()
	{
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		final int[] cnt = new int[1];
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(cnt[0]<100)
				{
//					System.out.println("ma2 called "+cnt[0]+" "+agent.getComponentIdentifier().getLocalName());
					ret.addIntermediateResult(Integer.valueOf(cnt[0]++));
					agent.getFeature(IExecutionFeature.class).waitForDelay(10, this, false);
				}
				else
				{
					ret.setFinished();
				}
				return IFuture.DONE;
			}
		};
		agent.getFeature(IExecutionFeature.class).scheduleStep(step);
		return ret;
	}
	
	/**
	 *  Example method 3 (for non func).
	 */
	public IFuture<TestReport> ma3(Map<String, Object> tprops)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		Map<String, Object> cprops = sc.getProperties();
		
		TestReport tr = new TestReport("#?", "Test of non-functional props");
		
		if(tprops.equals(cprops))
		{
			tr.setSucceeded(true);
		}
		else
		{
			System.out.println("properties (target, received): "+tprops+" "+cprops);
			tr.setReason("Unequal non-func props.");
		}
		
		return new Future<TestReport>(tr);
	}
}
