package jadex.micro.tutorial;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Chat micro agent that uses the clock service. 
 */
@Description("This agent uses the component management service.")
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class ChatC3Agent
{
	/** The underlying mirco agent. */
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		agent.getServiceContainer().getRequiredService("cms")
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				cms.getComponentDescriptions().addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IComponentDescription[] descs = (IComponentDescription[])result;
						for(int i=0; i<descs.length; i++)
						{
							System.out.println("Found: "+descs[i]);
						}
					}
				});
			}
		});
	}
}