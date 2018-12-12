package jadex.bdiv3.examples.shop;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Panel for the customer view.
 */
public class CustomerViewerPanel extends AbstractComponentViewerPanel
{
	//-------- attributes --------
	
	/** The panel. */
	protected JPanel panel;//	= new JPanel();	
	
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
				component.scheduleStep(new IComponentStep<Void>()
				{
					@Override
					public IFuture<Void> execute(IInternalAccess ia)
					{
						CustomerAgent ca = (CustomerAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
						panel = new CustomerPanel(ca.getCapability().getCapability());
						ret.setResult(result);
						return IFuture.DONE;
					}
				});
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
