package jadex.editor.bpmn.editor.preferences;



import jadex.editor.bpmn.runtime.task.PreferenceTaskProviderProxy;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.swt.widgets.Composite;

/**
 * Jadex list editor
 * 
 * @author claas
 */
public class JadexTaskProviderTypeListEditor extends AbstractPreferenceListEditor
{
	
	private static JavaElementVerifier taskProviderVerifier = new JavaElementVerifier()
	{
		@Override
		IStatus verify(IJavaElement toVerify)
		{
			
			String typeFQN = "";
			if (toVerify instanceof IType)
			{
				typeFQN = ((IType)toVerify).getFullyQualifiedName();
			}
			return PreferenceTaskProviderProxy.checkTaskProviderClass(typeFQN);
		}
	};

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
	protected String getNewInputObject()
	{
		return getNewInputObject(IJavaElementSearchConstants.CONSIDER_CLASSES, "New Task Provider", "Please select the new TaskProvider", taskProviderVerifier);
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.editor.preferences.AbstractPreferenceListEditor#openSelectDialog(int, java.lang.String, java.lang.String)
	 */
	@Override
	protected IType openSelectDialog(int iJavaElementSearchConstant,
			String dialogTitle, String dialogMessage) throws JavaModelException
	{
		return super.selectType(iJavaElementSearchConstant, dialogTitle, dialogMessage);
	}
	
	

}