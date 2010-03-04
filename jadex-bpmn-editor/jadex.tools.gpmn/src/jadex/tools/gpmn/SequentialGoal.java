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
 * A representation of the model object '<em><b>Sequential Goal</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.SequentialGoal#getTargetcondition <em>Targetcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.SequentialGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.SequentialGoal#getFailurecondition <em>Failurecondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.SequentialGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getSequentialGoal()
 * @model extendedMetaData="name='SequentialGoal' kind='elementOnly'"
 * @generated
 */
public interface SequentialGoal extends Goal
{

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The target condition can be used to specify a desired world state representing goal success.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Targetcondition</em>' attribute.
	 * @see #setTargetcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getSequentialGoal_Targetcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='targetcondition'"
	 * @generated
	 */
	String getTargetcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SequentialGoal#getTargetcondition <em>Targetcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Targetcondition</em>' attribute.
	 * @see #getTargetcondition()
	 * @generated
	 */
	void setTargetcondition(String value);

	/**
	 * Returns the value of the '<em><b>Targetcondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Targetcondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Targetcondition Language</em>' attribute.
	 * @see #isSetTargetconditionLanguage()
	 * @see #unsetTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getSequentialGoal_TargetconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='targetcondition_language'"
	 * @generated
	 */
	String getTargetconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SequentialGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Targetcondition Language</em>' attribute.
	 * @see #isSetTargetconditionLanguage()
	 * @see #unsetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @generated
	 */
	void setTargetconditionLanguage(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.SequentialGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(String)
	 * @generated
	 */
	void unsetTargetconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.SequentialGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Targetcondition Language</em>' attribute is set.
	 * @see #unsetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(String)
	 * @generated
	 */
	boolean isSetTargetconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Failurecondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Can be used to explicitly state when a goal cannot be pursued any longer and is failed. 
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Failurecondition</em>' attribute.
	 * @see #setFailurecondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getSequentialGoal_Failurecondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='failurecondition'"
	 * @generated
	 */
	String getFailurecondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SequentialGoal#getFailurecondition <em>Failurecondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Failurecondition</em>' attribute.
	 * @see #getFailurecondition()
	 * @generated
	 */
	void setFailurecondition(String value);

	/**
	 * Returns the value of the '<em><b>Failurecondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Failurecondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Failurecondition Language</em>' attribute.
	 * @see #isSetFailureconditionLanguage()
	 * @see #unsetFailureconditionLanguage()
	 * @see #setFailureconditionLanguage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getSequentialGoal_FailureconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='failurecondition_language'"
	 * @generated
	 */
	String getFailureconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SequentialGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Failurecondition Language</em>' attribute.
	 * @see #isSetFailureconditionLanguage()
	 * @see #unsetFailureconditionLanguage()
	 * @see #getFailureconditionLanguage()
	 * @generated
	 */
	void setFailureconditionLanguage(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.SequentialGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetFailureconditionLanguage()
	 * @see #getFailureconditionLanguage()
	 * @see #setFailureconditionLanguage(String)
	 * @generated
	 */
	void unsetFailureconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.SequentialGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Failurecondition Language</em>' attribute is set.
	 * @see #unsetFailureconditionLanguage()
	 * @see #getFailureconditionLanguage()
	 * @see #setFailureconditionLanguage(String)
	 * @generated
	 */
	boolean isSetFailureconditionLanguage();
} // SequentialGoal
