package jadex.base.gui.asynctree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.collection.MultiCollection;

/**
 * Tree model, which dynamically represents running components.
 */
public class AsyncTreeModel
{
	// -------- attributes --------

	/** The root node. */
	protected ITreeNode root;

	/** The tree listeners. */
	protected final List<TreeModelListener> listeners;

	/** The node listeners. */
	protected final List<INodeListener> nodelisteners;

	/** The node lookup table. */
	protected final Map<Object, ITreeNode> nodes;

	/** The added nodes. */
	protected final Map<Object, ITreeNode> added;

	/** The zombie node ids (id->remove counter). */
	protected final Map<Object, Integer> zombies;

	/** The icon overlays. */
	protected final List<INodeHandler> overlays;

	/** The changed nodes (delayed update for improving perceived speed). */
	protected MultiCollection<ITreeNode, ITreeNode> changed;

	// -------- constructors --------

	/**
	 * Create a component tree model.
	 */
	public AsyncTreeModel()
	{
		this.listeners = new ArrayList<TreeModelListener>();
		this.nodelisteners = new ArrayList<INodeListener>();
		this.nodes = new HashMap<Object, ITreeNode>();
		this.added = new HashMap<Object, ITreeNode>();
		this.zombies = new HashMap<Object, Integer>();
		this.overlays = new ArrayList<INodeHandler>();
	}

	// -------- TreeModel interface --------

	/**
	 * Get the root node.
	 */
	public ITreeNode getRoot()
	{
		return root;
	}

	/**
	 * Get the given child of a node.
	 */
	public ITreeNode getChild(Object parent, int index)
	{
		return ((ITreeNode) parent).getChild(index);
	}

	/**
	 * Get the number of children of a node.
	 */
	public int getChildCount(Object parent)
	{
		return ((ITreeNode) parent).getChildCount();
	}

	/**
	 * Get the index of a child.
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((ITreeNode) parent).getIndexOfChild((ITreeNode) child);
	}

	/**
	 * Test if the node is a leaf.
	 */
	public boolean isLeaf(Object node)
	{
		return ((ITreeNode) node).isLeaf();
	}

	// /**
	// * Edit the value of a node.
	// */
	// public void valueForPathChanged(TreePath path, Object newValue)
	// {
	//
	// throw new
	// UnsupportedOperationException("Component Tree is not editable.");
	// }

	/**
	 * Add a listener.
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		this.listeners.add(l);
	}

	/**
	 * Remove a listener.
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		this.listeners.remove(l);
	}

	// -------- helper methods --------

	/**
	 * Set the root node.
	 */
	public void setRoot(ITreeNode root)
	{
		if(this.root != null)
			deregisterNode(this.root);
		this.root = root;
		if (root != null)
			addNode(root);

		// System.err.println(""+hashCode()+" set root");
		fireTreeChanged(root);
	}

	/**
	 * Inform listeners that tree has changed from given node on.
	 */
	public void fireTreeChanged(ITreeNode node)
	{
		List<ITreeNode> path = buildTreePath(node);

		// System.err.println(""+hashCode()+" Path changed: "+node+", "+path+", "+node.getCachedChildren());

		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).treeStructureChanged(new AsyncTreeModelEvent(this, path.toArray()));
		}
	}

	/**
	 * Inform listeners that a node has changed.
	 */
	public void fireNodeChanged(ITreeNode node)
	{
		if(changed == null)
		{
			changed = new MultiCollection<ITreeNode, ITreeNode>(new HashMap(), HashSet.class);
			changed.add(node.getParent(), node);
			// SwingUtilities.invokeLater(new Runnable()
			// {
			// public void run()
			// {
			ITreeNode[] parents = (ITreeNode[]) changed.getKeys(ITreeNode.class);
			for (int i = 0; i < parents.length; i++)
			{
				// Only throw event when root node or parent still in tree
				if (parents[i] == null || getAddedNode(parents[i].getId()) != null)
				{
					boolean skip = false;
					Set<ITreeNode> set = (Set<ITreeNode>)changed.get(parents[i]);
					int[] indices;
					ITreeNode[] nodes;
					List<ITreeNode> path = null;
					if (parents[i] != null)
					{
						int cnt = 0;
						nodes = new ITreeNode[set.size()];
						indices = new int[nodes.length];
						Iterator<ITreeNode> it = set.iterator();
						for(int j = 0; j < nodes.length; j++)
						{
							nodes[cnt] = it.next();
							if(getAddedNode(nodes[cnt].getId()) != null)
							{
								indices[cnt] = parents[i].getIndexOfChild((ITreeNode) nodes[cnt]);
								if (indices[cnt] != -1)
								{
									cnt++;
								}
							}
						}
						if(cnt == 0)
						{
							skip = true;
						} 
						else
						{
							if(cnt < nodes.length)
							{
								ITreeNode[] ntmp = new ITreeNode[cnt];
								int[] itmp = new int[cnt];
								System.arraycopy(nodes, 0, ntmp, 0, cnt);
								System.arraycopy(indices, 0, itmp, 0, cnt);
								nodes = ntmp;
								indices = itmp;
							}
							path = buildTreePath(parents[i]);
						}
					}

					// Root node (there can be only one with parent==null)
					else
					{
						assert set.size() == 1 : set;
						indices = null;
						nodes = null;
						path = buildTreePath((ITreeNode) set.iterator().next());
					}

					if(!skip) // Nodes might be removed already.
					{
						for (int j = 0; j < listeners.size(); j++)
						{
							listeners.get(j).treeNodesChanged(new AsyncTreeModelEvent(this, path.toArray(), indices, nodes));
						}
					}
				}
			}

			changed = null;
			// }
			// });
		}

//		System.out.println("Node changed: "+node+", "+path);
	}

	/**
	 * Inform listeners that a node has been removed
	 */
	public void fireNodeRemoved(ITreeNode parent, ITreeNode child, int index)
	{
		List<ITreeNode> path = buildTreePath(parent);

//		System.err.println(""+hashCode()+" Node removed: "+child+", "+index+", "+path);

		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).treeNodesRemoved(
				new AsyncTreeModelEvent(this, path.toArray(), new int[]{index}, new Object[]{child}));
		}
	}

	/**
	 * Inform listeners that a node has been removed
	 */
	public void fireNodesRemoved(ITreeNode parent, ITreeNode[] childs, int[] indices)
	{
		List<ITreeNode> path = buildTreePath(parent);

//		System.err.println(""+hashCode()+" Node removed: "+child+", "+index+", "+path);

		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).treeNodesRemoved(new AsyncTreeModelEvent(this, path.toArray(), indices, childs));
		}
	}

	/**
	 * Inform listeners that a node has been added
	 */
	public void fireNodeAdded(ITreeNode parent, ITreeNode child, int index)
	{
		// if(child.toString().indexOf("A:")!=-1)
		// System.out.println("here4");

		List<ITreeNode> path = buildTreePath(parent);

		// System.err.println(""+hashCode()+" Node added: "+child+", "+index+", "+path);

		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).treeNodesInserted(
				new AsyncTreeModelEvent(this, path.toArray(), new int[]{index}, new Object[]{child}));
		}
	}

	/**
	 * Build a tree path to the given node.
	 * 
	 * @param desc The node.
	 * @return The path items.
	 */
	public List<ITreeNode> buildTreePath(ITreeNode node)
	{
		List<ITreeNode> path = new LinkedList<ITreeNode>();
		ITreeNode pnode = node;
		while (pnode != null)
		{
			path.add(0, pnode);
			pnode = pnode.getParent();
		}
		return path;
	}

	/**
	 * Register a node. Nodes can be registered for easy access.
	 */
	public void registerNode(ITreeNode node)
	{
		synchronized(nodes)
		{
//			System.out.println("registered: "+node.getId());
			
			if(nodes.containsKey(node.getId()))
				throw new RuntimeException("Node id already contained: " + node + ", " + node.getId());

			nodes.put(node.getId(), node);
		}
	}

	/**
	 * Add a node. Informs listeners.
	 */
	public void addNode(ITreeNode node)
	{
//		if(added.containsKey(node.getId()))
//			throw new RuntimeException("Already contained: "+node+" node is: "+this);
		added.put(node.getId(), node);

		INodeListener[] lis = nodelisteners.toArray(new INodeListener[nodelisteners.size()]);
		for(int i = 0; i < lis.length; i++)
		{
			lis[i].nodeAdded(node);
		}

		for(int i = 0; i < node.getCachedChildren().size(); i++)
		{
			addNode((ITreeNode)node.getCachedChildren().get(i));
		}
	}

	/**
	 * Get a node by its id.
	 */
	public ITreeNode getNode(Object id)
	{
		ITreeNode ret;
		synchronized (nodes)
		{
			ret = nodes.get(id);
		}
		return ret;
	}

	/**
	 * Get a node by its id.
	 */
	public ITreeNode getAddedNode(Object id)
	{
		return added.get(id);
	}

	/**
	 * Remove a node registration.
	 */
	public void deregisterNode(ITreeNode node)
	{
		node.dispose();
		boolean notify = false;
		synchronized (nodes)
		{
			if(zombies.containsKey(node.getId()))
			{
				zombies.remove(node.getId());
				nodes.remove(node.getId());
			} 
			else
			{
				// System.out.println("Removed: "+node.getId());
				nodes.remove(node.getId());
				added.remove(node.getId());
				notify = true;
			}
		}

		if(notify)
		{
			INodeListener[] lis = nodelisteners.toArray(new INodeListener[nodelisteners.size()]);
			for (int i = 0; i < lis.length; i++)
			{
				lis[i].nodeRemoved(node);
			}
		}

		for(int i = 0; i < node.getCachedChildren().size(); i++)
		{
			deregisterNode((ITreeNode)node.getCachedChildren().get(i));
		}
	}

	/**
	 * Add a node handler.
	 */
	public void addNodeHandler(INodeHandler overlay)
	{
		this.overlays.add(overlay);
	}

	/**
	 * Get the node handlers.
	 */
	public INodeHandler[] getNodeHandlers()
	{
		return overlays.toArray(new INodeHandler[overlays.size()]);
	}

	/**
	 * Register a node listener.
	 */
	public void addNodeListener(INodeListener listener)
	{

		nodelisteners.add(listener);
	}

	/**
	 * Deregister a node listener.
	 */
	public void removeNodeListener(INodeListener listener)
	{
		nodelisteners.remove(listener);
	}

	/**
	 * Check, if a node is a zombie.
	 */
	public boolean isZombieNode(Object id)
	{
		boolean ret;
		synchronized (nodes)
		{
			ret = zombies.containsKey(id);
		}
		return ret;
	}

	/**
	 * Called when the tree is removed.
	 */
	public void dispose()
	{
		ITreeNode[] anodes;
		synchronized (nodes)
		{
			anodes = nodes.values().toArray(new ITreeNode[nodes.values().size()]);
		}

		for (int i = 0; i < anodes.length; i++)
		{
			anodes[i].dispose();
		}
	}

	/**
	 * Get a node for removal. Add a zombie node, if node does not exist.
	 */
	public ITreeNode getNodeOrAddZombie(Object id)
	{
		ITreeNode ret;
		synchronized (nodes)
		{
			ret = nodes.get(id);
			if (ret == null)
			{
				addZombieNode(id);
			}
		}
		return ret;
	}

	/**
	 * Add a zombie node or increase the counter.
	 */
	public void addZombieNode(Object id)
	{
		synchronized (nodes)
		{
			Integer num = zombies.get(id);
			num = Integer.valueOf(num != null ? num.intValue() + 1 : 1);
			zombies.put(id, num);
			// if(id.toString().startsWith("ANDTest@"))
			// System.out.println("Zombie node count increased: "+id+", "+num);
		}
	}

	/**
	 * Remove a zombie node.
	 */
	public void removeZombieNode(ITreeNode node)
	{
		synchronized(nodes)
		{
			Integer num = zombies.get(node.getId());
			if(num.intValue()>1)
			{
				num = Integer.valueOf(num.intValue()-1);
				zombies.put(node.getId(), num);
				// if(node.getId().toString().startsWith("ANDTest@"))
				// System.out.println("Zombie node count decreased: "+node+", "+num);
			}
			else
			{
				// if(node.getId().toString().startsWith("ANDTest@"))
				// System.out.println("Zombie node removed: "+node+", "+num);
				deregisterNode(node);
			}
		}
	}

}
