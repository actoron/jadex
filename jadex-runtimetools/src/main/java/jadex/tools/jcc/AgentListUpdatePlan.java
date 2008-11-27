package jadex.tools.jcc;


import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;

/**
 *  Update the agent list.
 */
public class AgentListUpdatePlan extends Plan
{
	/**
     *  Plan body.
     */
	public void body()
	{
		ControlCenter ctrl = (ControlCenter) getBeliefbase().getBelief("jcc").getFact();
		
//		waitFor(1000);
//		
//		Object[] agents = getBeliefbase().getBeliefSet("agents").getFacts();
//		if(ctrl != null && agents != null)
//		{
//			ctrl.agentlist.updateAgents(agents, (IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE));
//		}
		
		while(true)
		{
			
//			Object tmp = waitForFactAddedOrRemoved("agents");
//			System.out.println(tmp+" "+((IChangeEvent)tmp).getValue());
//			
//			try
//			{
//				long delay = 300;
//				for(long wait=0; wait<1000; wait+=delay)
//				{
//					tmp = waitForFactAddedOrRemoved("agents" , delay);
//					System.out.println(tmp+" "+((IChangeEvent)tmp).getValue());
//				}
//			}
//			catch(TimeoutException e)
//			{
//				e.printStackTrace();
//			}
			
			// Hack! Plan currently polls agents.
			// Problem with the above code is that it requires that the plans
			// reacts on fact-added events, which currently flood the jcc agent
			// and cause it to slow down when many agents are started.
			
			waitFor(1000);
			
//			System.out.println("refreshing");
			Object[] agents = getBeliefbase().getBeliefSet("agents").getFacts();
			if(ctrl != null && agents != null)
			{
				ctrl.agentlist.updateAgents(agents, (IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE));
			}
		}
	}
	
   /**
    *  Plan body.
    * /
   public void body()
   {
      ControlCenter ctrl = (ControlCenter) getBeliefbase().getBelief("jcc").getFact();
      Object[] agents = getBeliefbase().getBeliefSet("agents").getFacts();
      if(ctrl != null && agents != null)
      {
         ctrl.agentlist.updateAgents(agents, (IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE));
      }
   }*/
}