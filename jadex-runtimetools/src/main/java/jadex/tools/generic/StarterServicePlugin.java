package jadex.tools.generic;

import jadex.base.gui.filetree.RefreshSubtreeAction;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.AddRemotePathAction;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.bridge.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.IService;
import jadex.tools.starter.StarterViewerPanel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
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
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		List	ret	= new ArrayList();
		JButton b;

		b = new JButton(new DelegationAction(AddPathAction.getName(), 
			AddPathAction.getIcon(), AddPathAction.getTooltipText(), AddPathAction.class));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
//		b = new JButton(new DelegationAction(AddRemotePathAction.getName(), 
//			AddRemotePathAction.getIcon(), AddRemotePathAction.getTooltipText(), AddRemotePathAction.class));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
		
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
//		b = new JButton(comptree.getKillAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(comptree.getSuspendAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(comptree.getResumeAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(comptree.getStepAction());
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
//		b = new JButton(comptree.getShowPropertiesAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(comptree.getShowObjectDetailsAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//
//		b = new JButton(comptree.getRefreshAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(comptree.getRefreshTreeAction());
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);

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
	 * 
	 */
	public class DelegationAction extends ToolTipAction
	{
		protected Class actionclass;
		
		/**
		 * 
		 */
		public DelegationAction(String name, Icon icon, String tooltip, Class actionclass)
		{
			super(name, icon, tooltip);
			this.actionclass = actionclass;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			StarterViewerPanel svp = (StarterViewerPanel)getSelectorPanel().getCurrentPanel();
			if(svp!=null)
			{
				 ModelTreePanel mpt = svp.getPanel().getModelTreepanel();
				 mpt.getAction(actionclass).actionPerformed(e);
			}
		}
	}
}


