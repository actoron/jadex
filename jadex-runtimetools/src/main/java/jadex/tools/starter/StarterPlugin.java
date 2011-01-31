package jadex.tools.starter;

import jadex.base.SComponentFactory;
import jadex.base.gui.ComponentIdentifierDialog;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.CreationInfo;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.IMenuItemConstructor;
import jadex.commons.gui.PopupBuilder;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.ModelExplorerTreeModel;
import jadex.xml.annotation.XMLClassname;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *  The starter plugin.
 */
public class StarterPlugin extends AbstractJCCPlugin	implements ICMSComponentListener
{
	//-------- constants --------

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
	private StarterPanel spanel;

	/** The panel showing the classpath models. */
	private ModelExplorer mpanel;
	
	/** The starter node functionality. */
	protected StarterNodeFunctionality snf;

	/** The menu item for enabling/disabling component model checking. */
	private JCheckBoxMenuItem checkingmenu;
	
	/** The component instances in a tree. */
	private ComponentTreePanel comptree;
	
	/** A split panel. */
	private JSplitPane lsplit;

	/** A split panel. */
    private JSplitPane csplit;

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 *  @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Starter";
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		List	ret	= new ArrayList();
		JButton b;

		b = new JButton(mpanel.ADD_PATH);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(mpanel.REMOVE_PATH);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(mpanel.REFRESH);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		JSeparator	separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret.add(separator);
		
		/*b = new JButton(GENERATE_JADEXDOC);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		bar.add(b);
		
		separator = new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		bar.add(separator);*/
		
		b = new JButton(ADD_REMOTE_COMPONENT);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

		b = new JButton(KILL_PLATFORM);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(comptree.getKillAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(comptree.getSuspendAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(comptree.getResumeAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(comptree.getStepAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret.add(separator);

		b = new JButton(comptree.getShowPropertiesAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(comptree.getShowObjectDetailsAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

		b = new JButton(comptree.getRefreshAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(comptree.getRefreshTreeAction());
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

		return (JComponent[])ret.toArray(new JComponent[ret.size()]);
	}
	
	/**
	 *  Create menu bar.
	 *  @return The menu bar.
	 */
	public JMenu[] createMenuBar()
	{
		JMenu[]	menu	= mpanel.createMenuBar();
		this.checkingmenu = new JCheckBoxMenuItem(TOGGLE_CHECKING);
		this.checkingmenu.setSelected(true);	// Default: on
		menu[0].insert(checkingmenu, 1);	// Hack??? Should not assume position.
		checkingmenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("turn: "+checkingmenu.isSelected());
				snf.setChecking(checkingmenu.isSelected());
			}
		});
		return menu;
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		csplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		csplit.setOneTouchExpandable(true);

		lsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		lsplit.setOneTouchExpandable(true);
		lsplit.setResizeWeight(0.7);

		snf = new StarterNodeFunctionality(jcc);
		mpanel = new ModelExplorer(getJCC().getExternalAccess(), snf);
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
		mpanel.setPopupBuilder(new PopupBuilder(new Object[]{new StartComponentMenuItemConstructor(), mpanel.ADD_PATH,
			mpanel.REMOVE_PATH, mpanel.REFRESH}));
		mpanel.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				Object	node = mpanel.getLastSelectedPathComponent();
				if(node instanceof FileNode)
				{
					// Models have to be loaded with absolute path.
					// An example to facilitate understanding:
					// root
					//  +-classes1
					//  |  +- MyComponent.component.xml
					//  +-classes2
					//  |  +- MyComponent.component.xml

					final String model = ((FileNode)node).getRelativePath();
//					if(getJCC().getComponent().getPlatform().getComponentFactory().isLoadable(model))
					SComponentFactory.isLoadable(getJCC().getExternalAccess(), model).addResultListener(new SwingDefaultResultListener(spanel)
					{
						public void customResultAvailable(Object result)
						{
							if(((Boolean)result).booleanValue())
								loadModel(model);
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
				int row = mpanel.getRowForLocation(e.getX(), e.getY());
				if(row != -1)
				{
					if(e.getClickCount() == 2)
					{
						Object	node = mpanel.getLastSelectedPathComponent();
						if(node instanceof FileNode)
						{
							mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							final String type = ((FileNode)node).getFile().getAbsolutePath();
//							if(getJCC().getComponent().getPlatform().getComponentFactory().isStartable(type))
							// todo: resultcollect = false?
							SComponentFactory.isStartable(getJCC().getExternalAccess(), type).addResultListener(new SwingDefaultResultListener(spanel)
							{
								public void customResultAvailable(Object result)
								{
									if(((Boolean)result).booleanValue())
										createComponent(type, null, null, null, false, null, null, null, null);
									mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							});
						}
					}
				}
      		}
  		};
  		mpanel.addMouseListener(ml);

		comptree = new ComponentTreePanel(getJCC().getExternalAccess(), JSplitPane.HORIZONTAL_SPLIT);
		comptree.setMinimumSize(new Dimension(0, 0));
		
		lsplit.add(new JScrollPane(mpanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
//		lsplit.add(tp);
		lsplit.add(comptree);
		lsplit.setDividerLocation(300);

		csplit.add(lsplit);
		spanel = new StarterPanel(getJCC().getExternalAccess(), getJCC());
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
						ces.addComponentListener(null, StarterPlugin.this);
					}
				});
				return null;
			}
		});
		

//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				components.adjustColumnWidths();
//			}
//		});
		
//		JTreeTableComboTest.test(components.getTreetable());
		

		return csplit;
	}
	
	/**
	 * @return the starter icon
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("starter_sel"): icons.getIcon("starter");
	}

	/**
	 * Load the properties.
	 */
	public void setProperties(Properties props)
	{
		checkingmenu.setSelected(false);
//		System.out.println("Starter set props: "+props);
		
		Properties	mpanelprops	= props.getSubproperty("mpanel");
		if(mpanelprops!=null)
			mpanel.setProperties(mpanelprops);
		Properties	spanelprops	= props.getSubproperty("spanel");
		if(spanelprops!=null)
			spanel.setProperties(spanelprops);

		lsplit.setDividerLocation(props.getIntProperty("leftsplit_location"));
		csplit.setDividerLocation(props.getIntProperty("mainsplit_location"));

		checkingmenu.setSelected(props.getBooleanProperty("checking"));
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		
		addSubproperties(props, "mpanel", mpanel.getProperties());
		addSubproperties(props, "spanel", spanel.getProperties());
		
		props.addProperty(new Property("leftsplit_location", ""+lsplit.getDividerLocation()));
		props.addProperty(new Property("mainsplit_location", ""+csplit.getDividerLocation()));
		
		props.addProperty(new Property("checking", ""+checkingmenu.isSelected()));
		
		return props;
	}
	
	/**
	 *  Load a model.
	 *  @param model The model name.
	 */
	protected void loadModel(final String model)
	{
		getCpanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getStarterPanel().loadModel(model);
		getCpanel().setCursor(Cursor.getDefaultCursor());
	}

	/**
	 *  Get the central panel.
	 *  @return cpanel The panel.
	 */
	protected Component getCpanel()
	{
		return csplit;
	}

	/**
	 *  Get the starter panel.
	 * @return the starter panel
	 */
	protected StarterPanel getStarterPanel()
	{
		return spanel;
	}
	
	/**
	 *  Get the checking menu.
	 */
	protected JCheckBoxMenuItem getCheckingMenu()
	{
		return checkingmenu;
	}
	
	/**
	 *  Get the model explorer.
	 * @return the model explorer
	 */
	protected ModelExplorer getModelExplorer()
	{
		return mpanel;
	}

	/**
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#reset()
	 */
	public void reset()
	{
		try
		{
			mpanel.reset();
			spanel.reset();
			this.checkingmenu.setSelected(true);	// Default: on
		}
		catch(Exception e)
		{
		}
	}

	/**
	 *  Called when the component is closed.
	 */
	public void	shutdown()
	{
		mpanel.close();
	}

//	/**
//	 *  Action for selecting a component as current parent.
//	 */
//	final AbstractAction USE_AS_PARENT = new AbstractAction("Use component as parent", icons.getIcon("start_component"))
//	{
//		public void actionPerformed(ActionEvent e)
//		{
//			TreePath[] paths = components.getTreetable().getTree().getSelectionPaths();
//			for(int i=0; paths!=null && i<paths.length; i++) 
//			{
//				DefaultTreeTableNode node = (DefaultTreeTableNode)paths[i].getLastPathComponent();
//				if(node!=null && node.getUserObject() instanceof IComponentDescription)
//				{
//					spanel.setParent(((IComponentDescription)node.getUserObject()).getName());
//				}
//			}
//		}
//	};

	/**
	 *  Action for killing the platform.
	 */
	final AbstractAction KILL_PLATFORM = new AbstractAction("Kill platform", icons.getIcon("kill_platform"))
	{
		public void actionPerformed(ActionEvent e)
		{
			SJCC.killPlattform(getJCC(), comptree);
		}
	};
	
	/**
	 *  Action for adding a remote component.
	 */
	final AbstractAction ADD_REMOTE_COMPONENT = new AbstractAction("Add remote component", icons.getIcon("add_remote_component"))
	{
		public void actionPerformed(ActionEvent e)
		{
			jcc.getExternalAccess().scheduleStep(new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					ia.getRequiredService("cms").addResultListener(new DefaultResultListener()		
					{
						public void resultAvailable(Object result)
						{
							ComponentIdentifierDialog dia = new ComponentIdentifierDialog(spanel, jcc.getExternalAccess().getServiceProvider());
							IComponentIdentifier cid = dia.getComponentIdentifier(null);
							
							if(cid!=null)
							{
								Map args = new HashMap();
								args.put("component", cid);
								createComponent("jadex/base/service/remote/ProxyAgent.class", cid.getLocalName(), null, args, false, null, null, null, null);
							}
						}
					});
					return null;
				}
			});
		}
	};


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
	 * @return the help id of the perspective
	 * @see jadex.base.gui.plugin.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.starter";
	}

	//-------- starter specific tree actions --------

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
				IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
				if(node instanceof FileNode)
				{
					final String type = ((FileNode)node).getFile().getAbsolutePath();
					
					if(((Boolean)SComponentFactory.isStartable(getJCC().getExternalAccess(), type).get(new ThreadSuspendable())).booleanValue())//&& ((FileNode)node).isValid())
					{
						try
						{
//							IComponentFactory componentfactory = getJCC().getComponent().getPlatform().getComponentFactory();
							IModelInfo model = (IModelInfo)SComponentFactory.loadModel(getJCC().getExternalAccess(), type).get(new ThreadSuspendable());
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
											createComponent(type, null, config, null, false, null, null, null, null);
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
										createComponent(type, null, null, null, false, null, null, null, null);
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
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			if(node instanceof FileNode)
			{
				String type = ((FileNode)node).getFile().getAbsolutePath();
				if(((Boolean)SComponentFactory.isStartable(getJCC().getExternalAccess(), type).get(new ThreadSuspendable())))
					ret = true;
			}
			return ret;
		}
	}
	
	/**
	 *  The action for changing integrity checking settings.
	 */
	public final AbstractAction TOGGLE_CHECKING = new AbstractAction("Auto check", icons.getIcon("checking_menu"))
	{
		public void actionPerformed(ActionEvent e)
		{
			((ModelExplorerTreeModel)getModelExplorer().getModel())
				.fireTreeStructureChanged(getModelExplorer().getRootNode());
		}
	};
	
	//-------- constants --------

	/** The Jadex component extension. * /
	public static final String FILE_EXTENSION_COMPONENT = ".component.xml";

	/** The Jadex capability extension. * /
	public static final String FILE_EXTENSION_CAPABILITY = ".capability.xml";
	
	/**
	 *  Test if a file is a Jadex file.
	 *  @param filename The filename.
	 *  @return True, if it is a Jadex file.
	 * /
	public static boolean isJadexFilename(String filename)
	{
		return filename!=null && (filename.toLowerCase().endsWith(FILE_EXTENSION_COMPONENT)
			|| filename.toLowerCase().endsWith(FILE_EXTENSION_CAPABILITY)
			|| filename.toLowerCase().endsWith("component.class"));
	}

	/**
	 *  Test if a file is an component file.
	 *  @param filename The filename.
	 *  @return True, if it is an component file.
	 * /
	public static boolean isComponentFilename(String filename)
	{
		return filename!=null && filename.toLowerCase().endsWith(FILE_EXTENSION_COMPONENT);
	}

	/**
	 *  Test if a file is a capability file.
	 *  @param filename The filename.
	 *  @return True, if it is a capability file.
	 * /
	public static boolean isCapabilityFilename(String filename)
	{
		return filename!=null && filename.toLowerCase().endsWith(FILE_EXTENSION_CAPABILITY);
	}*/

	/**
	 *  Create a new component on the platform.
	 *  Any errors will be displayed in a dialog to the user.
	 */
	public IFuture createComponent(final String type, final String name, final String configname, final Map arguments, final Boolean suspend, 
		final Boolean master, final Boolean daemon, final Boolean autosd, final IResultListener killlistener)
	{
		final Future ret = new Future(); 
		jcc.getExternalAccess().scheduleStep(new IComponentStep()
		{
			@XMLClassname("create-component")
			public Object execute(IInternalAccess ia)
			{
				ia.getRequiredService("cms").addResultListener(new SwingDefaultResultListener(spanel)
				{
					public void customResultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						cms.createComponent(name, type, new CreationInfo(configname, arguments, spanel.parent, suspend, master, daemon, autosd), killlistener)
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								ret.setResult(result);
								getJCC().setStatusText("Created component: " + ((IComponentIdentifier)result).getLocalName());
							}
							
							public void exceptionOccurred(Exception exception)
							{
								ret.setException(exception);
								getJCC().displayError("Problem Starting Component", "Component could not be started.", exception);
							}
						});
					}
				});
				return null;
			}
		});
		return ret;
	}
}