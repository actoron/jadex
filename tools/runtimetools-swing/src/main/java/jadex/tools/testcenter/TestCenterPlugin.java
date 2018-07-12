package jadex.tools.testcenter;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.INodeListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.base.gui.modeltree.RemovePathAction;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.collection.SCollection;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

/**
 *  Plugin for the test center.
 */
public class TestCenterPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"testcenter_sel", SGUI.makeIcon(TestCenterPlugin.class, "/jadex/tools/common/images/new_testcenter_sel.png"),
		"testcenter", SGUI.makeIcon(TestCenterPlugin.class, "/jadex/tools/common/images/new_testcenter.png"),
		"arrow_right", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/arrow_right.png"),		
		"test_small", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_agent_testable.png"),
		"add_agent", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_add_agent_testable.png"),
		"add_package", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_add_package_testable.png"),
		"remove_agent", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_remove_agent_testable.png"),
		"remove_package", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_remove_package_testable.png"),
//		"scanning_off",	SGUI.makeIcon(TestCenterPlugin.class, "/jadex/tools/common/images/new_agent_testable.png")
	});

	//-------- attributes --------

	/** The panel showing the classpath models. */
	protected ModelTreePanel	mpanel;

	/** The test center panel. */
	protected TestCenterPanel tcpanel;

	//-------- methods --------

	/**
	 * Return the unique name of this plugin. Used e.g. to store properties of
	 * each plugin.
	 */
	public String getName()
	{
		return "Test Center";
	}

	/**
	 * Return the icon representing this plugin.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("testcenter_sel"): icons.getIcon("testcenter");
	}

	/**
	 * Return the id for the help system
	 */
	public String getHelpID()
	{
		return "tools.testcenter";
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JComponent[] ret = new JComponent[5];
		JButton b;

		b = new JButton(mpanel.getAction(AddPathAction.getName()));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[0] = b;

		b = new JButton(mpanel.getAction(RemovePathAction.getName()));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[1] = b;

//		b = new JButton(new RefreshAction(mpanel.getTree()));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret[2] = b;
//		
//		b = new JButton(new RefreshSubtreeAction(mpanel.getTree()));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret[3] = b;
		
		JSeparator	separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret[2] = separator;

		b = new JButton(ADD_TESTCASES);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[3] = b;

		b = new JButton(REMOVE_TESTCASES);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[4] = b;

		return ret;
	}
	
//	/**
//	 *  Create menu bar.
//	 *  @return The menu bar.
//	 */
//	public JMenu[] createMenuBar()
//	{
//		JMenu[]	menu	= mpanel.createMenuBar();
//		this.checkingmenu = new JCheckBoxMenuItem(TOGGLE_CHECKING);
//		this.checkingmenu.setSelected(true);	// Default: on
//		menu[0].insert(checkingmenu, 1);	// Hack??? Should not assume position.
//		return menu;
//	}
	
//	/**
//	 *  Get the checking menu.
//	 */
//	protected JCheckBoxMenuItem getCheckingMenu()
//	{
//		return checkingmenu;
//	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{

//		final JButton select	= new JButton(icons.getIcon("arrow_right"));
//		select.setMargin(new Insets(1,1,1,1));
//		select.setToolTipText("Add selected agent to test suite");
//		select.setEnabled(false);
//		select.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				//Object	node = mpanel.getLastSelectedPathComponent();
//				TreePath[] sels = mpanel.getSelectionPaths();
//				for(int i=0; i<sels.length; i++)
//				{
//					Object node = sels[i].getLastPathComponent();
//					addTestcases((TreeNode)node);
//				}
//			}
//		});

		mpanel = new ModelTreePanel(getJCC().getPlatformAccess(), jcc.getJCCAccess(), !getJCC().getJCCAccess().getId().getPlatformName()
			.equals(getJCC().getPlatformAccess().getId().getPlatformName()));
//		mpanel.setPopupBuilder(new PopupBuilder(new Object[]{mpanel.ADD_PATH, mpanel.REMOVE_PATH, mpanel.REFRESH,
//			ADD_TESTCASE, ADD_TESTCASES, REMOVE_TESTCASE, REMOVE_TESTCASES}));

		// Update properties on node change to have consistent state (model vs. library) for remote JCCs.
		mpanel.getModel().addNodeListener(new INodeListener()
		{
			public void nodeRemoved(ITreeNode node)
			{
				pushPlatformSettings();
			}
			
			public void nodeAdded(ITreeNode node)
			{
				pushPlatformSettings();
			}
		});

//		mpanel.addTreeSelectionListener(new TreeSelectionListener()
//		{
//			public void valueChanged(TreeSelectionEvent e)
//			{
//				Object	node = mpanel.getLastSelectedPathComponent();
//				if(node!=null)
//				{
//					if(SXML.isAgentFilename(((FileNode)node).getFile().getAbsolutePath()) || node instanceof DirNode)
//						select.setEnabled(true);
//					else
//						select.setEnabled(false);
//				}
//			}
//		});
		
		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				int row = mpanel.getTree().getRowForLocation(e.getX(), e.getY());
				if(row != -1)
				{
					if(e.getClickCount() == 2)
					{
						TreePath selpath = mpanel.getTree().getPathForRow(row);
						Object	node = mpanel.getTree().getPathForRow(row).getLastPathComponent();
						if(node instanceof IFileNode)
						{
							if(((IFileNode)node).isDirectory())
							{
								if(mpanel.getTree().isExpanded(row))
								{
									mpanel.getTree().collapseRow(row);
								}
								else
								{
									mpanel.getTree().expandRow(row);									
								}
							}
							else
							{
								mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								final String model = ((IFileNode)node).getFilePath();
								createResourceIdentifier((IFileNode)selpath.getPathComponent(1))
									.addResultListener(new SwingDefaultResultListener<IResourceIdentifier>(mpanel)
								{
									public void customResultAvailable(final IResourceIdentifier rid)
									{
										SRemoteGui.isTestcase(model, getJCC().getPlatformAccess(), rid)
											.addResultListener(new SwingDefaultResultListener(mpanel)
										{
											public void customResultAvailable(Object result)
											{
												if(((Boolean)result).booleanValue())
												{
//													tcpanel.getTestList().addEntry(model);
													tcpanel.addTest(model, rid);
												}
												else
												{
													jcc.setStatusText("Component is not a testcase: "+model);
												}
												mpanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
											}
										});
									}
								});
							}
						}
					}
				}
      		}
  		};
  		mpanel.getTree().addMouseListener(ml);

//		JPanel	tp	= new JPanel(new GridBagLayout());
//		//tp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Known Agents "));
//		tp.add(		new JScrollPane(mpanel), new GridBagConstraints(0,0, 1,GridBagConstraints.REMAINDER,	1,1,GridBagConstraints.CENTER,
//			GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
////		tp(new JLabel(), new GridBagConstraints(1,0, GridBagConstraints.REMAINDER,1,0,1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));
//		tp.add(select, new GridBagConstraints(1,0, GridBagConstraints.REMAINDER,1,0,0,GridBagConstraints.CENTER,
//			GridBagConstraints.NONE,new Insets(0,2,0,2),0,0));
//		tp.add(new JLabel(), new GridBagConstraints(1,1, GridBagConstraints.REMAINDER,GridBagConstraints.REMAINDER,
//			0,1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));

		tcpanel = new TestCenterPanel(this);
		JSplitPanel mainpanel = new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT);
		mainpanel.setOneTouchExpandable(true);
		mainpanel.setResizeWeight(0.3);
		mainpanel.setDividerLocation(0.3);

//		mainpanel.add(tp);
		mainpanel.add(mpanel);
		mainpanel.add(tcpanel);
		
		loadPlatformProperties();	// Todo: wait for platform properties to be loaded?

		return mainpanel;
	}
	
	/**
	 *  Load and apply the platform properties.
	 */
	public IFuture loadPlatformProperties()
	{
		final Future	ret	= new Future();
		getJCC().getPlatformAccess().searchService( new ServiceQuery<>( ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.getProperties("TestCenterPlugin")
					.addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						if(result!=null)
						{
							final Properties	props	= (Properties)result;
							mpanel.setProperties(props.getSubproperty("mpanel"))
								.addResultListener(new SwingDelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									tcpanel.setProperties(props.getSubproperty("tcpanel"))
										.addResultListener(new SwingDelegationResultListener(ret));
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
	 *  Store settings if any in platform settings service.
	 */
	public IFuture pushPlatformSettings()
	{
		final Future	ret	= new Future();
		getJCC().getPlatformAccess().searchService( new ServiceQuery<>( ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final ISettingsService	settings	= (ISettingsService)result;
				mpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final Properties	props	= new Properties();
						props.addSubproperties("mpanel", (Properties)result);
						tcpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								props.addSubproperties("tcpanel", (Properties)result);
								settings.setProperties("TestCenterPlugin", props)
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
	 * Load the properties.
	 * @param props
	 */
	public IFuture setProperties(Properties props)
	{
		double dl = props.getDoubleProperty("tcsplit_location");
		if(dl!=0)
		{
			tcpanel.setDividerLocation(dl);
			tcpanel.setResizeWeight(dl);
		}
		dl = props.getDoubleProperty("mainsplit_location");
		if(dl!=0)
		{
			((JSplitPane)getView()).setDividerLocation(dl);
			((JSplitPane)getView()).setResizeWeight(dl);
		}
		
		return IFuture.DONE;
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public IFuture getProperties()
	{
		Properties	props	= new Properties();
		props.addProperty(new Property("mainsplit_location", Double.toString(((JSplitPanel)getView()).getProportionalDividerLocation())));
		props.addProperty(new Property("tcsplit_location", Double.toString(tcpanel.getProportionalDividerLocation())));
		return new Future(props);
	}
	
	/**
	 *  Recursively get all leaf (sub)children of a node.
	 */
	protected IFuture	getLeafChildren(IFileNode node)
	{
		final Future	fut	= new Future();
		
		if(node.isDirectory())
		{
			node.getChildren().addResultListener(new SwingDelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					List	children	= (List)result;
					final	List	list	= new ArrayList();
					CounterResultListener	crl	= new CounterResultListener(children.size(), new SwingDelegationResultListener(fut)
					{
						public void customResultAvailable(Object result)
						{
							fut.setResult(list);
						}
					})
					{
						public void intermediateResultAvailable(Object result)
						{
							list.addAll((Collection)result);
						}
					};
					for(int i=0; i<children.size(); i++)
					{
						getLeafChildren((IFileNode)children.get(i)).addResultListener(crl);
					}
				}
			});
		}
		else
		{
			fut.setResult(Collections.singletonList(node));
		}
		
		return fut;
	}
	
	/**
	 *  Add testcases for a file or directory recursively.
	 *  @param node The file/dir node to start.
	 */
	protected void addTestcases(IFileNode node)
	{
		getLeafChildren(node).addResultListener(new SwingDefaultResultListener(mpanel)
		{
			public void customResultAvailable(Object result)
			{
				final List	leafs	= (List)result;
				final int[]	cnt	= new int[1];	
				for(int i=0; i<leafs.size(); i++)
				{
					IFileNode node = (IFileNode)leafs.get(i);
					ITreeNode base = node;
					while(base.getParent()!=null && base.getParent().getParent()!=null)
						base = base.getParent();
					final String	model	= node.getFilePath();
					
					createResourceIdentifier((IFileNode)base)
						.addResultListener(new SwingDefaultResultListener<IResourceIdentifier>(mpanel)
					{
						public void customResultAvailable(final IResourceIdentifier rid)
						{
							SRemoteGui.isTestcase(model, getJCC().getPlatformAccess(), rid)
								.addResultListener(new SwingDefaultResultListener(mpanel)
							{
								public void customResultAvailable(Object result)
								{
									cnt[0]++;
									String	text	= "Scanned file "+cnt[0]+" of "+leafs.size()+" ("+(cnt[0]*100/leafs.size())+"%): "+model;
									getJCC().setStatusText(text);
									if(((Boolean)result).booleanValue())
									{
										tcpanel.addTest(model, rid);
//										tcpanel.getTestList().addEntry(model);
									}
								}
							});
						}
					});
				}
			}
		});
	}

	
	/**
	 *  Remove testcases for a file or directory recusively.
	 *  @param node The file/dir node to start.
	 */
	protected void removeTestcases(final IFileNode node)
	{
		createResourceIdentifier(node).addResultListener(new SwingDefaultResultListener<IResourceIdentifier>()
		{
			public void customResultAvailable(IResourceIdentifier rid)
			{
				java.util.List nodes = SCollection.createArrayList();
				nodes.add(node);
				while(nodes.size()>0)
				{
					IFileNode n	= (IFileNode)nodes.remove(0);
					if(n.isDirectory())
					{
						for(int i=0; i<n.getChildCount(); i++)
						{
							nodes.add(n.getChild(i));
						}
					}
					else
					{
						String model = n.getFilePath();
//						tcpanel.getTestList().removeEntry(model);
						tcpanel.removeTest(model, rid);
					}
				}
			}
		});
	}
	
	/**
	 *  Add testcase.
	 */
	public final Action ADD_TESTCASE = new ToolTipAction("Add Testcase", icons.getIcon("add_agent"), "Add this file as testcase")
	{
		public void actionPerformed(ActionEvent e)
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			addTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			return node!=null && !node.isDirectory();
		}
	};
	
	/**
	 *  Add testcases.
	 */
	public final Action ADD_TESTCASES = new ToolTipAction("Add All Testcases", icons.getIcon("add_package"), "Add recursively contained files as testcases.")
	{
		public void actionPerformed(ActionEvent e)
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			if(node!=null)
				addTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			return node!=null && node.isDirectory();
		}
	};
	
	/**
	 *  Remove testcase.
	 */
	public final Action REMOVE_TESTCASE = new ToolTipAction("Remove Testcase", icons.getIcon("remove_agent"), "Remove this file as testcase")
	{
		public void actionPerformed(ActionEvent e)
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			removeTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			return node!=null && !node.isDirectory();
		}
	};
	
	/**
	 *  Remove testcases.
	 */
	public final Action REMOVE_TESTCASES = new ToolTipAction("Remove All Testcases", icons.getIcon("remove_package"), "Remove recursively contained files as testcases.")
	{
		public void actionPerformed(ActionEvent e)
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			if(node!=null)
				removeTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IFileNode node = (IFileNode)mpanel.getTree().getLastSelectedPathComponent();
			return node!=null && node.isDirectory();
		}
	};
	
//	/**
//	 *  Create a resource identifier.
//	 */
//	public IResourceIdentifier createResourceIdentifier(IFileNode base)
//	{
//		// Get the first child of selection path as url
////		TreePath selpath = mpanel.getTree().getSelectionModel().getSelectionPath();
////		Object tmp = selpath.getPathComponent(1);
//		Tuple2<IComponentIdentifier, URL> lid = null;
//		URL url = SUtil.toURL(base.getFilePath());
//		IComponentIdentifier root = mpanel.getExternalAccess().getComponentIdentifier().getRoot();
//		lid = new Tuple2<IComponentIdentifier, URL>(root, url);
//		// todo: construct global identifier
//		ResourceIdentifier rid = new ResourceIdentifier(lid, null);
//		return rid;
//	}
	
	/**
	 *  Create a resource identifier.
	 */
	public IFuture<IResourceIdentifier> createResourceIdentifier(IFileNode node)
	{
		// Get the first child of selection path as url
//		TreePath selpath = mpanel.getTree().getSelectionModel().getSelectionPath();
		ITreeNode root = node;
		while(root.getParent()!=null && root.getParent().getParent()!=null)
			root = root.getParent();
		final String filepath = ((IFileNode)root).getFilePath();
		
		return ModelTreePanel.createResourceIdentifier(jcc.getPlatformAccess(), filepath);
		
//		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
//		jcc.getPlatformAccess().getServiceProvider().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<ILibraryService, IResourceIdentifier>(ret)
//		{
//			public void customResultAvailable(ILibraryService ls)
//			{
//				// Must be done on remote site as SUtil.toURL() uses new File()
//				final URL url = SUtil.toURL(filepath);
//
//				ls.getResourceIdentifier(url).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
//			}
//		});
//		
//		return ret;
	}
}
