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

import org.eclipse.emf.ecore.EModelElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Activatable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Activatable#getActivationEdges <em>Activation Edges</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getActivatable()
 * @model extendedMetaData="name='Activatable' kind='elementOnly'"
 * @generated
 */
public interface Activatable extends EModelElement
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
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ActivationEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Activation Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Activation Edges</em>' reference list.
	 * @see #isSetActivationEdges()
	 * @see #unsetActivationEdges()
	 * @see jadex.tools.gpmn.GpmnPackage#getActivatable_ActivationEdges()
	 * @see jadex.tools.gpmn.ActivationEdge#getTarget
	 * @model opposite="target" resolveProxies="false" unsettable="true" transient="true"
	 *        extendedMetaData="kind='element' name='activationEdges'"
	 * @generated
	 */
	EList<ActivationEdge> getActivationEdges();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Activatable#getActivationEdges <em>Activation Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetActivationEdges()
	 * @see #getActivationEdges()
	 * @generated
	 */
	void unsetActivationEdges();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Activatable#getActivationEdges <em>Activation Edges</em>}' reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Activation Edges</em>' reference list is set.
	 * @see #unsetActivationEdges()
	 * @see #getActivationEdges()
	 * @generated
	 */
	boolean isSetActivationEdges();

} // Activatable
