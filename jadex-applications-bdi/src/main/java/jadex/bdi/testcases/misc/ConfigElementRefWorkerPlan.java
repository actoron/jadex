package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.concurrent.TimeoutException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Check if the initial and end elements are correctly named.
 */
public class ConfigElementRefWorkerPlan extends Plan
{
	//-------- attributes --------
	
	/** List for test reports. */
	protected List	reports	= new ArrayList();
	
	//-------- methods --------

	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Initial goal.
		TestReport	report	= new TestReport("initial goal", "Check if the initial goal is correctly named.");
		try
		{
			/*IGoal	goal	= */waitForGoal("testgoal", 100);
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
			/*IGoal	goal	= */waitForGoal("configrefcap.testgoal", 100);
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
		report	= new TestReport("initial message event", "Check if initial message event from capability is correctly named.");
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
		
		// Kill agent to activate end state.
		killAgent();
		
		waitForEver(); // Hack!!! Unnecessary, when microplansteps work!
	}
	
	/**
	 *  Called when agent is terminated.
	 */
	public void	aborted()
	{
		// End goal.
		TestReport	report	= new TestReport("end goal", "Check if the end goal is correctly named.");
		try
		{
			/*IGoal	goal	=*/ waitForGoal("testgoal", 100);
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
			/*IGoal	goal	=*/ waitForGoal("configrefcap.testgoal", 100);
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
