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
 * A representation of the model object '<em><b>Process</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Process#isLooping <em>Looping</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Process#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getProcess()
 * @model extendedMetaData="name='Process' kind='elementOnly'"
 * @generated
 */
public interface Process extends Graph, InterGraphVertex
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Looping</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Looping</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Looping</em>' attribute.
	 * @see #isSetLooping()
	 * @see #unsetLooping()
	 * @see #setLooping(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getProcess_Looping()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='looping'"
	 * @generated
	 */
	boolean isLooping();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Process#isLooping <em>Looping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Looping</em>' attribute.
	 * @see #isSetLooping()
	 * @see #unsetLooping()
	 * @see #isLooping()
	 * @generated
	 */
	void setLooping(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Process#isLooping <em>Looping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetLooping()
	 * @see #isLooping()
	 * @see #setLooping(boolean)
	 * @generated
	 */
	void unsetLooping();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Process#isLooping <em>Looping</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Looping</em>' attribute is set.
	 * @see #unsetLooping()
	 * @see #isLooping()
	 * @see #setLooping(boolean)
	 * @generated
	 */
	boolean isSetLooping();

	/**
	 * Returns the value of the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.GpmnDiagram#getProcesses <em>Processes</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gpmn Diagram</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #setGpmnDiagram(GpmnDiagram)
	 * @see jadex.tools.gpmn.GpmnPackage#getProcess_GpmnDiagram()
	 * @see jadex.tools.gpmn.GpmnDiagram#getProcesses
	 * @model opposite="processes"
	 * @generated
	 */
	GpmnDiagram getGpmnDiagram();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Process#getGpmnDiagram <em>Gpmn Diagram</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	void setGpmnDiagram(GpmnDiagram value);

} // Process
