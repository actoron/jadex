package jadex.micro.testcases.intermediate;

import java.util.Collection;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class InvokerAgent
{
	@Agent
	protected MicroAgent agent;
	
	@AgentBody
	public void body()
	{
		final Future<Void> ret = new Future<Void>();
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("fini");
			}
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
			
//		testLocal().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("tests finished");
//			}
//		}));
		
//		testRemote().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("tests finished");
//			}
//		}));
		
		testLocal().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				testRemote().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						System.out.println("tests finished");
					}
				}));
			}
		}));
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> testLocal()
	{
		return performTest(agent.getServiceProvider());
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> testRemote()
	{
		final Future<Void> ret = new Future<Void>();
		
		// Start platform
		String url	= "new String[]{\"../jadex-applications-micro/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
		Starter.createPlatform(new String[]{"-platformname", "testi_1", "-libpath", url,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
			"-gui", "false", "-usepass", "false", "-simulation", "false"
//			"-logging_level", "java.util.logging.Level.INFO"
		}).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
				performTest(platform.getServiceProvider()).addResultListener(
					agent.createResultListener(new DelegationResultListener<Void>(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> performTest(IServiceProvider provider)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Start service agent
		IFuture<IComponentManagementService> fut = SServiceProvider.getServiceUpwards(
			provider, IComponentManagementService.class);
		fut.addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.createComponent(null, "jadex/micro/testcases/intermediate/IntermediateResultProviderAgent.class", null, null)
					.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{	
					public void customResultAvailable(IComponentIdentifier cid)
					{
						System.out.println("cid is: "+cid);
						SServiceProvider.getService(agent.getServiceProvider(), cid, IIntermediateResultService.class)
							.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IIntermediateResultService, Void>(ret)
						{
							public void customResultAvailable(IIntermediateResultService service)
							{
								// Invoke service agent
//								System.out.println("Invoking");
								IIntermediateFuture<String> fut = service.getResults();
								fut.addResultListener(agent.createResultListener(new IIntermediateResultListener<String>()
								{
									public void intermediateResultAvailable(String result)
									{
										System.out.println("intermediateResultAvailable: "+result);
									}
									public void finished()
									{
										System.out.println("finished");
										ret.setResult(null);
									}
									public void resultAvailable(Collection<String> result)
									{
										System.out.println("resultAvailable: "+result);
										ret.setResult(null);
									}
									public void exceptionOccurred(Exception exception)
									{
										System.out.println("exceptionOccurred: "+exception);
										ret.setException(exception);
									}
								}));
								System.out.println("Added listener");
							}		
						}));
					}
				}));
			}	
		}));
		
		return ret;
	}
}
