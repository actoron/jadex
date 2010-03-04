/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.diagram.edit.commands.AssociationCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.AssociationReorientCommand;
import jadex.tools.gpmn.diagram.edit.parts.AssociationEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;

/**
 * @generated
 */
public class GenericGpmnEdgeItemSemanticEditPolicy extends
		GpmnBaseItemSemanticEditPolicy
{

	/**
	 * @generated
	 */
	public GenericGpmnEdgeItemSemanticEditPolicy()
	{
		super(GpmnElementTypes.GenericGpmnEdge_4005);
	}

	/**
	 * @generated
	 */
	protected Command getDestroyElementCommand(DestroyElementRequest req)
	{
		return getGEFWrapper(new DestroyElementCommand(req));
	}

	/**
	 * @generated
	 */
	protected Command getCreateRelationshipCommand(CreateRelationshipRequest req)
	{
		Command command = req.getTarget() == null ? getStartCreateRelationshipCommand(req)
				: getCompleteCreateRelationshipCommand(req);
		return command != null ? command : super
				.getCreateRelationshipCommand(req);
	}

	/**
	 * @generated
	 */
	protected Command getStartCreateRelationshipCommand(
			CreateRelationshipRequest req)
	{
		if (GpmnElementTypes.Association_4001 == req.getElementType())
		{
			return null;
		}
		return null;
	}

	/**
	 * @generated
	 */
	protected Command getCompleteCreateRelationshipCommand(
			CreateRelationshipRequest req)
	{
		if (GpmnElementTypes.Association_4001 == req.getElementType())
		{
			return getGEFWrapper(new AssociationCreateCommand(req, req
					.getSource(), req.getTarget()));
		}
		return null;
	}

	/**
	 * Returns command to reorient EClass based link. New link target or source
	 * should be the domain model element associated with this node.
	 * 
	 * @generated
	 */
	protected Command getReorientRelationshipCommand(
			ReorientRelationshipRequest req)
	{
		switch (getVisualID(req))
		{
			case AssociationEditPart.VISUAL_ID:
				return getGEFWrapper(new AssociationReorientCommand(req));
		}
		return super.getReorientRelationshipCommand(req);
	}

}
