package jadex.tools.simcenter;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.base.service.simulation.ISimulationService;
import jadex.bridge.service.IService;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import javax.swing.JComponent;

/**
 *  Panel for the daemon view.
 */
public class SimServiceViewerPanel extends AbstractServiceViewerPanel
{
	//-------- attributes --------
	
	/** The panel. */
	protected SimCenterPanel	panel;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(final IControlCenter jcc, final IService service)
	{
		final Future ret = new Future();
		super.init(jcc, service).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				panel = new SimCenterPanel(jcc, (ISimulationService)service);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown()
	{
		return panel.shutdown();
	}
	
	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
	
	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture setProperties(Properties props)
	{
		return panel.setProperties(props);
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture getProperties()
	{
		return panel.getProperties();
	}
}