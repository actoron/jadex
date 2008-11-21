package jadex.tools.testcenter;

import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.SGUI;
import jadex.commons.collection.SCollection;
import jadex.tools.common.PopupBuilder;
import jadex.tools.common.ToolTipAction;
import jadex.tools.common.modeltree.DirNode;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.ModelExplorer;
import jadex.tools.common.modeltree.RootNode;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.starter.StarterNodeFunctionality;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.tree.TreeNode;

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
		"arrow_left", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/arrow_right.png"),
		"test_small", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_agent_testable.png"),
		"scanning_on",	SGUI.makeIcon(TestCenterPlugin.class, "/jadex/tools/common/images/new_agent_testcheckanim.gif"),
		"add_agent", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_add_agent_testable.png"),
		"add_package", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_add_package_testable.png"),
		"remove_agent", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_remove_agent_testable.png"),
		"remove_package", SGUI.makeIcon(TestCenterPlugin.class,	"/jadex/tools/common/images/new_remove_package_testable.png"),
//		"scanning_off",	SGUI.makeIcon(TestCenterPlugin.class, "/jadex/tools/common/images/new_agent_testable.png")
	});

	//-------- attributes --------

	/** The panel showing the classpath models. */
	protected ModelExplorer mpanel;

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
		return mpanel.createMenuBar();
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

		JLabel	refreshcomp	= new JLabel(icons.getIcon("scanning_on"));
		refreshcomp.setToolTipText("Loading/checking test cases.");
		mpanel = new ModelExplorer(getJCC(), new RootNode(new FileFilter()
		{
			public boolean accept(File pathname)
			{
//				return pathname.isDirectory() || SXML.isAgentFilename(pathname.getName());
				return pathname.isDirectory() || getJCC().getAgent().getPlatform()
					.getAgentFactory().isStartable(pathname.getName());
			}
		}), null, new TestCenterNodeFunctionality(getJCC())); // todo: popup for testcenter
		mpanel.setPopupBuilder(new PopupBuilder(new Object[]{mpanel.ADD_PATH, mpanel.REMOVE_PATH, mpanel.REFRESH,
			ADD_TESTCASE, ADD_TESTCASES, REMOVE_TESTCASE, REMOVE_TESTCASES}));
		// todo: hack, how can this be done better?
//		mpanel.TOGGLE_CHECKING.putValue(Action.NAME, "Auto find tests");
//		mpanel.TOGGLE_CHECKING.putValue(Action.SMALL_ICON, icons.get("test_small"));

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
						String model = ((FileNode)node).getFile().getAbsolutePath();
//						if(SXML.isAgentFilename(model))
						if(getJCC().getAgent().getPlatform()
							.getAgentFactory().isStartable(model))
						{
							tcpanel.getTestList().addEntry(model);
						}
						else
						{
							jcc.setStatusText("Only agents can be added as testcases.");
						}
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
	public void setProperties(Properties props)
	{
		Properties	mpanelprops	= props.getSubproperty("modelpanel");
		if(mpanelprops!=null)
			mpanel.setProperties(mpanelprops);
		Properties	tcpanelprops = props.getSubproperty("testspanel");
		if(tcpanelprops!=null)
			tcpanel.setProperties(tcpanelprops);

		if(props.getProperty("mainsplit.location")!=null);
			((JSplitPane)getView()).setDividerLocation(props.getIntProperty("mainsplit.location"));
		if(props.getProperty("tcsplit.location")!=null);
			tcpanel.setDividerLocation(props.getIntProperty("tcsplit.location"));
	}

	/**
	 * Save the properties.
	 * @param props
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		addSubproperties(props, "modelpanel", mpanel.getProperties());
		addSubproperties(props, "testspanel", tcpanel.getProperties());

		props.addProperty(new Property("mainsplit.location", Integer.toString(((JSplitPane)getView()).getDividerLocation())));
		props.addProperty(new Property("tcsplit.location", Integer.toString(tcpanel.getDividerLocation())));
		
		return props;
	}

	/**
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#reset()
	 */
	public void reset()
	{
		mpanel.reset();
		tcpanel.reset();
	}
	
	/**
	 *  Add testcases for a file or directory recusively.
	 *  @param node The file/dir node to start.
	 */
	protected void addTestcases(TreeNode node)
	{
		if(node instanceof DirNode)
		{
			java.util.List nodes = SCollection.createArrayList();
			nodes.add(node);
			while(nodes.size()>0)
			{
				TreeNode n = (TreeNode)nodes.remove(0);
				for(int j=0; j<n.getChildCount(); j++)
					nodes.add(n.getChildAt(j));
					
				if(n instanceof FileNode)
				{
					String model = ((FileNode)n).getFile().getAbsolutePath();
//					if(SXML.isAgentFilename(model) && ((FileNode)n).isValid())
					if(getJCC().getAgent().getPlatform()
						.getAgentFactory().isStartable(model) /* && ((FileNode)n).isValid() */)
					{
						tcpanel.getTestList().addEntry(model);
					}
				}
			}
		}
		else
		{
			String model = ((FileNode)node).getFile().getAbsolutePath();
//			if(SXML.isAgentFilename(model) && ((FileNode)node).isValid())
			if(getJCC().getAgent().getPlatform()
				.getAgentFactory().isStartable(model) /* && ((FileNode)node).isValid() */)
			{
				tcpanel.getTestList().addEntry(model);
			}
		}
	}
	
	/**
	 *  Remove testcases for a file or directory recusively.
	 *  @param node The file/dir node to start.
	 */
	protected void removeTestcases(TreeNode node)
	{
		if(node instanceof FileNode)
		{
			String model = ((FileNode)node).getFile().getAbsolutePath();
			tcpanel.getTestList().removeEntry(model);
		}
		else
		{
			java.util.List nodes = SCollection.createArrayList();
			nodes.add(node);
			while(nodes.size()>0)
			{
				TreeNode n = (TreeNode)nodes.remove(0);
				for(int j=0; j<n.getChildCount(); j++)
					nodes.add(n.getChildAt(j));
					
				if(n instanceof FileNode)
				{
					String model = ((FileNode)n).getFile().getAbsolutePath();
//					if(SXML.isAgentFilename(model) && ((FileNode)n).isValid())
					if(getJCC().getAgent().getPlatform()
						.getAgentFactory().isStartable(model) /*&& ((FileNode)n).isValid() */)
					{
						tcpanel.getTestList().removeEntry(model);
					}
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
			TreeNode node = (TreeNode)mpanel.getLastSelectedPathComponent();
			addTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			TreeNode node = (TreeNode)mpanel.getLastSelectedPathComponent();
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
			TreeNode node = (TreeNode)mpanel.getLastSelectedPathComponent();
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
			TreeNode node = (TreeNode)mpanel.getLastSelectedPathComponent();
			removeTestcases(node);
		}

		/**
		 *  Test if action is available in current context.
		 *  @return True, if available.
		 */
		public boolean isEnabled()
		{
			TreeNode node = (TreeNode)mpanel.getLastSelectedPathComponent();
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
			TreeNode node = (TreeNode)mpanel.getLastSelectedPathComponent();
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
}
