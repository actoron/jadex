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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see jadex.tools.gpmn.GpmnPackage
 * @generated
 */
public class GpmnAdapterFactory extends AdapterFactoryImpl
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static GpmnPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnAdapterFactory()
	{
		if (modelPackage == null)
		{
			modelPackage = GpmnPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object)
	{
		if (object == modelPackage)
		{
			return true;
		}
		if (object instanceof EObject)
		{
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GpmnSwitch<Adapter> modelSwitch = new GpmnSwitch<Adapter>()
		{
			@Override
			public Adapter caseAbstractEdge(AbstractEdge object)
			{
				return createAbstractEdgeAdapter();
			}
			@Override
			public Adapter caseAbstractNode(AbstractNode object)
			{
				return createAbstractNodeAdapter();
			}
			@Override
			public Adapter caseAbstractPlan(AbstractPlan object)
			{
				return createAbstractPlanAdapter();
			}
			@Override
			public Adapter caseActivatable(Activatable object)
			{
				return createActivatableAdapter();
			}
			@Override
			public Adapter caseActivationEdge(ActivationEdge object)
			{
				return createActivationEdgeAdapter();
			}
			@Override
			public Adapter caseBpmnPlan(BpmnPlan object)
			{
				return createBpmnPlanAdapter();
			}
			@Override
			public Adapter caseContext(Context object)
			{
				return createContextAdapter();
			}
			@Override
			public Adapter caseContextElement(ContextElement object)
			{
				return createContextElementAdapter();
			}
			@Override
			public Adapter caseGoal(Goal object)
			{
				return createGoalAdapter();
			}
			@Override
			public Adapter caseGpmnDiagram(GpmnDiagram object)
			{
				return createGpmnDiagramAdapter();
			}
			@Override
			public Adapter caseIdentifiable(Identifiable object)
			{
				return createIdentifiableAdapter();
			}
			@Override
			public Adapter caseActivationPlan(ActivationPlan object)
			{
				return createActivationPlanAdapter();
			}
			@Override
			public Adapter caseNamedObject(NamedObject object)
			{
				return createNamedObjectAdapter();
			}
			@Override
			public Adapter caseParameter(Parameter object)
			{
				return createParameterAdapter();
			}
			@Override
			public Adapter caseParameterMapping(ParameterMapping object)
			{
				return createParameterMappingAdapter();
			}
			@Override
			public Adapter casePlanEdge(PlanEdge object)
			{
				return createPlanEdgeAdapter();
			}
			@Override
			public Adapter caseSubProcess(SubProcess object)
			{
				return createSubProcessAdapter();
			}
			@Override
			public Adapter caseSuppressionEdge(SuppressionEdge object)
			{
				return createSuppressionEdgeAdapter();
			}
			@Override
			public Adapter caseEModelElement(EModelElement object)
			{
				return createEModelElementAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object)
			{
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target)
	{
		return modelSwitch.doSwitch((EObject)target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.AbstractEdge <em>Abstract Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.AbstractEdge
	 * @generated
	 */
	public Adapter createAbstractEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.AbstractNode <em>Abstract Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.AbstractNode
	 * @generated
	 */
	public Adapter createAbstractNodeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.AbstractPlan <em>Abstract Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.AbstractPlan
	 * @generated
	 */
	public Adapter createAbstractPlanAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Activatable <em>Activatable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Activatable
	 * @generated
	 */
	public Adapter createActivatableAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ActivationEdge <em>Activation Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ActivationEdge
	 * @generated
	 */
	public Adapter createActivationEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.BpmnPlan <em>Bpmn Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.BpmnPlan
	 * @generated
	 */
	public Adapter createBpmnPlanAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Context <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Context
	 * @generated
	 */
	public Adapter createContextAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ContextElement <em>Context Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ContextElement
	 * @generated
	 */
	public Adapter createContextElementAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Goal <em>Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Goal
	 * @generated
	 */
	public Adapter createGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.GpmnDiagram <em>Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.GpmnDiagram
	 * @generated
	 */
	public Adapter createGpmnDiagramAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Identifiable <em>Identifiable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Identifiable
	 * @generated
	 */
	public Adapter createIdentifiableAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ActivationPlan <em>Activation Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ActivationPlan
	 * @generated
	 */
	public Adapter createActivationPlanAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.NamedObject <em>Named Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.NamedObject
	 * @generated
	 */
	public Adapter createNamedObjectAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Parameter
	 * @generated
	 */
	public Adapter createParameterAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ParameterMapping <em>Parameter Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ParameterMapping
	 * @generated
	 */
	public Adapter createParameterMappingAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.PlanEdge <em>Plan Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.PlanEdge
	 * @generated
	 */
	public Adapter createPlanEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.SubProcess <em>Sub Process</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.SubProcess
	 * @generated
	 */
	public Adapter createSubProcessAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.SuppressionEdge <em>Suppression Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.SuppressionEdge
	 * @generated
	 */
	public Adapter createSuppressionEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.EModelElement <em>EModel Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.ecore.EModelElement
	 * @generated
	 */
	public Adapter createEModelElementAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter()
	{
		return null;
	}

} //GpmnAdapterFactory
