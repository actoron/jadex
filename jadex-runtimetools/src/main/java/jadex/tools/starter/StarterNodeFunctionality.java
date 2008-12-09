package jadex.tools.starter;

import jadex.bridge.IAgentModel;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IExecutable;
import jadex.tools.common.modeltree.CombiIcon;
import jadex.tools.common.modeltree.DefaultNodeFunctionality;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;

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
public class StarterNodeFunctionality extends DefaultNodeFunctionality
{
	//-------- constants --------
	
	/** The valid property. */
	protected static final String	VALID	= "valid";
	
	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"check_overlay", SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/check_overlay.png"),
		"checking_on",	SGUI.makeIcon(StarterNodeFunctionality.class, "/jadex/tools/common/images/new_agent_check_anim.gif"),	
	});
	
	//-------- attributes --------

	/** The starter plugin. */
	protected StarterPlugin	starter;
	
	/** The check indicator for the status bar. */
	protected JLabel	checkcomp;
	
	/** The nodes of queued check tasks. */
	protected Set	checkqueue;

	//-------- constructors --------
	
	/**
	 *  Create a starter node functionality.
	 */
	public StarterNodeFunctionality(StarterPlugin starter)
	{
		super(starter.getJCC());
		this.starter	= starter;
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
		Icon icon	= super.getIcon(node);
		if(icon!=null && !isValid(node))
		{
			icon	= new CombiIcon(new Icon[]
			{
				icon,
				icons.getIcon("check_overlay")
			});
		}
		return icon;
	}


	/**
	 *  Called when a change was detected in a node.
	 *  Check node, if necessary.
	 */
	public void	nodeChanged(IExplorerTreeNode node)
	{
		if(starter.getCheckingMenu()!=null && starter.getCheckingMenu().isSelected())
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
//			// Perform refresh only, when node still in tree.
//			if(isValidChild(node))
//			{
//				String	tip	= node.getToolTipText();
//				if(tip!=null)
//					jcc.setStatusText("Checking "+tip);
//
////				System.out.println("test valid: "+node);
//				if(node instanceof FileNode)
//				{
//					FileNode fn = (FileNode)node;
//					boolean	oldvalid	= isValid(node);
//					boolean	newvalid	= false;
//					
//					// Check directory.
//					if(node instanceof DirNode)
//					{
//						newvalid	= true;
//						for(int i=0; newvalid && i<node.getChildCount(); i++)
//						{
//							newvalid	= isValid((IExplorerTreeNode) node.getChildAt(i));
//						}
//					}
//					
//					// Check file.
//					else
//					{
//						String	file	= fn.getFile().getAbsolutePath();
//						if(jcc.getAgent().getPlatform().getAgentFactory().isLoadable(file))
//						{
//							try
//							{
//								IAgentModel model = jcc.getAgent().getPlatform().getAgentFactory().loadModel(file);
//								if(model!=null)
//								{
//									newvalid	= model.getReport().isEmpty();
//								}
//								// else unknown jadex file type -> ignore.
//							}
//							catch(Exception e)
//							{
//							}
//						}
//					}
//					
//					fn.getProperties().put(VALID, new Boolean(newvalid));	// Add always, because old value could be null.
//					if(oldvalid!=newvalid)
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								((DefaultTreeModel)explorer.getModel()).nodeChanged(node);
//							}
//						});
//
//						IExplorerTreeNode	parent	= (IExplorerTreeNode) fn.getParent();
//						if(parent instanceof DirNode && newvalid!=isValid(parent)
//							&& starter.getCheckingMenu()!=null && starter.getCheckingMenu().isSelected())
//						{
//							startCheckTask(parent);
//						}
////						System.out.println("Valid?: "+node+", "+newvalid);
//					}
//				}
//			}
//			checkTaskFinished(node);
			return false;
		}
	}

	/**
	 *  Check if the valid flag of a node is set to true.
	 */
	public boolean	isValid(IExplorerTreeNode node)
	{
		boolean	ret	= true;	// Unknown node types are valid by default
		if(node instanceof FileNode && starter.getCheckingMenu()!=null
			&& starter.getCheckingMenu().isSelected())
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
