package jadex.micro.examples.hunterprey;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.IEnvironmentService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  A prey implemented using the EnvSupport service interface.
 */
@Agent
@RequiredServices(
	@RequiredService(name="env", type=IEnvironmentService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class ServicePreyAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Register the agent as a prey at startup.
	 */
	@AgentCreated
	public IFuture<Void>	start()
	{
		final Future<Void>	ret	= new Future<Void>();
		IFuture<IEnvironmentService>	envfut	= agent.getServiceContainer().getRequiredService("env");
		envfut.addResultListener(new ExceptionDelegationResultListener<IEnvironmentService, Void>(ret)
		{
			public void customResultAvailable(IEnvironmentService env)
			{
				env.register("prey");
				ret.setResult(null);
			}
		});
		return ret;
	}
}
