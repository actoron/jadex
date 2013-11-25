package jadex.micro.testcases.autoterminate;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.testcases.TestAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  Test automatic termination of subscriptions, when subscriber dies.
 */
@Service
@Agent
@ProvidedServices(@ProvidedService(type=IAutoTerminateService.class))
public class AutoTerminateAgent	extends	TestAgent	implements IAutoTerminateService
{
	//-------- attributes --------
	
	/** The test reports. */
	protected List<TestReport>	reports	= new ArrayList<TestReport>();
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The finished future. */
	protected Future<Void>	ret;
	
	/** The test case. */
	protected Testcase tc;
	
	//-------- methods --------
	
	/**
	 *  Execute the tests.
	 */
	protected IFuture<Void> performTests(Testcase tc)
	{
		ret	= new Future<Void>();
		this.tc	= tc;
		if(SReflect.isAndroid()) 
		{
			tc.setTestCount(1);
		} 
		else 
		{
			tc.setTestCount(3);
		}
		
		setupLocalTest(SubscriberAgent.class.getName()+".class", null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
		{
			public void customResultAvailable(IComponentIdentifier result)
			{
				if(!SReflect.isAndroid()) 
				{
					setupRemoteTest(SubscriberAgent.class.getName()+".class", "self", null, false)
						.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
					{
						public void customResultAvailable(IComponentIdentifier result)
						{
							setupRemoteTest(SubscriberAgent.class.getName()+".class", "platform", null, true);
							// keep future open -> is set in check finished.
						}
					});
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Setup a remote test.
	 */
	protected IFuture<IComponentIdentifier>	setupRemoteTest(final String filename, final String config,
		final IResultListener<Collection<Tuple2<String,Object>>> reslis, final boolean remove)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		
		createPlatform(null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				if(remove)
					platforms.remove(exta);
				
				createProxy(agent.getComponentIdentifier().getRoot(), exta.getComponentIdentifier()).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
						// inverse proxy from remote to local.
						createProxy(exta.getComponentIdentifier(), agent.getComponentIdentifier().getRoot())
							.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
						{
							public void customResultAvailable(IComponentIdentifier result)
							{
								createComponent(filename, null, config, exta.getComponentIdentifier(), reslis)
									.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test subscription.
	 */
	public ISubscriptionIntermediateFuture<String>	subscribe()
	{
		final TestReport	report	= new TestReport("#"+reports.size()+1,
			reports.size()==0 ? "Test local automatic subscription termination: "+ServiceCall.getCurrentInvocation().getCaller()
			: reports.size()==1 ? "Test remote automatic subscription termination: "+ServiceCall.getCurrentInvocation().getCaller()
			: "Test remote offline automatic subscription termination: "+ServiceCall.getCurrentInvocation().getCaller());
		reports.add(report);
		
//		System.out.println("test: "+report.getDescription()+", "+BasicService.DEFAULT_LOCAL);
		
		waitForRealtimeDelay(BasicService.getLocalDefaultTimeout(),
			new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("test1: "+report.getDescription());
				
				if(!report.isSucceeded())
				{
					report.setFailed("Termination did not happen.");
					checkFinished();
				}
				return IFuture.DONE;
			}
		});
		
		final SubscriptionIntermediateFuture<String>	ret	= new SubscriptionIntermediateFuture<String>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
//				System.out.println("test2: "+report.getDescription());
				
				if(report.getReason()==null)
				{
					report.setSucceeded(true);
					checkFinished();
				} 
				else 
				{
					report.setFailed(reason.getMessage());
				}
			}
		});
		
		waitForRealtimeDelay(1000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("test3: "+report.getDescription());
				
				if(ret.addIntermediateResultIfUndone("ping"))
				{
					waitForRealtimeDelay(1000, this);
				}
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	protected void	checkFinished()
	{
		boolean	finished = false;
		if(SReflect.isAndroid()) 
		{
			finished = reports.size()==1 && reports.get(0).isFinished();
		} 
		else 
		{
			finished = reports.size()==3
				&& reports.get(0).isFinished()
				&& reports.get(1).isFinished()
				&& reports.get(2).isFinished();
		}

//		System.out.println("test4: "+reports.size()+", "+finished);

		if(finished)
		{
			tc.setReports(reports.toArray(new TestReport[reports.size()]));
			ret.setResult(null);
		}
	}
}
