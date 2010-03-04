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
import jadex.tools.gpmn.InterGraphEdge;
import jadex.tools.gpmn.InterGraphVertex;
import jadex.tools.gpmn.MessageGoal;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Message Goal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.MessageGoalImpl#getInterGraphMessages <em>Inter Graph Messages</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.MessageGoalImpl#getIncomingInterGraphEdges <em>Incoming Inter Graph Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.MessageGoalImpl#getOutgoingInterGraphEdges <em>Outgoing Inter Graph Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MessageGoalImpl extends GoalImpl implements MessageGoal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";
	/**
	 * The cached value of the '{@link #getInterGraphMessages() <em>Inter Graph Messages</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterGraphMessages()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap interGraphMessages;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MessageGoalImpl()
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
		return GpmnPackage.Literals.MESSAGE_GOAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getInterGraphMessages()
	{
		if (interGraphMessages == null)
		{
			interGraphMessages = new BasicFeatureMap(this, GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES);
		}
		return interGraphMessages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InterGraphEdge> getIncomingInterGraphEdges()
	{
		return getInterGraphMessages().list(GpmnPackage.Literals.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InterGraphEdge> getOutgoingInterGraphEdges()
	{
		return getInterGraphMessages().list(GpmnPackage.Literals.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingInterGraphEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingInterGraphEdges()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES:
				return ((InternalEList<?>)getInterGraphMessages()).basicRemove(otherEnd, msgs);
			case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES:
				return ((InternalEList<?>)getIncomingInterGraphEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES:
				return ((InternalEList<?>)getOutgoingInterGraphEdges()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
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
			case GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES:
				if (coreType) return getInterGraphMessages();
				return ((FeatureMap.Internal)getInterGraphMessages()).getWrapper();
			case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES:
				return getIncomingInterGraphEdges();
			case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES:
				return getOutgoingInterGraphEdges();
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
			case GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES:
				((FeatureMap.Internal)getInterGraphMessages()).set(newValue);
				return;
			case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES:
				getIncomingInterGraphEdges().clear();
				getIncomingInterGraphEdges().addAll((Collection<? extends InterGraphEdge>)newValue);
				return;
			case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES:
				getOutgoingInterGraphEdges().clear();
				getOutgoingInterGraphEdges().addAll((Collection<? extends InterGraphEdge>)newValue);
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
			case GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES:
				getInterGraphMessages().clear();
				return;
			case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES:
				getIncomingInterGraphEdges().clear();
				return;
			case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES:
				getOutgoingInterGraphEdges().clear();
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
			case GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES:
				return interGraphMessages != null && !interGraphMessages.isEmpty();
			case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES:
				return !getIncomingInterGraphEdges().isEmpty();
			case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES:
				return !getOutgoingInterGraphEdges().isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
	{
		if (baseClass == InterGraphVertex.class)
		{
			switch (derivedFeatureID)
			{
				case GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES: return GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES;
				case GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES: return GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES;
				case GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES: return GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
	{
		if (baseClass == InterGraphVertex.class)
		{
			switch (baseFeatureID)
			{
				case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES: return GpmnPackage.MESSAGE_GOAL__INTER_GRAPH_MESSAGES;
				case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES: return GpmnPackage.MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES;
				case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES: return GpmnPackage.MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		result.append(" (interGraphMessages: ");
		result.append(interGraphMessages);
		result.append(')');
		return result.toString();
	}

} //MessageGoalImpl
