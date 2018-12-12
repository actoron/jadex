package jadex.micro.tutorial;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

/**
 *  Chat micro agent that uses the clock service. 
 */
@Description("This agent uses the component management service.")
@Agent
public class ChatC3Agent
{
	@Agent
	protected IInternalAccess agent;
	
//	/** The required services feature. */
//	@AgentFeature
//	protected IRequiredServicesFeature requiredServicesFeature;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
//		IFuture<IComponentManagementService>	cms	= requiredServicesFeature.getService("cms");
//		cms.addResultListener(new DefaultResultListener<IComponentManagementService>()
//		{
//			public void resultAvailable(final IComponentManagementService cms)
//			{
		SComponentManagementService.getComponentDescriptions(agent)
//		cms.getComponentDescriptions()
		.addResultListener(
			new DefaultResultListener<IComponentDescription[]>()
		{
			public void resultAvailable(IComponentDescription[] descs)
			{
				for(int i=0; i<descs.length; i++)
				{
					System.out.println("Found: "+descs[i]);
				}
			}
		});
//			}
//		});
	}
}