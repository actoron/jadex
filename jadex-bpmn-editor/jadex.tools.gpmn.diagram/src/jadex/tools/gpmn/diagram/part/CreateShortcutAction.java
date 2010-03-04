/*
 * Copyright (c) 2009, Universit�t Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.diagram.edit.commands.GpmnCreateShortcutDecorationsCommand;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @generated
 */
public class CreateShortcutAction extends AbstractHandler
{
	/**
	 * @generated
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IEditorPart diagramEditor = HandlerUtil.getActiveEditorChecked(event);
		Shell shell = diagramEditor.getEditorSite().getShell();
		assert diagramEditor instanceof DiagramEditor;
		TransactionalEditingDomain editingDomain = ((DiagramEditor) diagramEditor)
				.getEditingDomain();
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		assert selection instanceof IStructuredSelection;
		assert ((IStructuredSelection) selection).size() == 1;
		assert ((IStructuredSelection) selection).getFirstElement() instanceof EditPart;
		EditPart selectedDiagramPart = (EditPart) ((IStructuredSelection) selection)
				.getFirstElement();
		final View view = (View) selectedDiagramPart.getModel();
		GpmnElementChooserDialog elementChooser = new GpmnElementChooserDialog(
				shell, view);
		int result = elementChooser.open();
		if (result != Window.OK)
		{
			return null;
		}
		URI selectedModelElementURI = elementChooser
				.getSelectedModelElementURI();
		final EObject selectedElement;
		try
		{
			selectedElement = editingDomain.getResourceSet().getEObject(
					selectedModelElementURI, true);
		}
		catch (WrappedException e)
		{
			GpmnDiagramEditorPlugin
					.getInstance()
					.logError(
							"Exception while loading object: " + selectedModelElementURI.toString(), e); //$NON-NLS-1$
			return null;
		}

		if (selectedElement == null)
		{
			return null;
		}
		CreateViewRequest.ViewDescriptor viewDescriptor = new CreateViewRequest.ViewDescriptor(
				new EObjectAdapter(selectedElement), Node.class, null,
				GpmnDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		ICommand command = new CreateCommand(editingDomain, viewDescriptor,
				view);
		command = command.compose(new GpmnCreateShortcutDecorationsCommand(
				editingDomain, view, viewDescriptor));
		try
		{
			OperationHistoryFactory.getOperationHistory().execute(command,
					new NullProgressMonitor(), null);
		}
		catch (ExecutionException e)
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Unable to create shortcut", e); //$NON-NLS-1$
		}
		return null;
	}

}
