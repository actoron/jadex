/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import jadex.editor.bpmn.editor.JadexBpmnEditor;
import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;
import jadex.editor.bpmn.editor.preferences.JadexPreferencesPage;
import jadex.editor.bpmn.editor.preferences.JadexTaskProviderTypeListEditor;

import java.io.InvalidObjectException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EModelElement;

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
public class PreferenceTaskProviderProxy implements IEditorTaskProvider
{
	// ---- instance ----
	
	/** The provider-proxy instance */
	private static IEditorTaskProvider instance;
	
	/** Access the default instance for {@link PreferenceTaskProviderProxy} */
	public static IEditorTaskProvider getInstance()
	{
		if (instance == null)
		{
			instance = new PreferenceTaskProviderProxy();
		}
		
		return instance;
	}
	
	// ---- class ----
	
	/** The list of ITaskProvider */
	private static HashMap<String, IEditorTaskProvider> iTaskProviderCache;

	/** Map for IRuntimeProvider access. className -> provider */
	private SortedMap<String, Object> fqClassname2providerMap;
	
	/** The lastly selected input EModelElement */
	private EModelElement inputElement;
	
	
	
	/**
	 * Default constructor
	 */
	private PreferenceTaskProviderProxy()
	{
		super();

		iTaskProviderCache = new HashMap<String, IEditorTaskProvider>();
		fqClassname2providerMap = new TreeMap<String, Object>();

		// initialize map and cache
		getAvailableTaskImplementations();
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// nothing to dispose
	}
	
	/*
	 * (non-Javadoc)
	 * @see jadex.editor.bpmn.runtime.task.IEditorTaskProvider#setInput(org.eclipse.emf.ecore.EModelElement)
	 */
	@Override
	public void setInput(EModelElement selectedElement)
	{
		this.inputElement = selectedElement;
		
		// set selected element in all task providers
		for (IEditorTaskProvider provider : iTaskProviderCache.values())
		{
			provider.setInput(selectedElement);
		}
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskProvider#refresh()
	 */
	@Override
	public void refresh()
	{
		iTaskProviderCache.clear();
		fqClassname2providerMap.clear();
		WorkspaceClassLoaderHelper.getWorkspaceClassLoader(true);
		//getAvailableTaskImplementations();
	}

	/**
	 * Returns a compound array of all Tasks defined by the TaskProviders
	 * in from the Jadex preference store list.  
	 * 
	 * @see jadex.editor.bpmn.runtime.task.IEditorTaskProvider#
	 * getAvailableTaskImplementations()
	 */
	@Override
	public String[] getAvailableTaskImplementations()
	{

		Set<String> preferenceSet = new HashSet<String>(
				JadexTaskProviderTypeListEditor
						.parseStringList(JadexBpmnEditorActivator
								.getDefault()
								.getPreferenceStore()
								.getString(
										JadexPreferencesPage.PREFERENCE_TASKPROVIDER_STRINGLIST)));

		boolean clearProviderCache = !iTaskProviderCache.keySet().equals(preferenceSet);
		loadTaskMetaInfos(preferenceSet, clearProviderCache);
		
		return fqClassname2providerMap.keySet().toArray(new String[fqClassname2providerMap.size()]);

	}
	
	/**
	 * Loads the provider and provided TaskMetaInfos into the provider map
	 * 
	 * @param iTaskProviderList to load
	 */
	protected void loadTaskMetaInfos(Set<String> iTaskProviderList, boolean clearProviderCache)
	{
		if (clearProviderCache)
		{
			iTaskProviderCache.clear();
		}
		
		// clear task list 2 provider map
		fqClassname2providerMap.clear();
		
		ClassLoader classLoader = WorkspaceClassLoaderHelper
				.getWorkspaceClassLoader(false);

		if (classLoader == null)
		{
			return;
		}

		for (String className : iTaskProviderList)
		{
			// load new classes
			if (!iTaskProviderCache.containsKey(className))
			{
				try
				{
					Class<?> clazz = classLoader.loadClass(className);
					Object instance = clazz.newInstance();
					
					IEditorTaskProvider provider;
					
					if (instance instanceof IEditorTaskProvider)
					{
						provider = (IEditorTaskProvider) instance;
					} 
					else
					{
						// use reflection proxy
						provider = new TaskProviderProxy(instance);
					}

					iTaskProviderCache.put(className, provider);
					
				}
				catch (Exception e)
				{
					JadexBpmnEditor.log("Problem during TaskMetaInfo load in "+this.getClass().getSimpleName(), e, IStatus.ERROR);
				}
			}
			
			String[] tasks;
			for (IEditorTaskProvider provider : iTaskProviderCache.values())
			{
				provider.setInput(inputElement);
				provider.refresh();
				
				// retrieve tasks
				tasks = provider.getAvailableTaskImplementations();
				
				for (int i = 0; i < tasks.length; i++)
				{
					fqClassname2providerMap.put(tasks[i], provider);
					// Insert mapping for unqualified class names (hack???)
					if(tasks[i].indexOf('.')!=-1)
					{
						fqClassname2providerMap.put(tasks[i].substring(tasks[i].lastIndexOf('.')+1), tasks[i]);
					}
				}
			}

		}
	}

	/**
	 * Proxy method to the original TaskProvider method
	 * 
	 * @see
	 * jadex.editor.bpmn.runtime.task.IEditorTaskProvider#getTaskMetaInfo
	 * (java.lang.String)
	 */
	@Override
	public IEditorTaskMetaInfo getTaskMetaInfo(String fqClassName)
	{
		IEditorTaskMetaInfo	ret;
		
		Object obj = fqClassname2providerMap.get(fqClassName);
		if(obj instanceof String)
		{
			// Call recursively with full name.
			ret	= getTaskMetaInfo((String)obj);
		}
		else if(obj instanceof IEditorTaskProvider)
		{
			// use interface if implemented
			IEditorTaskProvider provider = (IEditorTaskProvider) obj;
			ret	= provider.getTaskMetaInfo(fqClassName);
		}
		else if(obj != null)
		{
			throw new RuntimeException("Unexpected class in "+this, new InvalidObjectException("Object doesn't implement the IEditorTaskProvider interface nor its a Proxy object!" + obj));
		}
		else
		{
			ret	= TaskProviderSupport.NO_TASK_META_INFO_PROVIDED;
		}
		
		return ret;
	}
	
	public static IStatus checkTaskProviderClass(String fullQualifiedClassName)
	{
		IStatus status = null;
		
		if (iTaskProviderCache.keySet().contains(fullQualifiedClassName))
		{
			status = new Status(IStatus.OK, JadexBpmnEditorActivator.ID, "Already in list");;
		}
		
		try
		{
			// first check implementing marker interface
			ClassLoader classLoader = WorkspaceClassLoaderHelper
					.getWorkspaceClassLoader(false);
			Class<?> clazz = classLoader.loadClass(fullQualifiedClassName);
			if (Arrays.asList(clazz.getInterfaces()).contains(
					IEditorTaskProvider.class))
			{
				status = new Status(IStatus.OK, JadexBpmnEditorActivator.ID, "Implements IRuntimeTaskProvider");
				return status;
			}
			
			// second check implementing needed methods
			Method getAvailableTasksMethod = clazz
					.getMethod(IEditorTaskProvider.METHOD_IJADEXTASKPROVIDER_GET_AVAILABLE_TASK_IMPLEMENTATIONS);
			Method getTaskMetaInfoMethod = clazz
					.getMethod(IEditorTaskProvider.METHOD_IJADEXTASKPROVIDER_GET_TASK_META_INFO);
			
			if (getAvailableTasksMethod != null && getTaskMetaInfoMethod != null)
			{
				status = new Status(IStatus.OK, JadexBpmnEditorActivator.ID, "Implements '"+IEditorTaskProvider.METHOD_IJADEXTASKPROVIDER_GET_AVAILABLE_TASK_IMPLEMENTATIONS+"' and '"+IEditorTaskProvider.METHOD_IJADEXTASKPROVIDER_GET_TASK_META_INFO+"'");
				return status;
			}
		}
		// if something goes wrong, report it
		catch (Exception e)
		{
			JadexBpmnEditor.log("Exception during task provider check in "+PreferenceTaskProviderProxy.class.getSimpleName(), e, IStatus.ERROR);
			status = new Status(
					IStatus.ERROR,
					JadexBpmnEditorActivator.ID,
					IStatus.CANCEL,
					"Exception checking '"
							+ fullQualifiedClassName
							+ "'. The selected class does not implement the IRuntimeTaskProvider interface nor the specified methods",
					e);
		}
		
		return status;
	}

	

	

}
