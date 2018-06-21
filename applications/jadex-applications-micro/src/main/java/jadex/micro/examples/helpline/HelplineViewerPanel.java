package jadex.micro.examples.helpline;


import javax.swing.JComponent;
import javax.swing.JPanel;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Panel for the helpline view.
 */
public class HelplineViewerPanel extends AbstractComponentViewerPanel
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
	public IFuture<Void> init(IControlCenter jcc, final IExternalAccess component)
	{
		final Future<Void> ret = new Future<Void>();
		super.init(jcc, component).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				panel = new HelplinePanel(component);
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
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
}
