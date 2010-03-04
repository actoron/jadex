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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see jadex.tools.gpmn.GpmnFactory
 * @model kind="package"
 * @generated
 */
public interface GpmnPackage extends EPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "gpmn";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://jadex.sourceforge.net/gpmn";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "gpmn";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GpmnPackage eINSTANCE = jadex.tools.gpmn.impl.GpmnPackageImpl.init();

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.IdentifiableImpl <em>Identifiable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.IdentifiableImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getIdentifiable()
	 * @generated
	 */
	int IDENTIFIABLE = 13;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIABLE__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIABLE__ID = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Identifiable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIABLE_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.AssociationTargetImpl <em>Association Target</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.AssociationTargetImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAssociationTarget()
	 * @generated
	 */
	int ASSOCIATION_TARGET = 4;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_TARGET__EANNOTATIONS = IDENTIFIABLE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_TARGET__ID = IDENTIFIABLE__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_TARGET__ASSOCIATIONS = IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Association Target</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_TARGET_FEATURE_COUNT = IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.VertexImpl <em>Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.VertexImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getVertex()
	 * @generated
	 */
	int VERTEX = 34;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__EANNOTATIONS = ASSOCIATION_TARGET__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__ID = ASSOCIATION_TARGET__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__ASSOCIATIONS = ASSOCIATION_TARGET__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__DESCRIPTION = ASSOCIATION_TARGET_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__NAME = ASSOCIATION_TARGET_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__NCNAME = ASSOCIATION_TARGET_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__OUTGOING_EDGES = ASSOCIATION_TARGET_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__INCOMING_EDGES = ASSOCIATION_TARGET_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX__GRAPH = ASSOCIATION_TARGET_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VERTEX_FEATURE_COUNT = ASSOCIATION_TARGET_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ParameterizedVertexImpl <em>Parameterized Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ParameterizedVertexImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameterizedVertex()
	 * @generated
	 */
	int PARAMETERIZED_VERTEX = 23;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__EANNOTATIONS = VERTEX__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__ID = VERTEX__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__ASSOCIATIONS = VERTEX__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__DESCRIPTION = VERTEX__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__NAME = VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__NCNAME = VERTEX__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__OUTGOING_EDGES = VERTEX__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__INCOMING_EDGES = VERTEX__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__GRAPH = VERTEX__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX__PARAMETER = VERTEX_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Parameterized Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_VERTEX_FEATURE_COUNT = VERTEX_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GoalImpl <em>Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoal()
	 * @generated
	 */
	int GOAL = 9;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__EANNOTATIONS = PARAMETERIZED_VERTEX__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ID = PARAMETERIZED_VERTEX__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ASSOCIATIONS = PARAMETERIZED_VERTEX__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DESCRIPTION = PARAMETERIZED_VERTEX__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__NAME = PARAMETERIZED_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__NCNAME = PARAMETERIZED_VERTEX__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__OUTGOING_EDGES = PARAMETERIZED_VERTEX__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__INCOMING_EDGES = PARAMETERIZED_VERTEX__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__GRAPH = PARAMETERIZED_VERTEX__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__PARAMETER = PARAMETERIZED_VERTEX__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__UNIQUE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CREATIONCONDITION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CREATIONCONDITION_LANGUAGE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CONTEXTCONDITION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CONTEXTCONDITION_LANGUAGE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DROPCONDITION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DROPCONDITION_LANGUAGE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECURCONDITION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DELIBERATION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ON_SUCCESS_HANDLER = PARAMETERIZED_VERTEX_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ON_SKIP_HANDLER = PARAMETERIZED_VERTEX_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ON_FAILURE_HANDLER = PARAMETERIZED_VERTEX_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__EXCLUDE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__GOAL_TYPE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__POSTTOALL = PARAMETERIZED_VERTEX_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RANDOMSELECTION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECALCULATE = PARAMETERIZED_VERTEX_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECUR = PARAMETERIZED_VERTEX_FEATURE_COUNT + 17;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECURDELAY = PARAMETERIZED_VERTEX_FEATURE_COUNT + 18;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RETRY = PARAMETERIZED_VERTEX_FEATURE_COUNT + 19;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RETRYDELAY = PARAMETERIZED_VERTEX_FEATURE_COUNT + 20;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__SEQUENTIAL = PARAMETERIZED_VERTEX_FEATURE_COUNT + 21;

	/**
	 * The number of structural features of the '<em>Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL_FEATURE_COUNT = PARAMETERIZED_VERTEX_FEATURE_COUNT + 22;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.AchieveGoalImpl <em>Achieve Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.AchieveGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAchieveGoal()
	 * @generated
	 */
	int ACHIEVE_GOAL = 0;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__TARGETCONDITION = GOAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__TARGETCONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Failurecondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__FAILURECONDITION = GOAL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Failurecondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL__FAILURECONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Achieve Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACHIEVE_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.NamedObjectImpl <em>Named Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.NamedObjectImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getNamedObject()
	 * @generated
	 */
	int NAMED_OBJECT = 19;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT__DESCRIPTION = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT__NAME = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT__NCNAME = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Named Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ArtifactImpl <em>Artifact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ArtifactImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getArtifact()
	 * @generated
	 */
	int ARTIFACT = 1;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__EANNOTATIONS = NAMED_OBJECT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__DESCRIPTION = NAMED_OBJECT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__NAME = NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__NCNAME = NAMED_OBJECT__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__ID = NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__ASSOCIATIONS = NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Artifacts Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT__ARTIFACTS_CONTAINER = NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Artifact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACT_FEATURE_COUNT = NAMED_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ArtifactsContainerImpl <em>Artifacts Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ArtifactsContainerImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getArtifactsContainer()
	 * @generated
	 */
	int ARTIFACTS_CONTAINER = 2;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER__EANNOTATIONS = NAMED_OBJECT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER__DESCRIPTION = NAMED_OBJECT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER__NAME = NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER__NCNAME = NAMED_OBJECT__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER__ID = NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER__ARTIFACTS = NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Artifacts Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARTIFACTS_CONTAINER_FEATURE_COUNT = NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.AssociationImpl <em>Association</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.AssociationImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAssociation()
	 * @generated
	 */
	int ASSOCIATION = 3;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION__ID = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION__DIRECTION = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Source</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION__SOURCE = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION__TARGET = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Association</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOCIATION_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ContextImpl <em>Context</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ContextImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getContext()
	 * @generated
	 */
	int CONTEXT = 5;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__EANNOTATIONS = ARTIFACT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__DESCRIPTION = ARTIFACT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__NAME = ARTIFACT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__NCNAME = ARTIFACT__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__ID = ARTIFACT__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__ASSOCIATIONS = ARTIFACT__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Artifacts Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__ARTIFACTS_CONTAINER = ARTIFACT__ARTIFACTS_CONTAINER;

	/**
	 * The feature id for the '<em><b>Elements</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__ELEMENTS = ARTIFACT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Roles</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__ROLES = ARTIFACT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Groups</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__GROUPS = ARTIFACT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Types</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__TYPES = ARTIFACT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_FEATURE_COUNT = ARTIFACT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ContextElementImpl <em>Context Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ContextElementImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getContextElement()
	 * @generated
	 */
	int CONTEXT_ELEMENT = 6;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__EANNOTATIONS = IDENTIFIABLE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__ID = IDENTIFIABLE__ID;

	/**
	 * The feature id for the '<em><b>Context</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__CONTEXT = IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Dynamic</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__DYNAMIC = IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Initial Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__INITIAL_VALUE = IDENTIFIABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__NAME = IDENTIFIABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Set</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__SET = IDENTIFIABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__TYPE = IDENTIFIABLE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Context Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT_FEATURE_COUNT = IDENTIFIABLE_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.DataObjectImpl <em>Data Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.DataObjectImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDataObject()
	 * @generated
	 */
	int DATA_OBJECT = 7;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__EANNOTATIONS = ARTIFACT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__DESCRIPTION = ARTIFACT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__NAME = ARTIFACT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__NCNAME = ARTIFACT__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__ID = ARTIFACT__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__ASSOCIATIONS = ARTIFACT__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Artifacts Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT__ARTIFACTS_CONTAINER = ARTIFACT__ARTIFACTS_CONTAINER;

	/**
	 * The number of structural features of the '<em>Data Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OBJECT_FEATURE_COUNT = ARTIFACT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.EdgeImpl <em>Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.EdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getEdge()
	 * @generated
	 */
	int EDGE = 8;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__EANNOTATIONS = ASSOCIATION_TARGET__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__ID = ASSOCIATION_TARGET__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__ASSOCIATIONS = ASSOCIATION_TARGET__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__DESCRIPTION = ASSOCIATION_TARGET_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__NAME = ASSOCIATION_TARGET_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__NCNAME = ASSOCIATION_TARGET_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Is Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__IS_DEFAULT = ASSOCIATION_TARGET_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__SOURCE = ASSOCIATION_TARGET_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__TARGET = ASSOCIATION_TARGET_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE__GRAPH = ASSOCIATION_TARGET_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDGE_FEATURE_COUNT = ASSOCIATION_TARGET_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GraphImpl <em>Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GraphImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGraph()
	 * @generated
	 */
	int GRAPH = 11;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__EANNOTATIONS = ARTIFACTS_CONTAINER__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__DESCRIPTION = ARTIFACTS_CONTAINER__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__NAME = ARTIFACTS_CONTAINER__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__NCNAME = ARTIFACTS_CONTAINER__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__ID = ARTIFACTS_CONTAINER__ID;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__ARTIFACTS = ARTIFACTS_CONTAINER__ARTIFACTS;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__ASSOCIATIONS = ARTIFACTS_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__VERTICES = ARTIFACTS_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Sequence Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__SEQUENCE_EDGES = ARTIFACTS_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH_FEATURE_COUNT = ARTIFACTS_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GpmnDiagramImpl <em>Diagram</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GpmnDiagramImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGpmnDiagram()
	 * @generated
	 */
	int GPMN_DIAGRAM = 10;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__EANNOTATIONS = GRAPH__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__DESCRIPTION = GRAPH__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__NAME = GRAPH__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__NCNAME = GRAPH__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__ID = GRAPH__ID;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__ARTIFACTS = GRAPH__ARTIFACTS;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__ASSOCIATIONS = GRAPH__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__VERTICES = GRAPH__VERTICES;

	/**
	 * The feature id for the '<em><b>Sequence Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__SEQUENCE_EDGES = GRAPH__SEQUENCE_EDGES;

	/**
	 * The feature id for the '<em><b>Processes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__PROCESSES = GRAPH_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Messages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__MESSAGES = GRAPH_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Imports</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__IMPORTS = GRAPH_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__PACKAGE = GRAPH_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Diagram</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM_FEATURE_COUNT = GRAPH_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GroupImpl <em>Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GroupImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGroup()
	 * @generated
	 */
	int GROUP = 12;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__EANNOTATIONS = NAMED_OBJECT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__DESCRIPTION = NAMED_OBJECT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__NAME = NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__NCNAME = NAMED_OBJECT__NCNAME;

	/**
	 * The feature id for the '<em><b>Members</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__MEMBERS = NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Coordinator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__COORDINATOR = NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Head</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__HEAD = NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_FEATURE_COUNT = NAMED_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl <em>Inter Graph Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.InterGraphEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getInterGraphEdge()
	 * @generated
	 */
	int INTER_GRAPH_EDGE = 14;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__EANNOTATIONS = ASSOCIATION_TARGET__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__ID = ASSOCIATION_TARGET__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__ASSOCIATIONS = ASSOCIATION_TARGET__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__DESCRIPTION = ASSOCIATION_TARGET_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__NAME = ASSOCIATION_TARGET_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__NCNAME = ASSOCIATION_TARGET_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__SOURCE = ASSOCIATION_TARGET_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE__TARGET = ASSOCIATION_TARGET_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Inter Graph Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_EDGE_FEATURE_COUNT = ASSOCIATION_TARGET_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.InterGraphVertexImpl <em>Inter Graph Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.InterGraphVertexImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getInterGraphVertex()
	 * @generated
	 */
	int INTER_GRAPH_VERTEX = 15;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__EANNOTATIONS = ASSOCIATION_TARGET__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__ID = ASSOCIATION_TARGET__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__ASSOCIATIONS = ASSOCIATION_TARGET__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__DESCRIPTION = ASSOCIATION_TARGET_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__NAME = ASSOCIATION_TARGET_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__NCNAME = ASSOCIATION_TARGET_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Inter Graph Messages</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES = ASSOCIATION_TARGET_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Incoming Inter Graph Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES = ASSOCIATION_TARGET_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Outgoing Inter Graph Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES = ASSOCIATION_TARGET_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Inter Graph Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTER_GRAPH_VERTEX_FEATURE_COUNT = ASSOCIATION_TARGET_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.MaintainGoalImpl <em>Maintain Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.MaintainGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getMaintainGoal()
	 * @generated
	 */
	int MAINTAIN_GOAL = 16;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Maintaincondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__MAINTAINCONDITION = GOAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Maintaincondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__TARGETCONDITION = GOAL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Maintain Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAINTAIN_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.MessageGoalImpl <em>Message Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.MessageGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getMessageGoal()
	 * @generated
	 */
	int MESSAGE_GOAL = 17;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Inter Graph Messages</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__INTER_GRAPH_MESSAGES = GOAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Incoming Inter Graph Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__INCOMING_INTER_GRAPH_EDGES = GOAL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Outgoing Inter Graph Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL__OUTGOING_INTER_GRAPH_EDGES = GOAL_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Message Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.MessagingEdgeImpl <em>Messaging Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.MessagingEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getMessagingEdge()
	 * @generated
	 */
	int MESSAGING_EDGE = 18;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__EANNOTATIONS = INTER_GRAPH_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__ID = INTER_GRAPH_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__ASSOCIATIONS = INTER_GRAPH_EDGE__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__DESCRIPTION = INTER_GRAPH_EDGE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__NAME = INTER_GRAPH_EDGE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__NCNAME = INTER_GRAPH_EDGE__NCNAME;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__SOURCE = INTER_GRAPH_EDGE__SOURCE;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__TARGET = INTER_GRAPH_EDGE__TARGET;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__MESSAGE = INTER_GRAPH_EDGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE__GPMN_DIAGRAM = INTER_GRAPH_EDGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Messaging Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGING_EDGE_FEATURE_COUNT = INTER_GRAPH_EDGE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ParallelGoalImpl <em>Parallel Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ParallelGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParallelGoal()
	 * @generated
	 */
	int PARALLEL_GOAL = 20;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__TARGETCONDITION = GOAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__TARGETCONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Failurecondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__FAILURECONDITION = GOAL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Failurecondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL__FAILURECONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Parallel Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARALLEL_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ParameterImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 21;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__DIRECTION = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__TYPE = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__VALUE = 3;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ParameterizedEdgeImpl <em>Parameterized Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ParameterizedEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameterizedEdge()
	 * @generated
	 */
	int PARAMETERIZED_EDGE = 22;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__EANNOTATIONS = EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__ID = EDGE__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__ASSOCIATIONS = EDGE__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__DESCRIPTION = EDGE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__NAME = EDGE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__NCNAME = EDGE__NCNAME;

	/**
	 * The feature id for the '<em><b>Is Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__IS_DEFAULT = EDGE__IS_DEFAULT;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__SOURCE = EDGE__SOURCE;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__TARGET = EDGE__TARGET;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__GRAPH = EDGE__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE__PARAMETER_MAPPING = EDGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Parameterized Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_EDGE_FEATURE_COUNT = EDGE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.PerformGoalImpl <em>Perform Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.PerformGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPerformGoal()
	 * @generated
	 */
	int PERFORM_GOAL = 24;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The number of structural features of the '<em>Perform Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERFORM_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.PlanImpl <em>Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.PlanImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPlan()
	 * @generated
	 */
	int PLAN = 25;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__EANNOTATIONS = PARAMETERIZED_VERTEX__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__ID = PARAMETERIZED_VERTEX__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__ASSOCIATIONS = PARAMETERIZED_VERTEX__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__DESCRIPTION = PARAMETERIZED_VERTEX__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__NAME = PARAMETERIZED_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__NCNAME = PARAMETERIZED_VERTEX__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__OUTGOING_EDGES = PARAMETERIZED_VERTEX__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__INCOMING_EDGES = PARAMETERIZED_VERTEX__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__GRAPH = PARAMETERIZED_VERTEX__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__PARAMETER = PARAMETERIZED_VERTEX__PARAMETER;

	/**
	 * The feature id for the '<em><b>Bpmn Plan</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__BPMN_PLAN = PARAMETERIZED_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__PRIORITY = PARAMETERIZED_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Precondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__PRECONDITION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN__CONTEXTCONDITION = PARAMETERIZED_VERTEX_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_FEATURE_COUNT = PARAMETERIZED_VERTEX_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.PlanEdgeImpl <em>Plan Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.PlanEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPlanEdge()
	 * @generated
	 */
	int PLAN_EDGE = 26;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__EANNOTATIONS = PARAMETERIZED_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__ID = PARAMETERIZED_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__ASSOCIATIONS = PARAMETERIZED_EDGE__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__DESCRIPTION = PARAMETERIZED_EDGE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__NAME = PARAMETERIZED_EDGE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__NCNAME = PARAMETERIZED_EDGE__NCNAME;

	/**
	 * The feature id for the '<em><b>Is Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__IS_DEFAULT = PARAMETERIZED_EDGE__IS_DEFAULT;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__SOURCE = PARAMETERIZED_EDGE__SOURCE;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__TARGET = PARAMETERIZED_EDGE__TARGET;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__GRAPH = PARAMETERIZED_EDGE__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__PARAMETER_MAPPING = PARAMETERIZED_EDGE__PARAMETER_MAPPING;

	/**
	 * The number of structural features of the '<em>Plan Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE_FEATURE_COUNT = PARAMETERIZED_EDGE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ProcessImpl <em>Process</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ProcessImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getProcess()
	 * @generated
	 */
	int PROCESS = 27;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__EANNOTATIONS = GRAPH__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__DESCRIPTION = GRAPH__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__NAME = GRAPH__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__NCNAME = GRAPH__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__ID = GRAPH__ID;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__ARTIFACTS = GRAPH__ARTIFACTS;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__ASSOCIATIONS = GRAPH__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__VERTICES = GRAPH__VERTICES;

	/**
	 * The feature id for the '<em><b>Sequence Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__SEQUENCE_EDGES = GRAPH__SEQUENCE_EDGES;

	/**
	 * The feature id for the '<em><b>Inter Graph Messages</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__INTER_GRAPH_MESSAGES = GRAPH_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Incoming Inter Graph Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__INCOMING_INTER_GRAPH_EDGES = GRAPH_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Outgoing Inter Graph Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__OUTGOING_INTER_GRAPH_EDGES = GRAPH_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Looping</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__LOOPING = GRAPH_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS__GPMN_DIAGRAM = GRAPH_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Process</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_FEATURE_COUNT = GRAPH_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.QueryGoalImpl <em>Query Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.QueryGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getQueryGoal()
	 * @generated
	 */
	int QUERY_GOAL = 28;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__TARGETCONDITION = GOAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__TARGETCONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Failurecondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__FAILURECONDITION = GOAL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Failurecondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL__FAILURECONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Query Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.RoleImpl <em>Role</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.RoleImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getRole()
	 * @generated
	 */
	int ROLE = 29;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE__EANNOTATIONS = NAMED_OBJECT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE__DESCRIPTION = NAMED_OBJECT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE__NAME = NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE__NCNAME = NAMED_OBJECT__NCNAME;

	/**
	 * The feature id for the '<em><b>Initial Person</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE__INITIAL_PERSON = NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Person Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE__PERSON_TYPE = NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Role</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROLE_FEATURE_COUNT = NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.SequentialGoalImpl <em>Sequential Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.SequentialGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSequentialGoal()
	 * @generated
	 */
	int SEQUENTIAL_GOAL = 30;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__TARGETCONDITION = GOAL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__TARGETCONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Failurecondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__FAILURECONDITION = GOAL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Failurecondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL__FAILURECONDITION_LANGUAGE = GOAL_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Sequential Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENTIAL_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.SubGoalEdgeImpl <em>Sub Goal Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.SubGoalEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSubGoalEdge()
	 * @generated
	 */
	int SUB_GOAL_EDGE = 31;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__EANNOTATIONS = PARAMETERIZED_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__ID = PARAMETERIZED_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__ASSOCIATIONS = PARAMETERIZED_EDGE__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__DESCRIPTION = PARAMETERIZED_EDGE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__NAME = PARAMETERIZED_EDGE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__NCNAME = PARAMETERIZED_EDGE__NCNAME;

	/**
	 * The feature id for the '<em><b>Is Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__IS_DEFAULT = PARAMETERIZED_EDGE__IS_DEFAULT;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__SOURCE = PARAMETERIZED_EDGE__SOURCE;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__TARGET = PARAMETERIZED_EDGE__TARGET;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__GRAPH = PARAMETERIZED_EDGE__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__PARAMETER_MAPPING = PARAMETERIZED_EDGE__PARAMETER_MAPPING;

	/**
	 * The feature id for the '<em><b>Sequential Order</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE__SEQUENTIAL_ORDER = PARAMETERIZED_EDGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Sub Goal Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_GOAL_EDGE_FEATURE_COUNT = PARAMETERIZED_EDGE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.SubProcessGoalImpl <em>Sub Process Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.SubProcessGoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSubProcessGoal()
	 * @generated
	 */
	int SUB_PROCESS_GOAL = 32;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__EANNOTATIONS = GOAL__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__ID = GOAL__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__ASSOCIATIONS = GOAL__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__DESCRIPTION = GOAL__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__NAME = GOAL__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__NCNAME = GOAL__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__OUTGOING_EDGES = GOAL__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__INCOMING_EDGES = GOAL__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__GRAPH = GOAL__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__PARAMETER = GOAL__PARAMETER;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__UNIQUE = GOAL__UNIQUE;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__CREATIONCONDITION = GOAL__CREATIONCONDITION;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__CREATIONCONDITION_LANGUAGE = GOAL__CREATIONCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__CONTEXTCONDITION = GOAL__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__CONTEXTCONDITION_LANGUAGE = GOAL__CONTEXTCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__DROPCONDITION = GOAL__DROPCONDITION;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__DROPCONDITION_LANGUAGE = GOAL__DROPCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RECURCONDITION = GOAL__RECURCONDITION;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__DELIBERATION = GOAL__DELIBERATION;

	/**
	 * The feature id for the '<em><b>On Success Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__ON_SUCCESS_HANDLER = GOAL__ON_SUCCESS_HANDLER;

	/**
	 * The feature id for the '<em><b>On Skip Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__ON_SKIP_HANDLER = GOAL__ON_SKIP_HANDLER;

	/**
	 * The feature id for the '<em><b>On Failure Handler</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__ON_FAILURE_HANDLER = GOAL__ON_FAILURE_HANDLER;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__EXCLUDE = GOAL__EXCLUDE;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__GOAL_TYPE = GOAL__GOAL_TYPE;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__POSTTOALL = GOAL__POSTTOALL;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RANDOMSELECTION = GOAL__RANDOMSELECTION;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RECALCULATE = GOAL__RECALCULATE;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RECUR = GOAL__RECUR;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RECURDELAY = GOAL__RECURDELAY;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RETRY = GOAL__RETRY;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__RETRYDELAY = GOAL__RETRYDELAY;

	/**
	 * The feature id for the '<em><b>Sequential</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__SEQUENTIAL = GOAL__SEQUENTIAL;

	/**
	 * The feature id for the '<em><b>Goalref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL__GOALREF = GOAL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Sub Process Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_GOAL_FEATURE_COUNT = GOAL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.TextAnnotationImpl <em>Text Annotation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.TextAnnotationImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getTextAnnotation()
	 * @generated
	 */
	int TEXT_ANNOTATION = 33;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__EANNOTATIONS = ARTIFACT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__DESCRIPTION = ARTIFACT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__NAME = ARTIFACT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__NCNAME = ARTIFACT__NCNAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__ID = ARTIFACT__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__ASSOCIATIONS = ARTIFACT__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Artifacts Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION__ARTIFACTS_CONTAINER = ARTIFACT__ARTIFACTS_CONTAINER;

	/**
	 * The number of structural features of the '<em>Text Annotation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_ANNOTATION_FEATURE_COUNT = ARTIFACT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GenericGpmnElementImpl <em>Generic Gpmn Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GenericGpmnElementImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGenericGpmnElement()
	 * @generated
	 */
	int GENERIC_GPMN_ELEMENT = 35;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__EANNOTATIONS = PARAMETERIZED_VERTEX__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__ID = PARAMETERIZED_VERTEX__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__ASSOCIATIONS = PARAMETERIZED_VERTEX__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__DESCRIPTION = PARAMETERIZED_VERTEX__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__NAME = PARAMETERIZED_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__NCNAME = PARAMETERIZED_VERTEX__NCNAME;

	/**
	 * The feature id for the '<em><b>Outgoing Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__OUTGOING_EDGES = PARAMETERIZED_VERTEX__OUTGOING_EDGES;

	/**
	 * The feature id for the '<em><b>Incoming Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__INCOMING_EDGES = PARAMETERIZED_VERTEX__INCOMING_EDGES;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__GRAPH = PARAMETERIZED_VERTEX__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__PARAMETER = PARAMETERIZED_VERTEX__PARAMETER;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__ATTRIBUTES = PARAMETERIZED_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT__PROPERTIES = PARAMETERIZED_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Generic Gpmn Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_ELEMENT_FEATURE_COUNT = PARAMETERIZED_VERTEX_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GenericGpmnEdgeImpl <em>Generic Gpmn Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GenericGpmnEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGenericGpmnEdge()
	 * @generated
	 */
	int GENERIC_GPMN_EDGE = 36;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__EANNOTATIONS = PARAMETERIZED_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__ID = PARAMETERIZED_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Associations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__ASSOCIATIONS = PARAMETERIZED_EDGE__ASSOCIATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__DESCRIPTION = PARAMETERIZED_EDGE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__NAME = PARAMETERIZED_EDGE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__NCNAME = PARAMETERIZED_EDGE__NCNAME;

	/**
	 * The feature id for the '<em><b>Is Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__IS_DEFAULT = PARAMETERIZED_EDGE__IS_DEFAULT;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__SOURCE = PARAMETERIZED_EDGE__SOURCE;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__TARGET = PARAMETERIZED_EDGE__TARGET;

	/**
	 * The feature id for the '<em><b>Graph</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__GRAPH = PARAMETERIZED_EDGE__GRAPH;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE__PARAMETER_MAPPING = PARAMETERIZED_EDGE__PARAMETER_MAPPING;

	/**
	 * The number of structural features of the '<em>Generic Gpmn Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GENERIC_GPMN_EDGE_FEATURE_COUNT = PARAMETERIZED_EDGE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.DirectionType <em>Direction Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.DirectionType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDirectionType()
	 * @generated
	 */
	int DIRECTION_TYPE = 37;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.EdgeType <em>Edge Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.EdgeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getEdgeType()
	 * @generated
	 */
	int EDGE_TYPE = 38;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.ExcludeType <em>Exclude Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ExcludeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getExcludeType()
	 * @generated
	 */
	int EXCLUDE_TYPE = 39;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.GoalType <em>Goal Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.GoalType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoalType()
	 * @generated
	 */
	int GOAL_TYPE = 40;

	/**
	 * The meta object id for the '<em>Direction Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.DirectionType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDirectionTypeObject()
	 * @generated
	 */
	int DIRECTION_TYPE_OBJECT = 41;

	/**
	 * The meta object id for the '<em>Edge Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.EdgeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getEdgeTypeObject()
	 * @generated
	 */
	int EDGE_TYPE_OBJECT = 42;

	/**
	 * The meta object id for the '<em>Exclude Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ExcludeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getExcludeTypeObject()
	 * @generated
	 */
	int EXCLUDE_TYPE_OBJECT = 43;

	/**
	 * The meta object id for the '<em>Goal Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.GoalType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoalTypeObject()
	 * @generated
	 */
	int GOAL_TYPE_OBJECT = 44;

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.AchieveGoal <em>Achieve Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Achieve Goal</em>'.
	 * @see jadex.tools.gpmn.AchieveGoal
	 * @generated
	 */
	EClass getAchieveGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AchieveGoal#getTargetcondition <em>Targetcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition</em>'.
	 * @see jadex.tools.gpmn.AchieveGoal#getTargetcondition()
	 * @see #getAchieveGoal()
	 * @generated
	 */
	EAttribute getAchieveGoal_Targetcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AchieveGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.AchieveGoal#getTargetconditionLanguage()
	 * @see #getAchieveGoal()
	 * @generated
	 */
	EAttribute getAchieveGoal_TargetconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AchieveGoal#getFailurecondition <em>Failurecondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition</em>'.
	 * @see jadex.tools.gpmn.AchieveGoal#getFailurecondition()
	 * @see #getAchieveGoal()
	 * @generated
	 */
	EAttribute getAchieveGoal_Failurecondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AchieveGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition Language</em>'.
	 * @see jadex.tools.gpmn.AchieveGoal#getFailureconditionLanguage()
	 * @see #getAchieveGoal()
	 * @generated
	 */
	EAttribute getAchieveGoal_FailureconditionLanguage();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Artifact <em>Artifact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Artifact</em>'.
	 * @see jadex.tools.gpmn.Artifact
	 * @generated
	 */
	EClass getArtifact();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.Artifact#getAssociations <em>Associations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Associations</em>'.
	 * @see jadex.tools.gpmn.Artifact#getAssociations()
	 * @see #getArtifact()
	 * @generated
	 */
	EReference getArtifact_Associations();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Artifact#getArtifactsContainer <em>Artifacts Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Artifacts Container</em>'.
	 * @see jadex.tools.gpmn.Artifact#getArtifactsContainer()
	 * @see #getArtifact()
	 * @generated
	 */
	EReference getArtifact_ArtifactsContainer();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ArtifactsContainer <em>Artifacts Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Artifacts Container</em>'.
	 * @see jadex.tools.gpmn.ArtifactsContainer
	 * @generated
	 */
	EClass getArtifactsContainer();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.ArtifactsContainer#getArtifacts <em>Artifacts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Artifacts</em>'.
	 * @see jadex.tools.gpmn.ArtifactsContainer#getArtifacts()
	 * @see #getArtifactsContainer()
	 * @generated
	 */
	EReference getArtifactsContainer_Artifacts();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Association <em>Association</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Association</em>'.
	 * @see jadex.tools.gpmn.Association
	 * @generated
	 */
	EClass getAssociation();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Association#getDirection <em>Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Direction</em>'.
	 * @see jadex.tools.gpmn.Association#getDirection()
	 * @see #getAssociation()
	 * @generated
	 */
	EAttribute getAssociation_Direction();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Association#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Source</em>'.
	 * @see jadex.tools.gpmn.Association#getSource()
	 * @see #getAssociation()
	 * @generated
	 */
	EReference getAssociation_Source();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.Association#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see jadex.tools.gpmn.Association#getTarget()
	 * @see #getAssociation()
	 * @generated
	 */
	EReference getAssociation_Target();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.AssociationTarget <em>Association Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Association Target</em>'.
	 * @see jadex.tools.gpmn.AssociationTarget
	 * @generated
	 */
	EClass getAssociationTarget();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.AssociationTarget#getAssociations <em>Associations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Associations</em>'.
	 * @see jadex.tools.gpmn.AssociationTarget#getAssociations()
	 * @see #getAssociationTarget()
	 * @generated
	 */
	EReference getAssociationTarget_Associations();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Context <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Context</em>'.
	 * @see jadex.tools.gpmn.Context
	 * @generated
	 */
	EClass getContext();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.Context#getElements <em>Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Elements</em>'.
	 * @see jadex.tools.gpmn.Context#getElements()
	 * @see #getContext()
	 * @generated
	 */
	EReference getContext_Elements();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.Context#getRoles <em>Roles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Roles</em>'.
	 * @see jadex.tools.gpmn.Context#getRoles()
	 * @see #getContext()
	 * @generated
	 */
	EReference getContext_Roles();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.Context#getGroups <em>Groups</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Groups</em>'.
	 * @see jadex.tools.gpmn.Context#getGroups()
	 * @see #getContext()
	 * @generated
	 */
	EReference getContext_Groups();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Context#getTypes <em>Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Types</em>'.
	 * @see jadex.tools.gpmn.Context#getTypes()
	 * @see #getContext()
	 * @generated
	 */
	EAttribute getContext_Types();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ContextElement <em>Context Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Context Element</em>'.
	 * @see jadex.tools.gpmn.ContextElement
	 * @generated
	 */
	EClass getContextElement();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.ContextElement#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Context</em>'.
	 * @see jadex.tools.gpmn.ContextElement#getContext()
	 * @see #getContextElement()
	 * @generated
	 */
	EReference getContextElement_Context();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ContextElement#isDynamic <em>Dynamic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dynamic</em>'.
	 * @see jadex.tools.gpmn.ContextElement#isDynamic()
	 * @see #getContextElement()
	 * @generated
	 */
	EAttribute getContextElement_Dynamic();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ContextElement#getInitialValue <em>Initial Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Initial Value</em>'.
	 * @see jadex.tools.gpmn.ContextElement#getInitialValue()
	 * @see #getContextElement()
	 * @generated
	 */
	EAttribute getContextElement_InitialValue();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ContextElement#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see jadex.tools.gpmn.ContextElement#getName()
	 * @see #getContextElement()
	 * @generated
	 */
	EAttribute getContextElement_Name();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ContextElement#isSet <em>Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Set</em>'.
	 * @see jadex.tools.gpmn.ContextElement#isSet()
	 * @see #getContextElement()
	 * @generated
	 */
	EAttribute getContextElement_Set();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ContextElement#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see jadex.tools.gpmn.ContextElement#getType()
	 * @see #getContextElement()
	 * @generated
	 */
	EAttribute getContextElement_Type();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.DataObject <em>Data Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Object</em>'.
	 * @see jadex.tools.gpmn.DataObject
	 * @generated
	 */
	EClass getDataObject();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Edge <em>Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Edge</em>'.
	 * @see jadex.tools.gpmn.Edge
	 * @generated
	 */
	EClass getEdge();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Edge#isIsDefault <em>Is Default</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Default</em>'.
	 * @see jadex.tools.gpmn.Edge#isIsDefault()
	 * @see #getEdge()
	 * @generated
	 */
	EAttribute getEdge_IsDefault();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.Edge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see jadex.tools.gpmn.Edge#getSource()
	 * @see #getEdge()
	 * @generated
	 */
	EReference getEdge_Source();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.Edge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see jadex.tools.gpmn.Edge#getTarget()
	 * @see #getEdge()
	 * @generated
	 */
	EReference getEdge_Target();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Edge#getGraph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Graph</em>'.
	 * @see jadex.tools.gpmn.Edge#getGraph()
	 * @see #getEdge()
	 * @generated
	 */
	EReference getEdge_Graph();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Goal <em>Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Goal</em>'.
	 * @see jadex.tools.gpmn.Goal
	 * @generated
	 */
	EClass getGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getUnique <em>Unique</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique</em>'.
	 * @see jadex.tools.gpmn.Goal#getUnique()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Unique();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getCreationcondition <em>Creationcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Creationcondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getCreationcondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Creationcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getCreationconditionLanguage <em>Creationcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Creationcondition Language</em>'.
	 * @see jadex.tools.gpmn.Goal#getCreationconditionLanguage()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_CreationconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getContextcondition <em>Contextcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Contextcondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getContextcondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Contextcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getContextconditionLanguage <em>Contextcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Contextcondition Language</em>'.
	 * @see jadex.tools.gpmn.Goal#getContextconditionLanguage()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_ContextconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getDropcondition <em>Dropcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dropcondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getDropcondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Dropcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getDropconditionLanguage <em>Dropcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dropcondition Language</em>'.
	 * @see jadex.tools.gpmn.Goal#getDropconditionLanguage()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_DropconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getRecurcondition <em>Recurcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Recurcondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getRecurcondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Recurcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getDeliberation <em>Deliberation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deliberation</em>'.
	 * @see jadex.tools.gpmn.Goal#getDeliberation()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Deliberation();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.Goal#getOnSuccessHandler <em>On Success Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>On Success Handler</em>'.
	 * @see jadex.tools.gpmn.Goal#getOnSuccessHandler()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_OnSuccessHandler();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.Goal#getOnSkipHandler <em>On Skip Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>On Skip Handler</em>'.
	 * @see jadex.tools.gpmn.Goal#getOnSkipHandler()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_OnSkipHandler();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.Goal#getOnFailureHandler <em>On Failure Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>On Failure Handler</em>'.
	 * @see jadex.tools.gpmn.Goal#getOnFailureHandler()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_OnFailureHandler();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getExclude <em>Exclude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclude</em>'.
	 * @see jadex.tools.gpmn.Goal#getExclude()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Exclude();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getGoalType <em>Goal Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Goal Type</em>'.
	 * @see jadex.tools.gpmn.Goal#getGoalType()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_GoalType();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#isPosttoall <em>Posttoall</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Posttoall</em>'.
	 * @see jadex.tools.gpmn.Goal#isPosttoall()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Posttoall();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#isRandomselection <em>Randomselection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Randomselection</em>'.
	 * @see jadex.tools.gpmn.Goal#isRandomselection()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Randomselection();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#isRecalculate <em>Recalculate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Recalculate</em>'.
	 * @see jadex.tools.gpmn.Goal#isRecalculate()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Recalculate();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#isRecur <em>Recur</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Recur</em>'.
	 * @see jadex.tools.gpmn.Goal#isRecur()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Recur();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getRecurdelay <em>Recurdelay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Recurdelay</em>'.
	 * @see jadex.tools.gpmn.Goal#getRecurdelay()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Recurdelay();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#isRetry <em>Retry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Retry</em>'.
	 * @see jadex.tools.gpmn.Goal#isRetry()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Retry();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getRetrydelay <em>Retrydelay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Retrydelay</em>'.
	 * @see jadex.tools.gpmn.Goal#getRetrydelay()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Retrydelay();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#isSequential <em>Sequential</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sequential</em>'.
	 * @see jadex.tools.gpmn.Goal#isSequential()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Sequential();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.GpmnDiagram <em>Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Diagram</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram
	 * @generated
	 */
	EClass getGpmnDiagram();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getProcesses <em>Processes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Processes</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getProcesses()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_Processes();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getMessages <em>Messages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Messages</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getMessages()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_Messages();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.GpmnDiagram#getImports <em>Imports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Imports</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getImports()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EAttribute getGpmnDiagram_Imports();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.GpmnDiagram#getPackage <em>Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getPackage()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EAttribute getGpmnDiagram_Package();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Graph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Graph</em>'.
	 * @see jadex.tools.gpmn.Graph
	 * @generated
	 */
	EClass getGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.Graph#getVertices <em>Vertices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vertices</em>'.
	 * @see jadex.tools.gpmn.Graph#getVertices()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Vertices();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.Graph#getSequenceEdges <em>Sequence Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sequence Edges</em>'.
	 * @see jadex.tools.gpmn.Graph#getSequenceEdges()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_SequenceEdges();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Group <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Group</em>'.
	 * @see jadex.tools.gpmn.Group
	 * @generated
	 */
	EClass getGroup();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.Group#getMembers <em>Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Members</em>'.
	 * @see jadex.tools.gpmn.Group#getMembers()
	 * @see #getGroup()
	 * @generated
	 */
	EAttribute getGroup_Members();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Group#getCoordinator <em>Coordinator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Coordinator</em>'.
	 * @see jadex.tools.gpmn.Group#getCoordinator()
	 * @see #getGroup()
	 * @generated
	 */
	EAttribute getGroup_Coordinator();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Group#getHead <em>Head</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Head</em>'.
	 * @see jadex.tools.gpmn.Group#getHead()
	 * @see #getGroup()
	 * @generated
	 */
	EAttribute getGroup_Head();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Identifiable <em>Identifiable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Identifiable</em>'.
	 * @see jadex.tools.gpmn.Identifiable
	 * @generated
	 */
	EClass getIdentifiable();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Identifiable#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see jadex.tools.gpmn.Identifiable#getId()
	 * @see #getIdentifiable()
	 * @generated
	 */
	EAttribute getIdentifiable_Id();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.InterGraphEdge <em>Inter Graph Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Inter Graph Edge</em>'.
	 * @see jadex.tools.gpmn.InterGraphEdge
	 * @generated
	 */
	EClass getInterGraphEdge();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.InterGraphEdge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see jadex.tools.gpmn.InterGraphEdge#getSource()
	 * @see #getInterGraphEdge()
	 * @generated
	 */
	EReference getInterGraphEdge_Source();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.InterGraphEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see jadex.tools.gpmn.InterGraphEdge#getTarget()
	 * @see #getInterGraphEdge()
	 * @generated
	 */
	EReference getInterGraphEdge_Target();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.InterGraphVertex <em>Inter Graph Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Inter Graph Vertex</em>'.
	 * @see jadex.tools.gpmn.InterGraphVertex
	 * @generated
	 */
	EClass getInterGraphVertex();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.InterGraphVertex#getInterGraphMessages <em>Inter Graph Messages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Inter Graph Messages</em>'.
	 * @see jadex.tools.gpmn.InterGraphVertex#getInterGraphMessages()
	 * @see #getInterGraphVertex()
	 * @generated
	 */
	EAttribute getInterGraphVertex_InterGraphMessages();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.InterGraphVertex#getIncomingInterGraphEdges <em>Incoming Inter Graph Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Incoming Inter Graph Edges</em>'.
	 * @see jadex.tools.gpmn.InterGraphVertex#getIncomingInterGraphEdges()
	 * @see #getInterGraphVertex()
	 * @generated
	 */
	EReference getInterGraphVertex_IncomingInterGraphEdges();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.InterGraphVertex#getOutgoingInterGraphEdges <em>Outgoing Inter Graph Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Outgoing Inter Graph Edges</em>'.
	 * @see jadex.tools.gpmn.InterGraphVertex#getOutgoingInterGraphEdges()
	 * @see #getInterGraphVertex()
	 * @generated
	 */
	EReference getInterGraphVertex_OutgoingInterGraphEdges();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.MaintainGoal <em>Maintain Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Maintain Goal</em>'.
	 * @see jadex.tools.gpmn.MaintainGoal
	 * @generated
	 */
	EClass getMaintainGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.MaintainGoal#getMaintaincondition <em>Maintaincondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maintaincondition</em>'.
	 * @see jadex.tools.gpmn.MaintainGoal#getMaintaincondition()
	 * @see #getMaintainGoal()
	 * @generated
	 */
	EAttribute getMaintainGoal_Maintaincondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.MaintainGoal#getMaintainconditionLanguage <em>Maintaincondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maintaincondition Language</em>'.
	 * @see jadex.tools.gpmn.MaintainGoal#getMaintainconditionLanguage()
	 * @see #getMaintainGoal()
	 * @generated
	 */
	EAttribute getMaintainGoal_MaintainconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.MaintainGoal#getTargetcondition <em>Targetcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition</em>'.
	 * @see jadex.tools.gpmn.MaintainGoal#getTargetcondition()
	 * @see #getMaintainGoal()
	 * @generated
	 */
	EAttribute getMaintainGoal_Targetcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.MaintainGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.MaintainGoal#getTargetconditionLanguage()
	 * @see #getMaintainGoal()
	 * @generated
	 */
	EAttribute getMaintainGoal_TargetconditionLanguage();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.MessageGoal <em>Message Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Message Goal</em>'.
	 * @see jadex.tools.gpmn.MessageGoal
	 * @generated
	 */
	EClass getMessageGoal();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.MessagingEdge <em>Messaging Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Messaging Edge</em>'.
	 * @see jadex.tools.gpmn.MessagingEdge
	 * @generated
	 */
	EClass getMessagingEdge();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.MessagingEdge#getMessage <em>Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see jadex.tools.gpmn.MessagingEdge#getMessage()
	 * @see #getMessagingEdge()
	 * @generated
	 */
	EAttribute getMessagingEdge_Message();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.MessagingEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.MessagingEdge#getGpmnDiagram()
	 * @see #getMessagingEdge()
	 * @generated
	 */
	EReference getMessagingEdge_GpmnDiagram();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.NamedObject <em>Named Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Object</em>'.
	 * @see jadex.tools.gpmn.NamedObject
	 * @generated
	 */
	EClass getNamedObject();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.NamedObject#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see jadex.tools.gpmn.NamedObject#getDescription()
	 * @see #getNamedObject()
	 * @generated
	 */
	EAttribute getNamedObject_Description();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.NamedObject#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see jadex.tools.gpmn.NamedObject#getName()
	 * @see #getNamedObject()
	 * @generated
	 */
	EAttribute getNamedObject_Name();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.NamedObject#getNcname <em>Ncname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ncname</em>'.
	 * @see jadex.tools.gpmn.NamedObject#getNcname()
	 * @see #getNamedObject()
	 * @generated
	 */
	EAttribute getNamedObject_Ncname();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ParallelGoal <em>Parallel Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parallel Goal</em>'.
	 * @see jadex.tools.gpmn.ParallelGoal
	 * @generated
	 */
	EClass getParallelGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ParallelGoal#getTargetcondition <em>Targetcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition</em>'.
	 * @see jadex.tools.gpmn.ParallelGoal#getTargetcondition()
	 * @see #getParallelGoal()
	 * @generated
	 */
	EAttribute getParallelGoal_Targetcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ParallelGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.ParallelGoal#getTargetconditionLanguage()
	 * @see #getParallelGoal()
	 * @generated
	 */
	EAttribute getParallelGoal_TargetconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ParallelGoal#getFailurecondition <em>Failurecondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition</em>'.
	 * @see jadex.tools.gpmn.ParallelGoal#getFailurecondition()
	 * @see #getParallelGoal()
	 * @generated
	 */
	EAttribute getParallelGoal_Failurecondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ParallelGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition Language</em>'.
	 * @see jadex.tools.gpmn.ParallelGoal#getFailureconditionLanguage()
	 * @see #getParallelGoal()
	 * @generated
	 */
	EAttribute getParallelGoal_FailureconditionLanguage();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see jadex.tools.gpmn.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Parameter#getDirection <em>Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Direction</em>'.
	 * @see jadex.tools.gpmn.Parameter#getDirection()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Direction();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Parameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see jadex.tools.gpmn.Parameter#getName()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Name();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Parameter#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see jadex.tools.gpmn.Parameter#getType()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Type();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Parameter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see jadex.tools.gpmn.Parameter#getValue()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Value();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ParameterizedEdge <em>Parameterized Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameterized Edge</em>'.
	 * @see jadex.tools.gpmn.ParameterizedEdge
	 * @generated
	 */
	EClass getParameterizedEdge();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.ParameterizedEdge#getParameterMapping <em>Parameter Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Mapping</em>'.
	 * @see jadex.tools.gpmn.ParameterizedEdge#getParameterMapping()
	 * @see #getParameterizedEdge()
	 * @generated
	 */
	EAttribute getParameterizedEdge_ParameterMapping();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ParameterizedVertex <em>Parameterized Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameterized Vertex</em>'.
	 * @see jadex.tools.gpmn.ParameterizedVertex
	 * @generated
	 */
	EClass getParameterizedVertex();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.ParameterizedVertex#getParameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter</em>'.
	 * @see jadex.tools.gpmn.ParameterizedVertex#getParameter()
	 * @see #getParameterizedVertex()
	 * @generated
	 */
	EReference getParameterizedVertex_Parameter();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.PerformGoal <em>Perform Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Perform Goal</em>'.
	 * @see jadex.tools.gpmn.PerformGoal
	 * @generated
	 */
	EClass getPerformGoal();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Plan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Plan</em>'.
	 * @see jadex.tools.gpmn.Plan
	 * @generated
	 */
	EClass getPlan();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Plan#getBpmnPlan <em>Bpmn Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bpmn Plan</em>'.
	 * @see jadex.tools.gpmn.Plan#getBpmnPlan()
	 * @see #getPlan()
	 * @generated
	 */
	EAttribute getPlan_BpmnPlan();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Plan#getPriority <em>Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Priority</em>'.
	 * @see jadex.tools.gpmn.Plan#getPriority()
	 * @see #getPlan()
	 * @generated
	 */
	EAttribute getPlan_Priority();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Plan#getPrecondition <em>Precondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Precondition</em>'.
	 * @see jadex.tools.gpmn.Plan#getPrecondition()
	 * @see #getPlan()
	 * @generated
	 */
	EAttribute getPlan_Precondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Plan#getContextcondition <em>Contextcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Contextcondition</em>'.
	 * @see jadex.tools.gpmn.Plan#getContextcondition()
	 * @see #getPlan()
	 * @generated
	 */
	EAttribute getPlan_Contextcondition();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.PlanEdge <em>Plan Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Plan Edge</em>'.
	 * @see jadex.tools.gpmn.PlanEdge
	 * @generated
	 */
	EClass getPlanEdge();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Process <em>Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Process</em>'.
	 * @see jadex.tools.gpmn.Process
	 * @generated
	 */
	EClass getProcess();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Process#isLooping <em>Looping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Looping</em>'.
	 * @see jadex.tools.gpmn.Process#isLooping()
	 * @see #getProcess()
	 * @generated
	 */
	EAttribute getProcess_Looping();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Process#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.Process#getGpmnDiagram()
	 * @see #getProcess()
	 * @generated
	 */
	EReference getProcess_GpmnDiagram();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.QueryGoal <em>Query Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Goal</em>'.
	 * @see jadex.tools.gpmn.QueryGoal
	 * @generated
	 */
	EClass getQueryGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.QueryGoal#getTargetcondition <em>Targetcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition</em>'.
	 * @see jadex.tools.gpmn.QueryGoal#getTargetcondition()
	 * @see #getQueryGoal()
	 * @generated
	 */
	EAttribute getQueryGoal_Targetcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.QueryGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.QueryGoal#getTargetconditionLanguage()
	 * @see #getQueryGoal()
	 * @generated
	 */
	EAttribute getQueryGoal_TargetconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.QueryGoal#getFailurecondition <em>Failurecondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition</em>'.
	 * @see jadex.tools.gpmn.QueryGoal#getFailurecondition()
	 * @see #getQueryGoal()
	 * @generated
	 */
	EAttribute getQueryGoal_Failurecondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.QueryGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition Language</em>'.
	 * @see jadex.tools.gpmn.QueryGoal#getFailureconditionLanguage()
	 * @see #getQueryGoal()
	 * @generated
	 */
	EAttribute getQueryGoal_FailureconditionLanguage();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Role <em>Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Role</em>'.
	 * @see jadex.tools.gpmn.Role
	 * @generated
	 */
	EClass getRole();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Role#getInitialPerson <em>Initial Person</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Initial Person</em>'.
	 * @see jadex.tools.gpmn.Role#getInitialPerson()
	 * @see #getRole()
	 * @generated
	 */
	EAttribute getRole_InitialPerson();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Role#getPersonType <em>Person Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Person Type</em>'.
	 * @see jadex.tools.gpmn.Role#getPersonType()
	 * @see #getRole()
	 * @generated
	 */
	EAttribute getRole_PersonType();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.SequentialGoal <em>Sequential Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sequential Goal</em>'.
	 * @see jadex.tools.gpmn.SequentialGoal
	 * @generated
	 */
	EClass getSequentialGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SequentialGoal#getTargetcondition <em>Targetcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition</em>'.
	 * @see jadex.tools.gpmn.SequentialGoal#getTargetcondition()
	 * @see #getSequentialGoal()
	 * @generated
	 */
	EAttribute getSequentialGoal_Targetcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SequentialGoal#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.SequentialGoal#getTargetconditionLanguage()
	 * @see #getSequentialGoal()
	 * @generated
	 */
	EAttribute getSequentialGoal_TargetconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SequentialGoal#getFailurecondition <em>Failurecondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition</em>'.
	 * @see jadex.tools.gpmn.SequentialGoal#getFailurecondition()
	 * @see #getSequentialGoal()
	 * @generated
	 */
	EAttribute getSequentialGoal_Failurecondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SequentialGoal#getFailureconditionLanguage <em>Failurecondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition Language</em>'.
	 * @see jadex.tools.gpmn.SequentialGoal#getFailureconditionLanguage()
	 * @see #getSequentialGoal()
	 * @generated
	 */
	EAttribute getSequentialGoal_FailureconditionLanguage();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.SubGoalEdge <em>Sub Goal Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sub Goal Edge</em>'.
	 * @see jadex.tools.gpmn.SubGoalEdge
	 * @generated
	 */
	EClass getSubGoalEdge();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SubGoalEdge#getSequentialOrder <em>Sequential Order</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sequential Order</em>'.
	 * @see jadex.tools.gpmn.SubGoalEdge#getSequentialOrder()
	 * @see #getSubGoalEdge()
	 * @generated
	 */
	EAttribute getSubGoalEdge_SequentialOrder();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.SubProcessGoal <em>Sub Process Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sub Process Goal</em>'.
	 * @see jadex.tools.gpmn.SubProcessGoal
	 * @generated
	 */
	EClass getSubProcessGoal();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SubProcessGoal#getGoalref <em>Goalref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Goalref</em>'.
	 * @see jadex.tools.gpmn.SubProcessGoal#getGoalref()
	 * @see #getSubProcessGoal()
	 * @generated
	 */
	EAttribute getSubProcessGoal_Goalref();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.TextAnnotation <em>Text Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Text Annotation</em>'.
	 * @see jadex.tools.gpmn.TextAnnotation
	 * @generated
	 */
	EClass getTextAnnotation();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Vertex <em>Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Vertex</em>'.
	 * @see jadex.tools.gpmn.Vertex
	 * @generated
	 */
	EClass getVertex();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.Vertex#getOutgoingEdges <em>Outgoing Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Outgoing Edges</em>'.
	 * @see jadex.tools.gpmn.Vertex#getOutgoingEdges()
	 * @see #getVertex()
	 * @generated
	 */
	EReference getVertex_OutgoingEdges();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.Vertex#getIncomingEdges <em>Incoming Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Incoming Edges</em>'.
	 * @see jadex.tools.gpmn.Vertex#getIncomingEdges()
	 * @see #getVertex()
	 * @generated
	 */
	EReference getVertex_IncomingEdges();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Vertex#getGraph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Graph</em>'.
	 * @see jadex.tools.gpmn.Vertex#getGraph()
	 * @see #getVertex()
	 * @generated
	 */
	EReference getVertex_Graph();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.GenericGpmnElement <em>Generic Gpmn Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Generic Gpmn Element</em>'.
	 * @see jadex.tools.gpmn.GenericGpmnElement
	 * @generated
	 */
	EClass getGenericGpmnElement();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.GenericGpmnElement#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Attributes</em>'.
	 * @see jadex.tools.gpmn.GenericGpmnElement#getAttributes()
	 * @see #getGenericGpmnElement()
	 * @generated
	 */
	EAttribute getGenericGpmnElement_Attributes();

	/**
	 * Returns the meta object for the attribute list '{@link jadex.tools.gpmn.GenericGpmnElement#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Properties</em>'.
	 * @see jadex.tools.gpmn.GenericGpmnElement#getProperties()
	 * @see #getGenericGpmnElement()
	 * @generated
	 */
	EAttribute getGenericGpmnElement_Properties();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.GenericGpmnEdge <em>Generic Gpmn Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Generic Gpmn Edge</em>'.
	 * @see jadex.tools.gpmn.GenericGpmnEdge
	 * @generated
	 */
	EClass getGenericGpmnEdge();

	/**
	 * Returns the meta object for enum '{@link jadex.tools.gpmn.DirectionType <em>Direction Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Direction Type</em>'.
	 * @see jadex.tools.gpmn.DirectionType
	 * @generated
	 */
	EEnum getDirectionType();

	/**
	 * Returns the meta object for enum '{@link jadex.tools.gpmn.EdgeType <em>Edge Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Edge Type</em>'.
	 * @see jadex.tools.gpmn.EdgeType
	 * @generated
	 */
	EEnum getEdgeType();

	/**
	 * Returns the meta object for enum '{@link jadex.tools.gpmn.ExcludeType <em>Exclude Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Exclude Type</em>'.
	 * @see jadex.tools.gpmn.ExcludeType
	 * @generated
	 */
	EEnum getExcludeType();

	/**
	 * Returns the meta object for enum '{@link jadex.tools.gpmn.GoalType <em>Goal Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Goal Type</em>'.
	 * @see jadex.tools.gpmn.GoalType
	 * @generated
	 */
	EEnum getGoalType();

	/**
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.DirectionType <em>Direction Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Direction Type Object</em>'.
	 * @see jadex.tools.gpmn.DirectionType
	 * @model instanceClass="jadex.tools.gpmn.DirectionType"
	 *        extendedMetaData="name='DirectionType:Object' baseType='DirectionType'"
	 * @generated
	 */
	EDataType getDirectionTypeObject();

	/**
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.EdgeType <em>Edge Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Edge Type Object</em>'.
	 * @see jadex.tools.gpmn.EdgeType
	 * @model instanceClass="jadex.tools.gpmn.EdgeType"
	 *        extendedMetaData="name='EdgeType:Object' baseType='EdgeType'"
	 * @generated
	 */
	EDataType getEdgeTypeObject();

	/**
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.ExcludeType <em>Exclude Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Exclude Type Object</em>'.
	 * @see jadex.tools.gpmn.ExcludeType
	 * @model instanceClass="jadex.tools.gpmn.ExcludeType"
	 *        extendedMetaData="name='exclude_._type:Object' baseType='exclude_._type'"
	 * @generated
	 */
	EDataType getExcludeTypeObject();

	/**
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.GoalType <em>Goal Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Goal Type Object</em>'.
	 * @see jadex.tools.gpmn.GoalType
	 * @model instanceClass="jadex.tools.gpmn.GoalType"
	 *        extendedMetaData="name='GoalType:Object' baseType='GoalType'"
	 * @generated
	 */
	EDataType getGoalTypeObject();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GpmnFactory getGpmnFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals
	{
		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.AchieveGoalImpl <em>Achieve Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.AchieveGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAchieveGoal()
		 * @generated
		 */
		EClass ACHIEVE_GOAL = eINSTANCE.getAchieveGoal();

		/**
		 * The meta object literal for the '<em><b>Targetcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACHIEVE_GOAL__TARGETCONDITION = eINSTANCE.getAchieveGoal_Targetcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACHIEVE_GOAL__TARGETCONDITION_LANGUAGE = eINSTANCE.getAchieveGoal_TargetconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Failurecondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACHIEVE_GOAL__FAILURECONDITION = eINSTANCE.getAchieveGoal_Failurecondition();

		/**
		 * The meta object literal for the '<em><b>Failurecondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACHIEVE_GOAL__FAILURECONDITION_LANGUAGE = eINSTANCE.getAchieveGoal_FailureconditionLanguage();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ArtifactImpl <em>Artifact</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ArtifactImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getArtifact()
		 * @generated
		 */
		EClass ARTIFACT = eINSTANCE.getArtifact();

		/**
		 * The meta object literal for the '<em><b>Associations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARTIFACT__ASSOCIATIONS = eINSTANCE.getArtifact_Associations();

		/**
		 * The meta object literal for the '<em><b>Artifacts Container</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARTIFACT__ARTIFACTS_CONTAINER = eINSTANCE.getArtifact_ArtifactsContainer();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ArtifactsContainerImpl <em>Artifacts Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ArtifactsContainerImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getArtifactsContainer()
		 * @generated
		 */
		EClass ARTIFACTS_CONTAINER = eINSTANCE.getArtifactsContainer();

		/**
		 * The meta object literal for the '<em><b>Artifacts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ARTIFACTS_CONTAINER__ARTIFACTS = eINSTANCE.getArtifactsContainer_Artifacts();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.AssociationImpl <em>Association</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.AssociationImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAssociation()
		 * @generated
		 */
		EClass ASSOCIATION = eINSTANCE.getAssociation();

		/**
		 * The meta object literal for the '<em><b>Direction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSOCIATION__DIRECTION = eINSTANCE.getAssociation_Direction();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSOCIATION__SOURCE = eINSTANCE.getAssociation_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSOCIATION__TARGET = eINSTANCE.getAssociation_Target();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.AssociationTargetImpl <em>Association Target</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.AssociationTargetImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAssociationTarget()
		 * @generated
		 */
		EClass ASSOCIATION_TARGET = eINSTANCE.getAssociationTarget();

		/**
		 * The meta object literal for the '<em><b>Associations</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSOCIATION_TARGET__ASSOCIATIONS = eINSTANCE.getAssociationTarget_Associations();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ContextImpl <em>Context</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ContextImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getContext()
		 * @generated
		 */
		EClass CONTEXT = eINSTANCE.getContext();

		/**
		 * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT__ELEMENTS = eINSTANCE.getContext_Elements();

		/**
		 * The meta object literal for the '<em><b>Roles</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT__ROLES = eINSTANCE.getContext_Roles();

		/**
		 * The meta object literal for the '<em><b>Groups</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT__GROUPS = eINSTANCE.getContext_Groups();

		/**
		 * The meta object literal for the '<em><b>Types</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT__TYPES = eINSTANCE.getContext_Types();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ContextElementImpl <em>Context Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ContextElementImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getContextElement()
		 * @generated
		 */
		EClass CONTEXT_ELEMENT = eINSTANCE.getContextElement();

		/**
		 * The meta object literal for the '<em><b>Context</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT_ELEMENT__CONTEXT = eINSTANCE.getContextElement_Context();

		/**
		 * The meta object literal for the '<em><b>Dynamic</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_ELEMENT__DYNAMIC = eINSTANCE.getContextElement_Dynamic();

		/**
		 * The meta object literal for the '<em><b>Initial Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_ELEMENT__INITIAL_VALUE = eINSTANCE.getContextElement_InitialValue();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_ELEMENT__NAME = eINSTANCE.getContextElement_Name();

		/**
		 * The meta object literal for the '<em><b>Set</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_ELEMENT__SET = eINSTANCE.getContextElement_Set();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_ELEMENT__TYPE = eINSTANCE.getContextElement_Type();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.DataObjectImpl <em>Data Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.DataObjectImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDataObject()
		 * @generated
		 */
		EClass DATA_OBJECT = eINSTANCE.getDataObject();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.EdgeImpl <em>Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.EdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getEdge()
		 * @generated
		 */
		EClass EDGE = eINSTANCE.getEdge();

		/**
		 * The meta object literal for the '<em><b>Is Default</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDGE__IS_DEFAULT = eINSTANCE.getEdge_IsDefault();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDGE__SOURCE = eINSTANCE.getEdge_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDGE__TARGET = eINSTANCE.getEdge_Target();

		/**
		 * The meta object literal for the '<em><b>Graph</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EDGE__GRAPH = eINSTANCE.getEdge_Graph();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.GoalImpl <em>Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.GoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoal()
		 * @generated
		 */
		EClass GOAL = eINSTANCE.getGoal();

		/**
		 * The meta object literal for the '<em><b>Unique</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__UNIQUE = eINSTANCE.getGoal_Unique();

		/**
		 * The meta object literal for the '<em><b>Creationcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__CREATIONCONDITION = eINSTANCE.getGoal_Creationcondition();

		/**
		 * The meta object literal for the '<em><b>Creationcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__CREATIONCONDITION_LANGUAGE = eINSTANCE.getGoal_CreationconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Contextcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__CONTEXTCONDITION = eINSTANCE.getGoal_Contextcondition();

		/**
		 * The meta object literal for the '<em><b>Contextcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__CONTEXTCONDITION_LANGUAGE = eINSTANCE.getGoal_ContextconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Dropcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__DROPCONDITION = eINSTANCE.getGoal_Dropcondition();

		/**
		 * The meta object literal for the '<em><b>Dropcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__DROPCONDITION_LANGUAGE = eINSTANCE.getGoal_DropconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Recurcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RECURCONDITION = eINSTANCE.getGoal_Recurcondition();

		/**
		 * The meta object literal for the '<em><b>Deliberation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__DELIBERATION = eINSTANCE.getGoal_Deliberation();

		/**
		 * The meta object literal for the '<em><b>On Success Handler</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__ON_SUCCESS_HANDLER = eINSTANCE.getGoal_OnSuccessHandler();

		/**
		 * The meta object literal for the '<em><b>On Skip Handler</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__ON_SKIP_HANDLER = eINSTANCE.getGoal_OnSkipHandler();

		/**
		 * The meta object literal for the '<em><b>On Failure Handler</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__ON_FAILURE_HANDLER = eINSTANCE.getGoal_OnFailureHandler();

		/**
		 * The meta object literal for the '<em><b>Exclude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__EXCLUDE = eINSTANCE.getGoal_Exclude();

		/**
		 * The meta object literal for the '<em><b>Goal Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__GOAL_TYPE = eINSTANCE.getGoal_GoalType();

		/**
		 * The meta object literal for the '<em><b>Posttoall</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__POSTTOALL = eINSTANCE.getGoal_Posttoall();

		/**
		 * The meta object literal for the '<em><b>Randomselection</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RANDOMSELECTION = eINSTANCE.getGoal_Randomselection();

		/**
		 * The meta object literal for the '<em><b>Recalculate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RECALCULATE = eINSTANCE.getGoal_Recalculate();

		/**
		 * The meta object literal for the '<em><b>Recur</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RECUR = eINSTANCE.getGoal_Recur();

		/**
		 * The meta object literal for the '<em><b>Recurdelay</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RECURDELAY = eINSTANCE.getGoal_Recurdelay();

		/**
		 * The meta object literal for the '<em><b>Retry</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RETRY = eINSTANCE.getGoal_Retry();

		/**
		 * The meta object literal for the '<em><b>Retrydelay</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__RETRYDELAY = eINSTANCE.getGoal_Retrydelay();

		/**
		 * The meta object literal for the '<em><b>Sequential</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__SEQUENTIAL = eINSTANCE.getGoal_Sequential();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.GpmnDiagramImpl <em>Diagram</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.GpmnDiagramImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGpmnDiagram()
		 * @generated
		 */
		EClass GPMN_DIAGRAM = eINSTANCE.getGpmnDiagram();

		/**
		 * The meta object literal for the '<em><b>Processes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__PROCESSES = eINSTANCE.getGpmnDiagram_Processes();

		/**
		 * The meta object literal for the '<em><b>Messages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__MESSAGES = eINSTANCE.getGpmnDiagram_Messages();

		/**
		 * The meta object literal for the '<em><b>Imports</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__IMPORTS = eINSTANCE.getGpmnDiagram_Imports();

		/**
		 * The meta object literal for the '<em><b>Package</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__PACKAGE = eINSTANCE.getGpmnDiagram_Package();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.GraphImpl <em>Graph</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.GraphImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGraph()
		 * @generated
		 */
		EClass GRAPH = eINSTANCE.getGraph();

		/**
		 * The meta object literal for the '<em><b>Vertices</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__VERTICES = eINSTANCE.getGraph_Vertices();

		/**
		 * The meta object literal for the '<em><b>Sequence Edges</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__SEQUENCE_EDGES = eINSTANCE.getGraph_SequenceEdges();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.GroupImpl <em>Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.GroupImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGroup()
		 * @generated
		 */
		EClass GROUP = eINSTANCE.getGroup();

		/**
		 * The meta object literal for the '<em><b>Members</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROUP__MEMBERS = eINSTANCE.getGroup_Members();

		/**
		 * The meta object literal for the '<em><b>Coordinator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROUP__COORDINATOR = eINSTANCE.getGroup_Coordinator();

		/**
		 * The meta object literal for the '<em><b>Head</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROUP__HEAD = eINSTANCE.getGroup_Head();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.IdentifiableImpl <em>Identifiable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.IdentifiableImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getIdentifiable()
		 * @generated
		 */
		EClass IDENTIFIABLE = eINSTANCE.getIdentifiable();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IDENTIFIABLE__ID = eINSTANCE.getIdentifiable_Id();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.InterGraphEdgeImpl <em>Inter Graph Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.InterGraphEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getInterGraphEdge()
		 * @generated
		 */
		EClass INTER_GRAPH_EDGE = eINSTANCE.getInterGraphEdge();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTER_GRAPH_EDGE__SOURCE = eINSTANCE.getInterGraphEdge_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTER_GRAPH_EDGE__TARGET = eINSTANCE.getInterGraphEdge_Target();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.InterGraphVertexImpl <em>Inter Graph Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.InterGraphVertexImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getInterGraphVertex()
		 * @generated
		 */
		EClass INTER_GRAPH_VERTEX = eINSTANCE.getInterGraphVertex();

		/**
		 * The meta object literal for the '<em><b>Inter Graph Messages</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTER_GRAPH_VERTEX__INTER_GRAPH_MESSAGES = eINSTANCE.getInterGraphVertex_InterGraphMessages();

		/**
		 * The meta object literal for the '<em><b>Incoming Inter Graph Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTER_GRAPH_VERTEX__INCOMING_INTER_GRAPH_EDGES = eINSTANCE.getInterGraphVertex_IncomingInterGraphEdges();

		/**
		 * The meta object literal for the '<em><b>Outgoing Inter Graph Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTER_GRAPH_VERTEX__OUTGOING_INTER_GRAPH_EDGES = eINSTANCE.getInterGraphVertex_OutgoingInterGraphEdges();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.MaintainGoalImpl <em>Maintain Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.MaintainGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getMaintainGoal()
		 * @generated
		 */
		EClass MAINTAIN_GOAL = eINSTANCE.getMaintainGoal();

		/**
		 * The meta object literal for the '<em><b>Maintaincondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAINTAIN_GOAL__MAINTAINCONDITION = eINSTANCE.getMaintainGoal_Maintaincondition();

		/**
		 * The meta object literal for the '<em><b>Maintaincondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAINTAIN_GOAL__MAINTAINCONDITION_LANGUAGE = eINSTANCE.getMaintainGoal_MaintainconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Targetcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAINTAIN_GOAL__TARGETCONDITION = eINSTANCE.getMaintainGoal_Targetcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAINTAIN_GOAL__TARGETCONDITION_LANGUAGE = eINSTANCE.getMaintainGoal_TargetconditionLanguage();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.MessageGoalImpl <em>Message Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.MessageGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getMessageGoal()
		 * @generated
		 */
		EClass MESSAGE_GOAL = eINSTANCE.getMessageGoal();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.MessagingEdgeImpl <em>Messaging Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.MessagingEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getMessagingEdge()
		 * @generated
		 */
		EClass MESSAGING_EDGE = eINSTANCE.getMessagingEdge();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MESSAGING_EDGE__MESSAGE = eINSTANCE.getMessagingEdge_Message();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MESSAGING_EDGE__GPMN_DIAGRAM = eINSTANCE.getMessagingEdge_GpmnDiagram();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.NamedObjectImpl <em>Named Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.NamedObjectImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getNamedObject()
		 * @generated
		 */
		EClass NAMED_OBJECT = eINSTANCE.getNamedObject();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMED_OBJECT__DESCRIPTION = eINSTANCE.getNamedObject_Description();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMED_OBJECT__NAME = eINSTANCE.getNamedObject_Name();

		/**
		 * The meta object literal for the '<em><b>Ncname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMED_OBJECT__NCNAME = eINSTANCE.getNamedObject_Ncname();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ParallelGoalImpl <em>Parallel Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ParallelGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParallelGoal()
		 * @generated
		 */
		EClass PARALLEL_GOAL = eINSTANCE.getParallelGoal();

		/**
		 * The meta object literal for the '<em><b>Targetcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARALLEL_GOAL__TARGETCONDITION = eINSTANCE.getParallelGoal_Targetcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARALLEL_GOAL__TARGETCONDITION_LANGUAGE = eINSTANCE.getParallelGoal_TargetconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Failurecondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARALLEL_GOAL__FAILURECONDITION = eINSTANCE.getParallelGoal_Failurecondition();

		/**
		 * The meta object literal for the '<em><b>Failurecondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARALLEL_GOAL__FAILURECONDITION_LANGUAGE = eINSTANCE.getParallelGoal_FailureconditionLanguage();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ParameterImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Direction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__DIRECTION = eINSTANCE.getParameter_Direction();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__TYPE = eINSTANCE.getParameter_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ParameterizedEdgeImpl <em>Parameterized Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ParameterizedEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameterizedEdge()
		 * @generated
		 */
		EClass PARAMETERIZED_EDGE = eINSTANCE.getParameterizedEdge();

		/**
		 * The meta object literal for the '<em><b>Parameter Mapping</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETERIZED_EDGE__PARAMETER_MAPPING = eINSTANCE.getParameterizedEdge_ParameterMapping();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ParameterizedVertexImpl <em>Parameterized Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ParameterizedVertexImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameterizedVertex()
		 * @generated
		 */
		EClass PARAMETERIZED_VERTEX = eINSTANCE.getParameterizedVertex();

		/**
		 * The meta object literal for the '<em><b>Parameter</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETERIZED_VERTEX__PARAMETER = eINSTANCE.getParameterizedVertex_Parameter();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.PerformGoalImpl <em>Perform Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.PerformGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPerformGoal()
		 * @generated
		 */
		EClass PERFORM_GOAL = eINSTANCE.getPerformGoal();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.PlanImpl <em>Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.PlanImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPlan()
		 * @generated
		 */
		EClass PLAN = eINSTANCE.getPlan();

		/**
		 * The meta object literal for the '<em><b>Bpmn Plan</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PLAN__BPMN_PLAN = eINSTANCE.getPlan_BpmnPlan();

		/**
		 * The meta object literal for the '<em><b>Priority</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PLAN__PRIORITY = eINSTANCE.getPlan_Priority();

		/**
		 * The meta object literal for the '<em><b>Precondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PLAN__PRECONDITION = eINSTANCE.getPlan_Precondition();

		/**
		 * The meta object literal for the '<em><b>Contextcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PLAN__CONTEXTCONDITION = eINSTANCE.getPlan_Contextcondition();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.PlanEdgeImpl <em>Plan Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.PlanEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPlanEdge()
		 * @generated
		 */
		EClass PLAN_EDGE = eINSTANCE.getPlanEdge();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ProcessImpl <em>Process</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ProcessImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getProcess()
		 * @generated
		 */
		EClass PROCESS = eINSTANCE.getProcess();

		/**
		 * The meta object literal for the '<em><b>Looping</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROCESS__LOOPING = eINSTANCE.getProcess_Looping();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROCESS__GPMN_DIAGRAM = eINSTANCE.getProcess_GpmnDiagram();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.QueryGoalImpl <em>Query Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.QueryGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getQueryGoal()
		 * @generated
		 */
		EClass QUERY_GOAL = eINSTANCE.getQueryGoal();

		/**
		 * The meta object literal for the '<em><b>Targetcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY_GOAL__TARGETCONDITION = eINSTANCE.getQueryGoal_Targetcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY_GOAL__TARGETCONDITION_LANGUAGE = eINSTANCE.getQueryGoal_TargetconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Failurecondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY_GOAL__FAILURECONDITION = eINSTANCE.getQueryGoal_Failurecondition();

		/**
		 * The meta object literal for the '<em><b>Failurecondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY_GOAL__FAILURECONDITION_LANGUAGE = eINSTANCE.getQueryGoal_FailureconditionLanguage();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.RoleImpl <em>Role</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.RoleImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getRole()
		 * @generated
		 */
		EClass ROLE = eINSTANCE.getRole();

		/**
		 * The meta object literal for the '<em><b>Initial Person</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ROLE__INITIAL_PERSON = eINSTANCE.getRole_InitialPerson();

		/**
		 * The meta object literal for the '<em><b>Person Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ROLE__PERSON_TYPE = eINSTANCE.getRole_PersonType();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.SequentialGoalImpl <em>Sequential Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.SequentialGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSequentialGoal()
		 * @generated
		 */
		EClass SEQUENTIAL_GOAL = eINSTANCE.getSequentialGoal();

		/**
		 * The meta object literal for the '<em><b>Targetcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SEQUENTIAL_GOAL__TARGETCONDITION = eINSTANCE.getSequentialGoal_Targetcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SEQUENTIAL_GOAL__TARGETCONDITION_LANGUAGE = eINSTANCE.getSequentialGoal_TargetconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Failurecondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SEQUENTIAL_GOAL__FAILURECONDITION = eINSTANCE.getSequentialGoal_Failurecondition();

		/**
		 * The meta object literal for the '<em><b>Failurecondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SEQUENTIAL_GOAL__FAILURECONDITION_LANGUAGE = eINSTANCE.getSequentialGoal_FailureconditionLanguage();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.SubGoalEdgeImpl <em>Sub Goal Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.SubGoalEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSubGoalEdge()
		 * @generated
		 */
		EClass SUB_GOAL_EDGE = eINSTANCE.getSubGoalEdge();

		/**
		 * The meta object literal for the '<em><b>Sequential Order</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SUB_GOAL_EDGE__SEQUENTIAL_ORDER = eINSTANCE.getSubGoalEdge_SequentialOrder();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.SubProcessGoalImpl <em>Sub Process Goal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.SubProcessGoalImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSubProcessGoal()
		 * @generated
		 */
		EClass SUB_PROCESS_GOAL = eINSTANCE.getSubProcessGoal();

		/**
		 * The meta object literal for the '<em><b>Goalref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SUB_PROCESS_GOAL__GOALREF = eINSTANCE.getSubProcessGoal_Goalref();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.TextAnnotationImpl <em>Text Annotation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.TextAnnotationImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getTextAnnotation()
		 * @generated
		 */
		EClass TEXT_ANNOTATION = eINSTANCE.getTextAnnotation();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.VertexImpl <em>Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.VertexImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getVertex()
		 * @generated
		 */
		EClass VERTEX = eINSTANCE.getVertex();

		/**
		 * The meta object literal for the '<em><b>Outgoing Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VERTEX__OUTGOING_EDGES = eINSTANCE.getVertex_OutgoingEdges();

		/**
		 * The meta object literal for the '<em><b>Incoming Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VERTEX__INCOMING_EDGES = eINSTANCE.getVertex_IncomingEdges();

		/**
		 * The meta object literal for the '<em><b>Graph</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VERTEX__GRAPH = eINSTANCE.getVertex_Graph();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.GenericGpmnElementImpl <em>Generic Gpmn Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.GenericGpmnElementImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGenericGpmnElement()
		 * @generated
		 */
		EClass GENERIC_GPMN_ELEMENT = eINSTANCE.getGenericGpmnElement();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GENERIC_GPMN_ELEMENT__ATTRIBUTES = eINSTANCE.getGenericGpmnElement_Attributes();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GENERIC_GPMN_ELEMENT__PROPERTIES = eINSTANCE.getGenericGpmnElement_Properties();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.GenericGpmnEdgeImpl <em>Generic Gpmn Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.GenericGpmnEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGenericGpmnEdge()
		 * @generated
		 */
		EClass GENERIC_GPMN_EDGE = eINSTANCE.getGenericGpmnEdge();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.DirectionType <em>Direction Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.DirectionType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDirectionType()
		 * @generated
		 */
		EEnum DIRECTION_TYPE = eINSTANCE.getDirectionType();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.EdgeType <em>Edge Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.EdgeType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getEdgeType()
		 * @generated
		 */
		EEnum EDGE_TYPE = eINSTANCE.getEdgeType();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.ExcludeType <em>Exclude Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.ExcludeType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getExcludeType()
		 * @generated
		 */
		EEnum EXCLUDE_TYPE = eINSTANCE.getExcludeType();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.GoalType <em>Goal Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.GoalType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoalType()
		 * @generated
		 */
		EEnum GOAL_TYPE = eINSTANCE.getGoalType();

		/**
		 * The meta object literal for the '<em>Direction Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.DirectionType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDirectionTypeObject()
		 * @generated
		 */
		EDataType DIRECTION_TYPE_OBJECT = eINSTANCE.getDirectionTypeObject();

		/**
		 * The meta object literal for the '<em>Edge Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.EdgeType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getEdgeTypeObject()
		 * @generated
		 */
		EDataType EDGE_TYPE_OBJECT = eINSTANCE.getEdgeTypeObject();

		/**
		 * The meta object literal for the '<em>Exclude Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.ExcludeType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getExcludeTypeObject()
		 * @generated
		 */
		EDataType EXCLUDE_TYPE_OBJECT = eINSTANCE.getExcludeTypeObject();

		/**
		 * The meta object literal for the '<em>Goal Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.GoalType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoalTypeObject()
		 * @generated
		 */
		EDataType GOAL_TYPE_OBJECT = eINSTANCE.getGoalTypeObject();

	}

} //GpmnPackage
