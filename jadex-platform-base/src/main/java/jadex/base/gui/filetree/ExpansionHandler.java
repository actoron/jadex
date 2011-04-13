package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.TreeExpansionHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
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
		
		// Check if paths can be expanded on the fly
		// and add remaining paths.
		TreeModel	model	= tree.getModel();
		for(int i=0; i<paths.length; i++)
		{
			boolean	found	= false;
			if(model.getChildCount(model.getRoot())>paths[i].entry)
			{
				Object	node	= model.getChild(model.getRoot(), paths[i].entry);
				List	treepath	= new ArrayList();
				treepath.add(model.getRoot());
				treepath.add(node);
				String[]	path	= paths[i].getPath();
				found	= true;
				for(int j=0; found && j<path.length; j++)
				{
					found	= false;
					for(int k=0; !found && k<model.getChildCount(node); k++)
					{
						Object	child	= model.getChild(node, k);
						if(child instanceof FileNode
							&& path[j].equals(((FileNode)child).getFile().getName()))
						{
							found	= true;
							node	= child;
							treepath.add(child);
						}
					}
				}
				
				if(found)
				{
//					System.out.println("expanded: "+node);
					expanded.add(node);
					handlePath(new TreePath(treepath.toArray()));
				}
			}
			if(!found)
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
//		if(path.toString().indexOf("jadex")!=-1)
//			System.out.println("handle path: "+path);
		
		// Move from paths (loaded) to expanded nodes (created dynamically).
		if(expandedpaths!=null && expandedpaths.remove(NodePath.createNodePath((ITreeNode)path.getLastPathComponent())))
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
				else if(path.getLastPathComponent() instanceof FileNode && lastselectedpath!=null && lastselectedpath.equals(NodePath.createNodePath((ITreeNode)path.getLastPathComponent())))
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