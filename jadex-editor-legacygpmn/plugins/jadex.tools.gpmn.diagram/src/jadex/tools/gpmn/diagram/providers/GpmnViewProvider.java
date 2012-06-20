/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

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
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.providers.IViewProvider;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateDiagramViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateEdgeViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateNodeViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateViewForKindOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateViewOperation;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.preferences.IPreferenceConstants;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.gmf.runtime.notation.DecorationNode;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * @generated
 */
public class GpmnViewProvider extends AbstractProvider implements IViewProvider
{
	
	/**
	 * @generated
	 */
	public final boolean provides(IOperation operation)
	{
		if (operation instanceof CreateViewForKindOperation)
		{
			return provides((CreateViewForKindOperation) operation);
		}
		assert operation instanceof CreateViewOperation;
		if (operation instanceof CreateDiagramViewOperation)
		{
			return provides((CreateDiagramViewOperation) operation);
		}
		else if (operation instanceof CreateEdgeViewOperation)
		{
			return provides((CreateEdgeViewOperation) operation);
		}
		else if (operation instanceof CreateNodeViewOperation)
		{
			return provides((CreateNodeViewOperation) operation);
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected boolean provides(CreateViewForKindOperation op)
	{
		/*
		 if (op.getViewKind() == Node.class)
		 return getNodeViewClass(op.getSemanticAdapter(), op.getContainerView(), op.getSemanticHint()) != null;
		 if (op.getViewKind() == Edge.class)
		 return getEdgeViewClass(op.getSemanticAdapter(), op.getContainerView(), op.getSemanticHint()) != null;
		 */
		return true;
	}
	
	/**
	 * @generated
	 */
	protected boolean provides(CreateDiagramViewOperation op)
	{
		return GpmnDiagramEditPart.MODEL_ID.equals(op.getSemanticHint())
				&& GpmnVisualIDRegistry
						.getDiagramVisualID(getSemanticElement(op
								.getSemanticAdapter())) != -1;
	}
	
	/**
	 * @generated
	 */
	protected boolean provides(CreateNodeViewOperation op)
	{
		if (op.getContainerView() == null)
		{
			return false;
		}
		IElementType elementType = getSemanticElementType(op
				.getSemanticAdapter());
		EObject domainElement = getSemanticElement(op.getSemanticAdapter());
		int visualID;
		if (op.getSemanticHint() == null)
		{
			// Semantic hint is not specified. Can be a result of call from CanonicalEditPolicy.
			// In this situation there should be NO elementType, visualID will be determined
			// by VisualIDRegistry.getNodeVisualID() for domainElement.
			if (elementType != null || domainElement == null)
			{
				return false;
			}
			visualID = GpmnVisualIDRegistry.getNodeVisualID(op
					.getContainerView(), domainElement);
		}
		else
		{
			visualID = GpmnVisualIDRegistry.getVisualID(op.getSemanticHint());
			if (elementType != null)
			{
				if (!GpmnElementTypes.isKnownElementType(elementType)
						|| (!(elementType instanceof IHintedType)))
				{
					return false; // foreign element type
				}
				String elementTypeHint = ((IHintedType) elementType)
						.getSemanticHint();
				if (!op.getSemanticHint().equals(elementTypeHint))
				{
					return false; // if semantic hint is specified it should be the same as in element type
				}
				if (domainElement != null
						&& visualID != GpmnVisualIDRegistry.getNodeVisualID(op
								.getContainerView(), domainElement))
				{
					return false; // visual id for node EClass should match visual id from element type
				}
			}
			else
			{
				if (!GpmnDiagramEditPart.MODEL_ID.equals(GpmnVisualIDRegistry
						.getModelID(op.getContainerView())))
				{
					return false; // foreign diagram
				}
				switch (visualID)
				{
					case ActivationPlanEditPart.VISUAL_ID:
					case SubProcessEditPart.VISUAL_ID:
					case BpmnPlanEditPart.VISUAL_ID:
					case GoalEditPart.VISUAL_ID:
						if (domainElement == null
								|| visualID != GpmnVisualIDRegistry
										.getNodeVisualID(op.getContainerView(),
												domainElement))
						{
							return false; // visual id in semantic hint should match visual id for domain element
						}
						break;
					default:
						return false;
				}
			}
		}
		return ActivationPlanEditPart.VISUAL_ID == visualID
				|| SubProcessEditPart.VISUAL_ID == visualID
				|| BpmnPlanEditPart.VISUAL_ID == visualID
				|| GoalEditPart.VISUAL_ID == visualID;
	}
	
	/**
	 * @generated
	 */
	protected boolean provides(CreateEdgeViewOperation op)
	{
		IElementType elementType = getSemanticElementType(op
				.getSemanticAdapter());
		if (!GpmnElementTypes.isKnownElementType(elementType)
				|| (!(elementType instanceof IHintedType)))
		{
			return false; // foreign element type
		}
		String elementTypeHint = ((IHintedType) elementType).getSemanticHint();
		if (elementTypeHint == null
				|| (op.getSemanticHint() != null && !elementTypeHint.equals(op
						.getSemanticHint())))
		{
			return false; // our hint is visual id and must be specified, and it should be the same as in element type
		}
		int visualID = GpmnVisualIDRegistry.getVisualID(elementTypeHint);
		EObject domainElement = getSemanticElement(op.getSemanticAdapter());
		if (domainElement != null
				&& visualID != GpmnVisualIDRegistry
						.getLinkWithClassVisualID(domainElement))
		{
			return false; // visual id for link EClass should match visual id from element type
		}
		return true;
	}
	
	/**
	 * @generated
	 */
	public Diagram createDiagram(IAdaptable semanticAdapter,
			String diagramKind, PreferencesHint preferencesHint)
	{
		Diagram diagram = NotationFactory.eINSTANCE.createDiagram();
		diagram.getStyles().add(NotationFactory.eINSTANCE.createDiagramStyle());
		diagram.setType(GpmnDiagramEditPart.MODEL_ID);
		diagram.setElement(getSemanticElement(semanticAdapter));
		diagram.setMeasurementUnit(MeasurementUnit.PIXEL_LITERAL);
		return diagram;
	}
	
	/**
	 * @generated
	 */
	public Node createNode(IAdaptable semanticAdapter, View containerView,
			String semanticHint, int index, boolean persisted,
			PreferencesHint preferencesHint)
	{
		final EObject domainElement = getSemanticElement(semanticAdapter);
		final int visualID;
		if (semanticHint == null)
		{
			visualID = GpmnVisualIDRegistry.getNodeVisualID(containerView,
					domainElement);
		}
		else
		{
			visualID = GpmnVisualIDRegistry.getVisualID(semanticHint);
		}
		switch (visualID)
		{
			case ActivationPlanEditPart.VISUAL_ID:
				return createActivationPlan_2001(domainElement, containerView,
						index, persisted, preferencesHint);
			case SubProcessEditPart.VISUAL_ID:
				return createSubProcess_2002(domainElement, containerView,
						index, persisted, preferencesHint);
			case BpmnPlanEditPart.VISUAL_ID:
				return createBpmnPlan_2003(domainElement, containerView, index,
						persisted, preferencesHint);
			case GoalEditPart.VISUAL_ID:
				return createGoal_2004(domainElement, containerView, index,
						persisted, preferencesHint);
		}
		// can't happen, provided #provides(CreateNodeViewOperation) is correct
		return null;
	}
	
	/**
	 * @generated
	 */
	public Edge createEdge(IAdaptable semanticAdapter, View containerView,
			String semanticHint, int index, boolean persisted,
			PreferencesHint preferencesHint)
	{
		IElementType elementType = getSemanticElementType(semanticAdapter);
		String elementTypeHint = ((IHintedType) elementType).getSemanticHint();
		switch (GpmnVisualIDRegistry.getVisualID(elementTypeHint))
		{
			case ActivationEdgeEditPart.VISUAL_ID:
				return createActivationEdge_4001(
						getSemanticElement(semanticAdapter), containerView,
						index, persisted, preferencesHint);
			case PlanEdgeEditPart.VISUAL_ID:
				return createPlanEdge_4002(getSemanticElement(semanticAdapter),
						containerView, index, persisted, preferencesHint);
			case SuppressionEdgeEditPart.VISUAL_ID:
				return createSuppressionEdge_4004(
						getSemanticElement(semanticAdapter), containerView,
						index, persisted, preferencesHint);
			case VirtualActivationEdgeEditPart.VISUAL_ID:
				return createLink_4003(containerView, index, persisted,
						preferencesHint);
		}
		// can never happen, provided #provides(CreateEdgeViewOperation) is correct
		return null;
	}
	
	/**
	 * @generated
	 */
	public Node createActivationPlan_2001(EObject domainElement,
			View containerView, int index, boolean persisted,
			PreferencesHint preferencesHint)
	{
		Node node = NotationFactory.eINSTANCE.createNode();
		node.getStyles()
				.add(NotationFactory.eINSTANCE.createDescriptionStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFillStyle());
		node.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
		node.setType(GpmnVisualIDRegistry
				.getType(ActivationPlanEditPart.VISUAL_ID));
		ViewUtil.insertChildView(containerView, node, index, persisted);
		node.setElement(domainElement);
		stampShortcut(containerView, node);
		// initializeFromPreferences 
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle nodeFontStyle = (FontStyle) node
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (nodeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			nodeFontStyle.setFontName(fontData.getName());
			nodeFontStyle.setFontHeight(fontData.getHeight());
			nodeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			nodeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			nodeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		org.eclipse.swt.graphics.RGB fillRGB = PreferenceConverter.getColor(
				prefStore, IPreferenceConstants.PREF_FILL_COLOR);
		ViewUtil.setStructuralFeatureValue(node, NotationPackage.eINSTANCE
				.getFillStyle_FillColor(), FigureUtilities
				.RGBToInteger(fillRGB));
		Node label5001 = createLabel(node, GpmnVisualIDRegistry
				.getType(ActivationPlanNameEditPart.VISUAL_ID));
		return node;
	}
	
	/**
	 * @generated
	 */
	public Node createSubProcess_2002(EObject domainElement,
			View containerView, int index, boolean persisted,
			PreferencesHint preferencesHint)
	{
		Node node = NotationFactory.eINSTANCE.createNode();
		node.getStyles()
				.add(NotationFactory.eINSTANCE.createDescriptionStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFillStyle());
		node.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
		node
				.setType(GpmnVisualIDRegistry
						.getType(SubProcessEditPart.VISUAL_ID));
		ViewUtil.insertChildView(containerView, node, index, persisted);
		node.setElement(domainElement);
		stampShortcut(containerView, node);
		// initializeFromPreferences 
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle nodeFontStyle = (FontStyle) node
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (nodeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			nodeFontStyle.setFontName(fontData.getName());
			nodeFontStyle.setFontHeight(fontData.getHeight());
			nodeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			nodeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			nodeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		org.eclipse.swt.graphics.RGB fillRGB = PreferenceConverter.getColor(
				prefStore, IPreferenceConstants.PREF_FILL_COLOR);
		ViewUtil.setStructuralFeatureValue(node, NotationPackage.eINSTANCE
				.getFillStyle_FillColor(), FigureUtilities
				.RGBToInteger(fillRGB));
		Node label5002 = createLabel(node, GpmnVisualIDRegistry
				.getType(SubProcessNameEditPart.VISUAL_ID));
		return node;
	}
	
	/**
	 * @generated
	 */
	public Node createBpmnPlan_2003(EObject domainElement, View containerView,
			int index, boolean persisted, PreferencesHint preferencesHint)
	{
		Node node = NotationFactory.eINSTANCE.createNode();
		node.getStyles()
				.add(NotationFactory.eINSTANCE.createDescriptionStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFillStyle());
		node.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
		node.setType(GpmnVisualIDRegistry.getType(BpmnPlanEditPart.VISUAL_ID));
		ViewUtil.insertChildView(containerView, node, index, persisted);
		node.setElement(domainElement);
		stampShortcut(containerView, node);
		// initializeFromPreferences 
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle nodeFontStyle = (FontStyle) node
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (nodeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			nodeFontStyle.setFontName(fontData.getName());
			nodeFontStyle.setFontHeight(fontData.getHeight());
			nodeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			nodeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			nodeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		org.eclipse.swt.graphics.RGB fillRGB = PreferenceConverter.getColor(
				prefStore, IPreferenceConstants.PREF_FILL_COLOR);
		ViewUtil.setStructuralFeatureValue(node, NotationPackage.eINSTANCE
				.getFillStyle_FillColor(), FigureUtilities
				.RGBToInteger(fillRGB));
		Node label5003 = createLabel(node, GpmnVisualIDRegistry
				.getType(BpmnPlanNameEditPart.VISUAL_ID));
		return node;
	}
	
	/**
	 * @generated
	 */
	public Node createGoal_2004(EObject domainElement, View containerView,
			int index, boolean persisted, PreferencesHint preferencesHint)
	{
		Node node = NotationFactory.eINSTANCE.createNode();
		node.getStyles()
				.add(NotationFactory.eINSTANCE.createDescriptionStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		node.getStyles().add(NotationFactory.eINSTANCE.createFillStyle());
		node.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
		node.setType(GpmnVisualIDRegistry.getType(GoalEditPart.VISUAL_ID));
		ViewUtil.insertChildView(containerView, node, index, persisted);
		node.setElement(domainElement);
		stampShortcut(containerView, node);
		// initializeFromPreferences 
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle nodeFontStyle = (FontStyle) node
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (nodeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			nodeFontStyle.setFontName(fontData.getName());
			nodeFontStyle.setFontHeight(fontData.getHeight());
			nodeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			nodeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			nodeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		org.eclipse.swt.graphics.RGB fillRGB = PreferenceConverter.getColor(
				prefStore, IPreferenceConstants.PREF_FILL_COLOR);
		ViewUtil.setStructuralFeatureValue(node, NotationPackage.eINSTANCE
				.getFillStyle_FillColor(), FigureUtilities
				.RGBToInteger(fillRGB));
		Node label5004 = createLabel(node, GpmnVisualIDRegistry
				.getType(GoalNameEditPart.VISUAL_ID));
		return node;
	}
	
	/**
	 * @generated
	 */
	public Edge createActivationEdge_4001(EObject domainElement,
			View containerView, int index, boolean persisted,
			PreferencesHint preferencesHint)
	{
		Edge edge = NotationFactory.eINSTANCE.createEdge();
		edge.getStyles().add(NotationFactory.eINSTANCE.createRoutingStyle());
		edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		RelativeBendpoints bendpoints = NotationFactory.eINSTANCE
				.createRelativeBendpoints();
		ArrayList points = new ArrayList(2);
		points.add(new RelativeBendpoint());
		points.add(new RelativeBendpoint());
		bendpoints.setPoints(points);
		edge.setBendpoints(bendpoints);
		ViewUtil.insertChildView(containerView, edge, index, persisted);
		edge.setType(GpmnVisualIDRegistry
				.getType(ActivationEdgeEditPart.VISUAL_ID));
		edge.setElement(domainElement);
		// initializePreferences
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle edgeFontStyle = (FontStyle) edge
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (edgeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			edgeFontStyle.setFontName(fontData.getName());
			edgeFontStyle.setFontHeight(fontData.getHeight());
			edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		Routing routing = Routing.get(prefStore
				.getInt(IPreferenceConstants.PREF_LINE_STYLE));
		if (routing != null)
		{
			ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE
					.getRoutingStyle_Routing(), routing);
		}
		Node label6001 = createLabel(edge, GpmnVisualIDRegistry
				.getType(ActivationEdgeOrderEditPart.VISUAL_ID));
		label6001.setLayoutConstraint(NotationFactory.eINSTANCE
				.createLocation());
		Location location6001 = (Location) label6001.getLayoutConstraint();
		location6001.setX(0);
		location6001.setY(-10);
		return edge;
	}
	
	/**
	 * @generated
	 */
	public Edge createPlanEdge_4002(EObject domainElement, View containerView,
			int index, boolean persisted, PreferencesHint preferencesHint)
	{
		Edge edge = NotationFactory.eINSTANCE.createEdge();
		edge.getStyles().add(NotationFactory.eINSTANCE.createRoutingStyle());
		edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		RelativeBendpoints bendpoints = NotationFactory.eINSTANCE
				.createRelativeBendpoints();
		ArrayList points = new ArrayList(2);
		points.add(new RelativeBendpoint());
		points.add(new RelativeBendpoint());
		bendpoints.setPoints(points);
		edge.setBendpoints(bendpoints);
		ViewUtil.insertChildView(containerView, edge, index, persisted);
		edge.setType(GpmnVisualIDRegistry.getType(PlanEdgeEditPart.VISUAL_ID));
		edge.setElement(domainElement);
		// initializePreferences
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle edgeFontStyle = (FontStyle) edge
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (edgeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			edgeFontStyle.setFontName(fontData.getName());
			edgeFontStyle.setFontHeight(fontData.getHeight());
			edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		Routing routing = Routing.get(prefStore
				.getInt(IPreferenceConstants.PREF_LINE_STYLE));
		if (routing != null)
		{
			ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE
					.getRoutingStyle_Routing(), routing);
		}
		return edge;
	}
	
	/**
	 * @generated
	 */
	public Edge createSuppressionEdge_4004(EObject domainElement,
			View containerView, int index, boolean persisted,
			PreferencesHint preferencesHint)
	{
		Edge edge = NotationFactory.eINSTANCE.createEdge();
		edge.getStyles().add(NotationFactory.eINSTANCE.createRoutingStyle());
		edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		RelativeBendpoints bendpoints = NotationFactory.eINSTANCE
				.createRelativeBendpoints();
		ArrayList points = new ArrayList(2);
		points.add(new RelativeBendpoint());
		points.add(new RelativeBendpoint());
		bendpoints.setPoints(points);
		edge.setBendpoints(bendpoints);
		ViewUtil.insertChildView(containerView, edge, index, persisted);
		edge.setType(GpmnVisualIDRegistry
				.getType(SuppressionEdgeEditPart.VISUAL_ID));
		edge.setElement(domainElement);
		// initializePreferences
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle edgeFontStyle = (FontStyle) edge
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (edgeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			edgeFontStyle.setFontName(fontData.getName());
			edgeFontStyle.setFontHeight(fontData.getHeight());
			edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		Routing routing = Routing.get(prefStore
				.getInt(IPreferenceConstants.PREF_LINE_STYLE));
		if (routing != null)
		{
			ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE
					.getRoutingStyle_Routing(), routing);
		}
		return edge;
	}
	
	/**
	 * @generated
	 */
	public Edge createLink_4003(View containerView, int index,
			boolean persisted, PreferencesHint preferencesHint)
	{
		Edge edge = NotationFactory.eINSTANCE.createEdge();
		edge.getStyles().add(NotationFactory.eINSTANCE.createRoutingStyle());
		edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
		RelativeBendpoints bendpoints = NotationFactory.eINSTANCE
				.createRelativeBendpoints();
		ArrayList points = new ArrayList(2);
		points.add(new RelativeBendpoint());
		points.add(new RelativeBendpoint());
		bendpoints.setPoints(points);
		edge.setBendpoints(bendpoints);
		ViewUtil.insertChildView(containerView, edge, index, persisted);
		edge.setType(GpmnVisualIDRegistry
				.getType(VirtualActivationEdgeEditPart.VISUAL_ID));
		edge.setElement(null);
		// initializePreferences
		final IPreferenceStore prefStore = (IPreferenceStore) preferencesHint
				.getPreferenceStore();
		FontStyle edgeFontStyle = (FontStyle) edge
				.getStyle(NotationPackage.Literals.FONT_STYLE);
		if (edgeFontStyle != null)
		{
			FontData fontData = PreferenceConverter.getFontData(prefStore,
					IPreferenceConstants.PREF_DEFAULT_FONT);
			edgeFontStyle.setFontName(fontData.getName());
			edgeFontStyle.setFontHeight(fontData.getHeight());
			edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
			edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
			org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter
					.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
			edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB)
					.intValue());
		}
		Routing routing = Routing.get(prefStore
				.getInt(IPreferenceConstants.PREF_LINE_STYLE));
		if (routing != null)
		{
			ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE
					.getRoutingStyle_Routing(), routing);
		}
		Node label6002 = createLabel(edge, GpmnVisualIDRegistry
				.getType(VirtualActivationOrderEditPart.VISUAL_ID));
		label6002.setLayoutConstraint(NotationFactory.eINSTANCE
				.createLocation());
		Location location6002 = (Location) label6002.getLayoutConstraint();
		location6002.setX(0);
		location6002.setY(-10);
		return edge;
	}
	
	/**
	 * @generated
	 */
	private void stampShortcut(View containerView, Node target)
	{
		if (!GpmnDiagramEditPart.MODEL_ID.equals(GpmnVisualIDRegistry
				.getModelID(containerView)))
		{
			EAnnotation shortcutAnnotation = EcoreFactory.eINSTANCE
					.createEAnnotation();
			shortcutAnnotation.setSource("Shortcut"); //$NON-NLS-1$
			shortcutAnnotation.getDetails().put(
					"modelID", GpmnDiagramEditPart.MODEL_ID); //$NON-NLS-1$
			target.getEAnnotations().add(shortcutAnnotation);
		}
	}
	
	/**
	 * @generated
	 */
	private Node createLabel(View owner, String hint)
	{
		DecorationNode rv = NotationFactory.eINSTANCE.createDecorationNode();
		rv.setType(hint);
		ViewUtil.insertChildView(owner, rv, ViewUtil.APPEND, true);
		return rv;
	}
	
	/**
	 * @generated
	 */
	private EObject getSemanticElement(IAdaptable semanticAdapter)
	{
		if (semanticAdapter == null)
		{
			return null;
		}
		EObject eObject = (EObject) semanticAdapter.getAdapter(EObject.class);
		if (eObject != null)
		{
			return EMFCoreUtil.resolve(TransactionUtil
					.getEditingDomain(eObject), eObject);
		}
		return null;
	}
	
	/**
	 * @generated
	 */
	private IElementType getSemanticElementType(IAdaptable semanticAdapter)
	{
		if (semanticAdapter == null)
		{
			return null;
		}
		return (IElementType) semanticAdapter.getAdapter(IElementType.class);
	}
}
