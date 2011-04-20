package jadex.tools.testcenter;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.base.gui.modeltree.RemovePathAction;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.collection.SCollection;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

		mpanel = new ModelTreePanel(getJCC().getPlatformAccess(), !getJCC().getJCCAccess().getComponentIdentifier().getPlatformName()
			.equals(getJCC().getPlatformAccess().getComponentIdentifier().getPlatformName()));
//		mpanel.setPopupBuilder(new PopupBuilder(new Object[]{mpanel.ADD_PATH, mpanel.REMOVE_PATH, mpanel.REFRESH,
//			ADD_TESTCASE, ADD_TESTCASES, REMOVE_TESTCASE, REMOVE_TESTCASES}));

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
		
		mpanel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					//TreePath	path	= mpanel.getPathForLocation(e.getX(), e.getY());
					Object	node = mpanel.getTree().getLastSelectedPathComponent();
					if(node instanceof FileNode)
					{
						final String model = ((FileNode)node).getRelativePath();
						STestCenter.isTestcase(model, getJCC().getPlatformAccess())
							.addResultListener(new SwingDefaultResultListener(mpanel)
						{
							public void customResultAvailable(Object result)
							{
								if(((Boolean)result).booleanValue())
									tcpanel.getTestList().addEntry(model);
								else
									jcc.setStatusText("Only agents can be added as testcases.");
							}
						});
					}
				}
			}
		});

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
		SServiceProvider.getService(getJCC().getPlatformAccess().getServiceProvider(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) throws Exception
			{
				ISettingsService	settings	= (ISettingsService)result;
				settings.getProperties("TestCenterPlugin")
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
		SServiceProvider.getService(getJCC().getPlatformAccess().getServiceProvider(), ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) throws Exception
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
						public void customResultAvailable(Object result) throws Exception
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
					final String	model	= ((IFileNode)leafs.get(i)).getFileName();
					STestCenter.isTestcase(model, getJCC().getPlatformAccess()).addResultListener(new SwingDefaultResultListener(mpanel)
					{
						public void customResultAvailable(Object result)
						{
							cnt[0]++;
							String	text	= "Scanned file "+cnt[0]+" of "+leafs.size()+" ("+(cnt[0]*100/leafs.size())+"%): "+model;
							getJCC().setStatusText(text);
							if(((Boolean)result).booleanValue())
							{
								tcpanel.getTestList().addEntry(model);
							}
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
	protected void removeTestcases(IFileNode node)
	{
		java.util.List nodes = SCollection.createArrayList();
		nodes.add(node);
		while(nodes.size()>0)
		{
			node	= (IFileNode)nodes.remove(0);
			if(node.isDirectory())
			{
				for(int i=0; i<node.getChildCount(); i++)
				{
					nodes.add(node.getChild(i));
				}
			}
			else
			{
				String model = node.getFileName();
				tcpanel.getTestList().removeEntry(model);
			}
		}
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
}
