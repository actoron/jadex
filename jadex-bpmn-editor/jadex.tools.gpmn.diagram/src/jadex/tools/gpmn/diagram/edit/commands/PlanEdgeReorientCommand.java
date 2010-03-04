/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.commands;

import jadex.tools.gpmn.Graph;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.Vertex;
import jadex.tools.gpmn.diagram.edit.policies.GpmnBaseItemSemanticEditPolicy;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;

/**
 * @generated
 */
public class PlanEdgeReorientCommand extends EditElementCommand
{

	/**
	 * @generated
	 */
	private final int reorientDirection;

	/**
	 * @generated
	 */
	private final EObject oldEnd;

	/**
	 * @generated
	 */
	private final EObject newEnd;

	/**
	 * @generated
	 */
	public PlanEdgeReorientCommand(ReorientRelationshipRequest request)
	{
		super(request.getLabel(), request.getRelationship(), request);
		reorientDirection = request.getDirection();
		oldEnd = request.getOldRelationshipEnd();
		newEnd = request.getNewRelationshipEnd();
	}

	/**
	 * @generated
	 */
	public boolean canExecute()
	{
		if (false == getElementToEdit() instanceof PlanEdge)
		{
			return false;
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_SOURCE)
		{
			return canReorientSource();
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_TARGET)
		{
			return canReorientTarget();
		}
		return false;
	}

	/**
	 * @generated
	 */
	protected boolean canReorientSource()
	{
		if (!(oldEnd instanceof Vertex && newEnd instanceof Vertex))
		{
			return false;
		}
		Vertex target = getLink().getTarget();
		if (!(getLink().eContainer() instanceof Graph))
		{
			return false;
		}
		Graph container = (Graph) getLink().eContainer();
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canExistPlanEdge_4003(container, getNewSource(), target);
	}

	/**
	 * @generated
	 */
	protected boolean canReorientTarget()
	{
		if (!(oldEnd instanceof Vertex && newEnd instanceof Vertex))
		{
			return false;
		}
		Vertex source = getLink().getSource();
		if (!(getLink().eContainer() instanceof Graph))
		{
			return false;
		}
		Graph container = (Graph) getLink().eContainer();
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canExistPlanEdge_4003(container, source, getNewTarget());
	}

	/**
	 * @generated
	 */
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException
	{
		if (!canExecute())
		{
			throw new ExecutionException(
					"Invalid arguments in reorient link command"); //$NON-NLS-1$
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_SOURCE)
		{
			return reorientSource();
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_TARGET)
		{
			return reorientTarget();
		}
		throw new IllegalStateException();
	}

	/**
	 * @generated
	 */
	protected CommandResult reorientSource() throws ExecutionException
	{
		getLink().setSource(getNewSource());
		return CommandResult.newOKCommandResult(getLink());
	}

	/**
	 * @generated
	 */
	protected CommandResult reorientTarget() throws ExecutionException
	{
		getLink().setTarget(getNewTarget());
		return CommandResult.newOKCommandResult(getLink());
	}

	/**
	 * @generated
	 */
	protected PlanEdge getLink()
	{
		return (PlanEdge) getElementToEdit();
	}

	/**
	 * @generated
	 */
	protected Vertex getOldSource()
	{
		return (Vertex) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Vertex getNewSource()
	{
		return (Vertex) newEnd;
	}

	/**
	 * @generated
	 */
	protected Vertex getOldTarget()
	{
		return (Vertex) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Vertex getNewTarget()
	{
		return (Vertex) newEnd;
	}
}
