package jadex.tools.common.modeltree;

import jadex.bridge.IJadexAgentFactory;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

/**
 *  The root node of the explorer tree.
 */
public class RootNode	implements IExplorerTreeNode
{
	//-------- attributes --------
	
	/** The nodes for the directories and jar files of the project. */
	protected List	children;

	/** Filter for specifying which children should be shown. */
	protected FileFilter	filter;

	/** Is integrity checking enabled? */
	protected boolean	checking;

	/** The node actions (node class -> action). */
	protected Map actions;
	
	/** The node functionality. */
	protected INodeFunctionality nof;
	
	/** The agent factory. */
	protected IJadexAgentFactory agentfactory;
	
	//-------- constructors --------

	/**
	 *  Create a root node for file filter.
	 */
	public RootNode(FileFilter filter, INodeFunctionality nof)
	{
		this.filter = filter;
		this.checking	= true;
		this.actions = SCollection.createHashMap();
		this.nof = nof;
	}
	
	/**
	 *  Copy settings from another node.
	 *  @param source The source.
	 */
	public void copyFrom(RootNode source)
	{
		initFilter(source.getFileFilter());
		this.actions = (Map)((HashMap)source.actions).clone();
		//this.nof = source.nof;
	}
	
	/**
	 *  Create a new RootNode.
	 *  Bean constructor.
	 */
	public RootNode()
	{
		this.actions = SCollection.createHashMap();
	}

	/**
	 *  Reset the node to its initial state.
	 */
	public void	reset()
	{
		removeAllChildren();
		this.checking	= true;
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
		IExplorerTreeNode	node = nof.createNode(this, file);

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
	 *  Reset the state of the node.
	 *  After the reset, the next call to refresh will compute a new valid state. 
	 */
	public void	uncheck()
	{
		// Empty default impl.
	}

	/**
	 *  Update the node (when the file has changed).
	 *  @return true, when a change has been detected.
	 */
	public boolean	refresh()
	{
		// Empty default impl.
		return false;
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
	 *  Get the icon.
	 */
	public Icon getIcon()
	{
		return nof.getIcon(this);
	}

	/**
	 *  Get the tool tip text.
	 */
	public String getToolTipText()
	{
		return null;
	}

	/**
	 *  Get the filter of this RootNode.
	 *  @return Returns the filter.
	 */
	// Hack??? Needed for directory nodes, cannot be given to nodes, as not beanynizable.
	public FileFilter getFileFilter()
	{
		return filter;
	}
	
	/**
	 *  Set the file filter.
	 *  // Problem: Must not be named setFileFilter() as otherwise
	 *  // nuggets will try to persit the file filter.  
	 *  @param filter The filter.
	 */
	public void setNewFileFilter(FileFilter filter)
	{
		this.filter = filter;
	}
	
	/**
	 *  Set the filter of this RootNode.
	 *  Called init filter to avoid bean property.  
	 *  @return Returns the filter.
	 */
	// Hack??? Because FileFilter impls not beanynizable.
	public void	initFilter(FileFilter filter)
	{
		this.filter	= filter;
	}
	
	/**
	 *  Rebuild the complete tree.
	 * /
	public void refreshAll()
	{
		for(int i=0; children!=null && i<children.size(); i++)
		{
			FileNode node = (FileNode)children.get(i);
			if(node instanceof DirNode)
				((DirNode)node).refreshAll();
			//else
			//	node.refresh();
		}
	}*/
	
	/**
	 *  Rebuild the complete tree.
	 *  (Does not uncheck nodes)
	 * /
	public void rebuildAll()
	{
		for(int i=0; children!=null && i<children.size(); i++)
		{
			FileNode node = (FileNode)children.get(i);
			if(node instanceof DirNode)
				((DirNode)node).rebuildAll();
		}
	}*/
	
	//-------- bean properties --------
	
	/**
	 *  Get the integrity checking flag.
	 */
	public boolean	isChecking()
	{
		return checking;
	}
	
	/**
	 *  Set the integrity checking flag.
	 */
	public void	setChecking(boolean checking)
	{
		this.checking	= checking;
	}

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
	
	/**
	 *  Set a nodetype specific action.
	 *  @param nodetype The nodetype.
	 *  @param action The action. 
	 */
	public void setAction(Class nodetype, INodeAction action)
	{
		if(action==null)
			actions.remove(nodetype);
		else
			actions.put(nodetype, action);
	}
	
	/**
	 *  Get the node action.
	 *  @param nodetype The nodetype.
	 *  @return The action.
	 */
	public INodeAction getAction(Class nodetype)
	{
		return (INodeAction)actions.get(nodetype);
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
	 *  Set the agent factory.
	 *  @param agentfactory The agent factory.
	 *  note: is named with "my" to avoid that nuggets treats
	 *  the agentfactory as bean property.
	 */
	public void setMyAgentFactory(IJadexAgentFactory agentfactory)
	{
		this.agentfactory = agentfactory;
	}

	/**
	 *  Get the agentfactory.
	 *  @return The agentfactory.
	 */
	public IJadexAgentFactory getAgentFactory()
	{
		return agentfactory;
	}
}
