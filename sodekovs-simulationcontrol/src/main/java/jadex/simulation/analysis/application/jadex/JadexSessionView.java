package jadex.simulation.analysis.application.jadex;

import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;
import jadex.simulation.analysis.service.basic.view.session.SessionProperties;

import java.util.UUID;

import javax.swing.JTextArea;

public class JadexSessionView extends JTextArea implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	
	public JadexSessionView( IAnalysisSessionService service, final UUID id, final IAParameterEnsemble config)
	{
		super();
		this.service = service;
		prop = new SessionProperties(id, config);
	}

	@Override
	public void serviceEventOccur(AServiceEvent event)
	{
		// omitt
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop ;
	}
	


}
