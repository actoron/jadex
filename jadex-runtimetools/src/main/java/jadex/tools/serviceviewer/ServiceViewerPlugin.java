package jadex.tools.serviceviewer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IService;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;
import jadex.tools.common.CombiIcon;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.ObjectCardLayout;
import jadex.tools.common.componenttree.ComponentTreePanel;
import jadex.tools.common.componenttree.IActiveComponentTreeNode;
import jadex.tools.common.componenttree.IComponentTreeNode;
import jadex.tools.common.componenttree.INodeHandler;
import jadex.tools.common.componenttree.INodeListener;
import jadex.tools.common.componenttree.ServiceNode;
import jadex.tools.common.plugin.AbstractJCCPlugin;

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
public class ServiceViewerPlugin extends AbstractJCCPlugin
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"serviceviewer", SGUI.makeIcon(ServiceViewerPlugin.class, "/jadex/tools/common/images/configure.png"), 
		"serviceviewer_sel", SGUI.makeIcon(ServiceViewerPlugin.class, "/jadex/tools/common/images/configure.png"), 
		"open_viewer", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"close_viewer", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/close_introspector.png"),
		"viewer_empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/introspector_empty.png"),
		"overlay_viewable", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_edit.png"),
		"overlay_viewed", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_introspected.png"),
		"overlay_notviewed", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_notintrospected.png")
	});
	
	/** The property for the viewer panel class. */
	public static final String	PROPERTY_VIEWERCLASS	= "serviceviewer.viewerclass";

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
	public ServiceViewerPlugin()
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
		return "Service Viewer";
	}

	/**
	 *  @return The icon of plugin
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("serviceviewer_sel"): icons.getIcon("serviceviewer");
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

		comptree = new ComponentTreePanel(getJCC().getServiceContainer());
		comptree.setMinimumSize(new Dimension(0, 0));
		split.add(comptree);

		comptree.getTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				JTree tree = comptree.getTree();
				if(tree.getSelectionPath()!=null)
				{
					Object node = tree.getSelectionPath().getLastPathComponent();
					Object nodeid = getNodeId(node);
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
		
		comptree.addNodeHandler(new INodeHandler()
		{
			public Action[] getPopupActions(IComponentTreeNode[] nodes)
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
						allob	= cards.getComponent(getNodeId(nodes[i]))!=null;
					}
					boolean	allig	= true;
					for(int i=0; allig && i<nodes.length; i++)
					{
						allig	= cards.getComponent(getNodeId(nodes[i]))==null;
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
			
			public Icon getOverlay(IComponentTreeNode node)
			{
				Icon ret	= null;
				if(node instanceof ServiceNode && cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier())!=null)
				{
					ret = icons.getIcon("overlay_viewed");
				}
				else if(isNodeViewable(node))
				{
					ret = icons.getIcon("overlay_viewable");					
				}
				return ret;
			}
			
			public Action getDefaultAction(IComponentTreeNode node)
			{
				Action	a	= null;
				if(node instanceof ServiceNode && cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier())!=null)
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

		JLabel	emptylabel	= new JLabel("Select services to activate the service viewer",
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
		
		GuiProperties.setupHelp(split, getHelpID());

		split.setDividerLocation(150);
		
		// Listener to remove panels, when services vanish!
		comptree.getModel().addNodeListener(new INodeListener()
		{
			public void nodeRemoved(IComponentTreeNode node)
			{
				if(node instanceof ServiceNode && cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier())!=null)
				{
					detail.remove(cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier()));
				}
			}
			
			public void nodeAdded(IComponentTreeNode node)
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
				if(isNodeViewable((IComponentTreeNode)paths[i].getLastPathComponent()))
				{
					final Object tmp = paths[i].getLastPathComponent();
					
					if(tmp instanceof ServiceNode)
					{
						final ServiceNode node = (ServiceNode)tmp;
						final IService service = node.getService();
						final String classname = (String)service.getPropertyMap().get(PROPERTY_VIEWERCLASS);
						
						if(classname!=null)
						{
							SServiceProvider.getService(getJCC().getServiceContainer(), ILibraryService.class)
								.addResultListener(new SwingDefaultResultListener(comptree)
							{
								public void customResultAvailable(Object source, Object result)
								{
									ILibraryService	libservice	= (ILibraryService)result;
									try
									{
										storeCurrentPanelSettings();
										Class clazz	= SReflect.classForName(classname, libservice.getClassLoader());
										IServiceViewerPanel	panel = (IServiceViewerPanel)clazz.newInstance();
										panel.init(getJCC(), service);
										Properties	sub	= props!=null ? props.getSubproperty(panel.getId()) : null;
										panel.setProperties(sub);
										GuiProperties.setupHelp(panel.getComponent(), getHelpID());
										panels.put(service.getServiceIdentifier(), panel);
										detail.add(panel.getComponent(), service.getServiceIdentifier());
										comptree.getModel().fireNodeChanged(node);
									}
									catch(Exception e)
									{
										e.printStackTrace();
										getJCC().displayError("Error initializing service viewer panel.", "Service viewer panel class: "+classname, e);
									}
								}
							});
						}
					}
					else if(tmp instanceof IActiveComponentTreeNode)
					{
						final IActiveComponentTreeNode node = (IActiveComponentTreeNode)tmp;
						final IComponentIdentifier cid = node.getDescription().getName();
						
						SServiceProvider.getService(getJCC().getServiceContainer(), IComponentManagementService.class)
							.addResultListener(new SwingDefaultResultListener(comptree)
						{
							public void customResultAvailable(Object source, Object result)
							{
								final IComponentManagementService cms = (IComponentManagementService)result;
								
								cms.getExternalAccess(cid).addResultListener(new SwingDefaultResultListener(comptree)
								{
									public void customResultAvailable(Object source, Object result)
									{
										final IExternalAccess exta = (IExternalAccess)result;
										final String classname = (String)exta.getModel().getProperties().get(PROPERTY_VIEWERCLASS);
									
//										System.out.println("classname for comp: "+classname);
										
										if(classname!=null)
										{
											SServiceProvider.getService(getJCC().getServiceContainer(), ILibraryService.class)
												.addResultListener(new SwingDefaultResultListener(comptree)
											{
												public void customResultAvailable(Object source, Object result)
												{
													ILibraryService	libservice	= (ILibraryService)result;
													try
													{
														storeCurrentPanelSettings();
														Class clazz	= SReflect.classForName(classname, libservice.getClassLoader());
														IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
														panel.init(getJCC(), exta);
														Properties	sub	= props!=null ? props.getSubproperty(panel.getId()) : null;
														panel.setProperties(sub);
														GuiProperties.setupHelp(panel.getComponent(), getHelpID());
														panels.put(exta.getComponentIdentifier(), panel);
														detail.add(panel.getComponent(), exta.getComponentIdentifier());
														comptree.getModel().fireNodeChanged(node);
													}
													catch(Exception e)
													{
														e.printStackTrace();
														getJCC().displayError("Error initializing service viewer panel.", "Service viewer panel class: "+classname, e);
													}
												}
											});
										}
									}
								});
							}
						});
					}
				}
			}
		}
	};

	final AbstractAction STOP_VIEWER = new AbstractAction("Close service viewer", icons.getIcon("close_viewer"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(isNodeViewable((IComponentTreeNode)paths[i].getLastPathComponent()))
				{
					storeCurrentPanelSettings();
					ServiceNode node = (ServiceNode)paths[i].getLastPathComponent();
					Object nodeid = getNodeId(node);
					detail.remove(cards.getComponent(nodeid));
					IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.remove(nodeid);
					panel.shutdown();
					comptree.getModel().fireNodeChanged(node);
				}
			}
		}
	};

	/**
	 * @return the help id of the perspective
	 * @see jadex.tools.common.plugin.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.serviceviewer";
	}

	/**
	 *  Test if a node is viewable.
	 *  @param node	The node.
	 *  @return True, if the node is viewable.
	 */
	protected boolean isNodeViewable(final IComponentTreeNode node)
	{
		boolean ret = false;
		if(node instanceof ServiceNode)
		{
			ret = ((ServiceNode)node).getService().getPropertyMap().get(PROPERTY_VIEWERCLASS)!=null;
		}
		else if(node instanceof IActiveComponentTreeNode)
		{
			final IComponentIdentifier cid = ((IActiveComponentTreeNode)node).getDescription().getName();

			Boolean viewable = (Boolean)viewables.get(cid);
			if(viewable!=null)
			{
				ret = viewable.booleanValue();
			}
			else
			{
				// Unknown -> start search to find out synchronously
				SServiceProvider.getService(getJCC().getServiceContainer(), IComponentManagementService.class)
					.addResultListener(new SwingDefaultResultListener(comptree)
				{
					public void customResultAvailable(Object source, Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService)result;
						
						cms.getExternalAccess(cid).addResultListener(new SwingDefaultResultListener(comptree)
						{
							public void customResultAvailable(Object source, Object result)
							{
								final IExternalAccess exta = (IExternalAccess)result;
								final String classname = (String)exta.getModel().getProperties().get(PROPERTY_VIEWERCLASS);
								viewables.put(cid, classname==null? Boolean.FALSE: Boolean.TRUE);
//								System.out.println("node: "+viewables.get(cid));
								node.refresh(false);
							}
						});
					}
				});
			}
		}
		return ret;
	}
	
	//-------- loading / saving --------
	
	/**
	 *  Return properties to be saved in project.
	 */
	public Properties getProperties()
	{
		storeCurrentPanelSettings();
		
		return props;
	}
	
	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties ps)
	{
		this.props	=	ps;
		for(Iterator it=panels.values().iterator(); it.hasNext(); )
		{
			IAbstractViewerPanel	panel	= (IAbstractViewerPanel)it.next();
			Properties	sub	= props!=null ? props.getSubproperty(panel.getId()) : null;
			panel.setProperties(sub);
		}
	}

	
	/**
	 *  Store settings of current panel.
	 */
	protected void storeCurrentPanelSettings()
	{
		Object	old	= cards.getCurrentKey();
		if(old!=null)
		{
			IAbstractViewerPanel	panel	= (IAbstractViewerPanel)panels.get(old);
			if(panel!=null)
			{
				if(props==null)
					props	= new Properties();
				Properties	sub	= panel.getProperties();
				props.removeSubproperties(panel.getId());
				if(sub!=null)
				{
					sub.setType(panel.getId());
					props.addSubproperties(sub);
				}
			}
		}
	}
	
	/**
	 *  Get the node id.
	 */
	public Object getNodeId(Object node)
	{
		Object ret = null;
		
		if(node instanceof ServiceNode)
		{
			ret = ((ServiceNode)node).getService().getServiceIdentifier();
		}
		else if(node instanceof IActiveComponentTreeNode)
		{
			ret = ((IActiveComponentTreeNode)node).getDescription().getName();
		}
		
		return ret;
	}
}
