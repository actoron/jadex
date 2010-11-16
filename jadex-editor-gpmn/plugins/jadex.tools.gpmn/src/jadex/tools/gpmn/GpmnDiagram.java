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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getPackage <em>Package</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getImports <em>Imports</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getContext <em>Context</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getGoals <em>Goals</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getPlans <em>Plans</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getSubProcesses <em>Sub Processes</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getActivationEdges <em>Activation Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getPlanEdges <em>Plan Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getSuppressionEdges <em>Suppression Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getAuthor <em>Author</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getRevision <em>Revision</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getTitle <em>Title</em>}</li>
 *   <li>{@link jadex.tools.gpmn.GpmnDiagram#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram()
 * @model extendedMetaData="name='GpmnDiagram' kind='elementOnly'"
 * @generated
 */
public interface GpmnDiagram extends NamedObject
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Author</em>' attribute.
	 * @see #isSetAuthor()
	 * @see #unsetAuthor()
	 * @see #setAuthor(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Author()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='author'"
	 * @generated
	 */
	String getAuthor();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getAuthor <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' attribute.
	 * @see #isSetAuthor()
	 * @see #unsetAuthor()
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getAuthor <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAuthor()
	 * @see #getAuthor()
	 * @see #setAuthor(String)
	 * @generated
	 */
	void unsetAuthor();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getAuthor <em>Author</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Author</em>' attribute is set.
	 * @see #unsetAuthor()
	 * @see #getAuthor()
	 * @see #setAuthor(String)
	 * @generated
	 */
	boolean isSetAuthor();

	/**
	 * Returns the value of the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Revision</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Revision</em>' attribute.
	 * @see #isSetRevision()
	 * @see #unsetRevision()
	 * @see #setRevision(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Revision()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='revision'"
	 * @generated
	 */
	String getRevision();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getRevision <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Revision</em>' attribute.
	 * @see #isSetRevision()
	 * @see #unsetRevision()
	 * @see #getRevision()
	 * @generated
	 */
	void setRevision(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getRevision <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRevision()
	 * @see #getRevision()
	 * @see #setRevision(String)
	 * @generated
	 */
	void unsetRevision();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getRevision <em>Revision</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Revision</em>' attribute is set.
	 * @see #unsetRevision()
	 * @see #getRevision()
	 * @see #setRevision(String)
	 * @generated
	 */
	boolean isSetRevision();

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #isSetTitle()
	 * @see #unsetTitle()
	 * @see #setTitle(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Title()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='title'"
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #isSetTitle()
	 * @see #unsetTitle()
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTitle()
	 * @see #getTitle()
	 * @see #setTitle(String)
	 * @generated
	 */
	void unsetTitle();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getTitle <em>Title</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Title</em>' attribute is set.
	 * @see #unsetTitle()
	 * @see #getTitle()
	 * @see #setTitle(String)
	 * @generated
	 */
	boolean isSetTitle();

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #setVersion(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Version()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='version'"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	void unsetVersion();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getVersion <em>Version</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Version</em>' attribute is set.
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	boolean isSetVersion();

	/**
	 * Returns the value of the '<em><b>Context</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Context#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Context</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Context</em>' containment reference.
	 * @see #setContext(Context)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Context()
	 * @see jadex.tools.gpmn.Context#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" required="true"
	 *        extendedMetaData="kind='element' name='context'"
	 * @generated
	 */
	Context getContext();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getContext <em>Context</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Context</em>' containment reference.
	 * @see #getContext()
	 * @generated
	 */
	void setContext(Context value);

	/**
	 * Returns the value of the '<em><b>Goals</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.Goal}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Goal#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Goals</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Goals</em>' containment reference list.
	 * @see #isSetGoals()
	 * @see #unsetGoals()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Goals()
	 * @see jadex.tools.gpmn.Goal#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='goal'"
	 * @generated
	 */
	EList<Goal> getGoals();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getGoals <em>Goals</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetGoals()
	 * @see #getGoals()
	 * @generated
	 */
	void unsetGoals();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getGoals <em>Goals</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Goals</em>' containment reference list is set.
	 * @see #unsetGoals()
	 * @see #getGoals()
	 * @generated
	 */
	boolean isSetGoals();

	/**
	 * Returns the value of the '<em><b>Plans</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.AbstractPlan}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.AbstractPlan#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plans</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Plans</em>' containment reference list.
	 * @see #isSetPlans()
	 * @see #unsetPlans()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Plans()
	 * @see jadex.tools.gpmn.AbstractPlan#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='plan'"
	 * @generated
	 */
	EList<AbstractPlan> getPlans();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getPlans <em>Plans</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPlans()
	 * @see #getPlans()
	 * @generated
	 */
	void unsetPlans();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getPlans <em>Plans</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Plans</em>' containment reference list is set.
	 * @see #unsetPlans()
	 * @see #getPlans()
	 * @generated
	 */
	boolean isSetPlans();

	/**
	 * Returns the value of the '<em><b>Sub Processes</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.SubProcess}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.SubProcess#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Processes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Processes</em>' containment reference list.
	 * @see #isSetSubProcesses()
	 * @see #unsetSubProcesses()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_SubProcesses()
	 * @see jadex.tools.gpmn.SubProcess#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='subProcess'"
	 * @generated
	 */
	EList<SubProcess> getSubProcesses();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getSubProcesses <em>Sub Processes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSubProcesses()
	 * @see #getSubProcesses()
	 * @generated
	 */
	void unsetSubProcesses();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getSubProcesses <em>Sub Processes</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Sub Processes</em>' containment reference list is set.
	 * @see #unsetSubProcesses()
	 * @see #getSubProcesses()
	 * @generated
	 */
	boolean isSetSubProcesses();

	/**
	 * Returns the value of the '<em><b>Activation Edges</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.ActivationEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ActivationEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Activation Edges</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Activation Edges</em>' containment reference list.
	 * @see #isSetActivationEdges()
	 * @see #unsetActivationEdges()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_ActivationEdges()
	 * @see jadex.tools.gpmn.ActivationEdge#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='activationEdge'"
	 * @generated
	 */
	EList<ActivationEdge> getActivationEdges();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getActivationEdges <em>Activation Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetActivationEdges()
	 * @see #getActivationEdges()
	 * @generated
	 */
	void unsetActivationEdges();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getActivationEdges <em>Activation Edges</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Activation Edges</em>' containment reference list is set.
	 * @see #unsetActivationEdges()
	 * @see #getActivationEdges()
	 * @generated
	 */
	boolean isSetActivationEdges();

	/**
	 * Returns the value of the '<em><b>Plan Edges</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.PlanEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.PlanEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plan Edges</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Plan Edges</em>' containment reference list.
	 * @see #isSetPlanEdges()
	 * @see #unsetPlanEdges()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_PlanEdges()
	 * @see jadex.tools.gpmn.PlanEdge#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='planEdge'"
	 * @generated
	 */
	EList<PlanEdge> getPlanEdges();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getPlanEdges <em>Plan Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPlanEdges()
	 * @see #getPlanEdges()
	 * @generated
	 */
	void unsetPlanEdges();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getPlanEdges <em>Plan Edges</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Plan Edges</em>' containment reference list is set.
	 * @see #unsetPlanEdges()
	 * @see #getPlanEdges()
	 * @generated
	 */
	boolean isSetPlanEdges();

	/**
	 * Returns the value of the '<em><b>Suppression Edges</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.SuppressionEdge}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.SuppressionEdge#getGpmnDiagram <em>Gpmn Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Suppression Edges</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Suppression Edges</em>' containment reference list.
	 * @see #isSetSuppressionEdges()
	 * @see #unsetSuppressionEdges()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_SuppressionEdges()
	 * @see jadex.tools.gpmn.SuppressionEdge#getGpmnDiagram
	 * @model opposite="gpmnDiagram" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='suppressionEdge'"
	 * @generated
	 */
	EList<SuppressionEdge> getSuppressionEdges();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getSuppressionEdges <em>Suppression Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSuppressionEdges()
	 * @see #getSuppressionEdges()
	 * @generated
	 */
	void unsetSuppressionEdges();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getSuppressionEdges <em>Suppression Edges</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Suppression Edges</em>' containment reference list is set.
	 * @see #unsetSuppressionEdges()
	 * @see #getSuppressionEdges()
	 * @generated
	 */
	boolean isSetSuppressionEdges();

	/**
	 * Returns the value of the '<em><b>Package</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Package</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package</em>' attribute.
	 * @see #setPackage(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Package()
	 * @model default="" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='package'"
	 * @generated
	 */
	String getPackage();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getPackage <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package</em>' attribute.
	 * @see #getPackage()
	 * @generated
	 */
	void setPackage(String value);

	/**
	 * Returns the value of the '<em><b>Imports</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Imports</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Imports</em>' attribute list.
	 * @see #isSetImports()
	 * @see #unsetImports()
	 * @see jadex.tools.gpmn.GpmnPackage#getGpmnDiagram_Imports()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='import'"
	 * @generated
	 */
	EList<String> getImports();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getImports <em>Imports</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetImports()
	 * @see #getImports()
	 * @generated
	 */
	void unsetImports();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.GpmnDiagram#getImports <em>Imports</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Imports</em>' attribute list is set.
	 * @see #unsetImports()
	 * @see #getImports()
	 * @generated
	 */
	boolean isSetImports();

} // GpmnDiagram
