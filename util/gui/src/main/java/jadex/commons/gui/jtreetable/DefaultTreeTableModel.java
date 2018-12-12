package jadex.commons.gui.jtreetable;



/**
 *  Default implementation of the TreeTableModel interface.
 */
public class DefaultTreeTableModel	extends AbstractTreeTableModel
{
	//-------- attributes --------

	/** The root node. */
	protected TreeTableNode	root;

	/** The column names. */
	protected String[]	columns;

	/** The editable columns. */
	protected boolean[]	editable;

	//-------- constructors --------

	/**
	 *  Create a new tree table model with the given root node and column names.
	 *  @param root	The root node.
	 *  @param columns	The column names.
	 */
	public DefaultTreeTableModel(TreeTableNode root, String[] columns)
	{
		this(root, columns, new boolean[columns.length]);
	}

	/**
	 *  Create a new tree table model with the given root node and column names.
	 *  @param root	The root node.
	 *  @param columns	The column names.
	 *  @param editable	The editable columns.
	 */
	public DefaultTreeTableModel(TreeTableNode root, String[] columns,
		boolean[] editable)
	{
		super(root);
		this.root	= root;
		this.columns	= columns;
		this.editable	= editable;

		// Hack ??? Set model of root node.
		if(root instanceof DefaultTreeTableNode)
		{
			((DefaultTreeTableNode)root).setModel(this);
		}
	}

	//-------- methods --------

	/**
	 *  Get the class of a column.
	 *  @param column	The column.
	 *  @return The class.
	 */
    public Class getColumnClass(int column)
    {
	 	// Hack !!! Return tree table model class for first column ?!.
    	return column==0 ? TreeTableModel.class : Object.class;
    }

	/**
	 *  Get a child of a node.
	 *  @param node	The node.
	 *  @param index	The index of the child.
	 *  @return The child at the specified index.
	 */
	public Object getChild(Object node, int index)
	{
		return ((TreeTableNode)node).getChildAt(index);
	}

	/**
	 *  Get the number of children of a node.
	 *  @param node	The node.
	 *  @return The number of children of a node.
	 */
	public int getChildCount(Object node)
	{
		return ((TreeTableNode)node).getChildCount();
	}

	
	/**
	 *  Get the number of children of a node.
	 *  @param node	The node.
	 *  @param column	The column.
	 *  @return The node's value at the specified column.
	 */
	public Object getValueAt(Object node, int column)
	{
		return ((TreeTableNode)node).getValue(column);
	}

	/**
	 *  Get the number of columns.
	 *  @return The number of columns.
	 */
	public int getColumnCount()
	{
		return columns.length;
	}

	/**
	 *  Get the name of a column.
	 *  @param column	The column.
	 *  @return The name of the column.
	 */
	public String getColumnName(int column)
	{
		return columns[column];
	}

	/**
	 *  Test if a cell is editable
	 */ 
	public boolean isCellEditable(Object node, int column)
	{ 
		return super.isCellEditable(node, column) || editable[column]; 
	}
}

