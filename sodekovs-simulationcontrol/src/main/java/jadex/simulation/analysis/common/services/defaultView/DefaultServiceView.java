package jadex.simulation.analysis.common.services.defaultView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jadex.base.gui.componenttree.ServiceProperties;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.events.service.IAServiceListener;
import jadex.simulation.analysis.common.services.IAnalysisService;
import jadex.simulation.analysis.common.services.IAnalysisSessionService;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class DefaultServiceView extends JTabbedPane implements IAServiceListener
{
	// -------- attributes --------
	protected IAnalysisService service;

	private JPanel generalcomp;
	protected ThreadSuspendable susThread = new ThreadSuspendable(this);

	// -------- methods --------

	public DefaultServiceView(IAnalysisSessionService service)
	{
		super();
		this.service = (IAnalysisService) service;
		this.service.addServiceListener(this);
	}

	public void init()
	{	
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				//TODO: ADD Workload
//				serProp.createTextField("workload", ((Double)service.getWorkload().get(new ThreadSuspendable(this))).toString());

				ServiceProperties serProp = new ServiceProperties();
				serProp.setService(service);
				serProp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Jadex Service Eigenschaften "));
				generalcomp = serProp;
				
				addTab("Allgemein", null, generalcomp);
				setSelectedComponent(serProp);
				
				JButton button = new JButton("test");
				button.addActionListener(new ActionListener()
				{
					
					@Override
					public void actionPerformed(ActionEvent e)
					{
						service.serviceChanged(new AServiceEvent());
					
					}
				});
				addTab("Button", null, button);
				validate();
				updateUI();
			}
		});
	}

	@Override
	public void serviceEventOccur(AServiceEvent event)
	{
		//omit
	}
}
