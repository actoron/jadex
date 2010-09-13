/**
 * 
 */
package jadex.tools.bpmn.editor.preferences;

import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author claas
 *
 */
public class JadexBpmnPluginPreferenceInitializer extends
		AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = JadexBpmnPlugin.getDefault()
				.getPreferenceStore();

		store.setDefault(
				JadexPreferences.PREFERENCE_TASK_PROVIDER_LIST,
				JadexTaskProviderTypeListEditor
						.createStringList(new String[] {
								/*"jadex.tools.bpmn.runtime.task.StaticJadexTaskProvider" ,*/
								"jadex.tools.bpmn.runtime.task.PackageBasedTaskProvider" }));

	}

}
