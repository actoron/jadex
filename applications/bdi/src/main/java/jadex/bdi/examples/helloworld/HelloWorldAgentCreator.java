package jadex.bdi.examples.helloworld;

/**
 *  Example that shows how an agent model can be created via the editable model api.
 *  This model can be registered with a name at the BDI agent factory and instances
 *  can be created based on this new model.
 */
public class HelloWorldAgentCreator
{
	// todo: ? dynmic model creation not supported
	
//	/**
//	 *  Main for starting hello world agent.
//	 */
//	public static void main(String[] args)
//	{
//		Starter.createPlatform(args).addResultListener(new DefaultResultListener<IExternalAccess>()
//		{
//			public void resultAvailable(final IExternalAccess plat)
//			{
//				// Load dummy model to force loading of bdi kernel.
//				SComponentFactory.isLoadable(plat, "Dummy.agent.xml", null).addResultListener(new DefaultResultListener<Boolean>()
//				{
//					public void resultAvailable(Boolean result)
//					{
//						SServiceProvider.searchService(plat, new ServiceQuery<>( IDynamicBDIFactory.class, RequiredServiceInfo.SCOPE_PLATFORM))
//							.addResultListener(new DefaultResultListener<IDynamicBDIFactory>()
//						{
//							public void resultAvailable(final IDynamicBDIFactory fac)
//							{
//								fac.createAgentModel("HelloWorld", "jadex.bdi.examples.helloworld", null, null)
//									.addResultListener(new DefaultResultListener<IMECapability>()
//								{
//									public void resultAvailable(IMECapability agent)
//									{
//										IMEBelief	msgbelief	= agent.createBeliefbase().createBelief("msg");
//										msgbelief.createFact("\"Welcome to editable models!\"", null);
//										
//										IMEPlan helloplan = agent.createPlanbase().createPlan("hello");
//										helloplan.createBody("HelloWorldPlan", null);
//										IMEConfiguration conf = agent.createConfiguration("default");
//										conf.createInitialPlan("hello");
//										
//										fac.registerAgentModel(agent, "helloagent.agent.xml");
//										
//										SServiceProvider.searchService(plat, new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//											.addResultListener(new DefaultResultListener<IComponentManagementService>()
//										{
//											public void resultAvailable(IComponentManagementService cms)
//											{
//												cms.createComponent("hw1", "helloagent.agent.xml", null,
//													new DefaultResultListener<Collection<Tuple2<String, Object>>>()
//												{
//													public void resultAvailable(Collection<Tuple2<String, Object>> result)
//													{
//														System.out.println("finished.");
//													}
//												});
//											}
//										});
//									}
//								});								
//							}
//						});
//					}
//				});
//			}
//		});
//	}
}
