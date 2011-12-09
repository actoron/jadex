package jadex.base.gui.componentviewer;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.INodeListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.componenttree.ProvidedServiceInfoNode;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Properties;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.SGUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;


/**
 *  The service viewer allows to introspect details of services.
 */
public class ComponentViewerPlugin extends AbstractJCCPlugin
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"componentviewer", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/configure.png"), 
		"componentviewer_sel", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/configure_sel.png"), 
		"open_viewer", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/new_introspector.png"),
		"close_viewer", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/close_introspector.png"),
		"viewer_empty", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/viewer_empty.png"),
		"overlay_viewable", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/overlay_edit.png"),
		"overlay_viewed", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/overlay_introspected.png"),
		"overlay_notviewed", SGUI.makeIcon(ComponentViewerPlugin.class, "/jadex/base/gui/images/overlay_notintrospected.png")
	});
	
	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The agent tree table. */
	protected ComponentTreePanel	comptree;

	/** The detail panel. */
	protected JPanel	detail;

	/** The detail layout. */
	protected ObjectCardLayout	cards;
	
	/** The service viewer panels. */
	protected Map	panels;
	
	/** Loaded properties. */
	protected Properties	props;
	
	/** The active component node viewable state. */
	protected Map viewables;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plugin.
	 */
	public ComponentViewerPlugin()
	{
		this.panels	= new HashMap();
		this.viewables = Collections.synchronizedMap(new HashMap());
	}
	
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  @return The plugin name 
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Component Viewer";
	}

	/**
	 *  @return The icon of plugin
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("componentviewer_sel"): icons.getIcon("componentviewer");
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JButton b1 = new JButton(START_VIEWER);
		b1.setBorder(null);
		b1.setToolTipText(b1.getText());
		b1.setText(null);
		b1.setEnabled(true);

		JButton b2 = new JButton(STOP_VIEWER);
		b2.setBorder(null);
		b2.setToolTipText(b2.getText());
		b2.setText(null);
		b2.setEnabled(true);
		
		return new JComponent[]{b1, b2};
	}
		
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		this.split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		split.setOneTouchExpandable(true);

		comptree = new ComponentTreePanel(getJCC().getPlatformAccess(), getJCC().getCMSHandler());
		comptree.setMinimumSize(new Dimension(0, 0));
		split.add(comptree);

		comptree.getTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				JTree tree = comptree.getTree();
				if(tree.getSelectionPath()!=null)
				{
					ITreeNode node = (ITreeNode)tree.getSelectionPath().getLastPathComponent();
					Object nodeid = node.getId();
					if(nodeid!=null)
					{
						if(cards.getComponent(nodeid)!=null)
						{
							storeCurrentPanelSettings();
							IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(nodeid);
							panel.setProperties(props!=null ? props.getSubproperty(panel.getId()) : null);
							cards.show(nodeid);
						}
					}
				}
			}
		});
		
		comptree.addNodeHandler(new ShowRemoteControlCenterHandler(getJCC(), getView()));
		
		comptree.addNodeHandler(new INodeHandler()
		{
			public Action[] getPopupActions(ITreeNode[] nodes)
			{
				Action[]	ret	= null;
				
				boolean	allviewable	= true;
				for(int i=0; allviewable && i<nodes.length; i++)
				{
					allviewable	= isNodeViewable(nodes[i]);
				}
				
				if(allviewable)
				{
					boolean	allob	= true;
					for(int i=0; allob && i<nodes.length; i++)
					{
						allob	= cards.getComponent(nodes[i].getId())!=null;
					}
					boolean	allig	= true;
					for(int i=0; allig && i<nodes.length; i++)
					{
						allig	= cards.getComponent(nodes[i].getId())==null;
					}
					
					// Todo: Large icons for popup actions?
					if(allig)
					{
						Icon	base	= nodes[0].getIcon();
						Action	a	= new AbstractAction((String)START_VIEWER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_viewed")}) : (Icon)START_VIEWER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								START_VIEWER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
					else if(allob)
					{
						Icon	base	= nodes[0].getIcon();
						Action	a	= new AbstractAction((String)STOP_VIEWER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_notviewed")}) : (Icon)STOP_VIEWER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								STOP_VIEWER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
				}
				
				return ret;
			}
			
			public Icon getOverlay(ITreeNode node)
			{
				Icon ret	= null;
				if(cards.getComponent(node.getId())!=null)
				{
					ret = icons.getIcon("overlay_viewed");
				}
				else if(isNodeViewable(node))
				{
					ret = icons.getIcon("overlay_viewable");					
				}
				return ret;
			}
			
			public Action getDefaultAction(ITreeNode node)
			{
				Action	a	= null;
				if(cards.getComponent(node.getId())!=null)
				{
					a	= STOP_VIEWER;
				}
				else if(isNodeViewable(node))
				{
					a	= START_VIEWER;
				}
				return a;
			}
		});

		JLabel	emptylabel	= new JLabel("Select vieweable components or services to activate the viewer",
			icons.getIcon("viewer_empty"), JLabel.CENTER);
		emptylabel.setVerticalAlignment(JLabel.CENTER);
		emptylabel.setHorizontalTextPosition(JLabel.CENTER);
		emptylabel.setVerticalTextPosition(JLabel.BOTTOM);
		emptylabel.setFont(emptylabel.getFont().deriveFont(emptylabel.getFont().getSize()*1.3f));

		cards = new ObjectCardLayout();
		detail = new JPanel(cards);
		detail.setMinimumSize(new Dimension(0, 0));
		detail.add(ObjectCardLayout.DEFAULT_COMPONENT, emptylabel);
		split.add(detail);
		//split.setResizeWeight(1.0);
		
		// todo:
//		SHelp.setupHelp(split, getHelpID());

		split.setDividerLocation(150);
		
		// Listener to remove panels, when services vanish!
		comptree.getModel().addNodeListener(new INodeListener()
		{
			public void nodeRemoved(ITreeNode node)
			{
//				System.out.println("node rem: "+node);
				Object nodeid = node.getId();
				if(panels.containsKey(nodeid))
				{
					storeCurrentPanelSettings();
//					System.out.println("removeing: "+nodeid+" "+cards.getComponent(nodeid));
					detail.remove(cards.getComponent(nodeid));
					IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.remove(nodeid);
					panel.shutdown();
					comptree.getModel().fireNodeChanged(node);
				}
			}
			
			public void nodeAdded(ITreeNode node)
			{
			}
		});

		return split;
	}
		
	final AbstractAction START_VIEWER = new AbstractAction("Open service viewer", icons.getIcon("open_viewer"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(isNodeViewable((ITreeNode)paths[i].getLastPathComponent()))
				{
					final Object tmp = paths[i].getLastPathComponent();
					
					if(tmp instanceof ProvidedServiceInfoNode)
					{
						final ProvidedServiceInfoNode node = (ProvidedServiceInfoNode)tmp;
//						final IService service = node.getService();
						
						SServiceProvider.getService(getJCC().getJCCAccess().getServiceProvider(), node.getServiceIdentifier())
							.addResultListener(new SwingDefaultResultListener(comptree)
						{
							public void customResultAvailable(Object result)
							{
								final IService service = (IService)result;

								AbstractJCCPlugin.getClassLoader(((IActiveComponentTreeNode)node.getParent().getParent()).getComponentIdentifier(), getJCC())
									.addResultListener(new SwingDefaultResultListener(comptree)
								{
									public void customResultAvailable(Object result)
									{
										ClassLoader	cl	= (ClassLoader)result;
										
										final Object clid = service.getPropertyMap()!=null? service.getPropertyMap().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS) : null;
										final Class clazz = clid instanceof Class? (Class)clid: clid instanceof String? SReflect.classForName0((String)clid, cl): null;
										
										if(clid!=null)
										{
											try
											{
												storeCurrentPanelSettings();
												final IServiceViewerPanel	panel = (IServiceViewerPanel)clazz.newInstance();
												panel.init(getJCC(), service).addResultListener(new SwingDefaultResultListener(comptree)
												{
													public void customResultAvailable(Object result)
													{
														Properties	sub	= props!=null ? props.getSubproperty(panel.getId()) : null;
														if(sub!=null)
															panel.setProperties(sub);
														JComponent comp = panel.getComponent();
														
														// todo: help 
														//SHelp.setupHelp(comp, getHelpID());
														panels.put(node.getServiceIdentifier(), panel);
														detail.add(comp, node.getServiceIdentifier());
														comptree.getModel().fireNodeChanged(node);
													}
												});
											}
											catch(Exception e)
											{
												e.printStackTrace();
												getJCC().displayError("Error initializing service viewer panel.", "Component viewer panel class: "+clid, e);
											}
										}
									}
								});
							}
						});
					}
					else if(tmp instanceof IActiveComponentTreeNode)
					{
						final IActiveComponentTreeNode node = (IActiveComponentTreeNode)tmp;
						final IComponentIdentifier cid = node.getComponentIdentifier();
						
						SServiceProvider.getServiceUpwards(getJCC().getJCCAccess().getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new SwingDefaultResultListener(comptree)
						{
							public void customResultAvailable(Object result)
							{
								final IComponentManagementService cms = (IComponentManagementService)result;
								cms.getExternalAccess(cid).addResultListener(new SwingDefaultResultListener(comptree)
								{
									public void customResultAvailable(Object result)
									{
										final IExternalAccess exta = (IExternalAccess)result;
										
										AbstractJCCPlugin.getClassLoader(cid, getJCC())
											.addResultListener(new SwingDefaultResultListener<ClassLoader>(comptree)
										{
											public void customResultAvailable(ClassLoader cl)
											{
												final Object clid = exta.getModel().getProperty(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, cl);
												
												if(clid instanceof String)
												{
													Class clazz	= SReflect.classForName0((String)clid, cl);
													createPanel(clazz, exta, node);
												}
												else if(clid instanceof Class)
												{
													createPanel((Class)clid, exta, node);
												}
											}
										});
									}
								});
							}
						});
					}
				}
			}
		}
	};
	
	/**
	 * 
	 */
	protected void createPanel(Class clazz, final IExternalAccess exta, final IActiveComponentTreeNode node)
	{
		try
		{
			storeCurrentPanelSettings();
			final IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
			panel.init(getJCC(), exta).addResultListener(new SwingDefaultResultListener(comptree)
			{
				public void customResultAvailable(Object result)
				{
					Properties	sub	= props!=null ? props.getSubproperty(panel.getId()) : null;
					if(sub!=null)
						panel.setProperties(sub);
					JComponent comp = panel.getComponent();
					// todo: help
					//SHelp.setupHelp(comp, getHelpID());
					panels.put(exta.getComponentIdentifier(), panel);
					detail.add(comp, exta.getComponentIdentifier());
					comptree.getModel().fireNodeChanged(node);
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			getJCC().displayError("Error initializing component viewer panel.", "Component viewer panel class: "+clazz, e);
		}
	}

	final AbstractAction STOP_VIEWER = new AbstractAction("Close service viewer", icons.getIcon("close_viewer"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(isNodeViewable((ITreeNode)paths[i].getLastPathComponent()))
				{
					storeCurrentPanelSettings();
					final ITreeNode node = (ITreeNode)paths[i].getLastPathComponent();
					Object nodeid = node.getId();
					detail.remove(cards.getComponent(nodeid));
					IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.remove(nodeid);
					panel.shutdown().addResultListener(new SwingDefaultResultListener(comptree)
					{
						public void customResultAvailable(Object result)
						{
							comptree.getModel().fireNodeChanged(node);
						}
					});
				}
			}
		}
	};

	/**
	 *  Test if a node is viewable.
	 *  @param node	The node.
	 *  @return True, if the node is viewable.
	 */
	protected boolean isNodeViewable(final ITreeNode node)
	{
//		System.out.println("called isVis: "+node.getId());
		boolean ret = false;
		if(node instanceof ProvidedServiceInfoNode)
		{
			final IServiceIdentifier sid = ((ProvidedServiceInfoNode)node).getServiceIdentifier();
			if(sid!=null)
			{
				Boolean viewable = (Boolean)viewables.get(sid);
				if(viewable!=null)
				{
					ret = viewable.booleanValue();
//					System.out.println("isVis result: "+node.getId()+" "+ret);
				}
				else
				{
					// Unknown -> start search to find out asynchronously
					SServiceProvider.getService(getJCC().getJCCAccess().getServiceProvider(), sid)
						.addResultListener(new SwingDefaultResultListener(comptree)
					{
						public void customResultAvailable(Object result)
						{
							IService service = (IService)result;
							Map	props = service.getPropertyMap();
							boolean vis = props!=null && props.get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS)!=null;
							viewables.put(sid, vis? Boolean.TRUE: Boolean.FALSE);
//							System.out.println("isVis first res: "+viewables.get(sid));
							node.refresh(false);
						}
					});
				}
			}
//			Map	props	= ((ProvidedServiceInfoNode)node).getServiceIdentifier().get.getPropertyMap();
//			ret = props!=null && props.get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS)!=null;
		}
		else if(node instanceof IActiveComponentTreeNode)
		{
			final IComponentIdentifier cid = ((IActiveComponentTreeNode)node).getComponentIdentifier();
			
			// For proxy components the cid could be null if the remote cid has not yet been retrieved
			// Using a IFuture as return value in not very helpful because this method can't directly
			// return a result, even if known.
			// todo: how to initiate a repaint in case the the cid is null
			if(cid!=null)
			{
				Boolean viewable = (Boolean)viewables.get(cid);
				if(viewable!=null)
				{
					ret = viewable.booleanValue();
//					System.out.println("isVis result: "+node.getId()+" "+ret);
				}
				else
				{
					// Unknown -> start search to find out asynchronously
					SServiceProvider.getServiceUpwards(getJCC().getJCCAccess().getServiceProvider(), IComponentManagementService.class)
						.addResultListener(new SwingDefaultResultListener(comptree)
					{
						public void customResultAvailable(Object result)
						{
							final IComponentManagementService cms = (IComponentManagementService)result;
							
							cms.getExternalAccess(cid).addResultListener(new SwingDefaultResultListener(comptree)
							{
								public void customResultAvailable(Object result)
								{
									final IExternalAccess exta = (IExternalAccess)result;
									getJCC().getClassLoader(exta.getModel().getResourceIdentifier())
										.addResultListener(new SwingDefaultResultListener<ClassLoader>(comptree)
									{
										public void customResultAvailable(ClassLoader cl)
										{
											final Object clid = exta.getModel().getProperty(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, cl);
											viewables.put(cid, clid==null? Boolean.FALSE: Boolean.TRUE);
//											System.out.println("isVis first res: "+viewables.get(cid));
											node.refresh(false);
										}										
									});
								}
								
								public void customExceptionOccurred(Exception exception)
								{
									// Happens e.g. when remote classes not locally available or platform is shutting down.
//									exception.printStackTrace();
								}
							});
						}
					});
				}
			}
		}
		return ret;
	}
	
	/** 
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		final Future<Void> ret = new Future<Void>();
		comptree.dispose();
		CounterResultListener<Void> lis = new CounterResultListener<Void>(panels.size(), 
			true, new SwingDelegationResultListener<Void>(ret));
		for(Iterator it=panels.values().iterator(); it.hasNext(); )
		{
			((IAbstractViewerPanel)it.next()).shutdown().addResultListener(lis);
		}
		return ret;
	}
	
	//-------- loading / saving --------
	
	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture<Properties> getProperties()
	{
		storeCurrentPanelSettings();
		
		return new Future<Properties>(props);
	}
	
	/**
	 *  Set properties loaded from project.
	 */
	public IFuture<Void> setProperties(Properties ps)
	{
		Future<Void> ret = new Future<Void>();
		this.props	=	ps;
		
		IAbstractViewerPanel[] pans = (IAbstractViewerPanel[])panels.values().toArray(new IAbstractViewerPanel[0]);
		CounterResultListener<Void> lis = new CounterResultListener<Void>(pans.length, true, new SwingDelegationResultListener(ret));
		
		for(int i=0; i<pans.length; i++)
		{
			Properties	sub	= props!=null? props.getSubproperty(pans[i].getId()): null;
			pans[i].setProperties(sub).addResultListener(lis);
		}
		return ret;
	}

	
	/**
	 *  Store settings of current panel.
	 */
	protected IFuture<Properties> storeCurrentPanelSettings()
	{
		final Future<Properties> ret = new Future<Properties>();
		
		Object	old	= cards.getCurrentKey();
		if(old!=null)
		{
			final IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(old);
			if(panel!=null)
			{
				if(props==null)
					props	= new Properties();
				panel.getProperties().addResultListener(new SwingDelegationResultListener<Properties>(ret)
				{
					public void customResultAvailable(Properties sub) 
					{
//						Properties sub = (Properties)result;
						props.removeSubproperties(panel.getId());
						if(sub!=null)
						{
							sub.setType(panel.getId());
							props.addSubproperties(sub);
						}
						ret.setResult(props);
					};
				});
			}
		}
		
		return ret;
	}
}
