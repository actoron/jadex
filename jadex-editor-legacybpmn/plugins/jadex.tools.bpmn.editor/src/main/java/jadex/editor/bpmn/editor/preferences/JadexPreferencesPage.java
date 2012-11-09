/**
 * 
 */
package jadex.editor.bpmn.editor.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This is only a category page, maybe we add some preferences later.
 * 
 * @author Claas
 * 
 */
public class JadexPreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	// ---- preference keys ----

	public static final String PREFERENCE_EDITOR_CONVERT_BPMN_AUTOMATICALLY_BOOLEAN = "Automatically convert BPMN files to new format";
	
	
	public static final String PREFERENCE_EDITOR_REGISTER_AS_DEFAULT_BOOLEAN = "Register as default for *.bpmn files";
	
	public static final String PREFERENCE_EDITOR_PREVIEW_DEFAULT_EDITOR_STRINGID = "Preview default *.bpmn editor";
	
	
	public static final String PREFERENCE_TASKPROVIDER_STRINGLIST = "Task Provider";

	public static final String PREFERENCE_TASKPROVIDER_SEARCH_PACKAGE_STRINGLIST = "Search packages";
	
	// ---- interface implementation ----
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		// no preference store to initialize
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
		// currently nothing to to
	}

}
