/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.edit.parts.AchieveGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.AchieveGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.AssociationEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ContextEditPart;
import jadex.tools.gpmn.diagram.edit.parts.DataObjectEditPart;
import jadex.tools.gpmn.diagram.edit.parts.DataObjectNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnEdgeNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnElementEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnElementNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MaintainGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MaintainGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessageGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessageGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessagingEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessagingEdgeNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ParallelGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ParallelGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PerformGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PerformGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ProcessNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.QueryGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.QueryGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SequentialGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SequentialGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeSequentialOrderEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.TextAnnotationEditPart;
import jadex.tools.gpmn.diagram.edit.parts.TextAnnotationNameEditPart;

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
		if (!GpmnDiagramEditPart.MODEL_ID.equals(containerModelID)
				&& !"Gpmn".equals(containerModelID)) { //$NON-NLS-1$
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
				if (GpmnPackage.eINSTANCE.getProcess().isSuperTypeOf(
						domainElement.eClass()))
				{
					return ProcessEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getAchieveGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return AchieveGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getMaintainGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return MaintainGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getPerformGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return PerformGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getQueryGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return QueryGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getSequentialGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return SequentialGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getParallelGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return ParallelGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getMessageGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return MessageGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getSubProcessGoal().isSuperTypeOf(
						domainElement.eClass()))
				{
					return SubProcessGoalEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getPlan().isSuperTypeOf(
						domainElement.eClass()))
				{
					return PlanEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getContext().isSuperTypeOf(
						domainElement.eClass()))
				{
					return ContextEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getTextAnnotation().isSuperTypeOf(
						domainElement.eClass()))
				{
					return TextAnnotationEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getDataObject().isSuperTypeOf(
						domainElement.eClass()))
				{
					return DataObjectEditPart.VISUAL_ID;
				}
				if (GpmnPackage.eINSTANCE.getGenericGpmnElement()
						.isSuperTypeOf(domainElement.eClass()))
				{
					return GenericGpmnElementEditPart.VISUAL_ID;
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
		if (!GpmnDiagramEditPart.MODEL_ID.equals(containerModelID)
				&& !"Gpmn".equals(containerModelID)) { //$NON-NLS-1$
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
			case ProcessEditPart.VISUAL_ID:
				if (ProcessNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case AchieveGoalEditPart.VISUAL_ID:
				if (AchieveGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case MaintainGoalEditPart.VISUAL_ID:
				if (MaintainGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case PerformGoalEditPart.VISUAL_ID:
				if (PerformGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case QueryGoalEditPart.VISUAL_ID:
				if (QueryGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case SequentialGoalEditPart.VISUAL_ID:
				if (SequentialGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case ParallelGoalEditPart.VISUAL_ID:
				if (ParallelGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case MessageGoalEditPart.VISUAL_ID:
				if (MessageGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case SubProcessGoalEditPart.VISUAL_ID:
				if (SubProcessGoalNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case PlanEditPart.VISUAL_ID:
				if (PlanNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case TextAnnotationEditPart.VISUAL_ID:
				if (TextAnnotationNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case DataObjectEditPart.VISUAL_ID:
				if (DataObjectNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case GenericGpmnElementEditPart.VISUAL_ID:
				if (GenericGpmnElementNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case GpmnDiagramEditPart.VISUAL_ID:
				if (ProcessEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (AchieveGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (MaintainGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (PerformGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (QueryGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (SequentialGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (ParallelGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (MessageGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (SubProcessGoalEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (PlanEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (ContextEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (TextAnnotationEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (DataObjectEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				if (GenericGpmnElementEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case SubGoalEdgeEditPart.VISUAL_ID:
				if (SubGoalEdgeSequentialOrderEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case MessagingEdgeEditPart.VISUAL_ID:
				if (MessagingEdgeNameEditPart.VISUAL_ID == nodeVisualID)
				{
					return true;
				}
				break;
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				if (GenericGpmnEdgeNameEditPart.VISUAL_ID == nodeVisualID)
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
		if (GpmnPackage.eINSTANCE.getAssociation().isSuperTypeOf(
				domainElement.eClass()))
		{
			return AssociationEditPart.VISUAL_ID;
		}
		if (GpmnPackage.eINSTANCE.getSubGoalEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return SubGoalEdgeEditPart.VISUAL_ID;
		}
		if (GpmnPackage.eINSTANCE.getPlanEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return PlanEdgeEditPart.VISUAL_ID;
		}
		if (GpmnPackage.eINSTANCE.getMessagingEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return MessagingEdgeEditPart.VISUAL_ID;
		}
		if (GpmnPackage.eINSTANCE.getGenericGpmnEdge().isSuperTypeOf(
				domainElement.eClass()))
		{
			return GenericGpmnEdgeEditPart.VISUAL_ID;
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
