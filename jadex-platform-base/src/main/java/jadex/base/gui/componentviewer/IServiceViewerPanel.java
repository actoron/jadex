package jadex.base.gui.componentviewer;

import jadex.base.gui.plugin.IControlCenter;
import jadex.commons.IFuture;
import jadex.commons.service.IService;

/**
 *  An inner panel of the service viewer.
 */
public interface IServiceViewerPanel extends IAbstractViewerPanel
{
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture init(IControlCenter jcc, IService service);
}
