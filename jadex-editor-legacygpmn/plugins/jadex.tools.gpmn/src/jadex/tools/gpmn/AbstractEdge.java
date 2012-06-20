/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package jadex.tools.gpmn;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.AbstractEdge#getParameterMapping <em>Parameter Mapping</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getAbstractEdge()
 * @model extendedMetaData="name='AbstractEdge' kind='elementOnly'"
 * @generated
 */
public interface AbstractEdge extends Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Parameter Mapping</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.ParameterMapping}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Mapping</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Mapping</em>' containment reference list.
	 * @see #isSetParameterMapping()
	 * @see #unsetParameterMapping()
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractEdge_ParameterMapping()
	 * @model containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='parameterMapping'"
	 * @generated
	 */
	EList<ParameterMapping> getParameterMapping();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractEdge#getParameterMapping <em>Parameter Mapping</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetParameterMapping()
	 * @see #getParameterMapping()
	 * @generated
	 */
	void unsetParameterMapping();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractEdge#getParameterMapping <em>Parameter Mapping</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Parameter Mapping</em>' containment reference list is set.
	 * @see #unsetParameterMapping()
	 * @see #getParameterMapping()
	 * @generated
	 */
	boolean isSetParameterMapping();

} // AbstractEdge
