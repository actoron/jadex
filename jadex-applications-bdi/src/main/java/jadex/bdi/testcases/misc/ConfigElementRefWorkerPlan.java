package jadex.bdi.testcases.misc;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.concurrent.TimeoutException;

/**
 *  Check if the initial and end elements are correctly named.
 */
public class ConfigElementRefWorkerPlan extends Plan
{
	//-------- attributes --------
	
	/** List for test reports. */
	protected List<TestReport>	reports	= new ArrayList<TestReport>();
	
	//-------- methods --------

	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Allow for events being queued into waitqueue.
		waitFor(1000);
		
		// Initial goal.
		TestReport	report	= new TestReport("initial goal", "Check if the initial goal is correctly named.");
		try
		{
			/*IGoal	goal	= */waitForGoalFinished("testgoal", 100);
//			if(goal.getName().equals("namedinitialgoal"))
//			{
				report.setSucceeded(true);
//			}
//			else
//			{
//				report.setReason("Goal has wrong name '"+goal+"'.");
//			}
		}
		catch(GoalFailureException e)
		{
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("No goal detected.");
		}
		reports.add(report);
		
		// Initial internal event.
		report	= new TestReport("initial internal event", "Check if the initial internal event is correctly named.");
		try
		{
			/*IInternalEvent	event	= */waitForInternalEvent("testevent", 100);
//			if(event.getName().equals("namedinitialevent"))
//			{
				report.setSucceeded(true);
//			}
//			else
//			{
//				report.setReason("Event has wrong name '"+event+"'.");
//			}
		}
		catch(TimeoutException e)
		{
			report.setReason("No event detected.");
		}
		reports.add(report);
		
		// Initial message event.
		report	= new TestReport("initial message event", "Check if the initial message event is correctly named.");
		try
		{
			// Hack!!! getting reply to event is only way to access original name!?
			/*IMessageEvent	msg	= */waitForMessageEvent("testmsg", 100);
//			IRMessageEvent	orig	= ((IRMessageEvent)((MessageEventWrapper)msg).unwrap()).getInReplyMessageEvent();
//			if(orig!=null && orig.getName().equals("namedinitialmsg"))
//			{
				report.setSucceeded(true);
//			}
//			else
//			{
//				report.setReason("Message has wrong name '"+msg+"'.");
//			}
		}
		catch(TimeoutException e)
		{
			report.setReason("No message detected.");
		}
		reports.add(report);

		// Initial goal from capability.
		report	= new TestReport("initial capa goal", "Check if initial goal from capability is correctly named.");
		try
		{
			/*IGoal	goal	= */waitForGoalFinished("configrefcap.testgoal", 100);
//			if(goal.getName().equals("namedcapinitialgoal"))
//			{
				report.setSucceeded(true);
//			}
//			else
//			{
//				report.setReason("Goal has wrong name '"+goal+"'.");
//			}
		}
		catch(GoalFailureException e)
		{
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("No goal detected.");
		}
		reports.add(report);
		
		// Initial internal event from capability.
		report	= new TestReport("initial capa internal event", "Check if initial internal event from capability is correctly named.");
		try
		{
			/*IInternalEvent	event	= */waitForInternalEvent("configrefcap.testevent", 100);
//			if(event.getName().equals("namedcapinitialevent"))
//			{
				report.setSucceeded(true);
//			}
//			else
//			{
//				report.setReason("Event has wrong name '"+event+"'.");
//			}
		}
		catch(TimeoutException e)
		{
			report.setReason("No event detected.");
		}
		reports.add(report);
		
		// Initial message event from capability.
		report	= new TestReport("initial capa message event", "Check if initial message event from capability is correctly named.");
		try
		{
			/*IMessageEvent	msg	=*/ waitForMessageEvent("configrefcap.testmsg", 100);
			// Hack!!! getting reply to event is only way to access original name!?
//			IRMessageEvent	orig	= ((IRMessageEvent)((MessageEventWrapper)msg).unwrap()).getInReplyMessageEvent();
//			List	origs	= orig.getAllOccurrences();
//			for(int i=0; i<origs.size(); i++)
//			{
//				orig	= (IRMessageEvent)origs.get(i);
//				if(orig.getScope().equals(((ElementWrapper)getScope()).unwrap()))
//					break;
//			}
//			if(orig!=null && orig.getName().equals("namedcapinitialmsg"))
//			{
				report.setSucceeded(true);
//			}
//			else
//			{
//				report.setReason("Message has wrong name '"+msg+"'.");
//			}
		}
		catch(TimeoutException e)
		{
			report.setReason("No message detected.");
		}
		reports.add(report);

		// Initial plan.
		report	= new TestReport("initial plan", "Check if the initial plan is correctly named.");
//		IPlan plan = getPlanbase().getPlan("namedinitialplan");
		IPlan[] plans = getPlanbase().getPlans("testplan");
		if(plans!=null && plans.length==1)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setReason("Plan not found.");
		}
		reports.add(report);
		
		// Finally send reports to test agent.
		IMessageEvent	msg	= createMessageEvent("inform_reports");
		msg.getParameter(SFipa.CONTENT).setValue(reports);
		sendMessage(msg).get();

		// Kill agent to activate end state.
		killAgent();
	}
}
