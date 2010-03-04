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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Edge Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see jadex.tools.gpmn.GpmnPackage#getEdgeType()
 * @model extendedMetaData="name='EdgeType'"
 * @generated
 */
public enum EdgeType implements Enumerator
{
	/**
	 * The '<em><b>Sub Goal Edge</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SUB_GOAL_EDGE_VALUE
	 * @generated
	 * @ordered
	 */
	SUB_GOAL_EDGE(0, "SubGoalEdge", "SubGoalEdge"),

	/**
	 * The '<em><b>Plan Edge</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PLAN_EDGE_VALUE
	 * @generated
	 * @ordered
	 */
	PLAN_EDGE(1, "PlanEdge", "PlanEdge"),

	/**
	 * The '<em><b>Message Edge</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MESSAGE_EDGE_VALUE
	 * @generated
	 * @ordered
	 */
	MESSAGE_EDGE(2, "MessageEdge", "MessageEdge");

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The '<em><b>Sub Goal Edge</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Sub Goal Edge</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SUB_GOAL_EDGE
	 * @model name="SubGoalEdge"
	 * @generated
	 * @ordered
	 */
	public static final int SUB_GOAL_EDGE_VALUE = 0;

	/**
	 * The '<em><b>Plan Edge</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Plan Edge</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PLAN_EDGE
	 * @model name="PlanEdge"
	 * @generated
	 * @ordered
	 */
	public static final int PLAN_EDGE_VALUE = 1;

	/**
	 * The '<em><b>Message Edge</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Message Edge</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MESSAGE_EDGE
	 * @model name="MessageEdge"
	 * @generated
	 * @ordered
	 */
	public static final int MESSAGE_EDGE_VALUE = 2;

	/**
	 * An array of all the '<em><b>Edge Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final EdgeType[] VALUES_ARRAY = new EdgeType[]
		{
			SUB_GOAL_EDGE,
			PLAN_EDGE,
			MESSAGE_EDGE,
		};

	/**
	 * A public read-only list of all the '<em><b>Edge Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<EdgeType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Edge Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EdgeType get(String literal)
	{
		for (int i = 0; i < VALUES_ARRAY.length; ++i)
		{
			EdgeType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal))
			{
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Edge Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EdgeType getByName(String name)
	{
		for (int i = 0; i < VALUES_ARRAY.length; ++i)
		{
			EdgeType result = VALUES_ARRAY[i];
			if (result.getName().equals(name))
			{
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Edge Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EdgeType get(int value)
	{
		switch (value)
		{
			case SUB_GOAL_EDGE_VALUE: return SUB_GOAL_EDGE;
			case PLAN_EDGE_VALUE: return PLAN_EDGE;
			case MESSAGE_EDGE_VALUE: return MESSAGE_EDGE;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EdgeType(int value, String name, String literal)
	{
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue()
	{
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName()
	{
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral()
	{
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		return literal;
	}

} //EdgeType
