package jadex.tools.debugger;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentListener;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.tools.common.CombiIcon;
import jadex.tools.common.ComponentTreeTable;
import jadex.tools.common.ComponentTreeTableNodeType;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.ObjectCardLayout;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.plugin.AbstractJCCPlugin;

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
import javax.swing.JMenu;
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
 *  The debugger provides generic support for stepping and
 *  breakpointing components. Kernel-specific panels
 *  can be added through configuration options.
 */
public class DebuggerPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"debugger", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"debugger_sel", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector_sel.png"),
		"debug_component", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"close_debugger", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/close_introspector.png"),
		"component_debugged", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_introspected.png"),
		"debugger_empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/introspector_empty.png")
	});
	
	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The component tree table. */
	protected ComponentTreeTable	components;

	/** The detail panel. */
	protected JPanel	detail;

	/** The detail layout. */
	protected ObjectCardLayout	cards;
	
	//-------- constructors --------
	
	/**
	 *  Create a new debugger plugin.
	 */
	public DebuggerPlugin()
	{
	}
		
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  Get plugin properties to be saved in a project.
	 */
	public Properties getProperties()
	{
		Properties	props	= new Properties();
		addSubproperties(props, "components", components.getProperties());	
		return props;
	}
	
	/**
	 *  Set plugin properties loaded from a project.
	 */
	public void setProperties(Properties props)
	{
		Properties ps = props.getSubproperty("components");
		if(ps!=null)
			components.setProperties(ps);
	}
	
	/**
	 *  Return the unique name of this plugin.
	 *  This method may be called before init().
	 *  Used e.g. to store properties of each plugin.
	 */
	public String getName()
	{
		return "Debugger";
	}

	/**
	 *  Return the icon representing this plugin.
	 *  This method may be called before init().
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("debugger_sel"): icons.getIcon("debugger");
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JButton b1 = new JButton(START_DEBUGGER);
		b1.setBorder(null);
		b1.setToolTipText(b1.getText());
		b1.setText(null);
		b1.setEnabled(true);

		JButton b2 = new JButton(STOP_DEBUGGER);
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
		return null;
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		this.split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		split.setOneTouchExpandable(true);

		components = new ComponentTreeTable(getJCC().getServiceContainer());
		components.setMinimumSize(new Dimension(0, 0));
		split.add(components);
		components.getTreetable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		components.getTreetable().getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				JTree tree = components.getTreetable().getTree();
				if(!e.getValueIsAdjusting() && !tree.isSelectionEmpty())
				{
					DefaultTreeTableNode node = (DefaultTreeTableNode)tree.getSelectionPath().getLastPathComponent();
					cards.show(node.getUserObject());
				}
			}
		});
		// Change component node type to enable debugged icon for components.
		components.addNodeType(new ComponentTreeTableNodeType(getJCC().getServiceContainer())
		{
			public Icon selectIcon(Object value)
			{
				Icon ret	= super.selectIcon(value);

				Icon	overlay	= null;
				IComponentDescription ad = (IComponentDescription)((DefaultTreeTableNode)value).getUserObject();
				if(cards.getComponent(ad)!=null)
				{
					overlay = DebuggerPlugin.icons.getIcon("component_debugged");
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
		components.getNodeType(ComponentTreeTable.NODE_COMPONENT).addPopupAction(START_DEBUGGER);
		components.getNodeType(ComponentTreeTable.NODE_COMPONENT).addPopupAction(STOP_DEBUGGER);

		JLabel	emptylabel	= new JLabel("Select components to activate the debugger",
			icons.getIcon("debugger_empty"), JLabel.CENTER);
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

		GuiProperties.setupHelp(split, "tools.debugger");

		components.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		split.setDividerLocation(150);

		components.getTreetable().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					if(START_DEBUGGER.isEnabled())
						START_DEBUGGER.actionPerformed(null);
					else if(STOP_DEBUGGER.isEnabled())
						STOP_DEBUGGER.actionPerformed(null);
				}

			}
		});

		IComponentManagementService ces = (IComponentManagementService)jcc.getServiceContainer().getService(IComponentManagementService.class);
		IFuture ret = ces.getComponentDescriptions();
		ret.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentDescription[] res = (IComponentDescription[])result;
				for(int i=0; i<res.length; i++)
					agentBorn(res[i]);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
		ces.addComponentListener(null, new IComponentListener()
		{
			public void componentRemoved(IComponentDescription desc, Map results)
			{
				agentDied(desc);
			}
			
			public void componentAdded(IComponentDescription desc)
			{
				agentBorn(desc);
			}

			public void componentChanged(IComponentDescription desc)
			{
			}
		});
		
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				components.adjustColumnWidths();
//			}
//		});

		return split;
	}
	
	/**
	 *  Called when an agent has changed its state (e.g. suspended).
	 */
	public void agentChanged(IComponentDescription ad)
	{
	}
	
	/**
	 * @param ad
	 */
	public void agentDied(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				components.removeComponent(ad);
				if(cards.isAvailable(ad))
				{
					DebuggerMainPanel	panel	= (DebuggerMainPanel)cards.getComponent(ad);
//					System.err.println("Agent died: "+ad);
					panel.dispose();
					detail.remove(panel);
				}
			}
		});
	}

	/**
	 * @param ad
	 */
	public void agentBorn(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				components.addComponent(ad);
			}
		});
	}

	final AbstractAction START_DEBUGGER	= new AbstractAction("Debug Component", icons.getIcon("debug_component"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;
			
			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)components.getTreetable()
				.getTree().getSelectionPath().getLastPathComponent();
			IComponentDescription desc = (IComponentDescription)node.getUserObject();
			DebuggerMainPanel	panel = new DebuggerMainPanel(getJCC(), desc);
			GuiProperties.setupHelp(panel, "tools.debugger");
			detail.add(panel, node.getUserObject());
			components.updateComponent(desc);
			split.setCursor(Cursor.getDefaultCursor());
		}

		public boolean isEnabled()
		{
			boolean	ret	= false;
			TreePath	path	= components.getTreetable().getTree().getSelectionPath();
			if(path!=null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				ret = node!=null && node.getUserObject() instanceof IComponentDescription
					&& cards.getComponent(node.getUserObject())==null;
			}
			return ret;
		}
	};

	final AbstractAction	STOP_DEBUGGER	= new AbstractAction("Close Debugger", icons.getIcon("close_debugger"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;

			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)components.getTreetable().getTree().getSelectionPath().getLastPathComponent();
			DebuggerMainPanel panel = (DebuggerMainPanel)cards.getComponent(node.getUserObject());
			panel.dispose();
			detail.remove(panel);
			components.updateComponent((IComponentDescription)node.getUserObject());
			split.setCursor(Cursor.getDefaultCursor());
		}

		public boolean isEnabled()
		{
			boolean	ret	= false;
			TreePath	path	= components.getTreetable().getTree().getSelectionPath();
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
		return "tools.debugger";
	}
}
