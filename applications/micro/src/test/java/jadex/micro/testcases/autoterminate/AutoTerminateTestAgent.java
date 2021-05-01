package jadex.micro.testcases.autoterminate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.InitInfo;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Test automatic termination of subscriptions, when subscriber dies.
 */
@Service
@Agent
@ProvidedServices(@ProvidedService(type=IAutoTerminateService.class, scope=ServiceScope.GLOBAL))
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 4)")}) // cannot use $component.getId() because is extracted from test suite :-(
public class AutoTerminateTestAgent extends	TestAgent implements IAutoTerminateService
{
	//-------- attributes --------
	
	/** The test reports. */
	protected List<TestReport> reports = new ArrayList<TestReport>();
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
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
		tc.setTestCount(3);
		
		InitInfo ii = SComponentManagementService.getState(agent.getId().getRoot()).getComponent(agent.getId()).getInitInfo();
		if(ii!=null)
			System.out.println("ii: "+ii.getInitFuture().isDone());
		
//		agent.getLogger().severe("Testagent test local: "+agent.getComponentDescription());
		setupLocalTest(SubscriberAgent.class.getName()+".class", null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
		{
			public void customResultAvailable(IComponentIdentifier result)
			{
//					agent.getLogger().severe("Testagent test remote1: "+agent.getComponentDescription());
				setupRemoteTest(SubscriberAgent.class.getName()+".class", "self", null, false)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
//							agent.getLogger().severe("Testagent test remote2: "+agent.getComponentDescription());
						setupRemoteTest(SubscriberAgent.class.getName()+".class", "platform", null, true);
						// keep future open -> is set in check finished.
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
		final TestReport report	= new TestReport("#"+reports.size()+1,
			reports.size()==0 ? "Test local automatic subscription termination: "+ServiceCall.getCurrentInvocation().getCaller()
			: reports.size()==1 ? "Test remote automatic subscription termination: "+ServiceCall.getCurrentInvocation().getCaller()
			: "Test remote offline automatic subscription termination: "+ServiceCall.getCurrentInvocation().getCaller());
		reports.add(report);
		
//		agent.getLogger().severe("test: "+report.getDescription()+", "+Starter.getDefaultTimeout(agent.getComponentIdentifier()));
		
		agent.waitForDelay(Starter.getScaledDefaultTimeout(agent.getId(), 1.25),
			new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
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
//				agent.getLogger().severe("test2: "+report.getDescription());
				
				if(report.getReason()==null)
				{
					report.setSucceeded(true);
				} 
				else 
				{
					report.setFailed(reason.getMessage());
				}
				checkFinished();
			}
		});
		
		// sending ping every second (by default)
		agent.waitForDelay(Starter.getScaledDefaultTimeout(agent.getId(), 1.0/30), new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(ret.addIntermediateResultIfUndone("ping"))
				{
					agent.waitForDelay(Starter.getScaledDefaultTimeout(agent.getId(), 1.0/30), this);
				}
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	protected void	checkFinished()
	{
		boolean	finished = reports.size()==tc.getTestCount();
		for(TestReport report: reports)
		{
			finished = finished && report.isFinished();
		}

//		agent.getLogger().severe("test4: "+reports.size()+", "+finished);

		if(finished)
		{
			tc.setReports(reports.toArray(new TestReport[reports.size()]));
//			agent.getLogger().severe("test5: "+tc);
			System.out.println("Auto terminate result: "+tc);
			ret.setResult(null);
		}
	}
	
	/**
	 *  Setup a local test.
	 */
	protected IFuture<IComponentIdentifier>	setupLocalTest(String filename,  IResultListener<Map<String,Object>> reslis)
	{
		return createComponent(filename, agent.getId().getRoot(), reslis);
	}
	
	/**
	 *  Setup a remote test.
	 */
	protected IFuture<IComponentIdentifier>	setupRemoteTest(final String filename, final String config,
		final  IResultListener<Map<String,Object>> reslis, final boolean remove)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		
		setupRemotePlatform(remove)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
//				ComponentIdentifier.getTransportIdentifier(exta)
//					.addResultListener(new ExceptionDelegationResultListener<ITransportComponentIdentifier, IComponentIdentifier>(ret)
//                {
//                    public void customResultAvailable(ITransportComponentIdentifier cid)
//                    {
						createComponent(filename, null, config, exta.getId(), exta, reslis)
							.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//                    }
//                });
			}
		});
		
		return ret;
	}
	
	/**
	 *  Starter for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		// Start platform with agent.
		IPlatformConfiguration	config1	= PlatformConfigurationHandler.getMinimal();
		config1.getExtendedPlatformConfiguration().setSecurity(true);
		config1.getExtendedPlatformConfiguration().setTcpTransport(true);
		config1.getExtendedPlatformConfiguration().setCli(true);
//		config1.addComponent(UserAgent.class);
		for (int i = 0; i < 100; ++i)
		{
			System.out.println("======================= Try: " + i);
			IExternalAccess plat = Starter.createPlatform(config1).get();
			plat.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ia.createComponent(new CreationInfo().setFilename(AutoTerminateTestAgent.class.getCanonicalName() + ".class")).get().waitForTermination().get();
					System.out.println("Step done.");
					return IFuture.DONE;
				}
			}).get();
			plat.killComponent().get();
			System.out.println("DONE TRY ==================");
			SUtil.sleep(500);
		}
		System.out.println("Done.");
		System.exit(0);
	}

	
	/**
	 *  Starter for testing.
	 */
	public static void mainx(String[] args) throws Exception
	{
		// Start platform with agent.
		IPlatformConfiguration	config1	= PlatformConfigurationHandler.getMinimal();
//		config1.setLogging(true);
//		config1.setDefaultTimeout(-1);
		config1.getExtendedPlatformConfiguration().setSecurity(true);
//		config1.setAwaMechanisms(AWAMECHANISM.local);
//		config1.setAwareness(true);
		config1.getExtendedPlatformConfiguration().setTcpTransport(false);
		config1.getExtendedPlatformConfiguration().setWsTransport(true);
		config1.addComponent(AutoTerminateTestAgent.class);
		Starter.createPlatform(config1).get();
	}
}
