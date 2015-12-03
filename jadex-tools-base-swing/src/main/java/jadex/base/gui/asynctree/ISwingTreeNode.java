package jadex.base.gui.asynctree;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import jadex.commons.future.IFuture;

/**
 *  Node for the component tree panel.
 */
public interface ISwingTreeNode extends ITreeNode
{
	/**
	 *  Get the parent node.
	 */
	@Override
	public ISwingTreeNode	getParent();
	
	/**
	 *  Get the given child.
	 */
	@Override
	public ISwingTreeNode	getChild(int index);
	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon();
	
	/**
	 *  Get the current children, i.e. start a new update process and provide the result as a future.
	 */
	@Override
	public IFuture<List<ITreeNode>> getChildren();
	
	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent();
}
