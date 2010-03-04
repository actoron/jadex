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
 * A representation of the model object '<em><b>Context Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.ContextElement#getContext <em>Context</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ContextElement#isDynamic <em>Dynamic</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ContextElement#getInitialValue <em>Initial Value</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ContextElement#getName <em>Name</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ContextElement#isSet <em>Set</em>}</li>
 *   <li>{@link jadex.tools.gpmn.ContextElement#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see jadex.tools.gpmn.GpmnPackage#getContextElement()
 * @model extendedMetaData="name='ContextElement' kind='elementOnly'"
 * @generated
 */
public interface ContextElement extends Identifiable
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * Returns the value of the '<em><b>Context</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link jadex.tools.gpmn.Context#getElements <em>Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Context</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Context</em>' container reference.
	 * @see #setContext(Context)
	 * @see jadex.tools.gpmn.GpmnPackage#getContextElement_Context()
	 * @see jadex.tools.gpmn.Context#getElements
	 * @model opposite="elements" resolveProxies="false"
	 *        extendedMetaData="kind='attribute' name='context'"
	 * @generated
	 */
	Context getContext();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ContextElement#getContext <em>Context</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Context</em>' container reference.
	 * @see #getContext()
	 * @generated
	 */
	void setContext(Context value);

	/**
	 * Returns the value of the '<em><b>Dynamic</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dynamic</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dynamic</em>' attribute.
	 * @see #isSetDynamic()
	 * @see #unsetDynamic()
	 * @see #setDynamic(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getContextElement_Dynamic()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='dynamic'"
	 * @generated
	 */
	boolean isDynamic();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ContextElement#isDynamic <em>Dynamic</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dynamic</em>' attribute.
	 * @see #isSetDynamic()
	 * @see #unsetDynamic()
	 * @see #isDynamic()
	 * @generated
	 */
	void setDynamic(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.ContextElement#isDynamic <em>Dynamic</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDynamic()
	 * @see #isDynamic()
	 * @see #setDynamic(boolean)
	 * @generated
	 */
	void unsetDynamic();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.ContextElement#isDynamic <em>Dynamic</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Dynamic</em>' attribute is set.
	 * @see #unsetDynamic()
	 * @see #isDynamic()
	 * @see #setDynamic(boolean)
	 * @generated
	 */
	boolean isSetDynamic();

	/**
	 * Returns the value of the '<em><b>Initial Value</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Initial Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Initial Value</em>' attribute.
	 * @see #setInitialValue(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getContextElement_InitialValue()
	 * @model default="" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='initialValue'"
	 * @generated
	 */
	String getInitialValue();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ContextElement#getInitialValue <em>Initial Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Initial Value</em>' attribute.
	 * @see #getInitialValue()
	 * @generated
	 */
	void setInitialValue(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getContextElement_Name()
	 * @model default="" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ContextElement#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Set</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Set</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Set</em>' attribute.
	 * @see #isSetSet()
	 * @see #unsetSet()
	 * @see #setSet(boolean)
	 * @see jadex.tools.gpmn.GpmnPackage#getContextElement_Set()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='set'"
	 * @generated
	 */
	boolean isSet();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ContextElement#isSet <em>Set</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Set</em>' attribute.
	 * @see #isSetSet()
	 * @see #unsetSet()
	 * @see #isSet()
	 * @generated
	 */
	void setSet(boolean value);

	/**
	 * Unsets the value of the '{@link jadex.tools.gpmn.ContextElement#isSet <em>Set</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSet()
	 * @see #isSet()
	 * @see #setSet(boolean)
	 * @generated
	 */
	void unsetSet();

	/**
	 * Returns whether the value of the '{@link jadex.tools.gpmn.ContextElement#isSet <em>Set</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Set</em>' attribute is set.
	 * @see #unsetSet()
	 * @see #isSet()
	 * @see #setSet(boolean)
	 * @generated
	 */
	boolean isSetSet();

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see jadex.tools.gpmn.GpmnPackage#getContextElement_Type()
	 * @model default="" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='type'"
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link jadex.tools.gpmn.ContextElement#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

} // ContextElement
