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
package jadex.tools.gpmn.impl;

import jadex.tools.gpmn.AchieveGoal;
import jadex.tools.gpmn.Artifact;
import jadex.tools.gpmn.ArtifactsContainer;
import jadex.tools.gpmn.Association;
import jadex.tools.gpmn.AssociationTarget;
import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.ContextElement;
import jadex.tools.gpmn.DataObject;
import jadex.tools.gpmn.DirectionType;
import jadex.tools.gpmn.Edge;
import jadex.tools.gpmn.EdgeType;
import jadex.tools.gpmn.ExcludeType;
import jadex.tools.gpmn.GenericGpmnEdge;
import jadex.tools.gpmn.GenericGpmnElement;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnFactory;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EcoreUtil;

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
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

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
			GpmnFactory theGpmnFactory = (GpmnFactory)EPackage.Registry.INSTANCE.getEFactory("http://jadex.sourceforge.net/gpmn"); 
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
			case GpmnPackage.ACHIEVE_GOAL: return createAchieveGoal();
			case GpmnPackage.ARTIFACT: return createArtifact();
			case GpmnPackage.ARTIFACTS_CONTAINER: return createArtifactsContainer();
			case GpmnPackage.ASSOCIATION: return createAssociation();
			case GpmnPackage.ASSOCIATION_TARGET: return createAssociationTarget();
			case GpmnPackage.CONTEXT: return createContext();
			case GpmnPackage.CONTEXT_ELEMENT: return createContextElement();
			case GpmnPackage.DATA_OBJECT: return createDataObject();
			case GpmnPackage.EDGE: return createEdge();
			case GpmnPackage.GOAL: return createGoal();
			case GpmnPackage.GPMN_DIAGRAM: return createGpmnDiagram();
			case GpmnPackage.GRAPH: return createGraph();
			case GpmnPackage.GROUP: return createGroup();
			case GpmnPackage.IDENTIFIABLE: return createIdentifiable();
			case GpmnPackage.INTER_GRAPH_EDGE: return createInterGraphEdge();
			case GpmnPackage.INTER_GRAPH_VERTEX: return createInterGraphVertex();
			case GpmnPackage.MAINTAIN_GOAL: return createMaintainGoal();
			case GpmnPackage.MESSAGE_GOAL: return createMessageGoal();
			case GpmnPackage.MESSAGING_EDGE: return createMessagingEdge();
			case GpmnPackage.NAMED_OBJECT: return createNamedObject();
			case GpmnPackage.PARALLEL_GOAL: return createParallelGoal();
			case GpmnPackage.PARAMETER: return createParameter();
			case GpmnPackage.PARAMETERIZED_EDGE: return createParameterizedEdge();
			case GpmnPackage.PARAMETERIZED_VERTEX: return createParameterizedVertex();
			case GpmnPackage.PERFORM_GOAL: return createPerformGoal();
			case GpmnPackage.PLAN: return createPlan();
			case GpmnPackage.PLAN_EDGE: return createPlanEdge();
			case GpmnPackage.PROCESS: return createProcess();
			case GpmnPackage.QUERY_GOAL: return createQueryGoal();
			case GpmnPackage.ROLE: return createRole();
			case GpmnPackage.SEQUENTIAL_GOAL: return createSequentialGoal();
			case GpmnPackage.SUB_GOAL_EDGE: return createSubGoalEdge();
			case GpmnPackage.SUB_PROCESS_GOAL: return createSubProcessGoal();
			case GpmnPackage.TEXT_ANNOTATION: return createTextAnnotation();
			case GpmnPackage.VERTEX: return createVertex();
			case GpmnPackage.GENERIC_GPMN_ELEMENT: return createGenericGpmnElement();
			case GpmnPackage.GENERIC_GPMN_EDGE: return createGenericGpmnEdge();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
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
			case GpmnPackage.DIRECTION_TYPE:
				return createDirectionTypeFromString(eDataType, initialValue);
			case GpmnPackage.EDGE_TYPE:
				return createEdgeTypeFromString(eDataType, initialValue);
			case GpmnPackage.EXCLUDE_TYPE:
				return createExcludeTypeFromString(eDataType, initialValue);
			case GpmnPackage.GOAL_TYPE:
				return createGoalTypeFromString(eDataType, initialValue);
			case GpmnPackage.DIRECTION_TYPE_OBJECT:
				return createDirectionTypeObjectFromString(eDataType, initialValue);
			case GpmnPackage.EDGE_TYPE_OBJECT:
				return createEdgeTypeObjectFromString(eDataType, initialValue);
			case GpmnPackage.EXCLUDE_TYPE_OBJECT:
				return createExcludeTypeObjectFromString(eDataType, initialValue);
			case GpmnPackage.GOAL_TYPE_OBJECT:
				return createGoalTypeObjectFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
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
			case GpmnPackage.DIRECTION_TYPE:
				return convertDirectionTypeToString(eDataType, instanceValue);
			case GpmnPackage.EDGE_TYPE:
				return convertEdgeTypeToString(eDataType, instanceValue);
			case GpmnPackage.EXCLUDE_TYPE:
				return convertExcludeTypeToString(eDataType, instanceValue);
			case GpmnPackage.GOAL_TYPE:
				return convertGoalTypeToString(eDataType, instanceValue);
			case GpmnPackage.DIRECTION_TYPE_OBJECT:
				return convertDirectionTypeObjectToString(eDataType, instanceValue);
			case GpmnPackage.EDGE_TYPE_OBJECT:
				return convertEdgeTypeObjectToString(eDataType, instanceValue);
			case GpmnPackage.EXCLUDE_TYPE_OBJECT:
				return convertExcludeTypeObjectToString(eDataType, instanceValue);
			case GpmnPackage.GOAL_TYPE_OBJECT:
				return convertGoalTypeObjectToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public AchieveGoal createAchieveGoal()
	{
		AchieveGoalImpl achieveGoal = new AchieveGoalImpl();
		achieveGoal.id = EcoreUtil.generateUUID();
		achieveGoal.goalType = GoalType.ACHIEVE_GOAL;
		return achieveGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Artifact createArtifact()
	{
		ArtifactImpl artifact = new ArtifactImpl();
		artifact.id = EcoreUtil.generateUUID();
		return artifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArtifactsContainer createArtifactsContainer()
	{
		ArtifactsContainerImpl artifactsContainer = new ArtifactsContainerImpl();
		return artifactsContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Association createAssociation()
	{
		AssociationImpl association = new AssociationImpl();
		return association;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public AssociationTarget createAssociationTarget()
	{
		AssociationTargetImpl associationTarget = new AssociationTargetImpl();
		associationTarget.id = EcoreUtil.generateUUID();
		return associationTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Context createContext()
	{
		ContextImpl context = new ContextImpl();
		context.id = EcoreUtil.generateUUID();
		return context;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ContextElement createContextElement()
	{
		ContextElementImpl contextElement = new ContextElementImpl();
		contextElement.id = EcoreUtil.generateUUID();
		return contextElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public DataObject createDataObject()
	{
		DataObjectImpl dataObject = new DataObjectImpl();
		dataObject.id = EcoreUtil.generateUUID();
		return dataObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @@generated NOT
	 */
	public Edge createEdge()
	{
		EdgeImpl edge = new EdgeImpl();
		edge.id = EcoreUtil.generateUUID();
		return edge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Goal createGoal()
	{
		GoalImpl goal = new GoalImpl();
		return goal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public GpmnDiagram createGpmnDiagram()
	{
		GpmnDiagramImpl gpmnDiagram = new GpmnDiagramImpl();
		gpmnDiagram.id = EcoreUtil.generateUUID();
		return gpmnDiagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Graph createGraph()
	{
		GraphImpl graph = new GraphImpl();
		graph.id = EcoreUtil.generateUUID();
		return graph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Group createGroup()
	{
		GroupImpl group = new GroupImpl();
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Identifiable createIdentifiable()
	{
		IdentifiableImpl identifiable = new IdentifiableImpl();
		identifiable.id = EcoreUtil.generateUUID();
		return identifiable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public InterGraphEdge createInterGraphEdge()
	{
		InterGraphEdgeImpl interGraphEdge = new InterGraphEdgeImpl();
		interGraphEdge.id = EcoreUtil.generateUUID();
		return interGraphEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public InterGraphVertex createInterGraphVertex()
	{
		InterGraphVertexImpl interGraphVertex = new InterGraphVertexImpl();
		interGraphVertex.id = EcoreUtil.generateUUID();
		return interGraphVertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public MaintainGoal createMaintainGoal()
	{
		MaintainGoalImpl maintainGoal = new MaintainGoalImpl();
		maintainGoal.id = EcoreUtil.generateUUID();
		maintainGoal.goalType = GoalType.MAINTAIN_GOAL;
		return maintainGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public MessageGoal createMessageGoal()
	{
		MessageGoalImpl messageGoal = new MessageGoalImpl();
		messageGoal.id = EcoreUtil.generateUUID();
		messageGoal.goalType = GoalType.MESSAGE_GOAL;
		return messageGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public MessagingEdge createMessagingEdge()
	{
		MessagingEdgeImpl messagingEdge = new MessagingEdgeImpl();
		messagingEdge.id = EcoreUtil.generateUUID();
		return messagingEdge;
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
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ParallelGoal createParallelGoal()
	{
		ParallelGoalImpl parallelGoal = new ParallelGoalImpl();
		parallelGoal.id = EcoreUtil.generateUUID();
		parallelGoal.goalType = GoalType.PARALLEL_GOAL;
		return parallelGoal;
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
	public ParameterizedEdge createParameterizedEdge()
	{
		ParameterizedEdgeImpl parameterizedEdge = new ParameterizedEdgeImpl();
		return parameterizedEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterizedVertex createParameterizedVertex()
	{
		ParameterizedVertexImpl parameterizedVertex = new ParameterizedVertexImpl();
		return parameterizedVertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public PerformGoal createPerformGoal()
	{
		PerformGoalImpl performGoal = new PerformGoalImpl();
		performGoal.id = EcoreUtil.generateUUID();
		performGoal.goalType = GoalType.PERFORM_GOAL;
		return performGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Plan createPlan()
	{
		PlanImpl plan = new PlanImpl();
		plan.id = EcoreUtil.generateUUID();
		return plan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public PlanEdge createPlanEdge()
	{
		PlanEdgeImpl planEdge = new PlanEdgeImpl();
		planEdge.id = EcoreUtil.generateUUID();
		return planEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public jadex.tools.gpmn.Process createProcess()
	{
		ProcessImpl process = new ProcessImpl();
		process.id = EcoreUtil.generateUUID();
		return process;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public QueryGoal createQueryGoal()
	{
		QueryGoalImpl queryGoal = new QueryGoalImpl();
		queryGoal.id = EcoreUtil.generateUUID();
		queryGoal.goalType = GoalType.QUERY_GOAL;
		return queryGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Role createRole()
	{
		RoleImpl role = new RoleImpl();
		return role;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public SequentialGoal createSequentialGoal()
	{
		SequentialGoalImpl sequentialGoal = new SequentialGoalImpl();
		sequentialGoal.id = EcoreUtil.generateUUID();
		sequentialGoal.goalType = GoalType.SEQUENTIAL_GOAL;
		return sequentialGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public SubGoalEdge createSubGoalEdge()
	{
		SubGoalEdgeImpl subGoalEdge = new SubGoalEdgeImpl();
		subGoalEdge.id = EcoreUtil.generateUUID();
		return subGoalEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID.<p>
	 * Initialize default GoalType.
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public SubProcessGoal createSubProcessGoal()
	{
		SubProcessGoalImpl subProcessGoal = new SubProcessGoalImpl();
		subProcessGoal.id = EcoreUtil.generateUUID();
		subProcessGoal.goalType = GoalType.SUB_PROCESS_GOAL;
		return subProcessGoal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public TextAnnotation createTextAnnotation()
	{
		TextAnnotationImpl textAnnotation = new TextAnnotationImpl();
		textAnnotation.id = EcoreUtil.generateUUID();
		return textAnnotation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Added UUID to support XML REFID
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Vertex createVertex()
	{
		VertexImpl vertex = new VertexImpl();
		vertex.id = EcoreUtil.generateUUID();
		return vertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericGpmnElement createGenericGpmnElement() {
		GenericGpmnElementImpl genericGpmnElement = new GenericGpmnElementImpl();
		return genericGpmnElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenericGpmnEdge createGenericGpmnEdge() {
		GenericGpmnEdgeImpl genericGpmnEdge = new GenericGpmnEdgeImpl();
		return genericGpmnEdge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirectionType createDirectionType(String literal)
	{
		DirectionType result = DirectionType.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.DIRECTION_TYPE.getName() + "'");
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
	public EdgeType createEdgeType(String literal)
	{
		EdgeType result = EdgeType.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.EDGE_TYPE.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EdgeType createEdgeTypeFromString(EDataType eDataType,
			String initialValue)
	{
		return createEdgeType(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEdgeType(EdgeType instanceValue)
	{
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEdgeTypeToString(EDataType eDataType,
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
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.EXCLUDE_TYPE.getName() + "'");
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
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + GpmnPackage.Literals.GOAL_TYPE.getName() + "'");
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
	public EdgeType createEdgeTypeObject(String literal)
	{
		return createEdgeType(literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EdgeType createEdgeTypeObjectFromString(EDataType eDataType,
			String initialValue)
	{
		return createEdgeTypeFromString(GpmnPackage.Literals.EDGE_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEdgeTypeObject(EdgeType instanceValue)
	{
		return convertEdgeType(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEdgeTypeObjectToString(EDataType eDataType,
			Object instanceValue)
	{
		return convertEdgeTypeToString(GpmnPackage.Literals.EDGE_TYPE, instanceValue);
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
