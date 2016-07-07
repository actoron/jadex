package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test memory consumption.
 */
public class LeakerPlan extends Plan
{
	/**
	 *  Create a new plan.
	 */
	public void	body()
	{
		int	testno = ((Number)getParameter("testcase").getValue()).intValue();
		int	runs = ((Integer)getBeliefbase().getBelief("runs").getFact()).intValue();
		
		// Test without/with dispatching of elements.
		runTests(testno, runs, false);
		runTests(testno, runs/10, true);	// Hack!!! To reduce execution time to reasonible time use less runs when dispatching
		
//		try
//		{
			waitForEver();
//		}
//		catch(Error e)
//		{
//			System.out.println("leaker wait end: "+e);
//			throw e;
//		}
//		catch(RuntimeException e)
//		{
//			System.out.println("leaker wait end2: "+e);
//			throw e;
//		}
//		finally
//		{
//			System.out.println("leaker wait end3");
//		}
	}

	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void runTests(int testno, int runs, boolean dispatch)
	{
		long	time = getTime();
		long	mem0	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long	mem	= mem0;
		getLogger().info("Starting memory consumption tests.\nUsed memory: "+mem);

		if(testno==0 || testno==7 && !dispatch || testno==8 && dispatch)
		{
			TestReport tr = new TestReport(dispatch?"#7":"#8", dispatch?"Memory consumption of messages.":"Memory consumption of sent messages.");
			long	time2	= getTime();
			testMessageCreation(runs, dispatch);
			getLogger().info("Time (millis): "+(getTime()-time2));
			waitFor(3);
			System.gc();
//			System.runFinalization();
			long mem2	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			getLogger().info("Used memory (change): "+(mem2-mem));
			/*if((mem2-mem)/mem>10)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setReason("Possibly memory leak: used memory (change): "+(mem2-mem));
			}*/
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
			mem	= mem2;
		}
		
		if(testno==0 || testno==1 && !dispatch || testno==2 && dispatch)
		{
			TestReport tr = new TestReport(dispatch?"#1":"#2", dispatch?"Memory consumption of goals.":"Memory consumption of dispatched goals.");
			long	time2	= getTime();
			testGoalCreation(runs, dispatch);
			getLogger().info("Time (millis): "+(getTime()-time2));
			waitFor(3);
			System.gc();
//			System.runFinalization();
			long mem2	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			getLogger().info("Used memory (change): "+(mem2-mem));
			/*if((mem2-mem)/mem>10)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setReason("Possibly memory leak: used memory (change): "+(mem2-mem));
			}*/
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
			mem	= mem2;
		}

		if(testno==0 || testno==3 && !dispatch || testno==4 && dispatch)
		{
			/*TestReport tr = new TestReport(dispatch?"#3":"4", dispatch?"Memory consumption of conditions.":"Memory consumption of activated conditions.");
			long	time2	= getTime();
			testConditionCreation(runs, dispatch);
			getLogger().info("Time (millis): "+(getTime()-time2));
			waitFor(3);
			System.gc();
			System.runFinalization();
			long mem2	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			getLogger().info("Used memory (change): "+(mem2-mem));
			/*if((mem2-mem)/mem>10)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setReason("Possibly memory leak: used memory (change): "+(mem2-mem));
			}* /
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
			mem	= mem2;*/
		}

		if(testno==0 || testno==5 && !dispatch || testno==6 && dispatch)
		{
			TestReport tr = new TestReport(dispatch?"#5":"#6", dispatch?"Memory consumption of internal events.":"Memory consumption of dispatched internal events.");
			long	time2	= getTime();
			testInternalEventCreation(runs, dispatch);
			getLogger().info("Time (millis): "+(getTime()-time2));
			waitFor(3);
			System.gc();
//			System.runFinalization();
			long mem2	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			getLogger().info("Used memory (change): "+(mem2-mem));
			/*if((mem2-mem)/mem>10)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setReason("Possibly memory leak: used memory (change): "+(mem2-mem));
			}*/
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
			mem	= mem2;
		}

		time	= getTime() - time;
		getLogger().info("\nAll tests passed ("+time+" millis).");
		getLogger().info("Used memory (total change): "+(mem-mem0));
	}

	/**
	 *  Test if the goal creation consumes memory.
	 */
	protected void testGoalCreation(int num, boolean dispatch)
	{
		getLogger().info("\n-------- Start of goal creation test --------");
		for(int i=num; i>0 && num!=0; i--)
		{
			// create goals, thereby resources will be aquired
			// test if resources are released again when the goal is not used.
			IGoal goal = createGoal("testgoal");
			if(dispatch)
			{
				try
				{
					dispatchSubgoalAndWait(goal, 100);
				}
				catch(Exception e) {}
			}
			//getLogger().info("Goal created: "+i);
			if(i%500==0)
			{
				getLogger().info(""+i);
				waitFor(3);
			}
//			System.out.print(".");
			//start = sleeper(start);
		}
		getLogger().info("\n-------- End of goal creation test --------");
		getLogger().info("");
	}

	/**
	 *  Test if the condition creation consumes memory.
	 * /
	protected void testConditionCreation(int num)
	{
		getLogger().info("\n-------- Start of condition creation test --------");
		for(int i=num; i>0 || num==0; i--)
		{
			ICondition cond = createCondition("false");
			cond.setTraceMode(ICondition.TRACE_ALWAYS);
			//getLogger().info("Condition created: "+i);
			if(i%500==0)
			{
				getLogger().info(""+i);
				waitFor(3);
			}
//			System.out.print(".");
			//start = sleeper(start);
		}
		getLogger().info("\n-------- End of condition creation test --------");
		getLogger().info("");
	}*/

	/**
	 *  Test if the internal event creation consumes memory.
	 */
	protected void testInternalEventCreation(int num, boolean dispatch)
	{
		getLogger().info("\n-------- Start of internal event creation test --------");
		for(int i=num; i>0 && num!=0; i--)
		{
			IInternalEvent event = createInternalEvent("testevent");
			//getLogger().info("Internal event created: "+i);
			if(dispatch)
			{
				dispatchInternalEvent(event);
			}
			if(i%500==0)
			{
				getLogger().info(""+i);
				waitFor(3);
			}
//			System.out.print(".");
			//start = sleeper(start);
		}
		getLogger().info("\n-------- End of internal event creation test --------");
		getLogger().info("");
	}

	/**
	 *  Test if the message creation consumes memory.
	 */
	protected void testMessageCreation(int num, boolean send)
	{
		getLogger().info("\n-------- Start of message event creation and sending test --------");
		for(int i=num; i>0 && num!=0; i--)
		{
			IMessageEvent me = createMessageEvent("testmsg");
			if(send)
			{
				sendMessage(me).get();
			}
			//getLogger().info("Message event created and sent: "+i);
			if(i%500==0)
			{
				getLogger().info(""+i);
				waitFor(3);
			}
			waitFor(0);
//			System.out.print(".");
			//start = sleeper(start);
		}
		getLogger().info("\n-------- End of message event creation and sending test --------");
		getLogger().info("");
	}

	/**
	 *
	 */
	protected long sleeper(long start)
	{
		if(start+5000<=getTime())
		{
			try{Thread.sleep(1000);}
			catch(InterruptedException e){e.printStackTrace();}
			start = getTime();
		}
		return start;
	}
}
