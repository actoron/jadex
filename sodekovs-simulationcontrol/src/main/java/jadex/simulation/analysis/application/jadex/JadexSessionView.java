package jadex.simulation.analysis.application.jadex;

import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class JadexSessionView extends JTextArea implements IASessionView
{
	//not integraded yet
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	private JComponent component = new JPanel();
	
	public JadexSessionView( IAnalysisSessionService service, final String id, final IAParameterEnsemble config)
	{
		super();
		this.service = service;
		prop = new SessionProperties(id, config);
	}

	@Override
	public void update(IAEvent event)
	{
		// omit
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop ;
	}
	
	@Override
	public JComponent getComponent()
	{
		return component;
	}


}
