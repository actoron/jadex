package jadex.bpmn.runtime.handler.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bridge.IInternalAccess;
import jadex.commons.gui.SGUI;

/**
 *  Handler that opens a window and waits for the user to click a button.
 */
public class UserInteractionActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
//		thread.setWaitingState(ProcessThread.WAITING_FOR_TASK);
		thread.setWaiting(true);
		
		final JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		JButton clickme = new JButton("click me");
		clickme.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				getBpmnFeature(instance).notify(activity, thread, null);
				frame.dispose();
			}
		});
		
		frame.setLocation(SGUI.calculateMiddlePosition(frame).x, SGUI.calculateMiddlePosition(frame).y);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				getBpmnFeature(instance).notify(activity, thread, null);
//				System.exit(0);
			}
		});
		
		panel.add(clickme, BorderLayout.CENTER);
		frame.setContentPane(panel);
		
		frame.pack();
		frame.setVisible(true);
	}
	
}
