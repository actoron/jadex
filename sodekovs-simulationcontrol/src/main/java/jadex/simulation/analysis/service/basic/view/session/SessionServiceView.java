package jadex.simulation.analysis.service.basic.view.session;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.DefaultServiceView;

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
	public void serviceEventOccur(final AServiceEvent event)
	{
		super.serviceEventOccur(event);
		if (event.getCommand().equals(AConstants.SERVICE_SESSION_START))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					UUID id = (UUID) event.getValue();
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
