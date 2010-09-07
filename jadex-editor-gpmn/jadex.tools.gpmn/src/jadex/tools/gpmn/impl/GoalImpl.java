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

import jadex.tools.gpmn.Activatable;
import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ConditionLanguage;
import jadex.tools.gpmn.ExcludeType;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.PlanEdge;

import jadex.tools.gpmn.SuppressionEdge;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Goal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getActivationEdges <em>Activation Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getPlanEdges <em>Plan Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getSuppressionEdge <em>Suppression Edge</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getUnique <em>Unique</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getCreationcondition <em>Creationcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getCreationconditionLanguage <em>Creationcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getContextcondition <em>Contextcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getContextconditionLanguage <em>Contextcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getDropcondition <em>Dropcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getDropconditionLanguage <em>Dropcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getRecurcondition <em>Recurcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getDeliberation <em>Deliberation</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getTargetcondition <em>Targetcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getFailurecondition <em>Failurecondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getFailureconditionLanguage <em>Failurecondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getMaintaincondition <em>Maintaincondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getMaintainconditionLanguage <em>Maintaincondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getExclude <em>Exclude</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getGoalType <em>Goal Type</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isPosttoall <em>Posttoall</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRandomselection <em>Randomselection</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRecalculate <em>Recalculate</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRecur <em>Recur</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getRecurdelay <em>Recurdelay</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#isRetry <em>Retry</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getRetrydelay <em>Retrydelay</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GoalImpl#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GoalImpl extends AbstractNodeImpl implements Goal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getActivationEdges() <em>Activation Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivationEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<ActivationEdge> activationEdges;

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
	 * The cached value of the '{@link #getSuppressionEdge() <em>Suppression Edge</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuppressionEdge()
	 * @generated
	 * @ordered
	 */
	protected EList<SuppressionEdge> suppressionEdge;

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
	 * This is true if the Unique attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean uniqueESet;

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
	 * This is true if the Creationcondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean creationconditionESet;

	/**
	 * The default value of the '{@link #getCreationconditionLanguage() <em>Creationcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage CREATIONCONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JCL;

	/**
	 * The cached value of the '{@link #getCreationconditionLanguage() <em>Creationcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreationconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage creationconditionLanguage = CREATIONCONDITION_LANGUAGE_EDEFAULT;

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
	 * This is true if the Contextcondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean contextconditionESet;

	/**
	 * The default value of the '{@link #getContextconditionLanguage() <em>Contextcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage CONTEXTCONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JCL;

	/**
	 * The cached value of the '{@link #getContextconditionLanguage() <em>Contextcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage contextconditionLanguage = CONTEXTCONDITION_LANGUAGE_EDEFAULT;

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
	 * This is true if the Dropcondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean dropconditionESet;

	/**
	 * The default value of the '{@link #getDropconditionLanguage() <em>Dropcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDropconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage DROPCONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JCL;

	/**
	 * The cached value of the '{@link #getDropconditionLanguage() <em>Dropcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDropconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage dropconditionLanguage = DROPCONDITION_LANGUAGE_EDEFAULT;

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
	 * This is true if the Recurcondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean recurconditionESet;

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
	 * This is true if the Deliberation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean deliberationESet;

	/**
	 * The default value of the '{@link #getTargetcondition() <em>Targetcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetcondition()
	 * @generated
	 * @ordered
	 */
	protected static final String TARGETCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTargetcondition() <em>Targetcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetcondition()
	 * @generated
	 * @ordered
	 */
	protected String targetcondition = TARGETCONDITION_EDEFAULT;

	/**
	 * This is true if the Targetcondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean targetconditionESet;

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
	 * The default value of the '{@link #getFailurecondition() <em>Failurecondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailurecondition()
	 * @generated
	 * @ordered
	 */
	protected static final String FAILURECONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFailurecondition() <em>Failurecondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailurecondition()
	 * @generated
	 * @ordered
	 */
	protected String failurecondition = FAILURECONDITION_EDEFAULT;

	/**
	 * This is true if the Failurecondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean failureconditionESet;

	/**
	 * The default value of the '{@link #getFailureconditionLanguage() <em>Failurecondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailureconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage FAILURECONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JCL;

	/**
	 * The cached value of the '{@link #getFailureconditionLanguage() <em>Failurecondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFailureconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage failureconditionLanguage = FAILURECONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Failurecondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean failureconditionLanguageESet;

	/**
	 * The default value of the '{@link #getMaintaincondition() <em>Maintaincondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintaincondition()
	 * @generated
	 * @ordered
	 */
	protected static final String MAINTAINCONDITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaintaincondition() <em>Maintaincondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintaincondition()
	 * @generated
	 * @ordered
	 */
	protected String maintaincondition = MAINTAINCONDITION_EDEFAULT;

	/**
	 * This is true if the Maintaincondition attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maintainconditionESet;

	/**
	 * The default value of the '{@link #getMaintainconditionLanguage() <em>Maintaincondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintainconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionLanguage MAINTAINCONDITION_LANGUAGE_EDEFAULT = ConditionLanguage.JCL;

	/**
	 * The cached value of the '{@link #getMaintainconditionLanguage() <em>Maintaincondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaintainconditionLanguage()
	 * @generated
	 * @ordered
	 */
	protected ConditionLanguage maintainconditionLanguage = MAINTAINCONDITION_LANGUAGE_EDEFAULT;

	/**
	 * This is true if the Maintaincondition Language attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maintainconditionLanguageESet;

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
	protected static final GoalType GOAL_TYPE_EDEFAULT = GoalType.MAINTAIN_GOAL;

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
	public EList<ActivationEdge> getActivationEdges()
	{
		if (activationEdges == null)
		{
			activationEdges = new EObjectWithInverseEList.Unsettable<ActivationEdge>(ActivationEdge.class, this, GpmnPackage.GOAL__ACTIVATION_EDGES, GpmnPackage.ACTIVATION_EDGE__TARGET);
		}
		return activationEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetActivationEdges()
	{
		if (activationEdges != null) ((InternalEList.Unsettable<?>)activationEdges).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetActivationEdges()
	{
		return activationEdges != null && ((InternalEList.Unsettable<?>)activationEdges).isSet();
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
			planEdges = new EObjectEList<PlanEdge>(PlanEdge.class, this, GpmnPackage.GOAL__PLAN_EDGES);
		}
		return planEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SuppressionEdge> getSuppressionEdge()
	{
		if (suppressionEdge == null)
		{
			suppressionEdge = new EObjectEList<SuppressionEdge>(SuppressionEdge.class, this, GpmnPackage.GOAL__SUPPRESSION_EDGE);
		}
		return suppressionEdge;
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
		boolean oldUniqueESet = uniqueESet;
		uniqueESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__UNIQUE, oldUnique, unique, !oldUniqueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetUnique()
	{
		String oldUnique = unique;
		boolean oldUniqueESet = uniqueESet;
		unique = UNIQUE_EDEFAULT;
		uniqueESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__UNIQUE, oldUnique, UNIQUE_EDEFAULT, oldUniqueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetUnique()
	{
		return uniqueESet;
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
		boolean oldCreationconditionESet = creationconditionESet;
		creationconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__CREATIONCONDITION, oldCreationcondition, creationcondition, !oldCreationconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetCreationcondition()
	{
		String oldCreationcondition = creationcondition;
		boolean oldCreationconditionESet = creationconditionESet;
		creationcondition = CREATIONCONDITION_EDEFAULT;
		creationconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__CREATIONCONDITION, oldCreationcondition, CREATIONCONDITION_EDEFAULT, oldCreationconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetCreationcondition()
	{
		return creationconditionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage getCreationconditionLanguage()
	{
		return creationconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCreationconditionLanguage(
			ConditionLanguage newCreationconditionLanguage)
	{
		ConditionLanguage oldCreationconditionLanguage = creationconditionLanguage;
		creationconditionLanguage = newCreationconditionLanguage == null ? CREATIONCONDITION_LANGUAGE_EDEFAULT : newCreationconditionLanguage;
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
		ConditionLanguage oldCreationconditionLanguage = creationconditionLanguage;
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
		boolean oldContextconditionESet = contextconditionESet;
		contextconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__CONTEXTCONDITION, oldContextcondition, contextcondition, !oldContextconditionESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__CONTEXTCONDITION, oldContextcondition, CONTEXTCONDITION_EDEFAULT, oldContextconditionESet));
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
	public ConditionLanguage getContextconditionLanguage()
	{
		return contextconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContextconditionLanguage(
			ConditionLanguage newContextconditionLanguage)
	{
		ConditionLanguage oldContextconditionLanguage = contextconditionLanguage;
		contextconditionLanguage = newContextconditionLanguage == null ? CONTEXTCONDITION_LANGUAGE_EDEFAULT : newContextconditionLanguage;
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
		ConditionLanguage oldContextconditionLanguage = contextconditionLanguage;
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
		boolean oldDropconditionESet = dropconditionESet;
		dropconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__DROPCONDITION, oldDropcondition, dropcondition, !oldDropconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDropcondition()
	{
		String oldDropcondition = dropcondition;
		boolean oldDropconditionESet = dropconditionESet;
		dropcondition = DROPCONDITION_EDEFAULT;
		dropconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__DROPCONDITION, oldDropcondition, DROPCONDITION_EDEFAULT, oldDropconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDropcondition()
	{
		return dropconditionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage getDropconditionLanguage()
	{
		return dropconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDropconditionLanguage(
			ConditionLanguage newDropconditionLanguage)
	{
		ConditionLanguage oldDropconditionLanguage = dropconditionLanguage;
		dropconditionLanguage = newDropconditionLanguage == null ? DROPCONDITION_LANGUAGE_EDEFAULT : newDropconditionLanguage;
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
		ConditionLanguage oldDropconditionLanguage = dropconditionLanguage;
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
		boolean oldRecurconditionESet = recurconditionESet;
		recurconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__RECURCONDITION, oldRecurcondition, recurcondition, !oldRecurconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRecurcondition()
	{
		String oldRecurcondition = recurcondition;
		boolean oldRecurconditionESet = recurconditionESet;
		recurcondition = RECURCONDITION_EDEFAULT;
		recurconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__RECURCONDITION, oldRecurcondition, RECURCONDITION_EDEFAULT, oldRecurconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRecurcondition()
	{
		return recurconditionESet;
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
		boolean oldDeliberationESet = deliberationESet;
		deliberationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__DELIBERATION, oldDeliberation, deliberation, !oldDeliberationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDeliberation()
	{
		String oldDeliberation = deliberation;
		boolean oldDeliberationESet = deliberationESet;
		deliberation = DELIBERATION_EDEFAULT;
		deliberationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__DELIBERATION, oldDeliberation, DELIBERATION_EDEFAULT, oldDeliberationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDeliberation()
	{
		return deliberationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTargetcondition()
	{
		return targetcondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetcondition(String newTargetcondition)
	{
		String oldTargetcondition = targetcondition;
		targetcondition = newTargetcondition;
		boolean oldTargetconditionESet = targetconditionESet;
		targetconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__TARGETCONDITION, oldTargetcondition, targetcondition, !oldTargetconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTargetcondition()
	{
		String oldTargetcondition = targetcondition;
		boolean oldTargetconditionESet = targetconditionESet;
		targetcondition = TARGETCONDITION_EDEFAULT;
		targetconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__TARGETCONDITION, oldTargetcondition, TARGETCONDITION_EDEFAULT, oldTargetconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTargetcondition()
	{
		return targetconditionESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, targetconditionLanguage, !oldTargetconditionLanguageESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__TARGETCONDITION_LANGUAGE, oldTargetconditionLanguage, TARGETCONDITION_LANGUAGE_EDEFAULT, oldTargetconditionLanguageESet));
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
	public String getFailurecondition()
	{
		return failurecondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFailurecondition(String newFailurecondition)
	{
		String oldFailurecondition = failurecondition;
		failurecondition = newFailurecondition;
		boolean oldFailureconditionESet = failureconditionESet;
		failureconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__FAILURECONDITION, oldFailurecondition, failurecondition, !oldFailureconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetFailurecondition()
	{
		String oldFailurecondition = failurecondition;
		boolean oldFailureconditionESet = failureconditionESet;
		failurecondition = FAILURECONDITION_EDEFAULT;
		failureconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__FAILURECONDITION, oldFailurecondition, FAILURECONDITION_EDEFAULT, oldFailureconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetFailurecondition()
	{
		return failureconditionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage getFailureconditionLanguage()
	{
		return failureconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFailureconditionLanguage(
			ConditionLanguage newFailureconditionLanguage)
	{
		ConditionLanguage oldFailureconditionLanguage = failureconditionLanguage;
		failureconditionLanguage = newFailureconditionLanguage == null ? FAILURECONDITION_LANGUAGE_EDEFAULT : newFailureconditionLanguage;
		boolean oldFailureconditionLanguageESet = failureconditionLanguageESet;
		failureconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__FAILURECONDITION_LANGUAGE, oldFailureconditionLanguage, failureconditionLanguage, !oldFailureconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetFailureconditionLanguage()
	{
		ConditionLanguage oldFailureconditionLanguage = failureconditionLanguage;
		boolean oldFailureconditionLanguageESet = failureconditionLanguageESet;
		failureconditionLanguage = FAILURECONDITION_LANGUAGE_EDEFAULT;
		failureconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__FAILURECONDITION_LANGUAGE, oldFailureconditionLanguage, FAILURECONDITION_LANGUAGE_EDEFAULT, oldFailureconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetFailureconditionLanguage()
	{
		return failureconditionLanguageESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMaintaincondition()
	{
		return maintaincondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaintaincondition(String newMaintaincondition)
	{
		String oldMaintaincondition = maintaincondition;
		maintaincondition = newMaintaincondition;
		boolean oldMaintainconditionESet = maintainconditionESet;
		maintainconditionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__MAINTAINCONDITION, oldMaintaincondition, maintaincondition, !oldMaintainconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetMaintaincondition()
	{
		String oldMaintaincondition = maintaincondition;
		boolean oldMaintainconditionESet = maintainconditionESet;
		maintaincondition = MAINTAINCONDITION_EDEFAULT;
		maintainconditionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__MAINTAINCONDITION, oldMaintaincondition, MAINTAINCONDITION_EDEFAULT, oldMaintainconditionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetMaintaincondition()
	{
		return maintainconditionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage getMaintainconditionLanguage()
	{
		return maintainconditionLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaintainconditionLanguage(
			ConditionLanguage newMaintainconditionLanguage)
	{
		ConditionLanguage oldMaintainconditionLanguage = maintainconditionLanguage;
		maintainconditionLanguage = newMaintainconditionLanguage == null ? MAINTAINCONDITION_LANGUAGE_EDEFAULT : newMaintainconditionLanguage;
		boolean oldMaintainconditionLanguageESet = maintainconditionLanguageESet;
		maintainconditionLanguageESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__MAINTAINCONDITION_LANGUAGE, oldMaintainconditionLanguage, maintainconditionLanguage, !oldMaintainconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetMaintainconditionLanguage()
	{
		ConditionLanguage oldMaintainconditionLanguage = maintainconditionLanguage;
		boolean oldMaintainconditionLanguageESet = maintainconditionLanguageESet;
		maintainconditionLanguage = MAINTAINCONDITION_LANGUAGE_EDEFAULT;
		maintainconditionLanguageESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GOAL__MAINTAINCONDITION_LANGUAGE, oldMaintainconditionLanguage, MAINTAINCONDITION_LANGUAGE_EDEFAULT, oldMaintainconditionLanguageESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetMaintainconditionLanguage()
	{
		return maintainconditionLanguageESet;
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
	public GpmnDiagram getGpmnDiagram()
	{
		if (eContainerFeatureID() != GpmnPackage.GOAL__GPMN_DIAGRAM) return null;
		return (GpmnDiagram)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGpmnDiagram(GpmnDiagram newGpmnDiagram, NotificationChain msgs)
	{
		msgs = eBasicSetContainer((InternalEObject)newGpmnDiagram, GpmnPackage.GOAL__GPMN_DIAGRAM, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGpmnDiagram(GpmnDiagram newGpmnDiagram)
	{
		if (newGpmnDiagram != eInternalContainer() || (eContainerFeatureID() != GpmnPackage.GOAL__GPMN_DIAGRAM && newGpmnDiagram != null))
		{
			if (EcoreUtil.isAncestor(this, newGpmnDiagram))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newGpmnDiagram != null)
				msgs = ((InternalEObject)newGpmnDiagram).eInverseAdd(this, GpmnPackage.GPMN_DIAGRAM__GOALS, GpmnDiagram.class, msgs);
			msgs = basicSetGpmnDiagram(newGpmnDiagram, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GOAL__GPMN_DIAGRAM, newGpmnDiagram, newGpmnDiagram));
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
			case GpmnPackage.GOAL__ACTIVATION_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getActivationEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
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
			case GpmnPackage.GOAL__ACTIVATION_EDGES:
				return ((InternalEList<?>)getActivationEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
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
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
				return eInternalContainer().eInverseRemove(this, GpmnPackage.GPMN_DIAGRAM__GOALS, GpmnDiagram.class, msgs);
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
			case GpmnPackage.GOAL__ACTIVATION_EDGES:
				return getActivationEdges();
			case GpmnPackage.GOAL__PLAN_EDGES:
				return getPlanEdges();
			case GpmnPackage.GOAL__SUPPRESSION_EDGE:
				return getSuppressionEdge();
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
			case GpmnPackage.GOAL__TARGETCONDITION:
				return getTargetcondition();
			case GpmnPackage.GOAL__TARGETCONDITION_LANGUAGE:
				return getTargetconditionLanguage();
			case GpmnPackage.GOAL__FAILURECONDITION:
				return getFailurecondition();
			case GpmnPackage.GOAL__FAILURECONDITION_LANGUAGE:
				return getFailureconditionLanguage();
			case GpmnPackage.GOAL__MAINTAINCONDITION:
				return getMaintaincondition();
			case GpmnPackage.GOAL__MAINTAINCONDITION_LANGUAGE:
				return getMaintainconditionLanguage();
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
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
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
			case GpmnPackage.GOAL__ACTIVATION_EDGES:
				getActivationEdges().clear();
				getActivationEdges().addAll((Collection<? extends ActivationEdge>)newValue);
				return;
			case GpmnPackage.GOAL__PLAN_EDGES:
				getPlanEdges().clear();
				getPlanEdges().addAll((Collection<? extends PlanEdge>)newValue);
				return;
			case GpmnPackage.GOAL__SUPPRESSION_EDGE:
				getSuppressionEdge().clear();
				getSuppressionEdge().addAll((Collection<? extends SuppressionEdge>)newValue);
				return;
			case GpmnPackage.GOAL__UNIQUE:
				setUnique((String)newValue);
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION:
				setCreationcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				setCreationconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				setContextcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				setContextconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.GOAL__DROPCONDITION:
				setDropcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				setDropconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.GOAL__RECURCONDITION:
				setRecurcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__DELIBERATION:
				setDeliberation((String)newValue);
				return;
			case GpmnPackage.GOAL__TARGETCONDITION:
				setTargetcondition((String)newValue);
				return;
			case GpmnPackage.GOAL__TARGETCONDITION_LANGUAGE:
				setTargetconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.GOAL__FAILURECONDITION:
				setFailurecondition((String)newValue);
				return;
			case GpmnPackage.GOAL__FAILURECONDITION_LANGUAGE:
				setFailureconditionLanguage((ConditionLanguage)newValue);
				return;
			case GpmnPackage.GOAL__MAINTAINCONDITION:
				setMaintaincondition((String)newValue);
				return;
			case GpmnPackage.GOAL__MAINTAINCONDITION_LANGUAGE:
				setMaintainconditionLanguage((ConditionLanguage)newValue);
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
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
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
			case GpmnPackage.GOAL__ACTIVATION_EDGES:
				unsetActivationEdges();
				return;
			case GpmnPackage.GOAL__PLAN_EDGES:
				getPlanEdges().clear();
				return;
			case GpmnPackage.GOAL__SUPPRESSION_EDGE:
				getSuppressionEdge().clear();
				return;
			case GpmnPackage.GOAL__UNIQUE:
				unsetUnique();
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION:
				unsetCreationcondition();
				return;
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				unsetCreationconditionLanguage();
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				unsetContextcondition();
				return;
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				unsetContextconditionLanguage();
				return;
			case GpmnPackage.GOAL__DROPCONDITION:
				unsetDropcondition();
				return;
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				unsetDropconditionLanguage();
				return;
			case GpmnPackage.GOAL__RECURCONDITION:
				unsetRecurcondition();
				return;
			case GpmnPackage.GOAL__DELIBERATION:
				unsetDeliberation();
				return;
			case GpmnPackage.GOAL__TARGETCONDITION:
				unsetTargetcondition();
				return;
			case GpmnPackage.GOAL__TARGETCONDITION_LANGUAGE:
				unsetTargetconditionLanguage();
				return;
			case GpmnPackage.GOAL__FAILURECONDITION:
				unsetFailurecondition();
				return;
			case GpmnPackage.GOAL__FAILURECONDITION_LANGUAGE:
				unsetFailureconditionLanguage();
				return;
			case GpmnPackage.GOAL__MAINTAINCONDITION:
				unsetMaintaincondition();
				return;
			case GpmnPackage.GOAL__MAINTAINCONDITION_LANGUAGE:
				unsetMaintainconditionLanguage();
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
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
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
			case GpmnPackage.GOAL__ACTIVATION_EDGES:
				return isSetActivationEdges();
			case GpmnPackage.GOAL__PLAN_EDGES:
				return planEdges != null && !planEdges.isEmpty();
			case GpmnPackage.GOAL__SUPPRESSION_EDGE:
				return suppressionEdge != null && !suppressionEdge.isEmpty();
			case GpmnPackage.GOAL__UNIQUE:
				return isSetUnique();
			case GpmnPackage.GOAL__CREATIONCONDITION:
				return isSetCreationcondition();
			case GpmnPackage.GOAL__CREATIONCONDITION_LANGUAGE:
				return isSetCreationconditionLanguage();
			case GpmnPackage.GOAL__CONTEXTCONDITION:
				return isSetContextcondition();
			case GpmnPackage.GOAL__CONTEXTCONDITION_LANGUAGE:
				return isSetContextconditionLanguage();
			case GpmnPackage.GOAL__DROPCONDITION:
				return isSetDropcondition();
			case GpmnPackage.GOAL__DROPCONDITION_LANGUAGE:
				return isSetDropconditionLanguage();
			case GpmnPackage.GOAL__RECURCONDITION:
				return isSetRecurcondition();
			case GpmnPackage.GOAL__DELIBERATION:
				return isSetDeliberation();
			case GpmnPackage.GOAL__TARGETCONDITION:
				return isSetTargetcondition();
			case GpmnPackage.GOAL__TARGETCONDITION_LANGUAGE:
				return isSetTargetconditionLanguage();
			case GpmnPackage.GOAL__FAILURECONDITION:
				return isSetFailurecondition();
			case GpmnPackage.GOAL__FAILURECONDITION_LANGUAGE:
				return isSetFailureconditionLanguage();
			case GpmnPackage.GOAL__MAINTAINCONDITION:
				return isSetMaintaincondition();
			case GpmnPackage.GOAL__MAINTAINCONDITION_LANGUAGE:
				return isSetMaintainconditionLanguage();
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
			case GpmnPackage.GOAL__GPMN_DIAGRAM:
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
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
	{
		if (baseClass == Activatable.class)
		{
			switch (derivedFeatureID)
			{
				case GpmnPackage.GOAL__ACTIVATION_EDGES: return GpmnPackage.ACTIVATABLE__ACTIVATION_EDGES;
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
		if (baseClass == Activatable.class)
		{
			switch (baseFeatureID)
			{
				case GpmnPackage.ACTIVATABLE__ACTIVATION_EDGES: return GpmnPackage.GOAL__ACTIVATION_EDGES;
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
		result.append(" (unique: "); //$NON-NLS-1$
		if (uniqueESet) result.append(unique); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", creationcondition: "); //$NON-NLS-1$
		if (creationconditionESet) result.append(creationcondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", creationconditionLanguage: "); //$NON-NLS-1$
		if (creationconditionLanguageESet) result.append(creationconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", contextcondition: "); //$NON-NLS-1$
		if (contextconditionESet) result.append(contextcondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", contextconditionLanguage: "); //$NON-NLS-1$
		if (contextconditionLanguageESet) result.append(contextconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", dropcondition: "); //$NON-NLS-1$
		if (dropconditionESet) result.append(dropcondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", dropconditionLanguage: "); //$NON-NLS-1$
		if (dropconditionLanguageESet) result.append(dropconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", recurcondition: "); //$NON-NLS-1$
		if (recurconditionESet) result.append(recurcondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", deliberation: "); //$NON-NLS-1$
		if (deliberationESet) result.append(deliberation); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", targetcondition: "); //$NON-NLS-1$
		if (targetconditionESet) result.append(targetcondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", targetconditionLanguage: "); //$NON-NLS-1$
		if (targetconditionLanguageESet) result.append(targetconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", failurecondition: "); //$NON-NLS-1$
		if (failureconditionESet) result.append(failurecondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", failureconditionLanguage: "); //$NON-NLS-1$
		if (failureconditionLanguageESet) result.append(failureconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", maintaincondition: "); //$NON-NLS-1$
		if (maintainconditionESet) result.append(maintaincondition); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", maintainconditionLanguage: "); //$NON-NLS-1$
		if (maintainconditionLanguageESet) result.append(maintainconditionLanguage); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", exclude: "); //$NON-NLS-1$
		if (excludeESet) result.append(exclude); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", goalType: "); //$NON-NLS-1$
		if (goalTypeESet) result.append(goalType); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", posttoall: "); //$NON-NLS-1$
		if (posttoallESet) result.append(posttoall); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", randomselection: "); //$NON-NLS-1$
		if (randomselectionESet) result.append(randomselection); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", recalculate: "); //$NON-NLS-1$
		if (recalculateESet) result.append(recalculate); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", recur: "); //$NON-NLS-1$
		if (recurESet) result.append(recur); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", recurdelay: "); //$NON-NLS-1$
		if (recurdelayESet) result.append(recurdelay); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", retry: "); //$NON-NLS-1$
		if (retryESet) result.append(retry); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", retrydelay: "); //$NON-NLS-1$
		if (retrydelayESet) result.append(retrydelay); else result.append("<unset>"); //$NON-NLS-1$
		result.append(')');
		return result.toString();
	}

} //GoalImpl
