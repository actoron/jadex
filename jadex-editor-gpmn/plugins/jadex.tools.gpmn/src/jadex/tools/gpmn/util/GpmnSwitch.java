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
package jadex.tools.gpmn.util;

import jadex.tools.gpmn.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see jadex.tools.gpmn.GpmnPackage
 * @generated
 */
public class GpmnSwitch<T>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static GpmnPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnSwitch()
	{
		if (modelPackage == null)
		{
			modelPackage = GpmnPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject)
	{
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject)
	{
		if (theEClass.eContainer() == modelPackage)
		{
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else
		{
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject)
	{
		switch (classifierID)
		{
			case GpmnPackage.ABSTRACT_EDGE:
			{
				AbstractEdge abstractEdge = (AbstractEdge)theEObject;
				T result = caseAbstractEdge(abstractEdge);
				if (result == null) result = caseIdentifiable(abstractEdge);
				if (result == null) result = caseEModelElement(abstractEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ABSTRACT_NODE:
			{
				AbstractNode abstractNode = (AbstractNode)theEObject;
				T result = caseAbstractNode(abstractNode);
				if (result == null) result = caseIdentifiable(abstractNode);
				if (result == null) result = caseNamedObject(abstractNode);
				if (result == null) result = caseEModelElement(abstractNode);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ABSTRACT_PLAN:
			{
				AbstractPlan abstractPlan = (AbstractPlan)theEObject;
				T result = caseAbstractPlan(abstractPlan);
				if (result == null) result = caseAbstractNode(abstractPlan);
				if (result == null) result = caseIdentifiable(abstractPlan);
				if (result == null) result = caseNamedObject(abstractPlan);
				if (result == null) result = caseEModelElement(abstractPlan);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ACTIVATABLE:
			{
				Activatable activatable = (Activatable)theEObject;
				T result = caseActivatable(activatable);
				if (result == null) result = caseEModelElement(activatable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ACTIVATION_EDGE:
			{
				ActivationEdge activationEdge = (ActivationEdge)theEObject;
				T result = caseActivationEdge(activationEdge);
				if (result == null) result = caseAbstractEdge(activationEdge);
				if (result == null) result = caseIdentifiable(activationEdge);
				if (result == null) result = caseEModelElement(activationEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.BPMN_PLAN:
			{
				BpmnPlan bpmnPlan = (BpmnPlan)theEObject;
				T result = caseBpmnPlan(bpmnPlan);
				if (result == null) result = caseAbstractPlan(bpmnPlan);
				if (result == null) result = caseAbstractNode(bpmnPlan);
				if (result == null) result = caseIdentifiable(bpmnPlan);
				if (result == null) result = caseNamedObject(bpmnPlan);
				if (result == null) result = caseEModelElement(bpmnPlan);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.CONTEXT:
			{
				Context context = (Context)theEObject;
				T result = caseContext(context);
				if (result == null) result = caseEModelElement(context);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.CONTEXT_ELEMENT:
			{
				ContextElement contextElement = (ContextElement)theEObject;
				T result = caseContextElement(contextElement);
				if (result == null) result = caseEModelElement(contextElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GOAL:
			{
				Goal goal = (Goal)theEObject;
				T result = caseGoal(goal);
				if (result == null) result = caseAbstractNode(goal);
				if (result == null) result = caseActivatable(goal);
				if (result == null) result = caseIdentifiable(goal);
				if (result == null) result = caseNamedObject(goal);
				if (result == null) result = caseEModelElement(goal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GPMN_DIAGRAM:
			{
				GpmnDiagram gpmnDiagram = (GpmnDiagram)theEObject;
				T result = caseGpmnDiagram(gpmnDiagram);
				if (result == null) result = caseNamedObject(gpmnDiagram);
				if (result == null) result = caseEModelElement(gpmnDiagram);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.IDENTIFIABLE:
			{
				Identifiable identifiable = (Identifiable)theEObject;
				T result = caseIdentifiable(identifiable);
				if (result == null) result = caseEModelElement(identifiable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ACTIVATION_PLAN:
			{
				ActivationPlan activationPlan = (ActivationPlan)theEObject;
				T result = caseActivationPlan(activationPlan);
				if (result == null) result = caseAbstractPlan(activationPlan);
				if (result == null) result = caseAbstractNode(activationPlan);
				if (result == null) result = caseIdentifiable(activationPlan);
				if (result == null) result = caseNamedObject(activationPlan);
				if (result == null) result = caseEModelElement(activationPlan);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.NAMED_OBJECT:
			{
				NamedObject namedObject = (NamedObject)theEObject;
				T result = caseNamedObject(namedObject);
				if (result == null) result = caseEModelElement(namedObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PARAMETER:
			{
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = caseEModelElement(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PARAMETER_MAPPING:
			{
				ParameterMapping parameterMapping = (ParameterMapping)theEObject;
				T result = caseParameterMapping(parameterMapping);
				if (result == null) result = caseEModelElement(parameterMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PLAN_EDGE:
			{
				PlanEdge planEdge = (PlanEdge)theEObject;
				T result = casePlanEdge(planEdge);
				if (result == null) result = caseAbstractEdge(planEdge);
				if (result == null) result = caseIdentifiable(planEdge);
				if (result == null) result = caseEModelElement(planEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.SUB_PROCESS:
			{
				SubProcess subProcess = (SubProcess)theEObject;
				T result = caseSubProcess(subProcess);
				if (result == null) result = caseAbstractNode(subProcess);
				if (result == null) result = caseActivatable(subProcess);
				if (result == null) result = caseIdentifiable(subProcess);
				if (result == null) result = caseNamedObject(subProcess);
				if (result == null) result = caseEModelElement(subProcess);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.SUPPRESSION_EDGE:
			{
				SuppressionEdge suppressionEdge = (SuppressionEdge)theEObject;
				T result = caseSuppressionEdge(suppressionEdge);
				if (result == null) result = caseAbstractEdge(suppressionEdge);
				if (result == null) result = caseIdentifiable(suppressionEdge);
				if (result == null) result = caseEModelElement(suppressionEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractEdge(AbstractEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Node</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Node</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractNode(AbstractNode object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractPlan(AbstractPlan object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Activatable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Activatable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActivatable(Activatable object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Activation Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Activation Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActivationEdge(ActivationEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bpmn Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bpmn Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBpmnPlan(BpmnPlan object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Context</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Context</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseContext(Context object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Context Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Context Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseContextElement(ContextElement object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGoal(Goal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Diagram</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Diagram</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGpmnDiagram(GpmnDiagram object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Identifiable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Identifiable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIdentifiable(Identifiable object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Activation Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Activation Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActivationPlan(ActivationPlan object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Object</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedObject(NamedObject object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameter(Parameter object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterMapping(ParameterMapping object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plan Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plan Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlanEdge(PlanEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sub Process</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sub Process</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSubProcess(SubProcess object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Suppression Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Suppression Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSuppressionEdge(SuppressionEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EModel Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EModel Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEModelElement(EModelElement object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object)
	{
		return null;
	}

} //GpmnSwitch
