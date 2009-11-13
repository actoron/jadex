package jadex.tools.testcenter;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.planlib.test.TestReport;
import jadex.bdi.planlib.test.Testcase;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.IComponentIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 *  Perform one testcase.
 */
public class PerformTestPlan extends Plan
{
	//-------- attributes --------
	
	/** The created test agent. */
	protected IComponentIdentifier	testagent;
	
	//-------- methods --------
	
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		Testcase testcase = (Testcase)getParameter("testcase").getValue();
		ClassLoader classloader = (ClassLoader)getParameter("classloader").getValue();
		Long timeout = (Long)getBeliefbase().getBelief("timeout").getFact();

		getLogger().info("Performing testcase: "+testcase.getType());
		long	starttime	= getTime();

		try
		{
			// Create test agent without starting.
			IGoal create = createGoal("ams_create_agent");
			create.getParameter("type").setValue(testcase.getType());
			Map	args	= new HashMap();
			args.put("timeout", timeout);
			args.put("testcenter", this.getComponentIdentifier());
			create.getParameter("arguments").setValue(args);
			create.getParameter("classloader").setValue(classloader);
			create.getParameter("start").setValue(Boolean.FALSE);
			dispatchSubgoalAndWait(create);
			this.testagent	= (IComponentIdentifier)create.getParameter("agentidentifier").getValue();

			// Hack!!! convention agent2testcenter used for convid to allow matching.
			IMessageEvent	dummy	= createMessageEvent("inform_reports");	// Hack??? Need some conversation message to wait for
			dummy.getParameter(SFipa.CONVERSATION_ID).setValue(testagent.getLocalName()+"2"+getComponentIdentifier().getLocalName());
			getWaitqueue().addReply(dummy);
			
			IGoal start = createGoal("ams_start_agent");
			start.getParameter("agentidentifier").setValue(testagent);
			dispatchSubgoalAndWait(start);
			
			try
			{
				IMessageEvent	ans	= (IMessageEvent)waitForReply(dummy, timeout.longValue());
				Object	content	= ans.getParameter(SFipa.CONTENT).getValue();
//				if(content instanceof Testcase)
				{
					//getLogger().info("Test results are: "+result);
					Testcase	result	= (Testcase)content;
					testcase.setTestCount(result.getTestCount());
					testcase.setReports(result.getReports());
				}
//				else
//				{
//					jadex.bdi.planlib.test.Testcase	result	= (jadex.bdi.planlib.test.Testcase)content;
//					testcase.setTestCount(result.getTestCount());
//					TestReport[]	reports	= new TestReport[result.getReports().length];
//					for(int i=0; i<reports.length; i++)
//						reports[i]	= new TestReport(
//							result.getReports()[i].getName(),
//							result.getReports()[i].getDescription(),
//							result.getReports()[i].isSucceeded(),
//							result.getReports()[i].getReason());
//					testcase.setReports(reports);
//				}
				
			}
			catch(TimeoutException te)
			{
				IGoal destroy = createGoal("ams_destroy_agent");
				destroy.getParameter("agentidentifier").setValue(testagent);
				try
				{
					dispatchSubgoalAndWait(destroy);
				}
				catch(GoalFailureException ge)
				{
					getLogger().info("Test agent could not be deleted.");
				}
				testagent	= null;
				//getLogger().info("Test agent failed. No answer received.");
				testcase.setReports(new TestReport[]{new TestReport("answer", 
					"Test center report", false, "Test agent did not answer.")});
			}
		}
		catch(GoalFailureException ge)
		{
			//getLogger().info("Test agent could not be created.");
			Throwable	cause	= ge;
			while(cause.getCause()!=null && cause.getCause()!=cause)
				cause	= cause.getCause();
			testcase.setReports(new TestReport[]{new TestReport("creation", "Test center report", 
				false, "Test agent could not be created: "+cause)});
		}
		testcase.setDuration(getTime()-starttime);
	}
	
	/**
	 *  When plan is aborted, kill created agent.
	 */
	public void aborted()
	{
		if(testagent!=null)
		{
			IGoal destroy = createGoal("ams_destroy_agent");
			destroy.getParameter("agentidentifier").setValue(testagent);
			try
			{
				dispatchSubgoalAndWait(destroy);
			}
			catch(GoalFailureException ge)
			{
				getLogger().info("Test agent could not be deleted.");
			}
		}
	}
}
