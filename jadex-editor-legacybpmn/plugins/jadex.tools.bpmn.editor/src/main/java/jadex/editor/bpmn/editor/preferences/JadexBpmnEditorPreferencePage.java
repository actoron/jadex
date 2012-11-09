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
public class JadexBpmnEditorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{
	
	public static final String PREFERENCE_PAGE_ID = "jadex.editor.bpmn.editor.preferences.JadexBpmnEditorPreferencePage";

	/**
	 * Default constructor
	 */
	public JadexBpmnEditorPreferencePage()
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
		
		addField(new BooleanFieldEditor(JadexPreferencesPage.PREFERENCE_EDITOR_REGISTER_AS_DEFAULT_BOOLEAN, "Register as default editor for *.bpmn", getFieldEditorParent()));
		
	}
	
	

}
