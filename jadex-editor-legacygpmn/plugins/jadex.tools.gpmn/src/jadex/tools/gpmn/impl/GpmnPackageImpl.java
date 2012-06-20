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

import jadex.tools.gpmn.AbstractEdge;
import jadex.tools.gpmn.AbstractNode;
import jadex.tools.gpmn.AbstractPlan;
import jadex.tools.gpmn.Activatable;
import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.BpmnPlan;
import jadex.tools.gpmn.ConditionLanguage;
import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.ContextElement;
import jadex.tools.gpmn.DirectionType;
import jadex.tools.gpmn.ExcludeType;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.Identifiable;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.NamedObject;
import jadex.tools.gpmn.Parameter;
import jadex.tools.gpmn.ParameterMapping;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.SubProcess;

import jadex.tools.gpmn.SuppressionEdge;
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
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass abstractEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass abstractNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass abstractPlanEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass activatableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass activationEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bpmnPlanEClass = null;

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
	private EClass identifiableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass activationPlanEClass = null;

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
	private EClass parameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterMappingEClass = null;

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
	private EClass subProcessEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass suppressionEdgeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum conditionLanguageEEnum = null;

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
	private EEnum modeTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType conditionLanguageObjectEDataType = null;

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
	private EDataType excludeTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType goalTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType modeTypeObjectEDataType = null;

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
	public EClass getAbstractEdge()
	{
		return abstractEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAbstractEdge_ParameterMapping()
	{
		return (EReference)abstractEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAbstractNode()
	{
		return abstractNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAbstractNode_Parameter()
	{
		return (EReference)abstractNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAbstractPlan()
	{
		return abstractPlanEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAbstractPlan_PlanEdges()
	{
		return (EReference)abstractPlanEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractPlan_Contextcondition()
	{
		return (EAttribute)abstractPlanEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractPlan_TargetconditionLanguage()
	{
		return (EAttribute)abstractPlanEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractPlan_Precondition()
	{
		return (EAttribute)abstractPlanEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractPlan_PreconditionLanguage()
	{
		return (EAttribute)abstractPlanEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractPlan_Priority()
	{
		return (EAttribute)abstractPlanEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAbstractPlan_GpmnDiagram()
	{
		return (EReference)abstractPlanEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getActivatable()
	{
		return activatableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getActivatable_ActivationEdges()
	{
		return (EReference)activatableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getActivationEdge()
	{
		return activationEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getActivationEdge_Source()
	{
		return (EReference)activationEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getActivationEdge_Target()
	{
		return (EReference)activationEdgeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getActivationEdge_GpmnDiagram()
	{
		return (EReference)activationEdgeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getActivationEdge_Order()
	{
		return (EAttribute)activationEdgeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBpmnPlan()
	{
		return bpmnPlanEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBpmnPlan_Planref()
	{
		return (EAttribute)bpmnPlanEClass.getEStructuralFeatures().get(0);
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
	public EReference getContext_GpmnDiagram()
	{
		return (EReference)contextEClass.getEStructuralFeatures().get(1);
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
	public EAttribute getContextElement_Value()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Name()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Set()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContextElement_Type()
	{
		return (EAttribute)contextElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getContextElement_Context()
	{
		return (EReference)contextElementEClass.getEStructuralFeatures().get(4);
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
	public EReference getGoal_PlanEdges()
	{
		return (EReference)goalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGoal_SuppressionEdge()
	{
		return (EReference)goalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Unique()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Creationcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_CreationconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Contextcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_ContextconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Dropcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_DropconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recurcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Deliberation()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Targetcondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_TargetconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Failurecondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_FailureconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Maintaincondition()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_MaintainconditionLanguage()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Exclude()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_GoalType()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Posttoall()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Randomselection()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recalculate()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recur()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Recurdelay()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(23);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Retry()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(24);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGoal_Retrydelay()
	{
		return (EAttribute)goalEClass.getEStructuralFeatures().get(25);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGoal_GpmnDiagram()
	{
		return (EReference)goalEClass.getEStructuralFeatures().get(26);
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
	public EAttribute getGpmnDiagram_Author()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Revision()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Title()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Version()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_Context()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_Goals()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_Plans()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_SubProcesses()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_ActivationEdges()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_PlanEdges()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGpmnDiagram_SuppressionEdges()
	{
		return (EReference)gpmnDiagramEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Package()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGpmnDiagram_Imports()
	{
		return (EAttribute)gpmnDiagramEClass.getEStructuralFeatures().get(1);
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
	public EClass getActivationPlan()
	{
		return activationPlanEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getActivationPlan_ActivationEdges()
	{
		return (EReference)activationPlanEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getActivationPlan_Mode()
	{
		return (EAttribute)activationPlanEClass.getEStructuralFeatures().get(1);
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
	public EClass getParameter()
	{
		return parameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Value()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Direction()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Name()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Type()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameterMapping()
	{
		return parameterMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterMapping_Value()
	{
		return (EAttribute)parameterMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterMapping_Name()
	{
		return (EAttribute)parameterMappingEClass.getEStructuralFeatures().get(1);
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
	public EReference getPlanEdge_Source()
	{
		return (EReference)planEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPlanEdge_Target()
	{
		return (EReference)planEdgeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPlanEdge_GpmnDiagram()
	{
		return (EReference)planEdgeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubProcess()
	{
		return subProcessEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSubProcess_Processref()
	{
		return (EAttribute)subProcessEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSubProcess_Internal() {
		return (EAttribute)subProcessEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSubProcess_GpmnDiagram()
	{
		return (EReference)subProcessEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSuppressionEdge()
	{
		return suppressionEdgeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSuppressionEdge_Source()
	{
		return (EReference)suppressionEdgeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSuppressionEdge_Target()
	{
		return (EReference)suppressionEdgeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSuppressionEdge_GpmnDiagram()
	{
		return (EReference)suppressionEdgeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getConditionLanguage()
	{
		return conditionLanguageEEnum;
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
	public EEnum getModeType()
	{
		return modeTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getConditionLanguageObject()
	{
		return conditionLanguageObjectEDataType;
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
	public EDataType getModeTypeObject()
	{
		return modeTypeObjectEDataType;
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
		abstractEdgeEClass = createEClass(ABSTRACT_EDGE);
		createEReference(abstractEdgeEClass, ABSTRACT_EDGE__PARAMETER_MAPPING);

		abstractNodeEClass = createEClass(ABSTRACT_NODE);
		createEReference(abstractNodeEClass, ABSTRACT_NODE__PARAMETER);

		abstractPlanEClass = createEClass(ABSTRACT_PLAN);
		createEReference(abstractPlanEClass, ABSTRACT_PLAN__PLAN_EDGES);
		createEAttribute(abstractPlanEClass, ABSTRACT_PLAN__CONTEXTCONDITION);
		createEAttribute(abstractPlanEClass, ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE);
		createEAttribute(abstractPlanEClass, ABSTRACT_PLAN__PRECONDITION);
		createEAttribute(abstractPlanEClass, ABSTRACT_PLAN__PRECONDITION_LANGUAGE);
		createEAttribute(abstractPlanEClass, ABSTRACT_PLAN__PRIORITY);
		createEReference(abstractPlanEClass, ABSTRACT_PLAN__GPMN_DIAGRAM);

		activatableEClass = createEClass(ACTIVATABLE);
		createEReference(activatableEClass, ACTIVATABLE__ACTIVATION_EDGES);

		activationEdgeEClass = createEClass(ACTIVATION_EDGE);
		createEReference(activationEdgeEClass, ACTIVATION_EDGE__SOURCE);
		createEReference(activationEdgeEClass, ACTIVATION_EDGE__TARGET);
		createEReference(activationEdgeEClass, ACTIVATION_EDGE__GPMN_DIAGRAM);
		createEAttribute(activationEdgeEClass, ACTIVATION_EDGE__ORDER);

		bpmnPlanEClass = createEClass(BPMN_PLAN);
		createEAttribute(bpmnPlanEClass, BPMN_PLAN__PLANREF);

		contextEClass = createEClass(CONTEXT);
		createEReference(contextEClass, CONTEXT__ELEMENTS);
		createEReference(contextEClass, CONTEXT__GPMN_DIAGRAM);

		contextElementEClass = createEClass(CONTEXT_ELEMENT);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__VALUE);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__NAME);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__SET);
		createEAttribute(contextElementEClass, CONTEXT_ELEMENT__TYPE);
		createEReference(contextElementEClass, CONTEXT_ELEMENT__CONTEXT);

		goalEClass = createEClass(GOAL);
		createEReference(goalEClass, GOAL__PLAN_EDGES);
		createEReference(goalEClass, GOAL__SUPPRESSION_EDGE);
		createEAttribute(goalEClass, GOAL__UNIQUE);
		createEAttribute(goalEClass, GOAL__CREATIONCONDITION);
		createEAttribute(goalEClass, GOAL__CREATIONCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__CONTEXTCONDITION);
		createEAttribute(goalEClass, GOAL__CONTEXTCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__DROPCONDITION);
		createEAttribute(goalEClass, GOAL__DROPCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__RECURCONDITION);
		createEAttribute(goalEClass, GOAL__DELIBERATION);
		createEAttribute(goalEClass, GOAL__TARGETCONDITION);
		createEAttribute(goalEClass, GOAL__TARGETCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__FAILURECONDITION);
		createEAttribute(goalEClass, GOAL__FAILURECONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__MAINTAINCONDITION);
		createEAttribute(goalEClass, GOAL__MAINTAINCONDITION_LANGUAGE);
		createEAttribute(goalEClass, GOAL__EXCLUDE);
		createEAttribute(goalEClass, GOAL__GOAL_TYPE);
		createEAttribute(goalEClass, GOAL__POSTTOALL);
		createEAttribute(goalEClass, GOAL__RANDOMSELECTION);
		createEAttribute(goalEClass, GOAL__RECALCULATE);
		createEAttribute(goalEClass, GOAL__RECUR);
		createEAttribute(goalEClass, GOAL__RECURDELAY);
		createEAttribute(goalEClass, GOAL__RETRY);
		createEAttribute(goalEClass, GOAL__RETRYDELAY);
		createEReference(goalEClass, GOAL__GPMN_DIAGRAM);

		gpmnDiagramEClass = createEClass(GPMN_DIAGRAM);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__PACKAGE);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__IMPORTS);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__CONTEXT);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__GOALS);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__PLANS);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__SUB_PROCESSES);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__ACTIVATION_EDGES);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__PLAN_EDGES);
		createEReference(gpmnDiagramEClass, GPMN_DIAGRAM__SUPPRESSION_EDGES);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__AUTHOR);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__REVISION);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__TITLE);
		createEAttribute(gpmnDiagramEClass, GPMN_DIAGRAM__VERSION);

		identifiableEClass = createEClass(IDENTIFIABLE);
		createEAttribute(identifiableEClass, IDENTIFIABLE__ID);

		activationPlanEClass = createEClass(ACTIVATION_PLAN);
		createEReference(activationPlanEClass, ACTIVATION_PLAN__ACTIVATION_EDGES);
		createEAttribute(activationPlanEClass, ACTIVATION_PLAN__MODE);

		namedObjectEClass = createEClass(NAMED_OBJECT);
		createEAttribute(namedObjectEClass, NAMED_OBJECT__DESCRIPTION);
		createEAttribute(namedObjectEClass, NAMED_OBJECT__NAME);
		createEAttribute(namedObjectEClass, NAMED_OBJECT__NCNAME);

		parameterEClass = createEClass(PARAMETER);
		createEAttribute(parameterEClass, PARAMETER__VALUE);
		createEAttribute(parameterEClass, PARAMETER__DIRECTION);
		createEAttribute(parameterEClass, PARAMETER__NAME);
		createEAttribute(parameterEClass, PARAMETER__TYPE);

		parameterMappingEClass = createEClass(PARAMETER_MAPPING);
		createEAttribute(parameterMappingEClass, PARAMETER_MAPPING__VALUE);
		createEAttribute(parameterMappingEClass, PARAMETER_MAPPING__NAME);

		planEdgeEClass = createEClass(PLAN_EDGE);
		createEReference(planEdgeEClass, PLAN_EDGE__SOURCE);
		createEReference(planEdgeEClass, PLAN_EDGE__TARGET);
		createEReference(planEdgeEClass, PLAN_EDGE__GPMN_DIAGRAM);

		subProcessEClass = createEClass(SUB_PROCESS);
		createEAttribute(subProcessEClass, SUB_PROCESS__PROCESSREF);
		createEAttribute(subProcessEClass, SUB_PROCESS__INTERNAL);
		createEReference(subProcessEClass, SUB_PROCESS__GPMN_DIAGRAM);

		suppressionEdgeEClass = createEClass(SUPPRESSION_EDGE);
		createEReference(suppressionEdgeEClass, SUPPRESSION_EDGE__SOURCE);
		createEReference(suppressionEdgeEClass, SUPPRESSION_EDGE__TARGET);
		createEReference(suppressionEdgeEClass, SUPPRESSION_EDGE__GPMN_DIAGRAM);

		// Create enums
		conditionLanguageEEnum = createEEnum(CONDITION_LANGUAGE);
		directionTypeEEnum = createEEnum(DIRECTION_TYPE);
		excludeTypeEEnum = createEEnum(EXCLUDE_TYPE);
		goalTypeEEnum = createEEnum(GOAL_TYPE);
		modeTypeEEnum = createEEnum(MODE_TYPE);

		// Create data types
		conditionLanguageObjectEDataType = createEDataType(CONDITION_LANGUAGE_OBJECT);
		directionTypeObjectEDataType = createEDataType(DIRECTION_TYPE_OBJECT);
		excludeTypeObjectEDataType = createEDataType(EXCLUDE_TYPE_OBJECT);
		goalTypeObjectEDataType = createEDataType(GOAL_TYPE_OBJECT);
		modeTypeObjectEDataType = createEDataType(MODE_TYPE_OBJECT);
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
		abstractEdgeEClass.getESuperTypes().add(this.getIdentifiable());
		abstractNodeEClass.getESuperTypes().add(this.getIdentifiable());
		abstractNodeEClass.getESuperTypes().add(this.getNamedObject());
		abstractPlanEClass.getESuperTypes().add(this.getAbstractNode());
		activatableEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		activationEdgeEClass.getESuperTypes().add(this.getAbstractEdge());
		bpmnPlanEClass.getESuperTypes().add(this.getAbstractPlan());
		contextEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		contextElementEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		goalEClass.getESuperTypes().add(this.getAbstractNode());
		goalEClass.getESuperTypes().add(this.getActivatable());
		gpmnDiagramEClass.getESuperTypes().add(this.getNamedObject());
		identifiableEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		activationPlanEClass.getESuperTypes().add(this.getAbstractPlan());
		namedObjectEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		parameterEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		parameterMappingEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
		planEdgeEClass.getESuperTypes().add(this.getAbstractEdge());
		subProcessEClass.getESuperTypes().add(this.getAbstractNode());
		subProcessEClass.getESuperTypes().add(this.getActivatable());
		suppressionEdgeEClass.getESuperTypes().add(this.getAbstractEdge());

		// Initialize classes and features; add operations and parameters
		initEClass(abstractEdgeEClass, AbstractEdge.class, "AbstractEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getAbstractEdge_ParameterMapping(), this.getParameterMapping(), null, "parameterMapping", null, 0, -1, AbstractEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(abstractNodeEClass, AbstractNode.class, "AbstractNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getAbstractNode_Parameter(), this.getParameter(), null, "parameter", null, 0, -1, AbstractNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(abstractPlanEClass, AbstractPlan.class, "AbstractPlan", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getAbstractPlan_PlanEdges(), this.getPlanEdge(), this.getPlanEdge_Target(), "planEdges", null, 0, -1, AbstractPlan.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getAbstractPlan_Contextcondition(), theXMLTypePackage.getString(), "contextcondition", null, 0, 1, AbstractPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getAbstractPlan_TargetconditionLanguage(), this.getConditionLanguage(), "targetconditionLanguage", "jcl", 0, 1, AbstractPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getAbstractPlan_Precondition(), theXMLTypePackage.getString(), "precondition", null, 0, 1, AbstractPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getAbstractPlan_PreconditionLanguage(), this.getConditionLanguage(), "preconditionLanguage", "java", 0, 1, AbstractPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getAbstractPlan_Priority(), theXMLTypePackage.getInt(), "priority", "0", 0, 1, AbstractPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEReference(getAbstractPlan_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_Plans(), "gpmnDiagram", null, 0, 1, AbstractPlan.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(activatableEClass, Activatable.class, "Activatable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getActivatable_ActivationEdges(), this.getActivationEdge(), this.getActivationEdge_Target(), "activationEdges", null, 0, -1, Activatable.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(activationEdgeEClass, ActivationEdge.class, "ActivationEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getActivationEdge_Source(), this.getActivationPlan(), this.getActivationPlan_ActivationEdges(), "source", null, 0, 1, ActivationEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getActivationEdge_Target(), this.getActivatable(), this.getActivatable_ActivationEdges(), "target", null, 0, 1, ActivationEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getActivationEdge_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_ActivationEdges(), "gpmnDiagram", null, 0, 1, ActivationEdge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getActivationEdge_Order(), theXMLTypePackage.getInt(), "order", "0", 0, 1, ActivationEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(bpmnPlanEClass, BpmnPlan.class, "BpmnPlan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getBpmnPlan_Planref(), theXMLTypePackage.getAnyURI(), "planref", null, 0, 1, BpmnPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(contextEClass, Context.class, "Context", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getContext_Elements(), this.getContextElement(), this.getContextElement_Context(), "elements", null, 0, -1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getContext_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_Context(), "gpmnDiagram", null, 1, 1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(contextElementEClass, ContextElement.class, "ContextElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getContextElement_Value(), theXMLTypePackage.getString(), "value", null, 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getContextElement_Name(), theXMLTypePackage.getString(), "name", null, 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getContextElement_Set(), theXMLTypePackage.getBoolean(), "set", null, 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getContextElement_Type(), theXMLTypePackage.getString(), "type", null, 0, 1, ContextElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getContextElement_Context(), this.getContext(), this.getContext_Elements(), "context", null, 0, 1, ContextElement.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(goalEClass, Goal.class, "Goal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getGoal_PlanEdges(), this.getPlanEdge(), null, "planEdges", null, 0, -1, Goal.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGoal_SuppressionEdge(), this.getSuppressionEdge(), null, "suppressionEdge", null, 0, -1, Goal.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_Unique(), theXMLTypePackage.getString(), "unique", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_Creationcondition(), theXMLTypePackage.getString(), "creationcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_CreationconditionLanguage(), this.getConditionLanguage(), "creationconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Contextcondition(), theXMLTypePackage.getString(), "contextcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_ContextconditionLanguage(), this.getConditionLanguage(), "contextconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Dropcondition(), theXMLTypePackage.getString(), "dropcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_DropconditionLanguage(), this.getConditionLanguage(), "dropconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Recurcondition(), theXMLTypePackage.getString(), "recurcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_Deliberation(), theXMLTypePackage.getString(), "deliberation", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_Targetcondition(), theXMLTypePackage.getString(), "targetcondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_TargetconditionLanguage(), this.getConditionLanguage(), "targetconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Failurecondition(), theXMLTypePackage.getString(), "failurecondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_FailureconditionLanguage(), this.getConditionLanguage(), "failureconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Maintaincondition(), theXMLTypePackage.getString(), "maintaincondition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_MaintainconditionLanguage(), this.getConditionLanguage(), "maintainconditionLanguage", "jcl", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Exclude(), this.getExcludeType(), "exclude", "when_tried", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_GoalType(), this.getGoalType(), "goalType", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGoal_Posttoall(), theXMLTypePackage.getBoolean(), "posttoall", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Randomselection(), theXMLTypePackage.getBoolean(), "randomselection", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Recalculate(), theXMLTypePackage.getBoolean(), "recalculate", "true", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Recur(), theXMLTypePackage.getBoolean(), "recur", "false", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Recurdelay(), theXMLTypePackage.getLong(), "recurdelay", "0", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Retry(), theXMLTypePackage.getBoolean(), "retry", "true", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGoal_Retrydelay(), theXMLTypePackage.getLong(), "retrydelay", "0", 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEReference(getGoal_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_Goals(), "gpmnDiagram", null, 0, 1, Goal.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(gpmnDiagramEClass, GpmnDiagram.class, "GpmnDiagram", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getGpmnDiagram_Package(), theXMLTypePackage.getString(), "package", "", 0, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getGpmnDiagram_Imports(), theXMLTypePackage.getString(), "imports", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_Context(), this.getContext(), this.getContext_GpmnDiagram(), "context", null, 1, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_Goals(), this.getGoal(), this.getGoal_GpmnDiagram(), "goals", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_Plans(), this.getAbstractPlan(), this.getAbstractPlan_GpmnDiagram(), "plans", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_SubProcesses(), this.getSubProcess(), this.getSubProcess_GpmnDiagram(), "subProcesses", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_ActivationEdges(), this.getActivationEdge(), this.getActivationEdge_GpmnDiagram(), "activationEdges", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_PlanEdges(), this.getPlanEdge(), this.getPlanEdge_GpmnDiagram(), "planEdges", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getGpmnDiagram_SuppressionEdges(), this.getSuppressionEdge(), this.getSuppressionEdge_GpmnDiagram(), "suppressionEdges", null, 0, -1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGpmnDiagram_Author(), theXMLTypePackage.getString(), "author", null, 0, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGpmnDiagram_Revision(), theXMLTypePackage.getString(), "revision", null, 0, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGpmnDiagram_Title(), theXMLTypePackage.getString(), "title", null, 0, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getGpmnDiagram_Version(), theXMLTypePackage.getString(), "version", null, 0, 1, GpmnDiagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(identifiableEClass, Identifiable.class, "Identifiable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getIdentifiable_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, Identifiable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(activationPlanEClass, ActivationPlan.class, "ActivationPlan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getActivationPlan_ActivationEdges(), this.getActivationEdge(), this.getActivationEdge_Source(), "activationEdges", null, 0, -1, ActivationPlan.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getActivationPlan_Mode(), this.getModeType(), "mode", "Parallel", 0, 1, ActivationPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(namedObjectEClass, NamedObject.class, "NamedObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getNamedObject_Description(), theXMLTypePackage.getString(), "description", null, 0, 1, NamedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getNamedObject_Name(), theXMLTypePackage.getString(), "name", null, 0, 1, NamedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getNamedObject_Ncname(), theXMLTypePackage.getString(), "ncname", null, 0, 1, NamedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getParameter_Value(), theXMLTypePackage.getString(), "value", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getParameter_Direction(), this.getDirectionType(), "direction", "inout", 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getParameter_Name(), theXMLTypePackage.getString(), "name", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getParameter_Type(), theXMLTypePackage.getString(), "type", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(parameterMappingEClass, ParameterMapping.class, "ParameterMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getParameterMapping_Value(), theXMLTypePackage.getString(), "value", null, 0, 1, ParameterMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getParameterMapping_Name(), theXMLTypePackage.getString(), "name", null, 0, 1, ParameterMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(planEdgeEClass, PlanEdge.class, "PlanEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getPlanEdge_Source(), this.getGoal(), null, "source", null, 0, 1, PlanEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getPlanEdge_Target(), this.getAbstractPlan(), this.getAbstractPlan_PlanEdges(), "target", null, 0, 1, PlanEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getPlanEdge_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_PlanEdges(), "gpmnDiagram", null, 0, 1, PlanEdge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(subProcessEClass, SubProcess.class, "SubProcess", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getSubProcess_Processref(), theXMLTypePackage.getString(), "processref", null, 0, 1, SubProcess.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getSubProcess_Internal(), theXMLTypePackage.getBoolean(), "internal", "false", 0, 1, SubProcess.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEReference(getSubProcess_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_SubProcesses(), "gpmnDiagram", null, 0, 1, SubProcess.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(suppressionEdgeEClass, SuppressionEdge.class, "SuppressionEdge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getSuppressionEdge_Source(), this.getGoal(), null, "source", null, 0, 1, SuppressionEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getSuppressionEdge_Target(), this.getGoal(), null, "target", null, 0, 1, SuppressionEdge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getSuppressionEdge_GpmnDiagram(), this.getGpmnDiagram(), this.getGpmnDiagram_SuppressionEdges(), "gpmnDiagram", null, 0, 1, SuppressionEdge.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Initialize enums and add enum literals
		initEEnum(conditionLanguageEEnum, ConditionLanguage.class, "ConditionLanguage"); //$NON-NLS-1$
		addEEnumLiteral(conditionLanguageEEnum, ConditionLanguage.JCL);
		addEEnumLiteral(conditionLanguageEEnum, ConditionLanguage.JAVA);

		initEEnum(directionTypeEEnum, DirectionType.class, "DirectionType"); //$NON-NLS-1$
		addEEnumLiteral(directionTypeEEnum, DirectionType.IN);
		addEEnumLiteral(directionTypeEEnum, DirectionType.OUT);
		addEEnumLiteral(directionTypeEEnum, DirectionType.INOUT);

		initEEnum(excludeTypeEEnum, ExcludeType.class, "ExcludeType"); //$NON-NLS-1$
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.NEVER);
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.WHEN_TRIED);
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.WHEN_FAILED);
		addEEnumLiteral(excludeTypeEEnum, ExcludeType.WHEN_SUCCEEDED);

		initEEnum(goalTypeEEnum, GoalType.class, "GoalType"); //$NON-NLS-1$
		addEEnumLiteral(goalTypeEEnum, GoalType.MAINTAIN_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.ACHIEVE_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.PERFORM_GOAL);
		addEEnumLiteral(goalTypeEEnum, GoalType.QUERY_GOAL);

		initEEnum(modeTypeEEnum, ModeType.class, "ModeType"); //$NON-NLS-1$
		addEEnumLiteral(modeTypeEEnum, ModeType.PARALLEL);
		addEEnumLiteral(modeTypeEEnum, ModeType.SEQUENTIAL);

		// Initialize data types
		initEDataType(conditionLanguageObjectEDataType, ConditionLanguage.class, "ConditionLanguageObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(directionTypeObjectEDataType, DirectionType.class, "DirectionTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(excludeTypeObjectEDataType, ExcludeType.class, "ExcludeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(goalTypeObjectEDataType, GoalType.class, "GoalTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(modeTypeObjectEDataType, ModeType.class, "ModeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

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
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$		
		addAnnotation
		  (abstractEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "AbstractEdge", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getAbstractEdge_ParameterMapping(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "parameterMapping" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (abstractNodeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "AbstractNode", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getAbstractNode_Parameter(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "parameter" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (abstractPlanEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "AbstractPlan", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getAbstractPlan_PlanEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "planEdges" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getAbstractPlan_Contextcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "contextcondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getAbstractPlan_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "targetcondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getAbstractPlan_Precondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "precondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getAbstractPlan_PreconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "precondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getAbstractPlan_Priority(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "priority" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (activatableEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Activatable", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getActivatable_ActivationEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "activationEdges" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (activationEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ActivationEdge", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getActivationEdge_Source(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "source" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getActivationEdge_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "target" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getActivationEdge_Order(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "order" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (bpmnPlanEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "BpmnPlan", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getBpmnPlan_Planref(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "planref" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (conditionLanguageEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "ConditionLanguage" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (conditionLanguageObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "ConditionLanguage:Object", //$NON-NLS-1$ //$NON-NLS-2$
			 "baseType", "ConditionLanguage" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (contextEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Context", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getContext_Elements(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "element" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (contextElementEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ContextElement", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getContextElement_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "value" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getContextElement_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getContextElement_Set(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "set" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getContextElement_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (directionTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "direction_._type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (directionTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "direction_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
			 "baseType", "direction_._type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (excludeTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "exclude_._type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (excludeTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "exclude_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
			 "baseType", "exclude_._type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (goalEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Goal", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_PlanEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "planEdges" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Unique(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "unique" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Creationcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "creationcondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_CreationconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "creationcondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Contextcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "contextcondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_ContextconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "contextcondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Dropcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "dropcondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_DropconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "dropcondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_Recurcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "recurcondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Deliberation(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "deliberation" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Targetcondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "targetcondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_TargetconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "targetcondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Failurecondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "failurecondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_FailureconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "failurecondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Maintaincondition(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "maintaincondition" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_MaintainconditionLanguage(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "maintaincondition_language" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Exclude(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "exclude" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_GoalType(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "goalType" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Posttoall(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "posttoall" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Randomselection(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "randomselection" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Recalculate(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "recalculate" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_Recur(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "recur" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGoal_Recurdelay(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "recurdelay" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Retry(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "retry" //$NON-NLS-1$ //$NON-NLS-2$
		   });			
		addAnnotation
		  (getGoal_Retrydelay(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "retrydelay" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (goalTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "GoalType" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (goalTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "GoalType:Object", //$NON-NLS-1$ //$NON-NLS-2$
			 "baseType", "GoalType" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (gpmnDiagramEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "GpmnDiagram", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Package(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "package" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Imports(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "import" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Context(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "context" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Goals(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "goal" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Plans(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "plan" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_SubProcesses(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "subProcess" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_ActivationEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "activationEdge" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_PlanEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "planEdge" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_SuppressionEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "suppressionEdge" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Author(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "author" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Revision(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "revision" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Title(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "title" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getGpmnDiagram_Version(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "version" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (identifiableEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Identifiable", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getIdentifiable_Id(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "id" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (activationPlanEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ActivationPlan", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getActivationPlan_ActivationEdges(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "activationEdges" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getActivationPlan_Mode(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "mode" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (modeTypeEEnum, 
		   source, 
		   new String[] 
		   {
			 "name", "mode_._type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (modeTypeObjectEDataType, 
		   source, 
		   new String[] 
		   {
			 "name", "mode_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
			 "baseType", "mode_._type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (namedObjectEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "NamedObject", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getNamedObject_Description(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "description" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getNamedObject_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getNamedObject_Ncname(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "ncname" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (parameterEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Parameter", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getParameter_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "value" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getParameter_Direction(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "direction" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getParameter_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getParameter_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "type" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (parameterMappingEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ParameterMapping", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getParameterMapping_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "value" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getParameterMapping_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (planEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "PlanEdge", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getPlanEdge_Source(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "source" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getPlanEdge_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "target" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (subProcessEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SubProcess", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getSubProcess_Processref(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "processref" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getSubProcess_Internal(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "internal" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (suppressionEdgeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SuppressionEdge", //$NON-NLS-1$ //$NON-NLS-2$
			 "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getSuppressionEdge_Source(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "source" //$NON-NLS-1$ //$NON-NLS-2$
		   });		
		addAnnotation
		  (getSuppressionEdge_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
			 "name", "target" //$NON-NLS-1$ //$NON-NLS-2$
		   });
	}

} //GpmnPackageImpl
