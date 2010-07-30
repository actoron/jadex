package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.tools.starter.StarterPlugin;

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
		"resume_component", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_big.png"),
		"suspend_component", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_szzz_big.png"),
		"kill_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/new_killagent.png"),
		"component_suspended", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/tools/common/images/overlay_szzz.png")
	});
	
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
		
		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
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
							
							Action	kill	= new AbstractAction("Kill component", icons.getIcon("kill_component"))
							{
								public void actionPerformed(ActionEvent e)
								{
									for(int i=0; i<nodes.length; i++)
									{
										cms.destroyComponent(((ComponentTreeNode)nodes[i]).getDescription().getName())
											.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
										{
											public void customResultAvailable(Object source, Object result)
											{
											}
										});
									}
								}
							};
							Action	suspend	= new AbstractAction("Suspend component", icons.getIcon("suspend_component"))
							{
								public void actionPerformed(ActionEvent e)
								{
									for(int i=0; i<nodes.length; i++)
									{
										cms.suspendComponent(((ComponentTreeNode)nodes[i]).getDescription().getName())
											.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
										{
											public void customResultAvailable(Object source, Object result)
											{
											}
										});
									}
								}
							};
							Action	resume	= new AbstractAction("Resume component", icons.getIcon("resume_component"))
							{
								public void actionPerformed(ActionEvent e)
								{
									for(int i=0; i<nodes.length; i++)
									{
										cms.resumeComponent(((ComponentTreeNode)nodes[i]).getDescription().getName())
											.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
										{
											public void customResultAvailable(Object source, Object result)
											{
											}
										});
									}
								}
							};
							
							if(allact)
							{
								ret	= new Action[]{kill, suspend};
							}
							else if(allsusp)
							{
								ret	= new Action[]{kill, resume};
							}
							else
							{
								ret	= new Action[]{kill};								
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
}
