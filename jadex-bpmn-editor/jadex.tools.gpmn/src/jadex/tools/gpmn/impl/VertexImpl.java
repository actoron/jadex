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

import jadex.tools.gpmn.Edge;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Graph;
import jadex.tools.gpmn.NamedObject;
import jadex.tools.gpmn.Vertex;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Vertex</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.VertexImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.VertexImpl#getName <em>Name</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.VertexImpl#getNcname <em>Ncname</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.VertexImpl#getOutgoingEdges <em>Outgoing Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.VertexImpl#getIncomingEdges <em>Incoming Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.VertexImpl#getGraph <em>Graph</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VertexImpl extends AssociationTargetImpl implements Vertex
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
	 * The cached value of the '{@link #getOutgoingEdges() <em>Outgoing Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutgoingEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<Edge> outgoingEdges;

	/**
	 * The cached value of the '{@link #getIncomingEdges() <em>Incoming Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIncomingEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<Edge> incomingEdges;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected VertexImpl()
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
		return GpmnPackage.Literals.VERTEX;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.VERTEX__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.VERTEX__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.VERTEX__NCNAME, oldNcname, ncname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Edge> getOutgoingEdges()
	{
		if (outgoingEdges == null)
		{
			outgoingEdges = new EObjectWithInverseEList<Edge>(Edge.class, this, GpmnPackage.VERTEX__OUTGOING_EDGES, GpmnPackage.EDGE__SOURCE);
		}
		return outgoingEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Edge> getIncomingEdges()
	{
		if (incomingEdges == null)
		{
			incomingEdges = new EObjectWithInverseEList<Edge>(Edge.class, this, GpmnPackage.VERTEX__INCOMING_EDGES, GpmnPackage.EDGE__TARGET);
		}
		return incomingEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Graph getGraph()
	{
		if (eContainerFeatureID() != GpmnPackage.VERTEX__GRAPH) return null;
		return (Graph)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGraph(Graph newGraph,
			NotificationChain msgs)
	{
		msgs = eBasicSetContainer((InternalEObject)newGraph, GpmnPackage.VERTEX__GRAPH, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGraph(Graph newGraph)
	{
		if (newGraph != eInternalContainer() || (eContainerFeatureID() != GpmnPackage.VERTEX__GRAPH && newGraph != null))
		{
			if (EcoreUtil.isAncestor(this, newGraph))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newGraph != null)
				msgs = ((InternalEObject)newGraph).eInverseAdd(this, GpmnPackage.GRAPH__VERTICES, Graph.class, msgs);
			msgs = basicSetGraph(newGraph, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.VERTEX__GRAPH, newGraph, newGraph));
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
			case GpmnPackage.VERTEX__OUTGOING_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.VERTEX__INCOMING_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.VERTEX__GRAPH:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetGraph((Graph)otherEnd, msgs);
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
			case GpmnPackage.VERTEX__OUTGOING_EDGES:
				return ((InternalEList<?>)getOutgoingEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.VERTEX__INCOMING_EDGES:
				return ((InternalEList<?>)getIncomingEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.VERTEX__GRAPH:
				return basicSetGraph(null, msgs);
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
			case GpmnPackage.VERTEX__GRAPH:
				return eInternalContainer().eInverseRemove(this, GpmnPackage.GRAPH__VERTICES, Graph.class, msgs);
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
			case GpmnPackage.VERTEX__DESCRIPTION:
				return getDescription();
			case GpmnPackage.VERTEX__NAME:
				return getName();
			case GpmnPackage.VERTEX__NCNAME:
				return getNcname();
			case GpmnPackage.VERTEX__OUTGOING_EDGES:
				return getOutgoingEdges();
			case GpmnPackage.VERTEX__INCOMING_EDGES:
				return getIncomingEdges();
			case GpmnPackage.VERTEX__GRAPH:
				return getGraph();
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
			case GpmnPackage.VERTEX__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case GpmnPackage.VERTEX__NAME:
				setName((String)newValue);
				return;
			case GpmnPackage.VERTEX__NCNAME:
				setNcname((String)newValue);
				return;
			case GpmnPackage.VERTEX__OUTGOING_EDGES:
				getOutgoingEdges().clear();
				getOutgoingEdges().addAll((Collection<? extends Edge>)newValue);
				return;
			case GpmnPackage.VERTEX__INCOMING_EDGES:
				getIncomingEdges().clear();
				getIncomingEdges().addAll((Collection<? extends Edge>)newValue);
				return;
			case GpmnPackage.VERTEX__GRAPH:
				setGraph((Graph)newValue);
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
			case GpmnPackage.VERTEX__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case GpmnPackage.VERTEX__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GpmnPackage.VERTEX__NCNAME:
				setNcname(NCNAME_EDEFAULT);
				return;
			case GpmnPackage.VERTEX__OUTGOING_EDGES:
				getOutgoingEdges().clear();
				return;
			case GpmnPackage.VERTEX__INCOMING_EDGES:
				getIncomingEdges().clear();
				return;
			case GpmnPackage.VERTEX__GRAPH:
				setGraph((Graph)null);
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
			case GpmnPackage.VERTEX__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case GpmnPackage.VERTEX__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GpmnPackage.VERTEX__NCNAME:
				return NCNAME_EDEFAULT == null ? ncname != null : !NCNAME_EDEFAULT.equals(ncname);
			case GpmnPackage.VERTEX__OUTGOING_EDGES:
				return outgoingEdges != null && !outgoingEdges.isEmpty();
			case GpmnPackage.VERTEX__INCOMING_EDGES:
				return incomingEdges != null && !incomingEdges.isEmpty();
			case GpmnPackage.VERTEX__GRAPH:
				return getGraph() != null;
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
				case GpmnPackage.VERTEX__DESCRIPTION: return GpmnPackage.NAMED_OBJECT__DESCRIPTION;
				case GpmnPackage.VERTEX__NAME: return GpmnPackage.NAMED_OBJECT__NAME;
				case GpmnPackage.VERTEX__NCNAME: return GpmnPackage.NAMED_OBJECT__NCNAME;
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
				case GpmnPackage.NAMED_OBJECT__DESCRIPTION: return GpmnPackage.VERTEX__DESCRIPTION;
				case GpmnPackage.NAMED_OBJECT__NAME: return GpmnPackage.VERTEX__NAME;
				case GpmnPackage.NAMED_OBJECT__NCNAME: return GpmnPackage.VERTEX__NCNAME;
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
		result.append(')');
		return result.toString();
	}

} //VertexImpl
