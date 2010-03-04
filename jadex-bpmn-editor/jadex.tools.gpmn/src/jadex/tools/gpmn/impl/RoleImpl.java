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
import jadex.tools.gpmn.Role;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.RoleImpl#getInitialPerson <em>Initial Person</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.RoleImpl#getPersonType <em>Person Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RoleImpl extends NamedObjectImpl implements Role
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getInitialPerson() <em>Initial Person</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialPerson()
	 * @generated
	 * @ordered
	 */
	protected static final String INITIAL_PERSON_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInitialPerson() <em>Initial Person</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialPerson()
	 * @generated
	 * @ordered
	 */
	protected String initialPerson = INITIAL_PERSON_EDEFAULT;

	/**
	 * The default value of the '{@link #getPersonType() <em>Person Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPersonType()
	 * @generated
	 * @ordered
	 */
	protected static final String PERSON_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPersonType() <em>Person Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPersonType()
	 * @generated
	 * @ordered
	 */
	protected String personType = PERSON_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RoleImpl()
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
		return GpmnPackage.Literals.ROLE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInitialPerson()
	{
		return initialPerson;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInitialPerson(String newInitialPerson)
	{
		String oldInitialPerson = initialPerson;
		initialPerson = newInitialPerson;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ROLE__INITIAL_PERSON, oldInitialPerson, initialPerson));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPersonType()
	{
		return personType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPersonType(String newPersonType)
	{
		String oldPersonType = personType;
		personType = newPersonType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ROLE__PERSON_TYPE, oldPersonType, personType));
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
			case GpmnPackage.ROLE__INITIAL_PERSON:
				return getInitialPerson();
			case GpmnPackage.ROLE__PERSON_TYPE:
				return getPersonType();
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
			case GpmnPackage.ROLE__INITIAL_PERSON:
				setInitialPerson((String)newValue);
				return;
			case GpmnPackage.ROLE__PERSON_TYPE:
				setPersonType((String)newValue);
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
			case GpmnPackage.ROLE__INITIAL_PERSON:
				setInitialPerson(INITIAL_PERSON_EDEFAULT);
				return;
			case GpmnPackage.ROLE__PERSON_TYPE:
				setPersonType(PERSON_TYPE_EDEFAULT);
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
			case GpmnPackage.ROLE__INITIAL_PERSON:
				return INITIAL_PERSON_EDEFAULT == null ? initialPerson != null : !INITIAL_PERSON_EDEFAULT.equals(initialPerson);
			case GpmnPackage.ROLE__PERSON_TYPE:
				return PERSON_TYPE_EDEFAULT == null ? personType != null : !PERSON_TYPE_EDEFAULT.equals(personType);
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
		result.append(" (initialPerson: ");
		result.append(initialPerson);
		result.append(", personType: ");
		result.append(personType);
		result.append(')');
		return result.toString();
	}

} //RoleImpl
