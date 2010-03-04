/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.commands;

import jadex.tools.gpmn.Artifact;
import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.AssociationTarget;
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
public class AssociationReorientCommand extends EditElementCommand
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
	public AssociationReorientCommand(ReorientRelationshipRequest request)
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
		if (false == getElementToEdit() instanceof Association)
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
		if (!(oldEnd instanceof Artifact && newEnd instanceof Artifact))
		{
			return false;
		}
		AssociationTarget target = getLink().getTarget();
		if (!(getLink().eContainer() instanceof Artifact))
		{
			return false;
		}
		Artifact container = (Artifact) getLink().eContainer();
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canExistAssociation_4001(container, getNewSource(), target);
	}

	/**
	 * @generated
	 */
	protected boolean canReorientTarget()
	{
		if (!(oldEnd instanceof AssociationTarget && newEnd instanceof AssociationTarget))
		{
			return false;
		}
		Artifact source = getLink().getSource();
		if (!(getLink().eContainer() instanceof Artifact))
		{
			return false;
		}
		Artifact container = (Artifact) getLink().eContainer();
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canExistAssociation_4001(container, source, getNewTarget());
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
	protected Association getLink()
	{
		return (Association) getElementToEdit();
	}

	/**
	 * @generated
	 */
	protected Artifact getOldSource()
	{
		return (Artifact) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Artifact getNewSource()
	{
		return (Artifact) newEnd;
	}

	/**
	 * @generated
	 */
	protected AssociationTarget getOldTarget()
	{
		return (AssociationTarget) oldEnd;
	}

	/**
	 * @generated
	 */
	protected AssociationTarget getNewTarget()
	{
		return (AssociationTarget) newEnd;
	}
}
