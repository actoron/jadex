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
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Edge#isIsDefault <em>Is Default</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Edge#getSource <em>Source</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Edge#getTarget <em>Target</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Edge#getGraph <em>Graph</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getEdge()
 * @model extendedMetaData="name='Edge' kind='elementOnly'"
 * @generated
 */
public interface Edge extends AssociationTarget, NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Is Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Default</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Default</em>' attribute.
	 * @see #isSetIsDefault()
	 * @see #unsetIsDefault()
	 * @see #setIsDefault(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getEdge_IsDefault()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='isDefault'"
	 * @generated
	 */
	boolean isIsDefault();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Edge#isIsDefault <em>Is Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Default</em>' attribute.
	 * @see #isSetIsDefault()
	 * @see #unsetIsDefault()
	 * @see #isIsDefault()
	 * @generated
	 */
	void setIsDefault(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Edge#isIsDefault <em>Is Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetIsDefault()
	 * @see #isIsDefault()
	 * @see #setIsDefault(boolean)
	 * @generated
	 */
	void unsetIsDefault();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Edge#isIsDefault <em>Is Default</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Is Default</em>' attribute is set.
	 * @see #unsetIsDefault()
	 * @see #isIsDefault()
	 * @see #setIsDefault(boolean)
	 * @generated
	 */
	boolean isSetIsDefault();

	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Vertex#getOutgoingEdges <em>Outgoing Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(Vertex)
	 * @see jadex.tools.gpmn.GpmnPackage#getEdge_Source()
	 * @see jadex.tools.gpmn.Vertex#getOutgoingEdges
	 * @model opposite="outgoingEdges" resolveProxies="false" transient="true"
	 *        extendedMetaData="kind='attribute' name='source'"
	 * @generated
	 */
	Vertex getSource();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Edge#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Vertex value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Vertex#getIncomingEdges <em>Incoming Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(Vertex)
	 * @see jadex.tools.gpmn.GpmnPackage#getEdge_Target()
	 * @see jadex.tools.gpmn.Vertex#getIncomingEdges
	 * @model opposite="incomingEdges" resolveProxies="false" transient="true"
	 *        extendedMetaData="kind='attribute' name='target'"
	 * @generated
	 */
	Vertex getTarget();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Edge#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Vertex value);

	/**
	 * Returns the value of the '<em><b>Graph</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Graph#getSequenceEdges <em>Sequence Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Graph</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Graph</em>' container reference.
	 * @see #setGraph(Graph)
	 * @see jadex.tools.gpmn.GpmnPackage#getEdge_Graph()
	 * @see jadex.tools.gpmn.Graph#getSequenceEdges
	 * @model opposite="sequenceEdges"
	 * @generated
	 */
	Graph getGraph();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Edge#getGraph <em>Graph</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Graph</em>' container reference.
	 * @see #getGraph()
	 * @generated
	 */
	void setGraph(Graph value);

} // Edge
