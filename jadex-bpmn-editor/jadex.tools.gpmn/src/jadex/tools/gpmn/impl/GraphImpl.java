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

import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.AssociationTarget;
import jadex.tools.gpmn.Edge;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Graph;
import jadex.tools.gpmn.Vertex;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Graph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.GraphImpl#getAssociations <em>Associations</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GraphImpl#getVertices <em>Vertices</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GraphImpl#getSequenceEdges <em>Sequence Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GraphImpl extends ArtifactsContainerImpl implements Graph
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The cached value of the '{@link #getAssociations() <em>Associations</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssociations()
	 * @generated
	 * @ordered
	 */
	protected EList<Association> associations;

	/**
	 * The cached value of the '{@link #getVertices() <em>Vertices</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVertices()
	 * @generated
	 * @ordered
	 */
	protected EList<Vertex> vertices;

	/**
	 * The cached value of the '{@link #getSequenceEdges() <em>Sequence Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSequenceEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<Edge> sequenceEdges;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GraphImpl()
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
		return GpmnPackage.Literals.GRAPH;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Association> getAssociations()
	{
		if (associations == null)
		{
			associations = new EObjectWithInverseEList<Association>(Association.class, this, GpmnPackage.GRAPH__ASSOCIATIONS, GpmnPackage.ASSOCIATION__TARGET);
		}
		return associations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Vertex> getVertices()
	{
		if (vertices == null)
		{
			vertices = new EObjectContainmentWithInverseEList<Vertex>(Vertex.class, this, GpmnPackage.GRAPH__VERTICES, GpmnPackage.VERTEX__GRAPH);
		}
		return vertices;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Edge> getSequenceEdges()
	{
		if (sequenceEdges == null)
		{
			sequenceEdges = new EObjectContainmentWithInverseEList<Edge>(Edge.class, this, GpmnPackage.GRAPH__SEQUENCE_EDGES, GpmnPackage.EDGE__GRAPH);
		}
		return sequenceEdges;
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
			case GpmnPackage.GRAPH__ASSOCIATIONS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getAssociations()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GRAPH__VERTICES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getVertices()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getSequenceEdges()).basicAdd(otherEnd, msgs);
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
			case GpmnPackage.GRAPH__ASSOCIATIONS:
				return ((InternalEList<?>)getAssociations()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GRAPH__VERTICES:
				return ((InternalEList<?>)getVertices()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				return ((InternalEList<?>)getSequenceEdges()).basicRemove(otherEnd, msgs);
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
			case GpmnPackage.GRAPH__ASSOCIATIONS:
				return getAssociations();
			case GpmnPackage.GRAPH__VERTICES:
				return getVertices();
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				return getSequenceEdges();
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
			case GpmnPackage.GRAPH__ASSOCIATIONS:
				getAssociations().clear();
				getAssociations().addAll((Collection<? extends Association>)newValue);
				return;
			case GpmnPackage.GRAPH__VERTICES:
				getVertices().clear();
				getVertices().addAll((Collection<? extends Vertex>)newValue);
				return;
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				getSequenceEdges().clear();
				getSequenceEdges().addAll((Collection<? extends Edge>)newValue);
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
			case GpmnPackage.GRAPH__ASSOCIATIONS:
				getAssociations().clear();
				return;
			case GpmnPackage.GRAPH__VERTICES:
				getVertices().clear();
				return;
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				getSequenceEdges().clear();
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
			case GpmnPackage.GRAPH__ASSOCIATIONS:
				return associations != null && !associations.isEmpty();
			case GpmnPackage.GRAPH__VERTICES:
				return vertices != null && !vertices.isEmpty();
			case GpmnPackage.GRAPH__SEQUENCE_EDGES:
				return sequenceEdges != null && !sequenceEdges.isEmpty();
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
		if (baseClass == AssociationTarget.class)
		{
			switch (derivedFeatureID)
			{
				case GpmnPackage.GRAPH__ASSOCIATIONS: return GpmnPackage.ASSOCIATION_TARGET__ASSOCIATIONS;
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
		if (baseClass == AssociationTarget.class)
		{
			switch (baseFeatureID)
			{
				case GpmnPackage.ASSOCIATION_TARGET__ASSOCIATIONS: return GpmnPackage.GRAPH__ASSOCIATIONS;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

} //GraphImpl
