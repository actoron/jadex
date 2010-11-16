/**
 * 
 */
package jadex.editor.bpmn.editor.preferences;

import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;

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
		IPreferenceStore store = JadexBpmnEditorActivator.getDefault()
				.getPreferenceStore();

		store.setDefault(JadexPreferences.PREFERENCE_EDITOR_CONVERT_BPMN_AUTOMATICALLY_BOOLEAN, true);
		
		store.setDefault(JadexPreferences.PREFERENCE_EDITOR_REGISTER_AS_DEFAULT_BOOLEAN, true);
		
		//store.setDefault(JadexPreferences.PREFERENCE_EDITOR_PREVIEW_DEFAULT_EDITOR_STRINGID, "");
		
		store.setDefault(JadexPreferences.PREFERENCE_TASKPROVIDER_STRINGLIST,
				AbstractPreferenceListEditor.createStringList(new String[] {
				/* "jadex.tools.bpmn.runtime.task.StaticJadexTaskProvider" , */
				"jadex.editor.bpmn.runtime.task.PackageBasedTaskProvider" }));

		store.setDefault(
				JadexPreferences.PREFERENCE_TASKPROVIDER_SEARCH_PACKAGE_STRINGLIST,
				AbstractPreferenceListEditor.createStringList(new String[] {
						"jadex.bpmn.runtime.task", 
						"jadex.bdibpmn.task",
						"jadex.wfms.client.task"}));

	}

}
