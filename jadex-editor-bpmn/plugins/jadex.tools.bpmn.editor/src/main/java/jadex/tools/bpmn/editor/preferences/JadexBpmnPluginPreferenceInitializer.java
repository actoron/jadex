/**
 * 
 */
package jadex.tools.bpmn.editor.preferences;

import jadex.tools.bpmn.editor.JadexBpmnEditor;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

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
		IEclipsePreferences prefs = new DefaultScope()
				.getNode(JadexBpmnPlugin.PLUGIN_PREFERENCE_SCOPE);

		prefs.put(
				JadexBpmnEditor.PREFERENCE_TASK_PROVIDER_LIST,
				JadexTaskProviderTypeListEditor
						.createStringList(new String[] {
								"jadex.tools.bpmn.runtime.task.StaticJadexTaskProvider",
								"jadex.tools.bpmn.runtime.task.PackageBasedTaskProvider" }));

	}

}
