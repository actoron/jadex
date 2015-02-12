package jadex.android.clientappdemo.microagent;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.context.IContextService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Simple example agent that shows messages
 *  when it is started or stopped. 
 */
@Description("Sample Android Agent.")
@ProvidedServices({
	@ProvidedService(name="agentinterface", type=IAgentInterface.class)
})
@RequiredServices({
	@RequiredService(name="context", type=IContextService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Service
@Agent
public class MyAgent	implements IAgentInterface
{
	/** This field is injected by jadex. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- methods --------
	/**
	 *  Called when the agent is started.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		return new Future<Void>();
	}

	/**
	 *  Called when the agent is killed.
	 */
	@AgentKilled
	public IFuture<Void> agentKilled()
	{
		callAgent("This is Agent <<" + agent.getComponentIdentifier().getLocalName() + ">> saying goodbye!");
		return IFuture.DONE;
	}

	//-------- helper methods --------

	/**
	 *	Show a message on the device.  
	 *  @param msg The message to be shown.
	 */
	@Override
	public void callAgent(String message)
	{
		IContextService contextService = (IContextService) agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("context").get();
		MyEvent myEvent = new MyEvent();
		myEvent.setMessage("I was called with: " + message);
		contextService.dispatchEvent(myEvent);
	}
}
