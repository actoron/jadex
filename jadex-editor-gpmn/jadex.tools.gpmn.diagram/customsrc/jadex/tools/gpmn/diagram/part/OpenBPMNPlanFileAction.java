/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.BpmnPlan;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanEditPart;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

/**
 * @author claas
 *
 */
public class OpenBPMNPlanFileAction extends AbstractFileAction implements IObjectActionDelegate
{

	public final static String ID = "jadex.tools.gpmn.diagram.popup.OpenBPMNPlanFileActionID"; ////$NON-NLS-1$

	private BpmnPlanEditPart selectedElement;

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{

		BpmnPlan plan = (BpmnPlan) ((View) selectedElement.getModel()).getElement();
		String bpmnPlanFile = plan.getPlanref();
		
		if (bpmnPlanFile != null)
		{
			try
			{
				// HACK! Should be done through APIs
				IClasspathEntry[] classpathArray = JavaCore.create(getActiveProject()).getRawClasspath();
				IFile file = null;
				for (int i = 0; i < classpathArray.length; i++)
				{
					String filePath = classpathArray[i].getPath().append(bpmnPlanFile + "_diagram").removeFirstSegments(1).toPortableString();
					file = getActiveProject().getFile(filePath);
					if (file.exists() && file.isAccessible())
					{
						try
						{
							IDE.openEditor(targetPartWorkbench.getActiveWorkbenchWindow().getActivePage(), file, true);
						}
						catch (PartInitException e)
						{
							GpmnDiagramEditorPlugin.getInstance().getLog().log(
									new Status(IStatus.ERROR, GpmnDiagramEditorPlugin.ID,
											IStatus.ERROR, e.getMessage(), e));
						}
						break;
					}
				}
			}
			catch (JavaModelException e)
			{
				GpmnDiagramEditorPlugin.getInstance().getLog().log(
						new Status(IStatus.ERROR, GpmnDiagramEditorPlugin.ID,
								IStatus.ERROR, e.getMessage(), e));
			}
			
		}
		else
		{
			//execute select file Action ?? 
		}
		
		
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		selectedElement = null;
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.getFirstElement() instanceof BpmnPlanEditPart)
			{
				selectedElement = (BpmnPlanEditPart) structuredSelection
						.getFirstElement();
			}
		}
	}

}
