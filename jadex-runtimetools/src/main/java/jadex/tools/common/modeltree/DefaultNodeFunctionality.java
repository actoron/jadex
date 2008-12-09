package jadex.tools.common.modeltree;

import jadex.bridge.IAgentFactory;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.tools.common.plugin.IControlCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
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
	
	/** The last modified property (Long). */
	protected static final String	LAST_MODIFIED	= "last_modified";
	
	/** The children property (List). */
	protected static final String	CHILDREN	= "children";
	
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
	
	/** The task counters (status component -> Integer). */
	protected Map	taskcnt;
	
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
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
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
				startNodeTask(new RefreshTask(this, parent));
			}
			else
			{
				long	newdate	= fn.getFile().lastModified();
				Long	olddate	= (Long) fn.getProperties().get(LAST_MODIFIED);
				if(olddate==null || olddate.longValue()<newdate)
				{
					fn.getProperties().put(LAST_MODIFIED, new Long(newdate));
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
		if(hasChanged(node, CHILDREN))
		{
			boolean	changed	= true;
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
							
							startNodeTask(new RefreshTask(this, child));
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
					dn.getProperties().remove(CHILDREN);
				}
			}
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
	 *  Test if the underlying file has changed since
	 *  a property was last checked.
	 *  In case of a change, the property date will be
	 *  set to the new date (i.e. subsequent calls will
	 *  return false until the file is changed again).
	 */
	public boolean	hasChanged(FileNode node, String property)
	{
		Long	filedate	= (Long) node.getProperties().get(LAST_MODIFIED);
		Long	propdate	= (Long) node.getProperties().get(property+"_"+LAST_MODIFIED);
		boolean	ret	= !SUtil.equals(filedate, propdate);
		if(filedate==null)
			refresh(node);
		else if(ret)
			node.getProperties().put(property+"_"+LAST_MODIFIED, filedate);
		return ret;
	}

	/**
	 *  Start a refresh task for a given node.
	 */
	public synchronized void	startNodeTask(NodeTask task)
	{
		final JComponent	statuscomp	= task.getStatusComponent();
		if(statuscomp!=null)
		{
			int	cnt	= 0;
			if(taskcnt!=null)
			{
				Integer	icnt	= (Integer) taskcnt.get(statuscomp);
				cnt	= icnt!=null ? icnt.intValue() : cnt;
			}
			if(cnt==0)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						jcc.addStatusComponent(statuscomp, statuscomp);
					}
				});
			}

			cnt++;
			if(taskcnt==null)
				taskcnt	= new HashMap();
			taskcnt.put(statuscomp, new Integer(cnt));
		}
		
		explorer.getWorker().execute(task, task.getPriority());
	}

	
	/**
	 *  Called, when a node task is finished.
	 */
	public synchronized void	nodeTaskFinished(NodeTask task)
	{
		final JComponent	statuscomp	= task.getStatusComponent();
		if(statuscomp!=null)
		{
			int	cnt	= ((Integer) taskcnt.get(statuscomp)).intValue();
			cnt--;
			if(cnt==0)
			{
				taskcnt.remove(statuscomp);
				if(taskcnt.isEmpty())
					taskcnt	= null;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						jcc.removeStatusComponent(statuscomp);
						jcc.setStatusText("");
					}
				});
			}
			else
			{
				taskcnt.put(statuscomp, new Integer(cnt));
			}
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
	static class RefreshTask	extends NodeTask
	{
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public RefreshTask(DefaultNodeFunctionality nof, IExplorerTreeNode node)
		{
			super(nof, node, ModelExplorer.PERCENTAGE_USER, "Refreshing ", nof.refreshcomp);
		}
		
		//-------- NodeTask methods --------
		
		/**
		 *  Perform the task.
		 */
		public void performTask()
		{
			nof.refresh(node);
		}
	}
}
