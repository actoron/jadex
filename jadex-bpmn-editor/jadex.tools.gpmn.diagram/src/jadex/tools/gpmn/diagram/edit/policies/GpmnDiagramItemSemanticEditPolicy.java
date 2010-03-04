/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.diagram.edit.commands.AchieveGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.ContextCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.DataObjectCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.GenericGpmnElementCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.MaintainGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.MessageGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.ParallelGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.PerformGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.PlanCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.ProcessCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.QueryGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.SequentialGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.SubProcessGoalCreateCommand;
import jadex.tools.gpmn.diagram.edit.commands.TextAnnotationCreateCommand;
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
		if (GpmnElementTypes.Process_2001 == req.getElementType())
		{
			return getGEFWrapper(new ProcessCreateCommand(req));
		}
		if (GpmnElementTypes.AchieveGoal_2002 == req.getElementType())
		{
			return getGEFWrapper(new AchieveGoalCreateCommand(req));
		}
		if (GpmnElementTypes.MaintainGoal_2003 == req.getElementType())
		{
			return getGEFWrapper(new MaintainGoalCreateCommand(req));
		}
		if (GpmnElementTypes.PerformGoal_2004 == req.getElementType())
		{
			return getGEFWrapper(new PerformGoalCreateCommand(req));
		}
		if (GpmnElementTypes.QueryGoal_2005 == req.getElementType())
		{
			return getGEFWrapper(new QueryGoalCreateCommand(req));
		}
		if (GpmnElementTypes.SequentialGoal_2006 == req.getElementType())
		{
			return getGEFWrapper(new SequentialGoalCreateCommand(req));
		}
		if (GpmnElementTypes.ParallelGoal_2007 == req.getElementType())
		{
			return getGEFWrapper(new ParallelGoalCreateCommand(req));
		}
		if (GpmnElementTypes.MessageGoal_2008 == req.getElementType())
		{
			return getGEFWrapper(new MessageGoalCreateCommand(req));
		}
		if (GpmnElementTypes.SubProcessGoal_2009 == req.getElementType())
		{
			return getGEFWrapper(new SubProcessGoalCreateCommand(req));
		}
		if (GpmnElementTypes.Plan_2010 == req.getElementType())
		{
			return getGEFWrapper(new PlanCreateCommand(req));
		}
		if (GpmnElementTypes.Context_2011 == req.getElementType())
		{
			return getGEFWrapper(new ContextCreateCommand(req));
		}
		if (GpmnElementTypes.TextAnnotation_2012 == req.getElementType())
		{
			return getGEFWrapper(new TextAnnotationCreateCommand(req));
		}
		if (GpmnElementTypes.DataObject_2013 == req.getElementType())
		{
			return getGEFWrapper(new DataObjectCreateCommand(req));
		}
		if (GpmnElementTypes.GenericGpmnElement_2014 == req.getElementType())
		{
			return getGEFWrapper(new GenericGpmnElementCreateCommand(req));
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
