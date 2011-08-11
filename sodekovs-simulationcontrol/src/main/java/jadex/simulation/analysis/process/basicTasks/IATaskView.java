package jadex.simulation.analysis.process.basicTasks;

import jadex.simulation.analysis.common.events.task.IATaskListener;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

public interface IATaskView extends IATaskListener
{
	/**
	 * Synchronize Object of the {@link IATask}
	 * 
	 * @return Mutex of the {@link IATask}
	 */
	public Object getMutex();
	
	/**
	 * Returns the taskObject which is displayed
	 * @return a {@link IATask}
	 */
	IATask getDisplayedObject();
	
//	/**
//	 * Sets the taskObject to display
//	 * @param a {@link IATask}
//	 */
//	public void setDisplayedObject(IATask taskObject);
	
	/**
	 * Returns the generalComp which displays the taskObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();

	public TaskProperties getTaskProperties();
	
	public void setParent(JInternalFrame frame);


}