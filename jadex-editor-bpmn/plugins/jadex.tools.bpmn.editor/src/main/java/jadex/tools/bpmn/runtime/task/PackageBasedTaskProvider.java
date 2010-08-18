/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.UniqueEList;

/**
 * @author claas
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
	 * @see jadex.tools.bpmn.runtime.task.IJadexTaskProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
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
	private void initializeDiscoverdClasses()
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
	
	private void initializeSearchPackages()
	{
		searchPackages = new UniqueEList<String>();
		
		// TODO: use preference instead of static list
		searchPackages.add("jadex.bpmn.runtime.task");
		searchPackages.add("jadex.bdibpmn.task");
		searchPackages.add("jadex.wfms.client.task");
	}
	
}
