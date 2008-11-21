package jadex.tools.starter;

import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.JarAsDirectory;
import jadex.tools.common.modeltree.RootNode;

import javax.swing.Icon;

/**
 *  Functionality for jar nodes.
 */
public class JarNodeFunctionality //extends DirNodeFunctionality
{	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 * /
	public boolean refresh(IExplorerTreeNode node)
	{
		FileNode fn = (FileNode)node;
		if(!(fn.getFile() instanceof JarAsDirectory))
		{
			System.err.println("Failed to refresh jar node: " + fn.getFile());
		}
		boolean	changed	= ((JarAsDirectory)fn.getFile()).refresh();
		if(changed)
		{
			super.refresh(node);
//			System.out.println("changed: "+this);
		}
		return changed;
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 * /
	public Icon getIcon(IExplorerTreeNode node)
	{
		Icon icon;
		FileNode fn = (FileNode)node;
		boolean valid = fn.isValid() || !fn.getRootNode().isChecking();
		if(fn.getParent() instanceof RootNode)
		{
			icon =  FileNodeFunctionality.icons.getIcon(valid? "src_jar" : "src_jar_broken");
		}
		else
		{
			icon= FileNodeFunctionality.icons.getIcon(valid? "package" : "package_broken");
		}
		
		return icon;
	}*/
}
