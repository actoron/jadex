package jadex.tools.jcc;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;

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
      Object[] agents = getBeliefbase().getBeliefSet("agents").getFacts();
      if(ctrl != null && agents != null)
      {
         ctrl.agentlist.updateAgents(agents, (IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE));
      }
   }
}