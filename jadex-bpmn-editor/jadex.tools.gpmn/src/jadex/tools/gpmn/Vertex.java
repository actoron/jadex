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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Vertex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Vertex#getOutgoingEdges <em>Outgoing Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Vertex#getIncomingEdges <em>Incoming Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Vertex#getGraph <em>Graph</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getVertex()
 * @model extendedMetaData="name='Vertex' kind='elementOnly'"
 * @generated
 */
public interface Vertex extends AssociationTarget, NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Outgoing Edges</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Edge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Edge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outgoing Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outgoing Edges</em>' reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getVertex_OutgoingEdges()
	 * @see jadex.tools.gpmn.Edge#getSource
	 * @model opposite="source" resolveProxies="false"
	 *        extendedMetaData="kind='element' name='outgoingEdges'"
	 * @generated
	 */
	EList<Edge> getOutgoingEdges();

	/**
	 * Returns the value of the '<em><b>Incoming Edges</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Edge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Edge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Incoming Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Edges</em>' reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getVertex_IncomingEdges()
	 * @see jadex.tools.gpmn.Edge#getTarget
	 * @model opposite="target" resolveProxies="false"
	 *        extendedMetaData="kind='element' name='incomingEdges'"
	 * @generated
	 */
	EList<Edge> getIncomingEdges();

	/**
	 * Returns the value of the '<em><b>Graph</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Graph#getVertices <em>Vertices</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Graph</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Graph</em>' container reference.
	 * @see #setGraph(Graph)
	 * @see jadex.tools.gpmn.GpmnPackage#getVertex_Graph()
	 * @see jadex.tools.gpmn.Graph#getVertices
	 * @model opposite="vertices"
	 * @generated
	 */
	Graph getGraph();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Vertex#getGraph <em>Graph</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Graph</em>' container reference.
	 * @see #getGraph()
	 * @generated
	 */
	void setGraph(Graph value);

} // Vertex
