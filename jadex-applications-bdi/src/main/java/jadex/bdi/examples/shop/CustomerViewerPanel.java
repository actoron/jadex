package jadex.bdi.examples.shop;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;

import javax.swing.JComponent;

/**
 *  Panel for the customer view.
 */
public class CustomerViewerPanel implements IComponentViewerPanel
{
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
	protected IExternalAccess component;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = component;
		return new Future(null);
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown()
	{
		return new Future(null);
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "customer";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return new CustomerPanel((IBDIExternalAccess)component);
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}

}
