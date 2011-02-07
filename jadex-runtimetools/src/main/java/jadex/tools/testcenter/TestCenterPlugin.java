package jadex.tools.testcenter;

import jadex.base.SComponentFactory;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.ModelExplorerTreeModel;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
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

	/** The node functionality. */
	protected TestCenterNodeFunctionality	nof;

	/** The panel showing the classpath models. */
	protected ModelExplorer mpanel;

	/** The menu item for enabling/disabling agent model checking. */
	private JCheckBoxMenuItem	checkingmenu;
	
	/** The test center panel. */
	protected TestCenterPanel tcpanel;

	//-------- methods --------

	/**
	 * Return the model explorer.
	 */
	protected ModelExplorer	getModelExplorer()
	{
		return mpanel;
	}

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

		b = new JButton(ADD_TESTCASES);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret[4] = b;

		b = new JButton(REMOVE_TESTCASES);
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
	 *  Get the checking menu.
	 */
	protected JCheckBoxMenuItem getCheckingMenu()
	{
		return checkingmenu;
	}
	
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

		nof	= new TestCenterNodeFunctionality(this);
		mpanel = new ModelExplorer(getJCC().getExternalAccess(), nof);
		mpanel.setPopupBuilder(new PopupBuilder(new Object[]{mpanel.ADD_PATH, mpanel.REMOVE_PATH, mpanel.REFRESH,
			ADD_TESTCASE, ADD_TESTCASES, REMOVE_TESTCASE, REMOVE_TESTCASES}));

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
					Object	node = mpanel.getLastSelectedPathComponent();
					if(node instanceof FileNode)
					{
						final String model = ((FileNode)node).getRelativePath();
//						if(SXML.isAgentFilename(model))
						SComponentFactory.isStartable(getJCC().getExternalAccess(), model).addResultListener(new SwingDefaultResultListener(mpanel)
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
		JSplitPane mainpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainpanel.setOneTouchExpandable(true);
		mainpanel.setDividerLocation(200);

//		mainpanel.add(tp);
		mainpanel.add(new JScrollPane(mpanel));
		mainpanel.add(tcpanel);

		return mainpanel;
	}
	
	/**
	 * Load the properties.
	 * @param props
	 */
	public IFuture setProperties(Properties props)
	{
		Properties	mpanelprops	= props.getSubproperty("modelpanel");
		if(mpanelprops!=null)
			mpanel.setProperties(mpanelprops);
		Properties	tcpanelprops = props.getSubproperty("testspanel");
		if(tcpanelprops!=null)
			tcpanel.setProperties(tcpanelprops);

		if(props.getProperty("mainsplit_location")!=null);
			((JSplitPane)getView()).setDividerLocation(props.getIntProperty("mainsplit_location"));
		if(props.getProperty("tcsplit_location")!=null);
			tcpanel.setDividerLocation(props.getIntProperty("tcsplit_location"));

		checkingmenu.setSelected(props.getBooleanProperty("checking"));
		
		return new Future(null);
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public IFuture getProperties()
	{
		final Future ret = new Future();
		
		mpanel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Properties	props	= new Properties();
				addSubproperties(props, "modelpanel", (Properties)result);
				addSubproperties(props, "testspanel", tcpanel.getProperties());

				props.addProperty(new Property("mainsplit_location", Integer.toString(((JSplitPane)getView()).getDividerLocation())));
				props.addProperty(new Property("tcsplit_location", Integer.toString(tcpanel.getDividerLocation())));
				
				props.addProperty(new Property("checking", ""+checkingmenu.isSelected()));
				
				ret.setResult(props);
			}
		});
	
		return ret;
	}

	/**
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#reset()
	 */
	public void reset()
	{
		mpanel.reset();
		tcpanel.reset();
		this.checkingmenu.setSelected(true);	// Default: on
	}
	
	/**
	 *  Add testcases for a file or directory recusively.
	 *  @param node The file/dir node to start.
	 */
	protected void addTestcases(final IExplorerTreeNode node)
	{
		if(node instanceof DirNode)
		{
			java.util.List nodes = SCollection.createArrayList();
			nodes.add(node);
			while(nodes.size()>0)
			{
				final IExplorerTreeNode n = (IExplorerTreeNode)nodes.remove(0);
				List	lchildren	= nof.getChildren((FileNode)n);
				for(int j=0; lchildren!=null && j<lchildren.size(); j++)
					nodes.add(lchildren.get(j));
					
				if(n instanceof FileNode && !(n instanceof DirNode))
				{
					final String model = ((FileNode)n).getRelativePath();
					SComponentFactory.isStartable(getJCC().getExternalAccess(), model).addResultListener(new SwingDefaultResultListener(mpanel)
					{
						public void customResultAvailable(Object result)
						{
							if(((Boolean)result).booleanValue()
								&& nof.isTestcase((IExplorerTreeNode) n))
							{
								tcpanel.getTestList().addEntry(model);
							}
						}
					});
				}
			}
		}
		else
		{
			final String model = ((FileNode)node).getRelativePath();
//			if(SXML.isAgentFilename(model) && ((FileNode)node).isValid())
			SComponentFactory.isStartable(getJCC().getExternalAccess(), model).addResultListener(new SwingDefaultResultListener(mpanel)
			{
				public void customResultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue()
						&& nof.isTestcase((IExplorerTreeNode)node))
					{
						tcpanel.getTestList().addEntry(model);
					}
				}
			});
		}
	}
	
	/**
	 *  Remove testcases for a file or directory recusively.
	 *  @param node The file/dir node to start.
	 */
	protected void removeTestcases(IExplorerTreeNode node)
	{
		if(node instanceof FileNode)
		{
			String model = ((FileNode)node).getRelativePath();
			tcpanel.getTestList().removeEntry(model);
		}
		else
		{
			java.util.List nodes = SCollection.createArrayList();
			nodes.add(node);
			while(nodes.size()>0)
			{
				IExplorerTreeNode n = (IExplorerTreeNode)nodes.remove(0);
				List	lchildren	= nof.getChildren((FileNode)n);
				for(int j=0; lchildren!=null && j<lchildren.size(); j++)
					nodes.add(lchildren.get(j));
					
				if(n instanceof FileNode)
				{
					String model = ((FileNode)n).getRelativePath();
					tcpanel.getTestList().removeEntry(model);
				}
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
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			addTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			return node!=null && !(node instanceof DirNode) /*&& ((FileNode)node).isValid()*/;
		}
	};
	
	/**
	 *  Add testcases.
	 */
	public final Action ADD_TESTCASES = new ToolTipAction("Add All Testcases", icons.getIcon("add_package"), "Add recursively contained files as testcases.")
	{
		public void actionPerformed(ActionEvent e)
		{
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			if(node!=null)
				addTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			return mpanel.getLastSelectedPathComponent() instanceof DirNode;
		}
	};
	
	/**
	 *  Remove testcase.
	 */
	public final Action REMOVE_TESTCASE = new ToolTipAction("Remove Testcase", icons.getIcon("remove_agent"), "Remove this file as testcase")
	{
		public void actionPerformed(ActionEvent e)
		{
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			removeTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			return node!=null && !(node instanceof DirNode) /*&& ((FileNode)node).isValid() */;
		}
	};
	
	/**
	 *  Remove testcases.
	 */
	public final Action REMOVE_TESTCASES = new ToolTipAction("Remove All Testcases", icons.getIcon("remove_package"), "Remove recursively contained files as testcases.")
	{
		public void actionPerformed(ActionEvent e)
		{
			IExplorerTreeNode node = (IExplorerTreeNode)mpanel.getLastSelectedPathComponent();
			if(node!=null)
				removeTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			return mpanel.getLastSelectedPathComponent() instanceof DirNode;
		}
	};

	
	/**
	 *  The action for changing integrity checking settings.
	 */
	public final AbstractAction TOGGLE_CHECKING = new AbstractAction("Auto find tests", (Icon) icons.get("test_small"))
	{
		public void actionPerformed(ActionEvent e)
		{
			((ModelExplorerTreeModel)getModelExplorer().getModel())
				.fireTreeStructureChanged(getModelExplorer().getRootNode());
		}
	};
}
