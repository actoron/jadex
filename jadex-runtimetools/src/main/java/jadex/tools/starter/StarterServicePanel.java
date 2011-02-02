package jadex.tools.starter;

import jadex.base.SComponentFactory;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.modeltree.FileNode;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.base.gui.modeltree.RemoteFileNode;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.service.IService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * The starter gui allows for starting components platform independently.
 */
public class StarterServicePanel extends JPanel implements ICMSComponentListener
{
	//-------- static part --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"add_remote_component", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/add_remote_component.png"),
		"kill_platform", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_killplatform.png"),
		"starter", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_starter.png"),
		"starter_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_starter_sel.png"),
		"start_component",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/start.png"),
		"checking_menu",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_broken.png")
	});

	//-------- attributes --------

	/** The starter panel. */
	protected StarterPanel spanel;

	/** The panel showing the classpath models. */
//	protected ModelExplorer mpanel;
	protected ModelTreePanel mpanel;
	
	/** The component instances in a tree. */
	protected ComponentTreePanel comptree;
	
	/** A split panel. */
	protected JSplitPane lsplit;

	/** A split panel. */
    protected JSplitPane csplit;
	
    /** The jcc. */
    protected IControlCenter jcc;
    
    /** The service. */
    protected IComponentManagementService cms;
    
    /** The component. */
    protected IExternalAccess exta;
    
	//-------- constructors --------

	/**
	 * Open the GUI.
	 * @param starter The starter.
	 */
	public StarterServicePanel(final IControlCenter jcc, IComponentManagementService cms)
	{
		super(new BorderLayout());
		this.jcc = jcc;
		this.cms = (IComponentManagementService)cms;
	}
	
	/**
	 * 
	 */
	public IFuture init()
	{
		final Future ret = new Future();
		
		getComponentForService(jcc.getExternalAccess().getServiceProvider(), cms)
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{				
				exta = (IExternalAccess)result;
				
				csplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
				csplit.setOneTouchExpandable(true);
		
				lsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
				lsplit.setOneTouchExpandable(true);
				lsplit.setResizeWeight(0.7);
		
	//			mpanel = new ModelExplorer(jcc.getExternalAccess(), new StarterNodeFunctionality(jcc));
				mpanel = new ModelTreePanel(exta, !SUtil.equals(exta.getComponentIdentifier().getPlatformName(), jcc.getComponentIdentifier().getPlatformName()));
		//		mpanel.setAction(FileNode.class, new INodeAction()
		//		{
		//			public void validStateChanged(TreeNode node, boolean valid)
		//			{
		//				String file1 = ((FileNode)node).getFile().getAbsolutePath();
		//				String file2 = spanel.getFilename();
		//				//System.out.println(file1+" "+file2);
		//				if(file1!=null && file1.equals(file2))
		//				{
		//					spanel.reloadModel(file1);
		//				}
		//			}
		//		});
	//			mpanel.setPopupBuilder(new PopupBuilder(new Object[]{new StartComponentMenuItemConstructor(), mpanel.ADD_PATH,
	//				mpanel.REMOVE_PATH, mpanel.REFRESH}));
				mpanel.getTree().addTreeSelectionListener(new TreeSelectionListener()
				{
					public void valueChanged(TreeSelectionEvent e)
					{
						Object	node = mpanel.getTree().getLastSelectedPathComponent();
						String filename = null;
						if(node instanceof FileNode)
						{
							mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							filename = ((FileNode)node).getFile().getAbsolutePath();
						}
						else if(node instanceof RemoteFileNode)
						{
							mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							filename = ((RemoteFileNode)node).getRemoteFile().getPath();
						}
						if(filename!=null)
						{
							// Models have to be loaded with absolute path.
							// An example to facilitate understanding:
							// root
							//  +-classes1
							//  |  +- MyComponent.component.xml
							//  +-classes2
							//  |  +- MyComponent.component.xml
		
		//					if(getJCC().getComponent().getPlatform().getComponentFactory().isLoadable(model))
							final String ffilename = filename;
							SComponentFactory.isLoadable(exta, filename).addResultListener(new SwingDefaultResultListener(spanel)
							{
								public void customResultAvailable(Object result)
								{
									if(((Boolean)result).booleanValue())
										loadModel(ffilename);
									mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							});
		//					else if(getJCC().getComponent().getPlatform().getApplicationFactory().isLoadable(model))
		//					{
		//						loadModel(model);
		//					}
						}
					}
				});
				
				MouseListener ml = new MouseAdapter()
				{
					public void mousePressed(MouseEvent e)
					{
						int row = mpanel.getTree().getRowForLocation(e.getX(), e.getY());
						if(row != -1)
						{
							if(e.getClickCount() == 2)
							{
								Object	node = mpanel.getTree().getLastSelectedPathComponent();
								String filename = null;
								if(node instanceof FileNode)
								{
									mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									filename = ((FileNode)node).getFile().getAbsolutePath();
								}
								else if(node instanceof RemoteFileNode)
								{
									mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									filename = ((RemoteFileNode)node).getRemoteFile().getPath();
								}
		//						if(getJCC().getComponent().getPlatform().getComponentFactory().isStartable(type))
								// todo: resultcollect = false?
								if(filename!=null)
								{
									final String ftype = filename;
									SComponentFactory.isStartable(jcc.getExternalAccess(), filename).addResultListener(new SwingDefaultResultListener(spanel)
									{
										public void customResultAvailable(Object result)
										{
											if(((Boolean)result).booleanValue())
												StarterPanel.createComponent(exta, jcc, ftype, null, null, null, false, null, null, null, null, null, StarterServicePanel.this);
											mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										}
									});
								}
							}
						}
		      		}
		  		};
		  		mpanel.getTree().addMouseListener(ml);
		
				comptree = new ComponentTreePanel(exta, JSplitPane.HORIZONTAL_SPLIT);
				comptree.setMinimumSize(new Dimension(0, 0));
				
				lsplit.add(new JScrollPane(mpanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		//		lsplit.add(tp);
				lsplit.add(comptree);
				lsplit.setDividerLocation(300);
		
				csplit.add(lsplit);
				spanel = new StarterPanel(exta, jcc);
				csplit.add(spanel);
				csplit.setDividerLocation(180);
		            			
		//		jcc.addComponentListListener(this);
				
				// todo: ?! is this ok?
				
				jcc.getExternalAccess().scheduleStep(new IComponentStep()
				{
					@XMLClassname("add-component-listener")
					public Object execute(IInternalAccess ia)
					{
						ia.getRequiredService("cms").addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								IComponentManagementService ces = (IComponentManagementService)result;
								ces.getComponentDescriptions().addResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										IComponentDescription[] res = (IComponentDescription[])result;
										for(int i=0; i<res.length; i++)
											componentAdded(res[i]);
									}
								});
								ces.addComponentListener(null, StarterServicePanel.this);
							}
						});
						return null;
					}
				});
				
				add(csplit, BorderLayout.CENTER);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called when an component has died.
	 *  @param ad The component description.
	 */
	public IFuture componentRemoved(final IComponentDescription ad, Map results)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(ad.getName().equals(spanel.parent))
					spanel.setParent(null);
			}
		});
		return new Future(null);
	}

	/**
	 *  Called when an component is born.
	 *  @param ad the component description.
	 */
	public IFuture componentAdded(final IComponentDescription ad)
	{
		return new Future(null);
	}
	
	/**
	 *  Called when an component changed.
	 *  @param ad the component description.
	 */
	public IFuture componentChanged(final IComponentDescription ad)
	{
		return new Future(null);
	}
	
	/**
	 *  Load a model.
	 *  @param model The model name.
	 */
	protected void loadModel(final String model)
	{
		csplit.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		spanel.loadModel(model);
		csplit.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 *  Dynamically create a new menu item structure for starting components.
	 */
	class StartComponentMenuItemConstructor implements IMenuItemConstructor
	{
		/**
		 *  Get or create a new menu item (struture).
		 *  @return The menu item (structure).
		 */
		public JMenuItem getMenuItem()
		{
			JMenuItem ret = null;

			if(isEnabled())
			{
				ITreeNode node = (ITreeNode)mpanel.getTree().getLastSelectedPathComponent();
				if(node instanceof FileNode)
				{
					final String type = ((FileNode)node).getFile().getAbsolutePath();
					
					if(((Boolean)SComponentFactory.isStartable(exta, type).get(new ThreadSuspendable())).booleanValue())//&& ((FileNode)node).isValid())
					{
						try
						{
//							IComponentFactory componentfactory = getJCC().getComponent().getPlatform().getComponentFactory();
							IModelInfo model = (IModelInfo)SComponentFactory.loadModel(exta, type).get(new ThreadSuspendable());
							String[] inistates = model.getConfigurations();
//							IMBDIComponent model = SXML.loadComponentModel(type, null);
//							final IMConfiguration[] inistates = model.getConfigurationbase().getConfigurations();
							
							if(inistates.length>1)
							{
								JMenu re = new JMenu("Start Component");
								re.setIcon(icons.getIcon("start_component"));
								for(int i=0; i<inistates.length; i++)
								{
									final String config = inistates[i];
									JMenuItem me = new JMenuItem(config);
									re.add(me);
									me.addActionListener(new ActionListener()
									{
										public void actionPerformed(ActionEvent e)
										{
											// todo: collectresults = false?
											StarterPanel.createComponent(exta, jcc, type, null, config, null, false, null, null, null, null, null, spanel);
										}
									});
									me.setToolTipText("Start in configuration: "+config);

								}
								ret = re;
								ret.setToolTipText("Start component in selectable configuration");
							}
							else
							{
								if(inistates.length==1)
								{
									ret = new JMenuItem("Start Component ("+inistates[0]+")");
									ret.setToolTipText("Start component in configuration:"+inistates[0]);
								}
								else
								{
									ret = new JMenuItem("Start Component");
									ret.setToolTipText("Start component without explicit initial state");
								}
								ret.setIcon(icons.getIcon("start_component"));
								ret.addActionListener(new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
										// todo: collectresults = false?
										StarterPanel.createComponent(exta, jcc, type, null, null, null, false, null, null, null, null, null, spanel);
									}
								});
							}
						}
						catch(Exception e)
						{
							// NOP
						}
					}
				}
			}

			return ret;
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			boolean ret = false;
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getTree().getLastSelectedPathComponent();
			if(node instanceof FileNode)
			{
				String type = ((FileNode)node).getFile().getAbsolutePath();
				if(((Boolean)SComponentFactory.isStartable(exta, type).get(new ThreadSuspendable())))
					ret = true;
			}
			return ret;
		}
	}
	
	
	/**
	 *  Get the host component of a service. 
	 */
	public IFuture getComponentForService(IServiceProvider provider, final IService service)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.getExternalAccess((IComponentIdentifier)service.getServiceIdentifier().getProviderId())
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(result);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Load the properties.
	 */
	public void setProperties(Properties props)
	{
		// todo: checking
		
//		checkingmenu.setSelected(false);
//		System.out.println("Starter set props: "+props);
		
		Properties	mpanelprops	= props.getSubproperty("mpanel");
		if(mpanelprops!=null)
			mpanel.setProperties(mpanelprops);
		Properties	spanelprops	= props.getSubproperty("spanel");
		if(spanelprops!=null)
			spanel.setProperties(spanelprops);

		lsplit.setDividerLocation(props.getIntProperty("leftsplit_location"));
		csplit.setDividerLocation(props.getIntProperty("mainsplit_location"));

//		checkingmenu.setSelected(props.getBooleanProperty("checking"));
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		
		AbstractJCCPlugin.addSubproperties(props, "mpanel", mpanel.getProperties());
		AbstractJCCPlugin.addSubproperties(props, "spanel", spanel.getProperties());
		
		props.addProperty(new Property("leftsplit_location", ""+lsplit.getDividerLocation()));
		props.addProperty(new Property("mainsplit_location", ""+csplit.getDividerLocation()));
		
//		props.addProperty(new Property("checking", ""+checkingmenu.isSelected()));
		
		return props;
	}
}