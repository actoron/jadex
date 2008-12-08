package jadex.tools.introspector;

import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.SGUI;
import jadex.rules.tools.stateviewer.OAVTreeModel;
import jadex.tools.common.AgentTreeTable;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.ObjectCardLayout;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.TreeTableNodeType;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.common.plugin.IAgentListListener;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;


/**
 *  Introspector plugin allows to inspect beliefs, goals and plans of an agent
 *  and to debug the steps in the agent's agenda.
 */
public class IntrospectorPlugin extends AbstractJCCPlugin	 implements IAgentListListener
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"introspector", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"introspector_sel", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector_sel.png"),
		"introspect_agent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"close_introspector", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/close_introspector.png"),
		"agent_introspected", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent_introspected.png"),
		"introspector_empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/introspector_empty.png"),
		"show_state", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bulb2.png"),
		"show_rete", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bug_small.png"),
	});
	
	protected static final String REFRESH_KEY = "delay";
	
	/** Don't refresh. */
	protected static final Integer REFRESH0 = new Integer(0);
	
	/** Refresh every second */
	protected static final Integer REFRESH1 = new Integer(1000);

	/** Refresh every 5 seconds */
	protected static final Integer REFRESH5 = new Integer(5000);

	/** Refresh every 30 seconds */
	protected static final Integer REFRESH30 = new Integer(30000);

	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The agent tree table. */
	protected AgentTreeTable	agents;

	/** The detail panel. */
	protected JPanel	detail;

	/** The detail layout. */
	protected ObjectCardLayout	cards;
	
	/** The checkbox items for selecting default views. */
	protected JCheckBoxMenuItem[]	checkboxes;
	
	/** How long should the refresh process wait */
	protected Integer sleep = REFRESH5;
	
	/** Refresh settings . */
	protected JRadioButtonMenuItem[] refreshradio;

	//-------- constructors --------
	
	/**
	 *  Create a new introspector plugin.
	 */
	public IntrospectorPlugin()
	{
		this.checkboxes	= new JCheckBoxMenuItem[]
		{
			new JCheckBoxMenuItem("Show OAV State", icons.getIcon("show_state")),
			new JCheckBoxMenuItem("Show Rete Network", icons.getIcon("show_rete"))
		};
		
		AbstractAction action0 = new AbstractAction("Never")
		{
			public void actionPerformed(ActionEvent e)
			{
				sleep = (Integer) getValue(REFRESH_KEY);
				setTimerRefreshDelay();
			}
		};
		action0.putValue(REFRESH_KEY, REFRESH0);
		
		AbstractAction action1 = new AbstractAction("1 s")
		{
			public void actionPerformed(ActionEvent e)
			{
				sleep = (Integer) getValue(REFRESH_KEY);
				setTimerRefreshDelay();
			}
		};
		action1.putValue(REFRESH_KEY, REFRESH1);
		
		AbstractAction action5 = new AbstractAction("5 s")
		{
			public void actionPerformed(ActionEvent e)
			{
				sleep = (Integer) getValue(REFRESH_KEY);
				setTimerRefreshDelay();
			}
		};
		action5.putValue(REFRESH_KEY, REFRESH5);
		
		AbstractAction action30 = new AbstractAction("30 s")
		{
			public void actionPerformed(ActionEvent e)
			{
				sleep = (Integer) this.getValue(REFRESH_KEY);
				setTimerRefreshDelay();
			}
		};
		action30.putValue(REFRESH_KEY, REFRESH30);		
		
		this.refreshradio = new JRadioButtonMenuItem[]
		{
				new JRadioButtonMenuItem(action0),
				new JRadioButtonMenuItem(action1),
				new JRadioButtonMenuItem(action5),
				new JRadioButtonMenuItem(action30)
		};
	}
	
	/**
	 * Update all object inspection refresh timer for OAVTreeModel
	 */
	public void setTimerRefreshDelay()
	{
		OAVTreeModel.setRefreshDelay(sleep.intValue());
	}
	
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  Get plugin properties to be saved in a project.
	 */
	public Properties getProperties()
	{
		Properties	props	= new Properties();
		for(int i=0; i<checkboxes.length; i++)
		{
			props.addProperty(new Property(checkboxes[i].getText(), ""+checkboxes[i].isSelected()));
		}
		
		Properties ps = props.getSubproperty("agents");
		if(ps!=null)
			agents.setProperties(ps);
		
		return props;
	}
	
	/**
	 *  Set plugin properties loaded from a project.
	 */
	public void setProperties(Properties props)
	{
		for(int i=0; i<checkboxes.length; i++)
		{
			boolean	selected	= props.getBooleanProperty(checkboxes[i].getText());
			checkboxes[i].setSelected(selected);
		}
		
		addSubproperties(props, "agents", agents.getProperties());
	}
	
	/**
	 * @return "Introspector"
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Introspector";
	}

	/**
	 * @return the icon of introspector
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("introspector_sel"): icons.getIcon("introspector");
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JButton b1 = new JButton(START_INTROSPECTOR);
		b1.setBorder(null);
		b1.setToolTipText(b1.getText());
		b1.setText(null);
		b1.setEnabled(true);

		JButton b2 = new JButton(STOP_INTROSPECTOR);
		b2.setBorder(null);
		b2.setToolTipText(b2.getText());
		b2.setText(null);
		b2.setEnabled(true);
		
		return new JComponent[]{b1, b2};
	}
	
	/**
	 *  Create menu bar.
	 *  @return The menu bar.
	 */
	public JMenu[] createMenuBar()
	{
		JMenu	menu	= new JMenu("Default Options");
		for(int i=0; i<checkboxes.length; i++)
			menu.add(checkboxes[i]);
		
		// refresh menu
		ButtonGroup refreshgroup = new ButtonGroup();
		JMenu refreshmenu = new JMenu("Object Inspector");
		for (int i = 0; i < refreshradio.length; i++)
		{
			refreshgroup.add(refreshradio[i]);
			refreshmenu.add(refreshradio[i]);
			refreshradio[i].setSelected(sleep == refreshradio[i].getAction().getValue(REFRESH_KEY));
		}

		return new JMenu[]{menu,refreshmenu};
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		this.split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		split.setOneTouchExpandable(true);

		agents = new AgentTreeTable(getJCC().getAgent().getPlatform().getName());
		agents.setMinimumSize(new Dimension(0, 0));
		split.add(agents);
		agents.getTreetable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		agents.getTreetable().getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				JTree tree = agents.getTreetable().getTree();
				if(!e.getValueIsAdjusting() && !tree.isSelectionEmpty())
				{
					DefaultTreeTableNode node = (DefaultTreeTableNode)tree.getSelectionPath().getLastPathComponent();
					cards.show(node.getUserObject());
				}
			}
		});
		// Change agent node type to enable introspected icon for agents.
		agents.addNodeType(new TreeTableNodeType(AgentTreeTable.NODE_AGENT,
			new Icon[0], new String[]{"name", "address"}, new String[]{"Name", "Address"})
		{
			public Icon selectIcon(Object value)
			{
				Icon ret;
				IAMSAgentDescription ad = (IAMSAgentDescription)((DefaultTreeTableNode)value).getUserObject();
				if(cards.getComponent(ad)!=null)
				{
					ret = IntrospectorPlugin.icons.getIcon("agent_introspected");
				}
				else
				{
					ret = AgentTreeTable.icons.getIcon(AgentTreeTable.NODE_AGENT);
				}
				return ret;
			}
		});
		agents.getNodeType(AgentTreeTable.NODE_AGENT).addPopupAction(START_INTROSPECTOR);
		agents.getNodeType(AgentTreeTable.NODE_AGENT).addPopupAction(STOP_INTROSPECTOR);

		JLabel	emptylabel	= new JLabel("Select agents to activate the introspector",
			icons.getIcon("introspector_empty"), JLabel.CENTER);
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

		GuiProperties.setupHelp(split, "tools.introspector");

		agents.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		split.setDividerLocation(150);

		agents.getTreetable().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					if(START_INTROSPECTOR.isEnabled())
						START_INTROSPECTOR.actionPerformed(null);
					else if(STOP_INTROSPECTOR.isEnabled())
						STOP_INTROSPECTOR.actionPerformed(null);
				}

			}
		});

		jcc.addAgentListListener(this);
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				agents.adjustColumnWidths();
//			}
//		});

		return split;
	}
	
	/**
	 *  Called when an agent has changed its state (e.g. suspended).
	 */
	public void agentChanged(IAMSAgentDescription ad)
	{
	}
	
	/**
	 * @param ad
	 */
	public void agentDied(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				agents.removeAgent(ad);
				if(cards.isAvailable(ad))
				{
					ToolPanel intro = (ToolPanel)cards.getComponent(ad);
					System.err.println("Agent died: "+ad);
					intro.dispose();
					detail.remove(intro);
				}
			}
		});
	}

	/**
	 * @param ad
	 */
	public void agentBorn(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				// hack dont introspect the agent
				// if(!jcc.getAgent().getAgentIdentifier().equals(ad.getName()))
				// {
				agents.addAgent(ad);
				// }
			}
		});
	}

	final AbstractAction START_INTROSPECTOR	= new AbstractAction("Introspect Agent", icons.getIcon("introspect_agent"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;
			
			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable()
				.getTree().getSelectionPath().getLastPathComponent();
			IAMSAgentDescription desc = (IAMSAgentDescription)node.getUserObject();
			boolean[]	active	= new boolean[checkboxes.length];
			for(int i=0; i<checkboxes.length; i++)
				active[i]	= checkboxes[i].isSelected();
			ToolPanel	intro = new ToolPanel(getJCC().getAgent(), desc.getName());
			GuiProperties.setupHelp(intro, "tools.introspector");
			detail.add(intro, node.getUserObject());
			agents.updateAgent(desc);
			split.setCursor(Cursor.getDefaultCursor());
		}

		public boolean isEnabled()
		{
			boolean	ret	= false;
			TreePath	path	= agents.getTreetable().getTree().getSelectionPath();
			if(path!=null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				ret = node!=null && node.getUserObject() instanceof IAMSAgentDescription
					&& cards.getComponent(node.getUserObject())==null;
			}
			return ret;
		}
	};

	final AbstractAction	STOP_INTROSPECTOR	= new AbstractAction("Close Introspector", icons.getIcon("close_introspector"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;

			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable().getTree().getSelectionPath().getLastPathComponent();
			ToolPanel intro = (ToolPanel)cards.getComponent(node.getUserObject());
			intro.dispose();
			detail.remove(intro);
			agents.updateAgent((IAMSAgentDescription)node.getUserObject());
			split.setCursor(Cursor.getDefaultCursor());
		}

		public boolean isEnabled()
		{
			boolean	ret	= false;
			TreePath	path	= agents.getTreetable().getTree().getSelectionPath();
			if(path!=null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				ret = node!=null && node.getUserObject() instanceof IAMSAgentDescription
					&& cards.getComponent(node.getUserObject())!=null;
			}
			return ret;
		}
	};

	/**
	 * @return the help id of the perspective
	 * @see jadex.tools.common.plugin.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.introspectorv2";
	}
}
