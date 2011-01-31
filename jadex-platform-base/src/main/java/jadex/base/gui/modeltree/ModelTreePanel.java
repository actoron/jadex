package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeCellRenderer;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.asynctree.TreePopupListener;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *  A panel displaying components on the platform as tree.
 */
public class ModelTreePanel extends JSplitPane
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addpath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_addfolder.png"),
//		"removepath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_removefolder.png"),
//		"checker",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_checker.png"),
//		"refresh",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_refresh.png"),
//		"refresh_menu",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/tools/common/images/new_refresh_small.png"),
	});
	
	//-------- attributes --------
	
	/** The external access. */
	protected final IExternalAccess	exta;
	
	/** The component tree model. */
	protected final AsyncTreeModel	model;
	
	/** The component tree. */
	protected final JTree	tree;
	
	/** The component management service. */
//	protected IComponentManagementService	cms;
	
	/** The action for killing selected components. */
//	protected final Action	kill;
	
	/** The action for killing selected proxy component. */
//	protected final Action	proxykill;
	
	/** The action for suspending selected components. */
//	protected final Action	suspend;
	
	/** The action for resuming selected components. */
//	protected final Action	resume;
	
	/** The action for stepping selected components. */
//	protected final Action	step;
	
	/** The action for refreshing selected components. */
	protected Action	refresh;
	
	/** The action for recursively refreshing selected components. */
	protected Action	refreshtree;
	
	/** The action for showing properties of the selected node. */
	protected Action	showprops;
	
	/** The action for showing object details of the selected node. */
	protected Action showobject;
	
	/** The action for removing a service. */
//	protected final Action removeservice;

	/** The component listener. */
//	protected final ICMSComponentListener listener;
	
	/** The properties panel. */
	protected final JScrollPane	proppanel;
	
//	/** The object panel. */
//	protected final JScrollPane	objectpanel;

	/** The file chooser. */
	protected JFileChooser filechooser;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ModelTreePanel(IExternalAccess exta)
	{
		this(exta, VERTICAL_SPLIT);
	}
	
	/**
	 *  Create a new component tree panel.
	 */
	public ModelTreePanel(final IExternalAccess exta, int orientation)
	{
		super(orientation);
		this.setOneTouchExpandable(true);
		
		this.exta	= exta;
		this.model	= new AsyncTreeModel();
		this.tree	= new JTree(model);
		tree.setCellRenderer(new AsyncTreeCellRenderer());
		tree.addMouseListener(new TreePopupListener());
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(0);
//		final ComponentIconCache	cic	= new ComponentIconCache(access, tree);
		JScrollPane	scroll	= new JScrollPane(tree);
		this.add(scroll);
		
		this.proppanel	= new JScrollPane();
		proppanel.setMinimumSize(new Dimension(0, 0));
		proppanel.setPreferredSize(new Dimension(0, 0));
		this.add(proppanel);
		this.setResizeWeight(1.0);
		
		filechooser = new JFileChooser(".");
		filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		filechooser.addChoosableFileFilter(new FileFilter()
		{
			public String getDescription()
			{
				return "Paths or .jar files";
			}

			public boolean accept(File f)
			{
				String name = f.getName().toLowerCase();
				return f.isDirectory() || name.endsWith(".jar");
			}
		});
				
//		listener	= new ICMSComponentListener()
//		{
//			public IFuture componentRemoved(final IComponentDescription desc, Map results)
//			{
//				final ITreeNode node = model.getNodeOrAddZombie(desc.getName());
//				if(node!=null)
//				{
//					SwingUtilities.invokeLater(new Runnable()
//					{
//						public void run()
//						{
//							if(node.getParent()!=null)
//							{
//								((AbstractTreeNode)node.getParent()).removeChild(node);
//							}
//						}
//					});
//				}
//				return new Future(null);
//			}
//			
//			public IFuture componentChanged(final IComponentDescription desc)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						ITreeNode node = model.getAddedNode(desc.getName());
//						if(node!=null)
//						{
//							node.setDescription(desc);
//							model.fireNodeChanged(node);
//						}
//					}
//				});
//				return new Future(null);
//			}
//			
//			public IFuture componentAdded(final IComponentDescription desc)
//			{
////				System.err.println(""+model.hashCode()+" Panel->addChild queued: "+desc.getName()+", "+desc.getParent());
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						final ComponentTreeNode	parentnode = desc.getParent()==null? null: (ComponentTreeNode)model.getAddedNode(desc.getParent());
//						if(parentnode!=null)
//						{
//							parentnode.createComponentNode(desc).addResultListener(new SwingDefaultResultListener()
//							{
//								public void customResultAvailable(Object result)
//								{
//									IComponentTreeNode	node = (IComponentTreeNode)result;
////									System.out.println("addChild: "+parentnode+", "+node);
//									try
//									{
//										if(parentnode.getIndexOfChild(node)==-1)
//										{
////											System.err.println(""+model.hashCode()+" Panel->addChild: "+node+", "+parentnode);
//											parentnode.addChild(node);
//										}
//									}
//									catch(Exception e)
//									{
//										System.err.println(""+model.hashCode()+" Broken node: "+node);
//										System.err.println(""+model.hashCode()+" Parent: "+parentnode+", "+parentnode.getCachedChildren());
//										e.printStackTrace();
////										model.fireNodeAdded(parentnode, node, parentnode.getIndexOfChild(node));
//									}
//								}
//								
//								public void customExceptionOccurred(Exception exception)
//								{
//									// May happen, when component removed in mean time.
//								}										
//							});
//						}
//					}
//				});
//				
//				return new Future(null);
//			}
//		};
//
//		kill = new AbstractAction("Kill component", icons.getIcon("kill_component"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(cms!=null)
//				{
//					TreePath[]	paths	= tree.getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						// note: cannot use getComponentIdenfier() due to proxy components return their remote cid
//						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
//						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
//						cms.resumeComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//						{
//							public void customResultAvailable(Object result)
//							{
//								cms.destroyComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//								{
//									public void customResultAvailable(Object result)
//									{
//										if(sel instanceof VirtualComponentTreeNode && sel.getParent()!=null)
//										{
//											((AbstractComponentTreeNode)sel.getParent()).removeChild(sel);
//										}
//									}
//									
//									public void customExceptionOccurred(Exception exception)
//									{
//										super.customExceptionOccurred(new RuntimeException("Could not kill component: "+cid, exception));
//									}
//								});
//							}
//						});
//					}
//				}
//			}
//		};
//		
//		proxykill = new AbstractAction("Kill also remote component", icons.getIcon("kill_component"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(cms!=null)
//				{
//					TreePath[]	paths	= tree.getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						final ProxyComponentTreeNode sel = (ProxyComponentTreeNode)paths[i].getLastPathComponent();
//						
//						sel.getRemoteComponentIdentifier().addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//						{
//							public void customResultAvailable(Object result)
//							{
//								final IComponentIdentifier cid = (IComponentIdentifier)result;
//								
//								access.scheduleStep(new IComponentStep()
//								{
//									@XMLClassname("proxykill")
//									public Object	execute(IInternalAccess ia)
//									{
//										SServiceProvider.getService(ia.getServiceProvider(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////										ia.getRequiredService("rms")
//											.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//										{
//											public void customResultAvailable(Object result)
//											{
//												IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
//												
//												rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//												{
//													public void customResultAvailable(Object result)
//													{
//														final IComponentManagementService rcms = (IComponentManagementService)result;
//														rcms.destroyComponent(cid);
//														if(sel.getParent()!=null)
//														{
//															((AbstractComponentTreeNode)sel.getParent()).removeChild(sel);
//														}
//														
//														// Hack!!! Result will not be received when remote comp is platform. 
//		//													.addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//		//												{
//		//													public void customResultAvailable(Object source, Object result)
//		//													{
//		//														if(sel.getParent()!=null)
//		//														{
//		//															((AbstractComponentTreeNode)sel.getParent()).removeChild(sel);
//		//														}
//		//													}
//		//												});
//													}
//												});
//											}
//										});
//										return null;
//									}
//								});								
//							}
//						});
//					}
//				}
//			}
//		};
//		
//		suspend	= new AbstractAction("Suspend component", icons.getIcon("suspend_component"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(cms!=null)
//				{
//					TreePath[]	paths	= tree.getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
//						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
//						cms.suspendComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//						{
//							public void customResultAvailable(Object result)
//							{
//								if(sel instanceof VirtualComponentTreeNode)
//								{
//									sel.refresh(false, false);
//								}
//							}
//						});
//					}
//				}
//			}
//		};
//		
//		resume	= new AbstractAction("Resume component", icons.getIcon("resume_component"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(cms!=null)
//				{
//					TreePath[]	paths	= tree.getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
//						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
//						cms.resumeComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//						{
//							public void customResultAvailable(Object result)
//							{
//								if(sel instanceof VirtualComponentTreeNode)
//								{
//									sel.refresh(false, false);
//								}
//							}
//						});
//					}
//				}
//			}
//		};
//		
//		step	= new AbstractAction("Step component", icons.getIcon("step_component"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(cms!=null)
//				{
//					TreePath[]	paths	= tree.getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						final IComponentIdentifier cid = ((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getName();
//
//						final IComponentTreeNode sel = (IComponentTreeNode)paths[i].getLastPathComponent();
//						cms.stepComponent(cid).addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//						{
//							public void customResultAvailable(Object result)
//							{
//								if(sel instanceof VirtualComponentTreeNode)
//								{
//									sel.refresh(false, false);
//								}
//							}
//						});
//					}
//				}
//			}
//		};
//
//		refresh	= new AbstractAction("Refresh", icons.getIcon("refresh"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				TreePath[]	paths	= tree.getSelectionPaths();
//				for(int i=0; paths!=null && i<paths.length; i++)
//				{
//					((IComponentTreeNode)paths[i].getLastPathComponent()).refresh(false, true);
//				}
//			}
//		};
//
//		refreshtree	= new AbstractAction("Refresh subtree", icons.getIcon("refresh_tree"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				TreePath[]	paths	= tree.getSelectionPaths();
//				for(int i=0; paths!=null && i<paths.length; i++)
//				{
//					((IComponentTreeNode)paths[i].getLastPathComponent()).refresh(true, true);
//				}
//			}
//		};
//
//		showprops	= new AbstractAction("Show properties", icons.getIcon("show_properties"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				TreePath	path	= tree.getSelectionPath();
//				if(path!=null && ((IComponentTreeNode)path.getLastPathComponent()).hasProperties())
//				{
//					showProperties(((IComponentTreeNode)path.getLastPathComponent()).getPropertiesComponent());
//				}
//			}
//		};
//		
//		removeservice = new AbstractAction("Remove service", icons.getIcon("show_properties"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				TreePath path = tree.getSelectionPath();
//				if(path!=null)
//				{
//					final ServiceContainerNode scn = (ServiceContainerNode)path.getPathComponent(path.getPathCount()-2);
//					final ServiceNode sn = (ServiceNode)path.getLastPathComponent();
//					scn.getContainer().removeService(sn.getService().getServiceIdentifier()).addResultListener(new SwingDefaultResultListener(proppanel)
//					{
//						public void customResultAvailable(Object result)
//						{
//							scn.removeChild(sn);
//						}
//					});
//				}
//			}
//		};
//		
//		showobject = new AbstractAction("Show object details", icons.getIcon("show_details"))
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				TreePath path = tree.getSelectionPath();
//				if(path!=null)
//				{
//					final IComponentTreeNode node = (IComponentTreeNode)path.getLastPathComponent();
//					if(node instanceof ServiceNode)
//					{
//						Object obj = ((ServiceNode)node).getService();
//						JPanel panel = new ObjectInspectorPanel(obj);
//						showProperties(panel);
//					}
//					else if(node instanceof IActiveComponentTreeNode)
//					{
//						//IComponentDescription desc = ((IActiveComponentTreeNode)node).getDescription();
//						IComponentIdentifier cid = ((IActiveComponentTreeNode)node).getDescription().getName();
//						cms.getExternalAccess(cid).addResultListener(new SwingDefaultResultListener((Component)null)
//						{
//							public void customResultAvailable(Object result)
//							{
//								IExternalAccess	ea	= (IExternalAccess)result;
//								JPanel panel = new ObjectInspectorPanel(ea);
//								showProperties(panel);
//							}
//						});
//					}
//				}
//			}
//		};
//
//		// Default overlays and popups.
//		model.addNodeHandler(new INodeHandler()
//		{
//			public Icon getOverlay(IComponentTreeNode node)
//			{
//				return null;
//			}
//
//			public Action[] getPopupActions(IComponentTreeNode[] nodes)
//			{
//				List ret = new ArrayList();
//				Icon	base	= nodes[0].getIcon();
//				
//				if(nodes.length==1)
//				{
//					if(nodes[0].hasProperties())
//					{
//						Action pshowprops = new AbstractAction((String)showprops.getValue(Action.NAME),
//							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_showprops")}) : (Icon)showprops.getValue(Action.SMALL_ICON))
//						{
//							public void actionPerformed(ActionEvent e)
//							{
//								showprops.actionPerformed(e);
//							}
//						};
//						ret.add(pshowprops);
//					}
//					
//					if(nodes[0] instanceof ServiceNode || nodes[0] instanceof IActiveComponentTreeNode)
//					{
//						Action pshowobject = new AbstractAction((String)showobject.getValue(Action.NAME),
//							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_showobject")}) : (Icon)showprops.getValue(Action.SMALL_ICON))
//						{
//							public void actionPerformed(ActionEvent e)
//							{
//								showobject.actionPerformed(e);
//							}
//						};
//						ret.add(pshowobject);
//					}
//					
//					if(nodes[0] instanceof ServiceNode && !Proxy.isProxyClass(((ServiceNode)nodes[0]).getService().getClass()))
//					{
//						Action premoveservice = new AbstractAction((String)removeservice.getValue(Action.NAME),
//							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_kill")}) : (Icon)showprops.getValue(Action.SMALL_ICON))
//						{
//							public void actionPerformed(ActionEvent e)
//							{
//								removeservice.actionPerformed(e);
//							}
//						};
//						ret.add(premoveservice);
//					}
//				}
//				
//				Action	prefresh	= new AbstractAction((String)refresh.getValue(Action.NAME),
//					base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refresh")}) : (Icon)refresh.getValue(Action.SMALL_ICON))
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						refresh.actionPerformed(e);
//					}
//				};
//				ret.add(prefresh);
//				Action	prefreshtree	= new AbstractAction((String)refreshtree.getValue(Action.NAME),
//					base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refreshtree")}) : (Icon)refreshtree.getValue(Action.SMALL_ICON))
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						refreshtree.actionPerformed(e);
//					}
//				};
//				ret.add(prefreshtree);
//			
//				return (Action[])ret.toArray(new Action[0]);
//			}
//
//			public Action getDefaultAction(final IComponentTreeNode node)
//			{
//				Action	ret	= null;
//				if(node.hasProperties())
//				{
//					ret	= showprops;
//				}
//				return ret;
//			}
//		});
//		
		model.addNodeHandler(new INodeHandler()
		{
			public Icon getOverlay(ITreeNode node)
			{
				Icon	ret	= null;
				return ret;
			}
			
			public Action[] getPopupActions(final ITreeNode[] nodes)
			{
				java.util.List ret = new ArrayList();
				ret.add(ADD_PATH);
				return (Action[])ret.toArray(new Action[ret.size()]);
			}
			
			public Action getDefaultAction(ITreeNode node)
			{
				return null;
			}
		});
//
//		access.scheduleStep(new IComponentStep()
//		{
//			@XMLClassname("init")
//			public Object execute(IInternalAccess ia)
//			{
//				final Future ret = new Future();
//				SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////				ia.getRequiredService("cms")
//					.addResultListener(new DelegationResultListener(ret));
//				return ret;
//			}
//		}).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				cms	= (IComponentManagementService)result;
//				
//				// Hack!!! How to find root node?
//				cms.getComponentDescriptions().addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
//				{
//					public void customResultAvailable(Object result)
//					{
//						IComponentDescription[]	descriptions	= (IComponentDescription[])result;
//						if(descriptions.length!=0)
//						{
//							IComponentDescription	root	= null;
//							for(int i=0; root==null && i<descriptions.length; i++)
//							{
//								if(descriptions[i].getParent()==null)
//								{
//									root	= descriptions[i];
//								}
//							}
//							if(root==null)
//								throw new RuntimeException("No root node found: "+SUtil.arrayToString(descriptions));
//							model.setRoot(new ComponentTreeNode(null, model, tree, root, cms, cic));
//							// Expand root node.
//							TreeExpansionHandler	teh	= new TreeExpansionHandler(tree);
//							teh.treeExpanded(new TreeExpansionEvent(tree, new TreePath(model.getRoot())));
//						}
//					}
//				});
//				
//				cms.addComponentListener(null, listener);		
//			}
//		});
//		
//		// Remove selection in tree, when user clicks in background.
//		tree.addMouseListener(new MouseAdapter()
//		{
//			public void mouseClicked(MouseEvent e)
//			{
//				if(tree.getPathForLocation(e.getX(), e.getY())==null)
//				{
//					tree.clearSelection();
//				}
//			}
//		});
	}
	
	//-------- methods --------
	
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
	 *  Dispose the tree.
	 *  Should be called to remove listeners etc.
	 */
	public void	dispose()
	{
//		access.scheduleStep(new IComponentStep()
//		{
//			@XMLClassname("dispose")
//			public Object execute(IInternalAccess ia)
//			{
//				SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
////				ia.getRequiredService("cms")
//					.addResultListener(new SwingDefaultResultListener()
//				{
//					public void customResultAvailable(Object result)
//					{
//						cms	= (IComponentManagementService)result;
//						cms.removeComponentListener(null, listener);				
//					}
//					public void customExceptionOccurred(Exception exception)
//					{
//						// ignore
//					}
//				});
//				return null;
//			}
//		});
		
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
	
	/**
	 *  Add a new path to the explorer.
	 */
	public final Action ADD_PATH = new ToolTipAction("Add Path", icons.getIcon("addpath"),
		"Add a new directory path (package root) to the project structure")
	{
		/**
		 *  Called when action should be performed.
		 *  @param e The event.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(filechooser.showDialog(SGUI.getWindowParent(ModelTreePanel.this)
				, "Add Path")==JFileChooser.APPROVE_OPTION)
			{
				File file = filechooser.getSelectedFile();
				if(file!=null)
				{
					// Handle common user error of double clicking the directory to add.
					if(!file.exists() && file.getParentFile().exists() && file.getParentFile().getName().equals(file.getName()))
						file	= file.getParentFile();
					if(file.exists())
					{
						// Add file/directory to tree.
//						ITreeNode	node	= getModel().getRoot().addPathEntry(file);
						
						// todo: jars
						final File fcopy = file;
//						SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
//						{
//							public void resultAvailable(Object result)
//							{
//								ILibraryService ls = (ILibraryService)result;
//								File f = new File(fcopy.getParentFile(), fcopy.getName());
//								try
//								{
//									ls.addURL(f.toURI().toURL());
//								}
//								catch(MalformedURLException ex)
//								{
//									ex.printStackTrace();
//								}
//							}
//						});
						
						final RootNode root = (RootNode)getModel().getRoot();
						ModelTreePanel.createNode(root, model, tree, file).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								root.addChild((ITreeNode)result);
							}
						});
						
					}
					else
					{
						String	msg	= SUtil.wrapText("Cannot find file or directory:\n"+file);
//						JOptionPane.showMessageDialog(SGUI.getWindowParent(ModelExplorer.this),
//							msg, "Cannot find file or directory", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
//			ITreeNode rm = (ITreeNode)getLastSelectedPathComponent();
//			return rm==null;
			return true;
		}
	};

//	/**
//	 *  Remove an existing path from the explorer.
//	 */
//	public final Action REMOVE_PATH = new ToolTipAction("Remove Path", icons.getIcon("removepath"),
//		"Remove a directory path to the project structure")
//	{
//		/**
//		 *  Called when action should be performed.
//		 *  @param e The event.
//		 */
//		public void actionPerformed(ActionEvent e)
//		{
//			if(isEnabled())
//			{
//				final FileNode	node = (FileNode)getLastSelectedPathComponent();
//				final int index	= ((ModelExplorerTreeModel)getModel()).getIndexOfChild(root, node);
//				getRootNode().removePathEntry(node);
//				
//				// todo: jars
//				SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(ModelExplorer.this)
//				{
//					public void customResultAvailable(Object result)
//					{
//						ILibraryService ls = (ILibraryService)result;
//						File file = node.getFile();
//						file = new File(file.getParentFile(), file.getName());
//						try
//						{
//							ls.removeURL(file.toURI().toURL());
//						}
//						catch(MalformedURLException ex)
//						{
//							ex.printStackTrace();
//						}
//						
//						resetCrawler();
//
//						((ModelExplorerTreeModel)getModel()).fireNodeRemoved(getRootNode(), node, index);
//			
//					}
//				});
//			}
//		}
//
//		/**
//		 *  Test if action is available in current context.
//		 *  @return True, if available.
//		 */
//		public boolean isEnabled()
//		{
//			ITreeNode rm = (ITreeNode)getLastSelectedPathComponent();
//			return rm!=null && rm.getParent()==getRootNode();
//		}
//	};
//
//	/**
//	 *  Refresh the selected path.
//	 */
//	public final Action REFRESH = new ToolTipAction("Refresh [F5]", icons.getIcon("refresh"), null)
//	{
//		/**
//		 *  Called when action should be performed.
//		 *  @param e The event.
//		 */
//		public void actionPerformed(ActionEvent e)
//		{
//			ITreeNode	node	= (ITreeNode)getLastSelectedPathComponent();
//			refreshAll(node!=null? node: getRootNode());
//		}
//	};
	
	/**
	 *  Create a new component node.
	 */
	public static IFuture createNode(ITreeNode parent, AsyncTreeModel model, JTree tree, Object node)
	{
		final Future ret = new Future();
		
		if(node instanceof File)
		{
			File file = (File)node;
			if(((File)node).isDirectory())
			{
				ret.setResult(new DirNode(parent, model, tree, file));
			}
			else
			{
				ret.setResult(new FileNode(parent, model, tree, file));
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame f =  new JFrame();
		ModelTreePanel mtp = new ModelTreePanel(null);
		mtp.getModel().setRoot(new RootNode(mtp.getModel(), mtp.getTree()));
		f.add(mtp, BorderLayout.CENTER);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}
