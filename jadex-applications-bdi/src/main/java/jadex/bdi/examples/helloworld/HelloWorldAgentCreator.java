package jadex.bdi.examples.helloworld;

import jadex.base.Starter;
import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.editable.IMECapability;
import jadex.bdi.model.editable.IMEConfiguration;
import jadex.bdi.model.editable.IMEPlan;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.SServiceProvider;

/**
 * 
 */
public class HelloWorldAgentCreator
{
	/**
	 *  Main for starting hello world agent.
	 */
	public static void main(String[] args)
	{
		Starter.createPlatform(args).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IExternalAccess plat = (IExternalAccess)result;
				SServiceProvider.getService(plat.getServiceProvider(), new ComponentFactorySelector(BDIAgentFactory.FILETYPE_BDIAGENT))
					.addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						BDIAgentFactory fac = (BDIAgentFactory)result;
						
						IMECapability agent = fac.createAgentModel("HelloWorld", "jadex.bdi.examples.helloworld", null);
						IMEPlan helloplan = agent.createPlanbase().createPlan("hello");
						helloplan.createBody("HelloWorldPlan", null);
						IMEConfiguration conf = agent.createConfiguration("default");
						conf.createInitialPlan("hello");
						
						fac.registerAgentModel(agent, "helloagent");
						
						SServiceProvider.getServiceUpwards(plat.getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IComponentManagementService cms = (IComponentManagementService)result;
								cms.createComponent("hw1", "helloagent", null, new DefaultResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										System.out.println("eeende");
									}
								});
							}
						});
					}
				});
			}
		});
	}
}
