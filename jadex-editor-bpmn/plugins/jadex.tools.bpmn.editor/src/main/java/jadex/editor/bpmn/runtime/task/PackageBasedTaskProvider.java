/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

import jadex.editor.bpmn.editor.JadexBpmnEditor;
import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;
import jadex.editor.bpmn.editor.preferences.JadexPackageListEditor;
import jadex.editor.bpmn.editor.preferences.JadexPreferencesPage;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.UniqueEList;

/**
 *
 */
public class PackageBasedTaskProvider extends TaskProviderSupport
{

	List<String> searchPackages; 
	List<String> discoveredTasks;
	
	// ---- constructors ----
	
	/**
	 * 
	 */
	public PackageBasedTaskProvider()
	{
		super();
		initializeSearchPackages();
		initializeDiscoverdClasses();
	}
	
	// ---- interface methods ---

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// nothing to dispose
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.TaskProviderSupport#refresh()
	 */
	@Override
	public void refresh()
	{
		super.refresh();
		WorkspaceClassLoaderHelper.getWorkspaceClassLoader(true);
		initializeSearchPackages();
		initializeDiscoverdClasses();
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.TaskProviderSupport#getAvailableTaskImplementations()
	 */
	@Override
	public String[] getAvailableTaskImplementations()
	{
		taskImplementations = discoveredTasks.toArray(new String[discoveredTasks.size()]);
		return taskImplementations;
	}
	
	/**
	 * Initialize the discovered classes list
	 */
	protected void initializeDiscoverdClasses()
	{
		discoveredTasks = new UniqueEList<String>();
		for (String aPackage : searchPackages)
		{
			try
			{
				List<Class<?>> classList = WorkspaceClassLoaderHelper
						.getClassesForPackage(aPackage, "Task.class");
				for (Class<?> clazz : classList)
				{
					discoveredTasks.add(clazz.getName());
				}
			}
			catch (Exception e)
			{
				JadexBpmnEditor.log("Problem during task discovering in "+this.getClass().getSimpleName(), e, IStatus.WARNING);
			}
		}
	}
	
	protected void initializeSearchPackages()
	{
		searchPackages = JadexPackageListEditor
						.parseStringList(JadexBpmnEditorActivator
								.getDefault()
								.getPreferenceStore()
								.getString(
										JadexPreferencesPage.PREFERENCE_TASKPROVIDER_SEARCH_PACKAGE_STRINGLIST));

	}
	
}
