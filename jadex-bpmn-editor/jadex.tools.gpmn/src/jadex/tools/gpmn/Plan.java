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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Plan</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Plan#getBpmnPlan <em>Bpmn Plan</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Plan#getPriority <em>Priority</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Plan#getPrecondition <em>Precondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Plan#getContextcondition <em>Contextcondition</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getPlan()
 * @model extendedMetaData="name='Plan' kind='elementOnly'"
 * @generated
 */
public interface Plan extends ParameterizedVertex
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Bpmn Plan</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bpmn Plan</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bpmn Plan</em>' attribute.
	 * @see #setBpmnPlan(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getPlan_BpmnPlan()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='bpmnPlan'"
	 * @generated
	 */
	String getBpmnPlan();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Plan#getBpmnPlan <em>Bpmn Plan</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bpmn Plan</em>' attribute.
	 * @see #getBpmnPlan()
	 * @generated
	 */
	void setBpmnPlan(String value);

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
	 * @see jadex.tools.gpmn.GpmnPackage#getPlan_Priority()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='priority'"
	 * @generated
	 */
	int getPriority();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Plan#getPriority <em>Priority</em>}' attribute.
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
	 * Unsets the value of the '{@link jadex.tools.gpmn.Plan#getPriority <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPriority()
	 * @see #getPriority()
	 * @see #setPriority(int)
	 * @generated
	 */
	void unsetPriority();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Plan#getPriority <em>Priority</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Precondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Precondition</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Precondition</em>' attribute.
	 * @see #setPrecondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getPlan_Precondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='precondition'"
	 * @generated
	 */
	String getPrecondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Plan#getPrecondition <em>Precondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Precondition</em>' attribute.
	 * @see #getPrecondition()
	 * @generated
	 */
	void setPrecondition(String value);

	/**
	 * Returns the value of the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Contextcondition</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contextcondition</em>' attribute.
	 * @see #setContextcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getPlan_Contextcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='contextcondition'"
	 * @generated
	 */
	String getContextcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Plan#getContextcondition <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contextcondition</em>' attribute.
	 * @see #getContextcondition()
	 * @generated
	 */
	void setContextcondition(String value);

} // Plan
