/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Claas
 *
 */
public abstract class RuntimeTaskProviderSupport implements IRuntimeTaskProvider
{

	// ---- constants ----
	
	/** The default meta info if class not found */
	static final ITaskMetaInfo NO_TASK_META_INFO_PROVIDED = new TaskMetaInfo("No TaskMetaInfo provided", new IParameterMetaInfo[0]);

	/**
	 * The provided task implementation classes for this {@link IRuntimeTaskProvider}
	 */
	protected String[] taskImplementations;
	
	/**
	 * Map for provided runtime classes<p>
	 * Map(ClassName, TaskMetaInfo)
	 */
	protected Map<String, TaskMetaInfo> metaInfoMap;


	// ---- constructor ----
	
	/**
	 * Empty default constructor
	 */
	public RuntimeTaskProviderSupport()
	{
		super();
		Package classPackage = this.getClass().getPackage();
		System.out.println(classPackage.toString());
		
		taskImplementations = new String[]{""};
		metaInfoMap = new HashMap<String, TaskMetaInfo>();
	}

	/**
	 * @param taskImplementations
	 * @param metaInfoMap
	 */
	public RuntimeTaskProviderSupport(String[] taskImplementations,
			HashMap<String, TaskMetaInfo> metaInfoMap)
	{
		super();
		this.taskImplementations = taskImplementations;
		this.metaInfoMap = metaInfoMap;
	}


	// ---- interface methods ----

	/**
	 * Get the provided task implementations
	 * Per default return an String[] with an empty String
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getAvailableTaskImplementations()
	 */
	public String[] getAvailableTaskImplementations()
	{
		return taskImplementations;
	}

	
	/**
	 * Get {@link TaskMetaInfo} for provided task implementation.
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfo(java.lang.String)
	 */
	public ITaskMetaInfo getTaskMetaInfo(String className)
	{
		if (metaInfoMap != null)
		{
			TaskMetaInfo info;
			if (metaInfoMap.containsKey(className))
			{
				info = metaInfoMap.get(className);
				if (info != null) 
					return info;
			}
			
			info = loadMetaInfo(className);
			if (info != null)
			{
				metaInfoMap.put(className, info);
				return info;
			}
		}
		
		return NO_TASK_META_INFO_PROVIDED;
	}


	// ---- helper methods ----
	
	/**
	 * Loads a class from the workspace and call its getTaskMetaInfo method
	 * to retrieve the TaskMetaInfo.
	 * @param className
	 * @return TaskMetaInfo for class if provided, else null
	 */
	private TaskMetaInfo loadMetaInfo(String className) {
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

		if (classLoader == null)
			return null;

		try {
			Class<?> taskImpl = classLoader.loadClass(className);
			Object taskInstance = taskImpl.newInstance();
			Method myMethod = taskImpl
					.getMethod(IRuntimeTaskProvider.METHOD_GET_TASK_META_INFO);
			TaskMetaInfo returnValue = (TaskMetaInfo) myMethod
					.invoke(taskInstance);

			return returnValue;

		} catch (ClassNotFoundException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (InstantiationException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (IllegalAccessException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (SecurityException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (NoSuchMethodException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (IllegalArgumentException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		} catch (InvocationTargetException e) {
			JadexBpmnEditor.log(e, IStatus.WARNING);
		}

		return null;
	}
	
	
    


}
