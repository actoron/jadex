package jadex.base.gui.filetree;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.commons.SUtil;

/**
 *  Store a path in the project for selected or expanded nodes.
 */
public class NodePath
{
	//-------- attributes --------
	
	/** The path entry of the root node (stored as index to distinguish between paths added twice). */
	protected int entry;
	
	/** The path items to navigate to the node. */
	protected String[]	path;
	
	//-------- constructors --------
	
	/**
	 *  Create a node path.
	 */
	public NodePath()
	{
	}
	
	/**
	 *  Create a node path.
	 */
	public NodePath(int entry, String[] path)
	{
//		if(entry==-1)
//		{
//			Thread.dumpStack();
//			throw new IllegalArgumentException();
//		}
		this.entry	= entry;
		this.path	= path;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the entry index.
	 */
	public int getEntry()
	{
		return entry;
	}

	/**
	 *  Set the entry index.
	 */
	public void setEntry(int entry)
	{
//		if(entry==-1)
//		{
//			Thread.dumpStack();
//			throw new IllegalArgumentException();
//		}
		this.entry = entry;
	}

	/**
	 *  Get the path.
	 */
	public String[] getPath()
	{
		return path;
	}

	/**
	 *  Set the path.
	 */
	public void setPath(String[] path)
	{
		this.path = path;
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + entry;
		result = prime * result + Arrays.hashCode(path);
		return result;
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		boolean	ret	= this==obj;
		if(!ret && obj instanceof NodePath)
		{
			NodePath other = (NodePath)obj;
			ret	= getEntry()==other.getEntry() && Arrays.equals(getPath(), other.getPath());
		}
		return ret;
	}
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return "NodePath("+entry+": "+SUtil.arrayToString(path)+")";
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create a node path for a given node.
	 */
	public static NodePath	createNodePath(ISwingTreeNode node)
	{
		if(node instanceof RootNode)
			System.out.println("wurksn");
		RootNode	root	= null;
		List	path	= new LinkedList();
		while(root==null)
		{
			if(node.getParent() instanceof RootNode)
			{
				root	= (RootNode)node.getParent();
			}
			else
			{
				path.add(0, ((IFileNode)node).getFileName());
				node	= node.getParent();
			}
		}
		return new NodePath(root.getIndex(node), (String[])path.toArray(new String[path.size()]));
	}
}
