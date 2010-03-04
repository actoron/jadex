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
 * A representation of the model object '<em><b>Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Group#getMembers <em>Members</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Group#getCoordinator <em>Coordinator</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Group#getHead <em>Head</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getGroup()
 * @model extendedMetaData="name='Group' kind='elementOnly'"
 * @generated
 */
public interface Group extends NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Members</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Members</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Members</em>' attribute list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGroup_Members()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='members'"
	 * @generated
	 */
	EList<String> getMembers();

	/**
	 * Returns the value of the '<em><b>Coordinator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Coordinator</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Coordinator</em>' attribute.
	 * @see #setCoordinator(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGroup_Coordinator()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='coordinator'"
	 * @generated
	 */
	String getCoordinator();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Group#getCoordinator <em>Coordinator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Coordinator</em>' attribute.
	 * @see #getCoordinator()
	 * @generated
	 */
	void setCoordinator(String value);

	/**
	 * Returns the value of the '<em><b>Head</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Head</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Head</em>' attribute.
	 * @see #setHead(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGroup_Head()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='head'"
	 * @generated
	 */
	String getHead();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Group#getHead <em>Head</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Head</em>' attribute.
	 * @see #getHead()
	 * @generated
	 */
	void setHead(String value);

} // Group
