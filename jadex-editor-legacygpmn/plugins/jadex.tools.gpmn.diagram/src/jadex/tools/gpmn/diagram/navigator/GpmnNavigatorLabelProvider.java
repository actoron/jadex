/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.navigator;

import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.SuppressionEdge;
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
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.providers.GpmnParserProvider;

import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserOptions;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

/**
 * @generated
 */
public class GpmnNavigatorLabelProvider extends LabelProvider implements
		ICommonLabelProvider, ITreePathLabelProvider
{
	
	/**
	 * @generated
	 */
	static
	{
		GpmnDiagramEditorPlugin
				.getInstance()
				.getImageRegistry()
				.put(
						"Navigator?UnknownElement", ImageDescriptor.getMissingImageDescriptor()); //$NON-NLS-1$
		GpmnDiagramEditorPlugin
				.getInstance()
				.getImageRegistry()
				.put(
						"Navigator?ImageNotFound", ImageDescriptor.getMissingImageDescriptor()); //$NON-NLS-1$
	}
	
	/**
	 * @generated
	 */
	public void updateLabel(ViewerLabel label, TreePath elementPath)
	{
		Object element = elementPath.getLastSegment();
		if (element instanceof GpmnNavigatorItem
				&& !isOwnView(((GpmnNavigatorItem) element).getView()))
		{
			return;
		}
		label.setText(getText(element));
		label.setImage(getImage(element));
	}
	
	/**
	 * @generated
	 */
	public Image getImage(Object element)
	{
		if (element instanceof GpmnNavigatorGroup)
		{
			GpmnNavigatorGroup group = (GpmnNavigatorGroup) element;
			return GpmnDiagramEditorPlugin.getInstance().getBundledImage(
					group.getIcon());
		}
		
		if (element instanceof GpmnNavigatorItem)
		{
			GpmnNavigatorItem navigatorItem = (GpmnNavigatorItem) element;
			if (!isOwnView(navigatorItem.getView()))
			{
				return super.getImage(element);
			}
			return getImage(navigatorItem.getView());
		}
		
		return super.getImage(element);
	}
	
	/**
	 * @generated
	 */
	public Image getImage(View view)
	{
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case GpmnDiagramEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Diagram?http://jadex.sourceforge.net/gpmn?GpmnDiagram", GpmnElementTypes.GpmnDiagram_1000); //$NON-NLS-1$
			case ActivationPlanEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?ActivationPlan", GpmnElementTypes.ActivationPlan_2001); //$NON-NLS-1$
			case SubProcessEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?SubProcess", GpmnElementTypes.SubProcess_2002); //$NON-NLS-1$
			case BpmnPlanEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?BpmnPlan", GpmnElementTypes.BpmnPlan_2003); //$NON-NLS-1$
			case GoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?Goal", GpmnElementTypes.Goal_2004); //$NON-NLS-1$
			case ActivationEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?ActivationEdge", GpmnElementTypes.ActivationEdge_4001); //$NON-NLS-1$
			case PlanEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?PlanEdge", GpmnElementTypes.PlanEdge_4002); //$NON-NLS-1$
			case SuppressionEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?SuppressionEdge", GpmnElementTypes.SuppressionEdge_4004); //$NON-NLS-1$
			case VirtualActivationEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?VirtualActivationEdgeFigure", GpmnElementTypes.Link_4003); //$NON-NLS-1$
		}
		return getImage("Navigator?UnknownElement", null); //$NON-NLS-1$
	}
	
	/**
	 * @generated
	 */
	private Image getImage(String key, IElementType elementType)
	{
		ImageRegistry imageRegistry = GpmnDiagramEditorPlugin.getInstance()
				.getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null && elementType != null
				&& GpmnElementTypes.isKnownElementType(elementType))
		{
			image = GpmnElementTypes.getImage(elementType);
			imageRegistry.put(key, image);
		}
		
		if (image == null)
		{
			image = imageRegistry.get("Navigator?ImageNotFound"); //$NON-NLS-1$
			imageRegistry.put(key, image);
		}
		return image;
	}
	
	/**
	 * @generated
	 */
	public String getText(Object element)
	{
		if (element instanceof GpmnNavigatorGroup)
		{
			GpmnNavigatorGroup group = (GpmnNavigatorGroup) element;
			return group.getGroupName();
		}
		
		if (element instanceof GpmnNavigatorItem)
		{
			GpmnNavigatorItem navigatorItem = (GpmnNavigatorItem) element;
			if (!isOwnView(navigatorItem.getView()))
			{
				return null;
			}
			return getText(navigatorItem.getView());
		}
		
		return super.getText(element);
	}
	
	/**
	 * @generated
	 */
	public String getText(View view)
	{
		if (view.getElement() != null && view.getElement().eIsProxy())
		{
			return getUnresolvedDomainElementProxyText(view);
		}
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{
			case GpmnDiagramEditPart.VISUAL_ID:
				return getGpmnDiagram_1000Text(view);
			case ActivationPlanEditPart.VISUAL_ID:
				return getActivationPlan_2001Text(view);
			case SubProcessEditPart.VISUAL_ID:
				return getSubProcess_2002Text(view);
			case BpmnPlanEditPart.VISUAL_ID:
				return getBpmnPlan_2003Text(view);
			case GoalEditPart.VISUAL_ID:
				return getGoal_2004Text(view);
			case ActivationEdgeEditPart.VISUAL_ID:
				return getActivationEdge_4001Text(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4002Text(view);
			case SuppressionEdgeEditPart.VISUAL_ID:
				return getSuppressionEdge_4004Text(view);
			case VirtualActivationEdgeEditPart.VISUAL_ID:
				return getLink_4003Text(view);
		}
		return getUnknownElementText(view);
	}
	
	/**
	 * @generated
	 */
	private String getGpmnDiagram_1000Text(View view)
	{
		GpmnDiagram domainModelElement = (GpmnDiagram) view.getElement();
		if (domainModelElement != null)
		{
			return String.valueOf(domainModelElement.getName());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 1000); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getActivationPlan_2001Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.ActivationPlan_2001,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(ActivationPlanNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5001); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getSubProcess_2002Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.SubProcess_2002,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry.getType(SubProcessNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5002); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getBpmnPlan_2003Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.BpmnPlan_2003,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry.getType(BpmnPlanNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5003); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getGoal_2004Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.Goal_2004, view.getElement() != null ? view
						.getElement() : view, GpmnVisualIDRegistry
						.getType(GoalNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5004); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getActivationEdge_4001Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.ActivationEdge_4001,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(ActivationEdgeOrderEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 6001); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getPlanEdge_4002Text(View view)
	{
		PlanEdge domainModelElement = (PlanEdge) view.getElement();
		if (domainModelElement != null)
		{
			return String.valueOf(domainModelElement.getId());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4002); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getSuppressionEdge_4004Text(View view)
	{
		SuppressionEdge domainModelElement = (SuppressionEdge) view
				.getElement();
		if (domainModelElement != null)
		{
			return String.valueOf(domainModelElement.getId());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4004); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getLink_4003Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.Link_4003, view.getElement() != null ? view
						.getElement() : view, GpmnVisualIDRegistry
						.getType(VirtualActivationOrderEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 6002); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * @generated
	 */
	private String getUnknownElementText(View view)
	{
		return "<UnknownElement Visual_ID = " + view.getType() + ">"; //$NON-NLS-1$  //$NON-NLS-2$
	}
	
	/**
	 * @generated
	 */
	private String getUnresolvedDomainElementProxyText(View view)
	{
		return "<Unresolved domain element Visual_ID = " + view.getType() + ">"; //$NON-NLS-1$  //$NON-NLS-2$
	}
	
	/**
	 * @generated
	 */
	public void init(ICommonContentExtensionSite aConfig)
	{
	}
	
	/**
	 * @generated
	 */
	public void restoreState(IMemento aMemento)
	{
	}
	
	/**
	 * @generated
	 */
	public void saveState(IMemento aMemento)
	{
	}
	
	/**
	 * @generated
	 */
	public String getDescription(Object anElement)
	{
		return null;
	}
	
	/**
	 * @generated
	 */
	private boolean isOwnView(View view)
	{
		return GpmnDiagramEditPart.MODEL_ID.equals(GpmnVisualIDRegistry
				.getModelID(view));
	}
	
}
