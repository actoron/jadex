package jadex.rules.rulesystem.rules;

import java.util.List;

/**
 * Simple constraints:
 * (slot <op> <value>) -> LiteralConstraint
 * (slot <op> var) -> BoundConstraint
 * (slot <op> m/var1 .. m/varn) -> MultiBoundConstraint
 * (slot|var|<value> <op> f(var1, var2, ...)) -> ReturnValueConstraint
 * (true == p(var1, var2, ...)) -> PredicateConstraint
 * 
 * (slot subidx <op> <value>) -> MultiLiteralConstraint for multifields
 * (slot1 <op> slot2) -> AttributeConstraint
 * 
 * <op> : = | != | < | <= | > | >= | contains | excludes | matches
 * 
 * Complex constraints:
 * [const1 and const2 and ...] -> AndConstraint
 * [const1 or const2 or ...] -> OrConstraint
 * const1,2 : ComplexConstraint | SimpleConstraint 
 */
public interface IConstraint
{
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public abstract List getVariables();
}
