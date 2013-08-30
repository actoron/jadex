package jadex.micro.testcases.nfproperties;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.search.BasicEvaluator;
import jadex.bridge.nonfunctional.search.ComposedEvaluator;
import jadex.bridge.nonfunctional.search.CountThresholdSearchTerminationDecider;
import jadex.bridge.nonfunctional.search.ServiceRankingResultListener;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Agent

@Service
@ProvidedServices(@ProvidedService(type=ICoreDependentService.class, implementation=@Implementation(NFPropertyTestService.class)))
@NFProperties({@NFProperty(FakeCpuLoadProperty.class),
			   @NFProperty(FakeFreeMemoryProperty.class),
			   @NFProperty(FakeNetworkBandwidthProperty.class),
			   @NFProperty(FakeReliabilityProperty.class)})
public class ServiceSearchAgent
{
	protected static final int SEARCH_DELAY = 1000;
	
	/**
	 * The agent.
	 */
	@Agent
	protected MicroAgent agent;

	/**
	 *  Body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> done = new Future<Void>();
		final ComposedEvaluator ce = new ComposedEvaluator();
		ce.addEvaluator(new BasicEvaluator<Double>("fakecpuload")
		{
			public double calculateEvaluation(Double propertyvalue)
			{
				return (100.0 - propertyvalue) * 0.01;
			}
		});
		
		ce.addEvaluator(new BasicEvaluator<Double>("fakereliability")
		{
			public double calculateEvaluation(Double propertyvalue)
			{
				return propertyvalue * 0.01;
			}
		});
		
		ce.addEvaluator(new BasicEvaluator<Long>("fakefreemem", MemoryUnit.MB)
		{
			public double calculateEvaluation(Long propertyvalue)
			{
				return Math.min(4096.0, propertyvalue) / 4096.0;
			}
		});
		
		ce.addEvaluator(new BasicEvaluator<Long>("fakenetworkbandwith", MemoryUnit.MB)
		{
			public double calculateEvaluation(Long propertyvalue)
			{
				return Math.min(100.0, propertyvalue) / 100.0;
			}
		});
		
//		BasicEvaluatorConstraints cts = new BasicEvaluatorConstraints(null, evaluator, evaluationsize)
//		SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM, new Basic)
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> step = this;
				SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ServiceRankingResultListener<ICoreDependentService>(ce, new CountThresholdSearchTerminationDecider<ICoreDependentService>(10))
				{
					public void resultAvailable(Collection<ICoreDependentService> result)
					{
						System.out.println(Arrays.toString(((List<ICoreDependentService>) result).toArray()));
						agent.scheduleStep(step, SEARCH_DELAY);
					}

					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
				
				
				return IFuture.DONE;
			}
		}, SEARCH_DELAY);
		
		return done;
	}
}
