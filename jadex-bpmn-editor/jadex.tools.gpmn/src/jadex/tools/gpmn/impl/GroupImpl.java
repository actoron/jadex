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
import jadex.tools.gpmn.Group;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.GroupImpl#getMembers <em>Members</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GroupImpl#getCoordinator <em>Coordinator</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GroupImpl#getHead <em>Head</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GroupImpl extends NamedObjectImpl implements Group
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The cached value of the '{@link #getMembers() <em>Members</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMembers()
	 * @generated
	 * @ordered
	 */
	protected EList<String> members;

	/**
	 * The default value of the '{@link #getCoordinator() <em>Coordinator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCoordinator()
	 * @generated
	 * @ordered
	 */
	protected static final String COORDINATOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCoordinator() <em>Coordinator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCoordinator()
	 * @generated
	 * @ordered
	 */
	protected String coordinator = COORDINATOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getHead() <em>Head</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHead()
	 * @generated
	 * @ordered
	 */
	protected static final String HEAD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHead() <em>Head</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHead()
	 * @generated
	 * @ordered
	 */
	protected String head = HEAD_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GroupImpl()
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
		return GpmnPackage.Literals.GROUP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getMembers()
	{
		if (members == null)
		{
			members = new EDataTypeEList<String>(String.class, this, GpmnPackage.GROUP__MEMBERS);
		}
		return members;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCoordinator()
	{
		return coordinator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCoordinator(String newCoordinator)
	{
		String oldCoordinator = coordinator;
		coordinator = newCoordinator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GROUP__COORDINATOR, oldCoordinator, coordinator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getHead()
	{
		return head;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHead(String newHead)
	{
		String oldHead = head;
		head = newHead;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GROUP__HEAD, oldHead, head));
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
			case GpmnPackage.GROUP__MEMBERS:
				return getMembers();
			case GpmnPackage.GROUP__COORDINATOR:
				return getCoordinator();
			case GpmnPackage.GROUP__HEAD:
				return getHead();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case GpmnPackage.GROUP__MEMBERS:
				getMembers().clear();
				getMembers().addAll((Collection<? extends String>)newValue);
				return;
			case GpmnPackage.GROUP__COORDINATOR:
				setCoordinator((String)newValue);
				return;
			case GpmnPackage.GROUP__HEAD:
				setHead((String)newValue);
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
			case GpmnPackage.GROUP__MEMBERS:
				getMembers().clear();
				return;
			case GpmnPackage.GROUP__COORDINATOR:
				setCoordinator(COORDINATOR_EDEFAULT);
				return;
			case GpmnPackage.GROUP__HEAD:
				setHead(HEAD_EDEFAULT);
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
			case GpmnPackage.GROUP__MEMBERS:
				return members != null && !members.isEmpty();
			case GpmnPackage.GROUP__COORDINATOR:
				return COORDINATOR_EDEFAULT == null ? coordinator != null : !COORDINATOR_EDEFAULT.equals(coordinator);
			case GpmnPackage.GROUP__HEAD:
				return HEAD_EDEFAULT == null ? head != null : !HEAD_EDEFAULT.equals(head);
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
		result.append(" (members: ");
		result.append(members);
		result.append(", coordinator: ");
		result.append(coordinator);
		result.append(", head: ");
		result.append(head);
		result.append(')');
		return result.toString();
	}

} //GroupImpl
