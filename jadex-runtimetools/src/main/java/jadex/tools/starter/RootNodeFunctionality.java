package jadex.tools.starter;

import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.INodeFunctionality;
import jadex.tools.common.modeltree.JarNode;

import java.io.File;

import javax.swing.Icon;

/**
 *
 */
public class RootNodeFunctionality //implements INodeFunctionality
{
	/** The jar node functionality. * /
	public static INodeFunctionality jnf = new JarNodeFunctionality();

	/** The file node functionality. * /
	public static INodeFunctionality dnf = new DirNodeFunctionality();

	
	/**
	 *  Check if the node is valid.
	 *  @return True, is valid.
	 * /
	public boolean check(IExplorerTreeNode node)
	{
		return true; 
	}
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 * /
	public boolean refresh(IExplorerTreeNode node)
	{
		return false;
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 * /
	public Icon getIcon(IExplorerTreeNode node)
	{
		return null;
	}
	
	/**
	 *  Create a new child node.
	 *  @param file The file for the new child node.
	 *	@return The new node.
	 * /
	public IExplorerTreeNode createNode(IExplorerTreeNode node, File file)
	{
		FileNode ret	= null;

		// Check if entry is file or directory.
		if(file.exists())
		{
			if(file.isDirectory())
			{
				ret	= new DirNode(node, file, dnf);
			}
			else
			{
				ret	= new JarNode(node, file.getAbsolutePath(), jnf);
			}
		}
		return ret;
	}*/
}
