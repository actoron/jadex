package jadex.commons.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node of a tree.
 */
public class TreeNode
{
	//-------- attributes --------
	
	/** The user node data. */ 
	protected Object data;

	/** The children. */
	public List<TreeNode> children;

	//-------- constructors --------
	
	/**
	 *  Create a new node.
	 */
	public TreeNode()
	{
	}

	/**
	 *  Create a new node.
	 */
	public TreeNode(Object data)
	{
		this.data = data; 
	}

	/**
	 *  Return the children of node.
	 *  @return The children of node.
	 */
	public List<TreeNode> getChildren()
	{
		return children;
	}

	/**
	 * Sets the children of a Node object.
	 * @param children The list to set.
	 */
	public void setChildren(List<TreeNode> children)
	{
		this.children = children;
	}

	/**
	 *  Returns the number of immediate children of this node.
	 *  @return the number of immediate children.
	 */
	public int getNumberOfChildren()
	{
		return children==null? 0: children.size();
	}

	/**
	 *  Adds a child to the list of children for this node.
	 *  @param child a Node<T> object to set.
	 */
	public void addChild(TreeNode child)
	{
		if(children==null)
			children = new ArrayList();
		children.add(child);
	}

	/**
	 *  Inserts a node at the specified position in the child list. Will
	 *  throw an ArrayIndexOutOfBoundsException if the index does not exist.
	 *  @param index the position to insert at.
	 *  @param child the Node object to insert.
	 *  @throws IndexOutOfBoundsException if thrown.
	 */
	public void insertChildAt(int index, TreeNode child) throws IndexOutOfBoundsException
	{
		if(index==getNumberOfChildren())
		{
			addChild(child);
		}
		else
		{
			if(children==null)
				children = new ArrayList();
			children.add(index, child);
		}
	}

	/**
	 * Remove the Node element at index index of the List.
	 * @param index the index of the element to delete.
	 * @throws IndexOutOfBoundsException if thrown.
	 */
	public void removeChildAt(int index) throws IndexOutOfBoundsException
	{
		children.remove(index);
	}

	/**
	 *  Get the node data.
	 *  @return The node data.
	 */
	public Object getData()
	{
		return this.data;
	}

	/**
	 *  Set the node data.
	 *  @param data The node data.
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(""+getData()).append(",[");
		List children = getChildren();
		if(children!=null)
		{
			for(int i=0; i<children.size(); i++)
			{
				TreeNode node = (TreeNode)children.get(i);
				if(i > 0)
					sb.append(",");
				sb.append(""+node.getData());
				i++;
			}
		}
		sb.append("]").append("}");
		return sb.toString();
	}
}