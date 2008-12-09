package jadex.tools.common.modeltree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *  Model of the explorer tree, which gets dynamically
 *  filled by the node functionalities.
 */
public class ModelExplorerTreeModel implements TreeModel
{
	//-------- attributes --------
	
	/** The root node. */
	protected RootNode	root;
	
	/** The node functionality. */
	protected DefaultNodeFunctionality	nof;
	
	/** The listeners. */
	protected List	listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new explorer tree model.
	 */
	public ModelExplorerTreeModel(RootNode root, DefaultNodeFunctionality nof)
	{
		this.root	= root;
		this.nof	= nof;
	}
	
	//-------- TreeModel interface --------
	
	public Object getChild(Object parent, int index)
	{
		Object	ret;
		if(parent instanceof FileNode)
		{
			List	children	= nof.getChildren((FileNode)parent);
			if(children!=null)
				ret	= children.get(index);
			else
				throw new ArrayIndexOutOfBoundsException(index);
		}
		else
		{
			ret	= ((RootNode)parent).getChildAt(index);
		}
		return ret;
	}

	public int getChildCount(Object parent)
	{
		int	ret;
		if(parent instanceof FileNode)
		{
			List	children	= nof.getChildren((FileNode)parent);
			ret	= children!=null ? children.size() : 0;
		}
		else
		{
			ret	= ((RootNode)parent).getChildCount();
		}
		return ret;
	}

	public int getIndexOfChild(Object parent, Object child)
	{
		int	ret	= -1;
		if(parent instanceof FileNode)
		{
			List	children	= nof.getChildren((FileNode)parent);
			if(children!=null)
				ret	= children.indexOf(child);
		}
		else
		{
			ret	= ((RootNode)parent).getIndex((IExplorerTreeNode) child);
		}
		return ret;
	}

	public Object getRoot()
	{
		return root;
	}

	public boolean isLeaf(Object node)
	{
		return getChildCount(node)==0;
	}

	public void addTreeModelListener(TreeModelListener l)
	{
		if(listeners==null)
			listeners	= new ArrayList();
		listeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l)
	{
		if(listeners!=null && listeners.remove(l) && listeners.isEmpty())
			listeners	= null;
	}

	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// manipulation of nodes not supported / required
	}

	//-------- event methods --------
	
	/**
	 *  A node has changed.
	 */
	public void	fireNodeChanged(IExplorerTreeNode node)
	{
		if(listeners!=null)
		{
			TreeModelEvent	event	= new TreeModelEvent(this, getPathForNode(node));
			for(int i=0; i<listeners.size(); i++)
			{
				((TreeModelListener)listeners.get(i)).treeNodesChanged(event);
			}
		}
	}
	
	/**
	 *  A node was added.
	 */
	public void	fireNodeAdded(IExplorerTreeNode parent, IExplorerTreeNode child, int index)
	{
		if(listeners!=null)
		{
			if(parent instanceof RootNode)
			{
				// Hack!!! Tree will not update changes of root node, if root node is not visible!?
				fireTreeStructureChanged(parent);
			}
			else
			{
				TreeModelEvent	event	= new TreeModelEvent(this, getPathForNode(parent), new int[]{index}, new Object[]{child});
				for(int i=0; i<listeners.size(); i++)
				{
					((TreeModelListener)listeners.get(i)).treeNodesInserted(event);
				}
			}
		}
	}
	
	/**
	 *  A node was removed.
	 */
	public void	fireNodeRemoved(IExplorerTreeNode parent, IExplorerTreeNode child, int index)
	{
		if(listeners!=null)
		{
			if(parent instanceof RootNode)
			{
				// Hack!!! Tree will not update changes of root node, if root node is not visible!?
				fireTreeStructureChanged(parent);
			}
			else
			{
				TreeModelEvent	event	= new TreeModelEvent(this, getPathForNode(parent), new int[]{index}, new Object[]{child});
				for(int i=0; i<listeners.size(); i++)
				{
					((TreeModelListener)listeners.get(i)).treeNodesRemoved(event);
				}
			}
		}
	}
	
	/**
	 *  Structure below a node has changed.
	 */
	public void	fireTreeStructureChanged(IExplorerTreeNode node)
	{
		if(listeners!=null)
		{
			TreeModelEvent	event	= new TreeModelEvent(this, getPathForNode(node));
			for(int i=0; i<listeners.size(); i++)
			{
				((TreeModelListener)listeners.get(i)).treeStructureChanged(event);
			}
		}
	}

	/**
	 *  Set a new root node for the tree.
	 */
	public void setRoot(RootNode root)
	{
		this.root	= root;
		fireTreeStructureChanged(root);
	}

	/**
	 *  Get the path for a node.
	 */
	public Object[]	getPathForNode(IExplorerTreeNode node)
	{
		List	path	= new LinkedList();
		while(node!=null)
		{
			path.add(0, node);
			node	= node.getParent();
		}
		return path.toArray();
	}
}
