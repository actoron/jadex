package jadex.tools.dfbrowser;

import jadex.base.fipa.IDF;
import jadex.commons.SGUI;
import jadex.service.IService;
import jadex.tools.common.CombiIcon;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.ObjectCardLayout;
import jadex.tools.common.componenttree.ComponentTreeNode;
import jadex.tools.common.componenttree.ComponentTreePanel;
import jadex.tools.common.componenttree.IComponentTreeNode;
import jadex.tools.common.componenttree.INodeHandler;
import jadex.tools.common.componenttree.INodeListener;
import jadex.tools.common.componenttree.ServiceNode;
import jadex.tools.common.plugin.AbstractJCCPlugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;


/**
 *  The df browser allows to browse through
 *  contents of a DF service.
 */
public class DFBrowserPlugin extends AbstractJCCPlugin
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"dfbrowser", SGUI.makeIcon(DFBrowserPlugin.class, "/jadex/tools/common/images/new_dfbrowser.png"), 
		"dfbrowser_sel", SGUI.makeIcon(DFBrowserPlugin.class, "/jadex/tools/common/images/new_dfbrowser_sel.png"), 
		"open_dfbrowser", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_introspector.png"),
		"close_dfbrowser", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/close_introspector.png"),
		"dfbrowser_empty", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/introspector_empty.png"),
		"component_debugged", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_introspected.png"),
		"stop_debugger", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/overlay_notintrospected.png")
	});

	//-------- attributes --------

	/** The split panel. */
	protected JSplitPane	split;

	/** The agent tree table. */
	protected ComponentTreePanel	comptree;

	/** The detail panel. */
	protected JPanel	detail;

	/** The detail layout. */
	protected ObjectCardLayout	cards;
	
	/** Flag if the first panel is still to be opened. */
	protected boolean	first;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rule dfbrowser plugin.
	 */
	public DFBrowserPlugin()
	{
		first	= true;
	}
	
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  @return The plugin name 
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "DF Browser";
	}

	/**
	 *  @return The icon of plugin
	 *  @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("dfbrowser_sel"): icons.getIcon("dfbrowser");
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JButton b1 = new JButton(START_DFBROWSER);
		b1.setBorder(null);
		b1.setToolTipText(b1.getText());
		b1.setText(null);
		b1.setEnabled(true);

		JButton b2 = new JButton(STOP_DFBROWSER);
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

		comptree = new ComponentTreePanel(getJCC().getServiceContainer());
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
					if(node instanceof ComponentTreeNode)
					{
						cards.show(((ComponentTreeNode)node).getDescription());
					}
				}
			}
		});
		
		comptree.addNodeHandler(new INodeHandler()
		{
			public Action[] getPopupActions(IComponentTreeNode[] nodes)
			{
				Action[]	ret	= null;
				
				boolean	alldf	= true;
				for(int i=0; alldf && i<nodes.length; i++)
				{
					alldf	= nodes[i] instanceof ServiceNode && IDF.class.equals(((ServiceNode)nodes[i]).getService().getServiceIdentifier().getServiceType());
				}
				
				if(alldf)
				{
					boolean	allob	= true;
					for(int i=0; allob && i<nodes.length; i++)
					{
						allob	= cards.getComponent(((ServiceNode)nodes[i]).getService().getServiceIdentifier())!=null;
					}
					boolean	allig	= true;
					for(int i=0; allig && i<nodes.length; i++)
					{
						allig	= cards.getComponent(((ServiceNode)nodes[i]).getService().getServiceIdentifier())==null;
					}
					
					// Todo: Large icons for popup actions?
					if(allig)
					{
						Icon	base	= nodes[0].getIcon();
						Action	a	= new AbstractAction((String)START_DFBROWSER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("component_debugged")}) : (Icon)START_DFBROWSER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								START_DFBROWSER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
					else if(allob)
					{
						Icon	base	= nodes[0].getIcon();
						Action	a	= new AbstractAction((String)STOP_DFBROWSER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("stop_debugger")}) : (Icon)STOP_DFBROWSER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								STOP_DFBROWSER.actionPerformed(e);
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
				if(node instanceof ServiceNode)
				{
					if(cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier())!=null)
					{
						ret = icons.getIcon("component_debugged");
					}
				}
				return ret;
			}
			
			public Action getDefaultAction(IComponentTreeNode node)
			{
				Action	a	= null;
				if(node instanceof ServiceNode)
				{
					if(cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier())!=null)
					{
						a	= STOP_DFBROWSER;
					}
					else if(IDF.class.equals(((ServiceNode)node).getService().getServiceIdentifier().getServiceType()))
					{
						a	= START_DFBROWSER;
					}
				}
				return a;
			}
		});

		JLabel	emptylabel	= new JLabel("Select DF services to activate the DF Browser",
			icons.getIcon("dfbrowser_empty"), JLabel.CENTER);
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

		split.setDividerLocation(150);
		
		// Listener to remove panels, when services vanish!
		comptree.getModel().addNodeListener(new INodeListener()
		{
			public void nodeRemoved(IComponentTreeNode node)
			{
				if(node instanceof ServiceNode && cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier())!=null)
				{
					detail.remove(cards.getComponent(((ServiceNode)node).getService().getServiceIdentifier()));
				}
			}
			
			public void nodeAdded(IComponentTreeNode node)
			{
				if(first && node instanceof ServiceNode && IDF.class.equals(((ServiceNode)node).getService().getServiceIdentifier().getServiceType()))
				{
					first	= false;
					final List	path	= new LinkedList();
					while(node!=null)
					{
						path.add(0, node);
						node	= node.getParent();
					}

					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							comptree.getTree().setSelectionPath(new TreePath(path.toArray()));
							comptree.getTree().expandPath(new TreePath(path.subList(0, path.size()-1).toArray()));
							START_DFBROWSER.actionPerformed(new ActionEvent(comptree.getTree(), 0, null));
						}
					});
				}
			}
		});

		return split;
	}
		
	final AbstractAction	START_DFBROWSER	= new AbstractAction("Open DF browser", icons.getIcon("profile_agent"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof ServiceNode
					&& IDF.class.equals(((ServiceNode)paths[i].getLastPathComponent()).getService().getServiceIdentifier().getServiceType()))
				{
					ServiceNode node = (ServiceNode)paths[i].getLastPathComponent();
					IService service = node.getService();
					DFBrowserPanel	panel = new DFBrowserPanel((IDF)service);
					GuiProperties.setupHelp(panel, getHelpID());
					detail.add(panel, service.getServiceIdentifier());
					comptree.getModel().fireNodeChanged(node);
					first	= false;
				}
			}
		}
	};

	final AbstractAction	STOP_DFBROWSER	= new AbstractAction("Close DF browser", icons.getIcon("close_dfbrowser"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof ServiceNode
					&& IDF.class.equals(((ServiceNode)paths[i].getLastPathComponent()).getService().getServiceIdentifier().getServiceType()))
				{
					ServiceNode node = (ServiceNode)paths[i].getLastPathComponent();
					IService service = node.getService();
					Component panel = cards.getComponent(service.getServiceIdentifier());			
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
		return "tools.ruledfbrowser";
	}
}
