package jadex.bpmn.tutorial;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

/**
 *  A task that displays a message using a JOptionPane.
 */
@Task(description="A task that displays a message using a JOptionPane.", parameters={
	@TaskParameter(name="message", clazz=String.class, direction=TaskParameter.DIRECTION_IN, description="The message to be shown."),
	@TaskParameter(name="title", clazz=String.class, direction=TaskParameter.DIRECTION_IN, description="The title of the dialog."),
	@TaskParameter(name="y_offset", clazz=int.class, direction=TaskParameter.DIRECTION_IN, description="The y offset.")
})
public class AsynchronousOKTask	 implements ITask
{
	protected JDialog	dialog;
	protected boolean	cancelled;
	
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess process)
	{
		final Future<Void> ret = new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(!cancelled)
				{
					String	message	= (String)context.getParameterValue("message");
					String	title	= (String)context.getParameterValue("title");
					int	offset	= context.hasParameterValue("y_offset")
						? ((Integer)context.getParameterValue("y_offset")).intValue() : 0;
					
					JOptionPane	pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
					dialog = new JDialog((JDialog)null, false);
					dialog.setTitle(title);
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dialog.setContentPane(pane);
					dialog.pack();
					Point	loc	= SGUI.calculateMiddlePosition(dialog);
					dialog.setLocation(loc.x, loc.y+offset);
					
					pane.addPropertyChangeListener(new PropertyChangeListener()
					{
						public void propertyChange(PropertyChangeEvent e)
						{
							String	prop	= e.getPropertyName();
							if(prop.equals(JOptionPane.VALUE_PROPERTY))
							{
				                dialog.dispose();
				                ret.setResult(null);
							}
						}
				    });
					
					dialog.setVisible(true);
				}
			}
		});
	
		return ret;
	}
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		cancelled	= true;
		final Future<Void> ret = new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(dialog!=null)
				{
					dialog.dispose();
				}
				ret.setResult(null);
			}
		});
		return ret;
	}
}
