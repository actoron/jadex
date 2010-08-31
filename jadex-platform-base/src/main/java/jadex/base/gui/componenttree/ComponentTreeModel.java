package jadex.base.gui.componenttree;

import jadex.commons.collection.MultiCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
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
	
	/** The added nodes. */
	private final Map	added;
	
	/** The zombie node ids. */
	private final Set	zombies;
	
	/** The icon overlays. */
	private final List	overlays;
	
	/** The changed nodes (delayed update for improving perceived speed). */
	private MultiCollection	changed;
	
	//-------- constructors --------
	
	/**
	 *  Create a component tree model.
	 */
	public ComponentTreeModel()
	{
		this.listeners	= new ArrayList();
		this.nodelisteners	= new ArrayList();
		this.nodes	= new HashMap();
		this.added	= new HashMap();
		this.zombies	= new HashSet();
		this.overlays	= new ArrayList();
	}
	
	//-------- TreeModel interface --------
	
	/**
	 *  Get the root node.
	 */
	public Object getRoot()
	{
		assert SwingUtilities.isEventDispatchThread();

		return root;
	}
	
	/**
	 *  Get the given child of a node.
	 */
	public Object getChild(Object parent, int index)
	{
		assert SwingUtilities.isEventDispatchThread();

		return ((IComponentTreeNode)parent).getChild(index);
	}
	
	/**
	 *  Get the number of children of a node.
	 */
	public int getChildCount(Object parent)
	{
		assert SwingUtilities.isEventDispatchThread();

		return ((IComponentTreeNode)parent).getChildCount();
	}
	
	/**
	 *  Get the index of a child.
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		assert SwingUtilities.isEventDispatchThread();

		return ((IComponentTreeNode)parent).getIndexOfChild((IComponentTreeNode)child);
	}
	
	/**
	 *  Test if the node is a leaf.
	 */
	public boolean isLeaf(Object node)
	{
		assert SwingUtilities.isEventDispatchThread();

		return ((IComponentTreeNode)node).isLeaf();
	}
	
	/**
	 *  Edit the value of a node.
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		assert SwingUtilities.isEventDispatchThread();

		throw new UnsupportedOperationException("Component Tree is not editable.");
	}
	
	/**
	 *  Add a listener.
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		this.listeners.add(l);
	}
	
	/**
	 *  Remove a listener.
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		this.listeners.remove(l);
	}
	
	//-------- helper methods --------

	/**
	 *  Set the root node.
	 */
	public void	setRoot(IComponentTreeNode root)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(this.root!=null)
			deregisterNode(this.root);
		this.root = root;
		if(root!=null)
			addNode(root);
		fireTreeChanged(root);
	}
	
    /**
     *  Inform listeners that tree has changed from given node on.
     */
	public void fireTreeChanged(IComponentTreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		List path = buildTreePath(node);
		
//		System.out.println("Path changed: "+node+", "+path);
		
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
		assert SwingUtilities.isEventDispatchThread();

		if(changed==null)
		{
			changed	= new MultiCollection(new HashMap(), HashSet.class);
			changed.put(node.getParent(), node);
//			SwingUtilities.invokeLater(new Runnable()
//			{
//				public void run()
//				{
					IComponentTreeNode[]	parents	= (IComponentTreeNode[])changed.getKeys(IComponentTreeNode.class);
					for(int i=0; i<parents.length; i++)
					{
						// Only throw event when root node or parent still in tree
						if(parents[i]==null || getAddedNode(parents[i].getId())!=null)
						{
							boolean	skip	= false;
							Set	set	= (Set)changed.get(parents[i]);
							int[]	indices;
							Object[]	nodes;
							List	path	= null;
							if(parents[i]!=null)
							{
								int cnt	= 0;
								nodes	= new Object[set.size()];
								indices	= new int[nodes.length];
								Iterator	it	= set.iterator();
								for(int j=0; j<nodes.length; j++)
								{
									nodes[cnt]	= it.next();
									if(getAddedNode(((IComponentTreeNode)nodes[cnt]).getId())!=null)
									{
										indices[cnt]	= parents[i].getIndexOfChild((IComponentTreeNode)nodes[cnt]);
										if(indices[cnt]!=-1)
										{
											cnt++;
										}
									}
								}
								if(cnt==0)
								{
									skip	= true;
								}
								else
								{
									if(cnt<nodes.length)
									{
										Object[]	ntmp	= new Object[cnt];
										int[]	itmp	= new int[cnt];
										System.arraycopy(nodes, 0, ntmp, 0, cnt);
										System.arraycopy(indices, 0, itmp, 0, cnt);
										nodes	= ntmp;
										indices	= itmp;
									}
									path = buildTreePath(parents[i]);
								}
							}
							
							// Root node (there can be only one with parent==null)
							else
							{
								assert set.size()==1 : set;
								indices	= null;
								nodes	= null;
								path = buildTreePath((IComponentTreeNode)set.iterator().next());
								
							}
							
							if(!skip)	// Nodes might be removed already.
							{
								for(int j=0; j<listeners.size(); j++)
								{
									((TreeModelListener)listeners.get(j)).treeNodesChanged(new TreeModelEvent(this, path.toArray(), indices, nodes));
								}
							}
						}
					}
					
					changed	= null;
//				}
//			});
		}
		
//		System.out.println("Node changed: "+node+", "+path);		
	}

    /**
     *  Inform listeners that a node has been removed
     */
	public void fireNodeRemoved(IComponentTreeNode parent, IComponentTreeNode child, int index)
	{
		assert SwingUtilities.isEventDispatchThread();
		
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
		assert SwingUtilities.isEventDispatchThread();
		
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
		assert SwingUtilities.isEventDispatchThread();

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
	 *  Register a node.
	 *  Nodes can be registered for easy access.
	 */
	public void	registerNode(IComponentTreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();

		synchronized(nodes)
		{
			if(nodes.containsKey(node.getId()))
				throw new RuntimeException("Node id already contained: "+node+", "+node.getId());
			
			nodes.put(node.getId(), node);
		}
	}
	
	/**
	 *  Add a node.
	 *  Informs listeners.
	 */
	public void	addNode(IComponentTreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();

		added.put(node.getId(), node);
		
		INodeListener[]	lis	= (INodeListener[])nodelisteners.toArray(new INodeListener[nodelisteners.size()]);
		for(int i=0; i<lis.length; i++)
		{
			lis[i].nodeAdded(node);
		}

		for(int i=0; i<node.getCachedChildren().size(); i++)
		{
			addNode((IComponentTreeNode)node.getCachedChildren().get(i));
		}		
	}
	
	/**
	 *  Get a node by its id.
	 */
	public IComponentTreeNode	getNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();

		IComponentTreeNode	ret;
		synchronized(nodes)
		{
			ret	= (IComponentTreeNode)nodes.get(id);
		}
		return ret;
	}
	
	/**
	 *  Get a node by its id.
	 */
	public IComponentTreeNode	getAddedNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();

		return (IComponentTreeNode)added.get(id);
	}
	
	/**
	 *  Remove a node registration.
	 */
	public void	deregisterNode(IComponentTreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		node.dispose();
		boolean	notify	= false;
		synchronized(nodes)
		{
			if(zombies.contains(node.getId()))
			{
				zombies.remove(node.getId());
				nodes.remove(node.getId());
			}
			else
			{
	//			System.out.println("Removed: "+node.getId());
				nodes.remove(node.getId());
				added.remove(node.getId());
				notify	= true;
			}
		}
		
		if(notify)
		{
			INodeListener[]	lis	= (INodeListener[])nodelisteners.toArray(new INodeListener[nodelisteners.size()]);
			for(int i=0; i<lis.length; i++)
			{
				lis[i].nodeRemoved(node);
			}			
		}

		for(int i=0; i<node.getCachedChildren().size(); i++)
		{
			deregisterNode((IComponentTreeNode)node.getCachedChildren().get(i));
		}
	}
	
	/**
	 *  Add a node handler.
	 */
	public void	addNodeHandler(INodeHandler overlay)
	{
		assert SwingUtilities.isEventDispatchThread();

		this.overlays.add(overlay);
	}
	
	/**
	 *  Get the node handlers.
	 */
	public INodeHandler[]	getNodeHandlers()
	{
		assert SwingUtilities.isEventDispatchThread();

		return (INodeHandler[])overlays.toArray(new INodeHandler[overlays.size()]);
	}

	/**
	 *  Register a node listener.
	 */
	public void	addNodeListener(INodeListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();

		nodelisteners.add(listener);
	}
	
	/**
	 *  Deregister a node listener.
	 */
	public void	removeNodeListener(INodeListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();

		nodelisteners.remove(listener);
	}
	
	/**
	 *  Called, when a node should be removed that isn't there (yet).
	 */
	public void addZombieNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();

		synchronized(nodes)
		{
			assert !nodes.containsKey(id) : id;
			zombies.add(id);
		}
	}
	
	/**
	 *  Check, if a node is a zombie.
	 */
	public boolean	isZombieNode(Object id)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		boolean ret;
		synchronized(nodes)
		{
			ret	= zombies.contains(id);
		}
		return ret;
	}

	/**
	 *  Called when the tree is removed.
	 */
	public void dispose()
	{
		assert SwingUtilities.isEventDispatchThread();

		IComponentTreeNode[]	anodes;
		synchronized(nodes)
		{
			anodes	= (IComponentTreeNode[])nodes.values().toArray(new IComponentTreeNode[nodes.values().size()]);
		}
		
		for(int i=0; i<anodes.length; i++)
		{
			anodes[i].dispose();
		}
	}

	/**
	 *  Get a node for removal.
	 *  Add a zombie node, if node does not exist.
	 */
	public IComponentTreeNode getNodeOrAddZombie(Object id)
	{
		IComponentTreeNode	ret;
		synchronized(nodes)
		{
			ret	= (IComponentTreeNode)nodes.get(id);
			if(ret==null)
			{
				zombies.add(id);
			}
		}
		return ret;
	}
}
