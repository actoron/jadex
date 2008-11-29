package jadex.tools.ruleprofiler;

import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.commons.SGUI;
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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;


/**
 *  The rule profiler allows to browse through
 *  profiling information gathered in the rule system.
 */
public class RuleProfilerPlugin extends AbstractJCCPlugin implements IAgentListListener
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"profiler", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/ruleprofiler.png"),
		"profiler_sel", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/ruleprofiler_sel.png"),
		"profile_agent", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"close_profiler", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/close_introspector.png"),
		"agent_profiled", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent_introspected.png"),
		"profiler_empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/introspector_empty.png"),
	});

	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The agent tree table. */
	protected AgentTreeTable	agents;

	/** The detail panel. */
	protected JPanel	detail;

	/** The detail layout. */
	protected ObjectCardLayout	cards;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rule profiler plugin.
	 */
	public RuleProfilerPlugin()
	{
	}
	
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  @return The plugin name 
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Rule Profiler";
	}

	/**
	 *  @return The icon of plugin
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("profiler_sel"): icons.getIcon("profiler");
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JButton b1 = new JButton(START_PROFILER);
		b1.setBorder(null);
		b1.setToolTipText(b1.getText());
		b1.setText(null);
		b1.setEnabled(true);

		JButton b2 = new JButton(STOP_PROFILER);
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
		// Change agent node type to enable profiled icon for agents.
		agents.addNodeType(new TreeTableNodeType(AgentTreeTable.NODE_AGENT,
			new Icon[0], new String[]{"name", "address"}, new String[]{"Name", "Address"})
		{
			public Icon selectIcon(Object value)
			{
				Icon ret;
				IAMSAgentDescription ad = (IAMSAgentDescription)((DefaultTreeTableNode)value).getUserObject();
				if(cards.getComponent(ad)!=null)
				{
					ret = RuleProfilerPlugin.icons.getIcon("agent_profiled");
				}
				else
				{
					ret = AgentTreeTable.icons.getIcon(AgentTreeTable.NODE_AGENT);
				}
				return ret;
			}
		});
		agents.getNodeType(AgentTreeTable.NODE_AGENT).addPopupAction(START_PROFILER);
		agents.getNodeType(AgentTreeTable.NODE_AGENT).addPopupAction(STOP_PROFILER);

		JLabel	emptylabel	= new JLabel("Select agents to activate the profiler",
			icons.getIcon("profiler_empty"), JLabel.CENTER);
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

		agents.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		split.setDividerLocation(150);

		agents.getTreetable().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					if(START_PROFILER.isEnabled())
						START_PROFILER.actionPerformed(null);
					else if(STOP_PROFILER.isEnabled())
						STOP_PROFILER.actionPerformed(null);
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
					detail.remove(cards.getComponent(ad));
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

	/**
	 * @param ad
	 */
	public void agentChanged(final IAMSAgentDescription ad)
	{
		// nop?
		// Update components on awt thread.
		/*
		 * SwingUtilities.invokeLater(new Runnable() { public void run() {
		 * agents.addAgent(ad); } });
		 */
	}

	final AbstractAction	START_PROFILER	= new AbstractAction("Profile Agent", icons.getIcon("profile_agent"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;
			
			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable()
				.getTree().getSelectionPath().getLastPathComponent();
			IAMSAgentDescription desc = (IAMSAgentDescription)node.getUserObject();
			RuleProfilerPanel	panel = new RuleProfilerPanel(getJCC().getAgent(), desc.getName());
			GuiProperties.setupHelp(panel, getHelpID());
			detail.add(panel, node.getUserObject());
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

	final AbstractAction	STOP_PROFILER	= new AbstractAction("Close Profiler", icons.getIcon("close_profiler"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;

			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable().getTree().getSelectionPath().getLastPathComponent();
			RuleProfilerPanel intro = (RuleProfilerPanel)cards.getComponent(node.getUserObject());			
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
		return "tools.ruleprofiler";
	}
}
