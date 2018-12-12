package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  A hard constraint for non-functional properties.
 */
public class MHardConstraint
{
	/** The value should remain constant. */
	public static final String CONSTANT = "Constant";
	
	/** The value should be greater than the given value. */
	public static final String GREATER = "Greater";
	
	/** The value should be less than the given value. */
	public static final String LESS = "Less";
	
	/** The value should be greater than or equal to the given value. */
	public static final String GREATER_OR_EQUAL = "Greater or equal";
	
	/** The value should be less than or equal to the given value. */
	public static final String LESS_OR_EQUAL = "Less or equal";
	
	/** Name of the property. */
	protected String propname;
	
	/** Expected value. */
	protected UnparsedExpression value;
	
	/** Operator used to evaluate the value. */
	protected String operator;
	
	/**
	 *  Creates a hard constraint for non-functional properties.
	 */
	public MHardConstraint()
	{
	}
	
	/**
	 *  Creates a hard constraint for non-functional properties.
	 */
	public MHardConstraint(String propname, String operand, String value)
	{
		this(propname, operand, new UnparsedExpression(propname, Object.class, value, null));
	}
	
	/**
	 *  Creates a hard constraint for non-functional properties.
	 */
	public MHardConstraint(String propname, String operator, UnparsedExpression value)
	{
		this.propname = propname;
		this.operator = operator;
		this.value = value;
	}

	/**
	 *  Gets the propname.
	 *
	 *  @return The propname.
	 */
	public String getPropertyName()
	{
		return propname;
	}

	/**
	 *  Sets the propname.
	 *
	 *  @param propname The propname to set.
	 */
	public void setPropname(String propname)
	{
		this.propname = propname;
	}

	/**
	 *  Gets the value.
	 *
	 *  @return The value.
	 */
	public UnparsedExpression getValue()
	{
		return value;
	}

	/**
	 *  Sets the value.
	 *
	 *  @param value The value to set.
	 */
	public void setValue(UnparsedExpression value)
	{
		this.value = value;
	}

	/**
	 *  Gets the operator.
	 *
	 *  @return The operator.
	 */
	public String getOperator()
	{
		return operator;
	}

	/**
	 *  Sets the operand.
	 *
	 *  @param operand The operand to set.
	 */
	public void setOperator(String operator)
	{
		this.operator = operator;
	}
	
	
}
