/**
 * 
 */
package jadex.tools.bpmn.editor.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This is only a category page, maybe we add some preferences later.
 * 
 * @author Claas
 * 
 */
public class JadexPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	// ---- preference keys ----

	public static final String PREFERENCE_TASK_PROVIDER_LIST = "Task Provider";

	public static final String PREFERENCE_TASK_PROVIDER_SEARCH_PACKAGE_LIST = "Search packages";
	
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
