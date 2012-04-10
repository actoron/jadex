package jadex.micro.testcases.stream;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.io.InputStream;
import java.util.Collection;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@Arguments(replace=false, value=@Argument(name="filename", clazz=String.class, defaultvalue="\"jadex/micro/testcases/stream/test.jpg\""))
public class InitiatorAgent extends TestAgent
{
	/**
	 * 
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
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
		return performTest(agent.getServiceProvider(), agent.getComponentIdentifier().getRoot(), testno);
	}
	
	/**
	 * 
	 */
	protected IFuture<TestReport> testRemote(final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		createPlatform().addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getServiceProvider(), platform.getComponentIdentifier(), testno)
					.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(final TestReport result)
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
	 *  Perform the test. Consists of the following steps:
	 *  - start a receiver agent
	 *  - create connection
	 */
	protected IFuture<TestReport> performTest(final IServiceProvider provider, final IComponentIdentifier root, final int testno)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if streams work");
				tr.setReason(exception.getMessage());
				super.resultAvailable(tr);
			}
		});
		
		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);
		
		createComponent(provider, "jadex/micro/testcases/stream/ReceiverAgent.class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				IFuture<IMessageService> msfut = agent.getServiceContainer().getRequiredService("msgservice");
				msfut.addResultListener(new ExceptionDelegationResultListener<IMessageService, TestReport>(ret)
				{
					public void customResultAvailable(IMessageService ms)
					{
						ms.createOutputConnection(agent.getComponentIdentifier(), cid)
							.addResultListener(new ExceptionDelegationResultListener<IOutputConnection, TestReport>(ret)
						{
							public void customResultAvailable(final IOutputConnection ocon) 
							{
								sendBehavior(testno, ocon, resfut).addResultListener(new DelegationResultListener<TestReport>(ret));
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
	public IFuture<TestReport> sendBehavior(int testno, final IOutputConnection con, IFuture<Collection<Tuple2<String, Object>>> resfut)
	{
		final long start = System.currentTimeMillis();
		final long[] filesize = new long[1];

		final Future<TestReport> ret = new Future<TestReport>();
		
		try
		{
			final InputStream is = SUtil.getResource((String)agent.getArgument("filename"), agent.getClassLoader());
			
			final TestReport tr = new TestReport(""+testno, "Test if file is transferred correctly.");
			
			resfut.addResultListener(new IResultListener<Collection<Tuple2<String,Object>>>()
			{
				public void resultAvailable(Collection<Tuple2<String, Object>> results)
				{
					Long fs = (Long)jadex.bridge.modelinfo.Argument.getResult(results, "filesize");
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
						int size = Math.min(200000, is.available());
						filesize[0] += size;
						byte[] buf = new byte[size];
						int read = 0;
						while(read!=buf.length)
						{
							read += is.read(buf);
						}
						con.write(buf);
//						System.out.println("wrote: "+size);
						if(is.available()>0)
						{
							agent.scheduleStep(this);
//							agent.waitFor(10, this);
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
			agent.scheduleStep(step);
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
