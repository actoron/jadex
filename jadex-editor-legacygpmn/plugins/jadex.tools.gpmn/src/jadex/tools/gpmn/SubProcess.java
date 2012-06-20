/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *
 * $Id$
 */
package jadex.tools.gpmn;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Process</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.SubProcess#getProcessref <em>Processref</em>}</li>
 *   <li>{@link jadex.tools.gpmn.SubProcess#isInternal <em>Internal</em>}</li>
 *   <li>{@link jadex.tools.gpmn.SubProcess#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getSubProcess()
 * @model extendedMetaData="name='SubProcess' kind='elementOnly'"
 * @generated
 */
public interface SubProcess extends AbstractNode, Activatable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Processref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Processref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Processref</em>' attribute.
	 * @see #isSetProcessref()
	 * @see #unsetProcessref()
	 * @see #setProcessref(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getSubProcess_Processref()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='processref'"
	 * @generated
	 */
	String getProcessref();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SubProcess#getProcessref <em>Processref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Processref</em>' attribute.
	 * @see #isSetProcessref()
	 * @see #unsetProcessref()
	 * @see #getProcessref()
	 * @generated
	 */
	void setProcessref(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.SubProcess#getProcessref <em>Processref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetProcessref()
	 * @see #getProcessref()
	 * @see #setProcessref(String)
	 * @generated
	 */
	void unsetProcessref();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.SubProcess#getProcessref <em>Processref</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Processref</em>' attribute is set.
	 * @see #unsetProcessref()
	 * @see #getProcessref()
	 * @see #setProcessref(String)
	 * @generated
	 */
	boolean isSetProcessref();

	/**
	 * Returns the value of the '<em><b>Internal</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Internal</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Internal</em>' attribute.
	 * @see #setInternal(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getSubProcess_Internal()
	 * @model default="false" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='internal'"
	 * @generated
	 */
	boolean isInternal();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SubProcess#isInternal <em>Internal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Internal</em>' attribute.
	 * @see #isInternal()
	 * @generated
	 */
	void setInternal(boolean value);

	/**
	 * Returns the value of the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.GpmnDiagram#getSubProcesses <em>Sub Processes</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gpmn Diagram</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #setGpmnDiagram(GpmnDiagram)
	 * @see jadex.tools.gpmn.GpmnPackage#getSubProcess_GpmnDiagram()
	 * @see jadex.tools.gpmn.GpmnDiagram#getSubProcesses
	 * @model opposite="subProcesses"
	 * @generated
	 */
	GpmnDiagram getGpmnDiagram();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SubProcess#getGpmnDiagram <em>Gpmn Diagram</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	void setGpmnDiagram(GpmnDiagram value);

} // SubProcess
