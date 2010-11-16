/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SuppressionEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.part.GpmnDiagramUpdater;
import jadex.tools.gpmn.diagram.part.GpmnLinkDescriptor;
import jadex.tools.gpmn.diagram.part.GpmnNodeDescriptor;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredLayoutCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalConnectionEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GpmnDiagramCanonicalEditPolicy extends
		CanonicalConnectionEditPolicy
{
	
	/**
	 * @generated
	 */
	Set myFeaturesToSynchronize;
	
	/**
	 * @generated
	 */
	protected List getSemanticChildrenList()
	{
		View viewObject = (View) getHost().getModel();
		List result = new LinkedList();
		for (Iterator it = GpmnDiagramUpdater
				.getGpmnDiagram_1000SemanticChildren(viewObject).iterator(); it
				.hasNext();)
		{
			result.add(((GpmnNodeDescriptor) it.next()).getModelElement());
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	protected boolean shouldDeleteView(View view)
	{
		return true;
	}
	
	/**
	 * @generated
	 */
	protected boolean isOrphaned(Collection semanticChildren, final View view)
	{
		int visualID = GpmnVisualIDRegistry.getVisualID(view);
		switch (visualID)
		{
			case ActivationPlanEditPart.VISUAL_ID:
			case SubProcessEditPart.VISUAL_ID:
			case BpmnPlanEditPart.VISUAL_ID:
			case GoalEditPart.VISUAL_ID:
				if (!semanticChildren.contains(view.getElement()))
				{
					return true;
				}
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected String getDefaultFactoryHint()
	{
		return null;
	}
	
	/**
	 * @generated
	 */
	protected Set getFeaturesToSynchronize()
	{
		if (myFeaturesToSynchronize == null)
		{
			myFeaturesToSynchronize = new HashSet();
			myFeaturesToSynchronize.add(GpmnPackage.eINSTANCE
					.getGpmnDiagram_Plans());
			myFeaturesToSynchronize.add(GpmnPackage.eINSTANCE
					.getGpmnDiagram_SubProcesses());
			myFeaturesToSynchronize.add(GpmnPackage.eINSTANCE
					.getGpmnDiagram_Goals());
		}
		return myFeaturesToSynchronize;
	}
	
	/**
	 * @generated
	 */
	protected List getSemanticConnectionsList()
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	protected EObject getSourceElement(EObject relationship)
	{
		return null;
	}
	
	/**
	 * @generated
	 */
	protected EObject getTargetElement(EObject relationship)
	{
		return null;
	}
	
	/**
	 * @generated
	 */
	protected boolean shouldIncludeConnection(Edge connector,
			Collection children)
	{
		return false;
	}
	
	/**
	 * @generated
	 */
	protected void refreshSemantic()
	{
		List createdViews = new LinkedList();
		createdViews.addAll(refreshSemanticChildren());
		List createdConnectionViews = new LinkedList();
		createdConnectionViews.addAll(refreshSemanticConnections());
		createdConnectionViews.addAll(refreshConnections());
		
		if (createdViews.size() > 1)
		{
			// perform a layout of the container
			DeferredLayoutCommand layoutCmd = new DeferredLayoutCommand(host()
					.getEditingDomain(), createdViews, host());
			executeCommand(new ICommandProxy(layoutCmd));
		}
		
		createdViews.addAll(createdConnectionViews);
		makeViewsImmutable(createdViews);
	}
	
	/**
	 * @generated
	 */
	private Diagram getDiagram()
	{
		return ((View) getHost().getModel()).getDiagram();
	}
	
	/**
	 * @generated
	 */
	private Collection refreshConnections()
	{
		Map domain2NotationMap = new HashMap();
		Collection linkDescriptors = collectAllLinks(getDiagram(),
				domain2NotationMap);
		Collection existingLinks = new LinkedList(getDiagram().getEdges());
		for (Iterator linksIterator = existingLinks.iterator(); linksIterator
				.hasNext();)
		{
			Edge nextDiagramLink = (Edge) linksIterator.next();
			int diagramLinkVisualID = GpmnVisualIDRegistry
					.getVisualID(nextDiagramLink);
			if (diagramLinkVisualID == -1
					|| diagramLinkVisualID == VirtualActivationEdgeEditPart.VISUAL_ID)
			{
				if (nextDiagramLink.getSource() != null
						&& nextDiagramLink.getTarget() != null)
				{
					linksIterator.remove();
				}
				continue;
			}
			EObject diagramLinkObject = nextDiagramLink.getElement();
			EObject diagramLinkSrc = nextDiagramLink.getSource().getElement();
			EObject diagramLinkDst = nextDiagramLink.getTarget().getElement();
			for (Iterator linkDescriptorsIterator = linkDescriptors.iterator(); linkDescriptorsIterator
					.hasNext();)
			{
				GpmnLinkDescriptor nextLinkDescriptor = (GpmnLinkDescriptor) linkDescriptorsIterator
						.next();
				if (diagramLinkObject == nextLinkDescriptor.getModelElement()
						&& diagramLinkSrc == nextLinkDescriptor.getSource()
						&& diagramLinkDst == nextLinkDescriptor
								.getDestination()
						&& diagramLinkVisualID == nextLinkDescriptor
								.getVisualID())
				{
					linksIterator.remove();
					linkDescriptorsIterator.remove();
					break;
				}
			}
		}
		deleteViews(existingLinks.iterator());
		return createConnections(linkDescriptors, domain2NotationMap);
	}
	
	/**
	 * @generated
	 */
	private Collection collectAllLinks(View view, Map domain2NotationMap)
	{
		if (!GpmnDiagramEditPart.MODEL_ID.equals(GpmnVisualIDRegistry
				.getModelID(view)))
		{
			return Collections.EMPTY_LIST;
		}
		Collection result = new LinkedList();
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case GpmnDiagramEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getGpmnDiagram_1000ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case ActivationPlanEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getActivationPlan_2001ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case SubProcessEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getSubProcess_2002ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case BpmnPlanEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getBpmnPlan_2003ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case GoalEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getGoal_2004ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case ActivationEdgeEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getActivationEdge_4001ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case PlanEdgeEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getPlanEdge_4002ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
			case SuppressionEdgeEditPart.VISUAL_ID:
			{
				if (!domain2NotationMap.containsKey(view.getElement()))
				{
					result.addAll(GpmnDiagramUpdater
							.getSuppressionEdge_4004ContainedLinks(view));
				}
				if (!domain2NotationMap.containsKey(view.getElement())
						|| view.getEAnnotation("Shortcut") == null) { //$NON-NLS-1$
					domain2NotationMap.put(view.getElement(), view);
				}
				break;
			}
		}
		for (Iterator children = view.getChildren().iterator(); children
				.hasNext();)
		{
			result.addAll(collectAllLinks((View) children.next(),
					domain2NotationMap));
		}
		for (Iterator edges = view.getSourceEdges().iterator(); edges.hasNext();)
		{
			result.addAll(collectAllLinks((View) edges.next(),
					domain2NotationMap));
		}
		return result;
	}
	
	/**
	 * @generated
	 */
	private Collection createConnections(Collection linkDescriptors,
			Map domain2NotationMap)
	{
		List adapters = new LinkedList();
		for (Iterator linkDescriptorsIterator = linkDescriptors.iterator(); linkDescriptorsIterator
				.hasNext();)
		{
			final GpmnLinkDescriptor nextLinkDescriptor = (GpmnLinkDescriptor) linkDescriptorsIterator
					.next();
			EditPart sourceEditPart = getEditPart(nextLinkDescriptor
					.getSource(), domain2NotationMap);
			EditPart targetEditPart = getEditPart(nextLinkDescriptor
					.getDestination(), domain2NotationMap);
			if (sourceEditPart == null || targetEditPart == null)
			{
				continue;
			}
			CreateConnectionViewRequest.ConnectionViewDescriptor descriptor = new CreateConnectionViewRequest.ConnectionViewDescriptor(
					nextLinkDescriptor.getSemanticAdapter(), String
							.valueOf(nextLinkDescriptor.getVisualID()),
					ViewUtil.APPEND, false, ((IGraphicalEditPart) getHost())
							.getDiagramPreferencesHint());
			CreateConnectionViewRequest ccr = new CreateConnectionViewRequest(
					descriptor);
			ccr.setType(RequestConstants.REQ_CONNECTION_START);
			ccr.setSourceEditPart(sourceEditPart);
			sourceEditPart.getCommand(ccr);
			ccr.setTargetEditPart(targetEditPart);
			ccr.setType(RequestConstants.REQ_CONNECTION_END);
			Command cmd = targetEditPart.getCommand(ccr);
			if (cmd != null && cmd.canExecute())
			{
				executeCommand(cmd);
				IAdaptable viewAdapter = (IAdaptable) ccr.getNewObject();
				if (viewAdapter != null)
				{
					adapters.add(viewAdapter);
				}
			}
		}
		return adapters;
	}
	
	/**
	 * @generated
	 */
	private EditPart getEditPart(EObject domainModelElement,
			Map domain2NotationMap)
	{
		View view = (View) domain2NotationMap.get(domainModelElement);
		if (view != null)
		{
			return (EditPart) getHost().getViewer().getEditPartRegistry().get(
					view);
		}
		return null;
	}
}
