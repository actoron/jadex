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
	
	/**
	 * 
	 */
	public PackageBasedTaskProvider()
	{
		super();
		initializeSearchPackages();
		initializeDiscoverdClasses();
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.TaskProviderSupport#getAvailableTaskImplementations()
	 */
	@Override
	public String[] getAvailableTaskImplementations()
	{
		// TODO: what about re-init discovered classes ?
		return discoveredTasks.toArray(new String[discoveredTasks.size()]);
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
		
		searchPackages.add("jadex.bpmn.runtime.task");
		searchPackages.add("jadex.bdibpmn.task");
		searchPackages.add("jadex.wfms.client.task");
	}
	
}
