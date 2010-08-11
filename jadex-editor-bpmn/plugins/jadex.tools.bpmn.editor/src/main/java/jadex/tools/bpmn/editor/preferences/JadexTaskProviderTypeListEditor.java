package jadex.tools.bpmn.editor.preferences;

import jadex.tools.bpmn.editor.JadexBpmnEditor;
import jadex.tools.bpmn.runtime.task.PreferenceTaskProviderProxy;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * Jadex list editor
 * 
 * @author claas
 */
public class JadexTaskProviderTypeListEditor extends ListEditor
{
	/** The used delimiter */
	private static final String LIST_DELIMITER = " ";

	/**
	 * Default constructor
	 * 
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	protected JadexTaskProviderTypeListEditor(String name,
			String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}

	@Override
	protected String createList(String[] items)
	{
		return createStringList(items);
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
				// check IRuntimeTaskProvider or needed method implementation
				IStatus status = PreferenceTaskProviderProxy.checkTaskProviderClass(selectedType.getFullyQualifiedName());
				
				if (status.getSeverity() == IStatus.OK)
				{
					newInputObject = selectedType.getFullyQualifiedName();
				}
				else
				{
					ErrorDialog.openError(getShell(), "Error adding class",
							"Incompatible class selected", status);
				}

			}
		}
		catch (JavaModelException e)
		{
			JadexBpmnEditor.log("JavaModelException in JadexTaskProviderTypeListEditor#getNewInputObject()", e, IStatus.ERROR);
		}

		return newInputObject;
	}

	@Override
	protected String[] parseString(String stringList)
	{
		List<String> list = parseStringList(stringList);
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Open a dialog to select a java type
	 * 
	 * @return IType selected or null
	 * @throws JavaModelException
	 */
	private IType selectType() throws JavaModelException
	{
		Shell parent = getShell();

		SelectionDialog dialog = JavaUI.createTypeDialog(parent,
				new ProgressMonitorDialog(parent),
				SearchEngine.createWorkspaceScope(),
				IJavaElementSearchConstants.CONSIDER_CLASSES, false);
		dialog.setTitle("New Task Provider");
		dialog.setMessage("Please select the new TaskProvider");

		if (dialog.open() == IDialogConstants.CANCEL_ID)
		{
			return null;
		}

		Object[] types = dialog.getResult();
		if (types == null || types.length == 0 || types[0] == null)
		{
			return null;
		}
		return (IType) types[0];
	}
	
	// ---- static methods ----
	
	/**
	 * Create a String to for save in the preference store from String[]
	 * @param items 
	 * @return String for preference store
	 */
	public static String createStringList(String[] items)
	{
		StringBuffer buffer = new StringBuffer();
		if (items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				buffer.append(items[i]);
				if (i + 1 < items.length)
				{
					buffer.append(LIST_DELIMITER);
				}
			}
		}
		return buffer.toString();
	}
	
	/**
	 * Parse a preference from the preference store into a list
	 * @param stringList
	 * @return List of parsed strings
	 */
	public static List<String> parseStringList(String stringList)
	{
		StringTokenizer tokenizer = new StringTokenizer(stringList,
				LIST_DELIMITER);
		List<String> list = new UniqueEList<String>(
				tokenizer.countTokens());
		while (tokenizer.hasMoreTokens())
		{
			String value = tokenizer.nextToken();
			if (value != null && !value.equals(LIST_DELIMITER))
			{
				list.add(value);
			}
		}
		return list;

	}

}