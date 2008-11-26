package jadex.tools.testcenter;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IExecutable;
import jadex.rules.state.IOAVState;
import jadex.tools.common.modeltree.AbstractNodeFunctionality;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.JarNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.RootNode;
import jadex.tools.common.plugin.IControlCenter;

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
		"agent", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_agent.png"),
		"agent_testable", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_agent_testable.png"),
		"capability", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_capability_small.png"),
		// Todo: testable capability icon.
		"capability_testable", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_capability_small.png"),
		"src_folder", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder.png"),
		"src_folder_testable", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder_testable.png"),
		"package", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_package.png"),
		"package_testable", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_package_testable.png"),
		"src_jar", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar.png"),
		// Todo: testable jar icon.
		"src_jar_testable", SGUI.makeIcon(TestCenterNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar.png"),
		"checking_on",	SGUI.makeIcon(AbstractNodeFunctionality.class, "/jadex/tools/common/images/new_agent_testcheckanim.gif"),	
	});
	
	//-------- attributes --------

	/** The check indicator for the status bar. */
	protected JLabel	checkcomp;
	
	/** The nodes of queued check tasks. */
	protected Set	checkqueue;

	//-------- constructors --------
	
	/**
	 *  Create a test center node functionality.
	 */
	public TestCenterNodeFunctionality(IControlCenter jcc)
	{
		super(jcc);
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
		Icon icon	= null;
		if(node instanceof FileNode)
		{
			boolean	test = isTestcase(node);// || !fn.getRootNode().isChecking();

			if(node instanceof JarNode)
			{
				icon =  icons.getIcon(test ? "src_jar_testable" :  "src_jar");
			}
			else if(node instanceof DirNode)
			{
				if(node.getParent() instanceof RootNode)
				{
					icon	= icons.getIcon(test ? "src_folder_testable" : "src_folder");
				}
				else
				{
					icon	= icons.getIcon(test ? "package_testable" : "package");
				}
			}
			else if (node instanceof FileNode)
			{
				FileNode fn = (FileNode)node;
				IJadexAgentFactory	fac	= jcc.getAgent().getPlatform().getAgentFactory();
				if(fac.isLoadable(fn.getFile().getName()) && fac.isStartable(fn.getFile().getName()))
				{
					icon	= icons.getIcon(test ? "agent_testable" : "agent");
				}
				else if(fac.isLoadable(fn.getFile().getName()))
				{
					icon	= icons.getIcon(test ? "capability_testable" : "capability");
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
								IJadexModel model = jcc.getAgent().getPlatform().getAgentFactory().loadModel(file);
								
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
						if(parent instanceof DirNode && newtest!=isTestcase(parent))
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
		if(node instanceof FileNode)
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
