package jadex.commons.gui.jtreetable;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;


/**
 *  Default implementation for tree table nodes.
 *  Based on swings mutable tree node implementation.
 */
public class DefaultTreeTableNode	extends DefaultMutableTreeNode
	implements TreeTableNode
{
	//-------- attributes --------

	/** The node type identifier. */
	protected TreeTableNodeType	type;

	/** The values. */
	protected Map	values;

	/** The model, used to generate events. */
	protected DefaultTreeTableModel	model;

	//-------- constructors --------

	/**
	 *  Create a tree table node with single initial value.
	 *  @param type	The node type identifier.
	 *  @param object	The object represented by this node.
	 */
	public DefaultTreeTableNode(TreeTableNodeType type, Object object)
	{
		this(type, object, new HashMap());
		setValue(0, object);
	}

	/**
	 *  Create a tree table node with initial values.
	 *  @param type	The node type identifier.
	 *  @param object	The object represented by this node.
	 *  @param values	The initial display values.
	 */
	public DefaultTreeTableNode(TreeTableNodeType type, Object object, Map values)
	{
		super(object);
		this.type	= type;
		this.values	= values;
	}

	//-------- methods --------

	/**
	 *  Get the node type.
	 *  @return The node type identifier.
	 */
	public TreeTableNodeType	getType()
	{
		return this.type;
	}

	/**
	 *  Check if this node is an instance of the given
	 *  treetable node type.
	 *  @param type	The treetabel node type.
	 */
	public boolean	instanceOf(TreeTableNodeType type)
	{
		boolean ret	= false;
		TreeTableNodeType	mytype	= this.type;
		while(!ret && mytype!=null)
		{
			ret	= mytype.equals(type);
			mytype	= mytype.getSupertype();
		}
		return ret;
	}

	/**
	 *  Get the value at a specific column.
	 *  @param column	The column.
	 *  @return The value or empty string, when column index is too large.
	 */
	public Object	getValue(int column)
	{
		return type.getColumns().length>column && !type.isColumnExcluded(column)
			? values.get(type.getColumns()[column]) : "";
	}

	/**
	 *  Get all values at once.
	 *  @return	The values.
	 */
	public Map	getValues()
	{
		return values;
	}

	/**
	 *  Set the value at a specific column.
	 *  @param column	The column.
	 *  @param value	The new value.
	 */
	public void	setValue(int column, Object value)
	{
		values.put(type.getColumns()[column], value);

		if(getModel()!=null && (getParent()==null
			|| getParent() instanceof DefaultMutableTreeNode))
		{
			DefaultMutableTreeNode	parent	= (DefaultMutableTreeNode)getParent();
			getModel().fireTreeNodesChanged(this,
				parent==null ? new Object[0] : parent.getPath(),
				new int[]{parent==null ? 0 : parent.getIndex(this)},
				new Object[]{this});
		}
	}

	/**
	 *  Set all values at once.
	 *  @param values	The new values.
	 */
	public void	setValues(Map values)
	{
		this.values	= values;

		if(getModel()!=null && (getParent()==null
			|| getParent() instanceof DefaultMutableTreeNode))
		{
			DefaultMutableTreeNode	parent	= (DefaultMutableTreeNode)getParent();
			getModel().fireTreeNodesChanged(this,
				parent==null ? new Object[]{this} : parent.getPath(),
				new int[]{parent==null ? 0 : parent.getIndex(this)},
				new Object[]{this});
		}
	}

	/**
	 *  Adds <code>child</code> to the receiver at <code>index</code>.
	 *  <code>child</code> will be messaged with <code>setParent</code>.
	 *  Overridden to generate tree event.
	 */
	public void insert(MutableTreeNode child, int index)
	{
		super.insert(child, index);
		if(getModel()!=null)
		{
			if(child instanceof DefaultTreeTableNode)
			{
				((DefaultTreeTableNode)child).setModel(getModel());
			}
			getModel().fireTreeNodesInserted(this, this.getPath(),
				new int[]{index}, new Object[]{child});
		}
	}

	/**
	 *  Removes the child at <code>index</code> from the receiver.
	 *  Overridden to generate tree event.
	 */
	public void remove(int index)
	{
		Object	child	= getChildAt(index);
		if(getModel()!=null)
		{
			getModel().fireTreeNodesRemoved(this, this.getPath(),
				new int[]{index}, new Object[]{child});
		}
		super.remove(index);
	}
	
	// Does not work, don't know why.
	/**
	 *  Removes the child at from the receiver.
	 *  Overridden to generate tree event.
	 * /
	public void remove(MutableTreeNode child)
	{
		int index = getIndex(child);
		super.remove(child);
		if(getModel()!=null)
		{
			getModel().fireTreeNodesRemoved(this, this.getPath(),
				new int[]{index}, new Object[]{child});
		}
	}*/

	/**
	 *  Get a child for the specified user object.
	 *  @param obj	The user object.
	 *  @return A child node with a user object equal
	 *    to the the specified object (if any).
	 */
	public DefaultTreeTableNode	getChild(Object obj)
	{
		DefaultTreeTableNode	child	= null;
		for(int i=0; i<getChildCount() && child ==null; i++)
		{
			if(getChildAt(i) instanceof DefaultTreeTableNode)
			{
				child	= (DefaultTreeTableNode)getChildAt(i);
				if(child.getUserObject()==null || !child.getUserObject().equals(obj))
				{
					child	= null;
				}
			}
		}
		return child;
	}

	/**
	 *  Get all children of the node.
	 */
	public TreeTableNode[]	getChildren()
	{
		TreeTableNode[]	children	= new TreeTableNode[getChildCount()];
		for(int i=0; i<children.length; i++)
		{
			children[i]	= (TreeTableNode)getChildAt(i);
		}
		return children;
	}

	//-------- internal methods --------

	/**
	 *  Set the model.
	 *  @param model	The model.
	 */
	protected void	setModel(DefaultTreeTableModel model)
	{
		this.model	= model;
	}

	/**
	 *  Get the model.
	 */
	protected DefaultTreeTableModel	getModel()
	{
		if(this.model==null && getParent() instanceof DefaultTreeTableNode)
		{
			this.model	= ((DefaultTreeTableNode)getParent()).getModel();
		}
		return this.model;
	}

	/**
	 *  Get a string representation of this node.
	 */
	public String	toString()
	{
		return ""+getValue(0);
	}
}

