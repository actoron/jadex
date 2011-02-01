package jadex.base.gui.asynctree;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;


/**
 *  Basic node object.
 */
public abstract class AbstractTreeNode	implements ITreeNode 
{
	//-------- attributes --------
	
	/** The parent node. */
	protected final ITreeNode	parent;
	
	/** The tree model. */
	protected final AsyncTreeModel	model;
	
	/** The tree. */
	// Hack!!! Model should not have access to ui, required for refresh only on expanded nodes.
	protected final JTree	tree;
	
	/** The component description. */
	private List	children;
	
	/** Flag to indicate search in progress. */
	protected boolean	searching;

	/** Flag to indicate recursive refresh. */
	protected boolean	recurse;
	
	//-------- constructors --------
	
	/**
	 *  Create a node.
	 */
	public AbstractTreeNode(ITreeNode parent, AsyncTreeModel model, JTree tree)
	{
		this.parent	= parent;
		this.model	= model;
		this.tree	= tree;
	}
	
	//-------- IComponentTreeNode interface --------
	
	/**
	 *  Called when the node is removed or the tree is closed.
	 */
	public void	dispose()
	{
	}
	
	/**
	 *  Get the parent node.
	 */
	public ITreeNode	getParent()
	{
		return parent;
	}
	
	/**
	 *  Get the child count.
	 */
	public int	getChildCount()
	{
		assert SwingUtilities.isEventDispatchThread();

		if(children==null && !searching)
		{
			searching	= true;
			searchChildren(false);
		}
		return children==null ? 0 : children.size();
	}
	
	/**
	 *  Get the given child.
	 */
	public ITreeNode	getChild(int index)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(children==null && !searching)
		{
			searching	= true;
			searchChildren(false);
		}
		return children==null ? null : (ITreeNode)children.get(index);
	}
	
	/**
	 *  Get the index of a child.
	 */
	public int	getIndexOfChild(ITreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(children==null && !searching)
		{
			searching	= true;
			searchChildren(false);
		}
		return children==null ? -1 : children.indexOf(child);
	}
	
	/**
	 *  Check if the node is a leaf.
	 */
	public boolean	isLeaf()
	{
		assert SwingUtilities.isEventDispatchThread();

		return getChildCount()==0;
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void	refresh(boolean recurse, boolean force)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(!searching)
		{
			searching	= true;
			this.recurse	= recurse;
			searchChildren(force);
		}
		else
		{
			// If search in progress upgrade to recursive, but do not downgrade.
			this.recurse	= this.recurse || recurse;
		}
	}
	
	/**
	 *  Get the cached children, i.e. do not start any background processes for updating the children.
	 */
	public List	getCachedChildren()
	{
		assert SwingUtilities.isEventDispatchThread();

		return children!=null ? children : Collections.EMPTY_LIST;
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return false;
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
	public abstract Icon	getIcon();

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected abstract void	searchChildren(boolean force);
	
	/**
	 *  Set the children.
	 *  No children should be represented as empty list to avoid
	 *  ongoing search for children.
	 *  Method may be called from any thread.
	 */
	protected IFuture	setChildren(final List children)
	{
		final Future	ret	= new Future();
		
//		// For debugging: todo:remove
//		final	RuntimeException	rte;
//		try
//		{
//			throw new RuntimeException();
//		}
//		catch (RuntimeException e)
//		{
//			rte	= e;
//		}
//		assert false || checkChildren(children);
		
//		System.err.println(""+model.hashCode()+" setChildren queued: "+children);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				boolean	dorecurse	= recurse;
				searching	= false;
				recurse	= false;
				
				List	oldcs	= AbstractTreeNode.this.children;
				AbstractTreeNode.this.children	= new ArrayList(children);
				List	added	= new ArrayList();
				List	removed	= new ArrayList();
				if(oldcs!=null)
				{
					removed.addAll(oldcs);
				}
				if(children!=null)
				{
					added.addAll(children);
					removed.removeAll(children);
				}
				if(oldcs!=null)
				{
					added.removeAll(oldcs);
				}

//				try
//				{
					/*if(!added.isEmpty() && !removed.isEmpty())
					{
						for(int i=0; oldcs!=null && i<oldcs.size(); i++)
						{
							model.deregisterNode((IComponentTreeNode)oldcs.get(i));
						}
						for(int i=0; children!=null && i<children.size(); i++)
						{
							model.addNode((IComponentTreeNode)children.get(i));
						}
//						System.err.println(""+model.hashCode()+" tree change: "+AbstractComponentTreeNode.this+"#"+AbstractComponentTreeNode.this.hashCode());
//						System.err.println(""+model.hashCode()+" added: "+added);
//						System.err.println(""+model.hashCode()+" removed: "+removed);
//						System.err.println(""+model.hashCode()+" children: "+children);
//						System.err.println(""+model.hashCode()+" oldcs: "+oldcs);
//						rte.printStackTrace();
						model.fireTreeChanged(AbstractComponentTreeNode.this);
					}*/
					/*else*/ if(!removed.isEmpty())
					{
						for(int i=removed.size()-1; i>=0; i--)
						{
							ITreeNode	node	= (ITreeNode)removed.get(i);
							model.deregisterNode(node);
							model.fireNodeRemoved(AbstractTreeNode.this, node, oldcs.indexOf(node));
						}
						if(added.isEmpty())
							model.fireNodeChanged(AbstractTreeNode.this);
					}
					/*else*/ if(!added.isEmpty())
					{
						for(int i=0; i<added.size(); i++)
						{
							ITreeNode	node	= (ITreeNode)added.get(i);
							model.addNode(node);
							model.fireNodeAdded(AbstractTreeNode.this, node, children.indexOf(node));
						}
						model.fireNodeChanged(AbstractTreeNode.this);
					}
//				}
//				catch(RuntimeException e)
//				{
//					System.err.println("node problem: "+AbstractComponentTreeNode.this+"#"+AbstractComponentTreeNode.this.hashCode());
//					System.err.println("added: "+added);
//					System.err.println("removed: "+removed);
//					System.err.println("children: "+children);
//					System.err.println("oldcs: "+oldcs);
//					rte.printStackTrace();
//					throw e;
//				}
				
				if(dorecurse && tree.isExpanded(new TreePath(model.buildTreePath(AbstractTreeNode.this).toArray())))
				{
					for(int i=0; children!=null && i<children.size(); i++)
					{
						((ITreeNode)children.get(i)).refresh(dorecurse, false);
					}
				}
				
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check the children for validity.
	 *  I.e. it is not allowed to have two equal childrens in the list.
	 */
	protected boolean checkChildren(List children)
	{
		// Called by assert so throw exception when called and invalid.
		if(children!=null && children.size()>1)
		{
			for(int i=0; i<children.size()-1; i++)
			{
				for(int j=i+1; j<children.size(); j++)
				{
					if(SUtil.equals(children.get(i), children.get(j)))
					{
						throw new RuntimeException("Found equal children: "+children);
					}
				}
			}
		}
		return true;
	}

	/**
	 *  Get the model.
	 */
	public AsyncTreeModel	getModel()
	{
		return model;
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
	public void addChild(int index, ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();

		// Ignore when node already removed.
		if(!model.isZombieNode(node.getId()))
		{
			if(children==null)
				children = new ArrayList();
			children.add(index, node);
			model.addNode(node);
			model.fireNodeAdded(this, node, index);
		}
		else
		{
			model.deregisterNode(node);
		}
	}
	
	/**
	 *  Add a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void addChild(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();

		addChild(getChildCount(), node);
	}
	
	/**
	 *  Remove a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void removeChild(ITreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();

		int index	= getIndexOfChild(node);
		if(index!=-1)
		{
			children.remove(node);
			model.deregisterNode(node);
			model.fireNodeRemoved(this, node, index);
		}
	}
}
