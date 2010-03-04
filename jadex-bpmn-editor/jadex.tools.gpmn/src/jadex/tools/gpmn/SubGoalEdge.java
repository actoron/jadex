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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Goal Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.SubGoalEdge#getSequentialOrder <em>Sequential Order</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getSubGoalEdge()
 * @model extendedMetaData="name='SubGoalEdge' kind='elementOnly'"
 * @generated
 */
public interface SubGoalEdge extends ParameterizedEdge, NamedObject
{

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Sequential Order</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sequential Order</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sequential Order</em>' attribute.
	 * @see #isSetSequentialOrder()
	 * @see #unsetSequentialOrder()
	 * @see #setSequentialOrder(int)
	 * @see jadex.tools.gpmn.GpmnPackage#getSubGoalEdge_SequentialOrder()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='sequentialOrder'"
	 * @generated
	 */
	int getSequentialOrder();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.SubGoalEdge#getSequentialOrder <em>Sequential Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sequential Order</em>' attribute.
	 * @see #isSetSequentialOrder()
	 * @see #unsetSequentialOrder()
	 * @see #getSequentialOrder()
	 * @generated
	 */
	void setSequentialOrder(int value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.SubGoalEdge#getSequentialOrder <em>Sequential Order</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSequentialOrder()
	 * @see #getSequentialOrder()
	 * @see #setSequentialOrder(int)
	 * @generated
	 */
	void unsetSequentialOrder();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.SubGoalEdge#getSequentialOrder <em>Sequential Order</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Sequential Order</em>' attribute is set.
	 * @see #unsetSequentialOrder()
	 * @see #getSequentialOrder()
	 * @see #setSequentialOrder(int)
	 * @generated
	 */
	boolean isSetSequentialOrder();
} // SubGoalEdge
