/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessEditPart;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.part.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
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
	 * @generated
	 */
	public List getTypesForPopupBar(IAdaptable host)
	{
		IGraphicalEditPart editPart = (IGraphicalEditPart) host
				.getAdapter(IGraphicalEditPart.class);
		if (editPart instanceof GpmnDiagramEditPart)
		{
			ArrayList types = new ArrayList(4);
			types.add(GpmnElementTypes.ActivationPlan_2001);
			types.add(GpmnElementTypes.SubProcess_2002);
			types.add(GpmnElementTypes.BpmnPlan_2003);
			types.add(GpmnElementTypes.Goal_2004);
			return types;
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public List getRelTypesOnSource(IAdaptable source)
	{
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source
				.getAdapter(IGraphicalEditPart.class);
		if (sourceEditPart instanceof ActivationPlanEditPart)
		{
			return ((ActivationPlanEditPart) sourceEditPart)
					.getMARelTypesOnSource();
		}
		if (sourceEditPart instanceof GoalEditPart)
		{
			return ((GoalEditPart) sourceEditPart).getMARelTypesOnSource();
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public List getRelTypesOnTarget(IAdaptable target)
	{
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target
				.getAdapter(IGraphicalEditPart.class);
		if (targetEditPart instanceof ActivationPlanEditPart)
		{
			return ((ActivationPlanEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof SubProcessEditPart)
		{
			return ((SubProcessEditPart) targetEditPart)
					.getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof BpmnPlanEditPart)
		{
			return ((BpmnPlanEditPart) targetEditPart).getMARelTypesOnTarget();
		}
		if (targetEditPart instanceof GoalEditPart)
		{
			return ((GoalEditPart) targetEditPart).getMARelTypesOnTarget();
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public List getRelTypesOnSourceAndTarget(IAdaptable source,
			IAdaptable target)
	{
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source
				.getAdapter(IGraphicalEditPart.class);
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target
				.getAdapter(IGraphicalEditPart.class);
		if (sourceEditPart instanceof ActivationPlanEditPart)
		{
			return ((ActivationPlanEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		if (sourceEditPart instanceof GoalEditPart)
		{
			return ((GoalEditPart) sourceEditPart)
					.getMARelTypesOnSourceAndTarget(targetEditPart);
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public List getTypesForSource(IAdaptable target,
			IElementType relationshipType)
	{
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target
				.getAdapter(IGraphicalEditPart.class);
		if (targetEditPart instanceof ActivationPlanEditPart)
		{
			return ((ActivationPlanEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof SubProcessEditPart)
		{
			return ((SubProcessEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof BpmnPlanEditPart)
		{
			return ((BpmnPlanEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		if (targetEditPart instanceof GoalEditPart)
		{
			return ((GoalEditPart) targetEditPart)
					.getMATypesForSource(relationshipType);
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * @generated
	 */
	public List getTypesForTarget(IAdaptable source,
			IElementType relationshipType)
	{
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source
				.getAdapter(IGraphicalEditPart.class);
		if (sourceEditPart instanceof ActivationPlanEditPart)
		{
			return ((ActivationPlanEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		if (sourceEditPart instanceof GoalEditPart)
		{
			return ((GoalEditPart) sourceEditPart)
					.getMATypesForTarget(relationshipType);
		}
		return Collections.EMPTY_LIST;
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
