package jadex.tools.common.modeltree;

import jadex.bridge.IJadexAgentFactory;
import jadex.commons.SUtil;

import java.io.File;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

/**
 *  A node representing a file.
 */
public class FileNode implements IExplorerTreeNode
{
	//-------- attributes --------
	
	/** The parent of this node. */
	protected TreeNode	parent;

	/** The file represented by this node. */
	protected File	file;

	/** The date when the file was last modified. */
	protected long	lastmodified;

	/** The valid state. */
	protected boolean	valid;

	/** Flag indicating if the current file has been checked. */
	protected boolean	checked;

	/** The node checker. */
	protected INodeFunctionality nof;

	//-------- constructors --------
	
	/**
	 *  Create a new file node.
	 *  @param parent
	 *  @param file
	 */
	public FileNode(TreeNode parent, File file, INodeFunctionality nof)
	{
		this(parent, file, nof, true);
	}
	
	/**
	 *  Create a new file node.
	 *  @param parent
	 *  @param file
	 */
	public FileNode(TreeNode parent, File file, INodeFunctionality nof, boolean valid)
	{
		this.parent = parent;
		this.file = file;
		this.lastmodified	= Long.MIN_VALUE;
		this.nof = nof;
		this.valid = valid;
	}

	/**
	 *  Create a new FileNode.
	 *  Bean constructor.
	 */
	public FileNode()
	{
	}

	//-------- TreeNode interface --------

	/**
	 * Returns the child TreeNode at index childIndex.
	 * @param childIndex the index of the child to return
	 * @return a TreeNode instance
	 */
	public TreeNode getChildAt(int childIndex)
	{
		throw new ArrayIndexOutOfBoundsException(childIndex);
	}

	/**
	 * Returns the number of children TreeNodes the receiver contains.
	 * @return the number of children TreeNodes the receiver contains
	 */
	public int getChildCount()
	{
		return 0;
	}

	/**
	 * Returns the parent TreeNode of the receiver.
	 * @return a TreeNode
	 */
	public TreeNode getParent()
	{
		return parent;
	}

	/**
	 * Returns the index of node in the receivers children. If the receiver
	 * does not contain node, -1 will be returned.
	 * @param node
	 * @return an int.
	 */
	public int getIndex(TreeNode node)
	{
		return -1;
	}

	/**
	 * Returns true if the receiver allows children.
	 * @return an int.
	 */
	public boolean getAllowsChildren()
	{
		return false;
	}

	/**
	 * Returns true if the receiver is a leaf.
	 * @return a boolean
	 */
	public boolean isLeaf()
	{
		return true;
	}

	/**
	 * Returns the children of the reciever as an Enumeration.
	 * @return an Enumeration
	 */
	public Enumeration children()
	{
		return SUtil.EMPTY_ENUMERATION;
	}

	//-------- methods --------

	/**
	 *  Get the file represented by this node.
	 */
	public File getFile()
	{
		return this.file;
	}

	/**
	 * Return the string reoresentation of this node.
	 * @return its name
	 */
	public String toString()
	{
		return file.getName();
	}

	/**
	 *  Check if this object is equal to another object.
	 */
	public boolean equals(Object obj)
	{
		return (obj instanceof FileNode) && this.file.getAbsolutePath().equals(((FileNode)obj).file.getAbsolutePath());
	}

	/**
	 *  Return the hash code for this element. 
	 */
	public int hashCode()
	{
		return file.getAbsolutePath().hashCode();
	}

	/**
	 *  Reset the state of the node.
	 *  After the reset, the next call to refresh will compute a new valid state. 
	 */
	public void	uncheck()
	{
		this.checked	= false;
		this.lastmodified	= Long.MIN_VALUE;
	}

	/**
	 *  Update the node (when the file has changed).
	 *  @return true, when a change has been detected.
	 */
	public boolean	refresh()
	{
		boolean	changed	= doRefresh();
		
		// When file has changed, reset checked state
		// (will be checked on 2nd run to improve perceived speed).
		if(changed)
		{
			checked	= false;
		}
		// When checking has been disabled recently, set checked to false.
		//else if(checked && !valid && !getRootNode().isChecking())
		else if(checked && !getRootNode().isChecking())
		{
			changed	= true;
			this.checked	= false;
		}
		// Do check on 2nd run to improve perceived speed.
		else if(!checked && getRootNode().isChecking())
		{
			//boolean	state	= getState();
			boolean	state	= isValid();
			changed	= doCheck();
			checked	= true;

			// Reset parent state to be rechecked.
			//if(changed || state!=getState())
			if(changed || state!=isValid())
			{
				if(getParent() instanceof FileNode)
				{
//					System.out.println("recheck parent "+getParent()+", "+getState());
					((FileNode)getParent()).setChecked(false);
				}
			}

			// Execute valid changed action.
			if(changed)
			{
				INodeAction action = getRootNode().getAction(this.getClass());
				if(action!=null)
					action.validStateChanged(this, valid);
			}
		}
		return changed;
	}

	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 */
	protected boolean doRefresh()
	{
		return nof.refresh(this);
	}
	
	/**
	 *  Actualy perform the check.
	 *  Can be overridden by subclasses.
	 *  @return True, if the node has changed since last check (if any).
	 */
	protected boolean	doCheck()
	{
		boolean	oldvalid	= this.valid;
		try
		{
			this.valid = nof==null? true: nof.check(this);
		}
		catch(Exception e)
		{
			this.valid	= false;
		}
		return this.valid!=oldvalid;
	}
	
	/** 
	 * @return the icon for this node
	 * @see jadex.tools.common.modeltree.IExplorerTreeNode#getIcon()
	 */
	public Icon getIcon()
	{
		return nof.getIcon(this);
	}

	/**
	 *  Return the valid state of the node for display purposes.
	 * /
	public boolean getState()
	{
		//return valid;// || !checked;
		return valid;// || !getRootNode().isChecking();
	}*/

	/**
	 *  Return the tooltip text for the node (if any).
	 */
	public String getToolTipText()
	{
		return getFile().getAbsolutePath();
	}

	/**
	 *  Get the root node of this node.
	 */
	public RootNode	getRootNode()
	{
		TreeNode	node	= this;
		while(node.getParent()!=null)
			node	= node.getParent();
		
		return node instanceof RootNode ? (RootNode)node : null;
	}

	//-------- bean property accessors --------
	
	/**
	 *  Set the parent of this FileNode.
	 *  @param parent The parent to set.
	 */
	public void setParent(TreeNode parent)
	{
		this.parent = parent;
	}

	/**
	 *  Set the file of this FileNode.
	 *  @param file The file to set.
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 *  Get the checked of this FileNode.
	 *  @return Returns the checked.
	 */
	public boolean isChecked()
	{
		return checked;
	}

	/**
	 *  Set the checked of this FileNode.
	 *  @param checked The checked to set.
	 */
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	/**
	 *  Get the lastmodified of this FileNode.
	 *  @return Returns the lastmodified.
	 */
	public long getLastmodified()
	{
		return lastmodified;
	}

	/**
	 *  Set the lastmodified of this FileNode.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastmodified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the valid of this FileNode.
	 *  @return Returns the valid.
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 *  Set the valid of this FileNode.
	 *  @param valid The valid to set.
	 */
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}
	
	/**
	 *  Get the node functionality.
	 *  @return The node functionality.
	 */
	public INodeFunctionality getNodeFunctionality()
	{
		return nof;
	}
	
	/**
	 *  Get the node functionality.
	 *  @return The node functionality.
	 */
	public void setNodeFunctionality(INodeFunctionality nof)
	{
		this.nof = nof;
	}
	
	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 */
	public IJadexAgentFactory getAgentFactory()
	{
		return ((IExplorerTreeNode)parent).getAgentFactory();
	}
}
