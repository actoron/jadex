package jadex.micro.testcases.remotestepinservicecall;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.*;


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
		if (ServiceCall.getCurrentInvocation() == null) {
			return new Future<Void>(new RuntimeException("current service call before schedule is NULL. This was not really the purpose of this test."));
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

		if (ServiceCall.getCurrentInvocation() == null) {
			System.err.println("current service call after schedule is NULL!!!");
		}

		return IFuture.DONE;
	}
}
