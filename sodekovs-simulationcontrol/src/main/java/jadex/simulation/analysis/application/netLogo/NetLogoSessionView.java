package jadex.simulation.analysis.application.netLogo;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class NetLogoSessionView extends JPanel implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
//	private JTextArea comp = new JTextArea();
	
	public NetLogoSessionView( IAnalysisSessionService service, final UUID id, final IAParameterEnsemble config)
	{
		super();
		this.service = service;
		prop = new SessionProperties(id, config);

	}
	
	public IFuture showFull(final JComponent comp)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				add(comp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				comp.revalidate();
				comp.repaint();
				revalidate();
				repaint();
			}
		});
		return new Future(null);
	}
	
	public IFuture showLite(final JTextArea comp)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				add(comp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				comp.revalidate();
				comp.repaint();
				revalidate();
				repaint();
			}
		});
		return new Future(null);
	}

	@Override
	public void update(IAEvent event)
	{
		//omit
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop ;
	}
	


}
