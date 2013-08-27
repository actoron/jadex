package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.ComposedEvaluator;
import jadex.bridge.nonfunctional.IServiceEvaluator;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent

@Service
@ProvidedServices(@ProvidedService(type=ICoreDependentService.class, implementation=@Implementation(NFPropertyTestService.class)))
@NFProperties({@NFProperty(type=FakeCpuLoadProperty.class),
			   @NFProperty(type=FakeFreeMemoryProperty.class),
			   @NFProperty(type=FakeNetworkBandwidthProperty.class),
			   @NFProperty(type=FakeReliabilityProperty.class)})
public class ServiceSearchAgent
{
	@Agent
	protected MicroAgent agent;
	
//	@AgentBody
//	public void body()
//	{
//		ComposedEvaluator ce = new ComposedEvaluator();
//		ce.addEvaluator(new IServiceEvaluator()
//		{
//			public double evaluate(IService service)
//			{
//				return service.;
//			}
//		})
//		BasicEvaluatorConstraints cts = new BasicEvaluatorConstraints(null, evaluator, evaluationsize)
//		SServiceProvider.getServices(agent.getServiceProvider(), ICoreDependentService.class, RequiredServiceInfo.SCOPE_PLATFORM, new Basic)
//	}
}
