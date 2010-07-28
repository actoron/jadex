/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;
import jadex.tools.bpmn.editor.preferences.JadexTaskProviderTypeListEditor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.UniqueEList;

/**
 * This TaskProvider reads the Jadex BPMN preference list for TaskProvider,
 * collects all provided tasks and returns them in a single list.
 * 
 * The getTaskMetaInfoFor(className) method is a proxy to the original 
 * TaskProvider method.
 * 
 * @author Claas
 * 
 */
public class PreferenceTaskProviderProxy implements IRuntimeTaskProvider
{
	/** The list of ITaskProvider */
	private static List<String> iTaskProviderCache;

	/** Map for IRuntimeProvider access. className -> provider */
	private Map<String, Object> providerMap;

	/**
	 * Default constructor
	 */
	public PreferenceTaskProviderProxy()
	{
		super();

		iTaskProviderCache = new UniqueEList<String>();
		providerMap = new HashMap<String, Object>();

		// initialize map and cache
		getAvailableTaskImplementations();
	}

	/**
	 * Returns a compound array of all Tasks defined by the TaskProviders
	 * in from the Jadex preference store list.  
	 * 
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#
	 * getAvailableTaskImplementations()
	 */
	@Override
	public String[] getAvailableTaskImplementations()
	{

		List<String> preferenceList = JadexTaskProviderTypeListEditor.parseStringList(JadexBpmnPlugin.getDefault().getPreferenceStore().getString(JadexBpmnEditor.PREFERENCE_TASK_PROVIDER_LIST));
		if (!iTaskProviderCache.equals(preferenceList))
		{
			loadTaskMetaInfos(preferenceList);
		}
		
		return providerMap.keySet().toArray(new String[providerMap.size()]);

	}
	
	/**
	 * Loads the provider and provided TaskMetaInfos into the provider map
	 * 
	 * @param iTaskProviderList to load
	 */
	protected void loadTaskMetaInfos(List<String> iTaskProviderList)
	{
		// replace cache and clear map
		iTaskProviderCache = iTaskProviderList;
		providerMap.clear();
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

		if (classLoader == null)
		{
			return;
		}

		for (String className : iTaskProviderList)
		{
			try
			{
				
				
				Object obj = classLoader
						.loadClass(className).newInstance();
				
				if (obj instanceof IRuntimeTaskProvider)
				{
					IRuntimeTaskProvider provider = (IRuntimeTaskProvider) obj;
					String[] tasks = provider.getAvailableTaskImplementations();
					for (int i = 0; i < tasks.length; i++)
					{
						providerMap.put(tasks[i], provider);
					}
				} 
				else
				{
					// FIXME: use reflection if needed
				}
				
			}
			catch (Exception e)
			{
				JadexBpmnEditor.log(e, IStatus.ERROR);
			}
			
		}
	}

	/**
	 * Proxy method to the original TaskProvider method
	 * 
	 * @see
	 * jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfo
	 * (java.lang.String)
	 */
	@Override
	public ITaskMetaInfo getTaskMetaInfo(String fqClassName)
	{

		Object obj = providerMap.get(fqClassName);
		if (obj != null && obj instanceof IRuntimeTaskProvider)
		{
			// use interface if implemented
			IRuntimeTaskProvider provider = (IRuntimeTaskProvider) obj;
			return provider.getTaskMetaInfo(fqClassName);
		}
		else if (obj != null)
		{
			try
			{
				// use reflection
				Method getTaskMetaInfoMethod = obj.getClass()
						.getMethod(IRuntimeTaskProvider.METHOD_GET_TASK_META_INFO);
				Object returnValue = getTaskMetaInfoMethod.invoke(obj, new Object[]{fqClassName});
				
				// check the return value
				if (returnValue instanceof ITaskMetaInfo)
				{
					return (ITaskMetaInfo) returnValue;
				}
				else
				{
					// FIXME: use reflection proxy
				}
			}
			catch (Exception e)
			{
				JadexBpmnEditor.log(e, IStatus.ERROR);
			}
			
			
		}
		
		// fall through
		return RuntimeTaskProviderSupport.NO_TASK_META_INFO_PROVIDED;

	}
	
	public static IStatus checkTaskProviderClass(String fullQualifiedClassName)
	{
		IStatus status = null;
		
		if (iTaskProviderCache.contains(fullQualifiedClassName))
		{
			status = new Status(IStatus.OK, JadexBpmnPlugin.ID, "Already in list");;
		}
		
		try
		{
			// first check implementing marker interface
			ClassLoader classLoader = WorkspaceClassLoaderHelper
					.getWorkspaceClassLoader(false);
			Class<?> clazz = classLoader.loadClass(fullQualifiedClassName);
			if (Arrays.asList(clazz.getInterfaces()).contains(
					IRuntimeTaskProvider.class))
			{
				status = new Status(IStatus.OK, JadexBpmnPlugin.ID, "Implements IRuntimeTaskProvider");
			}
			
			// second check implementing needed methods
			Method getAvailableTasksMethod = clazz
					.getMethod(IRuntimeTaskProvider.METHOD_GET_AVAILABLE_TASK_IMPLEMENTATIONS);
			Method getTaskMetaInfoMethod = clazz
					.getMethod(IRuntimeTaskProvider.METHOD_GET_TASK_META_INFO);
			
			if (getAvailableTasksMethod != null && getTaskMetaInfoMethod != null)
			{
				status = new Status(IStatus.OK, JadexBpmnPlugin.ID, "Implements '"+IRuntimeTaskProvider.METHOD_GET_AVAILABLE_TASK_IMPLEMENTATIONS+"' and '"+IRuntimeTaskProvider.METHOD_GET_TASK_META_INFO+"'");
			}
		}
		// if something goes wrong, report it
		catch (Exception e)
		{
			JadexBpmnEditor.log(e, IStatus.ERROR);
			status = new Status(
					IStatus.ERROR,
					JadexBpmnPlugin.ID,
					IStatus.CANCEL,
					"Exception checking '"
							+ fullQualifiedClassName
							+ "'. The selected class does not implement the IRuntimeTaskProvider interface nor the specified methods",
					e);
		}
		
		return status;
	}

}
