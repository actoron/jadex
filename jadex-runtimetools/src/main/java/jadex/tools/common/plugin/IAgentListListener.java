package jadex.tools.common.plugin;

import jadex.adapter.base.fipa.IAMSAgentDescription;


/**
 *  Interface for plugins to be informed about agent list changes.
 */
public interface IAgentListListener
{

   /** 
    * @param ad
    */
   void agentDied(IAMSAgentDescription ad);

   /** 
    * @param ad
    */
   void agentBorn(IAMSAgentDescription ad);
   
   /** 
    * @param ad
    */
   void agentChanged(IAMSAgentDescription ad);

}
