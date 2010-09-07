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
import org.eclipse.emf.ecore.EModelElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.Context#getElements <em>Elements</em>}</li>
 *   <li>{@link jadex.tools.gpmn.Context#getGpmnDiagram <em>Gpmn Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getContext()
 * @model extendedMetaData="name='Context' kind='elementOnly'"
 * @generated
 */
public interface Context extends EModelElement
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
	 * The list contents are of type {@link jadex.tools.gpmn.ContextElement}.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.ContextElement#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Elements</em>' containment reference list.
	 * @see #isSetElements()
	 * @see #unsetElements()
	 * @see jadex.tools.gpmn.GpmnPackage#getContext_Elements()
	 * @see jadex.tools.gpmn.ContextElement#getContext
	 * @model opposite="context" containment="true" unsettable="true"
	 *        extendedMetaData="kind='element' name='element'"
	 * @generated
	 */
	EList<ContextElement> getElements();

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.Context#getElements <em>Elements</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetElements()
	 * @see #getElements()
	 * @generated
	 */
	void unsetElements();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.Context#getElements <em>Elements</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Elements</em>' containment reference list is set.
	 * @see #unsetElements()
	 * @see #getElements()
	 * @generated
	 */
	boolean isSetElements();

	/**
	 * Returns the value of the '<em><b>Gpmn Diagram</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.GpmnDiagram#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gpmn Diagram</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #setGpmnDiagram(GpmnDiagram)
	 * @see jadex.tools.gpmn.GpmnPackage#getContext_GpmnDiagram()
	 * @see jadex.tools.gpmn.GpmnDiagram#getContext
	 * @model opposite="context" required="true" transient="false"
	 * @generated
	 */
	GpmnDiagram getGpmnDiagram();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.Context#getGpmnDiagram <em>Gpmn Diagram</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gpmn Diagram</em>' container reference.
	 * @see #getGpmnDiagram()
	 * @generated
	 */
	void setGpmnDiagram(GpmnDiagram value);

} // Context
