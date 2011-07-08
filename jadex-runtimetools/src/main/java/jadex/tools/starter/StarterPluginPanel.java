package jadex.tools.starter;

import jadex.base.SComponentFactory;
import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.asynctree.INodeListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.RemoteFileNode;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.base.gui.plugin.AbstractJCCPlugin.ShowRemoteControlCenterHandler;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ISettingsService;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * The starter gui allows for starting components platform independently.
 */
public class StarterPluginPanel extends JPanel
{
	//-------- static part --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"add_remote_component", SGUI.makeIcon(StarterPluginPanel.class, "/jadex/tools/common/images/add_remote_component.png"),
		"kill_platform", SGUI.makeIcon(StarterPluginPanel.class, "/jadex/tools/common/images/new_killplatform.png"),
		"starter", SGUI.makeIcon(StarterPluginPanel.class, "/jadex/tools/common/images/new_starter.png"),
		"starter_sel", SGUI.makeIcon(StarterPluginPanel.class, "/jadex/tools/common/images/new_starter_sel.png"),
		"start_component",	SGUI.makeIcon(StarterPluginPanel.class, "/jadex/tools/common/images/start.png"),
		"checking_menu",	SGUI.makeIcon(StarterPluginPanel.class, "/jadex/tools/common/images/new_agent_broken.png")
	});

	//-------- attributes --------

	/** The starter panel. */
	protected StarterPanel spanel;

	/** The panel showing the classpath models. */
	protected ModelTreePanel mpanel;
	
	/** The component instances in a tree. */
	protected ComponentTreePanel comptree;
	
	/** A split panel. */
	protected JSplitPanel lsplit;

	/** A split panel. */
    protected JSplitPanel csplit;
	
    /** The jcc. */
    protected IControlCenter jcc;
    
	//-------- constructors --------

	/**
	 * Open the GUI.
	 * @param starter The starter.
	 */
	public StarterPluginPanel(final IControlCenter jcc)
	{
		super(new BorderLayout());
		this.jcc = jcc;
				
		csplit = new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT, true);
		csplit.setOneTouchExpandable(true);

		lsplit = new JSplitPanel(JSplitPane.VERTICAL_SPLIT, true);
		lsplit.setOneTouchExpandable(true);
		lsplit.setResizeWeight(0.7);

		mpanel = new ModelTreePanel(jcc.getPlatformAccess(), jcc.getJCCAccess(), !SUtil.equals(jcc.getPlatformAccess().getComponentIdentifier().getPlatformName(), jcc.getJCCAccess().getComponentIdentifier().getPlatformName()))
		{
			public void removeTopLevelNode(ITreeNode node)
			{
				if(node instanceof IFileNode && spanel!=null && spanel.lastfile!=null)
				{
					String	 path	= ((IFileNode)node).getFilePath();
					if(spanel.lastfile.startsWith(path))
					{
						
					}
					else
					{
						
					}
				}
				super.removeTopLevelNode(node);
			}
		};
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

					final String ffilename = filename;
					SComponentFactory.isLoadable(jcc.getPlatformAccess(), filename).addResultListener(new SwingDefaultResultListener(spanel)
					{
						public void customResultAvailable(Object result)
						{
							if(((Boolean)result).booleanValue())
								spanel.loadModel(ffilename);
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
							SComponentFactory.isStartable(jcc.getPlatformAccess(), filename).addResultListener(new SwingDefaultResultListener(spanel)
							{
								public void customResultAvailable(Object result)
								{
									if(((Boolean)result).booleanValue())
										StarterPanel.createComponent(jcc.getPlatformAccess(), jcc, ftype, null, null, null, false, null, null, null, null, null, StarterPluginPanel.this);
									mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							});
						}
					}
				}
      		}
  		};
  		mpanel.getTree().addMouseListener(ml);

		comptree = new ComponentTreePanel(jcc.getPlatformAccess(), jcc.getCMSHandler(), JSplitPane.HORIZONTAL_SPLIT);
		comptree.setMinimumSize(new Dimension(0, 0));
		comptree.getModel().addNodeListener(new INodeListener()
		{
			public void nodeRemoved(final ITreeNode node)
			{
				if(node instanceof IActiveComponentTreeNode)
				{
					if(((IActiveComponentTreeNode)node).getDescription().getName().equals(spanel.parent))
						spanel.setParent(null);
				}
			}
			
			public void nodeAdded(ITreeNode node)
			{
			}
		});
		
		comptree.addNodeHandler(new ShowRemoteControlCenterHandler(jcc, StarterPluginPanel.this));
		
		lsplit.add(mpanel);
//		lsplit.add(tp);
		lsplit.add(comptree);
//				lsplit.setDividerLocation(300);
		lsplit.setResizeWeight(0.7);
		lsplit.setDividerLocation(0.7);
//		SGUI.setDividerLocation(lsplit, 300);

		csplit.add(lsplit);
		spanel = new StarterPanel(jcc.getPlatformAccess(), jcc);
		csplit.add(spanel);
//				csplit.setDividerLocation(180);
		csplit.setResizeWeight(0.3);
		csplit.setDividerLocation(0.3);
//		SGUI.setDividerLocation(csplit, 180);
            			
		add(csplit, BorderLayout.CENTER);
		
		loadPlatformProperties();	// Todo: wait for loaded properties.
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
					
					if(((Boolean)SComponentFactory.isStartable(jcc.getPlatformAccess(), type).get(new ThreadSuspendable())).booleanValue())//&& ((FileNode)node).isValid())
					{
						try
						{
//							IComponentFactory componentfactory = getJCC().getComponent().getPlatform().getComponentFactory();
							IModelInfo model = (IModelInfo)SComponentFactory.loadModel(jcc.getPlatformAccess(), type).get(new ThreadSuspendable());
							String[] inistates = model.getConfigurationNames();
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
											StarterPanel.createComponent(jcc.getPlatformAccess(), jcc, type, null, config, null, false, null, null, null, null, null, spanel);
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
										StarterPanel.createComponent(jcc.getPlatformAccess(), jcc, type, null, null, null, false, null, null, null, null, null, spanel);
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
			Object	node	= mpanel.getTree().getLastSelectedPathComponent();
			if(node instanceof FileNode)
			{
				String type = ((FileNode)node).getFile().getAbsolutePath();
				if(((Boolean)SComponentFactory.isStartable(jcc.getPlatformAccess(), type).get(new ThreadSuspendable())))
					ret = true;
			}
			return ret;
		}
	}
	
	/**
	 *  Get the mpanel.
	 *  @return the mpanel.
	 */
	public ModelTreePanel getModelTreePanel()
	{
		return mpanel;
	}

	/**
	 *  Get the comptree.
	 *  @return the comptree.
	 */
	public ComponentTreePanel getComponentTreePanel()
	{
		return comptree;
	}

	/**
	 *  Load and apply the platform properties.
	 */
	public IFuture loadPlatformProperties()
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(jcc.getPlatformAccess().getServiceProvider(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) throws Exception
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.getProperties("StarterServicePanel")
					.addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result) throws Exception
					{
						if(result!=null)
						{
							final Properties	props	= (Properties)result;
							mpanel.setProperties(props.getSubproperty("mpanel"))
								.addResultListener(new SwingDelegationResultListener(ret)
							{
								public void customResultAvailable(Object result) throws Exception
								{
									spanel.setProperties(props.getSubproperty("spanel"));
									ret.setResult(null);
								}
							});
						}
						else
						{
							ret.setResult(null);
						}
					}
				});
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Save the platform properties.
	 */
	public IFuture	pushPlatformProperties()
	{
		final Future	ret	= new Future();
//		System.out.println("fetching settings service");
		SServiceProvider.getService(jcc.getPlatformAccess().getServiceProvider(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) throws Exception
			{
//				System.out.println("fetching mpanel properties");
				final ISettingsService	settings	= (ISettingsService)result;
				mpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
//						System.out.println("fetched mpanel properties");
						final Properties	props	= new Properties();
						props.addSubproperties("mpanel", (Properties)result);
						spanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
//								System.out.println("fetched spanel properties");
								props.addSubproperties("spanel", (Properties)result);
								settings.setProperties("StarterServicePanel", props)
									.addResultListener(new SwingDelegationResultListener(ret));
							}
						});
					}
				});
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				// No settings service: ignore.
				ret.setResult(null);
			}
		});
		
		return ret;
	}

	/**
	 *  Load the properties.
	 */
	public IFuture setProperties(Properties props)
	{
		double dl = props.getDoubleProperty("leftsplit_location");
		if(dl!=0)
		{
			lsplit.setDividerLocation(dl);
			lsplit.setResizeWeight(dl);
		}
		dl = props.getDoubleProperty("mainsplit_location");
		if(dl!=0)
		{
			csplit.setDividerLocation(dl);
			csplit.setResizeWeight(dl);
		}
//		SGUI.setDividerLocation(lsplit, props.getIntProperty("leftsplit_location"));
//		SGUI.setDividerLocation(csplit, props.getIntProperty("mainsplit_location"));
		return IFuture.DONE;
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public IFuture getProperties()
	{
		Properties	props	= new Properties();
		
		props.addProperty(new Property("leftsplit_location", ""+lsplit.getProportionalDividerLocation()));
		props.addProperty(new Property("mainsplit_location", ""+csplit.getProportionalDividerLocation()));
//		props.addProperty(new Property("leftsplit_location", ""+lsplit.getDividerLocation()));
//		props.addProperty(new Property("mainsplit_location", ""+csplit.getDividerLocation()));
		return new Future(props);
	}
	
	/**
	 *  Dispose the panel.
	 */
	public void dispose()
	{
		comptree.dispose();
	}
}