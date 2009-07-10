package jadex.bpmn.runtime;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jadex.bpmn.model.MActivity;
import jadex.commons.SGUI;

/**
 * 
 */
public class EventIntermediateRuleActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void execute(final MActivity activity, final BpmnInstance instance, final ProcessThread thread, final ThreadContext context)
	{
		thread.setWaiting(true);
		
		final JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		JButton clickme = new JButton("click me");
		clickme.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				EventIntermediateRuleActivityHandler.this.notify(activity, instance, thread, context);
				frame.dispose();
			}
		});
		
		frame.setLocation(SGUI.calculateMiddlePosition(frame).x, SGUI.calculateMiddlePosition(frame).y);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				EventIntermediateRuleActivityHandler.this.notify(activity, instance, thread, context);
				System.exit(0);
			}
		});
		
		panel.add(clickme, BorderLayout.CENTER);
		frame.setContentPane(panel);
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
}
