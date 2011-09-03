package jadex.simulation.analysis.common.superClasses.tasks;

import jadex.commons.gui.PropertiesPanel;
import jadex.simulation.analysis.common.superClasses.events.IAListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 * A Task View for analysis tasks
 * @author 5Haubeck
 *
 */
public interface IATaskView extends IAListener
{
	/**
	 * Returns Object for concurrent access
	 * @return mutex Object
	 */
	public Object getMutex();
	
	/**
	 * Returns the {@link IATask} which is displayed
	 * @return displayed {@link IATask} 
	 */
	public IATask getDisplayedTask();
	
	/**
	 * Returns the {@link JComponent} which displays the {@link IATask}
	 * @return {@link JComponent} to display
	 */
	public JComponent getComponent();

	/**
	 * A {@link TaskProperties} to display. See Jadex {@link PropertiesPanel}
	 * @return displayed {@link TaskProperties}
	 */
	public TaskProperties getTaskProperties();
	
	/**
	 * A Frame to display Task.
	 * @param {@link JInternalFrame} to display in
	 */
	public void setParent(JInternalFrame frame);
}