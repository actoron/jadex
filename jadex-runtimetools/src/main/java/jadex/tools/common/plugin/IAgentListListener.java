package jadex.tools.common.plugin;

import jadex.bridge.IComponentDescription;


/**
 *  Interface for plugins to be informed about agent list changes.
 */
public interface IAgentListListener
{

   /** 
    * @param ad
    */
   void agentDied(IComponentDescription ad);

   /** 
    * @param ad
    */
   void agentBorn(IComponentDescription ad);
   
   /** 
    * @param ad
    */
   void agentChanged(IComponentDescription ad);

}
