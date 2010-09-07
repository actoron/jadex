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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see jadex.tools.gpmn.GpmnPackage
 * @generated
 */
public interface GpmnFactory extends EFactory
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GpmnFactory eINSTANCE = jadex.tools.gpmn.impl.GpmnFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Abstract Edge</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Abstract Edge</em>'.
	 * @generated
	 */
	AbstractEdge createAbstractEdge();

	/**
	 * Returns a new object of class '<em>Abstract Node</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Abstract Node</em>'.
	 * @generated
	 */
	AbstractNode createAbstractNode();

	/**
	 * Returns a new object of class '<em>Activatable</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Activatable</em>'.
	 * @generated
	 */
	Activatable createActivatable();

	/**
	 * Returns a new object of class '<em>Activation Edge</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Activation Edge</em>'.
	 * @generated
	 */
	ActivationEdge createActivationEdge();

	/**
	 * Returns a new object of class '<em>Bpmn Plan</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bpmn Plan</em>'.
	 * @generated
	 */
	BpmnPlan createBpmnPlan();

	/**
	 * Returns a new object of class '<em>Context</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Context</em>'.
	 * @generated
	 */
	Context createContext();

	/**
	 * Returns a new object of class '<em>Context Element</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Context Element</em>'.
	 * @generated
	 */
	ContextElement createContextElement();

	/**
	 * Returns a new object of class '<em>Goal</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Goal</em>'.
	 * @generated
	 */
	Goal createGoal();

	/**
	 * Returns a new object of class '<em>Diagram</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Diagram</em>'.
	 * @generated
	 */
	GpmnDiagram createGpmnDiagram();

	/**
	 * Returns a new object of class '<em>Identifiable</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Identifiable</em>'.
	 * @generated
	 */
	Identifiable createIdentifiable();

	/**
	 * Returns a new object of class '<em>Activation Plan</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Activation Plan</em>'.
	 * @generated
	 */
	ActivationPlan createActivationPlan();

	/**
	 * Returns a new object of class '<em>Named Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Named Object</em>'.
	 * @generated
	 */
	NamedObject createNamedObject();

	/**
	 * Returns a new object of class '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter</em>'.
	 * @generated
	 */
	Parameter createParameter();

	/**
	 * Returns a new object of class '<em>Parameter Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter Mapping</em>'.
	 * @generated
	 */
	ParameterMapping createParameterMapping();

	/**
	 * Returns a new object of class '<em>Plan Edge</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Plan Edge</em>'.
	 * @generated
	 */
	PlanEdge createPlanEdge();

	/**
	 * Returns a new object of class '<em>Sub Process</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sub Process</em>'.
	 * @generated
	 */
	SubProcess createSubProcess();

	/**
	 * Returns a new object of class '<em>Suppression Edge</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Suppression Edge</em>'.
	 * @generated
	 */
	SuppressionEdge createSuppressionEdge();

	/**
	 * Returns an instance of data type '<em>Condition Language</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	ConditionLanguage createConditionLanguage(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Condition Language</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertConditionLanguage(ConditionLanguage instanceValue);

	/**
	 * Returns an instance of data type '<em>Direction Type</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	DirectionType createDirectionType(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Direction Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertDirectionType(DirectionType instanceValue);

	/**
	 * Returns an instance of data type '<em>Exclude Type</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	ExcludeType createExcludeType(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Exclude Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertExcludeType(ExcludeType instanceValue);

	/**
	 * Returns an instance of data type '<em>Goal Type</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	GoalType createGoalType(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Goal Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertGoalType(GoalType instanceValue);

	/**
	 * Returns an instance of data type '<em>Mode Type</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	ModeType createModeType(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Mode Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertModeType(ModeType instanceValue);

	/**
	 * Returns an instance of data type '<em>Condition Language Object</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	ConditionLanguage createConditionLanguageObject(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Condition Language Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertConditionLanguageObject(ConditionLanguage instanceValue);

	/**
	 * Returns an instance of data type '<em>Direction Type Object</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	DirectionType createDirectionTypeObject(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Direction Type Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertDirectionTypeObject(DirectionType instanceValue);

	/**
	 * Returns an instance of data type '<em>Exclude Type Object</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	ExcludeType createExcludeTypeObject(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Exclude Type Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertExcludeTypeObject(ExcludeType instanceValue);

	/**
	 * Returns an instance of data type '<em>Goal Type Object</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	GoalType createGoalTypeObject(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Goal Type Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertGoalTypeObject(GoalType instanceValue);

	/**
	 * Returns an instance of data type '<em>Mode Type Object</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	ModeType createModeTypeObject(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Mode Type Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertModeTypeObject(ModeType instanceValue);

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	GpmnPackage getGpmnPackage();

} //GpmnFactory
