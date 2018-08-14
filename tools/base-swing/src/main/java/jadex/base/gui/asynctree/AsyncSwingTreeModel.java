package jadex.base.gui.asynctree;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.adapter.TreeModelListenerWrapper;

/**
 *  Tree model, which dynamically represents running components.
 */
public class AsyncSwingTreeModel extends AsyncTreeModel implements TreeModel
{
	//-------- constructors --------
	
	/**
	 *  Create a component tree model.
	 */
	public AsyncSwingTreeModel()
	{
		super();
	}
	
	//-------- TreeModel interface --------
	
	/**
	 *  Get the root node.
	 */
	@Override
	public ITreeNode getRoot()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		return super.getRoot();
	}
	
	/**
	 *  Get the given child of a node.
	 */
	@Override
	public ITreeNode getChild(Object parent, int index)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getChild(parent, index);
	}
	
	/**
	 *  Get the number of children of a node.
	 */
	@Override
	public int getChildCount(Object parent)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getChildCount(parent);
	}
	
	/**
	 *  Get the index of a child.
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		return super.getIndexOfChild(parent, child);
	}
	
	/**
	 *  Test if the node is a leaf.
	 */
	@Override
	public boolean isLeaf(Object node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.isLeaf(node);
	}
	
	/**
	 *  Edit the value of a node.
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		throw new UnsupportedOperationException("Component Tree is not editable.");
	}
	
	/**
	 *  Add a listener.
	 */
	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.addTreeModelListener(TreeModelListenerWrapper.getWrapperFor(l));
	}
	
	/**
	 *  Remove a listener.
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.removeTreeModelListener(TreeModelListenerWrapper.getWrapperFor(l));
//		TreeModelListenerWrapper.deleteWrapperFor(l);
	}
	
	//-------- helper methods --------

	/**
	 *  Set the root node.
	 */
	@Override
	public void	setRoot(ITreeNode root)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.setRoot(root);
	}
	
    /**
     *  Inform listeners that tree has changed from given node on.
     */
	@Override
	public void fireTreeChanged(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.fireTreeChanged(node);
	}

    /**
     *  Inform listeners that a node has changed.
     */
	@Override
	public void fireNodeChanged(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.fireNodeChanged(node);	
	}

    /**
     *  Inform listeners that a node has been removed
     */
	@Override
	public void fireNodeRemoved(ITreeNode parent, ITreeNode child, int index)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.fireNodeRemoved(parent, child, index);
	}
	
	/**
     *  Inform listeners that a node has been removed
     */
	@Override
	public void fireNodesRemoved(ITreeNode parent, ITreeNode[] childs, int[] indices)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.fireNodesRemoved(parent, childs, indices);
	}

    /**
     *  Inform listeners that a node has been added
     */
	@Override
	public void fireNodeAdded(ITreeNode parent, ITreeNode child, int index)
	{
//		if(child.toString().indexOf("A:")!=-1)
//			System.out.println("here4");

		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.fireNodeAdded(parent, child, index);
	}
	
	/**
	 *  Build a tree path to the given node.
	 *  @param desc The node.
	 *  @return The path items.
	 */
	@Override
	public List buildTreePath(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.buildTreePath(node);
	}
	
	/**
	 *  Register a node.
	 *  Nodes can be registered for easy access.
	 */
	@Override
	public void	registerNode(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.registerNode(node);
	}
	
	/**
	 *  Add a node.
	 *  Informs listeners.
	 */
	@Override
	public void	addNode(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

//		System.out.println("addnode: "+node);
		super.addNode(node);
	}
	
	/**
	 *  Get a node by its id.
	 */
	@Override
	public ISwingTreeNode	getNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return (ISwingTreeNode) super.getNode(id);
	}
	
	/**
	 *  Get a node by its id.
	 */
	@Override
	public ISwingTreeNode	getAddedNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return (ISwingTreeNode) super.getAddedNode(id);
	}
	
	/**
	 *  Remove a node registration.
	 */
	@Override
	public void	deregisterNode(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.deregisterNode(node);
	}
	
	/**
	 *  Add a node handler.
	 */
	@Override
	public void	addNodeHandler(INodeHandler overlay)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		assert (overlay instanceof ISwingNodeHandler);
		super.addNodeHandler(overlay);
	}
	
	/**
	 *  Get the node handlers.
	 */
	@Override
	public INodeHandler[]	getNodeHandlers()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		return super.getNodeHandlers();
	}

	/**
	 *  Register a node listener.
	 */
	@Override
	public void	addNodeListener(INodeListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		super.addNodeListener(listener);
	}
	
	/**
	 *  Deregister a node listener.
	 */
	@Override
	public void	removeNodeListener(INodeListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.removeNodeListener(listener);
	}
	
	/**
	 *  Check, if a node is a zombie.
	 */
	@Override
	public boolean	isZombieNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		return super.isZombieNode(id);
	}

	/**
	 *  Called when the tree is removed.
	 */
	@Override
	public void dispose()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();

		super.dispose();
	}
	
	@Override
	public ISwingTreeNode getNodeOrAddZombie(Object id)
	{
		return (ISwingTreeNode) super.getNodeOrAddZombie(id);
	}

	/**
	 *  Remove a zombie node.
	 */
	@Override
	public void	removeZombieNode(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		super.removeZombieNode(node);
	}

}
