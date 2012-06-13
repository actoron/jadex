package jadex.micro.testcases.stream;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@RequiredServices(@RequiredService(name="ss", type=IStreamService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL)))
@Arguments(@Argument(name="testcnt", clazz=int.class, defaultvalue="8"))
public class StreamUserAgent extends TestAgent
{
	/**
	 * 
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
//		IFuture<IStreamService> fut = agent.getServiceContainer().getRequiredService("ss");
//		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
//		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);

		testLocal(1, tc).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
		{
			public void customResultAvailable(Integer testcnt)
			{
				testRemote(testcnt.intValue(), tc).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
				{
					public void customResultAvailable(Integer result)
					{
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Integer> testLocal(int testno, Testcase tc)
	{
		return performTests(testno, agent.getServiceProvider(), agent.getComponentIdentifier().getRoot(), tc);
	}
	
	/**
	 * 
	 */
	protected IFuture<Integer> testRemote(final int testno, final Testcase tc)
	{
		final Future<Integer> ret = new Future<Integer>();
		
		createPlatform(null).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, Integer>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTests(testno, platform.getServiceProvider(), platform.getComponentIdentifier(), tc)
					.addResultListener(agent.createResultListener(new DelegationResultListener<Integer>(ret)
				{
					public void customResultAvailable(final Integer result)
					{
						platform.killComponent();
//							.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
//						{
//							public void customResultAvailable(Map<String, Object> v)
//							{
//								ret.setResult(result);
//							}
//						});
						ret.setResult(result);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	
	/**
	 * 
	 */
	protected IFuture<Integer> performTests(int testcnt,IServiceProvider provider, IComponentIdentifier root, final Testcase tc)
	{
		final Future<Integer> ret = new Future<Integer>();
		
		final int[] cnt = new int[]{testcnt};
		
		createComponent(provider, "jadex/micro/testcases/stream/StreamProviderAgent.class", root, null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Integer>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				SServiceProvider.getService(agent.getServiceProvider(), cid, IStreamService.class)
					.addResultListener(new ExceptionDelegationResultListener<IStreamService, Integer>(ret)
				{
					public void customResultAvailable(final IStreamService ss)
					{
						testGetInputStream(cnt[0]++, ss).addResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
						{
							public void customResultAvailable(TestReport result)
							{
								tc.addReport(result);
								testGetOutputStream(cnt[0]++, ss).addResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
								{
									public void customResultAvailable(TestReport result)
									{
										tc.addReport(result);
										testPassInputStream(cnt[0]++, ss).addResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
										{
											public void customResultAvailable(TestReport result)
											{
												tc.addReport(result);
												testPassOutputStream(cnt[0]++, ss).addResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
												{
													public void customResultAvailable(TestReport result)
													{
														tc.addReport(result);
														destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Integer>(ret)
														{
															public void customResultAvailable(Map<String,Object> result) 
															{
																ret.setResult(new Integer(cnt[0]));
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
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testGetInputStream(int testno, IStreamService ss)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Test getInputStream()");
		
		ss.getInputStream().addResultListener(new IResultListener<IInputConnection>()
		{
			public void resultAvailable(IInputConnection con)
			{
//				System.out.println("received icon: "+con);
				StreamProviderAgent.read(con).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
				tr.setFailed("Exception: "+exception.getMessage());
				ret.setResult(tr);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testGetOutputStream(int testno, IStreamService ss)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Test getOutputStream()");
		
		ss.getOutputStream().addResultListener(new IResultListener<IOutputConnection>()
		{
			public void resultAvailable(final IOutputConnection con)
			{
//				System.out.println("received ocon: "+con);
				StreamProviderAgent.write(con, agent).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testPassInputStream(int testno, IStreamService ss)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Test passInputStream()");
		
		final ServiceOutputConnection con = new ServiceOutputConnection();
		
		ss.passInputStream(con.getInputConnection()).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));

		StreamProviderAgent.write(con, agent);
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testPassOutputStream(int testno, IStreamService ss)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Test passInputStream()");
		
		ServiceInputConnection con = new ServiceInputConnection();
		
		ss.passOutputStream(con.getOutputConnection()).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
		
		StreamProviderAgent.read(con);
		
		return ret;
	}

}
