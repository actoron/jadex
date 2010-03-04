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
package jadex.tools.gpmn.util;

import jadex.tools.gpmn.AchieveGoal;
import jadex.tools.gpmn.Artifact;
import jadex.tools.gpmn.ArtifactsContainer;
import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.AssociationTarget;
import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.ContextElement;
import jadex.tools.gpmn.DataObject;
import jadex.tools.gpmn.Edge;
import jadex.tools.gpmn.GenericGpmnEdge;
import jadex.tools.gpmn.GenericGpmnElement;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Graph;
import jadex.tools.gpmn.Group;
import jadex.tools.gpmn.Identifiable;
import jadex.tools.gpmn.InterGraphEdge;
import jadex.tools.gpmn.InterGraphVertex;
import jadex.tools.gpmn.MaintainGoal;
import jadex.tools.gpmn.MessageGoal;
import jadex.tools.gpmn.MessagingEdge;
import jadex.tools.gpmn.NamedObject;
import jadex.tools.gpmn.ParallelGoal;
import jadex.tools.gpmn.Parameter;
import jadex.tools.gpmn.ParameterizedEdge;
import jadex.tools.gpmn.ParameterizedVertex;
import jadex.tools.gpmn.PerformGoal;
import jadex.tools.gpmn.Plan;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.QueryGoal;
import jadex.tools.gpmn.Role;
import jadex.tools.gpmn.SequentialGoal;
import jadex.tools.gpmn.SubGoalEdge;
import jadex.tools.gpmn.SubProcessGoal;
import jadex.tools.gpmn.TextAnnotation;
import jadex.tools.gpmn.Vertex;

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
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";
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
			public Adapter caseAchieveGoal(AchieveGoal object)
			{
				return createAchieveGoalAdapter();
			}
			@Override
			public Adapter caseArtifact(Artifact object)
			{
				return createArtifactAdapter();
			}
			@Override
			public Adapter caseArtifactsContainer(ArtifactsContainer object)
			{
				return createArtifactsContainerAdapter();
			}
			@Override
			public Adapter caseAssociation(Association object)
			{
				return createAssociationAdapter();
			}
			@Override
			public Adapter caseAssociationTarget(AssociationTarget object)
			{
				return createAssociationTargetAdapter();
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
			public Adapter caseDataObject(DataObject object)
			{
				return createDataObjectAdapter();
			}
			@Override
			public Adapter caseEdge(Edge object)
			{
				return createEdgeAdapter();
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
			public Adapter caseGraph(Graph object)
			{
				return createGraphAdapter();
			}
			@Override
			public Adapter caseGroup(Group object)
			{
				return createGroupAdapter();
			}
			@Override
			public Adapter caseIdentifiable(Identifiable object)
			{
				return createIdentifiableAdapter();
			}
			@Override
			public Adapter caseInterGraphEdge(InterGraphEdge object)
			{
				return createInterGraphEdgeAdapter();
			}
			@Override
			public Adapter caseInterGraphVertex(InterGraphVertex object)
			{
				return createInterGraphVertexAdapter();
			}
			@Override
			public Adapter caseMaintainGoal(MaintainGoal object)
			{
				return createMaintainGoalAdapter();
			}
			@Override
			public Adapter caseMessageGoal(MessageGoal object)
			{
				return createMessageGoalAdapter();
			}
			@Override
			public Adapter caseMessagingEdge(MessagingEdge object)
			{
				return createMessagingEdgeAdapter();
			}
			@Override
			public Adapter caseNamedObject(NamedObject object)
			{
				return createNamedObjectAdapter();
			}
			@Override
			public Adapter caseParallelGoal(ParallelGoal object)
			{
				return createParallelGoalAdapter();
			}
			@Override
			public Adapter caseParameter(Parameter object)
			{
				return createParameterAdapter();
			}
			@Override
			public Adapter caseParameterizedEdge(ParameterizedEdge object)
			{
				return createParameterizedEdgeAdapter();
			}
			@Override
			public Adapter caseParameterizedVertex(ParameterizedVertex object)
			{
				return createParameterizedVertexAdapter();
			}
			@Override
			public Adapter casePerformGoal(PerformGoal object)
			{
				return createPerformGoalAdapter();
			}
			@Override
			public Adapter casePlan(Plan object)
			{
				return createPlanAdapter();
			}
			@Override
			public Adapter casePlanEdge(PlanEdge object)
			{
				return createPlanEdgeAdapter();
			}
			@Override
			public Adapter caseProcess(jadex.tools.gpmn.Process object)
			{
				return createProcessAdapter();
			}
			@Override
			public Adapter caseQueryGoal(QueryGoal object)
			{
				return createQueryGoalAdapter();
			}
			@Override
			public Adapter caseRole(Role object)
			{
				return createRoleAdapter();
			}
			@Override
			public Adapter caseSequentialGoal(SequentialGoal object)
			{
				return createSequentialGoalAdapter();
			}
			@Override
			public Adapter caseSubGoalEdge(SubGoalEdge object)
			{
				return createSubGoalEdgeAdapter();
			}
			@Override
			public Adapter caseSubProcessGoal(SubProcessGoal object)
			{
				return createSubProcessGoalAdapter();
			}
			@Override
			public Adapter caseTextAnnotation(TextAnnotation object)
			{
				return createTextAnnotationAdapter();
			}
			@Override
			public Adapter caseVertex(Vertex object)
			{
				return createVertexAdapter();
			}
			@Override
			public Adapter caseGenericGpmnElement(GenericGpmnElement object)
			{
				return createGenericGpmnElementAdapter();
			}
			@Override
			public Adapter caseGenericGpmnEdge(GenericGpmnEdge object)
			{
				return createGenericGpmnEdgeAdapter();
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.AchieveGoal <em>Achieve Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.AchieveGoal
	 * @generated
	 */
	public Adapter createAchieveGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Artifact <em>Artifact</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Artifact
	 * @generated
	 */
	public Adapter createArtifactAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ArtifactsContainer <em>Artifacts Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ArtifactsContainer
	 * @generated
	 */
	public Adapter createArtifactsContainerAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Association <em>Association</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Association
	 * @generated
	 */
	public Adapter createAssociationAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.AssociationTarget <em>Association Target</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.AssociationTarget
	 * @generated
	 */
	public Adapter createAssociationTargetAdapter()
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.DataObject <em>Data Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.DataObject
	 * @generated
	 */
	public Adapter createDataObjectAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Edge <em>Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Edge
	 * @generated
	 */
	public Adapter createEdgeAdapter()
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Graph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Graph
	 * @generated
	 */
	public Adapter createGraphAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Group <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Group
	 * @generated
	 */
	public Adapter createGroupAdapter()
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.InterGraphEdge <em>Inter Graph Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.InterGraphEdge
	 * @generated
	 */
	public Adapter createInterGraphEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.InterGraphVertex <em>Inter Graph Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.InterGraphVertex
	 * @generated
	 */
	public Adapter createInterGraphVertexAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.MaintainGoal <em>Maintain Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.MaintainGoal
	 * @generated
	 */
	public Adapter createMaintainGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.MessageGoal <em>Message Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.MessageGoal
	 * @generated
	 */
	public Adapter createMessageGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.MessagingEdge <em>Messaging Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.MessagingEdge
	 * @generated
	 */
	public Adapter createMessagingEdgeAdapter()
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ParallelGoal <em>Parallel Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ParallelGoal
	 * @generated
	 */
	public Adapter createParallelGoalAdapter()
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ParameterizedEdge <em>Parameterized Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ParameterizedEdge
	 * @generated
	 */
	public Adapter createParameterizedEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.ParameterizedVertex <em>Parameterized Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.ParameterizedVertex
	 * @generated
	 */
	public Adapter createParameterizedVertexAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.PerformGoal <em>Perform Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.PerformGoal
	 * @generated
	 */
	public Adapter createPerformGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Plan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Plan
	 * @generated
	 */
	public Adapter createPlanAdapter()
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
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Process <em>Process</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Process
	 * @generated
	 */
	public Adapter createProcessAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.QueryGoal <em>Query Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.QueryGoal
	 * @generated
	 */
	public Adapter createQueryGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Role <em>Role</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Role
	 * @generated
	 */
	public Adapter createRoleAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.SequentialGoal <em>Sequential Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.SequentialGoal
	 * @generated
	 */
	public Adapter createSequentialGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.SubGoalEdge <em>Sub Goal Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.SubGoalEdge
	 * @generated
	 */
	public Adapter createSubGoalEdgeAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.SubProcessGoal <em>Sub Process Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.SubProcessGoal
	 * @generated
	 */
	public Adapter createSubProcessGoalAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.TextAnnotation <em>Text Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.TextAnnotation
	 * @generated
	 */
	public Adapter createTextAnnotationAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.Vertex <em>Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.Vertex
	 * @generated
	 */
	public Adapter createVertexAdapter()
	{
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.GenericGpmnElement <em>Generic Gpmn Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.GenericGpmnElement
	 * @generated
	 */
	public Adapter createGenericGpmnElementAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link jadex.tools.gpmn.GenericGpmnEdge <em>Generic Gpmn Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see jadex.tools.gpmn.GenericGpmnEdge
	 * @generated
	 */
	public Adapter createGenericGpmnEdgeAdapter() {
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
