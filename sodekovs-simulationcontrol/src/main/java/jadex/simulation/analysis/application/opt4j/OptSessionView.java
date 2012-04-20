package jadex.simulation.analysis.application.opt4j;

import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

import java.awt.GridBagLayout;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * View of the JOpt Service
 * @author 5Haubeck
 *
 */
public class OptSessionView extends JPanel implements IASessionView
{
	//not integrated yet
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	private JComponent component = new JPanel();
	
	public OptSessionView(IAnalysisSessionService service, final String id, final IAParameterEnsemble config)
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

	@Override
	public JComponent getComponent()
	{
		return component;
	}

}
