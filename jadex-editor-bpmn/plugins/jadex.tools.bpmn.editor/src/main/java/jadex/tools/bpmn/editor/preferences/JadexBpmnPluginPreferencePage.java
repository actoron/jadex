/**
 * 
 */
package jadex.tools.bpmn.editor.preferences;

import jadex.tools.bpmn.editor.JadexBpmnPlugin;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author claas
 * 
 */
public class JadexBpmnPluginPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{

	/**
	 * Default constructor
	 */
	public JadexBpmnPluginPreferencePage()
	{
		super("Jadex", GRID);
	}

	// ---- IWorkbenchPreferencePage implementation ----

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(JadexBpmnPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors()
	{
		addField(new JadexTaskProviderTypeListEditor(JadexPreferences.PREFERENCE_TASK_PROVIDER_LIST,
				"BPMN-Task Provider", getFieldEditorParent()));

	}

}
