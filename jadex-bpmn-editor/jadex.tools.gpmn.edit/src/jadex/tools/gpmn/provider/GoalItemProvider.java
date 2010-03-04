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

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnPackage;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

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
 * This is the item provider adapter for a {@link jadex.tools.gpmn.Goal} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class GoalItemProvider extends ParameterizedVertexItemProvider implements
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
	public GoalItemProvider(AdapterFactory adapterFactory)
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

			addUniquePropertyDescriptor(object);
			addCreationconditionPropertyDescriptor(object);
			addCreationconditionLanguagePropertyDescriptor(object);
			addContextconditionPropertyDescriptor(object);
			addContextconditionLanguagePropertyDescriptor(object);
			addDropconditionPropertyDescriptor(object);
			addDropconditionLanguagePropertyDescriptor(object);
			addRecurconditionPropertyDescriptor(object);
			addDeliberationPropertyDescriptor(object);
			addOnSuccessHandlerPropertyDescriptor(object);
			addOnSkipHandlerPropertyDescriptor(object);
			addOnFailureHandlerPropertyDescriptor(object);
			addExcludePropertyDescriptor(object);
			addGoalTypePropertyDescriptor(object);
			addPosttoallPropertyDescriptor(object);
			addRandomselectionPropertyDescriptor(object);
			addRecalculatePropertyDescriptor(object);
			addRecurPropertyDescriptor(object);
			addRecurdelayPropertyDescriptor(object);
			addRetryPropertyDescriptor(object);
			addRetrydelayPropertyDescriptor(object);
			addSequentialPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Unique feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUniquePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_unique_feature"),
				 getString("_UI_Goal_unique_description"),
				 GpmnPackage.Literals.GOAL__UNIQUE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Creationcondition feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addCreationconditionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_creationcondition_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_creationcondition_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__CREATIONCONDITION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Creationcondition Language feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addCreationconditionLanguagePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_creationconditionLanguage_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_creationconditionLanguage_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__CREATIONCONDITION_LANGUAGE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Contextcondition feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addContextconditionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_contextcondition_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_contextcondition_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__CONTEXTCONDITION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Contextcondition Language feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addContextconditionLanguagePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_contextconditionLanguage_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_contextconditionLanguage_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__CONTEXTCONDITION_LANGUAGE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Dropcondition feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDropconditionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_dropcondition_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_dropcondition_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__DROPCONDITION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Dropcondition Language feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDropconditionLanguagePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_dropconditionLanguage_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_dropconditionLanguage_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__DROPCONDITION_LANGUAGE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Recurcondition feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRecurconditionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_recurcondition_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_recurcondition_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RECURCONDITION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Deliberation feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDeliberationPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_deliberation_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_deliberation_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__DELIBERATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the On Success Handler feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addOnSuccessHandlerPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_onSuccessHandler_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_onSuccessHandler_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__ON_SUCCESS_HANDLER,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the On Skip Handler feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addOnSkipHandlerPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_onSkipHandler_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_onSkipHandler_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__ON_SKIP_HANDLER,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the On Failure Handler feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addOnFailureHandlerPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_onFailureHandler_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_onFailureHandler_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__ON_FAILURE_HANDLER,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Exclude feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addExcludePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_exclude_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_exclude_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__EXCLUDE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Goal Type feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addGoalTypePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_goalType_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_goalType_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__GOAL_TYPE,
				 false,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Posttoall feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPosttoallPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_posttoall_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_posttoall_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__POSTTOALL,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Randomselection feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRandomselectionPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_randomselection_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_randomselection_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RANDOMSELECTION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Recalculate feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRecalculatePropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_recalculate_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_recalculate_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RECALCULATE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Recur feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRecurPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_recur_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_recur_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RECUR,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Recurdelay feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRecurdelayPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_recurdelay_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_recurdelay_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RECURDELAY,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Retry feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRetryPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_retry_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_retry_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RETRY,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Retrydelay feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRetrydelayPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_retrydelay_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_retrydelay_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__RETRYDELAY,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This adds a property descriptor for the Sequential feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addSequentialPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Goal_sequential_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Goal_sequential_feature", "_UI_Goal_type"),
				 GpmnPackage.Literals.GOAL__SEQUENTIAL,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 getString("_UI_StandardPropertyCategory"),
				 null));
	}

	/**
	 * This returns Goal.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object)
	{
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Goal"));
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
		String label = ((Goal)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_Goal_type") :
			getString("_UI_Goal_type") + " " + label;
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

		switch (notification.getFeatureID(Goal.class))
		{
			case GpmnPackage.GOAL__UNIQUE:
			case GpmnPackage.GOAL__CREATIONCONDITION:
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
			case GpmnPackage.GOAL__CONTEXTCONDITION:
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
			case GpmnPackage.GOAL__DROPCONDITION:
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
			case GpmnPackage.GOAL__RECURCONDITION:
			case GpmnPackage.GOAL__DELIBERATION:
			case GpmnPackage.GOAL__ON_SUCCESS_HANDLER:
			case GpmnPackage.GOAL__ON_SKIP_HANDLER:
			case GpmnPackage.GOAL__ON_FAILURE_HANDLER:
			case GpmnPackage.GOAL__EXCLUDE:
			case GpmnPackage.GOAL__GOAL_TYPE:
			case GpmnPackage.GOAL__POSTTOALL:
			case GpmnPackage.GOAL__RANDOMSELECTION:
			case GpmnPackage.GOAL__RECALCULATE:
			case GpmnPackage.GOAL__RECUR:
			case GpmnPackage.GOAL__RECURDELAY:
			case GpmnPackage.GOAL__RETRY:
			case GpmnPackage.GOAL__RETRYDELAY:
			case GpmnPackage.GOAL__SEQUENTIAL:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
	}

}
