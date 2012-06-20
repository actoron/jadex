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

import jadex.tools.gpmn.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GpmnFactoryImpl extends EFactoryImpl implements GpmnFactory
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GpmnFactory init()
	{
		try
		{
			GpmnFactory theGpmnFactory = (GpmnFactory)EPackage.Registry.INSTANCE.getEFactory("http://jadex.sourceforge.net/gpmn"); //$NON-NLS-1$ 
			if (theGpmnFactory != null)
			{
				return theGpmnFactory;
			}
		}
		catch (Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GpmnFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnFactoryImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass)
	{
		switch (eClass.getClassifierID())
		{
			case GpmnPackage.ABSTRACT_EDGE: return createAbstractEdge();
			case GpmnPackage.ABSTRACT_NODE: return createAbstractNode();
			case GpmnPackage.ACTIVATABLE: return createActivatable();
			case GpmnPackage.ACTIVATION_EDGE: return createActivationEdge();
			case GpmnPackage.BPMN_PLAN: return createBpmnPlan();
			case GpmnPackage.CONTEXT: return createContext();
			case GpmnPackage.CONTEXT_ELEMENT: return createContextElement();
			case GpmnPackage.GOAL: return createGoal();
			case GpmnPackage.GPMN_DIAGRAM: return createGpmnDiagram();
			case GpmnPackage.IDENTIFIABLE: return createIdentifiable();
			case GpmnPackage.ACTIVATION_PLAN: return createActivationPlan();
			case GpmnPackage.NAMED_OBJECT: return createNamedObject();
			case GpmnPackage.PARAMETER: return createParameter();
			case GpmnPackage.PARAMETER_MAPPING: return createParameterMapping();
			case GpmnPackage.PLAN_EDGE: return createPlanEdge();
			case GpmnPackage.SUB_PROCESS: return createSubProcess();
			case GpmnPackage.SUPPRESSION_EDGE: return createSuppressionEdge();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue)
	{
		switch (eDataType.getClassifierID())
		{
			case GpmnPackage.CONDITION_LANGUAGE:
				return createConditionLanguageFromString(eDataType, initialValue);
			case GpmnPackage.DIRECTION_TYPE:
				return createDirectionTypeFromString(eDataType, initialValue);
			case GpmnPackage.EXCLUDE_TYPE:
				return createExcludeTypeFromString(eDataType, initialValue);
			case GpmnPackage.GOAL_TYPE:
				return createGoalTypeFromString(eDataType, initialValue);
			case GpmnPackage.MODE_TYPE:
				return createModeTypeFromString(eDataType, initialValue);
			case GpmnPackage.CONDITION_LANGUAGE_OBJECT:
				return createConditionLanguageObjectFromString(eDataType, initialValue);
			case GpmnPackage.DIRECTION_TYPE_OBJECT:
				return createDirectionTypeObjectFromString(eDataType, initialValue);
			case GpmnPackage.EXCLUDE_TYPE_OBJECT:
				return createExcludeTypeObjectFromString(eDataType, initialValue);
			case GpmnPackage.GOAL_TYPE_OBJECT:
				return createGoalTypeObjectFromString(eDataType, initialValue);
			case GpmnPackage.MODE_TYPE_OBJECT:
				return createModeTypeObjectFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue)
	{
		switch (eDataType.getClassifierID())
		{
			case GpmnPackage.CONDITION_LANGUAGE:
				return convertConditionLanguageToString(eDataType, instanceValue);
			case GpmnPackage.DIRECTION_TYPE:
				return convertDirectionTypeToString(eDataType, instanceValue);
			case GpmnPackage.EXCLUDE_TYPE:
				return convertExcludeTypeToString(eDataType, instanceValue);
			case GpmnPackage.GOAL_TYPE:
				return convertGoalTypeToString(eDataType, instanceValue);
			case GpmnPackage.MODE_TYPE:
				return convertModeTypeToString(eDataType, instanceValue);
			case GpmnPackage.CONDITION_LANGUAGE_OBJECT:
				return convertConditionLanguageObjectToString(eDataType, instanceValue);
			case GpmnPackage.DIRECTION_TYPE_OBJECT:
				return convertDirectionTypeObjectToString(eDataType, instanceValue);
			case GpmnPackage.EXCLUDE_TYPE_OBJECT:
				return convertExcludeTypeObjectToString(eDataType, instanceValue);
			case GpmnPackage.GOAL_TYPE_OBJECT:
				return convertGoalTypeObjectToString(eDataType, instanceValue);
			case GpmnPackage.MODE_TYPE_OBJECT:
				return convertModeTypeObjectToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractEdge createAbstractEdge()
	{
		AbstractEdgeImpl abstractEdge = new AbstractEdgeImpl();
		return abstractEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractNode createAbstractNode()
	{
		AbstractNodeImpl abstractNode = new AbstractNodeImpl();
		return abstractNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Activatable createActivatable()
	{
		ActivatableImpl activatable = new ActivatableImpl();
		return activatable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ActivationEdge createActivationEdge()
	{
		ActivationEdgeImpl activationEdge = new ActivationEdgeImpl();
		return activationEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BpmnPlan createBpmnPlan()
	{
		BpmnPlanImpl bpmnPlan = new BpmnPlanImpl();
		return bpmnPlan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Context createContext()
	{
		ContextImpl context = new ContextImpl();
		return context;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ContextElement createContextElement()
	{
		ContextElementImpl contextElement = new ContextElementImpl();
		return contextElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Goal createGoal()
	{
		GoalImpl goal = new GoalImpl();
		return goal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT - initialized context
	 */
	public GpmnDiagram createGpmnDiagram()
	{
		GpmnDiagramImpl gpmnDiagram = new GpmnDiagramImpl();
		gpmnDiagram.setContext(createContext());
		return gpmnDiagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Identifiable createIdentifiable()
	{
		IdentifiableImpl identifiable = new IdentifiableImpl();
		return identifiable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ActivationPlan createActivationPlan()
	{
		ActivationPlanImpl activationPlan = new ActivationPlanImpl();
		return activationPlan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NamedObject createNamedObject()
	{
		NamedObjectImpl namedObject = new NamedObjectImpl();
		return namedObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter createParameter()
	{
		ParameterImpl parameter = new ParameterImpl();
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterMapping createParameterMapping()
	{
		ParameterMappingImpl parameterMapping = new ParameterMappingImpl();
		return parameterMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PlanEdge createPlanEdge()
	{
		PlanEdgeImpl planEdge = new PlanEdgeImpl();
		return planEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SubProcess createSubProcess()
	{
		SubProcessImpl subProcess = new SubProcessImpl();
		return subProcess;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SuppressionEdge createSuppressionEdge()
	{
		SuppressionEdgeImpl suppressionEdge = new SuppressionEdgeImpl();
		return suppressionEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage createConditionLanguage(String literal)
	{
		ConditionLanguage result = ConditionLanguage.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.CONDITION_LANGUAGE.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage createConditionLanguageFromString(
			EDataType eDataType, String initialValue)
	{
		return createConditionLanguage(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConditionLanguage(ConditionLanguage instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConditionLanguageToString(EDataType eDataType,
			Object instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirectionType createDirectionType(String literal)
	{
		DirectionType result = DirectionType.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.DIRECTION_TYPE.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirectionType createDirectionTypeFromString(EDataType eDataType,
			String initialValue)
	{
		return createDirectionType(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirectionType(DirectionType instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirectionTypeToString(EDataType eDataType,
			Object instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExcludeType createExcludeType(String literal)
	{
		ExcludeType result = ExcludeType.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.EXCLUDE_TYPE.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExcludeType createExcludeTypeFromString(EDataType eDataType,
			String initialValue)
	{
		return createExcludeType(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertExcludeType(ExcludeType instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertExcludeTypeToString(EDataType eDataType,
			Object instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GoalType createGoalType(String literal)
	{
		GoalType result = GoalType.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.GOAL_TYPE.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GoalType createGoalTypeFromString(EDataType eDataType,
			String initialValue)
	{
		return createGoalType(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGoalType(GoalType instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGoalTypeToString(EDataType eDataType,
			Object instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModeType createModeType(String literal)
	{
		ModeType result = ModeType.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.MODE_TYPE.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModeType createModeTypeFromString(EDataType eDataType,
			String initialValue)
	{
		return createModeType(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertModeType(ModeType instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertModeTypeToString(EDataType eDataType,
			Object instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage createConditionLanguageObject(String literal)
	{
		return createConditionLanguage(literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionLanguage createConditionLanguageObjectFromString(
			EDataType eDataType, String initialValue)
	{
		return createConditionLanguageFromString(GpmnPackage.Literals.CONDITION_LANGUAGE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConditionLanguageObject(ConditionLanguage instanceValue)
	{
		return convertConditionLanguage(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConditionLanguageObjectToString(EDataType eDataType,
			Object instanceValue)
	{
		return convertConditionLanguageToString(GpmnPackage.Literals.CONDITION_LANGUAGE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirectionType createDirectionTypeObject(String literal)
	{
		return createDirectionType(literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirectionType createDirectionTypeObjectFromString(
			EDataType eDataType, String initialValue)
	{
		return createDirectionTypeFromString(GpmnPackage.Literals.DIRECTION_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirectionTypeObject(DirectionType instanceValue)
	{
		return convertDirectionType(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirectionTypeObjectToString(EDataType eDataType,
			Object instanceValue)
	{
		return convertDirectionTypeToString(GpmnPackage.Literals.DIRECTION_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExcludeType createExcludeTypeObject(String literal)
	{
		return createExcludeType(literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExcludeType createExcludeTypeObjectFromString(EDataType eDataType,
			String initialValue)
	{
		return createExcludeTypeFromString(GpmnPackage.Literals.EXCLUDE_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertExcludeTypeObject(ExcludeType instanceValue)
	{
		return convertExcludeType(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertExcludeTypeObjectToString(EDataType eDataType,
			Object instanceValue)
	{
		return convertExcludeTypeToString(GpmnPackage.Literals.EXCLUDE_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GoalType createGoalTypeObject(String literal)
	{
		return createGoalType(literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GoalType createGoalTypeObjectFromString(EDataType eDataType,
			String initialValue)
	{
		return createGoalTypeFromString(GpmnPackage.Literals.GOAL_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGoalTypeObject(GoalType instanceValue)
	{
		return convertGoalType(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGoalTypeObjectToString(EDataType eDataType,
			Object instanceValue)
	{
		return convertGoalTypeToString(GpmnPackage.Literals.GOAL_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModeType createModeTypeObject(String literal)
	{
		return createModeType(literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModeType createModeTypeObjectFromString(EDataType eDataType,
			String initialValue)
	{
		return createModeTypeFromString(GpmnPackage.Literals.MODE_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertModeTypeObject(ModeType instanceValue)
	{
		return convertModeType(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertModeTypeObjectToString(EDataType eDataType,
			Object instanceValue)
	{
		return convertModeTypeToString(GpmnPackage.Literals.MODE_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnPackage getGpmnPackage()
	{
		return (GpmnPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GpmnPackage getPackage()
	{
		return GpmnPackage.eINSTANCE;
	}

} //GpmnFactoryImpl
