package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Simple test agent with one service for testing parameter and result copying.
 */
@ProvidedServices(@ProvidedService(type=ICService.class, implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(@RequiredService(name="cservice", type=ICService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_LOCAL)))
@Results(@Result(name="testresults", clazz=Testcase.class))
@Service(ICService.class)
@Agent
public class CAgent extends JunitAgentTest implements ICService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Test if copy parameters work.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final List<TestReport> testcases = new ArrayList<TestReport>();
		
		// Test with required service proxy.
		IFuture<ICService>	fut	= agent.getComponentFeature(IRequiredServicesFeature.class).getService("cservice");
		fut.addResultListener(new DefaultResultListener<ICService>()
		{
			public void resultAvailable(ICService result)
			{
				testService(testcases, result)
					.addResultListener(new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						// Test with provided service proxy.
						agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ICService.class))
							.addResultListener(new DefaultResultListener<ICService>()
						{
							public void resultAvailable(ICService result)
							{
								testService(testcases, result)
									.addResultListener(new DefaultResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{										
										agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(testcases.size(),
											(TestReport[])testcases.toArray(new TestReport[testcases.size()])));
//										killAgent();
										ret.setResult(null);
									}
								});
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test if no copy works.
	 */
	public IFuture<Boolean> testArgumentReference(Object arg, int hash)
	{
//		System.out.println("called service");
		return arg.hashCode()==hash ? IFuture.TRUE: IFuture.FALSE;
	}
	
	/**
	 *  Test if no copy works.
	 */
	public IFuture<Boolean> testArgumentCopy(Object arg, int hash)
	{
		return arg.hashCode()!=hash ? IFuture.TRUE: IFuture.FALSE;
	}
	
	/**
	 *  Test if result value can be passed by reference.
	 */
	public IFuture<Object> testResultReference(Object arg)
	{
		return new Future<Object>(arg);
	}
	
	/**
	 *  Test if result value can be passed by copy.
	 */
	public IFuture<Object> testResultCopy(Object arg)
	{
		return new Future<Object>(arg);
	}
	
	/**
	 *  Test if result value can be passed by reference.
	 */
	public IIntermediateFuture<Object> testResultReferences(Object[] args)
	{
		return new IntermediateFuture<Object>(Arrays.asList(args));
	}
	
	/**
	 *  Test if result value can be passed by copy.
	 */
	public IIntermediateFuture<Object> testResultCopies(Object[] args)
	{
		return new IntermediateFuture<Object>(Arrays.asList(args));		
	}
	
	//-------- helper methods --------
	/**
	 *  Perform test with the service.
	 */
	protected IFuture<Void>	testService(final List<TestReport> testcases, final ICService cservice)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		final Object arg = new Object();
		cservice.testArgumentReference(arg, arg.hashCode()).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
				TestReport tr = new TestReport("#1", "Test if argument is not copied.");
				if(result.booleanValue())
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setReason("Hashcode is not equal.");
				}
				testcases.add(tr);
				
				cservice.testArgumentCopy(arg, arg.hashCode()).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
				{
					public void customResultAvailable(Boolean result)
					{
						TestReport tr = new TestReport("#2", "Test if argument is copied.");
						if(result.booleanValue())
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setReason("Hashcode is equal.");
						}
						testcases.add(tr);
				
						cservice.testResultReference(arg).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
						{
							public void customResultAvailable(Object result)
							{
								TestReport tr = new TestReport("#3", "Test if result is not copied.");
								if(arg.hashCode()==result.hashCode())
								{
									tr.setSucceeded(true);
								}
								else
								{
									tr.setReason("Hashcode is not equal.");
								}
								testcases.add(tr);
								
								cservice.testResultCopy(arg).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
								{
									public void customResultAvailable(Object result)
									{
										TestReport tr = new TestReport("#4", "Test if result is copied.");
										if(arg.hashCode()!=result.hashCode())
										{
											tr.setSucceeded(true);
										}
										else
										{
											tr.setReason("Hashcode is equal.");
										}
										testcases.add(tr);
										
										final Object[]	args	= new Object[]{arg, new Object()};
										cservice.testResultReferences(args)
											.addResultListener(new ExceptionDelegationResultListener<Collection<Object>, Void>(ret)
										{
											public void customResultAvailable(Collection<Object> result)
											{
												TestReport tr = new TestReport("#4", "Test if results are not copied.");
												if(args.length!=result.size())
												{
													tr.setReason("Wrong number of results.");													
												}
												else
												{
													boolean	match	= true;
													Iterator<Object>	it	= result.iterator();
													for(int i=0; match && i<args.length; i++)
													{
														match	= args[i].hashCode()==it.next().hashCode();
													}
													
													if(match)
													{
														tr.setSucceeded(true);
													}
													else
													{
														tr.setReason("Hashcode is not equal.");
													}
												}
												testcases.add(tr);
												
												cservice.testResultCopies(args)
													.addResultListener(new ExceptionDelegationResultListener<Collection<Object>, Void>(ret)
												{
													public void customResultAvailable(Collection<Object> result)
													{
														TestReport tr = new TestReport("#5", "Test if results are copied.");
														if(args.length!=result.size())
														{
															tr.setReason("Wrong number of results.");													
														}
														else
														{
															boolean	match	= false;
															Iterator<Object>	it	= result.iterator();
															for(int i=0; !match && i<args.length; i++)
															{
																match	= args[i].hashCode()==it.next().hashCode();
															}
															
															if(!match)
															{
																tr.setSucceeded(true);
															}
															else
															{
																tr.setReason("Hashcode is equal.");
															}
														}
														testcases.add(tr);
														
														ret.setResult(null);
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
		
		return ret;
	}
}
