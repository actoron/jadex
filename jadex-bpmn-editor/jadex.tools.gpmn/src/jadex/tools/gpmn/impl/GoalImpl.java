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

import jadex.tools.gpmn.ExcludeType;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.GpmnPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Goal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getUnique <em>Unique</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getCreationcondition <em>Creationcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getCreationconditionLanguage <em>Creationcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getContextcondition <em>Contextcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getContextconditionLanguage <em>Contextcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getDropcondition <em>Dropcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getDropconditionLanguage <em>Dropcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getRecurcondition <em>Recurcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getDeliberation <em>Deliberation</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getOnSuccessHandler <em>On Success Handler</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getOnSkipHandler <em>On Skip Handler</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getOnFailureHandler <em>On Failure Handler</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getExclude <em>Exclude</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getGoalType <em>Goal Type</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isPosttoall <em>Posttoall</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRandomselection <em>Randomselection</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRecalculate <em>Recalculate</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRecur <em>Recur</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getRecurdelay <em>Recurdelay</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRetry <em>Retry</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getRetrydelay <em>Retrydelay</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isSequential <em>Sequential</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GoalImpl extends ParameterizedVertexImpl implements Goal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The default value of the '{@link #getUnique() <em>Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnique()
	 * @generated
	 * @ordered
	 */
	protected static final String UNIQUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUnique() <em>Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnique()
	 * @generated
	 * @ordered
	 */
	protected String unique = UNIQUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getCreationcondition() <em>Creationcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String CREATIONCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreationcondition() <em>Creationcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationcondition()
	 * @generated
	 * @ordered
	 */
	protected String creationcondition = CREATIONCONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getCreationconditionLanguage() <em>Creationcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String CREATIONCONDITION_LANGUAGE_EDEFAULT = "jcl";

	/**
	 * The cached value of the '{@link #getCreationconditionLanguage() <em>Creationcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected String creationconditionLanguage = CREATIONCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Creationcondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean creationconditionLanguageESet;

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
	 * The default value of the '{@link #getContextconditionLanguage() <em>Contextcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTEXTCONDITION_LANGUAGE_EDEFAULT = "jcl";

	/**
	 * The cached value of the '{@link #getContextconditionLanguage() <em>Contextcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected String contextconditionLanguage = CONTEXTCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Contextcondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean contextconditionLanguageESet;

	/**
	 * The default value of the '{@link #getDropcondition() <em>Dropcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDropcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String DROPCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDropcondition() <em>Dropcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDropcondition()
	 * @generated
	 * @ordered
	 */
	protected String dropcondition = DROPCONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDropconditionLanguage() <em>Dropcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDropconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String DROPCONDITION_LANGUAGE_EDEFAULT = "jcl";

	/**
	 * The cached value of the '{@link #getDropconditionLanguage() <em>Dropcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDropconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected String dropconditionLanguage = DROPCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Dropcondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean dropconditionLanguageESet;

	/**
	 * The default value of the '{@link #getRecurcondition() <em>Recurcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecurcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String RECURCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRecurcondition() <em>Recurcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecurcondition()
	 * @generated
	 * @ordered
	 */
	protected String recurcondition = RECURCONDITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeliberation() <em>Deliberation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeliberation()
	 * @generated
	 * @ordered
	 */
	protected static final String DELIBERATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeliberation() <em>Deliberation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeliberation()
	 * @generated
	 * @ordered
	 */
	protected String deliberation = DELIBERATION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOnSuccessHandler() <em>On Success Handler</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnSuccessHandler()
	 * @generated
	 * @ordered
	 */
	protected EList<String> onSuccessHandler;

	/**
	 * The cached value of the '{@link #getOnSkipHandler() <em>On Skip Handler</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnSkipHandler()
	 * @generated
	 * @ordered
	 */
	protected EList<String> onSkipHandler;

	/**
	 * The cached value of the '{@link #getOnFailureHandler() <em>On Failure Handler</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOnFailureHandler()
	 * @generated
	 * @ordered
	 */
	protected EList<String> onFailureHandler;

	/**
	 * The default value of the '{@link #getExclude() <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclude()
	 * @generated
	 * @ordered
	 */
	protected static final ExcludeType EXCLUDE_EDEFAULT = ExcludeType.NEVER;

	/**
	 * The cached value of the '{@link #getExclude() <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclude()
	 * @generated
	 * @ordered
	 */
	protected ExcludeType exclude = EXCLUDE_EDEFAULT;

	/**
	 * This is true if the Exclude attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean excludeESet;

	/**
	 * The default value of the '{@link #getGoalType() <em>Goal Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGoalType()
	 * @generated
	 * @ordered
	 */
	protected static final GoalType GOAL_TYPE_EDEFAULT = GoalType.META_GOAL;

	/**
	 * The cached value of the '{@link #getGoalType() <em>Goal Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGoalType()
	 * @generated
	 * @ordered
	 */
	protected GoalType goalType = GOAL_TYPE_EDEFAULT;

	/**
	 * This is true if the Goal Type attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean goalTypeESet;

	/**
	 * The default value of the '{@link #isPosttoall() <em>Posttoall</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPosttoall()
	 * @generated
	 * @ordered
	 */
	protected static final boolean POSTTOALL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPosttoall() <em>Posttoall</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPosttoall()
	 * @generated
	 * @ordered
	 */
	protected boolean posttoall = POSTTOALL_EDEFAULT;

	/**
	 * This is true if the Posttoall attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean posttoallESet;

	/**
	 * The default value of the '{@link #isRandomselection() <em>Randomselection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRandomselection()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RANDOMSELECTION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRandomselection() <em>Randomselection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRandomselection()
	 * @generated
	 * @ordered
	 */
	protected boolean randomselection = RANDOMSELECTION_EDEFAULT;

	/**
	 * This is true if the Randomselection attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean randomselectionESet;

	/**
	 * The default value of the '{@link #isRecalculate() <em>Recalculate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecalculate()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RECALCULATE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isRecalculate() <em>Recalculate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecalculate()
	 * @generated
	 * @ordered
	 */
	protected boolean recalculate = RECALCULATE_EDEFAULT;

	/**
	 * This is true if the Recalculate attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean recalculateESet;

	/**
	 * The default value of the '{@link #isRecur() <em>Recur</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecur()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RECUR_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRecur() <em>Recur</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecur()
	 * @generated
	 * @ordered
	 */
	protected boolean recur = RECUR_EDEFAULT;

	/**
	 * This is true if the Recur attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean recurESet;

	/**
	 * The default value of the '{@link #getRecurdelay() <em>Recurdelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecurdelay()
	 * @generated
	 * @ordered
	 */
	protected static final long RECURDELAY_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getRecurdelay() <em>Recurdelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecurdelay()
	 * @generated
	 * @ordered
	 */
	protected long recurdelay = RECURDELAY_EDEFAULT;

	/**
	 * This is true if the Recurdelay attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean recurdelayESet;

	/**
	 * The default value of the '{@link #isRetry() <em>Retry</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRetry()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RETRY_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isRetry() <em>Retry</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRetry()
	 * @generated
	 * @ordered
	 */
	protected boolean retry = RETRY_EDEFAULT;

	/**
	 * This is true if the Retry attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean retryESet;

	/**
	 * The default value of the '{@link #getRetrydelay() <em>Retrydelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetrydelay()
	 * @generated
	 * @ordered
	 */
	protected static final long RETRYDELAY_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getRetrydelay() <em>Retrydelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetrydelay()
	 * @generated
	 * @ordered
	 */
	protected long retrydelay = RETRYDELAY_EDEFAULT;

	/**
	 * This is true if the Retrydelay attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean retrydelayESet;

	/**
	 * The default value of the '{@link #isSequential() <em>Sequential</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSequential()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SEQUENTIAL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSequential() <em>Sequential</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSequential()
	 * @generated
	 * @ordered
	 */
	protected boolean sequential = SEQUENTIAL_EDEFAULT;

	/**
	 * This is true if the Sequential attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean sequentialESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GoalImpl()
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
		return GpmnPackage.Literals.GOAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUnique()
	{
		return unique;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUnique(String newUnique)
	{
		String oldUnique = unique;
		unique = newUnique;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__UNIQUE, oldUnique, unique));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCreationcondition()
	{
		return creationcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCreationcondition(String newCreationcondition)
	{
		String oldCreationcondition = creationcondition;
		creationcondition = newCreationcondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__CREATIONCONDITION, oldCreationcondition, creationcondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCreationconditionLanguage()
	{
		return creationconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCreationconditionLanguage(String newCreationconditionLanguage)
	{
		String oldCreationconditionLanguage = creationconditionLanguage;
		creationconditionLanguage = newCreationconditionLanguage;
		boolean oldCreationconditionLanguageESet = creationconditionLanguageESet;
		creationconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE, oldCreationconditionLanguage, creationconditionLanguage, !oldCreationconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetCreationconditionLanguage()
	{
		String oldCreationconditionLanguage = creationconditionLanguage;
		boolean oldCreationconditionLanguageESet = creationconditionLanguageESet;
		creationconditionLanguage = CREATIONCONDITION_LANGUAGE_EDEFAULT;
		creationconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE, oldCreationconditionLanguage, CREATIONCONDITION_LANGUAGE_EDEFAULT, oldCreationconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetCreationconditionLanguage()
	{
		return creationconditionLanguageESet;
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__CONTEXTCONDITION, oldContextcondition, contextcondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getContextconditionLanguage()
	{
		return contextconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContextconditionLanguage(String newContextconditionLanguage)
	{
		String oldContextconditionLanguage = contextconditionLanguage;
		contextconditionLanguage = newContextconditionLanguage;
		boolean oldContextconditionLanguageESet = contextconditionLanguageESet;
		contextconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE, oldContextconditionLanguage, contextconditionLanguage, !oldContextconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetContextconditionLanguage()
	{
		String oldContextconditionLanguage = contextconditionLanguage;
		boolean oldContextconditionLanguageESet = contextconditionLanguageESet;
		contextconditionLanguage = CONTEXTCONDITION_LANGUAGE_EDEFAULT;
		contextconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE, oldContextconditionLanguage, CONTEXTCONDITION_LANGUAGE_EDEFAULT, oldContextconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetContextconditionLanguage()
	{
		return contextconditionLanguageESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDropcondition()
	{
		return dropcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDropcondition(String newDropcondition)
	{
		String oldDropcondition = dropcondition;
		dropcondition = newDropcondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__DROPCONDITION, oldDropcondition, dropcondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDropconditionLanguage()
	{
		return dropconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDropconditionLanguage(String newDropconditionLanguage)
	{
		String oldDropconditionLanguage = dropconditionLanguage;
		dropconditionLanguage = newDropconditionLanguage;
		boolean oldDropconditionLanguageESet = dropconditionLanguageESet;
		dropconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__DROPCONDITION_LANGUAGE, oldDropconditionLanguage, dropconditionLanguage, !oldDropconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDropconditionLanguage()
	{
		String oldDropconditionLanguage = dropconditionLanguage;
		boolean oldDropconditionLanguageESet = dropconditionLanguageESet;
		dropconditionLanguage = DROPCONDITION_LANGUAGE_EDEFAULT;
		dropconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__DROPCONDITION_LANGUAGE, oldDropconditionLanguage, DROPCONDITION_LANGUAGE_EDEFAULT, oldDropconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDropconditionLanguage()
	{
		return dropconditionLanguageESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRecurcondition()
	{
		return recurcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecurcondition(String newRecurcondition)
	{
		String oldRecurcondition = recurcondition;
		recurcondition = newRecurcondition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RECURCONDITION, oldRecurcondition, recurcondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDeliberation()
	{
		return deliberation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeliberation(String newDeliberation)
	{
		String oldDeliberation = deliberation;
		deliberation = newDeliberation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__DELIBERATION, oldDeliberation, deliberation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getOnSuccessHandler()
	{
		if (onSuccessHandler == null)
		{
			onSuccessHandler = new EDataTypeEList<String>(String.class, this, GpmnPackage.GOAL__ON_SUCCESS_HANDLER);
		}
		return onSuccessHandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getOnSkipHandler()
	{
		if (onSkipHandler == null)
		{
			onSkipHandler = new EDataTypeEList<String>(String.class, this, GpmnPackage.GOAL__ON_SKIP_HANDLER);
		}
		return onSkipHandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getOnFailureHandler()
	{
		if (onFailureHandler == null)
		{
			onFailureHandler = new EDataTypeEList<String>(String.class, this, GpmnPackage.GOAL__ON_FAILURE_HANDLER);
		}
		return onFailureHandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExcludeType getExclude()
	{
		return exclude;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExclude(ExcludeType newExclude)
	{
		ExcludeType oldExclude = exclude;
		exclude = newExclude == null ? EXCLUDE_EDEFAULT : newExclude;
		boolean oldExcludeESet = excludeESet;
		excludeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__EXCLUDE, oldExclude, exclude, !oldExcludeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetExclude()
	{
		ExcludeType oldExclude = exclude;
		boolean oldExcludeESet = excludeESet;
		exclude = EXCLUDE_EDEFAULT;
		excludeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__EXCLUDE, oldExclude, EXCLUDE_EDEFAULT, oldExcludeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetExclude()
	{
		return excludeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GoalType getGoalType()
	{
		return goalType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGoalType(GoalType newGoalType)
	{
		GoalType oldGoalType = goalType;
		goalType = newGoalType == null ? GOAL_TYPE_EDEFAULT : newGoalType;
		boolean oldGoalTypeESet = goalTypeESet;
		goalTypeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__GOAL_TYPE, oldGoalType, goalType, !oldGoalTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetGoalType()
	{
		GoalType oldGoalType = goalType;
		boolean oldGoalTypeESet = goalTypeESet;
		goalType = GOAL_TYPE_EDEFAULT;
		goalTypeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__GOAL_TYPE, oldGoalType, GOAL_TYPE_EDEFAULT, oldGoalTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetGoalType()
	{
		return goalTypeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPosttoall()
	{
		return posttoall;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPosttoall(boolean newPosttoall)
	{
		boolean oldPosttoall = posttoall;
		posttoall = newPosttoall;
		boolean oldPosttoallESet = posttoallESet;
		posttoallESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__POSTTOALL, oldPosttoall, posttoall, !oldPosttoallESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPosttoall()
	{
		boolean oldPosttoall = posttoall;
		boolean oldPosttoallESet = posttoallESet;
		posttoall = POSTTOALL_EDEFAULT;
		posttoallESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__POSTTOALL, oldPosttoall, POSTTOALL_EDEFAULT, oldPosttoallESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPosttoall()
	{
		return posttoallESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRandomselection()
	{
		return randomselection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRandomselection(boolean newRandomselection)
	{
		boolean oldRandomselection = randomselection;
		randomselection = newRandomselection;
		boolean oldRandomselectionESet = randomselectionESet;
		randomselectionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RANDOMSELECTION, oldRandomselection, randomselection, !oldRandomselectionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRandomselection()
	{
		boolean oldRandomselection = randomselection;
		boolean oldRandomselectionESet = randomselectionESet;
		randomselection = RANDOMSELECTION_EDEFAULT;
		randomselectionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RANDOMSELECTION, oldRandomselection, RANDOMSELECTION_EDEFAULT, oldRandomselectionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRandomselection()
	{
		return randomselectionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRecalculate()
	{
		return recalculate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecalculate(boolean newRecalculate)
	{
		boolean oldRecalculate = recalculate;
		recalculate = newRecalculate;
		boolean oldRecalculateESet = recalculateESet;
		recalculateESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RECALCULATE, oldRecalculate, recalculate, !oldRecalculateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRecalculate()
	{
		boolean oldRecalculate = recalculate;
		boolean oldRecalculateESet = recalculateESet;
		recalculate = RECALCULATE_EDEFAULT;
		recalculateESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RECALCULATE, oldRecalculate, RECALCULATE_EDEFAULT, oldRecalculateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRecalculate()
	{
		return recalculateESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRecur()
	{
		return recur;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecur(boolean newRecur)
	{
		boolean oldRecur = recur;
		recur = newRecur;
		boolean oldRecurESet = recurESet;
		recurESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RECUR, oldRecur, recur, !oldRecurESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRecur()
	{
		boolean oldRecur = recur;
		boolean oldRecurESet = recurESet;
		recur = RECUR_EDEFAULT;
		recurESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RECUR, oldRecur, RECUR_EDEFAULT, oldRecurESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRecur()
	{
		return recurESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getRecurdelay()
	{
		return recurdelay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecurdelay(long newRecurdelay)
	{
		long oldRecurdelay = recurdelay;
		recurdelay = newRecurdelay;
		boolean oldRecurdelayESet = recurdelayESet;
		recurdelayESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RECURDELAY, oldRecurdelay, recurdelay, !oldRecurdelayESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRecurdelay()
	{
		long oldRecurdelay = recurdelay;
		boolean oldRecurdelayESet = recurdelayESet;
		recurdelay = RECURDELAY_EDEFAULT;
		recurdelayESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RECURDELAY, oldRecurdelay, RECURDELAY_EDEFAULT, oldRecurdelayESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRecurdelay()
	{
		return recurdelayESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRetry()
	{
		return retry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRetry(boolean newRetry)
	{
		boolean oldRetry = retry;
		retry = newRetry;
		boolean oldRetryESet = retryESet;
		retryESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RETRY, oldRetry, retry, !oldRetryESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRetry()
	{
		boolean oldRetry = retry;
		boolean oldRetryESet = retryESet;
		retry = RETRY_EDEFAULT;
		retryESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RETRY, oldRetry, RETRY_EDEFAULT, oldRetryESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRetry()
	{
		return retryESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getRetrydelay()
	{
		return retrydelay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRetrydelay(long newRetrydelay)
	{
		long oldRetrydelay = retrydelay;
		retrydelay = newRetrydelay;
		boolean oldRetrydelayESet = retrydelayESet;
		retrydelayESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RETRYDELAY, oldRetrydelay, retrydelay, !oldRetrydelayESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRetrydelay()
	{
		long oldRetrydelay = retrydelay;
		boolean oldRetrydelayESet = retrydelayESet;
		retrydelay = RETRYDELAY_EDEFAULT;
		retrydelayESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RETRYDELAY, oldRetrydelay, RETRYDELAY_EDEFAULT, oldRetrydelayESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRetrydelay()
	{
		return retrydelayESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSequential()
	{
		return sequential;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSequential(boolean newSequential)
	{
		boolean oldSequential = sequential;
		sequential = newSequential;
		boolean oldSequentialESet = sequentialESet;
		sequentialESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__SEQUENTIAL, oldSequential, sequential, !oldSequentialESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSequential()
	{
		boolean oldSequential = sequential;
		boolean oldSequentialESet = sequentialESet;
		sequential = SEQUENTIAL_EDEFAULT;
		sequentialESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__SEQUENTIAL, oldSequential, SEQUENTIAL_EDEFAULT, oldSequentialESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSequential()
	{
		return sequentialESet;
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
			case GpmnPackage.GOAL__UNIQUE:
				return getUnique();
			case GpmnPackage.GOAL__CREATIONCONDITION:
				return getCreationcondition();
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				return getCreationconditionLanguage();
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				return getContextcondition();
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				return getContextconditionLanguage();
			case GpmnPackage.GOAL__DROPCONDITION:
				return getDropcondition();
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				return getDropconditionLanguage();
			case GpmnPackage.GOAL__RECURCONDITION:
				return getRecurcondition();
			case GpmnPackage.GOAL__DELIBERATION:
				return getDeliberation();
			case GpmnPackage.GOAL__ON_SUCCESS_HANDLER:
				return getOnSuccessHandler();
			case GpmnPackage.GOAL__ON_SKIP_HANDLER:
				return getOnSkipHandler();
			case GpmnPackage.GOAL__ON_FAILURE_HANDLER:
				return getOnFailureHandler();
			case GpmnPackage.GOAL__EXCLUDE:
				return getExclude();
			case GpmnPackage.GOAL__GOAL_TYPE:
				return getGoalType();
			case GpmnPackage.GOAL__POSTTOALL:
				return isPosttoall();
			case GpmnPackage.GOAL__RANDOMSELECTION:
				return isRandomselection();
			case GpmnPackage.GOAL__RECALCULATE:
				return isRecalculate();
			case GpmnPackage.GOAL__RECUR:
				return isRecur();
			case GpmnPackage.GOAL__RECURDELAY:
				return getRecurdelay();
			case GpmnPackage.GOAL__RETRY:
				return isRetry();
			case GpmnPackage.GOAL__RETRYDELAY:
				return getRetrydelay();
			case GpmnPackage.GOAL__SEQUENTIAL:
				return isSequential();
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
			case GpmnPackage.GOAL__UNIQUE:
				setUnique((String)newValue);
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION:
				setCreationcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				setCreationconditionLanguage((String)newValue);
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				setContextcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				setContextconditionLanguage((String)newValue);
				return;
			case GpmnPackage.GOAL__DROPCONDITION:
				setDropcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				setDropconditionLanguage((String)newValue);
				return;
			case GpmnPackage.GOAL__RECURCONDITION:
				setRecurcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__DELIBERATION:
				setDeliberation((String)newValue);
				return;
			case GpmnPackage.GOAL__ON_SUCCESS_HANDLER:
				getOnSuccessHandler().clear();
				getOnSuccessHandler().addAll((Collection<? extends String>)newValue);
				return;
			case GpmnPackage.GOAL__ON_SKIP_HANDLER:
				getOnSkipHandler().clear();
				getOnSkipHandler().addAll((Collection<? extends String>)newValue);
				return;
			case GpmnPackage.GOAL__ON_FAILURE_HANDLER:
				getOnFailureHandler().clear();
				getOnFailureHandler().addAll((Collection<? extends String>)newValue);
				return;
			case GpmnPackage.GOAL__EXCLUDE:
				setExclude((ExcludeType)newValue);
				return;
			case GpmnPackage.GOAL__GOAL_TYPE:
				setGoalType((GoalType)newValue);
				return;
			case GpmnPackage.GOAL__POSTTOALL:
				setPosttoall((Boolean)newValue);
				return;
			case GpmnPackage.GOAL__RANDOMSELECTION:
				setRandomselection((Boolean)newValue);
				return;
			case GpmnPackage.GOAL__RECALCULATE:
				setRecalculate((Boolean)newValue);
				return;
			case GpmnPackage.GOAL__RECUR:
				setRecur((Boolean)newValue);
				return;
			case GpmnPackage.GOAL__RECURDELAY:
				setRecurdelay((Long)newValue);
				return;
			case GpmnPackage.GOAL__RETRY:
				setRetry((Boolean)newValue);
				return;
			case GpmnPackage.GOAL__RETRYDELAY:
				setRetrydelay((Long)newValue);
				return;
			case GpmnPackage.GOAL__SEQUENTIAL:
				setSequential((Boolean)newValue);
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
			case GpmnPackage.GOAL__UNIQUE:
				setUnique(UNIQUE_EDEFAULT);
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION:
				setCreationcondition(CREATIONCONDITION_EDEFAULT);
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				unsetCreationconditionLanguage();
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				setContextcondition(CONTEXTCONDITION_EDEFAULT);
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				unsetContextconditionLanguage();
				return;
			case GpmnPackage.GOAL__DROPCONDITION:
				setDropcondition(DROPCONDITION_EDEFAULT);
				return;
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				unsetDropconditionLanguage();
				return;
			case GpmnPackage.GOAL__RECURCONDITION:
				setRecurcondition(RECURCONDITION_EDEFAULT);
				return;
			case GpmnPackage.GOAL__DELIBERATION:
				setDeliberation(DELIBERATION_EDEFAULT);
				return;
			case GpmnPackage.GOAL__ON_SUCCESS_HANDLER:
				getOnSuccessHandler().clear();
				return;
			case GpmnPackage.GOAL__ON_SKIP_HANDLER:
				getOnSkipHandler().clear();
				return;
			case GpmnPackage.GOAL__ON_FAILURE_HANDLER:
				getOnFailureHandler().clear();
				return;
			case GpmnPackage.GOAL__EXCLUDE:
				unsetExclude();
				return;
			case GpmnPackage.GOAL__GOAL_TYPE:
				unsetGoalType();
				return;
			case GpmnPackage.GOAL__POSTTOALL:
				unsetPosttoall();
				return;
			case GpmnPackage.GOAL__RANDOMSELECTION:
				unsetRandomselection();
				return;
			case GpmnPackage.GOAL__RECALCULATE:
				unsetRecalculate();
				return;
			case GpmnPackage.GOAL__RECUR:
				unsetRecur();
				return;
			case GpmnPackage.GOAL__RECURDELAY:
				unsetRecurdelay();
				return;
			case GpmnPackage.GOAL__RETRY:
				unsetRetry();
				return;
			case GpmnPackage.GOAL__RETRYDELAY:
				unsetRetrydelay();
				return;
			case GpmnPackage.GOAL__SEQUENTIAL:
				unsetSequential();
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
			case GpmnPackage.GOAL__UNIQUE:
				return UNIQUE_EDEFAULT == null ? unique != null : !UNIQUE_EDEFAULT.equals(unique);
			case GpmnPackage.GOAL__CREATIONCONDITION:
				return CREATIONCONDITION_EDEFAULT == null ? creationcondition != null : !CREATIONCONDITION_EDEFAULT.equals(creationcondition);
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				return isSetCreationconditionLanguage();
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				return CONTEXTCONDITION_EDEFAULT == null ? contextcondition != null : !CONTEXTCONDITION_EDEFAULT.equals(contextcondition);
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				return isSetContextconditionLanguage();
			case GpmnPackage.GOAL__DROPCONDITION:
				return DROPCONDITION_EDEFAULT == null ? dropcondition != null : !DROPCONDITION_EDEFAULT.equals(dropcondition);
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				return isSetDropconditionLanguage();
			case GpmnPackage.GOAL__RECURCONDITION:
				return RECURCONDITION_EDEFAULT == null ? recurcondition != null : !RECURCONDITION_EDEFAULT.equals(recurcondition);
			case GpmnPackage.GOAL__DELIBERATION:
				return DELIBERATION_EDEFAULT == null ? deliberation != null : !DELIBERATION_EDEFAULT.equals(deliberation);
			case GpmnPackage.GOAL__ON_SUCCESS_HANDLER:
				return onSuccessHandler != null && !onSuccessHandler.isEmpty();
			case GpmnPackage.GOAL__ON_SKIP_HANDLER:
				return onSkipHandler != null && !onSkipHandler.isEmpty();
			case GpmnPackage.GOAL__ON_FAILURE_HANDLER:
				return onFailureHandler != null && !onFailureHandler.isEmpty();
			case GpmnPackage.GOAL__EXCLUDE:
				return isSetExclude();
			case GpmnPackage.GOAL__GOAL_TYPE:
				return isSetGoalType();
			case GpmnPackage.GOAL__POSTTOALL:
				return isSetPosttoall();
			case GpmnPackage.GOAL__RANDOMSELECTION:
				return isSetRandomselection();
			case GpmnPackage.GOAL__RECALCULATE:
				return isSetRecalculate();
			case GpmnPackage.GOAL__RECUR:
				return isSetRecur();
			case GpmnPackage.GOAL__RECURDELAY:
				return isSetRecurdelay();
			case GpmnPackage.GOAL__RETRY:
				return isSetRetry();
			case GpmnPackage.GOAL__RETRYDELAY:
				return isSetRetrydelay();
			case GpmnPackage.GOAL__SEQUENTIAL:
				return isSetSequential();
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
		result.append(" (unique: ");
		result.append(unique);
		result.append(", creationcondition: ");
		result.append(creationcondition);
		result.append(", creationconditionLanguage: ");
		if (creationconditionLanguageESet) result.append(creationconditionLanguage); else result.append("<unset>");
		result.append(", contextcondition: ");
		result.append(contextcondition);
		result.append(", contextconditionLanguage: ");
		if (contextconditionLanguageESet) result.append(contextconditionLanguage); else result.append("<unset>");
		result.append(", dropcondition: ");
		result.append(dropcondition);
		result.append(", dropconditionLanguage: ");
		if (dropconditionLanguageESet) result.append(dropconditionLanguage); else result.append("<unset>");
		result.append(", recurcondition: ");
		result.append(recurcondition);
		result.append(", deliberation: ");
		result.append(deliberation);
		result.append(", onSuccessHandler: ");
		result.append(onSuccessHandler);
		result.append(", onSkipHandler: ");
		result.append(onSkipHandler);
		result.append(", onFailureHandler: ");
		result.append(onFailureHandler);
		result.append(", exclude: ");
		if (excludeESet) result.append(exclude); else result.append("<unset>");
		result.append(", goalType: ");
		if (goalTypeESet) result.append(goalType); else result.append("<unset>");
		result.append(", posttoall: ");
		if (posttoallESet) result.append(posttoall); else result.append("<unset>");
		result.append(", randomselection: ");
		if (randomselectionESet) result.append(randomselection); else result.append("<unset>");
		result.append(", recalculate: ");
		if (recalculateESet) result.append(recalculate); else result.append("<unset>");
		result.append(", recur: ");
		if (recurESet) result.append(recur); else result.append("<unset>");
		result.append(", recurdelay: ");
		if (recurdelayESet) result.append(recurdelay); else result.append("<unset>");
		result.append(", retry: ");
		if (retryESet) result.append(retry); else result.append("<unset>");
		result.append(", retrydelay: ");
		if (retrydelayESet) result.append(retrydelay); else result.append("<unset>");
		result.append(", sequential: ");
		if (sequentialESet) result.append(sequential); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //GoalImpl
