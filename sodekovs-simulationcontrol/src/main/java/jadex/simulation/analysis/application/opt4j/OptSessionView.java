package jadex.simulation.analysis.application.opt4j;

import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

import java.awt.GridBagLayout;
import java.util.UUID;

import javax.swing.JPanel;

public class OptSessionView extends JPanel implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	
	public OptSessionView(IAnalysisSessionService service, final UUID id, final IAParameterEnsemble config)
	{
		super(new GridBagLayout());
		this.service = service;
		prop = new SessionProperties(id, config);
	}

	@Override
	public void update(IAEvent event)
	{
		//omit
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop;
	}

}
