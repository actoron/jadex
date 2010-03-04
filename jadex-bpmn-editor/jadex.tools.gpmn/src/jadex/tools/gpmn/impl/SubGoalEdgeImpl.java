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
import jadex.tools.gpmn.SubGoalEdge;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sub Goal Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.SubGoalEdgeImpl#getSequentialOrder <em>Sequential Order</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SubGoalEdgeImpl extends ParameterizedEdgeImpl implements
		SubGoalEdge
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getSequentialOrder() <em>Sequential Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequentialOrder()
	 * @generated
	 * @ordered
	 */
	protected static final int SEQUENTIAL_ORDER_EDEFAULT = 0;
	/**
	 * The cached value of the '{@link #getSequentialOrder() <em>Sequential Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequentialOrder()
	 * @generated
	 * @ordered
	 */
	protected int sequentialOrder = SEQUENTIAL_ORDER_EDEFAULT;
	/**
	 * This is true if the Sequential Order attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean sequentialOrderESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SubGoalEdgeImpl()
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
		return GpmnPackage.Literals.SUB_GOAL_EDGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getSequentialOrder()
	{
		return sequentialOrder;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSequentialOrder(int newSequentialOrder)
	{
		int oldSequentialOrder = sequentialOrder;
		sequentialOrder = newSequentialOrder;
		boolean oldSequentialOrderESet = sequentialOrderESet;
		sequentialOrderESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.SUB_GOAL_EDGE__SEQUENTIAL_ORDER, oldSequentialOrder, sequentialOrder, !oldSequentialOrderESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSequentialOrder()
	{
		int oldSequentialOrder = sequentialOrder;
		boolean oldSequentialOrderESet = sequentialOrderESet;
		sequentialOrder = SEQUENTIAL_ORDER_EDEFAULT;
		sequentialOrderESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.SUB_GOAL_EDGE__SEQUENTIAL_ORDER, oldSequentialOrder, SEQUENTIAL_ORDER_EDEFAULT, oldSequentialOrderESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSequentialOrder()
	{
		return sequentialOrderESet;
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
			case GpmnPackage.SUB_GOAL_EDGE__SEQUENTIAL_ORDER:
				return getSequentialOrder();
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
			case GpmnPackage.SUB_GOAL_EDGE__SEQUENTIAL_ORDER:
				setSequentialOrder((Integer)newValue);
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
			case GpmnPackage.SUB_GOAL_EDGE__SEQUENTIAL_ORDER:
				unsetSequentialOrder();
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
			case GpmnPackage.SUB_GOAL_EDGE__SEQUENTIAL_ORDER:
				return isSetSequentialOrder();
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
		result.append(" (sequentialOrder: ");
		if (sequentialOrderESet) result.append(sequentialOrder); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //SubGoalEdgeImpl
