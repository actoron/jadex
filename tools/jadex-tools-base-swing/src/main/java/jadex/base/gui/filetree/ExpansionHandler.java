package jadex.base.gui.filetree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.TreeExpansionHandler;

/**
 *  Handles all aspects (e.g. expansion, selection) of the tree
 *  that have to happen in background
 *  as the refresher thread adds/removes nodes.
 */
public class ExpansionHandler extends TreeExpansionHandler
{
	//-------- attributes --------
	
	/** The node that was selected before the current project was last saved. */
	// Hack!!! Move to treeselection listener.
	protected NodePath	lastselectedpath;

	/** The expanded node paths. */
	protected Set	expandedpaths;

	//-------- constructors --------
	
	/**
	 *  Create a new tree handler.
	 */
	public ExpansionHandler(JTree tree)
	{
		super(tree);
	}
	
	//-------- methods --------

	/**
	 *  Set the selected path.
	 */
	public void	setSelectedPath(NodePath path)
	{
//		System.out.println("selected: "+path);
		Object[]	resolved	= resolveNodePath(path);
		if(resolved!=null)
		{
			tree.setSelectionPath((TreePath)resolved[1]);
			tree.scrollPathToVisible((TreePath)resolved[1]);
			this.lastselectedpath	= null;
		}
		else
		{
			this.lastselectedpath	= path;
		}
	}

	/**
	 *  Set the expanded paths.
	 */
	public void	setExpandedPaths(NodePath[]	paths)
	{
		this.expandedpaths	= new HashSet();
		
		// Check if paths can be expanded on the fly
		// and add remaining paths.
		for(int i=0; i<paths.length; i++)
		{
			Object[]	resolved	= resolveNodePath(paths[i]);
				
			if(resolved!=null)
			{
//				System.out.println("expanded: "+resolved[0]);
				expanded.add(resolved[0]);
				handlePath((TreePath)resolved[1]);
			}
			else
			{
//				System.out.println("not expanded: "+paths[i]);
				expandedpaths.add(paths[i]);
			}
		}
	}

	/**
	 *  Check if an action (e.g. expand) has to be performed on the path.
	 */
	protected IFuture handlePath(final TreePath path)
	{
//		if(path.toString().indexOf("micro")!=-1)
//			System.out.println("handle path: "+path);
		
		// Move from paths (loaded) to expanded nodes (created dynamically).
		if(expandedpaths!=null && path.getPathCount()>1 && expandedpaths.remove(NodePath.createNodePath((ISwingTreeNode)path.getLastPathComponent())))
		{
//			System.out.println("loaded: "+path.getLastPathComponent());
			expanded.add(path.getLastPathComponent());
			if(expandedpaths.isEmpty())
				expandedpaths	= null;
		}
		
		IFuture	ret	= super.handlePath(path);
	
		ret.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				// Check if last selected path was added and can be selected.
				if(lastselectedpath!=null)
				{
					Object[]	resolved	= resolveNodePath(lastselectedpath);
					if(resolved!=null)
					{
//						System.out.println("selected1: "+resolved[0]);
						lastselectedpath	= null;
						tree.setSelectionPath((TreePath)resolved[1]);
						tree.scrollPathToVisible((TreePath)resolved[1]);
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Shouldn't happen
				exception.printStackTrace();
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the node and treepath for a nodepath.
	 *  @param nodepath	The node path.
	 *  @return An object array containing the node [0] and the tree path [1] or null if not found.
	 */
	protected Object[]	resolveNodePath(NodePath nodepath)
	{
		Object[]	ret	= null;
		TreeModel	model	= tree.getModel();
		if(model.getChildCount(model.getRoot())>nodepath.entry)
		{
			Object	node	= model.getChild(model.getRoot(), nodepath.entry);
			List	treepath	= new ArrayList();
			treepath.add(model.getRoot());
			treepath.add(node);
			String[]	path	= nodepath.getPath();
			boolean	found	= true;
			for(int j=0; found && j<path.length; j++)
			{
				found	= false;
				for(int k=0; !found && k<model.getChildCount(node); k++)
				{
					Object	child	= model.getChild(node, k);
					String	name	= ((IFileNode)child).getFileName();
					if(path[j].equals(name))
					{
						found	= true;
						node	= child;
						treepath.add(child);
					}
				}
			}
			
			if(found)
			{
				ret	= new Object[]{node, new TreePath(treepath.toArray())};
			}
		}
		return ret;
	}
	
	
	/**
	 *  Test if a path is expanded or should be.
	 */
	public boolean	isExpanded(TreePath path)
	{
		return super.isExpanded(path) || expandedpaths!=null && expandedpaths.contains(NodePath.createNodePath((ISwingTreeNode)path.getLastPathComponent()));
	}

}