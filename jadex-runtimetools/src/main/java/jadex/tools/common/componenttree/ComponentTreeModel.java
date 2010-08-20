package jadex.tools.common.componenttree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	/** The node listeners. */
	private final List	nodelisteners;
	
	/** The node lookup table. */
	private final Map	nodes;
	
	/** The zombie node ids. */
	private final Set	zombies;
	
	/** The icon overlays. */
	private final List	overlays;
	
	//-------- constructors --------
	
	/**
	 *  Create a component tree model.
	 */
	public ComponentTreeModel()
	{
		this.listeners	= new ArrayList();
		this.nodelisteners	= new ArrayList();
		this.nodes	= new HashMap();
		this.zombies	= new HashSet();
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
		if(this.root!=null)
			deregisterNode(this.root);
		this.root = root;
		if(root!=null)
			registerNode(root);
		fireTreeChanged(root);
	}
	
    /**
     *  Inform listeners that tree has changed from given node on.
     */
	public void fireTreeChanged(IComponentTreeNode node)
	{
		List path = buildTreePath(node);
		
//		System.out.println("Path changed: "+node+", "+path+", "+Thread.currentThread());
		
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
		
//		System.out.println("Node changed: "+node+", "+path+", "+Thread.currentThread());
		
		if(indices==null || indices[0]!=-1)	// Node might be removed already.
		{
			for(int i=0; i<listeners.size(); i++)
			{
				((TreeModelListener)listeners.get(i)).treeNodesChanged(new TreeModelEvent(this, path.toArray(), indices, nodes));
			}
		}
	}

    /**
     *  Inform listeners that a node has been removed
     */
	public void fireNodeRemoved(IComponentTreeNode parent, IComponentTreeNode child, int index)
	{
		List path = buildTreePath(parent);
		
//		System.out.println("Node removed: "+child+", "+index+", "+path+", "+Thread.currentThread());
		
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
		
//		System.out.println("Node added: "+child+", "+index+", "+path+", "+Thread.currentThread());
		
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
	public void	registerNode(IComponentTreeNode node)
	{
		if(nodes.containsKey(node.getId()))
			throw new RuntimeException("Node id already contained: "+node);
		
		nodes.put(node.getId(), node);
		
		INodeListener[]	lis	= (INodeListener[])nodelisteners.toArray(new INodeListener[nodelisteners.size()]);
		for(int i=0; i<lis.length; i++)
		{
			lis[i].nodeAdded(node);
		}
	}
	
	/**
	 *  Get a node by its id.
	 */
	public IComponentTreeNode	getNode(Object id)
	{
		return (IComponentTreeNode)nodes.get(id);
	}
	
	/**
	 *  Remove a node registration.
	 */
	public void	deregisterNode(IComponentTreeNode node)
	{
		node.dispose();
		if(zombies.contains(node.getId()))
		{
			assert !nodes.containsKey(node.getId()) : node.getId();
			zombies.remove(node.getId());
		}
		else
		{
			nodes.remove(node.getId());
			INodeListener[]	lis	= (INodeListener[])nodelisteners.toArray(new INodeListener[nodelisteners.size()]);
			for(int i=0; i<lis.length; i++)
			{
				lis[i].nodeRemoved(node);
			}
		}
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

	/**
	 *  Register a node listener.
	 */
	public void	addNodeListener(INodeListener listener)
	{
		nodelisteners.add(listener);
	}
	
	/**
	 *  Deregister a node listener.
	 */
	public void	removeNodeListener(INodeListener listener)
	{
		nodelisteners.remove(listener);
	}
	
	/**
	 *  Called, when a node should be removed that isn't there (yet).
	 */
	public void addZombieNode(Object id)
	{
		assert !nodes.containsKey(id) : id;
		zombies.add(id);
	}
	
	/**
	 *  Check, if a node is a zombie.
	 */
	public boolean	isZombieNode(Object id)
	{
		return zombies.contains(id);
	}

	/**
	 *  Called when the tree is removed.
	 */
	public void dispose()
	{
		for(Iterator it=nodes.values().iterator(); it.hasNext(); )
		{
			((IComponentTreeNode)it.next()).dispose();
		}
	}
}
