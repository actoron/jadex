package jadex.base.gui.asynctree;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.gui.TreeExpansionHandler;


/**
 *  Basic node object.
 */
public abstract class AbstractSwingTreeNode	extends AbstractTreeNode implements ISwingTreeNode 
{
	//-------- attributes --------
	
	/** The tree. */
	// Hack!!! Model should not have access to ui, required for refresh only on expanded nodes.
	protected final JTree	tree;
	
	//-------- constructors --------
	
	/**
	 *  Create a node.
	 */
	public AbstractSwingTreeNode(ITreeNode parent, AsyncTreeModel model, JTree tree)
	{
		super(parent, model);
		this.tree	= tree;
		
//		model.registerNode(this);
	}
	
	//-------- IComponentTreeNode interface --------
	
	
	/**
	 *  Get the parent node.
	 */
	@Override
	public ISwingTreeNode	getParent()
	{
		return (ISwingTreeNode) super.getParent();
	}
	
	/**
	 *  Get the child count.
	 */
	public int	getChildCount()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getChildCount();
	}
	
	/**
	 *  Get the given child.
	 */
	@Override
	public ISwingTreeNode	getChild(int index)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return (ISwingTreeNode) super.getChild(index);
	}
	
	/**
	 *  Get the index of a child.
	 */
	@Override
	public int	getIndexOfChild(ITreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getIndexOfChild(child);
	}
	
	/**
	 *  Check if the node is a leaf.
	 */
	@Override
	public boolean	isLeaf()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.isLeaf();
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	@Override
	public void	refresh(boolean recurse)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.refresh(recurse);
		tree.repaint();
	}
	
	/**
	 *  Get the cached children, i.e. do not start any background processes for updating the children.
	 */
	@Override
	public List<ITreeNode>	getCachedChildren()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getCachedChildren();
	}
	
	/**
	 *  Get the current children, i.e. start a new update process and provide the result as a future.
	 */
	@Override
	public IFuture<List<ITreeNode>> getChildren()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getChildren();
	}

	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		throw new UnsupportedOperationException("Node has no properties: "+this);
	}
	
	//-------- template methods --------
	
	
	/**
	 *  Get the icon for a node.
	 */
	public abstract Icon	getSwingIcon();
	
	/**
	 *  Set the children.
	 *  No children should be represented as empty list to avoid
	 *  ongoing search for children.
	 */
	@Override
	protected void	setChildren(List<? extends ITreeNode> newchildren)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.setChildren(newchildren);
	}
	
	@Override
	protected void expandChildren(boolean dorecurse, List<ITreeNode> children)
	{
		if(dorecurse && TreeExpansionHandler.isTreeExpanded(tree, new TreePath(model.buildTreePath(AbstractSwingTreeNode.this).toArray())))
		{
			for(int i=0; children!=null && i<children.size(); i++)
			{
				((ITreeNode)children.get(i)).refresh(dorecurse);
			}
		}
	}

	/**
	 *  Get the model.
	 */
	@Override
	public AsyncSwingTreeModel	getModel()
	{
		return (AsyncSwingTreeModel) super.getModel();
	}

	/**
	 *  Get the tree.
	 */
	public JTree	getTree()
	{
		return tree;
	}

	/**
	 *  Add a child and update the tree.
	 *  Must be called from swing thread.
	 */
	@Override
	public void addChild(int index, ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.addChild(index, node);
	}
	
	/**
	 *  Add a child and update the tree.
	 *  Must be called from swing thread.
	 */
	@Override
	public void addChild(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.addChild(node);
	}
	
	/**
	 *  Remove a child and update the tree.
	 *  Must be called from swing thread.
	 */
	@Override
	public void removeChild(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.removeChild(node);
	}
	
	/**
	 *  Test if two nodes are equal.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ISwingTreeNode && SUtil.equals(getId(), ((ISwingTreeNode)obj).getId());
	}
	
}
