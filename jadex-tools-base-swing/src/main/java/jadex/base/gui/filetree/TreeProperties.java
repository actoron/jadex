package jadex.base.gui.filetree;


/**
 *  Struct for storing Tree properties.
 */
public class TreeProperties
{
	//-------- attributes --------
	
	/** The root path entries. */
	protected String[]	roots;
	
	/** The selected node (if any). */
	protected NodePath	selected;
	
	/** The expanded nodes. */
	protected NodePath[] expanded;
	
	//-------- methods --------
	
	/**
	 *  Get the root node.
	 */
	public String[]	getRootPathEntries()
	{
		return roots;
	}
	
	/**
	 *  Get the selected node.
	 */
	public NodePath	getSelectedNode()
	{
		return selected;
	}
	
	/**
	 *  Get the expanded nodes.
	 */
	public NodePath[]	getExpandedNodes()
	{
		return expanded;
	}
	
	/**
	 *  Set the root path entries.
	 */
	public void	setRootPathEntries(String[] roots)
	{
		this.roots	= roots;
	}
	
	/**
	 *  Set the selected node.
	 */
	public void	setSelectedNode(NodePath selected)
	{
		this.selected	= selected;
	}
	
	/**
	 *  Set the expanded nodes.
	 */
	public void	setExpandedNodes(NodePath[] expanded)
	{
		this.expanded	= expanded;
	}
}