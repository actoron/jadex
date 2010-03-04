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
import jadex.tools.gpmn.InterGraphVertex;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link jadex.tools.gpmn.InterGraphVertex} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class InterGraphVertexItemProvider extends AssociationTargetItemProvider
		implements IEditingDomainItemProvider, IStructuredItemContentProvider,
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
	public InterGraphVertexItemProvider(AdapterFactory adapterFactory)
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

			addDescriptionPropertyDescriptor(object);
			addNamePropertyDescriptor(object);
			addNcnamePropertyDescriptor(object);
			addIncomingInterGraphEdgesPropertyDescriptor(object);
			addOutgoingInterGraphEdgesPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Description feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDescriptionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_NamedObject_description_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_NamedObject_description_feature", "_UI_NamedObject_type"),
				 GpmnPackage.Literals.NAMED_OBJECT__DESCRIPTION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_NamedObject_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_NamedObject_name_feature", "_UI_NamedObject_type"),
				 GpmnPackage.Literals.NAMED_OBJECT__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Ncname feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNcnamePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_NamedObject_ncname_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_NamedObject_ncname_feature", "_UI_NamedObject_type"),
				 GpmnPackage.Literals.NAMED_OBJECT__NCNAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Incoming Inter Graph Edges feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addIncomingInterGraphEdgesPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_InterGraphVertex_incomingInterGraphEdges_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_InterGraphVertex_incomingInterGraphEdges_feature", "_UI_InterGraphVertex_type"),
				 GpmnPackage.Literals.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES,
				 true,
				 false,
				 true,
				 null,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Outgoing Inter Graph Edges feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addOutgoingInterGraphEdgesPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_InterGraphVertex_outgoingInterGraphEdges_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_InterGraphVertex_outgoingInterGraphEdges_feature", "_UI_InterGraphVertex_type"),
				 GpmnPackage.Literals.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES,
				 true,
				 false,
				 true,
				 null,
				 null,
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
			childrenFeatures.add(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES);
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
	 * This returns InterGraphVertex.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object)
	{
		return overlayImage(object, getResourceLocator().getImage("full/obj16/InterGraphVertex"));
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
		String label = ((InterGraphVertex)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_InterGraphVertex_type") :
			getString("_UI_InterGraphVertex_type") + " " + label;
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

		switch (notification.getFeatureID(InterGraphVertex.class))
		{
			case GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION:
			case GpmnPackage.INTER_GRAPH_VERTEX__NAME:
			case GpmnPackage.INTER_GRAPH_VERTEX__NCNAME:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES:
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
				(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES,
				 FeatureMapUtil.createEntry
					(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES,
					 GpmnFactory.eINSTANCE.createInterGraphEdge())));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES,
				 FeatureMapUtil.createEntry
					(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES,
					 GpmnFactory.eINSTANCE.createMessagingEdge())));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES,
				 FeatureMapUtil.createEntry
					(GpmnPackage.Literals.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES,
					 GpmnFactory.eINSTANCE.createInterGraphEdge())));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES,
				 FeatureMapUtil.createEntry
					(GpmnPackage.Literals.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES,
					 GpmnFactory.eINSTANCE.createMessagingEdge())));
	}

	/**
	 * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature,
			Object child, Collection<?> selection)
	{
		Object childFeature = feature;
		Object childObject = child;

		if (childFeature instanceof EStructuralFeature && FeatureMapUtil.isFeatureMap((EStructuralFeature)childFeature))
		{
			FeatureMap.Entry entry = (FeatureMap.Entry)childObject;
			childFeature = entry.getEStructuralFeature();
			childObject = entry.getValue();
		}

		boolean qualify =
			childFeature == GpmnPackage.Literals.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES ||
			childFeature == GpmnPackage.Literals.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES;

		if (qualify)
		{
			return getString
				("_UI_CreateChild_text2",
				 new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

}
