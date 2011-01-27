/**
 * 
 */
package jadex.editor.bpmn.editor.preferences;

import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author claas
 * 
 */
public class JadexBpmnClassLoaderPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{
	
	public static final String PREFERENCE_PAGE_ID = "jadex.editor.bpmn.editor.preferences.JadexBpmnClassLoaderPreferencePage";

	/**
	 * Default constructor
	 */
	public JadexBpmnClassLoaderPreferencePage()
	{
		super(PREFERENCE_PAGE_ID, GRID);
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
		setPreferenceStore(JadexBpmnEditorActivator.getDefault().getPreferenceStore());
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
		
		addField(new JadexTaskProviderTypeListEditor(JadexPreferencesPage.PREFERENCE_TASKPROVIDER_STRINGLIST,
				"BPMN-Task Provider", getFieldEditorParent()));
		
		addField(new JadexPackageListEditor(JadexPreferencesPage.PREFERENCE_TASKPROVIDER_SEARCH_PACKAGE_STRINGLIST,
				"Search packages for PackageBasedTaskProvider", getFieldEditorParent()));

	}
	
	

}
