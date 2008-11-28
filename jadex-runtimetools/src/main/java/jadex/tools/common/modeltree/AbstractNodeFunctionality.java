package jadex.tools.common.modeltree;

import jadex.commons.SGUI;
import jadex.commons.concurrent.IExecutable;
import jadex.tools.common.plugin.IControlCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.DefaultTreeModel;

/**
 *  Base implementation for node functionality.
 */
public abstract class AbstractNodeFunctionality implements INodeFunctionality
{
	//-------- constants --------
	
	/** The last modified property. */
	protected static final String	LAST_MODIFIED	= "last_modified";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"scanning_on",	SGUI.makeIcon(AbstractNodeFunctionality.class, "/jadex/tools/common/images/new_refresh_anim.gif"),
	});

	//-------- attributes --------
	
	/** The tree model. */
	protected ModelExplorer	explorer;
	
	/** The JCC. */
	protected IControlCenter	jcc;
	
	/** The refresh indicator for the status bar. */
	protected JLabel	refreshcomp;
	
	/** The task counter. */
	protected int	cnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a starter node functionality.
	 */
	public AbstractNodeFunctionality(IControlCenter jcc)
	{
		this.jcc	= jcc;
		refreshcomp	= new JLabel(icons.getIcon("scanning_on"));
		refreshcomp.setToolTipText("Scanning disc for agent models.");
	}

	/**
	 *  Set the model explorer.
	 */
	public void	setModelExplorer(ModelExplorer explorer)
	{
		this.explorer	= explorer;
	}
	
	//-------- INodeFunctionality interface --------
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 */
	public void	refresh(final IExplorerTreeNode node)
	{
		// Todo: how/when to remove status comp?
//		if(refreshcomp!=null)
//			jcc.addStatusComponent(this, refreshcomp);

		boolean	changed	= false;
		if(node instanceof FileNode)
		{
			FileNode fn = (FileNode)node;
			if(!fn.getFile().exists())
			{
				// happens e.g. when manually refreshing already removed file/dir
				IExplorerTreeNode	parent	= (IExplorerTreeNode) fn.getParent();
				startRefreshTask(parent);
			}
			else
			{
				long newdate	= fn.getFile().lastModified();
				Long	olddate	= (Long) fn.getProperties().get(LAST_MODIFIED);
				if(olddate==null || olddate.longValue()<newdate)
				{
					fn.getProperties().put(LAST_MODIFIED, new Long(newdate));
					changed	= true;
				}
			}
		}
		
		if(changed && node instanceof JarNode)
		{
			FileNode fn = (FileNode)node;
			if(!(fn.getFile() instanceof JarAsDirectory))
			{
				System.err.println("Failed to refresh jar node: " + fn.getFile());
			}
			changed	= ((JarAsDirectory)fn.getFile()).refresh();
		}

		// Only check changed directories.
		if(changed && node instanceof DirNode)
		{
			DirNode dn = (DirNode)node;
			List children = dn.getChildren();
			File files[] = dn.getFile().listFiles(explorer.getFileFilter());
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
					IExplorerTreeNode	child = createNode(node, files[i]);
	
					// Check if child is new
					if(old==null || !old.remove(child))
					{
						int	index;
						for(index=0; index<children.size() 
							&& FILENODE_COMPARATOR.compare(
							children.get(index), child)<=0; index++);
						children.add(index, child);
						
						startRefreshTask(child);
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
				dn.setChildren(null);
			}
		}
				
		if(changed)
		{
			nodeChanged(node);
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					// Todo: remove old nodes from tree expansion handler
					((DefaultTreeModel)explorer.getModel()).nodeStructureChanged(node);
				}
			});
		}
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public abstract Icon getIcon(IExplorerTreeNode node);

	/**
	 *  Called when a change was detected in a node.
	 *  Empty default implementation to be overridden by subclasses.
	 */
	public void	nodeChanged(IExplorerTreeNode node)
	{
		
	}

	//-------- helper methods --------

	/**
	 *  Create a new child node.
	 *  @param file The file for the new child node.
	 *	@return The new node.
	 */
	protected IExplorerTreeNode createNode(IExplorerTreeNode parent, File file)
	{
		return file.isDirectory()
			? (IExplorerTreeNode)new DirNode(parent, file)
			: (IExplorerTreeNode)new FileNode(parent, file);
	}
	
	/**
	 *  Start a refresh task for a given node.
	 */
	protected synchronized void	startRefreshTask(IExplorerTreeNode node)
	{
		if(cnt==0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					jcc.addStatusComponent(refreshcomp, refreshcomp);
				}
			});
		}

		cnt++;
		explorer.getWorker().execute(new RefreshTask(node), ModelExplorer.PERCENTAGE_USER);
	}

	
	/**
	 *  Called, when a refresh task is finished.
	 */
	protected synchronized void	refreshTaskFinished(IExplorerTreeNode node)
	{
		cnt--;
		if(cnt==0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					jcc.removeStatusComponent(refreshcomp);
					jcc.setStatusText("");
				}
			});
		}
	}

	/**
	 *  Comparator for filenodes.
	 */
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
	};

	/**
	 *  A task to refresh a node.
	 */
	class RefreshTask	implements IExecutable
	{
		//-------- attributes --------
		
		/** The node to refresh. */
		protected IExplorerTreeNode	node;
		
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public RefreshTask(IExplorerTreeNode node)
		{
			this.node	= node;
		}
		
		//-------- IExecutable interface --------
		
		/**
		 *  Execute the task.
		 */
		public boolean execute()
		{
			String	tip	= node.getToolTipText();
			if(tip!=null)
				jcc.setStatusText("Refreshing "+tip);

			// Perform refresh only, when node still in tree.
			if(isValidChild(node))
			{
				refresh(node);
			}
			refreshTaskFinished(node);
			return false;
		}
	}

	/**
	 *  Test if a node is still a valid child, i.e. contained in tree.
	 */
	public static boolean	isValidChild(IExplorerTreeNode node)
	{
		return node instanceof RootNode ||
			node.getParent()!=null && node.getParent().getIndex(node)!=-1
				&& isValidChild((IExplorerTreeNode)node.getParent());
	}
}
