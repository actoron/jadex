/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.diagram.edit.commands.ActivationPlanCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.BpmnPlanCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.GoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.SubProcessCreateCommand;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.commands.DuplicateEObjectsCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DuplicateElementsRequest;

/**
 * @generated
 */
public class GpmnDiagramItemSemanticEditPolicy extends
		GpmnBaseItemSemanticEditPolicy
{
	
	/**
	 * @generated
	 */
	public GpmnDiagramItemSemanticEditPolicy()
	{
		super(GpmnElementTypes.GpmnDiagram_1000);
	}
	
	/**
	 * @generated
	 */
	protected Command getCreateCommand(CreateElementRequest req)
	{
		if (GpmnElementTypes.ActivationPlan_2001 == req.getElementType())
		{
			return getGEFWrapper(new ActivationPlanCreateCommand(req));
		}
		if (GpmnElementTypes.SubProcess_2002 == req.getElementType())
		{
			return getGEFWrapper(new SubProcessCreateCommand(req));
		}
		if (GpmnElementTypes.BpmnPlan_2003 == req.getElementType())
		{
			return getGEFWrapper(new BpmnPlanCreateCommand(req));
		}
		if (GpmnElementTypes.Goal_2004 == req.getElementType())
		{
			return getGEFWrapper(new GoalCreateCommand(req));
		}
		return super.getCreateCommand(req);
	}
	
	/**
	 * @generated
	 */
	protected Command getDuplicateCommand(DuplicateElementsRequest req)
	{
		TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost())
				.getEditingDomain();
		return getGEFWrapper(new DuplicateAnythingCommand(editingDomain, req));
	}
	
	/**
	 * @generated
	 */
	private static class DuplicateAnythingCommand extends
			DuplicateEObjectsCommand
	{
		
		/**
		 * @generated
		 */
		public DuplicateAnythingCommand(
				TransactionalEditingDomain editingDomain,
				DuplicateElementsRequest req)
		{
			super(editingDomain, req.getLabel(), req
					.getElementsToBeDuplicated(), req
					.getAllDuplicatedElementsMap());
		}
		
	}
	
}
