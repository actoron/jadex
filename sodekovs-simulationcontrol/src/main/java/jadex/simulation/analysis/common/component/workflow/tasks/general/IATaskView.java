package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.simulation.analysis.common.dataObjects.IADataObject;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.events.IADataObservable;
import jadex.simulation.analysis.common.events.IATaskListener;

import javax.swing.JComponent;

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
	public IATaskObservable getDisplayedObject();
	
	/**
	 * Sets the taskObject to display
	 * @param a {@link IATask}
	 */
	public void setDisplayedObject(IATaskObservable taskObject);
	
	/**
	 * Returns the component which displays the taskObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();
}