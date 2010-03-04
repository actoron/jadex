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
 * A representation of the model object '<em><b>Artifacts Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.ArtifactsContainer#getArtifacts <em>Artifacts</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getArtifactsContainer()
 * @model extendedMetaData="name='ArtifactsContainer' kind='elementOnly'"
 * @generated
 */
public interface ArtifactsContainer extends NamedObject, Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Artifacts</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Artifact}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Artifact#getArtifactsContainer <em>Artifacts Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Artifacts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifacts</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getArtifactsContainer_Artifacts()
	 * @see jadex.tools.gpmn.Artifact#getArtifactsContainer
	 * @model opposite="artifactsContainer" containment="true"
	 *        extendedMetaData="kind='element' name='artifacts'"
	 * @generated
	 */
	EList<Artifact> getArtifacts();

} // ArtifactsContainer
