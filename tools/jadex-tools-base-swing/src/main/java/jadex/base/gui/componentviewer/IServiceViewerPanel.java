package jadex.base.gui.componentviewer;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

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
	public IFuture<Void> init(IControlCenter jcc, IService service);
}
