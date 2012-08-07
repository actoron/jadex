/**
 * 
 */
package jadex.base.gui.modeltree;

import jadex.bridge.IExternalAccess;

import javax.swing.JTree;

/**
 *  Interface for tree abstraction used in generic add/remove path actions.
 */
public interface ITreeAbstraction
{
	/**
	 *  Test if action is called remotely.
	 *  @return True, if is local.
	 */
	public boolean isRemote();
	
	/**
	 *  Get the underlying JTree.
	 *  @return The tree.
	 */
	public JTree getTree();
	
	/**
	 *  Get the external access of the functionality.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess();
	
	/**
	 *  Get the external access of the gui.
	 *  @return The external access.
	 */
	public IExternalAccess getGUIExternalAccess();
	
	/**
	 *  Called to perform the specific add/remove action.
	 *  @param obj The node on which the action should be performed.
	 */
	public void action(Object obj);
}
