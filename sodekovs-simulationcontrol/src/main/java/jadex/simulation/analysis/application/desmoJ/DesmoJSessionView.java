package jadex.simulation.analysis.application.desmoJ;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

import java.awt.GridBagLayout;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Textview of DesmoJ
 * @author 5Haubeck
 *
 */
public class DesmoJSessionView extends JTextArea implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	private JTextArea component = new JTextArea();
	
	public DesmoJSessionView( IAnalysisSessionService service, final String id, final IAParameterEnsemble config)
	{
		super();
		this.service = service;
		prop = new SessionProperties(id, config);

	}
	
	/**
	 * Add the desmoJ Test to TextArea
	 * @param text the text to add
	 * @return null
	 */
	public IFuture addText(final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				component.append(text);
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
	
	@Override
	public JComponent getComponent()
	{
		return component;
	}
}
