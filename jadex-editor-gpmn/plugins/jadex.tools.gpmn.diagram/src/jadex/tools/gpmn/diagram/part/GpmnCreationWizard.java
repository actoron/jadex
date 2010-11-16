/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.part;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @generated
 */
public class GpmnCreationWizard extends Wizard implements INewWizard
{
	
	/**
	 * @generated
	 */
	private IWorkbench workbench;
	
	/**
	 * @generated
	 */
	protected IStructuredSelection selection;
	
	/**
	 * @generated
	 */
	protected GpmnCreationWizardPage diagramModelFilePage;
	
	/**
	 * @generated
	 */
	protected GpmnCreationWizardPage domainModelFilePage;
	
	/**
	 * @generated
	 */
	protected Resource diagram;
	
	/**
	 * @generated
	 */
	private boolean openNewlyCreatedDiagramEditor = true;
	
	/**
	 * @generated
	 */
	public IWorkbench getWorkbench()
	{
		return workbench;
	}
	
	/**
	 * @generated
	 */
	public IStructuredSelection getSelection()
	{
		return selection;
	}
	
	/**
	 * @generated
	 */
	public final Resource getDiagram()
	{
		return diagram;
	}
	
	/**
	 * @generated
	 */
	public final boolean isOpenNewlyCreatedDiagramEditor()
	{
		return openNewlyCreatedDiagramEditor;
	}
	
	/**
	 * @generated
	 */
	public void setOpenNewlyCreatedDiagramEditor(
			boolean openNewlyCreatedDiagramEditor)
	{
		this.openNewlyCreatedDiagramEditor = openNewlyCreatedDiagramEditor;
	}
	
	/**
	 * @generated
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle(Messages.GpmnCreationWizardTitle);
		setDefaultPageImageDescriptor(GpmnDiagramEditorPlugin
				.getBundledImageDescriptor("icons/wizban/NewGpmnWizard.gif")); //$NON-NLS-1$
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * @generated NOT, removed second page for domain model file
	 */
	public void addPages()
	{
		diagramModelFilePage = new GpmnCreationWizardPage(
				"DiagramModelFile", getSelection(), "gpmn_diagram"); //$NON-NLS-1$ //$NON-NLS-2$
		diagramModelFilePage
				.setTitle(Messages.GpmnCreationWizard_DiagramModelFilePageTitle);
		diagramModelFilePage
				.setDescription(Messages.GpmnCreationWizard_DiagramModelFilePageDescription);
		addPage(diagramModelFilePage);
		
		/*domainModelFilePage = new GpmnCreationWizardPage(
				"DomainModelFile", getSelection(), "gpmn") { //$NON-NLS-1$ //$NON-NLS-2$

			public void setVisible(boolean visible)
			{
				if (visible)
				{
					String fileName = diagramModelFilePage.getFileName();
					fileName = fileName.substring(0, fileName.length()
							- ".gpmn_diagram".length()); //$NON-NLS-1$
					setFileName(GpmnDiagramEditorUtil.getUniqueFileName(
							getContainerFullPath(), fileName, "gpmn")); //$NON-NLS-1$
				}
				super.setVisible(visible);
			}
		};
		domainModelFilePage
				.setTitle(Messages.GpmnCreationWizard_DomainModelFilePageTitle);
		domainModelFilePage
				.setDescription(Messages.GpmnCreationWizard_DomainModelFilePageDescription);
		addPage(domainModelFilePage);*/
	}
	
	/**
	 * @generated NOT, replaced domainModelFilePage with file name of diagram page.
	 */
	public boolean performFinish()
	{
		IRunnableWithProgress op = new WorkspaceModifyOperation(null)
		{
			
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InterruptedException
			{
				/*diagram = GpmnDiagramEditorUtil.createDiagram(
						diagramModelFilePage.getURI(), domainModelFilePage
								.getURI(), monitor);*/
				diagram = GpmnDiagramEditorUtil.createDiagram(
						diagramModelFilePage.getURI(),
						generateDomainModelFileURI(), monitor);
				if (isOpenNewlyCreatedDiagramEditor() && diagram != null)
				{
					try
					{
						GpmnDiagramEditorUtil.openDiagram(diagram);
					}
					catch (PartInitException e)
					{
						ErrorDialog.openError(getContainer().getShell(),
								Messages.GpmnCreationWizardOpenEditorError,
								null, e.getStatus());
					}
				}
			}
		};
		try
		{
			getContainer().run(false, true, op);
		}
		catch (InterruptedException e)
		{
			return false;
		}
		catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{
				ErrorDialog.openError(getContainer().getShell(),
						Messages.GpmnCreationWizardCreationError, null,
						((CoreException) e.getTargetException()).getStatus());
			}
			else
			{
				GpmnDiagramEditorPlugin.getInstance().logError(
						"Error creating diagram", e.getTargetException()); //$NON-NLS-1$
			}
			return false;
		}
		return diagram != null;
	}
	
	/**
	 * Calculate the domain model file URI from diagram file dialog.
	 * 
	 * @return URI for domain model file creation
	 * @generated NOT
	 */
	protected URI generateDomainModelFileURI()
	{
		String diagramModelFileName = diagramModelFilePage.getFileName();
		diagramModelFileName = diagramModelFileName.substring(0,
				diagramModelFileName.length() - ".gpmn_diagram".length()); //$NON-NLS-1$
		String domainModelFileName = GpmnDiagramEditorUtil.getUniqueFileName(
				diagramModelFilePage.getContainerFullPath(),
				diagramModelFileName, "gpmn"); //$NON-NLS-1$
		IPath filePath = diagramModelFilePage.getContainerFullPath().append(
				domainModelFileName);
		
		return URI.createPlatformResourceURI(filePath.toString(), false);
	}
}
