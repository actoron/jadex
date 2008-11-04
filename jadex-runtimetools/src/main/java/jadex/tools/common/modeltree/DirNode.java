package jadex.tools.common.modeltree;

import jadex.commons.SUtil;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 *  A directory node.
 */
public class DirNode	extends FileNode
{
	//-------- attributes --------

	/** The children of the node (i.e. contained files and subdirectories). */
	protected List	children;
	
	//-------- constructors --------

	/**
	 *  Create a new DirNode.
	 *  Bean constructor.
	 */
	public DirNode()
	{
		this(null, null, null);
	}
	
	/**
	 *  Create a directory node for a given directory.
	 */
	public DirNode(TreeNode parent, File dir, INodeFunctionality nof)
	{
		this(parent, dir, nof, true);
	}
	
	/**
	 *  Create a directory node for a given directory.
	 */
	public DirNode(TreeNode parent, File dir, INodeFunctionality nof, boolean valid)
	{
		super(parent, dir, nof, valid);
	}

	//-------- TreeNode interface --------

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
		return children!=null?Collections.enumeration(children): SUtil.EMPTY_ENUMERATION;
	}
	
	/**
	 *  Refresh and rebuild the complete tree.
	 * /
	public void refreshAll()
	{
		uncheck();
		nof.refresh(this);
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
		//nof.refresh(this);
		determineChildren();
		for(int i=0; children!=null && i<children.size(); i++)
		{
			FileNode node = (FileNode)children.get(i);
			if(node instanceof DirNode)
				((DirNode)node).rebuildAll();
		}
	}*/
	
	/**
	 * 
	 * /
	public void determineChildren()
	{
		File files[] = getFile().listFiles(getRootNode().getFilter());
		if(files!=null)
		{
			Set	old	= null;
			if(children!=null)
			{
				old	= new HashSet(children);
			}
			else if(files.length>0)
			{
				children = new ArrayList();
			}
			
			for(int i = 0; i<files.length; i++)
			{
				TreeNode child = nof.createNode(this, files[i]);

				// Check if child is new
				if(old==null || !old.remove(child))
				{
					int	index;
					for(index=0; index<children.size() 
						&& FILENODE_COMPARATOR.compare(
						children.get(index), child)<=0; index++);
					children.add(index, child);
				}
			}
			
			// Remove old entries.
			if(old!=null)
			{
				for(Iterator it=old.iterator(); it.hasNext(); )
				{
					children.remove(it.next());
				}
			}
		}
		
		// Cannot access directory.
		else if(children!=null)
		{
			children	= null;
		}
	}*/
	
	/**
	 *  Comparator for filenodes.
	 */
	public static final Comparator FILENODE_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			File f1 = ((FileNode)o1).getFile();
			File f2 = ((FileNode)o2).getFile();
			if(f1.isDirectory() && !f2.isDirectory()) return -1;
			if(!f1.isDirectory() && f2.isDirectory()) return 1;
	
			return f1.getName().compareTo(f2.getName());
		}
	};

	//-------- methods --------

	/**
	 *  Create a node for a given file.
	 */
	protected TreeNode createNode(File file)
	{
		return nof.createNode(this, file);
	}	

	/**
	 * Return the string reoresentation of this node.
	 * @return its name
	 */
	public String toString()
	{
		return (getParent() instanceof RootNode)? file.getName()+" ("+file.getAbsolutePath()+")": file.getName();
	}

	//-------- bean property accessors --------
	
	/**
	 *  Get the children of this DirNode.
	 *  @return Returns the children.
	 */
	public List getChildren()
	{
		return children;
	}

	/**
	 *  Set the children of this DirNode.
	 *  @param children The children to set.
	 */
	public void setChildren(List children)
	{
		this.children = children;
	}
	
	/**
	 * 
	 * /
	class ChildrenManager
	{
		protected List all_children;
		
		protected List cur_children;
		
		protected java.io.FileFilter myfilter;
		
		protected File[] myfiles;
		
		/**
		 * 
		 * /
		public ChildrenManager()
		{
			this.all_children = new ArrayList();
			this.cur_children = new ArrayList();
			refresh();
		}
		
		/**
		 * 
		 * /
		public void refresh()
		{
			File[] files = getFile().listFiles(getRootNode().getFilter());
			if(!Arrays.equals(myfiles, files))
			{
				all_children.clear();
				for(int i=0; i<files.length; i++)
				{	
					TreeNode child = nof.createNode(DirNode.this, files[i]);
					all_children.add(child);
				}
			}
			
			java.io.FileFilter filter = getRootNode().getFilter();
			if(!SUtil.equals(myfilter, filter))
			{
				cur_children.clear();
				for(int i=0; i<all_children.size(); i++)
				{	
					FileNode child = (FileNode)all_children.get(i);
					if(getRootNode().getFilter().accept(child.getFile()))
						cur_children.add(child);
				}
				myfilter = filter;
			}
		}
		
		/**
		 *  Returns the child TreeNode at index childIndex.
		 *  @param childIndex the index of the child to return
		 *  @return a TreeNode instance
		 * / 
		public TreeNode getChildAt(int childIndex)
		{
			return (TreeNode)cur_children.get(childIndex);
		}

		/**
		 *  Returns the number of children TreeNodes the receiver contains.
		 *  @return the number of children TreeNodes the receiver contains
		 * /
		public int getChildCount()
		{
			return cur_children.size();
		}

		/**
		 *  Returns the index of node in the receivers children. If the receiver
		 *  does not contain node, -1 will be returned.
		 *  @param node
		 *  @return an int.
		 * /
		public int getIndex(TreeNode node)
		{
			return cur_children.indexOf(node);
		}
		
		/**
		 *  Get the children of this DirNode.
		 *  @return Returns the children.
		 * /
		public List getChildren()
		{
			return cur_children;
		}
		
		/**
		 *  Set the children of this DirNode.
		 *  @param children The children to set.
		 * /
		public void setChildren(List children)
		{
			this.cur_children = cur_children;
		}
		
		/**
		 *  Returns the children of the reciever as an Enumeration.
		 *  @return an Enumeration
		 * /
		public Enumeration children()
		{
			return Collections.enumeration(cur_children);
		}
	}*/
}
