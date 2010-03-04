/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.AchieveGoal;
import jadex.tools.gpmn.Artifact;
import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.AssociationTarget;
import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.DataObject;
import jadex.tools.gpmn.GenericGpmnEdge;
import jadex.tools.gpmn.GenericGpmnElement;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Graph;
import jadex.tools.gpmn.InterGraphVertex;
import jadex.tools.gpmn.MaintainGoal;
import jadex.tools.gpmn.MessageGoal;
import jadex.tools.gpmn.MessagingEdge;
import jadex.tools.gpmn.ParallelGoal;
import jadex.tools.gpmn.PerformGoal;
import jadex.tools.gpmn.Plan;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.Process;
import jadex.tools.gpmn.QueryGoal;
import jadex.tools.gpmn.SequentialGoal;
import jadex.tools.gpmn.SubGoalEdge;
import jadex.tools.gpmn.SubProcessGoal;
import jadex.tools.gpmn.TextAnnotation;
import jadex.tools.gpmn.Vertex;
import jadex.tools.gpmn.diagram.edit.parts.AchieveGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.AssociationEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ContextEditPart;
import jadex.tools.gpmn.diagram.edit.parts.DataObjectEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnElementEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MaintainGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessageGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessagingEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ParallelGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PerformGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.QueryGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SequentialGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.TextAnnotationEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GpmnDiagramUpdater
{

	/**
	 * @generated
	 */
	public static boolean isShortcutOrphaned(View view)
	{
		return !view.isSetElement() || view.getElement() == null
				|| view.getElement().eIsProxy();
	}

	/**
	 * @generated
	 */
	public static List getSemanticChildren(View view)
	{
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case GpmnDiagramEditPart.VISUAL_ID:
				return getGpmnDiagram_1000SemanticChildren(view);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getGpmnDiagram_1000SemanticChildren(View view)
	{
		if (!view.isSetElement())
		{
			return Collections.EMPTY_LIST;
		}
		GpmnDiagram modelElement = (GpmnDiagram) view.getElement();
		List result = new LinkedList();
		for (Iterator it = modelElement.getProcesses().iterator(); it.hasNext();)
		{
			Process childElement = (Process) it.next();
			int visualID = GpmnVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == ProcessEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
		}
		for (Iterator it = modelElement.getVertices().iterator(); it.hasNext();)
		{
			Vertex childElement = (Vertex) it.next();
			int visualID = GpmnVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == AchieveGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == MaintainGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == PerformGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == QueryGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == SequentialGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == ParallelGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == MessageGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == SubProcessGoalEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == PlanEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == GenericGpmnElementEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
		}
		for (Iterator it = modelElement.getArtifacts().iterator(); it.hasNext();)
		{
			Artifact childElement = (Artifact) it.next();
			int visualID = GpmnVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == ContextEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == TextAnnotationEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == DataObjectEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	public static List getContainedLinks(View view)
	{
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case GpmnDiagramEditPart.VISUAL_ID:
				return getGpmnDiagram_1000ContainedLinks(view);
			case ProcessEditPart.VISUAL_ID:
				return getProcess_2001ContainedLinks(view);
			case AchieveGoalEditPart.VISUAL_ID:
				return getAchieveGoal_2002ContainedLinks(view);
			case MaintainGoalEditPart.VISUAL_ID:
				return getMaintainGoal_2003ContainedLinks(view);
			case PerformGoalEditPart.VISUAL_ID:
				return getPerformGoal_2004ContainedLinks(view);
			case QueryGoalEditPart.VISUAL_ID:
				return getQueryGoal_2005ContainedLinks(view);
			case SequentialGoalEditPart.VISUAL_ID:
				return getSequentialGoal_2006ContainedLinks(view);
			case ParallelGoalEditPart.VISUAL_ID:
				return getParallelGoal_2007ContainedLinks(view);
			case MessageGoalEditPart.VISUAL_ID:
				return getMessageGoal_2008ContainedLinks(view);
			case SubProcessGoalEditPart.VISUAL_ID:
				return getSubProcessGoal_2009ContainedLinks(view);
			case PlanEditPart.VISUAL_ID:
				return getPlan_2010ContainedLinks(view);
			case ContextEditPart.VISUAL_ID:
				return getContext_2011ContainedLinks(view);
			case TextAnnotationEditPart.VISUAL_ID:
				return getTextAnnotation_2012ContainedLinks(view);
			case DataObjectEditPart.VISUAL_ID:
				return getDataObject_2013ContainedLinks(view);
			case GenericGpmnElementEditPart.VISUAL_ID:
				return getGenericGpmnElement_2014ContainedLinks(view);
			case AssociationEditPart.VISUAL_ID:
				return getAssociation_4001ContainedLinks(view);
			case SubGoalEdgeEditPart.VISUAL_ID:
				return getSubGoalEdge_4002ContainedLinks(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4003ContainedLinks(view);
			case MessagingEdgeEditPart.VISUAL_ID:
				return getMessagingEdge_4004ContainedLinks(view);
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return getGenericGpmnEdge_4005ContainedLinks(view);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getIncomingLinks(View view)
	{
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case ProcessEditPart.VISUAL_ID:
				return getProcess_2001IncomingLinks(view);
			case AchieveGoalEditPart.VISUAL_ID:
				return getAchieveGoal_2002IncomingLinks(view);
			case MaintainGoalEditPart.VISUAL_ID:
				return getMaintainGoal_2003IncomingLinks(view);
			case PerformGoalEditPart.VISUAL_ID:
				return getPerformGoal_2004IncomingLinks(view);
			case QueryGoalEditPart.VISUAL_ID:
				return getQueryGoal_2005IncomingLinks(view);
			case SequentialGoalEditPart.VISUAL_ID:
				return getSequentialGoal_2006IncomingLinks(view);
			case ParallelGoalEditPart.VISUAL_ID:
				return getParallelGoal_2007IncomingLinks(view);
			case MessageGoalEditPart.VISUAL_ID:
				return getMessageGoal_2008IncomingLinks(view);
			case SubProcessGoalEditPart.VISUAL_ID:
				return getSubProcessGoal_2009IncomingLinks(view);
			case PlanEditPart.VISUAL_ID:
				return getPlan_2010IncomingLinks(view);
			case ContextEditPart.VISUAL_ID:
				return getContext_2011IncomingLinks(view);
			case TextAnnotationEditPart.VISUAL_ID:
				return getTextAnnotation_2012IncomingLinks(view);
			case DataObjectEditPart.VISUAL_ID:
				return getDataObject_2013IncomingLinks(view);
			case GenericGpmnElementEditPart.VISUAL_ID:
				return getGenericGpmnElement_2014IncomingLinks(view);
			case AssociationEditPart.VISUAL_ID:
				return getAssociation_4001IncomingLinks(view);
			case SubGoalEdgeEditPart.VISUAL_ID:
				return getSubGoalEdge_4002IncomingLinks(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4003IncomingLinks(view);
			case MessagingEdgeEditPart.VISUAL_ID:
				return getMessagingEdge_4004IncomingLinks(view);
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return getGenericGpmnEdge_4005IncomingLinks(view);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getOutgoingLinks(View view)
	{
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case ProcessEditPart.VISUAL_ID:
				return getProcess_2001OutgoingLinks(view);
			case AchieveGoalEditPart.VISUAL_ID:
				return getAchieveGoal_2002OutgoingLinks(view);
			case MaintainGoalEditPart.VISUAL_ID:
				return getMaintainGoal_2003OutgoingLinks(view);
			case PerformGoalEditPart.VISUAL_ID:
				return getPerformGoal_2004OutgoingLinks(view);
			case QueryGoalEditPart.VISUAL_ID:
				return getQueryGoal_2005OutgoingLinks(view);
			case SequentialGoalEditPart.VISUAL_ID:
				return getSequentialGoal_2006OutgoingLinks(view);
			case ParallelGoalEditPart.VISUAL_ID:
				return getParallelGoal_2007OutgoingLinks(view);
			case MessageGoalEditPart.VISUAL_ID:
				return getMessageGoal_2008OutgoingLinks(view);
			case SubProcessGoalEditPart.VISUAL_ID:
				return getSubProcessGoal_2009OutgoingLinks(view);
			case PlanEditPart.VISUAL_ID:
				return getPlan_2010OutgoingLinks(view);
			case ContextEditPart.VISUAL_ID:
				return getContext_2011OutgoingLinks(view);
			case TextAnnotationEditPart.VISUAL_ID:
				return getTextAnnotation_2012OutgoingLinks(view);
			case DataObjectEditPart.VISUAL_ID:
				return getDataObject_2013OutgoingLinks(view);
			case GenericGpmnElementEditPart.VISUAL_ID:
				return getGenericGpmnElement_2014OutgoingLinks(view);
			case AssociationEditPart.VISUAL_ID:
				return getAssociation_4001OutgoingLinks(view);
			case SubGoalEdgeEditPart.VISUAL_ID:
				return getSubGoalEdge_4002OutgoingLinks(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4003OutgoingLinks(view);
			case MessagingEdgeEditPart.VISUAL_ID:
				return getMessagingEdge_4004OutgoingLinks(view);
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return getGenericGpmnEdge_4005OutgoingLinks(view);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getGpmnDiagram_1000ContainedLinks(View view)
	{
		GpmnDiagram modelElement = (GpmnDiagram) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getContainedTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_MessagingEdge_4004(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getProcess_2001ContainedLinks(View view)
	{
		Process modelElement = (Process) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getContainedTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getAchieveGoal_2002ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getMaintainGoal_2003ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getPerformGoal_2004ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getQueryGoal_2005ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getSequentialGoal_2006ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getParallelGoal_2007ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getMessageGoal_2008ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getSubProcessGoal_2009ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getPlan_2010ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getContext_2011ContainedLinks(View view)
	{
		Context modelElement = (Context) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getContainedTypeModelFacetLinks_Association_4001(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getTextAnnotation_2012ContainedLinks(View view)
	{
		TextAnnotation modelElement = (TextAnnotation) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getContainedTypeModelFacetLinks_Association_4001(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getDataObject_2013ContainedLinks(View view)
	{
		DataObject modelElement = (DataObject) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getContainedTypeModelFacetLinks_Association_4001(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getGenericGpmnElement_2014ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getAssociation_4001ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getSubGoalEdge_4002ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getPlanEdge_4003ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getMessagingEdge_4004ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getGenericGpmnEdge_4005ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getProcess_2001IncomingLinks(View view)
	{
		Process modelElement = (Process) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_MessagingEdge_4004(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getAchieveGoal_2002IncomingLinks(View view)
	{
		AchieveGoal modelElement = (AchieveGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getMaintainGoal_2003IncomingLinks(View view)
	{
		MaintainGoal modelElement = (MaintainGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getPerformGoal_2004IncomingLinks(View view)
	{
		PerformGoal modelElement = (PerformGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getQueryGoal_2005IncomingLinks(View view)
	{
		QueryGoal modelElement = (QueryGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getSequentialGoal_2006IncomingLinks(View view)
	{
		SequentialGoal modelElement = (SequentialGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getParallelGoal_2007IncomingLinks(View view)
	{
		ParallelGoal modelElement = (ParallelGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getMessageGoal_2008IncomingLinks(View view)
	{
		MessageGoal modelElement = (MessageGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_MessagingEdge_4004(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getSubProcessGoal_2009IncomingLinks(View view)
	{
		SubProcessGoal modelElement = (SubProcessGoal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getPlan_2010IncomingLinks(View view)
	{
		Plan modelElement = (Plan) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getContext_2011IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getTextAnnotation_2012IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getDataObject_2013IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getGenericGpmnElement_2014IncomingLinks(View view)
	{
		GenericGpmnElement modelElement = (GenericGpmnElement) view
				.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getAssociation_4001IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getSubGoalEdge_4002IncomingLinks(View view)
	{
		SubGoalEdge modelElement = (SubGoalEdge) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getPlanEdge_4003IncomingLinks(View view)
	{
		PlanEdge modelElement = (PlanEdge) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getMessagingEdge_4004IncomingLinks(View view)
	{
		MessagingEdge modelElement = (MessagingEdge) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getGenericGpmnEdge_4005IncomingLinks(View view)
	{
		GenericGpmnEdge modelElement = (GenericGpmnEdge) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_Association_4001(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getProcess_2001OutgoingLinks(View view)
	{
		Process modelElement = (Process) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_MessagingEdge_4004(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getAchieveGoal_2002OutgoingLinks(View view)
	{
		AchieveGoal modelElement = (AchieveGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getMaintainGoal_2003OutgoingLinks(View view)
	{
		MaintainGoal modelElement = (MaintainGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getPerformGoal_2004OutgoingLinks(View view)
	{
		PerformGoal modelElement = (PerformGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getQueryGoal_2005OutgoingLinks(View view)
	{
		QueryGoal modelElement = (QueryGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getSequentialGoal_2006OutgoingLinks(View view)
	{
		SequentialGoal modelElement = (SequentialGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getParallelGoal_2007OutgoingLinks(View view)
	{
		ParallelGoal modelElement = (ParallelGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getMessageGoal_2008OutgoingLinks(View view)
	{
		MessageGoal modelElement = (MessageGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_MessagingEdge_4004(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getSubProcessGoal_2009OutgoingLinks(View view)
	{
		SubProcessGoal modelElement = (SubProcessGoal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getPlan_2010OutgoingLinks(View view)
	{
		Plan modelElement = (Plan) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getContext_2011OutgoingLinks(View view)
	{
		Context modelElement = (Context) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_Association_4001(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getTextAnnotation_2012OutgoingLinks(View view)
	{
		TextAnnotation modelElement = (TextAnnotation) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_Association_4001(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getDataObject_2013OutgoingLinks(View view)
	{
		DataObject modelElement = (DataObject) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_Association_4001(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getGenericGpmnElement_2014OutgoingLinks(View view)
	{
		GenericGpmnElement modelElement = (GenericGpmnElement) view
				.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4003(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List getAssociation_4001OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getSubGoalEdge_4002OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getPlanEdge_4003OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getMessagingEdge_4004OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	public static List getGenericGpmnEdge_4005OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_Association_4001(
			Artifact container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getAssociations().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof Association)
			{
				continue;
			}
			Association link = (Association) linkObject;
			if (AssociationEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			AssociationTarget dst = link.getTarget();
			Artifact src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.Association_4001,
					AssociationEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_SubGoalEdge_4002(
			Graph container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getSequenceEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof SubGoalEdge)
			{
				continue;
			}
			SubGoalEdge link = (SubGoalEdge) linkObject;
			if (SubGoalEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex dst = link.getTarget();
			Vertex src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.SubGoalEdge_4002,
					SubGoalEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_PlanEdge_4003(
			Graph container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getSequenceEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof PlanEdge)
			{
				continue;
			}
			PlanEdge link = (PlanEdge) linkObject;
			if (PlanEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex dst = link.getTarget();
			Vertex src = link.getSource();
			result
					.add(new GpmnLinkDescriptor(src, dst, link,
							GpmnElementTypes.PlanEdge_4003,
							PlanEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_MessagingEdge_4004(
			GpmnDiagram container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getMessages().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof MessagingEdge)
			{
				continue;
			}
			MessagingEdge link = (MessagingEdge) linkObject;
			if (MessagingEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			InterGraphVertex dst = link.getTarget();
			InterGraphVertex src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.MessagingEdge_4004,
					MessagingEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_GenericGpmnEdge_4005(
			Graph container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getSequenceEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof GenericGpmnEdge)
			{
				continue;
			}
			GenericGpmnEdge link = (GenericGpmnEdge) linkObject;
			if (GenericGpmnEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex dst = link.getTarget();
			Vertex src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.GenericGpmnEdge_4005,
					GenericGpmnEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_Association_4001(
			AssociationTarget target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getAssociation_Target()
					|| false == setting.getEObject() instanceof Association)
			{
				continue;
			}
			Association link = (Association) setting.getEObject();
			if (AssociationEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Artifact src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, target, link,
					GpmnElementTypes.Association_4001,
					AssociationEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_SubGoalEdge_4002(
			Vertex target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getEdge_Target()
					|| false == setting.getEObject() instanceof SubGoalEdge)
			{
				continue;
			}
			SubGoalEdge link = (SubGoalEdge) setting.getEObject();
			if (SubGoalEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, target, link,
					GpmnElementTypes.SubGoalEdge_4002,
					SubGoalEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_PlanEdge_4003(
			Vertex target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getEdge_Target()
					|| false == setting.getEObject() instanceof PlanEdge)
			{
				continue;
			}
			PlanEdge link = (PlanEdge) setting.getEObject();
			if (PlanEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex src = link.getSource();
			result
					.add(new GpmnLinkDescriptor(src, target, link,
							GpmnElementTypes.PlanEdge_4003,
							PlanEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_MessagingEdge_4004(
			InterGraphVertex target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getInterGraphEdge_Target()
					|| false == setting.getEObject() instanceof MessagingEdge)
			{
				continue;
			}
			MessagingEdge link = (MessagingEdge) setting.getEObject();
			if (MessagingEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			InterGraphVertex src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, target, link,
					GpmnElementTypes.MessagingEdge_4004,
					MessagingEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_GenericGpmnEdge_4005(
			Vertex target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getEdge_Target()
					|| false == setting.getEObject() instanceof GenericGpmnEdge)
			{
				continue;
			}
			GenericGpmnEdge link = (GenericGpmnEdge) setting.getEObject();
			if (GenericGpmnEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, target, link,
					GpmnElementTypes.GenericGpmnEdge_4005,
					GenericGpmnEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_Association_4001(
			Artifact source)
	{
		Artifact container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer())
		{
			if (element instanceof Artifact)
			{
				container = (Artifact) element;
			}
		}
		if (container == null)
		{
			return Collections.EMPTY_LIST;
		}
		Collection result = new LinkedList();
		for (Iterator links = container.getAssociations().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof Association)
			{
				continue;
			}
			Association link = (Association) linkObject;
			if (AssociationEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			AssociationTarget dst = link.getTarget();
			Artifact src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.Association_4001,
					AssociationEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_SubGoalEdge_4002(
			Vertex source)
	{
		Graph container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer())
		{
			if (element instanceof Graph)
			{
				container = (Graph) element;
			}
		}
		if (container == null)
		{
			return Collections.EMPTY_LIST;
		}
		Collection result = new LinkedList();
		for (Iterator links = container.getSequenceEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof SubGoalEdge)
			{
				continue;
			}
			SubGoalEdge link = (SubGoalEdge) linkObject;
			if (SubGoalEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex dst = link.getTarget();
			Vertex src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.SubGoalEdge_4002,
					SubGoalEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_PlanEdge_4003(
			Vertex source)
	{
		Graph container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer())
		{
			if (element instanceof Graph)
			{
				container = (Graph) element;
			}
		}
		if (container == null)
		{
			return Collections.EMPTY_LIST;
		}
		Collection result = new LinkedList();
		for (Iterator links = container.getSequenceEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof PlanEdge)
			{
				continue;
			}
			PlanEdge link = (PlanEdge) linkObject;
			if (PlanEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex dst = link.getTarget();
			Vertex src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result
					.add(new GpmnLinkDescriptor(src, dst, link,
							GpmnElementTypes.PlanEdge_4003,
							PlanEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_MessagingEdge_4004(
			InterGraphVertex source)
	{
		GpmnDiagram container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer())
		{
			if (element instanceof GpmnDiagram)
			{
				container = (GpmnDiagram) element;
			}
		}
		if (container == null)
		{
			return Collections.EMPTY_LIST;
		}
		Collection result = new LinkedList();
		for (Iterator links = container.getMessages().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof MessagingEdge)
			{
				continue;
			}
			MessagingEdge link = (MessagingEdge) linkObject;
			if (MessagingEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			InterGraphVertex dst = link.getTarget();
			InterGraphVertex src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.MessagingEdge_4004,
					MessagingEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_GenericGpmnEdge_4005(
			Vertex source)
	{
		Graph container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer())
		{
			if (element instanceof Graph)
			{
				container = (Graph) element;
			}
		}
		if (container == null)
		{
			return Collections.EMPTY_LIST;
		}
		Collection result = new LinkedList();
		for (Iterator links = container.getSequenceEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof GenericGpmnEdge)
			{
				continue;
			}
			GenericGpmnEdge link = (GenericGpmnEdge) linkObject;
			if (GenericGpmnEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Vertex dst = link.getTarget();
			Vertex src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.GenericGpmnEdge_4005,
					GenericGpmnEdgeEditPart.VISUAL_ID));
		}
		return result;
	}

}
