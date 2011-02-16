package jadex.tools.generic;

import jadex.base.gui.ComponentIdentifierDialog;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.AddRemotePathAction;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.base.gui.modeltree.RemovePathAction;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.IService;
import jadex.tools.starter.StarterPlugin;
import jadex.tools.starter.StarterViewerPanel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 *  Plugin for starting components.
 */
public class StarterServicePlugin extends AbstractServicePlugin
{
	//-------- constants --------

	static
	{
		icons.put("starter", SGUI.makeIcon(StarterServicePlugin.class, "/jadex/tools/common/images/new_starter.png"));
		icons.put("starter_sel", SGUI.makeIcon(StarterServicePlugin.class, "/jadex/tools/common/images/new_starter_sel.png"));
		icons.put("add_remote_component", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/add_remote_component.png"));
		icons.put("kill_platform", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_killplatform.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return IComponentManagementService.class;
	}
	
	/**
	 *  Create the component panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		final Future ret = new Future();
		final StarterViewerPanel stp = new StarterViewerPanel();
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
		return selected? icons.getIcon("starter_sel"): icons.getIcon("starter");
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return "Starter";
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		List	ret	= new ArrayList();
		JButton b;

		b = new JButton(new ToolTipAction(AddPathAction.getName(), 
			AddPathAction.getIcon(), AddPathAction.getTooltipText())
		{
			public void actionPerformed(ActionEvent e)
			{
				StarterViewerPanel svp = (StarterViewerPanel)getSelectorPanel().getCurrentPanel();
				if(svp!=null)
				{
					ModelTreePanel mpt = svp.getPanel().getModelTreePanel();
					if(mpt.isRemote())
					{
						mpt.getAction(AddRemotePathAction.getName()).actionPerformed(e);
					}
					else
					{
						mpt.getAction(AddPathAction.getName()).actionPerformed(e);
					}
				}
			}
		});
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(RemovePathAction.getName(), 
			RemovePathAction.getIcon(), RemovePathAction.getTooltipText(), true));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
//		b = new JButton(new DelegationAction(RefreshSubtreeAction.class));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
		
		JSeparator	separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret.add(separator);
		
		/*b = new JButton(GENERATE_JADEXDOC);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		bar.add(b);
		
		separator = new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		bar.add(separator);*/
		
		b = new JButton(ADD_REMOTE_COMPONENT);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

		b = new JButton(KILL_PLATFORM);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(ComponentTreePanel.KILL_ACTION, 
			ComponentTreePanel.icons.getIcon("kill_component"), "Kill selected components", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(ComponentTreePanel.SUSPEND_ACTION, 
			ComponentTreePanel.icons.getIcon("suspend_component"), "Suspend selected components", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(ComponentTreePanel.RESUME_ACTION, 
			ComponentTreePanel.icons.getIcon("resume_component"), "Resume selected components", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(ComponentTreePanel.STEP_ACTION, 
			ComponentTreePanel.icons.getIcon("step_component"), "Execute a step", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret.add(separator);

		b = new JButton(new DelegationAction(ComponentTreePanel.SHOWPROPERTIES_ACTION,
			ComponentTreePanel.icons.getIcon("show_properties"), "Show the properties of selected item", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(ComponentTreePanel.SHOWDETAILS_ACTION,
			ComponentTreePanel.icons.getIcon("show_details"), "Show the details of selected item", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

		b = new JButton(new DelegationAction(ComponentTreePanel.REFRESH_ACTION,
			ComponentTreePanel.icons.getIcon("refresh"), "Refresh selected node and direct subnodes", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(new DelegationAction(ComponentTreePanel.REFRESH_ACTION,
			ComponentTreePanel.icons.getIcon("refresh_tree"), "Refresh selected node and all subnodes", false));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

		return (JComponent[])ret.toArray(new JComponent[ret.size()]);
	}
	
//	/**
//	 *  Create menu bar.
//	 *  @return The menu bar.
//	 */
//	public JMenu[] createMenuBar()
//	{
//		JMenu[]	menu	= mpanel.createMenuBar();
//		this.checkingmenu = new JCheckBoxMenuItem(TOGGLE_CHECKING);
//		this.checkingmenu.setSelected(true);	// Default: on
//		menu[0].insert(checkingmenu, 1);	// Hack??? Should not assume position.
//		checkingmenu.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				System.out.println("turn: "+checkingmenu.isSelected());
//				snf.setChecking(checkingmenu.isSelected());
//			}
//		});
//		return menu;
//	}

	/**
	 *  Delegation action for searching the target of the action.
	 */
	public class DelegationAction extends ToolTipAction
	{
		/** Flag if action for model or instance tree. */
		protected boolean model;
		
		/**
		 *  Create a new delegation action.
		 */
		public DelegationAction(String name, Icon icon, String tooltip, boolean model)
		{
			super(name, icon, tooltip);
			this.model = model;
		}
		
		/**
		 *  Called when an action is issued.
		 */
		public void actionPerformed(ActionEvent e)
		{
			Action action = getAction();
			if(action!=null)
				action.actionPerformed(e);
		}
		
		/**
		 *  Get delegation action.
		 */
		public Action getAction()
		{
			Action ret = null;
			StarterViewerPanel svp = (StarterViewerPanel)getSelectorPanel().getCurrentPanel();
			if(svp!=null)
			{
				if(model)
				{
					ModelTreePanel mpt = svp.getPanel().getModelTreePanel();
					ret = mpt.getAction((String)getValue(Action.NAME));
				}
				else
				{
					ComponentTreePanel ctp = svp.getPanel().getComponentTreePanel();
					ret = ctp.getAction((String)getValue(Action.NAME));
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Action for killing the platform.
	 */
	final AbstractAction KILL_PLATFORM = new AbstractAction("Kill platform", icons.getIcon("kill_platform"))
	{
		public void actionPerformed(ActionEvent e)
		{
			StarterViewerPanel svp = (StarterViewerPanel)getSelectorPanel().getCurrentPanel();
			if(svp!=null)
			{
				ComponentTreePanel ctp = svp.getPanel().getComponentTreePanel();
				SJCC.killPlattform(ctp.getExternalAccess(), ctp);
			}
		}
	};
	
	/**
	 *  Action for adding a remote component.
	 */
	final AbstractAction ADD_REMOTE_COMPONENT = new AbstractAction("Add remote component", icons.getIcon("add_remote_component"))
	{
		public void actionPerformed(ActionEvent e)
		{
			StarterViewerPanel svp = (StarterViewerPanel)getSelectorPanel().getCurrentPanel();
			if(svp!=null)
			{
				ComponentIdentifierDialog dia = new ComponentIdentifierDialog(svp.getComponent(), jcc.getExternalAccess().getServiceProvider());
				final IComponentIdentifier cid = dia.getComponentIdentifier(null);
				if(cid!=null)
				{
					final Map args = new HashMap();
					args.put("component", cid);
					
					ComponentTreePanel ctp = svp.getPanel().getComponentTreePanel();
					ctp.getExternalAccess().scheduleStep(new IComponentStep()
					{
						public Object execute(IInternalAccess ia)
						{
							ia.getRequiredService("cms").addResultListener(new DefaultResultListener()		
							{
								public void resultAvailable(Object result)
								{
									IComponentManagementService cms = (IComponentManagementService)result;
	//								createComponent("jadex/base/service/remote/ProxyAgent.class", cid.getLocalName(), null, args, false, null, null, null, null);
									
									cms.createComponent(cid.getLocalName(), "jadex/base/service/remote/ProxyAgent.class", 
										new CreationInfo(args), null).addResultListener(new IResultListener()
									{
										public void resultAvailable(Object result)
										{
											getJCC().setStatusText("Created component: " + ((IComponentIdentifier)result).getLocalName());
										}
										
										public void exceptionOccurred(Exception exception)
										{
											getJCC().displayError("Problem Starting Component", "Component could not be started.", exception);
										}
									});
								}
							});
							return null;
						}
					});
				}
			}
		}
	};
}


