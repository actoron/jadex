package jadex.bpmn.examples.execute;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jadex.bpmn.runtime.handler.IExternalNotifier;
import jadex.bpmn.runtime.handler.Notifier;
import jadex.commons.gui.SGUI;

/**
 *  Notifier frame is an example for a very simple external system that
 *  can generate a notification for process continuation.
 */
public class NotifierFrame extends JFrame implements IExternalNotifier
{
	//-------- attributes --------
	
	/** The button for process notification. */
	protected JButton finishwait;
	
	/** The notifier to be called on process continuation. */
	protected Notifier notifier;
	
	//-------- constructors --------

	/**
	 *  Create a new Notifier. 
	 */
	public NotifierFrame()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				finishwait = new JButton("Notify process thread");
				finishwait.setEnabled(false);
				finishwait.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(notifier!=null)
						{
							notifier.notify(null);
							NotifierFrame.this.dispose();
						}
					}
				});
				getContentPane().setLayout(new BorderLayout());
				getContentPane().add(finishwait, BorderLayout.CENTER);
				pack();
				setLocation(SGUI.calculateMiddlePosition(NotifierFrame.this));
				setVisible(true);
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Activate a wait action on an external source.
	 *  @param properties The properties.
	 *  @param notifier The notifier.
	 */
	public void activateWait(Map properties, Notifier notifier)
	{
		this.notifier = notifier;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				finishwait.setEnabled(true);
			}
		});
	}
	
	
	/**
	 *  Cancel the wait action.
	 */
	public void cancel()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				dispose();
			}
		});	
	}
}
