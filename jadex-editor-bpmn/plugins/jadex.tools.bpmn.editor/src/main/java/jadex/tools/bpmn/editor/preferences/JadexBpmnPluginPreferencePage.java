/**
 * 
 */
package jadex.tools.bpmn.editor.preferences;

import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
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
		
		addField(new BooleanFieldEditor(JadexPreferences.PREFERENCE_EDITOR_REGISTER_AS_DEFAULT_BOOLEAN, "Register as default editor for *.bpmn", getFieldEditorParent()));
		
		addField(new JadexTaskProviderTypeListEditor(JadexPreferences.PREFERENCE_TASKPROVIDER_STRINGLIST,
				"BPMN-Task Provider", getFieldEditorParent()));
		
		addField(new JadexPackageListEditor(JadexPreferences.PREFERENCE_TASKPROVIDER_SEARCH_PACKAGE_STRINGLIST,
				"Search packages for PackageBasedTaskProvider", getFieldEditorParent()));

	}

}
