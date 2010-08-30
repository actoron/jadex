package jadex.tools.componentviewer;

import jadex.commons.IFuture;
import jadex.commons.service.IService;
import jadex.tools.common.plugin.IControlCenter;

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
