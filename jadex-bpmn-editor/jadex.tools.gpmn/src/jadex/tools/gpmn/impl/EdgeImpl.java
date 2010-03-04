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

import jadex.tools.gpmn.Edge;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Graph;
import jadex.tools.gpmn.NamedObject;
import jadex.tools.gpmn.Vertex;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#getName <em>Name</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#getNcname <em>Ncname</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#isIsDefault <em>Is Default</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#getSource <em>Source</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.EdgeImpl#getGraph <em>Graph</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EdgeImpl extends AssociationTargetImpl implements Edge
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
	 * The default value of the '{@link #isIsDefault() <em>Is Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsDefault()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_DEFAULT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsDefault() <em>Is Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsDefault()
	 * @generated
	 * @ordered
	 */
	protected boolean isDefault = IS_DEFAULT_EDEFAULT;

	/**
	 * This is true if the Is Default attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean isDefaultESet;

	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected Vertex source;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected Vertex target;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EdgeImpl()
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
		return GpmnPackage.Literals.EDGE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__NCNAME, oldNcname, ncname));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsDefault()
	{
		return isDefault;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsDefault(boolean newIsDefault)
	{
		boolean oldIsDefault = isDefault;
		isDefault = newIsDefault;
		boolean oldIsDefaultESet = isDefaultESet;
		isDefaultESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__IS_DEFAULT, oldIsDefault, isDefault, !oldIsDefaultESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetIsDefault()
	{
		boolean oldIsDefault = isDefault;
		boolean oldIsDefaultESet = isDefaultESet;
		isDefault = IS_DEFAULT_EDEFAULT;
		isDefaultESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.EDGE__IS_DEFAULT, oldIsDefault, IS_DEFAULT_EDEFAULT, oldIsDefaultESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetIsDefault()
	{
		return isDefaultESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Vertex getSource()
	{
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSource(Vertex newSource,
			NotificationChain msgs)
	{
		Vertex oldSource = source;
		source = newSource;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__SOURCE, oldSource, newSource);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSource(Vertex newSource)
	{
		if (newSource != source)
		{
			NotificationChain msgs = null;
			if (source != null)
				msgs = ((InternalEObject)source).eInverseRemove(this, GpmnPackage.VERTEX__OUTGOING_EDGES, Vertex.class, msgs);
			if (newSource != null)
				msgs = ((InternalEObject)newSource).eInverseAdd(this, GpmnPackage.VERTEX__OUTGOING_EDGES, Vertex.class, msgs);
			msgs = basicSetSource(newSource, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__SOURCE, newSource, newSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Vertex getTarget()
	{
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTarget(Vertex newTarget,
			NotificationChain msgs)
	{
		Vertex oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__TARGET, oldTarget, newTarget);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTarget(Vertex newTarget)
	{
		if (newTarget != target)
		{
			NotificationChain msgs = null;
			if (target != null)
				msgs = ((InternalEObject)target).eInverseRemove(this, GpmnPackage.VERTEX__INCOMING_EDGES, Vertex.class, msgs);
			if (newTarget != null)
				msgs = ((InternalEObject)newTarget).eInverseAdd(this, GpmnPackage.VERTEX__INCOMING_EDGES, Vertex.class, msgs);
			msgs = basicSetTarget(newTarget, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__TARGET, newTarget, newTarget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Graph getGraph()
	{
		if (eContainerFeatureID() != GpmnPackage.EDGE__GRAPH) return null;
		return (Graph)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGraph(Graph newGraph,
			NotificationChain msgs)
	{
		msgs = eBasicSetContainer((InternalEObject)newGraph, GpmnPackage.EDGE__GRAPH, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGraph(Graph newGraph)
	{
		if (newGraph != eInternalContainer() || (eContainerFeatureID() != GpmnPackage.EDGE__GRAPH && newGraph != null))
		{
			if (EcoreUtil.isAncestor(this, newGraph))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newGraph != null)
				msgs = ((InternalEObject)newGraph).eInverseAdd(this, GpmnPackage.GRAPH__SEQUENCE_EDGES, Graph.class, msgs);
			msgs = basicSetGraph(newGraph, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.EDGE__GRAPH, newGraph, newGraph));
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
			case GpmnPackage.EDGE__SOURCE:
				if (source != null)
					msgs = ((InternalEObject)source).eInverseRemove(this, GpmnPackage.VERTEX__OUTGOING_EDGES, Vertex.class, msgs);
				return basicSetSource((Vertex)otherEnd, msgs);
			case GpmnPackage.EDGE__TARGET:
				if (target != null)
					msgs = ((InternalEObject)target).eInverseRemove(this, GpmnPackage.VERTEX__INCOMING_EDGES, Vertex.class, msgs);
				return basicSetTarget((Vertex)otherEnd, msgs);
			case GpmnPackage.EDGE__GRAPH:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetGraph((Graph)otherEnd, msgs);
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
			case GpmnPackage.EDGE__SOURCE:
				return basicSetSource(null, msgs);
			case GpmnPackage.EDGE__TARGET:
				return basicSetTarget(null, msgs);
			case GpmnPackage.EDGE__GRAPH:
				return basicSetGraph(null, msgs);
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
			case GpmnPackage.EDGE__GRAPH:
				return eInternalContainer().eInverseRemove(this, GpmnPackage.GRAPH__SEQUENCE_EDGES, Graph.class, msgs);
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
			case GpmnPackage.EDGE__DESCRIPTION:
				return getDescription();
			case GpmnPackage.EDGE__NAME:
				return getName();
			case GpmnPackage.EDGE__NCNAME:
				return getNcname();
			case GpmnPackage.EDGE__IS_DEFAULT:
				return isIsDefault();
			case GpmnPackage.EDGE__SOURCE:
				return getSource();
			case GpmnPackage.EDGE__TARGET:
				return getTarget();
			case GpmnPackage.EDGE__GRAPH:
				return getGraph();
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
			case GpmnPackage.EDGE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case GpmnPackage.EDGE__NAME:
				setName((String)newValue);
				return;
			case GpmnPackage.EDGE__NCNAME:
				setNcname((String)newValue);
				return;
			case GpmnPackage.EDGE__IS_DEFAULT:
				setIsDefault((Boolean)newValue);
				return;
			case GpmnPackage.EDGE__SOURCE:
				setSource((Vertex)newValue);
				return;
			case GpmnPackage.EDGE__TARGET:
				setTarget((Vertex)newValue);
				return;
			case GpmnPackage.EDGE__GRAPH:
				setGraph((Graph)newValue);
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
			case GpmnPackage.EDGE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case GpmnPackage.EDGE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GpmnPackage.EDGE__NCNAME:
				setNcname(NCNAME_EDEFAULT);
				return;
			case GpmnPackage.EDGE__IS_DEFAULT:
				unsetIsDefault();
				return;
			case GpmnPackage.EDGE__SOURCE:
				setSource((Vertex)null);
				return;
			case GpmnPackage.EDGE__TARGET:
				setTarget((Vertex)null);
				return;
			case GpmnPackage.EDGE__GRAPH:
				setGraph((Graph)null);
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
			case GpmnPackage.EDGE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case GpmnPackage.EDGE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GpmnPackage.EDGE__NCNAME:
				return NCNAME_EDEFAULT == null ? ncname != null : !NCNAME_EDEFAULT.equals(ncname);
			case GpmnPackage.EDGE__IS_DEFAULT:
				return isSetIsDefault();
			case GpmnPackage.EDGE__SOURCE:
				return source != null;
			case GpmnPackage.EDGE__TARGET:
				return target != null;
			case GpmnPackage.EDGE__GRAPH:
				return getGraph() != null;
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
				case GpmnPackage.EDGE__DESCRIPTION: return GpmnPackage.NAMED_OBJECT__DESCRIPTION;
				case GpmnPackage.EDGE__NAME: return GpmnPackage.NAMED_OBJECT__NAME;
				case GpmnPackage.EDGE__NCNAME: return GpmnPackage.NAMED_OBJECT__NCNAME;
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
				case GpmnPackage.NAMED_OBJECT__DESCRIPTION: return GpmnPackage.EDGE__DESCRIPTION;
				case GpmnPackage.NAMED_OBJECT__NAME: return GpmnPackage.EDGE__NAME;
				case GpmnPackage.NAMED_OBJECT__NCNAME: return GpmnPackage.EDGE__NCNAME;
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
		result.append(", isDefault: ");
		if (isDefaultESet) result.append(isDefault); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //EdgeImpl
