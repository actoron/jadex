/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.GpmnPackage;
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
	public static final IElementType Process_2001 = getElementType("jadex.tools.gpmn.diagram.Process_2001"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType AchieveGoal_2002 = getElementType("jadex.tools.gpmn.diagram.AchieveGoal_2002"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType MaintainGoal_2003 = getElementType("jadex.tools.gpmn.diagram.MaintainGoal_2003"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType PerformGoal_2004 = getElementType("jadex.tools.gpmn.diagram.PerformGoal_2004"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType QueryGoal_2005 = getElementType("jadex.tools.gpmn.diagram.QueryGoal_2005"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType SequentialGoal_2006 = getElementType("jadex.tools.gpmn.diagram.SequentialGoal_2006"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType ParallelGoal_2007 = getElementType("jadex.tools.gpmn.diagram.ParallelGoal_2007"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType MessageGoal_2008 = getElementType("jadex.tools.gpmn.diagram.MessageGoal_2008"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType SubProcessGoal_2009 = getElementType("jadex.tools.gpmn.diagram.SubProcessGoal_2009"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Plan_2010 = getElementType("jadex.tools.gpmn.diagram.Plan_2010"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Context_2011 = getElementType("jadex.tools.gpmn.diagram.Context_2011"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType TextAnnotation_2012 = getElementType("jadex.tools.gpmn.diagram.TextAnnotation_2012"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType DataObject_2013 = getElementType("jadex.tools.gpmn.diagram.DataObject_2013"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType GenericGpmnElement_2014 = getElementType("jadex.tools.gpmn.diagram.GenericGpmnElement_2014"); //$NON-NLS-1$

	/**
	 * @generated
	 */
	public static final IElementType Association_4001 = getElementType("jadex.tools.gpmn.diagram.Association_4001"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType SubGoalEdge_4002 = getElementType("jadex.tools.gpmn.diagram.SubGoalEdge_4002"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType PlanEdge_4003 = getElementType("jadex.tools.gpmn.diagram.PlanEdge_4003"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType MessagingEdge_4004 = getElementType("jadex.tools.gpmn.diagram.MessagingEdge_4004"); //$NON-NLS-1$

	/**
	 * @generated
	 */
	public static final IElementType GenericGpmnEdge_4005 = getElementType("jadex.tools.gpmn.diagram.GenericGpmnEdge_4005"); //$NON-NLS-1$

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

			elements.put(Process_2001, GpmnPackage.eINSTANCE.getProcess());

			elements.put(AchieveGoal_2002, GpmnPackage.eINSTANCE
					.getAchieveGoal());

			elements.put(MaintainGoal_2003, GpmnPackage.eINSTANCE
					.getMaintainGoal());

			elements.put(PerformGoal_2004, GpmnPackage.eINSTANCE
					.getPerformGoal());

			elements.put(QueryGoal_2005, GpmnPackage.eINSTANCE.getQueryGoal());

			elements.put(SequentialGoal_2006, GpmnPackage.eINSTANCE
					.getSequentialGoal());

			elements.put(ParallelGoal_2007, GpmnPackage.eINSTANCE
					.getParallelGoal());

			elements.put(MessageGoal_2008, GpmnPackage.eINSTANCE
					.getMessageGoal());

			elements.put(SubProcessGoal_2009, GpmnPackage.eINSTANCE
					.getSubProcessGoal());

			elements.put(Plan_2010, GpmnPackage.eINSTANCE.getPlan());

			elements.put(Context_2011, GpmnPackage.eINSTANCE.getContext());

			elements.put(TextAnnotation_2012, GpmnPackage.eINSTANCE
					.getTextAnnotation());

			elements
					.put(DataObject_2013, GpmnPackage.eINSTANCE.getDataObject());

			elements.put(GenericGpmnElement_2014, GpmnPackage.eINSTANCE
					.getGenericGpmnElement());

			elements.put(Association_4001, GpmnPackage.eINSTANCE
					.getAssociation());

			elements.put(SubGoalEdge_4002, GpmnPackage.eINSTANCE
					.getSubGoalEdge());

			elements.put(PlanEdge_4003, GpmnPackage.eINSTANCE.getPlanEdge());

			elements.put(MessagingEdge_4004, GpmnPackage.eINSTANCE
					.getMessagingEdge());

			elements.put(GenericGpmnEdge_4005, GpmnPackage.eINSTANCE
					.getGenericGpmnEdge());
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
			KNOWN_ELEMENT_TYPES.add(Process_2001);
			KNOWN_ELEMENT_TYPES.add(AchieveGoal_2002);
			KNOWN_ELEMENT_TYPES.add(MaintainGoal_2003);
			KNOWN_ELEMENT_TYPES.add(PerformGoal_2004);
			KNOWN_ELEMENT_TYPES.add(QueryGoal_2005);
			KNOWN_ELEMENT_TYPES.add(SequentialGoal_2006);
			KNOWN_ELEMENT_TYPES.add(ParallelGoal_2007);
			KNOWN_ELEMENT_TYPES.add(MessageGoal_2008);
			KNOWN_ELEMENT_TYPES.add(SubProcessGoal_2009);
			KNOWN_ELEMENT_TYPES.add(Plan_2010);
			KNOWN_ELEMENT_TYPES.add(Context_2011);
			KNOWN_ELEMENT_TYPES.add(TextAnnotation_2012);
			KNOWN_ELEMENT_TYPES.add(DataObject_2013);
			KNOWN_ELEMENT_TYPES.add(GenericGpmnElement_2014);
			KNOWN_ELEMENT_TYPES.add(Association_4001);
			KNOWN_ELEMENT_TYPES.add(SubGoalEdge_4002);
			KNOWN_ELEMENT_TYPES.add(PlanEdge_4003);
			KNOWN_ELEMENT_TYPES.add(MessagingEdge_4004);
			KNOWN_ELEMENT_TYPES.add(GenericGpmnEdge_4005);
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
			case ProcessEditPart.VISUAL_ID:
				return Process_2001;
			case AchieveGoalEditPart.VISUAL_ID:
				return AchieveGoal_2002;
			case MaintainGoalEditPart.VISUAL_ID:
				return MaintainGoal_2003;
			case PerformGoalEditPart.VISUAL_ID:
				return PerformGoal_2004;
			case QueryGoalEditPart.VISUAL_ID:
				return QueryGoal_2005;
			case SequentialGoalEditPart.VISUAL_ID:
				return SequentialGoal_2006;
			case ParallelGoalEditPart.VISUAL_ID:
				return ParallelGoal_2007;
			case MessageGoalEditPart.VISUAL_ID:
				return MessageGoal_2008;
			case SubProcessGoalEditPart.VISUAL_ID:
				return SubProcessGoal_2009;
			case PlanEditPart.VISUAL_ID:
				return Plan_2010;
			case ContextEditPart.VISUAL_ID:
				return Context_2011;
			case TextAnnotationEditPart.VISUAL_ID:
				return TextAnnotation_2012;
			case DataObjectEditPart.VISUAL_ID:
				return DataObject_2013;
			case GenericGpmnElementEditPart.VISUAL_ID:
				return GenericGpmnElement_2014;
			case AssociationEditPart.VISUAL_ID:
				return Association_4001;
			case SubGoalEdgeEditPart.VISUAL_ID:
				return SubGoalEdge_4002;
			case PlanEdgeEditPart.VISUAL_ID:
				return PlanEdge_4003;
			case MessagingEdgeEditPart.VISUAL_ID:
				return MessagingEdge_4004;
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return GenericGpmnEdge_4005;
		}
		return null;
	}

}
