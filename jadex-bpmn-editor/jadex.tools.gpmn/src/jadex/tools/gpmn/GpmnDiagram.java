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
 * A representation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getProcesses <em>Processes</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getMessages <em>Messages</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getImports <em>Imports</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getPackage <em>Package</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram()
 * @model extendedMetaData="name='GpmnDiagram' kind='elementOnly'"
 * @generated
 */
public interface GpmnDiagram extends Graph, Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Processes</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Process}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Process#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Processes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Processes</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Processes()
	 * @see jadex.tools.gpmn.Process#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true"
	 *        extendedMetaData="kind='element' name='processes'"
	 * @generated
	 */
	EList<jadex.tools.gpmn.Process> getProcesses();

	/**
	 * Returns the value of the '<em><b>Messages</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.MessagingEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.MessagingEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Messages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Messages</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Messages()
	 * @see jadex.tools.gpmn.MessagingEdge#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true"
	 *        extendedMetaData="kind='element' name='messages'"
	 * @generated
	 */
	EList<MessagingEdge> getMessages();

	/**
	 * Returns the value of the '<em><b>Imports</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Imports</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Imports</em>' attribute list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Imports()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='imports'"
	 * @generated
	 */
	EList<String> getImports();

	/**
	 * Returns the value of the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Package</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package</em>' attribute.
	 * @see #setPackage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Package()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='package'"
	 * @generated
	 */
	String getPackage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getPackage <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package</em>' attribute.
	 * @see #getPackage()
	 * @generated
	 */
	void setPackage(String value);

} // GpmnDiagram
