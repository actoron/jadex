package jadex.rules.examples.fibonacci;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.PriorityAgenda;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.rulesystem.rules.functions.Sub;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

/**
 *  Calculating fibonacci numbers example.
 */
public class JavaFibonacci
{
	/**
	 *  Main method.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		// Create rete system.
		OAVTypeModel fibonacci_type_model = new OAVTypeModel("fibonacci_type_model");
		fibonacci_type_model.addTypeModel(OAVJavaType.java_type_model);
		final OAVJavaType fibo_type = fibonacci_type_model.createJavaType(FibonacciNumber.class, OAVJavaType.KIND_BEAN);
		
		IOAVState state = OAVStateFactory.createOAVState(fibonacci_type_model); // Create the production memory.
		Rulebase rb	= new Rulebase();
			
		// Add rules to rulebase.
		
		Variable fn = new Variable("?fn", fibo_type);
		ObjectCondition fncon = new ObjectCondition(fibo_type);
		fncon.addConstraint(new BoundConstraint(null, fn));
		fncon.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("value"), Long.valueOf(-1)));
		rb.addRule(new Rule("recurse", fncon, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				FibonacciNumber fn = (FibonacciNumber)assignments.getVariableValue("?fn");
				System.out.println("recurse for "+fn.getSequence());
				state.addJavaRootObject(new FibonacciNumber(fn.getSequence()-1));
			}
		}, IPriorityEvaluator.PRIORITY_1));

		Variable f1 = new Variable("?f1", fibo_type);
		ObjectCondition f1con = new ObjectCondition(fibo_type);
		f1con.addConstraint(new BoundConstraint(null, f1));
		f1con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("value"), Long.valueOf(-1)));
		f1con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("sequence"), Integer.valueOf(1)));
		rb.addRule(new Rule("bootstrap1", f1con, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				FibonacciNumber f1 = (FibonacciNumber)assignments.getVariableValue("?f1");
				f1.setValue(1);
				System.out.println("f"+f1.getSequence()+"="+f1.getValue());
			}
		}, IPriorityEvaluator.PRIORITY_2));
		
		Variable f2 = new Variable("?f2", fibo_type);
		ObjectCondition f2con = new ObjectCondition(fibo_type);
		f2con.addConstraint(new BoundConstraint(null, f2));
		f2con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("value"), Long.valueOf(-1)));
		f2con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("sequence"), Integer.valueOf(2)));
		rb.addRule(new Rule("bootstrap2", f2con, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				FibonacciNumber f2 = (FibonacciNumber)assignments.getVariableValue("?f2");
				f2.setValue(1);
				System.out.println("f"+f2.getSequence()+"="+f2.getValue());
			}
		}));
		
		Variable f0 = new Variable("?f0", fibo_type);
		Variable f0cnt = new Variable("?f0cnt", OAVJavaType.java_integer_type);
//		Variable f1 = new Variable("?f1", fibo_type);
		Variable f1cnt = new Variable("?f1cnt", OAVJavaType.java_integer_type);
//		Variable f2 = new Variable("?f2", fibo_type);
		Variable f2cnt = new Variable("?f2cnt", OAVJavaType.java_integer_type);
		
		ObjectCondition f00con = new ObjectCondition(fibo_type);
		f00con.addConstraint(new BoundConstraint(null, f0));
		f00con.addConstraint(new BoundConstraint(fibo_type.getAttributeType("sequence"), f0cnt));
		f00con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("value"), Long.valueOf(-1), IOperator.NOTEQUAL));
		
		ObjectCondition f11con = new ObjectCondition(fibo_type);
		f11con.addConstraint(new BoundConstraint(null, f1));
		f11con.addConstraint(new BoundConstraint(fibo_type.getAttributeType("sequence"), f1cnt));
		f11con.addConstraint(new PredicateConstraint(new FunctionCall(new OperatorFunction(IOperator.EQUAL), 
			new Object[]{f0cnt, new FunctionCall(new Sub(), new Object[]{f1cnt, Integer.valueOf(1)})})));
		f11con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("value"), Long.valueOf(-1), IOperator.NOTEQUAL));
		
		ObjectCondition f22con = new ObjectCondition(fibo_type);
		f22con.addConstraint(new BoundConstraint(null, f2));
		f22con.addConstraint(new BoundConstraint(fibo_type.getAttributeType("sequence"), f2cnt));
		f22con.addConstraint(new LiteralConstraint(fibo_type.getAttributeType("value"), Long.valueOf(-1)));
		f22con.addConstraint(new PredicateConstraint(new FunctionCall(new OperatorFunction(IOperator.EQUAL), 
			new Object[]{f1cnt, new FunctionCall(new Sub(), new Object[]{f2cnt, Integer.valueOf(1)})})));
		
		rb.addRule(new Rule("calc", new AndCondition(new ICondition[]{f00con, f11con, f22con}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				FibonacciNumber f0 = (FibonacciNumber)assignments.getVariableValue("?f0");
				FibonacciNumber f1 = (FibonacciNumber)assignments.getVariableValue("?f1");
				FibonacciNumber f2 = (FibonacciNumber)assignments.getVariableValue("?f2");
				f2.setValue(f0.getValue()+f1.getValue());
				System.out.println("f"+f2.getSequence()+"="+f2.getValue());
			}
		}));
		
		FibonacciNumber f50 = new FibonacciNumber(50);
		state.addJavaRootObject(f50);
		
		// Initialize rule system.
		RuleSystem rete = new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb), new PriorityAgenda());
		rete.init();
		
//		long start = System.currentTimeMillis();
//		rete.fireAllRules();
//		long stop = System.currentTimeMillis();
//		System.out.println( "fibanacci(" + f50.getSequence() + ") = " + 
//			f50.getValue() + " took " + (stop-start) + "ms" );
		
		RuleSystemExecutor exe = new RuleSystemExecutor(rete, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "Fibonacci");
	}
}
