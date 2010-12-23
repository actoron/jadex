package jadex.tools.daemon;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;

import javax.swing.JComponent;

/**
 *  Panel for the helpline view.
 */
public class DaemonViewerPanel extends AbstractComponentViewerPanel
{
	//-------- attributes --------
	
	/** The panel. */
	protected DaemonPanel panel;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(IControlCenter jcc, final IExternalAccess component)
	{
		final Future ret = new Future();
		super.init(jcc, component).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				panel = new DaemonPanel((IMicroExternalAccess)component);
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
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
}
