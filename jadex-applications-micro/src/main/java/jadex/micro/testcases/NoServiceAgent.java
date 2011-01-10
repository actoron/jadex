package jadex.micro.testcases;

import java.util.Collections;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.SServiceProvider;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  Test searching for services that don't exist. 
 */
public class NoServiceAgent extends MicroAgent
{
	public void executeBody()
	{
		final TestReport	tr	= new TestReport("#1", "Searching for services.");
		
		SServiceProvider.getServices(getServiceProvider(), INoService.class).addResultListener(
			createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(Collections.EMPTY_LIST.equals(result))
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("Expected empty list but was: "+result);
				}
				setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
				killAgent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed("Exception during test: "+exception);
				setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
				killAgent();
			}
		}));
	}
	
	/** Test service interface. */
	public static interface	INoService	extends IService {}
	
	/**
	 *  Add the 'testresults' marking this agent as a testcase. 
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("Test searching for services that don't exist.", 
			null, null, new IArgument[]{new Argument("testresults", null, "Testcase")});
	}
}
