package jadex.tools.testcenter;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bridge.ILoadableElementModel;
import jadex.commons.SGUI;
import jadex.rules.state.IOAVState;
import jadex.tools.common.modeltree.CombiIcon;
import jadex.tools.common.modeltree.DefaultNodeFunctionality;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.ModelExplorerTreeModel;
import jadex.tools.common.modeltree.NodeTask;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.DefaultTreeModel;

/**
 *  Model tree node functionality, specific for the test center plugin.
 */
public class TestCenterNodeFunctionality extends DefaultNodeFunctionality
{
	//-------- constants --------
	
	/** The testcase property. */
	protected static final String	TESTCASE	= "testcase";
	
	/** The date when the testcase property was last checked. */
	protected static final String	TESTCASE_DATE	= "testcase_date";
	
	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"test_overlay",	SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/test_overlay.png"),	
		"checking_on",	SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_agent_testcheckanim.gif")	
	});
	
	//-------- attributes --------

	/** The test center plugin. */
	protected TestCenterPlugin	testcenter;
	
	/** The check indicator for the status bar. */
	protected JLabel	checkcomp;
	
	//-------- constructors --------
	
	/**
	 *  Create a test center node functionality.
	 */
	public TestCenterNodeFunctionality(TestCenterPlugin testcenter)
	{
		super(testcenter.getJCC());
		this.testcenter	= testcenter;
		checkcomp	= new JLabel(icons.getIcon("checking_on"));
		checkcomp.setToolTipText("Checking if agent models are test cases.");
	}
	
	//-------- INodeFunctionality interface --------
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon getIcon(IExplorerTreeNode node)
	{
		Icon icon	= super.getIcon(node);
		if(icon!=null && isTestcase(node))
		{
			icon	= new CombiIcon(new Icon[]
			{
				icon,
				icons.getIcon("test_overlay")
			});
		}
		return icon;
	}

	/**
	 *  Check if the testcase flag of a node is set to true.
	 */
	public boolean	isTestcase(IExplorerTreeNode node)
	{
//		System.out.println("isValid: "+node.getToolTipText());
		boolean	ret	= false;	// Unknown node types are no testcases by default
		if(node instanceof FileNode && testcenter.getCheckingMenu()!=null
			&& testcenter.getCheckingMenu().isSelected())
		{
			FileNode fn = (FileNode)node;
			Date	filedate	= getLastModified(fn);
			Date	testdate	= (Date) fn.getProperties().get(TESTCASE_DATE);
			boolean	check	= filedate!=null && (testdate==null || testdate.before(filedate));
			if(!check && filedate!=null)
			{
				List	children	= getChildren(fn);
				if(children!=null)
				{
					for(int i=0; !check && i<children.size(); i++)
					{
						Date childate	= (Date) ((FileNode)children.get(i)).getProperties().get(TESTCASE_DATE);
						
						// If child not checked, check child first before continuing.
						if(childate==null)
						{
							startNodeTask(new CheckTask(fn));
							break;
						}
						else
						{
							check	= testdate==null || testdate.before(childate);
						}
					}
				}
			}
			if(check)
			{
				startNodeTask(new CheckTask(fn));
			}
			Boolean	val	= (Boolean)fn.getProperties().get(TESTCASE);
			ret	= val!=null && val.booleanValue();	// No testcase, if not yet checked.
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
		isTestcase(node);
	}

	/**
	 *  Called when children of a directory node have been added or removed.
	 *  Empty default implementation to be overridden by subclasses.
	 */
	public void	childrenChanged(DirNode node)
	{
//		System.out.println("childrenChanged("+node.getToolTipText()+")");
		isTestcase(node);
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
			super(TestCenterNodeFunctionality.this, node,
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
				Boolean	val	= (Boolean)fn.getProperties().get(TESTCASE);
				boolean	oldtest	= val!=null && val.booleanValue();
				boolean	newtest	= false;
				
				// Check directory.
				if(node instanceof DirNode)
				{
					List	children	= getChildren(fn);
					for(int i=0; !newtest && children!=null && i<children.size(); i++)
					{
						newtest	= isTestcase((IExplorerTreeNode)children.get(i));
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
							ILoadableElementModel model = jcc.getAgent().getPlatform().getAgentFactory().loadModel(file);
							
							if(model!=null)
							{
								boolean ok	= model.getReport().isEmpty();

								// HACK!!!
								if(ok && model instanceof OAVAgentModel)
								{
									IOAVState	state	= ((OAVAgentModel)model).getState();
									Object	magent	= ((OAVAgentModel)model).getHandle();
									Collection	caparefs	= state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_capabilityrefs);
									if(caparefs!=null)
									{
										for(Iterator it=caparefs.iterator(); !newtest && it.hasNext(); )
										{
											Object	name	= state.getAttributeValue(it.next(), OAVBDIMetaModel.capabilityref_has_file);
											newtest = "jadex.bdi.planlib.test.Test".equals(name);
										}
									}
								}
							}
							// else unknown jadex file type -> ignore.
						}
						catch(Exception e)
						{
						}
					}
				}
				
				fn.getProperties().put(TESTCASE, new Boolean(newtest));	// Add always, because old value could be null.
				fn.getProperties().put(TESTCASE_DATE, new Date());
				if(oldtest!=newtest)
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
