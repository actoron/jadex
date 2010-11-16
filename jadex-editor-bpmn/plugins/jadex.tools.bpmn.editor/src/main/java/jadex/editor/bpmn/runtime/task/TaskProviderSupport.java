/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import jadex.editor.bpmn.editor.JadexBpmnEditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Claas
 *
 */
public abstract class TaskProviderSupport implements IEditorTaskProvider
{

	// ---- constants ----
	
	/** The default meta info if class not found */
	static final IEditorTaskMetaInfo NO_TASK_META_INFO_PROVIDED = new TaskMetaInfo("No TaskMetaInfo provided", new IEditorParameterMetaInfo[0]);

	/**
	 * The provided task implementation classes for this {@link IRuntimeTaskProvider}
	 */
	protected String[] taskImplementations;
	
	/**
	 * Map for provided runtime classes<p>
	 * Map(ClassName, TaskMetaInfo)
	 */
	protected Map<String, IEditorTaskMetaInfo> metaInfoMap;


	// ---- constructor ----
	
	/**
	 * Empty default constructor
	 */
	public TaskProviderSupport()
	{
		super();
		//Package classPackage = this.getClass().getPackage();
		//System.out.println(classPackage.toString());
		
		taskImplementations = new String[]{""};
		metaInfoMap = new HashMap<String, IEditorTaskMetaInfo>();
	}

	/**
	 * @param taskImplementations
	 * @param metaInfoMap
	 */
	public TaskProviderSupport(String[] taskImplementations,
			HashMap<String, IEditorTaskMetaInfo> metaInfoMap)
	{
		super();
		this.taskImplementations = taskImplementations;
		this.metaInfoMap = metaInfoMap;
	}


	// ---- interface methods ----

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		taskImplementations = null;
		
		if (metaInfoMap != null)
			metaInfoMap.clear();
		metaInfoMap = null;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskProvider#refresh()
	 */
	@Override
	public void refresh()
	{
		taskImplementations = new String[0];
		metaInfoMap.clear();
	}
	
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
	public IEditorTaskMetaInfo getTaskMetaInfo(String className)
	{
		if (className != null && !className.trim().isEmpty())
		{
			if (metaInfoMap != null)
			{
				IEditorTaskMetaInfo info;
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
		}
		
		return NO_TASK_META_INFO_PROVIDED;
	}


	// ---- helper methods ----
	
	/**
	 * Loads a class from the workspace and call its getTaskMetaInfo method
	 * to retrieve the TaskMetaInfo.
	 * @param className to load and call getMetaInfo() on
	 * @return TaskMetaInfo for class if provided, else null
	 */
	private IEditorTaskMetaInfo loadMetaInfo(String className) {
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

		if (classLoader == null || className == null || className.trim().isEmpty())
		{
			JadexBpmnEditor.log("Method loadMetaInfo('" + className
					+ "') failed in " + this.getClass().getSimpleName()
					+ " with '" + classLoader + "' as ClassLoader",
					new Exception(), IStatus.WARNING);
			return null;
		}
			
		try 
		{
			Class<?> taskImpl = classLoader.loadClass(className);
			Object taskInstance = taskImpl.newInstance();
			
			Object returnValue = WorkspaceClassLoaderHelper
			.callUnparametrizedReflectionMethod(
				taskInstance,
				IEditorTask.METHOD_IJADEXTASK_GET_TASK_METAINFO);

			if (returnValue instanceof IEditorTaskMetaInfo)
			{
				return (IEditorTaskMetaInfo) returnValue;
			}
			else if (returnValue != null)
			{
				return new TaskMetaInfoProxy(returnValue);
			} 

		}
		catch (Exception e)
		{
			JadexBpmnEditor.log("Exception while loading meta info for class '"+className+"' in "+this.getClass().getSimpleName(), e, IStatus.WARNING);
		}

		// fall through
		return NO_TASK_META_INFO_PROVIDED;
	}
	
	
    


}
