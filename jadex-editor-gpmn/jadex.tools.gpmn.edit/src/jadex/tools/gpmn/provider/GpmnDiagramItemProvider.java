/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *
 * $Id$
 */
package jadex.tools.gpmn.provider;

import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.GpmnPackage;

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
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link jadex.tools.gpmn.GpmnDiagram} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class GpmnDiagramItemProvider extends NamedObjectItemProvider implements
		IEditingDomainItemProvider, IStructuredItemContentProvider,
		ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnDiagramItemProvider(AdapterFactory adapterFactory)
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

			addPackagePropertyDescriptor(object);
			addImportsPropertyDescriptor(object);
			addContextPropertyDescriptor(object);
			addAuthorPropertyDescriptor(object);
			addRevisionPropertyDescriptor(object);
			addTitlePropertyDescriptor(object);
			addVersionPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Author feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAuthorPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_author_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_author_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__AUTHOR,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_DefaultPropertyCategory"), //$NON-NLS-1$
				 null));
	}

	/**
	 * This adds a property descriptor for the Revision feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRevisionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_revision_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_revision_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__REVISION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_DefaultPropertyCategory"), //$NON-NLS-1$
				 null));
	}

	/**
	 * This adds a property descriptor for the Title feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTitlePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_title_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_title_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__TITLE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_DefaultPropertyCategory"), //$NON-NLS-1$
				 null));
	}

	/**
	 * This adds a property descriptor for the Version feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addVersionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_version_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_version_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__VERSION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Context feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addContextPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_context_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_context_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__CONTEXT,
				 true,
				 false,
				 true,
				 null,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Package feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPackagePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_package_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_package_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__PACKAGE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_DefaultPropertyCategory"), //$NON-NLS-1$
				 null));
	}

	/**
	 * This adds a property descriptor for the Imports feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addImportsPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_GpmnDiagram_imports_feature"), //$NON-NLS-1$
				 getString("_UI_PropertyDescriptor_description", "_UI_GpmnDiagram_imports_feature", "_UI_GpmnDiagram_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				 GpmnPackage.Literals.GPMN_DIAGRAM__IMPORTS,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
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
			childrenFeatures.add(GpmnPackage.Literals.GPMN_DIAGRAM__GOALS);
			childrenFeatures.add(GpmnPackage.Literals.GPMN_DIAGRAM__PLANS);
			childrenFeatures.add(GpmnPackage.Literals.GPMN_DIAGRAM__SUB_PROCESSES);
			childrenFeatures.add(GpmnPackage.Literals.GPMN_DIAGRAM__ACTIVATION_EDGES);
			childrenFeatures.add(GpmnPackage.Literals.GPMN_DIAGRAM__PLAN_EDGES);
			childrenFeatures.add(GpmnPackage.Literals.GPMN_DIAGRAM__SUPPRESSION_EDGES);
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
	 * This returns GpmnDiagram.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object)
	{
		return overlayImage(object, getResourceLocator().getImage("full/obj16/GpmnDiagram")); //$NON-NLS-1$
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
		String label = ((GpmnDiagram)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_GpmnDiagram_type") : //$NON-NLS-1$
			getString("_UI_GpmnDiagram_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

		switch (notification.getFeatureID(GpmnDiagram.class))
		{
			case GpmnPackage.GPMN_DIAGRAM__PACKAGE:
			case GpmnPackage.GPMN_DIAGRAM__IMPORTS:
			case GpmnPackage.GPMN_DIAGRAM__AUTHOR:
			case GpmnPackage.GPMN_DIAGRAM__REVISION:
			case GpmnPackage.GPMN_DIAGRAM__TITLE:
			case GpmnPackage.GPMN_DIAGRAM__VERSION:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
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
				(GpmnPackage.Literals.GPMN_DIAGRAM__GOALS,
				 GpmnFactory.eINSTANCE.createGoal()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GPMN_DIAGRAM__PLANS,
				 GpmnFactory.eINSTANCE.createBpmnPlan()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GPMN_DIAGRAM__PLANS,
				 GpmnFactory.eINSTANCE.createActivationPlan()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GPMN_DIAGRAM__SUB_PROCESSES,
				 GpmnFactory.eINSTANCE.createSubProcess()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GPMN_DIAGRAM__ACTIVATION_EDGES,
				 GpmnFactory.eINSTANCE.createActivationEdge()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GPMN_DIAGRAM__PLAN_EDGES,
				 GpmnFactory.eINSTANCE.createPlanEdge()));

		newChildDescriptors.add
			(createChildParameter
				(GpmnPackage.Literals.GPMN_DIAGRAM__SUPPRESSION_EDGES,
				 GpmnFactory.eINSTANCE.createSuppressionEdge()));
	}

}
