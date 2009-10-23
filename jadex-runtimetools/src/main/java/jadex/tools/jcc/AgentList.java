package jadex.tools.jcc;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.tools.common.plugin.IAgentListListener;

import java.util.Enumeration;
import java.util.Vector;

/**
 *  Update the agent list.
 */
public class AgentList extends Vector
{
   private final Vector listeners = new Vector();

   /**
    *  Plan body.
    * @param agents
    */
   public void updateAgents(Object[] agents, IComponentExecutionService ces)
   {
      Vector c = (Vector)clone();
      for(int i=0; i<agents.length; i++)
      {
         IComponentDescription ad = (IComponentDescription)agents[i];
         if(ad!=null)
         {
        	 int idx = c.indexOf(ad);
        	 if(idx!=-1)
        	 {
        		 IComponentDescription old = (IComponentDescription)c.remove(idx);
        		 if(!old.getState().equals(ad.getState()))
        		 {
        			 remove(ad);
        			 IComponentIdentifier	id	= ad.getName();
        			 id	= ces.createComponentIdentifier(id.getName(), false, id.getAddresses());
        			 add(ces.createComponentDescription(id, ad.getState(), ad.getOwnership()));
        			 fireAgentChanged(ad);
        		 }
        	 }
        	 else
        	 {
    			 IComponentIdentifier	id	= ad.getName();
    			 id	= ces.createComponentIdentifier(id.getName(), false, id.getAddresses());
    			 add(ces.createComponentDescription(id, ad.getState(), ad.getOwnership()));
        		 fireNewAgentEvent(ad);
        	 }
         }
      }

      Enumeration e = c.elements();
      while(e.hasMoreElements())
      {
         IComponentDescription agent = (IComponentDescription)e.nextElement();
         remove(agent);
         fireAgentDied(agent);
      }
   }

   /**
    * 
    * @param al
    */
   void addListener(IAgentListListener al)
   {
      if(!listeners.contains(al))
      {
         listeners.add(al);
         for(int i=0; i<size(); i++)
            al.agentBorn((IComponentDescription) get(i));
      }
   }

   /** 
    * @param ad
    */
   protected void fireAgentDied(IComponentDescription ad)
   {
      if(ad!=null) 
      {
         Enumeration e = listeners.elements();
         while (e.hasMoreElements())
         {
            ((IAgentListListener) e.nextElement()).agentDied(ad);
         }
      }
   }

   /** 
    * @param ad
    */
   protected void fireNewAgentEvent(IComponentDescription ad)
   {
      Enumeration e = listeners.elements();
      while (e.hasMoreElements())
      {
         ((IAgentListListener)e.nextElement()).agentBorn(ad);
      }
   }
   
   /** 
    * @param ad
    */
   protected void fireAgentChanged(IComponentDescription ad)
   {
	  //System.out.println("The agent state changed: "+ad);
      Enumeration e = listeners.elements();
      while (e.hasMoreElements())
      {
         ((IAgentListListener)e.nextElement()).agentChanged(ad);
      }
   }

}