package jadex.tools.simcenter;

import javax.swing.JComponent;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDelegationResultListener;

/**
 *  Panel for the daemon view.
 */
public class SimServiceViewerPanel extends AbstractServiceViewerPanel<ISimulationService>
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
	public IFuture<Void> init(final IControlCenter jcc, final IService service)
	{
		final Future<Void> ret = new Future<Void>();
		super.init(jcc, service).addResultListener(new SwingDelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
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
	public IFuture<Void> shutdown()
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
	public IFuture<Void> setProperties(Properties props)
	{
		return panel.setProperties(props);
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		return panel.getProperties();
	}
}