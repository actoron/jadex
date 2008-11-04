package jadex.tools.testcenter;

import jadex.commons.SGUI;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.JarAsDirectory;
import jadex.tools.common.modeltree.JarNode;
import jadex.tools.common.modeltree.RootNode;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *
 */
public class JarNodeFunctionality extends DirNodeFunctionality
{	
	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"src_jar", SGUI.makeIcon(JarNode.class, "/jadex/tools/common/images/new_src_jar.png"),
	});
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 */
	public boolean refresh(IExplorerTreeNode node)
	{
		FileNode fn = (FileNode)node;
		if(!(fn.getFile() instanceof JarAsDirectory))
		{
			System.err.println(fn.getFile());
		}
		boolean	changed	= ((JarAsDirectory)fn.getFile()).refresh();
		if(changed)
		{
			super.refresh(node);
//			System.out.println("changed "+this);
		}
		return changed;
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon getIcon(IExplorerTreeNode node)
	{
		if(node.getParent() instanceof RootNode)
		{
			return icons.getIcon("src_jar");
		}

		return super.getIcon(node);
	}
}
