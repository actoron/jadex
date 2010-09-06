package jadex.base.gui.componenttree;

import jadex.commons.Future;
import jadex.commons.IFuture;

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
public abstract class AbstractComponentTreeNode	implements IComponentTreeNode 
{
	//-------- attributes --------
	
	/** The parent node. */
	private final IComponentTreeNode	parent;
	
	/** The tree model. */
	private final ComponentTreeModel	model;
	
	/** The tree. */
	// Hack!!! Model should not have access to ui, required for refresh only on expanded nodes.
	private final JTree	tree;
	
	/** The component description. */
	private List	children;
	
	/** Flag to indicate search in progress. */
	private boolean	searching;

	/** Flag to indicate recursive refresh. */
	private boolean	recurse;
	
	//-------- constructors --------
	
	/**
	 *  Create a node.
	 */
	public AbstractComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, JTree tree)
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
	public IComponentTreeNode	getParent()
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
			searchChildren();
		}
		return children==null ? 0 : children.size();
	}
	
	/**
	 *  Get the given child.
	 */
	public IComponentTreeNode	getChild(int index)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(children==null && !searching)
		{
			searching	= true;
			searchChildren();
		}
		return children==null ? null : (IComponentTreeNode)children.get(index);
	}
	
	/**
	 *  Get the index of a child.
	 */
	public int	getIndexOfChild(IComponentTreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(children==null && !searching)
		{
			searching	= true;
			searchChildren();
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
	public void	refresh(boolean recurse)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(!searching)
		{
			searching	= true;
			this.recurse	= recurse;
			searchChildren();
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
	protected abstract void	searchChildren();
	
	/**
	 *  Set the children.
	 *  No children should be represented as empty list to avoid
	 *  ongoing search for children.
	 *  Method may be called from any thread.
	 */
	protected IFuture	setChildren(final List children)
	{
		final Future	ret	= new Future();
		
		// For debugging: todo:remove
		final	RuntimeException	rte;
		try
		{
			throw new RuntimeException();
		}
		catch (RuntimeException e)
		{
			rte	= e;
		}
		
//		System.err.println(""+model.hashCode()+" setChildren queued: "+children);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				boolean	dorecurse	= recurse;
				searching	= false;
				recurse	= false;
				List	oldcs	= AbstractComponentTreeNode.this.children;
				AbstractComponentTreeNode.this.children	= children;
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

				try
				{
					
				if(!added.isEmpty() && !removed.isEmpty())
				{
					for(int i=0; oldcs!=null && i<oldcs.size(); i++)
					{
						model.deregisterNode((IComponentTreeNode)oldcs.get(i));
					}
					for(int i=0; children!=null && i<children.size(); i++)
					{
						model.addNode((IComponentTreeNode)children.get(i));
					}
					System.err.println(""+model.hashCode()+" tree change: "+AbstractComponentTreeNode.this+"#"+AbstractComponentTreeNode.this.hashCode());
					System.err.println(""+model.hashCode()+" added: "+added);
					System.err.println(""+model.hashCode()+" removed: "+removed);
					System.err.println(""+model.hashCode()+" children: "+children);
					System.err.println(""+model.hashCode()+" oldcs: "+oldcs);
					model.fireTreeChanged(AbstractComponentTreeNode.this);			
				}
				else if(!added.isEmpty())
				{
					for(int i=0; i<added.size(); i++)
					{
						IComponentTreeNode	node	= (IComponentTreeNode)added.get(i);
						model.addNode(node);
//						System.err.println(""+model.hashCode()+" setChildren->fireNodeAdded: "+node+", "+added);
						model.fireNodeAdded(AbstractComponentTreeNode.this, node, children.indexOf(node));
					}
					model.fireNodeChanged(AbstractComponentTreeNode.this);
				}
				else if(!removed.isEmpty())
				{
					for(int i=removed.size()-1; i>=0; i--)
					{
						IComponentTreeNode	node	= (IComponentTreeNode)removed.get(i);
						model.deregisterNode(node);
						model.fireNodeRemoved(AbstractComponentTreeNode.this, node, oldcs.indexOf(node));
					}
					model.fireNodeChanged(AbstractComponentTreeNode.this);
				}
				
				}
				catch(RuntimeException e)
				{
					System.err.println("node problem: "+AbstractComponentTreeNode.this+"#"+AbstractComponentTreeNode.this.hashCode());
					System.err.println("added: "+added);
					System.err.println("removed: "+removed);
					System.err.println("children: "+children);
					System.err.println("oldcs: "+oldcs);
					rte.printStackTrace();
					throw e;
				}
				
				if(dorecurse && tree.isExpanded(new TreePath(model.buildTreePath(AbstractComponentTreeNode.this).toArray())))
				{
					for(int i=0; children!=null && i<children.size(); i++)
					{
						((IComponentTreeNode)children.get(i)).refresh(dorecurse);
					}
				}
				
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the model.
	 */
	public ComponentTreeModel	getModel()
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
	public void addChild(int index, IComponentTreeNode node)
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
	public void addChild(IComponentTreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();

		addChild(getChildCount(), node);
	}
	
	/**
	 *  Remove a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void removeChild(IComponentTreeNode node)
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
