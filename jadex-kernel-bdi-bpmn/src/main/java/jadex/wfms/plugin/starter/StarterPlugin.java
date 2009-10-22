package jadex.wfms.plugin.starter;

import jadex.adapter.base.MetaAgentFactory;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bridge.ILoadableElementModel;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.tools.common.AgentTreeTable;
import jadex.tools.common.IMenuItemConstructor;
import jadex.tools.common.PopupBuilder;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.TreeTableNodeType;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.ModelExplorerTreeModel;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.wfms.service.execution.IExecutionService;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *  The starter plugin.
 */
public class StarterPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"resume_process", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_big.png"),
		"suspend_process", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_szzz_big.png"),
		"kill_process", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_killagent.png"),
		"kill_wfms", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_killplatform.png"),
		"starter", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_starter.png"),
		"starter_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_starter_sel.png"),
		"start_process",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/start.png"),
		"process_suspended", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_szzz.png"),
		"checking_menu",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_agent_broken.png")
	});

	//-------- attributes --------

	/** The starter panel. */
	protected StarterPanel spanel;

	/** The panel showing the classpath models. */
	protected ModelExplorer mpanel;

	/** The menu item for enabling/disabling agent model checking. */
	protected JCheckBoxMenuItem	checkingmenu;
	
	/** The agent instances in a tree. */
	protected ElementTreeTable instances;
	
	/** The application instances in a tree. */
//	protected ApplicationTreeTable applications;

	/** A split panel. */
	protected JSplitPane lsplit;

	/** A split panel. */
    protected JSplitPane csplit;

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
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
		JComponent[] ret = new JComponent[6];
		JButton b;

		b = new JButton(mpanel.ADD_PATH);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[0] = b;
		
		b = new JButton(mpanel.REMOVE_PATH);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[1] = b;
		
		b = new JButton(mpanel.REFRESH);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[2] = b;
		
		JSeparator	separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret[3] = separator;
		
		/*b = new JButton(GENERATE_JADEXDOC);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		bar.add(b);
		
		separator = new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		bar.add(separator);*/
		
		b = new JButton(KILL_PROCESS);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[4] = b;
		
		b = new JButton(KILL_WFMS);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[5] = b;

		return ret;
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

		mpanel = new ModelExplorer(getJCC().getServiceContainer(), new StarterNodeFunctionality(this));
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
		mpanel.setPopupBuilder(new PopupBuilder(new Object[]{new StartAgentMenuItemConstructor(), mpanel.ADD_PATH,
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
					//  |  +- MyAgent.agent.xml
					//  +-classes2
					//  |  +- MyAgent.agent.xml

					String model = ((FileNode)node).getRelativePath();
					if(MetaAgentFactory.isLoadable(getJCC().getServiceContainer(), model))
					{
						loadModel(model);
					}
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
							String type = ((FileNode)node).getFile().getAbsolutePath();
							if(MetaAgentFactory.isStartable(getJCC().getServiceContainer(), type))
								((IExecutionService)getJCC().getServiceContainer().getService(IExecutionService.class)).startProcess(type, null, null, false);
							mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
      		}
  		};
  		mpanel.addMouseListener(ml);

  		String name = "wfms"; // todo
  		
		instances = new ElementTreeTable(name);
		instances.setMinimumSize(new Dimension(0, 0));
		instances.getTreetable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		// Change agent node type to enable suspended icon for agents. 
		instances.addNodeType(new TreeTableNodeType(ElementTreeTable.NODE_ELEMENT, 
			new Icon[0], new String[]{"name", "address"}, new String[]{"Name", "Address"})
		{
			public Icon selectIcon(Object value)
			{
				Icon	ret;
				IAMSAgentDescription ad = (IAMSAgentDescription)((DefaultTreeTableNode)value).getUserObject();
				if(IAMSAgentDescription.STATE_SUSPENDED.equals(ad.getState()))
				{
					ret = StarterPlugin.icons.getIcon("agent_suspended");
				}
				else
				{
					ret	= AgentTreeTable.icons.getIcon(AgentTreeTable.NODE_AGENT);
				}
				//System.out.println(value+" "+ad.getState());
				return ret;
			}
		});
		instances.getNodeType(ElementTreeTable.NODE_ELEMENT).addPopupAction(KILL_PROCESS);
		instances.getNodeType(ElementTreeTable.NODE_ELEMENT).addPopupAction(SUSPEND_PROCESS);
		instances.getNodeType(ElementTreeTable.NODE_ELEMENT).addPopupAction(RESUME_PROCESS);
		instances.getNodeType(ElementTreeTable.NODE_CONTAINER).addPopupAction(KILL_WFMS);
		instances.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		
//		applications = new ApplicationTreeTable(name);
//		applications.setMinimumSize(new Dimension(0, 0));
//		applications.getTreetable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		
//		applications.getNodeType(ApplicationTreeTable.NODE_APPLICATION).addPopupAction(KILL_APPLICATION);
//		applications.getNodeType(AgentTreeTable.NODE_PLATFORM).addPopupAction(KILL_PLATFORM);
//		applications.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
//		applications.setColumnWidths(new int[]{200});
		
//		IContextService cs = (IContextService)jcc.getServiceContainer().getService(IContextService.class);
//		if(cs!=null)
//		{
//			cs.addContextListener(new IChangeListener()
//			{
//				public void changeOccurred(final ChangeEvent event)
//				{
//					if(IContextService.EVENT_TYPE_CONTEXT_CREATED.equals(event.getType()))
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								applications.addApplication((IApplicationContext)event.getValue());
//							}
//						});
//					}
//					else if(IContextService.EVENT_TYPE_CONTEXT_DELETED.equals(event.getType()))
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//	//							System.out.println("Remove elem: "+event.getValue());
//								applications.removeApplication((IApplicationContext)event.getValue());
//							}
//						});
//					}
//				}	
//			});
//		}
		
		JTabbedPane tp = new JTabbedPane();
		tp.addTab("processes", instances);
//		tp.addTab("applications", applications);
		
		lsplit.add(new JScrollPane(mpanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		lsplit.add(tp);
		lsplit.setDividerLocation(300);

		csplit.add(lsplit);
		spanel = new StarterPanel(this);
		csplit.add(spanel);
		csplit.setDividerLocation(180);
            			
//		jcc.addAgentListListener(this);
		
		// todo: updates
		
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				agents.adjustColumnWidths();
//			}
//		});

		return csplit;
	}
	
	/**
	 * @return the starter icon
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
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
//		System.out.println("Starter set props: "+props);
		
		Properties	mpanelprops	= props.getSubproperty("mpanel");
		if(mpanelprops!=null)
			mpanel.setProperties(mpanelprops);
		Properties	spanelprops	= props.getSubproperty("spanel");
		if(spanelprops!=null)
			spanel.setProperties(spanelprops);

		lsplit.setDividerLocation(props.getIntProperty("leftsplit.location"));
		csplit.setDividerLocation(props.getIntProperty("mainsplit.location"));

		checkingmenu.setSelected(props.getBooleanProperty("checking"));
		
		Properties ps = props.getSubproperty("agents");
		if(ps!=null)
			instances.setProperties(ps);
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
		
		props.addProperty(new Property("leftsplit.location", ""+lsplit.getDividerLocation()));
		props.addProperty(new Property("mainsplit.location", ""+csplit.getDividerLocation()));
		
		props.addProperty(new Property("checking", ""+checkingmenu.isSelected()));

		addSubproperties(props, "agents", instances.getProperties());
		
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
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#reset()
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
	 *  Called when the agent is closed.
	 */
	public void	shutdown()
	{
		mpanel.close();
	}

	/**
	 *  Action for suspending a process.
	 */
	final AbstractAction SUSPEND_PROCESS = new AbstractAction("Suspend process", icons.getIcon("suspend_process"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[] paths = instances.getTreetable().getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++) 
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)paths[i].getLastPathComponent();
				if(node!=null && node.getUserObject() instanceof IAMSAgentDescription)
				{
					// todo: suspend process
				}
			}
		}

		public boolean isEnabled()
		{
			TreePath[] paths = instances.getTreetable().getTree().getSelectionPaths();
			boolean ret = paths!=null;
			for(int i=0; ret && paths!=null && i<paths.length; i++) 
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)paths[i].getLastPathComponent();
				if(node!=null && node.getUserObject() instanceof IAMSAgentDescription)
				{
					ret &= IAMSAgentDescription.STATE_ACTIVE.equals(
						((IAMSAgentDescription)node.getUserObject()).getState());
				}
			}
			return ret;
		}
	};
	
	/**
	 *  Action for resuming a process.
	 */
	final AbstractAction RESUME_PROCESS = new AbstractAction("Resume process", icons.getIcon("resume_process"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[] paths = instances.getTreetable().getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)paths[i].getLastPathComponent();
				if(node!=null && node.getUserObject() instanceof IAMSAgentDescription)
				{
//					jcc.resumeAgent(((IAMSAgentDescription)node.getUserObject()).getName());
					
					// todo: resume process
				}
			}
		}
		
		public boolean isEnabled()
		{
			TreePath[] paths = instances.getTreetable().getTree().getSelectionPaths();
			boolean ret = paths!=null;
			for(int i=0; ret && paths!=null && i<paths.length; i++) 
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)paths[i].getLastPathComponent();
				if(node!=null && node.getUserObject() instanceof IAMSAgentDescription)
				{
					ret &= IAMSAgentDescription.STATE_SUSPENDED.equals(
						((IAMSAgentDescription)node.getUserObject()).getState());
				}
			}
			return ret;
		}
	};
	
	/**
	 *  Action for killing an agent.
	 */
	final AbstractAction KILL_PROCESS = new AbstractAction("Kill process", icons.getIcon("kill_process"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[] paths = instances.getTreetable().getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++) 
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)paths[i].getLastPathComponent();
				if(node!=null && node.getUserObject() instanceof IAMSAgentDescription)
				{
//					jcc.killAgent(((IAMSAgentDescription)node.getUserObject()).getName());
//					((IExecutionService)jcc.getServiceContainer().getService(IExecutionService.class)).killProcess();
				
					// todo: implement me
				}
			}
		}
	};
	
	/**
	 *  Action for killing the wfms.
	 */
	final AbstractAction KILL_WFMS = new AbstractAction("Kill wfms", icons.getIcon("kill_wfms"))
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				jcc.getServiceContainer().shutdown(null);
			}
			catch(final GoalFailureException ex)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String text = SUtil.wrapText("Could not kill platform: "+ex.getMessage());
						JOptionPane.showMessageDialog(SGUI.getWindowParent(spanel), text, "Platform Shutdown Problem", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
	};

	/**
	 *  Called when an agent has died.
	 *  @param ad The agent description.
	 */
	public void agentDied(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				instances.removeAgent(ad);
			}
		});
	}

	/**
	 *  Called when an agent is born.
	 *  @param ad the agent description.
	 */
	public void agentBorn(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				instances.addAgent(ad);
			}
		});
	}
	
	/**
	 *  Called when an agent changed.
	 *  @param ad the agent description.
	 */
	public void agentChanged(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				instances.updateAgent(ad);
			}
		});
	}

	/** 
	 * @return the help id of the perspective
	 * @see jadex.tools.jcc.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.starter";
	}

	//-------- starter specific tree actions --------

	/**
	 *  Dynamically create a new menu item structure for starting agents.
	 */
	class StartAgentMenuItemConstructor implements IMenuItemConstructor
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
					if(MetaAgentFactory.isStartable(jcc.getServiceContainer(), type))
					{
						try
						{
							ILoadableElementModel model = MetaAgentFactory.loadModel(jcc.getServiceContainer(), type);
							String[] inistates = model.getConfigurations();
//							IMBDIAgent model = SXML.loadAgentModel(type, null);
//							final IMConfiguration[] inistates = model.getConfigurationbase().getConfigurations();
							
							if(inistates.length>1)
							{
								JMenu re = new JMenu("Start Process");
								re.setIcon(icons.getIcon("start_process"));
								for(int i=0; i<inistates.length; i++)
								{
									final String config = inistates[i];
									JMenuItem me = new JMenuItem(config);
									re.add(me);
									me.addActionListener(new ActionListener()
									{
										public void actionPerformed(ActionEvent e)
										{
											((IExecutionService)getJCC().getServiceContainer().getService(IExecutionService.class)).startProcess(type, null, null, false);
										}
									});
									me.setToolTipText("Start in configuration: "+config);

								}
								ret = re;
								ret.setToolTipText("Start agent in selectable configuration");
							}
							else
							{
								if(inistates.length==1)
								{
									ret = new JMenuItem("Start Agent ("+inistates[0]+")");
									ret.setToolTipText("Start agent in configuration:"+inistates[0]);
								}
								else
								{
									ret = new JMenuItem("Start Agent");
									ret.setToolTipText("Start agent without explicit initial state");
								}
								ret.setIcon(icons.getIcon("start_agent"));
								ret.addActionListener(new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
										((IExecutionService)getJCC().getServiceContainer().getService(IExecutionService.class)).startProcess(type, null, null, false);
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
				// todo: fix me
				if(type.endsWith(".agent.xml"))
//				if(SXML.isAgentFilename(type))
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

	/** The Jadex agent extension. * /
	public static final String FILE_EXTENSION_AGENT = ".agent.xml";

	/** The Jadex capability extension. * /
	public static final String FILE_EXTENSION_CAPABILITY = ".capability.xml";
	
	/**
	 *  Test if a file is a Jadex file.
	 *  @param filename The filename.
	 *  @return True, if it is a Jadex file.
	 * /
	public static boolean isJadexFilename(String filename)
	{
		return filename!=null && (filename.toLowerCase().endsWith(FILE_EXTENSION_AGENT)
			|| filename.toLowerCase().endsWith(FILE_EXTENSION_CAPABILITY)
			|| filename.toLowerCase().endsWith("agent.class"));
	}

	/**
	 *  Test if a file is an agent file.
	 *  @param filename The filename.
	 *  @return True, if it is an agent file.
	 * /
	public static boolean isAgentFilename(String filename)
	{
		return filename!=null && filename.toLowerCase().endsWith(FILE_EXTENSION_AGENT);
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

}


