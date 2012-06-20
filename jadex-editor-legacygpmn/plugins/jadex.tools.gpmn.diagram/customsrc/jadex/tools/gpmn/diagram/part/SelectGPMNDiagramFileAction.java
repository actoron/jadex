/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.SubProcess;
import jadex.tools.gpmn.diagram.edit.commands.ModifyModelElementCommand;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessEditPart;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;

/**
 * @author claas
 *
 */
public class SelectGPMNDiagramFileAction extends AbstractFileAction implements IObjectActionDelegate
{

	public final static String ID = "jadex.tools.gpmn.diagram.popup.SelectBPMNPlanFileActionID"; // //$NON-NLS-0$

	private SubProcessEditPart selectedElement;

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{

		Display display = targetPartWorkbench.getDisplay();
		Shell shell = display.getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Please choose GPMN diagram file");
		String[] filterNames = new String[] { "GPMN Files", "All Files (*)" };
		String[] filterExtensions = new String[] { "*.gpmn_diagram;*.gpmn", "*" };

		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf"))// //$NON-NLS-0$ //$NON-NLS-1$
		{
			filterNames = new String[] { "GPMN Files", "All Files (*.*)" };
			filterExtensions = new String[] { "*.gpmn_diagram;*.gpmn", "*.*" };
		}

		String filterPath = getActiveFileEditorInput().getFile().getLocation()
				.removeLastSegments(1).toOSString();
		
		// use location of current file if set
		String gpmnDiagramFile = ((SubProcess) ((View) selectedElement.getModel()).getElement()).getProcessref();
		if (gpmnDiagramFile != null && !gpmnDiagramFile.isEmpty())
		{
			try
			{
				// HACK! Should be done through APIs
				IClasspathEntry[] classpathArray = JavaCore.create(getActiveProject()).getRawClasspath();
				IFile currentFile = null;
				for (int i = 0; i < classpathArray.length; i++)
				{
					String filePath = classpathArray[i].getPath().append(gpmnDiagramFile + "_diagram").removeFirstSegments(1).toPortableString();
					currentFile = getActiveProject().getFile(filePath);
					if (currentFile.exists() && currentFile.isAccessible())
					{
						filterPath = currentFile.getLocation().removeLastSegments(1).toOSString();
						break;
					}
				}
			}
			catch (JavaModelException e)
			{
				// ignore?
				GpmnDiagramEditorPlugin.getInstance().getLog().log(
						new Status(IStatus.WARNING, GpmnDiagramEditorPlugin.ID,
								IStatus.WARNING, e.getMessage(), e));
			}
		}

		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);
		dialog.setFilterPath(filterPath);

		String diagramFile = null;
		String diagramLocation = dialog.open();
		
		// remove "_diagram" from extension
		if (diagramLocation.endsWith("_diagram")) ////$NON-NLS-1$
		{
			diagramLocation = diagramLocation.substring(0, diagramLocation.indexOf("_diagram")); //$NON-NLS-1$
		}
		
		// remove leading project path
		IPath diagramPath = new Path(diagramLocation);
		IPath projectPath = getActiveProject().getLocation();
		if (projectPath.isPrefixOf(diagramPath))
		{
			diagramPath = diagramPath.makeRelativeTo(projectPath);
		}

		// check file access
		IFile file = getActiveProject().getFile(diagramPath);
		if (file.exists() && file.isAccessible())
		{
			try
			{
				// HACK! Should be done through APIs
				IClasspathEntry[] classpathArray = JavaCore.create(getActiveProject()).getRawClasspath();
				for (int i = 0; i < classpathArray.length; i++)
				{
					if (classpathArray[i].getPath().isPrefixOf(file.getFullPath()))
					{
						diagramFile = file.getFullPath().makeRelativeTo(
								classpathArray[i].getPath()).toPortableString();
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

		// replace doesn't work bidirectional
		final String fProcessFile = diagramFile; // .replaceAll("/", "."); // this
		final SubProcess process = (SubProcess) ((View) selectedElement.getModel()).getElement();

		// modify the model element
		if (fProcessFile != null)
		{
			// modify the plan
			ModifyModelElementCommand command = new ModifyModelElementCommand(
					process,
					"update GPMN processref")
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor arg0, IAdaptable arg1)
						throws ExecutionException
				{
					process.setProcessref(fProcessFile);
					return CommandResult.newOKCommandResult();
				}
			};

			try
			{
				command.execute(null, null);
			}
			catch (ExecutionException e)
			{
				GpmnDiagramEditorPlugin.getInstance().getLog().log(
						new Status(IStatus.ERROR, GpmnDiagramEditorPlugin.ID,
								IStatus.ERROR, e.getMessage(), e));
			}

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
			if (structuredSelection.getFirstElement() instanceof SubProcessEditPart)
			{
				selectedElement = (SubProcessEditPart) structuredSelection
						.getFirstElement();
			}
		}
	}

}
