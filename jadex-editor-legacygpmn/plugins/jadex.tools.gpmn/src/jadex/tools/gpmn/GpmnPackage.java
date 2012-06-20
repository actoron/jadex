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
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "gpmn"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://jadex.sourceforge.net/gpmn"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "gpmn"; //$NON-NLS-1$

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
	int IDENTIFIABLE = 10;

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
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.AbstractEdgeImpl <em>Abstract Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.AbstractEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAbstractEdge()
	 * @generated
	 */
	int ABSTRACT_EDGE = 0;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EDGE__EANNOTATIONS = IDENTIFIABLE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EDGE__ID = IDENTIFIABLE__ID;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EDGE__PARAMETER_MAPPING = IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Abstract Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EDGE_FEATURE_COUNT = IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.AbstractNodeImpl <em>Abstract Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.AbstractNodeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAbstractNode()
	 * @generated
	 */
	int ABSTRACT_NODE = 1;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE__EANNOTATIONS = IDENTIFIABLE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE__ID = IDENTIFIABLE__ID;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE__DESCRIPTION = IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE__NAME = IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE__NCNAME = IDENTIFIABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE__PARAMETER = IDENTIFIABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Abstract Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_NODE_FEATURE_COUNT = IDENTIFIABLE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.AbstractPlanImpl <em>Abstract Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.AbstractPlanImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAbstractPlan()
	 * @generated
	 */
	int ABSTRACT_PLAN = 2;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__EANNOTATIONS = ABSTRACT_NODE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__ID = ABSTRACT_NODE__ID;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__DESCRIPTION = ABSTRACT_NODE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__NAME = ABSTRACT_NODE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__NCNAME = ABSTRACT_NODE__NCNAME;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__PARAMETER = ABSTRACT_NODE__PARAMETER;

	/**
	 * The feature id for the '<em><b>Plan Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__PLAN_EDGES = ABSTRACT_NODE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__CONTEXTCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Precondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__PRECONDITION = ABSTRACT_NODE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Precondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__PRECONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__PRIORITY = ABSTRACT_NODE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN__GPMN_DIAGRAM = ABSTRACT_NODE_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Abstract Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_PLAN_FEATURE_COUNT = ABSTRACT_NODE_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ActivatableImpl <em>Activatable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ActivatableImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getActivatable()
	 * @generated
	 */
	int ACTIVATABLE = 3;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATABLE__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Activation Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATABLE__ACTIVATION_EDGES = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Activatable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATABLE_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ActivationEdgeImpl <em>Activation Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ActivationEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getActivationEdge()
	 * @generated
	 */
	int ACTIVATION_EDGE = 4;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__EANNOTATIONS = ABSTRACT_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__ID = ABSTRACT_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__PARAMETER_MAPPING = ABSTRACT_EDGE__PARAMETER_MAPPING;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__SOURCE = ABSTRACT_EDGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__TARGET = ABSTRACT_EDGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__GPMN_DIAGRAM = ABSTRACT_EDGE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Order</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE__ORDER = ABSTRACT_EDGE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Activation Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_EDGE_FEATURE_COUNT = ABSTRACT_EDGE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.BpmnPlanImpl <em>Bpmn Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.BpmnPlanImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getBpmnPlan()
	 * @generated
	 */
	int BPMN_PLAN = 5;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__EANNOTATIONS = ABSTRACT_PLAN__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__ID = ABSTRACT_PLAN__ID;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__DESCRIPTION = ABSTRACT_PLAN__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__NAME = ABSTRACT_PLAN__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__NCNAME = ABSTRACT_PLAN__NCNAME;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__PARAMETER = ABSTRACT_PLAN__PARAMETER;

	/**
	 * The feature id for the '<em><b>Plan Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__PLAN_EDGES = ABSTRACT_PLAN__PLAN_EDGES;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__CONTEXTCONDITION = ABSTRACT_PLAN__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__TARGETCONDITION_LANGUAGE = ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Precondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__PRECONDITION = ABSTRACT_PLAN__PRECONDITION;

	/**
	 * The feature id for the '<em><b>Precondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__PRECONDITION_LANGUAGE = ABSTRACT_PLAN__PRECONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__PRIORITY = ABSTRACT_PLAN__PRIORITY;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__GPMN_DIAGRAM = ABSTRACT_PLAN__GPMN_DIAGRAM;

	/**
	 * The feature id for the '<em><b>Planref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN__PLANREF = ABSTRACT_PLAN_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Bpmn Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLAN_FEATURE_COUNT = ABSTRACT_PLAN_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ContextImpl <em>Context</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ContextImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getContext()
	 * @generated
	 */
	int CONTEXT = 6;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Elements</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__ELEMENTS = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__GPMN_DIAGRAM = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ContextElementImpl <em>Context Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ContextElementImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getContextElement()
	 * @generated
	 */
	int CONTEXT_ELEMENT = 7;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__VALUE = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__NAME = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Set</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__SET = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__TYPE = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Context</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT__CONTEXT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Context Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_ELEMENT_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GoalImpl <em>Goal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GoalImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoal()
	 * @generated
	 */
	int GOAL = 8;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__EANNOTATIONS = ABSTRACT_NODE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ID = ABSTRACT_NODE__ID;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DESCRIPTION = ABSTRACT_NODE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__NAME = ABSTRACT_NODE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__NCNAME = ABSTRACT_NODE__NCNAME;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__PARAMETER = ABSTRACT_NODE__PARAMETER;

	/**
	 * The feature id for the '<em><b>Activation Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__ACTIVATION_EDGES = ABSTRACT_NODE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Plan Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__PLAN_EDGES = ABSTRACT_NODE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Suppression Edge</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__SUPPRESSION_EDGE = ABSTRACT_NODE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__UNIQUE = ABSTRACT_NODE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Creationcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CREATIONCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Creationcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CREATIONCONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CONTEXTCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Contextcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__CONTEXTCONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Dropcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DROPCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Dropcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DROPCONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Recurcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECURCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Deliberation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__DELIBERATION = ABSTRACT_NODE_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Targetcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__TARGETCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__TARGETCONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Failurecondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__FAILURECONDITION = ABSTRACT_NODE_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Failurecondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__FAILURECONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Maintaincondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__MAINTAINCONDITION = ABSTRACT_NODE_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Maintaincondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__MAINTAINCONDITION_LANGUAGE = ABSTRACT_NODE_FEATURE_COUNT + 17;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__EXCLUDE = ABSTRACT_NODE_FEATURE_COUNT + 18;

	/**
	 * The feature id for the '<em><b>Goal Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__GOAL_TYPE = ABSTRACT_NODE_FEATURE_COUNT + 19;

	/**
	 * The feature id for the '<em><b>Posttoall</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__POSTTOALL = ABSTRACT_NODE_FEATURE_COUNT + 20;

	/**
	 * The feature id for the '<em><b>Randomselection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RANDOMSELECTION = ABSTRACT_NODE_FEATURE_COUNT + 21;

	/**
	 * The feature id for the '<em><b>Recalculate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECALCULATE = ABSTRACT_NODE_FEATURE_COUNT + 22;

	/**
	 * The feature id for the '<em><b>Recur</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECUR = ABSTRACT_NODE_FEATURE_COUNT + 23;

	/**
	 * The feature id for the '<em><b>Recurdelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RECURDELAY = ABSTRACT_NODE_FEATURE_COUNT + 24;

	/**
	 * The feature id for the '<em><b>Retry</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RETRY = ABSTRACT_NODE_FEATURE_COUNT + 25;

	/**
	 * The feature id for the '<em><b>Retrydelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__RETRYDELAY = ABSTRACT_NODE_FEATURE_COUNT + 26;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL__GPMN_DIAGRAM = ABSTRACT_NODE_FEATURE_COUNT + 27;

	/**
	 * The number of structural features of the '<em>Goal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GOAL_FEATURE_COUNT = ABSTRACT_NODE_FEATURE_COUNT + 28;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.NamedObjectImpl <em>Named Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.NamedObjectImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getNamedObject()
	 * @generated
	 */
	int NAMED_OBJECT = 12;

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
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.GpmnDiagramImpl <em>Diagram</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.GpmnDiagramImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGpmnDiagram()
	 * @generated
	 */
	int GPMN_DIAGRAM = 9;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__EANNOTATIONS = NAMED_OBJECT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__DESCRIPTION = NAMED_OBJECT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__NAME = NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__NCNAME = NAMED_OBJECT__NCNAME;

	/**
	 * The feature id for the '<em><b>Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__PACKAGE = NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Imports</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__IMPORTS = NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Context</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__CONTEXT = NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Goals</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__GOALS = NAMED_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Plans</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__PLANS = NAMED_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Sub Processes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__SUB_PROCESSES = NAMED_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Activation Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__ACTIVATION_EDGES = NAMED_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Plan Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__PLAN_EDGES = NAMED_OBJECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Suppression Edges</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__SUPPRESSION_EDGES = NAMED_OBJECT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__AUTHOR = NAMED_OBJECT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__REVISION = NAMED_OBJECT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__TITLE = NAMED_OBJECT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM__VERSION = NAMED_OBJECT_FEATURE_COUNT + 12;

	/**
	 * The number of structural features of the '<em>Diagram</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GPMN_DIAGRAM_FEATURE_COUNT = NAMED_OBJECT_FEATURE_COUNT + 13;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ActivationPlanImpl <em>Activation Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ActivationPlanImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getActivationPlan()
	 * @generated
	 */
	int ACTIVATION_PLAN = 11;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__EANNOTATIONS = ABSTRACT_PLAN__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__ID = ABSTRACT_PLAN__ID;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__DESCRIPTION = ABSTRACT_PLAN__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__NAME = ABSTRACT_PLAN__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__NCNAME = ABSTRACT_PLAN__NCNAME;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__PARAMETER = ABSTRACT_PLAN__PARAMETER;

	/**
	 * The feature id for the '<em><b>Plan Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__PLAN_EDGES = ABSTRACT_PLAN__PLAN_EDGES;

	/**
	 * The feature id for the '<em><b>Contextcondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__CONTEXTCONDITION = ABSTRACT_PLAN__CONTEXTCONDITION;

	/**
	 * The feature id for the '<em><b>Targetcondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__TARGETCONDITION_LANGUAGE = ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Precondition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__PRECONDITION = ABSTRACT_PLAN__PRECONDITION;

	/**
	 * The feature id for the '<em><b>Precondition Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__PRECONDITION_LANGUAGE = ABSTRACT_PLAN__PRECONDITION_LANGUAGE;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__PRIORITY = ABSTRACT_PLAN__PRIORITY;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__GPMN_DIAGRAM = ABSTRACT_PLAN__GPMN_DIAGRAM;

	/**
	 * The feature id for the '<em><b>Activation Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__ACTIVATION_EDGES = ABSTRACT_PLAN_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN__MODE = ABSTRACT_PLAN_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Activation Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVATION_PLAN_FEATURE_COUNT = ABSTRACT_PLAN_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ParameterImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 13;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__VALUE = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__DIRECTION = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__TYPE = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.ParameterMappingImpl <em>Parameter Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.ParameterMappingImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameterMapping()
	 * @generated
	 */
	int PARAMETER_MAPPING = 14;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_MAPPING__EANNOTATIONS = EcorePackage.EMODEL_ELEMENT__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_MAPPING__VALUE = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_MAPPING__NAME = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Parameter Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_MAPPING_FEATURE_COUNT = EcorePackage.EMODEL_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.PlanEdgeImpl <em>Plan Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.PlanEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getPlanEdge()
	 * @generated
	 */
	int PLAN_EDGE = 15;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__EANNOTATIONS = ABSTRACT_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__ID = ABSTRACT_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__PARAMETER_MAPPING = ABSTRACT_EDGE__PARAMETER_MAPPING;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__SOURCE = ABSTRACT_EDGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__TARGET = ABSTRACT_EDGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE__GPMN_DIAGRAM = ABSTRACT_EDGE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Plan Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLAN_EDGE_FEATURE_COUNT = ABSTRACT_EDGE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.SubProcessImpl <em>Sub Process</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.SubProcessImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSubProcess()
	 * @generated
	 */
	int SUB_PROCESS = 16;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__EANNOTATIONS = ABSTRACT_NODE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__ID = ABSTRACT_NODE__ID;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__DESCRIPTION = ABSTRACT_NODE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__NAME = ABSTRACT_NODE__NAME;

	/**
	 * The feature id for the '<em><b>Ncname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__NCNAME = ABSTRACT_NODE__NCNAME;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__PARAMETER = ABSTRACT_NODE__PARAMETER;

	/**
	 * The feature id for the '<em><b>Activation Edges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__ACTIVATION_EDGES = ABSTRACT_NODE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Processref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__PROCESSREF = ABSTRACT_NODE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Internal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__INTERNAL = ABSTRACT_NODE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS__GPMN_DIAGRAM = ABSTRACT_NODE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Sub Process</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_PROCESS_FEATURE_COUNT = ABSTRACT_NODE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.impl.SuppressionEdgeImpl <em>Suppression Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.impl.SuppressionEdgeImpl
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSuppressionEdge()
	 * @generated
	 */
	int SUPPRESSION_EDGE = 17;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE__EANNOTATIONS = ABSTRACT_EDGE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE__ID = ABSTRACT_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Parameter Mapping</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE__PARAMETER_MAPPING = ABSTRACT_EDGE__PARAMETER_MAPPING;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE__SOURCE = ABSTRACT_EDGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE__TARGET = ABSTRACT_EDGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE__GPMN_DIAGRAM = ABSTRACT_EDGE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Suppression Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPPRESSION_EDGE_FEATURE_COUNT = ABSTRACT_EDGE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.ConditionLanguage <em>Condition Language</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getConditionLanguage()
	 * @generated
	 */
	int CONDITION_LANGUAGE = 18;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.DirectionType <em>Direction Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.DirectionType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDirectionType()
	 * @generated
	 */
	int DIRECTION_TYPE = 19;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.ExcludeType <em>Exclude Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ExcludeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getExcludeType()
	 * @generated
	 */
	int EXCLUDE_TYPE = 20;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.GoalType <em>Goal Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.GoalType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoalType()
	 * @generated
	 */
	int GOAL_TYPE = 21;

	/**
	 * The meta object id for the '{@link jadex.tools.gpmn.ModeType <em>Mode Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ModeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getModeType()
	 * @generated
	 */
	int MODE_TYPE = 22;

	/**
	 * The meta object id for the '<em>Condition Language Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getConditionLanguageObject()
	 * @generated
	 */
	int CONDITION_LANGUAGE_OBJECT = 23;

	/**
	 * The meta object id for the '<em>Direction Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.DirectionType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getDirectionTypeObject()
	 * @generated
	 */
	int DIRECTION_TYPE_OBJECT = 24;

	/**
	 * The meta object id for the '<em>Exclude Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ExcludeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getExcludeTypeObject()
	 * @generated
	 */
	int EXCLUDE_TYPE_OBJECT = 25;

	/**
	 * The meta object id for the '<em>Goal Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.GoalType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getGoalTypeObject()
	 * @generated
	 */
	int GOAL_TYPE_OBJECT = 26;

	/**
	 * The meta object id for the '<em>Mode Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see jadex.tools.gpmn.ModeType
	 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getModeTypeObject()
	 * @generated
	 */
	int MODE_TYPE_OBJECT = 27;

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.AbstractEdge <em>Abstract Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Edge</em>'.
	 * @see jadex.tools.gpmn.AbstractEdge
	 * @generated
	 */
	EClass getAbstractEdge();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.AbstractEdge#getParameterMapping <em>Parameter Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Mapping</em>'.
	 * @see jadex.tools.gpmn.AbstractEdge#getParameterMapping()
	 * @see #getAbstractEdge()
	 * @generated
	 */
	EReference getAbstractEdge_ParameterMapping();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.AbstractNode <em>Abstract Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Node</em>'.
	 * @see jadex.tools.gpmn.AbstractNode
	 * @generated
	 */
	EClass getAbstractNode();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.AbstractNode#getParameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter</em>'.
	 * @see jadex.tools.gpmn.AbstractNode#getParameter()
	 * @see #getAbstractNode()
	 * @generated
	 */
	EReference getAbstractNode_Parameter();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.AbstractPlan <em>Abstract Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Plan</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan
	 * @generated
	 */
	EClass getAbstractPlan();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.AbstractPlan#getPlanEdges <em>Plan Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Plan Edges</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getPlanEdges()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EReference getAbstractPlan_PlanEdges();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AbstractPlan#getContextcondition <em>Contextcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Contextcondition</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getContextcondition()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EAttribute getAbstractPlan_Contextcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AbstractPlan#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getTargetconditionLanguage()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EAttribute getAbstractPlan_TargetconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AbstractPlan#getPrecondition <em>Precondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Precondition</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getPrecondition()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EAttribute getAbstractPlan_Precondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AbstractPlan#getPreconditionLanguage <em>Precondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Precondition Language</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getPreconditionLanguage()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EAttribute getAbstractPlan_PreconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.AbstractPlan#getPriority <em>Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Priority</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getPriority()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EAttribute getAbstractPlan_Priority();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.AbstractPlan#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.AbstractPlan#getGpmnDiagram()
	 * @see #getAbstractPlan()
	 * @generated
	 */
	EReference getAbstractPlan_GpmnDiagram();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Activatable <em>Activatable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Activatable</em>'.
	 * @see jadex.tools.gpmn.Activatable
	 * @generated
	 */
	EClass getActivatable();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.Activatable#getActivationEdges <em>Activation Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Activation Edges</em>'.
	 * @see jadex.tools.gpmn.Activatable#getActivationEdges()
	 * @see #getActivatable()
	 * @generated
	 */
	EReference getActivatable_ActivationEdges();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ActivationEdge <em>Activation Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Activation Edge</em>'.
	 * @see jadex.tools.gpmn.ActivationEdge
	 * @generated
	 */
	EClass getActivationEdge();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.ActivationEdge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see jadex.tools.gpmn.ActivationEdge#getSource()
	 * @see #getActivationEdge()
	 * @generated
	 */
	EReference getActivationEdge_Source();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.ActivationEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see jadex.tools.gpmn.ActivationEdge#getTarget()
	 * @see #getActivationEdge()
	 * @generated
	 */
	EReference getActivationEdge_Target();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.ActivationEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.ActivationEdge#getGpmnDiagram()
	 * @see #getActivationEdge()
	 * @generated
	 */
	EReference getActivationEdge_GpmnDiagram();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ActivationEdge#getOrder <em>Order</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Order</em>'.
	 * @see jadex.tools.gpmn.ActivationEdge#getOrder()
	 * @see #getActivationEdge()
	 * @generated
	 */
	EAttribute getActivationEdge_Order();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.BpmnPlan <em>Bpmn Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Bpmn Plan</em>'.
	 * @see jadex.tools.gpmn.BpmnPlan
	 * @generated
	 */
	EClass getBpmnPlan();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.BpmnPlan#getPlanref <em>Planref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Planref</em>'.
	 * @see jadex.tools.gpmn.BpmnPlan#getPlanref()
	 * @see #getBpmnPlan()
	 * @generated
	 */
	EAttribute getBpmnPlan_Planref();

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
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Context#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.Context#getGpmnDiagram()
	 * @see #getContext()
	 * @generated
	 */
	EReference getContext_GpmnDiagram();

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
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ContextElement#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see jadex.tools.gpmn.ContextElement#getValue()
	 * @see #getContextElement()
	 * @generated
	 */
	EAttribute getContextElement_Value();

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
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Goal <em>Goal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Goal</em>'.
	 * @see jadex.tools.gpmn.Goal
	 * @generated
	 */
	EClass getGoal();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.Goal#getPlanEdges <em>Plan Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Plan Edges</em>'.
	 * @see jadex.tools.gpmn.Goal#getPlanEdges()
	 * @see #getGoal()
	 * @generated
	 */
	EReference getGoal_PlanEdges();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.Goal#getSuppressionEdge <em>Suppression Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Suppression Edge</em>'.
	 * @see jadex.tools.gpmn.Goal#getSuppressionEdge()
	 * @see #getGoal()
	 * @generated
	 */
	EReference getGoal_SuppressionEdge();

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
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getTargetcondition <em>Targetcondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getTargetcondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Targetcondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getTargetconditionLanguage <em>Targetcondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Targetcondition Language</em>'.
	 * @see jadex.tools.gpmn.Goal#getTargetconditionLanguage()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_TargetconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getFailurecondition <em>Failurecondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getFailurecondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Failurecondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getFailureconditionLanguage <em>Failurecondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Failurecondition Language</em>'.
	 * @see jadex.tools.gpmn.Goal#getFailureconditionLanguage()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_FailureconditionLanguage();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getMaintaincondition <em>Maintaincondition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maintaincondition</em>'.
	 * @see jadex.tools.gpmn.Goal#getMaintaincondition()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_Maintaincondition();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.Goal#getMaintainconditionLanguage <em>Maintaincondition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maintaincondition Language</em>'.
	 * @see jadex.tools.gpmn.Goal#getMaintainconditionLanguage()
	 * @see #getGoal()
	 * @generated
	 */
	EAttribute getGoal_MaintainconditionLanguage();

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
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.Goal#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.Goal#getGpmnDiagram()
	 * @see #getGoal()
	 * @generated
	 */
	EReference getGoal_GpmnDiagram();

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
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.GpmnDiagram#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getAuthor()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EAttribute getGpmnDiagram_Author();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.GpmnDiagram#getRevision <em>Revision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Revision</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getRevision()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EAttribute getGpmnDiagram_Revision();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.GpmnDiagram#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getTitle()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EAttribute getGpmnDiagram_Title();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.GpmnDiagram#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getVersion()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EAttribute getGpmnDiagram_Version();

	/**
	 * Returns the meta object for the containment reference '{@link jadex.tools.gpmn.GpmnDiagram#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Context</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getContext()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_Context();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getGoals <em>Goals</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Goals</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getGoals()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_Goals();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getPlans <em>Plans</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Plans</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getPlans()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_Plans();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getSubProcesses <em>Sub Processes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sub Processes</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getSubProcesses()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_SubProcesses();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getActivationEdges <em>Activation Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Activation Edges</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getActivationEdges()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_ActivationEdges();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getPlanEdges <em>Plan Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Plan Edges</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getPlanEdges()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_PlanEdges();

	/**
	 * Returns the meta object for the containment reference list '{@link jadex.tools.gpmn.GpmnDiagram#getSuppressionEdges <em>Suppression Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Suppression Edges</em>'.
	 * @see jadex.tools.gpmn.GpmnDiagram#getSuppressionEdges()
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	EReference getGpmnDiagram_SuppressionEdges();

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
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ActivationPlan <em>Activation Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Activation Plan</em>'.
	 * @see jadex.tools.gpmn.ActivationPlan
	 * @generated
	 */
	EClass getActivationPlan();

	/**
	 * Returns the meta object for the reference list '{@link jadex.tools.gpmn.ActivationPlan#getActivationEdges <em>Activation Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Activation Edges</em>'.
	 * @see jadex.tools.gpmn.ActivationPlan#getActivationEdges()
	 * @see #getActivationPlan()
	 * @generated
	 */
	EReference getActivationPlan_ActivationEdges();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ActivationPlan#getMode <em>Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mode</em>'.
	 * @see jadex.tools.gpmn.ActivationPlan#getMode()
	 * @see #getActivationPlan()
	 * @generated
	 */
	EAttribute getActivationPlan_Mode();

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
	 * Returns the meta object for class '{@link jadex.tools.gpmn.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see jadex.tools.gpmn.Parameter
	 * @generated
	 */
	EClass getParameter();

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
	 * Returns the meta object for class '{@link jadex.tools.gpmn.ParameterMapping <em>Parameter Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Mapping</em>'.
	 * @see jadex.tools.gpmn.ParameterMapping
	 * @generated
	 */
	EClass getParameterMapping();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ParameterMapping#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see jadex.tools.gpmn.ParameterMapping#getValue()
	 * @see #getParameterMapping()
	 * @generated
	 */
	EAttribute getParameterMapping_Value();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.ParameterMapping#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see jadex.tools.gpmn.ParameterMapping#getName()
	 * @see #getParameterMapping()
	 * @generated
	 */
	EAttribute getParameterMapping_Name();

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
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.PlanEdge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see jadex.tools.gpmn.PlanEdge#getSource()
	 * @see #getPlanEdge()
	 * @generated
	 */
	EReference getPlanEdge_Source();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.PlanEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see jadex.tools.gpmn.PlanEdge#getTarget()
	 * @see #getPlanEdge()
	 * @generated
	 */
	EReference getPlanEdge_Target();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.PlanEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.PlanEdge#getGpmnDiagram()
	 * @see #getPlanEdge()
	 * @generated
	 */
	EReference getPlanEdge_GpmnDiagram();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.SubProcess <em>Sub Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sub Process</em>'.
	 * @see jadex.tools.gpmn.SubProcess
	 * @generated
	 */
	EClass getSubProcess();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SubProcess#getProcessref <em>Processref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Processref</em>'.
	 * @see jadex.tools.gpmn.SubProcess#getProcessref()
	 * @see #getSubProcess()
	 * @generated
	 */
	EAttribute getSubProcess_Processref();

	/**
	 * Returns the meta object for the attribute '{@link jadex.tools.gpmn.SubProcess#isInternal <em>Internal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Internal</em>'.
	 * @see jadex.tools.gpmn.SubProcess#isInternal()
	 * @see #getSubProcess()
	 * @generated
	 */
	EAttribute getSubProcess_Internal();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.SubProcess#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.SubProcess#getGpmnDiagram()
	 * @see #getSubProcess()
	 * @generated
	 */
	EReference getSubProcess_GpmnDiagram();

	/**
	 * Returns the meta object for class '{@link jadex.tools.gpmn.SuppressionEdge <em>Suppression Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Suppression Edge</em>'.
	 * @see jadex.tools.gpmn.SuppressionEdge
	 * @generated
	 */
	EClass getSuppressionEdge();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.SuppressionEdge#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see jadex.tools.gpmn.SuppressionEdge#getSource()
	 * @see #getSuppressionEdge()
	 * @generated
	 */
	EReference getSuppressionEdge_Source();

	/**
	 * Returns the meta object for the reference '{@link jadex.tools.gpmn.SuppressionEdge#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see jadex.tools.gpmn.SuppressionEdge#getTarget()
	 * @see #getSuppressionEdge()
	 * @generated
	 */
	EReference getSuppressionEdge_Target();

	/**
	 * Returns the meta object for the container reference '{@link jadex.tools.gpmn.SuppressionEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Gpmn Diagram</em>'.
	 * @see jadex.tools.gpmn.SuppressionEdge#getGpmnDiagram()
	 * @see #getSuppressionEdge()
	 * @generated
	 */
	EReference getSuppressionEdge_GpmnDiagram();

	/**
	 * Returns the meta object for enum '{@link jadex.tools.gpmn.ConditionLanguage <em>Condition Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Condition Language</em>'.
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @generated
	 */
	EEnum getConditionLanguage();

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
	 * Returns the meta object for enum '{@link jadex.tools.gpmn.ModeType <em>Mode Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Mode Type</em>'.
	 * @see jadex.tools.gpmn.ModeType
	 * @generated
	 */
	EEnum getModeType();

	/**
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.ConditionLanguage <em>Condition Language Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Condition Language Object</em>'.
	 * @see jadex.tools.gpmn.ConditionLanguage
	 * @model instanceClass="jadex.tools.gpmn.ConditionLanguage"
	 *        extendedMetaData="name='ConditionLanguage:Object' baseType='ConditionLanguage'"
	 * @generated
	 */
	EDataType getConditionLanguageObject();

	/**
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.DirectionType <em>Direction Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Direction Type Object</em>'.
	 * @see jadex.tools.gpmn.DirectionType
	 * @model instanceClass="jadex.tools.gpmn.DirectionType"
	 *        extendedMetaData="name='direction_._type:Object' baseType='direction_._type'"
	 * @generated
	 */
	EDataType getDirectionTypeObject();

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
	 * Returns the meta object for data type '{@link jadex.tools.gpmn.ModeType <em>Mode Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Mode Type Object</em>'.
	 * @see jadex.tools.gpmn.ModeType
	 * @model instanceClass="jadex.tools.gpmn.ModeType"
	 *        extendedMetaData="name='mode_._type:Object' baseType='mode_._type'"
	 * @generated
	 */
	EDataType getModeTypeObject();

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
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.AbstractEdgeImpl <em>Abstract Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.AbstractEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAbstractEdge()
		 * @generated
		 */
		EClass ABSTRACT_EDGE = eINSTANCE.getAbstractEdge();

		/**
		 * The meta object literal for the '<em><b>Parameter Mapping</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_EDGE__PARAMETER_MAPPING = eINSTANCE.getAbstractEdge_ParameterMapping();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.AbstractNodeImpl <em>Abstract Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.AbstractNodeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAbstractNode()
		 * @generated
		 */
		EClass ABSTRACT_NODE = eINSTANCE.getAbstractNode();

		/**
		 * The meta object literal for the '<em><b>Parameter</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_NODE__PARAMETER = eINSTANCE.getAbstractNode_Parameter();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.AbstractPlanImpl <em>Abstract Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.AbstractPlanImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getAbstractPlan()
		 * @generated
		 */
		EClass ABSTRACT_PLAN = eINSTANCE.getAbstractPlan();

		/**
		 * The meta object literal for the '<em><b>Plan Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_PLAN__PLAN_EDGES = eINSTANCE.getAbstractPlan_PlanEdges();

		/**
		 * The meta object literal for the '<em><b>Contextcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_PLAN__CONTEXTCONDITION = eINSTANCE.getAbstractPlan_Contextcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_PLAN__TARGETCONDITION_LANGUAGE = eINSTANCE.getAbstractPlan_TargetconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Precondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_PLAN__PRECONDITION = eINSTANCE.getAbstractPlan_Precondition();

		/**
		 * The meta object literal for the '<em><b>Precondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_PLAN__PRECONDITION_LANGUAGE = eINSTANCE.getAbstractPlan_PreconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Priority</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_PLAN__PRIORITY = eINSTANCE.getAbstractPlan_Priority();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_PLAN__GPMN_DIAGRAM = eINSTANCE.getAbstractPlan_GpmnDiagram();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ActivatableImpl <em>Activatable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ActivatableImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getActivatable()
		 * @generated
		 */
		EClass ACTIVATABLE = eINSTANCE.getActivatable();

		/**
		 * The meta object literal for the '<em><b>Activation Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVATABLE__ACTIVATION_EDGES = eINSTANCE.getActivatable_ActivationEdges();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ActivationEdgeImpl <em>Activation Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ActivationEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getActivationEdge()
		 * @generated
		 */
		EClass ACTIVATION_EDGE = eINSTANCE.getActivationEdge();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVATION_EDGE__SOURCE = eINSTANCE.getActivationEdge_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVATION_EDGE__TARGET = eINSTANCE.getActivationEdge_Target();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVATION_EDGE__GPMN_DIAGRAM = eINSTANCE.getActivationEdge_GpmnDiagram();

		/**
		 * The meta object literal for the '<em><b>Order</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVATION_EDGE__ORDER = eINSTANCE.getActivationEdge_Order();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.BpmnPlanImpl <em>Bpmn Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.BpmnPlanImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getBpmnPlan()
		 * @generated
		 */
		EClass BPMN_PLAN = eINSTANCE.getBpmnPlan();

		/**
		 * The meta object literal for the '<em><b>Planref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_PLAN__PLANREF = eINSTANCE.getBpmnPlan_Planref();

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
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT__GPMN_DIAGRAM = eINSTANCE.getContext_GpmnDiagram();

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
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT_ELEMENT__VALUE = eINSTANCE.getContextElement_Value();

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
		 * The meta object literal for the '<em><b>Context</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTEXT_ELEMENT__CONTEXT = eINSTANCE.getContextElement_Context();

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
		 * The meta object literal for the '<em><b>Plan Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GOAL__PLAN_EDGES = eINSTANCE.getGoal_PlanEdges();

		/**
		 * The meta object literal for the '<em><b>Suppression Edge</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GOAL__SUPPRESSION_EDGE = eINSTANCE.getGoal_SuppressionEdge();

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
		 * The meta object literal for the '<em><b>Targetcondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__TARGETCONDITION = eINSTANCE.getGoal_Targetcondition();

		/**
		 * The meta object literal for the '<em><b>Targetcondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__TARGETCONDITION_LANGUAGE = eINSTANCE.getGoal_TargetconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Failurecondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__FAILURECONDITION = eINSTANCE.getGoal_Failurecondition();

		/**
		 * The meta object literal for the '<em><b>Failurecondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__FAILURECONDITION_LANGUAGE = eINSTANCE.getGoal_FailureconditionLanguage();

		/**
		 * The meta object literal for the '<em><b>Maintaincondition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__MAINTAINCONDITION = eINSTANCE.getGoal_Maintaincondition();

		/**
		 * The meta object literal for the '<em><b>Maintaincondition Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GOAL__MAINTAINCONDITION_LANGUAGE = eINSTANCE.getGoal_MaintainconditionLanguage();

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
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GOAL__GPMN_DIAGRAM = eINSTANCE.getGoal_GpmnDiagram();

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
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__AUTHOR = eINSTANCE.getGpmnDiagram_Author();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__REVISION = eINSTANCE.getGpmnDiagram_Revision();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__TITLE = eINSTANCE.getGpmnDiagram_Title();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__VERSION = eINSTANCE.getGpmnDiagram_Version();

		/**
		 * The meta object literal for the '<em><b>Context</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__CONTEXT = eINSTANCE.getGpmnDiagram_Context();

		/**
		 * The meta object literal for the '<em><b>Goals</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__GOALS = eINSTANCE.getGpmnDiagram_Goals();

		/**
		 * The meta object literal for the '<em><b>Plans</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__PLANS = eINSTANCE.getGpmnDiagram_Plans();

		/**
		 * The meta object literal for the '<em><b>Sub Processes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__SUB_PROCESSES = eINSTANCE.getGpmnDiagram_SubProcesses();

		/**
		 * The meta object literal for the '<em><b>Activation Edges</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__ACTIVATION_EDGES = eINSTANCE.getGpmnDiagram_ActivationEdges();

		/**
		 * The meta object literal for the '<em><b>Plan Edges</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__PLAN_EDGES = eINSTANCE.getGpmnDiagram_PlanEdges();

		/**
		 * The meta object literal for the '<em><b>Suppression Edges</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GPMN_DIAGRAM__SUPPRESSION_EDGES = eINSTANCE.getGpmnDiagram_SuppressionEdges();

		/**
		 * The meta object literal for the '<em><b>Package</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__PACKAGE = eINSTANCE.getGpmnDiagram_Package();

		/**
		 * The meta object literal for the '<em><b>Imports</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GPMN_DIAGRAM__IMPORTS = eINSTANCE.getGpmnDiagram_Imports();

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
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ActivationPlanImpl <em>Activation Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ActivationPlanImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getActivationPlan()
		 * @generated
		 */
		EClass ACTIVATION_PLAN = eINSTANCE.getActivationPlan();

		/**
		 * The meta object literal for the '<em><b>Activation Edges</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVATION_PLAN__ACTIVATION_EDGES = eINSTANCE.getActivationPlan_ActivationEdges();

		/**
		 * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTIVATION_PLAN__MODE = eINSTANCE.getActivationPlan_Mode();

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
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ParameterImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

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
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.ParameterMappingImpl <em>Parameter Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.ParameterMappingImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getParameterMapping()
		 * @generated
		 */
		EClass PARAMETER_MAPPING = eINSTANCE.getParameterMapping();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_MAPPING__VALUE = eINSTANCE.getParameterMapping_Value();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_MAPPING__NAME = eINSTANCE.getParameterMapping_Name();

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
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PLAN_EDGE__SOURCE = eINSTANCE.getPlanEdge_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PLAN_EDGE__TARGET = eINSTANCE.getPlanEdge_Target();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PLAN_EDGE__GPMN_DIAGRAM = eINSTANCE.getPlanEdge_GpmnDiagram();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.SubProcessImpl <em>Sub Process</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.SubProcessImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSubProcess()
		 * @generated
		 */
		EClass SUB_PROCESS = eINSTANCE.getSubProcess();

		/**
		 * The meta object literal for the '<em><b>Processref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SUB_PROCESS__PROCESSREF = eINSTANCE.getSubProcess_Processref();

		/**
		 * The meta object literal for the '<em><b>Internal</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SUB_PROCESS__INTERNAL = eINSTANCE.getSubProcess_Internal();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUB_PROCESS__GPMN_DIAGRAM = eINSTANCE.getSubProcess_GpmnDiagram();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.impl.SuppressionEdgeImpl <em>Suppression Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.impl.SuppressionEdgeImpl
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getSuppressionEdge()
		 * @generated
		 */
		EClass SUPPRESSION_EDGE = eINSTANCE.getSuppressionEdge();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUPPRESSION_EDGE__SOURCE = eINSTANCE.getSuppressionEdge_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUPPRESSION_EDGE__TARGET = eINSTANCE.getSuppressionEdge_Target();

		/**
		 * The meta object literal for the '<em><b>Gpmn Diagram</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SUPPRESSION_EDGE__GPMN_DIAGRAM = eINSTANCE.getSuppressionEdge_GpmnDiagram();

		/**
		 * The meta object literal for the '{@link jadex.tools.gpmn.ConditionLanguage <em>Condition Language</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.ConditionLanguage
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getConditionLanguage()
		 * @generated
		 */
		EEnum CONDITION_LANGUAGE = eINSTANCE.getConditionLanguage();

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
		 * The meta object literal for the '{@link jadex.tools.gpmn.ModeType <em>Mode Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.ModeType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getModeType()
		 * @generated
		 */
		EEnum MODE_TYPE = eINSTANCE.getModeType();

		/**
		 * The meta object literal for the '<em>Condition Language Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.ConditionLanguage
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getConditionLanguageObject()
		 * @generated
		 */
		EDataType CONDITION_LANGUAGE_OBJECT = eINSTANCE.getConditionLanguageObject();

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

		/**
		 * The meta object literal for the '<em>Mode Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see jadex.tools.gpmn.ModeType
		 * @see jadex.tools.gpmn.impl.GpmnPackageImpl#getModeTypeObject()
		 * @generated
		 */
		EDataType MODE_TYPE_OBJECT = eINSTANCE.getModeTypeObject();

	}

} //GpmnPackage
