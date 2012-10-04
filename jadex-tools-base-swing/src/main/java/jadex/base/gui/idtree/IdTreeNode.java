package jadex.base.gui.idtree;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *  Id tree node.
 */
public class IdTreeNode<T> extends DefaultMutableTreeNode
{
	//-------- attributes --------

	/** The node id. */
	protected String key;
	
	/** The node name. */
	protected String name;
	
	/** The tree model. */
	protected IdTreeModel<T> tm;
	
	/** Flag if is leaf. */
	protected boolean leaf;
	
	/** The icon. */
	protected Icon icon;
	
	/** The tooltip text. */
	protected String tooltip;
	
	/** The artifact info. */
	protected T object;
	
	//-------- constructors --------
	
	/**
	 *  Create a new node.
	 */
	public IdTreeNode(String key, String name, IdTreeModel<T> tm, boolean leaf,
		Icon icon, String tooltip, T object)
	{
		this.key = key;
		this.name = name!=null? name: key;
		this.tm = tm;
		this.leaf = leaf;
		this.icon = icon;
		this.tooltip = tooltip;
		this.object = object;
	}
	
	/**
	 *  Add a new child.
	 *  @param child The child.
	 */
	public void add(MutableTreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		IdTreeNode<T> itn = (IdTreeNode<T>)child;
		tm.addNode(itn);
		super.add(itn);
		tm.nodesWereInserted(this, new int[]{getChildCount()-1});
	}
	
	/**
	 *  Insert a new child.
	 *  @param child The child.
	 */
	public void insert(MutableTreeNode child, int index)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		IdTreeNode<T> itn = (IdTreeNode<T>)child;
		tm.addNode(itn);
		super.insert(child, index);
		tm.nodesWereInserted(this, new int[]{index});
	}

	/**
	 *  Remove a child.
	 *  @param idx The index.
	 */
	public void remove(int idx)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		IdTreeNode<T> child = (IdTreeNode<T>)getChildAt(idx);
		tm.removeNode(child);
		super.remove(idx);
		tm.nodesWereRemoved(this, new int[]{idx}, new TreeNode[]{child});
	}
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return key;
	}

	/**
	 *  Test if node is leaf.
	 *  @return True, if is leaf.
	 */
	public boolean isLeaf()
	{
		return leaf;
	}

	/**
	 *  Get the icon.
	 */
	public Icon getIcon()
	{
		return icon;
	}
	
	/**
	 *  Get the tooltip.
	 */
	public String getTooltipText()
	{
		return tooltip;
	}
	
	/**
	 *  Get the artifact info.
	 *  @return The artifact info.
	 */
	public T getObject()
	{
		return object;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return name;
	}
}