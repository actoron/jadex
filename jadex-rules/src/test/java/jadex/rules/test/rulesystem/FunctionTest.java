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
import jadex.rules.rulesystem.rules.ValueSourceReturnValueConstraint;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.Div;
import jadex.rules.rulesystem.rules.functions.Length;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.rulesystem.rules.functions.Sum;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test if functions can be used in the rete.
 */
public class FunctionTest extends TestCase
{	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args)
	{
		//testSumFunction();
		//testLengthFunction();
		//testNestedFunction();
		testBetaFunction();
	}
	
	/**
	 *  Test the sum function. 
	 */
	public static void testSumFunction()
	{

//		(defrule test_rule
//		  ?z <- (Numberbox (numbers $?numbers) (solution ?s&:(Sum($?numbers)))
//		=> (printout "match"))
		
		final List triggers = new ArrayList();
		
		FunctionCall fc = new FunctionCall(new Sum(), new Object[]{new Variable("$?n", OAVJavaType.java_integer_type, true, false)});
		ObjectCondition xc = new ObjectCondition(Numberbox.numberbox_type);
		xc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, new Variable("$?n", OAVJavaType.java_integer_type, true, false)));
		xc.addConstraint(new ValueSourceReturnValueConstraint(Numberbox.numberbox_has_solution, fc, IOperator.LESS));
				
		AndCondition cond = new AndCondition(new ICondition[]{xc});
//		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object res = assigments.getVariableValue("$?n");
//				System.out.println("TRIGGERED: x="+res);
				triggers.add(res);
			}
		};
		
		Rule rule = new Rule("sum", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(Numberbox.numberbox_type_model), rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
		//RetePanel.createReteFrame("Numberbox Test", mat.getReteNode());
		IOAVState state = system.getState();
		
		Object b1 = state.createRootObject(Numberbox.numberbox_type);
		state.setAttributeValue(b1, Numberbox.numberbox_has_solution, Integer.valueOf(30));
		
		int sum = 0;
		for(int i=0; i<10; i++)
		{
			sum += i;
//			System.out.println("Adding: "+i+" sum is: "+sum);
			state.addAttributeValue(b1, Numberbox.numberbox_has_numbers, Integer.valueOf(i));
			system.fireAllRules();
		}
		
		assertEquals(2, triggers.size());
	}
	
	
	/**
	 *  Test the length function. 
	 */
	public static void testLengthFunction()
	{

//		(defrule test_rule
//		  ?z <- (Numberbox (numbers $?numbers) (solution ?s&:(Length($?numbers)))
//		=> (printout "match"))
	

		final List triggers = new ArrayList();
		
		FunctionCall fc = new FunctionCall(new Length(), new Object[]{new Variable("$?n", OAVJavaType.java_integer_type, true, false)});
		ObjectCondition xc = new ObjectCondition(Numberbox.numberbox_type);
		xc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, new Variable("$?n", OAVJavaType.java_integer_type, true, false)));
		xc.addConstraint(new ValueSourceReturnValueConstraint(Numberbox.numberbox_has_solution, fc));
			
		AndCondition cond = new AndCondition(new ICondition[]{xc});
//		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object res = assigments.getVariableValue("$?n");
//				System.out.println("TRIGGERED: x="+res);
				triggers.add(res);
			}
		};
		
		Rule rule = new Rule("length", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(Numberbox.numberbox_type_model), 
			rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
		//RetePanel.createReteFrame("Numberbox Test", mat.getReteNode());
		IOAVState state = system.getState();
		
		Object b1 = state.createRootObject(Numberbox.numberbox_type);
		state.setAttributeValue(b1, Numberbox.numberbox_has_solution, Integer.valueOf(5));
		
		for(int i=0; i<10; i++)
		{
			state.addAttributeValue(b1, Numberbox.numberbox_has_numbers, Integer.valueOf(i));
//			System.out.println("Length is: "+(i+1));
			system.fireAllRules();
		}
		
		assertTrue(triggers.size()==1);
	}
	
	/**
	 *  Test calling functions in functions. 
	 */
	public static void testNestedFunction()
	{

//		(defrule test_rule
//		  ?z <- (Numberbox (numbers $?numbers) (solution ?s&:(Length($?numbers)))
//		=> (printout "match"))
	
		final List triggers = new ArrayList();
		
		FunctionCall fc_sum = new FunctionCall(new Sum(), new Object[]{new Variable("$?n", OAVJavaType.java_integer_type, true, false)});
		FunctionCall fc_length = new FunctionCall(new Length(), new Object[]{new Variable("$?n", OAVJavaType.java_integer_type, true, false)});
		FunctionCall fc = new FunctionCall(new Div(), new Object[]{fc_sum, fc_length});
		
		ObjectCondition xc = new ObjectCondition(Numberbox.numberbox_type);
		xc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, new Variable("$?n", OAVJavaType.java_integer_type, true, false)));
		xc.addConstraint(new ValueSourceReturnValueConstraint(Numberbox.numberbox_has_solution, fc));
			
		AndCondition cond = new AndCondition(new ICondition[]{xc});
//		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object res = assigments.getVariableValue("$?n");
//				System.out.println("TRIGGERED: x="+res);
				triggers.add(res);
			}
		};
		
		Rule rule = new Rule("nested", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(Numberbox.numberbox_type_model), rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
		//RetePanel.createReteFrame("Numberbox Test", mat.getReteNode());
		IOAVState state = system.getState();
		
		Object b1 = state.createRootObject(Numberbox.numberbox_type);
		state.setAttributeValue(b1, Numberbox.numberbox_has_solution, Integer.valueOf(3));
		
		for(int i=0; i<10; i++)
		{
			state.addAttributeValue(b1, Numberbox.numberbox_has_numbers, Integer.valueOf(i));
//			System.out.println("i: "+i);
			system.fireAllRules();
		}
		
		assertTrue(triggers.size()==1);
	}
	
	/**
	 *  Test calling functions with variables. 
	 */
	public static void testBetaFunction()
	{

//		(defrule test_rule
//		  ?x <- (Numberbox (numbers $?n1) (solution ?s1))
//		  ?y <- (Numberbox (numbers $?n2) (solution ?s2) :equal(length($?n1), length($?n2)) )
//		=> (printout "match"))
	
		final List triggers = new ArrayList();
		
		FunctionCall fc_length1 = new FunctionCall(new Length(), new Object[]{new Variable("$?n1", OAVJavaType.java_integer_type, true, false)});
		FunctionCall fc_length2 = new FunctionCall(new Length(), new Object[]{new Variable("$?n2", OAVJavaType.java_integer_type, true, false)});
		FunctionCall fc = new FunctionCall(new OperatorFunction(IOperator.EQUAL), new Object[]{fc_length1, fc_length2});
		
		ObjectCondition xc = new ObjectCondition(Numberbox.numberbox_type);
		xc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, new Variable("$?n1", OAVJavaType.java_integer_type, true, false)));
		xc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_solution, new Variable("?s1", OAVJavaType.java_integer_type)));
		xc.addConstraint(new BoundConstraint(null, new Variable("?x", OAVJavaType.java_integer_type)));
			
		ObjectCondition yc = new ObjectCondition(Numberbox.numberbox_type);
		yc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, new Variable("$?n2", OAVJavaType.java_integer_type, true, false)));
		yc.addConstraint(new BoundConstraint(Numberbox.numberbox_has_solution, new Variable("?s2", OAVJavaType.java_integer_type)));
		yc.addConstraint(new PredicateConstraint(fc));
		yc.addConstraint(new BoundConstraint(null, new Variable("?y", OAVJavaType.java_integer_type)));
			
		AndCondition cond = new AndCondition(new ICondition[]{xc, yc});
//		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
//				System.out.println("TRIGGERED: x="+assigments.getVariableValue("$?n1")
//					+" "+assigments.getVariableValue("$?n2")+" "
//					+assigments.getVariableValue("?x")+" "+assigments.getVariableValue("?y"));
//				
				triggers.add(assigments.getVariableValue("$?n1"));
			}
		};
		
		Rule rule = new Rule("beta", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(Numberbox.numberbox_type_model), rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//		RetePanel.createReteFrame("Numberbox Test", system, new Object());
		IOAVState state = system.getState();
		
		Object n1 = state.createRootObject(Numberbox.numberbox_type);
		state.setAttributeValue(n1, Numberbox.numberbox_has_solution, Integer.valueOf(3));
		
		Object n2 = state.createRootObject(Numberbox.numberbox_type);
		state.setAttributeValue(n2, Numberbox.numberbox_has_solution, Integer.valueOf(3));
		
		system.fireAllRules();
		
		for(int i=0; i<10; i++)
		{
			//System.out.println("--------");
			state.addAttributeValue(n1, Numberbox.numberbox_has_numbers, Integer.valueOf(i));
			//System.out.println("Numberbox 1: "+i);
			//System.out.println(mat.getReteMemory());
			system.fireAllRules();
		}
		
		for(int i=0; i<10; i++)
		{
			//System.out.println("--------");
			state.addAttributeValue(n2, Numberbox.numberbox_has_numbers, Integer.valueOf(i));
			//System.out.println("Numberbox 2: "+i);
			//System.out.println(mat.getReteMemory());
			system.fireAllRules();
		}
		
		//System.out.println(mat.getReteMemory());
		
		assertEquals(26, triggers.size());
	}
}
