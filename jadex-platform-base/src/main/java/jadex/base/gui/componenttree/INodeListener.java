package jadex.base.gui.componenttree;

/**
 *  Notified when nodes are added or removed from the component tree.
 */
public interface INodeListener
{
	/**
	 *  A node was added.
	 */
	public void	nodeAdded(IComponentTreeNode node);
	
	/**
	 *  A node was removed.
	 */
	public void	nodeRemoved(IComponentTreeNode node);
}
