package jadex.tools.debugger;

import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.componenttree.IComponentTreeNode;
import jadex.base.gui.componenttree.INodeHandler;
import jadex.base.gui.componenttree.INodeListener;
import jadex.bridge.IComponentDescription;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.ObjectCardLayout;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.help.SHelp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
		"debugger", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/new_introspector.png"),
		"debugger_sel", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/new_introspector_sel.png"),
		"debug_component", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/new_introspector.png"),
		"close_debugger", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/close_introspector.png"),
		"component_debugged", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/overlay_introspected.png"),
		"stop_debugger", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/overlay_notintrospected.png"),
		"debugger_empty", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/introspector_empty.png")
	});
	
	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The component tree table. */
	protected ComponentTreePanel	comptree;

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
		return props;
	}
	
	/**
	 *  Set plugin properties loaded from a project.
	 */
	public void setProperties(Properties props)
	{
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

		comptree = new ComponentTreePanel(getJCC().getServiceProvider());
		comptree.setMinimumSize(new Dimension(0, 0));
		split.add(comptree);
		comptree.getTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				JTree tree = comptree.getTree();
				if(tree.getSelectionPath()!=null)
				{
					Object node = tree.getSelectionPath().getLastPathComponent();
					if(node instanceof IActiveComponentTreeNode)
					{
						cards.show(((IActiveComponentTreeNode)node).getDescription());
					}
				}
			}
		});
		
		comptree.addNodeHandler(new INodeHandler()
		{
			public Action[] getPopupActions(IComponentTreeNode[] nodes)
			{
				Action[]	ret	= null;
				
				boolean	allcomp	= true;
				for(int i=0; allcomp && i<nodes.length; i++)
				{
					allcomp	= nodes[i] instanceof IActiveComponentTreeNode;
				}
				
				if(allcomp)
				{
					boolean	allob	= true;
					for(int i=0; allob && i<nodes.length; i++)
					{
						allob	= cards.getComponent(((IActiveComponentTreeNode)nodes[i]).getDescription())!=null;
					}
					boolean	allig	= true;
					for(int i=0; allig && i<nodes.length; i++)
					{
						allig	= cards.getComponent(((IActiveComponentTreeNode)nodes[i]).getDescription())==null;
					}
					
					// Todo: Large icons for popup actions?
					if(allig)
					{
						Icon	base	= nodes[0].getIcon();
						Action	a	= new AbstractAction((String)START_DEBUGGER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("component_debugged")}) : (Icon)START_DEBUGGER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								START_DEBUGGER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
					else if(allob)
					{
						Icon	base	= nodes[0].getIcon();
						Action	a	= new AbstractAction((String)STOP_DEBUGGER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("stop_debugger")}) : (Icon)STOP_DEBUGGER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								STOP_DEBUGGER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
				}
				
				return ret;
			}
			
			public Icon getOverlay(IComponentTreeNode node)
			{
				Icon ret	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentDescription ad = ((IActiveComponentTreeNode)node).getDescription();
					if(cards.getComponent(ad)!=null)
					{
						ret = DebuggerPlugin.icons.getIcon("component_debugged");
					}
				}
				return ret;
			}
			
			public Action getDefaultAction(IComponentTreeNode node)
			{
				Action	a	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					if(cards.getComponent(((IActiveComponentTreeNode)node).getDescription())!=null)
					{
						a	= STOP_DEBUGGER;
					}
					else
					{
						a	= START_DEBUGGER;
					}
				}
				return a;
			}
		});
		
		comptree.getModel().addNodeListener(new INodeListener()
		{
			public void nodeRemoved(IComponentTreeNode node)
			{
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentDescription desc = ((IActiveComponentTreeNode)node).getDescription();
					if(cards.getComponent(desc)!=null)
					{
						DebuggerMainPanel panel = (DebuggerMainPanel)cards.getComponent(desc);
						panel.dispose();
						detail.remove(panel);
					}
				}
			}
			
			public void nodeAdded(IComponentTreeNode node)
			{
			}
		});

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

		SHelp.setupHelp(split, "tools.debugger");

		split.setDividerLocation(150);

		return split;
	}
	
	final AbstractAction START_DEBUGGER	= new AbstractAction("Debug Component", icons.getIcon("debug_component"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode)
				{
					IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					IComponentDescription desc = node.getDescription();
					DebuggerMainPanel	panel = new DebuggerMainPanel(getJCC(), desc);
					SHelp.setupHelp(panel, "tools.debugger");
					detail.add(panel, node.getDescription());
					comptree.getModel().fireNodeChanged(node);
				}
			}
		}
	};

	final AbstractAction	STOP_DEBUGGER	= new AbstractAction("Close Debugger", icons.getIcon("close_debugger"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode)
				{
					IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					IComponentDescription desc = node.getDescription();
					DebuggerMainPanel panel = (DebuggerMainPanel)cards.getComponent(desc);
					panel.dispose();
					detail.remove(panel);
					comptree.getModel().fireNodeChanged(node);
				}
			}
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
