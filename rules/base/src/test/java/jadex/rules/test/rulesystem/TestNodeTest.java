 package jadex.rules.test.rulesystem;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.TestCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test if the test node works.
 */
public class TestNodeTest extends TestCase
{	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args)
	{
		testSingleCondition();
		testMultiCondition();
	}
	
	/**
	 *  Test a single function evaluation. 
	 */
	public static void testSingleCondition()
	{
		final List triggers = new ArrayList();
		
		// test 1==1 -> true
		List values = new ArrayList();
		values.add(Integer.valueOf(1));
		values.add(Integer.valueOf(1));
		FunctionCall fc = new FunctionCall(new OperatorFunction(IOperator.EQUAL), values);
		final TestCondition cond = new TestCondition(new PredicateConstraint(fc));
				
//		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
//				System.out.println("Triggered: "+cond);
				triggers.add("Triggered: "+cond);
			}
		};
		
		Rule rule = new Rule("1==1", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(OAVJavaType.java_type_model),
			rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//		RetePatternMatcherState rs = ((RetePatternMatcherState)system.getMatcherState());
//		Object mon = new Object();
//		RetePanel.createReteFrame("1==1 Test", rs.getReteNode(), rs.getReteMemory(), mon);
		
		system.fireAllRules();
		
		assertTrue(triggers.size()==1);
	}
	
	/**
	 *  Test the sum function. 
	 */
	public static void testMultiCondition()
	{
		final List triggers = new ArrayList();
		
		// test ?num==1 -> true
		ObjectCondition ocond = new ObjectCondition(Numberbox.numberbox_type);
		ocond.addConstraint(new BoundConstraint(Numberbox.numberbox_has_solution, 
			new Variable("?num", OAVJavaType.java_integer_type)));
		
		List values = new ArrayList();
		values.add(new Variable("?num", OAVJavaType.java_integer_type));
		values.add(Integer.valueOf(1));
		FunctionCall fc = new FunctionCall(new OperatorFunction(IOperator.EQUAL), values);
		TestCondition tcond = new TestCondition(new PredicateConstraint(fc));
		
		final AndCondition cond = new AndCondition(new ICondition[]{ocond, tcond});		
//		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
//				System.out.println("Triggered: "+cond);
				triggers.add("Triggered: "+cond);
			}
		};
		
		Rule rule = new Rule("1==1", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(Numberbox.numberbox_type_model),
			rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//		RetePatternMatcherState rs = ((RetePatternMatcherState)system.getMatcherState());
//		Object mon = new Object();
//		RetePanel.createReteFrame("?num==1 Test", rs.getReteNode(), rs.getReteMemory(), mon);
		
		IOAVState state = system.getState();
		Object b1 = state.createRootObject(Numberbox.numberbox_type);
		state.setAttributeValue(b1, Numberbox.numberbox_has_solution, Integer.valueOf(1));
		system.fireAllRules();
		
		assertTrue(triggers.size()==1);
	}

}
