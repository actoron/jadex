package jadex.base.gui.componenttree;

import jadex.base.gui.ObjectInspectorPanel;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.TreeExpansionHandler;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreePath;

/**
 *  A panel displaying components on the platform as tree.
 */
public class ComponentTreePanel extends JSplitPane
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"component_suspended", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_szzz.png"),
		"kill_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/new_killagent.png"),
		"suspend_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/new_agent_szzz_big.png"),
		"resume_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/resume_component.png"),
		"step_component", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/step_component.png"),
		"refresh", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/refresh_component.png"),
		"refresh_tree", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/refresh_tree.png"),
		"show_properties", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/new_agent_props.png"),
		"show_details", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/new_agent_details.png"),
		"overlay_kill", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_kill.png"),
		"overlay_suspend", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_szzz.png"),
		"overlay_resume", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_wakeup.png"),
		"overlay_step", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_step.png"),
		// no overlay icon for idle (default state)
//		"overlay_idle", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_trafficlight_red.png"),
		"overlay_ready", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_busy.png"),
		"overlay_running", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_gearwheel.png"),
		"overlay_refresh", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
		"overlay_refreshtree", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
		"overlay_showprops", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_doc.png"),
		"overlay_showobject", SGUI.makeIcon(ComponentTreePanel.class, "/jadex/base/gui/images/overlay_bean.png")
	});
	
	//-------- attributes --------
	
	/** The service provider. */
	private final IServiceProvider	provider;
	
	/** The component tree model. */
	private final ComponentTreeModel	model;
	
	/** The component tree. */
	private final JTree	tree;
	
	/** The component management service. */
	private IComponentManagementService	cms;
	
	/** The action for killing selected components. */
	private final Action	kill;
	
	/** The action for killing selected proxy component. */
	private final Action	proxykill;
	
	/** The action for suspending selected components. */
	private final Action	suspend;
	
	/** The action for resuming selected components. */
	private final Action	resume;
	
	/** The action for stepping selected components. */
	private final Action	step;
	
	/** The action for refreshing selected components. */
	private final Action	refresh;
	
	/** The action for recursively refreshing selected components. */
	private final Action	refreshtree;
	
	/** The action for showing properties of the selected node. */
	private final Action	showprops;
	
	/** The action for showing object details of the selected node. */
	private final Action showobject;
	
	/** The action for removing a service. */
	private final Action removeservice;

	/** The component listener. */
	private final ICMSComponentListener	listener;
	
	/** The properties panel. */
	private final JScrollPane	proppanel;
	
//	/** The object panel. */
//	private final JScrollPane	objectpanel;

	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(IServiceProvider provider)
	{
		this(provider, VERTICAL_SPLIT);
	}
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(final IServiceProvider provider, int orientation)
	{
		super(orientation);
		this.setOneTouchExpandable(true);
		
		this.provider	= provider;
		this.model	= new ComponentTreeModel();
		this.tree	= new JTree(model);
		tree.setCellRenderer(new ComponentTreeCellRenderer());
		tree.addMouseListener(new ComponentTreePopupListener());
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(0);
		final ComponentIconCache	cic	= new ComponentIconCache(provider, tree);
		JScrollPane	scroll	= new JScrollPane(tree);
		this.add(scroll);
		
		this.proppanel	= new JScrollPane();
		proppanel.setMinimumSize(new Dimension(0, 0));
		proppanel.setPreferredSize(new Dimension(0, 0));
		this.add(proppanel);
		this.setResizeWeight(1.0);
				
		listener	= new ICMSComponentListener()
		{
			public void componentRemoved(final IComponentDescription desc, Map results)
			{
				final IComponentTreeNode	node	= model.getNodeOrAddZombie(desc.getName());
				if(node!=null)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							if(node.getParent()!=null)
							{
								((AbstractComponentTreeNode)node.getParent()).removeChild(node);
							}
						}
					});
				}
			}
			
			public void componentChanged(final IComponentDescription desc)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						ComponentTreeNode	node	= (ComponentTreeNode)model.getAddedNode(desc.getName());
						if(node!=null)
						{
							node.setDescription(desc);
							model.fireNodeChanged(node);
						}
					}
				});
			}
			
			public void componentAdded(final IComponentDescription desc)
			{
//				System.err.println(""+model.hashCode()+" Panel->addChild queued: "+desc.getName()+", "+desc.getParent());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						final ComponentTreeNode	parentnode = desc.getParent()==null? null: (ComponentTreeNode)model.getAddedNode(desc.getParent());
						if(parentnode!=null)
						{
							parentnode.createComponentNode(desc).addResultListener(new SwingDefaultResultListener()
							{
								public void customResultAvailable(Object result)
								{
									IComponentTreeNode	node = (IComponentTreeNode)result;
//									System.out.println("addChild: "+parentnode+", "+node);
									try
									{
										if(parentnode.getIndexOfChild(node)==-1)
										{
//											System.err.println(""+model.hashCode()+" Panel->addChild: "+node+", "+parentnode);
											parentnode.addChild(node);
										}
									}
									catch(Exception e)
									{
										System.err.println(""+model.hashCode()+" Broken node: "+node);
										System.err.println(""+model.hashCode()+" Parent: "+parentnode+", "+parentnode.getCachedChildren());
										e.printStackTrace();
//										model.fireNodeAdded(parentnode, node, parentnode.getIndexOfChild(node));
									}
								}
								
								public void customExceptionOccurred(Exception exception)
								{
									// May happen, when component removed in mean time.
								}										
							});
						}
					}
				});
			}
		};

		kill = new AbstractAction("Kill component", icons.getIcon("kill_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						// note: cannot use getComponentIdenfier() due to proxy components return their remote cid
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
						cms.resumeComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								cms.destroyComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
								{
									public void customResultAvailable(Object result)
									{
										if(sel instanceof VirtualComponentTreeNode && sel.getParent()!=null)
										{
											((AbstractComponentTreeNode)sel.getParent()).removeChild(sel);
										}
									}
									
									public void customExceptionOccurred(Exception exception)
									{
										super.customExceptionOccurred(new RuntimeException("Could not kill component: "+cid, exception));
									}
								});
							}
						});
					}
				}
			}
		};
		
		proxykill = new AbstractAction("Kill also remote component", icons.getIcon("kill_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final ProxyComponentTreeNode sel = (ProxyComponentTreeNode)paths[i].getLastPathComponent();
						
						sel.getRemoteComponentIdentifier().addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								final IComponentIdentifier cid = (IComponentIdentifier)result;
								
								SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
									.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
								{
									public void customResultAvailable(Object result)
									{
										IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
										
										rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
										{
											public void customResultAvailable(Object result)
											{
												final IComponentManagementService rcms = (IComponentManagementService)result;
												rcms.destroyComponent(cid);
												if(sel.getParent()!=null)
												{
													((AbstractComponentTreeNode)sel.getParent()).removeChild(sel);
												}
												
												// Hack!!! Result will not be received when remote comp is platform. 
//													.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//												{
//													public void customResultAvailable(Object source, Object result)
//													{
//														if(sel.getParent()!=null)
//														{
//															((AbstractComponentTreeNode)sel.getParent()).removeChild(sel);
//														}
//													}
//												});
											}
										});
									}
								});
							}
						});
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
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
						cms.suspendComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								if(sel instanceof VirtualComponentTreeNode)
								{
									sel.refresh(false, false);
								}
							}
						});
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
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
						cms.resumeComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								if(sel instanceof VirtualComponentTreeNode)
								{
									sel.refresh(false, false);
								}
							}
						});
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
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();

						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
						cms.stepComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								if(sel instanceof VirtualComponentTreeNode)
								{
									sel.refresh(false, false);
								}
							}
						});
					}
				}
			}
		};

		refresh	= new AbstractAction("Refresh", icons.getIcon("refresh"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath[]	paths	= tree.getSelectionPaths();
				for(int i=0; paths!=null && i<paths.length; i++)
				{
					((IComponentTreeNode)paths[i].getLastPathComponent()).refresh(false, true);
				}
			}
		};

		refreshtree	= new AbstractAction("Refresh subtree", icons.getIcon("refresh_tree"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath[]	paths	= tree.getSelectionPaths();
				for(int i=0; paths!=null && i<paths.length; i++)
				{
					((IComponentTreeNode)paths[i].getLastPathComponent()).refresh(true, true);
				}
			}
		};

		showprops	= new AbstractAction("Show properties", icons.getIcon("show_properties"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath	path	= tree.getSelectionPath();
				if(path!=null && ((IComponentTreeNode)path.getLastPathComponent()).hasProperties())
				{
					showProperties(((IComponentTreeNode)path.getLastPathComponent()).getPropertiesComponent());
				}
			}
		};
		
		removeservice = new AbstractAction("Remove service", icons.getIcon("show_properties"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionPath();
				if(path!=null)
				{
					final ServiceContainerNode scn = (ServiceContainerNode)path.getPathComponent(path.getPathCount()-2);
					final ServiceNode sn = (ServiceNode)path.getLastPathComponent();
					scn.getContainer().removeService(sn.getService().getServiceIdentifier()).addResultListener(new SwingDefaultResultListener(proppanel)
					{
						public void customResultAvailable(Object result)
						{
							scn.removeChild(sn);
						}
					});
				}
			}
		};
		
		showobject = new AbstractAction("Show object details", icons.getIcon("show_details"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionPath();
				if(path!=null)
				{
					final IComponentTreeNode node = (IComponentTreeNode)path.getLastPathComponent();
					if(node instanceof ServiceNode)
					{
						Object obj = ((ServiceNode)node).getService();
						JPanel panel = new ObjectInspectorPanel(obj);
						showProperties(panel);
					}
					else if(node instanceof IActiveComponentTreeNode)
					{
						//IComponentDescription desc = ((IActiveComponentTreeNode)node).getDescription();
						IComponentIdentifier cid = ((IActiveComponentTreeNode)node).getDescription().getName();
						cms.getExternalAccess(cid).addResultListener(new SwingDefaultResultListener((Component)null)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess	ea	= (IExternalAccess)result;
								JPanel panel = new ObjectInspectorPanel(ea);
								showProperties(panel);
							}
						});
					}
				}
			}
		};

		// Default overlays and popups.
		model.addNodeHandler(new INodeHandler()
		{
			public Icon getOverlay(IComponentTreeNode node)
			{
				return null;
			}

			public Action[] getPopupActions(IComponentTreeNode[] nodes)
			{
				List ret = new ArrayList();
				Icon	base	= nodes[0].getIcon();
				
				if(nodes.length==1)
				{
					if(nodes[0].hasProperties())
					{
						Action pshowprops = new AbstractAction((String)showprops.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_showprops")}) : (Icon)showprops.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								showprops.actionPerformed(e);
							}
						};
						ret.add(pshowprops);
					}
					
					if(nodes[0] instanceof ServiceNode || nodes[0] instanceof IActiveComponentTreeNode)
					{
						Action pshowobject = new AbstractAction((String)showobject.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_showobject")}) : (Icon)showprops.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								showobject.actionPerformed(e);
							}
						};
						ret.add(pshowobject);
					}
					
					if(nodes[0] instanceof ServiceNode && !Proxy.isProxyClass(((ServiceNode)nodes[0]).getService().getClass()))
					{
						Action premoveservice = new AbstractAction((String)removeservice.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_kill")}) : (Icon)showprops.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								removeservice.actionPerformed(e);
							}
						};
						ret.add(premoveservice);
					}
				}
				
				Action	prefresh	= new AbstractAction((String)refresh.getValue(Action.NAME),
					base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refresh")}) : (Icon)refresh.getValue(Action.SMALL_ICON))
				{
					public void actionPerformed(ActionEvent e)
					{
						refresh.actionPerformed(e);
					}
				};
				ret.add(prefresh);
				Action	prefreshtree	= new AbstractAction((String)refreshtree.getValue(Action.NAME),
					base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refreshtree")}) : (Icon)refreshtree.getValue(Action.SMALL_ICON))
				{
					public void actionPerformed(ActionEvent e)
					{
						refreshtree.actionPerformed(e);
					}
				};
				ret.add(prefreshtree);
			
				return (Action[])ret.toArray(new Action[0]);
			}

			public Action getDefaultAction(final IComponentTreeNode node)
			{
				Action	ret	= null;
				if(node.hasProperties())
				{
					ret	= showprops;
				}
				return ret;
			}
		});
		
		model.addNodeHandler(new INodeHandler()
		{
			public Icon getOverlay(IComponentTreeNode node)
			{
				Icon	ret	= null;
				
				// todo: interface or base class for real ac-nodes?
				IComponentDescription	desc = null;
				if(node instanceof IActiveComponentTreeNode)
				{
					desc = ((IActiveComponentTreeNode)node).getDescription();
				
					if(IComponentDescription.PROCESSINGSTATE_READY.equals(desc.getProcessingState()))
					{
						ret = icons.getIcon("overlay_ready");
					}
					else if(IComponentDescription.PROCESSINGSTATE_RUNNING.equals(desc.getProcessingState()))
					{
						ret = icons.getIcon("overlay_running");
					}
					else if(IComponentDescription.PROCESSINGSTATE_IDLE.equals(desc.getProcessingState()))
					{
						if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
						{
							ret = icons.getIcon("component_suspended");
						}
					}					
				}
				return ret;
			}
			
			public Action[] getPopupActions(final IComponentTreeNode[] nodes)
			{
				List ret = new ArrayList();
				
				boolean	allcomp	= true;
				for(int i=0; allcomp && i<nodes.length; i++)
				{
					allcomp	= nodes[i] instanceof IActiveComponentTreeNode;
				}
				boolean	allproxy = true;
				for(int i=0; allproxy && i<nodes.length; i++)
				{
					allproxy = nodes[i] instanceof ProxyComponentTreeNode && ((ProxyComponentTreeNode)nodes[i]).isConnected();
				}
				
				if(allcomp)
				{
					boolean	allsusp	= true;
					for(int i=0; allsusp && i<nodes.length; i++)
					{
						allsusp	= IComponentDescription.STATE_SUSPENDED.equals(((IActiveComponentTreeNode)nodes[i]).getDescription().getState());
					}
					boolean	allact	= true;
					for(int i=0; allact && i<nodes.length; i++)
					{
						allact	= IComponentDescription.STATE_ACTIVE.equals(((IActiveComponentTreeNode)nodes[i]).getDescription().getState());
					}
					
					// Todo: Large icons for popup actions?
					Icon	base	= nodes[0].getIcon();
					Action	pkill	= new AbstractAction((String)kill.getValue(Action.NAME),
						base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_kill")}) : (Icon)kill.getValue(Action.SMALL_ICON))
					{
						public void actionPerformed(ActionEvent e)
						{
							kill.actionPerformed(e);
						}
					};
					ret.add(pkill);
					
					if(allproxy)
					{
						Action	prkill	= new AbstractAction((String)proxykill.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_kill")}) : (Icon)proxykill.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								proxykill.actionPerformed(e);
							}
						};
						ret.add(prkill);
					}
					
					if(allact)
					{
						Action	psuspend	= new AbstractAction((String)suspend.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_suspend")}) : (Icon)suspend.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								suspend.actionPerformed(e);
							}
						};
						ret.add(psuspend);
//						ret	= new Action[]{pkill, psuspend};
					}
					else if(allsusp)
					{
						Action	presume	= new AbstractAction((String)resume.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_resume")}) : (Icon)resume.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								resume.actionPerformed(e);
							}
						};
						Action	pstep	= new AbstractAction((String)step.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_step")}) : (Icon)step.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								step.actionPerformed(e);
							}
						};
						ret.add(presume);
						ret.add(pstep);
//						ret	= new Action[]{pkill, presume, pstep};
					}
//					else
//					{
//						ret	= new Action[]{pkill};								
//					}
				}
				
				return (Action[])ret.toArray(new Action[ret.size()]);
			}
			
			public Action getDefaultAction(IComponentTreeNode node)
			{
				return null;
			}
		});

		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object result)
			{
				cms	= (IComponentManagementService)result;
				
				// Hack!!! How to find root node?
				cms.getComponentDescriptions().addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
				{
					public void customResultAvailable(Object result)
					{
						IComponentDescription[]	descriptions	= (IComponentDescription[])result;
						if(descriptions.length!=0)
						{
							IComponentDescription	root	= null;
							for(int i=0; root==null && i<descriptions.length; i++)
							{
								if(descriptions[i].getParent()==null)
								{
									root	= descriptions[i];
								}
							}
							if(root==null)
								throw new RuntimeException("No root node found: "+SUtil.arrayToString(descriptions));
							model.setRoot(new ComponentTreeNode(null, model, tree, root, cms, cic));
							// Expand root node.
							TreeExpansionHandler	teh	= new TreeExpansionHandler(tree);
							teh.treeExpanded(new TreeExpansionEvent(tree, new TreePath(model.getRoot())));
						}
					}
				});
				
				cms.addComponentListener(null, listener);				
			}
		});
		
		// Remove selection in tree, when user clicks in background.
		tree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(tree.getPathForLocation(e.getX(), e.getY())==null)
				{
					tree.clearSelection();
				}
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
	
	/**
	 *  Get the action for refreshing the components selected in the tree.
	 */
	public Action	getRefreshAction()
	{
		return refresh;
	}
	
	/**
	 *  Get the action for recursively refreshing the components selected in the tree.
	 */
	public Action	getRefreshTreeAction()
	{
		return refreshtree;
	}
	
	/**
	 *  Get the action for showing component properties.
	 */
	public Action	getShowPropertiesAction()
	{
		return showprops;
	}
	
	/**
	 *  Get the action for showing component details.
	 */
	public Action	getShowObjectDetailsAction()
	{
		return showobject;
	}
	
	/**
	 *  Add a node handler.
	 */
	public void	addNodeHandler(INodeHandler handler)
	{
		model.addNodeHandler(handler);
	}

	/**
	 *  Get the tree model.
	 */
	public ComponentTreeModel	getModel()
	{
		return model;
	}
	
	/**
	 *  Get the tree.
	 */
	public JTree	getTree()
	{
		return tree;
	}
	
	/**
	 *  Dispose the tree.
	 *  Should be called to remove listeners etc.
	 */
	public void	dispose()
	{
		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				cms	= (IComponentManagementService)result;
				cms.removeComponentListener(null, listener);				
			}
			public void customExceptionOccurred(Exception exception)
			{
				// ignore
			}
		});
		
		getModel().dispose();
	}
	
	/**
	 *  Set the title and contents of the properties panel.
	 */
	public void	showProperties(JComponent content)
	{
		proppanel.setViewportView(content);
		proppanel.repaint();

		// Code to simulate a one touch expandable click,
	 	// see BasicSplitPaneDivider.OneTouchActionHandler)
		
		Insets  insets = getInsets();
		int lastloc = getLastDividerLocation();
	    int currentloc = getUI().getDividerLocation(this);
		int newloc = currentloc;
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI)getUI()).getDivider();

		boolean	adjust	= false;
		if(getOrientation()==VERTICAL_SPLIT)
		{
			if(currentloc >= (getHeight() - insets.bottom - divider.getHeight())) 
			{
				adjust	= true;
				int maxloc = getMaximumDividerLocation();
				newloc = lastloc>=0 && lastloc<maxloc? lastloc: maxloc*1/2;
	        }			
		}
		else
		{
			if(currentloc >= (getWidth() - insets.right - divider.getWidth())) 
			{
				adjust	= true;
				int maxloc = getMaximumDividerLocation();
				newloc = lastloc>=0 && lastloc<maxloc? lastloc: maxloc*1/2;
	        }			
		}

		if(adjust && currentloc!=newloc) 
		{
			setDividerLocation(newloc);
			setLastDividerLocation(currentloc);
		}
	}
}
