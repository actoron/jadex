package jadex.tools.common.modeltree;


import javax.swing.Icon;

/**
 *	The node functionality encapsulates
 *  node behavior that is specific to a
 *  view (e.g. starter vs. test center).
 */
public interface INodeFunctionality
{
	/**
	 *  Set the model explorer (called once on init).
	 */
	public void	setModelExplorer(ModelExplorer explorer);
	
	/**
	 *  Perform the actual refresh.
	 */
	public void	refresh(IExplorerTreeNode node);
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon getIcon(IExplorerTreeNode node);
}
