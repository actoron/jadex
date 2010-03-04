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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Inter Graph Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.InterGraphEdge#getSource <em>Source</em>}</li>
 *   <li>{@link jadex.tools.gpmn.InterGraphEdge#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphEdge()
 * @model extendedMetaData="name='InterGraphEdge' kind='elementOnly'"
 * @generated
 */
public interface InterGraphEdge extends AssociationTarget, NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.InterGraphVertex#getIncomingInterGraphEdges <em>Incoming Inter Graph Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(InterGraphVertex)
	 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphEdge_Source()
	 * @see jadex.tools.gpmn.InterGraphVertex#getIncomingInterGraphEdges
	 * @model opposite="incomingInterGraphEdges"
	 *        extendedMetaData="kind='attribute' name='source'"
	 * @generated
	 */
	InterGraphVertex getSource();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.InterGraphEdge#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(InterGraphVertex value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.InterGraphVertex#getOutgoingInterGraphEdges <em>Outgoing Inter Graph Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(InterGraphVertex)
	 * @see jadex.tools.gpmn.GpmnPackage#getInterGraphEdge_Target()
	 * @see jadex.tools.gpmn.InterGraphVertex#getOutgoingInterGraphEdges
	 * @model opposite="outgoingInterGraphEdges"
	 *        extendedMetaData="kind='attribute' name='target'"
	 * @generated
	 */
	InterGraphVertex getTarget();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.InterGraphEdge#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(InterGraphVertex value);

} // InterGraphEdge
