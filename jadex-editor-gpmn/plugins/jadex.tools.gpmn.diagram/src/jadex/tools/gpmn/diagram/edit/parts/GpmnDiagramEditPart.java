/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.GpmnDiagramCanonicalEditPolicy;
import jadex.tools.gpmn.diagram.edit.policies.GpmnDiagramItemSemanticEditPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.widgets.Display;

/**
 * @generated
 */
public class GpmnDiagramEditPart extends DiagramEditPart
{
	
	/**
	 * @generated
	 */
	public final static String MODEL_ID = "Gpmn"; //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 1000;
	
	/**
	 * @generated NOT , ??
	 */
	public GpmnDiagramEditPart(View view)
	{
		super(view);
	}
	
	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies()
	{
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new GpmnDiagramItemSemanticEditPolicy());
		installEditPolicy(EditPolicyRoles.CANONICAL_ROLE,
				new GpmnDiagramCanonicalEditPolicy());
		// removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.POPUPBAR_ROLE);
	}
}
