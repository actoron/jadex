package jadex.editor.bpmn.editor.preferences;

import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.swt.widgets.Composite;

/**
 * Jadex list editor
 * 
 * @author claas
 */
public class JadexPackageListEditor extends AbstractPreferenceListEditor
{

	private static JavaElementVerifier packageVerifier = new JavaElementVerifier()
	{
		@Override
		IStatus verify(IJavaElement toVerify)
		{
			if (null != toVerify
					&& (toVerify.getElementType() == IJavaElement.PACKAGE_FRAGMENT 
							|| toVerify.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT))
			{
				return new Status(IStatus.OK, JadexBpmnEditorActivator.ID, "Is package");
			}
			else
			{
				return new Status(IStatus.ERROR, JadexBpmnEditorActivator.ID,
						"Not a package declaration");
			}
		}
	};

	/**
	 * Default constructor
	 * 
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	protected JadexPackageListEditor(String name, String labelText,
			Composite parent)
	{
		super(name, labelText, parent);
	}

	@Override
	protected String getNewInputObject()
	{
		return getNewInputObject(
				IJavaElementSearchConstants.CONSIDER_ALL_TYPES,
				"New search package",
				"Please select the new package to search for tasks",
				packageVerifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.editor.preferences.AbstractPreferenceListEditor#
	 * openSelectDialog(int, java.lang.String, java.lang.String)
	 */
	@Override
	protected IJavaElement openSelectDialog(int iJavaElementSearchConstant,
			String dialogTitle, String dialogMessage) throws JavaModelException
	{
		return super.selectPackage(iJavaElementSearchConstant, dialogTitle,
				dialogMessage);
	}

}
