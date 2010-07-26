/**
 * 
 */
package jadex.tools.bpmn.editor.preferences;

import jadex.tools.bpmn.editor.JadexBpmnEditor;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author claas
 *
 */
public class JadexBpmnPluginPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	
	
	// ---- constructor ----
	
	/**
	 * Default constructor
	 */
	public JadexBpmnPluginPreferencePage() {
		super("Jadex", GRID);
	}

	// ---- IWorkbenchPreferencePage implementation ----
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) 
	{
		setPreferenceStore(JadexBpmnPlugin.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new JadexTaskRuntimeProviderTypeListEditor(
				"Runtime",
				"BPMN-Task Provider",
				getFieldEditorParent()));

	}
	
	/**
	 * Jadex list editor
	 * @author claas
	 */
	class JadexTaskRuntimeProviderTypeListEditor extends ListEditor 
	{

		private static final String LIST_DELIMITER = " ";

		/**
		 * Default constructor
		 * @param name
		 * @param labelText
		 * @param parent
		 */
		protected JadexTaskRuntimeProviderTypeListEditor(String name, String labelText,
				Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		protected String createList(String[] items) 
		{
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < items.length; i++) 
			{
				buffer.append(items[i]);
				if(i+1 < items.length)
				{
					buffer.append(LIST_DELIMITER);
				}
			}
			return buffer.toString();
		}

		@Override
		protected String getNewInputObject() 
		{
			String newInputObject = null;
			try 
			{
				IType selectedType = selectType();
				if (null != selectedType)
				{
					// TODO: load class and check methods?
					newInputObject = selectedType.getFullyQualifiedName();
				}
			}
			catch (JavaModelException e) 
			{
				JadexBpmnEditor.log(e, IStatus.ERROR);
			}
			
			return newInputObject;
		}

		@Override
		protected String[] parseString(String stringList) 
		{
			StringTokenizer tokenizer = new StringTokenizer(stringList,LIST_DELIMITER);
			ArrayList<String> list = new ArrayList<String>(tokenizer.countTokens());
			while(tokenizer.hasMoreTokens())
			{
				String value = tokenizer.nextToken();
				if (value != null && !value.equals(LIST_DELIMITER))
				{
					list.add(value);
				}
			}
			return list.toArray(new String[list.size()]);
			
		}
		
		/**
		 * Open a dialog to select a java type
		 * @return IType selected or null 
		 * @throws JavaModelException
		 */
		private IType selectType() throws JavaModelException 
		{
	        Shell parent = getShell();
	        
	        SelectionDialog dialog= JavaUI.createTypeDialog(
	            parent, new ProgressMonitorDialog(parent),
	            SearchEngine.createWorkspaceScope(),
	            IJavaElementSearchConstants.CONSIDER_CLASSES, false);
	        dialog.setTitle("New Task Provider");
	        dialog.setMessage("Please selexct the new TaskProvider");
	        
	        if (dialog.open() == IDialogConstants.CANCEL_ID)
	        {
	            return null;
	        }

	        Object[] types= dialog.getResult();
	        if (types == null || types.length == 0)
	        {
	            return null;
	        }
	        return (IType)types[0];
	    }
		
	}

}
