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

import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.InterGraphEdge;
import jadex.tools.gpmn.InterGraphVertex;
import jadex.tools.gpmn.NamedObject;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inter Graph Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl#getName <em>Name</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl#getNcname <em>Ncname</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl#getSource <em>Source</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InterGraphEdgeImpl extends AssociationTargetImpl implements
		InterGraphEdge
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getNcname() <em>Ncname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNcname()
	 * @generated
	 * @ordered
	 */
	protected static final String NCNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNcname() <em>Ncname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNcname()
	 * @generated
	 * @ordered
	 */
	protected String ncname = NCNAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected InterGraphVertex source;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected InterGraphVertex target;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InterGraphEdgeImpl()
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
		return GpmnPackage.Literals.INTER_GRAPH_EDGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription)
	{
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName)
	{
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNcname()
	{
		return ncname;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNcname(String newNcname)
	{
		String oldNcname = ncname;
		ncname = newNcname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__NCNAME, oldNcname, ncname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterGraphVertex getSource()
	{
		if (source != null && source.eIsProxy())
		{
			InternalEObject oldSource = (InternalEObject)source;
			source = (InterGraphVertex)eResolveProxy(oldSource);
			if (source != oldSource)
			{
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GpmnPackage.INTER_GRAPH_EDGE__SOURCE, oldSource, source));
			}
		}
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterGraphVertex basicGetSource()
	{
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSource(InterGraphVertex newSource,
			NotificationChain msgs)
	{
		InterGraphVertex oldSource = source;
		source = newSource;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__SOURCE, oldSource, newSource);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSource(InterGraphVertex newSource)
	{
		if (newSource != source)
		{
			NotificationChain msgs = null;
			if (source != null)
				msgs = ((InternalEObject)source).eInverseRemove(this, GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES, InterGraphVertex.class, msgs);
			if (newSource != null)
				msgs = ((InternalEObject)newSource).eInverseAdd(this, GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES, InterGraphVertex.class, msgs);
			msgs = basicSetSource(newSource, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__SOURCE, newSource, newSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterGraphVertex getTarget()
	{
		if (target != null && target.eIsProxy())
		{
			InternalEObject oldTarget = (InternalEObject)target;
			target = (InterGraphVertex)eResolveProxy(oldTarget);
			if (target != oldTarget)
			{
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GpmnPackage.INTER_GRAPH_EDGE__TARGET, oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterGraphVertex basicGetTarget()
	{
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTarget(InterGraphVertex newTarget,
			NotificationChain msgs)
	{
		InterGraphVertex oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__TARGET, oldTarget, newTarget);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTarget(InterGraphVertex newTarget)
	{
		if (newTarget != target)
		{
			NotificationChain msgs = null;
			if (target != null)
				msgs = ((InternalEObject)target).eInverseRemove(this, GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES, InterGraphVertex.class, msgs);
			if (newTarget != null)
				msgs = ((InternalEObject)newTarget).eInverseAdd(this, GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES, InterGraphVertex.class, msgs);
			msgs = basicSetTarget(newTarget, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.INTER_GRAPH_EDGE__TARGET, newTarget, newTarget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.INTER_GRAPH_EDGE__SOURCE:
				if (source != null)
					msgs = ((InternalEObject)source).eInverseRemove(this, GpmnPackage.INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES, InterGraphVertex.class, msgs);
				return basicSetSource((InterGraphVertex)otherEnd, msgs);
			case GpmnPackage.INTER_GRAPH_EDGE__TARGET:
				if (target != null)
					msgs = ((InternalEObject)target).eInverseRemove(this, GpmnPackage.INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES, InterGraphVertex.class, msgs);
				return basicSetTarget((InterGraphVertex)otherEnd, msgs);
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
			case GpmnPackage.INTER_GRAPH_EDGE__SOURCE:
				return basicSetSource(null, msgs);
			case GpmnPackage.INTER_GRAPH_EDGE__TARGET:
				return basicSetTarget(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
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
			case GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION:
				return getDescription();
			case GpmnPackage.INTER_GRAPH_EDGE__NAME:
				return getName();
			case GpmnPackage.INTER_GRAPH_EDGE__NCNAME:
				return getNcname();
			case GpmnPackage.INTER_GRAPH_EDGE__SOURCE:
				if (resolve) return getSource();
				return basicGetSource();
			case GpmnPackage.INTER_GRAPH_EDGE__TARGET:
				if (resolve) return getTarget();
				return basicGetTarget();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__NAME:
				setName((String)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__NCNAME:
				setNcname((String)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__SOURCE:
				setSource((InterGraphVertex)newValue);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__TARGET:
				setTarget((InterGraphVertex)newValue);
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
			case GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__NCNAME:
				setNcname(NCNAME_EDEFAULT);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__SOURCE:
				setSource((InterGraphVertex)null);
				return;
			case GpmnPackage.INTER_GRAPH_EDGE__TARGET:
				setTarget((InterGraphVertex)null);
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
			case GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case GpmnPackage.INTER_GRAPH_EDGE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GpmnPackage.INTER_GRAPH_EDGE__NCNAME:
				return NCNAME_EDEFAULT == null ? ncname != null : !NCNAME_EDEFAULT.equals(ncname);
			case GpmnPackage.INTER_GRAPH_EDGE__SOURCE:
				return source != null;
			case GpmnPackage.INTER_GRAPH_EDGE__TARGET:
				return target != null;
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
		if (baseClass == NamedObject.class)
		{
			switch (derivedFeatureID)
			{
				case GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION: return GpmnPackage.NAMED_OBJECT__DESCRIPTION;
				case GpmnPackage.INTER_GRAPH_EDGE__NAME: return GpmnPackage.NAMED_OBJECT__NAME;
				case GpmnPackage.INTER_GRAPH_EDGE__NCNAME: return GpmnPackage.NAMED_OBJECT__NCNAME;
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
		if (baseClass == NamedObject.class)
		{
			switch (baseFeatureID)
			{
				case GpmnPackage.NAMED_OBJECT__DESCRIPTION: return GpmnPackage.INTER_GRAPH_EDGE__DESCRIPTION;
				case GpmnPackage.NAMED_OBJECT__NAME: return GpmnPackage.INTER_GRAPH_EDGE__NAME;
				case GpmnPackage.NAMED_OBJECT__NCNAME: return GpmnPackage.INTER_GRAPH_EDGE__NCNAME;
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
		result.append(" (description: ");
		result.append(description);
		result.append(", name: ");
		result.append(name);
		result.append(", ncname: ");
		result.append(ncname);
		result.append(')');
		return result.toString();
	}

} //InterGraphEdgeImpl
