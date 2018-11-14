package jadex.micro.testcases.killcomponent;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;


/**
 * 
 */
@Agent
public class ProviderAgent
{
	@AgentFeature
	private IArgumentsResultsFeature argResults;

	@AgentCreated
	public void created(IInternalAccess agent)
	{
//		agent.getLogger().severe("Agent created: " + agent.getDescription());
		argResults.getResults().put("exampleresult", "value");
	}

	@AgentBody
	private void body()
	{
		// cannot work because this agent is terminated from outside
//		argResults.getResults().put("exampleresult", "value");
	}

	/**
	 * Call a method that must use a secure transport under the hood.
	 */
	public IFuture<Void> method(String msg)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		Future<Void> ret = new Future<Void>();
		return ret;
	}

}
