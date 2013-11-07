package jadex.bdi.tutorial;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import javax.swing.SwingUtilities;

/**
 *  The plan for updating the gui.
 */
public class GUIPlanF1 extends Plan
{
	//-------- attributes --------

	/** The gui. */
	protected TranslationGuiF1 gui;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public GUIPlanF1()
	{
		getLogger().info("Created: "+this);
		//this.gui = new TranslationGuiF1();
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Could be done in a more elegant way via listeners since 0.96
		
		final TranslationGuiF1 gui = new TranslationGuiF1();
		
		getEventbase().addInternalEventListener("gui_update", new IInternalEventListener()
		{
			public void internalEventOccurred(AgentEvent ae)
			{
				String[] res = (String[])((IInternalEvent)ae.getSource()).getParameter("content").getValue();
				gui.addRow(res);	
//				gui.addRow((String[]));
			}
		});
		
//		getScope().addComponentListener(new TerminationAdapter()
//		{
//			public void componentTerminated()
//			{
////				System.out.println("terminating");
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						gui.dispose();
//					}
//				});
//			}
//		});
		
		getScope().subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
			.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent result)
			{
				gui.dispose();
			}
		}));
		
//		while(true)
//		{
//			IInternalEvent event = waitForInternalEvent("gui_update");
//			gui.addRow((String[])event.getParameter("content").getValue());
//		}
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
			}
		});
	}
}
