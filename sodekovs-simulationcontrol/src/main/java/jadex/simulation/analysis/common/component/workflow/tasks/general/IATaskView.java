package jadex.simulation.analysis.common.component.workflow.tasks.general;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jadex.simulation.analysis.common.component.workflow.tasks.SetModelTaskView;
import jadex.simulation.analysis.common.dataObjects.IADataObject;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.events.data.IADataObservable;
import jadex.simulation.analysis.common.events.task.IATaskListener;
import jadex.simulation.analysis.common.events.task.IATaskObservable;

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
	IATask getDisplayedObject();
	
	/**
	 * Sets the taskObject to display
	 * @param a {@link IATask}
	 */
	public void setDisplayedObject(IATask taskObject);
	
	/**
	 * Returns the generalComp which displays the taskObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();

	public TaskProperties getTaskProperties();


}