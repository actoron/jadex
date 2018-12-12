package jadex.rules.test.rulesystem;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
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
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test if multi field splits are correct.
 */
public class MultifieldSplitTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The list is filled by the action of the condition. */
	protected List	result;
		
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		state	= OAVStateFactory.createOAVState(Numberbox.numberbox_type_model);
		result	= new ArrayList();
	}
	
	//-------- test methods --------

	/**
	 *  Test all neighbors are found.
	 */
	public void testNeighborsTrigger()
	{
		// Find all pairs of neighboring numbers
		// Numberbox ($?c1, ?x, ?y, $?c2)
		ObjectCondition	cond = new ObjectCondition(Numberbox.numberbox_type);
		List pattern = new ArrayList();
		pattern.add(new Variable("$?c1", OAVJavaType.java_integer_type, true, false));
		pattern.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern.add(new Variable("?y", OAVJavaType.java_integer_type));
		pattern.add(new Variable("$?c2", OAVJavaType.java_integer_type, true, false));
		cond.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, pattern, IOperator.EQUAL));

		IRule	rule	= new Rule("neibors(x,y)", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object x = assigments.getVariableValue("?x");
				Object y = assigments.getVariableValue("?y");
				result.add("("+x+","+y+")");
			}
		});
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		// Create rule system.
		RuleSystem	system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//		RetePanel.createReteFrame("Split test", ((RetePatternMatcher)system.getMatcher()).getReteNode());
//		synchronized(system){system.wait();}
		
		// Add 5 numbers.
		Object nb = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(1));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(2));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(4));
		
		List test = new ArrayList();
		test.add("(1,3)");
		test.add("(3,2)");
		test.add("(2,5)");
		test.add("(5,4)");
		system.fireAllRules();
		assertEquals("The condition should trigger with: "+test, test, result);
		
//		System.out.println(result);
	}
	
	/**
	 *  Test all neighbors are found.
	 */
	public void testSameNeighborsTrigger()
	{
		// Find all pairs of neighboring same numbers 
		// Numberbox ($?c1 ?x ?x $?c2)
		ObjectCondition	cond = new ObjectCondition(Numberbox.numberbox_type);
		List pattern = new ArrayList();
		pattern.add(new Variable("$?c1", OAVJavaType.java_integer_type, true, false));
		pattern.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern.add(new Variable("$?c2", OAVJavaType.java_integer_type, true, false));
		cond.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, pattern, IOperator.EQUAL));

		IRule	rule	= new Rule("neibors(x,x)", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object x = assigments.getVariableValue("?x");
				//Object y = assigments.getVariableValue("?y");
				result.add("("+x+")");
			}
		});
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		// Create rule system.
		RuleSystem	system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//		RetePanel.createReteFrame("Split test", ((RetePatternMatcher)system.getMatcher()).getReteNode());
//		try{synchronized(system){system.wait();}} catch(Exception e){}
		
		// Add 5 numbers.
		Object nb = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(2));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		
		//System.out.println(((RetePatternMatcher)system.getMatcher()).getReteMemory());
		
		List test = new ArrayList();
		test.add("(3)");
		test.add("(5)");
		test.add("(5)");
		system.fireAllRules();
		assertEquals("The condition should trigger with: "+test, test, result);
		
//		System.out.println(result);
	}


	/**
	 *  Test all neighbors are found.
	 */
	public void testPredicateNeighborsTrigger()
	{
		// Find all pairs of neighboring numbers with the second greater than the first 
		// Numberbox ($?c1 ?x ?y $?c2) &:(?x<?y)
		ObjectCondition	cond = new ObjectCondition(Numberbox.numberbox_type);
		List pattern = new ArrayList();
		pattern.add(new Variable("$?c1", OAVJavaType.java_integer_type, true, false));
		pattern.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern.add(new Variable("?y", OAVJavaType.java_integer_type));
		pattern.add(new Variable("$?c2", OAVJavaType.java_integer_type, true, false));
		cond.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, pattern, IOperator.EQUAL));
		FunctionCall fc = new FunctionCall(new OperatorFunction(IOperator.LESS), 
			new Variable[]{
			new Variable("?x", OAVJavaType.java_integer_type), 
			new Variable("?y", OAVJavaType.java_integer_type)
		}); 
		cond.addConstraint(new PredicateConstraint(fc));
		
		IRule	rule	= new Rule("neibors(x,y) && x<y", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object x = assigments.getVariableValue("?x");
				Object y = assigments.getVariableValue("?y");
				result.add("("+x+","+y+")");
			}
		});
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		// Create rule system.
		RuleSystem	system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//		RetePanel.createReteFrame("Split test", ((RetePatternMatcher)system.getMatcher()).getReteNode());
//		synchronized(system){system.wait();}
		
		// Add 5 numbers.
		Object nb = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(1));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(2));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(4));
		
		List test = new ArrayList();
		test.add("(1,3)");
		//test.add("(3,2)");
		test.add("(2,5)");
		//test.add("(5,4)");
		system.fireAllRules();
		assertEquals("The condition should trigger with: "+test, test, result);
		
//		System.out.println(result);
	}
	
	/**
	 *  Test a multiple multi split.
	 */
	public void testMultiTrigger()
	{
		// Find all pairs of neighboring same numbers 
		// Numberbox (numbers $?c1 ?x ?y $?c2) (numbers2 $?c3 ?y ?x $?c4) 
		ObjectCondition	cond = new ObjectCondition(Numberbox.numberbox_type);
		List pattern = new ArrayList();
		pattern.add(new Variable("$?c1", OAVJavaType.java_integer_type, true, false));
		pattern.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern.add(new Variable("?y", OAVJavaType.java_integer_type));
		pattern.add(new Variable("$?c2", OAVJavaType.java_integer_type, true, false));
		cond.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, pattern, IOperator.EQUAL));
		
		List pattern2 = new ArrayList();
		pattern2.add(new Variable("$?c3", OAVJavaType.java_integer_type, true, false));
		pattern2.add(new Variable("?y", OAVJavaType.java_integer_type));
		pattern2.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern2.add(new Variable("$?c4", OAVJavaType.java_integer_type, true, false));
		cond.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers2, pattern2, IOperator.EQUAL));
		
		IRule	rule	= new Rule("neibors_numbers(x,y) && neibors_numbers2(y,x)", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object x = assigments.getVariableValue("?x");
				Object y = assigments.getVariableValue("?y");
				result.add("("+x+","+y+")");
			}
		});
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		// Create rule system.
		RuleSystem	system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();
				
//		RetePanel.createReteFrame("Split test", ((RetePatternMatcher)system.getMatcher()).getReteNode());
			
		// Add numbers.
		Object nb = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(1));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(2));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(4));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers2, Integer.valueOf(5));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers2, Integer.valueOf(4));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers2, Integer.valueOf(3));
		
//		System.out.println(((RetePatternMatcher)system.getMatcher()).getReteMemory());

//		try{synchronized(system){system.wait();}} catch(Exception e){}
		
		List test = new ArrayList();
		test.add("(3,4)");
		test.add("(4,5)");
		system.fireAllRules();
		assertEquals("The condition should trigger with: "+test, test, result);
		
//		System.out.println(result);
	}

	/**
	 *  Test a multiple multi split.
	 */
	public void testJoinTrigger()
	{
		// Find all pairs of neighboring same numbers 
		// Numberbox (numbers $?c1 ?x ?y $?c2) 
		// Numberbox (numbers $?c3 ?y ?x $?c4) 
		ObjectCondition	cond1 = new ObjectCondition(Numberbox.numberbox_type);
		List pattern = new ArrayList();
		pattern.add(new Variable("$?c1", OAVJavaType.java_integer_type, true, false));
		pattern.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern.add(new Variable("?y", OAVJavaType.java_integer_type));
		pattern.add(new Variable("$?c2", OAVJavaType.java_integer_type, true, false));
		cond1.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, pattern, IOperator.EQUAL));
		
		ObjectCondition	cond2 = new ObjectCondition(Numberbox.numberbox_type);
		List pattern2 = new ArrayList();
		pattern2.add(new Variable("$?c3", OAVJavaType.java_integer_type, true, false));
		pattern2.add(new Variable("?y", OAVJavaType.java_integer_type));
		pattern2.add(new Variable("?x", OAVJavaType.java_integer_type));
		pattern2.add(new Variable("$?c4", OAVJavaType.java_integer_type, true, false));
		cond2.addConstraint(new BoundConstraint(Numberbox.numberbox_has_numbers, pattern2, IOperator.EQUAL));
		
		IRule rule = new Rule("neibors_numbers(x,y) && neibors_numbers(y,x)", 
			new AndCondition(new ICondition[]{cond1, cond2}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object x = assigments.getVariableValue("?x");
				Object y = assigments.getVariableValue("?y");
				result.add("("+x+","+y+")");
			}
		});
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		// Create rule system.
		RuleSystem	system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();
				
//		RetePanel.createReteFrame("Split test", ((RetePatternMatcher)system.getMatcher()).getReteNode());
			
		// Add numbers.
		Object nb = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(1));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(2));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(4));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		
//		System.out.println(((RetePatternMatcher)system.getMatcher()).getReteMemory());
		
		Object nb2 = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb2, Numberbox.numberbox_has_numbers, Integer.valueOf(5));
		state.addAttributeValue(nb2, Numberbox.numberbox_has_numbers, Integer.valueOf(4));
		state.addAttributeValue(nb2, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		
//		System.out.println(((RetePatternMatcher)system.getMatcher()).getReteMemory());
		
//		try{synchronized(system){system.wait();}} catch(Exception e){}
		
		List test = new ArrayList();
		test.add("(5,4)");
		test.add("(4,5)");
		test.add("(4,3)");
		test.add("(3,4)");
		// Todo: which order would be correct?
//		test.add("(4,5)");
//		test.add("(5,4)");
//		test.add("(3,4)");
//		test.add("(4,3)");
		system.fireAllRules();
		assertEquals("The condition should trigger with: "+test, test, result);
	}
	
	/**
	 *  Test multi select -> removed. Now collect node.
	 * /
	public void testLiteralConstraints()
	{
		// Find all pairs of neighboring same numbers 
		// Numberbox ($?numbers:(value < 5 && value > 2)) 
		ObjectCondition	cond = new ObjectCondition(Numberbox.numberbox_type);

		// todo: is there another way to specify that here a virtual extractor should be used
		BoundSelectConstraint bsc = new BoundSelectConstraint(Numberbox.numberbox_has_numbers, new Variable("$?numbers", OAVJavaType.java_integer_type, true), IOperator.EQUAL);
		bsc.addConstraint(new LiteralConstraint(null, Integer.valueOf(5), IOperator.LESS));
		bsc.addConstraint(new LiteralConstraint(null, Integer.valueOf(2), IOperator.GREATER));
		cond.addConstraint(bsc);
		
		IRule	rule	= new Rule("", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object x = assigments.getVariableValue("$?numbers");
				result.add("("+x+")");
			}
		});
		system.getRulebase().addRule(rule);
		
//		RetePanel.createReteFrame("Split test", system, new Object());
//		try{synchronized(system){system.wait();}} catch(Exception e){}
		
		// Add numbers.
		Object nb = state.createRootObject(Numberbox.numberbox_type);
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(1));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(10));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(2));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(9));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(3));
		state.addAttributeValue(nb, Numberbox.numberbox_has_numbers, Integer.valueOf(8));
		system.fireAllRules();
		
		//System.out.println(((RetePatternMatcher)system.getMatcher()).getReteMemory());
		
		List test = new ArrayList();
		test.add("([3])");
		assertEquals("The condition should trigger with: "+test, test, result);
		
//		System.out.println(result);
		system.getRulebase().removeRule(rule);
	}*/
	
	/**
	 * 
	 *  @param args
	 * /
	public static void main(String[] args)
	{
		try
		{
			MultifieldSplitTest test = new MultifieldSplitTest();
			test.setUp();
			test.testLiteralConstraints();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
}
