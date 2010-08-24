package jadex.commons;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;


/**
 *  The tree expansion handler assures
 *  that tree nodes stay expanded, even when
 *  their last child is removed, and then new
 *  child nodes are added.
 *  Swing doesn't do this on its own, grrr.
 */
public class TreeExpansionHandler	implements TreeExpansionListener, TreeModelListener
{
	//-------- attributes --------

	/** The tree. */
	protected JTree	tree;

	/** A set with the tree nodes, which are expanded. */
	protected Set	expanded;

	//-------- constructors --------

	/**
	 *  Create a tree expansion handler for a given tree.
	 */
	public TreeExpansionHandler(JTree tree)
	{
		this.tree	= tree;
		this.expanded	= new HashSet();
		tree.addTreeExpansionListener(this);
		tree.getModel().addTreeModelListener(this);
	}

	//-------- TreeExpansionListener interface --------

	/**
	 *  Called whenever an item in the tree has been expanded.
	 */
	public void	treeExpanded(TreeExpansionEvent event)
	{
		// Mark path as expanded.
//		System.out.println("expand: "+event.getPath().getLastPathComponent());
		expanded.add(event.getPath().getLastPathComponent());
	}
	 
	/**
	 *  Called whenever an item in the tree has been collapsed.
	 */
	public void	treeCollapsed(TreeExpansionEvent event)
	{
		// Remove expansion mark, if any.
//		System.out.println("collapse: "+event.getPath().getLastPathComponent());
		expanded.remove(event.getPath().getLastPathComponent());
	}

	//-------- TreeModelListener interface --------

	/**
	 *  Invoked after a node (or a set of siblings) has changed in some way.
	 */
	public void	treeNodesChanged(TreeModelEvent event)
	{
		// I don't care.
	}
	 
	/**
	 *  Invoked after nodes have been inserted into the tree.
	 */
	public void	treeNodesInserted(TreeModelEvent event)
	{
		// When a new node has been inserted,
		// we may have to re-expand its parent.
		handlePath(event.getTreePath());
	}
	 
	/**
	 *  Invoked after nodes have been removed from the tree.
	 */
	public void	treeNodesRemoved(TreeModelEvent event)
	{
		// Remove nodes from set to facilitate garbage collection.
		Object[] children	= event.getChildren();
		for(int i=0; i<children.length; i++)
			expanded.remove(children[i]);
	}
	 
	/**
	 *  Invoked after the tree has drastically changed structure from a
	 *  given node down.
	 */
	public void	treeStructureChanged(final TreeModelEvent event)
	{
		TreePath	root	= event.getTreePath();
//		System.out.println("root: "+root);
		for(int i=Math.max(tree.getRowForPath(root), 0); i<tree.getRowCount(); i++)
		{
			TreePath	path	= tree.getPathForRow(i);
//			System.out.println("path "+i+": "+path);
			if(!root.isDescendant(path))
			{
//				System.out.println("break: "+path);
				break;
			}
			
			handlePath(path);
		}
	}
	
	/**
	 *  Check if an action (e.g. expand) has to be performed on the path.
	 */
	protected void	handlePath(final TreePath path)
	{
//		System.out.println("handle expand: "+path.getLastPathComponent()+", "+expanded);
		if(expanded.contains(path.getLastPathComponent()))
		{
			// Can't expand during change event (Java bug?)
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					tree.expandPath(path);
				}
			});
//			System.out.println("expanded: "+path.getLastPathComponent());
		}
	}
}

