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
import jadex.tools.gpmn.NamedObject;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inter Graph Vertex</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphVertexImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphVertexImpl#getName <em>Name</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphVertexImpl#getNcname <em>Ncname</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphVertexImpl#getInterGraphMessages <em>Inter Graph Messages</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphVertexImpl#getIncomingInterGraphEdges <em>Incoming Inter Graph Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphVertexImpl#getOutgoingInterGraphEdges <em>Outgoing Inter Graph Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InterGraphVertexImpl extends AssociationTargetImpl implements
		InterGraphVertex
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getNcname() <em>Ncname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNcname()
	 * @generated
	 * @ordered
	 */
	protected static final String NCNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNcname() <em>Ncname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNcname()
	 * @generated
	 * @ordered
	 */
	protected String ncname = NCNAME_EDEFAULT;

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
	protected InterGraphVertexImpl()
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
		return GpmnPackage.Literals.INTER_GRAPH_VERTEX;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription)
	{
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName)
	{
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_VERTEX__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNcname()
	{
		return ncname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNcname(String newNcname)
	{
		String oldNcname = ncname;
		ncname = newNcname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_VERTEX__NCNAME, oldNcname, ncname));
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
			interGraphMessages = new BasicFeatureMap(this, GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES);
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
			case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingInterGraphEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES:
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
			case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES:
				return ((InternalEList<?>)getInterGraphMessages()).basicRemove(otherEnd, msgs);
			case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES:
				return ((InternalEList<?>)getIncomingInterGraphEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES:
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
			case GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION:
				return getDescription();
			case GpmnPackage.INTER_GRAPH_VERTEX__NAME:
				return getName();
			case GpmnPackage.INTER_GRAPH_VERTEX__NCNAME:
				return getNcname();
			case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES:
				if (coreType) return getInterGraphMessages();
				return ((FeatureMap.Internal)getInterGraphMessages()).getWrapper();
			case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES:
				return getIncomingInterGraphEdges();
			case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES:
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
			case GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__NAME:
				setName((String)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__NCNAME:
				setNcname((String)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES:
				((FeatureMap.Internal)getInterGraphMessages()).set(newValue);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES:
				getIncomingInterGraphEdges().clear();
				getIncomingInterGraphEdges().addAll((Collection<? extends InterGraphEdge>)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES:
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
			case GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__NCNAME:
				setNcname(NCNAME_EDEFAULT);
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES:
				getInterGraphMessages().clear();
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES:
				getIncomingInterGraphEdges().clear();
				return;
			case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES:
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
			case GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case GpmnPackage.INTER_GRAPH_VERTEX__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GpmnPackage.INTER_GRAPH_VERTEX__NCNAME:
				return NCNAME_EDEFAULT == null ? ncname != null : !NCNAME_EDEFAULT.equals(ncname);
			case GpmnPackage.INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES:
				return interGraphMessages != null && !interGraphMessages.isEmpty();
			case GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES:
				return !getIncomingInterGraphEdges().isEmpty();
			case GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES:
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
		if (baseClass == NamedObject.class)
		{
			switch (derivedFeatureID)
			{
				case GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION: return GpmnPackage.NAMED_OBJECT__DESCRIPTION;
				case GpmnPackage.INTER_GRAPH_VERTEX__NAME: return GpmnPackage.NAMED_OBJECT__NAME;
				case GpmnPackage.INTER_GRAPH_VERTEX__NCNAME: return GpmnPackage.NAMED_OBJECT__NCNAME;
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
		if (baseClass == NamedObject.class)
		{
			switch (baseFeatureID)
			{
				case GpmnPackage.NAMED_OBJECT__DESCRIPTION: return GpmnPackage.INTER_GRAPH_VERTEX__DESCRIPTION;
				case GpmnPackage.NAMED_OBJECT__NAME: return GpmnPackage.INTER_GRAPH_VERTEX__NAME;
				case GpmnPackage.NAMED_OBJECT__NCNAME: return GpmnPackage.INTER_GRAPH_VERTEX__NCNAME;
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
		result.append(" (description: ");
		result.append(description);
		result.append(", name: ");
		result.append(name);
		result.append(", ncname: ");
		result.append(ncname);
		result.append(", interGraphMessages: ");
		result.append(interGraphMessages);
		result.append(')');
		return result.toString();
	}

} //InterGraphVertexImpl
