/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeOrderEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessNameEditPart;

import jadex.tools.gpmn.diagram.edit.parts.SuppressionEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationOrderEditPart;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

/**
 * This registry is used to determine which type of visual object should be
 * created for the corresponding Diagram, Node, ChildNode or Link represented
 * by a domain model object.
 * 
 * @generated
 */
public class GpmnVisualIDRegistry
{
	
	/**
	 * @generated
	 */
	private static final String DEBUG_KEY = "jadex.tools.gpmn.diagram/debug/visualID"; //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	public static int getVisualID(View view)
	{
		if (view instanceof Diagram)
		{
			if (GpmnDiagramEditPart.MODEL_ID.equals(view.getType()))
			{
				return GpmnDiagramEditPart.VISUAL_ID;
			}
			else
			{
				return -1;
			}
		}
		return jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry
				.getVisualID(view.getType());
	}
	
	/**
	 * @generated
	 */
	public static String getModelID(View view)
	{
		View diagram = view.getDiagram();
		while (view != diagram)
		{
			EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
			if (annotation != null)
			{
				return (String) annotation.getDetails().get("modelID"); //$NON-NLS-1$
			}
			view = (View) view.eContainer();
		}
		return diagram != null ? diagram.getType() : null;
	}
	
	/**
	 * @generated
	 */
	public static int getVisualID(String type)
	{
		try
		{
			return Integer.parseInt(type);
		}
		catch (NumberFormatException e)
		{
			if (Boolean.TRUE.toString().equalsIgnoreCase(
					Platform.getDebugOption(DEBUG_KEY)))
			{
				GpmnDiagramEditorPlugin.getInstance().logError(
						"Unable to parse view type as a visualID number: "
								+ type);
			}
		}
		return -1;
	}
	
	/**
	 * @generated
	 */
	public static String getType(int visualID)
	{
		return String.valueOf(visualID);
	}
	
	/**
	 * @generated
	 */
	public static int getDiagramVisualID(EObject domainElement)
	{
		if (domainElement == null)
		{
			return -1;
		}
		if (GpmnPackage.eINSTANCE.getGpmnDiagram().isSuperTypeOf(
				domainElement.eClass())
				&& isDiagram((GpmnDiagram) domainElement))
		{
			return GpmnDiagramEditPart.VISUAL_ID;
		}
		return -1;
	}
	
	/**
	 * @generated
	 */
	public static int getNodeVisualID(View containerView, EObject domainElement)
	{
		if (domainElement == null)
		{
			return -1;
		}
		String containerModelID = jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry
				.getModelID(containerView);
		if (!GpmnDiagramEditPart.MODEL_ID.equals(containerModelID))
		{
			return -1;
		}
		int containerVisualID;
		if (GpmnDiagramEditPart.MODEL_ID.equals(containerModelID))
		{
			containerVisualID = jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry
					.getVisualID(containerView);
		}
		else
		{
			if (containerView instanceof Diagram)
			{
				containerVisualID = GpmnDiagramEditPart.VISUAL_ID;
			}
			else
			{
				return -1;
			}
		}
		switch (containerVisualID)
		{
			case GpmnDiagramEditPart.VISUAL_ID:
				if (GpmnPackage.eINSTANCE.getActivationPlan().isSuperTypeOf(
						domainElement.eClass()))
				{
					return ActivationPlanEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getSubProcess().isSuperTypeOf(
						domainElement.eClass()))
				{
					return SubProcessEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getBpmnPlan().isSuperTypeOf(
						domainElement.eClass()))
				{
					return BpmnPlanEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return GoalEditPart.VISUAL_ID;
				}
				break;
		}
		return -1;
	}
	
	/**
	 * @generated
	 */
	public static boolean canCreateNode(View containerView, int nodeVisualID)
	{
		String containerModelID = jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry
				.getModelID(containerView);
		if (!GpmnDiagramEditPart.MODEL_ID.equals(containerModelID))
		{
			return false;
		}
		int containerVisualID;
		if (GpmnDiagramEditPart.MODEL_ID.equals(containerModelID))
		{
			containerVisualID = jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry
					.getVisualID(containerView);
		}
		else
		{
			if (containerView instanceof Diagram)
			{
				containerVisualID = GpmnDiagramEditPart.VISUAL_ID;
			}
			else
			{
				return false;
			}
		}
		switch (containerVisualID)
		{
			case ActivationPlanEditPart.VISUAL_ID:
				if (ActivationPlanNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case SubProcessEditPart.VISUAL_ID:
				if (SubProcessNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case BpmnPlanEditPart.VISUAL_ID:
				if (BpmnPlanNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case GoalEditPart.VISUAL_ID:
				if (GoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case GpmnDiagramEditPart.VISUAL_ID:
				if (ActivationPlanEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (SubProcessEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (BpmnPlanEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (GoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case ActivationEdgeEditPart.VISUAL_ID:
				if (ActivationEdgeOrderEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case VirtualActivationEdgeEditPart.VISUAL_ID:
				if (VirtualActivationOrderEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	public static int getLinkWithClassVisualID(EObject domainElement)
	{
		if (domainElement == null)
		{
			return -1;
		}
		if (GpmnPackage.eINSTANCE.getActivationEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return ActivationEdgeEditPart.VISUAL_ID;
		}
		if (GpmnPackage.eINSTANCE.getPlanEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return PlanEdgeEditPart.VISUAL_ID;
		}
		if (GpmnPackage.eINSTANCE.getSuppressionEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return SuppressionEdgeEditPart.VISUAL_ID;
		}
		return -1;
	}
	
	/**
	 * User can change implementation of this method to handle some specific
	 * situations not covered by default logic.
	 * 
	 * @generated
	 */
	private static boolean isDiagram(GpmnDiagram element)
	{
		return true;
	}
	
}
