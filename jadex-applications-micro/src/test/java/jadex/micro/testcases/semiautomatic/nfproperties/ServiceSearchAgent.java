package jadex.micro.testcases.semiautomatic.nfproperties;

import java.util.Arrays;
import java.util.Collection;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.search.BasicEvaluator;
import jadex.bridge.nonfunctional.search.ComposedEvaluator;
import jadex.bridge.nonfunctional.search.CountThresholdSearchTerminationDecider;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent

@Service
@ProvidedServices(@ProvidedService(type=ICoreDependentService.class, implementation=@Implementation(NFPropertyTestService.class)))
@NFProperties({@NFProperty(FakeCpuLoadProperty.class),
			   @NFProperty(FakeFreeMemoryProperty.class),
			   @NFProperty(FakeNetworkBandwidthProperty.class),
			   @NFProperty(FakeReliabilityProperty.class)
})
public class ServiceSearchAgent
{
	protected static final int SEARCH_DELAY = 1000;
	
	/**
	 * The agent.
	 */
	@Agent
	protected IInternalAccess agent;

	/**
	 *  Body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> done = new Future<Void>();
		final ComposedEvaluator ce = new ComposedEvaluator();
		ce.addEvaluator(new BasicEvaluator<Double>(agent.getExternalAccess(), "fakecpuload")
		{
			public double calculateEvaluation(Double propertyvalue)
			{
				return (100.0 - propertyvalue) * 0.01;
			}
		});
		
		ce.addEvaluator(new BasicEvaluator<Double>(agent.getExternalAccess(), "fakereliability")
		{
			public double calculateEvaluation(Double propertyvalue)
			{
				return propertyvalue * 0.01;
			}
		});
		
		ce.addEvaluator(new BasicEvaluator<Long>(agent.getExternalAccess(), "fakefreemem", MemoryUnit.MB)
		{
			public double calculateEvaluation(Long propertyvalue)
			{
				return Math.min(4096.0, propertyvalue) / 4096.0;
			}
		});
		
		ce.addEvaluator(new BasicEvaluator<Long>(agent.getExternalAccess(), "fakenetworkbandwith", MemoryUnit.MB)
		{
			public double calculateEvaluation(Long propertyvalue)
			{
				return Math.min(100.0, propertyvalue) / 100.0;
			}
		});
		
//		BasicEvaluatorConstraints cts = new BasicEvaluatorConstraints(null, evaluator, evaluationsize)
//		SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM, new Basic)
		
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(SEARCH_DELAY, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> step = this;
//				SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new ServiceRankingResultListener<ICoreDependentService>(ce, new CountThresholdSearchTerminationDecider<ICoreDependentService>(10), 
//					new IResultListener<Collection<ICoreDependentService>>()
//				{
//					public void resultAvailable(Collection<ICoreDependentService> result)
//					{
//						System.out.println(Arrays.toString(((List<ICoreDependentService>) result).toArray()));
//						agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step, SEARCH_DELAY);
//					}
//
//					public void exceptionOccurred(Exception exception)
//					{
//						exception.printStackTrace();
//					}
//				}));
				
//				SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new ServiceRankingResultListener<ICoreDependentService>(new IResultListener<Collection<Tuple2<ICoreDependentService, Double>>>()
//				{
//					public void resultAvailable(Collection<Tuple2<ICoreDependentService, Double>> result)
//					{
//						System.out.println(Arrays.toString(((List<Tuple2<ICoreDependentService, Double>>)result).toArray()));
//						agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step, SEARCH_DELAY);
//					}
//	
//					public void exceptionOccurred(Exception exception)
//					{
//						exception.printStackTrace();
//					}
//				}, ce, new CountThresholdSearchTerminationDecider<ICoreDependentService>(10))); 
				
//				ITerminableIntermediateFuture<ICoreDependentService> fut = SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//				ITerminableIntermediateFuture<ICoreDependentService> res = SServiceProvider.rankServices(fut, ce, new CountThresholdSearchTerminationDecider<ICoreDependentService>(10));
//				res.addResultListener(new IResultListener<Collection<ICoreDependentService>>()
//				{
//					public void resultAvailable(Collection<ICoreDependentService> result)
//					{
//						System.out.println(Arrays.toString(((List<ICoreDependentService>)result).toArray()));
//						agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step, SEARCH_DELAY);
//					}
//	
//					public void exceptionOccurred(Exception exception)
//					{
//						exception.printStackTrace();
//					}
//				}); 
				
				ITerminableIntermediateFuture<ICoreDependentService> fut = SServiceProvider.getServices(agent, ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				ITerminableIntermediateFuture<Tuple2<ICoreDependentService, Double>> res = SServiceProvider.rankServicesWithScores(fut, ce, new CountThresholdSearchTerminationDecider<ICoreDependentService>(10));
				res.addResultListener(new IResultListener<Collection<Tuple2<ICoreDependentService, Double>>>()
				{
					public void resultAvailable(Collection<Tuple2<ICoreDependentService, Double>> result)
					{
						System.out.println(Arrays.toString(result.toArray()));
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(SEARCH_DELAY, step);
					}
	
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
				
				return IFuture.DONE;
			}
		});
		
		return done;
	}
}
