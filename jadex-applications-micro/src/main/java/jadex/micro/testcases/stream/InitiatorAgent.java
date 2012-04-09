package jadex.micro.testcases.stream;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.KillAgent;

import java.io.InputStream;
import java.util.Collection;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="msgservice", type=IMessageService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cms", type=IComponentManagementService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
//@ComponentTypes(
//	@ComponentType(name="receiver", filename="jadex/micro/testcases/stream/ReceiverAgent.class")
//)
//@Arguments(@Argument(name="filename", clazz=String.class, defaultvalue="\"jadex/micro/testcases/stream/test.jpg\""))
@Arguments(@Argument(name="filename", clazz=String.class, defaultvalue="\"jadex/micro/testcases/stream/android-sdk_r07-windows.zip\""))
@Results(@Result(name="testresults", clazz=Testcase.class))

public class InitiatorAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(2);
		
		final Future<TestReport> ret = new Future<TestReport>();
		ret.addResultListener(agent.createResultListener(new IResultListener<TestReport>()
		{
			public void resultAvailable(TestReport result)
			{
//				System.out.println("tests finished");

				agent.setResultValue("testresults", tc);
				agent.killAgent();				
			}
			public void exceptionOccurred(Exception exception)
			{
				agent.setResultValue("testresults", tc);
				agent.killAgent();	
			}
		}));
			
//		testLocal(1).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
//		{
//			public void customResultAvailable(TestReport result)
//			{
//				tc.addReport(result);
//				ret.setResult(null);
//				agent.killAgent();
//			}
//		}));
		
//		testRemote(2).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
//		{
//			public void customResultAvailable(TestReport result)
//			{
//				tc.addReport(result);
//				ret.setResult(null);
//				agent.killAgent();
//			}
//		}));
		
		testLocal(1).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2).addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						tc.addReport(result);
						ret.setResult(null);
					}
				}));
			}
		}));
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
		
		// Start platform
		String url	= "new String[]{\"../jadex-applications-micro/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
//		Starter.createPlatform(new String[]{"-platformname", "testi_1", "-libpath", url,
		Starter.createPlatform(new String[]{"-libpath", url,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
//			"-logging_level", "java.util.logging.Level.INFO",
			"-gui", "false", "-usepass", "false", "-simulation", "false"
		}).addResultListener(agent.createResultListener(
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
				TestReport tr = new TestReport("#"+testno, "Tests if intermediate results work");
				tr.setReason(exception.getMessage());
				super.resultAvailable(tr);
			}
		});
		
		IFuture<IComponentManagementService> cmsfut = SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		cmsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
				IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);
				
				// "receiver" cannot use parent due to remote case new CreationInfo(agent.getComponentIdentifier())
				IResourceIdentifier	rid	= new ResourceIdentifier(
					new LocalResourceIdentifier(root, agent.getModel().getResourceIdentifier().getLocalIdentifier().getUrl()), null);
				cms.createComponent(null, "jadex/micro/testcases/stream/ReceiverAgent.class", new CreationInfo(rid), reslis)
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
	
//	/**
//	 * 
//	 */
//	public void sendBehavior(final IOutputConnection con)
//	{
//		final IComponentStep<Void> step = new IComponentStep<Void>()
//		{
//			final int[] cnt = new int[]{1};
//			final int max = 100;
//			final IComponentStep<Void> self = this;
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				byte[] tosend = new byte[cnt[0]];
//				for(int i=0; i<cnt[0]; i++)
//					tosend[i] = (byte)cnt[0];
//				con.write(tosend).addResultListener(new IResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//						if(cnt[0]++<max)
//						{
//							agent.waitFor(200, self);
//						}
//						else
//						{
//	//						ocon.close();
//	//						ret.setResult(null);
//						}
//					}
//					public void exceptionOccurred(Exception exception)
//					{
//						System.out.println("Write failed: "+exception);
//					}
//				});
//				return IFuture.DONE;
//			}
//		};
//		agent.waitFor(200, step);
//	}
	
//	/**
//	 * 
//	 */
//	@AgentBody
//	public IFuture<Void> body()
//	{
//		final Future<Void> ret = new Future<Void>();
//		IFuture<IComponentManagementService> cmsfut = agent.getServiceContainer().getRequiredService("cms");
//		cmsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
//				cms.createComponent(null, "receiver", new CreationInfo(agent.getComponentIdentifier()), null)
//					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
//				{
//					public void customResultAvailable(final IComponentIdentifier cid) 
//					{
//						IFuture<IMessageService> msfut = agent.getServiceContainer().getRequiredService("msgservice");
//						msfut.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
//						{
//							public void customResultAvailable(IMessageService ms)
//							{
//								ms.createOutputConnection(agent.getComponentIdentifier(), cid)
//									.addResultListener(new ExceptionDelegationResultListener<IOutputConnection, Void>(ret)
//								{
//									public void customResultAvailable(final IOutputConnection ocon) 
//									{
//										sendBehavior(ocon);
//									}
//								});
//							}
//						});
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}
}
