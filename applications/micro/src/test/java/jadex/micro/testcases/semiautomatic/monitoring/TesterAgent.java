package jadex.micro.testcases.semiautomatic.monitoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  This agent creates call chains to check if monitoring event are created with correct
 *  origins and can be connected to chains.
 *  
 *  - agent creates 2 subagents of same type
 *  - each of the subagents call the test method on the parent
 *  - the test service method is then recursively called of random component until level is reached 
 */
@Agent
@Configurations({@Configuration(name="default"), @Configuration(name="created")})
@ProvidedServices(@ProvidedService(type=ITestService.class))
@Service
public class TesterAgent implements ITestService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		if(agent.getConfiguration().equals("created"))
		{
			ITestService tsa = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ITestService.class).setProvider(agent.getId().getParent())).get();
			tsa.test(0).get();
		}
		else
		{	
//			IMonitoringService mons = agent.getServiceProvider().searchService( new ServiceQuery<>( IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
//			mons.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(new IFilter<IMonitoringEvent>()
//			{
//				public boolean filter(IMonitoringEvent obj)
//				{
//					return obj.getType().indexOf(IMonitoringEvent.SOURCE_CATEGORY_SERVICE)!=-1;
//				}
//			}).addResultListener(new IntermediateDefaultResultListener<IMonitoringEvent>()
//			{
//				public void intermediateResultAvailable(IMonitoringEvent result)
//				{
//					System.out.println("received: "+result);
//				}
//			});
			
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						ThreadSuspendable sus = new ThreadSuspendable();
						
						CreationInfo ci = new CreationInfo("created", null);
						ci.setParent(agent.getId());
						ci.setResourceIdentifier(agent.getModel().getResourceIdentifier());
						final String name =  TesterAgent.class.getName()+".class";
						
						IExternalAccess eaa = agent.createComponent(ci.setFilename(name)).get();
						IExternalAccess eab = agent.createComponent(ci.setFilename(name)).get();
					
						IComponentDescription desca = eaa.scheduleStep(new IComponentStep<IComponentDescription>()
						{
							@Override
							public IFuture<IComponentDescription> execute(IInternalAccess ia)
							{
								return new Future<IComponentDescription>(ia.getDescription());
							}
						}).get();
						IComponentDescription descb = eaa.scheduleStep(new IComponentStep<IComponentDescription>()
						{
							@Override
							public IFuture<IComponentDescription> execute(IInternalAccess ia)
							{
								return new Future<IComponentDescription>(ia.getDescription());
							}
						}).get();
					
//						System.out.println("chain a: "+eaa+" "+desca.getCause().getOrigin());
//						System.out.println("chain b: "+eab+" "+descb.getCause().getOrigin());
					}
					catch(ComponentTerminatedException e)
					{
						// avoid exception being printed during start test.
					}
				}
			}).start();
		}
	}
	
	/**
	 * 
	 */
	public IFuture<Void> test(int level)
	{
//		final Future<Void> ret = new Future<Void>();
//		System.out.println("invoked test on: "+agent.getComponentIdentifier()+" level="+level+" "+ServiceCall.getCurrentInvocation().getCause());
		if(level<10)
		{
			Collection<ITestService> tss = agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(ITestService.class)).get();
			if(tss.size()>0)
			{
				int num = (int)(Math.random()*tss.size());
//				System.out.println("found: "+tss.size()+" "+num);
				List<ITestService> l = new ArrayList<ITestService>(tss);
				ITestService ts = l.get(num);
				// The .get() is important to not interrupt the call chain
				// Do we want it to work also without?
				ts.test(level+1).get();
			}
		}
//		return ret;
		return IFuture.DONE;
	}
}
