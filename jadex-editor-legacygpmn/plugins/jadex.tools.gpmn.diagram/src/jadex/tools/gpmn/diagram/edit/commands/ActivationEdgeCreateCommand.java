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
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.diagram.edit.policies.GpmnBaseItemSemanticEditPolicy;

import java.util.List;

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
public class ActivationEdgeCreateCommand extends EditElementCommand
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
	public ActivationEdgeCreateCommand(CreateRelationshipRequest request,
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
		if (source != null && false == source instanceof ActivationPlan)
		{
			return false;
		}
		if (target != null && false == target instanceof Activatable)
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
				.canCreateActivationEdge_4001(getContainer(), getSource(),
						getTarget());
	}
	
	/**
	 * @generated NOT, update the sequentialOrder dependent on source
	 */
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException
	{
		if (!canExecute())
		{
			throw new ExecutionException(
					"Invalid arguments in create link command"); //$NON-NLS-1$
		}
		
		ActivationEdge newElement = GpmnFactory.eINSTANCE
				.createActivationEdge();
		getContainer().getActivationEdges().add(newElement);
		newElement.setSource(getSource());
		newElement.setTarget(getTarget());
		
		// update sequential order
		newElement.setOrder(calculateSequentialOrder(getSource()
				.getActivationEdges()));
		
		doConfigure(newElement, monitor, info);
		((CreateElementRequest) getRequest()).setNewElement(newElement);
		return CommandResult.newOKCommandResult(newElement);
		
	}
	
	/**
	 * Calculate the order for a new edge
	 * 
	 * @param edges
	 *            list of outgoing edges
	 * @return next order number (max order +1)
	 * @generated NOT
	 */
	protected int calculateSequentialOrder(List<ActivationEdge> edges)
	{
		// int order = getSource().getOutgoingEdges().size()
		int order = 0;
		for (ActivationEdge edge : edges)
		{
			
			order = Math.max(order, ((ActivationEdge) edge).getOrder());
		}
		return order + 1;
	}
	
	/**
	 * @generated
	 */
	protected void doConfigure(ActivationEdge newElement,
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
	protected ActivationPlan getSource()
	{
		return (ActivationPlan) source;
	}
	
	/**
	 * @generated
	 */
	protected Activatable getTarget()
	{
		return (Activatable) target;
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
