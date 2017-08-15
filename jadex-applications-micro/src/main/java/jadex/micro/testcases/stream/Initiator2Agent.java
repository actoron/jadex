package jadex.micro.testcases.stream;

import java.util.Map;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
//@RequiredServices(
//{
//	@RequiredService(name="msgservice", type=IMessageService.class, 
//		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
//	@RequiredService(name="cms", type=IComponentManagementService.class, 
//		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
//})
//@ComponentTypes(
//	@ComponentType(name="receiver", filename="jadex/micro/testcases/stream/Receiver2Agent.class")
//)
public class Initiator2Agent extends TestAgent
{
	protected IInputConnection icon;
	
	/**
	 * 
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						tc.addReport(result);
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
	protected IFuture<TestReport> testLocal(int testno)
	{
		return performTest(agent.getComponentIdentifier().getRoot(), testno);
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testRemote(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				Starter.createProxy(agent.getExternalAccess(), exta).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
						// inverse proxy from remote to local.
						Starter.createProxy(exta, agent.getExternalAccess())
							.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
						{
							public void customResultAvailable(IComponentIdentifier result)
							{
			                   	performTest(exta.getComponentIdentifier(), testno)
	                    		.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  - start a receiver agent
	 *  - create connection
	 */
	protected IFuture<TestReport> performTest(final IComponentIdentifier root, final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if streams work");
				tr.setFailed(exception);
				super.resultAvailable(tr);
			}
		});
		
		final Future<Map<String, Object>> resfut = new Future<Map<String, Object>>();
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut);
		
		createComponent("jadex/micro/testcases/stream/Receiver2Agent.class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				IMessageFeature mf = agent.getComponentFeature(IMessageFeature.class);
				mf.createInputConnection(agent.getComponentIdentifier(), cid, null)
					.addResultListener(new ExceptionDelegationResultListener<IInputConnection, TestReport>(ret)
				{
					public void customResultAvailable(final IInputConnection icon) 
					{
						receiveBehavior(testno, icon, resfut).addResultListener(new DelegationResultListener<TestReport>(ret)
						{
							public void customResultAvailable(final TestReport tr)
							{
								destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, TestReport>(ret)
								{
									public void customResultAvailable(Map<String,Object> result) 
									{
										ret.setResult(tr);
									}
								});
							}
						});
					}
				});
			}
		});
		
		return res;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> receiveBehavior(int testno, final IInputConnection con, IFuture<Map<String, Object>> resfut)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport(""+testno, "Test if file is transferred correctly.");
		StreamProviderAgent.read(con).addResultListener(new TestReportListener(tr, ret, Receiver2Agent.getNumberOfBytes()));
			
		return ret;
	}
	
}

