package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test DF usage from micro agent.
 *  @author Dirk, Alex
 */
@Description("Test DF usage from micro agent.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
public class DFTestAgent extends JunitAgentTest
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	/** The reports of executed tests, used as result. */
	protected List<TestReport>	reports;
	
	//-------- methods --------
	
	/**
	 *  At startup register the agent at the DF.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		this.reports	= new ArrayList<TestReport>();
		return registerDF();
	}
	
	/**
	 *  Called when agent finishes.
	 */
	@AgentKilled
	public IFuture<Void>	agentKilled()
	{
		final Future<Void>	ret	= new Future<Void>();
		// Store test results.
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(reports.size(), (TestReport[])reports.toArray(new TestReport[reports.size()])));

		// Deregister agent.
		// Todo: use fix component service container
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IDF.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IDF, Void>(ret)
		{
			public void customResultAvailable(IDF df)
			{
				IDFComponentDescription ad = df.createDFComponentDescription(agent.getComponentIdentifier(), null);
				df.deregister(ad).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return IFuture.DONE;
	}
	
	/**
	 *  Register the agent at the DF.
	 */
	protected IFuture<Void> registerDF()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport tr	= new TestReport("#1", "Test DF registration.");
		reports.add(tr);

		//agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IDF.class, RequiredServiceInfo.SCOPE_PLATFORM))  
			.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<IDF>()
		{
			public void resultAvailable(IDF df)
			{
				IDFServiceDescription sd = df.createDFServiceDescription(null, "testType", null);
				IDFComponentDescription ad = df.createDFComponentDescription(agent.getComponentIdentifier(), sd);

				IFuture<IDFComponentDescription> re = df.register(ad); 
				re.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IDFComponentDescription>()
				{
					public void resultAvailable(IDFComponentDescription result)
					{
						// Set test success and continue test.
						tr.setSucceeded(true);
						searchDF().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
					}
					
					public void exceptionOccurred(Exception e)
					{
						// Set test failure and kill agent.
						tr.setFailed(e.toString());
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Search for the agent at the DF.
	 */
	protected  IFuture<Void> searchDF()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport	tr	= new TestReport("#2", "Test DF search.");
		reports.add(tr);

		// Create a service description to search for.
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IDF.class, RequiredServiceInfo.SCOPE_PLATFORM))  
			.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<IDF>()
		{
			public void resultAvailable(IDF df)
			{
				IDFServiceDescription sd = df.createDFServiceDescription(null, "testType", null);
				IDFComponentDescription ad = df.createDFComponentDescription(null, sd);
				ISearchConstraints	cons = df.createSearchConstraints(-1, 0);
				
				IFuture<IDFComponentDescription[]> re = df.search(ad, cons); 
				re.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IDFComponentDescription[]>() 
				{
					public void resultAvailable(IDFComponentDescription[] agentDesc)
					{
						if(agentDesc.length != 0)
						{
							// Set test success and continue test.
							tr.setSucceeded(true);
							IComponentIdentifier receiver = agentDesc[0].getName();
							sendMessageToReceiver(receiver).addResultListener(new DelegationResultListener<Void>(ret));
						}
						else
						{
							// Set test failure and kill agent.
							tr.setFailed("No suitable service found.");
							agent.killComponent();
							ret.setResult(null);
						}
					}
					
					public void exceptionOccurred(Exception e)
					{
						// Set test failure and kill agent.
						tr.setFailed(e.toString());
//						killAgent();
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	private IFuture<Void> sendMessageToReceiver(IComponentIdentifier cid)
	{
		final TestReport	tr	= new TestReport("#3", "Test sending message to service (i.e. myself).");
		reports.add(tr);

		Map<String, Object> hlefMessage = new HashMap<String, Object>();
		hlefMessage.put(SFipa.PERFORMATIVE, SFipa.INFORM);
		hlefMessage.put(SFipa.SENDER, agent.getComponentIdentifier());
		hlefMessage.put(SFipa.RECEIVERS, cid);
		hlefMessage.put(SFipa.CONTENT, "testMessage");
		
		agent.getComponentFeature(IMessageFeature.class).sendMessage(hlefMessage, agent.getComponentIdentifier())
			.addResultListener(new IResultListener<Void>()
		{
			@Override
			public void resultAvailable(Void result)
			{
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed(exception);
				agent.killComponent();
			}
		});
		
//		return agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				// Set test failure and kill agent.
//				tr.setFailed("No message received.");
//				return IFuture.DONE;
//			}
//		});
		
		// todo: set body future?!
		return new Future<Void>();
	}
	
	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg)
	{
		TestReport	tr	= (TestReport)reports.get(reports.size()-1);
		
		if("testMessage".equals(msg.get(SFipa.CONTENT)))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong message received: "+msg);
		}

		// All tests done.
		agent.killComponent();
	}

	/**
	 *  Add df service to config.
	 */
	@Override
	public IPlatformConfiguration getConfig()
	{
		super.getConfig().getExtendedPlatformConfiguration().setDf(true);
		return super.getConfig();
	}
	
//	/**
//	 *  Add the 'testresults' marking this agent as a testcase. 
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("Test DF usage from micro agent.", 
//			null, null, new IArgument[]{new Argument("testresults", null, "Testcase")});
//	}
}
