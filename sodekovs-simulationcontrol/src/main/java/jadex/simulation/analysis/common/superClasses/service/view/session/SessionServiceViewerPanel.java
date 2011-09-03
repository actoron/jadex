package jadex.simulation.analysis.common.superClasses.service.view.session;

import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.DefaultServiceViewerPanel;

import javax.swing.JComponent;

/**
 * Service Panel for Jadex componentviewer
 * @author 5Haubeck
 *
 */
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
