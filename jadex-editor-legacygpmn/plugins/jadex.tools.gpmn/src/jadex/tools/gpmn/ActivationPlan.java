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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Activation Plan</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.ActivationPlan#getActivationEdges <em>Activation Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ActivationPlan#getMode <em>Mode</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getActivationPlan()
 * @model extendedMetaData="name='ActivationPlan' kind='elementOnly'"
 * @generated
 */
public interface ActivationPlan extends AbstractPlan
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Activation Edges</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.ActivationEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ActivationEdge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Activation Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Activation Edges</em>' reference list.
	 * @see #isSetActivationEdges()
	 * @see #unsetActivationEdges()
	 * @see jadex.tools.gpmn.GpmnPackage#getActivationPlan_ActivationEdges()
	 * @see jadex.tools.gpmn.ActivationEdge#getSource
	 * @model opposite="source" resolveProxies="false" unsettable="true" transient="true"
	 *        extendedMetaData="kind='element' name='activationEdges'"
	 * @generated
	 */
	EList<ActivationEdge> getActivationEdges();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.ActivationPlan#getActivationEdges <em>Activation Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetActivationEdges()
	 * @see #getActivationEdges()
	 * @generated
	 */
	void unsetActivationEdges();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.ActivationPlan#getActivationEdges <em>Activation Edges</em>}' reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Activation Edges</em>' reference list is set.
	 * @see #unsetActivationEdges()
	 * @see #getActivationEdges()
	 * @generated
	 */
	boolean isSetActivationEdges();

	/**
	 * Returns the value of the '<em><b>Mode</b></em>' attribute.
	 * The default value is <code>"Parallel"</code>.
	 * The literals are from the enumeration {@link jadex.tools.gpmn.ModeType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mode</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mode</em>' attribute.
	 * @see jadex.tools.gpmn.ModeType
	 * @see #isSetMode()
	 * @see #unsetMode()
	 * @see #setMode(ModeType)
	 * @see jadex.tools.gpmn.GpmnPackage#getActivationPlan_Mode()
	 * @model default="Parallel" unsettable="true"
	 *        extendedMetaData="kind='attribute' name='mode'"
	 * @generated
	 */
	ModeType getMode();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ActivationPlan#getMode <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mode</em>' attribute.
	 * @see jadex.tools.gpmn.ModeType
	 * @see #isSetMode()
	 * @see #unsetMode()
	 * @see #getMode()
	 * @generated
	 */
	void setMode(ModeType value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.ActivationPlan#getMode <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMode()
	 * @see #getMode()
	 * @see #setMode(ModeType)
	 * @generated
	 */
	void unsetMode();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.ActivationPlan#getMode <em>Mode</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Mode</em>' attribute is set.
	 * @see #unsetMode()
	 * @see #getMode()
	 * @see #setMode(ModeType)
	 * @generated
	 */
	boolean isSetMode();

} // ActivationPlan
