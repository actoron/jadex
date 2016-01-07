package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.servicecall.DecoupledServiceAgent;
import jadex.micro.servicecall.DirectServiceAgent;
import jadex.micro.servicecall.IServiceCallService;
import jadex.micro.servicecall.RawServiceAgent;

/**
 *  A test case for testing access to services of already terminated components.
 */
@Description("A test case for testing access to services of already terminated components.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
@Agent
public class RemovedServiceAgent
{
	/** The agent reference. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The cms service. */
	@AgentService
	protected IComponentManagementService	cms;
	
	/** The test counter. */
	protected int cnt;
	
	/**
	 *  Perform the tests and indicate completion in the future.
	 */
	@AgentBody
	public IFuture<Void>	body()
	{
		final Future<Collection<TestReport>>	reports	= new Future<Collection<TestReport>>();
		
		performTests(RawServiceAgent.class.getName()+".class", true).addResultListener(new DelegationResultListener<Collection<TestReport>>(reports)
		{
			public void customResultAvailable(final Collection<TestReport> reports1)
			{
				performTests(DirectServiceAgent.class.getName()+".class", true).addResultListener(new DelegationResultListener<Collection<TestReport>>(reports)
				{
					public void customResultAvailable(final Collection<TestReport> reports2)
					{
						performTests(DecoupledServiceAgent.class.getName()+".class", false).addResultListener(new DelegationResultListener<Collection<TestReport>>(reports)
						{
							public void customResultAvailable(Collection<TestReport> reports3)
							{
								Collection<TestReport>	result	= new ArrayList<TestReport>();
								result.addAll(reports1);
								result.addAll(reports2);
								result.addAll(reports3);
								super.customResultAvailable(result);
							}
						});
					}
				});
			}
		});
		
		final Future<Void>	ret	= new Future<Void>();
		reports.addResultListener(new IResultListener<Collection<TestReport>>()
		{
			public void resultAvailable(Collection<TestReport> results)
			{				
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), results.toArray(new TestReport[results.size()])));
				ret.setResult(null);
			}
			public void exceptionOccurred(Exception exception)
			{
				final TestReport	tr	= new TestReport("#1", "Exception during test.");
				tr.setFailed(exception.toString());
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Perform the tests and indicate completion in the future.
	 */
	public IFuture<Collection<TestReport>> performTests(final String agentname, final boolean callsuccess)
	{
		final IntermediateFuture<TestReport>	testfut	= new IntermediateFuture<TestReport>();
		
		// Create agent to call service on.
		cms.createComponent(null, agentname, new CreationInfo(agent.getComponentIdentifier()), null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<TestReport>>(testfut)
		{
			public void customResultAvailable(final IComponentIdentifier cid)
			{
				// Get service reference of created agent.
				agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IServiceCallService.class, Binding.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IServiceCallService, Collection<TestReport>>(testfut)
				{
					public void customResultAvailable(final IServiceCallService scs)
					{
						final TestReport	tr1	= new TestReport("#"+(++cnt), "Test if service of "+agentname+" can be called.");
						testfut.addIntermediateResult(tr1);
						scs.call().addResultListener(new ExceptionDelegationResultListener<Void, Collection<TestReport>>(testfut)
						{
							public void customResultAvailable(Void result)
							{
								tr1.setSucceeded(true);
								
								// Now kill the agent.
								cms.destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Collection<TestReport>>(testfut)
								{
									public void customResultAvailable(Map<String, Object> result)
									{
										final TestReport	tr2	= new TestReport("#"+(++cnt), "Test if service of destroyed "+agentname+" can be found.");
										testfut.addIntermediateResult(tr2);
										agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IServiceCallService.class, Binding.SCOPE_PLATFORM)
											.addResultListener(new IResultListener<IServiceCallService>()
										{
											public void resultAvailable(IServiceCallService result)
											{
												tr2.setFailed("Service was found: "+result);
												cont();
											}
											
											public void exceptionOccurred(Exception exception)
											{
												if(exception instanceof ServiceNotFoundException
													|| exception instanceof ComponentTerminatedException)	// decoupled (todo: should be same exception?)
												{
													tr2.setSucceeded(true);
												}
												else
												{
													tr2.setFailed("Wrong exception. Expected ServiceNotFoundException but was: "+exception);													
												}
												cont();
											}
											
											protected void cont()
											{
												final TestReport	tr3	= new TestReport("#"+(++cnt), "Test if service of destroyed "+agentname+" can be called.");
												testfut.addIntermediateResult(tr3);
												scs.call().addResultListener(new IResultListener<Void>()
												{
													public void exceptionOccurred(Exception exception)
													{
														cont(exception);
													}
													public void resultAvailable(Void result)
													{
														cont(null);
													}
													
													protected void cont(Exception ex)
													{
														if(callsuccess)
														{
															if(ex==null)
															{
																tr3.setSucceeded(true);
															}
															else
															{
																tr3.setFailed("Service call did not succeed: "+ex);
															}
														}
														else
														{
															if(ex==null)
															{
																tr3.setFailed("Service call did not fail as expected.");
															}
															else
															{
																if(ex instanceof ServiceInvalidException	// direct
																	|| ex instanceof ComponentTerminatedException)	// decoupled (todo: should be same exception?)
																{
																	tr3.setSucceeded(true);
																}
																else
																{
																	tr3.setFailed("Wrong exception. Expected ServiceInvalidException but was: "+ex);													
																}																
															}
														}
														
														agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(3, new TestReport[]{tr1, tr2, tr3}));
														testfut.setFinished();														
													}
												});
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
		
		return testfut;
	}
}
