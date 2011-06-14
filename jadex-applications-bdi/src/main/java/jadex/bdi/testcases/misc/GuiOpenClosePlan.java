package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *  Tests to open and close a gui.
 */
public class GuiOpenClosePlan extends Plan
{
	//-------- attributes --------

	/** The frame. */
	protected JFrame frame;

	/** The button. */
	protected JButton button;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public GuiOpenClosePlan()
	{
		frame = new JFrame();
		button = new JButton("close_me");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					getExternalAccess().scheduleStep(new IComponentStep()
					{
						@XMLClassname("closed")
						public Object execute(IInternalAccess ia)
						{
							IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
							IInternalEvent	event	= scope.getEventbase().createInternalEvent("gui_closed");
							scope.getEventbase().dispatchInternalEvent(event);
							return null;
						}
					});
				}
				catch(ComponentTerminatedException cte)
				{
				}
			}
		});
		frame.getContentPane().add("Center", button);
		frame.pack();
		frame.setLocation(SGUI.calculateMiddlePosition(frame));
		frame.setVisible(true);
	}

	//-------- methods --------

	/**
	 *  The body method.
	 */
	public void body()
	{
		// Timeout fails in sim mode because clock doesn't wait -> wait before event. (hack???)
		getWaitqueue().addInternalEvent("gui_closed");
		
		final Timer t = new Timer(50, null);
		t.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				t.stop();
				button.doClick();
			}
		});
		t.start();
		
		TestReport tr = new TestReport("#1", "Test closing a gui throws gui_event.");
		getLogger().info("Plan is waiting 3 seconds for gui close.");
		try
		{
			try
			{
				Thread.sleep(250);
			}
			catch(InterruptedException e)
			{
			}
			
			waitForInternalEvent("gui_closed", 3000); 
			getLogger().info("Gui was closed.");
			tr.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			getLogger().info("3 secs are over, gui was not closed.");
			tr.setReason("3 secs are over, gui was not closed.");
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				frame.dispose();
			}
		});
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

	/**
	 *  Cleanup when testcase is aborted.
	 */
	public void aborted()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				frame.dispose();
			}
		});
	}
}
