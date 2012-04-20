package jadex.simulation.analysis.application.standalone;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
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
import javax.swing.SwingUtilities;

public class ADataSessionView extends JPanel implements IASessionView
{
	protected IAnalysisSessionService service;
	private SessionProperties prop = null;
	private JComponent component = new JPanel(new GridBagLayout());

	public ADataSessionView( IAnalysisSessionService service, final String id, final IAParameterEnsemble config)
	{
		super(new GridBagLayout());
		this.service = service;
		prop = new SessionProperties(id, config);

	}
	
	public IFuture startGUI(final IADataObject dataObj)
	{
		final Future ret = new Future(dataObj);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final Insets insets = new Insets(1, 1, 1, 1);
				JPanel basicPanel = new JPanel(new GridBagLayout());

				basicPanel.add(dataObj.getView().getComponent(), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				component.add(basicPanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
			}
		});
		return ret;
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
		return component ;
	}
	


}
