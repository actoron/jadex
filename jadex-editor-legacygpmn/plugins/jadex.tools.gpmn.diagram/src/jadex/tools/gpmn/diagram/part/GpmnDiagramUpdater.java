/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.AbstractPlan;
import jadex.tools.gpmn.Activatable;
import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.BpmnPlan;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.SubProcess;
import jadex.tools.gpmn.SuppressionEdge;
import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SuppressionEdgeEditPart;
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
		for (Iterator it = modelElement.getPlans().iterator(); it.hasNext();)
		{
			AbstractPlan childElement = (AbstractPlan) it.next();
			int visualID = GpmnVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == ActivationPlanEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
			if (visualID == BpmnPlanEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
		}
		for (Iterator it = modelElement.getSubProcesses().iterator(); it
				.hasNext();)
		{
			SubProcess childElement = (SubProcess) it.next();
			int visualID = GpmnVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == SubProcessEditPart.VISUAL_ID)
			{
				result.add(new GpmnNodeDescriptor(childElement, visualID));
				continue;
			}
		}
		for (Iterator it = modelElement.getGoals().iterator(); it.hasNext();)
		{
			Goal childElement = (Goal) it.next();
			int visualID = GpmnVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == GoalEditPart.VISUAL_ID)
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
			case ActivationPlanEditPart.VISUAL_ID:
				return getActivationPlan_2001ContainedLinks(view);
			case SubProcessEditPart.VISUAL_ID:
				return getSubProcess_2002ContainedLinks(view);
			case BpmnPlanEditPart.VISUAL_ID:
				return getBpmnPlan_2003ContainedLinks(view);
			case GoalEditPart.VISUAL_ID:
				return getGoal_2004ContainedLinks(view);
			case ActivationEdgeEditPart.VISUAL_ID:
				return getActivationEdge_4001ContainedLinks(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4002ContainedLinks(view);
			case SuppressionEdgeEditPart.VISUAL_ID:
				return getSuppressionEdge_4004ContainedLinks(view);
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
			case ActivationPlanEditPart.VISUAL_ID:
				return getActivationPlan_2001IncomingLinks(view);
			case SubProcessEditPart.VISUAL_ID:
				return getSubProcess_2002IncomingLinks(view);
			case BpmnPlanEditPart.VISUAL_ID:
				return getBpmnPlan_2003IncomingLinks(view);
			case GoalEditPart.VISUAL_ID:
				return getGoal_2004IncomingLinks(view);
			case ActivationEdgeEditPart.VISUAL_ID:
				return getActivationEdge_4001IncomingLinks(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4002IncomingLinks(view);
			case SuppressionEdgeEditPart.VISUAL_ID:
				return getSuppressionEdge_4004IncomingLinks(view);
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
			case ActivationPlanEditPart.VISUAL_ID:
				return getActivationPlan_2001OutgoingLinks(view);
			case SubProcessEditPart.VISUAL_ID:
				return getSubProcess_2002OutgoingLinks(view);
			case BpmnPlanEditPart.VISUAL_ID:
				return getBpmnPlan_2003OutgoingLinks(view);
			case GoalEditPart.VISUAL_ID:
				return getGoal_2004OutgoingLinks(view);
			case ActivationEdgeEditPart.VISUAL_ID:
				return getActivationEdge_4001OutgoingLinks(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4002OutgoingLinks(view);
			case SuppressionEdgeEditPart.VISUAL_ID:
				return getSuppressionEdge_4004OutgoingLinks(view);
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
				.addAll(getContainedTypeModelFacetLinks_ActivationEdge_4001(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_PlanEdge_4002(modelElement));
		result
				.addAll(getContainedTypeModelFacetLinks_SuppressionEdge_4004(modelElement));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getActivationPlan_2001ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getSubProcess_2002ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getBpmnPlan_2003ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getGoal_2004ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getActivationEdge_4001ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getPlanEdge_4002ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getSuppressionEdge_4004ContainedLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getActivationPlan_2001IncomingLinks(View view)
	{
		ActivationPlan modelElement = (ActivationPlan) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4002(
				modelElement, crossReferences));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getSubProcess_2002IncomingLinks(View view)
	{
		SubProcess modelElement = (SubProcess) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_ActivationEdge_4001(
				modelElement, crossReferences));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getBpmnPlan_2003IncomingLinks(View view)
	{
		BpmnPlan modelElement = (BpmnPlan) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_PlanEdge_4002(
				modelElement, crossReferences));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getGoal_2004IncomingLinks(View view)
	{
		Goal modelElement = (Goal) view.getElement();
		Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource()
				.getResourceSet().getResources());
		List result = new LinkedList();
		result.addAll(getIncomingTypeModelFacetLinks_ActivationEdge_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_SuppressionEdge_4004(
				modelElement, crossReferences));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getActivationEdge_4001IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getPlanEdge_4002IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getSuppressionEdge_4004IncomingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getActivationPlan_2001OutgoingLinks(View view)
	{
		ActivationPlan modelElement = (ActivationPlan) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_ActivationEdge_4001(modelElement));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getSubProcess_2002OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getBpmnPlan_2003OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getGoal_2004OutgoingLinks(View view)
	{
		Goal modelElement = (Goal) view.getElement();
		List result = new LinkedList();
		result
				.addAll(getOutgoingTypeModelFacetLinks_PlanEdge_4002(modelElement));
		result
				.addAll(getOutgoingTypeModelFacetLinks_SuppressionEdge_4004(modelElement));
		return result;
	}
	
	/**
	 * @generated
	 */
	public static List getActivationEdge_4001OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getPlanEdge_4002OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public static List getSuppressionEdge_4004OutgoingLinks(View view)
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_ActivationEdge_4001(
			GpmnDiagram container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getActivationEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ActivationEdge)
			{
				continue;
			}
			ActivationEdge link = (ActivationEdge) linkObject;
			if (ActivationEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Activatable dst = link.getTarget();
			ActivationPlan src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.ActivationEdge_4001,
					ActivationEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_PlanEdge_4002(
			GpmnDiagram container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getPlanEdges().iterator(); links
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
			AbstractPlan dst = link.getTarget();
			Goal src = link.getSource();
			result
					.add(new GpmnLinkDescriptor(src, dst, link,
							GpmnElementTypes.PlanEdge_4002,
							PlanEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getContainedTypeModelFacetLinks_SuppressionEdge_4004(
			GpmnDiagram container)
	{
		Collection result = new LinkedList();
		for (Iterator links = container.getSuppressionEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof SuppressionEdge)
			{
				continue;
			}
			SuppressionEdge link = (SuppressionEdge) linkObject;
			if (SuppressionEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Goal dst = link.getTarget();
			Goal src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.SuppressionEdge_4004,
					SuppressionEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_ActivationEdge_4001(
			Activatable target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getActivationEdge_Target()
					|| false == setting.getEObject() instanceof ActivationEdge)
			{
				continue;
			}
			ActivationEdge link = (ActivationEdge) setting.getEObject();
			if (ActivationEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			ActivationPlan src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, target, link,
					GpmnElementTypes.ActivationEdge_4001,
					ActivationEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_PlanEdge_4002(
			AbstractPlan target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getPlanEdge_Target()
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
			Goal src = link.getSource();
			result
					.add(new GpmnLinkDescriptor(src, target, link,
							GpmnElementTypes.PlanEdge_4002,
							PlanEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getIncomingTypeModelFacetLinks_SuppressionEdge_4004(
			Goal target, Map crossReferences)
	{
		Collection result = new LinkedList();
		Collection settings = (Collection) crossReferences.get(target);
		for (Iterator it = settings.iterator(); it.hasNext();)
		{
			EStructuralFeature.Setting setting = (EStructuralFeature.Setting) it
					.next();
			if (setting.getEStructuralFeature() != GpmnPackage.eINSTANCE
					.getSuppressionEdge_Target()
					|| false == setting.getEObject() instanceof SuppressionEdge)
			{
				continue;
			}
			SuppressionEdge link = (SuppressionEdge) setting.getEObject();
			if (SuppressionEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Goal src = link.getSource();
			result.add(new GpmnLinkDescriptor(src, target, link,
					GpmnElementTypes.SuppressionEdge_4004,
					SuppressionEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_ActivationEdge_4001(
			ActivationPlan source)
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
		for (Iterator links = container.getActivationEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ActivationEdge)
			{
				continue;
			}
			ActivationEdge link = (ActivationEdge) linkObject;
			if (ActivationEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Activatable dst = link.getTarget();
			ActivationPlan src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.ActivationEdge_4001,
					ActivationEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_PlanEdge_4002(
			Goal source)
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
		for (Iterator links = container.getPlanEdges().iterator(); links
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
			AbstractPlan dst = link.getTarget();
			Goal src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result
					.add(new GpmnLinkDescriptor(src, dst, link,
							GpmnElementTypes.PlanEdge_4002,
							PlanEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private static Collection getOutgoingTypeModelFacetLinks_SuppressionEdge_4004(
			Goal source)
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
		for (Iterator links = container.getSuppressionEdges().iterator(); links
				.hasNext();)
		{
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof SuppressionEdge)
			{
				continue;
			}
			SuppressionEdge link = (SuppressionEdge) linkObject;
			if (SuppressionEdgeEditPart.VISUAL_ID != GpmnVisualIDRegistry
					.getLinkWithClassVisualID(link))
			{
				continue;
			}
			Goal dst = link.getTarget();
			Goal src = link.getSource();
			if (src != source)
			{
				continue;
			}
			result.add(new GpmnLinkDescriptor(src, dst, link,
					GpmnElementTypes.SuppressionEdge_4004,
					SuppressionEdgeEditPart.VISUAL_ID));
		}
		return result;
	}
	
}
