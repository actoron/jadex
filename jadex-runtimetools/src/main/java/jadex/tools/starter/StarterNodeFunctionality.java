package jadex.tools.starter;

import jadex.bridge.IAgentModel;
import jadex.commons.SGUI;
import jadex.tools.common.modeltree.CombiIcon;
import jadex.tools.common.modeltree.DefaultNodeFunctionality;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.ModelExplorerTreeModel;
import jadex.tools.common.modeltree.NodeTask;

import java.util.List;

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
	 * The image for (m/r) elements.
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

	//-------- helper classes --------
	
	/**
	 *  A task to check a file.
	 */
	class CheckTask	extends NodeTask
	{
		//-------- constructors --------
		
		/**
		 *  Create a refresh task. 
		 */
		public CheckTask(IExplorerTreeNode node)
		{
			super(StarterNodeFunctionality.this, node,
				ModelExplorer.PERCENTAGE_USER*0.9, "Checking ", checkcomp);
		}
		
		//-------- IExecutable interface --------
		
		/**
		 *  Execute the task.
		 */
		public void	performTask()
		{
			if(node instanceof FileNode)
			{
				FileNode fn = (FileNode)node;
				boolean	oldvalid	= isValid(node);
				boolean	newvalid	= false;
				
				// Check directory.
				if(node instanceof DirNode)
				{
					newvalid	= true;
					List	children	= getChildren(fn);
					for(int i=0; newvalid && children!=null && i<children.size(); i++)
					{
						newvalid	= isValid((IExplorerTreeNode)children.get(i));
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
							IAgentModel model = jcc.getAgent().getPlatform().getAgentFactory().loadModel(file);
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
							((ModelExplorerTreeModel)explorer.getModel()).fireNodeChanged(node);
						}
					});
	
					IExplorerTreeNode	parent	= (IExplorerTreeNode) fn.getParent();
					if(parent instanceof DirNode && newvalid!=isValid(parent)
						&& starter.getCheckingMenu()!=null && starter.getCheckingMenu().isSelected())
					{
						startNodeTask(new CheckTask(parent));
					}
	//				System.out.println("Valid?: "+node+", "+newvalid);
				}
			}
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
			if(hasChanged(fn, VALID))
			{
				startNodeTask(new CheckTask(fn));
			}
			Boolean	val	= (Boolean)fn.getProperties().get(VALID);
			ret	= val==null || val.booleanValue();	// Valid, if not yet checked.
		}
		return ret;
	}
}
