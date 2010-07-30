package jadex.tools.common.componenttree;

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
		return children==null ? 0 : children.indexOf(child);
	}
	
	/**
	 *  Check if the node is a leaf.
	 */
	public boolean	isLeaf()
	{
		return getChildCount()==0;
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
				AbstractComponentTreeNode.this.children	= children;
				model.fireTreeChanged(AbstractComponentTreeNode.this);
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
		children.add(index, node);
		model.fireNodeAdded(this, node, index);
	}
	
	/**
	 *  Add a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void addChild(IComponentTreeNode node)
	{
		addChild(children.size(), node);
	}
	
	/**
	 *  Remove a child and update the tree.
	 *  Must be called from swing thread.
	 */
	public void removeChild(IComponentTreeNode node)
	{
		int index	= getIndexOfChild(node);
		children.remove(node);
		model.fireNodeRemoved(this, node, index);
	}
}
