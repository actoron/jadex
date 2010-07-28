package jadex.bdi.examples.helloworld;

import jadex.base.Starter;
import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.editable.IMEBelief;
import jadex.bdi.model.editable.IMECapability;
import jadex.bdi.model.editable.IMEConfiguration;
import jadex.bdi.model.editable.IMEPlan;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.SServiceProvider;

/**
 *  Example that shows how an agent model can be created via the editable model api.
 *  This model can be registered with a name at the BDI agent factory and instances
 *  can be created based on this new model.
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
						
						IMEBelief	msgbelief	= agent.createBeliefbase().createBelief("msg");
						msgbelief.createFact("\"Welcome to editable models!\"", null);
						
						IMEPlan helloplan = agent.createPlanbase().createPlan("hello");
						helloplan.createBody("HelloWorldPlan", null);
						IMEConfiguration conf = agent.createConfiguration("default");
						conf.createInitialPlan("hello");
						
						fac.registerAgentModel(agent, "helloagent.agent.xml");
						
						SServiceProvider.getServiceUpwards(plat.getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IComponentManagementService cms = (IComponentManagementService)result;
								cms.createComponent("hw1", "helloagent.agent.xml", null, new DefaultResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										System.out.println("finished.");
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
