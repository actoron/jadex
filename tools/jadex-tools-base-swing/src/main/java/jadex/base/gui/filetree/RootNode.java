package jadex.base.gui.filetree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import jadex.base.JarAsDirectory;
import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.componenttree.ComponentProperties;

/**
 *  The root node.
 */
public class RootNode extends AbstractSwingTreeNode
{
	//-------- attributes --------
	
	/** The list of child nodes. */
	protected List children;
	
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public RootNode(AsyncSwingTreeModel model, JTree tree)
	{
		super(null, model, tree);
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		this.children = new ArrayList();
		
		model.registerNode(this);
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Set the children.
	 */
	// Made public to support external refresh in deployer.
	public void setChildren(List newchildren)
	{
		this.children	= newchildren;
		super.setChildren(newchildren);
	}
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return "root";
	}

	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
		return null;
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		return null;
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse)
	{
		assert SwingUtilities.isEventDispatchThread();
		
//		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener()
//		{
//			public void customResultAvailable(Object result)
//			{
//				FileTreeNode.this.desc	= (IComponentDescription)result;
//				getModel().fireNodeChanged(FileTreeNode.this);
//			}
//			public void customExceptionOccurred(Exception exception)
//			{
//				// ignore
//			}
//		});

		super.refresh(recurse);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		setChildren(children);
	}
	
	/**
	 *  Add a child node.
	 *  @param child The child node.
	 */
	public void addChild(ISwingTreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		children.add(child);
		setChildren(children);
	}
	
	/**
	 *  Remove a path entry from the tree.
	 *  @param child The child node.
	 */
	public void removeChild(ISwingTreeNode child)
	{
		assert SwingUtilities.isEventDispatchThread();
//		super.removeChild(child);
		children.remove(child);
		setChildren(children);
	}
	
	/**
	 *  Remove a path entry from the tree.
	 */
	public void removeAll()
	{
		assert SwingUtilities.isEventDispatchThread();
		
//		for(int i=0; i<children.size(); i++)
//			super.removeChild((ISwingTreeNode)children.get(i));
		
		children.clear();
		setChildren(children);
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return "root";
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return null;
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return false;
//		return true;
	}
	
	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		return null;
//		if(propcomp==null)
//		{
//			propcomp	= new ComponentProperties();
//		}
//		propcomp.setDescription(desc);
//		return propcomp;
	}
	
	/**
	 *  Check if the node is a leaf.
	 */
	public boolean	isLeaf()
	{
		assert SwingUtilities.isEventDispatchThread();

		return false;
	}
	
	/**
	 *  Returns the index of node in the receivers children. If the receiver
	 *  does not contain node, -1 will be returned.
	 *  @param node
	 *  @return an int.
	 */
	public int getIndex(ISwingTreeNode node)
	{
		return children!=null ? children.indexOf(node) : -1;
	}
	
	/**
	 *  Get the path entries {path, display name}.
	 */
	public String[]	getPathEntries()
	{
		String[]	ret	= new String[getChildCount()];
		for(int i=0; i<ret.length; i++)
		{
			ISwingTreeNode	node	= getChild(i);
			if(node instanceof FileNode)
			{
				if(node instanceof JarNode)
				{
					ret[i]	= ((JarAsDirectory)((FileNode)node).getFile()).getJarPath();
				}
				else
				{
					ret[i]	= ((FileNode)node).getFile().getAbsolutePath();
				}
			}
			else
			{
				// Todo: if(node instanceof RemoteJarNode)???
				ret[i]	= ((RemoteFileNode)node).getRemoteFile().getPath();
			}
		}
		return ret;
	}
}
