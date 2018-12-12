package jadex.bdi.tutorial;

import javax.swing.SwingUtilities;

import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.Future;

/**
 *  The plan for updating the gui.
 */
public class GUIPlanF1 extends Plan
{
	//-------- attributes --------

	/** The gui. */
	protected TranslationGuiF1 gui;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Create gui on swing thread.
		final Future<Void>	created	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				GUIPlanF1.this.gui = new TranslationGuiF1();
				created.setResult(null);
			}
		});
		created.get();
		
//		getEventbase().addInternalEventListener("gui_update", new IInternalEventListener()
//		{
//			public void internalEventOccurred(AgentEvent ae)
//			{
//				String[] res = (String[])((IInternalEvent)ae.getSource()).getParameter("content").getValue();
//				gui.addRow(res);	
////				gui.addRow((String[]));
//			}
//		});
		
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
		
//		getScope().subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
//		getAgent().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
//			.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
//		{
//			public void intermediateResultAvailable(IMonitoringEvent result)
//			{
//				gui.dispose();
//			}
//		}));
		
		while(true)
		{
			IInternalEvent event = waitForInternalEvent("gui_update");
			final String[]	row	= (String[])event.getParameter("content").getValue();
			// Change gui on swing thread.
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					gui.addRow(row);
				}
			});
		}
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
		// Close gui on swing thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
			}
		});
	}
}
