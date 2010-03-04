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
 * A representation of the model object '<em><b>Maintain Goal</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A maintain goal aims at preserving a certain state.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.MaintainGoal#getMaintaincondition <em>Maintaincondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.MaintainGoal#getMaintainconditionLanguage <em>Maintaincondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.MaintainGoal#getTargetcondition <em>Targetcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.MaintainGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getMaintainGoal()
 * @model extendedMetaData="name='MaintainGoal' kind='elementOnly'"
 * @generated
 */
public interface MaintainGoal extends Goal
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Maintaincondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The mandatory maintain condition represents a world state that should be monitored and re-established whenever it gets violated. 
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Maintaincondition</em>' attribute.
	 * @see #setMaintaincondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getMaintainGoal_Maintaincondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='maintaincondition'"
	 * @generated
	 */
	String getMaintaincondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.MaintainGoal#getMaintaincondition <em>Maintaincondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maintaincondition</em>' attribute.
	 * @see #getMaintaincondition()
	 * @generated
	 */
	void setMaintaincondition(String value);

	/**
	 * Returns the value of the '<em><b>Maintaincondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Maintaincondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maintaincondition Language</em>' attribute.
	 * @see #isSetMaintainconditionLanguage()
	 * @see #unsetMaintainconditionLanguage()
	 * @see #setMaintainconditionLanguage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getMaintainGoal_MaintainconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='maintaincondition_language'"
	 * @generated
	 */
	String getMaintainconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.MaintainGoal#getMaintainconditionLanguage <em>Maintaincondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maintaincondition Language</em>' attribute.
	 * @see #isSetMaintainconditionLanguage()
	 * @see #unsetMaintainconditionLanguage()
	 * @see #getMaintainconditionLanguage()
	 * @generated
	 */
	void setMaintainconditionLanguage(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.MaintainGoal#getMaintainconditionLanguage <em>Maintaincondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaintainconditionLanguage()
	 * @see #getMaintainconditionLanguage()
	 * @see #setMaintainconditionLanguage(String)
	 * @generated
	 */
	void unsetMaintainconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.MaintainGoal#getMaintainconditionLanguage <em>Maintaincondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Maintaincondition Language</em>' attribute is set.
	 * @see #unsetMaintainconditionLanguage()
	 * @see #getMaintainconditionLanguage()
	 * @see #setMaintainconditionLanguage(String)
	 * @generated
	 */
	boolean isSetMaintainconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A specalisation of the maintain condition taht should be re-established when the maintain condition is violated.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Targetcondition</em>' attribute.
	 * @see #setTargetcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getMaintainGoal_Targetcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='targetcondition'"
	 * @generated
	 */
	String getTargetcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.MaintainGoal#getTargetcondition <em>Targetcondition</em>}' attribute.
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
	 * @see jadex.tools.gpmn.GpmnPackage#getMaintainGoal_TargetconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='targetcondition_language'"
	 * @generated
	 */
	String getTargetconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.MaintainGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute.
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
	 * Unsets the value of the '{@link jadex.tools.gpmn.MaintainGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(String)
	 * @generated
	 */
	void unsetTargetconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.MaintainGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Targetcondition Language</em>' attribute is set.
	 * @see #unsetTargetconditionLanguage()
	 * @see #getTargetconditionLanguage()
	 * @see #setTargetconditionLanguage(String)
	 * @generated
	 */
	boolean isSetTargetconditionLanguage();

} // MaintainGoal
