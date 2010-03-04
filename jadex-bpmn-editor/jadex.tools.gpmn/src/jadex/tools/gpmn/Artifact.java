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
 * A representation of the model object '<em><b>Artifact</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Artifact#getAssociations <em>Associations</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Artifact#getArtifactsContainer <em>Artifacts Container</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getArtifact()
 * @model extendedMetaData="name='Artifact' kind='elementOnly'"
 * @generated
 */
public interface Artifact extends NamedObject, Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Associations</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Association}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Association#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Associations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Associations</em>' containment reference list.
	 * @see jadex.tools.gpmn.GpmnPackage#getArtifact_Associations()
	 * @see jadex.tools.gpmn.Association#getSource
	 * @model opposite="source" containment="true"
	 *        extendedMetaData="kind='element' name='associations'"
	 * @generated
	 */
	EList<Association> getAssociations();

	/**
	 * Returns the value of the '<em><b>Artifacts Container</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ArtifactsContainer#getArtifacts <em>Artifacts</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Artifacts Container</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifacts Container</em>' container reference.
	 * @see #setArtifactsContainer(ArtifactsContainer)
	 * @see jadex.tools.gpmn.GpmnPackage#getArtifact_ArtifactsContainer()
	 * @see jadex.tools.gpmn.ArtifactsContainer#getArtifacts
	 * @model opposite="artifacts"
	 * @generated
	 */
	ArtifactsContainer getArtifactsContainer();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Artifact#getArtifactsContainer <em>Artifacts Container</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Artifacts Container</em>' container reference.
	 * @see #getArtifactsContainer()
	 * @generated
	 */
	void setArtifactsContainer(ArtifactsContainer value);

} // Artifact
