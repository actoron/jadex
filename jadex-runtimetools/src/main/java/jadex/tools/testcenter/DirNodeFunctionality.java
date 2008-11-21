package jadex.tools.testcenter;

import jadex.commons.SGUI;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.INodeFunctionality;
import jadex.tools.common.modeltree.RootNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.tree.TreeNode;

/**
 *
 */
public class DirNodeFunctionality //extends FileNodeFunctionality
{
	//-------- constants --------

	/**
	 * The image  for (m/r) elements.
	 * /
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"src_folder", SGUI.makeIcon(DirNode.class, "/jadex/tools/common/images/new_src_folder.png"),
		"src_folder_testable", SGUI.makeIcon(DirNode.class, "/jadex/tools/common/images/new_src_folder_testable.png"),
		"package", SGUI.makeIcon(DirNode.class, "/jadex/tools/common/images/new_package.png"),
		"package_testable", SGUI.makeIcon(DirNode.class, "/jadex/tools/common/images/new_package_testable.png"),
	});
	
	/** The file node functionality. * /
	public static INodeFunctionality fnf = new FileNodeFunctionality();
	/** The dir node functionality. * /
	public static INodeFunctionality dnf = new DirNodeFunctionality();
	
	/**
	 *  Check if the node is valid.
	 *  @return True, is valid.
	 * /
	public boolean check(IExplorerTreeNode node)
	{
		boolean	valid	= false;
		for(int i=0; !valid && i<node.getChildCount(); i++)
			valid	= valid || ((FileNode)node.getChildAt(i)).isValid();
							
		return valid;
	}
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 * /
	public boolean refresh(IExplorerTreeNode node)
	{
		boolean	changed	= super.refresh(node);
		DirNode dn = (DirNode)node;
		
		List children = dn.getChildren();
		if(changed)
		{
//			System.out.println("refreshing: "+this);
			File files[] = dn.getFile().listFiles(dn.getRootNode().getFileFilter());
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
					dn.setChildren(children);
				}
				
				for(int i = 0; i<files.length; i++)
				{
					TreeNode child = createNode(node, files[i]);
	
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
		}

		return changed;
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 * /
	public Icon getIcon(IExplorerTreeNode node)
	{
		Icon	icon	= null;
		FileNode fn = (FileNode)node;
		if(node.getParent() instanceof RootNode)
		{
			icon	= icons.getIcon(fn.isValid()? "src_folder_testable": "src_folder");
		}
		else
		{
			icon	= icons.getIcon(fn.isValid()? "package_testable": "package");
		}
		return icon;
	}
	
	/**
	 *  Create a new child node.
	 *  @param file The file for the new child node.
	 *	@return The new node.
	 * /
	public IExplorerTreeNode createNode(IExplorerTreeNode node, File file)
	{
		return file.isDirectory()
			? (IExplorerTreeNode)new DirNode(node, file, dnf, false)
			: (IExplorerTreeNode)new FileNode(node, file, fnf, false);
	}*/
	
	
}
