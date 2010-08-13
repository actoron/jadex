package jadex.bdi.tutorial;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IEAInternalEvent;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.Plan;
import jadex.commons.concurrent.SwingDefaultResultListener;

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
				((IEAInternalEvent)ae.getSource()).getParameterValue("content").addResultListener(new SwingDefaultResultListener(gui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						gui.addRow((String[])result);	
					}
				});
//				gui.addRow((String[]));
			}
		});
		
		getScope().addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
//				System.out.println("terminating");
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						gui.dispose();
					}
				});
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});
		
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
