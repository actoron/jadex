package jadex.commons.gui.jtreetable;

import java.util.Map;


/**
 *  A tree table node, that has as name its number.
 */
public class SetTreeTableNode	extends DefaultTreeTableNode
{
	//-------- constructors --------

	/**
	 *  Create a tree table node with initial values.
	 *  @param type	The node type identifier.
	 *  @param values	The initial values.
	 */
	public SetTreeTableNode(TreeTableNodeType type, Map values)
	{
		super(type, null, values);
	}

	//-------- methods --------

	/**
	 *  Get the value at a specific column.
	 *  @param column	The column.
	 *  @return The value or empty string, when column index is too large.
	 */
	public Object	getValue(int column)
	{
		if(column==0 && getParent()!=null)
			return	""+getParent().getIndex(this);
		else
			return super.getValue(column);
	}

	/**
	 *  Get the user value.
	 *  @return The value.
	 */
	public Object	getUserObject()
	{
		return toString();
	}

	/**
	 *  Get the node name.
	 *  @return The name of the node.
	 */
	public String	toString()
	{
		if(getParent()!=null)
			return	""+getParent().getIndex(this);
		else
			return super.toString();
	}
}

