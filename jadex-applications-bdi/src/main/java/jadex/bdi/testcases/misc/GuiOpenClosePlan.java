package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IEAInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.ComponentTerminatedException;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;

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
					getExternalAccess().createInternalEvent("gui_closed").addResultListener(new IResultListener() 
					{
						public void resultAvailable(Object source, Object result) 
						{
							getExternalAccess().dispatchInternalEvent((IEAInternalEvent)result);
						}
						
						public void exceptionOccurred(Object source, Exception exception) 
						{
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
		TestReport tr = new TestReport("#1", "Test closing a gui throws gui_event.");
		final Timer t = new Timer(200, null);
		t.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				t.stop();
				button.doClick();
			}
		});
		t.start();
		getLogger().info("Plan is waiting 3 seconds for gui close.");
		try
		{
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
