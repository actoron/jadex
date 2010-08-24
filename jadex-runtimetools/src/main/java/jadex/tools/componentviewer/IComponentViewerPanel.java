package jadex.tools.componentviewer;

import jadex.bridge.IExternalAccess;
import jadex.tools.common.plugin.IControlCenter;

/**
 *  An inner panel of the viewer.
 */
public interface IComponentViewerPanel extends IAbstractViewerPanel
{
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public void init(IControlCenter jcc, IExternalAccess component);
}