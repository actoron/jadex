package jadex.tools.jcc;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.bridge.IAgentIdentifier;
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
   public void updateAgents(Object[] agents, IAMS ams)
   {
      Vector c = (Vector)clone();
      for(int i=0; i<agents.length; i++)
      {
         IAMSAgentDescription ad = (IAMSAgentDescription)agents[i];
         if(ad!=null)
         {
        	 int idx = c.indexOf(ad);
        	 if(idx!=-1)
        	 {
        		 IAMSAgentDescription old = (IAMSAgentDescription)c.remove(idx);
        		 if(!old.getState().equals(ad.getState()))
        		 {
        			 remove(ad);
        			 IAgentIdentifier	id	= ad.getName();
        			 id	= ams.createAgentIdentifier(id.getName(), false, id.getAddresses());
        			 add(ams.createAMSAgentDescription(id, ad.getState(), ad.getOwnership()));
        			 fireAgentChanged(ad);
        		 }
        	 }
        	 else
        	 {
    			 IAgentIdentifier	id	= ad.getName();
    			 id	= ams.createAgentIdentifier(id.getName(), false, id.getAddresses());
    			 add(ams.createAMSAgentDescription(id, ad.getState(), ad.getOwnership()));
        		 fireNewAgentEvent(ad);
        	 }
         }
      }

      Enumeration e = c.elements();
      while(e.hasMoreElements())
      {
         IAMSAgentDescription agent = (IAMSAgentDescription)e.nextElement();
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
            al.agentBorn((IAMSAgentDescription) get(i));
      }
   }

   /** 
    * @param ad
    */
   protected void fireAgentDied(IAMSAgentDescription ad)
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
   protected void fireNewAgentEvent(IAMSAgentDescription ad)
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
   protected void fireAgentChanged(IAMSAgentDescription ad)
   {
	  //System.out.println("The agent state changed: "+ad);
      Enumeration e = listeners.elements();
      while (e.hasMoreElements())
      {
         ((IAgentListListener)e.nextElement()).agentChanged(ad);
      }
   }

}