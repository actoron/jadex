package jadex.bdi.examples.shop;

import javax.swing.JComponent;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.componentviewer.IComponentViewerPanel;

/**
 * 
 */
public class CustomerViewerPanel implements IComponentViewerPanel
{
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
	protected IExternalAccess component;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public void init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
//		IBDIExternalAccess agent = (IBDIExternalAccess)component;
//		agent.getCapability()
		this.component = component;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public void shutdown()
	{
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
		return new CustomerGui((IBDIExternalAccess)component);
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
