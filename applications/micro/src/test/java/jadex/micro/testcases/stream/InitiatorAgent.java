package jadex.micro.testcases.stream;

import java.io.InputStream;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@Arguments(replace=false, value=@Argument(name="filename", clazz=String.class, defaultvalue="\"jadex/micro/testcases/stream/test.jpg\""))
//@Arguments(@Argument(name="filename", clazz=String.class, defaultvalue="\"jadex/micro/testcases/stream/android-sdk_r07-windows.zip\""))
public class InitiatorAgent extends TestAgent
{
	/**
	 * 
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.getLogger().severe("Testagent test local: "+agent.getComponentDescription());
		testLocal(1).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				if(SReflect.isAndroid()) 
				{
					tc.setTestCount(1);
					ret.setResult(null);
				} 
				else 
				{
					agent.getLogger().severe("Testagent test remote: "+agent.getComponentDescription());
					testRemote(2).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
					{
						public void customResultAvailable(TestReport result)
						{
							agent.getLogger().severe("Testagent tests finished: "+agent.getComponentDescription());
							tc.addReport(result);
							ret.setResult(null);
						}
					}));
				}
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
		
		setupRemotePlatform(false).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
	        	performTest(exta.getComponentIdentifier(), testno)
	        		.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<TestReport>(ret)));
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
		IResultListener<Map<String, Object>> reslis = new DelegationResultListener<Map<String,Object>>(resfut)
		{
			@Override
			public void customResultAvailable(Map<String, Object> result)
			{
				agent.getLogger().severe("Testagent receiver finished: "+agent.getComponentDescription());
				super.customResultAvailable(result);
			}
			@Override
			public void exceptionOccurred(Exception exception)
			{
				agent.getLogger().severe("Testagent receiver failed: "+agent.getComponentDescription()+", "+exception);
				super.exceptionOccurred(exception);
			}
		};
		
		agent.getLogger().severe("Testagent setup receiver: "+agent.getComponentDescription());
		createComponent("jadex/micro/testcases/stream/ReceiverAgent.class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				agent.getLogger().severe("Testagent setup receiver done: "+agent.getComponentDescription());
				IMessageFeature mf = agent.getComponentFeature(IMessageFeature.class);
				mf.createOutputConnection(agent.getComponentIdentifier(), cid, null)
					.addResultListener(new ExceptionDelegationResultListener<IOutputConnection, TestReport>(ret)
				{
					public void customResultAvailable(final IOutputConnection ocon) 
					{
						sendBehavior(testno, ocon, resfut).addResultListener(new DelegationResultListener<TestReport>(ret));
					}
				});
			}
		});
		
		return res;
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> sendBehavior(int testno, final IOutputConnection con, IFuture<Map<String, Object>> resfut)
	{
		final long start = System.currentTimeMillis();
		final long[] filesize = new long[1];

		final Future<TestReport> ret = new Future<TestReport>();
		
		try
		{
			final InputStream is = SUtil.getResource((String)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("filename"), agent.getClassLoader());
			
			final TestReport tr = new TestReport(""+testno, "Test if file is transferred correctly.");
			
			resfut.addResultListener(new IResultListener<Map<String,Object>>()
			{
				public void resultAvailable(Map<String, Object> results)
				{
//					Long fs = (Long)jadex.bridge.modelinfo.Argument.getResult(results, "filesize");
					Long fs = (Long) results.get("filesize");
					if(fs!=null)
					{
						if(fs.longValue()==filesize[0])
						{
							long end = System.currentTimeMillis();
							System.out.println("Needed "+(end-start)/1000.0+" seconds for "+filesize[0]/1024+" kbytes.");
							tr.setSucceeded(true);
						}
						else
						{
							tr.setFailed("Wrong file size [expected, received]: "+filesize[0]+" "+fs.longValue());
						}
					}
					else
					{
						tr.setFailed("No target file size reported.");
					}
					ret.setResult(tr);
				}
				public void exceptionOccurred(Exception exception)
				{
					tr.setFailed("Receiver agent had exception: "+exception);
					ret.setResult(tr);
				}
			});
			
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						final IComponentStep<Void> self = this;
						int size = Math.min(200000, is.available());
						filesize[0] += size;
						byte[] buf = new byte[size];
						int read = 0;
						while(read!=buf.length)
						{
							read += is.read(buf, read, buf.length-read);
						}
						con.write(buf);
						System.out.println("wrote: "+size);
						if(is.available()>0)
						{
							con.waitForReady().addResultListener(new IResultListener<Integer>()
							{
								public void resultAvailable(Integer result)
								{
									agent.getComponentFeature(IExecutionFeature.class).scheduleStep(self);
//									agent.getComponentFeature(IExecutionFeature.class).waitForDelay(10, self);
								}
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
									con.close();
								}
							});
						}
						else
						{
							con.close();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						ret.setException(e);
					}
					
					return IFuture.DONE;
				}
			};
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step);
//			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
	}
}
