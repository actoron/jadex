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
import jadex.tools.gpmn.QueryGoal;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Query Goal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.QueryGoalImpl#getTargetcondition <em>Targetcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.QueryGoalImpl#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.QueryGoalImpl#getFailurecondition <em>Failurecondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.QueryGoalImpl#getFailureconditionLanguage <em>Failurecondition Language</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class QueryGoalImpl extends GoalImpl implements QueryGoal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getTargetcondition() <em>Targetcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String TARGETCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTargetcondition() <em>Targetcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetcondition()
	 * @generated
	 * @ordered
	 */
	protected String targetcondition = TARGETCONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getTargetconditionLanguage() <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String TARGETCONDITION_LANGUAGE_EDEFAULT = "jcl";

	/**
	 * The cached value of the '{@link #getTargetconditionLanguage() <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected String targetconditionLanguage = TARGETCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Targetcondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean targetconditionLanguageESet;

	/**
	 * The default value of the '{@link #getFailurecondition() <em>Failurecondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailurecondition()
	 * @generated
	 * @ordered
	 */
	protected static final String FAILURECONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFailurecondition() <em>Failurecondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailurecondition()
	 * @generated
	 * @ordered
	 */
	protected String failurecondition = FAILURECONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getFailureconditionLanguage() <em>Failurecondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailureconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String FAILURECONDITION_LANGUAGE_EDEFAULT = "jcl";

	/**
	 * The cached value of the '{@link #getFailureconditionLanguage() <em>Failurecondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailureconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected String failureconditionLanguage = FAILURECONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Failurecondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean failureconditionLanguageESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected QueryGoalImpl()
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
		return GpmnPackage.Literals.QUERY_GOAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTargetcondition()
	{
		return targetcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetcondition(String newTargetcondition)
	{
		String oldTargetcondition = targetcondition;
		targetcondition = newTargetcondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.QUERY_GOAL__TARGETCONDITION, oldTargetcondition, targetcondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTargetconditionLanguage()
	{
		return targetconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetconditionLanguage(String newTargetconditionLanguage)
	{
		String oldTargetconditionLanguage = targetconditionLanguage;
		targetconditionLanguage = newTargetconditionLanguage;
		boolean oldTargetconditionLanguageESet = targetconditionLanguageESet;
		targetconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.QUERY_GOAL__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, targetconditionLanguage, !oldTargetconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTargetconditionLanguage()
	{
		String oldTargetconditionLanguage = targetconditionLanguage;
		boolean oldTargetconditionLanguageESet = targetconditionLanguageESet;
		targetconditionLanguage = TARGETCONDITION_LANGUAGE_EDEFAULT;
		targetconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.QUERY_GOAL__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, TARGETCONDITION_LANGUAGE_EDEFAULT, oldTargetconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTargetconditionLanguage()
	{
		return targetconditionLanguageESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFailurecondition()
	{
		return failurecondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFailurecondition(String newFailurecondition)
	{
		String oldFailurecondition = failurecondition;
		failurecondition = newFailurecondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.QUERY_GOAL__FAILURECONDITION, oldFailurecondition, failurecondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFailureconditionLanguage()
	{
		return failureconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFailureconditionLanguage(String newFailureconditionLanguage)
	{
		String oldFailureconditionLanguage = failureconditionLanguage;
		failureconditionLanguage = newFailureconditionLanguage;
		boolean oldFailureconditionLanguageESet = failureconditionLanguageESet;
		failureconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.QUERY_GOAL__FAILURECONDITION_LANGUAGE, oldFailureconditionLanguage, failureconditionLanguage, !oldFailureconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetFailureconditionLanguage()
	{
		String oldFailureconditionLanguage = failureconditionLanguage;
		boolean oldFailureconditionLanguageESet = failureconditionLanguageESet;
		failureconditionLanguage = FAILURECONDITION_LANGUAGE_EDEFAULT;
		failureconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.QUERY_GOAL__FAILURECONDITION_LANGUAGE, oldFailureconditionLanguage, FAILURECONDITION_LANGUAGE_EDEFAULT, oldFailureconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetFailureconditionLanguage()
	{
		return failureconditionLanguageESet;
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
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION:
				return getTargetcondition();
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION_LANGUAGE:
				return getTargetconditionLanguage();
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION:
				return getFailurecondition();
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION_LANGUAGE:
				return getFailureconditionLanguage();
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
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION:
				setTargetcondition((String)newValue);
				return;
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION_LANGUAGE:
				setTargetconditionLanguage((String)newValue);
				return;
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION:
				setFailurecondition((String)newValue);
				return;
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION_LANGUAGE:
				setFailureconditionLanguage((String)newValue);
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
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION:
				setTargetcondition(TARGETCONDITION_EDEFAULT);
				return;
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION_LANGUAGE:
				unsetTargetconditionLanguage();
				return;
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION:
				setFailurecondition(FAILURECONDITION_EDEFAULT);
				return;
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION_LANGUAGE:
				unsetFailureconditionLanguage();
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
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION:
				return TARGETCONDITION_EDEFAULT == null ? targetcondition != null : !TARGETCONDITION_EDEFAULT.equals(targetcondition);
			case GpmnPackage.QUERY_GOAL__TARGETCONDITION_LANGUAGE:
				return isSetTargetconditionLanguage();
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION:
				return FAILURECONDITION_EDEFAULT == null ? failurecondition != null : !FAILURECONDITION_EDEFAULT.equals(failurecondition);
			case GpmnPackage.QUERY_GOAL__FAILURECONDITION_LANGUAGE:
				return isSetFailureconditionLanguage();
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
		result.append(" (targetcondition: ");
		result.append(targetcondition);
		result.append(", targetconditionLanguage: ");
		if (targetconditionLanguageESet) result.append(targetconditionLanguage); else result.append("<unset>");
		result.append(", failurecondition: ");
		result.append(failurecondition);
		result.append(", failureconditionLanguage: ");
		if (failureconditionLanguageESet) result.append(failureconditionLanguage); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //QueryGoalImpl
