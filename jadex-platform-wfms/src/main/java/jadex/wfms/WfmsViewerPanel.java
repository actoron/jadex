package jadex.wfms;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.client.standard.StandardClientApplication;

public class WfmsViewerPanel extends AbstractComponentViewerPanel
{
	protected StandardClientApplication app;
	
	public IFuture<Void> init(IControlCenter jcc, IExternalAccess component)
	{
		app = new StandardClientApplication(component);
		return IFuture.DONE;
	}
	
	public IFuture<Void> shutdown()
	{
		final Future<Void> ret = new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				app.disconnect();
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	
	public JComponent getComponent()
	{
		return app.getView();
	}
}
