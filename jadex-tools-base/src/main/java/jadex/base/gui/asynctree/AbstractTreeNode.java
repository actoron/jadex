package jadex.base.gui.asynctree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * Basic node object.
 */
public abstract class AbstractTreeNode implements ITreeNode
{
	// -------- attributes --------

	/** The parent node. */
	protected ITreeNode parent;

	/** The tree model. */
	protected final AsyncTreeModel model;

	/** The cached children. */
	private List<ITreeNode> children;

	/** Flag to indicate search in progress. */
	protected boolean searching;

	/** Flag to indicate recursive refresh. */
	protected boolean recurse;

	/**
	 * Flag to indicate that children were added / removed during ongoing search
	 * (->restart search).
	 */
	protected boolean dirty;

	/** The children future (result of next search). */
	protected Future<List<ITreeNode>> childrenfuture;

	// -------- constructors --------

	/**
	 * Create a node.
	 */
	public AbstractTreeNode(ITreeNode parent, AsyncTreeModel model)
	{
		this.parent = parent;
		this.model = model;

		// model.registerNode(this);
	}

	// -------- IComponentTreeNode interface --------

	/**
	 * Called when the node is removed or the tree is closed.
	 */
	public void dispose()
	{
	}

	/**
	 * Get the parent node.
	 */
	public ITreeNode getParent()
	{
		return parent;
	}
	
	/**
	 *  The dirty to set.
	 *  @param dirty The dirty to set
	 */
	public void setParent(ITreeNode parent)
	{
		this.parent = parent;
	}

	/**
	 * Get the child count.
	 */
	public int getChildCount()
	{
		if(children == null && !searching)
		{
			searching = true;
			// System.out.println("searchChildren: "+getId());
			searchChildren();
		}
		return children == null ? 0 : children.size();
	}

	/**
	 * Get the given child.
	 */
	public ITreeNode getChild(int index)
	{
		if(children == null && !searching)
		{
			searching = true;
			// System.out.println("searchChildren: "+getId());
			searchChildren();
		}
		return children == null ? null : (ITreeNode)children.get(index);
	}

	/**
	 * Get the index of a child.
	 */
	public int getIndexOfChild(ITreeNode child)
	{
		if(children == null && !searching)
		{
			searching = true;
			// System.out.println("searchChildren: "+getId());
			searchChildren();
		}
		return children == null ? -1 : children.indexOf(child);
	}

	/**
	 * Check if the node is a leaf.
	 */
	public boolean isLeaf()
	{
		return getChildCount() == 0;
	}

	/**
	 * Refresh the node.
	 * 
	 * @param recurse
	 *            Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse)
	{
//		System.out.println("ATN refresh: "+getId());

		if(!searching)
		{
			searching = true;
			this.recurse = recurse;
			// System.out.println("searchChildren: "+getId());
			searchChildren();
		}
		else
		{
			// If search in progress upgrade to recursive, but do not downgrade.
			this.recurse = this.recurse || recurse;
			dirty = true;
		}
	}

	/**
	 * Get the cached children, i.e. do not start any background processes for
	 * updating the children.
	 */
	public List<ITreeNode> getCachedChildren()
	{
		// Conditional does not work with generic Collections.emptyMap() method?
		return children != null ? children : (List<ITreeNode>) Collections.EMPTY_LIST;
	}

	/**
	 * Get the current children, i.e. start a new update process and provide the
	 * result as a future.
	 */
	public IFuture<List<ITreeNode>> getChildren()
	{
		if(childrenfuture == null)
		{
			childrenfuture = new Future<List<ITreeNode>>();
		}

		IFuture<List<ITreeNode>> ret = childrenfuture;

		// System.out.println("searchChildren: "+getId());

		searchChildren(); // might reset childrenfuture.

		return ret;
	}

	/**
	 * True, if the node has properties that can be displayed.
	 */
	public boolean hasProperties()
	{
		return false;
	}


	// -------- template methods --------

	/**
	 * Get the icon for a node.
	 */
	public abstract byte[] getIcon();

	/**
	 * Get tooltip text.
	 */
	public abstract String getTooltipText();

	/**
	 * Asynchronously search for children. Called once for each node. Should
	 * call setChildren() once children are found.
	 */
	protected abstract void searchChildren();

	/**
	 * Set the children. No children should be represented as empty list to
	 * avoid ongoing search for children.
	 */
	protected void setChildren(List<? extends ITreeNode> newchildren)
	{
//		System.out.println("childs: "+getId()+" "+newchildren.size()+" "+newchildren);
		
		List<ITreeNode> oldcs = children != null ? new ArrayList<ITreeNode>(children) : null;
		List<ITreeNode> newcs = newchildren != null ? new ArrayList<ITreeNode>(newchildren) : null;

		assert false || checkChildren(oldcs, newcs);

		searching = false;
		if(dirty)
		{
			// Restart search when nodes have been added/removed in the mean
			// time.
			dirty = false;
			// System.out.println("searchChildren: "+getId());
			searchChildren();
		} 
		else
		{
			// System.err.println(""+model.hashCode()+" setChildren executing: "+parent+"/"+AbstractTreeNode.this+", "+children+", "+newcs);
			boolean dorecurse = recurse;
			recurse = false;

			if(children == null)
				children = new ArrayList<ITreeNode>();

			// New update algorithm (can cope with changing orderings)
			boolean changed = false;
			// Traverse through source list and remove changed nodes
			int newidx = 0; // Pointer to target list.
			int removed = 0; // Counter for correct tree event index
			List<ITreeNode> rems = new ArrayList<ITreeNode>();
			for(int i = 0; oldcs != null && i < oldcs.size(); i++)
			{
				ITreeNode node = (ITreeNode) oldcs.get(i);
				if(newcs != null && newidx < newcs.size() && node.equals(newcs.get(newidx)))
				{
					// Node at correct position -> move on
					newidx++;
				} 
				else
				{
					// Node removed or moved -> remove (moved nodes will be
					// re-added later at correct position)
					children.remove(node);
					rems.add(node);
					model.fireNodeRemoved(AbstractTreeNode.this, node, i - removed);
					removed++;
					changed = true;
				}
			}

			// Traverse through target list and add missing nodes
			for(int i = 0; newcs != null && i < newcs.size(); i++)
			{
				ITreeNode node = (ITreeNode) newcs.get(i);
				if(i >= children.size() || !node.equals(children.get(i)))
				{
					// set parent to this
					((AbstractTreeNode)node).setParent(this);
					
					children.add(i, node);
					rems.remove(node);
					model.addNode(node);
					model.fireNodeAdded(AbstractTreeNode.this, node, i);
					changed = true;
				}
			}
			
			// Only deregister really removed nodes
			for(ITreeNode node: rems)
			{
				model.deregisterNode(node);
			}
		
			if(changed)
				model.fireNodeChanged(AbstractTreeNode.this);

			if(childrenfuture != null)
			{
				childrenfuture.setResult(new ArrayList<ITreeNode>(children));
				childrenfuture = null;
			}

			expandChildren(dorecurse, children);
		}
	}
	
	protected void expandChildren(boolean dorecurse, List<ITreeNode> children) 
	{
	};

	/**
	 * Check the children for validity. I.e. it is not allowed to have two equal
	 * children in the list or to alter the ordering of existing children.
	 */
	protected boolean checkChildren(List oldcs, List newcs)
	{
		// Check if duplicates are present.
		if (newcs != null && newcs.size() > 1)
		{
			for (int i = 0; i < newcs.size() - 1; i++)
			{
				for (int j = i + 1; j < newcs.size(); j++)
				{
					if (SUtil.equals(newcs.get(i), newcs.get(j)))
					{
						throw new RuntimeException("Found equal children: " + newcs);
					}
				}
			}
		}

		return true;
	}

	/**
	 * Get the model.
	 */
	public AsyncTreeModel getModel()
	{
		return model;
	}

	/**
	 * Add a child and update the tree. Must be called from swing thread.
	 */
	public void addChild(int index, ITreeNode node)
	{
		// Ignore when node already removed.
		if(!model.isZombieNode(node.getId()))
		{
			// set parent to this
			((AbstractTreeNode)node).setParent(this);
			
			if(children == null)
				children = new ArrayList();
			children.add(index, node);
			model.addNode(node);
			model.fireNodeAdded(this, node, index);
			if (searching)
				dirty = true;
			// if(node.getId().toString().startsWith("ANDTest@"))
			// System.out.println("Node added: "+node+", "+children);
		} 
		else
		{
			model.removeZombieNode(node);
		}
	}

	/**
	 * Add a child and update the tree. 
	 */
	// not using addChild(getCachedChildren().size(), node) because
	// could be overridden by subclass and cause loop
	public void addChild(ITreeNode node)
	{
		//// model.registerNode(node);
		//addChild(getCachedChildren().size(), node);
		
		// Ignore when node already removed.
		int index = getCachedChildren().size();
		if(!model.isZombieNode(node.getId()))
		{
			// set parent to this
			((AbstractTreeNode)node).setParent(this);
			
			if(children == null)
				children = new ArrayList();
			children.add(index, node);
			model.addNode(node);
			model.fireNodeAdded(this, node, index);
			if (searching)
				dirty = true;
			// if(node.getId().toString().startsWith("ANDTest@"))
			// System.out.println("Node added: "+node+", "+children);
		} 
		else
		{
			model.removeZombieNode(node);
		}
	}

	/**
	 * Remove a child and update the tree. 
	 */
	public void removeChild(ITreeNode node)
	{
		int index = getIndexOfChild(node);
		if(index != -1)
		{
			// boolean removed =
			children.remove(node);
			// if(node.getId().toString().startsWith("ANDTest@"))
			// System.out.println("removed: "+node+", "+removed);
			model.deregisterNode(node);
			model.fireNodeRemoved(this, node, index);
			if (searching)
				dirty = true;
		} 
		else
		{
			getModel().addZombieNode(node.getId());
		}
	}

	/**
	 * Remove all children.
	 */
	public void removeAllChildren()
	{
		if(children != null && children.size() > 0)
		{
			int[] indices = new int[children.size()];
			for(int i = 0; i < children.size(); i++)
			{
				indices[i] = i;
			}

			children.clear();

			model.fireNodesRemoved(this, children.toArray(new ITreeNode[children.size()]), indices);

			if (searching)
				dirty = true;
		}
	}

	/**
	 * Test if two nodes are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof ITreeNode && SUtil.equals(getId(), ((ITreeNode) obj).getId());
	}

	/**
	 * Generate a has code.
	 */
	public int hashCode()
	{
		return 31 + (getId() != null ? getId().hashCode() : 0);
	}
}
