package jadex.base.gui.componenttree;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.ObjectInspectorPanel;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeCellRenderer;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.asynctree.TreePopupListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.xml.annotation.XMLClassname;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
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
	public static final UIDefaults icons = new UIDefaults(new Object[]
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
	
	/** The kill action constant. */
	public static final String KILL_ACTION = "Kill component";
	
	/** The proxy kill action constant. */
	public static final String PROXYKILL_ACTION = "Kill also remote component";

	/** The suspend action constant. */
	public static final String SUSPEND_ACTION = "Suspend component";

	/** The resume action constant. */
	public static final String RESUME_ACTION = "Resume component";

	/** The step action constant. */
	public static final String STEP_ACTION = "Step component";

	/** The refresh action constant. */
	public static final String REFRESH_ACTION = "Refresh";

	/** The refreshtree action constant. */
	public static final String REFRESHSUBTREE_ACTION = "Refresh subtree";

	/** The show properties action constant. */
	public static final String SHOWPROPERTIES_ACTION = "Show properties";
	
	/** The remove service action constant. */
	public static final String REMOVESERVICE_ACTION = "Remove service";

	/** The remove service action constant. */
	public static final String SHOWDETAILS_ACTION = "Show object details";

	
	//-------- attributes --------
	
	/** The external access. */
	protected final IExternalAccess	access;
	
	/** The component tree model. */
	protected final AsyncTreeModel	model;
	
	/** The component tree. */
	protected final JTree	tree;
	
	/** The component management service. */
	protected IComponentManagementService	cms;

	/** The actions. */
	protected Map actions;
	
	/** The properties panel. */
	protected final JScrollPane	proppanel;
	
	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(IExternalAccess access, CMSUpdateHandler cmshandler)
	{
		this(access, cmshandler, VERTICAL_SPLIT);
	}
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(final IExternalAccess access, CMSUpdateHandler cmshandler, int orientation)
	{
		super(orientation);
		this.setOneTouchExpandable(true);
		
		this.actions = new HashMap();
		this.access	= access;
		this.model	= new AsyncTreeModel();
		this.tree	= new JTree(model);
		tree.setCellRenderer(new AsyncTreeCellRenderer());
		tree.addMouseListener(new TreePopupListener());
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(0);
		tree.putClientProperty(CMSUpdateHandler.class, cmshandler);
		final ComponentIconCache	cic	= new ComponentIconCache(access, tree);
		JScrollPane	scroll	= new JScrollPane(tree);
		this.add(scroll);
		
		this.proppanel	= new JScrollPane();
		proppanel.setMinimumSize(new Dimension(0, 0));
		proppanel.setPreferredSize(new Dimension(0, 0));
		this.add(proppanel);
		this.setResizeWeight(1.0);
				

		final Action kill = new AbstractAction(KILL_ACTION, icons.getIcon("kill_component"))
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
//						final ITreeNode sel = (ITreeNode)paths[i].getLastPathComponent();
						cms.resumeComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								cms.destroyComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
								{
									public void customResultAvailable(Object result)
									{
										// Done by CMS listener?
//										if(sel.getParent()!=null)
//										{
//											((AbstractTreeNode)sel.getParent()).removeChild(sel);
//										}
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
		actions.put(kill.getValue(Action.NAME), kill);
		
		final Action proxykill = new AbstractAction(PROXYKILL_ACTION, icons.getIcon("kill_component"))
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
								
								access.scheduleStep(new IComponentStep()
								{
									@XMLClassname("proxykill")
									public Object	execute(IInternalAccess ia)
									{
										SServiceProvider.getService(ia.getServiceContainer(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//										ia.getRequiredService("rms")
											.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
										{
											public void customResultAvailable(Object result)
											{
												IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
												
												rms.getServiceProxy(cid, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
												{
													public void customResultAvailable(Object result)
													{
														final IComponentManagementService rcms = (IComponentManagementService)result;
														rcms.destroyComponent(cid);
														if(sel.getParent()!=null)
														{
															((AbstractTreeNode)sel.getParent()).removeChild(sel);
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
										return null;
									}
								});								
							}
						});
					}
				}
			}
		};
		actions.put(proxykill.getValue(Action.NAME), proxykill);

		final Action suspend	= new AbstractAction(SUSPEND_ACTION, icons.getIcon("suspend_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
						final ITreeNode sel = (ITreeNode)paths[i].getLastPathComponent();
						cms.suspendComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								// Required for remote nodes.
								sel.refresh(false);
							}
						});
					}
				}
			}
		};
		actions.put(suspend.getValue(Action.NAME), suspend);

		
		final Action resume = new AbstractAction(RESUME_ACTION, icons.getIcon("resume_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
						final ITreeNode sel = (ITreeNode)paths[i].getLastPathComponent();
						cms.resumeComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								// Required for remote nodes.
								sel.refresh(false);
							}
						});
					}
				}
			}
		};
		actions.put(resume.getValue(Action.NAME), resume);

		final Action step	= new AbstractAction(STEP_ACTION, icons.getIcon("step_component"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cms!=null)
				{
					TreePath[]	paths	= tree.getSelectionPaths();
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();

						final ITreeNode sel = (ITreeNode)paths[i].getLastPathComponent();
						cms.stepComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								// Required for remote nodes.
								sel.refresh(false);
							}
						});
					}
				}
			}
		};
		actions.put(step.getValue(Action.NAME), step);

		final Action refresh = new AbstractAction(REFRESH_ACTION, icons.getIcon("refresh"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath[]	paths	= tree.getSelectionPaths();
				for(int i=0; paths!=null && i<paths.length; i++)
				{
					((ITreeNode)paths[i].getLastPathComponent()).refresh(false);
				}
			}
		};
		actions.put(refresh.getValue(Action.NAME), refresh);

		final Action refreshtree	= new AbstractAction(REFRESHSUBTREE_ACTION, icons.getIcon("refresh_tree"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath[]	paths	= tree.getSelectionPaths();
				for(int i=0; paths!=null && i<paths.length; i++)
				{
					((ITreeNode)paths[i].getLastPathComponent()).refresh(true);
				}
			}
		};
		actions.put(refreshtree.getValue(Action.NAME), refreshtree);

		final Action showprops = new AbstractAction(SHOWPROPERTIES_ACTION, icons.getIcon("show_properties"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath	path	= tree.getSelectionPath();
				if(path!=null && ((ITreeNode)path.getLastPathComponent()).hasProperties())
				{
					showProperties(((ITreeNode)path.getLastPathComponent()).getPropertiesComponent());
				}
			}
		};
		actions.put(showprops.getValue(Action.NAME), showprops);

		final Action removeservice = new AbstractAction(REMOVESERVICE_ACTION, icons.getIcon("show_properties"))
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
		actions.put(removeservice.getValue(Action.NAME), removeservice);

		final Action showobject = new AbstractAction(SHOWDETAILS_ACTION, icons.getIcon("show_details"))
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionPath();
				if(path!=null)
				{
					final ITreeNode node = (ITreeNode)path.getLastPathComponent();
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
		actions.put(showobject.getValue(Action.NAME), showobject);

		// Default overlays and popups.
		model.addNodeHandler(new INodeHandler()
		{
			public Icon getOverlay(ITreeNode node)
			{
				return null;
			}

			public Action[] getPopupActions(ITreeNode[] nodes)
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

			public Action getDefaultAction(final ITreeNode node)
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
			public Icon getOverlay(ITreeNode node)
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
			
			public Action[] getPopupActions(final ITreeNode[] nodes)
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
			
			public Action getDefaultAction(ITreeNode node)
			{
				return null;
			}
		});

		access.scheduleStep(new IComponentStep()
		{
			@XMLClassname("init")
			public Object execute(IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				ia.getRequiredService("cms")
					.addResultListener(new DelegationResultListener(ret));
				return ret;
			}
		}).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
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
	 *  Get the action.
	 *  @param name The action name.
	 *  @return The action.
	 */
	public Action getAction(String name)
	{
		return (Action)actions.get(name);
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
	public AsyncTreeModel	getModel()
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
	 *  Get the access.
	 *  @return the access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return access;
	}

	/**
	 *  Dispose the tree.
	 *  Should be called to remove listeners etc.
	 */
	public void	dispose()
	{
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
