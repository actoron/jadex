package jadex.tools.ruleprofiler;

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bdi.BDIAgentFactory;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Map;

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
 *  The rule profiler allows to browse through
 *  profiling information gathered in the rule system.
 */
public class RuleProfilerPlugin extends AbstractJCCPlugin	implements ICMSComponentListener
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"profiler", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/ruleprofiler.png"),
		"profiler_sel", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/ruleprofiler_sel.png"),
		"profile_agent", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/new_introspector.png"),
		"close_profiler", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/close_introspector.png"),
		"profiler_empty", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/introspector_empty.png"),
		"component_debugged", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/overlay_introspected.png"),
		"stop_debugger", SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/tools/ruleprofiler/images/overlay_notintrospected.png")
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
	 *  @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Rule Profiler";
	}

	/**
	 *  @return The icon of plugin
	 *  @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
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

		comptree = new ComponentTreePanel(getJCC().getPlatformAccess(), getJCC().getCMSHandler(), getJCC().getPropertyHandler(), getJCC().getIconCache());
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
		
		comptree.addNodeHandler(new ShowRemoteControlCenterHandler(getJCC(), getView()));
		comptree.addNodeHandler(new ISwingNodeHandler()
		{
			public Action[] getPopupActions(ISwingTreeNode[] nodes)
			{
				Action[]	ret	= null;
				
				boolean	allbdi	= true;
				for(int i=0; allbdi && i<nodes.length; i++)
				{
					allbdi	= nodes[i] instanceof IActiveComponentTreeNode
						&& BDIAgentFactory.FILETYPE_BDIAGENT.equals(((IActiveComponentTreeNode)nodes[i]).getDescription().getType());
				}
				
				if(allbdi)
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
						Icon	base	= nodes[0].getSwingIcon();
						Action	a	= new AbstractAction((String)START_PROFILER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("component_debugged")}) : (Icon)START_PROFILER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								START_PROFILER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
					else if(allob)
					{
						Icon	base	= nodes[0].getSwingIcon();
						Action	a	= new AbstractAction((String)STOP_PROFILER.getValue(Action.NAME),
							base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("stop_debugger")}) : (Icon)STOP_PROFILER.getValue(Action.SMALL_ICON))
						{
							public void actionPerformed(ActionEvent e)
							{
								STOP_PROFILER.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
				}
				
				return ret;
			}
			
			public byte[] getOverlay(ITreeNode node)
			{
				return null;
			}


			public Icon getSwingOverlay(ISwingTreeNode node)
			{
				Icon ret	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentDescription ad = ((IActiveComponentTreeNode)node).getDescription();
					if(cards.getComponent(ad)!=null)
					{
						ret = icons.getIcon("component_debugged");
					}
				}
				return ret;
			}
			
			public Action getDefaultAction(ISwingTreeNode node)
			{
				Action	a	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					if(cards.getComponent(((IActiveComponentTreeNode)node).getDescription())!=null)
					{
						a	= STOP_PROFILER;
					}
					else if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(((IActiveComponentTreeNode)node).getDescription().getType()))
					{
						a	= START_PROFILER;
					}
				}
				return a;
			}
		});

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

//		SHelp.setupHelp(split, getHelpID());

		split.setDividerLocation(150);

		SServiceProvider.getServiceUpwards(jcc.getPlatformAccess().getServiceProvider(),
			IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(comptree)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.addComponentListener(null, RuleProfilerPlugin.this);
				cms.getComponentDescriptions().addResultListener(new SwingDefaultResultListener(comptree)
				{
					public void customResultAvailable(Object result)
					{
						IComponentDescription[] res = (IComponentDescription[])result;
						for(int i=0; i<res.length; i++)
						{
							if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(res[i].getType()))
								componentAdded(res[i]);
						}
					}
				});
			}
		});
		
		
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
	public IFuture<Void> componentRemoved(final IComponentDescription ad, Map results)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(BDIAgentFactory.FILETYPE_BDIAGENT.equals(ad.getType()))
				{
					if(cards.isAvailable(ad))
					{
						detail.remove(cards.getComponent(ad));
					}
				}
			}
		});
		return IFuture.DONE;
	}

	/**
	 *  Called when an component is born.
	 *  @param ad the component description.
	 */
	public IFuture<Void> componentAdded(final IComponentDescription ad)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Called when an component changed.
	 *  @param ad the component description.
	 */
	public IFuture<Void> componentChanged(final IComponentDescription ad)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		if(comptree!=null)
			comptree.dispose();
		return IFuture.DONE;
	}
	
	final AbstractAction	START_PROFILER	= new AbstractAction("Profile Agent", icons.getIcon("profile_agent"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode
					&& BDIAgentFactory.FILETYPE_BDIAGENT.equals(((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getType()))
				{
					IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					IComponentDescription desc = node.getDescription();
					RuleProfilerPanel	panel = new RuleProfilerPanel(getJCC().getPlatformAccess().getServiceProvider(), desc.getName());
//					SHelp.setupHelp(panel, getHelpID());
					detail.add(panel, desc);
					comptree.getModel().fireNodeChanged(node);
				}
			}
		}
	};

	final AbstractAction	STOP_PROFILER	= new AbstractAction("Close Profiler", icons.getIcon("close_profiler"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode
					&& BDIAgentFactory.FILETYPE_BDIAGENT.equals(((IActiveComponentTreeNode)paths[i].getLastPathComponent()).getDescription().getType()))
				{
					IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					IComponentDescription desc = node.getDescription();
					RuleProfilerPanel intro = (RuleProfilerPanel)cards.getComponent(desc);			
					detail.remove(intro);
					comptree.getModel().fireNodeChanged(node);
				}
			}
		}
	};
}
