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
 * A representation of the model object '<em><b>Graph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Graph#getVertices <em>Vertices</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Graph#getSequenceEdges <em>Sequence Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getGraph()
 * @model extendedMetaData="name='Graph' kind='elementOnly'"
 * @generated
 */
public interface Graph extends ArtifactsContainer, AssociationTarget
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Vertices</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Vertex}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Vertex#getGraph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vertices</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vertices</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGraph_Vertices()
	 * @see jadex.tools.gpmn.Vertex#getGraph
	 * @model opposite="graph" containment="true"
	 *        extendedMetaData="kind='element' name='vertices'"
	 * @generated
	 */
	EList<Vertex> getVertices();

	/**
	 * Returns the value of the '<em><b>Sequence Edges</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Edge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Edge#getGraph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sequence Edges</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sequence Edges</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGraph_SequenceEdges()
	 * @see jadex.tools.gpmn.Edge#getGraph
	 * @model opposite="graph" containment="true"
	 *        extendedMetaData="kind='element' name='sequenceEdges'"
	 * @generated
	 */
	EList<Edge> getSequenceEdges();

} // Graph
