package jadex.tools.serviceviewer;

import jadex.commons.Properties;
import jadex.service.IService;
import jadex.tools.common.plugin.IControlCenter;

import javax.swing.JComponent;

/**
 *  An inner panel of the service viewer.
 */
public interface IServiceViewerPanel
{
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public void init(IControlCenter jcc, IService service);
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public void shutdown();

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent();

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps);

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties();
}
