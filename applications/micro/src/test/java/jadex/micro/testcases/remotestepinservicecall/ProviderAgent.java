package jadex.micro.testcases.remotestepinservicecall;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;

	/**
	 * Method that schedules a step on the given exta
	 * @param exta
	 * @return
	 */
	public IFuture<Void> method(IExternalAccess exta)
	{
		ServiceCall	sc	= ServiceCall.getCurrentInvocation();
		
		if (ServiceCall.getCurrentInvocation() == null) {
			return new Future<Void>(new RuntimeException("current service call before schedule is NULL. This was not really the purpose of this test."));
		}

		agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>() {
			@Override
			@Classname("myuniquestepname")
			public IFuture<Void> execute(IInternalAccess ia) {
//				System.out.println("im on the remote platform now.");
				// doing nothing here
				return Future.DONE;
			}
		}).get();

		if (ServiceCall.getCurrentInvocation() != sc) {
			return new Future<Void>(new RuntimeException("Current service call after schedule internal has changed: "+ServiceCall.getCurrentInvocation()+", "+sc));
		}

		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, Binding.SCOPE_PLATFORM));
		IExternalAccess	rootacc	= cms.getExternalAccess(agent.getIdentifier().getRoot()).get();
		rootacc.scheduleStep(new IComponentStep<Void>() {
			@Override
			@Classname("myuniquestepname")
			public IFuture<Void> execute(IInternalAccess ia) {
//				System.out.println("im on the remote platform now.");
				// doing nothing here
				return Future.DONE;
			}
		}).get();

		if (ServiceCall.getCurrentInvocation() != sc) {
			return new Future<Void>(new RuntimeException("Current service call after schedule external local has changed: "+ServiceCall.getCurrentInvocation()+", "+sc));
		}

		cms	= SServiceProvider.searchService(exta, new ServiceQuery<>( IComponentManagementService.class, Binding.SCOPE_PLATFORM)).get();
		cms.getComponentDescription(exta.getComponentIdentifier()).get();
		if (ServiceCall.getCurrentInvocation() != sc) {
			return new Future<Void>(new RuntimeException("Current service call has changed after remote CMS call: "+ServiceCall.getCurrentInvocation()+", "+sc));
		}
		
		ITestService	ts	= SServiceProvider.searchService(exta, new ServiceQuery<>( ITestService.class, Binding.SCOPE_LOCAL)).get();
		ts.method(agent.getExternalAccess()).get();
		if (ServiceCall.getCurrentInvocation() != sc) {
			return new Future<Void>(new RuntimeException("Current service call has changed after remote callback: "+ServiceCall.getCurrentInvocation()+", "+sc));
		}
		
		exta.scheduleStep(new IComponentStep<Void>() {
			@Override
			@Classname("myuniquestepname")
			public IFuture<Void> execute(IInternalAccess ia) {
//				System.out.println("im on the remote platform now.");
				// doing nothing here
				return Future.DONE;
			}
		}).get();

		if (ServiceCall.getCurrentInvocation() != sc) {
			return new Future<Void>(new RuntimeException("Current service call after schedule external remote has changed: "+ServiceCall.getCurrentInvocation()+", "+sc));
		}

		return IFuture.DONE;
	}
}
