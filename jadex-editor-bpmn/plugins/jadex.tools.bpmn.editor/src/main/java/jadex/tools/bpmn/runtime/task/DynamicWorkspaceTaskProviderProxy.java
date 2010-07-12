/**
 * 
 */
package jadex.tools.bpmn.runtime.task;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.UniqueEList;

/**
 * @author Claas
 *
 */
public class DynamicWorkspaceTaskProviderProxy implements IRuntimeTaskProvider
{
	/** The list of ITaskProvider */
	private List<String> iTaskProviderList;
	
	/** Map for IRuntimeProvider access. className -> provider */
	private Map<String, IRuntimeTaskProvider> providerMap;

	/**
	 * Default constructor
	 */
	public DynamicWorkspaceTaskProviderProxy() {
		super();
		
		iTaskProviderList = new UniqueEList<String>();
		providerMap = new HashMap<String, IRuntimeTaskProvider>();
		
		// TODO: read plugin property
		iTaskProviderList.add("some.pack.age.MyTestProvider");
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getAvailableTaskImplementations()
	 */
	@Override
	public String[] getAvailableTaskImplementations()
	{
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper.getWorkspaceClassLoader(false);
		
		if (classLoader == null)
			return new String[]{""};
		
		
		for (String className : iTaskProviderList) {
			try {
				IRuntimeTaskProvider provider = (IRuntimeTaskProvider) classLoader.loadClass(className).newInstance();
				String[] tasks = provider.getAvailableTaskImplementations();
				for (int i = 0; i < tasks.length; i++) {
					providerMap.put(tasks[i], provider);
				}
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		return providerMap.keySet().toArray(new String[providerMap.size()]);
		
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfoFor(java.lang.String)
	 */
	@Override
	public TaskMetaInfo getTaskMetaInfoFor(String className)
	{
		
		IRuntimeTaskProvider provider = providerMap.get(className);
		if (provider != null)
		{
			return provider.getTaskMetaInfoFor(className);
		}
		else
		{
			return RuntimeTaskProviderSupport.NO_TASK_META_INFO_PROVIDED;
		}

	}

}