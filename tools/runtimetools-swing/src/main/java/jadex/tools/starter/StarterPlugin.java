package jadex.tools.starter;

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
import javax.swing.UIDefaults;

import jadex.base.gui.ComponentIdentifierDialog;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.AddRIDAction;
import jadex.base.gui.modeltree.CollapseAllAction;
import jadex.base.gui.modeltree.RemovePathAction;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Properties;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;

/**
 *  Plugin for starting components.
 */
public class StarterPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"starter",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_starter.png"),
		"starter_sel",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_starter_sel.png"),
		"add_remote_component",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/add_remote_component.png"),
		"kill_platform",	SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_killplatform.png")
	});

	//-------- methods --------
	
	
	/**
	 *  Return the unique name of this plugin.
	 *  This method may be called before init().
	 *  Used e.g. to store properties of each plugin.
	 */
	public String getName()
	{
		return "Starter";
	}
	
	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture setProperties(Properties props)
	{
		return ((StarterPluginPanel)getView()).setProperties(props);
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture getProperties()
	{
		return ((StarterPluginPanel)getView()).getProperties();
	}
	
	/**
	 *  Store settings if any in platform settings service.
	 */
	public IFuture pushPlatformSettings()
	{
		return ((StarterPluginPanel)getView()).pushPlatformProperties();
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

		b = new JButton(((StarterPluginPanel)getView()).getModelTreePanel().getAction(AddPathAction.getName()));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		Action a = ((StarterPluginPanel)getView()).getModelTreePanel().getAction(AddRIDAction.getName());
		b = new JButton(a);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(a.isEnabled());
//		b.setEnabled(false);
		ret.add(b);
		
		b = new JButton(((StarterPluginPanel)getView()).getModelTreePanel().getAction(RemovePathAction.getName()));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		b = new JButton(((StarterPluginPanel)getView()).getModelTreePanel().getAction(CollapseAllAction.getName()));
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);
		
		JSeparator	separator	= new JToolBar.Separator();
		separator.setOrientation(JSeparator.VERTICAL);
		ret.add(separator);
		
		b = new JButton(ADD_REMOTE_COMPONENT);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		ret.add(b);

//		b = new JButton(KILL_PLATFORM);
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.KILL_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.SUSPEND_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.RESUME_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.STEP_ACTION));
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
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.SHOWPROPERTIES_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.SHOWDETAILS_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.REFRESH_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);
//		
//		b = new JButton(((StarterPluginPanel)getView()).getComponentTreePanel().getAction(ComponentTreePanel.REFRESHSUBTREE_ACTION));
//		b.setBorder(null);
//		b.setToolTipText(b.getText());
//		b.setText(null);
//		b.setEnabled(true);
//		ret.add(b);

		return (JComponent[])ret.toArray(new JComponent[ret.size()]);
	}
	
	public JComponent createView()
	{
		return new StarterPluginPanel(getJCC());
	}
	
	/**
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		((StarterPluginPanel)getView()).dispose();
		return super.shutdown();
	}

	
	/**
	 *  Action for killing the platform.
	 */
	final AbstractAction KILL_PLATFORM = new AbstractAction("Kill platform", icons.getIcon("kill_platform"))
	{
		public void actionPerformed(ActionEvent e)
		{
			SJCC.killPlattform(getJCC().getPlatformAccess(), getView());
		}
	};
	
	/**
	 *  Action for adding a remote component.
	 */
	final AbstractAction ADD_REMOTE_COMPONENT = new AbstractAction("Add remote component", icons.getIcon("add_remote_component"))
	{
		public void actionPerformed(ActionEvent e)
		{
			ComponentIdentifierDialog dia = new ComponentIdentifierDialog(getView(), jcc.getPlatformAccess());
			final IComponentIdentifier cid = dia.getComponentIdentifier(null);
			if(cid!=null)
			{
				final Map args = new HashMap();
				args.put("component", cid);
				
				SServiceProvider.searchService(getJCC().getPlatformAccess(), new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new DefaultResultListener()		
				{
					public void resultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
//								createComponent("jadex/platform/service/remote/ProxyAgent.class", cid.getLocalName(), null, args, false, null, null, null, null);
						
						cms.createComponent(cid.getLocalName(), "jadex/platform/service/remote/ProxyAgent.class", 
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
			}
		}
	};
}


