package jadex.base.gui.modeltree;

import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.TreeExpansionHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

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
	protected FileNode	lastselected;

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
	 *  Set the selected node.
	 */
	public void	setSelectedNode(FileNode node)
	{
		this.lastselected	= node;
	}

	/**
	 *  Set the selected path.
	 */
	public void	setSelectedPath(NodePath path)
	{
		this.lastselectedpath	= path;
	}

	/**
	 *  Set the expanded paths.
	 */
	public void	setExpandedPaths(NodePath[]	paths)
	{
		this.expandedpaths	= new HashSet();
		expandedpaths.addAll(Arrays.asList(paths));
	}

	/**
	 *  Check if an action (e.g. expand) has to be performed on the path.
	 */
	protected IFuture handlePath(final TreePath path)
	{
		// Move from paths (loaded) to expanded nodes (created dynamically).
		if(path.getLastPathComponent() instanceof FileNode && expandedpaths!=null && expandedpaths.remove(NodePath.createNodePath((FileNode)path.getLastPathComponent())))
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
				// Check if the node that was saved as selected is added.
				if(lastselected!=null && lastselected.equals(path.getLastPathComponent()))
				{
					lastselected	= null;
					lastselectedpath	= null;
					tree.setSelectionPath(path);
					tree.scrollPathToVisible(path);
				}
				else if(path.getLastPathComponent() instanceof FileNode && lastselectedpath!=null && lastselectedpath.equals(NodePath.createNodePath((FileNode)path.getLastPathComponent())))
				{
//					System.out.println("selected: "+path.getLastPathComponent());
					lastselected	= null;
					lastselectedpath	= null;
					tree.setSelectionPath(path);
					tree.scrollPathToVisible(path);
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
}