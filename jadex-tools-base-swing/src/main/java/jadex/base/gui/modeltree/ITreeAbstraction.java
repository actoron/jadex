/**
 * 
 */
package jadex.base.gui.modeltree;

import jadex.bridge.IExternalAccess;

import javax.swing.JTree;

/**
 *
 */
public interface ITreeAbstraction
{
	/**
	 * 
	 */
	public boolean isRemote();
	
	/**
	 * 
	 */
	public JTree getTree();
	
	/**
	 * 
	 */
	public IExternalAccess getExternalAccess();
	
	/**
	 * 
	 */
	public IExternalAccess getGUIExternalAccess();
	
//	/**
//	 * 
//	 */
//	public boolean containsNode(Object id);
	
	/**
	 * 
	 */
	public void action(Object obj);
}
