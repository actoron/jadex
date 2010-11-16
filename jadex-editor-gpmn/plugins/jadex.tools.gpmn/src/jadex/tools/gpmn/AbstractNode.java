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
 * A representation of the model object '<em><b>Abstract Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.AbstractNode#getParameter <em>Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getAbstractNode()
 * @model extendedMetaData="name='AbstractNode' kind='elementOnly'"
 * @generated
 */
public interface AbstractNode extends Identifiable, NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Parameter</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter</em>' containment reference list.
	 * @see #isSetParameter()
	 * @see #unsetParameter()
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractNode_Parameter()
	 * @model containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='parameter'"
	 * @generated
	 */
	EList<Parameter> getParameter();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractNode#getParameter <em>Parameter</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetParameter()
	 * @see #getParameter()
	 * @generated
	 */
	void unsetParameter();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractNode#getParameter <em>Parameter</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Parameter</em>' containment reference list is set.
	 * @see #unsetParameter()
	 * @see #getParameter()
	 * @generated
	 */
	boolean isSetParameter();

} // AbstractNode
