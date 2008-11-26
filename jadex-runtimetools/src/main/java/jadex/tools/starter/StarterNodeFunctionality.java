package jadex.tools.starter;

import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IExecutable;
import jadex.tools.common.modeltree.AbstractNodeFunctionality;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.JarNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.RootNode;
import jadex.tools.common.plugin.IControlCenter;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.DefaultTreeModel;

/**
 *  Model tree node functionality, specific for the starter plugin.
 */
public class StarterNodeFunctionality extends AbstractNodeFunctionality
{
	//-------- constants --------
	
	/** The valid property. */
	protected static final String	VALID	= "valid";
	
	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"agent", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_agent.png"),
		"agent_broken", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_agent_broken.png"),
		"capability", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_capability_small.png"),
		"capability_broken", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_capability_broken.png"),
		"src_folder", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder.png"),
		"src_folder_broken", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder_broken.png"),
		"src_jar", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar.png"),
		"src_jar_broken", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar_broken.png"),
		"package", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_package.png"),
		"package_broken", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_package_broken.png"),
		"java_file", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/java_file.png"),
		"checking_on",	SGUI.makeIcon(AbstractNodeFunctionality.class, "/jadex/tools/common/images/new_agent_testcheckanim.gif"),	
	});
	
	//-------- attributes --------

	/** The check indicator for the status bar. */
	protected JLabel	checkcomp;
	
	/** The nodes of queued check tasks. */
	protected Set	checkqueue;

	//-------- constructors --------
	
	/**
	 *  Create a starter node functionality.
	 */
	public StarterNodeFunctionality(IControlCenter jcc)
	{
		super(jcc);
		checkcomp	= new JLabel(icons.getIcon("checking_on"));
		checkcomp.setToolTipText("Checking validity of agent models.");
	}
	
	//-------- INodeFunctionality interface --------
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon getIcon(IExplorerTreeNode node)
	{
		Icon icon	= null;
		if(node instanceof FileNode)
		{
			boolean	valid = isValid(node);// || !fn.getRootNode().isChecking();

			if(node instanceof JarNode)
			{
				icon =  icons.getIcon(valid? "src_jar" : "src_jar_broken");
			}
			else if(node instanceof DirNode)
			{
				if(node.getParent() instanceof RootNode)
				{
					icon	= icons.getIcon(valid? "src_folder" : "src_folder_broken");
				}
				else
				{
					icon	= icons.getIcon(valid? "package" : "package_broken");
				}
			}
			else if (node instanceof FileNode)
			{
				FileNode fn = (FileNode)node;
				IJadexAgentFactory	fac	= jcc.getAgent().getPlatform().getAgentFactory();
				if(fac.isLoadable(fn.getFile().getName()) && fac.isStartable(fn.getFile().getName()))
				{
					icon	= icons.getIcon(valid? "agent" : "agent_broken");
				}
				else if(fac.isLoadable(fn.getFile().getName()))
				{
					icon	= icons.getIcon(valid? "capability" : "capability_broken");
				}
				else if(SUtil.isJavaSourceFilename(fn.getFile().getName()))
				{
					icon	= icons.getIcon("java_file");
				}
			}
		}
		return icon;
	}


	/**
	 *  Called when a change was detected in a node.
	 *  Check node, if necessary.
	 */
	public void	nodeChanged(IExplorerTreeNode node)
	{
		startCheckTask(node);
	}

	
	/**
	 *  Start a check task for a given node.
	 */
	protected synchronized void	startCheckTask(IExplorerTreeNode node)
	{
		if(checkqueue==null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					jcc.addStatusComponent(checkcomp, checkcomp);
				}
			});
			checkqueue	= new HashSet();
		}

		if(!checkqueue.contains(node))
		{
			checkqueue.add(node);
			// Use priority below user priority to scan first, then check.
			explorer.getWorker().execute(new CheckTask(node), ModelExplorer.PERCENTAGE_USER*0.9);
		}
	}

	
	/**
	 *  Called, when a check task is finished.
	 */
	protected synchronized void	checkTaskFinished(IExplorerTreeNode node)
	{
		checkqueue.remove(node);
		if(checkqueue.isEmpty())
		{
			checkqueue	= null;
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					jcc.removeStatusComponent(checkcomp);
					jcc.setStatusText("");
				}
			});
		}
	}

	//-------- helper classes --------
	
	/**
	 *  A task to check a file.
	 */
	class CheckTask	implements IExecutable
	{
		//-------- attributes --------
		
		/** The node to refresh. */
		protected IExplorerTreeNode	node;
		
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public CheckTask(IExplorerTreeNode node)
		{
			this.node	= node;
		}
		
		//-------- IExecutable interface --------
		
		/**
		 *  Execute the task.
		 */
		public boolean execute()
		{
			// Perform refresh only, when node still in tree.
			if(isValidChild(node))
			{
				String	tip	= node.getToolTipText();
				if(tip!=null)
					jcc.setStatusText("Checking "+tip);

//				System.out.println("test valid: "+node);
				if(node instanceof FileNode)
				{
					FileNode fn = (FileNode)node;
					boolean	oldvalid	= isValid(node);
					boolean	newvalid	= false;
					
					// Check directory.
					if(node instanceof DirNode)
					{
						newvalid	= true;
						for(int i=0; newvalid && i<node.getChildCount(); i++)
						{
							newvalid	= isValid((IExplorerTreeNode) node.getChildAt(i));
						}
					}
					
					// Check file.
					else
					{
						String	file	= fn.getFile().getAbsolutePath();
						if(jcc.getAgent().getPlatform().getAgentFactory().isLoadable(file))
						{
							try
							{
								IJadexModel model = jcc.getAgent().getPlatform().getAgentFactory().loadModel(file);
								if(model!=null)
								{
									newvalid	= model.getReport().isEmpty();
								}
								// else unknown jadex file type -> ignore.
							}
							catch(Exception e)
							{
							}
						}
					}
					
					fn.getProperties().put(VALID, new Boolean(newvalid));	// Add always, because old value could be null.
					if(oldvalid!=newvalid)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								((DefaultTreeModel)explorer.getModel()).nodeChanged(node);
							}
						});

						IExplorerTreeNode	parent	= (IExplorerTreeNode) fn.getParent();
						if(parent instanceof DirNode && newvalid!=isValid(parent))
						{
							startCheckTask(parent);
						}
//						System.out.println("Valid?: "+node+", "+newvalid);
					}
				}
			}
			checkTaskFinished(node);
			return false;
		}
	}

	/**
	 *  Check if the valid flag of a node is set to true.
	 */
	public boolean	isValid(IExplorerTreeNode node)
	{
		boolean	ret	= true;	// Unknown node types are valid by default
		if(node instanceof FileNode)
		{
			FileNode fn = (FileNode)node;
			Boolean	val	= (Boolean)fn.getProperties().get(VALID);
			ret	= val==null || val.booleanValue();	// Valid, if not yet checked.
			
			// If not yet checked queue a check task.
			if(val==null)
				startCheckTask(node);
		}
		return ret;
	}
}
