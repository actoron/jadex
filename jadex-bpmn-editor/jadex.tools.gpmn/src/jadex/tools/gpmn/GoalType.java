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
 * A representation of the literals of the enumeration '<em><b>Goal Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see jadex.tools.gpmn.GpmnPackage#getGoalType()
 * @model extendedMetaData="name='GoalType'"
 * @generated
 */
public enum GoalType implements Enumerator
{
	/**
	 * The '<em><b>Meta Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #META_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	META_GOAL(0, "MetaGoal", "MetaGoal"),

	/**
	 * The '<em><b>Sub Process Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SUB_PROCESS_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	SUB_PROCESS_GOAL(1, "SubProcessGoal", "SubProcessGoal"),

	/**
	 * The '<em><b>Maintain Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MAINTAIN_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	MAINTAIN_GOAL(2, "MaintainGoal", "MaintainGoal"),

	/**
	 * The '<em><b>Achieve Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ACHIEVE_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	ACHIEVE_GOAL(3, "AchieveGoal", "AchieveGoal"),

	/**
	 * The '<em><b>Perform Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PERFORM_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	PERFORM_GOAL(4, "PerformGoal", "PerformGoal"),

	/**
	 * The '<em><b>Query Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #QUERY_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	QUERY_GOAL(5, "QueryGoal", "QueryGoal"),

	/**
	 * The '<em><b>Sequential Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SEQUENTIAL_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	SEQUENTIAL_GOAL(7, "SequentialGoal", "SequentialGoal"),

	/**
	 * The '<em><b>Parallel Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PARALLEL_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	PARALLEL_GOAL(8, "ParallelGoal", "ParallelGoal"),

	/**
	 * The '<em><b>Message Goal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MESSAGE_GOAL_VALUE
	 * @generated
	 * @ordered
	 */
	MESSAGE_GOAL(9, "MessageGoal", "MessageGoal");

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\nAll rights reserved. This program and the accompanying \nmaterials are made available under the terms of the \n###_LICENSE_REPLACEMENT_MARKER_###\nwhich accompanies this distribution, and is available at\n###_LICENSE_URL_REPLACEMENT_MARKER_###";

	/**
	 * The '<em><b>Meta Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Meta Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #META_GOAL
	 * @model name="MetaGoal"
	 * @generated
	 * @ordered
	 */
	public static final int META_GOAL_VALUE = 0;

	/**
	 * The '<em><b>Sub Process Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Sub Process Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SUB_PROCESS_GOAL
	 * @model name="SubProcessGoal"
	 * @generated
	 * @ordered
	 */
	public static final int SUB_PROCESS_GOAL_VALUE = 1;

	/**
	 * The '<em><b>Maintain Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Maintain Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MAINTAIN_GOAL
	 * @model name="MaintainGoal"
	 * @generated
	 * @ordered
	 */
	public static final int MAINTAIN_GOAL_VALUE = 2;

	/**
	 * The '<em><b>Achieve Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Achieve Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ACHIEVE_GOAL
	 * @model name="AchieveGoal"
	 * @generated
	 * @ordered
	 */
	public static final int ACHIEVE_GOAL_VALUE = 3;

	/**
	 * The '<em><b>Perform Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Perform Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PERFORM_GOAL
	 * @model name="PerformGoal"
	 * @generated
	 * @ordered
	 */
	public static final int PERFORM_GOAL_VALUE = 4;

	/**
	 * The '<em><b>Query Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Query Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #QUERY_GOAL
	 * @model name="QueryGoal"
	 * @generated
	 * @ordered
	 */
	public static final int QUERY_GOAL_VALUE = 5;

	/**
	 * The '<em><b>Sequential Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Sequential Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SEQUENTIAL_GOAL
	 * @model name="SequentialGoal"
	 * @generated
	 * @ordered
	 */
	public static final int SEQUENTIAL_GOAL_VALUE = 7;

	/**
	 * The '<em><b>Parallel Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Parallel Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PARALLEL_GOAL
	 * @model name="ParallelGoal"
	 * @generated
	 * @ordered
	 */
	public static final int PARALLEL_GOAL_VALUE = 8;

	/**
	 * The '<em><b>Message Goal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Message Goal</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MESSAGE_GOAL
	 * @model name="MessageGoal"
	 * @generated
	 * @ordered
	 */
	public static final int MESSAGE_GOAL_VALUE = 9;

	/**
	 * An array of all the '<em><b>Goal Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final GoalType[] VALUES_ARRAY = new GoalType[]
		{
			META_GOAL,
			SUB_PROCESS_GOAL,
			MAINTAIN_GOAL,
			ACHIEVE_GOAL,
			PERFORM_GOAL,
			QUERY_GOAL,
			SEQUENTIAL_GOAL,
			PARALLEL_GOAL,
			MESSAGE_GOAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Goal Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<GoalType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Goal Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GoalType get(String literal)
	{
		for (int i = 0; i < VALUES_ARRAY.length; ++i)
		{
			GoalType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal))
			{
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Goal Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GoalType getByName(String name)
	{
		for (int i = 0; i < VALUES_ARRAY.length; ++i)
		{
			GoalType result = VALUES_ARRAY[i];
			if (result.getName().equals(name))
			{
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Goal Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GoalType get(int value)
	{
		switch (value)
		{
			case META_GOAL_VALUE: return META_GOAL;
			case SUB_PROCESS_GOAL_VALUE: return SUB_PROCESS_GOAL;
			case MAINTAIN_GOAL_VALUE: return MAINTAIN_GOAL;
			case ACHIEVE_GOAL_VALUE: return ACHIEVE_GOAL;
			case PERFORM_GOAL_VALUE: return PERFORM_GOAL;
			case QUERY_GOAL_VALUE: return QUERY_GOAL;
			case SEQUENTIAL_GOAL_VALUE: return SEQUENTIAL_GOAL;
			case PARALLEL_GOAL_VALUE: return PARALLEL_GOAL;
			case MESSAGE_GOAL_VALUE: return MESSAGE_GOAL;
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
	private GoalType(int value, String name, String literal)
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

} //GoalType
