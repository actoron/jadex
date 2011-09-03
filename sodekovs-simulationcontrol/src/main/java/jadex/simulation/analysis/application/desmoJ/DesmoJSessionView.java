package jadex.simulation.analysis.application.desmoJ;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

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
	public void update(IAEvent event)
	{
		// omit
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop ;
	}
	


}
