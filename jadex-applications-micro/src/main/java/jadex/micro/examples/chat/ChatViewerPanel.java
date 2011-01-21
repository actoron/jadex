package jadex.micro.examples.chat;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *  Panel for the chat view.
 */
public class ChatViewerPanel extends AbstractComponentViewerPanel
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
			public void resultAvailable(Object result)
			{
				panel = new ChatPanel((IExternalAccess)component);
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
