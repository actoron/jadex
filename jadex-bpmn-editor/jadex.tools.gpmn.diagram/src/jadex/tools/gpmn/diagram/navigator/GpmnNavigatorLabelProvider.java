/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.navigator;

import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.PlanEdge;
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
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.providers.GpmnParserProvider;

import org.eclipse.core.runtime.IAdaptable;
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

		// Due to plugin.xml content will be called only for "own" views
		if (element instanceof IAdaptable)
		{
			View view = (View) ((IAdaptable) element).getAdapter(View.class);
			if (view != null && isOwnView(view))
			{
				return getImage(view);
			}
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
			case ProcessEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?Process", GpmnElementTypes.Process_2001); //$NON-NLS-1$
			case AchieveGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?AchieveGoal", GpmnElementTypes.AchieveGoal_2002); //$NON-NLS-1$
			case MaintainGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?MaintainGoal", GpmnElementTypes.MaintainGoal_2003); //$NON-NLS-1$
			case PerformGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?PerformGoal", GpmnElementTypes.PerformGoal_2004); //$NON-NLS-1$
			case QueryGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?QueryGoal", GpmnElementTypes.QueryGoal_2005); //$NON-NLS-1$
			case SequentialGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?SequentialGoal", GpmnElementTypes.SequentialGoal_2006); //$NON-NLS-1$
			case ParallelGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?ParallelGoal", GpmnElementTypes.ParallelGoal_2007); //$NON-NLS-1$
			case MessageGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?MessageGoal", GpmnElementTypes.MessageGoal_2008); //$NON-NLS-1$
			case SubProcessGoalEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?SubProcessGoal", GpmnElementTypes.SubProcessGoal_2009); //$NON-NLS-1$
			case PlanEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?Plan", GpmnElementTypes.Plan_2010); //$NON-NLS-1$
			case ContextEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?Context", GpmnElementTypes.Context_2011); //$NON-NLS-1$
			case TextAnnotationEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?TextAnnotation", GpmnElementTypes.TextAnnotation_2012); //$NON-NLS-1$
			case DataObjectEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?DataObject", GpmnElementTypes.DataObject_2013); //$NON-NLS-1$
			case GenericGpmnElementEditPart.VISUAL_ID:
				return getImage(
						"Navigator?TopLevelNode?http://jadex.sourceforge.net/gpmn?GenericGpmnElement", GpmnElementTypes.GenericGpmnElement_2014); //$NON-NLS-1$
			case AssociationEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?Association", GpmnElementTypes.Association_4001); //$NON-NLS-1$
			case SubGoalEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?SubGoalEdge", GpmnElementTypes.SubGoalEdge_4002); //$NON-NLS-1$
			case PlanEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?PlanEdge", GpmnElementTypes.PlanEdge_4003); //$NON-NLS-1$
			case MessagingEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?MessagingEdge", GpmnElementTypes.MessagingEdge_4004); //$NON-NLS-1$
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return getImage(
						"Navigator?Link?http://jadex.sourceforge.net/gpmn?GenericGpmnEdge", GpmnElementTypes.GenericGpmnEdge_4005); //$NON-NLS-1$
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

		// Due to plugin.xml content will be called only for "own" views
		if (element instanceof IAdaptable)
		{
			View view = (View) ((IAdaptable) element).getAdapter(View.class);
			if (view != null && isOwnView(view))
			{
				return getText(view);
			}
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
			case ProcessEditPart.VISUAL_ID:
				return getProcess_2001Text(view);
			case AchieveGoalEditPart.VISUAL_ID:
				return getAchieveGoal_2002Text(view);
			case MaintainGoalEditPart.VISUAL_ID:
				return getMaintainGoal_2003Text(view);
			case PerformGoalEditPart.VISUAL_ID:
				return getPerformGoal_2004Text(view);
			case QueryGoalEditPart.VISUAL_ID:
				return getQueryGoal_2005Text(view);
			case SequentialGoalEditPart.VISUAL_ID:
				return getSequentialGoal_2006Text(view);
			case ParallelGoalEditPart.VISUAL_ID:
				return getParallelGoal_2007Text(view);
			case MessageGoalEditPart.VISUAL_ID:
				return getMessageGoal_2008Text(view);
			case SubProcessGoalEditPart.VISUAL_ID:
				return getSubProcessGoal_2009Text(view);
			case PlanEditPart.VISUAL_ID:
				return getPlan_2010Text(view);
			case ContextEditPart.VISUAL_ID:
				return getContext_2011Text(view);
			case TextAnnotationEditPart.VISUAL_ID:
				return getTextAnnotation_2012Text(view);
			case DataObjectEditPart.VISUAL_ID:
				return getDataObject_2013Text(view);
			case GenericGpmnElementEditPart.VISUAL_ID:
				return getGenericGpmnElement_2014Text(view);
			case AssociationEditPart.VISUAL_ID:
				return getAssociation_4001Text(view);
			case SubGoalEdgeEditPart.VISUAL_ID:
				return getSubGoalEdge_4002Text(view);
			case PlanEdgeEditPart.VISUAL_ID:
				return getPlanEdge_4003Text(view);
			case MessagingEdgeEditPart.VISUAL_ID:
				return getMessagingEdge_4004Text(view);
			case GenericGpmnEdgeEditPart.VISUAL_ID:
				return getGenericGpmnEdge_4005Text(view);
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
	private String getProcess_2001Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.Process_2001, view.getElement() != null ? view
						.getElement() : view, GpmnVisualIDRegistry
						.getType(ProcessNameEditPart.VISUAL_ID));
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
	private String getAchieveGoal_2002Text(View view)
	{
		IParser parser = GpmnParserProvider
				.getParser(GpmnElementTypes.AchieveGoal_2002,
						view.getElement() != null ? view.getElement() : view,
						GpmnVisualIDRegistry
								.getType(AchieveGoalNameEditPart.VISUAL_ID));
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
	private String getMaintainGoal_2003Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.MaintainGoal_2003,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(MaintainGoalNameEditPart.VISUAL_ID));
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
	private String getPerformGoal_2004Text(View view)
	{
		IParser parser = GpmnParserProvider
				.getParser(GpmnElementTypes.PerformGoal_2004,
						view.getElement() != null ? view.getElement() : view,
						GpmnVisualIDRegistry
								.getType(PerformGoalNameEditPart.VISUAL_ID));
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
	private String getQueryGoal_2005Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.QueryGoal_2005,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry.getType(QueryGoalNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5005); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getSequentialGoal_2006Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.SequentialGoal_2006,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(SequentialGoalNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5006); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getParallelGoal_2007Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.ParallelGoal_2007,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(ParallelGoalNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5007); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getMessageGoal_2008Text(View view)
	{
		IParser parser = GpmnParserProvider
				.getParser(GpmnElementTypes.MessageGoal_2008,
						view.getElement() != null ? view.getElement() : view,
						GpmnVisualIDRegistry
								.getType(MessageGoalNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5008); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getSubProcessGoal_2009Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.SubProcessGoal_2009,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(SubProcessGoalNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5009); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getPlan_2010Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.Plan_2010, view.getElement() != null ? view
						.getElement() : view, GpmnVisualIDRegistry
						.getType(PlanNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5010); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getContext_2011Text(View view)
	{
		Context domainModelElement = (Context) view.getElement();
		if (domainModelElement != null)
		{
			return String.valueOf(domainModelElement.getName());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 2011); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getTextAnnotation_2012Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.TextAnnotation_2012,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(TextAnnotationNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5012); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getDataObject_2013Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.DataObject_2013,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry.getType(DataObjectNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5013); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getGenericGpmnElement_2014Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.GenericGpmnElement_2014,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(GenericGpmnElementNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5014); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getAssociation_4001Text(View view)
	{
		Association domainModelElement = (Association) view.getElement();
		if (domainModelElement != null)
		{
			return String.valueOf(domainModelElement.getId());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4001); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getSubGoalEdge_4002Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.SubGoalEdge_4002,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(SubGoalEdgeSequentialOrderEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 6003); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getPlanEdge_4003Text(View view)
	{
		PlanEdge domainModelElement = (PlanEdge) view.getElement();
		if (domainModelElement != null)
		{
			return String.valueOf(domainModelElement.getName());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4003); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getMessagingEdge_4004Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.MessagingEdge_4004,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(MessagingEdgeNameEditPart.VISUAL_ID));
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
	private String getGenericGpmnEdge_4005Text(View view)
	{
		IParser parser = GpmnParserProvider.getParser(
				GpmnElementTypes.GenericGpmnEdge_4005,
				view.getElement() != null ? view.getElement() : view,
				GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeNameEditPart.VISUAL_ID));
		if (parser != null)
		{
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		}
		else
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 6004); //$NON-NLS-1$
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
