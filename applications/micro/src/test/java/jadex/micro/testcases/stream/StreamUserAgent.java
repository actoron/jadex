package jadex.micro.testcases.stream;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@RequiredServices(@RequiredService(name="ss", type=IStreamService.class, scope=RequiredServiceInfo.SCOPE_GLOBAL))
public class StreamUserAgent extends TestAgent
{
	/**
	 *  The test count.
	 */
	protected int getTestCount()
	{
		return 8;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
//		IFuture<IStreamService> fut = agent.getServiceContainer().getService("ss");
//		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
//		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);

		testLocal(1, tc).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
		{
			public void customResultAvailable(Integer testcnt)
			{
//				testRemote(testcnt.intValue(), tc, false).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
				testRemote(1, tc, false).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
				{
					public void customResultAvailable(Integer testcnt)
					{
//						testRemote(testcnt.intValue(), tc, true).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
//						{
//							public void customResultAvailable(Integer result)
//							{
								ret.setResult(null);
//							}
//						}));
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test the local case.
	 */
	protected IFuture<Integer> testLocal(int testno, Testcase tc)
	{
		return performTests(testno, agent.getId().getRoot(), tc);
	}
	
	/**
	 *  Test the remote case.
	 */
	protected IFuture<Integer> testRemote(final int testno, final Testcase tc, final boolean sec)
	{
		final Future<Integer> ret = new Future<Integer>();
		
		setupRemotePlatform(/*sec ? new String[]{"-ssltcptransport", "true"} : null*/false).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, Integer>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
//				ComponentIdentifier.getTransportIdentifier(platform).addResultListener(new ExceptionDelegationResultListener<ITransportComponentIdentifier, Integer>(ret)
//				{
//					public void customResultAvailable(ITransportComponentIdentifier result) 
//					{
//						if(!sec)
//						{
							performTests(testno, platform.getId(), tc)
								.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Integer>(ret)
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
//						}
//						else
//						{
//							performSecureTests(testno, platform.getComponentIdentifier(), tc)
//								.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Integer>(ret)
//							{
//								public void customResultAvailable(final Integer result)
//								{
//									platform.killComponent();
//			//							.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
//			//						{
//			//							public void customResultAvailable(Map<String, Object> v)
//			//							{
//			//								ret.setResult(result);
//			//							}
//			//						});
//									ret.setResult(result);
//								}
//							}));
//						}
//					}
//				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Integer> performTests(int testcnt, IComponentIdentifier root, final Testcase tc)
	{
		final Future<Integer> ret = new Future<Integer>();
		
		final int[] cnt = new int[]{testcnt};
		
		createComponent(StreamProviderAgent.class.getName()+".class", root, null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Integer>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IStreamService.class).setProvider(cid))
					.addResultListener(new ExceptionDelegationResultListener<IStreamService, Integer>(ret)
				{
					public void customResultAvailable(final IStreamService ss)
					{
						testGetInputStream(cnt[0]++, ss).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
						{
							public void customResultAvailable(TestReport result)
							{
								tc.addReport(result);
								testGetOutputStream(cnt[0]++, ss).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
								{
									public void customResultAvailable(TestReport result)
									{
										tc.addReport(result);
										testPassInputStream(cnt[0]++, ss).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
										{
											public void customResultAvailable(TestReport result)
											{
												tc.addReport(result);
												testPassOutputStream(cnt[0]++, ss).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
												{
													public void customResultAvailable(TestReport result)
													{
														tc.addReport(result);
														destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Integer>(ret)
														{
															public void customResultAvailable(Map<String,Object> result) 
															{
																ret.setResult(Integer.valueOf(cnt[0]));
															}
														});
													}
												}));
											}
										}));
									}
								}));
							}
						}));
					}
				});
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Perform the secure tests.
//	 */
//	protected IFuture<Integer> performSecureTests(int testcnt, IComponentIdentifier root, final Testcase tc)
//	{
//		final Future<Integer> ret = new Future<Integer>();
//		
//		final int[] cnt = new int[]{testcnt};
//		
//		createComponent(StreamProviderAgent.class.getName()+".class", root, null)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Integer>(ret)
//		{
//			public void customResultAvailable(final IComponentIdentifier cid) 
//			{
//				System.out.println("created: "+cid+" by "+agent.getComponentIdentifier());
//				agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( cid, IStreamService.class))
//					.addResultListener(new ExceptionDelegationResultListener<IStreamService, Integer>(ret)
//				{
//					public void customResultAvailable(final IStreamService ss)
//					{
//						testSecureGetInputStream(cnt[0]++, ss).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
//						{
//							public void customResultAvailable(TestReport result)
//							{
//								tc.addReport(result);
//								testSecureGetOutputStream(cnt[0]++, ss).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
//								{
//									public void customResultAvailable(TestReport result)
//									{
//										tc.addReport(result);
//										testSecurePassInputStream(cnt[0]++, ss).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
//										{
//											public void customResultAvailable(TestReport result)
//											{
//												tc.addReport(result);
//												testSecurePassOutputStream(cnt[0]++, ss).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Integer>(ret)
//												{
//													public void customResultAvailable(TestReport result)
//													{
//														tc.addReport(result);
//														destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Integer>(ret)
//														{
//															public void customResultAvailable(Map<String,Object> result) 
//															{
//																ret.setResult(Integer.valueOf(cnt[0]));
//															}
//														});
//													}
//												}));
//											}
//										}));
//									}
//								}));
//							}
//						}));
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Test 'getInputStream()'.
	 */
	protected IFuture<TestReport> testGetInputStream(int testno, IStreamService ss)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport("#"+testno, "Test getInputStream()");
		
		ss.getInputStream().addResultListener(new IResultListener<IInputConnection>()
		{
			public void resultAvailable(IInputConnection con)
			{
				System.out.println("received icon: "+con);
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
	 *  Test 'getOututStream()'.
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
	 *  Test 'passInputStream()'.
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
	 *  Test 'passOututStream()'.
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
	
	
	
//	/**
//	 *  Test 'getSecureInputStream()'.
//	 */
//	protected IFuture<TestReport> testSecureGetInputStream(int testno, IStreamService ss)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		final TestReport tr = new TestReport("#"+testno, "Test getSecureInputStream()");
//		
//		ss.getSecureInputStream().addResultListener(new IResultListener<IInputConnection>()
//		{
//			public void resultAvailable(IInputConnection con)
//			{
//				System.out.println("received icon: "+con);//.getNonFunctionalProperties());
//				Map<String, Object> props = con.getNonFunctionalProperties();
//				Boolean sec = props!=null? (Boolean)props.get(SecureTransmission.SECURE_TRANSMISSION): null;
//				if(sec==null || !sec.booleanValue())
//				{
//					tr.setFailed("Received unsecure input stream in 'getSecureInputStream'");
//					ret.setResult(tr);
//				}
//				else
//				{
//					StreamProviderAgent.read(con).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
////				System.out.println("ex: "+exception);
//				tr.setFailed("Exception: "+exception.getMessage());
//				ret.setResult(tr);
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Test 'getSecureOututStream()'.
//	 */
//	protected IFuture<TestReport> testSecureGetOutputStream(int testno, IStreamService ss)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		final TestReport tr = new TestReport("#"+testno, "Test getSecureOutputStream()");
//		
//		ss.getSecureOutputStream().addResultListener(new IResultListener<IOutputConnection>()
//		{
//			public void resultAvailable(final IOutputConnection con)
//			{
//				System.out.println("received ocon: "+con.getNonFunctionalProperties());
//				Map<String, Object> props = con.getNonFunctionalProperties();
//				Boolean sec = props!=null? (Boolean)props.get(SecureTransmission.SECURE_TRANSMISSION): null;
//				if(sec==null || !sec.booleanValue())
//				{
//					tr.setFailed("Received unsecure output stream in 'getSecureOutputStream'");
//					ret.setResult(tr);
//				}
//				else
//				{
//					StreamProviderAgent.write(con, agent).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
////				System.out.println("ex: "+exception);
//				ret.setException(exception);
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Test 'passSecureInputStream()'.
//	 */
//	protected IFuture<TestReport> testSecurePassInputStream(int testno, IStreamService ss)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		final TestReport tr = new TestReport("#"+testno, "Test passSecureInputStream()");
//		
//		final ServiceOutputConnection con = new ServiceOutputConnection();
//		
//		ss.passSecureInputStream(con.getInputConnection()).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
//
//		StreamProviderAgent.write(con, agent);
//		
//		return ret;
//	}
//	
//
//	/**
//	 *  Test 'passSecureOututStream()'.
//	 */
//	protected IFuture<TestReport> testSecurePassOutputStream(int testno, IStreamService ss)
//	{
//		final Future<TestReport> ret = new Future<TestReport>();
//		final TestReport tr = new TestReport("#"+testno, "Test passSecureInputStream()");
//		
//		ServiceInputConnection con = new ServiceInputConnection();
//		
//		ss.passSecureOutputStream(con.getOutputConnection()).addResultListener(new TestReportListener(tr, ret, StreamProviderAgent.getWriteLength()));
//		
//		StreamProviderAgent.read(con);
//		
//		return ret;
//	}

}
