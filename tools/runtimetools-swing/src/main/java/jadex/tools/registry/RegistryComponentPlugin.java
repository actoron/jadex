package jadex.tools.registry;

import javax.swing.Icon;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.platform.PlatformAgent;
import jadex.tools.generic.AbstractComponentPlugin;

/**
 *  The registry component plugin is used to wrap the registry agent panel as JCC plugin.
 */
public class RegistryComponentPlugin extends AbstractComponentPlugin
{
	//-------- constants --------

	static
	{
		icons.put("registry", SGUI.makeIcon(RegistryComponentPlugin.class, "/jadex/base/gui/images/registry.png"));
		icons.put("registry_sel", SGUI.makeIcon(RegistryComponentPlugin.class, "/jadex/base/gui/images/registry_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public String getModelName()
	{
		String ret = PlatformAgent.class.getName();
		ret = ret.substring(0, ret.length()-5); // strip Agent
		return ret;
//		return "jadex.platform.service.awareness.management.AwarenessManagement";
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return "Registry Infos";
	}
	
	/**
	 *  Create the component panel.
	 */
	public IFuture<IAbstractViewerPanel> createComponentPanel(IExternalAccess component)
	{
		RegistryPanel regp = new RegistryPanel();
		regp.init(getJCC(), component);
		return new Future<IAbstractViewerPanel>(regp);
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("registry_sel"): icons.getIcon("registry");
	}
}
