package jadex.simulation.analysis.application.netLogo;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Lite and Full visualisation of NetLogo
 * @author 5Haubeck
 *
 */
public class NetLogoSessionView extends JPanel implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	private JComponent component = new JPanel(new GridBagLayout());
	
	public NetLogoSessionView( IAnalysisSessionService service, final String id, final IAParameterEnsemble config)
	{
		super();
		this.service = service;
		prop = new SessionProperties(id, config);

	}
	
	/**
	 * Add the visualization to component panel
	 * @param comp the component to add (netLogo comp)
	 * @return null
	 */
	public IFuture showFull(final JComponent comp)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				component.add(comp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				comp.revalidate();
				comp.repaint();
				component.revalidate();
				component.repaint();
			}
		});
		return new Future(null);
	}
	
	/**
	 * Add textArea to component Panel
	 * @param comp the JTextArea to add (netLogo comp)
	 * @return null
	 */
	public IFuture showLite(final JTextArea comp)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				component.add(comp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				comp.revalidate();
				comp.repaint();
				component.revalidate();
				component.repaint();
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
	
	@Override
	public JComponent getComponent()
	{
		return component;
	}

}
