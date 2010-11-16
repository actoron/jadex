package jadex.tools.gpmn.diagram.part;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class AbstractFileAction {

	protected IWorkbench targetPartWorkbench;

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		targetPartWorkbench = null;
		IWorkbench workbench = targetPart.getSite().getWorkbenchWindow().getWorkbench();
		targetPartWorkbench = workbench;
		//Display display = workbench.getDisplay();
	}

	protected IProject getActiveProject() {
		IProject activeProject = null;
	
		IFileEditorInput fileInput = getActiveFileEditorInput();
		if (fileInput != null)
		{
			activeProject = fileInput.getFile().getProject();
		}
	
		return activeProject;
	}

	protected IFileEditorInput getActiveFileEditorInput() {
		IFileEditorInput activeFileEditorInput = null;
		IWorkbenchWindow window = targetPartWorkbench
				.getActiveWorkbenchWindow();
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				IEditorPart editor = page.getActiveEditor();
				if (editor != null)
				{
					IEditorInput input = editor.getEditorInput();
					if (input instanceof IFileEditorInput)
					{
						activeFileEditorInput = (IFileEditorInput) input;
					}
				}
			}
		}
		
		return activeFileEditorInput;
	}

}