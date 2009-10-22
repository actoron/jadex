package jadex.wfms.service.admin;

import javax.swing.JFrame;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;

/**
 * 
 */
public class AdminGui implements IService
{
	/** The service container. */
	protected IServiceContainer container;
	
	/** The plugin properties. */
	protected String plugins_prop;
	
	/**
	 * 
	 */
	public AdminGui(IServiceContainer container, String plugins_prop)
	{
		this.container = container;
		this.plugins_prop = plugins_prop;
	}
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		ControlCenter cc = new ControlCenter(container, plugins_prop);
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		
	}
}
