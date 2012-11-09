package jadex.editor.bpmn.editor.preferences;

import jadex.editor.bpmn.editor.JadexBpmnEditor;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.IJavaElement;
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

public abstract class AbstractPreferenceListEditor extends ListEditor
{

	/** The used delimiter */
	private static final String LIST_DELIMITER = " ";

	/** Empty constructor */
	public AbstractPreferenceListEditor()
	{
		super();
	}

	/** Default constructor */
	public AbstractPreferenceListEditor(String name, String labelText,
			Composite parent)
	{
		super(name, labelText, parent);
	}
	
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

	@Override
	protected String createList(String[] items)
	{
		return createStringList(items);
	}

	
	protected String getNewInputObject(int iJavaElementSearchConstant, String dialogTitle, String dialogMessage, JavaElementVerifier verifier)
	{
		String newInputObject = null;
		
		try 
		{
			IJavaElement selectedElement = openSelectDialog(iJavaElementSearchConstant, dialogTitle, dialogMessage);
			if (null != selectedElement)
			{
				// check the selected type
				IStatus status = verifier.verify(selectedElement);
				
				if (status.getSeverity() == IStatus.OK)
				{
					newInputObject = selectedElement.getElementName(); // getFullyQualifiedName();
				}
				else
				{
					ErrorDialog.openError(getShell(), "Error adding Type",
							"Incompatible selection", status);
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
	 * Override in subclass to use type / package dialog
	 * 
	 * @param iJavaElementSearchConstant constant from {@link IJavaElementSearchConstants}
	 * @param dialogTitle the Title
	 * @param dialogMessage the Message
	 * @return
	 */
	protected abstract IJavaElement openSelectDialog(int iJavaElementSearchConstant, String dialogTitle, String dialogMessage) throws JavaModelException;
	
	/**
	 * Open a dialog to select a java type
	 * 
	 * @return IType selected or null
	 * @throws JavaModelException
	 */
	protected IType selectType(int iJavaElementSearchConstant, String dialogTitle, String dialogMessage) throws JavaModelException
	{
		Shell parent = getShell();
	
		SelectionDialog dialog = JavaUI.createTypeDialog(parent,
				new ProgressMonitorDialog(parent),
				SearchEngine.createWorkspaceScope(),
				iJavaElementSearchConstant, false);
		dialog.setTitle(dialogTitle);
		dialog.setMessage(dialogMessage);
	
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

	/**
	 * Open a dialog to select a java type
	 * 
	 * @return IType selected or null
	 * @throws JavaModelException
	 */
	protected IJavaElement selectPackage(int iJavaElementSearchConstant, String dialogTitle, String dialogMessage) throws JavaModelException
	{
		Shell parent = getShell();
	
		SelectionDialog dialog = JavaUI.createPackageDialog(parent,
				new ProgressMonitorDialog(parent),
				SearchEngine.createWorkspaceScope(),
				false, true, "");
		dialog.setTitle(dialogTitle);
		dialog.setMessage(dialogMessage);
	
		if (dialog.open() == IDialogConstants.CANCEL_ID)
		{
			return null;
		}
	
		Object[] elements = dialog.getResult();
		if (elements == null || elements.length == 0 || elements[0] == null)
		{
			return null;
		}
		return (IJavaElement) elements[0];
	}
	
	/**
	 * Class to verify a selected Type
	 * @author Claas
	 *
	 */
	protected abstract static class JavaElementVerifier 
	{
		/**
		 * Verify a selected IType with this verifier
		 * @param typeToVerify IJavaElement to verify
		 * @return The IStatus verify result
		 */
		abstract IStatus verify(IJavaElement toVerify);
	}

}
