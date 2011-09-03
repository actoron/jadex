package jadex.simulation.analysis.common.superClasses.service.view.session;

import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.service.AServiceEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.DefaultServiceView;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Set;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A Tab for a SessionSerivceView
 * @author 5Haubeck
 *
 */
public class SessionServiceView extends DefaultServiceView
{
	IAnalysisSessionService sservice;
	Integer sessionCount = 1;

	public SessionServiceView(IAnalysisService service)
	{
		super(service);
		sservice = (IAnalysisSessionService) service;
	}

	@Override
	public void init()
	{
		super.init();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Set<UUID> setIDs = (Set<UUID>) sservice.getSessions().get(susThread);
				for (UUID id : setIDs)
				{
					JComponent comp = (JComponent) sservice.getSessionView(id).get(susThread);
					addTab("Session " + sessionCount, comp);
					sessionCount++;
				}
			}
		});
	}

	@Override
	public void update(final IAEvent event)
	{
		super.update(event);
		if (event.getCommand().equals(AConstants.SERVICE_SESSION_START))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					UUID id = (UUID) ((AServiceEvent)event).getValue();
					JComponent comp = (JComponent) sservice.getSessionView(id).get(susThread);
					addTab("Session " + sessionCount, comp);
					sessionCount++;
					revalidate();
					repaint();
				}
			});
		}
		
	}
}
