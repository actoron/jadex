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

import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.InterGraphEdge;
import jadex.tools.gpmn.InterGraphVertex;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Process</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.ProcessImpl#getInterGraphMessages <em>Inter Graph Messages</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.ProcessImpl#getIncomingInterGraphEdges <em>Incoming Inter Graph Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.ProcessImpl#getOutgoingInterGraphEdges <em>Outgoing Inter Graph Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.ProcessImpl#isLooping <em>Looping</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.ProcessImpl#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProcessImpl extends GraphImpl implements jadex.tools.gpmn.Process
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
	 * The default value of the '{@link #isLooping() <em>Looping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLooping()
	 * @generated
	 * @ordered
	 */
	protected static final boolean LOOPING_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isLooping() <em>Looping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLooping()
	 * @generated
	 * @ordered
	 */
	protected boolean looping = LOOPING_EDEFAULT;

	/**
	 * This is true if the Looping attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean loopingESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProcessImpl()
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
		return GpmnPackage.Literals.PROCESS;
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
			interGraphMessages = new BasicFeatureMap(this, GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES);
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
	public boolean isLooping()
	{
		return looping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLooping(boolean newLooping)
	{
		boolean oldLooping = looping;
		looping = newLooping;
		boolean oldLoopingESet = loopingESet;
		loopingESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.PROCESS__LOOPING, oldLooping, looping, !oldLoopingESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLooping()
	{
		boolean oldLooping = looping;
		boolean oldLoopingESet = loopingESet;
		looping = LOOPING_EDEFAULT;
		loopingESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.PROCESS__LOOPING, oldLooping, LOOPING_EDEFAULT, oldLoopingESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLooping()
	{
		return loopingESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnDiagram getGpmnDiagram()
	{
		if (eContainerFeatureID() != GpmnPackage.PROCESS__GPMN_DIAGRAM) return null;
		return (GpmnDiagram)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGpmnDiagram(GpmnDiagram newGpmnDiagram,
			NotificationChain msgs)
	{
		msgs = eBasicSetContainer((InternalEObject)newGpmnDiagram, GpmnPackage.PROCESS__GPMN_DIAGRAM, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGpmnDiagram(GpmnDiagram newGpmnDiagram)
	{
		if (newGpmnDiagram != eInternalContainer() || (eContainerFeatureID() != GpmnPackage.PROCESS__GPMN_DIAGRAM && newGpmnDiagram != null))
		{
			if (EcoreUtil.isAncestor(this, newGpmnDiagram))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newGpmnDiagram != null)
				msgs = ((InternalEObject)newGpmnDiagram).eInverseAdd(this, GpmnPackage.GPMN_DIAGRAM__PROCESSES, GpmnDiagram.class, msgs);
			msgs = basicSetGpmnDiagram(newGpmnDiagram, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.PROCESS__GPMN_DIAGRAM, newGpmnDiagram, newGpmnDiagram));
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
			case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingInterGraphEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingInterGraphEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetGpmnDiagram((GpmnDiagram)otherEnd, msgs);
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
			case GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES:
				return ((InternalEList<?>)getInterGraphMessages()).basicRemove(otherEnd, msgs);
			case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES:
				return ((InternalEList<?>)getIncomingInterGraphEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES:
				return ((InternalEList<?>)getOutgoingInterGraphEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				return basicSetGpmnDiagram(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(
			NotificationChain msgs)
	{
		switch (eContainerFeatureID())
		{
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				return eInternalContainer().eInverseRemove(this, GpmnPackage.GPMN_DIAGRAM__PROCESSES, GpmnDiagram.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
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
			case GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES:
				if (coreType) return getInterGraphMessages();
				return ((FeatureMap.Internal)getInterGraphMessages()).getWrapper();
			case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES:
				return getIncomingInterGraphEdges();
			case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES:
				return getOutgoingInterGraphEdges();
			case GpmnPackage.PROCESS__LOOPING:
				return isLooping();
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				return getGpmnDiagram();
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
			case GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES:
				((FeatureMap.Internal)getInterGraphMessages()).set(newValue);
				return;
			case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES:
				getIncomingInterGraphEdges().clear();
				getIncomingInterGraphEdges().addAll((Collection<? extends InterGraphEdge>)newValue);
				return;
			case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES:
				getOutgoingInterGraphEdges().clear();
				getOutgoingInterGraphEdges().addAll((Collection<? extends InterGraphEdge>)newValue);
				return;
			case GpmnPackage.PROCESS__LOOPING:
				setLooping((Boolean)newValue);
				return;
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				setGpmnDiagram((GpmnDiagram)newValue);
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
			case GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES:
				getInterGraphMessages().clear();
				return;
			case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES:
				getIncomingInterGraphEdges().clear();
				return;
			case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES:
				getOutgoingInterGraphEdges().clear();
				return;
			case GpmnPackage.PROCESS__LOOPING:
				unsetLooping();
				return;
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				setGpmnDiagram((GpmnDiagram)null);
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
			case GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES:
				return interGraphMessages != null && !interGraphMessages.isEmpty();
			case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES:
				return !getIncomingInterGraphEdges().isEmpty();
			case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES:
				return !getOutgoingInterGraphEdges().isEmpty();
			case GpmnPackage.PROCESS__LOOPING:
				return isSetLooping();
			case GpmnPackage.PROCESS__GPMN_DIAGRAM:
				return getGpmnDiagram() != null;
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
				case GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES: return GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES;
				case GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES: return GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES;
				case GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES: return GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES;
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
				case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES: return GpmnPackage.PROCESS__INTER_GRAPH_MESSAGES;
				case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES: return GpmnPackage.PROCESS__INCOMING_INTER_GRAPH_EDGES;
				case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES: return GpmnPackage.PROCESS__OUTGOING_INTER_GRAPH_EDGES;
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
		result.append(", looping: ");
		if (loopingESet) result.append(looping); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //ProcessImpl
