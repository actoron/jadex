package jadex.base.gui.componentviewer;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

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
	public IFuture<Void> init(IControlCenter jcc, IExternalAccess component);
}