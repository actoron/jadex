package jadex.bpmn.model;

import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ClassInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;


/**
 * Container for the current model.
 *
 */
public interface IModelContainer
{
	/**
	 *  Get the taskclasses.
	 *  @return The taskclasses.
	 */
	public List<ClassInfo> getTaskClasses();
	
	/**
	 *  Get the interfaces.
	 *  @return The interfaces.
	 */
	public List<ClassInfo> getInterfaces();
	
	/**
	 *  Returns the BPMN model.
	 *  @return BPMN model.
	 */
	public MBpmnModel getBpmnModel();
	
	/**
	 *  Tests if the state is dirty.
	 *  @return True, if dirty.
	 */
	public boolean isDirty();
	
	/**
	 *  Gets the project root.
	 *  @return The project root.
	 */
	public File getProjectRoot();
	
	/**
	 *  Returns the root for the project class loader.
	 *  @return The root of the project class loader.
	 */
	public File getProjectClassLoaderRoot();
	
//	/**
//	 *  Gets the global settings.
//	 *  @return The settings
//	 */
//	public Settings getSettings();
	
	/**
	 *  Gets the model file.
	 *  @return The model file.
	 */
	public File getFile();
	
	/**
	 *  Gets the project class loader.
	 *  @return The project class loader.
	 */
	public ClassLoader getProjectClassLoader();
	
	/**
	 *  Get the project task meta infos.
	 *  @return The meta infos.
	 */
	public Map<String, TaskMetaInfo> getProjectTaskMetaInfos();
	
	/**
	 *  Gets the edit mode.
	 *  @return The edit mode.
	 */
	public String getEditMode();
	
	/**
	 *  Gets the property panel container.
	 *  @return The property panel container.
	 */
	public JPanel getPropertypanelcontainer();

	/**
	 *  Adds a change listener. Currently only reports dirty events.
	 *  @param listener The listener.
	 */
	public void addChangeListener(ChangeListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public void removeChangeListener(ChangeListener listener);
	
}
