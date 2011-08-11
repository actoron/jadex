package jadex.simulation.analysis.application.opt4j;

import java.awt.GridBagLayout;
import java.util.UUID;

import javax.swing.JPanel;

import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;
import jadex.simulation.analysis.service.basic.view.session.SessionProperties;

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
	public void serviceEventOccur(AServiceEvent event)
	{
		

	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop;
	}

}
