package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.tools.common.CombiIcon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

/**
 *  A panel displaying components on the platform as tree.
 */
public class ComponentTreePanel extends JPanel
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"component_suspended", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/overlay_szzz.png"),
		"kill_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/overlay_kill.png"),
		"suspend_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/overlay_szzz.png"),
		"resume_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/overlay_wakeup.png"),
		"step_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/overlay_step.png")
	});
	
	//-------- attributes --------
	
	/** The component management service. */
	private IComponentManagementService	cms;
	
	/** The action for killing selected components. */
	private final Action	kill;
	
	/** The action for suspending selected components. */
	private final Action	suspend;
	
	/** The action for resuming selected components. */
	private final Action	resume;
	
	/** The action for stepping selected components. */
	private final Action	step;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(final IServiceProvider provider)
	{
		final ComponentTreeModel	model	= new ComponentTreeModel();
		final JTree	tree	= new JTree(model);
		tree.setCellRenderer(new ComponentTreeCellRenderer());
		tree.addMouseListener(new ComponentTreePopupListener());
		tree.setShowsRootHandles(true);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tree));
		
		kill	= new AbstractAction("Kill component", icons.getIcon("kill_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; i<paths.length; i++)
					{
						if(paths[i].getLastPathComponent() instanceof ComponentTreeNode)
						{
							cms.destroyComponent(((ComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName())
								.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
								}
							});
						}
					}
				}
			}
		};
		
		suspend	= new AbstractAction("Suspend component", icons.getIcon("suspend_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; i<paths.length; i++)
					{
						if(paths[i].getLastPathComponent() instanceof ComponentTreeNode)
						{
							cms.suspendComponent(((ComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName())
								.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
								}
							});
						}
					}
				}
			}
		};
		
		resume	= new AbstractAction("Resume component", icons.getIcon("resume_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; i<paths.length; i++)
					{
						if(paths[i].getLastPathComponent() instanceof ComponentTreeNode)
						{
							cms.resumeComponent(((ComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName())
								.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
								}
							});
						}
					}
				}
			}
		};
		
		step	= new AbstractAction("Step component", icons.getIcon("step_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; i<paths.length; i++)
					{
						if(paths[i].getLastPathComponent() instanceof ComponentTreeNode)
						{
							cms.stepComponent(((ComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName())
								.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
								}
							});
						}
					}
				}
			}
		};
		
		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				cms	= (IComponentManagementService)result;
				final ComponentIconCache	cic	= new ComponentIconCache(provider, tree);
				
				// Default overlays and popups.
				model.addOverlay(new INodeHandler()
				{
					public Icon getOverlay(IComponentTreeNode node)
					{
						Icon	ret	= null;
						if(node instanceof ComponentTreeNode)
						{
							IComponentDescription	desc	= ((ComponentTreeNode)node).getDescription();
							if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState())
								|| IComponentDescription.STATE_WAITING.equals(desc.getState()))
							{
								ret = icons.getIcon("component_suspended");
							}
						}
						return ret;
					}
					
					public Action[] getPopupActions(final IComponentTreeNode[] nodes)
					{
						Action[]	ret	= null;
						
						boolean	allcomp	= true;
						for(int i=0; allcomp && i<nodes.length; i++)
						{
							allcomp	= nodes[i] instanceof ComponentTreeNode;
						}
						
						if(allcomp)
						{
							boolean	allsusp	= true;
							for(int i=0; allsusp && i<nodes.length; i++)
							{
								allsusp	= IComponentDescription.STATE_SUSPENDED.equals(((ComponentTreeNode)nodes[i]).getDescription().getState())
									|| IComponentDescription.STATE_WAITING.equals(((ComponentTreeNode)nodes[i]).getDescription().getState());
							}
							boolean	allact	= true;
							for(int i=0; allact && i<nodes.length; i++)
							{
								allact	= IComponentDescription.STATE_ACTIVE.equals(((ComponentTreeNode)nodes[i]).getDescription().getState());
							}
							
							// Todo: Large icons for popup actions?
							Icon	base	= cic.getIcon(nodes[0], ((ComponentTreeNode)nodes[0]).getDescription().getType());
							Action	pkill	= new AbstractAction((String)kill.getValue(Action.NAME), new CombiIcon(new Icon[]{base, icons.getIcon("kill_component")}))
							{
								public void actionPerformed(ActionEvent e)
								{
									kill.actionPerformed(e);
								}
							};
							if(allact)
							{
								Action	psuspend	= new AbstractAction((String)suspend.getValue(Action.NAME), new CombiIcon(new Icon[]{base, icons.getIcon("suspend_component")}))
								{
									public void actionPerformed(ActionEvent e)
									{
										suspend.actionPerformed(e);
									}
								};
								ret	= new Action[]{pkill, psuspend};
							}
							else if(allsusp)
							{
								Action	presume	= new AbstractAction((String)resume.getValue(Action.NAME), new CombiIcon(new Icon[]{base, icons.getIcon("resume_component")}))
								{
									public void actionPerformed(ActionEvent e)
									{
										resume.actionPerformed(e);
									}
								};
								Action	pstep	= new AbstractAction((String)step.getValue(Action.NAME), new CombiIcon(new Icon[]{base, icons.getIcon("step_component")}))
								{
									public void actionPerformed(ActionEvent e)
									{
										step.actionPerformed(e);
									}
								};
								ret	= new Action[]{pkill, presume, pstep};
							}
							else
							{
								ret	= new Action[]{pkill};								
							}
						}
						
						return ret;
					}
				});

				
				// Hack!!! How to find root node?
				cms.getComponentDescriptions().addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentDescription[]	descriptions	= (IComponentDescription[])result;
						IComponentDescription	root	= null;
						for(int i=0; root==null && i<descriptions.length; i++)
						{
							if(descriptions[i].getParent()==null)
							{
								root	= descriptions[i];
							}
						}
						model.setRoot(new ComponentTreeNode(null, model, root, cms, ComponentTreePanel.this, cic));
					}
				});
				
				cms.addComponentListener(null, new IComponentListener()
				{
					public void componentRemoved(final IComponentDescription desc, Map results)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								IComponentTreeNode	node	= model.getNode(desc.getName());
								model.deregisterNode(desc.getName());
								if(node.getParent()!=null)
								{
									((AbstractComponentTreeNode)node.getParent()).removeChild(node);
								}
							}
						});
					}
					
					public void componentChanged(IComponentDescription desc)
					{
						ComponentTreeNode	node	= (ComponentTreeNode)model.getNode(desc.getName());
						node.setDescription(desc);
						model.fireNodeChanged(node);
					}
					
					public void componentAdded(final IComponentDescription desc)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								ComponentTreeNode	parent	= null;
								if(desc.getParent()!=null)
								{
									parent	= (ComponentTreeNode)model.getNode(desc.getParent());
								}
								
								IComponentTreeNode	node	= new ComponentTreeNode(parent, model, desc, cms, ComponentTreePanel.this, cic);
								if(parent!=null)
								{
									parent.addChild(node);
								}
							}
						});
					}
				});				
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Get the action for killing the components selected in the tree.
	 */
	public Action	getKillAction()
	{
		return kill;
	}
	
	/**
	 *  Get the action for suspending the components selected in the tree.
	 */
	public Action	getSuspendAction()
	{
		return suspend;
	}
	
	/**
	 *  Get the action for resuming the components selected in the tree.
	 */
	public Action	getResumeAction()
	{
		return resume;
	}
	
	/**
	 *  Get the action for stepping the components selected in the tree.
	 */
	public Action	getStepAction()
	{
		return step;
	}
}
