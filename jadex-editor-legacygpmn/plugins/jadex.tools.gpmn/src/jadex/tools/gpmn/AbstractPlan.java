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
package jadex.tools.gpmn;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Plan</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getPlanEdges <em>Plan Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getContextcondition <em>Contextcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getPrecondition <em>Precondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getPreconditionLanguage <em>Precondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getPriority <em>Priority</em>}</li>
 *   <li>{@link jadex.tools.gpmn.AbstractPlan#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan()
 * @model abstract="true"
 *        extendedMetaData="name='AbstractPlan' kind='elementOnly'"
 * @generated
 */
public interface AbstractPlan extends AbstractNode
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Plan Edges</b></em>' reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.PlanEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.PlanEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plan Edges</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Plan Edges</em>' reference list.
	 * @see #isSetPlanEdges()
	 * @see #unsetPlanEdges()
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_PlanEdges()
	 * @see jadex.tools.gpmn.PlanEdge#getTarget
	 * @model opposite="target" resolveProxies="false" unsettable="true" transient="true"
	 *        extendedMetaData="kind='element' name='planEdges'"
	 * @generated
	 */
	EList<PlanEdge> getPlanEdges();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPlanEdges <em>Plan Edges</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPlanEdges()
	 * @see #getPlanEdges()
	 * @generated
	 */
	void unsetPlanEdges();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPlanEdges <em>Plan Edges</em>}' reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Plan Edges</em>' reference list is set.
	 * @see #unsetPlanEdges()
	 * @see #getPlanEdges()
	 * @generated
	 */
	boolean isSetPlanEdges();

	/**
	 * Returns the value of the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A context condition.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Contextcondition</em>' attribute.
	 * @see #isSetContextcondition()
	 * @see #unsetContextcondition()
	 * @see #setContextcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_Contextcondition()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='contextcondition'"
	 * @generated
	 */
	String getContextcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getContextcondition <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contextcondition</em>' attribute.
	 * @see #isSetContextcondition()
	 * @see #unsetContextcondition()
	 * @see #getContextcondition()
	 * @generated
	 */
	void setContextcondition(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getContextcondition <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetContextcondition()
	 * @see #getContextcondition()
	 * @see #setContextcondition(String)
	 * @generated
	 */
	void unsetContextcondition();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractPlan#getContextcondition <em>Contextcondition</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Contextcondition</em>' attribute is set.
	 * @see #unsetContextcondition()
	 * @see #getContextcondition()
	 * @see #setContextcondition(String)
	 * @generated
	 */
	boolean isSetContextcondition();

	/**
	 * Returns the value of the '<em><b>Targetcondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * The literals are from the enumeration {@link jadex.tools.gpmn.ConditionLanguage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Targetcondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Targetcondition Language</em>' attribute.
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @see #isSetTargetconditionLanguage()
	 * @see #unsetTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(ConditionLanguage)
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_TargetconditionLanguage()
	 * @model default="jcl" unsettable="true"
	 *        extendedMetaData="kind='element' name='targetcondition_language'"
	 * @generated
	 */
	ConditionLanguage getTargetconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Targetcondition Language</em>' attribute.
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @see #isSetTargetconditionLanguage()
	 * @see #unsetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @generated
	 */
	void setTargetconditionLanguage(ConditionLanguage value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(ConditionLanguage)
	 * @generated
	 */
	void unsetTargetconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractPlan#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Targetcondition Language</em>' attribute is set.
	 * @see #unsetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(ConditionLanguage)
	 * @generated
	 */
	boolean isSetTargetconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Precondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A precondition.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Precondition</em>' attribute.
	 * @see #isSetPrecondition()
	 * @see #unsetPrecondition()
	 * @see #setPrecondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_Precondition()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='precondition'"
	 * @generated
	 */
	String getPrecondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPrecondition <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Precondition</em>' attribute.
	 * @see #isSetPrecondition()
	 * @see #unsetPrecondition()
	 * @see #getPrecondition()
	 * @generated
	 */
	void setPrecondition(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPrecondition <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPrecondition()
	 * @see #getPrecondition()
	 * @see #setPrecondition(String)
	 * @generated
	 */
	void unsetPrecondition();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPrecondition <em>Precondition</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Precondition</em>' attribute is set.
	 * @see #unsetPrecondition()
	 * @see #getPrecondition()
	 * @see #setPrecondition(String)
	 * @generated
	 */
	boolean isSetPrecondition();

	/**
	 * Returns the value of the '<em><b>Precondition Language</b></em>' attribute.
	 * The default value is <code>"java"</code>.
	 * The literals are from the enumeration {@link jadex.tools.gpmn.ConditionLanguage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Precondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Precondition Language</em>' attribute.
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @see #isSetPreconditionLanguage()
	 * @see #unsetPreconditionLanguage()
	 * @see #setPreconditionLanguage(ConditionLanguage)
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_PreconditionLanguage()
	 * @model default="java" unsettable="true"
	 *        extendedMetaData="kind='element' name='precondition_language'"
	 * @generated
	 */
	ConditionLanguage getPreconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPreconditionLanguage <em>Precondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Precondition Language</em>' attribute.
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @see #isSetPreconditionLanguage()
	 * @see #unsetPreconditionLanguage()
	 * @see #getPreconditionLanguage()
	 * @generated
	 */
	void setPreconditionLanguage(ConditionLanguage value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPreconditionLanguage <em>Precondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPreconditionLanguage()
	 * @see #getPreconditionLanguage()
	 * @see #setPreconditionLanguage(ConditionLanguage)
	 * @generated
	 */
	void unsetPreconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPreconditionLanguage <em>Precondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Precondition Language</em>' attribute is set.
	 * @see #unsetPreconditionLanguage()
	 * @see #getPreconditionLanguage()
	 * @see #setPreconditionLanguage(ConditionLanguage)
	 * @generated
	 */
	boolean isSetPreconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Priority</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The priority can be used for controlling the plan selection process (default priority is 0). Plans with higher priority have precedence for plans with lower priority.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Priority</em>' attribute.
	 * @see #isSetPriority()
	 * @see #unsetPriority()
	 * @see #setPriority(int)
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_Priority()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='priority'"
	 * @generated
	 */
	int getPriority();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPriority <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Priority</em>' attribute.
	 * @see #isSetPriority()
	 * @see #unsetPriority()
	 * @see #getPriority()
	 * @generated
	 */
	void setPriority(int value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPriority <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPriority()
	 * @see #getPriority()
	 * @see #setPriority(int)
	 * @generated
	 */
	void unsetPriority();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.AbstractPlan#getPriority <em>Priority</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Priority</em>' attribute is set.
	 * @see #unsetPriority()
	 * @see #getPriority()
	 * @see #setPriority(int)
	 * @generated
	 */
	boolean isSetPriority();

	/**
	 * Returns the value of the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.GpmnDiagram#getPlans <em>Plans</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gpmn Diagram</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #setGpmnDiagram(GpmnDiagram)
	 * @see jadex.tools.gpmn.GpmnPackage#getAbstractPlan_GpmnDiagram()
	 * @see jadex.tools.gpmn.GpmnDiagram#getPlans
	 * @model opposite="plans"
	 * @generated
	 */
	GpmnDiagram getGpmnDiagram();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.AbstractPlan#getGpmnDiagram <em>Gpmn Diagram</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	void setGpmnDiagram(GpmnDiagram value);

} // AbstractPlan
