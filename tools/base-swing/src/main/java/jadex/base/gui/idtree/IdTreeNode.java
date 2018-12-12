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
	protected Boolean leaf;
	
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
	public IdTreeNode(String key, String name, IdTreeModel<T> tm, Boolean leaf,
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
		
        insert(child, getChildCount());
        
//		IdTreeNode<T> itn = (IdTreeNode<T>)child;
//		tm.addNode(itn);
//		super.add(itn);
//		tm.nodesWereInserted(this, new int[]{getChildCount()-1});
	}
	
	/**
	 *  Insert a new child.
	 *  @param child The child.
	 */
	public void insert(MutableTreeNode child, int index)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(child!=null && child.getParent()!=this)
		{
			IdTreeNode<T> itn = (IdTreeNode<T>)child;
			tm.addNode(itn);
			super.insert(child, index);
			tm.nodesWereInserted(this, new int[]{index});
		}
	}

	/**
	 *  Remove a child.
	 *  @param idx The index.
	 */
	public void remove(int idx)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		IdTreeNode<T> child = (IdTreeNode<T>)getChildAt(idx);
		super.remove(idx);
		if(tm.removeNode(child))
		{
			tm.nodesWereRemoved(this, new int[]{idx}, new TreeNode[]{child});
		}
	}
	
	/**
	 *  Remove a child.
	 *  @param idx The index.
	 */
	public void remove(MutableTreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		int idx = getIndex(child);
		if(tm.removeNode((IdTreeNode<T>)child))
		{
			super.remove(child);
			tm.nodesWereRemoved(this, new int[]{idx}, new TreeNode[]{child});
		}
	}
	
	/**
	 *  Get all children.
	 *  @return An array of children.s
	 */
	public IdTreeNode<T>[] getChildren()
	{
		IdTreeNode<T>[] ret = (IdTreeNode<T>[])new IdTreeNode[getChildCount()];
		for(int i=0; i<getChildCount(); i++)
		{
			ret[i] = (IdTreeNode<T>)getChildAt(i);
		}
		return ret;
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
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Test if node is leaf.
	 *  @return True, if is leaf.
	 */
	public boolean isLeaf()
	{
		return leaf!=null? leaf.booleanValue(): super.isLeaf();
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