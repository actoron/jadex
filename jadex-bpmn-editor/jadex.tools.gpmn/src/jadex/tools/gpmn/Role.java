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
 * A representation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Role#getInitialPerson <em>Initial Person</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Role#getPersonType <em>Person Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getRole()
 * @model extendedMetaData="name='Role' kind='elementOnly'"
 * @generated
 */
public interface Role extends NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Initial Person</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Initial Person</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Initial Person</em>' attribute.
	 * @see #setInitialPerson(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getRole_InitialPerson()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='initialPerson'"
	 * @generated
	 */
	String getInitialPerson();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Role#getInitialPerson <em>Initial Person</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Initial Person</em>' attribute.
	 * @see #getInitialPerson()
	 * @generated
	 */
	void setInitialPerson(String value);

	/**
	 * Returns the value of the '<em><b>Person Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Person Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Person Type</em>' attribute.
	 * @see #setPersonType(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getRole_PersonType()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='personType'"
	 * @generated
	 */
	String getPersonType();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Role#getPersonType <em>Person Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Person Type</em>' attribute.
	 * @see #getPersonType()
	 * @generated
	 */
	void setPersonType(String value);

} // Role
