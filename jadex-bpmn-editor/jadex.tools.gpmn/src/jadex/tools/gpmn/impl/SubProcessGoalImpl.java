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
import jadex.tools.gpmn.SubProcessGoal;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sub Process Goal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.SubProcessGoalImpl#getGoalref <em>Goalref</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SubProcessGoalImpl extends GoalImpl implements SubProcessGoal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getGoalref() <em>Goalref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGoalref()
	 * @generated
	 * @ordered
	 */
	protected static final String GOALREF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGoalref() <em>Goalref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGoalref()
	 * @generated
	 * @ordered
	 */
	protected String goalref = GOALREF_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SubProcessGoalImpl()
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
		return GpmnPackage.Literals.SUB_PROCESS_GOAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGoalref()
	{
		return goalref;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGoalref(String newGoalref)
	{
		String oldGoalref = goalref;
		goalref = newGoalref;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.SUB_PROCESS_GOAL__GOALREF, oldGoalref, goalref));
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
			case GpmnPackage.SUB_PROCESS_GOAL__GOALREF:
				return getGoalref();
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
			case GpmnPackage.SUB_PROCESS_GOAL__GOALREF:
				setGoalref((String)newValue);
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
			case GpmnPackage.SUB_PROCESS_GOAL__GOALREF:
				setGoalref(GOALREF_EDEFAULT);
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
			case GpmnPackage.SUB_PROCESS_GOAL__GOALREF:
				return GOALREF_EDEFAULT == null ? goalref != null : !GOALREF_EDEFAULT.equals(goalref);
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
		result.append(" (goalref: ");
		result.append(goalref);
		result.append(')');
		return result.toString();
	}

} //SubProcessGoalImpl
