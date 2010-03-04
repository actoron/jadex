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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Goal</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Goal#getUnique <em>Unique</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getCreationcondition <em>Creationcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getCreationconditionLanguage <em>Creationcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getContextcondition <em>Contextcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getContextconditionLanguage <em>Contextcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getDropcondition <em>Dropcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getDropconditionLanguage <em>Dropcondition Language</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getRecurcondition <em>Recurcondition</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getDeliberation <em>Deliberation</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getOnSuccessHandler <em>On Success Handler</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getOnSkipHandler <em>On Skip Handler</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getOnFailureHandler <em>On Failure Handler</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getExclude <em>Exclude</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getGoalType <em>Goal Type</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#isPosttoall <em>Posttoall</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#isRandomselection <em>Randomselection</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#isRecalculate <em>Recalculate</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#isRecur <em>Recur</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getRecurdelay <em>Recurdelay</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#isRetry <em>Retry</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#getRetrydelay <em>Retrydelay</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Goal#isSequential <em>Sequential</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getGoal()
 * @model extendedMetaData="name='Goal' kind='elementOnly'"
 * @generated
 */
public interface Goal extends ParameterizedVertex
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * If a goal is declared unique only one instance of this type is allowed being adopted at any one time. To determine if two goals are equal the type and parameters are used. Parameters that should not be considered can explicitly be excluded. 
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Unique</em>' attribute.
	 * @see #setUnique(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Unique()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='unique'"
	 * @generated
	 */
	String getUnique();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getUnique <em>Unique</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unique</em>' attribute.
	 * @see #getUnique()
	 * @generated
	 */
	void setUnique(String value);

	/**
	 * Returns the value of the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A condition that creates a new goal of the given type when triggered. If binding-parameters are used for each possible binding a new goal is created. 
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Creationcondition</em>' attribute.
	 * @see #setCreationcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Creationcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='creationcondition'"
	 * @generated
	 */
	String getCreationcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getCreationcondition <em>Creationcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Creationcondition</em>' attribute.
	 * @see #getCreationcondition()
	 * @generated
	 */
	void setCreationcondition(String value);

	/**
	 * Returns the value of the '<em><b>Creationcondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Creationcondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Creationcondition Language</em>' attribute.
	 * @see #isSetCreationconditionLanguage()
	 * @see #unsetCreationconditionLanguage()
	 * @see #setCreationconditionLanguage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_CreationconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='creationcondition_language'"
	 * @generated
	 */
	String getCreationconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getCreationconditionLanguage <em>Creationcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Creationcondition Language</em>' attribute.
	 * @see #isSetCreationconditionLanguage()
	 * @see #unsetCreationconditionLanguage()
	 * @see #getCreationconditionLanguage()
	 * @generated
	 */
	void setCreationconditionLanguage(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getCreationconditionLanguage <em>Creationcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetCreationconditionLanguage()
	 * @see #getCreationconditionLanguage()
	 * @see #setCreationconditionLanguage(String)
	 * @generated
	 */
	void unsetCreationconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getCreationconditionLanguage <em>Creationcondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Creationcondition Language</em>' attribute is set.
	 * @see #unsetCreationconditionLanguage()
	 * @see #getCreationconditionLanguage()
	 * @see #setCreationconditionLanguage(String)
	 * @generated
	 */
	boolean isSetCreationconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The context condition is checked during the whole execution time of a goal. If it becomes invalid the goal will become suspended and is not actively pursued until reactivation.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Contextcondition</em>' attribute.
	 * @see #setContextcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Contextcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='contextcondition'"
	 * @generated
	 */
	String getContextcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getContextcondition <em>Contextcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contextcondition</em>' attribute.
	 * @see #getContextcondition()
	 * @generated
	 */
	void setContextcondition(String value);

	/**
	 * Returns the value of the '<em><b>Contextcondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Contextcondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contextcondition Language</em>' attribute.
	 * @see #isSetContextconditionLanguage()
	 * @see #unsetContextconditionLanguage()
	 * @see #setContextconditionLanguage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_ContextconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='contextcondition_language'"
	 * @generated
	 */
	String getContextconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getContextconditionLanguage <em>Contextcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contextcondition Language</em>' attribute.
	 * @see #isSetContextconditionLanguage()
	 * @see #unsetContextconditionLanguage()
	 * @see #getContextconditionLanguage()
	 * @generated
	 */
	void setContextconditionLanguage(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getContextconditionLanguage <em>Contextcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetContextconditionLanguage()
	 * @see #getContextconditionLanguage()
	 * @see #setContextconditionLanguage(String)
	 * @generated
	 */
	void unsetContextconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getContextconditionLanguage <em>Contextcondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Contextcondition Language</em>' attribute is set.
	 * @see #unsetContextconditionLanguage()
	 * @see #getContextconditionLanguage()
	 * @see #setContextconditionLanguage(String)
	 * @generated
	 */
	boolean isSetContextconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * If the dropcondition triggers the goal instance is dropped.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Dropcondition</em>' attribute.
	 * @see #setDropcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Dropcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='dropcondition'"
	 * @generated
	 */
	String getDropcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getDropcondition <em>Dropcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dropcondition</em>' attribute.
	 * @see #getDropcondition()
	 * @generated
	 */
	void setDropcondition(String value);

	/**
	 * Returns the value of the '<em><b>Dropcondition Language</b></em>' attribute.
	 * The default value is <code>"jcl"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dropcondition Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dropcondition Language</em>' attribute.
	 * @see #isSetDropconditionLanguage()
	 * @see #unsetDropconditionLanguage()
	 * @see #setDropconditionLanguage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_DropconditionLanguage()
	 * @model default="jcl" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='dropcondition_language'"
	 * @generated
	 */
	String getDropconditionLanguage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getDropconditionLanguage <em>Dropcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dropcondition Language</em>' attribute.
	 * @see #isSetDropconditionLanguage()
	 * @see #unsetDropconditionLanguage()
	 * @see #getDropconditionLanguage()
	 * @generated
	 */
	void setDropconditionLanguage(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getDropconditionLanguage <em>Dropcondition Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDropconditionLanguage()
	 * @see #getDropconditionLanguage()
	 * @see #setDropconditionLanguage(String)
	 * @generated
	 */
	void unsetDropconditionLanguage();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getDropconditionLanguage <em>Dropcondition Language</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Dropcondition Language</em>' attribute is set.
	 * @see #unsetDropconditionLanguage()
	 * @see #getDropconditionLanguage()
	 * @see #setDropconditionLanguage(String)
	 * @generated
	 */
	boolean isSetDropconditionLanguage();

	/**
	 * Returns the value of the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Recurcondition</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recurcondition</em>' attribute.
	 * @see #setRecurcondition(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Recurcondition()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='recurcondition'"
	 * @generated
	 */
	String getRecurcondition();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getRecurcondition <em>Recurcondition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recurcondition</em>' attribute.
	 * @see #getRecurcondition()
	 * @generated
	 */
	void setRecurcondition(String value);

	/**
	 * Returns the value of the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The goal deliberation setting for the easy deliberation strategy.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Deliberation</em>' attribute.
	 * @see #setDeliberation(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Deliberation()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='deliberation'"
	 * @generated
	 */
	String getDeliberation();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getDeliberation <em>Deliberation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deliberation</em>' attribute.
	 * @see #getDeliberation()
	 * @generated
	 */
	void setDeliberation(String value);

	/**
	 * Returns the value of the '<em><b>On Success Handler</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On Success Handler</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Success Handler</em>' attribute list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_OnSuccessHandler()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='onSuccessHandler'"
	 * @generated
	 */
	EList<String> getOnSuccessHandler();

	/**
	 * Returns the value of the '<em><b>On Skip Handler</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On Skip Handler</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Skip Handler</em>' attribute list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_OnSkipHandler()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='onSkipHandler'"
	 * @generated
	 */
	EList<String> getOnSkipHandler();

	/**
	 * Returns the value of the '<em><b>On Failure Handler</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On Failure Handler</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Failure Handler</em>' attribute list.
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_OnFailureHandler()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='onFailureHandler'"
	 * @generated
	 */
	EList<String> getOnFailureHandler();

	/**
	 * Returns the value of the '<em><b>Exclude</b></em>' attribute.
	 * The default value is <code>"when_tried"</code>.
	 * The literals are from the enumeration {@link jadex.tools.gpmn.ExcludeType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The exclude flag can be specified when a plan will be excluded from the applicable plan list. The default is when_tried, which means that a candidate is excluded independently of its state when executed one time for a goal. Other options are when_succeeded, when_failed and never.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclude</em>' attribute.
	 * @see jadex.tools.gpmn.ExcludeType
	 * @see #isSetExclude()
	 * @see #unsetExclude()
	 * @see #setExclude(ExcludeType)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Exclude()
	 * @model default="when_tried" unsettable="true"
	 *        extendedMetaData="kind='attribute' name='exclude'"
	 * @generated
	 */
	ExcludeType getExclude();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getExclude <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclude</em>' attribute.
	 * @see jadex.tools.gpmn.ExcludeType
	 * @see #isSetExclude()
	 * @see #unsetExclude()
	 * @see #getExclude()
	 * @generated
	 */
	void setExclude(ExcludeType value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getExclude <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExclude()
	 * @see #getExclude()
	 * @see #setExclude(ExcludeType)
	 * @generated
	 */
	void unsetExclude();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getExclude <em>Exclude</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Exclude</em>' attribute is set.
	 * @see #unsetExclude()
	 * @see #getExclude()
	 * @see #setExclude(ExcludeType)
	 * @generated
	 */
	boolean isSetExclude();

	/**
	 * Returns the value of the '<em><b>Goal Type</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * The literals are from the enumeration {@link jadex.tools.gpmn.GoalType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Goal Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Goal Type</em>' attribute.
	 * @see jadex.tools.gpmn.GoalType
	 * @see #isSetGoalType()
	 * @see #unsetGoalType()
	 * @see #setGoalType(GoalType)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_GoalType()
	 * @model default="" unsettable="true"
	 *        extendedMetaData="kind='attribute' name='goalType'"
	 * @generated
	 */
	GoalType getGoalType();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getGoalType <em>Goal Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Goal Type</em>' attribute.
	 * @see jadex.tools.gpmn.GoalType
	 * @see #isSetGoalType()
	 * @see #unsetGoalType()
	 * @see #getGoalType()
	 * @generated
	 */
	void setGoalType(GoalType value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getGoalType <em>Goal Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetGoalType()
	 * @see #getGoalType()
	 * @see #setGoalType(GoalType)
	 * @generated
	 */
	void unsetGoalType();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getGoalType <em>Goal Type</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Goal Type</em>' attribute is set.
	 * @see #unsetGoalType()
	 * @see #getGoalType()
	 * @see #setGoalType(GoalType)
	 * @generated
	 */
	boolean isSetGoalType();

	/**
	 * Returns the value of the '<em><b>Posttoall</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * When post-to-all is set to true (default is false), a goal is dispatched to all candidates of the applicable plan list at once. This process will only happen one time regardless of the retry settings. A post-to-all goal has implicit or semantics meaning that if one plan achieves the goal all others will be terminated.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Posttoall</em>' attribute.
	 * @see #isSetPosttoall()
	 * @see #unsetPosttoall()
	 * @see #setPosttoall(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Posttoall()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='posttoall'"
	 * @generated
	 */
	boolean isPosttoall();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#isPosttoall <em>Posttoall</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Posttoall</em>' attribute.
	 * @see #isSetPosttoall()
	 * @see #unsetPosttoall()
	 * @see #isPosttoall()
	 * @generated
	 */
	void setPosttoall(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#isPosttoall <em>Posttoall</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPosttoall()
	 * @see #isPosttoall()
	 * @see #setPosttoall(boolean)
	 * @generated
	 */
	void unsetPosttoall();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#isPosttoall <em>Posttoall</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Posttoall</em>' attribute is set.
	 * @see #unsetPosttoall()
	 * @see #isPosttoall()
	 * @see #setPosttoall(boolean)
	 * @generated
	 */
	boolean isSetPosttoall();

	/**
	 * Returns the value of the '<em><b>Randomselection</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Random selection can be used to choose among applicable plans for a given goal randomly. If used this flag makes the order of plan declaration within the ADF unimportantly, i.e. only random selection is only applied to plans of the same priority and rank (cf. mlreasoning comment). The mechanism is implemented in the jadex.impl.DefaultMetaLevelReasoner.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Randomselection</em>' attribute.
	 * @see #isSetRandomselection()
	 * @see #unsetRandomselection()
	 * @see #setRandomselection(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Randomselection()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='randomselection'"
	 * @generated
	 */
	boolean isRandomselection();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#isRandomselection <em>Randomselection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Randomselection</em>' attribute.
	 * @see #isSetRandomselection()
	 * @see #unsetRandomselection()
	 * @see #isRandomselection()
	 * @generated
	 */
	void setRandomselection(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#isRandomselection <em>Randomselection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRandomselection()
	 * @see #isRandomselection()
	 * @see #setRandomselection(boolean)
	 * @generated
	 */
	void unsetRandomselection();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#isRandomselection <em>Randomselection</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Randomselection</em>' attribute is set.
	 * @see #unsetRandomselection()
	 * @see #isRandomselection()
	 * @see #setRandomselection(boolean)
	 * @generated
	 */
	boolean isSetRandomselection();

	/**
	 * Returns the value of the '<em><b>Recalculate</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * When recalculate is set to false (default is true) the applicable candidates list will be calculated only once for the goal. Otherwise it will be recalculated whenever the goal should be processed.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Recalculate</em>' attribute.
	 * @see #isSetRecalculate()
	 * @see #unsetRecalculate()
	 * @see #setRecalculate(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Recalculate()
	 * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='recalculate'"
	 * @generated
	 */
	boolean isRecalculate();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#isRecalculate <em>Recalculate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recalculate</em>' attribute.
	 * @see #isSetRecalculate()
	 * @see #unsetRecalculate()
	 * @see #isRecalculate()
	 * @generated
	 */
	void setRecalculate(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#isRecalculate <em>Recalculate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRecalculate()
	 * @see #isRecalculate()
	 * @see #setRecalculate(boolean)
	 * @generated
	 */
	void unsetRecalculate();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#isRecalculate <em>Recalculate</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Recalculate</em>' attribute is set.
	 * @see #unsetRecalculate()
	 * @see #isRecalculate()
	 * @see #setRecalculate(boolean)
	 * @generated
	 */
	boolean isSetRecalculate();

	/**
	 * Returns the value of the '<em><b>Recur</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Recur</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recur</em>' attribute.
	 * @see #isSetRecur()
	 * @see #unsetRecur()
	 * @see #setRecur(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Recur()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='recur'"
	 * @generated
	 */
	boolean isRecur();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#isRecur <em>Recur</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recur</em>' attribute.
	 * @see #isSetRecur()
	 * @see #unsetRecur()
	 * @see #isRecur()
	 * @generated
	 */
	void setRecur(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#isRecur <em>Recur</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRecur()
	 * @see #isRecur()
	 * @see #setRecur(boolean)
	 * @generated
	 */
	void unsetRecur();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#isRecur <em>Recur</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Recur</em>' attribute is set.
	 * @see #unsetRecur()
	 * @see #isRecur()
	 * @see #setRecur(boolean)
	 * @generated
	 */
	boolean isSetRecur();

	/**
	 * Returns the value of the '<em><b>Recurdelay</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Recurdelay</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recurdelay</em>' attribute.
	 * @see #isSetRecurdelay()
	 * @see #unsetRecurdelay()
	 * @see #setRecurdelay(long)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Recurdelay()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long"
	 *        extendedMetaData="kind='attribute' name='recurdelay'"
	 * @generated
	 */
	long getRecurdelay();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getRecurdelay <em>Recurdelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recurdelay</em>' attribute.
	 * @see #isSetRecurdelay()
	 * @see #unsetRecurdelay()
	 * @see #getRecurdelay()
	 * @generated
	 */
	void setRecurdelay(long value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getRecurdelay <em>Recurdelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRecurdelay()
	 * @see #getRecurdelay()
	 * @see #setRecurdelay(long)
	 * @generated
	 */
	void unsetRecurdelay();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getRecurdelay <em>Recurdelay</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Recurdelay</em>' attribute is set.
	 * @see #unsetRecurdelay()
	 * @see #getRecurdelay()
	 * @see #setRecurdelay(long)
	 * @generated
	 */
	boolean isSetRecurdelay();

	/**
	 * Returns the value of the '<em><b>Retry</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The retry flag can be used to determine the behaviour on plan failures. If retry is turned on (by default it is on) and a plan fails to achieve the considered goal another plan from the applicables plan list will be chosen for execution.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Retry</em>' attribute.
	 * @see #isSetRetry()
	 * @see #unsetRetry()
	 * @see #setRetry(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Retry()
	 * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='retry'"
	 * @generated
	 */
	boolean isRetry();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#isRetry <em>Retry</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Retry</em>' attribute.
	 * @see #isSetRetry()
	 * @see #unsetRetry()
	 * @see #isRetry()
	 * @generated
	 */
	void setRetry(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#isRetry <em>Retry</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRetry()
	 * @see #isRetry()
	 * @see #setRetry(boolean)
	 * @generated
	 */
	void unsetRetry();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#isRetry <em>Retry</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Retry</em>' attribute is set.
	 * @see #unsetRetry()
	 * @see #isRetry()
	 * @see #setRetry(boolean)
	 * @generated
	 */
	boolean isSetRetry();

	/**
	 * Returns the value of the '<em><b>Retrydelay</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * With the retrydelay the delay between the failure of one plan and the execution of the next plan can be specified in milliseconds.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Retrydelay</em>' attribute.
	 * @see #isSetRetrydelay()
	 * @see #unsetRetrydelay()
	 * @see #setRetrydelay(long)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Retrydelay()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long"
	 *        extendedMetaData="kind='attribute' name='retrydelay'"
	 * @generated
	 */
	long getRetrydelay();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#getRetrydelay <em>Retrydelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Retrydelay</em>' attribute.
	 * @see #isSetRetrydelay()
	 * @see #unsetRetrydelay()
	 * @see #getRetrydelay()
	 * @generated
	 */
	void setRetrydelay(long value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#getRetrydelay <em>Retrydelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRetrydelay()
	 * @see #getRetrydelay()
	 * @see #setRetrydelay(long)
	 * @generated
	 */
	void unsetRetrydelay();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#getRetrydelay <em>Retrydelay</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Retrydelay</em>' attribute is set.
	 * @see #unsetRetrydelay()
	 * @see #getRetrydelay()
	 * @see #setRetrydelay(long)
	 * @generated
	 */
	boolean isSetRetrydelay();

	/**
	 * Returns the value of the '<em><b>Sequential</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sequential</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sequential</em>' attribute.
	 * @see #isSetSequential()
	 * @see #unsetSequential()
	 * @see #setSequential(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getGoal_Sequential()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='sequential'"
	 * @generated
	 */
	boolean isSequential();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Goal#isSequential <em>Sequential</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sequential</em>' attribute.
	 * @see #isSetSequential()
	 * @see #unsetSequential()
	 * @see #isSequential()
	 * @generated
	 */
	void setSequential(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Goal#isSequential <em>Sequential</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSequential()
	 * @see #isSequential()
	 * @see #setSequential(boolean)
	 * @generated
	 */
	void unsetSequential();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Goal#isSequential <em>Sequential</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Sequential</em>' attribute is set.
	 * @see #unsetSequential()
	 * @see #isSequential()
	 * @see #setSequential(boolean)
	 * @generated
	 */
	boolean isSetSequential();

} // Goal
