/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 *
 * $Id$
 */
package jadex.tools.gpmn.provider;

import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Graph;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link jadex.tools.gpmn.Graph} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class GraphItemProvider extends ArtifactsContainerItemProvider implements
		IEditingDomainItemProvider, IStructuredItemContentProvider,
		ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource,
		ITableItemLabelProvider
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GraphItemProvider(AdapterFactory adapterFactory)
	{
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
	{
		if (itemPropertyDescriptors == null)
		{
			super.getPropertyDescriptors(object);

			addAssociationsPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Associations feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAssociationsPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_AssociationTarget_associations_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_AssociationTarget_associations_feature", "_UI_AssociationTarget_type"),
				 GpmnPackage.Literals.ASSOCIATION_TARGET__ASSOCIATIONS,
				 true,
				 false,
				 true,
				 null,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(
			Object object)
	{
		if (childrenFeatures == null)
		{
			super.getChildrenFeatures(object);
			childrenFeatures.add(GpmnPackage.Literals.GRAPH__VERTICES);
			childrenFeatures.add(GpmnPackage.Literals.GRAPH__SEQUENCE_EDGES);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child)
	{
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns Graph.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object)
	{
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Graph"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object)
	{
		String label = ((Graph)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_Graph_type") :
			getString("_UI_Graph_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification)
	{
		updateChildren(notification);

		switch (notification.getFeatureID(Graph.class))
		{
			case GpmnPackage.GRAPH__VERTICES:
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(
			Collection<Object> newChildDescriptors, Object object)
	{
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createVertex()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createParameterizedVertex()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createAchieveGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createMaintainGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createMessageGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createParallelGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createPerformGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createPlan()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createQueryGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createSequentialGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createSubProcessGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__VERTICES,
				 GpmnFactory.eINSTANCE.createGenericGpmnElement()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__SEQUENCE_EDGES,
				 GpmnFactory.eINSTANCE.createEdge()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__SEQUENCE_EDGES,
				 GpmnFactory.eINSTANCE.createParameterizedEdge()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__SEQUENCE_EDGES,
				 GpmnFactory.eINSTANCE.createPlanEdge()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__SEQUENCE_EDGES,
				 GpmnFactory.eINSTANCE.createSubGoalEdge()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GRAPH__SEQUENCE_EDGES,
				 GpmnFactory.eINSTANCE.createGenericGpmnEdge()));
	}

}
