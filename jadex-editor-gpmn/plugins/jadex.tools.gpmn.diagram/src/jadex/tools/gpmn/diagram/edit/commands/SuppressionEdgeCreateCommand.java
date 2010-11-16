/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.commands;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.SuppressionEdge;
import jadex.tools.gpmn.diagram.edit.policies.GpmnBaseItemSemanticEditPolicy;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;

/**
 * @generated
 */
public class SuppressionEdgeCreateCommand extends EditElementCommand
{
	
	/**
	 * @generated
	 */
	private final EObject source;
	
	/**
	 * @generated
	 */
	private final EObject target;
	
	/**
	 * @generated
	 */
	private final GpmnDiagram container;
	
	/**
	 * @generated
	 */
	public SuppressionEdgeCreateCommand(CreateRelationshipRequest request,
			EObject source, EObject target)
	{
		super(request.getLabel(), null, request);
		this.source = source;
		this.target = target;
		container = deduceContainer(source, target);
	}
	
	/**
	 * @generated
	 */
	public boolean canExecute()
	{
		if (source == null && target == null)
		{
			return false;
		}
		if (source != null && false == source instanceof Goal)
		{
			return false;
		}
		if (target != null && false == target instanceof Goal)
		{
			return false;
		}
		if (getSource() == null)
		{
			return true; // link creation is in progress; source is not defined yet
		}
		// target may be null here but it's possible to check constraint
		if (getContainer() == null)
		{
			return false;
		}
		return GpmnBaseItemSemanticEditPolicy.LinkConstraints
				.canCreateSuppressionEdge_4004(getContainer(), getSource(),
						getTarget());
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
					"Invalid arguments in create link command"); //$NON-NLS-1$
		}
		
		SuppressionEdge newElement = GpmnFactory.eINSTANCE
				.createSuppressionEdge();
		getContainer().getSuppressionEdges().add(newElement);
		newElement.setSource(getSource());
		newElement.setTarget(getTarget());
		doConfigure(newElement, monitor, info);
		((CreateElementRequest) getRequest()).setNewElement(newElement);
		return CommandResult.newOKCommandResult(newElement);
		
	}
	
	/**
	 * @generated
	 */
	protected void doConfigure(SuppressionEdge newElement,
			IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
	{
		IElementType elementType = ((CreateElementRequest) getRequest())
				.getElementType();
		ConfigureRequest configureRequest = new ConfigureRequest(
				getEditingDomain(), newElement, elementType);
		configureRequest.setClientContext(((CreateElementRequest) getRequest())
				.getClientContext());
		configureRequest.addParameters(getRequest().getParameters());
		configureRequest.setParameter(CreateRelationshipRequest.SOURCE,
				getSource());
		configureRequest.setParameter(CreateRelationshipRequest.TARGET,
				getTarget());
		ICommand configureCommand = elementType
				.getEditCommand(configureRequest);
		if (configureCommand != null && configureCommand.canExecute())
		{
			configureCommand.execute(monitor, info);
		}
	}
	
	/**
	 * @generated
	 */
	protected void setElementToEdit(EObject element)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @generated
	 */
	protected Goal getSource()
	{
		return (Goal) source;
	}
	
	/**
	 * @generated
	 */
	protected Goal getTarget()
	{
		return (Goal) target;
	}
	
	/**
	 * @generated
	 */
	public GpmnDiagram getContainer()
	{
		return container;
	}
	
	/**
	 * Default approach is to traverse ancestors of the source to find instance of container.
	 * Modify with appropriate logic.
	 * @generated
	 */
	private static GpmnDiagram deduceContainer(EObject source, EObject target)
	{
		// Find container element for the new link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null; element = element
				.eContainer())
		{
			if (element instanceof GpmnDiagram)
			{
				return (GpmnDiagram) element;
			}
		}
		return null;
	}
	
}
