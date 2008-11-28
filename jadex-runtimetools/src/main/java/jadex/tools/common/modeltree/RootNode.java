package jadex.tools.common.modeltree;

import jadex.commons.SUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 *  The root node of the explorer tree.
 */
public class RootNode	implements IExplorerTreeNode
{
	//-------- attributes --------
	
	/** The nodes for the directories and jar files of the project. */
	protected List	children;
	
	//-------- constructors --------
	
	/**
	 *  Create a new RootNode.
	 *  Bean constructor.
	 */
	public RootNode()
	{
	}

	/**
	 *  Reset the node to its initial state.
	 */
	public void	reset()
	{
		removeAllChildren();
	}

	//-------- TreeNode interface --------

	/**
	 *  Returns the parent TreeNode of the receiver.
	 *  @return a TreeNode
	 */
	public TreeNode getParent()
	{
		return null;
	}

	/**
	 *  Returns the child TreeNode at index childIndex.
	 *  @param childIndex the index of the child to return
	 *  @return a TreeNode instance
	 */
	public TreeNode getChildAt(int childIndex)
	{
		if(children==null)
			throw new ArrayIndexOutOfBoundsException(childIndex);
		return (TreeNode)children.get(childIndex);
	}

	/**
	 *  Returns the number of children TreeNodes the receiver contains.
	 *  @return the number of children TreeNodes the receiver contains
	 */
	public int getChildCount()
	{
		return children!=null ? children.size() : 0;
	}

	/**
	 *  Returns the index of node in the receivers children. If the receiver
	 *  does not contain node, -1 will be returned.
	 *  @param node
	 *  @return an int.
	 */
	public int getIndex(TreeNode node)
	{
		return children!=null ? children.indexOf(node) : -1;
	}

	/**
	 *  Returns true if the receiver allows children.
	 *  @return a boolean.
	 */
	public boolean getAllowsChildren()
	{
		return true;
	}

	/**
	 *  Returns true if the receiver is a leaf.
	 *  @return a boolean
	 */
	public boolean isLeaf()
	{
		return false;
	}

	/**
	 *  Returns the children of the reciever as an Enumeration.
	 *  @return an Enumeration
	 */
	public Enumeration children()
	{
		return children!=null?Collections.enumeration(children):SUtil.EMPTY_ENUMERATION;
	}

	//-------- methods --------

	/**
	 *  Add a directory or jar file to the model path.
	 */
	// Todo: UrlEntry
	public IExplorerTreeNode	addPathEntry(File file)
	{
		if(file.getClass()!=File.class)
		{
//			System.err.println("Unknown file class: "+file.getClass());
			file	= new File(file.getPath());
		}

		// Check if entry is file or directory.
		IExplorerTreeNode	node = null;
		if(file.exists())
		{
			if(file.isDirectory())
			{
				node	= new DirNode(this, file);
			}
			else
			{
				node	= new JarNode(this, file.getAbsolutePath());
			}
		}

		if(node!=null)
		{
			if(children==null)
				children	= new ArrayList();

			if(!children.contains(node))
				children.add(node);
		}
		
		return node;
	}

	/**
	 *  Remove a path entry from the tree.
	 */
	public void removePathEntry(TreeNode child)
	{
		if(children!=null)
			children.remove(child);
	}

	/**
	 *  Get the path entries.
	 */
	public String[]	getPathEntries()
	{
		String[]	ret	= new String[getChildCount()];
		for(int i=0; i<ret.length; i++)
		{
			TreeNode	node	= getChildAt(i);
//			if(node instanceof DirNode)
//			{
				ret[i]	= ((DirNode)node).getFile().getAbsolutePath();
//			}
//			else
//			{
//				// Todo: UrlNode ???
//			}
		}
		return ret;
	}
	
	/**
	 *  Return a string representation of this element.
	 */
	public String	toString()
	{
		return "ModelExplorer.root";
	}

	/**
	 *  Remove all children of the root node.
	 */
	public void removeAllChildren()
	{
		children	= null;
	}

	/**
	 *  Get the tool tip text.
	 */
	public String getToolTipText()
	{
		return null;
	}
	
	//-------- bean properties --------

	/**
	 *  Get the children of this RootNode.
	 *  @return Returns the children.
	 */
	public List getChildren()
	{
		return children;
	}

	/**
	 *  Set the children of this RootNode.
	 *  @param children The children to set.
	 */
	public void setChildren(List children)
	{
		this.children = children;
	}
}
