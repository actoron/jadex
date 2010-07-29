package jadex.tools.serviceviewer;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.SServiceProvider;
import jadex.tools.common.componenttree.ComponentTreePanel;
import jadex.tools.common.plugin.AbstractJCCPlugin;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIDefaults;

public class ServiceViewerPlugin extends AbstractJCCPlugin
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"service_viewer", SGUI.makeIcon(ServiceViewerPlugin.class, "/jadex/tools/serviceviewer/images/configure.png")
	});
	
	//-------- IControlCenterPlugin interface --------
	
	/**
	 *  Return the unique name of this plugin.
	 *  This method may be called before init().
	 *  Used e.g. to store properties of each plugin.
	 */
	public String getName()
	{
		return "ServiceViewerPlugin";
	}

	/**
	 *  Return the icon representing this plugin.
	 *  This method may be called before init().
	 */
	public Icon getToolIcon(boolean selected)
	{
		return icons.getIcon("service_viewer");
	}

	/**
	 *  Return the id for the help system
	 *  This method may be called before init().
	 */
	public String getHelpID()
	{
		return null;
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		final JPanel	view	= new JPanel(new BorderLayout());
		SServiceProvider.getServiceUpwards(getJCC().getServiceContainer(), IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(view)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
				// Hack!!! How to find root node?
				cms.getComponentDescriptions().addResultListener(new SwingDefaultResultListener(view)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentDescription[]	descriptions	= (IComponentDescription[])result;
						IComponentDescription	root	= null;
						for(int i=0; root==null && i<descriptions.length; i++)
						{
							if(descriptions[i].getParent()==null)
							{
								root	= descriptions[i];
							}
						}
						view.add(new ComponentTreePanel(cms, getJCC().getServiceContainer(), root));
						view.invalidate();
						view.doLayout();
						view.paintComponents(view.getGraphics());
					}
				});
			}
		});
		return view;
	}
}
