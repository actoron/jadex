package jadex.tools.starter;

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
import javax.swing.tree.TreeNode;

/**
 *
 */
public class DirNodeFunctionality //extends FileNodeFunctionality
{
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
		boolean	valid	= true;
		for(int i=0; valid	&& i<node.getChildCount(); i++)
			valid	= valid && ((FileNode)node.getChildAt(i)).isValid();
		//System.out.println("Checking: "+((DirNode)node).getFile()+" "+valid);
		return valid;
	}
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 * /
	public boolean refresh(IExplorerTreeNode node)
	{
//		System.out.println("DNF refresh: "+node);
		boolean	changed	= super.refresh(node);
		DirNode dn = (DirNode)node;
		
		List children = dn.getChildren();
		if(changed)
		{
//			System.out.println("refreshing: "+node);
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
		boolean valid = fn.isValid() || !fn.getRootNode().isChecking();
		//System.out.println(fn.getFile()+" "+fn.isValid());
		if(fn.getParent() instanceof RootNode)
		{
			icon	= icons.getIcon(valid? "src_folder" : "src_folder_broken");
		}
		else
		{
			icon	= icons.getIcon(valid? "package" : "package_broken");
		}
		return icon;
	}
	
	/**
	 *  Comparator for filenodes.
	 * /
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
	};*/
}
