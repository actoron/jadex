/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GpmnValidationProvider
{
	
	/**
	 * @generated
	 */
	private static boolean constraintsActive = false;
	
	/**
	 * @generated
	 */
	public static boolean shouldConstraintsBePrivate()
	{
		return false;
	}
	
	/**
	 * @generated
	 */
	public static void runWithConstraints(
			TransactionalEditingDomain editingDomain, Runnable operation)
	{
		final Runnable op = operation;
		Runnable task = new Runnable()
		{
			public void run()
			{
				try
				{
					constraintsActive = true;
					op.run();
				}
				finally
				{
					constraintsActive = false;
				}
			}
		};
		if (editingDomain != null)
		{
			try
			{
				editingDomain.runExclusive(task);
			}
			catch (Exception e)
			{
				GpmnDiagramEditorPlugin.getInstance().logError(
						"Validation failed", e); //$NON-NLS-1$
			}
		}
		else
		{
			task.run();
		}
	}
	
	/**
	 * @generated
	 */
	static boolean isInDefaultEditorContext(Object object)
	{
		if (shouldConstraintsBePrivate() && !constraintsActive)
		{
			return false;
		}
		if (object instanceof View)
		{
			return constraintsActive
					&& GpmnDiagramEditPart.MODEL_ID.equals(GpmnVisualIDRegistry
							.getModelID((View) object));
		}
		return true;
	}
	
}
