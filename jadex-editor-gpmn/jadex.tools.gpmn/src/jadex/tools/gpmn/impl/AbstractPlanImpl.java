/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *
 * $Id$
 */
package jadex.tools.gpmn.impl;

import jadex.tools.gpmn.AbstractPlan;
import jadex.tools.gpmn.ConditionLanguage;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.PlanEdge;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Plan</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getPlanEdges <em>Plan Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getContextcondition <em>Contextcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getPrecondition <em>Precondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getPreconditionLanguage <em>Precondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getPriority <em>Priority</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.AbstractPlanImpl#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AbstractPlanImpl extends AbstractNodeImpl implements
		AbstractPlan
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getPlanEdges() <em>Plan Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlanEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<PlanEdge> planEdges;

	/**
	 * The default value of the '{@link #getContextcondition() <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTEXTCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContextcondition() <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextcondition()
	 * @generated
	 * @ordered
	 */
	protected String contextcondition = CONTEXTCONDITION_EDEFAULT;

	/**
	 * This is true if the Contextcondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean contextconditionESet;

	/**
	 * The default value of the '{@link #getTargetconditionLanguage() <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage TARGETCONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JCL;

	/**
	 * The cached value of the '{@link #getTargetconditionLanguage() <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage targetconditionLanguage = TARGETCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Targetcondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean targetconditionLanguageESet;

	/**
	 * The default value of the '{@link #getPrecondition() <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrecondition()
	 * @generated
	 * @ordered
	 */
	protected static final String PRECONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrecondition() <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrecondition()
	 * @generated
	 * @ordered
	 */
	protected String precondition = PRECONDITION_EDEFAULT;

	/**
	 * This is true if the Precondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean preconditionESet;

	/**
	 * The default value of the '{@link #getPreconditionLanguage() <em>Precondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPreconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage PRECONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JAVA;

	/**
	 * The cached value of the '{@link #getPreconditionLanguage() <em>Precondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPreconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage preconditionLanguage = PRECONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Precondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean preconditionLanguageESet;

	/**
	 * The default value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected static final int PRIORITY_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected int priority = PRIORITY_EDEFAULT;

	/**
	 * This is true if the Priority attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean priorityESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AbstractPlanImpl()
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
		return GpmnPackage.Literals.ABSTRACT_PLAN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PlanEdge> getPlanEdges()
	{
		if (planEdges == null)
		{
			planEdges = new EObjectWithInverseEList.Unsettable<PlanEdge>(PlanEdge.class, this, GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES, GpmnPackage.PLAN_EDGE__TARGET);
		}
		return planEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPlanEdges()
	{
		if (planEdges != null) ((InternalEList.Unsettable<?>)planEdges).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPlanEdges()
	{
		return planEdges != null && ((InternalEList.Unsettable<?>)planEdges).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getContextcondition()
	{
		return contextcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContextcondition(String newContextcondition)
	{
		String oldContextcondition = contextcondition;
		contextcondition = newContextcondition;
		boolean oldContextconditionESet = contextconditionESet;
		contextconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ABSTRACT_PLAN__CONTEXTCONDITION, oldContextcondition, contextcondition, !oldContextconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetContextcondition()
	{
		String oldContextcondition = contextcondition;
		boolean oldContextconditionESet = contextconditionESet;
		contextcondition = CONTEXTCONDITION_EDEFAULT;
		contextconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.ABSTRACT_PLAN__CONTEXTCONDITION, oldContextcondition, CONTEXTCONDITION_EDEFAULT, oldContextconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetContextcondition()
	{
		return contextconditionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage getTargetconditionLanguage()
	{
		return targetconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetconditionLanguage(
			ConditionLanguage newTargetconditionLanguage)
	{
		ConditionLanguage oldTargetconditionLanguage = targetconditionLanguage;
		targetconditionLanguage = newTargetconditionLanguage == null ? TARGETCONDITION_LANGUAGE_EDEFAULT : newTargetconditionLanguage;
		boolean oldTargetconditionLanguageESet = targetconditionLanguageESet;
		targetconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, targetconditionLanguage, !oldTargetconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTargetconditionLanguage()
	{
		ConditionLanguage oldTargetconditionLanguage = targetconditionLanguage;
		boolean oldTargetconditionLanguageESet = targetconditionLanguageESet;
		targetconditionLanguage = TARGETCONDITION_LANGUAGE_EDEFAULT;
		targetconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, TARGETCONDITION_LANGUAGE_EDEFAULT, oldTargetconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTargetconditionLanguage()
	{
		return targetconditionLanguageESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPrecondition()
	{
		return precondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrecondition(String newPrecondition)
	{
		String oldPrecondition = precondition;
		precondition = newPrecondition;
		boolean oldPreconditionESet = preconditionESet;
		preconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ABSTRACT_PLAN__PRECONDITION, oldPrecondition, precondition, !oldPreconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPrecondition()
	{
		String oldPrecondition = precondition;
		boolean oldPreconditionESet = preconditionESet;
		precondition = PRECONDITION_EDEFAULT;
		preconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.ABSTRACT_PLAN__PRECONDITION, oldPrecondition, PRECONDITION_EDEFAULT, oldPreconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPrecondition()
	{
		return preconditionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage getPreconditionLanguage()
	{
		return preconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPreconditionLanguage(
			ConditionLanguage newPreconditionLanguage)
	{
		ConditionLanguage oldPreconditionLanguage = preconditionLanguage;
		preconditionLanguage = newPreconditionLanguage == null ? PRECONDITION_LANGUAGE_EDEFAULT : newPreconditionLanguage;
		boolean oldPreconditionLanguageESet = preconditionLanguageESet;
		preconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ABSTRACT_PLAN__PRECONDITION_LANGUAGE, oldPreconditionLanguage, preconditionLanguage, !oldPreconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPreconditionLanguage()
	{
		ConditionLanguage oldPreconditionLanguage = preconditionLanguage;
		boolean oldPreconditionLanguageESet = preconditionLanguageESet;
		preconditionLanguage = PRECONDITION_LANGUAGE_EDEFAULT;
		preconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.ABSTRACT_PLAN__PRECONDITION_LANGUAGE, oldPreconditionLanguage, PRECONDITION_LANGUAGE_EDEFAULT, oldPreconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPreconditionLanguage()
	{
		return preconditionLanguageESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPriority(int newPriority)
	{
		int oldPriority = priority;
		priority = newPriority;
		boolean oldPriorityESet = priorityESet;
		priorityESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ABSTRACT_PLAN__PRIORITY, oldPriority, priority, !oldPriorityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPriority()
	{
		int oldPriority = priority;
		boolean oldPriorityESet = priorityESet;
		priority = PRIORITY_EDEFAULT;
		priorityESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.ABSTRACT_PLAN__PRIORITY, oldPriority, PRIORITY_EDEFAULT, oldPriorityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPriority()
	{
		return priorityESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnDiagram getGpmnDiagram()
	{
		if (eContainerFeatureID() != GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM) return null;
		return (GpmnDiagram)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGpmnDiagram(GpmnDiagram newGpmnDiagram,
			NotificationChain msgs)
	{
		msgs = eBasicSetContainer((InternalEObject)newGpmnDiagram, GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGpmnDiagram(GpmnDiagram newGpmnDiagram)
	{
		if (newGpmnDiagram != eInternalContainer() || (eContainerFeatureID() != GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM && newGpmnDiagram != null))
		{
			if (EcoreUtil.isAncestor(this, newGpmnDiagram))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newGpmnDiagram != null)
				msgs = ((InternalEObject)newGpmnDiagram).eInverseAdd(this, GpmnPackage.GPMN_DIAGRAM__PLANS, GpmnDiagram.class, msgs);
			msgs = basicSetGpmnDiagram(newGpmnDiagram, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM, newGpmnDiagram, newGpmnDiagram));
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
			case GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getPlanEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetGpmnDiagram((GpmnDiagram)otherEnd, msgs);
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
			case GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES:
				return ((InternalEList<?>)getPlanEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				return basicSetGpmnDiagram(null, msgs);
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
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				return eInternalContainer().eInverseRemove(this, GpmnPackage.GPMN_DIAGRAM__PLANS, GpmnDiagram.class, msgs);
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
			case GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES:
				return getPlanEdges();
			case GpmnPackage.ABSTRACT_PLAN__CONTEXTCONDITION:
				return getContextcondition();
			case GpmnPackage.ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE:
				return getTargetconditionLanguage();
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION:
				return getPrecondition();
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION_LANGUAGE:
				return getPreconditionLanguage();
			case GpmnPackage.ABSTRACT_PLAN__PRIORITY:
				return getPriority();
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				return getGpmnDiagram();
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
			case GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES:
				getPlanEdges().clear();
				getPlanEdges().addAll((Collection<? extends PlanEdge>)newValue);
				return;
			case GpmnPackage.ABSTRACT_PLAN__CONTEXTCONDITION:
				setContextcondition((String)newValue);
				return;
			case GpmnPackage.ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE:
				setTargetconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION:
				setPrecondition((String)newValue);
				return;
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION_LANGUAGE:
				setPreconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.ABSTRACT_PLAN__PRIORITY:
				setPriority((Integer)newValue);
				return;
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				setGpmnDiagram((GpmnDiagram)newValue);
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
			case GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES:
				unsetPlanEdges();
				return;
			case GpmnPackage.ABSTRACT_PLAN__CONTEXTCONDITION:
				unsetContextcondition();
				return;
			case GpmnPackage.ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE:
				unsetTargetconditionLanguage();
				return;
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION:
				unsetPrecondition();
				return;
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION_LANGUAGE:
				unsetPreconditionLanguage();
				return;
			case GpmnPackage.ABSTRACT_PLAN__PRIORITY:
				unsetPriority();
				return;
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				setGpmnDiagram((GpmnDiagram)null);
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
			case GpmnPackage.ABSTRACT_PLAN__PLAN_EDGES:
				return isSetPlanEdges();
			case GpmnPackage.ABSTRACT_PLAN__CONTEXTCONDITION:
				return isSetContextcondition();
			case GpmnPackage.ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE:
				return isSetTargetconditionLanguage();
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION:
				return isSetPrecondition();
			case GpmnPackage.ABSTRACT_PLAN__PRECONDITION_LANGUAGE:
				return isSetPreconditionLanguage();
			case GpmnPackage.ABSTRACT_PLAN__PRIORITY:
				return isSetPriority();
			case GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM:
				return getGpmnDiagram() != null;
		}
		return super.eIsSet(featureID);
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
		result.append(" (contextcondition: "); //$NON-NLS-1$
		if (contextconditionESet) result.append(contextcondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", targetconditionLanguage: "); //$NON-NLS-1$
		if (targetconditionLanguageESet) result.append(targetconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", precondition: "); //$NON-NLS-1$
		if (preconditionESet) result.append(precondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", preconditionLanguage: "); //$NON-NLS-1$
		if (preconditionLanguageESet) result.append(preconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", priority: "); //$NON-NLS-1$
		if (priorityESet) result.append(priority); else result.append("<unset>"); //$NON-NLS-1$
		result.append(')');
		return result.toString();
	}

} //AbstractPlanImpl
