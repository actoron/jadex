package jadex.wfms.plugin.starter;

import jadex.bridge.ILoadableElementModel;
import jadex.commons.SGUI;
import jadex.tools.common.modeltree.CombiIcon;
import jadex.tools.common.modeltree.DefaultNodeFunctionality;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.ModelExplorerTreeModel;
import jadex.tools.common.modeltree.NodeTask;
import jadex.wfms.service.execution.IExecutionService;
import jadex.wfms.service.repository.IModelRepositoryService;

import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  Model tree node functionality, specific for the starter plugin.
 */
public class StarterNodeFunctionality extends DefaultNodeFunctionality
{
	//-------- constants --------
	
	/** The valid property. */
	protected static final String	VALID	= "valid";
	
	/** The last check date of the valid property (Date). */
	protected static final String	VALID_DATE	= "valid_date";

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
	
	//-------- methods --------
	
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
	 *  Check if the valid flag of a node is set to true.
	 */
	public boolean	isValid(IExplorerTreeNode node)
	{
//		System.out.println("isValid: "+node.getToolTipText());
		boolean	ret	= true;	// Unknown node types are valid by default
		if(node instanceof FileNode && starter.getCheckingMenu()!=null
			&& starter.getCheckingMenu().isSelected())
		{
			FileNode fn = (FileNode)node;
			Date	filedate	= getLastModified(fn);
			Date	validate	= (Date) fn.getProperties().get(VALID_DATE);
			boolean	check	= filedate!=null && (validate==null || validate.before(filedate));
			if(!check && filedate!=null)
			{
				List	children	= getChildren(fn);
				if(children!=null)
				{
					for(int i=0; !check && i<children.size(); i++)
					{
						Date childate	= (Date) ((FileNode)children.get(i)).getProperties().get(VALID_DATE);
						
						// If child not checked, check child first before continuing.
						if(childate==null)
						{
							startNodeTask(new CheckTask(fn));
							break;
						}
						else
						{
							check	= validate==null || validate.before(childate);
						}
					}
				}
			}
			if(check)
			{
				startNodeTask(new CheckTask(fn));
			}
			Boolean	val	= (Boolean)fn.getProperties().get(VALID);
			ret	= val==null || val.booleanValue();	// Valid, if not yet checked.
		}
		return ret;
	}

	/**
	 *  Called when the corresponding file of a node has changed.
	 *  Empty default implementation to be overridden by subclasses.
	 */
	public void	nodeChanged(FileNode node)
	{
//		System.out.println("nodeChanged("+node.getToolTipText()+")");
		isValid(node);
	}

	/**
	 *  Called when children of a directory node have been added or removed.
	 *  Empty default implementation to be overridden by subclasses.
	 */
	public void	childrenChanged(DirNode node)
	{
//		System.out.println("childrenChanged("+node.getToolTipText()+")");
		isValid(node);
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
//			System.out.println("Check: "+node.getToolTipText());
			if(node instanceof FileNode)
			{
				FileNode fn = (FileNode)node;
				Boolean	val	= (Boolean)fn.getProperties().get(VALID);
				boolean	oldvalid	= val==null || val.booleanValue();
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
					try
					{
						if(((IExecutionService)jcc.getServiceContainer().getService(IExecutionService.class)).isLoadable(file))
						{
//							IModelRepositoryService repo = (IModelRepositoryService)jcc.getServiceContainer().getService(IModelRepositoryService.class);
							ILoadableElementModel model = ((IExecutionService)jcc.getServiceContainer().getService(IExecutionService.class)).loadModel(file);
							if(model!=null)
							{
								newvalid	= model.getReport().isEmpty();
							}
						}
					}
					catch(Exception e)
					{
						System.err.println("Error checking: "+node.getToolTipText());
//							e.printStackTrace();
						newvalid	= false;
					}
				}
				
				fn.getProperties().put(VALID, new Boolean(newvalid));	// Add always, because old value could be null.
				fn.getProperties().put(VALID_DATE, new Date());
				if(oldvalid!=newvalid)
				{
					if(node.getParent() instanceof DirNode)
						childrenChanged((DirNode) node.getParent());
					
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
}
