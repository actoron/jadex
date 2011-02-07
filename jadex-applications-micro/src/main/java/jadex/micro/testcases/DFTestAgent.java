package jadex.micro.testcases;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.base.fipa.SFipa;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Test DF usage from micro agent.
 *  @author Dirk, Alex
 */
public class DFTestAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The reports of executed tests, used as result. */
	protected List	reports;
	
	//-------- methods --------
	
	/**
	 *  At startup register the agent at the DF.
	 */
	public void executeBody()
	{
		this.reports	= new ArrayList();
		registerDF();
	}
	
	/**
	 *  Called when agent finishes.
	 */
	public void agentKilled()
	{
		// Store test results.
		setResultValue("testresults", new Testcase(reports.size(), (TestReport[])reports.toArray(new TestReport[reports.size()])));

		// Deregister agent.
		SServiceProvider.getService(getServiceProvider(), IDF.class).addResultListener(
			createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IDF df = (IDF)result;
				IDFComponentDescription ad = df.createDFComponentDescription(getComponentIdentifier(), null);
				df.deregister(ad);
			}
		}));
	}
	
	/**
	 *  Register the agent at the DF.
	 */
	protected void registerDF()
	{
		final TestReport tr	= new TestReport("#1", "Test DF registration.");
		reports.add(tr);

		SServiceProvider.getService(getServiceProvider(), IDF.class).addResultListener(
			createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IDF df = (IDF)result;
				IDFServiceDescription sd = df.createDFServiceDescription(null, "testType", null);
				IDFComponentDescription ad = df.createDFComponentDescription(getComponentIdentifier(), sd);

				IFuture ret = df.register(ad); 
				ret.addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						// Set test success and continue test.
						tr.setSucceeded(true);
						searchDF();
					}
					
					public void exceptionOccurred(Exception e)
					{
						// Set test failure and kill agent.
						tr.setFailed(e.toString());
						killAgent();
					}
				}));
			}
		}));
	}
	
	/**
	 *  Search for the agent at the DF.
	 */
	protected  void searchDF()
	{
		final TestReport	tr	= new TestReport("#2", "Test DF search.");
		reports.add(tr);

		// Create a service description to search for.
		SServiceProvider.getService(getServiceProvider(), IDF.class).addResultListener(
			createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IDF df = (IDF)result;
				IDFServiceDescription sd = df.createDFServiceDescription(null, "testType", null);
				IDFComponentDescription ad = df.createDFComponentDescription(null, sd);
				ISearchConstraints	cons = df.createSearchConstraints(-1, 0);
				
				IFuture ret = df.search(ad, cons); 
				ret.addResultListener(createResultListener(new IResultListener() 
				{
					public void resultAvailable(Object result)
					{
						IDFComponentDescription[] agentDesc = (IDFComponentDescription[])result;
						if(agentDesc.length != 0)
						{
							// Set test success and continue test.
							tr.setSucceeded(true);
							IComponentIdentifier receiver = agentDesc[0].getName();
							sendMessageToReceiver(receiver);
						}
						else
						{
							// Set test failure and kill agent.
							tr.setFailed("No suitable service found.");
							killAgent();
						}
					}
					
					public void exceptionOccurred(Exception e)
					{
						// Set test failure and kill agent.
						tr.setFailed(e.toString());
						killAgent();
					}
				}));
			}
		}));
	}
	
	private void sendMessageToReceiver(IComponentIdentifier cid)
	{
		final TestReport	tr	= new TestReport("#3", "Test sending message to service (i.e. myself).");
		reports.add(tr);

		Map hlefMessage = new HashMap();
		hlefMessage.put(SFipa.PERFORMATIVE, SFipa.INFORM);
		hlefMessage.put(SFipa.SENDER, getComponentIdentifier());
		hlefMessage.put(SFipa.RECEIVERS, cid);
		hlefMessage.put(SFipa.CONTENT, "testMessage");
		
		sendMessage(hlefMessage, SFipa.FIPA_MESSAGE_TYPE);
		
		waitFor(1000, new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				// Set test failure and kill agent.
				tr.setFailed("No message received.");
				killAgent();
				return null;
			}
		});
	}
	
	public void messageArrived(Map msg, MessageType mt)
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
		killAgent();
	}

	
	/**
	 *  Add the 'testresults' marking this agent as a testcase. 
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("Test DF usage from micro agent.", 
			null, null, new IArgument[]{new Argument("testresults", null, "Testcase")});
	}
}
