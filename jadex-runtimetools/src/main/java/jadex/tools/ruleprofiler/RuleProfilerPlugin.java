package jadex.tools.ruleprofiler;

import jadex.bdi.interpreter.BDIAgentFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentListener;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.tools.common.CombiIcon;
import jadex.tools.common.ComponentTreeTable;
import jadex.tools.common.ComponentTreeTableNodeType;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.ObjectCardLayout;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.jcc.AgentControlCenter;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

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
public class RuleProfilerPlugin extends AbstractJCCPlugin	implements IComponentListener
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
		"agent_profiled", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_introspected.png"),
		"profiler_empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/introspector_empty.png"),
	});

	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The agent tree table. */
	protected ComponentTreeTable	agents;

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

		agents = new ComponentTreeTable(((IServiceContainer)getJCC().getServiceContainer()));
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
		agents.addNodeType(new ComponentTreeTableNodeType(getJCC().getServiceContainer())
		{
			public Icon selectIcon(Object value)
			{
				Icon ret	= super.selectIcon(value);

				Icon	overlay	= null;
				IComponentDescription ad = (IComponentDescription)((DefaultTreeTableNode)value).getUserObject();
				if(cards.getComponent(ad)!=null)
				{
					overlay = RuleProfilerPlugin.icons.getIcon("agent_profiled");
				}
				
				if(ret!=null && overlay!=null)
				{
					ret	= new CombiIcon(new Icon[]{ret, overlay});
				}
				else if(overlay!=null)
				{
					ret	= overlay;
				}

				return ret;
			}
		});
		agents.getNodeType(ComponentTreeTable.NODE_COMPONENT).addPopupAction(START_PROFILER);
		agents.getNodeType(ComponentTreeTable.NODE_COMPONENT).addPopupAction(STOP_PROFILER);

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

//		jcc.addAgentListListener(this);
		// todo: ?! is this ok?
		
		IComponentManagementService ces = (IComponentManagementService)jcc.getServiceContainer().getService(IComponentManagementService.class);
		ces.getComponentDescriptions(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentDescription[] res = (IComponentDescription[])result;
				for(int i=0; i<res.length; i++)
				{
					if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(res[i].getType()))
						componentAdded(res[i]);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
		ces.addComponentListener(null, this);
		
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
	 *  Called when an component has died.
	 *  @param ad The component description.
	 */
	public void componentRemoved(final IComponentDescription ad, Map results)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(ad.getType()))
				{
					agents.removeComponent(ad);
					if(cards.isAvailable(ad))
					{
						detail.remove(cards.getComponent(ad));
					}
				}
			}
		});
	}

	/**
	 *  Called when an component is born.
	 *  @param ad the component description.
	 */
	public void componentAdded(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(ad.getType()))
					agents.addComponent(ad);
			}
		});
	}
	
	/**
	 *  Called when an component changed.
	 *  @param ad the component description.
	 */
	public void componentChanged(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(ad.getType()))
					agents.updateComponent(ad);
			}
		});
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
			IComponentDescription desc = (IComponentDescription)node.getUserObject();
			RuleProfilerPanel	panel = new RuleProfilerPanel(((AgentControlCenter)getJCC()).getAgent(), desc.getName());
			GuiProperties.setupHelp(panel, getHelpID());
			detail.add(panel, node.getUserObject());
			agents.updateComponent(desc);
			split.setCursor(Cursor.getDefaultCursor());
		}

		public boolean isEnabled()
		{
			boolean	ret	= false;
			TreePath	path	= agents.getTreetable().getTree().getSelectionPath();
			if(path!=null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				ret = node!=null && node.getUserObject() instanceof IComponentDescription
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
			agents.updateComponent((IComponentDescription)node.getUserObject());
			split.setCursor(Cursor.getDefaultCursor());
		}

		public boolean isEnabled()
		{
			boolean	ret	= false;
			TreePath	path	= agents.getTreetable().getTree().getSelectionPath();
			if(path!=null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				ret = node!=null && node.getUserObject() instanceof IComponentDescription
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
