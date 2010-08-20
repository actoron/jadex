package jadex.tools.common.componenttree;

import jadex.commons.Future;
import jadex.commons.IFuture;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;


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
	public AbstractComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model)
	{
		this.parent	= parent;
		this.model	= model;
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
		return getChildCount()==0;
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void	refresh(boolean recurse)
	{
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
	 *  Test if a node is a child.
	 */
	public boolean isChild(IComponentTreeNode node)
	{
		return children!=null && children.contains(node);
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
		final	RuntimeException	rte;
		try
		{
			throw new RuntimeException();
		}
		catch (RuntimeException e)
		{
			rte	= e;
		}
		
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
						model.registerNode((IComponentTreeNode)children.get(i));
					}
					model.fireTreeChanged(AbstractComponentTreeNode.this);					
				}
				else if(!added.isEmpty())
				{
					for(int i=0; i<added.size(); i++)
					{
						IComponentTreeNode	node	= (IComponentTreeNode)added.get(i);
						model.registerNode(node);
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
				
				if(dorecurse)
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
	 *  Add a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void addChild(int index, IComponentTreeNode node)
	{
		// Ignore when node already removed.
		if(!model.isZombieNode(node.getId()))
		{
			if(children==null)
				children = new ArrayList();
			children.add(index, node);
			model.registerNode(node);
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
		addChild(getChildCount(), node);
	}
	
	/**
	 *  Remove a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void removeChild(IComponentTreeNode node)
	{
		int index	= getIndexOfChild(node);
		if(index!=-1)
		{
			children.remove(node);
			model.deregisterNode(node);
			model.fireNodeRemoved(this, node, index);
		}
	}
}
