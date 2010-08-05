package jadex.tools.common.componenttree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
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
	protected void	setChildren(final List children)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				searching	= false;
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
					for(int i=0; i<removed.size(); i++)
					{
						IComponentTreeNode	node	= (IComponentTreeNode)removed.get(i);
						model.deregisterNode(node);
						model.fireNodeRemoved(AbstractComponentTreeNode.this, node, oldcs.indexOf(node));
					}
					model.fireNodeChanged(AbstractComponentTreeNode.this);
				}
				
				if(recurse)
				{
					for(int i=0; children!=null && i<children.size(); i++)
					{
						((IComponentTreeNode)children.get(i)).refresh(recurse);
					}
				}
			}
		});
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
		if(children==null)
			children = new ArrayList();
		children.add(index, node);
		model.registerNode(node);
		model.fireNodeAdded(this, node, index);
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
