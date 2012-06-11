package jadex.tools.awareness.gui;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.gui.AbstractComponentPlugin;

import javax.swing.Icon;

/**
 *  The awareness component plugin is used to wrap the awareness agent panel as JCC plugin.
 */
public class AwarenessComponentPlugin extends AbstractComponentPlugin
{
	//-------- constants --------

	static
	{
		icons.put("awareness", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/awareness.png"));
		icons.put("awareness_sel", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/awareness_sel.png"));
	}

	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public String getModelName()
	{
		return "jadex.base.service.awareness.management.AwarenessManagement";
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return "Awareness Settings";
	}
	
	/**
	 *  Create the component panel.
	 */
	public IFuture createComponentPanel(IExternalAccess component)
	{
		AwarenessAgentPanel awap = new AwarenessAgentPanel();
		awap.init(getJCC(), component);
		return new Future(awap);
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("awareness_sel"): icons.getIcon("awareness");
	}
}
