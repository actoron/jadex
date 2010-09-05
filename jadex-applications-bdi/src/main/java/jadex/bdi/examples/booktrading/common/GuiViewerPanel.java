package jadex.bdi.examples.booktrading.common;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *  Panel for the customer view.
 */
public class GuiViewerPanel extends AbstractComponentViewerPanel
{
	//-------- attributes --------
	
	/** The panel. */
	protected JPanel panel;
	
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
			public void resultAvailable(Object source, Object result)
			{
				panel = new GuiPanel((IBDIExternalAccess)component);
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
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