package jadex.tools.common.modeltree;

import jadex.commons.SUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *  A node representing a file.
 */
public class FileNode implements IExplorerTreeNode
{
	//-------- constants --------
	
	/** Node id counter. */
	protected static int	COUNTER	= 0;
	
	//-------- attributes --------
	
	/** The parent of this node. */
	protected IExplorerTreeNode	parent;

	/** The file represented by this node. */
	protected File	file;

	/** The relative file name. */
	protected String	relative;

	/** Custom properties used by different views (e.g. starter, test center). */
	protected Map properties;

	//-------- constructors --------
	
	/**
	 *  Create a new file node.
	 *  @param parent
	 *  @param file
	 */
	public FileNode(IExplorerTreeNode parent, File file)
	{
		this.parent = parent;
		this.file = file;
		this.relative	= convertPathToRelative(file);
		this.properties	= new HashMap();
	}

	/**
	 *  Create a new FileNode.
	 *  Bean constructor.
	 */
	public FileNode()
	{
	}

	//-------- TreeNode interface --------

//	/**
//	 * Returns the child TreeNode at index childIndex.
//	 * @param childIndex the index of the child to return
//	 * @return a TreeNode instance
//	 */
//	public TreeNode getChildAt(int childIndex)
//	{
//		throw new ArrayIndexOutOfBoundsException(childIndex);
//	}
//
//	/**
//	 * Returns the number of children TreeNodes the receiver contains.
//	 * @return the number of children TreeNodes the receiver contains
//	 */
//	public int getChildCount()
//	{
//		return 0;
//	}
//
	/**
	 * Returns the parent TreeNode of the receiver.
	 * @return a TreeNode
	 */
	public IExplorerTreeNode getParent()
	{
		return parent;
	}
//
//	/**
//	 * Returns the index of node in the receivers children. If the receiver
//	 * does not contain node, -1 will be returned.
//	 * @param node
//	 * @return an int.
//	 */
//	public int getIndex(TreeNode node)
//	{
//		return -1;
//	}
//
//	/**
//	 * Returns true if the receiver allows children.
//	 * @return an int.
//	 */
//	public boolean getAllowsChildren()
//	{
//		return false;
//	}
//
//	/**
//	 * Returns true if the receiver is a leaf.
//	 * @return a boolean
//	 */
//	public boolean isLeaf()
//	{
//		return true;
//	}
//
//	/**
//	 * Returns the children of the reciever as an Enumeration.
//	 * @return an Enumeration
//	 */
//	public Enumeration children()
//	{
//		return SUtil.EMPTY_ENUMERATION;
//	}

	//-------- methods --------

	/**
	 *  Get the relative path.
	 */
	public String	getRelativePath()
	{
		return this.relative;
	}

	/**
	 *  Set the relative path.
	 */
	public void	setRelativePath(String relative)
	{
		this.relative	= relative;
	}

	/**
	 *  Get the file represented by this node.
	 */
	public File getFile()
	{
		if(file==null)
		{
			IExplorerTreeNode	parent	= getParent();
			JarNode	node	= null;
			while(node==null && parent!=null)
			{
				if(parent instanceof JarNode)
					node	= (JarNode)parent;
				parent	= parent.getParent();
			}
			
			if(node!=null)
			{
				file	= ((JarAsDirectory)node.getFile()).getFile(relative);
			}
			else
			{
				file	= new File(relative);
			}
		}
		return this.file;
	}

	/**
	 * Return the string reoresentation of this node.
	 * @return its name
	 */
	public String toString()
	{
		return getFile().getName();
	}

	/**
	 *  Return the tooltip text for the node (if any).
	 */
	public String getToolTipText()
	{
		return getFile().getAbsolutePath();
	}

//	/**
//	 *  Get the root node of this node.
//	 */
//	public RootNode	getRootNode()
//	{
//		IExplorerTreeNode	node	= this;
//		while(node.getParent()!=null)
//			node	= node.getParent();
//		
//		return node instanceof RootNode ? (RootNode)node : null;
//	}

	//-------- bean property accessors --------
	
	/**
	 *  Set the parent of this FileNode.
	 *  @param parent The parent to set.
	 */
	public void setParent(IExplorerTreeNode parent)
	{
		this.parent = parent;
	}

	/**
	 *  Set the file of this FileNode.
	 *  @param file The file to set.
	 * /
	public void setFile(File file)
	{
		this.file = file;
	}
	
	/**
	 *  Get the properties of the node.
	 */
	public Map	getProperties()
	{
		return this.properties;
	}

	/**
	 *  Set the properties of the node.
	 *  Bean setter method.
	 */
	public void	setProperties(Map properties)
	{
		this.properties	= properties;
	}

	/**
	 *  Get the corresponding relative path for a file.
	 *  Handles jars specially.
	 */
	protected String convertPathToRelative(File file)
	{
		String	ret;
		if(file instanceof JarAsDirectory)
		{
			JarAsDirectory	jar	= (JarAsDirectory) file;
			if(jar.getZipEntry()!=null)
				ret	= jar.getZipEntry().getName();
			else
				ret	= SUtil.convertPathToRelative(jar.getJarPath());
		}
		else
		{
			ret	= file!=null ? SUtil.convertPathToRelative(file.getAbsolutePath()) : null;
		}
		return ret;
	}
}
