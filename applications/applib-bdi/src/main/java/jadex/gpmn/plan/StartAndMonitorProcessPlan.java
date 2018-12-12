package jadex.gpmn.plan;

/**
 *  Create a process and the corresponding parameters.
 */
public class StartAndMonitorProcessPlan //extends Plan
{
//	/**
//	 *  Plan body.
//	 */
//	public void body()
//	{
//		final String[] agoalnames = (String[])getParameterSet("goals").getValues();
//		String[] mgoalnames = (String[])getParameterSet("maintain_goals").getValues();
//
//		List mgoals = new ArrayList();
//		for(int i=0; i<mgoalnames.length; i++)
//		{
//			IGoal goal = createGoal(mgoalnames[i]);
//			dispatchSubgoal(goal);
//			mgoals.add(goal);
//		}
//			
//		final SyncResultListener lis = new SyncResultListener();
//		IGoalListener listener = new IGoalListener()
//		{
//			protected int goalcnt = 0;
//			public void goalFinished(AgentEvent ae)
//			{
//				IGoal goal = (IGoal)ae.getSource();
//				if(goal.isSucceeded())
//				{
//					goalcnt++;
//					if(goalcnt==agoalnames.length)
//						lis.resultAvailable(null);
//				}
//				else
//				{
//					lis.exceptionOccurred(goal.getException());
//				}
//			}
//			
//			public void goalAdded(AgentEvent ae)
//			{
//				
//			}
//		};
//		
//		for(int i=0; i<agoalnames.length; i++)
//		{
//			System.out.println("Creating Goal: "+agoalnames[i]);
//			IGoal goal = createGoal(agoalnames[i]);
//			goal.addGoalListener(listener);
//			dispatchSubgoal(goal);
//		}
//		try
//		{
//			lis.waitForResult();
//		}
//		catch(Exception e)
//		{
//			//TODO: Ignore?
//		}
//		
//		IGoal mviolated = null;
//		do
//		{
//			for(int i=0; i<mgoals.size() && mviolated==null; i++)
//			{
//				IGoal goal = (IGoal)mgoals.get(i);
//				if(!goal.isSucceeded())
//					mviolated = goal;
//			}
//			if(mviolated!=null)
//			{
//				waitForGoal(mviolated);
//				mviolated = null;
//			}
//		}
//		while(mviolated!=null);
//		
//		killAgent();
//	}
//	
//	public void passed()
//	{
//		System.out.println("Passed: "+this+" "+SUtil.arrayToString(getParameterSet("achieve_goals").getValues())
//			+SUtil.arrayToString(getParameterSet("maintain_goals").getValues()));
//	}
//	public void failed()
//	{
//		System.out.println("Failed: "+this+" "+SUtil.arrayToString(getParameterSet("achieve_goals").getValues())
//			+SUtil.arrayToString(getParameterSet("maintain_goals").getValues())+", "+getException());
//	}
//	public void aborted()
//	{
//		System.out.println("Aborted: "+this+" "+SUtil.arrayToString(getParameterSet("achieve_goals").getValues())
//			+SUtil.arrayToString(getParameterSet("maintain_goals").getValues()));
//	}
}
