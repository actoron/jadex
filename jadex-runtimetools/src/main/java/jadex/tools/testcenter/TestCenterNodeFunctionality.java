package jadex.tools.testcenter;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bridge.IAgentModel;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IExecutable;
import jadex.rules.state.IOAVState;
import jadex.tools.common.modeltree.AbstractNodeFunctionality;
import jadex.tools.common.modeltree.CombiIcon;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.DefaultTreeModel;

/**
 *  Model tree node functionality, specific for the test center plugin.
 */
public class TestCenterNodeFunctionality extends AbstractNodeFunctionality
{
	//-------- constants --------
	
	/** The testcase property. */
	protected static final String	TESTCASE	= "testcase";
	
	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"test_overlay",	SGUI.makeIcon(AbstractNodeFunctionality.class, "/jadex/tools/common/images/test_overlay.png"),	
		"checking_on",	SGUI.makeIcon(AbstractNodeFunctionality.class, "/jadex/tools/common/images/new_agent_testcheckanim.gif")	
	});
	
	//-------- attributes --------

	/** The test center plugin. */
	protected TestCenterPlugin	testcenter;
	
	/** The check indicator for the status bar. */
	protected JLabel	checkcomp;
	
	/** The nodes of queued check tasks. */
	protected Set	checkqueue;

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
	 *  Called when a change was detected in a node.
	 *  Check node, if necessary.
	 */
	public void	nodeChanged(IExplorerTreeNode node)
	{
		if(testcenter.getCheckingMenu()!=null && testcenter.getCheckingMenu().isSelected())
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

				if(node instanceof FileNode)
				{
					FileNode fn = (FileNode)node;
					boolean	oldtest	= isTestcase(node);
					boolean	newtest	= false;
					
					// Check directory.
					if(node instanceof DirNode)
					{
						for(int i=0; !newtest && i<node.getChildCount(); i++)
						{
							newtest	= isTestcase((IExplorerTreeNode) node.getChildAt(i));
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
					
					fn.getProperties().put(TESTCASE, new Boolean(newtest));	// Add always, because old value might be null.
					if(oldtest!=newtest)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String	tip	= node.getToolTipText();
								if(tip!=null)
									jcc.setStatusText("Checking "+tip);

								((DefaultTreeModel)explorer.getModel()).nodeChanged(node);
							}
						});

						IExplorerTreeNode	parent	= (IExplorerTreeNode) fn.getParent();
						if(parent instanceof DirNode && newtest!=isTestcase(parent)
							&& testcenter.getCheckingMenu()!=null && testcenter.getCheckingMenu().isSelected())
						{
							startCheckTask(parent);
						}
					}
				}
			}
			checkTaskFinished(node);
			return false;
		}
	}

	/**
	 *  Check if the testcase flag of a node is set to true.
	 */
	public boolean	isTestcase(IExplorerTreeNode node)
	{
		boolean	ret	= false;	// Unknown node types are no testcases by default
		if(node instanceof FileNode && testcenter.getCheckingMenu()!=null
			&& testcenter.getCheckingMenu().isSelected())
		{
			FileNode fn = (FileNode)node;
			Boolean	val	= (Boolean)fn.getProperties().get(TESTCASE);
			ret	= val!=null && val.booleanValue();	// No testcase, if not yet checked.
			
			// If not yet checked queue a check task.
			if(val==null)
				startCheckTask(node);
		}
		return ret;
	}
}
