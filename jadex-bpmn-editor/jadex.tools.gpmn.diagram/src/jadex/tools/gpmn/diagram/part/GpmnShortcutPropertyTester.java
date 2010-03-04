/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GpmnShortcutPropertyTester extends PropertyTester
{

	/**
	 * @generated
	 */
	protected static final String SHORTCUT_PROPERTY = "isShortcut"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	public boolean test(Object receiver, String method, Object[] args,
			Object expectedValue)
	{
		if (false == receiver instanceof View)
		{
			return false;
		}
		View view = (View) receiver;
		if (SHORTCUT_PROPERTY.equals(method))
		{
			EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
			if (annotation != null)
			{
				return GpmnDiagramEditPart.MODEL_ID.equals(annotation
						.getDetails().get("modelID")); //$NON-NLS-1$
			}
		}
		return false;
	}

}
