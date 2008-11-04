package jadex.tools.common.modeltree;

import javax.swing.tree.TreeNode;

/**
 *  Command for nodes to execute on valid changes. 
 */
public interface INodeAction
{
	/**
	 *  Called when valid state changed.
	 *  @param valid The valid state.
	 */
	public void validStateChanged(TreeNode node, boolean valid);
}
