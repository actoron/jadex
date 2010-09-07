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
 * A representation of the model object '<em><b>Bpmn Plan</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.BpmnPlan#getPlanref <em>Planref</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getBpmnPlan()
 * @model extendedMetaData="name='BpmnPlan' kind='elementOnly'"
 * @generated
 */
public interface BpmnPlan extends AbstractPlan
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Planref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Planref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Planref</em>' attribute.
	 * @see #isSetPlanref()
	 * @see #unsetPlanref()
	 * @see #setPlanref(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getBpmnPlan_Planref()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 *        extendedMetaData="kind='attribute' name='planref'"
	 * @generated
	 */
	String getPlanref();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.BpmnPlan#getPlanref <em>Planref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Planref</em>' attribute.
	 * @see #isSetPlanref()
	 * @see #unsetPlanref()
	 * @see #getPlanref()
	 * @generated
	 */
	void setPlanref(String value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.BpmnPlan#getPlanref <em>Planref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPlanref()
	 * @see #getPlanref()
	 * @see #setPlanref(String)
	 * @generated
	 */
	void unsetPlanref();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.BpmnPlan#getPlanref <em>Planref</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Planref</em>' attribute is set.
	 * @see #unsetPlanref()
	 * @see #getPlanref()
	 * @see #setPlanref(String)
	 * @generated
	 */
	boolean isSetPlanref();

} // BpmnPlan
