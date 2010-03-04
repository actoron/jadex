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
import jadex.tools.gpmn.MaintainGoal;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Maintain Goal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.MaintainGoalImpl#getMaintaincondition <em>Maintaincondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.MaintainGoalImpl#getMaintainconditionLanguage <em>Maintaincondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.MaintainGoalImpl#getTargetcondition <em>Targetcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.MaintainGoalImpl#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MaintainGoalImpl extends GoalImpl implements MaintainGoal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getMaintaincondition() <em>Maintaincondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintaincondition()
	 * @generated
	 * @ordered
	 */
	protected static final String MAINTAINCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaintaincondition() <em>Maintaincondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintaincondition()
	 * @generated
	 * @ordered
	 */
	protected String maintaincondition = MAINTAINCONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaintainconditionLanguage() <em>Maintaincondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintainconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String MAINTAINCONDITION_LANGUAGE_EDEFAULT = "jcl";

	/**
	 * The cached value of the '{@link #getMaintainconditionLanguage() <em>Maintaincondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintainconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected String maintainconditionLanguage = MAINTAINCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Maintaincondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maintainconditionLanguageESet;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MaintainGoalImpl()
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
		return GpmnPackage.Literals.MAINTAIN_GOAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMaintaincondition()
	{
		return maintaincondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaintaincondition(String newMaintaincondition)
	{
		String oldMaintaincondition = maintaincondition;
		maintaincondition = newMaintaincondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION, oldMaintaincondition, maintaincondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMaintainconditionLanguage()
	{
		return maintainconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaintainconditionLanguage(String newMaintainconditionLanguage)
	{
		String oldMaintainconditionLanguage = maintainconditionLanguage;
		maintainconditionLanguage = newMaintainconditionLanguage;
		boolean oldMaintainconditionLanguageESet = maintainconditionLanguageESet;
		maintainconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE, oldMaintainconditionLanguage, maintainconditionLanguage, !oldMaintainconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetMaintainconditionLanguage()
	{
		String oldMaintainconditionLanguage = maintainconditionLanguage;
		boolean oldMaintainconditionLanguageESet = maintainconditionLanguageESet;
		maintainconditionLanguage = MAINTAINCONDITION_LANGUAGE_EDEFAULT;
		maintainconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE, oldMaintainconditionLanguage, MAINTAINCONDITION_LANGUAGE_EDEFAULT, oldMaintainconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetMaintainconditionLanguage()
	{
		return maintainconditionLanguageESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION, oldTargetcondition, targetcondition));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, targetconditionLanguage, !oldTargetconditionLanguageESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, TARGETCONDITION_LANGUAGE_EDEFAULT, oldTargetconditionLanguageESet));
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
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION:
				return getMaintaincondition();
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE:
				return getMaintainconditionLanguage();
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION:
				return getTargetcondition();
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE:
				return getTargetconditionLanguage();
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
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION:
				setMaintaincondition((String)newValue);
				return;
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE:
				setMaintainconditionLanguage((String)newValue);
				return;
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION:
				setTargetcondition((String)newValue);
				return;
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE:
				setTargetconditionLanguage((String)newValue);
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
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION:
				setMaintaincondition(MAINTAINCONDITION_EDEFAULT);
				return;
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE:
				unsetMaintainconditionLanguage();
				return;
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION:
				setTargetcondition(TARGETCONDITION_EDEFAULT);
				return;
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE:
				unsetTargetconditionLanguage();
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
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION:
				return MAINTAINCONDITION_EDEFAULT == null ? maintaincondition != null : !MAINTAINCONDITION_EDEFAULT.equals(maintaincondition);
			case GpmnPackage.MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE:
				return isSetMaintainconditionLanguage();
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION:
				return TARGETCONDITION_EDEFAULT == null ? targetcondition != null : !TARGETCONDITION_EDEFAULT.equals(targetcondition);
			case GpmnPackage.MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE:
				return isSetTargetconditionLanguage();
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
		result.append(" (maintaincondition: ");
		result.append(maintaincondition);
		result.append(", maintainconditionLanguage: ");
		if (maintainconditionLanguageESet) result.append(maintainconditionLanguage); else result.append("<unset>");
		result.append(", targetcondition: ");
		result.append(targetcondition);
		result.append(", targetconditionLanguage: ");
		if (targetconditionLanguageESet) result.append(targetconditionLanguage); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //MaintainGoalImpl
