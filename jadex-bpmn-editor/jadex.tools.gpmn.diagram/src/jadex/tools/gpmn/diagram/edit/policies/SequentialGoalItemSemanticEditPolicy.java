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
import jadex.tools.gpmn.diagram.edit.commands.GenericGpmnEdgeCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.GenericGpmnEdgeReorientCommand;
import jadex.tools.gpmn.diagram.edit.commands.PlanEdgeCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.PlanEdgeReorientCommand;
import jadex.tools.gpmn.diagram.edit.commands.SubGoalEdgeCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.SubGoalEdgeReorientCommand;
import jadex.tools.gpmn.diagram.edit.parts.AssociationEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeEditPart;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.Iterator;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class SequentialGoalItemSemanticEditPolicy extends
		GpmnBaseItemSemanticEditPolicy
{

	/**
	 * @generated
	 */
	public SequentialGoalItemSemanticEditPolicy()
	{
		super(GpmnElementTypes.SequentialGoal_2006);
	}

	/**
	 * @generated
	 */
	protected Command getDestroyElementCommand(DestroyElementRequest req)
	{
		View view = (View) getHost().getModel();
		CompositeTransactionalCommand cmd = new CompositeTransactionalCommand(
				getEditingDomain(), null);
		cmd.setTransactionNestingEnabled(false);
		for (Iterator it = view.getTargetEdges().iterator(); it.hasNext();)
		{
			Edge incomingLink = (Edge) it.next();
			if (GpmnVisualIDRegistry.getVisualID(incomingLink) == AssociationEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
			if (GpmnVisualIDRegistry.getVisualID(incomingLink) == SubGoalEdgeEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
			if (GpmnVisualIDRegistry.getVisualID(incomingLink) == PlanEdgeEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
			if (GpmnVisualIDRegistry.getVisualID(incomingLink) == GenericGpmnEdgeEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
		}
		for (Iterator it = view.getSourceEdges().iterator(); it.hasNext();)
		{
			Edge outgoingLink = (Edge) it.next();
			if (GpmnVisualIDRegistry.getVisualID(outgoingLink) == SubGoalEdgeEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						outgoingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), outgoingLink));
				continue;
			}
			if (GpmnVisualIDRegistry.getVisualID(outgoingLink) == PlanEdgeEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						outgoingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), outgoingLink));
				continue;
			}
			if (GpmnVisualIDRegistry.getVisualID(outgoingLink) == GenericGpmnEdgeEditPart.VISUAL_ID)
			{
				DestroyElementRequest r = new DestroyElementRequest(
						outgoingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), outgoingLink));
				continue;
			}
		}
		EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
		if (annotation == null)
		{
			// there are indirectly referenced children, need extra commands: false
			addDestroyShortcutsCommand(cmd, view);
			// delete host element
			cmd.add(new DestroyElementCommand(req));
		}
		else
		{
			cmd.add(new DeleteCommand(getEditingDomain(), view));
		}
		return getGEFWrapper(cmd.reduce());
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
		if (GpmnElementTypes.SubGoalEdge_4002 == req.getElementType())
		{
			return getGEFWrapper(new SubGoalEdgeCreateCommand(req, req
					.getSource(), req.getTarget()));
		}
		if (GpmnElementTypes.PlanEdge_4003 == req.getElementType())
		{
			return getGEFWrapper(new PlanEdgeCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		if (GpmnElementTypes.GenericGpmnEdge_4005 == req.getElementType())
		{
			return getGEFWrapper(new GenericGpmnEdgeCreateCommand(req, req
					.getSource(), req.getTarget()));
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
		if (GpmnElementTypes.SubGoalEdge_4002 == req.getElementType())
		{
			return getGEFWrapper(new SubGoalEdgeCreateCommand(req, req
					.getSource(), req.getTarget()));
		}
		if (GpmnElementTypes.PlanEdge_4003 == req.getElementType())
		{
			return getGEFWrapper(new PlanEdgeCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		if (GpmnElementTypes.GenericGpmnEdge_4005 == req.getElementType())
		{
			return getGEFWrapper(new GenericGpmnEdgeCreateCommand(req, req
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
			case SubGoalEdgeEditPart.VISUAL_ID:
				return getGEFWrapper(new SubGoalEdgeReorientCommand(req));
			case PlanEdgeEditPart.VISUAL_ID:
				return getGEFWrapper(new PlanEdgeReorientCommand(req));
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return getGEFWrapper(new GenericGpmnEdgeReorientCommand(req));
		}
		return super.getReorientRelationshipCommand(req);
	}

}
