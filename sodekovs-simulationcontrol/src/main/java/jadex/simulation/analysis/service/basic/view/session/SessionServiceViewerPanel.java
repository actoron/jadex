package jadex.simulation.analysis.service.basic.view.session;

import javax.swing.JComponent;

import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.DefaultServiceViewerPanel;

public class SessionServiceViewerPanel extends DefaultServiceViewerPanel
{
	@Override
	public JComponent getComponent()
	{
		SessionServiceView view = new SessionServiceView((IAnalysisSessionService) service);
		view.init();
		return view;
	}

}
