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
package jadex.tools.gpmn.impl;

import jadex.tools.gpmn.Artifact;
import jadex.tools.gpmn.ArtifactsContainer;
import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Identifiable;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Artifact</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.ArtifactImpl#getId <em>Id</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.ArtifactImpl#getAssociations <em>Associations</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.ArtifactImpl#getArtifactsContainer <em>Artifacts Container</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ArtifactImpl extends NamedObjectImpl implements Artifact
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAssociations() <em>Associations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssociations()
	 * @generated
	 * @ordered
	 */
	protected EList<Association> associations;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ArtifactImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return GpmnPackage.Literals.ARTIFACT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId)
	{
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ARTIFACT__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Association> getAssociations()
	{
		if (associations == null)
		{
			associations = new EObjectContainmentWithInverseEList<Association>(Association.class, this, GpmnPackage.ARTIFACT__ASSOCIATIONS, GpmnPackage.ASSOCIATION__SOURCE);
		}
		return associations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactsContainer getArtifactsContainer()
	{
		if (eContainerFeatureID() != GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER) return null;
		return (ArtifactsContainer)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetArtifactsContainer(
			ArtifactsContainer newArtifactsContainer, NotificationChain msgs)
	{
		msgs = eBasicSetContainer((InternalEObject)newArtifactsContainer, GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArtifactsContainer(ArtifactsContainer newArtifactsContainer)
	{
		if (newArtifactsContainer != eInternalContainer() || (eContainerFeatureID() != GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER && newArtifactsContainer != null))
		{
			if (EcoreUtil.isAncestor(this, newArtifactsContainer))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newArtifactsContainer != null)
				msgs = ((InternalEObject)newArtifactsContainer).eInverseAdd(this, GpmnPackage.ARTIFACTS_CONTAINER__ARTIFACTS, ArtifactsContainer.class, msgs);
			msgs = basicSetArtifactsContainer(newArtifactsContainer, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER, newArtifactsContainer, newArtifactsContainer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.ARTIFACT__ASSOCIATIONS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getAssociations()).basicAdd(otherEnd, msgs);
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetArtifactsContainer((ArtifactsContainer)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.ARTIFACT__ASSOCIATIONS:
				return ((InternalEList<?>)getAssociations()).basicRemove(otherEnd, msgs);
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				return basicSetArtifactsContainer(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(
			NotificationChain msgs)
	{
		switch (eContainerFeatureID())
		{
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				return eInternalContainer().eInverseRemove(this, GpmnPackage.ARTIFACTS_CONTAINER__ARTIFACTS, ArtifactsContainer.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case GpmnPackage.ARTIFACT__ID:
				return getId();
			case GpmnPackage.ARTIFACT__ASSOCIATIONS:
				return getAssociations();
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				return getArtifactsContainer();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case GpmnPackage.ARTIFACT__ID:
				setId((String)newValue);
				return;
			case GpmnPackage.ARTIFACT__ASSOCIATIONS:
				getAssociations().clear();
				getAssociations().addAll((Collection<? extends Association>)newValue);
				return;
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				setArtifactsContainer((ArtifactsContainer)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case GpmnPackage.ARTIFACT__ID:
				setId(ID_EDEFAULT);
				return;
			case GpmnPackage.ARTIFACT__ASSOCIATIONS:
				getAssociations().clear();
				return;
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				setArtifactsContainer((ArtifactsContainer)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case GpmnPackage.ARTIFACT__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case GpmnPackage.ARTIFACT__ASSOCIATIONS:
				return associations != null && !associations.isEmpty();
			case GpmnPackage.ARTIFACT__ARTIFACTS_CONTAINER:
				return getArtifactsContainer() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
	{
		if (baseClass == Identifiable.class)
		{
			switch (derivedFeatureID)
			{
				case GpmnPackage.ARTIFACT__ID: return GpmnPackage.IDENTIFIABLE__ID;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
	{
		if (baseClass == Identifiable.class)
		{
			switch (baseFeatureID)
			{
				case GpmnPackage.IDENTIFIABLE__ID: return GpmnPackage.ARTIFACT__ID;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //ArtifactImpl
