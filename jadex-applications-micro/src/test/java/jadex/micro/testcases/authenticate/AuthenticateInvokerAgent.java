package jadex.micro.testcases.authenticate;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;

/**
 *  Sub agent to find and invoke test services and return the results.
 */
@Agent
public class AuthenticateInvokerAgent
{
	@AgentBody
	public IFuture<boolean[]>	runTests(IInternalAccess agent)
	{
		final Future<boolean[]>	ret	= new Future<boolean[]>();
		agent.getComponentFeature(IRequiredServicesFeature.class).searchServices(ITestService.class, Binding.SCOPE_GLOBAL)
			.addResultListener(new ExceptionDelegationResultListener<Collection<ITestService>, boolean[]>(ret)
		{
			@Override
			public void customResultAvailable(Collection<ITestService> services) throws Exception
			{
				System.out.println("found: "+services);
				ret.setResult(new boolean[8]);	// Todo
			}
		});
		
		return ret;
	}
}
