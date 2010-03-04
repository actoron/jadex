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
package jadex.tools.gpmn;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Inter Graph Vertex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.InterGraphVertex#getInterGraphMessages <em>Inter Graph Messages</em>}</li>
 *   <li>{@link jadex.tools.gpmn.InterGraphVertex#getIncomingInterGraphEdges <em>Incoming Inter Graph Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.InterGraphVertex#getOutgoingInterGraphEdges <em>Outgoing Inter Graph Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphVertex()
 * @model extendedMetaData="name='InterGraphVertex' kind='elementOnly'"
 * @generated
 */
public interface InterGraphVertex extends AssociationTarget, NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Inter Graph Messages</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Inter Graph Messages</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inter Graph Messages</em>' attribute list.
	 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphVertex_InterGraphMessages()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='interGraphMessages:6'"
	 * @generated
	 */
	FeatureMap getInterGraphMessages();

	/**
	 * Returns the value of the '<em><b>Incoming Inter Graph Edges</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.InterGraphEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.InterGraphEdge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Incoming Inter Graph Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Inter Graph Edges</em>' reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphVertex_IncomingInterGraphEdges()
	 * @see jadex.tools.gpmn.InterGraphEdge#getSource
	 * @model opposite="source" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='incomingInterGraphEdges' group='#interGraphMessages:6'"
	 * @generated
	 */
	EList<InterGraphEdge> getIncomingInterGraphEdges();

	/**
	 * Returns the value of the '<em><b>Outgoing Inter Graph Edges</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.InterGraphEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.InterGraphEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outgoing Inter Graph Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outgoing Inter Graph Edges</em>' reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphVertex_OutgoingInterGraphEdges()
	 * @see jadex.tools.gpmn.InterGraphEdge#getTarget
	 * @model opposite="target" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='outgoingInterGraphEdges' group='#interGraphMessages:6'"
	 * @generated
	 */
	EList<InterGraphEdge> getOutgoingInterGraphEdges();

} // InterGraphVertex
