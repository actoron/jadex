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
 * A representation of the model object '<em><b>Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Context#getElements <em>Elements</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Context#getRoles <em>Roles</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Context#getGroups <em>Groups</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Context#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getContext()
 * @model extendedMetaData="name='Context' kind='elementOnly'"
 * @generated
 */
public interface Context extends Artifact, Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.ContextElement}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ContextElement#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Elements</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getContext_Elements()
	 * @see jadex.tools.gpmn.ContextElement#getContext
	 * @model opposite="context" containment="true"
	 *        extendedMetaData="kind='element' name='elements'"
	 * @generated
	 */
	EList<ContextElement> getElements();

	/**
	 * Returns the value of the '<em><b>Roles</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Role}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Roles</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Roles</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getContext_Roles()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='roles'"
	 * @generated
	 */
	EList<Role> getRoles();

	/**
	 * Returns the value of the '<em><b>Groups</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Group}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Groups</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Groups</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getContext_Groups()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='groups'"
	 * @generated
	 */
	EList<Group> getGroups();

	/**
	 * Returns the value of the '<em><b>Types</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Types</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Types</em>' attribute.
	 * @see #setTypes(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getContext_Types()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='types'"
	 * @generated
	 */
	String getTypes();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Context#getTypes <em>Types</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Types</em>' attribute.
	 * @see #getTypes()
	 * @generated
	 */
	void setTypes(String value);

} // Context
