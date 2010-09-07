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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Plan Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.PlanEdge#getSource <em>Source</em>}</li>
 *   <li>{@link jadex.tools.gpmn.PlanEdge#getTarget <em>Target</em>}</li>
 *   <li>{@link jadex.tools.gpmn.PlanEdge#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getPlanEdge()
 * @model extendedMetaData="name='PlanEdge' kind='elementOnly'"
 * @generated
 */
public interface PlanEdge extends AbstractEdge
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(Goal)
	 * @see jadex.tools.gpmn.GpmnPackage#getPlanEdge_Source()
	 * @model resolveProxies="false"
	 *        extendedMetaData="kind='attribute' name='source'"
	 * @generated
	 */
	Goal getSource();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.PlanEdge#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Goal value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.AbstractPlan#getPlanEdges <em>Plan Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(AbstractPlan)
	 * @see jadex.tools.gpmn.GpmnPackage#getPlanEdge_Target()
	 * @see jadex.tools.gpmn.AbstractPlan#getPlanEdges
	 * @model opposite="planEdges" resolveProxies="false"
	 *        extendedMetaData="kind='attribute' name='target'"
	 * @generated
	 */
	AbstractPlan getTarget();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.PlanEdge#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(AbstractPlan value);

	/**
	 * Returns the value of the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.GpmnDiagram#getPlanEdges <em>Plan Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gpmn Diagram</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #setGpmnDiagram(GpmnDiagram)
	 * @see jadex.tools.gpmn.GpmnPackage#getPlanEdge_GpmnDiagram()
	 * @see jadex.tools.gpmn.GpmnDiagram#getPlanEdges
	 * @model opposite="planEdges"
	 * @generated
	 */
	GpmnDiagram getGpmnDiagram();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.PlanEdge#getGpmnDiagram <em>Gpmn Diagram</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	void setGpmnDiagram(GpmnDiagram value);

} // PlanEdge
