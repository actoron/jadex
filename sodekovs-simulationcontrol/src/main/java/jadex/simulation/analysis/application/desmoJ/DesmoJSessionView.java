package jadex.simulation.analysis.application.desmoJ;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.session.IASessionView;
import jadex.simulation.analysis.service.basic.view.session.SessionProperties;

import java.util.UUID;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class DesmoJSessionView extends JTextArea implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
//	private JTextArea comp = new JTextArea();
	
	public DesmoJSessionView( IAnalysisSessionService service, final UUID id, final IAParameterEnsemble config)
	{
		super();
		this.service = service;
		prop = new SessionProperties(id, config);

	}
	
	public IFuture addText(final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				append(text);
			}
		});
		return new Future(null);
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
