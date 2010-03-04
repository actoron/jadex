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

import org.eclipse.emf.ecore.EModelElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Identifiable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Identifiable#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getIdentifiable()
 * @model extendedMetaData="name='Identifiable' kind='elementOnly'"
 * @generated
 */
public interface Identifiable extends EModelElement
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getIdentifiable_Id()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Identifiable#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // Identifiable
