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
package jadex.tools.gpmn.impl;

import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Plan;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Plan</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.PlanImpl#getBpmnPlan <em>Bpmn Plan</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.PlanImpl#getPriority <em>Priority</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.PlanImpl#getPrecondition <em>Precondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.PlanImpl#getContextcondition <em>Contextcondition</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PlanImpl extends ParameterizedVertexImpl implements Plan
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getBpmnPlan() <em>Bpmn Plan</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBpmnPlan()
	 * @generated
	 * @ordered
	 */
	protected static final String BPMN_PLAN_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBpmnPlan() <em>Bpmn Plan</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBpmnPlan()
	 * @generated
	 * @ordered
	 */
	protected String bpmnPlan = BPMN_PLAN_EDEFAULT;

	/**
	 * The default value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected static final int PRIORITY_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected int priority = PRIORITY_EDEFAULT;

	/**
	 * This is true if the Priority attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean priorityESet;

	/**
	 * The default value of the '{@link #getPrecondition() <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrecondition()
	 * @generated
	 * @ordered
	 */
	protected static final String PRECONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrecondition() <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrecondition()
	 * @generated
	 * @ordered
	 */
	protected String precondition = PRECONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getContextcondition() <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTEXTCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContextcondition() <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextcondition()
	 * @generated
	 * @ordered
	 */
	protected String contextcondition = CONTEXTCONDITION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PlanImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return GpmnPackage.Literals.PLAN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBpmnPlan()
	{
		return bpmnPlan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBpmnPlan(String newBpmnPlan)
	{
		String oldBpmnPlan = bpmnPlan;
		bpmnPlan = newBpmnPlan;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.PLAN__BPMN_PLAN, oldBpmnPlan, bpmnPlan));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPriority(int newPriority)
	{
		int oldPriority = priority;
		priority = newPriority;
		boolean oldPriorityESet = priorityESet;
		priorityESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.PLAN__PRIORITY, oldPriority, priority, !oldPriorityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPriority()
	{
		int oldPriority = priority;
		boolean oldPriorityESet = priorityESet;
		priority = PRIORITY_EDEFAULT;
		priorityESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.PLAN__PRIORITY, oldPriority, PRIORITY_EDEFAULT, oldPriorityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPriority()
	{
		return priorityESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPrecondition()
	{
		return precondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrecondition(String newPrecondition)
	{
		String oldPrecondition = precondition;
		precondition = newPrecondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.PLAN__PRECONDITION, oldPrecondition, precondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getContextcondition()
	{
		return contextcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContextcondition(String newContextcondition)
	{
		String oldContextcondition = contextcondition;
		contextcondition = newContextcondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.PLAN__CONTEXTCONDITION, oldContextcondition, contextcondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case GpmnPackage.PLAN__BPMN_PLAN:
				return getBpmnPlan();
			case GpmnPackage.PLAN__PRIORITY:
				return getPriority();
			case GpmnPackage.PLAN__PRECONDITION:
				return getPrecondition();
			case GpmnPackage.PLAN__CONTEXTCONDITION:
				return getContextcondition();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case GpmnPackage.PLAN__BPMN_PLAN:
				setBpmnPlan((String)newValue);
				return;
			case GpmnPackage.PLAN__PRIORITY:
				setPriority((Integer)newValue);
				return;
			case GpmnPackage.PLAN__PRECONDITION:
				setPrecondition((String)newValue);
				return;
			case GpmnPackage.PLAN__CONTEXTCONDITION:
				setContextcondition((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case GpmnPackage.PLAN__BPMN_PLAN:
				setBpmnPlan(BPMN_PLAN_EDEFAULT);
				return;
			case GpmnPackage.PLAN__PRIORITY:
				unsetPriority();
				return;
			case GpmnPackage.PLAN__PRECONDITION:
				setPrecondition(PRECONDITION_EDEFAULT);
				return;
			case GpmnPackage.PLAN__CONTEXTCONDITION:
				setContextcondition(CONTEXTCONDITION_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case GpmnPackage.PLAN__BPMN_PLAN:
				return BPMN_PLAN_EDEFAULT == null ? bpmnPlan != null : !BPMN_PLAN_EDEFAULT.equals(bpmnPlan);
			case GpmnPackage.PLAN__PRIORITY:
				return isSetPriority();
			case GpmnPackage.PLAN__PRECONDITION:
				return PRECONDITION_EDEFAULT == null ? precondition != null : !PRECONDITION_EDEFAULT.equals(precondition);
			case GpmnPackage.PLAN__CONTEXTCONDITION:
				return CONTEXTCONDITION_EDEFAULT == null ? contextcondition != null : !CONTEXTCONDITION_EDEFAULT.equals(contextcondition);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (bpmnPlan: ");
		result.append(bpmnPlan);
		result.append(", priority: ");
		if (priorityESet) result.append(priority); else result.append("<unset>");
		result.append(", precondition: ");
		result.append(precondition);
		result.append(", contextcondition: ");
		result.append(contextcondition);
		result.append(')');
		return result.toString();
	}

} //PlanImpl
