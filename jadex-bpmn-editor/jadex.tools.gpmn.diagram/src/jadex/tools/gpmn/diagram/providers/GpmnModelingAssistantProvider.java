/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.diagram.edit.parts.AchieveGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ContextEditPart;
import jadex.tools.gpmn.diagram.edit.parts.DataObjectEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnElementEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MaintainGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessageGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ParallelGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PerformGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.QueryGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SequentialGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.TextAnnotationEditPart;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditDomain;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.part.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantProvider;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @generated
 */
public class GpmnModelingAssistantProvider extends ModelingAssistantProvider
{

	/**
	 * Filter the domain removed {@link IElementType} from model assistant element list
	 * @param listToFilter
	 * @param host
	 * @return filtered list
	 */
	protected List filterDomainRemovedIElementTypesFromHostList(
			List listToFilter, IAdaptable host)
	{
		if (listToFilter == Collections.EMPTY_LIST)
		{
			return listToFilter;
		}

		Set<IElementType> removedElementTypes = getRemovedDomainElementTypes(host);
		if (removedElementTypes != Collections.EMPTY_SET)
		{
			ArrayList filteredList = new ArrayList();
			for (Object type : listToFilter)
			{
				if (removedElementTypes.contains(type))
				{
					continue;
				}
				filteredList.add(type);
			}
			return filteredList;
		}

		// fall through
		return listToFilter;
	}

	/**
	 * Access the editing domain an resolve removed model elements
	 * @param host
	 * @return Set<IElementType> of removed domain elements
	 * @generated NOT
	 */
	protected Set getRemovedDomainElementTypes(IAdaptable host)
	{
		IGraphicalEditPart editPart = (IGraphicalEditPart) host
				.getAdapter(IGraphicalEditPart.class);
		if (editPart != null)
		{
			EditDomain domain = editPart.getViewer().getEditDomain();
			if (domain != null && domain instanceof GpmnDiagramEditDomain)
			{
				Set<IElementType> removedElementTypes = ((GpmnDiagramEditDomain) domain)
						.getRemovedElementTypes();

				return removedElementTypes;
			}
		}

		return Collections.EMPTY_SET;
	}

	/**
	 * @generated
	 */
	public List getTypesForPopupBarGen(IAdaptable host)
	{
		IGraphicalEditPart editPart = (IGraphicalEditPart) host
				.getAdapter(IGraphicalEditPart.class);
		if (editPart instanceof GpmnDiagramEditPart)
		{
			ArrayList types = new ArrayList(14);
			types.add(GpmnElementTypes.Process_2001);
			types.add(GpmnElementTypes.AchieveGoal_2002);
			types.add(GpmnElementTypes.MaintainGoal_2003);
			types.add(GpmnElementTypes.PerformGoal_2004);
			types.add(GpmnElementTypes.QueryGoal_2005);
			types.add(GpmnElementTypes.SequentialGoal_2006);
			types.add(GpmnElementTypes.ParallelGoal_2007);
			types.add(GpmnElementTypes.MessageGoal_2008);
			types.add(GpmnElementTypes.SubProcessGoal_2009);
			types.add(GpmnElementTypes.Plan_2010);
			types.add(GpmnElementTypes.Context_2011);
			types.add(GpmnElementTypes.TextAnnotation_2012);
			types.add(GpmnElementTypes.DataObject_2013);
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
			return types;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated NOT, remove some types from popup bar
	 */
	public List getTypesForPopupBar(IAdaptable host)
	{
		return filterDomainRemovedIElementTypesFromHostList(
				getTypesForPopupBarGen(host), host);
	}

	/**
	 * @generated
	 */
	public List getRelTypesOnSourceGen(IAdaptable source)
	{
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source
				.getAdapter(IGraphicalEditPart.class);
		if (sourceEditPart instanceof ProcessEditPart)
		{
			return ((ProcessEditPart) sourceEditPart).getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof AchieveGoalEditPart)
		{
			return ((AchieveGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof MaintainGoalEditPart)
		{
			return ((MaintainGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof PerformGoalEditPart)
		{
			return ((PerformGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof QueryGoalEditPart)
		{
			return ((QueryGoalEditPart) sourceEditPart).getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof SequentialGoalEditPart)
		{
			return ((SequentialGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof ParallelGoalEditPart)
		{
			return ((ParallelGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof MessageGoalEditPart)
		{
			return ((MessageGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof SubProcessGoalEditPart)
		{
			return ((SubProcessGoalEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof PlanEditPart)
		{
			return ((PlanEditPart) sourceEditPart).getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof ContextEditPart)
		{
			return ((ContextEditPart) sourceEditPart).getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof TextAnnotationEditPart)
		{
			return ((TextAnnotationEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof DataObjectEditPart)
		{
			return ((DataObjectEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof GenericGpmnElementEditPart)
		{
			return ((GenericGpmnElementEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated NOT
	 */
	public List getRelTypesOnSource(IAdaptable source)
	{
		return filterDomainRemovedIElementTypesFromHostList(
				getRelTypesOnSourceGen(source), source);
	}

	/**
	 * @generated
	 */
	public List getRelTypesOnTargetGen(IAdaptable target)
	{
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target
				.getAdapter(IGraphicalEditPart.class);
		if (targetEditPart instanceof ProcessEditPart)
		{
			return ((ProcessEditPart) targetEditPart).getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			return ((AchieveGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			return ((MaintainGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			return ((PerformGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			return ((QueryGoalEditPart) targetEditPart).getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			return ((SequentialGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			return ((ParallelGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof MessageGoalEditPart)
		{
			return ((MessageGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			return ((SubProcessGoalEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof PlanEditPart)
		{
			return ((PlanEditPart) targetEditPart).getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof GenericGpmnElementEditPart)
		{
			return ((GenericGpmnElementEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated NOT
	 */
	public List getRelTypesOnTarget(IAdaptable target)
	{
		return filterDomainRemovedIElementTypesFromHostList(
				getRelTypesOnTargetGen(target), target);
	}

	/**
	 * @generated
	 */
	public List getRelTypesOnSourceAndTargetGen(IAdaptable source,
			IAdaptable target)
	{
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source
				.getAdapter(IGraphicalEditPart.class);
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target
				.getAdapter(IGraphicalEditPart.class);
		if (sourceEditPart instanceof ProcessEditPart)
		{
			return ((ProcessEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof AchieveGoalEditPart)
		{
			return ((AchieveGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof MaintainGoalEditPart)
		{
			return ((MaintainGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof PerformGoalEditPart)
		{
			return ((PerformGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof QueryGoalEditPart)
		{
			return ((QueryGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof SequentialGoalEditPart)
		{
			return ((SequentialGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof ParallelGoalEditPart)
		{
			return ((ParallelGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof MessageGoalEditPart)
		{
			return ((MessageGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof SubProcessGoalEditPart)
		{
			return ((SubProcessGoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof PlanEditPart)
		{
			return ((PlanEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof ContextEditPart)
		{
			return ((ContextEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof TextAnnotationEditPart)
		{
			return ((TextAnnotationEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof DataObjectEditPart)
		{
			return ((DataObjectEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof GenericGpmnElementEditPart)
		{
			return ((GenericGpmnElementEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated NOT
	 */
	public List getRelTypesOnSourceAndTarget(IAdaptable source,
			IAdaptable target)
	{
		return filterDomainRemovedIElementTypesFromHostList(
				getRelTypesOnSourceAndTargetGen(source, target), target);
	}

	/**
	 * @generated
	 */
	public List getTypesForSourceGen(IAdaptable target,
			IElementType relationshipType)
	{
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target
				.getAdapter(IGraphicalEditPart.class);
		if (targetEditPart instanceof ProcessEditPart)
		{
			return ((ProcessEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			return ((AchieveGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			return ((MaintainGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			return ((PerformGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			return ((QueryGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			return ((SequentialGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			return ((ParallelGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof MessageGoalEditPart)
		{
			return ((MessageGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			return ((SubProcessGoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof PlanEditPart)
		{
			return ((PlanEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof GenericGpmnElementEditPart)
		{
			return ((GenericGpmnElementEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated NOT
	 */
	public List getTypesForSource(IAdaptable target,
			IElementType relationshipType)
	{
		return filterDomainRemovedIElementTypesFromHostList(
				getTypesForSourceGen(target, relationshipType), target);
	}

	/**
	 * @generated
	 */
	public List getTypesForTargetGen(IAdaptable source,
			IElementType relationshipType)
	{
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source
				.getAdapter(IGraphicalEditPart.class);
		if (sourceEditPart instanceof ProcessEditPart)
		{
			return ((ProcessEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof AchieveGoalEditPart)
		{
			return ((AchieveGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof MaintainGoalEditPart)
		{
			return ((MaintainGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof PerformGoalEditPart)
		{
			return ((PerformGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof QueryGoalEditPart)
		{
			return ((QueryGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof SequentialGoalEditPart)
		{
			return ((SequentialGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof ParallelGoalEditPart)
		{
			return ((ParallelGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof MessageGoalEditPart)
		{
			return ((MessageGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof SubProcessGoalEditPart)
		{
			return ((SubProcessGoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof PlanEditPart)
		{
			return ((PlanEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof ContextEditPart)
		{
			return ((ContextEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof TextAnnotationEditPart)
		{
			return ((TextAnnotationEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof DataObjectEditPart)
		{
			return ((DataObjectEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof GenericGpmnElementEditPart)
		{
			return ((GenericGpmnElementEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @generated NOT
	 */
	public List getTypesForTarget(IAdaptable source,
			IElementType relationshipType)
	{
		return filterDomainRemovedIElementTypesFromHostList(
				getTypesForTargetGen(source, relationshipType), source);
	}

	/**
	 * @generated
	 */
	public EObject selectExistingElementForSource(IAdaptable target,
			IElementType relationshipType)
	{
		return selectExistingElement(target, getTypesForSource(target,
				relationshipType));
	}

	/**
	 * @generated
	 */
	public EObject selectExistingElementForTarget(IAdaptable source,
			IElementType relationshipType)
	{
		return selectExistingElement(source, getTypesForTarget(source,
				relationshipType));
	}

	/**
	 * @generated
	 */
	protected EObject selectExistingElement(IAdaptable host, Collection types)
	{
		if (types.isEmpty())
		{
			return null;
		}
		IGraphicalEditPart editPart = (IGraphicalEditPart) host
				.getAdapter(IGraphicalEditPart.class);
		if (editPart == null)
		{
			return null;
		}
		Diagram diagram = (Diagram) editPart.getRoot().getContents().getModel();
		Collection elements = new HashSet();
		for (Iterator it = diagram.getElement().eAllContents(); it.hasNext();)
		{
			EObject element = (EObject) it.next();
			if (isApplicableElement(element, types))
			{
				elements.add(element);
			}
		}
		if (elements.isEmpty())
		{
			return null;
		}
		return selectElement((EObject[]) elements.toArray(new EObject[elements
				.size()]));
	}

	/**
	 * @generated
	 */
	protected boolean isApplicableElement(EObject element, Collection types)
	{
		IElementType type = ElementTypeRegistry.getInstance().getElementType(
				element);
		return types.contains(type);
	}

	/**
	 * @generated
	 */
	protected EObject selectElement(EObject[] elements)
	{
		Shell shell = Display.getCurrent().getActiveShell();
		ILabelProvider labelProvider = new AdapterFactoryLabelProvider(
				GpmnDiagramEditorPlugin.getInstance()
						.getItemProvidersAdapterFactory());
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, labelProvider);
		dialog.setMessage(Messages.GpmnModelingAssistantProviderMessage);
		dialog.setTitle(Messages.GpmnModelingAssistantProviderTitle);
		dialog.setMultipleSelection(false);
		dialog.setElements(elements);
		EObject selected = null;
		if (dialog.open() == Window.OK)
		{
			selected = (EObject) dialog.getFirstResult();
		}
		return selected;
	}
}
