/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.commands;

import jadex.tools.gpmn.Activatable;
import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.GpmnDiagram;
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
public class ActivationEdgeReorientCommand extends EditElementCommand
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
	public ActivationEdgeReorientCommand(ReorientRelationshipRequest request)
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
		if (false == getElementToEdit() instanceof ActivationEdge)
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
		if (!(oldEnd instanceof ActivationPlan && newEnd instanceof ActivationPlan))
		{
			return false;
		}
		Activatable target = getLink().getTarget();
		if (!(getLink().eContainer() instanceof GpmnDiagram))
		{
			return false;
		}
		GpmnDiagram container = (GpmnDiagram) getLink().eContainer();
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canExistActivationEdge_4001(container, getNewSource(), target);
	}
	
	/**
	 * @generated
	 */
	protected boolean canReorientTarget()
	{
		if (!(oldEnd instanceof Activatable && newEnd instanceof Activatable))
		{
			return false;
		}
		ActivationPlan source = getLink().getSource();
		if (!(getLink().eContainer() instanceof GpmnDiagram))
		{
			return false;
		}
		GpmnDiagram container = (GpmnDiagram) getLink().eContainer();
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canExistActivationEdge_4001(container, source, getNewTarget());
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
	protected ActivationEdge getLink()
	{
		return (ActivationEdge) getElementToEdit();
	}
	
	/**
	 * @generated
	 */
	protected ActivationPlan getOldSource()
	{
		return (ActivationPlan) oldEnd;
	}
	
	/**
	 * @generated
	 */
	protected ActivationPlan getNewSource()
	{
		return (ActivationPlan) newEnd;
	}
	
	/**
	 * @generated
	 */
	protected Activatable getOldTarget()
	{
		return (Activatable) oldEnd;
	}
	
	/**
	 * @generated
	 */
	protected Activatable getNewTarget()
	{
		return (Activatable) newEnd;
	}
}
