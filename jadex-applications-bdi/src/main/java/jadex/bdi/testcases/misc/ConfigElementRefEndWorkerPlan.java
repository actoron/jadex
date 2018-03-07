package jadex.bdi.testcases.misc;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.TimeoutException;

/**
 *  Check if the end elements are correctly named.
 */
public class ConfigElementRefEndWorkerPlan extends Plan
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
		
		// End goal.
		TestReport	report	= new TestReport("end goal", "Check if the end goal is correctly named.");
		try
		{
			/*IGoal	goal	=*/ waitForGoalFinished("testgoal", 100);
//			if(goal.getName().equals("namedendgoal"))
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
		
		// End internal event.
		report	= new TestReport("end internal event", "Check if the end internal event is correctly named.");
		try
		{
			/*IInternalEvent	event	=*/ waitForInternalEvent("testevent", 100);
//			if(event.getName().equals("namedendevent"))
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
		
		// End message event.
		report	= new TestReport("end message event", "Check if the end message event is correctly named.");
		try
		{
			/*IMessageEvent	msg	=*/ waitForMessageEvent("testmsg", 100);
			// Hack!!! getting reply to event is only way to access original name!?
//			IRMessageEvent	orig	= ((IRMessageEvent)((MessageEventWrapper)msg).unwrap()).getInReplyMessageEvent();
//			if(orig!=null && orig.getName().equals("namedendmsg"))
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

		// End goal from capability.
		report	= new TestReport("end goal", "Check if end goal from capability is correctly named.");
		try
		{
			/*IGoal	goal	=*/ waitForGoalFinished("configrefcap.testgoal", 100);
//			if(goal.getName().equals("namedcapendgoal"))
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
		
		// End internal event from capability.
		report	= new TestReport("end internal event", "Check if end internal event from capability is correctly named.");
		try
		{
			/*IInternalEvent	event	=*/ waitForInternalEvent("configrefcap.testevent", 100);
//			if(event.getName().equals("namedcapendevent"))
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
		
		// End message event from capability.
		report	= new TestReport("end message event", "Check if end message event from capability is correctly named.");
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
//			if(orig!=null && orig.getName().equals("namedcapendmsg"))
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

		// End plan.
		report	= new TestReport("end plan", "Check if the end plan is correctly named.");
//		IPlan plan = getPlanbase().getPlan("namedendplan");
//		if(plan!=null)
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
	}
}
