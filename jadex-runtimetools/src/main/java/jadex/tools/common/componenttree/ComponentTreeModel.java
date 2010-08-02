package jadex.tools.common.componenttree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.help.UnsupportedOperationException;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *  Tree model, which dynamically represents running components.
 */
public class ComponentTreeModel implements TreeModel
{
	//-------- attributes --------
	
	/** The root node. */
	private IComponentTreeNode	root;
	
	/** The tree listeners. */
	private final List	listeners;
	
	/** The node lookup table. */
	private final Map	nodes;
	
	/** The icon overlays. */
	private final List	overlays;
	
	//-------- constructors --------
	
	/**
	 *  Create a component tree model.
	 */
	public ComponentTreeModel()
	{
		this.listeners	= new ArrayList();
		this.nodes	= new HashMap();
		this.overlays	= new ArrayList();
	}
	
	//-------- TreeModel interface --------
	
	/**
	 *  Get the root node.
	 */
	public Object getRoot()
	{
		return root;
	}
	
	/**
	 *  Get the given child of a node.
	 */
	public Object getChild(Object parent, int index)
	{
		return ((IComponentTreeNode)parent).getChild(index);
	}
	
	/**
	 *  Get the number of children of a node.
	 */
	public int getChildCount(Object parent)
	{
		return ((IComponentTreeNode)parent).getChildCount();
	}
	
	/**
	 *  Get the index of a child.
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((IComponentTreeNode)parent).getIndexOfChild((IComponentTreeNode)child);
	}
	
	/**
	 *  Test if the node is a leaf.
	 */
	public boolean isLeaf(Object node)
	{
		return ((IComponentTreeNode)node).isLeaf();
	}
	
	/**
	 *  Edit the value of a node.
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		throw new UnsupportedOperationException("Component Tree is not editable.");
	}
	
	/**
	 *  Add a listener.
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		this.listeners.add(l);
	}
	
	/**
	 *  Remove a listener.
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		this.listeners.remove(l);
	}
	
	//-------- helper methods --------

	/**
	 *  Set the root node.
	 */
	public void	setRoot(IComponentTreeNode root)
	{
		this.root = root;
		fireTreeChanged(root);
	}
	
    /**
     *  Inform listeners that tree has changed from given node on.
     */
	public void fireTreeChanged(IComponentTreeNode node)
	{
		List path = buildTreePath(node);
		
//		System.out.println("Path changed: "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeStructureChanged(new TreeModelEvent(this, path.toArray()));
		}
	}

    /**
     *  Inform listeners that a node has changed.
     */
	public void fireNodeChanged(IComponentTreeNode node)
	{
		int[]	indices;
		Object[]	nodes;
		List	path;
		if(node.getParent()!=null)
		{
			IComponentTreeNode	parent	= node.getParent();
			indices	= new int[]{parent.getIndexOfChild(node)};
			nodes	= new Object[]{node};
			path = buildTreePath(parent);	
		}
		else
		{
			indices	= null;
			nodes	= null;
			path = buildTreePath(node);	
			
		}
		
//		System.out.println("Path changed: "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeNodesChanged(new TreeModelEvent(this, path.toArray(), indices, nodes));
		}
	}

    /**
     *  Inform listeners that a node has been removed
     */
	public void fireNodeRemoved(IComponentTreeNode parent, IComponentTreeNode child, int index)
	{
		List path = buildTreePath(parent);
		
//		System.out.println("Node removed: "+child+", "+index+", "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeNodesRemoved(new TreeModelEvent(this, path.toArray(), new int[]{index}, new Object[]{child}));
		}
	}

    /**
     *  Inform listeners that a node has been added
     */
	public void fireNodeAdded(IComponentTreeNode parent, IComponentTreeNode child, int index)
	{
		List path = buildTreePath(parent);
		
//		System.out.println("Node added: "+child+", "+index+", "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeNodesInserted(new TreeModelEvent(this, path.toArray(), new int[]{index}, new Object[]{child}));
		}
	}
	
	/**
	 *  Build a tree path to the given node.
	 *  @param desc The node.
	 *  @return The path items.
	 */
	public List buildTreePath(IComponentTreeNode node)
	{
		List	path	= new LinkedList();
		IComponentTreeNode	pnode	= node;
		while(pnode!=null)
		{
			path.add(0, pnode);
			pnode	= pnode.getParent();
		}
		return path;
	}
	
	/**
	 *  Add a node (optional).
	 *  Nodes can be registered for easy access.
	 */
	public void	registerNode(Object id, IComponentTreeNode node)
	{
		nodes.put(id, node);
	}
	
	/**
	 *  Get a registered node.
	 */
	public IComponentTreeNode	getNode(Object id)
	{
		return (IComponentTreeNode)nodes.get(id);
	}
	
	/**
	 *  Remove a node registration.
	 */
	public void	deregisterNode(Object id)
	{
		nodes.remove(id);
	}
	
	/**
	 *  Add a node handler.
	 */
	public void	addNodeHandler(INodeHandler overlay)
	{
		this.overlays.add(overlay);
	}
	
	/**
	 *  Get the node handlers.
	 */
	public INodeHandler[]	getNodeHandlers()
	{
		return (INodeHandler[])overlays.toArray(new INodeHandler[overlays.size()]);
	}
}
