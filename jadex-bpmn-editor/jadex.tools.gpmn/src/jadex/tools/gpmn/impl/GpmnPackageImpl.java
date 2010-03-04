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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GpmnPackageImpl extends EPackageImpl implements GpmnPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass achieveGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass artifactEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass artifactsContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass associationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass associationTargetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contextEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contextElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass edgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass goalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gpmnDiagramEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass graphEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass groupEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass identifiableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interGraphEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interGraphVertexEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass maintainGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messageGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messagingEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namedObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parallelGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterizedEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterizedVertexEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass performGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass planEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass planEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass processEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass queryGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass roleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sequentialGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subGoalEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subProcessGoalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass textAnnotationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass vertexEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass genericGpmnElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass genericGpmnEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum directionTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum edgeTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum excludeTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum goalTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType directionTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType edgeTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType excludeTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType goalTypeObjectEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see jadex.tools.gpmn.GpmnPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GpmnPackageImpl()
	{
		super(eNS_URI, GpmnFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link GpmnPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GpmnPackage init()
	{
		if (isInited) return (GpmnPackage)EPackage.Registry.INSTANCE.getEPackage(GpmnPackage.eNS_URI);

		// Obtain or create and register package
		GpmnPackageImpl theGpmnPackage = (GpmnPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof GpmnPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new GpmnPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theGpmnPackage.createPackageContents();

		// Initialize created meta-data
		theGpmnPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theGpmnPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GpmnPackage.eNS_URI, theGpmnPackage);
		return theGpmnPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAchieveGoal()
	{
		return achieveGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAchieveGoal_Targetcondition()
	{
		return (EAttribute)achieveGoalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAchieveGoal_TargetconditionLanguage()
	{
		return (EAttribute)achieveGoalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAchieveGoal_Failurecondition()
	{
		return (EAttribute)achieveGoalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAchieveGoal_FailureconditionLanguage()
	{
		return (EAttribute)achieveGoalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getArtifact()
	{
		return artifactEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getArtifact_Associations()
	{
		return (EReference)artifactEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getArtifact_ArtifactsContainer()
	{
		return (EReference)artifactEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getArtifactsContainer()
	{
		return artifactsContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getArtifactsContainer_Artifacts()
	{
		return (EReference)artifactsContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAssociation()
	{
		return associationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssociation_Direction()
	{
		return (EAttribute)associationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAssociation_Source()
	{
		return (EReference)associationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAssociation_Target()
	{
		return (EReference)associationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAssociationTarget()
	{
		return associationTargetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAssociationTarget_Associations()
	{
		return (EReference)associationTargetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getContext()
	{
		return contextEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getContext_Elements()
	{
		return (EReference)contextEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getContext_Roles()
	{
		return (EReference)contextEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getContext_Groups()
	{
		return (EReference)contextEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContext_Types()
	{
		return (EAttribute)contextEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getContextElement()
	{
		return contextElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getContextElement_Context()
	{
		return (EReference)contextElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Dynamic()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_InitialValue()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Name()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Set()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Type()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataObject()
	{
		return dataObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEdge()
	{
		return edgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEdge_IsDefault()
	{
		return (EAttribute)edgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEdge_Source()
	{
		return (EReference)edgeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEdge_Target()
	{
		return (EReference)edgeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEdge_Graph()
	{
		return (EReference)edgeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGoal()
	{
		return goalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Unique()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Creationcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_CreationconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Contextcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_ContextconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Dropcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_DropconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recurcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Deliberation()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_OnSuccessHandler()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_OnSkipHandler()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_OnFailureHandler()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Exclude()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_GoalType()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Posttoall()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Randomselection()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recalculate()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recur()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recurdelay()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Retry()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Retrydelay()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Sequential()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGpmnDiagram()
	{
		return gpmnDiagramEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_Processes()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_Messages()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Imports()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Package()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGraph()
	{
		return graphEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGraph_Vertices()
	{
		return (EReference)graphEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGraph_SequenceEdges()
	{
		return (EReference)graphEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGroup()
	{
		return groupEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGroup_Members()
	{
		return (EAttribute)groupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGroup_Coordinator()
	{
		return (EAttribute)groupEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGroup_Head()
	{
		return (EAttribute)groupEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIdentifiable()
	{
		return identifiableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIdentifiable_Id()
	{
		return (EAttribute)identifiableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInterGraphEdge()
	{
		return interGraphEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterGraphEdge_Source()
	{
		return (EReference)interGraphEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterGraphEdge_Target()
	{
		return (EReference)interGraphEdgeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInterGraphVertex()
	{
		return interGraphVertexEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterGraphVertex_InterGraphMessages()
	{
		return (EAttribute)interGraphVertexEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterGraphVertex_IncomingInterGraphEdges()
	{
		return (EReference)interGraphVertexEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterGraphVertex_OutgoingInterGraphEdges()
	{
		return (EReference)interGraphVertexEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMaintainGoal()
	{
		return maintainGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMaintainGoal_Maintaincondition()
	{
		return (EAttribute)maintainGoalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMaintainGoal_MaintainconditionLanguage()
	{
		return (EAttribute)maintainGoalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMaintainGoal_Targetcondition()
	{
		return (EAttribute)maintainGoalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMaintainGoal_TargetconditionLanguage()
	{
		return (EAttribute)maintainGoalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMessageGoal()
	{
		return messageGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMessagingEdge()
	{
		return messagingEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMessagingEdge_Message()
	{
		return (EAttribute)messagingEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMessagingEdge_GpmnDiagram()
	{
		return (EReference)messagingEdgeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNamedObject()
	{
		return namedObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNamedObject_Description()
	{
		return (EAttribute)namedObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNamedObject_Name()
	{
		return (EAttribute)namedObjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNamedObject_Ncname()
	{
		return (EAttribute)namedObjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParallelGoal()
	{
		return parallelGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParallelGoal_Targetcondition()
	{
		return (EAttribute)parallelGoalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParallelGoal_TargetconditionLanguage()
	{
		return (EAttribute)parallelGoalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParallelGoal_Failurecondition()
	{
		return (EAttribute)parallelGoalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParallelGoal_FailureconditionLanguage()
	{
		return (EAttribute)parallelGoalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameter()
	{
		return parameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Direction()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Name()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Type()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Value()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameterizedEdge()
	{
		return parameterizedEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterizedEdge_ParameterMapping()
	{
		return (EAttribute)parameterizedEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameterizedVertex()
	{
		return parameterizedVertexEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getParameterizedVertex_Parameter()
	{
		return (EReference)parameterizedVertexEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPerformGoal()
	{
		return performGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPlan()
	{
		return planEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPlan_BpmnPlan()
	{
		return (EAttribute)planEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPlan_Priority()
	{
		return (EAttribute)planEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPlan_Precondition()
	{
		return (EAttribute)planEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPlan_Contextcondition()
	{
		return (EAttribute)planEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPlanEdge()
	{
		return planEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProcess()
	{
		return processEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProcess_Looping()
	{
		return (EAttribute)processEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getProcess_GpmnDiagram()
	{
		return (EReference)processEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getQueryGoal()
	{
		return queryGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getQueryGoal_Targetcondition()
	{
		return (EAttribute)queryGoalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getQueryGoal_TargetconditionLanguage()
	{
		return (EAttribute)queryGoalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getQueryGoal_Failurecondition()
	{
		return (EAttribute)queryGoalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getQueryGoal_FailureconditionLanguage()
	{
		return (EAttribute)queryGoalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRole()
	{
		return roleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRole_InitialPerson()
	{
		return (EAttribute)roleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRole_PersonType()
	{
		return (EAttribute)roleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSequentialGoal()
	{
		return sequentialGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSequentialGoal_Targetcondition()
	{
		return (EAttribute)sequentialGoalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSequentialGoal_TargetconditionLanguage()
	{
		return (EAttribute)sequentialGoalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSequentialGoal_Failurecondition()
	{
		return (EAttribute)sequentialGoalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSequentialGoal_FailureconditionLanguage()
	{
		return (EAttribute)sequentialGoalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubGoalEdge()
	{
		return subGoalEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSubGoalEdge_SequentialOrder()
	{
		return (EAttribute)subGoalEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubProcessGoal()
	{
		return subProcessGoalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSubProcessGoal_Goalref()
	{
		return (EAttribute)subProcessGoalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTextAnnotation()
	{
		return textAnnotationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getVertex()
	{
		return vertexEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getVertex_OutgoingEdges()
	{
		return (EReference)vertexEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getVertex_IncomingEdges()
	{
		return (EReference)vertexEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getVertex_Graph()
	{
		return (EReference)vertexEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGenericGpmnElement() {
		return genericGpmnElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGenericGpmnElement_Attributes() {
		return (EAttribute)genericGpmnElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGenericGpmnElement_Properties() {
		return (EAttribute)genericGpmnElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGenericGpmnEdge() {
		return genericGpmnEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDirectionType()
	{
		return directionTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getEdgeType()
	{
		return edgeTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getExcludeType()
	{
		return excludeTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getGoalType()
	{
		return goalTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getDirectionTypeObject()
	{
		return directionTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getEdgeTypeObject()
	{
		return edgeTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getExcludeTypeObject()
	{
		return excludeTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getGoalTypeObject()
	{
		return goalTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GpmnFactory getGpmnFactory()
	{
		return (GpmnFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents()
	{
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		achieveGoalEClass = createEClass(ACHIEVE_GOAL);
		createEAttribute(achieveGoalEClass, ACHIEVE_GOAL__TARGETCONDITION);
		createEAttribute(achieveGoalEClass, ACHIEVE_GOAL__TARGETCONDITION_LANGUAGE);
		createEAttribute(achieveGoalEClass, ACHIEVE_GOAL__FAILURECONDITION);
		createEAttribute(achieveGoalEClass, ACHIEVE_GOAL__FAILURECONDITION_LANGUAGE);

		artifactEClass = createEClass(ARTIFACT);
		createEReference(artifactEClass, ARTIFACT__ASSOCIATIONS);
		createEReference(artifactEClass, ARTIFACT__ARTIFACTS_CONTAINER);

		artifactsContainerEClass = createEClass(ARTIFACTS_CONTAINER);
		createEReference(artifactsContainerEClass, ARTIFACTS_CONTAINER__ARTIFACTS);

		associationEClass = createEClass(ASSOCIATION);
		createEAttribute(associationEClass, ASSOCIATION__DIRECTION);
		createEReference(associationEClass, ASSOCIATION__SOURCE);
		createEReference(associationEClass, ASSOCIATION__TARGET);

		associationTargetEClass = createEClass(ASSOCIATION_TARGET);
		createEReference(associationTargetEClass, ASSOCIATION_TARGET__ASSOCIATIONS);

		contextEClass = createEClass(CONTEXT);
		createEReference(contextEClass, CONTEXT__ELEMENTS);
		createEReference(contextEClass, CONTEXT__ROLES);
		createEReference(contextEClass, CONTEXT__GROUPS);
		createEAttribute(contextEClass, CONTEXT__TYPES);

		contextElementEClass = createEClass(CONTEXT_ELEMENT);
		createEReference(contextElementEClass, CONTEXT_ELEMENT__CONTEXT);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__DYNAMIC);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__INITIAL_VALUE);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__NAME);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__SET);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__TYPE);

		dataObjectEClass = createEClass(DATA_OBJECT);

		edgeEClass = createEClass(EDGE);
		createEAttribute(edgeEClass, EDGE__IS_DEFAULT);
		createEReference(edgeEClass, EDGE__SOURCE);
		createEReference(edgeEClass, EDGE__TARGET);
		createEReference(edgeEClass, EDGE__GRAPH);

		goalEClass = createEClass(GOAL);
		createEAttribute(goalEClass, GOAL__UNIQUE);
		createEAttribute(goalEClass, GOAL__CREATIONCONDITION);
		createEAttribute(goalEClass, GOAL__CREATIONCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__CONTEXTCONDITION);
		createEAttribute(goalEClass, GOAL__CONTEXTCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__DROPCONDITION);
		createEAttribute(goalEClass, GOAL__DROPCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__RECURCONDITION);
		createEAttribute(goalEClass, GOAL__DELIBERATION);
		createEAttribute(goalEClass, GOAL__ON_SUCCESS_HANDLER);
		createEAttribute(goalEClass, GOAL__ON_SKIP_HANDLER);
		createEAttribute(goalEClass, GOAL__ON_FAILURE_HANDLER);
		createEAttribute(goalEClass, GOAL__EXCLUDE);
		createEAttribute(goalEClass, GOAL__GOAL_TYPE);
		createEAttribute(goalEClass, GOAL__POSTTOALL);
		createEAttribute(goalEClass, GOAL__RANDOMSELECTION);
		createEAttribute(goalEClass, GOAL__RECALCULATE);
		createEAttribute(goalEClass, GOAL__RECUR);
		createEAttribute(goalEClass, GOAL__RECURDELAY);
		createEAttribute(goalEClass, GOAL__RETRY);
		createEAttribute(goalEClass, GOAL__RETRYDELAY);
		createEAttribute(goalEClass, GOAL__SEQUENTIAL);

		gpmnDiagramEClass = createEClass(GPMN_DIAGRAM);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__PROCESSES);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__MESSAGES);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__IMPORTS);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__PACKAGE);

		graphEClass = createEClass(GRAPH);
		createEReference(graphEClass, GRAPH__VERTICES);
		createEReference(graphEClass, GRAPH__SEQUENCE_EDGES);

		groupEClass = createEClass(GROUP);
		createEAttribute(groupEClass, GROUP__MEMBERS);
		createEAttribute(groupEClass, GROUP__COORDINATOR);
		createEAttribute(groupEClass, GROUP__HEAD);

		identifiableEClass = createEClass(IDENTIFIABLE);
		createEAttribute(identifiableEClass, IDENTIFIABLE__ID);

		interGraphEdgeEClass = createEClass(INTER_GRAPH_EDGE);
		createEReference(interGraphEdgeEClass, INTER_GRAPH_EDGE__SOURCE);
		createEReference(interGraphEdgeEClass, INTER_GRAPH_EDGE__TARGET);

		interGraphVertexEClass = createEClass(INTER_GRAPH_VERTEX);
		createEAttribute(interGraphVertexEClass, INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES);
		createEReference(interGraphVertexEClass, INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES);
		createEReference(interGraphVertexEClass, INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES);

		maintainGoalEClass = createEClass(MAINTAIN_GOAL);
		createEAttribute(maintainGoalEClass, MAINTAIN_GOAL__MAINTAINCONDITION);
		createEAttribute(maintainGoalEClass, MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE);
		createEAttribute(maintainGoalEClass, MAINTAIN_GOAL__TARGETCONDITION);
		createEAttribute(maintainGoalEClass, MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE);

		messageGoalEClass = createEClass(MESSAGE_GOAL);

		messagingEdgeEClass = createEClass(MESSAGING_EDGE);
		createEAttribute(messagingEdgeEClass, MESSAGING_EDGE__MESSAGE);
		createEReference(messagingEdgeEClass, MESSAGING_EDGE__GPMN_DIAGRAM);

		namedObjectEClass = createEClass(NAMED_OBJECT);
		createEAttribute(namedObjectEClass, NAMED_OBJECT__DESCRIPTION);
		createEAttribute(namedObjectEClass, NAMED_OBJECT__NAME);
		createEAttribute(namedObjectEClass, NAMED_OBJECT__NCNAME);

		parallelGoalEClass = createEClass(PARALLEL_GOAL);
		createEAttribute(parallelGoalEClass, PARALLEL_GOAL__TARGETCONDITION);
		createEAttribute(parallelGoalEClass, PARALLEL_GOAL__TARGETCONDITION_LANGUAGE);
		createEAttribute(parallelGoalEClass, PARALLEL_GOAL__FAILURECONDITION);
		createEAttribute(parallelGoalEClass, PARALLEL_GOAL__FAILURECONDITION_LANGUAGE);

		parameterEClass = createEClass(PARAMETER);
		createEAttribute(parameterEClass, PARAMETER__DIRECTION);
		createEAttribute(parameterEClass, PARAMETER__NAME);
		createEAttribute(parameterEClass, PARAMETER__TYPE);
		createEAttribute(parameterEClass, PARAMETER__VALUE);

		parameterizedEdgeEClass = createEClass(PARAMETERIZED_EDGE);
		createEAttribute(parameterizedEdgeEClass, PARAMETERIZED_EDGE__PARAMETER_MAPPING);

		parameterizedVertexEClass = createEClass(PARAMETERIZED_VERTEX);
		createEReference(parameterizedVertexEClass, PARAMETERIZED_VERTEX__PARAMETER);

		performGoalEClass = createEClass(PERFORM_GOAL);

		planEClass = createEClass(PLAN);
		createEAttribute(planEClass, PLAN__BPMN_PLAN);
		createEAttribute(planEClass, PLAN__PRIORITY);
		createEAttribute(planEClass, PLAN__PRECONDITION);
		createEAttribute(planEClass, PLAN__CONTEXTCONDITION);

		planEdgeEClass = createEClass(PLAN_EDGE);

		processEClass = createEClass(PROCESS);
		createEAttribute(processEClass, PROCESS__LOOPING);
		createEReference(processEClass, PROCESS__GPMN_DIAGRAM);

		queryGoalEClass = createEClass(QUERY_GOAL);
		createEAttribute(queryGoalEClass, QUERY_GOAL__TARGETCONDITION);
		createEAttribute(queryGoalEClass, QUERY_GOAL__TARGETCONDITION_LANGUAGE);
		createEAttribute(queryGoalEClass, QUERY_GOAL__FAILURECONDITION);
		createEAttribute(queryGoalEClass, QUERY_GOAL__FAILURECONDITION_LANGUAGE);

		roleEClass = createEClass(ROLE);
		createEAttribute(roleEClass, ROLE__INITIAL_PERSON);
		createEAttribute(roleEClass, ROLE__PERSON_TYPE);

		sequentialGoalEClass = createEClass(SEQUENTIAL_GOAL);
		createEAttribute(sequentialGoalEClass, SEQUENTIAL_GOAL__TARGETCONDITION);
		createEAttribute(sequentialGoalEClass, SEQUENTIAL_GOAL__TARGETCONDITION_LANGUAGE);
		createEAttribute(sequentialGoalEClass, SEQUENTIAL_GOAL__FAILURECONDITION);
		createEAttribute(sequentialGoalEClass, SEQUENTIAL_GOAL__FAILURECONDITION_LANGUAGE);

		subGoalEdgeEClass = createEClass(SUB_GOAL_EDGE);
		createEAttribute(subGoalEdgeEClass, SUB_GOAL_EDGE__SEQUENTIAL_ORDER);

		subProcessGoalEClass = createEClass(SUB_PROCESS_GOAL);
		createEAttribute(subProcessGoalEClass, SUB_PROCESS_GOAL__GOALREF);

		textAnnotationEClass = createEClass(TEXT_ANNOTATION);

		vertexEClass = createEClass(VERTEX);
		createEReference(vertexEClass, VERTEX__OUTGOING_EDGES);
		createEReference(vertexEClass, VERTEX__INCOMING_EDGES);
		createEReference(vertexEClass, VERTEX__GRAPH);

		genericGpmnElementEClass = createEClass(GENERIC_GPMN_ELEMENT);
		createEAttribute(genericGpmnElementEClass, GENERIC_GPMN_ELEMENT__ATTRIBUTES);
		createEAttribute(genericGpmnElementEClass, GENERIC_GPMN_ELEMENT__PROPERTIES);

		genericGpmnEdgeEClass = createEClass(GENERIC_GPMN_EDGE);

		// Create enums
		directionTypeEEnum = createEEnum(DIRECTION_TYPE);
		edgeTypeEEnum = createEEnum(EDGE_TYPE);
		excludeTypeEEnum = createEEnum(EXCLUDE_TYPE);
		goalTypeEEnum = createEEnum(GOAL_TYPE);

		// Create data types
		directionTypeObjectEDataType = createEDataType(DIRECTION_TYPE_OBJECT);
		edgeTypeObjectEDataType = createEDataType(EDGE_TYPE_OBJECT);
		excludeTypeObjectEDataType = createEDataType(EXCLUDE_TYPE_OBJECT);
		goalTypeObjectEDataType = createEDataType(GOAL_TYPE_OBJECT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents()
	{
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		achieveGoalEClass.getESuperTypes().add(this.getGoal());
		artifactEClass.getESuperTypes().add(this.getNamedObject());
		artifactEClass.getESuperTypes().add(this.getIdentifiable());
		artifactsContainerEClass.getESuperTypes().add(this.getNamedObject());
		artifactsContainerEClass.getESuperTypes().add(this.getIdentifiable());
		associationEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		associationEClass.getESuperTypes().add(this.getIdentifiable());
		associationTargetEClass.getESuperTypes().add(this.getIdentifiable());
		contextEClass.getESuperTypes().add(this.getArtifact());
		contextEClass.getESuperTypes().add(this.getIdentifiable());
		contextElementEClass.getESuperTypes().add(this.getIdentifiable());
		dataObjectEClass.getESuperTypes().add(this.getArtifact());
		edgeEClass.getESuperTypes().add(this.getAssociationTarget());
		edgeEClass.getESuperTypes().add(this.getNamedObject());
		goalEClass.getESuperTypes().add(this.getParameterizedVertex());
		gpmnDiagramEClass.getESuperTypes().add(this.getGraph());
		gpmnDiagramEClass.getESuperTypes().add(this.getIdentifiable());
		graphEClass.getESuperTypes().add(this.getArtifactsContainer());
		graphEClass.getESuperTypes().add(this.getAssociationTarget());
		groupEClass.getESuperTypes().add(this.getNamedObject());
		identifiableEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		interGraphEdgeEClass.getESuperTypes().add(this.getAssociationTarget());
		interGraphEdgeEClass.getESuperTypes().add(this.getNamedObject());
		interGraphVertexEClass.getESuperTypes().add(this.getAssociationTarget());
		interGraphVertexEClass.getESuperTypes().add(this.getNamedObject());
		maintainGoalEClass.getESuperTypes().add(this.getGoal());
		messageGoalEClass.getESuperTypes().add(this.getGoal());
		messageGoalEClass.getESuperTypes().add(this.getInterGraphVertex());
		messagingEdgeEClass.getESuperTypes().add(this.getInterGraphEdge());
		messagingEdgeEClass.getESuperTypes().add(this.getNamedObject());
		namedObjectEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		parallelGoalEClass.getESuperTypes().add(this.getGoal());
		parameterizedEdgeEClass.getESuperTypes().add(this.getEdge());
		parameterizedEdgeEClass.getESuperTypes().add(this.getNamedObject());
		parameterizedVertexEClass.getESuperTypes().add(this.getVertex());
		performGoalEClass.getESuperTypes().add(this.getGoal());
		planEClass.getESuperTypes().add(this.getParameterizedVertex());
		planEdgeEClass.getESuperTypes().add(this.getParameterizedEdge());
		planEdgeEClass.getESuperTypes().add(this.getNamedObject());
		processEClass.getESuperTypes().add(this.getGraph());
		processEClass.getESuperTypes().add(this.getInterGraphVertex());
		queryGoalEClass.getESuperTypes().add(this.getGoal());
		roleEClass.getESuperTypes().add(this.getNamedObject());
		sequentialGoalEClass.getESuperTypes().add(this.getGoal());
		subGoalEdgeEClass.getESuperTypes().add(this.getParameterizedEdge());
		subGoalEdgeEClass.getESuperTypes().add(this.getNamedObject());
		subProcessGoalEClass.getESuperTypes().add(this.getGoal());
		textAnnotationEClass.getESuperTypes().add(this.getArtifact());
		vertexEClass.getESuperTypes().add(this.getAssociationTarget());
		vertexEClass.getESuperTypes().add(this.getNamedObject());
		genericGpmnElementEClass.getESuperTypes().add(this.getParameterizedVertex());
		genericGpmnEdgeEClass.getESuperTypes().add(this.getParameterizedEdge());
		genericGpmnEdgeEClass.getESuperTypes().add(this.getNamedObject());

		// Initialize classes and features; add operations and parameters
		initEClass(achieveGoalEClass, AchieveGoal.class, "AchieveGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAchieveGoal_Targetcondition(), theXMLTypePackage.getString(), "targetcondition", null, 0, 1, AchieveGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAchieveGoal_TargetconditionLanguage(), theXMLTypePackage.getString(), "targetconditionLanguage", "jcl", 0, 1, AchieveGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAchieveGoal_Failurecondition(), theXMLTypePackage.getString(), "failurecondition", null, 0, 1, AchieveGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAchieveGoal_FailureconditionLanguage(), theXMLTypePackage.getString(), "failureconditionLanguage", "jcl", 0, 1, AchieveGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(artifactEClass, Artifact.class, "Artifact", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getArtifact_Associations(), this.getAssociation(), this.getAssociation_Source(), "associations", null, 0, -1, Artifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getArtifact_ArtifactsContainer(), this.getArtifactsContainer(), this.getArtifactsContainer_Artifacts(), "artifactsContainer", null, 0, 1, Artifact.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(artifactsContainerEClass, ArtifactsContainer.class, "ArtifactsContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getArtifactsContainer_Artifacts(), this.getArtifact(), this.getArtifact_ArtifactsContainer(), "artifacts", null, 0, -1, ArtifactsContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(associationEClass, Association.class, "Association", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAssociation_Direction(), this.getDirectionType(), "direction", "in", 0, 1, Association.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAssociation_Source(), this.getArtifact(), this.getArtifact_Associations(), "source", null, 0, 1, Association.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAssociation_Target(), this.getAssociationTarget(), this.getAssociationTarget_Associations(), "target", null, 0, 1, Association.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(associationTargetEClass, AssociationTarget.class, "AssociationTarget", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAssociationTarget_Associations(), this.getAssociation(), this.getAssociation_Target(), "associations", null, 0, -1, AssociationTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(contextEClass, Context.class, "Context", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getContext_Elements(), this.getContextElement(), this.getContextElement_Context(), "elements", null, 0, -1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContext_Roles(), this.getRole(), null, "roles", null, 0, -1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContext_Groups(), this.getGroup(), null, "groups", null, 0, -1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContext_Types(), theXMLTypePackage.getAnyURI(), "types", null, 0, 1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(contextElementEClass, ContextElement.class, "ContextElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getContextElement_Context(), this.getContext(), this.getContext_Elements(), "context", null, 0, 1, ContextElement.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextElement_Dynamic(), theXMLTypePackage.getBoolean(), "dynamic", "false", 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextElement_InitialValue(), theXMLTypePackage.getString(), "initialValue", "", 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextElement_Name(), theXMLTypePackage.getString(), "name", "", 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextElement_Set(), theXMLTypePackage.getBoolean(), "set", "false", 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContextElement_Type(), theXMLTypePackage.getString(), "type", "", 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataObjectEClass, DataObject.class, "DataObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(edgeEClass, Edge.class, "Edge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEdge_IsDefault(), theXMLTypePackage.getBoolean(), "isDefault", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEdge_Source(), this.getVertex(), this.getVertex_OutgoingEdges(), "source", null, 0, 1, Edge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEdge_Target(), this.getVertex(), this.getVertex_IncomingEdges(), "target", null, 0, 1, Edge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEdge_Graph(), this.getGraph(), this.getGraph_SequenceEdges(), "graph", null, 0, 1, Edge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(goalEClass, Goal.class, "Goal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGoal_Unique(), theXMLTypePackage.getString(), "unique", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Creationcondition(), theXMLTypePackage.getString(), "creationcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_CreationconditionLanguage(), theXMLTypePackage.getString(), "creationconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Contextcondition(), theXMLTypePackage.getString(), "contextcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_ContextconditionLanguage(), theXMLTypePackage.getString(), "contextconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Dropcondition(), theXMLTypePackage.getString(), "dropcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_DropconditionLanguage(), theXMLTypePackage.getString(), "dropconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Recurcondition(), theXMLTypePackage.getString(), "recurcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Deliberation(), theXMLTypePackage.getString(), "deliberation", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_OnSuccessHandler(), theXMLTypePackage.getString(), "onSuccessHandler", null, 0, -1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_OnSkipHandler(), theXMLTypePackage.getString(), "onSkipHandler", null, 0, -1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_OnFailureHandler(), theXMLTypePackage.getString(), "onFailureHandler", null, 0, -1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Exclude(), this.getExcludeType(), "exclude", "when_tried", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_GoalType(), this.getGoalType(), "goalType", "", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Posttoall(), theXMLTypePackage.getBoolean(), "posttoall", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Randomselection(), theXMLTypePackage.getBoolean(), "randomselection", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Recalculate(), theXMLTypePackage.getBoolean(), "recalculate", "true", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Recur(), theXMLTypePackage.getBoolean(), "recur", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Recurdelay(), theXMLTypePackage.getLong(), "recurdelay", "0", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Retry(), theXMLTypePackage.getBoolean(), "retry", "true", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Retrydelay(), theXMLTypePackage.getLong(), "retrydelay", "0", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGoal_Sequential(), theXMLTypePackage.getBoolean(), "sequential", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(gpmnDiagramEClass, GpmnDiagram.class, "GpmnDiagram", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGpmnDiagram_Processes(), this.getProcess(), this.getProcess_GpmnDiagram(), "processes", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGpmnDiagram_Messages(), this.getMessagingEdge(), this.getMessagingEdge_GpmnDiagram(), "messages", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGpmnDiagram_Imports(), theXMLTypePackage.getString(), "imports", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGpmnDiagram_Package(), theXMLTypePackage.getString(), "package", null, 0, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(graphEClass, Graph.class, "Graph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGraph_Vertices(), this.getVertex(), this.getVertex_Graph(), "vertices", null, 0, -1, Graph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGraph_SequenceEdges(), this.getEdge(), this.getEdge_Graph(), "sequenceEdges", null, 0, -1, Graph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(groupEClass, Group.class, "Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGroup_Members(), theXMLTypePackage.getString(), "members", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGroup_Coordinator(), theXMLTypePackage.getString(), "coordinator", null, 0, 1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGroup_Head(), theXMLTypePackage.getString(), "head", null, 0, 1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(identifiableEClass, Identifiable.class, "Identifiable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIdentifiable_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, Identifiable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(interGraphEdgeEClass, InterGraphEdge.class, "InterGraphEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInterGraphEdge_Source(), this.getInterGraphVertex(), this.getInterGraphVertex_IncomingInterGraphEdges(), "source", null, 0, 1, InterGraphEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterGraphEdge_Target(), this.getInterGraphVertex(), this.getInterGraphVertex_OutgoingInterGraphEdges(), "target", null, 0, 1, InterGraphEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(interGraphVertexEClass, InterGraphVertex.class, "InterGraphVertex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInterGraphVertex_InterGraphMessages(), ecorePackage.getEFeatureMapEntry(), "interGraphMessages", null, 0, -1, InterGraphVertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterGraphVertex_IncomingInterGraphEdges(), this.getInterGraphEdge(), this.getInterGraphEdge_Source(), "incomingInterGraphEdges", null, 0, -1, InterGraphVertex.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getInterGraphVertex_OutgoingInterGraphEdges(), this.getInterGraphEdge(), this.getInterGraphEdge_Target(), "outgoingInterGraphEdges", null, 0, -1, InterGraphVertex.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(maintainGoalEClass, MaintainGoal.class, "MaintainGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMaintainGoal_Maintaincondition(), theXMLTypePackage.getString(), "maintaincondition", null, 0, 1, MaintainGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMaintainGoal_MaintainconditionLanguage(), theXMLTypePackage.getString(), "maintainconditionLanguage", "jcl", 0, 1, MaintainGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMaintainGoal_Targetcondition(), theXMLTypePackage.getString(), "targetcondition", null, 0, 1, MaintainGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMaintainGoal_TargetconditionLanguage(), theXMLTypePackage.getString(), "targetconditionLanguage", "jcl", 0, 1, MaintainGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(messageGoalEClass, MessageGoal.class, "MessageGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(messagingEdgeEClass, MessagingEdge.class, "MessagingEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMessagingEdge_Message(), theXMLTypePackage.getString(), "message", null, 0, 1, MessagingEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMessagingEdge_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_Messages(), "gpmnDiagram", null, 0, 1, MessagingEdge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(namedObjectEClass, NamedObject.class, "NamedObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNamedObject_Description(), theXMLTypePackage.getString(), "description", null, 0, 1, NamedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNamedObject_Name(), theXMLTypePackage.getString(), "name", null, 0, 1, NamedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNamedObject_Ncname(), theXMLTypePackage.getNCName(), "ncname", null, 0, 1, NamedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parallelGoalEClass, ParallelGoal.class, "ParallelGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParallelGoal_Targetcondition(), theXMLTypePackage.getString(), "targetcondition", null, 0, 1, ParallelGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParallelGoal_TargetconditionLanguage(), theXMLTypePackage.getString(), "targetconditionLanguage", "jcl", 0, 1, ParallelGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParallelGoal_Failurecondition(), theXMLTypePackage.getString(), "failurecondition", null, 0, 1, ParallelGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParallelGoal_FailureconditionLanguage(), theXMLTypePackage.getString(), "failureconditionLanguage", "jcl", 0, 1, ParallelGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameter_Direction(), this.getDirectionType(), "direction", "in", 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Name(), theXMLTypePackage.getString(), "name", "", 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Type(), theXMLTypePackage.getString(), "type", "", 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Value(), theXMLTypePackage.getString(), "value", "", 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterizedEdgeEClass, ParameterizedEdge.class, "ParameterizedEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameterizedEdge_ParameterMapping(), theXMLTypePackage.getString(), "parameterMapping", null, 0, -1, ParameterizedEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterizedVertexEClass, ParameterizedVertex.class, "ParameterizedVertex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParameterizedVertex_Parameter(), this.getParameter(), null, "parameter", null, 0, -1, ParameterizedVertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(performGoalEClass, PerformGoal.class, "PerformGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(planEClass, Plan.class, "Plan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPlan_BpmnPlan(), theXMLTypePackage.getAnyURI(), "bpmnPlan", null, 0, 1, Plan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPlan_Priority(), theXMLTypePackage.getInt(), "priority", "0", 0, 1, Plan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPlan_Precondition(), theXMLTypePackage.getString(), "precondition", null, 0, 1, Plan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPlan_Contextcondition(), theXMLTypePackage.getString(), "contextcondition", null, 0, 1, Plan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(planEdgeEClass, PlanEdge.class, "PlanEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(processEClass, jadex.tools.gpmn.Process.class, "Process", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProcess_Looping(), theXMLTypePackage.getBoolean(), "looping", null, 0, 1, jadex.tools.gpmn.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProcess_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_Processes(), "gpmnDiagram", null, 0, 1, jadex.tools.gpmn.Process.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(queryGoalEClass, QueryGoal.class, "QueryGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getQueryGoal_Targetcondition(), theXMLTypePackage.getString(), "targetcondition", null, 0, 1, QueryGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQueryGoal_TargetconditionLanguage(), theXMLTypePackage.getString(), "targetconditionLanguage", "jcl", 0, 1, QueryGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQueryGoal_Failurecondition(), theXMLTypePackage.getString(), "failurecondition", null, 0, 1, QueryGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQueryGoal_FailureconditionLanguage(), theXMLTypePackage.getString(), "failureconditionLanguage", "jcl", 0, 1, QueryGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(roleEClass, Role.class, "Role", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRole_InitialPerson(), theXMLTypePackage.getString(), "initialPerson", null, 0, 1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRole_PersonType(), theXMLTypePackage.getString(), "personType", null, 0, 1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(sequentialGoalEClass, SequentialGoal.class, "SequentialGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSequentialGoal_Targetcondition(), theXMLTypePackage.getString(), "targetcondition", null, 0, 1, SequentialGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSequentialGoal_TargetconditionLanguage(), theXMLTypePackage.getString(), "targetconditionLanguage", "jcl", 0, 1, SequentialGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSequentialGoal_Failurecondition(), theXMLTypePackage.getString(), "failurecondition", null, 0, 1, SequentialGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSequentialGoal_FailureconditionLanguage(), theXMLTypePackage.getString(), "failureconditionLanguage", "jcl", 0, 1, SequentialGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(subGoalEdgeEClass, SubGoalEdge.class, "SubGoalEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSubGoalEdge_SequentialOrder(), theXMLTypePackage.getInt(), "sequentialOrder", "0", 0, 1, SubGoalEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(subProcessGoalEClass, SubProcessGoal.class, "SubProcessGoal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSubProcessGoal_Goalref(), theXMLTypePackage.getString(), "goalref", null, 0, 1, SubProcessGoal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(textAnnotationEClass, TextAnnotation.class, "TextAnnotation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(vertexEClass, Vertex.class, "Vertex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getVertex_OutgoingEdges(), this.getEdge(), this.getEdge_Source(), "outgoingEdges", null, 0, -1, Vertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getVertex_IncomingEdges(), this.getEdge(), this.getEdge_Target(), "incomingEdges", null, 0, -1, Vertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getVertex_Graph(), this.getGraph(), this.getGraph_Vertices(), "graph", null, 0, 1, Vertex.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(genericGpmnElementEClass, GenericGpmnElement.class, "GenericGpmnElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGenericGpmnElement_Attributes(), theXMLTypePackage.getString(), "attributes", null, 0, -1, GenericGpmnElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGenericGpmnElement_Properties(), theXMLTypePackage.getString(), "properties", null, 0, -1, GenericGpmnElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(genericGpmnEdgeEClass, GenericGpmnEdge.class, "GenericGpmnEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Initialize enums and add enum literals
		initEEnum(directionTypeEEnum, DirectionType.class, "DirectionType");
		addEEnumLiteral(directionTypeEEnum, DirectionType.IN);
		addEEnumLiteral(directionTypeEEnum, DirectionType.OUT);
		addEEnumLiteral(directionTypeEEnum, DirectionType.INOUT);
		addEEnumLiteral(directionTypeEEnum, DirectionType.FIXED);

		initEEnum(edgeTypeEEnum, EdgeType.class, "EdgeType");
		addEEnumLiteral(edgeTypeEEnum, EdgeType.SUB_GOAL_EDGE);
		addEEnumLiteral(edgeTypeEEnum, EdgeType.PLAN_EDGE);
		addEEnumLiteral(edgeTypeEEnum, EdgeType.MESSAGE_EDGE);

		initEEnum(excludeTypeEEnum, ExcludeType.class, "ExcludeType");
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.NEVER);
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.WHEN_TRIED);
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.WHEN_FAILED);
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.WHEN_SUCCEEDED);

		initEEnum(goalTypeEEnum, GoalType.class, "GoalType");
		addEEnumLiteral(goalTypeEEnum, GoalType.META_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.SUB_PROCESS_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.MAINTAIN_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.ACHIEVE_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.PERFORM_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.QUERY_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.SEQUENTIAL_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.PARALLEL_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.MESSAGE_GOAL);

		// Initialize data types
		initEDataType(directionTypeObjectEDataType, DirectionType.class, "DirectionTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
		initEDataType(edgeTypeObjectEDataType, EdgeType.class, "EdgeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
		initEDataType(excludeTypeObjectEDataType, ExcludeType.class, "ExcludeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
		initEDataType(goalTypeObjectEDataType, GoalType.class, "GoalTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations()
	{
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";			
		addAnnotation
		  (achieveGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "AchieveGoal",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getAchieveGoal_Targetcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition"
		   });		
		addAnnotation
		  (getAchieveGoal_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition_language"
		   });			
		addAnnotation
		  (getAchieveGoal_Failurecondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition"
		   });		
		addAnnotation
		  (getAchieveGoal_FailureconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition_language"
		   });		
		addAnnotation
		  (artifactEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Artifact",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getArtifact_Associations(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "associations"
		   });		
		addAnnotation
		  (artifactsContainerEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ArtifactsContainer",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getArtifactsContainer_Artifacts(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "artifacts"
		   });		
		addAnnotation
		  (associationEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Association",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getAssociation_Direction(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "direction"
		   });		
		addAnnotation
		  (getAssociation_Source(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "source"
		   });		
		addAnnotation
		  (getAssociation_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "target"
		   });		
		addAnnotation
		  (associationTargetEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "AssociationTarget",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getAssociationTarget_Associations(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "associations"
		   });		
		addAnnotation
		  (contextEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Context",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getContext_Elements(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "elements"
		   });		
		addAnnotation
		  (getContext_Roles(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "roles"
		   });		
		addAnnotation
		  (getContext_Groups(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "groups"
		   });		
		addAnnotation
		  (getContext_Types(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "types"
		   });		
		addAnnotation
		  (contextElementEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ContextElement",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getContextElement_Context(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "context"
		   });		
		addAnnotation
		  (getContextElement_Dynamic(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "dynamic"
		   });		
		addAnnotation
		  (getContextElement_InitialValue(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "initialValue"
		   });		
		addAnnotation
		  (getContextElement_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getContextElement_Set(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "set"
		   });		
		addAnnotation
		  (getContextElement_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "type"
		   });		
		addAnnotation
		  (dataObjectEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "DataObject",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (directionTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "DirectionType"
		   });		
		addAnnotation
		  (directionTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "DirectionType:Object",
			 "baseType", "DirectionType"
		   });		
		addAnnotation
		  (edgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Edge",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getEdge_IsDefault(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "isDefault"
		   });		
		addAnnotation
		  (getEdge_Source(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "source"
		   });		
		addAnnotation
		  (getEdge_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "target"
		   });		
		addAnnotation
		  (edgeTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "EdgeType"
		   });		
		addAnnotation
		  (edgeTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "EdgeType:Object",
			 "baseType", "EdgeType"
		   });		
		addAnnotation
		  (excludeTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "exclude_._type"
		   });		
		addAnnotation
		  (excludeTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "exclude_._type:Object",
			 "baseType", "exclude_._type"
		   });		
		addAnnotation
		  (goalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Goal",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getGoal_Unique(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "unique"
		   });			
		addAnnotation
		  (getGoal_Creationcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "creationcondition"
		   });		
		addAnnotation
		  (getGoal_CreationconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "creationcondition_language"
		   });			
		addAnnotation
		  (getGoal_Contextcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "contextcondition"
		   });		
		addAnnotation
		  (getGoal_ContextconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "contextcondition_language"
		   });			
		addAnnotation
		  (getGoal_Dropcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "dropcondition"
		   });		
		addAnnotation
		  (getGoal_DropconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "dropcondition_language"
		   });		
		addAnnotation
		  (getGoal_Recurcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "recurcondition"
		   });			
		addAnnotation
		  (getGoal_Deliberation(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "deliberation"
		   });		
		addAnnotation
		  (getGoal_OnSuccessHandler(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "onSuccessHandler"
		   });		
		addAnnotation
		  (getGoal_OnSkipHandler(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "onSkipHandler"
		   });		
		addAnnotation
		  (getGoal_OnFailureHandler(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "onFailureHandler"
		   });			
		addAnnotation
		  (getGoal_Exclude(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "exclude"
		   });		
		addAnnotation
		  (getGoal_GoalType(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "goalType"
		   });			
		addAnnotation
		  (getGoal_Posttoall(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "posttoall"
		   });			
		addAnnotation
		  (getGoal_Randomselection(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "randomselection"
		   });			
		addAnnotation
		  (getGoal_Recalculate(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "recalculate"
		   });		
		addAnnotation
		  (getGoal_Recur(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "recur"
		   });		
		addAnnotation
		  (getGoal_Recurdelay(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "recurdelay"
		   });			
		addAnnotation
		  (getGoal_Retry(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "retry"
		   });			
		addAnnotation
		  (getGoal_Retrydelay(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "retrydelay"
		   });		
		addAnnotation
		  (getGoal_Sequential(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "sequential"
		   });		
		addAnnotation
		  (goalTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "GoalType"
		   });		
		addAnnotation
		  (goalTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "GoalType:Object",
			 "baseType", "GoalType"
		   });		
		addAnnotation
		  (gpmnDiagramEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "GpmnDiagram",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getGpmnDiagram_Processes(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "processes"
		   });		
		addAnnotation
		  (getGpmnDiagram_Messages(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "messages"
		   });		
		addAnnotation
		  (getGpmnDiagram_Imports(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "imports"
		   });		
		addAnnotation
		  (getGpmnDiagram_Package(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "package"
		   });		
		addAnnotation
		  (graphEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Graph",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getGraph_Vertices(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "vertices"
		   });		
		addAnnotation
		  (getGraph_SequenceEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "sequenceEdges"
		   });		
		addAnnotation
		  (groupEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Group",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getGroup_Members(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "members"
		   });		
		addAnnotation
		  (getGroup_Coordinator(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "coordinator"
		   });		
		addAnnotation
		  (getGroup_Head(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "head"
		   });		
		addAnnotation
		  (identifiableEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Identifiable",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getIdentifiable_Id(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (interGraphEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "InterGraphEdge",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getInterGraphEdge_Source(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "source"
		   });		
		addAnnotation
		  (getInterGraphEdge_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "target"
		   });		
		addAnnotation
		  (interGraphVertexEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "InterGraphVertex",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getInterGraphVertex_InterGraphMessages(), 
		   source, 
		   new String[] 
		   {
			 "kind", "group",
			 "name", "interGraphMessages:6"
		   });		
		addAnnotation
		  (getInterGraphVertex_IncomingInterGraphEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "incomingInterGraphEdges",
			 "group", "#interGraphMessages:6"
		   });		
		addAnnotation
		  (getInterGraphVertex_OutgoingInterGraphEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "outgoingInterGraphEdges",
			 "group", "#interGraphMessages:6"
		   });			
		addAnnotation
		  (maintainGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "MaintainGoal",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getMaintainGoal_Maintaincondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "maintaincondition"
		   });		
		addAnnotation
		  (getMaintainGoal_MaintainconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "maintaincondition_language"
		   });			
		addAnnotation
		  (getMaintainGoal_Targetcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition"
		   });		
		addAnnotation
		  (getMaintainGoal_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition_language"
		   });		
		addAnnotation
		  (messageGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "MessageGoal",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (messagingEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "MessagingEdge",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getMessagingEdge_Message(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "message"
		   });		
		addAnnotation
		  (namedObjectEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "NamedObject",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getNamedObject_Description(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "description"
		   });		
		addAnnotation
		  (getNamedObject_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getNamedObject_Ncname(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "ncname"
		   });		
		addAnnotation
		  (parallelGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ParallelGoal",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getParallelGoal_Targetcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition"
		   });		
		addAnnotation
		  (getParallelGoal_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition_language"
		   });			
		addAnnotation
		  (getParallelGoal_Failurecondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition"
		   });		
		addAnnotation
		  (getParallelGoal_FailureconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition_language"
		   });		
		addAnnotation
		  (parameterEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Parameter",
			 "kind", "empty"
		   });			
		addAnnotation
		  (getParameter_Direction(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "direction"
		   });		
		addAnnotation
		  (getParameter_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getParameter_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "type"
		   });		
		addAnnotation
		  (getParameter_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "value"
		   });		
		addAnnotation
		  (parameterizedEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ParameterizedEdge",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getParameterizedEdge_ParameterMapping(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameterMapping"
		   });		
		addAnnotation
		  (parameterizedVertexEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ParameterizedVertex",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getParameterizedVertex_Parameter(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameter"
		   });			
		addAnnotation
		  (performGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "PerformGoal",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (planEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Plan",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getPlan_BpmnPlan(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "bpmnPlan"
		   });			
		addAnnotation
		  (getPlan_Priority(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "priority"
		   });		
		addAnnotation
		  (getPlan_Precondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "precondition"
		   });		
		addAnnotation
		  (getPlan_Contextcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "contextcondition"
		   });		
		addAnnotation
		  (planEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "PlanEdge",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (processEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Process",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getProcess_Looping(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "looping"
		   });			
		addAnnotation
		  (queryGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "QueryGoal",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getQueryGoal_Targetcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition"
		   });		
		addAnnotation
		  (getQueryGoal_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition_language"
		   });			
		addAnnotation
		  (getQueryGoal_Failurecondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition"
		   });		
		addAnnotation
		  (getQueryGoal_FailureconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition_language"
		   });		
		addAnnotation
		  (roleEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Role",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRole_InitialPerson(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "initialPerson"
		   });		
		addAnnotation
		  (getRole_PersonType(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "personType"
		   });		
		addAnnotation
		  (sequentialGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SequentialGoal",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getSequentialGoal_Targetcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition"
		   });		
		addAnnotation
		  (getSequentialGoal_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "targetcondition_language"
		   });			
		addAnnotation
		  (getSequentialGoal_Failurecondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition"
		   });		
		addAnnotation
		  (getSequentialGoal_FailureconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "failurecondition_language"
		   });		
		addAnnotation
		  (subGoalEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SubGoalEdge",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getSubGoalEdge_SequentialOrder(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "sequentialOrder"
		   });		
		addAnnotation
		  (subProcessGoalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SubProcessGoal",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getSubProcessGoal_Goalref(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "goalref"
		   });		
		addAnnotation
		  (textAnnotationEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "TextAnnotation",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (vertexEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Vertex",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getVertex_OutgoingEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "outgoingEdges"
		   });		
		addAnnotation
		  (getVertex_IncomingEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "incomingEdges"
		   });		
		addAnnotation
		  (genericGpmnElementEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Goal",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getGenericGpmnElement_Attributes(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "onSuccessHandler"
		   });		
		addAnnotation
		  (getGenericGpmnElement_Properties(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "onSkipHandler"
		   });		
		addAnnotation
		  (genericGpmnEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SubGoalEdge",
			 "kind", "elementOnly"
		   });
	}

} //GpmnPackageImpl
