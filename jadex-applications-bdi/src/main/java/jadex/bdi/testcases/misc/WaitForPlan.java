package jadex.bdi.testcases.misc;

import jadex.base.fipa.SFipa;
import jadex.base.test.TestReport;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;

import java.util.Arrays;

/**
 *  Test various waitFor methods first from plan, then from external access.
 */
public class WaitForPlan extends Plan	implements Runnable
{
	/** Boolean that indicates if the thread is finished. */
	//boolean thread_finished;

	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
//		Thread t = new Thread(this);
//		t.start();
//		waitFor(40000000000000L);
		
		TestReport	report	= new TestReport("time", "Waiting for 100 ms.");
		waitFor(100);
		report.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		//int i=0;
		//System.out.println(i++);
		
		/*
		report	= new TestReport("ticktime", "Waiting for tick.");
		long delta = getClock().getDelta();
		waitForTick();
		long start = getTime();
		waitForTick();
		long end = getTime();
		if(Math.abs(end-start-delta)<30)
			report.setSucceeded(true);
		else
			report.setFailed("Tick wait times error: dur="+(end-start)+", delta="+delta);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		//System.out.println(i++);
		 */
		
		report	= new TestReport("beliefchange", "Waiting for belief 'time' to change.");
		long oldt = ((Long)getBeliefbase().getBelief("time").getFact()).longValue();
		try
		{
			waitForFactChanged("time", 2000);
			long newt = ((Long)getBeliefbase().getBelief("time").getFact()).longValue();
			if(newt!=oldt)
				report.setSucceeded(true);
			else
				report.setReason("No change in belief detected.");
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		//System.out.println(i++);
		
		report	= new TestReport("beliefsetadd", "Waiting for addition in beliefset 'belset'.");
		try
		{
			Object[] oldfacts = getBeliefbase().getBeliefSet("belset").getFacts();
			waitForFactAdded("belset", 2000);
			Object[] newfacts = getBeliefbase().getBeliefSet("belset").getFacts();
			if(!Arrays.equals(oldfacts, newfacts))
				report.setSucceeded(true);
			else
				report.setReason("No addition in beliefset detected: "+oldfacts+" "+newfacts);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("beliefsetremove", "Waiting for removal in beliefset 'belset'.");
		try
		{
			Object[] oldfacts = getBeliefbase().getBeliefSet("belset").getFacts();
			waitForFactRemoved("belset", 2000);
			Object[] newfacts = getBeliefbase().getBeliefSet("belset").getFacts();
			if(!Arrays.equals(oldfacts, newfacts))
				report.setSucceeded(true);
			else
				report.setReason("No removal in beliefset detected: "+oldfacts+" "+newfacts);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		
//		report	= new TestReport("condition", "Waiting for a condition to become true.");
//		oldt = ((Long)getBeliefbase().getBelief("time").getFact()).longValue();
//		try
//		{
//			waitForCondition("$beliefbase.time>"+(oldt+100)+"L", 3000);
//			long newt = ((Long)getBeliefbase().getBelief("time").getFact()).longValue();
//			if(newt>oldt+100)
//				report.setSucceeded(true);
//			else
//				report.setReason("Condition does not hold.");
//		}
//		catch(TimeoutException e)
//		{
//			report.setReason("Timeout occurred.");
//		}
//		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
//		//System.out.println(i++);
		
//		report	= new TestReport("truecondition", "Waiting for a condition that is initially true.");
//		try
//		{
//			waitForCondition("true", 1000);
//			report.setSucceeded(true);
//		}
//		catch(TimeoutException e)
//		{
//			report.setReason("Timeout occurred.");
//		}
//		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
//		//System.out.println(i++);
		
		report	= new TestReport("goal", "Waiting for a goal to complete.");
		IGoal goal = getGoalbase().createGoal("test");
		try
		{
			dispatchSubgoalAndWait(goal, 1000);
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		//System.out.println(i++);
		
		report	= new TestReport("message", "Waiting for a message reply.");
		IMessageEvent me = createMessageEvent("default_query_ping");
		me.getParameterSet(SFipa.RECEIVERS).addValue(getScope().getComponentIdentifier());
		try
		{
			sendMessageAndWait(me, 2000);
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		//System.out.println(i++);
		
		report	= new TestReport("timeout", "Waiting for a timeout.");
		try
		{
			IMessageEvent rep = waitForMessageEvent("default_query_ping", 1000);
			report.setReason("Received message: "+rep);
		}
		catch(TimeoutException e)
		{
			report.setSucceeded(true);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		//System.out.println(i++);
		
		// Test external access.
//		getExternalAccess().startSynchronizedExternalThread(this);
		
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * The run method is called from the external thread.
	 */
	public void run()
	{
		TestReport	report	= new TestReport("x-time", "Waiting for external 100 ms.");
		getExternalAccess().waitFor(100);
		report.setSucceeded(true);
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		/*
		report	= new TestReport("x-ticktime", "Waiting for tick.");
		long delta = getClock().getDelta();
		getExternalAccess().waitForTick();
		long start = getTime();
		getExternalAccess().waitForTick();
		long end = getTime();
		if(Math.abs(end-start-delta)<30)
			report.setSucceeded(true);
		else
			report.setFailed("Tick wait times error: dur="+(end-start)+", delta="+delta);
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		*/
		
		report	= new TestReport("x-beliefchange", "Waiting for external belief 'time' to change.");
		try
		{
			// It can happen that we are just before the belief change scheduled.
			// Therefore we have to ensure that we are in a fresh period.
			getExternalAccess().waitForFactChanged("time", 1000);

			long oldt = ((Long)getExternalAccess().getBeliefbase().getBelief("time").getFact()).longValue();
			getExternalAccess().waitForFactChanged("time", 2000);
			long newt = ((Long)getExternalAccess().getBeliefbase().getBelief("time").getFact()).longValue();
			if(newt!=oldt)
				report.setSucceeded(true);
			else
				report.setReason("No change in belief detected: "+oldt+" "+newt);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("x-beliefsetadd", "Waiting for external addition in beliefset 'belset'.");
		try
		{
			Object[] oldfacts = getExternalAccess().getBeliefbase().getBeliefSet("belset").getFacts();
			getExternalAccess().waitForFactAdded("belset", 2000);
			Object[] newfacts = getExternalAccess().getBeliefbase().getBeliefSet("belset").getFacts();
			if(!Arrays.equals(oldfacts, newfacts))
				report.setSucceeded(true);
			else
				report.setReason("No add in beliefset detected: "+oldfacts+" "+newfacts);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("x-beliefsetremove", "Waiting for external removal in beliefset 'belset'.");
		try
		{
			Object[] oldfacts = getExternalAccess().getBeliefbase().getBeliefSet("belset").getFacts();
			getExternalAccess().waitForFactRemoved("belset", 2000);
			Object[] newfacts = getExternalAccess().getBeliefbase().getBeliefSet("belset").getFacts();
			if(!Arrays.equals(oldfacts, newfacts))
				report.setSucceeded(true);
			else
				report.setReason("No removal in beliefset detected: "+oldfacts+" "+newfacts);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		
		
//		report	= new TestReport("x-condition", "Waiting for a condition to become true.");
//		long oldt = ((Long)getExternalAccess().getBeliefbase().getBelief("time").getFact()).longValue();
//		try
//		{
//			getExternalAccess().waitForCondition("$beliefbase.time>"+(oldt+100)+"L", 3000);
//			long newt = ((Long)getExternalAccess().getBeliefbase().getBelief("time").getFact()).longValue();
//			if(newt>oldt+100)
//				report.setSucceeded(true);
//			else
//				report.setReason("Condition does not hold.");
//		}
//		catch(TimeoutException e)
//		{
//			report.setReason("Timeout occurred.");
//		}
//		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

//		report	= new TestReport("x-truecondition", "Waiting for a condition that is initially true.");
//		try
//		{
//			getExternalAccess().waitForCondition("true", 1000);
//			report.setSucceeded(true);
//		}
//		catch(TimeoutException e)
//		{
//			report.setReason("Timeout occurred.");
//		}
//		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("x-goal", "Waiting for an external goal to complete.");
		IGoal goal = getExternalAccess().getGoalbase().createGoal("test");
		try
		{
			getExternalAccess().dispatchTopLevelGoalAndWait(goal, 1000);
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		catch(Exception e)
		{
			report.setReason("Exception occurred.");
		}
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

		report	= new TestReport("x-message", "Waiting for an external message reply.");
		IMessageEvent me = getExternalAccess().createMessageEvent("default_query_ping");
		me.getParameterSet(SFipa.RECEIVERS).addValue(getExternalAccess().getComponentIdentifier());
		try
		{
			getExternalAccess().sendMessageAndWait(me, 1000);
			report.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			report.setReason("Timeout occurred.");
		}
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		report	= new TestReport("x-timeout", "Waiting for an external timeout.");
		try
		{
			IMessageEvent rep = getExternalAccess().waitForMessageEvent("default_query_ping", 1000);
			report.setReason("Received message: "+rep);
		}
		catch(TimeoutException e)
		{
			report.setSucceeded(true);
		}
		getExternalAccess().getBeliefbase().getBeliefSet("testcap.reports").addFact(report);

//		System.err.println("thread end");
		//getExternalAccess().removeSynchronizedExternalThread(Thread.currentThread());
		//thread_finished = true;
	}
}
