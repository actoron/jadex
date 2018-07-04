package jadex.tools.simcenter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIDefaults;

import jadex.bridge.service.IService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.tools.generic.AbstractServicePlugin;

/**
 *  Plugin for starting components.
 */
public class SimulationServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"simcenter_sel", SGUI.makeIcon(SimulationServicePlugin.class, "/jadex/tools/common/images/stopwatch_sel.png"),
		"simcenter", SGUI.makeIcon(SimulationServicePlugin.class, "/jadex/tools/common/images/stopwatch.png"),
	});
	
	//-------- attributes --------
	
	/** The long time setting menu item. */
	protected JRadioButtonMenuItem time_long;

	/** The relative time setting menu item. */
	protected JRadioButtonMenuItem time_rel;

	/** The date time setting menu item. */
	protected JRadioButtonMenuItem time_date;
	
	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return ISimulationService.class;
	}
	
	/**
	 *  Create the component panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		final Future ret = new Future();
		final SimServiceViewerPanel stp = new SimServiceViewerPanel();
		stp.init(getJCC(), service).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(stp);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("simcenter_sel"): icons.getIcon("simcenter");
	}
	
//	/**
//	 *  Create tool bar.
//	 *  @return The tool bar.
//	 */
//	public JComponent[] createToolBar()
//	{
//		List	ret	= new ArrayList();
//		JButton b;
//
//		b = new JButton(new ToolTipAction(AddPathAction.getName(), 
//			AddPathAction.getIcon(), AddPathAction.getTooltipText())
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				SimServiceViewerPanel svp = (SimServiceViewerPanel)getSelectorPanel().getCurrentPanel();
//				if(svp!=null)
//				{
//					ModelTreePanel mpt = svp.getPanel().getModelTreePanel();
//					if(mpt.isRemote())
//					{
//						mpt.getAction(AddRemotePathAction.getName()).actionPerformed(e);
//					}
//					else
//					{
//						mpt.getAction(AddPathAction.getName()).actionPerformed(e);
//					}
//				}
//			}
//		});
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(RemovePathAction.getName(), 
//			RemovePathAction.getIcon(), RemovePathAction.getTooltipText(), true));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
////		b = new JButton(new DelegationAction(RefreshSubtreeAction.class));
////		b.setBorder(null);
////		b.setToolTipText(b.getText());
////		b.setText(null);
////		b.setEnabled(true);
////		ret.add(b);
//		
//		JSeparator	separator	= new JToolBar.Separator();
//		separator.setOrientation(JSeparator.VERTICAL);
//		ret.add(separator);
//		
//		/*b = new JButton(GENERATE_JADEXDOC);
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		bar.add(b);
//		
//		separator = new JToolBar.Separator();
//		separator.setOrientation(JSeparator.VERTICAL);
//		bar.add(separator);*/
//		
//		b = new JButton(ADD_REMOTE_COMPONENT);
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//
//		b = new JButton(KILL_PLATFORM);
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(ComponentTreePanel.KILL_ACTION, 
//			ComponentTreePanel.icons.getIcon("kill_component"), "Kill selected components", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(ComponentTreePanel.SUSPEND_ACTION, 
//			ComponentTreePanel.icons.getIcon("suspend_component"), "Suspend selected components", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(ComponentTreePanel.RESUME_ACTION, 
//			ComponentTreePanel.icons.getIcon("resume_component"), "Resume selected components", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(ComponentTreePanel.STEP_ACTION, 
//			ComponentTreePanel.icons.getIcon("step_component"), "Execute a step", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		separator	= new JToolBar.Separator();
//		separator.setOrientation(JSeparator.VERTICAL);
//		ret.add(separator);
//
//		b = new JButton(new DelegationAction(ComponentTreePanel.SHOWPROPERTIES_ACTION,
//			ComponentTreePanel.icons.getIcon("show_properties"), "Show the properties of selected item", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(ComponentTreePanel.SHOWDETAILS_ACTION,
//			ComponentTreePanel.icons.getIcon("show_details"), "Show the details of selected item", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//
//		b = new JButton(new DelegationAction(ComponentTreePanel.REFRESH_ACTION,
//			ComponentTreePanel.icons.getIcon("refresh"), "Refresh selected node and direct subnodes", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(new DelegationAction(ComponentTreePanel.REFRESH_ACTION,
//			ComponentTreePanel.icons.getIcon("refresh_tree"), "Refresh selected node and all subnodes", false));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//
//		return (JComponent[])ret.toArray(new JComponent[ret.size()]);
//	}
	
	/**
	 *  Create the menu bar.
	 *  @return The menubar.
	 */
	public JMenu[] createMenuBar()
	{	
		ButtonGroup group = new ButtonGroup();
		
		JMenu menu = new JMenu("Time Settings");
//		SHelp.setupHelp(menu, "tools.simcenter");

		time_long = new JRadioButtonMenuItem(new AbstractAction("Long value")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(getSelectorPanel().getCurrentPanel()!=null)
				{
					SimCenterPanel	scpanel	= (SimCenterPanel)getSelectorPanel().getCurrentPanel().getComponent();
					scpanel.setTimemode(0);
					scpanel.updateView();
				}
			}
		});
		
		group.add(time_long);
		menu.add(time_long);
		
		time_rel = new JRadioButtonMenuItem(new AbstractAction("Relative value")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(getSelectorPanel().getCurrentPanel()!=null)
				{
					SimCenterPanel	scpanel	= (SimCenterPanel)getSelectorPanel().getCurrentPanel().getComponent();
					scpanel.setTimemode(1);
					scpanel.updateView();
				}
			}
		});
		group.add(time_rel);
		menu.add(time_rel);
		
		time_date = new JRadioButtonMenuItem(new AbstractAction("Date value")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(getSelectorPanel().getCurrentPanel()!=null)
				{
					SimCenterPanel	scpanel	= (SimCenterPanel)getSelectorPanel().getCurrentPanel().getComponent();
					scpanel.setTimemode(2);
					scpanel.updateView();
				}
			}
		});
		group.add(time_date);
		menu.add(time_date);
		
		updateMenu();
		
		return new JMenu[]{menu};
	}
	
	/**
	 *  Change menu item state according to panel settings. 
	 */
	protected void	updateMenu()
	{
		if(getSelectorPanel().getCurrentPanel()!=null)
		{
			SimCenterPanel	scpanel	= (SimCenterPanel)getSelectorPanel().getCurrentPanel().getComponent();
			if(scpanel.getTimeMode()==0)
				time_long.setSelected(true);
			else if(scpanel.getTimeMode()==1)
				time_rel.setSelected(true);
			else if(scpanel.getTimeMode()==2)
				time_date.setSelected(true);
		}

	}
	
	/**
	 *  Set the properties.
	 *  Updates menu after properties are set. 
	 */
	public IFuture setProperties(Properties props)
	{
		final Future	ret	= new Future();
		super.setProperties(props).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				updateMenu();
				ret.setResult(null);
			}	
		});
		return ret;
	}

//	/**
//	 *  Delegation action for searching the target of the action.
//	 */
//	public class DelegationAction extends ToolTipAction
//	{
//		/** Flag if action for model or instance tree. */
//		protected boolean model;
//		
//		/**
//		 *  Create a new delegation action.
//		 */
//		public DelegationAction(String name, Icon icon, String tooltip, boolean model)
//		{
//			super(name, icon, tooltip);
//			this.model = model;
//		}
//		
//		/**
//		 *  Called when an action is issued.
//		 */
//		public void actionPerformed(ActionEvent e)
//		{
//			Action action = getAction();
//			if(action!=null)
//				action.actionPerformed(e);
//		}
//		
//		/**
//		 *  Get delegation action.
//		 */
//		public Action getAction()
//		{
//			Action ret = null;
//			SimServiceViewerPanel svp = (SimServiceViewerPanel)getSelectorPanel().getCurrentPanel();
//			if(svp!=null)
//			{
//				if(model)
//				{
//					ModelTreePanel mpt = svp.getPanel().getModelTreePanel();
//					ret = mpt.getAction((String)getValue(Action.NAME));
//				}
//				else
//				{
//					ComponentTreePanel ctp = svp.getPanel().getComponentTreePanel();
//					ret = ctp.getAction((String)getValue(Action.NAME));
//				}
//			}
//			return ret;
//		}
//	}
//	
//	/**
//	 *  Action for killing the platform.
//	 */
//	final AbstractAction KILL_PLATFORM = new AbstractAction("Kill platform", icons.getIcon("kill_platform"))
//	{
//		public void actionPerformed(ActionEvent e)
//		{
//			SimServiceViewerPanel svp = (SimServiceViewerPanel)getSelectorPanel().getCurrentPanel();
//			if(svp!=null)
//			{
//				ComponentTreePanel ctp = svp.getPanel().getComponentTreePanel();
//				SJCC.killPlattform(ctp.getExternalAccess(), ctp);
//			}
//		}
//	};
//	
//	/**
//	 *  Action for adding a remote component.
//	 */
//	final AbstractAction ADD_REMOTE_COMPONENT = new AbstractAction("Add remote component", icons.getIcon("add_remote_component"))
//	{
//		public void actionPerformed(ActionEvent e)
//		{
//			SimServiceViewerPanel svp = (SimServiceViewerPanel)getSelectorPanel().getCurrentPanel();
//			if(svp!=null)
//			{
//				ComponentIdentifierDialog dia = new ComponentIdentifierDialog(svp.getComponent(), jcc.getPlatformAccess().getServiceProvider());
//				final IComponentIdentifier cid = dia.getComponentIdentifier(null);
//				if(cid!=null)
//				{
//					final Map args = new HashMap();
//					args.put("component", cid);
//					
//					ComponentTreePanel ctp = svp.getPanel().getComponentTreePanel();
//					SServiceProvider.searchService(ctp.getExternalAccess().getServiceProvider(), new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//						.addResultListener(new DefaultResultListener()		
//					{
//						public void resultAvailable(Object result)
//						{
//							IComponentManagementService cms = (IComponentManagementService)result;
////								createComponent("jadex/platform/service/remote/ProxyAgent.class", cid.getLocalName(), null, args, false, null, null, null, null);
//							
//							cms.createComponent(cid.getLocalName(), "jadex/platform/service/remote/ProxyAgent.class", 
//								new CreationInfo(args), null).addResultListener(new IResultListener()
//							{
//								public void resultAvailable(Object result)
//								{
//									getJCC().setStatusText("Created component: " + ((IComponentIdentifier)result).getLocalName());
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									getJCC().displayError("Problem Starting Component", "Component could not be started.", exception);
//								}
//							});
//						}
//					});
//				}
//			}
//		}
//	};
}


