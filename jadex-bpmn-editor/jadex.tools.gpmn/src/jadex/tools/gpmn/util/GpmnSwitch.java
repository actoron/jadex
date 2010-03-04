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
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";
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
			case GpmnPackage.ACHIEVE_GOAL:
			{
				AchieveGoal achieveGoal = (AchieveGoal)theEObject;
				T result = caseAchieveGoal(achieveGoal);
				if (result == null) result = caseGoal(achieveGoal);
				if (result == null) result = caseParameterizedVertex(achieveGoal);
				if (result == null) result = caseVertex(achieveGoal);
				if (result == null) result = caseAssociationTarget(achieveGoal);
				if (result == null) result = caseNamedObject(achieveGoal);
				if (result == null) result = caseIdentifiable(achieveGoal);
				if (result == null) result = caseEModelElement(achieveGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ARTIFACT:
			{
				Artifact artifact = (Artifact)theEObject;
				T result = caseArtifact(artifact);
				if (result == null) result = caseNamedObject(artifact);
				if (result == null) result = caseIdentifiable(artifact);
				if (result == null) result = caseEModelElement(artifact);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ARTIFACTS_CONTAINER:
			{
				ArtifactsContainer artifactsContainer = (ArtifactsContainer)theEObject;
				T result = caseArtifactsContainer(artifactsContainer);
				if (result == null) result = caseNamedObject(artifactsContainer);
				if (result == null) result = caseIdentifiable(artifactsContainer);
				if (result == null) result = caseEModelElement(artifactsContainer);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ASSOCIATION:
			{
				Association association = (Association)theEObject;
				T result = caseAssociation(association);
				if (result == null) result = caseIdentifiable(association);
				if (result == null) result = caseEModelElement(association);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.ASSOCIATION_TARGET:
			{
				AssociationTarget associationTarget = (AssociationTarget)theEObject;
				T result = caseAssociationTarget(associationTarget);
				if (result == null) result = caseIdentifiable(associationTarget);
				if (result == null) result = caseEModelElement(associationTarget);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.CONTEXT:
			{
				Context context = (Context)theEObject;
				T result = caseContext(context);
				if (result == null) result = caseArtifact(context);
				if (result == null) result = caseNamedObject(context);
				if (result == null) result = caseIdentifiable(context);
				if (result == null) result = caseEModelElement(context);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.CONTEXT_ELEMENT:
			{
				ContextElement contextElement = (ContextElement)theEObject;
				T result = caseContextElement(contextElement);
				if (result == null) result = caseIdentifiable(contextElement);
				if (result == null) result = caseEModelElement(contextElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.DATA_OBJECT:
			{
				DataObject dataObject = (DataObject)theEObject;
				T result = caseDataObject(dataObject);
				if (result == null) result = caseArtifact(dataObject);
				if (result == null) result = caseNamedObject(dataObject);
				if (result == null) result = caseIdentifiable(dataObject);
				if (result == null) result = caseEModelElement(dataObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.EDGE:
			{
				Edge edge = (Edge)theEObject;
				T result = caseEdge(edge);
				if (result == null) result = caseAssociationTarget(edge);
				if (result == null) result = caseNamedObject(edge);
				if (result == null) result = caseIdentifiable(edge);
				if (result == null) result = caseEModelElement(edge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GOAL:
			{
				Goal goal = (Goal)theEObject;
				T result = caseGoal(goal);
				if (result == null) result = caseParameterizedVertex(goal);
				if (result == null) result = caseVertex(goal);
				if (result == null) result = caseAssociationTarget(goal);
				if (result == null) result = caseNamedObject(goal);
				if (result == null) result = caseIdentifiable(goal);
				if (result == null) result = caseEModelElement(goal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GPMN_DIAGRAM:
			{
				GpmnDiagram gpmnDiagram = (GpmnDiagram)theEObject;
				T result = caseGpmnDiagram(gpmnDiagram);
				if (result == null) result = caseGraph(gpmnDiagram);
				if (result == null) result = caseArtifactsContainer(gpmnDiagram);
				if (result == null) result = caseAssociationTarget(gpmnDiagram);
				if (result == null) result = caseNamedObject(gpmnDiagram);
				if (result == null) result = caseIdentifiable(gpmnDiagram);
				if (result == null) result = caseEModelElement(gpmnDiagram);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GRAPH:
			{
				Graph graph = (Graph)theEObject;
				T result = caseGraph(graph);
				if (result == null) result = caseArtifactsContainer(graph);
				if (result == null) result = caseAssociationTarget(graph);
				if (result == null) result = caseNamedObject(graph);
				if (result == null) result = caseIdentifiable(graph);
				if (result == null) result = caseEModelElement(graph);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GROUP:
			{
				Group group = (Group)theEObject;
				T result = caseGroup(group);
				if (result == null) result = caseNamedObject(group);
				if (result == null) result = caseEModelElement(group);
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
			case GpmnPackage.INTER_GRAPH_EDGE:
			{
				InterGraphEdge interGraphEdge = (InterGraphEdge)theEObject;
				T result = caseInterGraphEdge(interGraphEdge);
				if (result == null) result = caseAssociationTarget(interGraphEdge);
				if (result == null) result = caseNamedObject(interGraphEdge);
				if (result == null) result = caseIdentifiable(interGraphEdge);
				if (result == null) result = caseEModelElement(interGraphEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.INTER_GRAPH_VERTEX:
			{
				InterGraphVertex interGraphVertex = (InterGraphVertex)theEObject;
				T result = caseInterGraphVertex(interGraphVertex);
				if (result == null) result = caseAssociationTarget(interGraphVertex);
				if (result == null) result = caseNamedObject(interGraphVertex);
				if (result == null) result = caseIdentifiable(interGraphVertex);
				if (result == null) result = caseEModelElement(interGraphVertex);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.MAINTAIN_GOAL:
			{
				MaintainGoal maintainGoal = (MaintainGoal)theEObject;
				T result = caseMaintainGoal(maintainGoal);
				if (result == null) result = caseGoal(maintainGoal);
				if (result == null) result = caseParameterizedVertex(maintainGoal);
				if (result == null) result = caseVertex(maintainGoal);
				if (result == null) result = caseAssociationTarget(maintainGoal);
				if (result == null) result = caseNamedObject(maintainGoal);
				if (result == null) result = caseIdentifiable(maintainGoal);
				if (result == null) result = caseEModelElement(maintainGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.MESSAGE_GOAL:
			{
				MessageGoal messageGoal = (MessageGoal)theEObject;
				T result = caseMessageGoal(messageGoal);
				if (result == null) result = caseGoal(messageGoal);
				if (result == null) result = caseInterGraphVertex(messageGoal);
				if (result == null) result = caseParameterizedVertex(messageGoal);
				if (result == null) result = caseVertex(messageGoal);
				if (result == null) result = caseAssociationTarget(messageGoal);
				if (result == null) result = caseNamedObject(messageGoal);
				if (result == null) result = caseIdentifiable(messageGoal);
				if (result == null) result = caseEModelElement(messageGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.MESSAGING_EDGE:
			{
				MessagingEdge messagingEdge = (MessagingEdge)theEObject;
				T result = caseMessagingEdge(messagingEdge);
				if (result == null) result = caseInterGraphEdge(messagingEdge);
				if (result == null) result = caseAssociationTarget(messagingEdge);
				if (result == null) result = caseNamedObject(messagingEdge);
				if (result == null) result = caseIdentifiable(messagingEdge);
				if (result == null) result = caseEModelElement(messagingEdge);
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
			case GpmnPackage.PARALLEL_GOAL:
			{
				ParallelGoal parallelGoal = (ParallelGoal)theEObject;
				T result = caseParallelGoal(parallelGoal);
				if (result == null) result = caseGoal(parallelGoal);
				if (result == null) result = caseParameterizedVertex(parallelGoal);
				if (result == null) result = caseVertex(parallelGoal);
				if (result == null) result = caseAssociationTarget(parallelGoal);
				if (result == null) result = caseNamedObject(parallelGoal);
				if (result == null) result = caseIdentifiable(parallelGoal);
				if (result == null) result = caseEModelElement(parallelGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PARAMETER:
			{
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PARAMETERIZED_EDGE:
			{
				ParameterizedEdge parameterizedEdge = (ParameterizedEdge)theEObject;
				T result = caseParameterizedEdge(parameterizedEdge);
				if (result == null) result = caseEdge(parameterizedEdge);
				if (result == null) result = caseAssociationTarget(parameterizedEdge);
				if (result == null) result = caseNamedObject(parameterizedEdge);
				if (result == null) result = caseIdentifiable(parameterizedEdge);
				if (result == null) result = caseEModelElement(parameterizedEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PARAMETERIZED_VERTEX:
			{
				ParameterizedVertex parameterizedVertex = (ParameterizedVertex)theEObject;
				T result = caseParameterizedVertex(parameterizedVertex);
				if (result == null) result = caseVertex(parameterizedVertex);
				if (result == null) result = caseAssociationTarget(parameterizedVertex);
				if (result == null) result = caseNamedObject(parameterizedVertex);
				if (result == null) result = caseIdentifiable(parameterizedVertex);
				if (result == null) result = caseEModelElement(parameterizedVertex);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PERFORM_GOAL:
			{
				PerformGoal performGoal = (PerformGoal)theEObject;
				T result = casePerformGoal(performGoal);
				if (result == null) result = caseGoal(performGoal);
				if (result == null) result = caseParameterizedVertex(performGoal);
				if (result == null) result = caseVertex(performGoal);
				if (result == null) result = caseAssociationTarget(performGoal);
				if (result == null) result = caseNamedObject(performGoal);
				if (result == null) result = caseIdentifiable(performGoal);
				if (result == null) result = caseEModelElement(performGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PLAN:
			{
				Plan plan = (Plan)theEObject;
				T result = casePlan(plan);
				if (result == null) result = caseParameterizedVertex(plan);
				if (result == null) result = caseVertex(plan);
				if (result == null) result = caseAssociationTarget(plan);
				if (result == null) result = caseNamedObject(plan);
				if (result == null) result = caseIdentifiable(plan);
				if (result == null) result = caseEModelElement(plan);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PLAN_EDGE:
			{
				PlanEdge planEdge = (PlanEdge)theEObject;
				T result = casePlanEdge(planEdge);
				if (result == null) result = caseParameterizedEdge(planEdge);
				if (result == null) result = caseEdge(planEdge);
				if (result == null) result = caseAssociationTarget(planEdge);
				if (result == null) result = caseNamedObject(planEdge);
				if (result == null) result = caseIdentifiable(planEdge);
				if (result == null) result = caseEModelElement(planEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.PROCESS:
			{
				jadex.tools.gpmn.Process process = (jadex.tools.gpmn.Process)theEObject;
				T result = caseProcess(process);
				if (result == null) result = caseGraph(process);
				if (result == null) result = caseInterGraphVertex(process);
				if (result == null) result = caseArtifactsContainer(process);
				if (result == null) result = caseAssociationTarget(process);
				if (result == null) result = caseNamedObject(process);
				if (result == null) result = caseIdentifiable(process);
				if (result == null) result = caseEModelElement(process);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.QUERY_GOAL:
			{
				QueryGoal queryGoal = (QueryGoal)theEObject;
				T result = caseQueryGoal(queryGoal);
				if (result == null) result = caseGoal(queryGoal);
				if (result == null) result = caseParameterizedVertex(queryGoal);
				if (result == null) result = caseVertex(queryGoal);
				if (result == null) result = caseAssociationTarget(queryGoal);
				if (result == null) result = caseNamedObject(queryGoal);
				if (result == null) result = caseIdentifiable(queryGoal);
				if (result == null) result = caseEModelElement(queryGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.SEQUENTIAL_GOAL:
			{
				SequentialGoal sequentialGoal = (SequentialGoal)theEObject;
				T result = caseSequentialGoal(sequentialGoal);
				if (result == null) result = caseGoal(sequentialGoal);
				if (result == null) result = caseParameterizedVertex(sequentialGoal);
				if (result == null) result = caseVertex(sequentialGoal);
				if (result == null) result = caseAssociationTarget(sequentialGoal);
				if (result == null) result = caseNamedObject(sequentialGoal);
				if (result == null) result = caseIdentifiable(sequentialGoal);
				if (result == null) result = caseEModelElement(sequentialGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.SUB_GOAL_EDGE:
			{
				SubGoalEdge subGoalEdge = (SubGoalEdge)theEObject;
				T result = caseSubGoalEdge(subGoalEdge);
				if (result == null) result = caseParameterizedEdge(subGoalEdge);
				if (result == null) result = caseEdge(subGoalEdge);
				if (result == null) result = caseAssociationTarget(subGoalEdge);
				if (result == null) result = caseNamedObject(subGoalEdge);
				if (result == null) result = caseIdentifiable(subGoalEdge);
				if (result == null) result = caseEModelElement(subGoalEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.SUB_PROCESS_GOAL:
			{
				SubProcessGoal subProcessGoal = (SubProcessGoal)theEObject;
				T result = caseSubProcessGoal(subProcessGoal);
				if (result == null) result = caseGoal(subProcessGoal);
				if (result == null) result = caseParameterizedVertex(subProcessGoal);
				if (result == null) result = caseVertex(subProcessGoal);
				if (result == null) result = caseAssociationTarget(subProcessGoal);
				if (result == null) result = caseNamedObject(subProcessGoal);
				if (result == null) result = caseIdentifiable(subProcessGoal);
				if (result == null) result = caseEModelElement(subProcessGoal);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.TEXT_ANNOTATION:
			{
				TextAnnotation textAnnotation = (TextAnnotation)theEObject;
				T result = caseTextAnnotation(textAnnotation);
				if (result == null) result = caseArtifact(textAnnotation);
				if (result == null) result = caseNamedObject(textAnnotation);
				if (result == null) result = caseIdentifiable(textAnnotation);
				if (result == null) result = caseEModelElement(textAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.VERTEX:
			{
				Vertex vertex = (Vertex)theEObject;
				T result = caseVertex(vertex);
				if (result == null) result = caseAssociationTarget(vertex);
				if (result == null) result = caseNamedObject(vertex);
				if (result == null) result = caseIdentifiable(vertex);
				if (result == null) result = caseEModelElement(vertex);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GENERIC_GPMN_ELEMENT:
			{
				GenericGpmnElement genericGpmnElement = (GenericGpmnElement)theEObject;
				T result = caseGenericGpmnElement(genericGpmnElement);
				if (result == null) result = caseParameterizedVertex(genericGpmnElement);
				if (result == null) result = caseVertex(genericGpmnElement);
				if (result == null) result = caseAssociationTarget(genericGpmnElement);
				if (result == null) result = caseNamedObject(genericGpmnElement);
				if (result == null) result = caseIdentifiable(genericGpmnElement);
				if (result == null) result = caseEModelElement(genericGpmnElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case GpmnPackage.GENERIC_GPMN_EDGE:
			{
				GenericGpmnEdge genericGpmnEdge = (GenericGpmnEdge)theEObject;
				T result = caseGenericGpmnEdge(genericGpmnEdge);
				if (result == null) result = caseParameterizedEdge(genericGpmnEdge);
				if (result == null) result = caseEdge(genericGpmnEdge);
				if (result == null) result = caseAssociationTarget(genericGpmnEdge);
				if (result == null) result = caseNamedObject(genericGpmnEdge);
				if (result == null) result = caseIdentifiable(genericGpmnEdge);
				if (result == null) result = caseEModelElement(genericGpmnEdge);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Achieve Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Achieve Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAchieveGoal(AchieveGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Artifact</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Artifact</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseArtifact(Artifact object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Artifacts Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Artifacts Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseArtifactsContainer(ArtifactsContainer object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Association</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Association</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAssociation(Association object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Association Target</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Association Target</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAssociationTarget(AssociationTarget object)
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
	 * Returns the result of interpreting the object as an instance of '<em>Data Object</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataObject(DataObject object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEdge(Edge object)
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
	 * Returns the result of interpreting the object as an instance of '<em>Graph</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Graph</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGraph(Graph object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Group</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Group</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGroup(Group object)
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
	 * Returns the result of interpreting the object as an instance of '<em>Inter Graph Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Inter Graph Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInterGraphEdge(InterGraphEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Inter Graph Vertex</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Inter Graph Vertex</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInterGraphVertex(InterGraphVertex object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Maintain Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Maintain Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMaintainGoal(MaintainGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Message Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Message Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMessageGoal(MessageGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Messaging Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Messaging Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMessagingEdge(MessagingEdge object)
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
	 * Returns the result of interpreting the object as an instance of '<em>Parallel Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parallel Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParallelGoal(ParallelGoal object)
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
	 * Returns the result of interpreting the object as an instance of '<em>Parameterized Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameterized Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterizedEdge(ParameterizedEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameterized Vertex</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameterized Vertex</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterizedVertex(ParameterizedVertex object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Perform Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Perform Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePerformGoal(PerformGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlan(Plan object)
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
	 * Returns the result of interpreting the object as an instance of '<em>Process</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Process</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProcess(jadex.tools.gpmn.Process object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Query Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Query Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseQueryGoal(QueryGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sequential Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sequential Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSequentialGoal(SequentialGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sub Goal Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sub Goal Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSubGoalEdge(SubGoalEdge object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sub Process Goal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sub Process Goal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSubProcessGoal(SubProcessGoal object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Text Annotation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Text Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTextAnnotation(TextAnnotation object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Vertex</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Vertex</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVertex(Vertex object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Generic Gpmn Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Generic Gpmn Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGenericGpmnElement(GenericGpmnElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Generic Gpmn Edge</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Generic Gpmn Edge</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGenericGpmnEdge(GenericGpmnEdge object) {
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
