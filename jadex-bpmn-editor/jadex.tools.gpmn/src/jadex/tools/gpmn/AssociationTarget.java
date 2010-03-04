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
 * A representation of the model object '<em><b>Association Target</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.AssociationTarget#getAssociations <em>Associations</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getAssociationTarget()
 * @model extendedMetaData="name='AssociationTarget' kind='elementOnly'"
 * @generated
 */
public interface AssociationTarget extends Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Associations</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Association}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Association#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Associations</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Associations</em>' reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getAssociationTarget_Associations()
	 * @see jadex.tools.gpmn.Association#getTarget
	 * @model opposite="target" resolveProxies="false"
	 *        extendedMetaData="kind='element' name='associations'"
	 * @generated
	 */
	EList<Association> getAssociations();

} // AssociationTarget
