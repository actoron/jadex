package jadex.tools.common.modeltree;

import jadex.bridge.IAgentFactory;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.tools.common.plugin.IControlCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  Base implementation for node functionality.
 */
public class	DefaultNodeFunctionality
{
	//-------- constants --------
	
	/** The last modified property (Date). */
	protected static final String	LAST_MODIFIED	= "last_modified";
	
	/** The children property (List). */
	protected static final String	CHILDREN	= "children";
	
	/** The last check date of the children property (Date). */
	protected static final String	CHILDREN_DATE	= "children_date";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"scanning_on",	SGUI.makeIcon(DefaultNodeFunctionality.class, "/jadex/tools/common/images/new_refresh_anim.gif"),
		"src_folder", SGUI.makeIcon(DefaultNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder.png"),
		"src_jar", SGUI.makeIcon(DefaultNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar.png"),
		"package", SGUI.makeIcon(DefaultNodeFunctionality.class, "/jadex/tools/common/images/new_package.png")
	});

	//-------- attributes --------
	
	/** The tree model. */
	protected ModelExplorer	explorer;
	
	/** The JCC. */
	protected IControlCenter	jcc;
	
	/** The refresh indicator for the status bar. */
	protected JLabel	refreshcomp;
	
	/** The task queues (status component -> Set{queued nodes}). */
	protected Map	taskqueues;
	
	//-------- constructors --------
	
	/**
	 *  Create a starter node functionality.
	 */
	public DefaultNodeFunctionality(IControlCenter jcc)
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
	
	//-------- methods --------
	
	/**
	 *  Get the jcc.
	 */
	public IControlCenter	getJCC()
	{
		return this.jcc;
	}
	
	/**
	 *  Check for changes in the file system.
	 *  If the file has changed, the last modified
	 *  date is saved and the node is repainted
	 *  leading to other properties being updated,
	 *  if necessary.
	 */
	public void	refresh(final IExplorerTreeNode node)
	{
		if(isValidChild(node) && node instanceof FileNode)
		{
			FileNode fn = (FileNode)node;
			if(!fn.getFile().exists())
			{
				// happens e.g. when manually refreshing already removed file/dir
				IExplorerTreeNode	parent	= (IExplorerTreeNode) fn.getParent();
				startNodeTask(new RefreshNodeTask(this, parent));
			}
			else
			{
				Date	newdate	= new Date(fn.getFile().lastModified());
				Date	olddate	= (Date) fn.getProperties().get(LAST_MODIFIED);
				if(olddate==null || olddate.before(newdate))
				{
					fn.getProperties().put(LAST_MODIFIED, newdate);
					nodeChanged(fn);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							((ModelExplorerTreeModel)explorer.getModel()).fireNodeChanged(node);
						}
					});
				}
			}
		}		
	}
	
	/**
	 *  Get the children opf a node.
	 */
	public List	getChildren(FileNode node)
	{
//		System.out.println("getChildren: "+node.getToolTipText());
		Date	filedate	= getLastModified(node);
		Date	childate	= (Date)node.getProperties().get(CHILDREN_DATE);
		if(filedate!=null && (childate==null || childate.before(filedate)))
		{
			startNodeTask(new UpdateChildrenTask(node));
//			System.out.println("Get Children2: "+node.getToolTipText());
		}
		return (List) node.getProperties().get(CHILDREN);
	}

	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon	getIcon(IExplorerTreeNode node)
	{
		Icon icon	= null;
		if(node instanceof FileNode)
		{
			if(node instanceof JarNode)
			{
				icon =  icons.getIcon("src_jar");
			}
			else if(node instanceof DirNode)
			{
				if(node.getParent() instanceof RootNode)
				{
					icon	= icons.getIcon("src_folder");
				}
				else
				{
					icon	= icons.getIcon("package");
				}
			}
			else if (node instanceof FileNode)
			{
				FileNode fn = (FileNode)node;
				IAgentFactory	fac	= jcc.getAgent().getPlatform().getAgentFactory();
				String	type	= fac.getFileType(fn.getFile().getAbsolutePath());
				if(type!=null)
						icon	= fac.getFileTypeIcon(type);
			}
		}
		return icon;
	}
	
	/**
	 *  Called when the corresponding file of a node has changed.
	 *  Empty default implementation to be overridden by subclasses.
	 */
	public void	nodeChanged(FileNode node)
	{
	}

	/**
	 *  Called when children of a directory node have been added or removed.
	 *  Empty default implementation to be overridden by subclasses.
	 */
	public void	childrenChanged(DirNode node)
	{
	}

	//-------- helper methods --------

	/**
	 *  Test if a node is still a valid child, i.e. contained in tree.
	 */
	public boolean	isValidChild(IExplorerTreeNode node)
	{
		return node instanceof RootNode ||
			node.getParent()!=null
			&& ((ModelExplorerTreeModel)explorer.getModel()).getIndexOfChild(node.getParent(), node)!=-1
			&& isValidChild((IExplorerTreeNode)node.getParent());
	}

	/**
	 *  Get the last modified date (if already checked).
	 */
	public Date	getLastModified(FileNode node)
	{
		Date	ret	= (Date)node.getProperties().get(LAST_MODIFIED);
		if(ret==null)
			startNodeTask(new RefreshNodeTask(this, node));
		return ret;
	}

	/**
	 *  Start a task for a given node.
	 *  Task will be added to the queue if not
	 *  already contained.
	 */
	public synchronized void	startNodeTask(NodeTask task)
	{
		boolean	add	= true;
		Set	queue	= null;
		final JComponent	statuscomp	= task.getStatusComponent();
		if(taskqueues!=null)
		{
			queue	= (Set) taskqueues.get(statuscomp);
			if(queue!=null)
				add	= !queue.contains(task.getNode());
		}

		if(add)
		{
			if(taskqueues==null)
			{
				taskqueues	= new HashMap();
			}
			if(queue==null)
			{
				queue	= new HashSet();
				taskqueues.put(statuscomp, queue);
			}
			queue.add(task.getNode());
			explorer.getWorker().execute(task, task.getPriority());
//			System.out.println("Added: "+task);

			if(statuscomp!=null)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						jcc.addStatusComponent(statuscomp, statuscomp);
					}
				});
			}
		}
	}
	
	/**
	 *  Called, when a node task actually starts.
	 *  Task will be removed from the queue.
	 */
	public synchronized void	nodeTaskStarting(NodeTask task)
	{
		Set	queue	= (Set)taskqueues.get(task.getStatusComponent());
		queue.remove(task.getNode());
		if(queue.isEmpty())
		{
			taskqueues.remove(task.getStatusComponent());
			if(taskqueues.isEmpty())
			{
				taskqueues	= null;
			}
		}
	}

	/**
	 *  Called, when a node task is finished.
	 *  Removes the status component, if last task of type.
	 */
	public synchronized void	nodeTaskFinished(NodeTask task)
	{
		final JComponent	statuscomp	= task.getStatusComponent();
		if(taskqueues==null || !taskqueues.containsKey(statuscomp))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					jcc.removeStatusComponent(statuscomp);
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
	static class RefreshNodeTask	extends NodeTask
	{
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public RefreshNodeTask(DefaultNodeFunctionality nof, IExplorerTreeNode node)
		{
			super(nof, node, ModelExplorer.PERCENTAGE_USER, "Refreshing ", nof.refreshcomp);
		}
		
		//-------- NodeTask methods --------
		
		/**
		 *  Perform the task.
		 */
		public void performTask()
		{
//			System.out.println("Refresh: "+node.getToolTipText());
			nof.refresh(node);
		}
	}

	/**
	 *  A task to update the children of a node.
	 */
	class UpdateChildrenTask	extends NodeTask
	{
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public UpdateChildrenTask(IExplorerTreeNode node)
		{
			super(DefaultNodeFunctionality.this, node, ModelExplorer.PERCENTAGE_USER, "Refreshing ", refreshcomp);
		}
		
		//-------- NodeTask methods --------
		
		/**
		 *  Perform the task.
		 */
		public void performTask()
		{
//			System.out.println("UpdateChildren: "+node.getToolTipText());
			boolean	changed	= node instanceof DirNode;
			if(node instanceof JarNode)
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
				changed	= false;
				DirNode dn = (DirNode)node;
				List children = (List) dn.getProperties().get(CHILDREN);
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
						dn.getProperties().put(CHILDREN, children);
					}
					
					for(int i = 0; i<files.length; i++)
					{
						IExplorerTreeNode	child = files[i].isDirectory()
							? (IExplorerTreeNode)new DirNode(node, files[i])
							: (IExplorerTreeNode)new FileNode(node, files[i]);
		
						// Check if child is new
						if(old==null || !old.remove(child))
						{
							int	index;
							for(index=0; index<children.size() 
								&& FILENODE_COMPARATOR.compare(
								children.get(index), child)<=0; index++);
							children.add(index, child);	
							changed	= true;
						}
					}
					
					// Remove old entries.
					if(old!=null)
					{
						for(Iterator it=old.iterator(); it.hasNext(); )
						{
							children.remove(it.next());
							changed	= true;
						}
					}
				}
				
				// Cannot access directory.
				else if(children!=null)
				{
					dn.getProperties().remove(CHILDREN);
					changed	= true;
				}
			}
			
			((FileNode)node).getProperties().put(CHILDREN_DATE, new Date());
			if(changed)
			{
				childrenChanged((DirNode) node);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Hack??? Should propagate adds/removes separately?
						((ModelExplorerTreeModel)explorer.getModel()).fireTreeStructureChanged(node);
					}
				});
			}
		}
	}
}
