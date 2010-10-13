/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

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
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @generated
 */
public class GpmnElementTypes extends ElementInitializers
{
	
	/**
	 * @generated
	 */
	private GpmnElementTypes()
	{
	}
	
	/**
	 * @generated
	 */
	private static Map elements;
	
	/**
	 * @generated
	 */
	private static ImageRegistry imageRegistry;
	
	/**
	 * @generated
	 */
	private static Set KNOWN_ELEMENT_TYPES;
	
	/**
	 * @generated
	 */
	public static final IElementType GpmnDiagram_1000 = getElementType("jadex.tools.gpmn.diagram.GpmnDiagram_1000"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType ActivationPlan_2001 = getElementType("jadex.tools.gpmn.diagram.ActivationPlan_2001"); //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	public static final IElementType SubProcess_2002 = getElementType("jadex.tools.gpmn.diagram.SubProcess_2002"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType BpmnPlan_2003 = getElementType("jadex.tools.gpmn.diagram.BpmnPlan_2003"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Goal_2004 = getElementType("jadex.tools.gpmn.diagram.Goal_2004"); //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	public static final IElementType ActivationEdge_4001 = getElementType("jadex.tools.gpmn.diagram.ActivationEdge_4001"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType PlanEdge_4002 = getElementType("jadex.tools.gpmn.diagram.PlanEdge_4002"); //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	public static final IElementType SuppressionEdge_4004 = getElementType("jadex.tools.gpmn.diagram.SuppressionEdge_4004"); //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	public static final IElementType Link_4003 = getElementType("jadex.tools.gpmn.diagram.Link_4003"); //$NON-NLS-1$
	
	/**
	 * @generated
	 */
	private static ImageRegistry getImageRegistry()
	{
		if (imageRegistry == null)
		{
			imageRegistry = new ImageRegistry();
		}
		return imageRegistry;
	}
	
	/**
	 * @generated
	 */
	private static String getImageRegistryKey(ENamedElement element)
	{
		return element.getName();
	}
	
	/**
	 * @generated
	 */
	private static ImageDescriptor getProvidedImageDescriptor(
			ENamedElement element)
	{
		if (element instanceof EStructuralFeature)
		{
			EStructuralFeature feature = ((EStructuralFeature) element);
			EClass eContainingClass = feature.getEContainingClass();
			EClassifier eType = feature.getEType();
			if (eContainingClass != null && !eContainingClass.isAbstract())
			{
				element = eContainingClass;
			}
			else if (eType instanceof EClass && !((EClass) eType).isAbstract())
			{
				element = eType;
			}
		}
		if (element instanceof EClass)
		{
			EClass eClass = (EClass) element;
			if (!eClass.isAbstract())
			{
				return GpmnDiagramEditorPlugin.getInstance()
						.getItemImageDescriptor(
								eClass.getEPackage().getEFactoryInstance()
										.create(eClass));
			}
		}
		// TODO : support structural features
		return null;
	}
	
	/**
	 * @generated
	 */
	public static ImageDescriptor getImageDescriptor(ENamedElement element)
	{
		String key = getImageRegistryKey(element);
		ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(key);
		if (imageDescriptor == null)
		{
			imageDescriptor = getProvidedImageDescriptor(element);
			if (imageDescriptor == null)
			{
				imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
			}
			getImageRegistry().put(key, imageDescriptor);
		}
		return imageDescriptor;
	}
	
	/**
	 * @generated
	 */
	public static Image getImage(ENamedElement element)
	{
		String key = getImageRegistryKey(element);
		Image image = getImageRegistry().get(key);
		if (image == null)
		{
			ImageDescriptor imageDescriptor = getProvidedImageDescriptor(element);
			if (imageDescriptor == null)
			{
				imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
			}
			getImageRegistry().put(key, imageDescriptor);
			image = getImageRegistry().get(key);
		}
		return image;
	}
	
	/**
	 * @generated
	 */
	public static ImageDescriptor getImageDescriptor(IAdaptable hint)
	{
		ENamedElement element = getElement(hint);
		if (element == null)
		{
			return null;
		}
		return getImageDescriptor(element);
	}
	
	/**
	 * @generated
	 */
	public static Image getImage(IAdaptable hint)
	{
		ENamedElement element = getElement(hint);
		if (element == null)
		{
			return null;
		}
		return getImage(element);
	}
	
	/**
	 * Returns 'type' of the ecore object associated with the hint.
	 * 
	 * @generated
	 */
	public static ENamedElement getElement(IAdaptable hint)
	{
		Object type = hint.getAdapter(IElementType.class);
		if (elements == null)
		{
			elements = new IdentityHashMap();
			
			elements.put(GpmnDiagram_1000, GpmnPackage.eINSTANCE
					.getGpmnDiagram());
			
			elements.put(ActivationPlan_2001, GpmnPackage.eINSTANCE
					.getActivationPlan());
			
			elements
					.put(SubProcess_2002, GpmnPackage.eINSTANCE.getSubProcess());
			
			elements.put(BpmnPlan_2003, GpmnPackage.eINSTANCE.getBpmnPlan());
			
			elements.put(Goal_2004, GpmnPackage.eINSTANCE.getGoal());
			
			elements.put(ActivationEdge_4001, GpmnPackage.eINSTANCE
					.getActivationEdge());
			
			elements.put(PlanEdge_4002, GpmnPackage.eINSTANCE.getPlanEdge());
			
			elements.put(SuppressionEdge_4004, GpmnPackage.eINSTANCE
					.getSuppressionEdge());
		}
		return (ENamedElement) elements.get(type);
	}
	
	/**
	 * @generated
	 */
	private static IElementType getElementType(String id)
	{
		return ElementTypeRegistry.getInstance().getType(id);
	}
	
	/**
	 * @generated
	 */
	public static boolean isKnownElementType(IElementType elementType)
	{
		if (KNOWN_ELEMENT_TYPES == null)
		{
			KNOWN_ELEMENT_TYPES = new HashSet();
			KNOWN_ELEMENT_TYPES.add(GpmnDiagram_1000);
			KNOWN_ELEMENT_TYPES.add(ActivationPlan_2001);
			KNOWN_ELEMENT_TYPES.add(SubProcess_2002);
			KNOWN_ELEMENT_TYPES.add(BpmnPlan_2003);
			KNOWN_ELEMENT_TYPES.add(Goal_2004);
			KNOWN_ELEMENT_TYPES.add(ActivationEdge_4001);
			KNOWN_ELEMENT_TYPES.add(PlanEdge_4002);
			KNOWN_ELEMENT_TYPES.add(SuppressionEdge_4004);
			KNOWN_ELEMENT_TYPES.add(Link_4003);
		}
		return KNOWN_ELEMENT_TYPES.contains(elementType);
	}
	
	/**
	 * @generated
	 */
	public static IElementType getElementType(int visualID)
	{
		switch (visualID)
		{
			case GpmnDiagramEditPart.VISUAL_ID:
				return GpmnDiagram_1000;
			case ActivationPlanEditPart.VISUAL_ID:
				return ActivationPlan_2001;
			case SubProcessEditPart.VISUAL_ID:
				return SubProcess_2002;
			case BpmnPlanEditPart.VISUAL_ID:
				return BpmnPlan_2003;
			case GoalEditPart.VISUAL_ID:
				return Goal_2004;
			case ActivationEdgeEditPart.VISUAL_ID:
				return ActivationEdge_4001;
			case PlanEdgeEditPart.VISUAL_ID:
				return PlanEdge_4002;
			case SuppressionEdgeEditPart.VISUAL_ID:
				return SuppressionEdge_4004;
			case VirtualActivationEdgeEditPart.VISUAL_ID:
				return Link_4003;
		}
		return null;
	}
	
}
