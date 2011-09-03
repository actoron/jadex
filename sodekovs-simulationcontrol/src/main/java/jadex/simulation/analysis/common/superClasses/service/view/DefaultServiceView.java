package jadex.simulation.analysis.common.superClasses.service.view;

import jadex.base.gui.componenttree.ProvidedServiceProperties;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAListener;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class DefaultServiceView extends JTabbedPane implements IAListener
{
	// -------- attributes --------
	protected IAnalysisService service;

	private JPanel generalcomp;
	protected ThreadSuspendable susThread = new ThreadSuspendable(this);

	// -------- methods --------

	public DefaultServiceView(IAnalysisService service)
	{
		super();
		this.service = service;
		this.service.addListener(this);
	}

	public void init()
	{	
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				//TODO: ADD Workload
				ProvidedServiceProperties serProp = new ProvidedServiceProperties();
				
//				serProp.createTextField("workload", ((Double)service.getWorkload().get(new ThreadSuspendable(this))).toString());
				serProp.setService(service);
				serProp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Jadex 'Service' Eigenschaften "));
				generalcomp = serProp;
				
				addTab("Jadex Eigenschaften", null, generalcomp);
				setSelectedComponent(serProp);
				
				validate();
				updateUI();
			}
		});
	}

	@Override
	public void update(IAEvent event)
	{
		//omit
	}
}
