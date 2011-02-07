package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *  A task that displays a message using a JOptionPane.
 */
public class AsynchronousOKTask	 implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter process)
	{
		final Future ret = new Future();
		
		String	message	= (String)context.getParameterValue("message");
		String	title	= (String)context.getParameterValue("title");
		int	offset	= context.hasParameterValue("y_offset")
			? ((Integer)context.getParameterValue("y_offset")).intValue() : 0;
		
		JOptionPane	pane	= new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
		final JDialog	dialog	= new JDialog((JDialog)null, title, false);
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
	                dialog.setVisible(false);
//	                listener.resultAvailable(this, null);
	                ret.setResult(null);
				}
			}
	    });

		
		dialog.setVisible(true);
	
		return ret;
	}
}
