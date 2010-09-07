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
 * A representation of the model object '<em><b>Activation Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.ActivationEdge#getSource <em>Source</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ActivationEdge#getTarget <em>Target</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ActivationEdge#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ActivationEdge#getOrder <em>Order</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getActivationEdge()
 * @model extendedMetaData="name='ActivationEdge' kind='elementOnly'"
 * @generated
 */
public interface ActivationEdge extends AbstractEdge
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ActivationPlan#getActivationEdges <em>Activation Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(ActivationPlan)
	 * @see jadex.tools.gpmn.GpmnPackage#getActivationEdge_Source()
	 * @see jadex.tools.gpmn.ActivationPlan#getActivationEdges
	 * @model opposite="activationEdges" resolveProxies="false"
	 *        extendedMetaData="kind='attribute' name='source'"
	 * @generated
	 */
	ActivationPlan getSource();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ActivationEdge#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(ActivationPlan value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Activatable#getActivationEdges <em>Activation Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(Activatable)
	 * @see jadex.tools.gpmn.GpmnPackage#getActivationEdge_Target()
	 * @see jadex.tools.gpmn.Activatable#getActivationEdges
	 * @model opposite="activationEdges" resolveProxies="false"
	 *        extendedMetaData="kind='attribute' name='target'"
	 * @generated
	 */
	Activatable getTarget();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ActivationEdge#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Activatable value);

	/**
	 * Returns the value of the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.GpmnDiagram#getActivationEdges <em>Activation Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gpmn Diagram</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #setGpmnDiagram(GpmnDiagram)
	 * @see jadex.tools.gpmn.GpmnPackage#getActivationEdge_GpmnDiagram()
	 * @see jadex.tools.gpmn.GpmnDiagram#getActivationEdges
	 * @model opposite="activationEdges"
	 * @generated
	 */
	GpmnDiagram getGpmnDiagram();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ActivationEdge#getGpmnDiagram <em>Gpmn Diagram</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	void setGpmnDiagram(GpmnDiagram value);

	/**
	 * Returns the value of the '<em><b>Order</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Order</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Order</em>' attribute.
	 * @see #isSetOrder()
	 * @see #unsetOrder()
	 * @see #setOrder(int)
	 * @see jadex.tools.gpmn.GpmnPackage#getActivationEdge_Order()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='order'"
	 * @generated
	 */
	int getOrder();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ActivationEdge#getOrder <em>Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Order</em>' attribute.
	 * @see #isSetOrder()
	 * @see #unsetOrder()
	 * @see #getOrder()
	 * @generated
	 */
	void setOrder(int value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.ActivationEdge#getOrder <em>Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetOrder()
	 * @see #getOrder()
	 * @see #setOrder(int)
	 * @generated
	 */
	void unsetOrder();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.ActivationEdge#getOrder <em>Order</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Order</em>' attribute is set.
	 * @see #unsetOrder()
	 * @see #getOrder()
	 * @see #setOrder(int)
	 * @generated
	 */
	boolean isSetOrder();

} // ActivationEdge
