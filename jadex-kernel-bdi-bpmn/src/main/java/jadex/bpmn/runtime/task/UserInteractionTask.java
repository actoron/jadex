package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *  Opens a dialog for the task and lets the user enter
 *  result parameters.
 */
public class UserInteractionTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @listener	To be notified, when the task has completed.
	 */
	public void execute(final ITaskContext context, final IResultListener listener)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JOptionPane	pane	= new JOptionPane("Please enter values for task "+context.getModelElement().getName(), JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
				pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
				
				final JDialog	dialog	= new JDialog((JFrame)null, context.getModelElement().getName());
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(pane, BorderLayout.CENTER);

		        dialog.addWindowListener(new WindowAdapter()
		        {
		            public void windowClosing(WindowEvent we)
		            {
		                pane.setValue(null);
		            }
		        });

		        pane.addPropertyChangeListener(new PropertyChangeListener()
		        {
		            public void propertyChange(PropertyChangeEvent event)
		            {
		            	if(pane.getValue()!=JOptionPane.UNINITIALIZED_VALUE)
		            	{
			            	// Close window, if button was pressed.
			                if(pane.getValue()!=null)
			                {
			                    dialog.setVisible(false);
			                }
		                
			                if(pane.getValue()==null || ((Integer)pane.getValue()).intValue()==JOptionPane.CANCEL_OPTION)
			                {
								Exception	e	= new RuntimeException("Task not completed");
								e.fillInStackTrace();
								listener.exceptionOccurred(e);		                	
			                }
			                else
			                {
								listener.resultAvailable(null);
			                }
		            	}
		            }
		        });

				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			}
		});
	}
}
